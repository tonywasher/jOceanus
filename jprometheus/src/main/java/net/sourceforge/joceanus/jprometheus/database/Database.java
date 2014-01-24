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
package net.sourceforge.joceanus.jprometheus.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.joceanus.jprometheus.JPrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.JPrometheusIOException;
import net.sourceforge.joceanus.jprometheus.JPrometheusLogicException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.preferences.DatabasePreferences;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class that encapsulates a database connection.
 * @param <T> the dataSet type.
 */
public abstract class Database<T extends DataSet<T, ?>> {
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
     * The logger.
     */
    private final Logger theLogger;

    /**
     * List of Database tables.
     */
    private final List<DatabaseTable<?, ?>> theTables;

    /**
     * Obtain the Driver.
     * @return the driver
     */
    protected JDBCDriver getDriver() {
        return theDriver;
    }

    /**
     * Construct a new Database class.
     * @param pLogger the logger
     * @param pPreferences the preferences
     * @throws JOceanusException on error
     */
    public Database(final Logger pLogger,
                    final DatabasePreferences pPreferences) throws JOceanusException {
        /* Create the connection */
        try {
            /* Store the logger */
            theLogger = pLogger;

            /* Access the batch size */
            theBatchSize = pPreferences.getIntegerValue(DatabasePreferences.NAME_DBBATCH);

            /* Access the JDBC Driver */
            theDriver = pPreferences.getEnumValue(DatabasePreferences.NAME_DBDRIVER, JDBCDriver.class);

            /* Load the database driver */
            Class.forName(theDriver.getDriver());

            /* Obtain the connection */
            String myConnString = theDriver.getConnectionString(pPreferences);

            /* If we are using integrated security */
            if (theDriver.useIntegratedSecurity()) {
                /* Connect without userId/password */
                theConn = DriverManager.getConnection(myConnString);

                /* else we must use userId and password */
            } else {
                /* Create the properties and record user */
                Properties myProperties = new Properties();
                String myUser = pPreferences.getStringValue(DatabasePreferences.NAME_DBUSER);
                String myPass = pPreferences.getStringValue(DatabasePreferences.NAME_DBPASS);
                myProperties.setProperty(PROPERTY_USER, myUser);
                myProperties.setProperty(PROPERTY_PASS, myPass);

                /* Connect using properties */
                theConn = DriverManager.getConnection(myConnString, myProperties);
            }

            theConn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new JPrometheusIOException("Failed to locate driver", e);

        } catch (SQLException e) {
            throw new JPrometheusIOException("Failed to load driver", e);
        }

        /* Create table list and add the tables to the list */
        theTables = new ArrayList<DatabaseTable<?, ?>>();
        theTables.add(new TableControlKeys(this));
        theTables.add(new TableDataKeys(this));
        theTables.add(new TableControlData(this));
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
    protected void addTable(final DatabaseTable<?, ?> pTable) {
        pTable.getDefinition().resolveReferences(theTables);
        theTables.add(pTable);
    }

    /**
     * Close the connection to the database rolling back any outstanding transaction.
     */
    public void close() {
        /* Ignore if no connection */
        if (theConn == null) {
            return;
        }

        /* Protect against exceptions */
        try {
            /* Roll-back any outstanding transaction */
            theConn.rollback();

            /* Create the iterator */
            Iterator<DatabaseTable<?, ?>> myIterator;
            DatabaseTable<?, ?> myTable;
            myIterator = theTables.iterator();

            /* Loop through the tables */
            while (myIterator.hasNext()) {
                myTable = myIterator.next();

                /* Close the Statement */
                myTable.closeStmt();
            }

            /* Close the connection */
            theConn.close();
            theConn = null;

            /* Discard Exceptions */
        } catch (SQLException e) {
            theLogger.log(Level.SEVERE, "Failed to close database connection", e);
            theConn = null;
        }
    }

    /**
     * Load data from the database.
     * @param pTask the task control
     * @return the new DataSet
     * @throws JOceanusException on error
     */
    public T loadDatabase(final TaskControl<T> pTask) throws JOceanusException {
        boolean bContinue = true;

        /* Set the number of stages */
        if (!pTask.setNumStages(1 + theTables.size())) {
            return null;
        }

        /* Create an empty DataSet */
        T myData = pTask.getNewDataSet();

        /* Create the iterator */
        Iterator<DatabaseTable<?, ?>> myIterator;
        DatabaseTable<?, ?> myTable;
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
            throw new JPrometheusLogicException("Operation Cancelled");
        }

        /* Return the data */
        return myData;
    }

    /**
     * Update data into database.
     * @param pTask the task control
     * @param pData the data
     * @throws JOceanusException on error
     */
    public void updateDatabase(final TaskControl<T> pTask,
                               final T pData) throws JOceanusException {
        boolean bContinue = true;
        BatchControl myBatch = new BatchControl(theBatchSize);

        /* Set the number of stages */
        if (!pTask.setNumStages(NUM_STEPS_PER_TABLE * theTables.size())) {
            return;
        }

        /* Create the iterator */
        Iterator<DatabaseTable<?, ?>> myIterator;
        DatabaseTable<?, ?> myTable;
        myIterator = theTables.iterator();

        /* Loop through the tables */
        while ((bContinue) && (myIterator.hasNext())) {
            myTable = myIterator.next();

            /* Load the items */
            bContinue = myTable.insertItems(pTask, pData, myBatch);
        }

        /* Create the list iterator */
        ListIterator<DatabaseTable<?, ?>> myListIterator;
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
            } catch (SQLException e) {
                close();
                throw new JPrometheusIOException("Failed to commit transaction", e);
            }

            /* Commit the batch */
            myBatch.commitItems();
        }

        /* Check for cancellation */
        if (!bContinue) {
            throw new JPrometheusCancelException("Operation Cancelled");
        }
    }

    /**
     * Create tables.
     * @param pTask the task control
     * @throws JOceanusException on error
     */
    public void createTables(final TaskControl<T> pTask) throws JOceanusException {
        /* Drop any existing tables */
        dropTables(pTask);

        /* Set the number of stages */
        if (!pTask.setNumStages(1)) {
            return;
        }

        /* Create the iterator */
        Iterator<DatabaseTable<?, ?>> myIterator;
        DatabaseTable<?, ?> myTable;
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
     * @throws JOceanusException on error
     */
    public void dropTables(final TaskControl<T> pTask) throws JOceanusException {
        /* Set the number of stages */
        if (!pTask.setNumStages(1)) {
            return;
        }

        /* Create the iterator */
        ListIterator<DatabaseTable<?, ?>> myIterator;
        DatabaseTable<?, ?> myTable;
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
     * @throws JOceanusException on error
     */
    public void purgeTables(final TaskControl<T> pTask) throws JOceanusException {

        /* Set the number of stages */
        if (!pTask.setNumStages(1)) {
            return;
        }

        /* Create the iterator */
        ListIterator<DatabaseTable<?, ?>> myIterator;
        DatabaseTable<?, ?> myTable;
        myIterator = theTables.listIterator(theTables.size());

        /* Loop through the tables in reverse order */
        while (myIterator.hasPrevious()) {
            myTable = myIterator.previous();

            /* Purge the table */
            myTable.purgeTable();
        }
    }
}
