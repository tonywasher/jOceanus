/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.prometheus.preference;

import java.io.File;

import net.sourceforge.joceanus.metis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;

/**
 * Backup preferences.
 */
public interface PrometheusBackup {
    /**
     * BackupPreferenceKeys.
     */
    enum PrometheusBackupPreferenceKey implements MetisPreferenceKey {
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
     * PrometheusBackupPreferences.
     */
    class PrometheusBackupPreferences
            extends MetisPreferenceSet {
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
            super(pManager, PrometheusPreferenceResource.BUPREF_PREFNAME);
        }

        @Override
        protected void definePreferences() {
            defineDirectoryPreference(PrometheusBackupPreferenceKey.BACKUPDIR);
            defineStringPreference(PrometheusBackupPreferenceKey.BACKUPPFIX);
            defineEnumPreference(PrometheusBackupPreferenceKey.BACKUPTYPE, PrometheusSheetWorkBookType.class);
            defineFilePreference(PrometheusBackupPreferenceKey.ARCHIVE);
            defineDatePreference(PrometheusBackupPreferenceKey.LASTEVENT);
            defineBooleanPreference(PrometheusBackupPreferenceKey.BACKUPTIME);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the prefix is specified */
            MetisStringPreference myPref = getStringPreference(PrometheusBackupPreferenceKey.BACKUPPFIX);
            if (!myPref.isAvailable()) {
                myPref.setValue("MoneyWiseBackup");
            }

            /* Make sure that the backup directory is specified */
            myPref = getStringPreference(PrometheusBackupPreferenceKey.BACKUPDIR);
            if (!myPref.isAvailable()) {
                myPref.setValue(HOME_DIR);
            }

            /* Make sure that the archive file is specified */
            myPref = getStringPreference(PrometheusBackupPreferenceKey.ARCHIVE);
            if (!myPref.isAvailable()) {
                myPref.setValue(HOME_DIR + File.separator + "Archive.xls");
            }

            /* Make sure that the enum is specified */
            final MetisEnumPreference<PrometheusSheetWorkBookType> myTypePref
                    = getEnumPreference(PrometheusBackupPreferenceKey.BACKUPTYPE, PrometheusSheetWorkBookType.class);
            if (!myTypePref.isAvailable()) {
                myTypePref.setValue(PrometheusSheetWorkBookType.OASIS);
            }

            /* Make sure that the date is specified */
            final MetisDatePreference myDatePref = getDatePreference(PrometheusBackupPreferenceKey.LASTEVENT);
            if (!myDatePref.isAvailable()) {
                myDatePref.setValue(new OceanusDate());
            }

            /* Make sure that the option is specified */
            final MetisBooleanPreference myOption = getBooleanPreference(PrometheusBackupPreferenceKey.BACKUPTIME);
            if (!myOption.isAvailable()) {
                myOption.setValue(Boolean.FALSE);
            }
        }
    }
}
