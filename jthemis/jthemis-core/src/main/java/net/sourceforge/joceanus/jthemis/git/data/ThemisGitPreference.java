/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jthemis.git.data;

import java.io.File;

import net.sourceforge.joceanus.jmetis.atlas.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.atlas.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.atlas.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Preferences for Git.
 * @author Tony Washer
 */
public abstract class ThemisGitPreference {
    /**
     * Constructor.
     */
    private ThemisGitPreference() {
    }

    /**
     * gitPreferenceKeys.
     */
    public enum ThemisGitPreferenceKey implements MetisPreferenceKey {
        /**
         * GIT Repository Base.
         */
        BASE("GitRepoBase", "Git Repository Base"),

        /**
         * GIT Repository Name.
         */
        NAME("GitRepoName", "Git Repository Name"),

        /**
         * GIT User.
         */
        USER("GitUser", "Git User");

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
        ThemisGitPreferenceKey(final String pName,
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
     * ThemisGitPreferences.
     */
    public static class ThemisGitPreferences
            extends MetisPreferenceSet<ThemisGitPreferenceKey> {
        /**
         * Default value for SubversionRepository Base.
         */
        private static final String DEFAULT_GIT_REPO = System.getProperty("user.home") + File.separator + "Git";

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public ThemisGitPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, ThemisGitPreferenceKey.class, "Git Preferences");
        }

        @Override
        protected void definePreferences() {
            defineDirectoryPreference(ThemisGitPreferenceKey.BASE);
            defineStringPreference(ThemisGitPreferenceKey.NAME);
            defineStringPreference(ThemisGitPreferenceKey.USER);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the repository is specified */
            MetisStringPreference<ThemisGitPreferenceKey> myPref = getStringPreference(ThemisGitPreferenceKey.BASE);
            if (!myPref.isAvailable()) {
                myPref.setValue(DEFAULT_GIT_REPO);
            }

            /* Make sure that the name is specified */
            myPref = getStringPreference(ThemisGitPreferenceKey.NAME);
            if (!myPref.isAvailable()) {
                myPref.setValue("GitRepo");
            }

            /* Make sure that the user is specified */
            myPref = getStringPreference(ThemisGitPreferenceKey.USER);
            if (!myPref.isAvailable()) {
                myPref.setValue("User@mail.com");
            }
        }
    }
}
