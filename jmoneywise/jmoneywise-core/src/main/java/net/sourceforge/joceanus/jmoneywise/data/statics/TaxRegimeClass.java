/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Enumeration of TaxRegime Classes.
 */
public enum TaxRegimeClass implements StaticInterface {
    /**
     * Archive tax regime.
     */
    ARCHIVE(1, 0),

    /**
     * Standard tax regime.
     */
    STANDARD(2, 1),

    /**
     * Low Interest Tax Band.
     */
    LOINTEREST(3, 2),

    /**
     * Additional tax band.
     */
    ADDITIONALBAND(4, 3),

    /**
     * Savings Allowance.
     */
    SAVINGSALLOWANCE(5, 4);

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
     * @param uId the id
     * @param uOrder the default order
     */
    TaxRegimeClass(final int uId,
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
            theName = StaticDataResource.getKeyForTaxRegime(this).getValue();
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
    public static TaxRegimeClass fromId(final int id) throws OceanusException {
        for (TaxRegimeClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TAXREGIME.toString() + ":" + id);
    }

    /**
     * Determine whether this tax regime supports a Low Salary Band.
     * @return <code>true/false</code>
     */
    public boolean hasLoSalaryBand() {
        switch (this) {
            case ARCHIVE:
            case STANDARD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether this tax regime treats capital gains as standard income.
     * @return <code>true/false</code>
     */
    public boolean hasCapitalGainsAsIncome() {
        return this == TaxRegimeClass.STANDARD;
    }

    /**
     * Determine whether this tax regime supports an additional taxation band.
     * @return <code>true/false</code>
     */
    public boolean hasAdditionalTaxBand() {
        switch (this) {
            case ADDITIONALBAND:
            case SAVINGSALLOWANCE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether this tax regime supports a savings allowance.
     * @return <code>true/false</code>
     */
    public boolean hasSavingsAllowance() {
        return this == TaxRegimeClass.SAVINGSALLOWANCE;
    }

    /**
     * Determine whether this tax regime supports age related allowances.
     * @return <code>true/false</code>
     */
    public boolean hasAgeRelatedAllowance() {
        return this != TaxRegimeClass.SAVINGSALLOWANCE;
    }

    /**
     * Determine whether this tax regime splits out residential capital gains.
     * @return <code>true/false</code>
     */
    public boolean hasResidentialCapitalGains() {
        return this == TaxRegimeClass.SAVINGSALLOWANCE;
    }
}
