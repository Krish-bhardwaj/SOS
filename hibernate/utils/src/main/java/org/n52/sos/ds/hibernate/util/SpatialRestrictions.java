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

import org.hibernate.criterion.Criterion;
import org.locationtech.jts.geom.Geometry;
import org.n52.shetland.ogc.filter.FilterConstants.SpatialOperator;
import org.n52.shetland.ogc.filter.SpatialFilter;
import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.exception.ows.concrete.UnsupportedOperatorException;

/**
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public final class SpatialRestrictions {
    private static final int DWITHIN_DISTANCE = 10;

    private SpatialRestrictions() {
    }

    /**
     * Get the spatial restriction for the supplied filter.
     *
     * @param propertyName the column to apply the filter to
     * @param filter       the filter
     *
     * @return the criterion
     *
     * @throws OwsExceptionReport if the spatial filter is not supported
     */
    public static Criterion filter(String propertyName, SpatialFilter filter) throws OwsExceptionReport {
        return filter(propertyName, filter.getOperator(), filter.getGeometry().toGeometry());
    }

    /**
     * Get spatial filter restrictions.
     *
     * @param propertyName column name
     * @param operator     Spatial filter
     * @param geometry     the geometry
     *
     * @return filter restriction
     *
     * @throws OwsExceptionReport If the spatial filter is not supported
     */
    public static Criterion filter(String propertyName, SpatialOperator operator, Geometry geometry)
            throws OwsExceptionReport {
        switch (operator) {
            case BBOX:
                return within(propertyName, geometry);
            case Contains:
                return contains(propertyName, geometry);
            case Crosses:
                return crosses(propertyName, geometry);
            case Disjoint:
                return disjoint(propertyName, geometry);
            case DWithin:
                return distanceWithin(propertyName, geometry, DWITHIN_DISTANCE);
            case Equals:
                return eq(propertyName, geometry);
            case Intersects:
                return intersects(propertyName, geometry);
            case Overlaps:
                return overlaps(propertyName, geometry);
            case Touches:
                return touches(propertyName, geometry);
            case Within:
                return within(propertyName, geometry);
            case Beyond:
            default:
                throw new UnsupportedOperatorException(operator);
        }
    }

    public static Criterion eq(String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.eq(propertyName, value);
    }

    public static Criterion within(String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.within(propertyName, value);
    }

    public static Criterion contains(String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.contains(propertyName, value);
    }

    public static Criterion crosses(String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.crosses(propertyName, value);
    }

    public static Criterion disjoint(String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.disjoint(propertyName, value);
    }

    public static Criterion intersects(String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.intersects(propertyName, value);
    }

    public static Criterion overlaps(String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.overlaps(propertyName, value);
    }

    public static Criterion touches(String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.touches(propertyName, value);
    }

    public static Criterion distanceWithin(String propertyName, Geometry value, double distance) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.distanceWithin(propertyName,
                                                                                  value,
                                                                                  distance);
    }

    public static Criterion havingSRID(String propertyName, int srid) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.havingSRID(propertyName, srid);
    }

    public static Criterion isEmpty(String propertyName) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.isEmpty(propertyName);
    }

    public static Criterion isNotEmpty(String propertyName) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.isNotEmpty(propertyName);
    }

    public static Criterion spatialRestriction(int relation, String propertyName, Geometry value) {
        return org.hibernate.spatial.criterion.SpatialRestrictions.spatialRestriction(relation,
                                                                                      propertyName,
                                                                                      value);
    }

}
