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
package net.sourceforge.joceanus.jprometheus.atlas.database;

import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusColumnType;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceResource;

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
     * PostgreSQL.
     */
    MYSQL,

    /**
     * H2.
     */
    H2;

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = PrometheusPreferenceResource.getKeyForDriver(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain driver class.
     * @return the driver class
     */
    public String getDriver() {
        switch (this) {
            case SQLSERVER:
                return "net.sourceforge.jtds.jdbc.Driver";
            case MYSQL:
                return "com.mysql.jdbc.Driver";
            case H2:
                return "org.h2.Driver";
            case POSTGRESQL:
            default:
                return "org.postgresql.Driver";
        }
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
            case H2:
            case POSTGRESQL:
            default:
                return false;
        }
    }

    /**
     * Obtain connection prefix.
     * @return the connection prefix
     */
    public String getPrefix() {
        switch (this) {
            case SQLSERVER:
                return "jdbc:jtds:sqlserver://";
            case MYSQL:
                return "jdbc:mysql://";
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
        return getConnectionString(myDB, myServer);
    }

    /**
     * Get connection string.
     * @param pDatabase the database
     * @param pServer the server
     * @return the connection string
     */
    public String getConnectionString(final String pDatabase,
                                      final String pServer) {
        /* Create the buffer */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append(getPrefix());
        if (this != H2) {
            myBuilder.append(pServer);
            myBuilder.append("/");
        }
        myBuilder.append(pDatabase);
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
            default:
                return false;
        }
    }
}
