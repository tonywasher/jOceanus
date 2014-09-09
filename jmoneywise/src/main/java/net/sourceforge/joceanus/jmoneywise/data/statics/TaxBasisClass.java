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
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;

/**
 * Enumeration of Tax Basis Classes.
 */
public enum TaxBasisClass implements StaticInterface {
    /**
     * Gross Salary Income.
     */
    GROSSSALARY(1, 0),

    /**
     * Gross Interest Income.
     */
    GROSSINTEREST(2, 1),

    /**
     * Gross Dividend Income.
     */
    GROSSDIVIDEND(3, 2),

    /**
     * Gross Unit Trust Dividend Income.
     */
    GROSSUTDIVIDEND(4, 3),

    /**
     * Gross Rental Income.
     */
    GROSSRENTAL(5, 4),

    /**
     * Gross Taxable gains.
     */
    GROSSTAXABLEGAINS(6, 5),

    /**
     * Gross Capital gains.
     */
    GROSSCAPITALGAINS(7, 6),

    /**
     * Total Tax Paid.
     */
    TAXPAID(8, 7),

    /**
     * Market Growth.
     */
    MARKET(9, 8),

    /**
     * Tax Free Income.
     */
    TAXFREE(10, 9),

    /**
     * Gross Expense.
     */
    EXPENSE(11, 10),

    /**
     * Virtual Income.
     */
    VIRTUAL(12, 11);

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
            theName = ResourceMgr.getString(StaticDataResource.getKeyForTaxBasis(this));
        }

        /* return the name */
        return theName;
    }

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the order
     */
    private TaxBasisClass(final int uId,
                          final int uOrder) {
        /* Set values */
        theId = uId;
        theOrder = uOrder;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static TaxBasisClass fromId(final int id) throws JOceanusException {
        for (TaxBasisClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TAXBASIS.toString() + ":" + id);
    }
}
