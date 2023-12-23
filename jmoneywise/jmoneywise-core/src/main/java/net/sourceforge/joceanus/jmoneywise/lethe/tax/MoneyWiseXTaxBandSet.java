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
package net.sourceforge.joceanus.jmoneywise.lethe.tax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet.MoneyWiseXTaxBand;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Set of taxBands.
 */
public class MoneyWiseXTaxBandSet
        implements MetisDataList<MoneyWiseXTaxBand>, MetisDataObjectFormat, Iterable<MoneyWiseXTaxBand> {
    /**
     * List of Tax Bands.
     */
    private final List<MoneyWiseXTaxBand> theTaxBands;

    /**
     * Constructor.
     */
    public MoneyWiseXTaxBandSet() {
        theTaxBands = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pBands the set of tax bands
     */
    public MoneyWiseXTaxBandSet(final MoneyWiseXTaxBand... pBands) {
        this();
        Collections.addAll(theTaxBands, pBands);
    }

    /**
     * Constructor.
     * @param pSource the source to clone
     */
    public MoneyWiseXTaxBandSet(final MoneyWiseXTaxBandSet pSource) {
        this();
        for (MoneyWiseXTaxBand myBand : pSource.theTaxBands) {
            theTaxBands.add(new MoneyWiseXTaxBand(myBand));
        }
    }

    @Override
    public Iterator<MoneyWiseXTaxBand> iterator() {
        return theTaxBands.iterator();
    }

    @Override
    public List<MoneyWiseXTaxBand> getUnderlyingList() {
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
    public void addTaxBand(final MoneyWiseXTaxBand pBand) {
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
    public static class MoneyWiseXTaxBand
            implements MetisFieldItem {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseXTaxBand> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXTaxBand.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_RATE, MoneyWiseXTaxBand::getAmount);
            FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_AMOUNT, MoneyWiseXTaxBand::getRate);
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
        public MoneyWiseXTaxBand(final TethysMoney pAmount,
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
        public MoneyWiseXTaxBand(final TethysRate pRate) {
            this(null, pRate);
        }

        /**
         * Constructor.
         * @param pSource the source band
         */
        public MoneyWiseXTaxBand(final MoneyWiseXTaxBand pSource) {
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
        public MetisFieldSet<MoneyWiseXTaxBand> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
