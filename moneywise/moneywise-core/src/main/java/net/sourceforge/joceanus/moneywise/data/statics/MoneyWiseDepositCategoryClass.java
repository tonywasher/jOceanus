/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.statics;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Enumeration of DepositCategory Type Classes.
 */
public enum MoneyWiseDepositCategoryClass
        implements MoneyWiseCategoryInterface {
    /**
     * Checking Deposit.
     * <p>
     * These are standard checking deposit accounts that hold money on behalf of the client. Each
     * such account must be owned by a {@link MoneyWisePayeeClass#INSTITUTION} payee.
     */
    CHECKING(1, 1),

    /**
     * Savings Deposit.
     * <p>
     * These are standard savings accounts that hold money on behalf of the client. There is no
     * distinction as to whether they are easy access or restricted access. Each such account must
     * be owned by a {@link MoneyWisePayeeClass#INSTITUTION} payee.
     */
    SAVINGS(2, 2),

    /**
     * TaxFreeSavings.
     * <p>
     * This a bond account which is a specialised form of an {@link #SAVINGS} account. It has an
     * associated maturity date for the account.
     */
    TAXFREESAVINGS(3, 3),

    /**
     * Peer2Peer Deposit.
     * <p>
     * This a peer2peer account which is a specialised form of an {@link #SAVINGS} account.
     * LoyaltyBonuses are allowed, and the tax situation varies.
     */
    PEER2PEER(4, 4),

    /**
     * Bond Deposit.
     * <p>
     * This a bond account which is a specialised form of an {@link #SAVINGS} account. It has an
     * associated maturity date for the account.
     */
    BOND(5, 5),

    /**
     * Bond Deposit.
     * <p>
     * This a bond account which is a specialised form of an {@link #SAVINGS} account. It has an
     * associated maturity date for the account.
     */
    TAXFREEBOND(6, 6),

    /**
     * Parent Category.
     * <p>
     * This is used as a sub-total bucket and is used purely for reporting purposes.
     */
    PARENT(7, 0);

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
     * @param uId the Id
     * @param uOrder the default order.
     */
    MoneyWiseDepositCategoryClass(final int uId,
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
            theName = MoneyWiseStaticResource.getKeyForDepositType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws OceanusException on error
     */
    public static MoneyWiseDepositCategoryClass fromId(final int id) throws OceanusException {
        for (MoneyWiseDepositCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseStaticDataType.DEPOSITTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the DepositCategoryType is a child, and needs a parent.
     * @return <code>true</code> if the deposit category type is a child, <code>false</code>
     * otherwise.
     */
    public boolean isChild() {
        switch (this) {
            case CHECKING:
            case SAVINGS:
            case TAXFREESAVINGS:
            case PEER2PEER:
            case BOND:
            case TAXFREEBOND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the DepositCategoryType is be tax free.
     * @return <code>true</code> if the deposit category type is tax free, <code>false</code>
     * otherwise.
     */
    public boolean isTaxFree() {
        switch (this) {
            case TAXFREESAVINGS:
            case TAXFREEBOND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the DepositCategoryType has a maturity date.
     * @return <code>true</code> if the deposit category type has maturity, <code>false</code>
     * otherwise.
     */
    public boolean hasMaturity() {
        switch (this) {
            case BOND:
            case TAXFREEBOND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the DepositCategoryType is gross.
     * @return <code>true</code> if the deposit category type is gross, <code>false</code>
     * otherwise.
     */
    public boolean isGross() {
        return this == PEER2PEER;
    }

    /**
     * Determine whether the DepositCategoryType can provide cashBack.
     * @return <code>true</code> if the deposit category type can provide cashBack
     * <code>false</code> otherwise.
     */
    public boolean canCashBack() {
        switch (this) {
            case CHECKING:
            case PEER2PEER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the DepositCategoryType can provide loyaltyBonus.
     * @return <code>true</code> if the deposit category type can provide loyaltyBonus
     * <code>false</code> otherwise.
     */
    public boolean canLoyaltyBonus() {
        return this == PEER2PEER;
    }

    /**
     * Determine whether the DepositCategoryType is a parent category.
     * @return <code>true</code> if the deposit category type is a parent category,
     * <code>false</code> otherwise.
     */
    public boolean isParentCategory() {
        return this == PARENT;
    }

    @Override
    public boolean isTotals() {
        return false;
    }
}
