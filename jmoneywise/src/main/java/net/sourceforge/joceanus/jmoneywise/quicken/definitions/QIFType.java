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
     * This is required for those programs who fail to recognise matching transfers due to differing payee lines.
     * @return true/false
     */
    public boolean useSimpleTransfer() {
        switch (this) {
            case MONEYDANCE:
                return true;
            case ACEMONEY:
            case QUICKEN:
            default:
                return false;
        }
    }

    /**
     * Should we hide balancing split transfers?
     * <p>
     * This is required for those programs who fail to recognise balancing transfers when one side forms a split transaction.
     * @return true/false
     */
    public boolean hideBalancingSplitTransfer() {
        switch (this) {
            case ACEMONEY:
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
     * This is required when opening balances should be created by a self reference. Generally not used since the resulting opening balance is not assigned to
     * any category.
     * @return true/false
     */
    public boolean selfOpeningBalance() {
        switch (this) {
            case ACEMONEY:
            case MONEYDANCE:
            case QUICKEN:
                return false;
            default:
                return true;
        }
    }

    /**
     * Should we use Holding account for investments when using transfer for category?
     * <p>
     * Generally required since QIF does not provide a mechanism for non-transfers into the portfolio account, and hence TaxCredits and Inheritance cannot be
     * achieved correctly without using a holding account
     * @return true/false
     */
    public boolean useInvestmentHolding4Category() {
        switch (this) {
            case ACEMONEY:
            case MONEYDANCE:
            case QUICKEN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Should we provide true stock splits?
     * <p>
     * Some programs do not support the StockSplit operation and hence require stock splits to be represented by StockAdjustments
     * @return true/false
     */
    public boolean useStockSplit() {
        switch (this) {
            case ACEMONEY:
                return false;
            case MONEYDANCE:
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Can we invest capital?
     * <p>
     * Some programs do not allow you to buy zero shares, in order to directly add to the cost. In this case we must buy a single share and remove it.
     * @return true/false
     */
    public boolean canInvestCapital() {
        switch (this) {
            case ACEMONEY:
                return false;
            case MONEYDANCE:
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Can we return capital?
     * <p>
     * Some programs do not allow you to sell zero shares, treating such an event as a dividend as opposed to a return of capital. In this case we must sell a
     * single share and restore it.
     * @return true/false
     */
    public boolean canReturnCapital() {
        switch (this) {
            case MONEYDANCE:
            case ACEMONEY:
                return false;
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Can we transfer directly to/from the portfolio account?
     * <p>
     * Some programs do not allow you to use the investment transfer types XIn and XOut. In this case the transfer has to be driven from the partner account.
     * @return true/false
     */
    public boolean canXferPortfolioDirect() {
        switch (this) {
            case ACEMONEY:
                return false;
            case MONEYDANCE:
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Should we escape prices?
     * <p>
     * Some programs require prices in price records to be escaped with quotes. Others insist on no escaping.
     * @return true/false
     */
    public boolean escapePrices() {
        switch (this) {
            case QUICKEN:
                return false;
            case MONEYDANCE:
            case ACEMONEY:
            default:
                return true;
        }
    }

    /**
     * Can we transfer to/from the portfolio account as part of an investment transaction?
     * <p>
     * Some programs do not allow you to use the investment transfer types BuyX, SellX, RtrnCapX and DivX, which allow a linked transfer. In this case the
     * transfer has to be specified separately by the partner account.
     * @return true/false
     */
    public boolean canXferPortfolioLinked() {
        switch (this) {
            case ACEMONEY:
                return false;
            case MONEYDANCE:
            case QUICKEN:
            default:
                return true;
        }
    }

    /**
     * Obtain date format.
     * @return true/false
     */
    public String getDateFormat() {
        switch (this) {
            case QUICKEN:
            case ACEMONEY:
            case MONEYDANCE:
            default:
                return QIF_DATEFORMAT;
        }
    }

    /**
     * Obtain filename.
     * @return true/false
     */
    public String getFileName() {
        switch (this) {
            case ACEMONEY:
                return "all accounts"
                       + QIF_SUFFIX;
            case MONEYDANCE:
            case QUICKEN:
            default:
                return toString()
                       + QIF_SUFFIX;
        }
    }
}
