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
package net.sourceforge.joceanus.jthemis.git.data;

import net.sourceforge.joceanus.jmetis.preference.PreferenceSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Preferences for Git.
 * @author Tony Washer
 */
public class GitPreferences
        extends PreferenceSet {
    /**
     * Registry name for Git Repository Base.
     */
    public static final String NAME_GIT_REPO = "GitRepoBase";

    /**
     * Registry name for Git Repository Name.
     */
    public static final String NAME_GIT_NAME = "GitRepoName";

    /**
     * Registry name for Git Repository User.
     */
    public static final String NAME_GIT_USER = "GitUser";

    /**
     * Display name for Git Base.
     */
    protected static final String DISPLAY_GIT_REPO = "Git Repository Base";

    /**
     * Display name for Git Name.
     */
    protected static final String DISPLAY_GIT_NAME = "Git Repository Name";

    /**
     * Display name for GitUser.
     */
    protected static final String DISPLAY_GIT_USER = "Git User";

    /**
     * Default value for SubversionRepository Base.
     */
    private static final String DEFAULT_GIT_REPO = "c:\\Users\\User\\Git";

    /**
     * Default value for SubversionRepository Name.
     */
    private static final String DEFAULT_GIT_NAME = "GitRepo";

    /**
     * Default value for SubversionRepository Base.
     */
    private static final String DEFAULT_GIT_USER = "User@mail.com";

    /**
     * Constructor.
     * @throws JOceanusException on error
     */
    public GitPreferences() throws JOceanusException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        defineStringPreference(NAME_GIT_REPO, DEFAULT_GIT_REPO);
        defineStringPreference(NAME_GIT_NAME, DEFAULT_GIT_NAME);
        defineStringPreference(NAME_GIT_USER, DEFAULT_GIT_USER);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle display names */
        if (pName.equals(NAME_GIT_REPO)) {
            return DISPLAY_GIT_REPO;
        }
        if (pName.equals(NAME_GIT_NAME)) {
            return DISPLAY_GIT_NAME;
        }
        if (pName.equals(NAME_GIT_USER)) {
            return DISPLAY_GIT_USER;
        }
        return null;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
