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

import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.n52.iceland.util.action.Action;
import org.n52.shetland.ogc.gml.time.Time;
import org.n52.shetland.ogc.om.OmObservation;
import org.n52.shetland.ogc.om.features.samplingFeatures.AbstractSamplingFeature;
import org.n52.sos.cache.InMemoryCacheImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When executing this &auml;ction (see {@link Action}), the following relations
 * are added, settings are updated in cache:
 * <ul>
 * <li>'Result template identifier' &rarr; 'observable property' relation</li>
 * <li>'Result template identifier' &rarr; 'feature of interest' relation</li>
 * </ul>
 * TODO update list above
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class ResultInsertionUpdate extends InMemoryCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultInsertionUpdate.class);

    private final List<OmObservation> observations;

    private final String templateIdentifier;

    public ResultInsertionUpdate(String templateIdentifier, List<OmObservation> observations) {
        if (observations == null || observations.isEmpty() || templateIdentifier == null
                || templateIdentifier.isEmpty()) {
            String msg = String.format("Missing argument: '%s': %s; template identifier: '%s'",
                    OmObservation.class.getName(), observations, templateIdentifier);
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.observations = observations;
        this.templateIdentifier = templateIdentifier;
    }

    @Override
    public void execute() {
        // TODO remove not required updates and adjust test accordingly
        final InMemoryCacheImpl cache = (InMemoryCacheImpl) getCache();
        for (OmObservation observation : observations) {
            final String observationType = observation.getObservationConstellation().getObservationType();
            final String procedure = observation.getObservationConstellation().getProcedure().getIdentifier();
            final String observableProperty =
                    observation.getObservationConstellation().getObservableProperty().getIdentifier();
            final Time phenomenonTime = observation.getPhenomenonTime();
            final Time resultTime = observation.getResultTime();

            cache.updatePhenomenonTime(phenomenonTime);
            cache.updateResultTime(resultTime != null ? resultTime : phenomenonTime);

            cache.addProcedure(procedure);
            cache.updatePhenomenonTimeForProcedure(procedure, phenomenonTime);

            cache.addProcedureForObservableProperty(observableProperty, procedure);
            cache.addObservablePropertyForResultTemplate(templateIdentifier, observableProperty);
            cache.addObservablePropertyForProcedure(procedure, observableProperty);

            List<AbstractSamplingFeature> observedFeatures =
                    sosFeaturesToList(observation.getObservationConstellation().getFeatureOfInterest());

            Envelope envelope = createEnvelopeFrom(observedFeatures);

            cache.updateGlobalEnvelope(envelope);

            observedFeatures.stream().map(AbstractSamplingFeature::getIdentifier).forEach(featureOfInterest -> {
                cache.addFeatureOfInterest(featureOfInterest);
                cache.addFeatureOfInterestForResultTemplate(templateIdentifier, featureOfInterest);
                cache.addProcedureForFeatureOfInterest(featureOfInterest, procedure);
                observation.getObservationConstellation().getOfferings()
                        .forEach(offering -> cache.addFeatureOfInterestForOffering(offering, featureOfInterest));
            });
            observation.getObservationConstellation().getOfferings().stream().forEach(offering -> {
                cache.addOffering(offering);
                if (!cache.getHiddenChildProceduresForOffering(offering).contains(procedure)) {
                    cache.addProcedureForOffering(offering, procedure);
                }
                        cache.addOfferingForProcedure(procedure, offering);
                        cache.updateEnvelopeForOffering(offering,
                                !envelope.isNull() ? envelope
                                        : cache.getGlobalEnvelope()
                                                .getEnvelope());
                        cache.updatePhenomenonTimeForOffering(offering, phenomenonTime);
                        cache.updateResultTimeForOffering(offering, resultTime);
                // observable property
                cache.addOfferingForObservableProperty(observableProperty, offering);
                cache.addObservablePropertyForOffering(offering, observableProperty);
                // observation type
                cache.addObservationTypesForOffering(offering, observationType);
            });
        }
    }
}
