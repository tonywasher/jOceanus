/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.preference;

/**
 * Database Drivers. Also code that encapsulates differences between databases.
 */
public enum JDBCDriver {
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
    MYSQL;

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
            theName = PreferenceResource.getKeyForDriver(this).getValue();
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
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case MYSQL:
                return "com.mysql.jdbc.Driver";
            case POSTGRESQL:
            default:
                return "org.postgresql.Driver";
        }
    }

    /**
     * Determine whether we use integrated security.
     * @return true/false
     */
    public boolean useIntegratedSecurity() {
        switch (this) {
            case SQLSERVER:
                return true;
            case MYSQL:
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
                return "jdbc:sqlserver://";
            case MYSQL:
                return "jdbc:mysql://";
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
    public String getConnectionString(final DatabasePreferences pPreferences) {
        /* Create the buffer */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        switch (this) {
            case SQLSERVER:
                /* Build the connection string */
                myBuilder.append(getPrefix());
                myBuilder.append(pPreferences.getStringValue(DatabasePreferences.NAME_DBSERVER));
                myBuilder.append(";instanceName=");
                myBuilder.append(pPreferences.getStringValue(DatabasePreferences.NAME_DBINSTANCE));
                myBuilder.append(";database=");
                myBuilder.append(pPreferences.getStringValue(DatabasePreferences.NAME_DBNAME));
                myBuilder.append(";integratedSecurity=true");
                break;
            case MYSQL:
            case POSTGRESQL:
            default:
                /* Build the connection string */
                myBuilder.append(getPrefix());
                myBuilder.append(pPreferences.getStringValue(DatabasePreferences.NAME_DBSERVER));
                myBuilder.append("/");
                myBuilder.append(pPreferences.getStringValue(DatabasePreferences.NAME_DBNAME));
                break;
        }

        /* Return the string */
        return myBuilder.toString();
    }

    /**
     * Obtain the database type for the field.
     * @param pType the data type
     * @return the database column type
     */
    public String getDatabaseType(final ColumnType pType) {
        boolean isSQLServer = this.equals(SQLSERVER);
        boolean isPostgreSQL = this.equals(POSTGRESQL);
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
                return (isSQLServer)
                                    ? "money"
                                    : "numeric(18,2)";
            case DECIMAL:
                return (isSQLServer)
                                    ? "decimal"
                                    : "numeric";
            case BINARY:
                return (isPostgreSQL)
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
                return true;
            case MYSQL:
            default:
                return false;
        }
    }
}
