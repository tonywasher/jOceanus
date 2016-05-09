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

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jira Preferences.
 * @author Tony Washer
 */
public abstract class ThemisJiraPreference {
    /**
     * Constructor.
     */
    private ThemisJiraPreference() {
    }

    /**
     * jiraPreferenceKeys.
     */
    public enum ThemisJiraPreferenceKey implements MetisPreferenceKey {
        /**
         * JIRA Server.
         */
        SERVER("JiraServer", "Jira Server"),

        /**
         * JIRA User.
         */
        USER("JiraUser", "Jira User"),

        /**
         * JIRA Password.
         */
        PASS("JiraPassword", "Jira Password"),

        /**
         * JIRA Prefix.
         */
        PFIX("JiraPrefix", "Jira Prefix");

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
        ThemisJiraPreferenceKey(final String pName,
                                final String pDisplay) {
            theName = pName;
            theDisplay = pDisplay;
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
     * ThemisJiraPreferences.
     */
    public static class ThemisJiraPreferences
            extends MetisPreferenceSet<ThemisJiraPreferenceKey> {
        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public ThemisJiraPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager);
            defineStringPreference(ThemisJiraPreferenceKey.SERVER, "http://localhost:8080");
            defineStringPreference(ThemisJiraPreferenceKey.USER, "JiraUser");
            defineCharArrayPreference(ThemisJiraPreferenceKey.PASS, "Secret".toCharArray());
            defineStringPreference(ThemisJiraPreferenceKey.PFIX, "Issue #:");
            setName("Jira Preferences");
            storeChanges();
        }
    }
}
