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
package org.n52.sos.ds.hibernate.util.procedure.create;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

import org.hibernate.Session;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.ogc.sos.SosProcedureDescriptionUnknownType;
import org.n52.sos.ds.hibernate.util.procedure.HibernateProcedureCreationContext;
import org.n52.svalbard.decode.exception.DecodingException;
import org.n52.svalbard.util.XmlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

public class LinkedDescriptionCreationStrategy implements DescriptionCreationStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkedDescriptionCreationStrategy.class);

    @Override
    public boolean apply(ProcedureEntity p) {
        return p != null && !Strings.isNullOrEmpty(p.getDescriptionFile())
                && (p.getDescriptionFile().startsWith("http"));
    }

    @Override
    public SosProcedureDescription<?> create(ProcedureEntity p, String descriptionFormat, Locale i18n,
            HibernateProcedureCreationContext ctx, Session s) throws OwsExceptionReport {
        String xml = loadDescriptionFromHttp(p.getDescriptionFile());
        return new SosProcedureDescriptionUnknownType(p.getIdentifier(), p.getFormat().getFormat(), xml);
    }

    private String loadDescriptionFromHttp(String descriptionFile) throws OwsExceptionReport {
        InputStream is = null;
        Scanner scanner = null;
        try {
            URL url = new URL(descriptionFile);
            HttpURLConnection request1 = (HttpURLConnection) url.openConnection();
            request1.setRequestMethod("GET");
            request1.connect();
            is = request1.getInputStream();
            scanner = new Scanner(is, "UTF-8");
            String inputStreamString = scanner.useDelimiter("\\A").next();
            return checkXml(inputStreamString);
        } catch (IOException | DecodingException e) {
            throw new NoApplicableCodeException().causedBy(e)
                    .withMessage("Error while querying sensor description from:{}", descriptionFile);
        } finally {
            if (scanner != null) {
                scanner.close();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    LOGGER.error("Error while closing inputStream", ioe);
                }
            }
        }
    }

    private String checkXml(String xml) throws DecodingException {
        XmlHelper.parseXmlString(xml);
        if (xml.startsWith("<?xml")) {
            return xml.substring(xml.indexOf(">") + 1);
        }
        return xml;
    }
}
