/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.database;

import net.sourceforge.joceanus.jprometheus.database.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusColumnType;

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
            theName = PrometheusDBResource.getKeyForDriver(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Determine whether we use instance.
     * @return true/false
     */
    public boolean useInstance() {
        switch (this) {
            case SQLSERVER:
                return true;
            case MYSQL:
            case MARIADB:
            case H2:
            case POSTGRESQL:
            default:
                return false;
        }
    }

    /**
     * Determine whether we use quotes.
     * @return true/false
     */
    public boolean useQuotes() {
        switch (this) {
            case MYSQL:
            case MARIADB:
                return false;
            case SQLSERVER:
            case H2:
            case POSTGRESQL:
            default:
                return true;
        }
    }

    /**
     * Determine whether we use port.
     * @return true/false
     */
    public boolean usePort() {
        switch (this) {
            case MYSQL:
            case MARIADB:
            case POSTGRESQL:
            case SQLSERVER:
                return true;
            case H2:
            default:
                return false;
        }
    }

    /**
     * Obtain default port.
     * @return the default port
     */
    public Integer getDefaultPort() {
        switch (this) {
            case MYSQL:
            case MARIADB:
                return PORT_MARIADB;
            case POSTGRESQL:
                return PORT_POSTGRESQL;
            case SQLSERVER:
                return PORT_SQLEXPRESS;
            case H2:
            default:
                return null;
        }
    }

    /**
     * Obtain connection prefix.
     * @return the connection prefix
     */
    public String getPrefix() {
        switch (this) {
            case SQLSERVER:
                return "jdbc:sqlserver://";
            case MYSQL:
                return "jdbc:mysql://";
            case MARIADB:
                return "jdbc:mariadb://";
            case H2:
                return "jdbc:h2:mem:";
            case POSTGRESQL:
            default:
                return "jdbc:postgresql://";
        }
    }

    /**
     * Get connection string.
     * @param pPreferences the preferences
     * @return the connection string
     */
    public String getConnectionString(final PrometheusDatabasePreferences pPreferences) {
        /* Create the buffer */
        final String myDB = pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBNAME);
        final String myServer = pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBSERVER);
        final Integer myPort = pPreferences.getIntegerValue(PrometheusDatabasePreferenceKey.DBPORT);
        return getConnectionString(myDB, myServer, myPort);
    }

    /**
     * Get connection string.
     * @param pDatabase the database
     * @param pServer the server
     * @param pPort the port
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
     * Obtain the database type for the field.
     * @param pType the data type
     * @return the database column type
     */
    public String getDatabaseType(final PrometheusColumnType pType) {
        final boolean isSQLServer = this.equals(SQLSERVER);
        final boolean isPostgreSQL = this.equals(POSTGRESQL);
        switch (pType) {
            case BOOLEAN:
                return isPostgreSQL
                                    ? "boolean"
                                    : "bit";
            case SHORT:
                return "smallint";
            case INTEGER:
                return "int";
            case LONG:
                return "bigint";
            case FLOAT:
                return "real";
            case DOUBLE:
                return isSQLServer
                                   ? "float"
                                   : isPostgreSQL
                                                  ? "double precision"
                                                  : "double";
            case DATE:
                return "date";
            case MONEY:
                return isSQLServer
                                   ? "money"
                                   : "numeric(18,2)";
            case DECIMAL:
                return isSQLServer
                                   ? "decimal"
                                   : "numeric";
            case BINARY:
                return isPostgreSQL
                                    ? "bytea"
                                    : "varbinary";
            case STRING:
            default:
                return "varchar";
        }
    }

    /**
     * Should we define binary length?
     * @return true/false
     */
    public boolean defineBinaryLength() {
        switch (this) {
            case MYSQL:
            case MARIADB:
            case SQLSERVER:
            case H2:
                return true;
            case POSTGRESQL:
            default:
                return false;
        }
    }

    /**
     * Should we explicitly drop indexes?
     * @return true/false
     */
    public boolean explicitDropIndex() {
        switch (this) {
            case POSTGRESQL:
            case SQLSERVER:
            case H2:
                return true;
            case MYSQL:
            case MARIADB:
            default:
                return false;
        }
    }
}
