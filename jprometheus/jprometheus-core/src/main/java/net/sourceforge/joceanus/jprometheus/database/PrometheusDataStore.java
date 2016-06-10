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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jprometheus.PrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.PrometheusLogicException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusJDBCDriver;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Class that encapsulates a database connection.
 * @param <T> the dataSet type.
 */
public abstract class PrometheusDataStore<T extends DataSet<T, ?>> {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusDataStore.class);

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
    private final List<PrometheusTableDataItem<?, ?>> theTables;

    /**
     * Construct a new Database class.
     * @param pPreferences the preferences
     * @throws OceanusException on error
     */
    public PrometheusDataStore(final PrometheusDatabasePreferences pPreferences) throws OceanusException {
        /* Create the connection */
        try {
            /* Access the batch size */
            theBatchSize = pPreferences.getIntegerValue(PrometheusDatabasePreferenceKey.DBBATCH);

            /* Access the JDBC Driver */
            theDriver = pPreferences.getEnumValue(PrometheusDatabasePreferenceKey.DBDRIVER, PrometheusJDBCDriver.class);

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
                String myUser = pPreferences.getStringValue(PrometheusDatabasePreferenceKey.DBUSER);
                char[] myPass = pPreferences.getCharArrayValue(PrometheusDatabasePreferenceKey.DBPASS);
                myProperties.setProperty(PROPERTY_USER, myUser);
                myProperties.setProperty(PROPERTY_PASS, new String(myPass));

                /* Connect using properties */
                theConn = DriverManager.getConnection(myConnString, myProperties);
            }

            theConn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new PrometheusIOException("Failed to locate driver", e);

        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to load driver", e);
        }

        /* Create table list and add the tables to the list */
        theTables = new ArrayList<>();
        theTables.add(new PrometheusTableControlKeys(this));
        theTables.add(new PrometheusTableDataKeySet(this));
        theTables.add(new PrometheusTableDataKeys(this));
        theTables.add(new PrometheusTableControlData(this));
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
    protected void addTable(final PrometheusTableDataItem<?, ?> pTable) {
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
            Iterator<PrometheusTableDataItem<?, ?>> myIterator = theTables.iterator();
            while (myIterator.hasNext()) {
                PrometheusTableDataItem<?, ?> myTable = myIterator.next();

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
    public void loadDatabase(final MetisThreadStatusReport pReport,
                             final T pData) throws OceanusException {
        /* Initialise the flag */
        boolean bContinue = true;

        /* Set the number of stages */
        if (!pReport.setNumStages(1 + theTables.size())) {
            return;
        }

        /* Obtain the active profile */
        MetisProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("loadDatabase");

        /* Loop through the tables */
        Iterator<PrometheusTableDataItem<?, ?>> myIterator = theTables.iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            PrometheusTableDataItem<?, ?> myTable = myIterator.next();

            /* Note the new step */
            myTask.startTask(myTable.getTableName());

            /* Load the items */
            bContinue = myTable.loadItems(pReport, pData);
        }

        /* analyse the data */
        if (bContinue) {
            bContinue = pReport.setNewStage("Refreshing data");
        }

        /* Complete the task */
        myTask.end();

        /* Check for cancellation */
        if (!bContinue) {
            throw new PrometheusLogicException("Operation Cancelled");
        }
    }

    /**
     * Update data into database.
     * @param pReport the report
     * @param pData the data
     * @throws OceanusException on error
     */
    public void updateDatabase(final MetisThreadStatusReport pReport,
                               final T pData) throws OceanusException {
        boolean bContinue = true;
        PrometheusBatchControl myBatch = new PrometheusBatchControl(theBatchSize);

        /* Set the number of stages */
        if (!pReport.setNumStages(NUM_STEPS_PER_TABLE * theTables.size())) {
            return;
        }

        /* Obtain the active profile */
        MetisProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("updateDatabase");

        /* Loop through the tables */
        MetisProfile myStage = myTask.startTask("insertData");
        Iterator<PrometheusTableDataItem<?, ?>> myIterator = theTables.iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            PrometheusTableDataItem<?, ?> myTable = myIterator.next();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* insert the items */
            bContinue = myTable.insertItems(pReport, pData, myBatch);
        }

        /* Loop through the tables */
        myStage = myTask.startTask("updateData");
        ListIterator<PrometheusTableDataItem<?, ?>> myListIterator = theTables.listIterator();
        while ((bContinue) && (myListIterator.hasNext())) {
            PrometheusTableDataItem<?, ?> myTable = myListIterator.next();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* Load the items */
            bContinue = myTable.updateItems(pReport, myBatch);
        }

        /* Loop through the tables in reverse order */
        myStage = myTask.startTask("deleteData");
        while ((bContinue) && (myListIterator.hasPrevious())) {
            PrometheusTableDataItem<?, ?> myTable = myListIterator.previous();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* Delete items from the table */
            bContinue = myTable.deleteItems(pReport, myBatch);
        }

        /* If we have active work in the batch */
        if ((bContinue) && (myBatch.isActive())) {
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

        /* Check for cancellation */
        if (!bContinue) {
            throw new PrometheusCancelException("Operation Cancelled");
        }
    }

    /**
     * Create tables.
     * @param pReport the report
     * @throws OceanusException on error
     */
    public void createTables(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Drop any existing tables */
        dropTables(pReport);

        /* Set the number of stages */
        if (!pReport.setNumStages(1)) {
            return;
        }

        /* Obtain the active profile */
        MetisProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("createTables");

        /* Loop through the tables */
        Iterator<PrometheusTableDataItem<?, ?>> myIterator = theTables.iterator();
        while (myIterator.hasNext()) {
            PrometheusTableDataItem<?, ?> myTable = myIterator.next();

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
    private void dropTables(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Set the number of stages */
        if (!pReport.setNumStages(1)) {
            return;
        }

        /* Obtain the active profile */
        MetisProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("dropTables");

        /* Loop through the tables in reverse order */
        ListIterator<PrometheusTableDataItem<?, ?>> myIterator = theTables.listIterator(theTables.size());
        while (myIterator.hasPrevious()) {
            PrometheusTableDataItem<?, ?> myTable = myIterator.previous();

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
    public void purgeTables(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Set the number of stages */
        if (!pReport.setNumStages(1)) {
            return;
        }

        /* Obtain the active profile */
        MetisProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("purgeTables");

        /* Loop through the tables in reverse order */
        ListIterator<PrometheusTableDataItem<?, ?>> myIterator = theTables.listIterator(theTables.size());
        while (myIterator.hasPrevious()) {
            PrometheusTableDataItem<?, ?> myTable = myIterator.previous();

            /* Note the new step */
            myTask.startTask(myTable.getTableName());

            /* Purge the table */
            myTable.purgeTable();
        }

        /* Complete the task */
        myTask.end();
    }
}
