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

import java.util.Currency;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxSource.MoneyWiseTaxSourceItem;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Income Tax Scheme.
 */
public class MoneyWiseTaxSource
        implements MetisDataMap, Iterable<MoneyWiseTaxSourceItem> {
    /**
     * List of items.
     */
    private final Map<TaxBasisClass, MoneyWiseTaxSourceItem> theItemMap;

    /**
     * Zero amount.
     */
    private final TethysMoney theZeroAmount;

    /**
     * Constructor.
     * @param pCurrency the currency
     */
    public MoneyWiseTaxSource(final Currency pCurrency) {
        theItemMap = new EnumMap<>(TaxBasisClass.class);
        theZeroAmount = new TethysMoney(pCurrency);
    }

    /**
     * Add item.
     * @param pItem the item
     */
    public void addItem(final MoneyWiseTaxSourceItem pItem) {
        theItemMap.put(pItem.getTaxBasis(), pItem);
    }

    @Override
    public Iterator<MoneyWiseTaxSourceItem> iterator() {
        return theItemMap.values().iterator();
    }

    @Override
    public Map<?, ?> getUnderlyingMap() {
        return theItemMap;
    }

    /**
     * Obtain the amount for the taxBasis.
     * @param pBasis the taxBasis
     * @return the amount
     */
    public TethysMoney getAmountForTaxBasis(final TaxBasisClass pBasis) {
        MoneyWiseTaxSourceItem myItem = theItemMap.get(pBasis);
        return myItem != null
                              ? myItem.getAmount()
                              : theZeroAmount;
    }

    /**
     * TaxSourceItem.
     */
    public static class MoneyWiseTaxSourceItem
            implements MetisDataContents {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxSourceItem.class.getSimpleName());

        /**
         * TaxBasis Field Id.
         */
        private static final MetisField FIELD_BASIS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TAXBASIS.getItemName());

        /**
         * Amount Field Id.
         */
        private static final MetisField FIELD_AMOUNT = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_AMOUNT.getValue());

        /**
         * TaxBasis.
         */
        private final TaxBasisClass theBasis;

        /**
         * Amount.
         */
        private final TethysMoney theAmount;

        /**
         * Constructor.
         * @param pBasis the taxBasis.
         * @param pAmount the amount
         */
        public MoneyWiseTaxSourceItem(final TaxBasisClass pBasis,
                                      final TethysMoney pAmount) {
            theBasis = pBasis;
            theAmount = pAmount;
        }

        /**
         * Obtain the taxBasis.
         * @return the basis
         */
        public TaxBasisClass getTaxBasis() {
            return theBasis;
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public TethysMoney getAmount() {
            return theAmount;
        }

        @Override
        public String formatObject() {
            StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theBasis);
            myBuilder.append('=');
            myBuilder.append(theAmount);
            return myBuilder.toString();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_BASIS.equals(pField)) {
                return theBasis;
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
