/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Account Category Bucket.
 * @param <T> the account data type
 * @param <C> the account category data type
 */
public abstract class AccountCategoryBucket<T extends AssetBase<T>, C>
        implements MetisFieldTableItem {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<AccountCategoryBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(AccountCategoryBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES, AccountCategoryBucket::getBaseValues);
        FIELD_DEFS.declareLocalFieldsForEnum(AccountAttribute.class, AccountCategoryBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    protected static final MetisDataFieldId NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS;

    /**
     * Values.
     */
    private final AccountValues theValues;

    /**
     * The base values.
     */
    private final AccountValues theBaseValues;

    /**
     * Does this bucket have foreign currency values?
     */
    private boolean hasForeignCurrency;

    /**
     * Constructor.
     * @param pCurrency the currency
     */
    protected AccountCategoryBucket(final AssetCurrency pCurrency) {
        /* Create the value maps */
        final Currency myCurrency = pCurrency == null
                                                      ? AccountBucket.DEFAULT_CURRENCY
                                                      : pCurrency.getCurrency();
        theValues = new AccountValues(myCurrency);
        theBaseValues = new AccountValues(myCurrency);
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
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
    public AccountValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base values.
     * @return the base values
     */
    public AccountValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final AccountAttribute pAttr,
                            final TethysMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final AccountAttribute pAttr) {
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
    private Object getValue(final AccountAttribute pAttr) {
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
        TethysMoney myValue = theValues.getMoneyValue(AccountAttribute.VALUATION);
        myValue = new TethysMoney(myValue);

        /* Subtract any base value */
        final TethysMoney myBase = theBaseValues.getMoneyValue(AccountAttribute.VALUATION);
        myValue.subtractAmount(myBase);

        /* Set the delta */
        setValue(AccountAttribute.VALUEDELTA, myValue);
    }

    /**
     * Add account category bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final AccountCategoryBucket<T, C> pBucket) {
        /* Add values */
        addValues(theValues, pBucket.getValues());

        /* Add base values */
        addValues(theBaseValues, pBucket.getBaseValues());
    }

    /**
     * Add account bucket to totals.
     * @param pBucket the underlying bucket
     */
    protected void addValues(final AccountBucket<T> pBucket) {
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
    protected void addValues(final PortfolioBucket pBucket) {
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
    private static void addValues(final AccountValues pTotals,
                                  final AccountValues pSource) {
        /* Add base values */
        final TethysMoney myValue = pTotals.getMoneyValue(AccountAttribute.VALUATION);
        final TethysMoney mySrcValue = pSource.getMoneyValue(AccountAttribute.VALUATION);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Add bucket to totals.
     * @param pTotals the totals
     * @param pSource the values to add
     */
    private static void addValues(final AccountValues pTotals,
                                  final SecurityValues pSource) {
        /* Add base values */
        final TethysMoney myValue = pTotals.getMoneyValue(AccountAttribute.VALUATION);
        final TethysMoney mySrcValue = pSource.getMoneyValue(SecurityAttribute.VALUATION);
        myValue.addAmount(mySrcValue);
    }
}
