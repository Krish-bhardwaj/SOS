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
package org.n52.sos.ds.hibernate.dao.observation.ereporting;

import java.util.Set;

import org.hibernate.Criteria;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.ereporting.EReportingDaoHelper;
import org.n52.sos.ds.hibernate.dao.observation.ValuedObservationFactory;
import org.n52.sos.ds.hibernate.dao.observation.series.AbstractSeriesValueDAO;

public class EReportingValueDAO extends AbstractSeriesValueDAO implements EReportingDaoHelper {

    private final Set<Integer> verificationFlags;

    private final Set<Integer> validityFlags;

    public EReportingValueDAO(Set<Integer> verificationFlags, Set<Integer> validityFlags, DaoFactory daoFactory) {
        super(daoFactory);
        this.verificationFlags = verificationFlags;
        this.validityFlags = validityFlags;
    }

    @Override
    public Set<Integer> getVerificationFlags() {
        return this.verificationFlags;
    }

    @Override
    public Set<Integer> getValidityFlags() {
        return this.validityFlags;
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request, StringBuilder logArgs)
            throws OwsExceptionReport {
        addValidityAndVerificationRestrictions(c, request, logArgs);
    }

    @Override
    protected ValuedObservationFactory getValuedObservationFactory() {
        return EReportingValuedObservationFactory.getInstance();
    }

}
