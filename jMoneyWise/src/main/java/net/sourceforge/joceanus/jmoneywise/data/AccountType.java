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
    VALUED,

    /**
     * Priced account (unit based).
     */
    PRICED,

    /**
     * NonAsset (payee/institution etc.).
     */
    NONASSET,

    /**
     * AutoExpense.
     */
    AUTOEXPENSE,

    /**
     * Illegal.
     */
    ILLEGAL;

    /**
     * Is the account a nonAsset?
     * @return true/false
     */
    public boolean isNonAsset() {
        return this == NONASSET;
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
        return this == PRICED;
    }

    /**
     * Is the account valued?
     * @return true/false
     */
    public boolean isValued() {
        return this == VALUED;
    }

    /**
     * Is the account autoExpense?
     * @return true/false
     */
    public boolean isAutoExpense() {
        return this == AUTOEXPENSE;
    }

    /**
     * Is the account illegal?
     * @return true/false
     */
    public boolean isIllegal() {
        return this == ILLEGAL;
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
            return TransactionType.ILLEGAL;
        }
        boolean toAsset = pPartner.isAsset();
        if (isAsset()) {
            if (isAutoExpense()) {
                if (pPartner.isAutoExpense()) {
                    return TransactionType.ILLEGAL;
                }
                return (toAsset)
                        ? TransactionType.CASHDEPOSIT
                        : TransactionType.CASHPAYMENT;
            }
            return (toAsset)
                    ? pPartner.isAutoExpense()
                            ? TransactionType.CASHWITHDRAWAL
                            : TransactionType.TRANSFER
                    : TransactionType.EXPENSE;
        } else {
            if (!toAsset) {
                return TransactionType.ILLEGAL;
            }
            return pPartner.isAutoExpense()
                    ? TransactionType.CASHRECOVERY
                    : TransactionType.INCOME;
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
            case SAVINGS:
            case BOND:
            case CREDITCARD:
            case PRIVATELOAN:
            case LOAN:
                return VALUED;
            case CASH:
                return (pAccount.getAutoExpense() != null)
                        ? VALUED
                        : AUTOEXPENSE;
            case SHARES:
            case UNITTRUST:
            case LIFEBOND:
            case ENDOWMENT:
            case ASSET:
            case PROPERTY:
            case VEHICLE:
                return PRICED;
            case EMPLOYER:
            case PAYEE:
            case TAXMAN:
            case GOVERNMENT:
            case PORTFOLIO:
            case INSTITUTION:
            case INDIVIDUAL:
                return NONASSET;
            case SAVINGSTOTALS:
            case CASHTOTALS:
            case PRICEDTOTALS:
            case LOANTOTALS:
            case TOTALS:
            default:
                return ILLEGAL;
        }
    }
}
