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
package org.n52.sos.ds.datasource;

import java.util.Properties;
import java.util.Set;

import org.n52.faroe.SettingDefinition;

import com.google.common.collect.ImmutableSet;


/**
 * ProstgreSQL datasource for core mapping
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class PostgresCoreDatasource extends AbstractPostgresDatasource {

    private static final String DIALECT_NAME = "PostgreSQL/PostGIS Core";

    public PostgresCoreDatasource() {
        super();
    }

    @Override
    public String getDialectName() {
        return DIALECT_NAME;
    }

    @Override
    public boolean supportsClear() {
        return false;
    }

    @Override
    public Set<SettingDefinition<?>> getChangableSettingDefinitions(Properties current) {
        return filter(super.getChangableSettingDefinitions(current), ImmutableSet.of(BATCH_SIZE_KEY));
    }

    @Override
    public Set<SettingDefinition<?>> getSettingDefinitions() {
        return filter(super.getSettingDefinitions(), ImmutableSet.of(BATCH_SIZE_KEY));
    }
}
