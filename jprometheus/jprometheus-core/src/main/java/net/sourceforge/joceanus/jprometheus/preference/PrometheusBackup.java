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

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.sheet.MetisWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Backup preferences.
 * @author Tony Washer
 */
public final class PrometheusBackup {
    /**
     * Constructor.
     */
    private PrometheusBackup() {
    }

    /**
     * SecurityPreferenceKeys.
     */
    public enum PrometheusBackupPreferenceKey implements MetisPreferenceKey {
        /**
         * BackUp Directory.
         */
        BACKUPDIR("BackupDir", PrometheusPreferenceResource.BUPREF_DIR),

        /**
         * BackUp Prefix.
         */
        BACKUPPFIX("BackupPrefix", PrometheusPreferenceResource.BUPREF_PFIX),

        /**
         * BackUp Type.
         */
        BACKUPTYPE("BackupType", PrometheusPreferenceResource.BUPREF_TYPE),

        /**
         * Archive File.
         */
        ARCHIVE("SourceFileNew", PrometheusPreferenceResource.BUPREF_ARCHIVE),

        /**
         * Last Event Date.
         */
        LASTEVENT("LastEvent", PrometheusPreferenceResource.BUPREF_EVENT),

        /**
         * backUp TimeStamp.
         */
        BACKUPTIME("BackupTimeStamp", PrometheusPreferenceResource.BUPREF_TIMESTAMP);

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
        PrometheusBackupPreferenceKey(final String pName,
                                      final PrometheusPreferenceResource pDisplay) {
            theName = pName;
            theDisplay = pDisplay.getValue();
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
     * PrometheusSecurityPreferences.
     */
    public static class PrometheusBackupPreferences
            extends MetisPreferenceSet<PrometheusBackupPreferenceKey> {
        /**
         * Default Number of Active KeySets.
         */
        private static final String HOME_DIR = System.getProperty("user.home");

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public PrometheusBackupPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager);
            defineDirectoryPreference(PrometheusBackupPreferenceKey.BACKUPDIR, HOME_DIR);
            defineStringPreference(PrometheusBackupPreferenceKey.BACKUPPFIX, "MoneyWiseBackup");
            defineEnumPreference(PrometheusBackupPreferenceKey.BACKUPTYPE, MetisWorkBookType.EXCELXLS, MetisWorkBookType.class);
            defineFilePreference(PrometheusBackupPreferenceKey.ARCHIVE, HOME_DIR + File.separator + "Archive.xls");
            defineDatePreference(PrometheusBackupPreferenceKey.LASTEVENT, new TethysDate());
            defineBooleanPreference(PrometheusBackupPreferenceKey.BACKUPTIME, Boolean.FALSE);
            setName(PrometheusPreferenceResource.BUPREF_PREFNAME.getValue());
            storeChanges();
        }
    }
}
