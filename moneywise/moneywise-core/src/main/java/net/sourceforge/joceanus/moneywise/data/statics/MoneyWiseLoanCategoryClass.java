/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;

/**
 * Enumeration of LoanCategory Type Classes.
 */
public enum MoneyWiseLoanCategoryClass
        implements MoneyWiseCategoryInterface {
    /**
     * CreditCard.
     * <p>
     * This is a Credit Card account, which is a specialised form of a {@link #LOAN} account. It
     * simply differs in reporting treatment in that overall spend is tracked.
     */
    CREDITCARD(1, 1),

    /**
     * PrivateLoan.
     * <p>
     * This is a PrivateLoan account, which is a specialised form of a {@link #LOAN} account. It
     * represents a loan to/from the client from/to an individual represented by a
     * {@link MoneyWisePayeeClass#INDIVIDUAL} account.
     */
    PRIVATELOAN(2, 2),

    /**
     * Generic Loan.
     * <p>
     * This is a Loan account which represents a loan to/from the client from/to a
     * {@link MoneyWisePayeeClass#INSTITUTION} account.
     */
    LOAN(3, 3),

    /**
     * Parent category.
     * <p>
     * This is used for the total of all categories and is used purely for reporting purposes.
     */
    PARENT(4, 0);

    /**
     * The String name.
     */
    private String theName;

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Constructor.
     *
     * @param uId    the Id
     * @param uOrder the default order.
     */
    MoneyWiseLoanCategoryClass(final int uId,
                               final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    @Override
    public int getClassId() {
        return theId;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseStaticResource.getKeyForLoanType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     *
     * @param id the id value
     * @return the corresponding enum object
     * @throws OceanusException on error
     */
    public static MoneyWiseLoanCategoryClass fromId(final int id) throws OceanusException {
        for (MoneyWiseLoanCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseStaticDataType.LOANTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the LoanCategoryType is a child, and needs a parent.
     *
     * @return <code>true</code> if the account category type is a child, <code>false</code>
     * otherwise.
     */
    public boolean isChild() {
        switch (this) {
            case LOAN:
            case PRIVATELOAN:
            case CREDITCARD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the LoanCategoryType can provide cashBack.
     *
     * @return <code>true</code> if the loan category type can provide cashBack, <code>false</code>
     * otherwise.
     */
    public boolean canCashBack() {
        switch (this) {
            case CREDITCARD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the LoanCategoryType is a parent category.
     *
     * @return <code>true</code> if the loan category type is a parent category, <code>false</code>
     * otherwise.
     */
    public boolean isParentCategory() {
        switch (this) {
            case PARENT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isTotals() {
        return false;
    }
}
