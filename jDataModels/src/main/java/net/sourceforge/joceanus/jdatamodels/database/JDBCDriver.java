/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jdatamodels.database;

import net.sourceforge.joceanus.jdatamodels.database.ColumnDefinition.ColumnType;
import net.sourceforge.joceanus.jdatamodels.preferences.DatabasePreferences;

/**
 * Database Drivers. Also code that encapsulates differences between databases.
 */
public enum JDBCDriver {
    /**
     * SQLServer.
     */
    SQLServer,

    /**
     * PostgreSQL.
     */
    PostgreSQL;

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
            case SQLServer:
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case PostgreSQL:
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
            case SQLServer:
                return true;
            case PostgreSQL:
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
            case SQLServer:
                return "jdbc:sqlserver://";
            case PostgreSQL:
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
            case SQLServer:
                /* Build the connection string */
                myBuilder.append(getPrefix());
                myBuilder.append(pPreferences.getStringValue(DatabasePreferences.NAME_DBSERVER));
                myBuilder.append(";instanceName=");
                myBuilder.append(pPreferences.getStringValue(DatabasePreferences.NAME_DBINSTANCE));
                myBuilder.append(";database=");
                myBuilder.append(pPreferences.getStringValue(DatabasePreferences.NAME_DBNAME));
                myBuilder.append(";integratedSecurity=true");
                break;
            case PostgreSQL:
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
        boolean isSQLServer = this.equals(SQLServer);
        switch (pType) {
            case Boolean:
                return (isSQLServer)
                        ? "bit"
                        : "boolean";
            case Short:
                return "smallint";
            case Integer:
                return "int";
            case Long:
                return "bigint";
            case Float:
                return "real";
            case Double:
                return (isSQLServer)
                        ? "float"
                        : "double precision";
            case Date:
                return "date";
            case Money:
                return (isSQLServer)
                        ? "money"
                        : "numeric(18,2)";
            case Decimal:
                return (isSQLServer)
                        ? "decimal"
                        : "numeric";
            case Binary:
                return (isSQLServer)
                        ? "varbinary"
                        : "bytea";
            case String:
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
            case SQLServer:
                myBuilder.append("if exists (select * from sys.tables where name = '");
                myBuilder.append(TableDefinition.QUOTE_STRING);
                myBuilder.append(pName);
                myBuilder.append(TableDefinition.QUOTE_STRING);
                myBuilder.append("') drop table ");
                myBuilder.append(pName);
                break;
            case PostgreSQL:
            default:
                myBuilder.append("drop table if exists ");
                myBuilder.append(TableDefinition.QUOTE_STRING);
                myBuilder.append(pName);
                myBuilder.append(TableDefinition.QUOTE_STRING);
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
            case SQLServer:
                myBuilder.append("if exists (select * from sys.indexes where name = '");
                myBuilder.append(TableDefinition.QUOTE_STRING);
                myBuilder.append(TableDefinition.PREFIX_INDEX);
                myBuilder.append(pName);
                myBuilder.append(TableDefinition.QUOTE_STRING);
                myBuilder.append("') drop index ");
                myBuilder.append(TableDefinition.QUOTE_STRING);
                myBuilder.append(TableDefinition.PREFIX_INDEX);
                myBuilder.append(pName);
                myBuilder.append(TableDefinition.QUOTE_STRING);
                break;
            case PostgreSQL:
            default:
                myBuilder.append("drop index if exists ");
                myBuilder.append(TableDefinition.QUOTE_STRING);
                myBuilder.append(TableDefinition.PREFIX_INDEX);
                myBuilder.append(pName);
                myBuilder.append(TableDefinition.QUOTE_STRING);
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
            case SQLServer:
                return true;
            case PostgreSQL:
            default:
                return false;
        }
    }
}
