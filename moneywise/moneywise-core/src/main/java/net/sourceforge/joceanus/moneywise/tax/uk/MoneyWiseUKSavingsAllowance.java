/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.tax.uk;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseMarginalReduction;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Savings UK Tax Allowance.
 */
public class MoneyWiseUKSavingsAllowance
        extends MoneyWiseUKBasicAllowance {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKSavingsAllowance> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKSavingsAllowance.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_SAVINGS, MoneyWiseUKSavingsAllowance::getSavingsAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_DIVIDEND, MoneyWiseUKSavingsAllowance::getDividendAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.LIMIT_ADDALLOWANCE, MoneyWiseUKSavingsAllowance::getAdditionalAllowanceLimit);
    }

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
    protected MoneyWiseUKSavingsAllowance(final TethysMoney pAllowance,
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
    protected TethysMoney calculateBasicAllowance(final MoneyWiseUKTaxConfig pConfig) {
        /* Determine Basic allowance */
        TethysMoney myAllowance = getAllowance();

        /* If we have additional tax possible and we are above the allowance limit */
        final TethysMoney myGross = pConfig.getGrossTaxable();
        if (myGross.compareTo(theAddAllowLimit) > 0) {
            /* Calculate and apply the reduction */
            final TethysMoney myReduction = getMarginalReduction().calculateReduction(myGross, theAddAllowLimit);
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
    protected TethysMoney calculateSavingsAllowance(final MoneyWiseUKTaxConfig pConfig) {
        /* Obtain the gross taxable and allowance */
        final TethysMoney myGross = pConfig.getGrossTaxable();
        final TethysMoney myBoundary = new TethysMoney(pConfig.getAllowance());

        /* Obtain the tax bands */
        final MoneyWiseUKTaxBands myBands = pConfig.getTaxYear().getTaxBands();
        final Iterator<MoneyWiseTaxBand> myIterator = myBands.getStandardSet().iterator();

        /* If we have a basic band */
        if (myIterator.hasNext()) {
            /* If we are only a basic taxPayer return the full allowance */
            myBoundary.addAmount(myIterator.next().getAmount());
            if (myBoundary.compareTo(myGross) >= 0) {
                return theSavingsAllowance;
            }
        }

        /* If we have a high band */
        final TethysMoney myAllowance = new TethysMoney(theSavingsAllowance);
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
    public MetisFieldSet<MoneyWiseUKSavingsAllowance> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }
}
