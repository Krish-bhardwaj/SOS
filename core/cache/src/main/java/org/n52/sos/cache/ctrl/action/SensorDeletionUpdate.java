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

import java.util.Set;

import org.n52.sos.cache.SosWritableContentCache;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.iceland.util.action.Action;
import org.n52.sos.ds.CacheFeederHandler;
import org.n52.shetland.ogc.sos.request.DeleteSensorRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * When executing this &auml;ction (see {@link Action}), the following relations
 * are deleted, settings are updated in cache:
 * <ul>
 * <li>Result template</li>
 * <li>Offering &rarr; Result template</li>
 * </ul>
 *
 * @author <a href="mailto:e.h.juerrens@52north.org">Eike Hinderk
 *         J&uuml;rrens</a>
 * @since 4.0.0
 */
public class SensorDeletionUpdate extends CacheFeederDAOCacheUpdate {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorDeletionUpdate.class);

    private final DeleteSensorRequest request;

    public SensorDeletionUpdate(CacheFeederHandler cacheFeederDAO, DeleteSensorRequest request) {
        super(cacheFeederDAO);
        if (request == null) {
            String msg = String.format("Missing argument: '%s': %s", DeleteSensorRequest.class.getName(), "request");
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        this.request = request;
    }

    @Override
    public void execute() {
        final SosWritableContentCache cache = getCache();
        final String procedure = request.getProcedureIdentifier();

        cache.removeProcedure(procedure);
        cache.removePublishedProcedure(procedure);

        cache.removeMinPhenomenonTimeForProcedure(procedure);
        cache.removeMaxPhenomenonTimeForProcedure(procedure);

        for (String feature : cache.getFeaturesOfInterest()) {
            cache.removeProcedureForFeatureOfInterest(feature, procedure);
            if (cache.getProceduresForFeatureOfInterest(feature).isEmpty()) {
                cache.removeProceduresForFeatureOfInterest(feature);
            }
        }

        Set<String> offeringsNeedingReload = Sets.newHashSet();
        for (String offering : cache.getOfferingsForProcedure(procedure)) {
            cache.removeProcedureForOffering(offering, procedure);

            if (cache.getHiddenChildProceduresForOffering(offering).contains(procedure)) {
                //offering is a parent offering, don't delete it but we need to reload all its cache data
                offeringsNeedingReload.add(offering);
            } else {
                //offering is not a parent offering, destroy it
                cache.removeMaxPhenomenonTimeForOffering(offering);
                cache.removeMinPhenomenonTimeForOffering(offering);
                cache.removeMaxResultTimeForOffering(offering);
                cache.removeMinResultTimeForOffering(offering);
                cache.removeNameForOffering(offering);
                cache.removeFeaturesOfInterestForOffering(offering);
                cache.removeRelatedFeaturesForOffering(offering);
                cache.removeObservationTypesForOffering(offering);
                cache.removeEnvelopeForOffering(offering);
                cache.removeSpatialFilteringProfileEnvelopeForOffering(offering);
                for (String observableProperty : cache.getObservablePropertiesForOffering(offering)) {
                    cache.removeOfferingForObservableProperty(observableProperty, offering);
                }
                cache.clearCompositePhenomenonForOffering(offering);
                cache.removeObservablePropertiesForOffering(offering);
                Set<String> resultTemplatesToRemove = cache.getResultTemplatesForOffering(offering);
                cache.removeResultTemplatesForOffering(offering);
                cache.removeResultTemplates(resultTemplatesToRemove);
                for (String resultTemplate : resultTemplatesToRemove) {
                    cache.removeFeaturesOfInterestForResultTemplate(resultTemplate);
                    cache.removeObservablePropertiesForResultTemplate(resultTemplate);
                }
                cache.removeOffering(offering);
                cache.removePublishedOffering(offering);
            }
        }

        try {
            getCacheFeederDAO().updateCacheOfferings(cache, offeringsNeedingReload);
        } catch (OwsExceptionReport ex) {
            fail(ex);
        }

        cache.removeRolesForRelatedFeatureNotIn(cache.getRelatedFeatures());
        cache.setFeaturesOfInterest(cache.getFeaturesOfInterestWithOffering());
        cache.setPublishedFeaturesOfInterest(cache.getFeaturesOfInterestWithOffering());

        // observable property relations
        for (String observableProperty : cache.getObservablePropertiesForProcedure(procedure)) {
            cache.removeProcedureForObservableProperty(observableProperty, procedure);
            cache.removeObservablePropertyForProcedure(procedure, observableProperty);
        }

        cache.clearCompositePhenomenonForProcedure(procedure);

        // At the latest
        cache.removeOfferingsForProcedure(procedure);
        cache.recalculatePhenomenonTime();
        cache.recalculateResultTime();
        cache.recalculateGlobalEnvelope();

        cache.removeComponentAggregationProcedure(procedure);
        cache.removeTypeInstanceProcedure(procedure);
        cache.removeTypeOfProcedure(procedure);
    }
}
