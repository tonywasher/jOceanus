/*******************************************************************************
 * jSvnManager: Java SubVersion Management
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
package net.sourceforge.joceanus.jsvnmanager.data;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jpreferenceset.PreferenceSet;

/**
 * Preferences for SubVersion.
 * @author Tony Washer
 */
public class SubVersionPreferences
        extends PreferenceSet {
    /**
     * Registry name for Subversion Repository Base.
     */
    public static final String NAME_SVN_REPO = "SubVersionRepoBase";

    /**
     * Registry name for Subversion Repository Name.
     */
    public static final String NAME_SVN_NAME = "SubVersionRepoName";

    /**
     * Registry name for Subversion Repository User.
     */
    public static final String NAME_SVN_USER = "SubVersionUser";

    /**
     * Registry name for Subversion Repository Password.
     */
    public static final String NAME_SVN_PASS = "SubVersionPassword";

    /**
     * Registry name for Subversion WorkSpace.
     */
    public static final String NAME_SVN_WORK = "SubVersionWork";

    /**
     * Registry name for Subversion BuildSpace.
     */
    public static final String NAME_SVN_BUILD = "SubVersionBuild";

    /**
     * Registry name for Subversion Directory.
     */
    public static final String NAME_SVN_DIR = "SubVersionDir";

    /**
     * Registry name for Repo Prefix.
     */
    public static final String NAME_REPO_PFIX = "RepoPrefix";

    /**
     * Display name for SubversionRepository Base.
     */
    protected static final String DISPLAY_SVN_REPO = "Subversion Repository Base";

    /**
     * Display name for SubversionRepository Name.
     */
    protected static final String DISPLAY_SVN_NAME = "Subversion Repository Name";

    /**
     * Display name for SubversionUser.
     */
    protected static final String DISPLAY_SVN_USER = "Subversion User";

    /**
     * Display name for SubversionPassword.
     */
    protected static final String DISPLAY_SVN_PASS = "Subversion Password";

    /**
     * Display name for SubversionWorkSpace.
     */
    protected static final String DISPLAY_SVN_WORK = "Subversion WorkSpace";

    /**
     * Display name for SubversionBuildSpace.
     */
    protected static final String DISPLAY_SVN_BUILD = "Subversion BuildSpace";

    /**
     * Display name for SubversionDirectory.
     */
    protected static final String DISPLAY_SVN_DIR = "Subversion Directory";

    /**
     * Display name for Repository Prefix.
     */
    protected static final String DISPLAY_REPO_PFIX = "Repository Prefix";

    /**
     * Default value for SubversionRepository Base.
     */
    private static final String DEFAULT_SVN_REPO = "http://Tony-HP";

    /**
     * Default value for SubversionRepository Name.
     */
    private static final String DEFAULT_SVN_NAME = "Finance";

    /**
     * Default value for SubversionUser.
     */
    private static final String DEFAULT_SVN_USER = "Tony";

    /**
     * Default value for SubversionPassword.
     */
    private static final String DEFAULT_SVN_PASS = "c2Resmin";

    /**
     * Default value for SubversionWorkSpace.
     */
    private static final String DEFAULT_SVN_WORK = "C:\\Users\\Tony\\financeWorkSpace";

    /**
     * Default value for SubversionBuildSpace.
     */
    private static final String DEFAULT_SVN_BUILD = "C:\\Users\\Tony\\TestWC";

    /**
     * Default value for SubversionDirectory.
     */
    private static final String DEFAULT_SVN_DIR = "C:\\csvn\\data\\repositories";

    /**
     * Default value for BackupPrefix.
     */
    private static final String DEFAULT_REPO_PFIX = "SvnRepo";

    /**
     * Constructor.
     * @throws JDataException on error
     */
    public SubVersionPreferences() throws JDataException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        defineStringPreference(NAME_SVN_REPO, DEFAULT_SVN_REPO);
        defineStringPreference(NAME_SVN_NAME, DEFAULT_SVN_NAME);
        defineStringPreference(NAME_SVN_USER, DEFAULT_SVN_USER);
        defineStringPreference(NAME_SVN_PASS, DEFAULT_SVN_PASS);
        defineDirectoryPreference(NAME_SVN_WORK, DEFAULT_SVN_WORK);
        defineDirectoryPreference(NAME_SVN_BUILD, DEFAULT_SVN_BUILD);
        defineDirectoryPreference(NAME_SVN_DIR, DEFAULT_SVN_DIR);
        defineStringPreference(NAME_REPO_PFIX, DEFAULT_REPO_PFIX);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle display names */
        if (pName.equals(NAME_SVN_REPO)) {
            return DISPLAY_SVN_REPO;
        }
        if (pName.equals(NAME_SVN_NAME)) {
            return DISPLAY_SVN_NAME;
        }
        if (pName.equals(NAME_SVN_USER)) {
            return DISPLAY_SVN_USER;
        }
        if (pName.equals(NAME_SVN_PASS)) {
            return DISPLAY_SVN_PASS;
        }
        if (pName.equals(NAME_SVN_WORK)) {
            return DISPLAY_SVN_WORK;
        }
        if (pName.equals(NAME_SVN_BUILD)) {
            return DISPLAY_SVN_BUILD;
        }
        if (pName.equals(NAME_SVN_DIR)) {
            return DISPLAY_SVN_DIR;
        }
        if (pName.equals(NAME_REPO_PFIX)) {
            return DISPLAY_REPO_PFIX;
        }
        return null;
    }
}
