/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.preference;

import java.io.File;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.sheet.MetisWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Backup preferences.
 * @author Tony Washer
 */
public class BackupPreferences
        extends MetisPreferenceSet {
    /**
     * Registry name for Backup Directory.
     */
    public static final String NAME_BACKUP_DIR = "BackupDir";

    /**
     * Registry name for Backup Prefix.
     */
    public static final String NAME_BACKUP_PFIX = "BackupPrefix";

    /**
     * Backup type.
     */
    public static final String NAME_BACKUP_TYPE = "BackupType";

    /**
     * Registry name for Archive FileName.
     */
    public static final String NAME_ARCHIVE_FILE = "SourceFileNew";

    /**
     * Registry name for Last Event.
     */
    public static final String NAME_LAST_EVENT = "LastEvent";

    /**
     * Registry name for Backup TimeStamp.
     */
    public static final String NAME_BACKUP_TIME = "BackupTimeStamp";

    /**
     * Display name for BackupDirectory.
     */
    private static final String DISPLAY_BACKUPDIR = "Backup Directory";

    /**
     * Display name for BackupPrefix.
     */
    private static final String DISPLAY_BACKUP_PFIX = "Backup Prefix";

    /**
     * Display name for BackupType.
     */
    private static final String DISPLAY_BACKUP_TYPE = "Backup Type";

    /**
     * Display name for BackupDirectory.
     */
    private static final String DISPLAY_ARCHIVE_FILE = "Archive File";

    /**
     * Display name for Last Event.
     */
    private static final String DISPLAY_LAST_EVENT = "Last Event";

    /**
     * Display name for Backup Timestamp.
     */
    private static final String DISPLAY_BACKUP_TIME = "Backup TimeStamps";

    /**
     * Default value for BackupDirectory.
     */
    private static final String DEFAULT_BACKUPDIR = System.getProperty("user.home");

    /**
     * Default value for BackupPrefix.
     */
    private static final String DEFAULT_BACKUP_PFIX = "MoneyWiseBackup";

    /**
     * Default value for BackupType.
     */
    private static final MetisWorkBookType DEFAULT_BACKUP_TYPE = MetisWorkBookType.EXCELXLS;

    /**
     * Default value for Archive File.
     */
    private static final String DEFAULT_ARCHIVE_FILE = DEFAULT_BACKUPDIR + File.separator + "Archive.xls";

    /**
     * Default value for Last Event.
     */
    private static final TethysDate DEFAULT_LAST_EVENT = new TethysDate();

    /**
     * Default value for Backup Time.
     */
    private static final Boolean DEFAULT_BACKUP_TIME = Boolean.FALSE;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public BackupPreferences() throws OceanusException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        defineDirectoryPreference(NAME_BACKUP_DIR, DEFAULT_BACKUPDIR);
        defineStringPreference(NAME_BACKUP_PFIX, DEFAULT_BACKUP_PFIX);
        definePreference(NAME_BACKUP_TYPE, DEFAULT_BACKUP_TYPE, MetisWorkBookType.class);
        defineFilePreference(NAME_ARCHIVE_FILE, DEFAULT_ARCHIVE_FILE);
        defineDatePreference(NAME_LAST_EVENT, DEFAULT_LAST_EVENT);
        defineBooleanPreference(NAME_BACKUP_TIME, DEFAULT_BACKUP_TIME);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_BACKUP_DIR)) {
            return DISPLAY_BACKUPDIR;
        }
        if (pName.equals(NAME_BACKUP_PFIX)) {
            return DISPLAY_BACKUP_PFIX;
        }
        if (pName.equals(NAME_BACKUP_TYPE)) {
            return DISPLAY_BACKUP_TYPE;
        }
        if (pName.equals(NAME_ARCHIVE_FILE)) {
            return DISPLAY_ARCHIVE_FILE;
        }
        if (pName.equals(NAME_LAST_EVENT)) {
            return DISPLAY_LAST_EVENT;
        }
        if (pName.equals(NAME_BACKUP_TIME)) {
            return DISPLAY_BACKUP_TIME;
        }
        return null;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
