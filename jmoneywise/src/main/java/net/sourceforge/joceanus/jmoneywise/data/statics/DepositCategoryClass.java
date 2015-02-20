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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of DepositCategory Type Classes.
 */
public enum DepositCategoryClass implements CategoryInterface {
    /**
     * Checking Deposit.
     * <p>
     * These are standard checking deposit accounts that hold money on behalf of the client. Each such account must be owned by a
     * {@link PayeeTypeClass#INSTITUTION} payee.
     */
    CHECKING(1, 1),

    /**
     * Savings Deposit.
     * <p>
     * These are standard savings accounts that hold money on behalf of the client. There is no distinction as to whether they are easy access or restricted
     * access. Each such account must be owned by a {@link PayeeTypeClass#INSTITUTION} payee.
     */
    SAVINGS(2, 2),

    /**
     * Bond Deposit.
     * <p>
     * This a bond account which is a specialised form of an {@link #SAVINGS} account. It has an associated maturity date for the account.
     */
    BOND(3, 3),

    /**
     * Parent Category.
     * <p>
     * This is used as a sub-total bucket and is used purely for reporting purposes.
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
     * @param uId the Id
     * @param uOrder the default order.
     */
    private DepositCategoryClass(final int uId,
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
            theName = StaticDataResource.getKeyForDepositType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static DepositCategoryClass fromId(final int id) throws JOceanusException {
        for (DepositCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.DEPOSITTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the DepositCategoryType is a child, and needs a parent.
     * @return <code>true</code> if the deposit category type is a child, <code>false</code> otherwise.
     */
    public boolean isChild() {
        switch (this) {
            case CHECKING:
            case SAVINGS:
            case BOND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the DepositCategoryType can be tax free.
     * @return <code>true</code> if the deposit category type can be tax free, <code>false</code> otherwise.
     */
    public boolean canTaxFree() {
        switch (this) {
            case SAVINGS:
            case BOND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the DepositCategoryType is a parent category.
     * @return <code>true</code> if the deposit category type is a parent category, <code>false</code> otherwise.
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
