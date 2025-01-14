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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hibernate.criterion.Criterion;
import org.joda.time.DateTime;
import org.junit.Test;
import org.n52.shetland.ogc.filter.FilterConstants;
import org.n52.shetland.ogc.filter.TemporalFilter;
import org.n52.shetland.ogc.gml.time.TimeInstant;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;
import org.n52.sos.exception.ows.concrete.UnsupportedTimeException;
import org.n52.sos.exception.ows.concrete.UnsupportedValueReferenceException;

/**
 * Test with instant as temporal filter
 *
 * @author <a href="mailto:c.hollman@52north.org">Carsten Hollmann</a>
 *
 * @since 4.4.0
 */
public class TemporalRestrictionHqlInstantInstantTest implements TemporalRestrictionTestConstants {

    private static final String STE_LT_INSTANT = "samplingTimeEnd<:instant1";

    private static final String STS_GT_INSTANT_AND_STE_LT_INSTANT =
            "samplingTimeStart>:instant1 and samplingTimeEnd<:instant1";

    @Test
    public void testAfterPhenomenonTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_After, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo("samplingTimeStart>:instant1"));
    }

    @Test
    public void testBeforePhenomenonTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Before, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo("samplingTimeEnd<:instant1"));
    }

    @Test
    public void testEqualsPhenomenonTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Equals, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo("samplingTimeStart=:instant1 and samplingTimeEnd=:instant1"));
    }

    @Test
    public void testContainsPhenomenonTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Contains, PHENOMENON_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testDuringPhenomenonTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_During, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(STS_GT_INSTANT_AND_STE_LT_INSTANT));
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testBeginsPhenomenonTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Begins, PHENOMENON_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test
    public void testBegunByPhenomenonTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_BegunBy, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo("samplingTimeStart=:instant1"));
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testEndsPhenomenonTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Ends, PHENOMENON_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test
    public void testEndedByPhenomenonTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_EndedBy, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo("samplingTimeEnd=:instant1"));
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlapsPhenomenonTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Overlaps, PHENOMENON_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlappedByPhenomenonTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_OverlappedBy, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(STE_LT_INSTANT));
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMeetsPhenomenonTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Meets, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(STS_GT_INSTANT_AND_STE_LT_INSTANT));
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMetByPhenomenonTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_MetBy, PHENOMENON_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(STS_GT_INSTANT_AND_STE_LT_INSTANT));
    }

    @Test
    public void testAfterResultTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_After, RESULT_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(
                "((resultTime is not null and resultTime>:instant1) or (resultTime is null and samplingTimeEnd>:instant1))"));
    }

    @Test
    public void testBeforeResultTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Before, RESULT_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(
                "((resultTime is not null and resultTime<:instant1) or (resultTime is null and samplingTimeEnd<:instant1))"));
    }

    @Test
    public void testEqualsResultTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Equals, RESULT_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(
                "((resultTime is not null and resultTime=:instant1) or (resultTime is null and samplingTimeEnd=:instant1))"));
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testContainsResultTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Contains, RESULT_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(
                "((resultTime is not null and resultTime>:instant1 and resultTime<:instant1) or (resultTime is null and samplingTimeEnd>:instant1 and samplingTimeEnd<:instant1))"));
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testDuringResultTime()
            throws UnsupportedValueReferenceException, UnsupportedTimeException, UnsupportedOperatorException {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_During, RESULT_TIME);
        Criterion filterHql = SosTemporalRestrictions.filterHql(tf, 1);
        assertThat(filterHql.toString(), equalTo(
                "((resultTime is not null and resultTime>:resultTime1 and resultTime<:resultTime1) or (resultTime is null and samplingTimeEnd>:resultTime1 and samplingTimeEnd<:resultTime1))"));
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testBeginsResultTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Begins, RESULT_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testBegunByResultTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_BegunBy, RESULT_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testEndsResultTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Ends, RESULT_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testEndedByResultTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_EndedBy, RESULT_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlapsResultTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Overlaps, RESULT_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testOverlappedByResultTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_OverlappedBy, RESULT_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMeetsResultTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_Meets, RESULT_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    @Test(expected = UnsupportedTimeException.class)
    public void testMetByResultTime() throws OwsExceptionReport {
        TemporalFilter tf = create(FilterConstants.TimeOperator.TM_MetBy, RESULT_TIME);
        SosTemporalRestrictions.filterHql(tf, 1);
    }

    private TemporalFilter create(FilterConstants.TimeOperator op, String vr) {
        return new TemporalFilter(op, new TimeInstant(DateTime.now()), vr);
    }

}
