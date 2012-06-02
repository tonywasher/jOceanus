/*******************************************************************************
 * Jira: Java Jira Link
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.jira.data;

import net.sourceforge.JDataManager.JDataException;
import uk.co.tolcroft.models.data.PreferenceSet;

/**
 * Jira Preferences.
 * @author Tony Washer
 */
public class JiraPreferences extends PreferenceSet {
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
    private static final String DEFAULT_USER = "User";

    /**
     * Default value for JiraPassword.
     */
    private static final String DEFAULT_PASS = "";

    /**
     * Default value for JiraPrefix.
     */
    private static final String DEFAULT_PFIX = "Issue #:";

    /**
     * Constructor.
     * @throws JDataException on error
     */
    public JiraPreferences() throws JDataException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        definePreference(NAME_SERVER, PreferenceType.String);
        definePreference(NAME_USER, PreferenceType.String);
        definePreference(NAME_PASS, PreferenceType.String);
        definePreference(NAME_PFIX, PreferenceType.String);
    }

    @Override
    protected Object getDefaultValue(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_SERVER)) {
            return DEFAULT_SERVER;
        }
        if (pName.equals(NAME_USER)) {
            return DEFAULT_USER;
        }
        if (pName.equals(NAME_PASS)) {
            return DEFAULT_PASS;
        }
        if (pName.equals(NAME_PFIX)) {
            return DEFAULT_PFIX;
        }
        return null;
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
}
