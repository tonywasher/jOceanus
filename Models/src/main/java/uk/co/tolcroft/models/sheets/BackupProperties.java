/*******************************************************************************
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
package uk.co.tolcroft.models.sheets;

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JPreferenceSet.PreferenceSet;

public class BackupProperties extends PreferenceSet {
    /**
     * Registry name for Backup Directory
     */
    public final static String nameBackupDir = "BackupDir";

    /**
     * Registry name for Backup Prefix
     */
    public final static String nameBackupPfix = "BackupPrefix";

    /**
     * Registry name for Archive FileName
     */
    public final static String nameArchiveFile = "ArchiveFile";

    /**
     * Registry name for Backup TimeStamp
     */
    public final static String nameBackupTime = "BackupTimeStamp";

    /**
     * Display name for BackupDirectory
     */
    protected final static String dispBackupDir = "Backup Directory";

    /**
     * Display name for BackupPrefix
     */
    protected final static String dispBackupPfix = "Backup Prefix";

    /**
     * Display name for BackupDirectory
     */
    protected final static String dispArchiveFile = "Archive File";

    /**
     * Display name for Backup Timestamp
     */
    protected final static String dispBackupTime = "Backup TimeStamps";

    /**
     * Default value for BackupDirectory
     */
    private final static String defBackupDir = "C:\\";

    /**
     * Default value for BackupPrefix
     */
    private final static String defBackupPfix = "FinanceBackup";

    /**
     * Default value for Archive File
     */
    private final static String defArchiveFile = "C:\\Archive.xls";

    /**
     * Default value for Archive File
     */
    private final static Boolean defBackupTime = Boolean.FALSE;

    /**
     * Constructor
     * @throws ModelException
     */
    public BackupProperties() throws ModelException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        definePreference(nameBackupDir, PreferenceType.Directory);
        definePreference(nameBackupPfix, PreferenceType.String);
        definePreference(nameArchiveFile, PreferenceType.File);
        definePreference(nameBackupTime, PreferenceType.Boolean);
    }

    @Override
    protected Object getDefaultValue(String pName) {
        /* Handle default values */
        if (pName.equals(nameBackupDir))
            return defBackupDir;
        if (pName.equals(nameBackupPfix))
            return defBackupPfix;
        if (pName.equals(nameArchiveFile))
            return defArchiveFile;
        if (pName.equals(nameBackupTime))
            return defBackupTime;
        return null;
    }

    @Override
    protected String getDisplayName(String pName) {
        /* Handle default values */
        if (pName.equals(nameBackupDir))
            return dispBackupDir;
        if (pName.equals(nameBackupPfix))
            return dispBackupPfix;
        if (pName.equals(nameArchiveFile))
            return dispArchiveFile;
        if (pName.equals(nameBackupTime))
            return dispBackupTime;
        return null;
    }
}
