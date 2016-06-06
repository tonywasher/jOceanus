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
package net.sourceforge.joceanus.jprometheus.preference;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Preferences for a database.
 */
public final class PrometheusDatabase {
    /**
     * Constructor.
     */
    private PrometheusDatabase() {
    }

    /**
     * databasePreferenceKeys.
     */
    public enum PrometheusDatabasePreferenceKey implements MetisPreferenceKey {
        /**
         * Database Driver.
         */
        DBDRIVER("DBDriver", PrometheusPreferenceResource.DBPREF_DRIVER),

        /**
         * Database Server.
         */
        DBSERVER("DBServer", PrometheusPreferenceResource.DBPREF_SERVER),

        /**
         * Database Instance.
         */
        DBINSTANCE("DBInstance", PrometheusPreferenceResource.DBPREF_INSTANCE),

        /**
         * Database Name.
         */
        DBNAME("DBName", PrometheusPreferenceResource.DBPREF_NAME),

        /**
         * Database Batch.
         */
        DBBATCH("DBBatchSize", PrometheusPreferenceResource.DBPREF_BATCH),

        /**
         * Database User.
         */
        DBUSER("DBUser", PrometheusPreferenceResource.DBPREF_USER),

        /**
         * Database Password.
         */
        DBPASS("DBPass", PrometheusPreferenceResource.DBPREF_PASS);

        /**
         * The name of the Preference.
         */
        private final String theName;

        /**
         * The display string.
         */
        private final String theDisplay;

        /**
         * Constructor.
         * @param pName the name
         * @param pDisplay the display string;
         */
        PrometheusDatabasePreferenceKey(final String pName,
                                        final PrometheusPreferenceResource pDisplay) {
            theName = pName;
            theDisplay = pDisplay.getValue();
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public String getDisplay() {
            return theDisplay;
        }
    }

    /**
     * PrometheusDatabasePreferences.
     */
    public static class PrometheusDatabasePreferences
            extends MetisPreferenceSet<PrometheusDatabasePreferenceKey> {
        /**
         * Default Database batch size.
         */
        private static final Integer DEFAULT_DBBATCH = 50;

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public PrometheusDatabasePreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, PrometheusDatabasePreferenceKey.class, PrometheusPreferenceResource.DBPREF_PREFNAME);
        }

        @Override
        protected void definePreferences() throws OceanusException {
            defineEnumPreference(PrometheusDatabasePreferenceKey.DBDRIVER, PrometheusJDBCDriver.class);
            defineStringPreference(PrometheusDatabasePreferenceKey.DBSERVER);
            defineStringPreference(PrometheusDatabasePreferenceKey.DBINSTANCE);
            defineStringPreference(PrometheusDatabasePreferenceKey.DBNAME);
            defineIntegerPreference(PrometheusDatabasePreferenceKey.DBBATCH);
            defineStringPreference(PrometheusDatabasePreferenceKey.DBUSER);
            defineCharArrayPreference(PrometheusDatabasePreferenceKey.DBPASS);
        }

        @Override
        protected void autoCorrectPreferences() {
            /* Make sure that the enum is specified */
            MetisEnumPreference<PrometheusDatabasePreferenceKey, PrometheusJDBCDriver> myTypePref = getEnumPreference(PrometheusDatabasePreferenceKey.DBDRIVER, PrometheusJDBCDriver.class);
            if (!myTypePref.isAvailable()) {
                myTypePref.setValue(PrometheusJDBCDriver.POSTGRESQL);
            }

            /* Make sure that the hostName is specified */
            MetisStringPreference<PrometheusDatabasePreferenceKey> myPref = getStringPreference(PrometheusDatabasePreferenceKey.DBSERVER);
            if (!myPref.isAvailable()) {
                myPref.setValue("localhost");
            }

            /* Make sure that the instance is specified (for SQLSSERVER) */
            myPref = getStringPreference(PrometheusDatabasePreferenceKey.DBINSTANCE);
            myPref.setHidden(!PrometheusJDBCDriver.SQLSERVER.equals(myTypePref.getValue()));
            if (myPref.isHidden()) {
                myPref.setValue(null);
            } else if (!myPref.isAvailable()) {
                myPref.setValue("SQLEXPRESS");
            }

            /* Make sure that the database is specified */
            myPref = getStringPreference(PrometheusDatabasePreferenceKey.DBNAME);
            if (!myPref.isAvailable()) {
                myPref.setValue("MoneyWise");
            }

            /* Make sure that the user is specified */
            myPref = getStringPreference(PrometheusDatabasePreferenceKey.DBUSER);
            if (!myPref.isAvailable()) {
                myPref.setValue(System.getProperty("user.name"));
            }

            /* Make sure that the passWord is specified */
            MetisCharArrayPreference<PrometheusDatabasePreferenceKey> myPassPref = getCharArrayPreference(PrometheusDatabasePreferenceKey.DBPASS);
            if (!myPassPref.isAvailable()) {
                myPassPref.setValue("secret".toCharArray());
            }

            /* Make sure that the value is specified */
            MetisIntegerPreference<PrometheusDatabasePreferenceKey> myBatchPref = getIntegerPreference(PrometheusDatabasePreferenceKey.DBBATCH);
            if (!myBatchPref.isAvailable()) {
                myBatchPref.setValue(DEFAULT_DBBATCH);
            }

            /* Define the range */
            myBatchPref.setRange(DEFAULT_DBBATCH, null);
            if (!myBatchPref.validate()) {
                myBatchPref.setValue(DEFAULT_DBBATCH);
            }
        }
    }
}
