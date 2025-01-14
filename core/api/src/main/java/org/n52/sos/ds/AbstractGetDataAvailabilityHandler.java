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
package org.n52.sos.ds;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.n52.faroe.annotation.Configurable;
import org.n52.faroe.annotation.Setting;
import org.n52.shetland.ogc.ows.OwsDomain;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.ResultFilterConstants;
import org.n52.shetland.ogc.sos.SosSpatialFilterConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityConstants;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityRequest;
import org.n52.shetland.ogc.sos.gda.GetDataAvailabilityResponse;

import com.google.common.collect.Sets;

/**
 * Handler to get the DataAvailabilities out of the database.
 *
 * Renamed, in version 4.x called AbstractGetDataAvailabilityDAO
 *
 * @author Christian Autermann
 *
 * @since 5.0.0
 */
@Configurable
public abstract class AbstractGetDataAvailabilityHandler extends AbstractSosOperationHandler {
    public static final String INCLUDE_RESULT_TIMES = "IncludeResultTimes";
    public static final String SHOW_COUNT = "ShowCount";
    private boolean forceValueCount;
    private boolean forceGDAv20Response = true;

    public AbstractGetDataAvailabilityHandler(String service) {
        super(service, GetDataAvailabilityConstants.OPERATION_NAME);
    }

    /**
     * Get the DataAvailability out of the Database.
     *
     * @param sosRequest
     *                   the <code>GetDataAvailabilityRequest</code>
     *
     * @return the <code>GetDataAvailabilityResponse</code>
     *
     *
     * @throws OwsExceptionReport
     *                            if an error occurs
     */
    public abstract GetDataAvailabilityResponse getDataAvailability(GetDataAvailabilityRequest sosRequest)
            throws OwsExceptionReport;

    /**
     * @return the forceValueCount
     */
    protected boolean isForceValueCount() {
        return forceValueCount;
    }

    /**
     * @param forceValueCount the forceValueCount to set
     */
    @Setting(GetDataAvailabilitySettings.FORCE_GDA_VALUE_COUNT)
    public void setForceValueCount(boolean forceValueCount) {
        this.forceValueCount = forceValueCount;
    }

    @Override
    protected Set<OwsDomain> getOperationParameters(String service, String version) throws OwsExceptionReport {
        return new HashSet<>(Arrays.asList(getQueryableProcedureParameter(service, version),
                                           getPublishedObservablePropertyParameter(service, version),
                                           getPublishedFeatureOfInterestParameter(service, version),
                                           getOfferingParameter(service, version)));
    }

    /**
     * @return the forEachOffering
     */
    protected boolean isForceGDAv20Response() {
        return forceGDAv20Response;
    }

    /**
     * @param forceGDAv20Response the forceGDAv20Response to set
     */
    @Setting(GetDataAvailabilitySettings.FORCE_GDA_20_RESPONSE)
    public void setForceGDAv20Response(boolean forceGDAv20Response) {
        this.forceGDAv20Response = forceGDAv20Response;
    }

    @Override
    public Set<String> getConformanceClasses(String service, String version) {
        return Sets.newHashSet(ResultFilterConstants.CONFORMANCE_CLASS_RF,
                SosSpatialFilterConstants.CONFORMANCE_CLASS_SF);
    }
}
