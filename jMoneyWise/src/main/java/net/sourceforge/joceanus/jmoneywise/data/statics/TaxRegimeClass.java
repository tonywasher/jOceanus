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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.StaticInterface;

/**
 * Enumeration of TaxRegime Classes.
 */
public enum TaxRegimeClass implements StaticInterface {
    /**
     * Archive tax regime.
     */
    Archive(1, 0),

    /**
     * Standard tax regime.
     */
    Standard(2, 1),

    /**
     * Low Interest Tax Band.
     */
    LoInterest(3, 2),

    /**
     * Additional tax band.
     */
    AdditionalBand(4, 3);

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

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the default order
     */
    private TaxRegimeClass(final int uId,
                           final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException on error
     */
    public static TaxRegimeClass fromId(final int id) throws JDataException {
        for (TaxRegimeClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Tax Regime Class Id: "
                                                      + id);
    }

    /**
     * Determine whether this tax regime supports a Low Salary Band.
     * @return <code>true/false</code>
     */
    public boolean hasLoSalaryBand() {
        switch (this) {
            case Archive:
                return true;
            case Standard:
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
        switch (this) {
            case Standard:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether this tax regime supports an additional taxation band.
     * @return <code>true/false</code>
     */
    public boolean hasAdditionalTaxBand() {
        switch (this) {
            case AdditionalBand:
                return true;
            default:
                return false;
        }
    }
}