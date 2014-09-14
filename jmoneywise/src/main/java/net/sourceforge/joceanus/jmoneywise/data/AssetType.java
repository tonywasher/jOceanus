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

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of Asset Types.
 */
public enum AssetType {
    /**
     * Deposit.
     */
    DEPOSIT(1),

    /**
     * Cash.
     */
    CASH(2),

    /**
     * AutoExpense.
     */
    AUTOEXPENSE(3),

    /**
     * Portfolio.
     */
    PORTFOLIO(4),

    /**
     * Loan.
     */
    LOAN(5),

    /**
     * Security.
     */
    SECURITY(6),

    /**
     * Payee.
     */
    PAYEE(7);

    /**
     * The String name.
     */
    private String theName;

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Obtain class Id.
     * @return the Id
     */
    public int getId() {
        return theId;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseDataResource.getKeyForAssetType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Constructor.
     * @param uId the Id
     */
    private AssetType(final int uId) {
        theId = uId;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static AssetType fromId(final int id) throws JOceanusException {
        for (AssetType myClass : values()) {
            if (myClass.getId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + AssetType.class.getSimpleName() + ":" + id);
    }

    /**
     * Determine whether this is a valued asset.
     * @return true/false
     */
    public boolean isValued() {
        switch (this) {
            case DEPOSIT:
            case CASH:
            case LOAN:
                return true;
            case AUTOEXPENSE:
            case SECURITY:
            case PORTFOLIO:
            case PAYEE:
            default:
                return false;
        }
    }

    /**
     * Determine whether this is an asset.
     * @return true/false
     */
    public boolean isAsset() {
        switch (this) {
            case DEPOSIT:
            case AUTOEXPENSE:
            case SECURITY:
            case CASH:
            case LOAN:
                return true;
            case PORTFOLIO:
            case PAYEE:
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a deposit.
     * @return true/false
     */
    public boolean isDeposit() {
        switch (this) {
            case DEPOSIT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a loan.
     * @return true/false
     */
    public boolean isLoan() {
        switch (this) {
            case LOAN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a payee.
     * @return true/false
     */
    public boolean isPayee() {
        switch (this) {
            case PAYEE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is an autoExpense.
     * @return true/false
     */
    public boolean isAutoExpense() {
        switch (this) {
            case AUTOEXPENSE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain transaction type.
     * @param pPartner the partner account type.
     * @return transaction type
     */
    public TransactionType getTransactionType(final AssetType pPartner) {
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
}
