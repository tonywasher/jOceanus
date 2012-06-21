/*******************************************************************************
 * JDataModel: Data models
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.models.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.PreferenceSet;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceSetChooser;
import uk.co.tolcroft.models.data.TaskControl;
import uk.co.tolcroft.models.database.ColumnDefinition.ColumnType;

/**
 * Class that encapsulates a database connection.
 * @param <T> the dataSet type.
 */
public abstract class Database<T extends DataSet<T>> implements PreferenceSetChooser {
    /**
     * Number of update steps per table (INSERT/UPDATE/DELETE).
     */
    private static final int NUM_STEPS_PER_TABLE = 3;

    /**
     * User property name.
     */
    private static final String PROPERTY_USER = "user";

    /**
     * Password property name.
     */
    private static final String PROPERTY_PASS = "password";

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Database connection.
     */
    private Connection theConn = null;

    /**
     * Batch Size.
     */
    private final Integer theBatchSize;

    /**
     * Database Driver.
     */
    private final JDBCDriver theDriver;

    /**
     * List of Database tables.
     */
    private final List<DatabaseTable<?>> theTables;

    @Override
    public Class<? extends PreferenceSet> getPreferenceSetClass() {
        return DatabasePreferences.class;
    }

    /**
     * Obtain the Driver.
     * @return the driver
     */
    protected JDBCDriver getDriver() {
        return theDriver;
    }

    /**
     * Database Properties.
     */
    public static class DatabasePreferences extends PreferenceSet {
        /**
         * Registry name for DataBase driver.
         */
        public static final String NAME_DBDRIVER = "DBDriver";

        /**
         * Registry name for DataBase server.
         */
        public static final String NAME_DBSERVER = "DBServer";

        /**
         * Registry name for DataBase instance.
         */
        public static final String NAME_DBINSTANCE = "DBInstance";

        /**
         * Registry name for DataBase name.
         */
        public static final String NAME_DBNAME = "DBName";

        /**
         * Registry name for DataBase batch size.
         */
        public static final String NAME_DBBATCH = "DBBatchSize";

        /**
         * Registry name for DataBase user.
         */
        public static final String NAME_DBUSER = "DBUser";

        /**
         * Registry name for DataBase password.
         */
        public static final String NAME_DBPASS = "DBPass";

        /**
         * Display name for DataBase driver.
         */
        private static final String DISPLAY_DBDRIVER = "Database Driver Class";

        /**
         * Display name for DataBase server.
         */
        private static final String DISPLAY_DBSERVER = "Server Host Machine";

        /**
         * Display name for DataBase instance.
         */
        private static final String DISPLAY_DBINSTANCE = "Server Instance";

        /**
         * Display name for DataBase name.
         */
        private static final String DISPLAY_DBNAME = "Database Name";

        /**
         * Display name for DataBase batch size.
         */
        private static final String DISPLAY_DBBATCH = "Batch Size";

        /**
         * Display name for DataBase user.
         */
        private static final String DISPLAY_DBUSER = "Database User";

        /**
         * Display name for DataBase password.
         */
        private static final String DISPLAY_DBPASS = "Database Password";

        /**
         * Default Database driver string.
         */
        private static final JDBCDriver DEFAULT_DBDRIVER = JDBCDriver.SQLServer;

        /**
         * Default Database connection string.
         */
        private static final String DEFAULT_DBSERVER = "localhost";

        /**
         * Default Database instance.
         */
        private static final String DEFAULT_DBINSTANCE = "SQLEXPRESS";

        /**
         * Default Database name.
         */
        private static final String DEFAULT_DBNAME = "Finance";

        /**
         * Default Database batch size.
         */
        private static final Integer DEFAULT_DBBATCH = BatchControl.DEF_BATCH_SIZE;

        /**
         * Default Database user.
         */
        private static final String DEFAULT_DBUSER = "FinanceUser";

        /**
         * Default Database password.
         */
        private static final String DEFAULT_DBPASS = "secret";

        /**
         * Constructor.
         * @throws JDataException on error
         */
        public DatabasePreferences() throws JDataException {
            super();
        }

        @Override
        protected void definePreferences() {
            /* Define the preferences */
            definePreference(NAME_DBDRIVER, JDBCDriver.class);
            definePreference(NAME_DBSERVER, PreferenceType.String);
            definePreference(NAME_DBINSTANCE, PreferenceType.String);
            definePreference(NAME_DBNAME, PreferenceType.String);
            definePreference(NAME_DBBATCH, PreferenceType.Integer);
            definePreference(NAME_DBUSER, PreferenceType.String);
            definePreference(NAME_DBPASS, PreferenceType.String);
        }

        @Override
        protected Object getDefaultValue(final String pName) {
            /* Handle default values */
            if (pName.equals(NAME_DBDRIVER)) {
                return DEFAULT_DBDRIVER;
            }
            if (pName.equals(NAME_DBSERVER)) {
                return DEFAULT_DBSERVER;
            }
            if (pName.equals(NAME_DBINSTANCE)) {
                return DEFAULT_DBINSTANCE;
            }
            if (pName.equals(NAME_DBNAME)) {
                return DEFAULT_DBNAME;
            }
            if (pName.equals(NAME_DBBATCH)) {
                return DEFAULT_DBBATCH;
            }
            if (pName.equals(NAME_DBUSER)) {
                return DEFAULT_DBUSER;
            }
            if (pName.equals(NAME_DBPASS)) {
                return DEFAULT_DBPASS;
            }
            return null;
        }

        @Override
        protected String getDisplayName(final String pName) {
            /* Handle default values */
            if (pName.equals(NAME_DBDRIVER)) {
                return DISPLAY_DBDRIVER;
            }
            if (pName.equals(NAME_DBSERVER)) {
                return DISPLAY_DBSERVER;
            }
            if (pName.equals(NAME_DBINSTANCE)) {
                return DISPLAY_DBINSTANCE;
            }
            if (pName.equals(NAME_DBNAME)) {
                return DISPLAY_DBNAME;
            }
            if (pName.equals(NAME_DBBATCH)) {
                return DISPLAY_DBBATCH;
            }
            if (pName.equals(NAME_DBUSER)) {
                return DISPLAY_DBUSER;
            }
            if (pName.equals(NAME_DBPASS)) {
                return DISPLAY_DBPASS;
            }
            return null;
        }
    }

    /**
     * JDBCDriver class.
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
                    return (isSQLServer) ? "bit" : "boolean";
                case Short:
                    return "smallint";
                case Integer:
                    return "int";
                case Long:
                    return "bigint";
                case Float:
                    return "real";
                case Double:
                    return (isSQLServer) ? "float" : "double precision";
                case Date:
                    return "date";
                case Money:
                    return "money";
                case Decimal:
                    return (isSQLServer) ? "decimal" : "numeric";
                case Binary:
                    return (isSQLServer) ? "varbinary" : "bytea";
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
                    myBuilder.append(pName);
                    myBuilder.append("') drop table ");
                    myBuilder.append(pName);
                    break;
                case PostgreSQL:
                default:
                    myBuilder.append("drop table if exists ");
                    myBuilder.append(pName);
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
                    myBuilder.append(TableDefinition.PREFIX_INDEX);
                    myBuilder.append(pName);
                    myBuilder.append("') drop index ");
                    myBuilder.append(TableDefinition.PREFIX_INDEX);
                    myBuilder.append(pName);
                    break;
                case PostgreSQL:
                default:
                    myBuilder.append("drop index if exists ");
                    myBuilder.append(TableDefinition.PREFIX_INDEX);
                    myBuilder.append(pName);
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

    /**
     * Construct a new Database class.
     * @throws JDataException on error
     */
    public Database() throws JDataException {
        /* Create the connection */
        try {
            /* Access the database preferences */
            DatabasePreferences myPreferences = (DatabasePreferences) PreferenceManager
                    .getPreferenceSet(this);

            /* Access the batch size */
            theBatchSize = myPreferences.getIntegerValue(DatabasePreferences.NAME_DBBATCH);

            /* Access the JDBC Driver */
            theDriver = myPreferences.getEnumValue(DatabasePreferences.NAME_DBDRIVER, JDBCDriver.class);

            /* Load the database driver */
            Class.forName(theDriver.getDriver());

            /* Obtain the connection */
            String myConnString = theDriver.getConnectionString(myPreferences);

            /* If we are using integrated security */
            if (theDriver.useIntegratedSecurity()) {
                /* Connect without userId/password */
                theConn = DriverManager.getConnection(myConnString);

                /* else we must use userId and password */
            } else {
                /* Create the properties and record user */
                Properties myProperties = new Properties();
                String myUser = myPreferences.getStringValue(DatabasePreferences.NAME_DBUSER);
                String myPass = myPreferences.getStringValue(DatabasePreferences.NAME_DBPASS);
                myProperties.setProperty(PROPERTY_USER, myUser);
                myProperties.setProperty(PROPERTY_PASS, myPass);

                /* Connect using properties */
                theConn = DriverManager.getConnection(myConnString, myProperties);
            }

            theConn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new JDataException(ExceptionClass.SQLSERVER, "Failed to locate driver", e);

        } catch (SQLException e) {
            throw new JDataException(ExceptionClass.SQLSERVER, "Failed to load driver", e);
        }

        /* Create table list and add the tables to the list */
        theTables = new ArrayList<DatabaseTable<?>>();
        theTables.add(new TableControlKeys(this));
        theTables.add(new TableDataKeys(this));
        theTables.add(new TableControl(this));
    }

    /**
     * Access the connection.
     * @return the connection
     */
    protected Connection getConn() {
        return theConn;
    }

    /**
     * Add a table.
     * @param pTable the Table to add
     */
    protected void addTable(final DatabaseTable<?> pTable) {
        pTable.getDefinition().resolveReferences(theTables);
        theTables.add(pTable);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    /**
     * Close the connection to the database rolling back any outstanding transaction.
     */
    protected void close() {
        /* Ignore if no connection */
        if (theConn == null) {
            return;
        }

        /* Protect against exceptions */
        try {
            /* Roll-back any outstanding transaction */
            theConn.rollback();

            /* Create the iterator */
            Iterator<DatabaseTable<?>> myIterator;
            DatabaseTable<?> myTable;
            myIterator = theTables.iterator();

            /* Loop through the tables */
            while (myIterator.hasNext()) {
                myTable = myIterator.next();

                /* Close the Statement */
                myTable.closeStmt();
            }

            /* Close the connection */
            theConn.close();

            /* Discard Exceptions */
        } catch (SQLException e) {
            theConn = null;
        }
    }

    /**
     * Load data from the database.
     * @param pTask the task control
     * @return the new DataSet
     * @throws JDataException on error
     */
    public T loadDatabase(final TaskControl<T> pTask) throws JDataException {
        boolean bContinue = true;

        /* Set the number of stages */
        if (!pTask.setNumStages(1 + theTables.size())) {
            return null;
        }

        /* Create an empty DataSet */
        T myData = pTask.getNewDataSet();

        /* Create the iterator */
        Iterator<DatabaseTable<?>> myIterator;
        DatabaseTable<?> myTable;
        myIterator = theTables.iterator();

        /* Loop through the tables */
        while ((bContinue) && (myIterator.hasNext())) {
            myTable = myIterator.next();

            /* Load the items */
            bContinue = myTable.loadItems(pTask, myData);
        }

        /* analyse the data */
        if (bContinue) {
            bContinue = pTask.setNewStage("Refreshing data");
        }

        /* Check for cancellation */
        if (!bContinue) {
            throw new JDataException(ExceptionClass.LOGIC, "Operation Cancelled");
        }

        /* Return the data */
        return myData;
    }

    /**
     * Update data into database.
     * @param pTask the task control
     * @param pData the data
     * @throws JDataException on error
     */
    public void updateDatabase(final TaskControl<T> pTask,
                               final T pData) throws JDataException {
        boolean bContinue = true;
        BatchControl myBatch = new BatchControl(theBatchSize);

        /* Set the number of stages */
        if (!pTask.setNumStages(NUM_STEPS_PER_TABLE * theTables.size())) {
            return;
        }

        /* Create the iterator */
        Iterator<DatabaseTable<?>> myIterator;
        DatabaseTable<?> myTable;
        myIterator = theTables.iterator();

        /* Loop through the tables */
        while ((bContinue) && (myIterator.hasNext())) {
            myTable = myIterator.next();

            /* Load the items */
            bContinue = myTable.insertItems(pTask, pData, myBatch);
        }

        /* Create the list iterator */
        ListIterator<DatabaseTable<?>> myListIterator;
        myListIterator = theTables.listIterator();

        /* Loop through the tables */
        while ((bContinue) && (myListIterator.hasNext())) {
            myTable = myListIterator.next();

            /* Load the items */
            bContinue = myTable.updateItems(pTask, myBatch);
        }

        /* Loop through the tables in reverse order */
        while ((bContinue) && (myListIterator.hasPrevious())) {
            myTable = myListIterator.previous();

            /* Delete items from the table */
            bContinue = myTable.deleteItems(pTask, myBatch);
        }

        /* If we have active work in the batch */
        if ((bContinue) && (myBatch.isActive())) {
            /* Commit the database */
            try {
                theConn.commit();
            } catch (Exception e) {
                close();
                throw new JDataException(ExceptionClass.SQLSERVER, "Failed to commit transction", e);
            }

            /* Commit the batch */
            myBatch.commitItems();
        }

        /* Check for cancellation */
        if (!bContinue) {
            throw new JDataException(ExceptionClass.LOGIC, "Operation Cancelled");
        }
    }

    /**
     * Create tables.
     * @param pTask the task control
     * @throws JDataException on error
     */
    public void createTables(final TaskControl<T> pTask) throws JDataException {
        /* Drop any existing tables */
        dropTables(pTask);

        /* Set the number of stages */
        if (!pTask.setNumStages(1)) {
            return;
        }

        /* Create the iterator */
        Iterator<DatabaseTable<?>> myIterator;
        DatabaseTable<?> myTable;
        myIterator = theTables.iterator();

        /* Loop through the tables */
        while (myIterator.hasNext()) {
            myTable = myIterator.next();

            /* Create the table */
            myTable.createTable();
        }
    }

    /**
     * Drop tables.
     * @param pTask the task control
     * @throws JDataException on error
     */
    public void dropTables(final TaskControl<T> pTask) throws JDataException {
        /* Set the number of stages */
        if (!pTask.setNumStages(1)) {
            return;
        }

        /* Create the iterator */
        ListIterator<DatabaseTable<?>> myIterator;
        DatabaseTable<?> myTable;
        myIterator = theTables.listIterator(theTables.size());

        /* Loop through the tables in reverse order */
        while (myIterator.hasPrevious()) {
            myTable = myIterator.previous();

            /* Drop the table */
            myTable.dropTable();
        }
    }

    /**
     * Purge tables.
     * @param pTask the task control
     * @throws JDataException on error
     */
    public void purgeTables(final TaskControl<T> pTask) throws JDataException {

        /* Set the number of stages */
        if (!pTask.setNumStages(1)) {
            return;
        }

        /* Create the iterator */
        ListIterator<DatabaseTable<?>> myIterator;
        DatabaseTable<?> myTable;
        myIterator = theTables.listIterator(theTables.size());

        /* Loop through the tables in reverse order */
        while (myIterator.hasPrevious()) {
            myTable = myIterator.previous();

            /* Purge the table */
            myTable.purgeTable();
        }
    }
}
