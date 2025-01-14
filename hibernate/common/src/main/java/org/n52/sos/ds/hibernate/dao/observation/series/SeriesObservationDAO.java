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
package org.n52.sos.ds.hibernate.dao.observation.series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.n52.series.db.beans.DataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.gml.time.IndeterminateValue;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.ows.exception.CodedException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.request.GetObservationRequest;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.dao.observation.ObservationFactory;
import org.n52.sos.ds.hibernate.util.QueryHelper;

public class SeriesObservationDAO extends AbstractSeriesObservationDAO {

    public SeriesObservationDAO(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Query series observation for series and offerings
     *
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DataEntity<?>> getSeriesObservationFor(DatasetEntity series, List<String> offerings, Session session) {
        return getSeriesObservationCriteriaFor(series, offerings, session).list();
    }

    /**
     * Query series obserations for series, temporal filter, and offerings
     *
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param filterCriterion filter criterion
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DataEntity<?>> getSeriesObservationFor(DatasetEntity series, List<String> offerings,
            Criterion filterCriterion, Session session) {
        return getSeriesObservationCriteriaFor(series, offerings, filterCriterion, session).list();
    }

    /**
     * Query first/latest series obserations for series (and offerings)
     *
     * @param series
     *            Series to get values for
     * @param offerings
     *            Offerings to get values for
     * @param sosIndeterminateTime sos indeterminate time value
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<DataEntity<?>> getSeriesObservationForExtendedIndeterminateTimeFilter(DatasetEntity series,
            List<String> offerings, IndeterminateValue sosIndeterminateTime, Session session) {
        return getSeriesObservationCriteriaForIndeterminateTimeFilter(series, offerings, sosIndeterminateTime, session)
                .list();
    }

    /**
     * Query series observations for GetObservation request and features
     *
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport If an error occurs
     */
    @Override
    public List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, null, session);
    }

    /**
     * Query series observations for GetObservation request, features, and a
     * filter criterion (typically a temporal filter)
     *
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport If an error occurs
     */
    @Override
    public List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, filterCriterion, null, session);
    }

    /**
     * Query series observations for GetObservation request, features, and an
     * indeterminate time (first/latest)
     *
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session
     *            Hibernate session
     * @return Series observations that fit
     * @throws OwsExceptionReport If an error occurs
     */
    @Override
    public List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationsFor(request, features, null, sosIndeterminateTime, session);
    }

    /**
     * Query series observations for GetObservation request, features, and
     * filter criterion (typically a temporal filter) or an indeterminate time
     * (first/latest). This method is private and accepts all possible arguments
     * for request-based getSeriesObservationFor. Other public methods overload
     * this method with sensible combinations of arguments.
     *
     * @param request
     *            GetObservation request
     * @param features
     *            Collection of feature identifiers resolved from the request
     * @param filterCriterion
     *            Criterion to apply to criteria query (typically a temporal
     *            filter)
     * @param sosIndeterminateTime
     *            Indeterminate time to use in a temporal filter (first/latest)
     * @param session teh session
     * @return Series observations that fit
     * @throws OwsExceptionReport If an error occurs
     */
    @Override
    protected List<DataEntity<?>> getSeriesObservationsFor(GetObservationRequest request, Collection<String> features,
            Criterion filterCriterion, IndeterminateValue sosIndeterminateTime, Session session)
            throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(features)) {
            List<DataEntity<?>> observations = new ArrayList<>();
            for (List<String> ids : QueryHelper.getListsForIdentifiers(features)) {
                observations.addAll(
                        getSeriesObservationCriteriaFor(request, ids, filterCriterion, sosIndeterminateTime, session));
            }
            return observations;
        } else {
            return getSeriesObservationCriteriaFor(request, features, filterCriterion, sosIndeterminateTime, session);
        }
    }

    @Override
    public List<DataEntity<?>> getSeriesObservationsFor(DatasetEntity series, GetObservationRequest request,
            IndeterminateValue sosIndeterminateTime, Session session) throws OwsExceptionReport {
        return getSeriesObservationCriteriaFor(series, request, sosIndeterminateTime, session);
    }

    @Override
    protected void addSpecificRestrictions(Criteria c, GetObservationRequest request, StringBuilder logArgs)
            throws CodedException {
        // nothing to add
    }

    public ObservationFactory getObservationFactory() {
        return SeriesObservationFactory.getInstance();
    }

    @Override
    protected Criteria addAdditionalObservationIdentification(Criteria c, OmObservation sosObservation) {
        return c;
    }

}
