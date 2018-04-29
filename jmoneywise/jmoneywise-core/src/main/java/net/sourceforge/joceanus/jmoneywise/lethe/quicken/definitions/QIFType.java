/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions;

import java.util.ResourceBundle;

/**
 * Output types.
 */
public enum QIFType {
    /**
     * AceMoney.
     */
    ACEMONEY,

    /**
     * GnuCash.
     */
    GNUCASH,

    /**
     * MoneyDance.
     */
    MONEYDANCE,

    /**
     * Quicken2004.
     */
    QUICKEN;

    /**
     * Quicken suffix.
     */
    public static final String QIF_SUFFIX = ".qif";

    /**
     * Standard Quicken Date Format.
     */
    private static final String QIF_DATEFORMAT = "dd/MM/yy";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(QIFType.class.getName());

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }

    /**
     * Should we use a consolidated file?
     * <p>
     * This is required to handle programs who cannot handle a consolidated file.
     * @return true/false
     */
    public boolean useConsolidatedFile() {
        switch (this) {
            case ACEMONEY:
            case GNUCASH:
            case MONEYDANCE:
            case QUICKEN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Should we use simple transfer payee lines?
     * <p>
     * This is required for those programs who fail to recognise matching transfers due to differing
     * payee lines.
     * @return true/false
     */
    public boolean useSimpleTransfer() {
        switch (this) {
            case GNUCASH:
            case MONEYDANCE:
            case QUICKEN:
                return true;
            case ACEMONEY:
            default:
                return false;
        }
    }

    /**
     * Should we hide balancing split transfers?
     * <p>
     * This is required for those programs who fail to recognise balancing transfers when one side
     * forms a split transaction.
     * @return true/false
     */
    public boolean hideBalancingSplitTransfer() {
        switch (this) {
            case ACEMONEY:
            case GNUCASH:
            case MONEYDANCE:
                return true;
            case QUICKEN:
            default:
                return false;
        }
    }

    /**
     * Should we use Self-Opening Balance?
     * <p>
     * This is required when opening balances should be created by a self reference. Generally not
     * used since the resulting opening balance is not assigned to any category.
     * @return true/false
     */
    public boolean selfOpeningBalance() {
        switch (this) {
            case ACEMONEY:
                return false;
            case GNUCASH:
            case MONEYDANCE:
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Should we use Holding account for investments when using transfer for category?
     * <p>
     * Generally required since QIF does not provide a mechanism for non-transfers into the
     * portfolio account, and hence TaxCredits and Inheritance cannot be achieved correctly without
     * using a holding account
     * @return true/false
     */
    public boolean useInvestmentHolding4Category() {
        switch (this) {
            case ACEMONEY:
                return true;
            case GNUCASH:
            case MONEYDANCE:
            case QUICKEN:
            default:
                return false;
        }
    }

    /**
     * Can we use MiscIncX for Reinvested TaxCredit?
     * <p>
     * The Tax Credit for a re-invested dividend must be extracted from the security using MiscInc.
     * Ideally we use MiscIncX to directly assign the value to the TaxCredit category. This is not
     * always available and instead we must use MiscInc and then either Cash or the holding account
     * @return true/false
     */
    public boolean useMiscIncX4TaxCredit() {
        switch (this) {
            case QUICKEN:
                return true;
            case GNUCASH:
            case MONEYDANCE:
            case ACEMONEY:
            default:
                return false;
        }
    }

    /**
     * Should we provide true stock splits?
     * <p>
     * Some programs do not support the StockSplit operation and hence require stock splits to be
     * represented by StockAdjustments
     * @return true/false
     */
    public boolean useStockSplit() {
        switch (this) {
            case ACEMONEY:
                return false;
            case GNUCASH:
            case MONEYDANCE:
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Can we buy zero shares?
     * <p>
     * Some programs do not allow you to buy/sell zero shares, in order to directly add to the cost.
     * In this case we must buy/sell a single share and then perform an adjustment.
     * @return true/false
     */
    public boolean canTradeZeroShares() {
        switch (this) {
            case ACEMONEY:
                return false;
            case GNUCASH:
            case MONEYDANCE:
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Can we return capital?
     * <p>
     * Some programs do not allow you to return capital, treating such an event as a dividend as
     * opposed to a return of capital. In this case we must treat it as a sale of zero shares.
     * @return true/false
     */
    public boolean canReturnCapital() {
        switch (this) {
            case ACEMONEY:
            case GNUCASH:
            case MONEYDANCE:
                return false;
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Can we transfer to/from the portfolio account?
     * <p>
     * Some programs do not allow you to perform a transfer using the investment transfer types
     * XIn/XOut and the consolidated types BuyX/SellX/RtrnCapX/DivX etc. In this case the transfer
     * has to be driven from the partner account.
     * @return true/false
     */
    public boolean canXferPortfolio() {
        switch (this) {
            case ACEMONEY:
                return false;
            case GNUCASH:
            case MONEYDANCE:
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Should we escape prices?
     * <p>
     * Some programs require prices in price records to be escaped with quotes. Others insist on no
     * escaping.
     * @return true/false
     */
    public boolean escapePrices() {
        switch (this) {
            case GNUCASH:
            case QUICKEN:
                return false;
            case MONEYDANCE:
            case ACEMONEY:
            default:
                return true;
        }
    }

    /**
     * Obtain date format.
     * @return the date string
     */
    public String getDateFormat() {
        switch (this) {
            case QUICKEN:
            case GNUCASH:
            case ACEMONEY:
            case MONEYDANCE:
            default:
                return QIF_DATEFORMAT;
        }
    }

    /**
     * Obtain filename.
     * @return the file name
     */
    public String getFileName() {
        switch (this) {
            case ACEMONEY:
                return "all accounts"
                       + QIF_SUFFIX;
            case GNUCASH:
            case MONEYDANCE:
            case QUICKEN:
            default:
                return toString()
                       + QIF_SUFFIX;
        }
    }

    /**
     * Use standard asset types.
     * @return true/false
     */
    public boolean useStandardAssets() {
        switch (this) {
            case ACEMONEY:
            case MONEYDANCE:
                return true;
            case GNUCASH:
            case QUICKEN:
            default:
                return false;
        }
    }
}
