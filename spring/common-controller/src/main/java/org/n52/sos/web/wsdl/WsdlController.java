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
package org.n52.sos.web.wsdl;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.n52.faroe.ConfigurationError;
import org.n52.sos.web.common.AbstractController;
import org.n52.sos.web.common.ControllerConstants;
import org.n52.sos.wsdl.WSDLFactory;

/**
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
@Controller
@RequestMapping(ControllerConstants.Paths.WSDL)
public class WsdlController extends AbstractController {

    @Inject
    private WSDLFactory factory;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public void get(HttpServletRequest req, HttpServletResponse res) throws IOException, ConfigurationError {
        res.setContentType(MediaType.APPLICATION_XML_VALUE);
        res.setCharacterEncoding("UTF-8");
        IOUtils.write(factory.get(), res.getOutputStream(), Charset.forName(res.getCharacterEncoding()));
    }
}
