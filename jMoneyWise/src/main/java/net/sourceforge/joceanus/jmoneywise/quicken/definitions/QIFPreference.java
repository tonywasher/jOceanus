/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.quicken.definitions;

import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jpreferenceset.PreferenceSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Quicken Preferences.
 */
public class QIFPreference
        extends PreferenceSet {
    /**
     * Registry name for QIF Directory.
     */
    public static final String NAME_QIFDIR = "QIFDir";

    /**
     * Backup type.
     */
    public static final String NAME_QIFTYPE = "QIFType";

    /**
     * Registry name for Last Event.
     */
    public static final String NAME_LASTEVENT = "LastEvent";

    /**
     * Display name for Last Event.
     */
    private static final String DISPLAY_QIFDIR = "Output Directory";

    /**
     * Display name for QIFType.
     */
    private static final String DISPLAY_QIFTYPE = "Output Type";

    /**
     * Display name for Last Event.
     */
    private static final String DISPLAY_LASTEVENT = "Last Event";

    /**
     * Default value for QIFDirectory.
     */
    private static final String DEFAULT_QIFDIR = "C:\\";

    /**
     * Default value for BackupType.
     */
    private static final QIFType DEFAULT_QIFTYPE = QIFType.ACEMONEY;

    /**
     * Default value for Last Event.
     */
    private static final JDateDay DEFAULT_LASTEVENT = new JDateDay();

    /**
     * Constructor.
     * @throws JOceanusException on error
     */
    public QIFPreference() throws JOceanusException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the preferences */
        defineDirectoryPreference(NAME_QIFDIR, DEFAULT_QIFDIR);
        definePreference(NAME_QIFTYPE, DEFAULT_QIFTYPE, QIFType.class);
        defineDatePreference(NAME_LASTEVENT, DEFAULT_LASTEVENT);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_QIFDIR)) {
            return DISPLAY_QIFDIR;
        }
        if (pName.equals(NAME_QIFTYPE)) {
            return DISPLAY_QIFTYPE;
        }
        if (pName.equals(NAME_LASTEVENT)) {
            return DISPLAY_LASTEVENT;
        }
        return null;
    }
}
