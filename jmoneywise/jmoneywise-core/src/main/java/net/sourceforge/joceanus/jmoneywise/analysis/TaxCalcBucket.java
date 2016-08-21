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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Currency;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategorySection;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * The Tax Bucket class.
 */
public final class TaxCalcBucket
        implements MetisDataContents, Comparable<TaxCalcBucket>, MetisOrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.TAXCALC_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Tax Category Field Id.
     */
    private static final MetisField FIELD_TAXCAT = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TAXTYPE.getItemName());

    /**
     * Tax Section Field Id.
     */
    private static final MetisField FIELD_TAXSECT = FIELD_DEFS.declareEqualityField(AnalysisResource.TAXCALC_SECTION.getValue());

    /**
     * Parent Field Id.
     */
    private static final MetisField FIELD_PARENT = FIELD_DEFS.declareLocalField(AnalysisResource.TAXCALC_PARENT.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, TaxAttribute> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, TaxAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The reporting currency.
     */
    private final Currency theCurrency;

    /**
     * Tax Category.
     */
    private final TaxCategory theTaxCategory;

    /**
     * Tax Section.
     */
    private final TaxCategorySection theTaxSection;

    /**
     * Attribute Map.
     */
    private final Map<TaxAttribute, TethysDecimal> theAttributes;

    /**
     * The parent.
     */
    private TaxCalcBucket theParent;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTaxCategory the category
     */
    private TaxCalcBucket(final Analysis pAnalysis,
                          final TaxCategory pTaxCategory) {
        /* Store the parameters */
        theTaxCategory = pTaxCategory;
        theAnalysis = pAnalysis;

        /* Determine currency */
        AssetCurrency myCurrency = pAnalysis.getCurrency();
        theCurrency = myCurrency == null
                                         ? AccountBucket.DEFAULT_CURRENCY
                                         : myCurrency.getCurrency();

        /* Determine the tax section */
        theTaxSection = theTaxCategory.getTaxClass().getClassSection();

        /* Create the attribute map */
        theAttributes = new EnumMap<>(TaxAttribute.class);

        /* Create all possible values */
        setAttribute(TaxAttribute.AMOUNT, new TethysMoney(theCurrency));
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_TAXCAT.equals(pField)) {
            return theTaxCategory;
        }
        if (FIELD_TAXSECT.equals(pField)) {
            return theTaxSection;
        }
        if (FIELD_PARENT.equals(pField)) {
            return (theParent != null)
                                       ? theParent
                                       : MetisFieldValue.SKIP;
        }

        /* Handle Attribute fields */
        TaxAttribute myClass = getClassForField(pField);
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
    public Integer getOrderedId() {
        return theTaxCategory.getId();
    }

    /**
     * Obtain name.
     * @return the name
     */
    public String getName() {
        return theTaxCategory.getName();
    }

    /**
     * Obtain tax category.
     * @return the category
     */
    public TaxCategory getTaxCategory() {
        return theTaxCategory;
    }

    /**
     * Obtain the tax section.
     * @return the tax section
     */
    public TaxCategorySection getCategorySection() {
        return theTaxSection;
    }

    /**
     * Obtain the parent.
     * @return the parent
     */
    public TaxCalcBucket getParent() {
        return theParent;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setAttribute(final TaxAttribute pAttr,
                                final TethysDecimal pValue) {
        /* Set the value into the list */
        theAttributes.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final TaxAttribute pAttr) {
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
    private static TaxAttribute getClassForField(final MetisField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X extends TethysDecimal> X getValue(final TaxAttribute pAttr,
                                                 final Class<X> pClass) {
        /* Obtain the attribute */
        return pClass.cast(getValue(pAttr));
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final TaxAttribute pAttr) {
        /* Obtain the attribute */
        return theAttributes.get(pAttr);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public TethysMoney getMoneyValue(final TaxAttribute pAttr) {
        /* Obtain the attribute */
        return getValue(pAttr, TethysMoney.class);
    }

    /**
     * Obtain a rate attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public TethysRate getRateValue(final TaxAttribute pAttr) {
        /* Obtain the attribute */
        return getValue(pAttr, TethysRate.class);
    }

    @Override
    public int compareTo(final TaxCalcBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the TaxCategories */
        return getTaxCategory().compareTo(pThat.getTaxCategory());
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
        if (!(pThat instanceof TaxCalcBucket)) {
            return false;
        }

        /* Compare the Tax Categories */
        TaxCalcBucket myThat = (TaxCalcBucket) pThat;
        return getTaxCategory().equals(myThat.getTaxCategory());
    }

    @Override
    public int hashCode() {
        return getTaxCategory().hashCode();
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return false;
    }

    /**
     * Is the bucket relevant?
     * @return true/false
     */
    protected boolean isRelevant() {
        /* Check for non-zero amount */
        TethysMoney myAmount = getMoneyValue(TaxAttribute.AMOUNT);
        TethysMoney myTax = getMoneyValue(TaxAttribute.TAXATION);
        return myAmount.isNonZero() || ((myTax != null) && (myTax.isNonZero()));
    }

    /**
     * Set a taxation amount and calculate the tax on it.
     * @param pAmount Amount to set
     * @return the taxation on this bucket
     */
    protected TethysMoney setAmount(final TethysMoney pAmount) {
        /* Access the rate */
        TethysRate myRate = getRateValue(TaxAttribute.RATE);

        /* Set the value */
        TethysMoney myAmount = new TethysMoney(pAmount);
        setAttribute(TaxAttribute.AMOUNT, myAmount);

        /* Calculate the tax if we have a rate */
        TethysMoney myTaxation = (myRate != null)
                                                  ? myAmount.valueAtRate(myRate)
                                                  : new TethysMoney(theCurrency);

        /* Return the taxation amount */
        setAttribute(TaxAttribute.TAXATION, myTaxation);
        return myTaxation;
    }

    /**
     * Set explicit taxation value.
     * @param pAmount Amount to set
     */
    protected void setTaxation(final TethysMoney pAmount) {
        /* Set the value */
        setAttribute(TaxAttribute.TAXATION, new TethysMoney(pAmount));
    }

    /**
     * Set parent bucket for reporting purposes.
     * @param pParent the parent bucket
     */
    protected void setParent(final TaxCalcBucket pParent) {
        /* Set the value */
        theParent = pParent;
    }

    /**
     * Set a tax rate.
     * @param pRate Amount to set
     */
    protected void setRate(final TethysRate pRate) {
        /* Set the value */
        setAttribute(TaxAttribute.RATE, new TethysRate(pRate));
    }

    /**
     * Add values.
     * @param pBucket tax category bucket
     */
    protected void addValues(final TaxCalcBucket pBucket) {
        /* Adjust the value */
        TethysMoney myAmount = getMoneyValue(TaxAttribute.AMOUNT);
        myAmount.addAmount(pBucket.getMoneyValue(TaxAttribute.AMOUNT));
    }

    /**
     * subtract values.
     * @param pBucket tax category bucket
     */
    protected void subtractValues(final TaxCalcBucket pBucket) {
        /* Adjust the value */
        TethysMoney myAmount = getMoneyValue(TaxAttribute.AMOUNT);
        myAmount.subtractAmount(pBucket.getMoneyValue(TaxAttribute.AMOUNT));
    }

    /**
     * TaxCategoryBucketList class.
     */
    public static class TaxCalcBucketList
            extends MetisOrderedIdList<Integer, TaxCalcBucket>
            implements MetisDataContents {

        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.TAXCALC_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * TaxYear Field Id.
         */
        private static final MetisField FIELD_TAXYEAR = FIELD_DEFS.declareLocalField(AnalysisResource.TAXCALC_YEAR.getValue());

        /**
         * Age Field Id.
         */
        private static final MetisField FIELD_AGE = FIELD_DEFS.declareLocalField(AnalysisResource.TAXCALC_AGE.getValue());

        /**
         * GainsSlices Field Id.
         */
        private static final MetisField FIELD_GAINS = FIELD_DEFS.declareLocalField(AnalysisResource.TAXCALC_SLICES.getValue());

        /**
         * ReducedAllowance Field Id.
         */
        private static final MetisField FIELD_ALLOW = FIELD_DEFS.declareLocalField(AnalysisResource.TAXCALC_ALLOW.getValue());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The taxYear.
         */
        private final TaxYear theYear;

        /**
         * The data.
         */
        private final MoneyWiseData theData;

        /**
         * User age.
         */
        private Integer theAge;

        /**
         * Are there Gains slices.
         */
        private Boolean hasGainsSlices = Boolean.FALSE;

        /**
         * Is there a reduced allowance?
         */
        private Boolean hasReducedAllow = Boolean.FALSE;

        /**
         * has tax been calculated?
         */
        private boolean taxCalculated;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         * @param pYear the TaxYear
         */
        protected TaxCalcBucketList(final Analysis pAnalysis,
                                    final TaxYear pYear) {
            super(TaxCalcBucket.class);
            theAnalysis = pAnalysis;
            theYear = pYear;
            theData = theAnalysis.getData();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TAXYEAR.equals(pField)) {
                return theYear;
            }
            if (FIELD_AGE.equals(pField)) {
                return theAge;
            }
            if (FIELD_GAINS.equals(pField)) {
                return hasGainsSlices
                                      ? hasGainsSlices
                                      : MetisFieldValue.SKIP;
            }
            if (FIELD_ALLOW.equals(pField)) {
                return hasReducedAllow
                                       ? hasReducedAllow
                                       : MetisFieldValue.SKIP;
            }
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Obtain the taxYear.
         * @return the year
         */
        public TaxYear getTaxYear() {
            return theYear;
        }

        /**
         * Obtain the user age.
         * @return the age
         */
        public Integer getAge() {
            return theAge;
        }

        /**
         * Have we a reduced allowance?
         * @return true/false
         */
        public Boolean hasReducedAllow() {
            return hasReducedAllow;
        }

        /**
         * Do we have gains slices?
         * @return true/false
         */
        public Boolean hasGainsSlices() {
            return hasGainsSlices;
        }

        /**
         * Set the age.
         * @param pAge the age
         */
        protected void setAge(final Integer pAge) {
            theAge = pAge;
        }

        /**
         * Set whether the allowance is reduced.
         * @param hasReduced true/false
         */
        protected void setHasReducedAllow(final Boolean hasReduced) {
            hasReducedAllow = hasReduced;
        }

        /**
         * Set whether we have gains slices.
         * @param hasSlices true/false
         */
        protected void setHasGainsSlices(final Boolean hasSlices) {
            hasGainsSlices = hasSlices;
        }

        /**
         * Obtain the TaxCalcBucket for a given tax category class.
         * @param pClass the event category class
         * @return the bucket
         */
        public TaxCalcBucket getBucket(final TaxCategoryClass pClass) {
            /* Locate the bucket in the list */
            TaxCategory myCategory = theData.getTaxCategories().findItemByClass(pClass);
            TaxCalcBucket myItem = findItemById(myCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TaxCalcBucket(theAnalysis, myCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            /* Loop through the buckets */
            Iterator<TaxCalcBucket> myIterator = listIterator();
            while (myIterator.hasNext()) {
                TaxCalcBucket myCurr = myIterator.next();

                /* Remove the bucket if it is irrelevant */
                if (!myCurr.isRelevant()) {
                    myIterator.remove();
                }
            }
        }

        /**
         * calculate tax.
         */
        public void calculateTax() {
            /* If tax has not yet been calculated */
            if (!taxCalculated) {
                /* Create a new TaxAnalysis */
                TaxAnalysis myTaxAnalysis = new TaxAnalysis(theAnalysis);

                /* Calculate the tax and record the fact */
                myTaxAnalysis.calculateTax();
                taxCalculated = true;
            }
        }
    }

    /**
     * TaxAttribute enumeration.
     */
    public enum TaxAttribute {
        /**
         * Amount.
         */
        AMOUNT,

        /**
         * Rate.
         */
        RATE,

        /**
         * Taxation.
         */
        TAXATION;
    }
}
