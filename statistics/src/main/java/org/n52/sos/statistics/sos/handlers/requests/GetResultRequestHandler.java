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
package org.n52.sos.statistics.sos.handlers.requests;

import org.n52.shetland.ogc.sos.request.GetResultRequest;
import org.n52.sos.statistics.sos.SosDataMapping;
import org.n52.sos.statistics.sos.models.SpatialFilterEsModel;
import org.n52.sos.statistics.sos.models.TimeEsModel;

public class GetResultRequestHandler extends AbstractSosRequestHandler<GetResultRequest> {

    @Override
    protected void resolveConcreteRequest() {
        put(SosDataMapping.GR_FEATURE_IDENTIFIERS, request.getFeatureIdentifiers());
        put(SosDataMapping.GR_OBSERVATION_TEMPLATE_IDENTIFIER, request.getObservationTemplateIdentifier());
        put(SosDataMapping.GR_OBSERVATION_PROPERTY, request.getObservedProperty());
        put(SosDataMapping.GR_OFFERING, request.getOffering());
        put(SosDataMapping.GR_SPATIAL_FILTER, SpatialFilterEsModel.convert(request.getSpatialFilter()));
        put(SosDataMapping.GR_TEMPORAL_FILTER, TimeEsModel.convert(request.getTemporalFilter()));
    }
}
