/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database.
 */
public abstract class PrometheusAtlasDatabase {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusAtlasDatabase.class);

    /**
     * The quote string.
     */
    private static final String QUOTE_STRING = "\"";

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
    protected static final int BUFFER_LEN = 100;

    /**
     * Preferences.
     */
    private final PrometheusDatabasePreferences thePreferences;

    /**
     * Database prefix.
     */
    private String thePrefix;

    /**
     * Database connection.
     */
    private Connection theConnection;

    /**
     * Constructor.
     * @param pPreferences the preferences
     */
    protected PrometheusAtlasDatabase(final PrometheusDatabasePreferences pPreferences) {
        thePreferences = pPreferences;
    }

    /**
     * Obtain the preferences.
     * @return the preferences
     */
    PrometheusDatabasePreferences getPreferences() {
        return thePreferences;
    }

    /**
     * Obtain the prefix.
     * @return the prefix
     */
    String getPrefix() {
        return thePrefix;
    }

    /**
     * Set the prefix.
     * @param pPrefix the prefix
     */
    public void setPrefix(final String pPrefix) {
        thePrefix = pPrefix;
    }

    /**
     * Connect to database.
     * @param pDatabase the name of the database (or null for maintenance DB)
     * @throws OceanusException on error
     */
    public void connectToDatabase(final String pDatabase) throws OceanusException {
        /* Close any existing connection */
        closeDatabase();

        /* Create the connection */
        try {
            /* Obtain the connectionString */
            final String myConnString = getConnectionString(pDatabase);

            /* Create the properties and record user/password */
            final Properties myProperties = new Properties();
            final String myUser = thePreferences.getStringValue(PrometheusDatabasePreferenceKey.DBUSER);
            final char[] myPass = thePreferences.getCharArrayValue(PrometheusDatabasePreferenceKey.DBPASS);
            myProperties.setProperty(PROPERTY_USER, myUser);
            myProperties.setProperty(PROPERTY_PASS, new String(myPass));

            /* Connect using properties */
            theConnection = DriverManager.getConnection(myConnString, myProperties);

            /* Switch off autoCommit */
            theConnection.setAutoCommit(false);

            /* handle exceptions */
        } catch (SQLException e) {
            throw new PrometheusIOException("Failed to load driver", e);
        }
    }

    /**
     * Obtain the connection string for the database.
     * @param pDatabase the name of the database (or null for maintenance DB)
     * @return the connection string
     */
    protected abstract String getConnectionString(String pDatabase);

    /**
     * Determine database name.
     * @param pDatabase the name of the database (or null for maintenance DB)
     * @return the connection string
     */
    protected String getDatabaseName(String pDatabase) {
        if (pDatabase == null) {
            return getMaintenanceDatabase();
        }

        /* If we have a prefix */
        if (thePrefix != null) {
            /* Build the full database name */
            StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
            myBuilder.append(thePrefix);
            myBuilder.append(pDatabase);
            return myBuilder.toString();
        }

        /* Just return the database name */
        return pDatabase;
    }

    /**
     * Obtain the maintenance database name.
     * @return the maintenance database name
     */
    protected abstract String getMaintenanceDatabase();

    /**
     * Close the connection to the database, rolling back any outstanding transaction.
     */
    public void closeDatabase() {
        /* Ignore if no connection */
        if (theConnection == null) {
            return;
        }

        /* Protect against exceptions */
        try {
            /* Roll-back any outstanding transaction */
            theConnection.rollback();

            /* Close the connection */
            theConnection.close();
            theConnection = null;

            /* Discard Exceptions */
        } catch (SQLException e) {
            LOGGER.error("Failed to close database connection", e);
            theConnection = null;
        }
    }

    /**
     * List databases.
     * @return the list of databases
     * @throws OceanusException on error
     */
    public List<String> listDatabases() throws OceanusException {
        /* create the list */
        List<String> myList = new ArrayList<>();

        /* Protect against exceptions */
        String myCommand = getListDatabaseCommand();
        try (PreparedStatement myStatement = theConnection.prepareStatement(myCommand);
             ResultSet myResults = myStatement.executeQuery()) {

            /* Loop through the results */
            while (myResults.next()) {
                /* Obtain the database */
                String myDB = myResults.getString(1);

                /* If we have a prefix */
                if (thePrefix != null) {
                    /* If the item does not match */
                    if (!myDB.startsWith(thePrefix)) {
                        continue;
                    }

                    /* Strip the prefix */
                    myDB = myDB.substring(thePrefix.length());
                }

                /* Add the database to the list */
                myList.add(myDB);
            }

            /* Handle exceptions */
        } catch (SQLException e) {
            throw new PrometheusDataException("Failed to query databases", e);
        }

        /* Return the list */
        return myList;
    }

    /**
     * Obtain the listDatabases sqlCommand.
     * @return the sqlCommand
     */
    protected abstract String getListDatabaseCommand();

    /**
     * Create database.
     * @param pName the name of the database to create
     * @throws OceanusException on error
     */
    public void createDatabase(final String pName) throws OceanusException {
        /* create the list */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append("create database ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(getDatabaseName(pName));
        myBuilder.append(QUOTE_STRING);
        final String myCommand = myBuilder.toString();

        /* Protect against exceptions */
        try (PreparedStatement myStatement = theConnection.prepareStatement(myCommand)) {
            /* Create the database */
            myStatement.execute();

            /* Handle exceptions */
        } catch (SQLException e) {
            throw new PrometheusDataException("Failed to create database", e);
        }
    }

    /**
     * Drop database.
     * @param pName the name of the database to create
     * @throws OceanusException on error
     */
    public void dropDatabase(final String pName) throws OceanusException {
        /* create the list */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append("drop database IF EXISTS ");
        myBuilder.append(QUOTE_STRING);
        myBuilder.append(getDatabaseName(pName));
        myBuilder.append(QUOTE_STRING);
        final String myCommand = myBuilder.toString();

        /* Protect against exceptions */
        try (PreparedStatement myStatement = theConnection.prepareStatement(myCommand)) {
            /* Create the database */
            myStatement.execute();

            /* Handle exceptions */
        } catch (SQLException e) {
            throw new PrometheusDataException("Failed to drop database", e);
        }
    }
}
