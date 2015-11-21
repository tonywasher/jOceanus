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
 * Enumeration of CashCategory Type Classes.
 */
public enum CashCategoryClass implements CategoryInterface {
    /**
     * Cash Account.
     * <p>
     * This is a cash account and represents cash that is held by the client outside of any
     * institution.
     */
    CASH(1, 1),

    /**
     * AutoExpense Cash Account.
     * <p>
     * This is a cash account and represents cash that is held by the client outside of any
     * institution.
     */
    AUTOEXPENSE(2, 2),

    /**
     * Parent Category.
     * <p>
     * This is used as a sub-total bucket and is used purely for reporting purposes.
     */
    PARENT(3, 0);

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
    CashCategoryClass(final int uId,
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
            theName = StaticDataResource.getKeyForCashType(this).getValue();
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
    public static CashCategoryClass fromId(final int id) throws JOceanusException {
        for (CashCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.CASHTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the CashCategoryType is a parent category.
     * @return <code>true</code> if the cash category type is a parent category, <code>false</code>
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
