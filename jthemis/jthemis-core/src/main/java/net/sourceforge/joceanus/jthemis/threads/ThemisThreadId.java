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
package net.sourceforge.joceanus.jthemis.threads;

/**
 * Themis Thread Ids.
 */
public enum ThemisThreadId {
    /**
     * Create WorkingCopy.
     */
    CREATEWORKINGCOPY("CreateWorkingCopy"),

    /**
     * Extract Tag.
     */
    EXTRACTTAG("ExtractTag"),

    /**
     * Update WorkingCopy.
     */
    UPDATEWORKINGCOPY("UpdateWorkingCopy"),

    /**
     * Revert WorkingCopy.
     */
    REVERTWORKINGCOPY("RevertWorkingCopy"),

    /**
     * Create Tag.
     */
    CREATETAG("CreateTag"),

    /**
     * Create Branch.
     */
    CREATEBRANCH("CreateNewBranch"),

    /**
     * Create GitRepo.
     */
    CREATEGITREPO("CreateGitRepo"),

    /**
     * Backup Subversion.
     */
    BACKUPSVN("BackupSubversion"),

    /**
     * Restore Subversion.
     */
    RESTORESVN("CreateWorkingCopy");

    /**
     * The name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pName the name
     */
    ThemisThreadId(final String pName) {
        theName = pName;
    }

    @Override
    public String toString() {
        return theName;
    }
}
