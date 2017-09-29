/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Income Tax Scheme.
 */
public class MoneyWiseUKIncomeScheme
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MoneyWiseUKIncomeScheme.class);

    /**
     * Relief Available Field Id.
     */
    private static final MetisDataField FIELD_RELIEF = FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.SCHEME_RELIEF_AVAILABLE.getValue());

    /**
     * Tax Relief available.
     */
    private final Boolean reliefAvailable;

    /**
     * Constructor.
     */
    protected MoneyWiseUKIncomeScheme() {
        this(Boolean.TRUE);
    }

    /**
     * Constructor.
     * @param pReliefAvailable Is tax relief available?
     */
    protected MoneyWiseUKIncomeScheme(final Boolean pReliefAvailable) {
        reliefAvailable = pReliefAvailable;
    }

    /**
     * Is tax relief available?
     * @return true/false
     */
    public Boolean taxReliefAvailable() {
        return reliefAvailable;
    }

    /**
     * Allocate the amount to the appropriate tax bands.
     * @param pConfig the taxConfig
     * @param pBasis the taxBasis
     * @param pAmount the amount to be allocated
     * @return the tax bands
     */
    protected MoneyWiseTaxBandSet allocateToTaxBands(final MoneyWiseUKTaxConfig pConfig,
                                                     final TaxBasisClass pBasis,
                                                     final TethysMoney pAmount) {
        /* Handle negative amounts */
        final TethysMoney myAmount = new TethysMoney(pAmount);
        if (!myAmount.isPositive()) {
            myAmount.setZero();
        }

        /* Determine the taxBand set */
        final MoneyWiseTaxBandSet myTaxBands = determineTaxBands(pConfig, pBasis, myAmount);

        /* Adjust allowances and taxBands */
        final TethysMoney myRemaining = adjustAllowances(pConfig, myAmount);
        adjustTaxBands(pConfig, myRemaining);

        /* return the taxBands */
        return myTaxBands;
    }

    /**
     * Determine the taxBand set.
     * @param pConfig the taxConfig
     * @param pBasis the taxBasis
     * @param pAmount the amount to be allocated
     * @return the amount remaining
     */
    private MoneyWiseTaxBandSet determineTaxBands(final MoneyWiseUKTaxConfig pConfig,
                                                  final TaxBasisClass pBasis,
                                                  final TethysMoney pAmount) {
        /* Create a new taxBand set */
        final MoneyWiseTaxBandSet myTaxBands = new MoneyWiseTaxBandSet();
        final TethysMoney myRemaining = new TethysMoney(pAmount);

        /* Determine allowance */
        final TethysMoney myAllowance = new TethysMoney(getAmountInAllowance(pConfig, myRemaining));
        if (myAllowance.isNonZero() && reliefAvailable) {
            myTaxBands.addTaxBand(new MoneyWiseTaxBand(myAllowance, TethysRate.getWholePercentage(0)));
            myRemaining.subtractAmount(myAllowance);
        }

        /* Loop through the taxBands */
        final Iterator<MoneyWiseTaxBand> myIterator = taxBandIterator(pConfig, pBasis);
        while (myRemaining.isNonZero()
               && myIterator.hasNext()) {
            /* Determine amount in band */
            final MoneyWiseTaxBand myBand = myIterator.next();
            TethysMoney myAmount = getAmountInBand(myBand.getAmount(), myRemaining);

            /* Add any held-over allowance */
            if (!reliefAvailable && myAllowance.isNonZero()) {
                myAmount = new TethysMoney(myAmount);
                myAmount.addAmount(myAllowance);
                myAllowance.setZero();
            }

            /* allocate band and adjust */
            myTaxBands.addTaxBand(new MoneyWiseTaxBand(myAmount, myBand.getRate()));
            myRemaining.subtractAmount(myAmount);
        }

        /* Return the taxBands */
        return myTaxBands;
    }

    /**
     * Obtain the taxBand iterator.
     * @param pConfig the taxConfig
     * @param pBasis the taxBasis
     * @return the iterator
     */
    protected Iterator<MoneyWiseTaxBand> taxBandIterator(final MoneyWiseUKTaxConfig pConfig,
                                                         final TaxBasisClass pBasis) {
        return pConfig.getTaxBands().iterator();
    }

    /**
     * Obtain the taxFree amount.
     * @param pConfig the taxConfig
     * @param pAmount the amount that is to be adjusted
     * @return the amount remaining
     */
    protected TethysMoney getAmountInAllowance(final MoneyWiseUKTaxConfig pConfig,
                                               final TethysMoney pAmount) {
        /* Obtain the amount covered by the allowance */
        return getAmountInBand(pConfig.getAllowance(), pAmount);
    }

    /**
     * Adjust Allowances.
     * @param pConfig the taxConfig
     * @param pAmount the amount that is to be adjusted
     * @return the amount remaining
     */
    protected TethysMoney adjustAllowances(final MoneyWiseUKTaxConfig pConfig,
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
        final TethysMoney myRemaining = new TethysMoney(pAmount);

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
    protected static TethysMoney getAmountInBand(final TethysMoney pBand,
                                                 final TethysMoney pAmount) {
        /* Return the lesser of the two */
        return pBand != null && pAmount.compareTo(pBand) > 0
                                                             ? pBand
                                                             : pAmount;
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_RELIEF.equals(pField)) {
            return reliefAvailable;
        }

        /* Not recognised */
        return MetisDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return getDataFieldSet().getName();
    }
}
