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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseChargeableGainSlice;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * Taxable Gains Scheme.
 */
public class MoneyWiseUKChargeableGainsScheme
        extends MoneyWiseUKIncomeScheme {
    /**
     * Sliced Gains.
     */
    public static class MoneyWiseUKSlicedGains
            implements MetisDataContents, MetisDataList<MoneyWiseChargeableGainSlice> {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKSlicedGains.class.getSimpleName());

        /**
         * Total Gains Field Id.
         */
        private static final MetisField FIELD_TOTALGAIN = FIELD_DEFS.declareEqualityField("TotalGain");

        /**
         * Total Slices Field Id.
         */
        private static final MetisField FIELD_TOTALSLICES = FIELD_DEFS.declareEqualityField("TotalSlices");

        /**
         * Ratio Field Id.
         */
        private static final MetisField FIELD_RATIO = FIELD_DEFS.declareEqualityField("Ratio");

        /**
         * TaxedGains Field Id.
         */
        private static final MetisField FIELD_TAXEDGAINS = FIELD_DEFS.declareEqualityField("TaxedGains");

        /**
         * TaxedSlices Field Id.
         */
        private static final MetisField FIELD_TAXEDSLICES = FIELD_DEFS.declareEqualityField("TaxedSlices");

        /**
         * TaxDue Field Id.
         */
        private static final MetisField FIELD_TAXDUE = FIELD_DEFS.declareEqualityField("TaxDue");

        /**
         * Ratio Field Id.
         */
        private static final MetisField FIELD_TAXRELIEF = FIELD_DEFS.declareEqualityField("TaxRelief");

        /**
         * The list of Slices.
         */
        private final List<MoneyWiseChargeableGainSlice> theSlices;

        /**
         * The total gains.
         */
        private final TethysMoney theTotalGains;

        /**
         * The total slices.
         */
        private final TethysMoney theTotalSlices;

        /**
         * The ratio.
         */
        private final TethysRatio theRatio;

        /**
         * The taxAnalysis on gains.
         */
        private MoneyWiseUKChargeableTaxDueBucket theTaxedGains;

        /**
         * The taxAnalysis on slices.
         */
        private MoneyWiseTaxDueBucket theTaxedSlices;

        /**
         * The taxDue on slices.
         */
        private TethysMoney theTaxDue;

        /**
         * The taxRelief.
         */
        private TethysMoney theTaxRelief;

        /**
         * Constructor.
         * @param pConfig the tax configuration
         * @param pSource the tax source
         */
        protected MoneyWiseUKSlicedGains(final MoneyWiseUKTaxConfig pConfig,
                                         final MoneyWiseTaxSource pSource) {
            /* Create the slices */
            theSlices = new ArrayList<>();

            /* Create the totals */
            TethysMoney myZero = pConfig.getTaxBands().getZeroAmount();
            theTotalGains = new TethysMoney(myZero);
            theTotalSlices = new TethysMoney(myZero);

            /* Process the slices */
            Iterator<MoneyWiseChargeableGainSlice> myIterator = pSource.getGainSlices().getUnderlyingList().iterator();
            while (myIterator.hasNext()) {
                MoneyWiseChargeableGainSlice mySlice = myIterator.next();
                processSlice(mySlice);
            }

            /* Calculate the ratio */
            theRatio = new TethysRatio(theTotalGains, theTotalSlices);
        }

        /**
         * Obtain the total gains.
         * @return the basis
         */
        public TethysMoney getTotalGains() {
            return theTotalGains;
        }

        /**
         * Obtain the total slices.
         * @return the basis
         */
        public TethysMoney getTotalSlices() {
            return theTotalGains;
        }

        /**
         * Obtain the ratio.
         * @return the ratio
         */
        public TethysRatio getRatio() {
            return theRatio;
        }

        /**
         * Obtain the taxed gains.
         * @return the taxed gains
         */
        public MoneyWiseUKChargeableTaxDueBucket getTaxedGains() {
            return theTaxedGains;
        }

        /**
         * Obtain the taxed slices.
         * @return the taxed slices
         */
        public MoneyWiseTaxDueBucket getTaxedSlices() {
            return theTaxedSlices;
        }

        /**
         * Obtain the tax due.
         * @return the tax due
         */
        public TethysMoney getTaxDue() {
            return theTaxDue;
        }

        /**
         * Obtain the tax relief.
         * @return the tax relief
         */
        public TethysMoney getTaxRelief() {
            return theTaxRelief;
        }

        /**
         * Process slice.
         * @param pSlice the slice
         */
        private void processSlice(final MoneyWiseChargeableGainSlice pSlice) {
            /* Add the slice to the list */
            theSlices.add(pSlice);

            /* Adjust totals */
            theTotalGains.addAmount(pSlice.getGain());
            theTotalSlices.addAmount(pSlice.getSlice());
        }

        /**
         * calculate the tax.
         */
        protected void calculateTax() {
            /* Calculate tax due */
            theTaxDue = theTaxedSlices.getTaxDue().valueAtRatio(theRatio);

            /* Calculate tax relief */
            theTaxRelief = theTaxedGains.getTaxDue();
            theTaxRelief.subtractAmount(theTaxDue);
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_TOTALGAIN.equals(pField)) {
                return theTotalGains;
            }
            if (FIELD_TOTALSLICES.equals(pField)) {
                return theTotalSlices;
            }
            if (FIELD_RATIO.equals(pField)) {
                return theRatio;
            }
            if (FIELD_TAXEDGAINS.equals(pField)) {
                return theTaxedGains;
            }
            if (FIELD_TAXEDSLICES.equals(pField)) {
                return theTaxedSlices;
            }
            if (FIELD_TAXDUE.equals(pField)) {
                return theTaxDue;
            }
            if (FIELD_TAXRELIEF.equals(pField)) {
                return theTaxRelief;
            }

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public List<MoneyWiseChargeableGainSlice> getUnderlyingList() {
            return theSlices;
        }
    }
}
