/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.quicken;

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
     * MoneyDance.
     */
    MoneyDance,

    /**
     * YouNeedABudget.
     */
    YNAB;

    /**
     * Quicken suffix.
     */
    protected static final String QIF_SUFFIX = ".qif";

    /**
     * Should we use a consolidated file?
     * <p>
     * This is required to handle programs who cannot handle a consolidated file.
     * @return true/false
     */
    public boolean useConsolidatedFile() {
        switch (this) {
            case AceMoney:
            case BankTree:
            case MoneyDance:
            case YNAB:
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
            case BankTree:
            case YNAB:
            case MoneyDance:
                return true;
            case AceMoney:
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
            case AceMoney:
            case MoneyDance:
                return true;
            case BankTree:
            case YNAB:
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
            case AceMoney:
            case BankTree:
            case MoneyDance:
            case YNAB:
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
            case AceMoney:
            case MoneyDance:
                return true;
            case BankTree:
            case YNAB:
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
            case AceMoney:
            case BankTree:
            case YNAB:
                return false;
            case MoneyDance:
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
            case AceMoney:
                return false;
            case MoneyDance:
            case BankTree:
            case YNAB:
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
            case MoneyDance:
            case AceMoney:
                return false;
            case BankTree:
            case YNAB:
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
            case AceMoney:
                return false;
            case MoneyDance:
            case BankTree:
            case YNAB:
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
            case AceMoney:
                return false;
            case MoneyDance:
            case BankTree:
            case YNAB:
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
            case MoneyDance:
            case YNAB:
            default:
                return toString()
                       + QIF_SUFFIX;
        }
    }
}
