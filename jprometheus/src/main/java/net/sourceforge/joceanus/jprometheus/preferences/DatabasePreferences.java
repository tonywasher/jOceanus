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
package net.sourceforge.joceanus.jprometheus.preferences;

import net.sourceforge.joceanus.jmetis.preference.PreferenceSet;
import net.sourceforge.joceanus.jprometheus.database.BatchControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Preferences for a database.
 */
public class DatabasePreferences
        extends PreferenceSet {
    /**
     * Registry name for DataBase driver.
     */
    public static final String NAME_DBDRIVER = "DBDriver";

    /**
     * Registry name for DataBase server.
     */
    public static final String NAME_DBSERVER = "DBServer";

    /**
     * Registry name for DataBase instance.
     */
    public static final String NAME_DBINSTANCE = "DBInstance";

    /**
     * Registry name for DataBase name.
     */
    public static final String NAME_DBNAME = "DBaseNameNew";

    /**
     * Registry name for DataBase batch size.
     */
    public static final String NAME_DBBATCH = "DBBatchSize";

    /**
     * Registry name for DataBase user.
     */
    public static final String NAME_DBUSER = "DBUser";

    /**
     * Registry name for DataBase password.
     */
    public static final String NAME_DBPASS = "DBPass";

    /**
     * Display name for DataBase driver.
     */
    private static final String DISPLAY_DBDRIVER = "Database Driver Class";

    /**
     * Display name for DataBase server.
     */
    private static final String DISPLAY_DBSERVER = "Server Host Machine";

    /**
     * Display name for DataBase instance.
     */
    private static final String DISPLAY_DBINSTANCE = "Server Instance";

    /**
     * Display name for DataBase name.
     */
    private static final String DISPLAY_DBNAME = "Database Name";

    /**
     * Display name for DataBase batch size.
     */
    private static final String DISPLAY_DBBATCH = "Batch Size";

    /**
     * Display name for DataBase user.
     */
    private static final String DISPLAY_DBUSER = "Database User";

    /**
     * Display name for DataBase password.
     */
    private static final String DISPLAY_DBPASS = "Database Password";

    /**
     * Default Database driver string.
     */
    private static final JDBCDriver DEFAULT_DBDRIVER = JDBCDriver.POSTGRESQL;

    /**
     * Default Database connection string.
     */
    private static final String DEFAULT_DBSERVER = "localhost";

    /**
     * Default Database instance.
     */
    private static final String DEFAULT_DBINSTANCE = "SQLEXPRESS";

    /**
     * Default Database name.
     */
    private static final String DEFAULT_DBNAME = "MoneyWise";

    /**
     * Default Database batch size.
     */
    private static final Integer DEFAULT_DBBATCH = BatchControl.DEF_BATCH_SIZE;

    /**
     * Default Database user.
     */
    private static final String DEFAULT_DBUSER = "MoneyWiseUser";

    /**
     * Default Database password.
     */
    private static final String DEFAULT_DBPASS = "secret";

    /**
     * Constructor.
     * @throws JOceanusException on error
     */
    public DatabasePreferences() throws JOceanusException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        definePreference(NAME_DBDRIVER, DEFAULT_DBDRIVER, JDBCDriver.class);
        defineStringPreference(NAME_DBSERVER, DEFAULT_DBSERVER);
        defineStringPreference(NAME_DBINSTANCE, DEFAULT_DBINSTANCE);
        defineStringPreference(NAME_DBNAME, DEFAULT_DBNAME);
        defineIntegerPreference(NAME_DBBATCH, DEFAULT_DBBATCH);
        defineStringPreference(NAME_DBUSER, DEFAULT_DBUSER);
        defineStringPreference(NAME_DBPASS, DEFAULT_DBPASS);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_DBDRIVER)) {
            return DISPLAY_DBDRIVER;
        }
        if (pName.equals(NAME_DBSERVER)) {
            return DISPLAY_DBSERVER;
        }
        if (pName.equals(NAME_DBINSTANCE)) {
            return DISPLAY_DBINSTANCE;
        }
        if (pName.equals(NAME_DBNAME)) {
            return DISPLAY_DBNAME;
        }
        if (pName.equals(NAME_DBBATCH)) {
            return DISPLAY_DBBATCH;
        }
        if (pName.equals(NAME_DBUSER)) {
            return DISPLAY_DBUSER;
        }
        if (pName.equals(NAME_DBPASS)) {
            return DISPLAY_DBPASS;
        }
        return null;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
