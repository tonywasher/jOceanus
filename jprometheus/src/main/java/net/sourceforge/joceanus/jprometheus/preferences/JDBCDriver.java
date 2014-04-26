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
package net.sourceforge.joceanus.jprometheus.preferences;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition.ColumnType;

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
    POSTGRESQL;

    /**
     * The index prefix.
     */
    public static final String PREFIX_INDEX = "idx_";

    /**
     * The quote string.
     */
    public static final String QUOTE_STRING = "\"";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(JDBCDriver.class.getName());

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Obtain driver class.
     * @return the driver class
     */
    public String getDriver() {
        switch (this) {
            case SQLSERVER:
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
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
        switch (pType) {
            case BOOLEAN:
                return (isSQLServer)
                                    ? "bit"
                                    : "boolean";
            case SHORT:
                return "smallint";
            case INTEGER:
                return "int";
            case LONG:
                return "bigint";
            case FLOAT:
                return "real";
            case DOUBLE:
                return (isSQLServer)
                                    ? "float"
                                    : "double precision";
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
                return (isSQLServer)
                                    ? "varbinary"
                                    : "bytea";
            case STRING:
            default:
                return "varchar";
        }
    }

    /**
     * Get Drop table command.
     * @param pName the table name
     * @return the command
     */
    public String getDropTableCommand(final String pName) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        switch (this) {
            case SQLSERVER:
                myBuilder.append("if exists (select * from sys.tables where name = '");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(pName);
                myBuilder.append(QUOTE_STRING);
                myBuilder.append("') drop table ");
                myBuilder.append(pName);
                break;
            case POSTGRESQL:
            default:
                myBuilder.append("drop table if exists ");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(pName);
                myBuilder.append(QUOTE_STRING);
                break;
        }

        /* Return the command */
        return myBuilder.toString();
    }

    /**
     * Get Drop index command.
     * @param pName the table name
     * @return the command
     */
    public String getDropIndexCommand(final String pName) {
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        switch (this) {
            case SQLSERVER:
                myBuilder.append("if exists (select * from sys.indexes where name = '");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(PREFIX_INDEX);
                myBuilder.append(pName);
                myBuilder.append(QUOTE_STRING);
                myBuilder.append("') drop index ");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(PREFIX_INDEX);
                myBuilder.append(pName);
                myBuilder.append(QUOTE_STRING);
                break;
            case POSTGRESQL:
            default:
                myBuilder.append("drop index if exists ");
                myBuilder.append(QUOTE_STRING);
                myBuilder.append(PREFIX_INDEX);
                myBuilder.append(pName);
                myBuilder.append(QUOTE_STRING);
                break;
        }

        /* Return the command */
        return myBuilder.toString();
    }

    /**
     * Should we define binary length?
     * @return true/false
     */
    public boolean defineBinaryLength() {
        switch (this) {
            case SQLSERVER:
                return true;
            case POSTGRESQL:
            default:
                return false;
        }
    }
}
