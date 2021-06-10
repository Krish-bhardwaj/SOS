/*
 * Copyright (C) 2012-2021 52°North Spatial Information Research GmbH
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
package org.n52.sos.aquarius.requests;

import java.util.Map;

import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.pojo.ExtendedFilters;
import org.n52.sos.proxy.request.AbstractGetRequest;

public class GetTimeSeriesDescriptionList extends AbstractGetRequest {

    private String locationIdentifier;

    private String parameter;

    private ExtendedFilters extendedFilter;

    public GetTimeSeriesDescriptionList(boolean addExtendedFilterForSOS) {
        if (addExtendedFilterForSOS) {
            setExtendedFilter(new ExtendedFilters().addFilter("SOS_SYNC", Boolean.toString(true)));
        }
    }

    @Override
    public Map<String, String> getQueryParameters() {
        Map<String, String> parameters = createMap();
        if (hasLocationIdentifier()) {
            parameters.put(AquariusConstants.Parameters.LOCATION_IDENTIFIER, getLocationIdentifier());
        }
        if (hasParameter()) {
            parameters.put(AquariusConstants.Parameters.PARAMETER, getParameter());
        }
        if (hasExtendedFilters()) {
            parameters.put(AquariusConstants.Parameters.EXTENDED_FILTERS, getExtendedFilter().encodeFilters());
        }
        return parameters;
    }

    @Override
    public String getPath() {
        return AquariusConstants.Paths.GET_TIME_SERIES_DESCRIPTION_LIST;
    }

    /**
     * @return the locationIdentifier
     */
    public String getLocationIdentifier() {
        return locationIdentifier;
    }

    /**
     * @param locationIdentifier
     *            the locationIdentifier to set
     */
    public GetTimeSeriesDescriptionList setLocationIdentifier(String locationIdentifier) {
        this.locationIdentifier = locationIdentifier;
        return this;
    }

    public boolean hasLocationIdentifier() {
        return getLocationIdentifier() != null && !getLocationIdentifier().isEmpty();
    }

    /**
     * @return the parameter
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * @param parameter
     *            the parameter to set
     */
    public GetTimeSeriesDescriptionList setParameter(String parameter) {
        this.parameter = parameter;
        return this;
    }

    public boolean hasParameter() {
        return getParameter() != null && !getParameter().isEmpty();
    }

    /**
     * @return the extendedFilter
     */
    public ExtendedFilters getExtendedFilter() {
        return extendedFilter;
    }

    /**
     * @param extendedFilter
     *            the extendedFilter to set
     */
    public GetTimeSeriesDescriptionList setExtendedFilter(ExtendedFilters extendedFilter) {
        this.extendedFilter = extendedFilter;
        return this;
    }

    public boolean hasExtendedFilters() {
        return getExtendedFilter() != null && getExtendedFilter().hasFilters();
    }

}
