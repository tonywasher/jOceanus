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
package net.sourceforge.joceanus.jmoneywise.data;

/**
 * Account types.
 */
public enum AccountType {
    /**
     * Valued account (Savings/Loan).
     */
    Valued,

    /**
     * Priced account (unit based).
     */
    Priced,

    /**
     * NonAsset (payee/institution etc.).
     */
    NonAsset,

    /**
     * AutoExpense.
     */
    AutoExpense,

    /**
     * Illegal.
     */
    Illegal;

    /**
     * Is the account a nonAsset?
     * @return true/false
     */
    public boolean isNonAsset() {
        return (this == NonAsset);
    }

    /**
     * Is the account an Asset?
     * @return true/false
     */
    public boolean isAsset() {
        return !isNonAsset()
               && !isIllegal();
    }

    /**
     * Is the account priced?
     * @return true/false
     */
    public boolean isPriced() {
        return (this == Priced);
    }

    /**
     * Is the account valued?
     * @return true/false
     */
    public boolean isValued() {
        return (this == Valued);
    }

    /**
     * Is the account autoExpense?
     * @return true/false
     */
    public boolean isAutoExpense() {
        return (this == AutoExpense);
    }

    /**
     * Is the account illegal?
     * @return true/false
     */
    public boolean isIllegal() {
        return (this == Illegal);
    }

    /**
     * Obtain transaction type.
     * @param pPartner the partner account type.
     * @return transaction type
     */
    public TransactionType getTransactionType(final AccountType pPartner) {
        /* Handle illegal accounts */
        if ((isIllegal())
            || (pPartner.isIllegal())) {
            return TransactionType.Illegal;
        }
        boolean toAsset = pPartner.isAsset();
        if (isAsset()) {
            if (isAutoExpense()) {
                if (pPartner.isAutoExpense()) {
                    return TransactionType.Illegal;
                }
                return (toAsset)
                        ? TransactionType.CashDeposit
                        : TransactionType.CashPayment;
            }
            return (toAsset)
                    ? (pPartner.isAutoExpense()
                            ? TransactionType.CashWithdrawal
                            : TransactionType.Transfer)
                    : TransactionType.Expense;
        } else {
            if (!toAsset) {
                return TransactionType.Illegal;
            }
            return (pPartner.isAutoExpense())
                    ? TransactionType.CashRecovery
                    : TransactionType.Income;
        }
    }

    /**
     * Derive Account type.
     * @param pAccount the account
     * @return the account type
     */
    public static AccountType deriveType(final Account pAccount) {
        /* Switch on account category class */
        switch (pAccount.getAccountCategoryClass()) {
            case Savings:
            case Bond:
            case CreditCard:
            case PrivateLoan:
            case Loan:
                return Valued;
            case Cash:
                return (pAccount.getAutoExpense() != null)
                        ? Valued
                        : AutoExpense;
            case Shares:
            case UnitTrust:
            case LifeBond:
            case Endowment:
            case Asset:
            case Property:
            case Vehicle:
                return Priced;
            case Employer:
            case Payee:
            case TaxMan:
            case Government:
            case Portfolio:
            case Institution:
            case Individual:
            case OpeningBalance:
                return NonAsset;
            case SavingsTotals:
            case CashTotals:
            case PricedTotals:
            case LoanTotals:
            case Totals:
            default:
                return Illegal;
        }
    }
}
