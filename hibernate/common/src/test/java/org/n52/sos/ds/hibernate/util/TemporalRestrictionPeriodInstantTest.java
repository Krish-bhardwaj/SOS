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
package org.n52.sos.ds.hibernate.util;

import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;

/**
 * Test with instant as temporal filter
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class TemporalRestrictionPeriodInstantTest extends TemporalRestrictionTest {
    @Override
    public TimeInstant createScenario(Session session) throws OwsExceptionReport {
        Transaction transaction = null;
        try {
            DateTime ref = new DateTime(DateTimeZone.UTC).minusDays(1);
            transaction = session.beginTransaction();
            HibernateObservationBuilder b = getBuilder(session);
            b.createObservation(TemporalRestrictionTest.Identifier.PI_BEGUN_BY_ID, ref, ref.plus(1));
            b.createObservation(TemporalRestrictionTest.Identifier.PI_ENDED_BY_ID, ref.minus(1), ref);
            b.createObservation(TemporalRestrictionTest.Identifier.PI_AFTER_ID, ref.plus(1), ref.plus(2));
            b.createObservation(TemporalRestrictionTest.Identifier.PI_BEFORE_ID, ref.minus(2), ref.minus(1));
            b.createObservation(TemporalRestrictionTest.Identifier.PI_CONTAINS_ID, ref.minus(1), ref.plus(1));
            session.flush();
            transaction.commit();
            return new TimeInstant(ref);
        } catch (HibernateException he) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw he;
        }
    }

    @Test
    public void testAfterPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_After);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(TemporalRestrictionTest.Identifier.PI_AFTER_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testAfterResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterResultTime(session, FilterConstants.TimeOperator.TM_After);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(TemporalRestrictionTest.Identifier.PI_AFTER_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testBeforePhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_Before);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(TemporalRestrictionTest.Identifier.PI_BEFORE_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testBeforeResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterResultTime(session, FilterConstants.TimeOperator.TM_Before);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered,
                    hasItems(TemporalRestrictionTest.Identifier.PI_BEFORE_ID,
                            TemporalRestrictionTest.Identifier.PI_ENDED_BY_ID,
                            TemporalRestrictionTest.Identifier.PI_CONTAINS_ID));
            assertThat(filtered, hasSize(3));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testEqualsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_Equals);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasSize(0));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testEqualsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterResultTime(session, FilterConstants.TimeOperator.TM_Equals);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(TemporalRestrictionTest.Identifier.PI_BEGUN_BY_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testContainsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_Contains);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(TemporalRestrictionTest.Identifier.PI_CONTAINS_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testContainsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_Contains);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testDuringPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_During);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testDuringResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_During);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testBeginsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_Begins);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testBeginsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_Begins);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testBegunByPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_BegunBy);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(TemporalRestrictionTest.Identifier.PI_BEGUN_BY_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testBegunByResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_BegunBy);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testEndsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_Ends);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testEndsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_Ends);
        } finally {
            returnSession(session);
        }
    }

    @Test
    public void testEndedByPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            Set<Identifier> filtered = filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_EndedBy);
            assertThat(filtered, is(notNullValue()));
            assertThat(filtered, hasItem(TemporalRestrictionTest.Identifier.PI_ENDED_BY_ID));
            assertThat(filtered, hasSize(1));
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testEndedByResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_EndedBy);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlapsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_Overlaps);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlapsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_Overlaps);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlappedByPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_OverlappedBy);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlappedByResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_OverlappedBy);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMeetsPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_Meets);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMeetsResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_Meets);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMetByPhenomenonTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterPhenomenonTime(session, FilterConstants.TimeOperator.TM_MetBy);
        } finally {
            returnSession(session);
        }
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMetByResultTime() throws OwsExceptionReport {
        Session session = getSession();
        try {
            filterResultTime(session, FilterConstants.TimeOperator.TM_MetBy);
        } finally {
            returnSession(session);
        }
    }
}
