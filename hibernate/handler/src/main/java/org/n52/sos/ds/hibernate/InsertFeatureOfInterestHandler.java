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
package org.n52.sos.ds.hibernate;

import javax.inject.Inject;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.http.HTTPStatus;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.ProcedureHistoryEntity;
import org.n52.shetland.ogc.gml.AbstractFeature;
import org.n52.shetland.ogc.ows.exception.NoApplicableCodeException;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.shetland.ogc.sos.SosConstants;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestRequest;
import org.n52.shetland.ogc.sos.ifoi.InsertFeatureOfInterestResponse;
import org.n52.sos.ds.AbstractInsertFeatureOfInterestHandler;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.n52.sos.ds.hibernate.util.HibernateHelper;

import com.google.common.annotations.VisibleForTesting;

public class InsertFeatureOfInterestHandler extends AbstractInsertFeatureOfInterestHandler implements Constructable {

    @Inject
    private ConnectionProvider connectionProvider;

    @Inject
    private DaoFactory daoFactory;

    private HibernateSessionHolder sessionHolder;

    public InsertFeatureOfInterestHandler() {
        super(SosConstants.SOS);
    }

    @Override
    public void init() {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public synchronized InsertFeatureOfInterestResponse insertFeatureOfInterest(InsertFeatureOfInterestRequest request)
            throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getHibernateSessionHolder().getSession();
            transaction = session.beginTransaction();
            for (AbstractFeature abstractFeature : request.getFeatureMembers()) {
                getDaoFactory().getFeatureDAO().insertFeature(abstractFeature, session);
            }
            session.flush();
            transaction.commit();
        } catch (final HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            handleHibernateException(he);
        } finally {
            getHibernateSessionHolder().returnSession(session);
        }
        InsertFeatureOfInterestResponse response = new InsertFeatureOfInterestResponse();
        response.set(request);
        return response;
    }

    @Override
    public boolean isSupported() {
        return HibernateHelper.isEntitySupported(ProcedureHistoryEntity.class);
    }

    private synchronized DaoFactory getDaoFactory() {
        return daoFactory;
    }

    private synchronized HibernateSessionHolder getHibernateSessionHolder() {
        return sessionHolder;
    }

    @VisibleForTesting
    protected synchronized void initForTesting(DaoFactory daoFactory, ConnectionProvider connectionProvider) {
        this.daoFactory = daoFactory;
        this.connectionProvider = connectionProvider;
    }

    protected void handleHibernateException(HibernateException he) throws OwsExceptionReport {
        HTTPStatus status = HTTPStatus.INTERNAL_SERVER_ERROR;
        String exceptionMsg = "Error while inserting new featureOfInterest!";
        throw new NoApplicableCodeException().causedBy(he).withMessage(exceptionMsg).setStatus(status);
    }

}
