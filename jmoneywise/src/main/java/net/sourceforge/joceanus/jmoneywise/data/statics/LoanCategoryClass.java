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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of LoanCategory Type Classes.
 */
public enum LoanCategoryClass implements CategoryInterface {
    /**
     * CreditCard.
     * <p>
     * This is a Credit Card account, which is a specialised form of a {@link #LOAN} account. It simply differs in reporting treatment in that overall spend is
     * tracked.
     */
    CREDITCARD(1, 0),

    /**
     * PrivateLoan.
     * <p>
     * This is a PrivateLoan account, which is a specialised form of a {@link #LOAN} account. It represents a loan to/from the client from/to an individual
     * represented by a {@link PayeeTypeClass#INDIVIDUAL} account.
     */
    PRIVATELOAN(2, 1),

    /**
     * Generic Loan.
     * <p>
     * This is a Loan account which represents a loan to/from the client from/to a {@link PayeeTypeClass#INSTITUTION} account.
     */
    LOAN(3, 2),

    /**
     * Parent category.
     * <p>
     * This is used for the total of all categories and is used purely for reporting purposes.
     */
    PARENT(4, 3);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(LoanCategoryClass.class.getName());

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
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    private LoanCategoryClass(final int uId,
                              final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static LoanCategoryClass fromId(final int id) throws JOceanusException {
        for (LoanCategoryClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.LOANTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the LoanCategoryType is a child, and needs a parent.
     * @return <code>true</code> if the account category type is a child, <code>false</code> otherwise.
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
     * Determine whether the LoanCategoryType is a parent category.
     * @return <code>true</code> if the loan category type is a parent category, <code>false</code> otherwise.
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
