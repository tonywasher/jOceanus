/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.tax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Set of taxBands.
 */
public class MoneyWiseTaxBandSet
        implements MetisDataList<MoneyWiseTaxBand>, MetisDataObjectFormat, Iterable<MoneyWiseTaxBand> {
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
        Collections.addAll(theTaxBands, pBands);
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
    public List<MoneyWiseTaxBand> getUnderlyingList() {
        return theTaxBands;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
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
     * Obtain a zero amount.
     * @return the zero amount
     */
    public TethysMoney getZeroAmount() {
        TethysMoney myAmount = theTaxBands.get(0).getAmount();
        myAmount = new TethysMoney(myAmount);
        myAmount.setZero();
        return myAmount;
    }

    /**
     * Are there multiple taxBands?
     * @return true/false
     */
    public boolean multipleBands() {
        return theTaxBands.size() > 1;
    }

    /**
     * MoneyWiseTaxBand class.
     */
    public static class MoneyWiseTaxBand
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTaxBand> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTaxBand.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_RATE, MoneyWiseTaxBand::getAmount);
            FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_AMOUNT, MoneyWiseTaxBand::getRate);
        }

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
            theAmount = pAmount == null
                    ? null
                    : new TethysMoney(pAmount);
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
            final TethysMoney myAmount = pSource.getAmount();
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
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return toString();
        }

        @Override
        public String toString() {
            final StringBuilder myBuilder = new StringBuilder();
            if (theAmount != null) {
                myBuilder.append(theAmount);
                myBuilder.append('@');
            }
            myBuilder.append(theRate);
            return myBuilder.toString();
        }

        @Override
        public MetisFieldSet<MoneyWiseTaxBand> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
