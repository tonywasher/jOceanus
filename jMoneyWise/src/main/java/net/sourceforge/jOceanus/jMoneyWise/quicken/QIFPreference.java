package net.sourceforge.jOceanus.jMoneyWise.quicken;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet;

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
    private static final QIFType DEFAULT_QIFTYPE = QIFType.AceMoney;

    /**
     * Default value for Last Event.
     */
    private static final JDateDay DEFAULT_LASTEVENT = new JDateDay();

    /**
     * Constructor.
     * @throws JDataException on error
     */
    public QIFPreference() throws JDataException {
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

    /**
     * Output types.
     */
    public enum QIFType {
        /**
         * AceMoney.
         */
        AceMoney,

        /**
         * MSMoney.
         */
        MSMoneyPlus;
    }
}
