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
package net.sourceforge.joceanus.jmoneywise.tax.uk;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.tax.uk.MoneyWiseTaxBands.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Savings UK Tax Allowance.
 */
public class MoneyWiseSavingsAllowance
        extends MoneyWiseBasicAllowance {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseSavingsAllowance.class.getSimpleName(), MoneyWiseBasicAllowance.getBaseFields());

    /**
     * SavingsAllowance Field Id.
     */
    private static final MetisField FIELD_SAVINGSALLOWANCE = FIELD_DEFS.declareEqualityField("SavingsAllowance");

    /**
     * DividendsAllowance Field Id.
     */
    private static final MetisField FIELD_DIVIDENDALLOWANCE = FIELD_DEFS.declareEqualityField("DividendAllowance");

    /**
     * AdditionalAllowanceLimit Field Id.
     */
    private static final MetisField FIELD_ADDALLOWLIMIT = FIELD_DEFS.declareEqualityField("AdditionalAllowanceLimit");

    /**
     * SavingsAllowance.
     */
    private final TethysMoney theSavingsAllowance;

    /**
     * DividendAllowance.
     */
    private final TethysMoney theDividendAllowance;

    /**
     * AdditionalAllowanceLimit.
     */
    private final TethysMoney theAddAllowLimit;

    /**
     * Constructor.
     * @param pAllowance the allowance
     * @param pRentalAllowance the rental allowance
     * @param pCapitalAllowance the capital allowance
     * @param pSavingsAllowance the savings allowance
     * @param pDividendAllowance the dividend allowance
     * @param pAddAllowLimit the additional allowance limit
     */
    protected MoneyWiseSavingsAllowance(final TethysMoney pAllowance,
                                        final TethysMoney pRentalAllowance,
                                        final TethysMoney pCapitalAllowance,
                                        final TethysMoney pSavingsAllowance,
                                        final TethysMoney pDividendAllowance,
                                        final TethysMoney pAddAllowLimit) {
        super(pAllowance, pRentalAllowance, pCapitalAllowance, MoneyWiseMarginalReduction.ONEINTWO);
        theSavingsAllowance = pSavingsAllowance;
        theDividendAllowance = pDividendAllowance;
        theAddAllowLimit = pAddAllowLimit;
    }

    /**
     * Obtain the savings allowance.
     * @return the Allowance
     */
    protected TethysMoney getSavingsAllowance() {
        return theSavingsAllowance;
    }

    /**
     * Obtain the dividend allowance.
     * @return the Allowance
     */
    protected TethysMoney getDividendAllowance() {
        return theDividendAllowance;
    }

    /**
     * Obtain the additional Allowance limit.
     * @return the Limit
     */
    protected TethysMoney getAdditionalAllowanceLimit() {
        return theAddAllowLimit;
    }

    @Override
    protected TethysMoney calculateBasicAllowance(final MoneyWiseTaxConfig pConfig) {
        /* Determine Basic allowance */
        TethysMoney myAllowance = getAllowance();

        /* If we have additional tax possible and we are above the allowance limit */
        TethysMoney myGross = pConfig.getGrossTaxable();
        if (myGross.compareTo(theAddAllowLimit) > 0) {
            /* Calculate and apply the reduction */
            TethysMoney myReduction = getMarginalReduction().calculateReduction(myGross, theAddAllowLimit);
            myAllowance = new TethysMoney(myAllowance);
            myAllowance.subtractAmount(myReduction);

            /* If we have reduced below zero */
            if (!myAllowance.isPositive()) {
                /* Set the allowance to zero */
                myAllowance.setZero();
            }
        }

        /* Return the allowance */
        return myAllowance;
    }

    @Override
    protected TethysMoney calculateSavingsAllowance(final MoneyWiseTaxConfig pConfig) {
        /* Obtain the gross taxable and allowance */
        TethysMoney myGross = pConfig.getGrossTaxable();
        TethysMoney myBoundary = new TethysMoney(pConfig.getAllowance());

        /* Obtain the tax bands */
        MoneyWiseTaxBands myBands = pConfig.getTaxYear().getStandardBands();
        Iterator<MoneyWiseTaxBand> myIterator = myBands.getStandardSet().iterator();

        /* If we have a basic band */
        if (myIterator.hasNext()) {
            /* If we are only a basic taxPayer return the full allowance */
            myBoundary.addAmount(myIterator.next().getAmount());
            if (myBoundary.compareTo(myGross) >= 0) {
                return theSavingsAllowance;
            }
        }

        /* If we have a high band */
        TethysMoney myAllowance = new TethysMoney(theSavingsAllowance);
        if (myIterator.hasNext()) {
            /* If we are only a high taxPayer return the half allowance */
            myBoundary.addAmount(myIterator.next().getAmount());
            if (myBoundary.compareTo(myGross) >= 0) {
                myAllowance.divide(2);
                return myAllowance;
            }
        }

        /* No Allowance */
        myAllowance.setZero();
        return theSavingsAllowance;
    }

    @Override
    protected TethysMoney calculateDividendAllowance() {
        return theDividendAllowance;
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_SAVINGSALLOWANCE.equals(pField)) {
            return theSavingsAllowance;
        }
        if (FIELD_DIVIDENDALLOWANCE.equals(pField)) {
            return theDividendAllowance;
        }
        if (FIELD_ADDALLOWLIMIT.equals(pField)) {
            return theAddAllowLimit;
        }

        /* Pass call on */
        return super.getFieldValue(pField);
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }
}
