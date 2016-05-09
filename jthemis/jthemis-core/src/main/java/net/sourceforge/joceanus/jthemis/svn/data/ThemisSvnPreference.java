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
public abstract class ThemisSvnPreference {
    /**
     * Constructor.
     */
    private ThemisSvnPreference() {
    }

    /**
     * svnPreferenceKeys.
     */
    public enum ThemisSvnPreferenceKey implements MetisPreferenceKey {
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
     * ThemisGitPreferences.
     */
    public static class ThemisSvnPreferences
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
            super(pManager);
            defineStringPreference(ThemisSvnPreferenceKey.BASE, "http://localhost");
            defineStringPreference(ThemisSvnPreferenceKey.NAME, "Repository");
            defineStringPreference(ThemisSvnPreferenceKey.USER, "SvnUser");
            defineCharArrayPreference(ThemisSvnPreferenceKey.PASS, "Secret".toCharArray());
            defineDirectoryPreference(ThemisSvnPreferenceKey.WORK, BASE_DIR + "WorkSpace");
            defineDirectoryPreference(ThemisSvnPreferenceKey.BUILD, BASE_DIR + "Build");
            defineDirectoryPreference(ThemisSvnPreferenceKey.INSTALL, "C:\\csvn\\data\\repositories");
            defineStringPreference(ThemisSvnPreferenceKey.PFIX, "SvnRepo");
            setName("Subversion Preferences");
            storeChanges();
        }
    }
}
