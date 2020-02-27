/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.database;

import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Database.
 */
public abstract class PrometheusAtlasDatabase {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(PrometheusAtlasDatabase.class);

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
     * Database type.
     */
    private final PrometheusAtlasDatabaseType theType;

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
     * Quote database name?
     */
    private boolean quoteDatabase;

    /**
     * Do we convert prefixes to lowerCase?
     */
    private boolean prefixToLower;

    /**
     * Do we convert prefixes to upperCase?
     */
    private boolean prefixToUpper;

    /**
     * Constructor.
     * @param pType the database type
     * @param pPreferences the preferences
     */
    protected PrometheusAtlasDatabase(final PrometheusAtlasDatabaseType pType,
                                      final PrometheusDatabasePreferences pPreferences) {
        theType = pType;
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
     * Obtain the connection.
     * @return the connection
     */
    protected Connection getConnection() {
        return theConnection;
    }

    /**
     * Set the prefix.
     * @param pPrefix the prefix
     */
    public void setPrefix(final String pPrefix) {
        /* Store the prefix */
        thePrefix = pPrefix;

        /* Convert prefix to upperCase if required */
        if (prefixToUpper && thePrefix != null) {
            thePrefix = thePrefix.toUpperCase();
        }

        /* Convert prefix to lowerCase if required */
        if (prefixToLower && thePrefix != null) {
            thePrefix = thePrefix.toLowerCase();
        }
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
            final DatabaseMetaData myMetaData = theConnection.getMetaData();

            /* Switch off autoCommit for non-maintenance databases */
            theConnection.setAutoCommit(pDatabase == null);

            /* Determine whether we should quote identifiers */
            final boolean quoteIdentifiers = !myMetaData.supportsMixedCaseIdentifiers()
                                             && myMetaData.supportsMixedCaseQuotedIdentifiers();

            /* Determine whether we should quote database */
            quoteDatabase = quoteIdentifiers && theType.canQuoteDatabase();

            /* If we are not quoting database and we do not support mixed case */
            if (!quoteDatabase && !myMetaData.supportsMixedCaseIdentifiers()) {
                /* Determine whether prefix is switched to upper or lower case */
                prefixToLower = myMetaData.storesLowerCaseIdentifiers();
                prefixToUpper = myMetaData.storesUpperCaseIdentifiers();
            } else {
                prefixToLower = false;
                prefixToUpper = false;
            }

            /* Update the prefix appropriately */
            setPrefix(thePrefix);

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
    protected String getDatabaseName(final String pDatabase) {
        if (pDatabase == null) {
            return getMaintenanceDatabase();
        }

        /* If we have a prefix */
        if (thePrefix != null) {
            /* Build the full database name */
            final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
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
        final List<String> myList = new ArrayList<>();

        /* Protect against exceptions */
        final String myCommand = getListDatabaseCommand();
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
        /* create the command */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append("create database ");
        if (quoteDatabase) {
            myBuilder.append(QUOTE_STRING);
        }
        myBuilder.append(getDatabaseName(pName));
        if (quoteDatabase) {
            myBuilder.append(QUOTE_STRING);
        }
        final String myCommand = myBuilder.toString();

        /* Protect against exceptions */
        try (Statement myStatement = theConnection.createStatement()) {
            /* Create the database */
            myStatement.executeUpdate(myCommand);

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
        /* create the command */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append("drop database IF EXISTS ");
        if (quoteDatabase) {
            myBuilder.append(QUOTE_STRING);
        }
        myBuilder.append(getDatabaseName(pName));
        if (quoteDatabase) {
            myBuilder.append(QUOTE_STRING);
        }
        final String myCommand = myBuilder.toString();

        /* Protect against exceptions */
        try (Statement myStatement = theConnection.createStatement()) {
            /* drop the database */
            myStatement.executeUpdate(myCommand);

            /* Handle exceptions */
        } catch (SQLException e) {
            throw new PrometheusDataException("Failed to drop database", e);
        }
    }
}
