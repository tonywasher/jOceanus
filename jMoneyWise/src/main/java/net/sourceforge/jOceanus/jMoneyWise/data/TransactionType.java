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
package net.sourceforge.jOceanus.jMoneyWise.data;

/**
 * Transaction types.
 */
public enum TransactionType {
    /**
     * Transfer.
     */
    Transfer,

    /**
     * Income.
     */
    Income,

    /**
     * Expense.
     */
    Expense,

    /**
     * CashWithdrawal.
     */
    CashWithdrawal,

    /**
     * CashDeposit.
     */
    CashDeposit,

    /**
     * CashPayment.
     */
    CashPayment,

    /**
     * CashRecovery.
     */
    CashRecovery,

    /**
     * Illegal.
     */
    Illegal;

    /**
     * Is the transaction a transfer?
     * @return true/false
     */
    public boolean isTransfer() {
        switch (this) {
            case Transfer:
            case CashDeposit:
            case CashWithdrawal:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the transaction an income?
     * @return true/false
     */
    public boolean isIncome() {
        switch (this) {
            case Income:
            case CashRecovery:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the transaction an expense?
     * @return true/false
     */
    public boolean isExpense() {
        switch (this) {
            case Expense:
            case CashPayment:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the transaction an autoExpense?
     * @return true/false
     */
    public boolean isAutoExpense() {
        switch (this) {
            case CashRecovery:
            case CashPayment:
            case CashWithdrawal:
            case CashDeposit:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the transaction illegal?
     * @return true/false
     */
    public boolean isIllegal() {
        return (this == Illegal);
    }

    /**
     * Is the transaction recovered?
     * @return true/false
     */
    public TransactionType getRecovered() {
        switch (this) {
            case Expense:
                return Income;
            case CashPayment:
                return CashRecovery;
            case CashWithdrawal:
                return CashDeposit;
            default:
                return Illegal;
        }
    }

    /**
     * Derive Transaction type.
     * @param pCategory the event category
     * @return the transaction type
     */
    public static TransactionType deriveType(final EventCategory pCategory) {
        /* Switch on event category class */
        switch (pCategory.getCategoryTypeClass()) {
            case TaxedIncome:
            case GrantIncome:
            case BenefitIncome:
            case Interest:
            case TaxFreeInterest:
            case Dividend:
            case UnitTrustDividend:
            case TaxFreeDividend:
            case OtherIncome:
            case RentalIncome:
            case Inherited:
            case OpeningBalance:
            case MarketGrowth:
            case CapitalGain:
                return Income;
            case Transfer:
            case StockSplit:
            case StockAdjust:
            case StockRightsTaken:
            case StockRightsWaived:
            case StockDeMerger:
            case StockTakeOver:
                return Transfer;
            case Category:
            case Totals:
                return Illegal;
            case Expense:
            case WriteOff:
            case LocalTaxes:
            case TaxRelief:
            case TaxSettlement:
            case TaxCredit:
            case NatInsurance:
            case Benefit:
            case CharityDonation:
            case LoanInterest:
            default:
                return Expense;
        }
    }
}
