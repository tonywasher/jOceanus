/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.svn.data;

import java.io.File;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Preferences for SubVersion.
 * @author Tony Washer
 */
public interface ThemisSvnPreference {
    /**
     * svnPreferenceKeys.
     */
    enum ThemisSvnPreferenceKey implements MetisPreferenceKey {
        /**
         * SVN Repository Base.
         */
        BASE("SubVersionRepoBase", "Subversion Repository Base"),

        /**
         * SVN Repository Name.
         */
        NAME("SubVersionRepoName", "Subversion Repository Name"),

        /**
         * SVN User.
         */
        USER("SubVersionUser", "Subversion User"),

        /**
         * SVN Password.
         */
        PASS("SubVersionPassword", "Subversion Password"),

        /**
         * SVN Work Directory.
         */
        WORK("SubVersionWork", "Subversion WorkSpace"),

        /**
         * SVN Build directory.
         */
        BUILD("SubVersionBuild", "Subversion BuildSpace"),

        /**
         * SVN Install Directory.
         */
        INSTALL("SubVersionInstall", "Subversion Install Directory"),

        /**
         * BackUp Directory.
         */
        BACKUP("BackupDir", "Backup Directory"),

        /**
         * SVN Prefix.
         */
        PFIX("RepoPrefix", "Repository Prefix");

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
        ThemisSvnPreferenceKey(final String pName,
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
     * ThemisSvnPreferences.
     */
    class ThemisSvnPreferences
            extends MetisPreferenceSet<ThemisSvnPreferenceKey> {
        /**
         * Base value for directories.
         */
        private static final String BASE_DIR = System.getProperty("user.home") + File.separator;

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public ThemisSvnPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, ThemisSvnPreferenceKey.class, "Subversion Preferences");
        }

        @Override
        protected void definePreferences() throws OceanusException {
            defineStringPreference(ThemisSvnPreferenceKey.BASE);
            defineStringPreference(ThemisSvnPreferenceKey.NAME);
            defineStringPreference(ThemisSvnPreferenceKey.USER);
            defineCharArrayPreference(ThemisSvnPreferenceKey.PASS);
            defineDirectoryPreference(ThemisSvnPreferenceKey.WORK);
            defineDirectoryPreference(ThemisSvnPreferenceKey.BUILD);
            defineDirectoryPreference(ThemisSvnPreferenceKey.INSTALL);
            defineDirectoryPreference(ThemisSvnPreferenceKey.BACKUP);
            defineStringPreference(ThemisSvnPreferenceKey.PFIX);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the server is specified */
            MetisStringPreference<ThemisSvnPreferenceKey> myPref = getStringPreference(ThemisSvnPreferenceKey.BASE);
            if (!myPref.isAvailable()) {
                myPref.setValue("http://localhost");
            }

            /* Make sure that the name is specified */
            myPref = getStringPreference(ThemisSvnPreferenceKey.NAME);
            if (!myPref.isAvailable()) {
                myPref.setValue("Repository");
            }

            /* Make sure that the prefix is specified */
            myPref = getStringPreference(ThemisSvnPreferenceKey.PFIX);
            if (!myPref.isAvailable()) {
                myPref.setValue("SvnRepo");
            }

            /* Make sure that the user is specified */
            myPref = getStringPreference(ThemisSvnPreferenceKey.USER);
            if (!myPref.isAvailable()) {
                myPref.setValue("SvnUser");
            }

            /* Make sure that the password is specified */
            final MetisCharArrayPreference<ThemisSvnPreferenceKey> myPassPref = getCharArrayPreference(ThemisSvnPreferenceKey.PASS);
            if (!myPassPref.isAvailable()) {
                myPassPref.setValue("Secret".toCharArray());
            }

            /* Make sure that the workDir is specified */
            myPref = getStringPreference(ThemisSvnPreferenceKey.WORK);
            if (!myPref.isAvailable()) {
                myPref.setValue(BASE_DIR + "WorkSpace");
            }

            /* Make sure that the buildDir is specified */
            myPref = getStringPreference(ThemisSvnPreferenceKey.BUILD);
            if (!myPref.isAvailable()) {
                myPref.setValue(BASE_DIR + "Build");
            }

            /* Make sure that the backupDir is specified */
            myPref = getStringPreference(ThemisSvnPreferenceKey.BACKUP);
            if (!myPref.isAvailable()) {
                myPref.setValue(BASE_DIR + "backup");
            }

            /* Make sure that the installDir is specified */
            myPref = getStringPreference(ThemisSvnPreferenceKey.INSTALL);
            if (!myPref.isAvailable()) {
                myPref.setValue("C:\\csvn\\data\\repositories");
            }
        }
    }
}
