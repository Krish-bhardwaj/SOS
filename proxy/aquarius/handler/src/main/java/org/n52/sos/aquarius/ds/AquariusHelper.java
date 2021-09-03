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
package org.n52.sos.aquarius.ds;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.geotools.util.Range;
import org.joda.time.DateTime;
import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.shetland.util.DateTimeHelper;
import org.n52.sos.aquarius.AquariusConstants;
import org.n52.sos.aquarius.pojo.Location;
import org.n52.sos.aquarius.pojo.Parameter;
import org.n52.sos.aquarius.pojo.TimeSeriesData;
import org.n52.sos.aquarius.pojo.TimeSeriesDescription;
import org.n52.sos.aquarius.pojo.data.Qualifier;
import org.n52.sos.aquarius.pojo.data.Qualifier.QualifierKey;
import org.n52.sos.aquarius.requests.AbstractGetTimeSeriesData;
import org.n52.sos.aquarius.requests.GetLocationDescriptionList;
import org.n52.sos.aquarius.requests.GetTimeSeriesCorrectedData;
import org.n52.sos.aquarius.requests.GetTimeSeriesDescriptionList;
import org.n52.sos.aquarius.requests.GetTimeSeriesRawData;
import org.n52.sos.aquarius.requests.GetTimeSeriesUniqueIdList;
import org.n52.sos.proxy.harvest.AbstractProxyHelper;

import com.google.common.base.Strings;

@Configurable
public class AquariusHelper extends AbstractProxyHelper {

    public static final String APPLY_ROUNDING = "proxy.aquarius.applyRounding";

    public static final String RETURN_FULL_COVERAGE = "proxy.aquarius.returnFullCoverage";

    public static final String INCLUDE_GAP_MARKERS = "proxy.aquarius.includeGapMarkers";

    public static final String DATA_TYPE = "proxy.aquarius.dataType";

    public static final String CREATE_TEMPORAL = "proxy.aquarius.temporal.create";

    public static final String PUBLISHED = "proxy.aquarius.published";

    private static final String EXTENDED_ATTRIBUTE_TIMESERIES_KEY = "proxy.aquarius.extendenattribute.timeseries.key";

    private static final String EXTENDED_ATTRIBUTE_TIMESERIES_VALUE =
            "proxy.aquarius.extendenattribute.timeseries.value";

    private static final String EXTENDED_ATTRIBUTE_LOCATION = "proxy.aquarius.extendenattribute.location";

    private static final String EXTENDED_ATTRIBUTE_LOCATION_KEY = "proxy.aquarius.extendenattribute.location.key";

    private static final String EXTENDED_ATTRIBUTE_LOCATION_VALUE = "proxy.aquarius.extendenattribute.location.value";

    private static final String BELOW_IDENTIFIER = "proxy.aquarius.detectionlimit.below";

    private static final String ABOVE_IDENTIFIER = "proxy.aquarius.detectionlimit.above";

    private Map<String, Parameter> parameters = new HashMap<>();

    private Map<String, Location> locations = new HashMap<>();

    private Map<String, TimeSeriesDescription> datasets = new HashMap<>();

    private boolean applyRounding = Boolean.TRUE.booleanValue();

    private boolean returnFullCoverage = Boolean.FALSE.booleanValue();

    private boolean includeGapMarkers = Boolean.TRUE.booleanValue();

    private DataType dataType = DataType.RAW;

    private Published published = Published.ALL;

    private String extendedAttributeTimeSeriesKey;

    private String extendedAttributeTimeSeriesValue;

    private boolean extendedAttributeLocationAsTimeSeries = Boolean.FALSE.booleanValue();

    private String extendedAttributeLocationKey;

    private String extendedAttributeLocationValue;

    private String belowQualifier;

    private String aboveQualifier;

    private boolean createTemporal = Boolean.FALSE.booleanValue();

    @Setting(APPLY_ROUNDING)
    public AquariusHelper setApplyRoundig(boolean applyRounding) {
        this.applyRounding = applyRounding;
        return this;
    }

    @Setting(RETURN_FULL_COVERAGE)
    public AquariusHelper setreturnFullCoverage(boolean returnFullCoverage) {
        this.returnFullCoverage = returnFullCoverage;
        return this;
    }

    @Setting(INCLUDE_GAP_MARKERS)
    public AquariusHelper setIncludeGapMarkers(boolean includeGapMarkers) {
        this.includeGapMarkers = includeGapMarkers;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_TIMESERIES_KEY)
    public AquariusHelper setextendedAttributeTimeseriesKey(String key) {
        this.extendedAttributeTimeSeriesKey = key;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_TIMESERIES_VALUE)
    public AquariusHelper setExtendedAttributeTimeseriesValue(String value) {
        this.extendedAttributeTimeSeriesValue = value;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_LOCATION)
    public AquariusHelper setExtendedAttributeLocationAsTimeSeries(boolean extendedAttributeLocationAsTimeSeries) {
        this.extendedAttributeLocationAsTimeSeries = extendedAttributeLocationAsTimeSeries;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_LOCATION_KEY)
    public AquariusHelper setextendedAttributeLocationKey(String key) {
        this.extendedAttributeLocationKey = key;
        return this;
    }

    @Setting(EXTENDED_ATTRIBUTE_LOCATION_VALUE)
    public AquariusHelper setExtendedAttributeLocationValue(String value) {
        this.extendedAttributeLocationValue = value;
        return this;
    }

    @Setting(BELOW_IDENTIFIER)
    public AquariusHelper setBelowQualifierIdentifier(String belowQualifier) {
        this.belowQualifier = belowQualifier;
        return this;
    }

    @Setting(ABOVE_IDENTIFIER)
    public AquariusHelper setAboveQualifierIdentifier(String aboveQualifier) {
        this.aboveQualifier = aboveQualifier;
        return this;
    }

    public String getBelowQualifier() {
        return belowQualifier;
    }

    public boolean isSetBelowQualifier() {
        return !Strings.isNullOrEmpty(getAboveQualifier());
    }

    public String getAboveQualifier() {
        return aboveQualifier;
    }

    public boolean isSetAboveQualifier() {
        return !Strings.isNullOrEmpty(getAboveQualifier());
    }

    public boolean isSetApplyRounding() {
        return applyRounding;
    }

    private boolean isSetExtendedAttributeTimeSeriesKey() {
        return extendedAttributeTimeSeriesKey != null && !extendedAttributeTimeSeriesKey.isEmpty();
    }

    private boolean isSetExtendedAttributeTimeSeriesValue() {
        return extendedAttributeTimeSeriesValue != null && !extendedAttributeTimeSeriesValue.isEmpty();
    }

    private boolean isExtendendAttributeTimeSeries() {
        return isSetExtendedAttributeTimeSeriesKey() && isSetExtendedAttributeTimeSeriesValue();
    }

    private boolean isSetExtendedAttributeLocationKey() {
        return extendedAttributeLocationKey != null && !extendedAttributeLocationKey.isEmpty();
    }

    private boolean isSetExtendedAttributeLocationValue() {
        return extendedAttributeLocationValue != null && !extendedAttributeLocationValue.isEmpty();
    }

    private boolean isExtendendAttributeLocation() {
        return isSetExtendedAttributeLocationKey() && isSetExtendedAttributeLocationValue();
    }

    private boolean isExtendendAttributeLocationAsTimeseries() {
        return extendedAttributeLocationAsTimeSeries;
    }

//    @Setting(CREATE_TEMPORAL)
    public AquariusHelper setCreateTemporal(boolean createTemporal) {
        this.createTemporal  = createTemporal;
        return this;
    }

    public boolean isCreateTemporal() {
        return createTemporal;
    }

    @Setting(DATA_TYPE)
    public AquariusHelper setDataType(String dataType) {
        if (dataType != null && !this.dataType.equals(DataType.valueOf(dataType))) {
            this.dataType = DataType.valueOf(dataType);
        }
        return this;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Parameter getParameter(String parameterId) {
        return parameters.get(parameterId);
    }

    @Setting(PUBLISHED)
    public AquariusHelper setPublished(String published) {
        if (published != null && !this.published.equals(Published.valueOf(published))) {
            this.published = Published.valueOf(published);
        }
        return this;
    }

    public Published getPublished() {
        return published;
    }

    public AquariusHelper addParameter(Parameter parameter) {
        if (parameter != null) {
            parameters.put(parameter.getIdentifier(), parameter);
        }
        return this;
    }

    public AquariusHelper addParameters(Collection<Parameter> parameters) {
        if (parameters != null) {
            parameters.stream()
                    .forEach(m -> addParameter(m));
        }
        return this;
    }

    public boolean hasLocation(String locationId) {
        return locations.containsKey(locationId);
    }

    public Location getLocation(String locationId) {
        return locations.get(locationId);
    }

    public AquariusHelper addLocation(Location location) {
        if (location != null) {
            locations.put(location.getIdentifier(), location);
        }
        return this;
    }

    public AquariusHelper addLocations(Collection<Location> locations) {
        if (locations != null) {
            locations.stream()
                    .forEach(m -> addLocation(m));
        }
        return this;
    }

    public TimeSeriesDescription getDataset(String timeSeriesIdentifier) {
        return datasets.get(timeSeriesIdentifier);
    }

    public AquariusHelper addDataset(TimeSeriesDescription timeSeries) {
        if (timeSeries != null) {
            datasets.put(timeSeries.getUniqueId(), timeSeries);
        }
        return this;
    }

    public boolean hasDataset(String dataSetId) {
        return datasets.containsKey(dataSetId);
    }

    public GetLocationDescriptionList getLocationDescriptionListRequest(String locationIdentifier) {
        GetLocationDescriptionList request = getLocationDescriptionListRequest();
        request.setLocationIdentifier(locationIdentifier);
        return request;
    }

    public GetLocationDescriptionList getLocationDescriptionListRequest() {
        switch (getPublished()) {
            case FALSE:
                return (GetLocationDescriptionList) getBaseLocationDescriptionListRequest()
                        .addHeader(AquariusConstants.Parameters.PUBLISHED, Boolean.FALSE.toString());
            case TURE:
                return (GetLocationDescriptionList) getBaseLocationDescriptionListRequest()
                        .addHeader(AquariusConstants.Parameters.PUBLISHED, Boolean.TRUE.toString());
            case ALL:
            default:
                return getBaseLocationDescriptionListRequest();
        }
    }

    private GetLocationDescriptionList getBaseLocationDescriptionListRequest() {
        if (isExtendendAttributeLocation()
                || (isExtendendAttributeLocationAsTimeseries() && isExtendendAttributeTimeSeries())) {
            return new GetLocationDescriptionList(getExtendedFilterMapLocation());
        }
        return new GetLocationDescriptionList();
    }

    public GetTimeSeriesDescriptionList getGetTimeSeriesDescriptionListRequest() {
        switch (getPublished()) {
            case FALSE:
                return (GetTimeSeriesDescriptionList) getBaseTimeSeriesDescriptionListRequest()
                        .addHeader(AquariusConstants.Parameters.PUBLISHED, Boolean.FALSE.toString());
            case TURE:
                return (GetTimeSeriesDescriptionList) getBaseTimeSeriesDescriptionListRequest()
                        .addHeader(AquariusConstants.Parameters.PUBLISHED, Boolean.TRUE.toString());
            case ALL:
            default:
                return getBaseTimeSeriesDescriptionListRequest();
        }
    }

    private GetTimeSeriesDescriptionList getBaseTimeSeriesDescriptionListRequest() {
        if (isExtendendAttributeTimeSeries()) {
            return new GetTimeSeriesDescriptionList(getExtendedFilterMapTimeSeries());
        }
        return new GetTimeSeriesDescriptionList();
    }

    public GetTimeSeriesUniqueIdList getTimeSeriesUniqueIdsRequest() {
        switch (getPublished()) {
            case FALSE:
                return (GetTimeSeriesUniqueIdList) getBaseTimeSeriesUnitqueIdsListRequest()
                        .addHeader(AquariusConstants.Parameters.PUBLISHED, Boolean.FALSE.toString());
            case TURE:
                return (GetTimeSeriesUniqueIdList) getBaseTimeSeriesUnitqueIdsListRequest()
                        .addHeader(AquariusConstants.Parameters.PUBLISHED, Boolean.TRUE.toString());
            case ALL:
            default:
                return getBaseTimeSeriesUnitqueIdsListRequest();
        }
    }

    private GetTimeSeriesUniqueIdList getBaseTimeSeriesUnitqueIdsListRequest() {
        if (isExtendendAttributeTimeSeries()) {
            return new GetTimeSeriesUniqueIdList(getExtendedFilterMapTimeSeries());
        }
        return new GetTimeSeriesUniqueIdList();
    }

    private Map<String, String> getExtendedFilterMapTimeSeries() {
        Map<String, String> map = new LinkedHashMap<>();
        if (isExtendendAttributeTimeSeries()) {
            map.put(extendedAttributeTimeSeriesKey, extendedAttributeTimeSeriesValue);
        }
        return map;
    }

    private Map<String, String> getExtendedFilterMapLocation() {
        if (isExtendendAttributeLocationAsTimeseries() && isExtendendAttributeTimeSeries()) {
            return getExtendedFilterMapTimeSeries();
        }
        Map<String, String> map = new LinkedHashMap<>();
        if (isExtendendAttributeLocation()) {
            map.put(extendedAttributeLocationKey, extendedAttributeLocationValue);
        }
        return map;
    }

    public AbstractGetTimeSeriesData getTimeSeriesDataRequest(String timeSeriesUniqueId) {
        switch (getDataType()) {
            case CORRECTED:
                return new GetTimeSeriesCorrectedData(timeSeriesUniqueId).setReturnFullCoverage(returnFullCoverage)
                        .setIncludeGapMarkers(includeGapMarkers)
                        .setApplyRounding(applyRounding);
            case RAW:
            default:
                return new GetTimeSeriesRawData(timeSeriesUniqueId).setApplyRounding(applyRounding);
        }
    }

    public Range<DateTime> getTimeRange(TimeSeriesDescription timeSeries) {
        switch (getDataType()) {
            case CORRECTED:
                return (timeSeries.getCorrectedStartTime() != null && timeSeries.getCorrectedEndTime() != null)
                        ? new Range<DateTime>(DateTime.class,
                                DateTimeHelper.parseIsoString2DateTime(timeSeries.getCorrectedStartTime()),
                                DateTimeHelper.parseIsoString2DateTime(timeSeries.getCorrectedEndTime()))
                        : null;
            default:
                return (timeSeries.getRawStartTime() != null && timeSeries.getRawEndTime() != null)
                        ? new Range<DateTime>(DateTime.class,
                                DateTimeHelper.parseIsoString2DateTime(timeSeries.getRawStartTime()),
                                DateTimeHelper.parseIsoString2DateTime(timeSeries.getRawEndTime()))
                        : null;
        }
    }

    public enum DataType {
        RAW, CORRECTED;
    }

    public enum Published {
        TURE, FALSE, ALL;
    }

    public boolean checkForData(TimeSeriesDescription timeSeries) {
        switch (getDataType()) {
            case CORRECTED:
                return timeSeries.getCorrectedStartTime() != null && timeSeries.getCorrectedEndTime() != null;
            default:
                return timeSeries.getRawStartTime() != null && timeSeries.getRawEndTime() != null;
        }
    }

    public TimeSeriesData applyQualifierChecker(TimeSeriesData timeSeriesData) {
        QualifierChecker checker = new QualifierChecker();
        if (isSetAboveQualifier() || isSetBelowQualifier() && timeSeriesData.hasQualifiers()) {
            for (Qualifier qualifier : timeSeriesData.getQualifiers()) {
                if (isSetAboveQualifier() && qualifier.getIdentifier()
                        .equalsIgnoreCase(getAboveQualifier())) {
                    checker.addQualifier(qualifier.setKey(QualifierKey.ABOVE));
                }
                if (isSetBelowQualifier() && qualifier.getIdentifier()
                        .equalsIgnoreCase(getBelowQualifier())) {
                    checker.addQualifier(qualifier.setKey(QualifierKey.BELOW));
                }
            }
        }
        return timeSeriesData.setQualifierChecker(checker);
    }
}