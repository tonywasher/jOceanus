/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
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

import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;

/**
 * SQLServer Database.
 */
public class PrometheusAtlasSQLServerDatabase
        extends PrometheusAtlasDatabase {
    /**
     * Constructor.
     * @param pPreferences the preferences
     */
    protected PrometheusAtlasSQLServerDatabase(final PrometheusDatabasePreferences pPreferences) {
        super(PrometheusAtlasDatabaseType.SQLSERVER, pPreferences);
    }

    @Override
    protected String getMaintenanceDatabase() {
        return "master";
    }

    @Override
    protected String getConnectionString(final String pDatabase) {
        /* Create the buffer */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        myBuilder.append("jdbc:jtds:sqlserver://")
                .append(getPreferences().getStringValue(PrometheusDatabasePreferenceKey.DBSERVER))
                .append('/')
                .append(getDatabaseName(pDatabase))
                .append(";instance=")
                .append(getPreferences().getStringValue(PrometheusDatabasePreferenceKey.DBINSTANCE));
        return myBuilder.toString();
    }

    @Override
    protected String getListDatabaseCommand() {
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
        final String myPrefix = getPrefix();
        myBuilder.append("select name from sys.databases");
        if (myPrefix != null) {
            myBuilder.append(" where name like '");
            myBuilder.append(myPrefix);
            myBuilder.append("%'");
        }
        return myBuilder.toString();
    }
}
