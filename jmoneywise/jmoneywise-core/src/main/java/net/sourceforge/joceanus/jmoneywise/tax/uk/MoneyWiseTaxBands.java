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
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * UK TaxBands.
 */
public class MoneyWiseTaxBands
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxBands.class.getSimpleName());

    /**
     * StandardSet Field Id.
     */
    private static final MetisField FIELD_STANDARD = FIELD_DEFS.declareEqualityField("StandardSet");

    /**
     * Low Savings Band Field Id.
     */
    private static final MetisField FIELD_LOSAVINGS = FIELD_DEFS.declareEqualityField("LoSavingsBand");

    /**
     * Standard TaxBandSet.
     */
    private final MoneyWiseTaxBandSet theStandard;

    /**
     * Low Savings TaxBandSet.
     */
    private final MoneyWiseTaxBand theLoSavings;

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     */
    protected MoneyWiseTaxBands(final MoneyWiseTaxBandSet pStandard) {
        this(pStandard, null);
    }

    /**
     * Constructor.
     * @param pStandard the standard taxBands
     * @param pLoSavings the loSavings taxBand
     */
    protected MoneyWiseTaxBands(final MoneyWiseTaxBandSet pStandard,
                                final MoneyWiseTaxBand pLoSavings) {
        theStandard = pStandard;
        theLoSavings = pLoSavings;
    }

    /**
     * Constructor.
     * @param pSource the source taxBands
     */
    protected MoneyWiseTaxBands(final MoneyWiseTaxBands pSource) {
        theStandard = new MoneyWiseTaxBandSet(pSource.getStandardSet());
        MoneyWiseTaxBand mySavings = pSource.getLoSavings();
        theLoSavings = mySavings == null
                                         ? null
                                         : new MoneyWiseTaxBand(mySavings);
    }

    /**
     * Obtain the standard taxBands.
     * @return the taxBands
     */
    public MoneyWiseTaxBandSet getStandardSet() {
        return theStandard;
    }

    /**
     * Obtain the low savings taxBand.
     * @return the taxBands
     */
    public MoneyWiseTaxBand getLoSavings() {
        return theLoSavings;
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
        if (FIELD_STANDARD.equals(pField)) {
            return theStandard;
        }
        if (FIELD_LOSAVINGS.equals(pField)) {
            return theLoSavings == null
                                        ? MetisFieldValue.SKIP
                                        : theLoSavings;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * MoneyWiseTaxBand set.
     */
    public static class MoneyWiseTaxBandSet
            implements MetisDataList {
        /**
         * List of Tax Bands.
         */
        private final List<MoneyWiseTaxBand> theTaxBands;

        /**
         * Constructor.
         * @param pBands the set of tax bands
         */
        protected MoneyWiseTaxBandSet(final MoneyWiseTaxBand... pBands) {
            theTaxBands = new ArrayList<>();
            for (MoneyWiseTaxBand myBand : pBands) {
                theTaxBands.add(myBand);
            }
        }

        /**
         * Constructor.
         * @param pSource the source to clone
         */
        protected MoneyWiseTaxBandSet(final MoneyWiseTaxBandSet pSource) {
            theTaxBands = new ArrayList<>();
            for (MoneyWiseTaxBand myBand : pSource.theTaxBands) {
                theTaxBands.add(new MoneyWiseTaxBand(myBand));
            }
        }

        /**
         * Obtain an iterator.
         * @return the iterator
         */
        public Iterator<MoneyWiseTaxBand> iterator() {
            return theTaxBands.iterator();
        }

        @Override
        public List<?> getUnderlyingList() {
            return theTaxBands;
        }
    }

    /**
     * MoneyWiseTaxBand class.
     */
    public static class MoneyWiseTaxBand
            implements MetisDataContents {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxBand.class.getSimpleName());

        /**
         * StandardSet Field Id.
         */
        private static final MetisField FIELD_RATE = FIELD_DEFS.declareEqualityField("Rate");

        /**
         * Amount Field Id.
         */
        private static final MetisField FIELD_AMOUNT = FIELD_DEFS.declareEqualityField("Amount");

        /**
         * Amount.
         */
        private final TethysMoney theAmount;

        /**
         * Rate.
         */
        private final TethysRate theRate;

        /**
         * Constructor.
         * @param pAmount the amount
         * @param pRate the rate
         */
        protected MoneyWiseTaxBand(final TethysMoney pAmount,
                                   final TethysRate pRate) {
            theAmount = pAmount;
            theRate = pRate;
        }

        /**
         * Constructor.
         * @param pRate the rate
         */
        protected MoneyWiseTaxBand(final TethysRate pRate) {
            this(null, pRate);
        }

        /**
         * Constructor.
         * @param pSource the source band
         */
        protected MoneyWiseTaxBand(final MoneyWiseTaxBand pSource) {
            TethysMoney myAmount = pSource.getAmount();
            theAmount = myAmount == null
                                         ? null
                                         : new TethysMoney(myAmount);
            theRate = pSource.getRate();
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public TethysMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the rate.
         * @return the rate
         */
        public TethysRate getRate() {
            return theRate;
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
            if (FIELD_RATE.equals(pField)) {
                return theRate;
            }
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount == null
                                         ? MetisFieldValue.SKIP
                                         : theAmount;
            }

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }
    }
}
