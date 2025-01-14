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
package org.n52.sos.ds.hibernate.admin;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.n52.faroe.annotation.Configurable;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.janmayen.lifecycle.Constructable;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.AbstractDeleteDeletedDataHandler;
import org.n52.sos.ds.hibernate.DeleteDataHelper;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.dao.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configurable
public class DeleteDeletedDataHandler implements AbstractDeleteDeletedDataHandler, DeleteDataHelper, Constructable {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteDeletedDataHandler.class);
    private HibernateSessionHolder sessionHolder;

    @Inject
    private DaoFactory daoFactory;

    @Inject
    private ConnectionProvider connectionProvider;

    @Override
    public void init() {
        sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public synchronized void deleteDeletedData() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = getHibernateSessionHolder().getSession();
            transaction = session.beginTransaction();
            Criteria c = daoFactory.getSeriesDAO()
                    .getDefaultAllSeriesCriteria(session)
                    .add(Restrictions.eq(DatasetEntity.PROPERTY_DELETED, true))
                    .add(Restrictions.eq(DatasetEntity.PROPERTY_PUBLISHED, false));
            List<DatasetEntity> list = c.list();
            if (list != null && !list.isEmpty()) {
                for (DatasetEntity dataset : list) {
                    deleteDataset(dataset, session);
                }
            }
            deleteDeletedObservations(session);
            session.flush();
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            getHibernateSessionHolder().returnSession(session);
        }
    }

    @Override
    public DaoFactory getDaoFactory() {
        return daoFactory;
    }

    @Override
    public Logger getLogger() {
        return LOG;
    }

    @Override
    public boolean isDeletePhysically() {
        return true;
    }

    private synchronized HibernateSessionHolder getHibernateSessionHolder() {
        return sessionHolder;
    }

}
