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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.PreferenceSet;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceSetChooser;
import uk.co.tolcroft.models.data.TaskControl;

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
     * List of Database tables.
     */
    private final List<DatabaseTable<?>> theTables;

    @Override
    public Class<? extends PreferenceSet> getPreferenceSetClass() {
        return DatabasePreferences.class;
    }

    /**
     * Database Properties.
     */
    public static class DatabasePreferences extends PreferenceSet {
        /**
         * Registry name for DataBase driver.
         */
        protected static final String NAME_DBDRIVER = "DBDriver";

        /**
         * Registry name for DataBase server.
         */
        protected static final String NAME_DBSERVER = "DBServer";

        /**
         * Registry name for DataBase instance.
         */
        protected static final String NAME_DBINSTANCE = "DBInstance";

        /**
         * Registry name for DataBase name.
         */
        protected static final String NAME_DBNAME = "DBName";

        /**
         * Registry name for DataBase batch size.
         */
        protected static final String NAME_DBBATCH = "DBBatchSize";

        /**
         * Display name for DataBase driver.
         */
        protected static final String DISPLAY_DBDRIVER = "Database Driver Class";

        /**
         * Display name for DataBase server.
         */
        protected static final String DISPLAY_DBSERVER = "Server Host Machine";

        /**
         * Display name for DataBase instance.
         */
        protected static final String DISPLAY_DBINSTANCE = "Server Instance";

        /**
         * Display name for DataBase name.
         */
        protected static final String DISPLAY_DBNAME = "Database Name";

        /**
         * Display name for DataBase batch size.
         */
        protected static final String DISPLAY_DBBATCH = "Batch Size";

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
            return null;
        }

        /**
         * Obtain connection string.
         * @return the connection string
         */
        private String getConnectionString() {
            StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Access the driver */
            JDBCDriver myDriver = getEnumValue(NAME_DBDRIVER, JDBCDriver.class);

            /* Build the connection string */
            myBuilder.append(myDriver.getPrefix());
            myBuilder.append(getStringValue(NAME_DBSERVER));
            myBuilder.append(";instanceName=");
            myBuilder.append(getStringValue(NAME_DBINSTANCE));
            myBuilder.append(";database=");
            myBuilder.append(getStringValue(NAME_DBNAME));
            myBuilder.append(";integratedSecurity=true");

            /* Return the string */
            return myBuilder.toString();
        }
    }

    /**
     * JDBCDriver class.
     */
    public enum JDBCDriver {
        /**
         * SQLServer.
         */
        SQLServer;

        /**
         * Obtain driver class.
         * @return the driver class
         */
        public String getDriver() {
            switch (this) {
                case SQLServer:
                    return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
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
                case SQLServer:
                    return "jdbc:sqlserver://";
                default:
                    return null;
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

            /* Load the database driver */
            Class.forName(myPreferences.getEnumValue(DatabasePreferences.NAME_DBDRIVER, JDBCDriver.class)
                    .getDriver());

            /* Obtain the connection */
            theConn = DriverManager.getConnection(myPreferences.getConnectionString());
            theConn.setAutoCommit(false);
        } catch (Exception e) {
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
        if (theConn != null) {
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
        } catch (Exception e) {
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
