/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountValues;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Currency;

/**
 * Account Category Bucket.
 * @param <T> the account data type
 * @param <C> the account category data type
 */
public abstract class MoneyWiseXAnalysisAccountCategoryBucket<T extends MoneyWiseAssetBase, C>
        implements MetisFieldTableItem {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseXAnalysisAccountCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisAccountCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_BASEVALUES, MoneyWiseXAnalysisAccountCategoryBucket::getBaseValues);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseXAnalysisAccountAttr.class, MoneyWiseXAnalysisAccountCategoryBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    protected static final MetisDataFieldId NAME_TOTALS = MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS;

    /**
     * Values.
     */
    private final MoneyWiseXAnalysisAccountValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseXAnalysisAccountValues theBaseValues;

    /**
     * Does this bucket have foreign currency values?
     */
    private boolean hasForeignCurrency;

    /**
     * Constructor.
     * @param pCurrency the currency
     */
    protected MoneyWiseXAnalysisAccountCategoryBucket(final MoneyWiseCurrency pCurrency) {
        /* Create the value maps */
        final Currency myCurrency = pCurrency == null
                ? MoneyWiseXAnalysisAccountBucket.DEFAULT_CURRENCY
                : pCurrency.getCurrency();
        theValues = new MoneyWiseXAnalysisAccountValues(myCurrency);
        theBaseValues = new MoneyWiseXAnalysisAccountValues(myCurrency);
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
     * @return the name
     */
    public abstract String getName();

    /**
     * Obtain the account category.
     * @return the account category
     */
    public abstract C getAccountCategory();

    /**
     * Obtain the values.
     * @return the values
     */
    public MoneyWiseXAnalysisAccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    public MoneyWiseXAnalysisAccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final MoneyWiseXAnalysisAccountAttr pAttr,
                            final OceanusMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseXAnalysisAccountAttr pAttr) {
        /* Access value of object */
        final Object myValue = getValue(pAttr);

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final MoneyWiseXAnalysisAccountAttr pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * Does this category hold foreign currency accounts?
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
        OceanusMoney myValue = theValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myValue = new OceanusMoney(myValue);

        /* Subtract any base value */
        final OceanusMoney myBase = theBaseValues.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA, myValue);
    }

    /**
     * Add account category bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final MoneyWiseXAnalysisAccountCategoryBucket<T, C> pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add account bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final MoneyWiseXAnalysisAccountBucket<T> pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());

        /* Adjust foreign currency indication */
        hasForeignCurrency |= pBucket.isForeignCurrency();
    }

    /**
     * Add portfolio bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final MoneyWiseXAnalysisPortfolioBucket pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseXAnalysisAccountValues pTotals,
                                  final MoneyWiseXAnalysisAccountValues pSource) {
        /* Add base values */
        final OceanusMoney myValue = pTotals.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        final OceanusMoney mySrcValue = pSource.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final MoneyWiseXAnalysisAccountValues pTotals,
                                  final MoneyWiseXAnalysisSecurityValues pSource) {
        /* Add base values */
        final OceanusMoney myValue = pTotals.getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION);
        final OceanusMoney mySrcValue = pSource.getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUATION);
        myValue.addAmount(mySrcValue);
    }
}
