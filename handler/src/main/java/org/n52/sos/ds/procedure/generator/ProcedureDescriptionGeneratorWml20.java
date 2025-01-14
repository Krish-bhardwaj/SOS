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
package org.n52.sos.ds.procedure.generator;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.n52.iceland.cache.ContentCacheController;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.series.db.beans.ProcedureEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.gml.ReferenceType;
import org.n52.shetland.ogc.om.NamedValue;
import org.n52.shetland.ogc.om.series.wml.ObservationProcess;
import org.n52.shetland.ogc.om.series.wml.WaterMLConstants;
import org.n52.shetland.ogc.om.values.TextValue;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosProcedureDescription;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.service.profile.ProfileHandler;
import org.n52.sos.util.GeometryHandler;

class ProcedureDescriptionGeneratorWml20 extends AbstractProcedureDescriptionGenerator {

    public static final Set<ProcedureDescriptionGeneratorKey> GENERATOR_KEY_TYPES =
            CollectionHelper.set(new ProcedureDescriptionGeneratorKey(WaterMLConstants.NS_WML_20_PROCEDURE_ENCODING));

    ProcedureDescriptionGeneratorWml20(ProfileHandler profileHandler, GeometryHandler geometryHandler,
            I18NDAORepository i18ndaoRepository, ContentCacheController cacheController) {
        super(i18ndaoRepository, cacheController);
    }

    @Override
    public Set<ProcedureDescriptionGeneratorKey> getKeys() {
        return Collections.unmodifiableSet(GENERATOR_KEY_TYPES);
    }

    /**
     * Generate procedure description from Hibernate procedure entity if no
     * description (file, XML text) is available
     *
     * @param procedure
     *            Hibernate procedure entity
     * @param session
     *            the session
     *
     * @return Generated procedure description
     *
     * @throws OwsExceptionReport
     *             If an error occurs
     */
    public SosProcedureDescription<AbstractFeature> generateProcedureDescription(ProcedureEntity procedure,
            Locale i18n, Session session) throws OwsExceptionReport {
        setLocale(i18n);
        final ObservationProcess op = new ObservationProcess();
        setCommonData(procedure, op, session);
        addName(procedure, op);
        op.setProcessType(new ReferenceType(WaterMLConstants.PROCESS_TYPE_ALGORITHM));
        return new SosProcedureDescription<>(op);
    }

    private void addName(ProcedureEntity procedure, ObservationProcess op) {
        String name = procedure.getIdentifier();
        if (procedure.isSetName()) {
            name = procedure.getName();
        }
        op.addParameter(createName("shortName", name));
        op.addParameter(createName("longName", name));
    }

    private NamedValue<String> createName(String type, String name) {
        final NamedValue<String> namedValueProperty = new NamedValue<>();
        final ReferenceType refType = new ReferenceType(type);
        refType.setTitle(name);
        namedValueProperty.setName(refType);
        namedValueProperty.setValue(new TextValue(name));
        return namedValueProperty;
    }
}
