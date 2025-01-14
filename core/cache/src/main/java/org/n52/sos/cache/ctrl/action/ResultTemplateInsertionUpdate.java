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
package org.n52.sos.cache.ctrl.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.locationtech.jts.geom.Geometry;
import org.n52.iceland.util.action.Action;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.om.AbstractPhenomenon;
import org.n52.shetland.ogc.om.OmCompositePhenomenon;
import org.n52.shetland.ogc.om.OmObservableProperty;
import org.n52.shetland.ogc.om.features.samplingFeatures.SamplingFeature;
import org.n52.sos.cache.SosWritableContentCache;
import org.n52.shetland.ogc.sos.request.InsertResultTemplateRequest;
import org.n52.shetland.ogc.sos.response.InsertResultTemplateResponse;

/**
 * When executing this &auml;ction (see {@link Action}), the following relations
 * are added, settings are updated in cache:
 * <ul>
 * <li>Result template</li>
 * <li>Offering &rarr; Result template</li>
 * </ul>
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class ResultTemplateInsertionUpdate extends InMemoryCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultTemplateInsertionUpdate.class);

    private final InsertResultTemplateResponse response;

    private final InsertResultTemplateRequest request;

    public ResultTemplateInsertionUpdate(InsertResultTemplateRequest request, InsertResultTemplateResponse response) {
        if (request == null || response == null) {
            String msg =
                    String.format("Missing argument: '%s': %s; '%s': %s", InsertResultTemplateRequest.class.getName(),
                            request, InsertResultTemplateResponse.class.getName(), response);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.response = response;
        this.request = request;
    }

    @Override
    public void execute() {
        final SosWritableContentCache cache = getCache();
        final String resultTemplate = response.getAcceptedTemplate();
        cache.addResultTemplate(resultTemplate);
        for (String offering : request.getObservationTemplate().getOfferings()) {
            cache.addOffering(offering);
            cache.addPublishedOffering(offering);
            cache.addResultTemplateForOffering(offering, resultTemplate);
        }
        AbstractFeature featureOfInterest = request.getObservationTemplate().getFeatureOfInterest();
        if (featureOfInterest != null && featureOfInterest.isSetName()) {
            cache.addFeatureOfInterestIdentifierHumanReadableName(featureOfInterest.getIdentifier(),
                    featureOfInterest.getFirstName().getValue());
            cache.addPublishedFeatureOfInterest(featureOfInterest.getIdentifier());
            cache.addFeatureOfInterestIdentifierHumanReadableName(featureOfInterest.getIdentifier(),
                    featureOfInterest.getFirstName().getValue());
            cache.addPublishedFeatureOfInterest(featureOfInterest.getIdentifier());
            cache.addFeatureOfInterest(featureOfInterest.getIdentifier());
            if (featureOfInterest instanceof SamplingFeature && ((SamplingFeature) featureOfInterest).isSetGeometry()) {
                Geometry geometry = ((SamplingFeature) featureOfInterest).getGeometry();
                cache.updateGlobalEnvelope(geometry.getEnvelopeInternal());
                for (String offering : request.getObservationTemplate().getOfferings()) {
                    cache.updateEnvelopeForOffering(offering, geometry.getEnvelopeInternal());
                }
            }
        }

        AbstractPhenomenon observableProperty = request.getObservationTemplate().getObservableProperty();
        if (observableProperty instanceof OmCompositePhenomenon) {
            OmCompositePhenomenon parent = (OmCompositePhenomenon) observableProperty;
            cache.addCompositePhenomenon(parent.getIdentifier());
            cache.addCompositePhenomenonForProcedure(request.getObservationTemplate().getProcedureIdentifier(),
                    parent.getIdentifier());
            for (String offering : request.getObservationTemplate().getOfferings()) {
                cache.addCompositePhenomenonForOffering(offering, parent.getIdentifier());
            }

            for (OmObservableProperty child : parent) {
                cache.addObservablePropertyForCompositePhenomenon(parent.getIdentifier(), child.getIdentifier());
                cache.addCompositePhenomenonForObservableProperty(child.getIdentifier(), parent.getIdentifier());
            }
        }
    }
}
