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
package net.sourceforge.joceanus.jprometheus.lethe.database;

import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusJDBCDriver;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

/**
 * Class that encapsulates a database connection.
 */
public abstract class PrometheusXDataStore {
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
     * Instance property name.
     */
    private static final String PROPERTY_INSTANCE = "instance";

    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(PrometheusXDataStore.class);

    /**
     * Database connection.
     */
    private Connection theConn;

    /**
     * Batch Size.
     */
    private final Integer theBatchSize;

    /**
     * Database Driver.
     */
    private final PrometheusJDBCDriver theDriver;

    /**
     * List of Database tables.
     */
    private final List<PrometheusXTableDataItem<?>> theTables;

    /**
     * Construct a new Database class.
     * @param pPreferences the preferences
     * @throws OceanusException on error
     */
    protected PrometheusXDataStore(final PrometheusDatabasePreferences pPreferences) throws OceanusException {
        /* Create the connection */
        try {
            /* Access the batch size */
            theBatchSize = pPreferences.getIntegerValue(PrometheusDatabasePreferenceKey.DBBATCH);

            /* Access the JDBC Driver */
            theDriver = pPreferences.getEnumValue(PrometheusDatabasePreferenceKey.DBDRIVER, PrometheusJDBCDriver.class);

            /* Obtain the connection */
            final String myConnString = theDriver.getConnectionString(pPreferences);

            /* Create the properties and record user */
            final Properties myProperties = new Properties();
            final String myUser = pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBUSER);
            final char[] myPass = pPreferences.getCharArrayValue(PrometheusDatabasePreferenceKey.DBPASS);
            myProperties.setProperty(PROPERTY_USER, myUser);
            myProperties.setProperty(PROPERTY_PASS, new String(myPass));

            /* If we are using instance */
            if (theDriver.useInstance()) {
                final String myInstance = pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBINSTANCE);
                myProperties.setProperty(PROPERTY_INSTANCE, myInstance);
            }

            /* Connect using properties */
            theConn = DriverManager.getConnection(myConnString, myProperties);

            /* Connect to the correct database */
            final String myCatalog = pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBNAME);
            theConn.setCatalog(myCatalog);

            /* Switch off autoCommit */
            theConn.setAutoCommit(false);

            /* handle exceptions */
        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to load driver", e);
        }

        /* Create table list and add the tables to the list */
        theTables = new ArrayList<>();
        theTables.add(new PrometheusXTableControlKeys(this));
        theTables.add(new PrometheusXTableDataKeySet(this));
        theTables.add(new PrometheusXTableControlData(this));
    }

    /**
     * Obtain the Driver.
     * @return the driver
     */
    protected PrometheusJDBCDriver getDriver() {
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
    protected void addTable(final PrometheusXTableDataItem<?> pTable) {
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
            for (PrometheusXTableDataItem<?> myTable : theTables) {
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
     * @param pReport the report
     * @param pData the new DataSet
     * @throws OceanusException on error
     */
    public void loadDatabase(final TethysUIThreadStatusReport pReport,
                             final DataSet pData) throws OceanusException {
        /* Initialise task */
        pReport.initTask("loadDatabase");
        pReport.setNumStages(theTables.size());

        /* Obtain the active profile */
        TethysProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("loadDatabase");

        /* Loop through the tables */
        for (PrometheusXTableDataItem<?> myTable : theTables) {
            /* Note the new step */
            myTask.startTask(myTable.getTableName());

            /* Load the items */
            myTable.loadItems(pReport, pData);
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Update data into database.
     * @param pReport the report
     * @param pData the data
     * @throws OceanusException on error
     */
    public void updateDatabase(final TethysUIThreadStatusReport pReport,
                               final DataSet pData) throws OceanusException {
        final PrometheusXBatchControl myBatch = new PrometheusXBatchControl(theBatchSize);

        /* Set the number of stages */
        pReport.setNumStages(NUM_STEPS_PER_TABLE * theTables.size());

        /* Obtain the active profile */
        TethysProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("updateDatabase");

        /* Loop through the tables */
        TethysProfile myStage = myTask.startTask("insertData");
        final Iterator<PrometheusXTableDataItem<?>> myIterator = theTables.iterator();
        while (myIterator.hasNext()) {
            final PrometheusXTableDataItem<?> myTable = myIterator.next();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* insert the items */
            myTable.insertItems(pReport, pData, myBatch);
        }

        /* Loop through the tables */
        myStage = myTask.startTask("updateData");
        final ListIterator<PrometheusXTableDataItem<?>> myListIterator = theTables.listIterator();
        while (myListIterator.hasNext()) {
            final PrometheusXTableDataItem<?> myTable = myListIterator.next();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* Load the items */
            myTable.updateItems(pReport, myBatch);
        }

        /* Loop through the tables in reverse order */
        myStage = myTask.startTask("deleteData");
        while (myListIterator.hasPrevious()) {
            final PrometheusXTableDataItem<?> myTable = myListIterator.previous();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* Delete items from the table */
            myTable.deleteItems(pReport, myBatch);
        }

        /* If we have active work in the batch */
        if (myBatch.isActive()) {
            /* Commit the database */
            try {
                theConn.commit();
            } catch (SQLException e) {
                close();
                throw new PrometheusIOException("Failed to commit transaction", e);
            }

            /* Commit the batch */
            myBatch.commitItems();
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Create tables.
     * @param pReport the report
     * @throws OceanusException on error
     */
    public void createTables(final TethysUIThreadStatusReport pReport) throws OceanusException {
        /* Set the number of stages */
        pReport.setNumStages(2);

        /* Drop any existing tables */
        dropTables(pReport);

        /* Obtain the active profile */
        TethysProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("createTables");

        /* Loop through the tables */
        final Iterator<PrometheusXTableDataItem<?>> myIterator = theTables.iterator();
        while (myIterator.hasNext()) {
            final PrometheusXTableDataItem<?> myTable = myIterator.next();

            /* Check for cancellation */
            pReport.checkForCancellation();

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
     * @param pReport the report
     * @throws OceanusException on error
     */
    private void dropTables(final TethysUIThreadStatusReport pReport) throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("dropTables");

        /* Loop through the tables in reverse order */
        final ListIterator<PrometheusXTableDataItem<?>> myIterator = theTables.listIterator(theTables.size());
        while (myIterator.hasPrevious()) {
            final PrometheusXTableDataItem<?> myTable = myIterator.previous();

            /* Check for cancellation */
            pReport.checkForCancellation();

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
     * @param pReport the report
     * @throws OceanusException on error
     */
    public void purgeTables(final TethysUIThreadStatusReport pReport) throws OceanusException {
        /* Set the number of stages */
        pReport.setNumStages(1);

        /* Obtain the active profile */
        TethysProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("purgeTables");

        /* Loop through the tables in reverse order */
        final ListIterator<PrometheusXTableDataItem<?>> myIterator = theTables.listIterator(theTables.size());
        while (myIterator.hasPrevious()) {
            final PrometheusXTableDataItem<?> myTable = myIterator.previous();

            /* Check for cancellation */
            pReport.checkForCancellation();

            /* Note the new step */
            myTask.startTask(myTable.getTableName());

            /* Purge the table */
            myTable.purgeTable();
        }

        /* Complete the task */
        myTask.end();
    }
}