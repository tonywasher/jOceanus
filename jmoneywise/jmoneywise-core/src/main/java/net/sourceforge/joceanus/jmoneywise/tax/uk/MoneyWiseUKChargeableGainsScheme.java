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
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
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
            implements MetisDataContents, MetisDataList {
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
         * TaxCredit Field Id.
         */
        private static final MetisField FIELD_TAXCREDIT = FIELD_DEFS.declareEqualityField(StaticDataResource.TRANSTYPE_TAXCREDIT.getValue());

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
        private static final MetisField FIELD_TAXOWING = FIELD_DEFS.declareEqualityField("TaxOwing");

        /**
         * Ratio Field Id.
         */
        private static final MetisField FIELD_TAXRELIEF = FIELD_DEFS.declareEqualityField("TaxRelief");

        /**
         * The list of Slices.
         */
        private final List<MoneyWiseUKGainSlice> theSlices;

        /**
         * The total gains.
         */
        private final TethysMoney theTotalGains;

        /**
         * The total slices.
         */
        private final TethysMoney theTotalSlices;

        /**
         * The tax credit.
         */
        private final TethysMoney theTaxCredit;

        /**
         * The ratio.
         */
        private TethysRatio theRatio;

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
         * The taxOwing.
         */
        private TethysMoney theTaxOwing;

        /**
         * The taxRelief.
         */
        private TethysMoney theTaxRelief;

        /**
         * Constructor.
         * @param pConfig the tax configuration
         */
        protected MoneyWiseUKSlicedGains(final MoneyWiseUKTaxConfig pConfig) {
            /* Create the slices */
            theSlices = new ArrayList<>();

            /* Create the totals */
            TethysMoney myZero = pConfig.getTaxBands().getZeroAmount();
            theTotalGains = new TethysMoney(myZero);
            theTotalSlices = new TethysMoney(myZero);
            theTaxCredit = new TethysMoney(myZero);
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
         * Obtain the tax credit.
         * @return the tax credit
         */
        public TethysMoney getTaxCredit() {
            return theTaxCredit;
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
         * Obtain the tax owing.
         * @return the tax owing
         */
        public TethysMoney getTaxOwing() {
            return theTaxOwing;
        }

        /**
         * Obtain the tax relief.
         * @return the tax relief
         */
        public TethysMoney getTaxRelief() {
            return theTaxRelief;
        }

        /**
         * Process transaction.
         * @param pTrans the transaction
         * @param pGain the gain
         */
        protected void processTransaction(final Transaction pTrans,
                                          final TethysMoney pGain) {
            /* Create the slice and add to the list */
            MoneyWiseUKGainSlice mySlice = new MoneyWiseUKGainSlice(pTrans, pGain);
            theSlices.add(mySlice);

            /* Adjust totals */
            theTotalGains.addAmount(pGain);
            theTotalSlices.addAmount(mySlice.getSlice());
            theTaxCredit.addAmount(mySlice.getTaxCredit());
        }

        /**
         * calculate the tax.
         */
        protected void calculateTax() {
            /* Calculate the ratio */
            theRatio = new TethysRatio(theTotalGains, theTotalSlices);

            /* Calculate tax due */
            theTaxDue = theTaxedSlices.getTaxDue().valueAtRatio(theRatio);

            /* Calculate tax owing */
            theTaxOwing = new TethysMoney(theTaxDue);
            theTaxOwing.subtractAmount(theTaxCredit);

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
            if (FIELD_TAXCREDIT.equals(pField)) {
                return theTaxCredit;
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
            if (FIELD_TAXOWING.equals(pField)) {
                return theTaxOwing;
            }
            if (FIELD_TAXRELIEF.equals(pField)) {
                return theTaxRelief;
            }

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }

        @Override
        public List<?> getUnderlyingList() {
            return theSlices;
        }
    }

    /**
     * Sliced Gain.
     */
    public static class MoneyWiseUKGainSlice
            implements MetisDataContents {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKGainSlice.class.getSimpleName());

        /**
         * Date Field Id.
         */
        private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

        /**
         * Gain Field Id.
         */
        private static final MetisField FIELD_GAIN = FIELD_DEFS.declareEqualityField("Gain");

        /**
         * Years Field Id.
         */
        private static final MetisField FIELD_YEARS = FIELD_DEFS.declareEqualityField(StaticDataResource.TRANSINFO_QUALYEARS.getValue());

        /**
         * TaxCredit Field Id.
         */
        private static final MetisField FIELD_TAXCREDIT = FIELD_DEFS.declareEqualityField(StaticDataResource.TRANSTYPE_TAXCREDIT.getValue());

        /**
         * Slice Field Id.
         */
        private static final MetisField FIELD_SLICE = FIELD_DEFS.declareEqualityField("Slice");

        /**
         * Transaction Field Id.
         */
        private static final MetisField FIELD_TRANS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TRANSACTION.getItemName());

        /**
         * The Date.
         */
        private final TethysDate theDate;

        /**
         * The Gain.
         */
        private final TethysMoney theGain;

        /**
         * The TaxCredit.
         */
        private final TethysMoney theTaxCredit;

        /**
         * The Years.
         */
        private final Integer theYears;

        /**
         * The Slice.
         */
        private final TethysMoney theSlice;

        /**
         * The Transaction.
         */
        private final Transaction theTrans;

        /**
         * Constructor.
         * @param pTrans the transaction
         * @param pGain the gain
         */
        public MoneyWiseUKGainSlice(final Transaction pTrans,
                                    final TethysMoney pGain) {
            /* Store the parameters */
            theDate = pTrans.getDate();
            theGain = pGain;
            theTrans = pTrans;
            theYears = pTrans.getYears();
            theTaxCredit = pTrans.getTaxCredit();

            /* Calculate slice */
            theSlice = new TethysMoney(pGain);
            theSlice.divide(theYears);
        }

        /**
         * Obtain the date.
         * @return the date
         */
        public TethysDate getDate() {
            return theDate;
        }

        /**
         * Obtain the gain.
         * @return the gain
         */
        public TethysMoney getGain() {
            return theGain;
        }

        /**
         * Obtain the taxCredit.
         * @return the taxCredit
         */
        public TethysMoney getTaxCredit() {
            return theTaxCredit;
        }

        /**
         * Obtain the years.
         * @return the years
         */
        public Integer getYears() {
            return theYears;
        }

        /**
         * Obtain the slice.
         * @return the slice
         */
        public TethysMoney getSlice() {
            return theSlice;
        }

        /**
         * Obtain the transaction.
         * @return the transaction
         */
        public Transaction getTransaction() {
            return theTrans;
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
            if (FIELD_DATE.equals(pField)) {
                return theDate;
            }
            if (FIELD_GAIN.equals(pField)) {
                return theGain;
            }
            if (FIELD_TAXCREDIT.equals(pField)) {
                return theTaxCredit;
            }
            if (FIELD_YEARS.equals(pField)) {
                return theYears;
            }
            if (FIELD_SLICE.equals(pField)) {
                return theSlice;
            }
            if (FIELD_TRANS.equals(pField)) {
                return theTrans;
            }

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }
    }
}
