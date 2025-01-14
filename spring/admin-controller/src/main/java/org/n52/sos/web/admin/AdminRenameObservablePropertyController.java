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


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.n52.iceland.exception.ows.concrete.NoImplementationFoundException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.cache.SosContentCache;
import org.n52.sos.ds.RenameDAO;
import org.n52.sos.exception.AlreadyUsedIdentifierException;
import org.n52.sos.exception.NoSuchObservablePropertyException;
import org.n52.sos.web.common.ControllerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 */
@Controller
@RequestMapping(ControllerConstants.Paths.ADMIN_RENAME_OBSERVABLE_PROPERTIES)
public class AdminRenameObservablePropertyController extends AbstractAdminController {
    public static final String OLD_IDENTIFIER_REQUEST_PARAM = "old";
    public static final String NEW_IDENTIFIER_REQUEST_PARAM = "new";
    private static final Logger log = LoggerFactory.getLogger(AdminRenameObservablePropertyController.class);

    @Inject
    private Optional<RenameDAO> dao;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView view() {
        SosContentCache cache = getCache();
        List<String> observableProperties = Lists.newArrayList(cache.getObservableProperties());
        Collections.sort(observableProperties);
        return new ModelAndView(ControllerConstants.Views.ADMIN_RENAME_OBSERVABLE_PROPERTIES,
                                "observableProperties", observableProperties);
    }



    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = RequestMethod.POST)
    public void change(@RequestParam(OLD_IDENTIFIER_REQUEST_PARAM) String oldName,
                       @RequestParam(NEW_IDENTIFIER_REQUEST_PARAM) String newName)
            throws NoSuchObservablePropertyException, NoImplementationFoundException,
                   AlreadyUsedIdentifierException, OwsExceptionReport {
        log.info("Changing observable property: {} -> {}", oldName, newName);
        SosContentCache cache = getCache();
        if (!cache.hasObservableProperty(oldName)) {
            throw new NoSuchObservablePropertyException(oldName);
        }
        if (cache.hasObservableProperty(newName)) {
            throw new AlreadyUsedIdentifierException(newName);
        }
        if (!this.dao.isPresent()) {
            throw new NoImplementationFoundException(RenameDAO.class);
        }
        this.dao.get().renameObservableProperty(oldName, newName);
        updateCache();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NoImplementationFoundException.class)
    public String onError(NoImplementationFoundException e) {
        return String.format("No RenameDAO implementation found!");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(OwsExceptionReport.class)
    public String onError(OwsExceptionReport e) {
        return e.getMessage();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyUsedIdentifierException.class)
    public String onError(AlreadyUsedIdentifierException e) {
        return String.format("The identifier %s is already assigned!", e.getIdentifier());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchObservablePropertyException.class)
    public String onError(NoSuchObservablePropertyException e) {
        return String.format("The identifier %s is not assigned!", e.getIdentifier());
    }


}
