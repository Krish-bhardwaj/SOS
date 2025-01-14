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
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;
import org.n52.faroe.ConfigurationError;
import org.n52.faroe.SettingDefinition;
import org.n52.faroe.settings.StringSettingDefinition;
import org.n52.hibernate.spatial.dialect.sqlserver.TimestampWithTimeZoneSqlServer2012SpatialDialect;
import org.n52.shetland.util.CollectionHelper;
import org.n52.sos.ds.hibernate.util.HibernateConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * Abstract class for MS SQL Server datasources
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 4.2.0
 *
 */
public abstract class AbstractSqlServerDatasource extends AbstractHibernateFullDBDatasource {

    protected static final String URL_INSTANCE = "instance=";

    protected static final String URL_DATABASE_NAME = "databaseName=";

    protected static final String INSTANCE_KEY = "jdbc.instance";

    protected static final String INSTANCE_TITLE = "SQL Server instance";

    protected static final String INSTANCE_DESCRIPTION = "Your SQL Server instance.";

    protected static final String SQL_SERVER_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    // TODO adjust make instance optional in regex
    protected static final Pattern JDBC_URL_PATTERN = Pattern.compile("^jdbc:sqlserver://([^:]+):([0-9]+)(?:;"
            + URL_INSTANCE + "([^:]+))?;" + URL_DATABASE_NAME + "([^:]+)");

    protected static final String USERNAME_DESCRIPTION =
            "Your database server user name. The default value for SQL Server is \"sqlserver\".";

    protected static final String USERNAME_DEFAULT_VALUE = "sqlserver";

    protected static final String PASSWORD_DESCRIPTION =
            "Your database server password. The default value is \"sqlserver\".";

    protected static final String PASSWORD_DEFAULT_VALUE = USERNAME_DEFAULT_VALUE;

    protected static final String HOST_DESCRIPTION =
            "Set this to the IP/net location of SQL Server database server. "
            + "The default value for SQL Server is \"localhost\".";

    protected static final String PORT_DESCRIPTION =
            "Set this to the port number of your SQL Server server. The default value for SQL Server is \"1433\".";

    protected static final int PORT_DEFAULT_VALUE = 1433;

    protected static final String SCHEMA_DEFAULT_VALUE = "dbo";

    private static final boolean PROVIDED_JDBC_DEFAULT_VALUE = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSqlServerDatasource.class);

    private static final int INSTANCE = 3;

    private static final int DATABASE = 4;

    private static final int PORT = 2;

    private static final int HOST = 1;

    private static final String ALTER_TABLE = "ALTER TABLE \"";

    public AbstractSqlServerDatasource() {
        super();
        setUsernameDefault(USERNAME_DEFAULT_VALUE);
        setUsernameDescription(USERNAME_DESCRIPTION);
        setPasswordDefault(PASSWORD_DEFAULT_VALUE);
        setPasswordDescription(PASSWORD_DESCRIPTION);
        setDatabaseDefault(DATABASE_DEFAULT_VALUE);
        setDatabaseDescription(DATABASE_DESCRIPTION);
        setHostDefault(HOST_DEFAULT_VALUE);
        setHostDescription(HOST_DESCRIPTION);
        setPortDefault(PORT_DEFAULT_VALUE);
        setPortDescription(PORT_DESCRIPTION);
        setSchemaDefault(SCHEMA_DEFAULT_VALUE);
        setSchemaDescription(SCHEMA_DESCRIPTION);
        setProvidedJdbcDefault(PROVIDED_JDBC_DEFAULT_VALUE);
    }

    @Override
    public Set<SettingDefinition<?>> getSettingDefinitions() {
        Set<SettingDefinition<?>> settingDefinitions = super.getSettingDefinitions();
        return CollectionHelper.union(Sets.<SettingDefinition<?>> newHashSet(createInstanceDefinition(null)),
                settingDefinitions);
    }

    @Override
    public Set<SettingDefinition<?>> getChangableSettingDefinitions(final Properties current) {
        final Map<String, Object> settings = parseDatasourceProperties(current);
        return CollectionHelper.union(Sets
                .<SettingDefinition<?>> newHashSet(createInstanceDefinition((String) settings.get(INSTANCE_KEY))),
                super.getChangableSettingDefinitions(current));
    }

    protected StringSettingDefinition createInstanceDefinition(String instanceValue) {
        StringSettingDefinition def = new StringSettingDefinition();
        def.setGroup(BASE_GROUP);
        def.setOrder(2);
        def.setKey(INSTANCE_KEY);
        def.setTitle(INSTANCE_TITLE);
        def.setDescription(INSTANCE_DESCRIPTION);
        def.setDefaultValue(instanceValue == null ? "" : instanceValue);
        def.setOptional(true);
        return def;
    }

    @Override
    protected Dialect createDialect() {
        return new TimestampWithTimeZoneSqlServer2012SpatialDialect();
    }

    @Override
    protected String getDriverClass() {
        return SQL_SERVER_DRIVER_CLASS;
    }

    @Override
    public boolean checkSchemaCreation(Map<String, Object> settings) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            stmt = conn.createStatement();
            final String schema = (String) settings.get(createSchemaDefinition().getKey());
            final String schemaPrefix = schema == null ? "" : "\"" + schema + "\".";
            final String testTable = schemaPrefix + "sos_installer_test_table";
            final String command =
                    String.format("BEGIN; " + "IF (OBJECT_ID('%1$s') >0 ) DROP TABLE %1$s; "
                            + "CREATE TABLE %1$s (id integer NOT NULL); " + "DROP TABLE %1$s; " + "END;", testTable);
            stmt.execute(command);
            return true;
        } catch (SQLException e) {
            return false;
        } finally {
            close(stmt);
            close(conn);
        }
    }

    @Override
    protected void validatePrerequisites(Connection con, Metadata metadata, Map<String, Object> settings) {
        checkClasspath();
    }

    private void checkClasspath() throws ConfigurationError {
        try {
            Class.forName(SQL_SERVER_DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationError("SQL Server jar file (sqljdbc.jar) must be "
                    + "included in the server classpath. ", e);
        }
    }

    /*
     * SQL-Server JDBC String specification:
     * https://msdn.microsoft.com/en-us/library/ms378428%28v=sql.110%29.aspx
     *
     * String url =
     * String.format("jdbc:sqlserver://%s:%d;instance=%s;databaseName=%s",
     * settings.get(HOST_KEY), settings.get(PORT_KEY),
     * settings.get(INSTANCE_KEY), settings.get(DATABASE_KEY));
     */
    @Override
    protected String toURL(Map<String, Object> settings) {
        StringBuilder builder = new StringBuilder("jdbc:sqlserver://");
        builder.append(settings.get(HOST_KEY)).append(':');
        builder.append(settings.get(PORT_KEY)).append(';');
        if (settings.containsKey(INSTANCE_KEY) &&
                settings.get(INSTANCE_KEY) != null &&
                settings.get(INSTANCE_KEY) instanceof String &&
                !((String) settings.get(INSTANCE_KEY)).isEmpty()) {
            builder.append(URL_INSTANCE).append(settings.get(INSTANCE_KEY)).append(';');
        }
        builder.append(URL_DATABASE_NAME).append(settings.get(DATABASE_KEY));
        return builder.toString();
    }

    @Override
    protected String[] parseURL(String url) {
        Matcher matcher = JDBC_URL_PATTERN.matcher(url);
        matcher.find();
        if (matcher.group(INSTANCE) == null) {
            return new String[] { matcher.group(HOST), matcher.group(PORT), matcher.group(DATABASE) };
        }
        return new String[] { matcher.group(HOST), matcher.group(PORT), matcher.group(DATABASE),
                matcher.group(INSTANCE) };
    }

    @Override
    public Map<String, Object> parseDatasourceProperties(final Properties current) {
        super.parseDatasourceProperties(current);
        final Map<String, Object> settings = super.parseDatasourceProperties(current);
        // parse optional instance
        final String[] parsed = parseURL(current.getProperty(HibernateConstants.CONNECTION_URL));
        // TODO what happens here
        if (parsed.length == 4) {
            settings.put(INSTANCE_KEY, parsed[3]);
        }
        return settings;
    }

    @Override
    public boolean supportsClear() {
        return true;
    }

    @Override
    public void clear(Properties properties) {
        Map<String, Object> settings = parseDatasourceProperties(properties);
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = openConnection(settings);
            Iterator<Table> tables = getMetadata(conn, settings).collectTableMappings().iterator();
            List<String> names = new LinkedList<>();
            while (tables.hasNext()) {
                Table table = tables.next();
                if (table.isPhysicalTable()) {
                    names.add(table.getName());
                }
            }
            if (!names.isEmpty()) {
                stmt = conn.createStatement();
                StringBuffer statement = new StringBuffer();
                // alter table MyOtherTable nocheck constraint all
                for (String table : names) {
                    statement = statement.append(ALTER_TABLE)
                            .append(table)
                            .append("\" NOCHECK CONSTRAINT ALL; ");
                }
                // delete from MyTable
                for (String table : names) {
                    statement = statement.append("DELETE from \"")
                            .append(table)
                            .append("\"; DBCC CHECKIDENT(\"")
                            .append(table)
                            .append("\", RESEED, 0); ");
                }
                // alter table MyOtherTable check constraint all
                for (String table : names) {
                    statement = statement.append(ALTER_TABLE)
                            .append(table)
                            .append("\" CHECK CONSTRAINT ALL; ");
                }
                statement =
                        statement.append("DBCC SHRINKDATABASE (")
                        .append(settings.get(DATABASE_KEY).toString())
                        .append(");");
                LOGGER.debug("Executed clear datasource SQL statement: {}", statement);
                stmt.execute(statement.toString());
            }
        } catch (SQLException ex) {
            throw new ConfigurationError(ex);
        } finally {
            close(stmt);
            close(conn);
        }
    }

    @Override
    protected Connection openConnection(Map<String, Object> settings) throws SQLException {
        try {
            String jdbc = toURL(settings);
            Class.forName(getDriverClass());
            String pass = (String) settings.get(HibernateConstants.CONNECTION_PASSWORD);
            String user = (String) settings.get(HibernateConstants.CONNECTION_USERNAME);
            precheckDriver(jdbc, user, pass);
            return DriverManager.getConnection(jdbc, user, pass);
        } catch (ClassNotFoundException ex) {
            throw new SQLException(ex);
        }
    }

}
