/*
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
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
package org.n52.sos.ds.hibernate.cache.base;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.i18n.I18NDAO;
import org.n52.iceland.i18n.I18NDAORepository;
import org.n52.iceland.i18n.LocalizedString;
import org.n52.iceland.i18n.MultilingualString;
import org.n52.iceland.i18n.metadata.I18NOfferingMetadata;
import org.n52.iceland.util.CollectionHelper;
import org.n52.iceland.util.DateTimeHelper;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.OfferingEntity;
import org.n52.sos.ds.hibernate.cache.AbstractThreadableDatasourceCacheUpdate;
import org.n52.sos.ds.hibernate.cache.DatasourceCacheUpdateHelper;
import org.n52.sos.ds.hibernate.cache.ProcedureFlag;
import org.n52.sos.ds.hibernate.dao.ProcedureDAO;
import org.n52.sos.ds.hibernate.entities.ObservationConstellation;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ogc.sos.SosEnvelope;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 *
 * @author Christian Autermann <c.autermann@52north.org>
 *
 * @since 4.0.0
 */
public class OfferingCacheUpdateTask extends AbstractThreadableDatasourceCacheUpdate {

//    private final FeatureOfInterestDAO featureDAO = new FeatureOfInterestDAO();
    private final String identifier;
    private final Collection<DatasetEntity> datasets;
    private final OfferingEntity offering;
    private boolean obsConstSupported;
    private final boolean hasSamplingGeometry;

    private final Locale defaultLanguage;
    private final I18NDAORepository i18NDAORepository;

    /**
     * Constructor. Note: never pass in Hibernate objects that have been loaded
     * by a session in a different thread
     *
     * @param offering
     *            Offering identifier
     * @param observationConstellationInfos
     *            Observation Constellation info collection, passed in from
     *            parent update if supported
     * @param hasSamplingGeometry
     *            Indicator to execute or not the extent query for the Spatial
     *            Filtering Profile
     */
    public OfferingCacheUpdateTask(OfferingEntity offering,
                                   Collection<DatasetEntity> datasets,
                                   boolean hasSamplingGeometry,
                                   Locale defaultLanguage,
                                   I18NDAORepository i18NDAORepository) {
        this.offering = offering;
        this.identifier = offering.getDomainId();
        this.datasets = datasets;
        this.hasSamplingGeometry = hasSamplingGeometry;
        this.defaultLanguage = defaultLanguage;
        this.i18NDAORepository = i18NDAORepository;
    }

    protected void getOfferingInformationFromDbAndAddItToCacheMaps(Session session) throws OwsExceptionReport {
        // process all offering updates here (in multiple threads) which have
        // the potential to perform large
        // queries that aren't able to be loaded all at once. many (but not all)
        // of these can be avoided
        // if ObservationConstellation is supported

        // NOTE: Don't perform queries or load obecjts here unless you have to,
        // since they are performed once per offering

        getCache().addOffering(identifier);
        addOfferingNamesAndDescriptionsToCache(identifier, session);
        // only check once, check flag in other methods
        obsConstSupported = HibernateHelper.isEntitySupported(ObservationConstellation.class);
        // Procedures
        final Map<ProcedureFlag, Set<String>> procedureIdentifiers = getProcedureIdentifier(session);
        getCache().setProceduresForOffering(identifier, procedureIdentifiers.get(ProcedureFlag.PARENT));
        Set<String> hiddenChilds = procedureIdentifiers.get(ProcedureFlag.HIDDEN_CHILD);
        if (!hiddenChilds.isEmpty()) {
            getCache().setHiddenChildProceduresForOffering(identifier, hiddenChilds);
        }

        // Observable properties
        getCache().setObservablePropertiesForOffering(identifier, getObservablePropertyIdentifier(session));

        // Observation types
        getCache().setObservationTypesForOffering(identifier, offering.getObservationTypes());

        // Features of Interest
        getCache().setFeaturesOfInterestForOffering(identifier, DatasourceCacheUpdateHelper.getAllFeatureIdentifiersFromDatasets(datasets));
        getCache().setFeatureOfInterestTypesForOffering(identifier, offering.getFeatureTypes());

        // Spatial Envelope
        getCache().setEnvelopeForOffering(identifier, getEnvelopeForOffering(offering));
        
        // Temporal extent
        getCache().setMinPhenomenonTimeForOffering(identifier, DateTimeHelper.makeDateTime(offering.getPhenomenonTimeStart()));
        getCache().setMaxPhenomenonTimeForOffering(identifier, DateTimeHelper.makeDateTime(offering.getPhenomenonTimeEnd()));
        getCache().setMinResultTimeForOffering(identifier, DateTimeHelper.makeDateTime(offering.getResultTimeStart()));
        getCache().setMaxResultTimeForOffering(identifier,DateTimeHelper.makeDateTime(offering.getResultTimeEnd()));
    }

    protected void addOfferingNamesAndDescriptionsToCache(String identifier, Session session)
            throws OwsExceptionReport {
        final MultilingualString name;
        final MultilingualString description;

        I18NDAO<I18NOfferingMetadata> dao = i18NDAORepository.getDAO(I18NOfferingMetadata.class);

        if (dao != null) {
            I18NOfferingMetadata metadata = dao.getMetadata(identifier);
            name = metadata.getName();
            description = metadata.getDescription();
        } else {
            name = new MultilingualString();
            description = new MultilingualString();
            if (offering.isSetName()) {
                final Locale locale = defaultLanguage;
                name.addLocalization(locale, offering.getName());
            } else {
                String offeringName = identifier;
                if (offeringName.startsWith("http")) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf('/') + 1,
                                    offeringName.length());
                } else if (offeringName.startsWith("urn")) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf(':') + 1,
                                    offeringName.length());
                }
                if (offeringName.contains("#")) {
                    offeringName =
                            offeringName.substring(offeringName.lastIndexOf('#') + 1,
                                    offeringName.length());
                }
                name.addLocalization(defaultLanguage, offeringName);
            }
            if (offering.isSetDescription()) {
                final Locale locale  = defaultLanguage;
                description.addLocalization(locale, offering.getDescription());
            }
        }

        getCache().setI18nDescriptionForOffering(identifier, description);
        getCache().setI18nNameForOffering(identifier, name);
        addHumanReadableIdentifier(identifier, offering, name);
    }

    private void addHumanReadableIdentifier(String offeringId, OfferingEntity offering, MultilingualString name) {
        if (offering.isSetName()) {
            getCache().addOfferingIdentifierHumanReadableName(offeringId, offering.getName());
        } else {
            if (!name.isEmpty()) {
                Optional<LocalizedString> defaultName = name.getLocalization(defaultLanguage);
                if (defaultName.isPresent()) {
                    getCache().addOfferingIdentifierHumanReadableName(offeringId, defaultName.get().getText());
                } else {
                    getCache().addOfferingIdentifierHumanReadableName(offeringId, offeringId);
                }
            }
        }

    }

    protected Map<ProcedureFlag, Set<String>> getProcedureIdentifier(Session session) throws OwsExceptionReport {
        Set<String> procedures = new HashSet<>(0);
        Set<String> hiddenChilds = new HashSet<>(0);
        if (obsConstSupported) {
            if (CollectionHelper.isNotEmpty(datasets)) {
                procedures.addAll(DatasourceCacheUpdateHelper
                        .getAllProcedureIdentifiersFromDatasets(datasets,
                                ProcedureFlag.PARENT));
                hiddenChilds.addAll(DatasourceCacheUpdateHelper
                        .getAllProcedureIdentifiersFromDatasets(datasets,
                                ProcedureFlag.HIDDEN_CHILD));
            }
        } else {
            List<String> list = new ProcedureDAO().getProcedureIdentifiersForOffering(identifier, session);
            for (String procedureIdentifier : list) {
                procedures.add(procedureIdentifier);
            }
        }
        Map<ProcedureFlag, Set<String>> allProcedures = Maps.newEnumMap(ProcedureFlag.class);
        allProcedures.put(ProcedureFlag.PARENT, procedures);
        allProcedures.put(ProcedureFlag.HIDDEN_CHILD, hiddenChilds);
        return allProcedures;
    }

    protected Collection<String> getValidFeaturesOfInterestFrom(Collection<String> featureOfInterestIdentifiers) {
        Set<String> features = new HashSet<>(featureOfInterestIdentifiers.size());
        for (String featureIdentifier : featureOfInterestIdentifiers) {
            features.add(featureIdentifier);
        }
        return features;
    }

    protected Set<String> getObservablePropertyIdentifier(Session session) throws OwsExceptionReport {
        if (CollectionHelper.isNotEmpty(datasets)) {
            return DatasourceCacheUpdateHelper
                    .getAllObservablePropertyIdentifiersFromDatasets(datasets);
        } else {
            return Sets.newHashSet();
        }
    }

    protected SosEnvelope getEnvelopeForOffering(OfferingEntity offering) throws OwsExceptionReport {
        if (offering.hasEnvelope()) {
            return new SosEnvelope(offering.getEnvelope().getEnvelopeInternal(), offering.getEnvelope().getSRID());
        }
        return new SosEnvelope();
    }

    @Override
    public void execute() {
        try {
            getOfferingInformationFromDbAndAddItToCacheMaps(getSession());
        } catch (OwsExceptionReport owse) {
            getErrors().add(owse);
        }
    }
}
