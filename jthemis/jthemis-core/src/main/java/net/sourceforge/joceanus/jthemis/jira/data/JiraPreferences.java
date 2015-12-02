/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.jira.data;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jira Preferences.
 * @author Tony Washer
 */
public class JiraPreferences
        extends MetisPreferenceSet {
    /**
     * Registry name for Jira Server.
     */
    public static final String NAME_SERVER = "JiraServer";

    /**
     * Registry name for Jira User.
     */
    public static final String NAME_USER = "JiraUser";

    /**
     * Registry name for Jira Password.
     */
    public static final String NAME_PASS = "JiraPassword";

    /**
     * Registry name for Jira Prefix.
     */
    public static final String NAME_PFIX = "JiraPrefix";

    /**
     * Display name for JiraServer.
     */
    protected static final String DISPLAY_SERVER = "Jira Server";

    /**
     * Display name for JiraUser.
     */
    protected static final String DISPLAY_USER = "Jira User";

    /**
     * Display name for JiraPassword.
     */
    protected static final String DISPLAY_PASS = "Jira Password";

    /**
     * Display name for JiraPrefix.
     */
    protected static final String DISPLAY_PFIX = "Jira Prefix";

    /**
     * Default value for JiraServer.
     */
    private static final String DEFAULT_SERVER = "http://localhost:8080";

    /**
     * Default value for JiraUser.
     */
    private static final String DEFAULT_USER = NAME_USER;

    /**
     * Default value for JiraPassword.
     */
    private static final String DEFAULT_PASS = "Secret";

    /**
     * Default value for JiraPrefix.
     */
    private static final String DEFAULT_PFIX = "Issue #:";

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public JiraPreferences() throws OceanusException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        defineStringPreference(NAME_SERVER, DEFAULT_SERVER);
        defineStringPreference(NAME_USER, DEFAULT_USER);
        defineStringPreference(NAME_PASS, DEFAULT_PASS);
        defineStringPreference(NAME_PFIX, DEFAULT_PFIX);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_SERVER)) {
            return DISPLAY_SERVER;
        }
        if (pName.equals(NAME_USER)) {
            return DISPLAY_USER;
        }
        if (pName.equals(NAME_PASS)) {
            return DISPLAY_PASS;
        }
        if (pName.equals(NAME_PFIX)) {
            return DISPLAY_PFIX;
        }
        return null;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
