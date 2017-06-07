/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Currency;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Account Category Bucket.
 * @param <T> the account data type
 * @param <C> the account category data type
 */
public abstract class AccountCategoryBucket<T extends AssetBase<T>, C>
        implements MetisDataContents, Comparable<AccountCategoryBucket<T, C>>, MetisOrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(AccountCategoryBucket.class.getSimpleName());

    /**
     * Base Field Id.
     */
    private static final MetisField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, AccountAttribute> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, AccountAttribute.class);

    /**
     * Totals bucket name.
     */
    protected static final String NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS.getValue();

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
        Currency myCurrency = pCurrency == null
                                                ? AccountBucket.DEFAULT_CURRENCY
                                                : pCurrency.getCurrency();
        theValues = new AccountValues(myCurrency);
        theBaseValues = new AccountValues(myCurrency);
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        AccountAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof TethysDecimal) {
                return ((TethysDecimal) myValue).isNonZero()
                                                             ? myValue
                                                             : MetisFieldValue.SKIP;
            }
            return myValue;
        }

        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
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
        Object myValue = getValue(pAttr);

        /* Return the value */
        return (myValue != null)
                                 ? myValue
                                 : MetisFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static AccountAttribute getClassForField(final MetisField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
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

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }
        if (!(pThat instanceof AccountCategoryBucket)) {
            return false;
        }

        /* Compare the Account Categories */
        AccountCategoryBucket<?, ?> myThat = (AccountCategoryBucket<?, ?>) pThat;
        return getAccountCategory().equals(myThat.getAccountCategory());
    }

    @Override
    public int hashCode() {
        return getAccountCategory().hashCode();
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
        TethysMoney myBase = theBaseValues.getMoneyValue(AccountAttribute.VALUATION);
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
        TethysMoney myValue = pTotals.getMoneyValue(AccountAttribute.VALUATION);
        TethysMoney mySrcValue = pSource.getMoneyValue(AccountAttribute.VALUATION);
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
        TethysMoney myValue = pTotals.getMoneyValue(AccountAttribute.VALUATION);
        TethysMoney mySrcValue = pSource.getMoneyValue(SecurityAttribute.VALUATION);
        myValue.addAmount(mySrcValue);
    }
}