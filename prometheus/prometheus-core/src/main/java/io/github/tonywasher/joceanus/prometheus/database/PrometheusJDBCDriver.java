/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.prometheus.database;

import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusColumnType;

/**
 * Database Drivers. Also code that encapsulates differences between databases.
 */
public enum PrometheusJDBCDriver {
    /**
     * SQLServer.
     */
    SQLSERVER,

    /**
     * PostgreSQL.
     */
    POSTGRESQL,

    /**
     * MySQL.
     */
    MYSQL,

    /**
     * MariaDB.
     */
    MARIADB,

    /**
     * H2.
     */
    H2;

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * DefaultPort for MariaDB/MySQL.
     */
    static final int PORT_MARIADB = 3306;

    /**
     * DefaultPort for PostgreSQL.
     */
    static final int PORT_POSTGRESQL = 5432;

    /**
     * DefaultPort for SQLExpress.
     */
    static final int PORT_SQLEXPRESS = 50843;

    /**
     * DefaultInstance for SQLExpress.
     */
    static final String INSTANCE_SQLEXPRESS = "SQLEXPRESS";

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForDriver(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Determine whether we use instance.
     *
     * @return true/false
     */
    public boolean useInstance() {
        return this == SQLSERVER;
    }

    /**
     * Determine whether we use quotes.
     *
     * @return true/false
     */
    public boolean useQuotes() {
        return switch (this) {
            case MYSQL, MARIADB -> false;
            default -> true;
        };
    }

    /**
     * Determine whether we use port.
     *
     * @return true/false
     */
    public boolean usePort() {
        return switch (this) {
            case MYSQL, MARIADB, POSTGRESQL, SQLSERVER -> true;
            default -> false;
        };
    }

    /**
     * Obtain default port.
     *
     * @return the default port
     */
    public Integer getDefaultPort() {
        return switch (this) {
            case MYSQL, MARIADB -> PORT_MARIADB;
            case POSTGRESQL -> PORT_POSTGRESQL;
            case SQLSERVER -> PORT_SQLEXPRESS;
            default -> null;
        };
    }

    /**
     * Obtain connection prefix.
     *
     * @return the connection prefix
     */
    public String getPrefix() {
        return switch (this) {
            case SQLSERVER -> "jdbc:sqlserver://";
            case MYSQL -> "jdbc:mysql://";
            case MARIADB -> "jdbc:mariadb://";
            case H2 -> "jdbc:h2:mem:";
            default -> "jdbc:postgresql://";
        };
    }

    /**
     * Get connection string.
     *
     * @param pDatabase the database
     * @param pServer   the server
     * @param pPort     the port
     * @return the connection string
     */
    public String getConnectionString(final String pDatabase,
                                      final String pServer,
                                      final Integer pPort) {
        /* Create the buffer */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append(getPrefix());
        if (this != H2) {
            myBuilder.append(pServer);
            if (pPort != null) {
                myBuilder.append(":");
                myBuilder.append(pPort);
            }
            if (this != SQLSERVER) {
                myBuilder.append("/");
            }
        }
        if (this != SQLSERVER) {
            myBuilder.append(pDatabase);
        }
        if (this == H2) {
            myBuilder.append(";DB_CLOSE_DELAY=-1");
        }

        /* Return the string */
        return myBuilder.toString();
    }

    /**
     * Get connection string for database create.
     *
     * @param pServer the server
     * @param pPort   the port
     * @return the connection string
     */
    public String getConnectionString(final String pServer,
                                      final Integer pPort) {
        /* Create the buffer */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append(getPrefix());
        myBuilder.append(pServer);
        if (pPort != null) {
            myBuilder.append(":");
            myBuilder.append(pPort);
        }
        if (this != SQLSERVER) {
            myBuilder.append("/");
        }
        if (this == POSTGRESQL) {
            myBuilder.append("postgres");
        }

        /* Return the string */
        return myBuilder.toString();
    }

    /**
     * Obtain the database type for the field.
     *
     * @param pType the data type
     * @return the database column type
     */
    public String getDatabaseType(final PrometheusColumnType pType) {
        final boolean isSQLServer = this.equals(SQLSERVER);
        final boolean isPostgreSQL = this.equals(POSTGRESQL);
        return switch (pType) {
            case BOOLEAN -> isPostgreSQL
                    ? "boolean"
                    : "bit";
            case SHORT -> "smallint";
            case INTEGER -> "int";
            case LONG -> "bigint";
            case FLOAT -> "real";
            case DOUBLE -> {
                if (isSQLServer) {
                    yield "float";
                }
                yield isPostgreSQL ? "double precision" : "double";
            }
            case DATE -> "date";
            case MONEY -> isSQLServer
                    ? "money"
                    : "numeric(18,2)";
            case DECIMAL -> isSQLServer
                    ? "decimal"
                    : "numeric";
            case BINARY -> isPostgreSQL
                    ? "bytea"
                    : "varbinary";
            default -> "varchar";
        };
    }

    /**
     * Should we define binary length?
     *
     * @return true/false
     */
    public boolean defineBinaryLength() {
        return switch (this) {
            case MYSQL, MARIADB, SQLSERVER, H2 -> true;
            default -> false;
        };
    }

    /**
     * Should we explicitly drop indexes?
     *
     * @return true/false
     */
    public boolean explicitDropIndex() {
        return switch (this) {
            case POSTGRESQL, SQLSERVER, H2 -> true;
            default -> false;
        };
    }

    /**
     * Obtain the resource bundleId for the driver.
     *
     * @param pDriver the driver
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForDriver(final PrometheusJDBCDriver pDriver) {
        return switch (pDriver) {
            case SQLSERVER -> PrometheusDBResource.DRIVER_SQLSERVER;
            case POSTGRESQL -> PrometheusDBResource.DRIVER_POSTGRESQL;
            case MYSQL -> PrometheusDBResource.DRIVER_MYSQL;
            case MARIADB -> PrometheusDBResource.DRIVER_MARIADB;
            case H2 -> PrometheusDBResource.DRIVER_H2;
            default -> throw new IllegalArgumentException();
        };
    }
}
