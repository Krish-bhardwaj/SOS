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

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.n52.iceland.ds.ConnectionProvider;
import org.n52.series.db.beans.DataEntity;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.ds.DeleteDeletedObservationDAO;
import org.n52.sos.ds.hibernate.HibernateSessionHolder;
import org.n52.sos.ds.hibernate.util.HibernateHelper;
import org.n52.sos.ds.hibernate.util.ScrollableIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 *
 * @deprecated see {@link DeleteDeletedDataHandler}
 */
@Deprecated
public class HibernateDeleteDeletedObservationsDAO implements DeleteDeletedObservationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateDeleteDeletedObservationsDAO.class);
    private HibernateSessionHolder sessionHolder;

    @Inject
    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.sessionHolder = new HibernateSessionHolder(connectionProvider);
    }

    @Override
    public void deleteDeletedObservations() throws OwsExceptionReport {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionHolder.getSession();
            transaction = session.beginTransaction();
            ScrollableIterable<DataEntity<?>> sr = ScrollableIterable.fromCriteria(getCriteria(session));
            try {
                for (DataEntity<?> o : sr) {
                    session.delete(o);
                }
            } finally {
                sr.close();
            }

            session.flush();
            transaction.commit();
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        } finally {
            sessionHolder.returnSession(session);
        }
    }

    /**
     * Get Hibernate Criteria for deleted observations and supported concept
     *
     * @param session
     *            Hibernate session
     * @return Criteria to query deleted observations
     */
    private Criteria getCriteria(Session session) {
        final Criteria criteria = session.createCriteria(DataEntity.class);
        criteria.add(Restrictions.eq(DataEntity.PROPERTY_DELETED, true));
        LOG.trace("QUERY getCriteria(): {}", HibernateHelper.getSqlString(criteria));
        return criteria;
    }
}
