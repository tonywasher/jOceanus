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
     * Quicken suffix.
     */
    protected static final String QIF_SUFFIX = ".qif";

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
         * BankTree.
         */
        BankTree,

        /**
         * GnuCash.
         */
        GnuCash,

        /**
         * HomeBank.
         */
        HomeBank,

        /**
         * jGnash.
         */
        jGnash,

        /**
         * MoneyDance.
         */
        MoneyDance,

        /**
         * YouNeedABudget.
         */
        YNAB;

        /**
         * Should we use a consolidated file?
         * @return true/false
         */
        public boolean useConsolidated() {
            switch (this) {
                case AceMoney:
                case BankTree:
                case GnuCash:
                case HomeBank:
                case jGnash:
                case MoneyDance:
                case YNAB:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Should we use simple transfer payee lines?
         * @return true/false
         */
        public boolean useSimpleTransfer() {
            switch (this) {
                case BankTree:
                case HomeBank:
                case jGnash:
                case MoneyDance:
                case YNAB:
                    return true;
                case AceMoney:
                case GnuCash:
                default:
                    return false;
            }
        }

        /**
         * Should we hide balancing tax transfers?
         * @return true/false
         */
        public boolean hideBalancingTaxTransfer() {
            switch (this) {
                case AceMoney:
                    return true;
                case BankTree:
                case GnuCash:
                case HomeBank:
                case jGnash:
                case MoneyDance:
                case YNAB:
                default:
                    return false;
            }
        }

        /**
         * do we support split transfer?
         * @return true/false
         */
        public boolean supportsSplitTransfer() {
            switch (this) {
                case AceMoney:
                case BankTree:
                case jGnash:
                case MoneyDance:
                case YNAB:
                    return true;
                case GnuCash:
                case HomeBank:
                default:
                    return false;
            }
        }

        /**
         * Should we use Self-Opening Balance?
         * @return true/false
         */
        public boolean selfOpeningBalance() {
            switch (this) {
                case AceMoney:
                case BankTree:
                case GnuCash:
                case HomeBank:
                case jGnash:
                case MoneyDance:
                case YNAB:
                    return false;
                default:
                    return true;
            }
        }

        /**
         * Obtain filename.
         * @return true/false
         */
        public String getFileName() {
            switch (this) {
                case AceMoney:
                    return "all accounts"
                           + QIF_SUFFIX;
                case BankTree:
                case GnuCash:
                case HomeBank:
                case jGnash:
                case MoneyDance:
                case YNAB:
                default:
                    return toString()
                           + QIF_SUFFIX;
            }
        }
    }
}
