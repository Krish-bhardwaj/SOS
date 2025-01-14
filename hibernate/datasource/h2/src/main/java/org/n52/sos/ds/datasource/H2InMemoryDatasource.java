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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hibernate.boot.Metadata;
import org.n52.faroe.SettingDefinition;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import com.google.common.collect.ImmutableSet;

/**
 * TODO JavaDoc
 *
 * @author <a href="mailto:c.autermann@52north.org">Christian Autermann</a>
 *
 * @since 4.0.0
 */
public class H2InMemoryDatasource extends AbstractH2Datasource {
    private static final String DIALECT = "H2/GeoDB (in memory)";

    private static final String JDBC_URL =
            "jdbc:h2:mem:sos;DB_CLOSE_DELAY=-1;";

    @Override
    public String getDialectName() {
        return DIALECT;
    }

    @Override
    public Set<SettingDefinition<?>> getSettingDefinitions() {
        return ImmutableSet.<SettingDefinition<?>> of(getDatabaseConceptDefinition(), getDatabaseExtensionDefinition(),
                getFeatureConceptDefinition());
    }

    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        return true;
    }

    @Override
    public Properties getDatasourceProperties(Map<String, Object> settings) {
        Properties p = new Properties();
        p.put(HibernateConstants.CONNECTION_URL, JDBC_URL);
        p.put(HibernateConstants.DRIVER_CLASS, H2_DRIVER_CLASS);
        p.put(HibernateConstants.DIALECT, H2_DIALECT_CLASS);
        p.put(HibernateConstants.CONNECTION_USERNAME, DEFAULT_USERNAME);
        p.put(HibernateConstants.CONNECTION_PASSWORD, DEFAULT_PASSWORD);
        p.put(HibernateConstants.HBM2DDL_AUTO, HibernateConstants.HBM2DDL_CREATE);
        p.put(DATABASE_CONCEPT_KEY, settings.get(DATABASE_CONCEPT_KEY));
        addMappingFileDirectories(settings, p);
        return p;
    }

    @Override
    public boolean needsSchema() {
        return false;
    }

    @Override
    public Map<String, Object> parseDatasourceProperties(Properties current) {
        Map<String, Object> settings = new HashMap<>(4);
        settings.put(HIBERNATE_DIRECTORY, current.get(HIBERNATE_DIRECTORY));
        settings.put(DATABASE_CONCEPT_KEY,  current.getProperty(DATABASE_CONCEPT_KEY));
        return settings;
    }

    @Override
    protected void validatePrerequisites(Connection con, Metadata metadata, Map<String, Object> settings) {
    }

    @Override
    @SuppressFBWarnings("DMI_EMPTY_DB_PASSWORD")
    protected Connection openConnection(Map<String, Object> settings) throws SQLException {
        try {
            Class.forName(H2_DRIVER_CLASS);
            precheckDriver(JDBC_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
            return DriverManager.getConnection(JDBC_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected String toURL(Map<String, Object> settings) {
        return JDBC_URL;
    }

    @Override
    protected String[] parseURL(String url) {
        return new String[0];
    }

}
