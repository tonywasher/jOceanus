/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataFieldValue;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountValues;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityValues;

import java.util.Currency;

/**
 * Account Category Bucket.
 *
 * @param <T> the account data type
 * @param <C> the account category data type
 */
public abstract class MoneyWiseAnalysisAccountCategoryBucket<T extends MoneyWiseAssetBase, C>
        implements MetisFieldTableItem {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseAnalysisAccountCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisAccountCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_BASEVALUES, MoneyWiseAnalysisAccountCategoryBucket::getBaseValues);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseAnalysisAccountAttr.class, MoneyWiseAnalysisAccountCategoryBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    protected static final MetisDataFieldId NAME_TOTALS = MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS;

    /**
     * Values.
     */
    private final MoneyWiseAnalysisAccountValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseAnalysisAccountValues theBaseValues;

    /**
     * Does this bucket have foreign currency values?
     */
    private boolean hasForeignCurrency;

    /**
     * Constructor.
     *
     * @param pCurrency the currency
     */
    protected MoneyWiseAnalysisAccountCategoryBucket(final MoneyWiseCurrency pCurrency) {
        /* Create the value maps */
        final Currency myCurrency = pCurrency == null
                ? MoneyWiseAnalysisAccountBucket.DEFAULT_CURRENCY
                : pCurrency.getCurrency();
        theValues = new MoneyWiseAnalysisAccountValues(myCurrency);
        theBaseValues = new MoneyWiseAnalysisAccountValues(myCurrency);
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public abstract String getName();

    /**
     * Obtain the account category.
     *
     * @return the account category
     */
    public abstract C getAccountCategory();

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public MoneyWiseAnalysisAccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     *
     * @return the base values
     */
    public MoneyWiseAnalysisAccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Set Value.
     *
     * @param pAttr  the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final MoneyWiseAnalysisAccountAttr pAttr,
                            final OceanusMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     *
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseAnalysisAccountAttr pAttr) {
        /* Access value of object */
        final Object myValue = getValue(pAttr);

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain an attribute value.
     *
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final MoneyWiseAnalysisAccountAttr pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * Does this category hold foreign currency accounts?
     *
     * @return true/false
     */
    public Boolean hasForeignCurrency() {
        return hasForeignCurrency;
    }

    /**
     * Calculate delta.
     */
    protected void calculateDelta() {
        /* Obtain a copy of the value */
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        myValue = new OceanusMoney(myValue);

        /* Subtract any base value */
        final OceanusMoney myBase = theBaseValues.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA, myValue);
    }

    /**
     * Add account category bucket to totals.
     *
     * @param pBucket the underlying bucket
     */
    protected void addValues(final MoneyWiseAnalysisAccountCategoryBucket<T, C> pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add account bucket to totals.
     *
     * @param pBucket the underlying bucket
     */
    protected void addValues(final MoneyWiseAnalysisAccountBucket<T> pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());

        /* Adjust foreign currency indication */
        hasForeignCurrency |= pBucket.isForeignCurrency();
    }

    /**
     * Add portfolio bucket to totals.
     *
     * @param pBucket the underlying bucket
     */
    protected void addValues(final MoneyWiseAnalysisPortfolioBucket pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add bucket to totals.
     *
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseAnalysisAccountValues pTotals,
                                  final MoneyWiseAnalysisAccountValues pSource) {
        /* Add base values */
        final OceanusMoney myValue = pTotals.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        final OceanusMoney mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Add bucket to totals.
     *
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseAnalysisAccountValues pTotals,
                                  final MoneyWiseAnalysisSecurityValues pSource) {
        /* Add base values */
        final OceanusMoney myValue = pTotals.getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUATION);
        final OceanusMoney mySrcValue = pSource.getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUATION);
        myValue.addAmount(mySrcValue);
    }
}
