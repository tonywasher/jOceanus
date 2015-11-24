/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.list.OrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.dateday.TethysDate;
import net.sourceforge.joceanus.jtethys.dateday.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Transaction Category Bucket.
 */
public final class TransactionCategoryBucket
        implements JDataContents, Comparable<TransactionCategoryBucket>, OrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.TRANSCATEGORY_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Transaction Category Field Id.
     */
    private static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSCATEGORY.getItemName());

    /**
     * Transaction Type Field Id.
     */
    private static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSTYPE.getItemName());

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * History Field Id.
     */
    private static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, TransactionAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, TransactionAttribute.class);

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS.getValue();

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The transaction category.
     */
    private final TransactionCategory theCategory;

    /**
     * The transaction category type.
     */
    private final TransactionCategoryType theType;

    /**
     * Values.
     */
    private final CategoryValues theValues;

    /**
     * The base values.
     */
    private final CategoryValues theBaseValues;

    /**
     * History Map.
     */
    private final BucketHistory<CategoryValues, TransactionAttribute> theHistory;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCategory the transaction category
     */
    private TransactionCategoryBucket(final Analysis pAnalysis,
                                      final TransactionCategory pCategory) {
        /* Store the parameters */
        theAnalysis = pAnalysis;
        theCategory = pCategory;
        theType = (pCategory == null)
                                     ? null
                                     : pCategory.getCategoryType();

        /* Create the history map */
        AssetCurrency myDefault = theAnalysis.getCurrency();
        Currency myCurrency = myDefault == null
                                               ? AccountBucket.DEFAULT_CURRENCY
                                               : myDefault.getCurrency();
        CategoryValues myValues = new CategoryValues(myCurrency);
        theHistory = new BucketHistory<CategoryValues, TransactionAttribute>(myValues);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private TransactionCategoryBucket(final Analysis pAnalysis,
                                      final TransactionCategoryBucket pBase,
                                      final TethysDate pDate) {
        /* Copy details from base */
        theCategory = pBase.getTransactionCategory();
        theType = pBase.getTransactionCategoryType();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new BucketHistory<CategoryValues, TransactionAttribute>(pBase.getHistoryMap(), pDate);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private TransactionCategoryBucket(final Analysis pAnalysis,
                                      final TransactionCategoryBucket pBase,
                                      final TethysDateRange pRange) {
        /* Copy details from base */
        theCategory = pBase.getTransactionCategory();
        theType = pBase.getTransactionCategoryType();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new BucketHistory<CategoryValues, TransactionAttribute>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_CATEGORY.equals(pField)) {
            return theCategory;
        }
        if (FIELD_TYPE.equals(pField)) {
            return theType;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBaseValues != null)
                                          ? theBaseValues
                                          : JDataFieldValue.SKIP;
        }

        /* Handle Attribute fields */
        TransactionAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof TethysDecimal) {
                return ((TethysDecimal) myValue).isNonZero()
                                                       ? myValue
                                                       : JDataFieldValue.SKIP;
            }
            return myValue;
        }

        return JDataFieldValue.SKIP;
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
    public String getName() {
        return (theCategory == null)
                                    ? NAME_TOTALS
                                    : theCategory.getName();
    }

    @Override
    public Integer getOrderedId() {
        return theCategory.getId();
    }

    /**
     * Obtain the transaction category.
     * @return the transaction category
     */
    public TransactionCategory getTransactionCategory() {
        return theCategory;
    }

    /**
     * Obtain the transaction category type.
     * @return the transaction category type
     */
    public TransactionCategoryType getTransactionCategoryType() {
        return theType;
    }

    /**
     * Is this bucket idle?
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the value map.
     * @return the value map
     */
    public CategoryValues getValues() {
        return theValues;
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public CategoryValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public CategoryValues getValuesForTransaction(final Transaction pTrans) {
        /* Obtain values for transaction */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForTransaction(final Transaction pTrans,
                                           final TransactionAttribute pAttr) {
        /* Obtain delta for transaction */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<CategoryValues, TransactionAttribute> getHistoryMap() {
        return theHistory;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     * @return the range
     */
    public TethysDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final TransactionAttribute pAttr,
                            final TethysMoney pValue) {
        /* Set the value */
        theValues.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final TransactionAttribute pAttr) {
        /* Access value of object */
        Object myValue = getValue(pAttr);

        /* Return the value */
        return (myValue != null)
                                ? myValue
                                : JDataFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static TransactionAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final TransactionAttribute pAttr) {
        /* Obtain the value */
        return theValues.get(pAttr);
    }

    @Override
    public int compareTo(final TransactionCategoryBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the EventCategories */
        return getTransactionCategory().compareTo(pThat.getTransactionCategory());
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
        if (!(pThat instanceof TransactionCategoryBucket)) {
            return false;
        }

        /* Compare the transaction Categories */
        TransactionCategoryBucket myThat = (TransactionCategoryBucket) pThat;
        if (!getTransactionCategory().equals(myThat.getTransactionCategory())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
    }

    @Override
    public int hashCode() {
        return getTransactionCategory().hashCode();
    }

    /**
     * Adjust counter.
     * @param pAttr the attribute
     * @param pDelta the delta
     */
    protected void adjustCounter(final TransactionAttribute pAttr,
                                 final TethysMoney pDelta) {
        TethysMoney myValue = theValues.getMoneyValue(pAttr);
        myValue = new TethysMoney(myValue);
        myValue.addAmount(pDelta);
        setValue(pAttr, myValue);
    }

    /**
     * Add income value.
     * @param pTrans the transaction helper
     * @param pValue the value to add
     */
    protected void addIncome(final TransactionHelper pTrans,
                             final TethysMoney pValue) {
        /* Add the expense */
        addIncome(pValue);

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    protected void addIncome(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(TransactionAttribute.INCOME, pValue);
        }
    }

    /**
     * Subtract income value.
     * @param pValue the value to subtract
     */
    protected void subtractIncome(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            TethysMoney myIncome = new TethysMoney(pValue);
            myIncome.negate();
            adjustCounter(TransactionAttribute.INCOME, myIncome);
        }
    }

    /**
     * Add expense value.
     * @param pTrans the transaction helper
     * @param pValue the value to add
     */
    protected void addExpense(final TransactionHelper pTrans,
                              final TethysMoney pValue) {
        /* Add the expense */
        addExpense(pValue);

        /* Register the transaction in the history */
        registerTransaction(pTrans);
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    protected void addExpense(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            adjustCounter(TransactionAttribute.EXPENSE, pValue);
        }
    }

    /**
     * Subtract expense value.
     * @param pTrans the transaction causing the expense
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final TransactionHelper pTrans,
                                   final TethysMoney pValue) {
        /* Subtract the expense */
        subtractExpense(pValue);

        /* Register the event in the history */
        registerTransaction(pTrans);
    }

    /**
     * Subtract expense value.
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final TethysMoney pValue) {
        /* Only adjust on non-zero */
        if (pValue.isNonZero()) {
            TethysMoney myExpense = new TethysMoney(pValue);
            myExpense.negate();
            adjustCounter(TransactionAttribute.EXPENSE, myExpense);
        }
    }

    /**
     * Add transaction to totals.
     * @param pTrans the transaction helper
     * @return isIncome true/false
     */
    private boolean adjustValues(final TransactionHelper pTrans) {
        /* Analyse the event */
        TransactionCategoryClass myClass = pTrans.getCategoryClass();
        AssetDirection myDir = pTrans.getDirection();
        boolean isExpense = myClass.isExpense();
        TethysMoney myAmount = new TethysMoney(pTrans.getLocalDebitAmount());

        /* If this is an expense */
        if (isExpense) {
            /* Adjust for TaxCredit */
            TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                myAmount.addAmount(myTaxCredit);
            }

            /* if we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* If this is a recovered expense */
                if (myDir.isFrom()) {
                    /* Add as income */
                    adjustCounter(TransactionAttribute.INCOME, myAmount);
                    isExpense = false;

                    /* else its standard expense */
                } else {
                    /* Add as expense */
                    adjustCounter(TransactionAttribute.EXPENSE, myAmount);
                }
            }

            /* else this is an income */
        } else {
            /* Adjust for TaxCredit */
            TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                myAmount.addAmount(myTaxCredit);
            }

            /* Adjust for NatInsurance */
            TethysMoney myNatIns = pTrans.getNatInsurance();
            if (myNatIns != null) {
                myAmount.addAmount(myNatIns);
            }

            /* Adjust for DeemedBenefit */
            TethysMoney myBenefit = pTrans.getDeemedBenefit();
            if (myBenefit != null) {
                myAmount.addAmount(myBenefit);
            }

            /* Adjust for CharityDonation */
            TethysMoney myDonation = pTrans.getCharityDonation();
            if (myDonation != null) {
                myAmount.addAmount(myDonation);
            }

            /* If we need to switch direction */
            if (myClass.isSwitchDirection()) {
                /* switch the direction */
                myDir = myDir.reverse();
            }

            /* if we have a non-zero amount */
            if (myAmount.isNonZero()) {
                /* If this is a returned income */
                if (myDir.isTo()) {
                    /* Add as expense */
                    adjustCounter(TransactionAttribute.EXPENSE, myAmount);

                    /* else standard income */
                } else {
                    /* Add as income */
                    adjustCounter(TransactionAttribute.INCOME, myAmount);
                    isExpense = false;
                }
            }
        }

        /* Register the transaction in the history */
        registerTransaction(pTrans);

        /* Return the income flag */
        return !isExpense;
    }

    /**
     * Calculate Income delta.
     */
    protected void calculateDelta() {
        /* Calculate delta for the values */
        theValues.calculateDelta();
    }

    /**
     * Adjust to base.
     */
    private void adjustToBase() {
        /* Adjust to base values */
        theValues.adjustToBaseValues(theBaseValues);
        theBaseValues.resetBaseValues();
    }

    /**
     * Add bucket to totals.
     * @param pSource the bucket to add
     */
    private void addValues(final TransactionCategoryBucket pSource) {
        /* Access source values */
        CategoryValues mySource = pSource.getValues();

        /* Add income values */
        TethysMoney myValue = theValues.getMoneyValue(TransactionAttribute.INCOME);
        TethysMoney mySrcValue = mySource.getMoneyValue(TransactionAttribute.INCOME);
        myValue.addAmount(mySrcValue);

        /* Add expense values */
        myValue = theValues.getMoneyValue(TransactionAttribute.EXPENSE);
        mySrcValue = mySource.getMoneyValue(TransactionAttribute.EXPENSE);
        myValue.addAmount(mySrcValue);
    }

    /**
     * Register the transaction.
     * @param pTrans the transaction helper
     */
    protected void registerTransaction(final TransactionHelper pTrans) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans.getTransaction(), theValues);
    }

    /**
     * CategoryValues class.
     */
    public static final class CategoryValues
            extends BucketValues<CategoryValues, TransactionAttribute> {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 8946533427978809288L;

        /**
         * Constructor.
         * @param pCurrency the reporting currency
         */
        private CategoryValues(final Currency pCurrency) {
            /* Initialise class */
            super(TransactionAttribute.class);

            /* Create all possible values */
            put(TransactionAttribute.INCOME, new TethysMoney(pCurrency));
            put(TransactionAttribute.EXPENSE, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         */
        private CategoryValues(final CategoryValues pSource) {
            /* Initialise class */
            super(pSource);
        }

        @Override
        protected CategoryValues getSnapShot() {
            return new CategoryValues(this);
        }

        @Override
        protected void adjustToBaseValues(final CategoryValues pBase) {
            /* Adjust income/expense values */
            adjustMoneyToBase(pBase, TransactionAttribute.INCOME);
            adjustMoneyToBase(pBase, TransactionAttribute.EXPENSE);
            calculateDelta();
        }

        /**
         * Calculate delta.
         */
        private void calculateDelta() {
            /* Obtain a copy of the value */
            TethysMoney myDelta = getMoneyValue(TransactionAttribute.INCOME);
            myDelta = new TethysMoney(myDelta);

            /* Subtract the expense value */
            TethysMoney myExpense = getMoneyValue(TransactionAttribute.EXPENSE);
            myDelta.subtractAmount(myExpense);

            /* Set the delta */
            put(TransactionAttribute.PROFIT, myDelta);
        }

        @Override
        protected void resetBaseValues() {
            /* Create a zero value in the correct currency */
            TethysMoney myValue = getMoneyValue(TransactionAttribute.INCOME);
            myValue = new TethysMoney(myValue);
            myValue.setZero();

            /* Reset Income and expense values */
            put(TransactionAttribute.INCOME, myValue);
            put(TransactionAttribute.EXPENSE, new TethysMoney(myValue));
            put(TransactionAttribute.PROFIT, new TethysMoney(myValue));
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            TethysMoney myIncome = getMoneyValue(TransactionAttribute.INCOME);
            TethysMoney myExpense = getMoneyValue(TransactionAttribute.EXPENSE);
            return (myIncome.isNonZero()) || (myExpense.isNonZero());
        }
    }

    /**
     * TransactionCategoryBucket list class.
     */
    public static final class TransactionCategoryBucketList
            extends OrderedIdList<Integer, TransactionCategoryBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.TRANSCATEGORY_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NAME_TOTALS);

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The data.
         */
        private final MoneyWiseData theData;

        /**
         * The totals.
         */
        private final TransactionCategoryBucket theTotals;

        /**
         * The TaxBasis.
         */
        private final TaxBasisBucketList theTaxBasis;

        /**
         * The TaxCredit.
         */
        private final TransactionCategoryBucket theTaxCredit;

        /**
         * The TaxRelief.
         */
        private final TransactionCategoryBucket theTaxRelief;

        /**
         * The NatInsurance.
         */
        private final TransactionCategoryBucket theNatInsurance;

        /**
         * The DeemedBenefit.
         */
        private final TransactionCategoryBucket theDeemedBenefit;

        /**
         * The CharityDonation.
         */
        private final TransactionCategoryBucket theCharityDonation;

        /**
         * The CapitalGains.
         */
        private final TransactionCategoryBucket theCapitalGains;

        /**
         * The TaxFreeGains.
         */
        private final TransactionCategoryBucket theTaxFreeGains;

        /**
         * The TaxableGains.
         */
        private final TransactionCategoryBucket theTaxableGains;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected TransactionCategoryBucketList(final Analysis pAnalysis) {
            /* Initialise class */
            super(TransactionCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Access taxBasis list */
            theTaxBasis = theAnalysis.getTaxBasis();

            /* Obtain the implied buckets */
            TransactionCategoryList myList = theData.getTransCategories();
            theTaxCredit = getBucket(myList.getEventInfoCategory(TransactionInfoClass.TAXCREDIT));
            theNatInsurance = getBucket(myList.getEventInfoCategory(TransactionInfoClass.NATINSURANCE));
            theDeemedBenefit = getBucket(myList.getEventInfoCategory(TransactionInfoClass.DEEMEDBENEFIT));
            theCharityDonation = getBucket(myList.getEventInfoCategory(TransactionInfoClass.CHARITYDONATION));
            theTaxRelief = getBucket(myList.getSingularClass(TransactionCategoryClass.TAXRELIEF));
            theTaxableGains = getBucket(myList.getSingularClass(TransactionCategoryClass.TAXABLEGAIN));
            theTaxFreeGains = getBucket(myList.getSingularClass(TransactionCategoryClass.TAXFREEGAIN));
            theCapitalGains = getBucket(myList.getSingularClass(TransactionCategoryClass.CAPITALGAIN));
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected TransactionCategoryBucketList(final Analysis pAnalysis,
                                                final TransactionCategoryBucketList pBase,
                                                final TethysDate pDate) {
            /* Initialise class */
            super(TransactionCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Don't use implied buckets */
            theTaxBasis = null;
            theTaxCredit = null;
            theNatInsurance = null;
            theDeemedBenefit = null;
            theCharityDonation = null;
            theTaxRelief = null;
            theTaxableGains = null;
            theTaxFreeGains = null;
            theCapitalGains = null;

            /* Loop through the buckets */
            Iterator<TransactionCategoryBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                TransactionCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                TransactionCategoryBucket myBucket = new TransactionCategoryBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
                    add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected TransactionCategoryBucketList(final Analysis pAnalysis,
                                                final TransactionCategoryBucketList pBase,
                                                final TethysDateRange pRange) {
            /* Initialise class */
            super(TransactionCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Don't use implied buckets */
            theTaxBasis = null;
            theTaxCredit = null;
            theNatInsurance = null;
            theDeemedBenefit = null;
            theCharityDonation = null;
            theTaxRelief = null;
            theTaxableGains = null;
            theTaxFreeGains = null;
            theCapitalGains = null;

            /* Loop through the buckets */
            Iterator<TransactionCategoryBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                TransactionCategoryBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                TransactionCategoryBucket myBucket = new TransactionCategoryBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    add(myBucket);
                }
            }
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Obtain the Totals.
         * @return the totals bucket
         */
        public TransactionCategoryBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals TransactionCategoryBucket.
         * @return the bucket
         */
        private TransactionCategoryBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new TransactionCategoryBucket(theAnalysis, null);
        }

        /**
         * Obtain the TransactionCategoryBucket for a given transaction category.
         * @param pCategory the transaction category
         * @return the bucket
         */
        protected TransactionCategoryBucket getBucket(final TransactionCategory pCategory) {
            /* Handle null category */
            if (pCategory == null) {
                return null;
            }

            /* Locate the bucket in the list */
            TransactionCategoryBucket myItem = findItemById(pCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TransactionCategoryBucket(theAnalysis, pCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain an orphan CategoryBucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        public TransactionCategoryBucket getOrphanBucket(final TransactionCategory pCategory) {
            /* Allocate an orphan bucket */
            return new TransactionCategoryBucket(theAnalysis, pCategory);
        }

        /**
         * Obtain the TransactionCategoryBucket for a given transaction category class.
         * @param pClass the transaction category class
         * @return the bucket
         */
        protected TransactionCategoryBucket getBucket(final TransactionCategoryClass pClass) {
            /* Determine required category */
            TransactionCategory myCategory = theData.getTransCategories().getSingularClass(pClass);

            /* Return the bucket */
            return getBucket(myCategory);
        }

        /**
         * Adjust category buckets.
         * @param pTrans the transaction helper
         * @param pCategory primary category
         */
        protected void adjustCategories(final TransactionHelper pTrans,
                                        final TransactionCategory pCategory) {
            /* Adjust the primary category bucket */
            TransactionCategoryBucket myCatBucket = getBucket(pCategory);
            myCatBucket.adjustValues(pTrans);

            /* Adjust tax basis */
            theTaxBasis.adjustBasis(pTrans, pCategory);

            /* Adjust for Tax Credit */
            TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                if (pCategory.isCategoryClass(TransactionCategoryClass.LOANINTERESTCHARGED)) {
                    theTaxRelief.addIncome(pTrans, myTaxCredit);
                    myTaxCredit = new TethysMoney(myTaxCredit);
                    myTaxCredit.negate();
                    theTaxBasis.adjustValue(pTrans, TaxBasisClass.VIRTUAL, myTaxCredit);
                } else {
                    theTaxCredit.addExpense(pTrans, myTaxCredit);
                    theTaxBasis.adjustValue(pTrans, TaxBasisClass.TAXPAID, myTaxCredit);
                }
            }

            /* Adjust for NatInsurance */
            TethysMoney myNatIns = pTrans.getNatInsurance();
            if (myNatIns != null) {
                theNatInsurance.addExpense(pTrans, myNatIns);
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.VIRTUAL, myNatIns);
            }

            /* Adjust for DeemedBenefit */
            TethysMoney myBenefit = pTrans.getDeemedBenefit();
            if (myBenefit != null) {
                theDeemedBenefit.addExpense(pTrans, myBenefit);
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.VIRTUAL, myBenefit);
            }

            /* Adjust for Charity Donation */
            TethysMoney myDonation = pTrans.getCharityDonation();
            if (myDonation != null) {
                theCharityDonation.addExpense(pTrans, myDonation);
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.EXPENSE, myDonation);
            }
        }

        /**
         * Adjust for Standard Gains.
         * @param pTrans the transaction helper
         * @param pSource the source security holding
         * @param pGains the gains
         */
        protected void adjustStandardGain(final TransactionHelper pTrans,
                                          final SecurityHolding pSource,
                                          final TethysMoney pGains) {
            /* Access security and portfolio */
            Security mySecurity = pSource.getSecurity();
            Portfolio myPortfolio = pSource.getPortfolio();

            boolean bTaxFreeGains = myPortfolio.isTaxFree()
                                    || !mySecurity.getSecurityTypeClass().isCapitalGains();
            TransactionCategoryBucket myCategory = bTaxFreeGains
                                                                ? theTaxFreeGains
                                                                : theCapitalGains;
            TaxBasisClass myTaxBasis = bTaxFreeGains
                                                    ? TaxBasisClass.TAXFREE
                                                    : TaxBasisClass.CAPITALGAINS;

            /* Add to Capital Gains income/expense */
            if (pGains.isPositive()) {
                myCategory.addIncome(pTrans, pGains);
            } else {
                myCategory.subtractExpense(pTrans, pGains);
            }
            theTaxBasis.adjustValue(pTrans, myTaxBasis, pGains);
        }

        /**
         * Adjust for Taxable Gains.
         * @param pTrans the transaction helper
         * @param pReduction the income reduction
         */
        protected void adjustTaxableGain(final TransactionHelper pTrans,
                                         final TethysMoney pReduction) {
            /* Adjust Taxable Gains */
            theTaxableGains.subtractIncome(pReduction);
            theTaxableGains.adjustValues(pTrans);

            /* Obtain normalised value */
            TethysMoney myGains = new TethysMoney(pTrans.getLocalDebitAmount());
            myGains.subtractAmount(pReduction);

            /* Adjust for Tax Credit */
            TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                theTaxCredit.addExpense(pTrans, myTaxCredit);
                myGains.addAmount(myTaxCredit);

                /* Adjust tax basis */
                theTaxBasis.adjustValue(pTrans, TaxBasisClass.TAXPAID, myTaxCredit);
            }

            /* Adjust tax basis */
            theTaxBasis.adjustValue(pTrans, TaxBasisClass.TAXABLEGAINS, myGains);
        }

        /**
         * Produce totals for the TransactionCategories.
         */
        protected void produceTotals() {
            /* Create a list of new buckets */
            OrderedIdList<Integer, TransactionCategoryBucket> myTotals = new OrderedIdList<Integer, TransactionCategoryBucket>(TransactionCategoryBucket.class);

            /* Loop through the buckets */
            Iterator<TransactionCategoryBucket> myIterator = listIterator();
            while (myIterator.hasNext()) {
                TransactionCategoryBucket myCurr = myIterator.next();

                /* Obtain category and parent category */
                TransactionCategory myCategory = myCurr.getTransactionCategory();
                TransactionCategory myParent = myCategory.getParentCategory();

                /* Access parent bucket */
                TransactionCategoryBucket myTotal = findItemById(myParent.getId());

                /* Calculate the delta */
                myCurr.calculateDelta();

                /* If the bucket does not exist */
                if (myTotal == null) {
                    /* Look for bucket in the new list */
                    myTotal = myTotals.findItemById(myParent.getId());

                    /* If the bucket is completely new */
                    if (myTotal == null) {
                        /* Create the new bucket and add to new list */
                        myTotal = new TransactionCategoryBucket(theAnalysis, myParent);
                        myTotals.add(myTotal);
                    }
                }

                /* Add to totals bucket */
                myTotal.addValues(myCurr);
                theTotals.addValues(myCurr);
            }

            /* Loop through the new totals */
            myIterator = myTotals.listIterator();
            while (myIterator.hasNext()) {
                TransactionCategoryBucket myCurr = myIterator.next();

                /* Calculate delta for the category total */
                myCurr.calculateDelta();

                /* Add it to the list */
                add(myCurr);
            }

            /* Calculate delta for the totals */
            if (theTotals != null) {
                theTotals.calculateDelta();
            }
        }
    }
}
