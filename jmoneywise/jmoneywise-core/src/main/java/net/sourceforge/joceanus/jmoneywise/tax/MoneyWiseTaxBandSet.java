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
package net.sourceforge.joceanus.jmoneywise.tax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataFormat;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Set of taxBands.
 */
public class MoneyWiseTaxBandSet
        implements MetisDataList, MetisDataFormat, Iterable<MoneyWiseTaxBand> {
    /**
     * List of Tax Bands.
     */
    private final List<MoneyWiseTaxBand> theTaxBands;

    /**
     * Constructor.
     */
    public MoneyWiseTaxBandSet() {
        theTaxBands = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pBands the set of tax bands
     */
    public MoneyWiseTaxBandSet(final MoneyWiseTaxBand... pBands) {
        this();
        for (MoneyWiseTaxBand myBand : pBands) {
            theTaxBands.add(myBand);
        }
    }

    /**
     * Constructor.
     * @param pSource the source to clone
     */
    public MoneyWiseTaxBandSet(final MoneyWiseTaxBandSet pSource) {
        this();
        for (MoneyWiseTaxBand myBand : pSource.theTaxBands) {
            theTaxBands.add(new MoneyWiseTaxBand(myBand));
        }
    }

    @Override
    public Iterator<MoneyWiseTaxBand> iterator() {
        return theTaxBands.iterator();
    }

    @Override
    public List<?> getUnderlyingList() {
        return theTaxBands;
    }

    @Override
    public String formatObject() {
        return theTaxBands.toString();
    }

    /**
     * Add a tax band.
     * @param pBand the tax band
     */
    public void addTaxBand(final MoneyWiseTaxBand pBand) {
        theTaxBands.add(pBand);
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
        private static final MetisField FIELD_RATE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_RATE.getValue());

        /**
         * Amount Field Id.
         */
        private static final MetisField FIELD_AMOUNT = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_AMOUNT.getValue());

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
        public MoneyWiseTaxBand(final TethysMoney pAmount,
                                final TethysRate pRate) {
            theAmount = pAmount;
            theRate = pRate;
        }

        /**
         * Constructor.
         * @param pRate the rate
         */
        public MoneyWiseTaxBand(final TethysRate pRate) {
            this(null, pRate);
        }

        /**
         * Constructor.
         * @param pSource the source band
         */
        public MoneyWiseTaxBand(final MoneyWiseTaxBand pSource) {
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
            return toString();
        }

        @Override
        public String toString() {
            StringBuilder myBuilder = new StringBuilder();
            if (theAmount != null) {
                myBuilder.append(theAmount);
                myBuilder.append('@');
            }
            myBuilder.append(theRate);
            return myBuilder.toString();
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
