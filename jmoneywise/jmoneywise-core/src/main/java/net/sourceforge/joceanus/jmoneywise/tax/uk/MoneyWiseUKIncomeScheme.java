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

import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Income Tax Scheme.
 */
public class MoneyWiseUKIncomeScheme {
    /**
     * Adjust For TaxBasis.
     * @param pConfig the taxConfig
     * @param pBasis the taxBasis
     * @param pAmount the amount that is to be adjusted
     * @return the amount remaining
     */
    protected MoneyWiseTaxBandSet adjustForBasis(final MoneyWiseUKTaxConfig pConfig,
                                                 final TaxBasisClass pBasis,
                                                 final TethysMoney pAmount) {
        /* Adjust allowances and taxBands */
        TethysMoney myRemaining = adjustAllowances(pConfig, pBasis, pAmount);
        adjustTaxBands(pConfig, pAmount);
        return null;
    }

    /**
     * Obtain the taxFree amount.
     * @param pConfig the taxConfig
     * @param pBasis the taxBasis
     * @param pAmount the amount that is to be adjusted
     * @return the amount remaining
     */
    protected TethysMoney getAmountInAllowance(final MoneyWiseUKTaxConfig pConfig,
                                               final TaxBasisClass pBasis,
                                               final TethysMoney pAmount) {
        /* Obtain the amount covered by the allowance */
        return getAmountInBand(pConfig.getAllowance(), pAmount);
    }

    /**
     * Adjust Allowances.
     * @param pConfig the taxConfig
     * @param pBasis the taxBasis
     * @param pAmount the amount that is to be adjusted
     * @return the amount remaining
     */
    protected TethysMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
                                           final TaxBasisClass pBasis,
                                           final TethysMoney pAmount) {
        /* Adjust the basic allowance */
        return adjustForAllowance(pConfig.getAllowance(), pAmount);
    }

    /**
     * Adjust TaxBands.
     * @param pConfig the taxConfig
     * @param pAmount the amount that is to be adjusted
     */
    protected void adjustTaxBands(final MoneyWiseUKTaxConfig pConfig,
                                  final TethysMoney pAmount) {
        /* Loop through the taxBands */
        TethysMoney myRemaining = pAmount;
        for (MoneyWiseTaxBand myBand : pConfig.getTaxBands()) {
            /* If we have nothing left to adjust, we have finished */
            if (myRemaining.isZero()
                || myBand.getAmount() == null) {
                break;
            }

            /* Adjust the band */
            myRemaining = adjustForAllowance(myBand.getAmount(), myRemaining);
        }
    }

    /**
     * Adjust For an allowance/band.
     * @param pAllowance the allowance
     * @param pAmount the amount that is to be adjusted
     * @return the amount remaining
     */
    protected TethysMoney adjustForAllowance(final TethysMoney pAllowance,
                                             final TethysMoney pAmount) {
        /* Take a copy of the amount */
        TethysMoney myRemaining = new TethysMoney(pAmount);

        /* If we have exhausted the allowance */
        if (myRemaining.compareTo(pAllowance) > 0) {
            /* Subtract allowance */
            myRemaining.subtractAmount(pAllowance);
            pAllowance.setZero();

            /* else the allowance covers everything */
        } else {
            /* adjust the allowance */
            pAllowance.subtractAmount(myRemaining);
            myRemaining.setZero();
        }

        /* return the remaining amount */
        return myRemaining;
    }

    /**
     * Obtain the amount of income that falls in a band.
     * @param pBand the band
     * @param pAmount the amount available
     * @return the amount within the band
     */
    protected TethysMoney getAmountInBand(final TethysMoney pBand,
                                          final TethysMoney pAmount) {
        /* Return the lesser of the two */
        return pAmount.compareTo(pBand) > 0
                                            ? pBand
                                            : pAmount;
    }
}
