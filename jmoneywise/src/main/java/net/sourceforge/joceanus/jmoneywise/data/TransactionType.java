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
package net.sourceforge.joceanus.jmoneywise.data;

/**
 * Transaction types.
 */
public enum TransactionType {
    /**
     * Transfer.
     */
    TRANSFER,

    /**
     * Income.
     */
    INCOME,

    /**
     * Expense.
     */
    EXPENSE,

    /**
     * CashWithdrawal.
     */
    CASHWITHDRAWAL,

    /**
     * CashDeposit.
     */
    CASHDEPOSIT,

    /**
     * CashPayment.
     */
    CASHPAYMENT,

    /**
     * CashRecovery.
     */
    CASHRECOVERY,

    /**
     * Illegal.
     */
    ILLEGAL;

    /**
     * Is the transaction a transfer?
     * @return true/false
     */
    public boolean isTransfer() {
        switch (this) {
            case TRANSFER:
            case CASHDEPOSIT:
            case CASHWITHDRAWAL:
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
            case INCOME:
            case CASHRECOVERY:
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
            case EXPENSE:
            case CASHPAYMENT:
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
            case CASHRECOVERY:
            case CASHPAYMENT:
            case CASHWITHDRAWAL:
            case CASHDEPOSIT:
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
        return this == ILLEGAL;
    }

    /**
     * Is the transaction recovered?
     * @return true/false
     */
    public TransactionType getRecovered() {
        switch (this) {
            case EXPENSE:
                return INCOME;
            case CASHPAYMENT:
                return CASHRECOVERY;
            case CASHWITHDRAWAL:
                return CASHDEPOSIT;
            default:
                return ILLEGAL;
        }
    }

    /**
     * Derive Transaction type.
     * @param pCategory the event category
     * @return the transaction type
     */
    public static TransactionType deriveType(final TransactionCategory pCategory) {
        /* Switch on event category class */
        switch (pCategory.getCategoryTypeClass()) {
            case TAXEDINCOME:
            case GRANTINCOME:
            case BENEFITINCOME:
            case INTEREST:
            case TAXFREEINTEREST:
            case LOANINTERESTEARNED:
            case DIVIDEND:
            case UNITTRUSTDIVIDEND:
            case TAXFREEDIVIDEND:
            case GIFTEDINCOME:
            case RENTALINCOME:
            case ROOMRENTALINCOME:
            case INHERITED:
            case MARKETGROWTH:
            case CAPITALGAIN:
            case CASHBACK:
            case LOYALTYBONUS:
            case OPTIONSEXERCISE:
            case OPTIONSVEST:
            case OTHERINCOME:
                return INCOME;
            case TRANSFER:
            case STOCKSPLIT:
            case STOCKADJUST:
            case STOCKRIGHTSTAKEN:
            case STOCKRIGHTSWAIVED:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
                return TRANSFER;
            case INCOMETOTALS:
            case EXPENSETOTALS:
            case TOTALS:
                return ILLEGAL;
            case EXPENSE:
            case WRITEOFF:
            case LOCALTAXES:
            case TAXRELIEF:
            case TAXSETTLEMENT:
            case TAXCREDIT:
            case NATINSURANCE:
            case DEEMEDBENEFIT:
            case CHARITYDONATION:
            case LOANINTERESTCHARGED:
                return EXPENSE;
            default:
                throw new IllegalStateException();
        }
    }
}
