/*
 * Copyright (C) 2012-2022 52°North Spatial Information Research GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sos.web.admin;

import java.io.File;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.SettingValue;
import org.n52.faroe.SettingValueFactory;
import org.n52.faroe.json.JsonSettingsEncoder;
import org.n52.iceland.exception.JSONException;
import org.n52.janmayen.Json;
import org.n52.sos.web.common.ControllerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @since 4.0.0
 *
 */
@Controller
@RequestMapping(ControllerConstants.Paths.ADMIN_DATABASE_SETTINGS)
public class AdminDatasourceSettingsController extends AbstractDatasourceController {
    public static final String SETTINGS = "settings";

    private static final Logger LOG = LoggerFactory.getLogger(AdminDatasourceSettingsController.class);

    @Inject
    private SettingValueFactory settingValueFactory;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView view() {
        try {
            return new ModelAndView(ControllerConstants.Views.ADMIN_DATASOURCE_SETTINGS, SETTINGS, encodeSettings());

        } catch (Exception ex) {
            LOG.error("Error reading database settings", ex);
            return new ModelAndView(ControllerConstants.Views.ADMIN_DATASOURCE_SETTINGS,
                    ControllerConstants.ERROR_MODEL_ATTRIBUTE, ex.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView save(HttpServletRequest req) throws JSONException {

        // parse them
        Map<String, Object> newSettings =
                parseDatasourceSettings(getDatasource().getChangableSettingDefinitions(getSettings()), req);
        Properties settings = getSettings();

        // test them
        try {
            getDatasource().validateConnection(settings, newSettings);
            getDatasource().validatePrerequisites(settings, newSettings);
            if (getDatasource().needsSchema()) {
                if (getDatasource().checkIfSchemaExists(settings, newSettings)) {
                    getDatasource().validateSchema(settings, newSettings);
                } else {
                    return error(newSettings, "No schema is present", null);
                }
            }
        } catch (ConfigurationError e) {
            return error(newSettings, null, e);
        }

        // save them
        Properties datasourceProperties = getDatasource().getDatasourceProperties(settings, newSettings);
        getDatabaseSettingsHandler().saveAll(datasourceProperties);

        reloadContext();

        return new ModelAndView(new RedirectView(ControllerConstants.Paths.ADMIN_DATABASE_SETTINGS, true));
    }

    protected Map<String, Object> parseDatasourceSettings(Set<SettingDefinition<?>> defs, HttpServletRequest req) {
        Map<String, String> parameters = new HashMap<>(req.getParameterMap().size());
        Enumeration<?> e = req.getParameterNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            parameters.put(key, req.getParameter(key));
        }
        Map<String, Object> parsedSettings = new HashMap<>(parameters.size());
        for (SettingDefinition<?> def : defs) {
            SettingValue<?> newValue =
                    this.settingValueFactory.newSettingValue(def, parameters.get(def.getKey()));
            parsedSettings.put(def.getKey(), newValue.getValue());
        }
        return parsedSettings;
    }

    private ModelAndView error(Map<String, Object> newSettings, String message, Throwable e) throws JSONException {
        Map<String, Object> model = new HashMap<>(2);
        model.put(ControllerConstants.ERROR_MODEL_ATTRIBUTE,
                (message != null) ? message : (e != null) ? e.getMessage() : "Could not save settings");
        model.put(SETTINGS, encodeSettings(newSettings));
        LOG.error("Error saving database settings: " + message, e);
        return new ModelAndView(ControllerConstants.Views.ADMIN_DATASOURCE_SETTINGS, model);
    }

    private JsonNode encodeSettings() throws JSONException {
        return encodeSettings(getDatabaseSettingsHandler().getAll());
    }

    private JsonNode encodeSettings(Properties p) throws JSONException {
        JsonSettingsEncoder enc = getSettingsEncoder();
        Set<SettingDefinition<?>> defs = getDatasource().getChangableSettingDefinitions(p);
        JsonNode settings = enc.encode(enc.sortByGroup(defs));
        ObjectNode node = Json.nodeFactory().objectNode();
        node.set(SETTINGS, settings);
        return node;
    }

    private JsonNode encodeSettings(Map<String, Object> p) throws JSONException {
        JsonSettingsEncoder enc = getSettingsEncoder();
        Set<SettingDefinition<?>> defs =
                getDatasource().getChangableSettingDefinitions(
                        getDatasource().getDatasourceProperties(getSettings(), p));
        for (SettingDefinition<?> def : defs) {
            setDefaultValue(def, p.get(def.getKey()));
        }
        JsonNode settings = enc.encode(enc.sortByGroup(defs));
        ObjectNode node = Json.nodeFactory().objectNode();
        node.set(SETTINGS, settings);
        return node;
    }

    protected void setDefaultValue(SettingDefinition<?> def, String sval) {
        if (sval != null) {
            Object val = this.settingValueFactory.newSettingValue(def, sval).getValue();
            setDefaultValue(def, val);
        }
    }

    @SuppressWarnings("unchecked")
    protected void setDefaultValue(SettingDefinition<?> def, Object val) {
        if (val != null) {
            switch (def.getType()) {
                case BOOLEAN:
                    SettingDefinition<Boolean> bsd = (SettingDefinition<Boolean>) def;
                    bsd.setDefaultValue((Boolean) val);
                    break;
                case FILE:
                    SettingDefinition<File> fsd = (SettingDefinition<File>) def;
                    fsd.setDefaultValue((File) val);
                    break;
                case INTEGER:
                    SettingDefinition<Integer> isd = (SettingDefinition<Integer>) def;
                    isd.setDefaultValue((Integer) val);
                    break;
                case NUMERIC:
                    SettingDefinition<Double> dsd = (SettingDefinition<Double>) def;
                    dsd.setDefaultValue((Double) val);
                    break;
                case STRING:
                    SettingDefinition<String> ssd = (SettingDefinition<String>) def;
                    ssd.setDefaultValue((String) val);
                    break;
                case URI:
                    SettingDefinition<URI> usd = (SettingDefinition<URI>) def;
                    usd.setDefaultValue((URI) val);
                    break;
                case CHOICE:
                    break;
                case MULTILINGUAL_STRING:
                    break;
                case TIMEINSTANT:
                    break;
                default:
                    break;
            }
        }
    }


}
