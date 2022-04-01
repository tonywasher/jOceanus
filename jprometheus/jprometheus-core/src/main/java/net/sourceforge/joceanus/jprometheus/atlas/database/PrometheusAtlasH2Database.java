/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.PrometheusLogicException;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * H2 Database.
 */
public class PrometheusAtlasH2Database
        extends PrometheusAtlasDatabase {
    /**
     * The database map.
     */
    private static final List<String> ACTIVEDBS = new ArrayList<>();

    /**
     * Constructor.
     * @param pPreferences the preferences
     */
    protected PrometheusAtlasH2Database(final PrometheusDatabasePreferences pPreferences) {
        super(PrometheusAtlasDatabaseType.H2, pPreferences);
    }

    @Override
    protected String getMaintenanceDatabase() {
        return null;
    }

    @Override
    protected String getConnectionString(final String pDatabase) {
        /* Create the buffer */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append("jdbc:h2:mem:")
                 .append(getDatabaseName(pDatabase))
                 .append(";DB_CLOSE_DELAY=-1");
        return myBuilder.toString();
    }

    @Override
    protected String getListDatabaseCommand() {
        return null;
    }

    @Override
    public List<String> listDatabases() throws OceanusException {
        /* Synchronise on the list */
        synchronized (ACTIVEDBS) {
            /* return copy of list */
            return new ArrayList<>(ACTIVEDBS);
        }
    }

    @Override
    public void createDatabase(final String pName) throws OceanusException {
        /* Synchronise on the list */
        synchronized (ACTIVEDBS) {
            /* If the database already exists */
            if (ACTIVEDBS.contains(pName)) {
                throw new PrometheusLogicException("Database " + pName + " already exists");
            }

            /* Add the name to the list */
            ACTIVEDBS.add(pName);
        }
    }

    @Override
    public void dropDatabase(final String pName) throws OceanusException {
        /* Synchronise on the list */
        synchronized (ACTIVEDBS) {
            /* If the database exists */
            if (ACTIVEDBS.contains(pName)) {
                /* Connect to the database */
                connectToDatabase(pName);

                /* Set Close delay to zero */
                final Connection myConn = getConnection();
                try (PreparedStatement myStatement = myConn.prepareStatement("set DB_CLOSE_DELAY 0")) {
                    myStatement.execute();
                } catch (SQLException e) {
                    throw new PrometheusDataException("Failed to set DB_CLOSE_DELAY", e);
                }

                /* Close the connection */
                closeDatabase();
            }

            /* Remove the name from the list */
            ACTIVEDBS.remove(pName);
        }
    }

    @Override
    public void connectToDatabase(final String pDatabase) throws OceanusException {
        /* Make sure that the database is in the list */
        synchronized (ACTIVEDBS) {
            /* If the database already exists */
            if (!ACTIVEDBS.contains(pDatabase)) {
                throw new PrometheusLogicException("Database " + pDatabase + " does not exist");
            }
        }

        /* Pass call on */
        super.connectToDatabase(pDatabase);
    }
}
