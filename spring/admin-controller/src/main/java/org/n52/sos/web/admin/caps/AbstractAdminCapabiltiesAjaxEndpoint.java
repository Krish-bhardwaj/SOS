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
package org.n52.sos.web.admin.caps;

import javax.inject.Inject;

import org.apache.xmlbeans.XmlException;
import org.n52.faroe.ConfigurationError;
import org.n52.iceland.exception.JSONException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.config.CapabilitiesExtensionService;
import org.n52.sos.exception.NoSuchExtensionException;
import org.n52.sos.exception.NoSuchOfferingException;
import org.n52.sos.web.admin.AbstractAdminController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AbstractAdminCapabiltiesAjaxEndpoint extends AbstractAdminController {
    protected static final String OFFERING = "offeringId";
    protected static final String IDENTIFIER = "identifier";
    protected static final String DISABLED_PROPERTY = "disabled";
    protected static final String EXTENSION_PROPERTY = "extensionContent";
    protected static final String IDENTIFIER_PROPERTY = IDENTIFIER;
    protected static final String ERRORS_PROPERTY = "errors";
    protected static final String VALID_PROPERTY = "valid";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAdminCapabiltiesAjaxEndpoint.class);
    @Inject
    private CapabilitiesExtensionService capabilitiesExtensionService;

    protected CapabilitiesExtensionService getCapabilitiesExtensionService() {
        return capabilitiesExtensionService;
    }

    @ResponseBody
    @ExceptionHandler(XmlException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String error(final XmlException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(NoSuchOfferingException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String error(final NoSuchOfferingException e) {
        return String.format("Offering '%s' not found!", e.getIdentifier());
    }

    @ResponseBody
    @ExceptionHandler(NoSuchExtensionException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String error(final NoSuchExtensionException e) {
        return String.format("Extension '%s' not found!", e.getIdentifier());
    }


    @ResponseBody
    @ExceptionHandler(JSONException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String error(final JSONException e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ConfigurationError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String error(final ConfigurationError e) {
        return e.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(OwsExceptionReport.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String error(final OwsExceptionReport e) {
        return e.getMessage();
    }

    protected String getSelectedStaticCapabilities() throws OwsExceptionReport {
        return this.capabilitiesExtensionService.getActiveStaticCapabilities();
    }

    protected void setSelectedStaticCapabilities(String id) throws ConfigurationError,
                                                                   OwsExceptionReport,
                                                                   NoSuchExtensionException {
        final String current = getSelectedStaticCapabilities();
        String identi = (id == null || id.trim().isEmpty()) ? null : id;
        boolean change = false;
        if (current == null) {
            if (identi == null) {
                LOG.debug("Staying with dynamic capabilities.");
            } else {
                LOG.debug("Choosing static capabilities '{}'", identi);
                change = true;
            }
        } else if (identi == null) {
            LOG.debug("Reverting to dynamic capabilities.");
            change = true;
        } else {
            LOG.debug("Switching static capabilities from '{}' to '{}'", current, identi);
            change = true;
        }

        if (change) {
            this.capabilitiesExtensionService.setActiveStaticCapabilities(id);
        }
    }

    protected void showDynamicCapabilities() throws ConfigurationError, OwsExceptionReport, NoSuchExtensionException {
        setSelectedStaticCapabilities(null);
    }
}
