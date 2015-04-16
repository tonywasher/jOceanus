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

import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jprometheus.JPrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.JPrometheusIOException;
import net.sourceforge.joceanus.jprometheus.JPrometheusLogicException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.preference.DatabasePreferences;
import net.sourceforge.joceanus.jprometheus.preference.JDBCDriver;
import net.sourceforge.joceanus.jtethys.JOceanusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

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
    private final List<DatabaseTable<?, ?>> theTables;

    /**
     * Construct a new Database class.
     * @param pPreferences the preferences
     * @throws JOceanusException on error
     */
    public Database(final DatabasePreferences pPreferences) throws JOceanusException {
        /* Create the connection */
        try {
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
        theTables.add(new TableDataKeySet(this));
        theTables.add(new TableDataKeys(this));
        theTables.add(new TableControlData(this));
    }

    /**
     * Obtain the Driver.
     * @return the driver
     */
    protected JDBCDriver getDriver() {
        return theDriver;
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

            /* Loop through the tables */
            Iterator<DatabaseTable<?, ?>> myIterator = theTables.iterator();
            while (myIterator.hasNext()) {
                DatabaseTable<?, ?> myTable = myIterator.next();

                /* Close the Statement */
                myTable.closeStmt();
            }

            /* Close the connection */
            theConn.close();
            theConn = null;

            /* Discard Exceptions */
        } catch (SQLException e) {
            LOGGER.error("Failed to close database connection", e);
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

        /* Obtain the active profile */
        JDataProfile myTask = pTask.getActiveTask();
        myTask = myTask.startTask("loadDatabase");

        /* Create an empty DataSet */
        T myData = pTask.getNewDataSet();

        /* Loop through the tables */
        Iterator<DatabaseTable<?, ?>> myIterator = theTables.iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            DatabaseTable<?, ?> myTable = myIterator.next();

            /* Note the new step */
            myTask.startTask(myTable.getTableName());

            /* Load the items */
            bContinue = myTable.loadItems(pTask, myData);
        }

        /* analyse the data */
        if (bContinue) {
            bContinue = pTask.setNewStage("Refreshing data");
        }

        /* Complete the task */
        myTask.end();

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

        /* Obtain the active profile */
        JDataProfile myTask = pTask.getActiveTask();
        myTask = myTask.startTask("updateDatabase");

        /* Loop through the tables */
        JDataProfile myStage = myTask.startTask("insertData");
        Iterator<DatabaseTable<?, ?>> myIterator = theTables.iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            DatabaseTable<?, ?> myTable = myIterator.next();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* Load the items */
            bContinue = myTable.insertItems(pTask, pData, myBatch);
        }

        /* Loop through the tables */
        myStage = myTask.startTask("updateData");
        ListIterator<DatabaseTable<?, ?>> myListIterator = theTables.listIterator();
        while ((bContinue) && (myListIterator.hasNext())) {
            DatabaseTable<?, ?> myTable = myListIterator.next();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* Load the items */
            bContinue = myTable.updateItems(pTask, myBatch);
        }

        /* Loop through the tables in reverse order */
        myStage = myTask.startTask("deleteData");
        while ((bContinue) && (myListIterator.hasPrevious())) {
            DatabaseTable<?, ?> myTable = myListIterator.previous();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

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

        /* Complete the task */
        myTask.end();

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

        /* Obtain the active profile */
        JDataProfile myTask = pTask.getActiveTask();
        myTask = myTask.startTask("createTables");

        /* Loop through the tables */
        Iterator<DatabaseTable<?, ?>> myIterator = theTables.iterator();
        while (myIterator.hasNext()) {
            DatabaseTable<?, ?> myTable = myIterator.next();

            /* Note the new step */
            myTask.startTask(myTable.getTableName());

            /* Create the table */
            myTable.createTable();
        }

        /* Complete the task */
        myTask.end();
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

        /* Obtain the active profile */
        JDataProfile myTask = pTask.getActiveTask();
        myTask = myTask.startTask("dropTables");

        /* Loop through the tables in reverse order */
        ListIterator<DatabaseTable<?, ?>> myIterator = theTables.listIterator(theTables.size());
        while (myIterator.hasPrevious()) {
            DatabaseTable<?, ?> myTable = myIterator.previous();

            /* Note the new step */
            myTask.startTask(myTable.getTableName());

            /* Drop the table */
            myTable.dropTable();
        }

        /* Complete the task */
        myTask.end();
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

        /* Obtain the active profile */
        JDataProfile myTask = pTask.getActiveTask();
        myTask = myTask.startTask("purgeTables");

        /* Loop through the tables in reverse order */
        ListIterator<DatabaseTable<?, ?>> myIterator = theTables.listIterator(theTables.size());
        while (myIterator.hasPrevious()) {
            DatabaseTable<?, ?> myTable = myIterator.previous();

            /* Note the new step */
            myTask.startTask(myTable.getTableName());

            /* Purge the table */
            myTable.purgeTable();
        }

        /* Complete the task */
        myTask.end();
    }
}