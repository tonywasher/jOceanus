/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.database;

import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

/**
 * Class that encapsulates a database connection.
 */
public abstract class PrometheusDataStore {
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
     * Encrypt property name.
     */
    private static final String PROPERTY_ENCRYPT = "encrypt";

    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(PrometheusDataStore.class);

    /**
     * Database connection.
     */
    private Connection theConn;

    /**
     * Database name.
     */
    private String theDatabase;

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
    private final List<PrometheusTableDataItem<?>> theTables;

    /**
     * Construct a new Database class.
     * @param pDatabase the database
     * @param pConfig the config
     * @throws OceanusException on error
     */
    protected PrometheusDataStore(final String pDatabase,
                                  final PrometheusDBConfig pConfig) throws OceanusException {
        /* Create the connection */
        try {
            /* Access the batch size */
            theBatchSize = pConfig.getBatchSize();

            /* Access the JDBC Driver */
            theDriver = pConfig.getDriver();

            /* Store the name */
            theDatabase = pDatabase;

            /* Obtain the connection */
            final String myConnString = theDriver.getConnectionString(pDatabase, pConfig.getServer(), pConfig.getPort());

            /* Create the properties and record user */
            final Properties myProperties = new Properties();
            final String myUser = pConfig.getUser();
            final char[] myPass = pConfig.getPassword();
            myProperties.setProperty(PROPERTY_USER, myUser);
            myProperties.setProperty(PROPERTY_PASS, new String(myPass));

            /* If we are using instance */
            if (theDriver.useInstance()) {
                final String myInstance = pConfig.getInstance();
                myProperties.setProperty(PROPERTY_INSTANCE, myInstance);
                myProperties.setProperty(PROPERTY_ENCRYPT, "false");
            }

            /* Connect using properties */
            theConn = DriverManager.getConnection(myConnString, myProperties);

            /* Connect to the correct database */
            theConn.setCatalog(pDatabase);

            /* Switch off autoCommit */
            theConn.setAutoCommit(false);

            /* handle exceptions */
        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to load driver", e);
        }

        /* Create table list and add the tables to the list */
        theTables = new ArrayList<>();

        /* Loop through the tables */
        for (PrometheusCryptographyDataType myType : PrometheusCryptographyDataType.values()) {
            /* Create the sheet */
            theTables.add(newTable(myType));
        }
    }

    /**
     * Construct a new Database class.
     * @param pConfig the config
     * @throws OceanusException on error
     */
    protected PrometheusDataStore(final PrometheusDBConfig pConfig) throws OceanusException {
        /* Create the connection */
        try {
            /* Access the batch size */
            theBatchSize = pConfig.getBatchSize();

            /* Access the JDBC Driver */
            theDriver = pConfig.getDriver();

            /* Obtain the connection */
            final String myConnString = theDriver.getConnectionString(pConfig.getServer(), pConfig.getPort());

            /* Create the properties and record user */
            final Properties myProperties = new Properties();
            final String myUser = pConfig.getUser();
            final char[] myPass = pConfig.getPassword();
            myProperties.setProperty(PROPERTY_USER, myUser);
            myProperties.setProperty(PROPERTY_PASS, new String(myPass));

            /* If we are using instance */
            if (theDriver.useInstance()) {
                final String myInstance = pConfig.getInstance();
                myProperties.setProperty(PROPERTY_INSTANCE, myInstance);
                myProperties.setProperty(PROPERTY_ENCRYPT, "false");
            }

            /* Connect using properties */
            theConn = DriverManager.getConnection(myConnString, myProperties);

            /* Switch off autoCommit */
            theConn.setAutoCommit(false);

            /* handle exceptions */
        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to load driver", e);
        }

        /* Create table list and add the tables to the list */
        theTables = new ArrayList<>();

        /* Loop through the tables */
        for (PrometheusCryptographyDataType myType : PrometheusCryptographyDataType.values()) {
            /* Create the sheet */
            theTables.add(newTable(myType));
        }
    }

    /**
     * Obtain the database name.
     * @return the name
     */
    public String getName() {
        return theDatabase;
    }

    /**
     * Execute the statement outside a transaction.
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    void executeStatement(final String pStatement) throws OceanusException {
        /* Protect the statement and execute without commit */
        try (PreparedStatement myStmt = theConn.prepareStatement(pStatement)) {
            theConn.setAutoCommit(true);
            myStmt.execute();
            theConn.setAutoCommit(false);

        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to execute statement", e);
        }
    }

    /**
     * Create new sheet of required type.
     * @param pListType the list type
     * @return the new sheet
     */
    private PrometheusTableDataItem<?> newTable(final PrometheusCryptographyDataType pListType) {
        /* Switch on list Type */
        switch (pListType) {
            case CONTROLDATA:
                return new PrometheusTableControlData(this);
            case CONTROLKEY:
                return new PrometheusTableControlKeys(this);
            case CONTROLKEYSET:
                return new PrometheusTableControlKeySet(this);
            case DATAKEYSET:
                return new PrometheusTableDataKeySet(this);
            default:
                throw new IllegalArgumentException(pListType.toString());
        }
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
    protected void addTable(final PrometheusTableDataItem<?> pTable) {
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
            if (!theConn.getAutoCommit()) {
                theConn.rollback();
            }

            /* Loop through the tables */
            for (PrometheusTableDataItem<?> myTable : theTables) {
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
                             final PrometheusDataSet pData) throws OceanusException {
        /* Initialise task */
        pReport.initTask("loadDatabase");
        pReport.setNumStages(theTables.size());

        /* Obtain the active profile */
        TethysProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("loadDatabase");

        /* Loop through the tables */
        for (PrometheusTableDataItem<?> myTable : theTables) {
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
                               final PrometheusDataSet pData) throws OceanusException {
        /* Set the number of stages */
        final PrometheusBatchControl myBatch = new PrometheusBatchControl(theBatchSize);
        pReport.setNumStages(NUM_STEPS_PER_TABLE * theTables.size());

        /* Obtain the active profile */
        TethysProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("updateDatabase");

        /* Loop through the tables */
        TethysProfile myStage = myTask.startTask("insertData");
        final Iterator<PrometheusTableDataItem<?>> myIterator = theTables.iterator();
        while (myIterator.hasNext()) {
            final PrometheusTableDataItem<?> myTable = myIterator.next();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* insert the items */
            myTable.insertItems(pReport, pData, myBatch);
        }

        /* Loop through the tables */
        myStage = myTask.startTask("updateData");
        final ListIterator<PrometheusTableDataItem<?>> myListIterator = theTables.listIterator();
        while (myListIterator.hasNext()) {
            final PrometheusTableDataItem<?> myTable = myListIterator.next();

            /* Note the new step */
            myStage.startTask(myTable.getTableName());

            /* Load the items */
            myTable.updateItems(pReport, myBatch);
        }

        /* Loop through the tables in reverse order */
        myStage = myTask.startTask("deleteData");
        while (myListIterator.hasPrevious()) {
            final PrometheusTableDataItem<?> myTable = myListIterator.previous();

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
     * Create database.
     * @param pReport the report
     * @param pDatabase the database to create
     * @throws OceanusException on error
     */
    public void createDatabase(final TethysUIThreadStatusReport pReport,
                               final String pDatabase) throws OceanusException {
        /* Set the number of stages */
        pReport.setNumStages(2);

        /* Obtain the active profile */
        TethysProfile myTask = pReport.getActiveTask();
        myTask = myTask.startTask("dropDatabase");
        executeStatement("DROP DATABASE IF EXISTS " + pDatabase);

        /* Create database */
        myTask = myTask.startTask("createDatabase");
        executeStatement("CREATE DATABASE " + pDatabase);

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
        final Iterator<PrometheusTableDataItem<?>> myIterator = theTables.iterator();
        while (myIterator.hasNext()) {
            final PrometheusTableDataItem<?> myTable = myIterator.next();

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
        final ListIterator<PrometheusTableDataItem<?>> myIterator = theTables.listIterator(theTables.size());
        while (myIterator.hasPrevious()) {
            final PrometheusTableDataItem<?> myTable = myIterator.previous();

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
        final ListIterator<PrometheusTableDataItem<?>> myIterator = theTables.listIterator(theTables.size());
        while (myIterator.hasPrevious()) {
            final PrometheusTableDataItem<?> myTable = myIterator.previous();

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
