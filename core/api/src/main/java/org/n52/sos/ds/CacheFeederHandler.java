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
package org.n52.sos.ds;

import java.util.Collection;

import org.n52.shetland.ogc.ows.exception.OwsExceptionReport;
import org.n52.sos.cache.SosWritableContentCache;

/**
 * Interface for implementations of cache feeder Handlers. Used to feed the
 * CapabilitiesCache with data from the data source.
 *
 * @since 4.0.0
 */
public interface CacheFeederHandler {

    /**
     * Reload all cache data for a list of offerings, for instance after a
     * DeleteSensor event
     *
     * @param cache     The cache to update
     * @param offerings A list of offerings to update
     *
     * @throws OwsExceptionReport if an occurs during the cache update
     */
    void updateCacheOfferings(SosWritableContentCache cache,
                              Collection<String> offerings)
            throws OwsExceptionReport;

    /**
     * Updates the cached data.
     *
     * @param cache the cache to update
     *
     * @throws OwsExceptionReport if an occurs during the cache update
     */
    void updateCache(SosWritableContentCache cache)
            throws OwsExceptionReport;
}
