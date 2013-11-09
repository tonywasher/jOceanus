/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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

import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.data.AccountType;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategoryClass;
import net.sourceforge.joceanus.jsortedlist.OrderedIdItem;
import net.sourceforge.joceanus.jsortedlist.OrderedIdList;

/**
 * The TaxBasis Bucket class.
 */
public final class TaxBasisBucket
        implements JDataContents, Comparable<TaxBasisBucket>, OrderedIdItem<Integer> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TaxBasisBucket.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataAnalysis"));

    /**
     * Tax Category Field Id.
     */
    private static final JDataField FIELD_TAXCAT = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataCategory"));

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataBaseValues"));

    /**
     * History Field Id.
     */
    private static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataHistory"));

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = NLS_BUNDLE.getString("NameTotals");

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, TaxBasisAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, TaxBasisAttribute.class);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_TAXCAT.equals(pField)) {
            return theTaxCategory;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        TaxBasisAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof JDecimal) {
                return ((JDecimal) myValue).isNonZero()
                        ? myValue
                        : JDataFieldValue.SkipField;
            }
            return myValue;
        }

        return JDataFieldValue.UnknownField;
    }

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * Tax Category.
     */
    private final TaxCategory theTaxCategory;

    /**
     * Values.
     */
    private final TaxBasisValues theValues;

    /**
     * The base values.
     */
    private final TaxBasisValues theBaseValues;

    /**
     * History Map.
     */
    private final BucketHistory<TaxBasisValues> theHistory;

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
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
        return (theTaxCategory == null)
                ? NAME_TOTALS
                : theTaxCategory.getName();
    }

    /**
     * Obtain tax category.
     * @return the category
     */
    public TaxCategory getTaxCategory() {
        return theTaxCategory;
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
    public TaxBasisValues getValues() {
        return theValues;
    }

    /**
     * Obtain the value for a particular attribute.
     * @param pAttr the attribute
     * @return the value
     */
    public JMoney getMoneyValue(final TaxBasisAttribute pAttr) {
        return theValues.getMoneyValue(pAttr);
    }

    /**
     * Obtain the base value map.
     * @return the base value map
     */
    public TaxBasisValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<TaxBasisValues> getHistoryMap() {
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
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final TaxBasisAttribute pAttr,
                            final JMoney pValue) {
        /* Set the value into the list */
        theValues.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final TaxBasisAttribute pAttr) {
        /* Access value of object */
        Object myValue = getValue(pAttr);

        /* Return the value */
        return (myValue != null)
                ? myValue
                : JDataFieldValue.SkipField;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static TaxBasisAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final TaxBasisAttribute pAttr) {
        /* Obtain the attribute */
        return theValues.get(pAttr);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTaxCategory the category
     */
    private TaxBasisBucket(final Analysis pAnalysis,
                           final TaxCategory pTaxCategory) {
        /* Store the parameters */
        theTaxCategory = pTaxCategory;
        theAnalysis = pAnalysis;

        /* Create the history map */
        theHistory = new BucketHistory<TaxBasisValues>(new TaxBasisValues());

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
    private TaxBasisBucket(final Analysis pAnalysis,
                           final TaxBasisBucket pBase,
                           final JDateDay pDate) {
        /* Copy details from base */
        theTaxCategory = pBase.getTaxCategory();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new BucketHistory<TaxBasisValues>(pBase.getHistoryMap(), pDate);

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
    private TaxBasisBucket(final Analysis pAnalysis,
                           final TaxBasisBucket pBase,
                           final JDateDayRange pRange) {
        /* Copy details from base */
        theTaxCategory = pBase.getTaxCategory();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new BucketHistory<TaxBasisValues>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public int compareTo(final TaxBasisBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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
        if (!(pThat instanceof TaxBasisBucket)) {
            return false;
        }

        /* Compare the Tax Categories */
        TaxBasisBucket myThat = (TaxBasisBucket) pThat;
        return getTaxCategory().equals(myThat.getTaxCategory());
    }

    @Override
    public int hashCode() {
        return getTaxCategory().hashCode();
    }

    /**
     * Add income event.
     * @param pEvent the event
     */
    private void addIncomeEvent(final Event pEvent) {
        /* Access details */
        JMoney myAmount = pEvent.getAmount();
        JMoney myTaxCredit = pEvent.getTaxCredit();
        JMoney myNatIns = pEvent.getNatInsurance();
        JMoney myBenefit = pEvent.getDeemedBenefit();
        JMoney myDonation = pEvent.getCharityDonation();

        /* Determine style of event */
        AccountType myDebitType = AccountType.deriveType(pEvent.getDebit());
        AccountType myCreditType = AccountType.deriveType(pEvent.getCredit());
        TransactionType myActTran = myDebitType.getTransactionType(myCreditType);

        /* Access the counters */
        JMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.Gross);
        myGross = new JMoney(myGross);
        JMoney myNet = theValues.getMoneyValue(TaxBasisAttribute.Net);
        myNet = new JMoney(myNet);

        /* If this is an expense */
        if (myActTran.isExpense()) {
            /* Adjust the gross and net */
            myGross.subtractAmount(myAmount);
            myNet.subtractAmount(myAmount);
        } else {
            /* Adjust the gross and net */
            myGross.addAmount(myAmount);
            myNet.addAmount(myAmount);

            /* If we have a tax credit */
            if ((myTaxCredit != null)
                && (myTaxCredit.isNonZero())) {
                /* Adjust the tax */
                JMoney myTax = theValues.getMoneyValue(TaxBasisAttribute.TaxCredit);
                myTax = new JMoney(myTax);
                myTax.addAmount(myTaxCredit);
                setValue(TaxBasisAttribute.TaxCredit, myTax);

                /* Adjust the gross */
                myGross.addAmount(myTaxCredit);
            }

            /* If we have a natInsurance payment */
            if ((myNatIns != null)
                && (myNatIns.isNonZero())) {
                /* Adjust the gross */
                myGross.addAmount(myNatIns);
            }

            /* If we have a Benefit payment */
            if ((myBenefit != null)
                && (myBenefit.isNonZero())) {
                /* Adjust the gross */
                myGross.addAmount(myBenefit);
            }

            /* If we have a Charity donation */
            if ((myDonation != null)
                && (myDonation.isNonZero())) {
                /* Adjust the gross */
                myGross.addAmount(myDonation);
            }
        }

        /* Set the values */
        setValue(TaxBasisAttribute.Gross, myGross);
        setValue(TaxBasisAttribute.Net, myNet);

        /* Register the event */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Add expense event.
     * @param pEvent the event
     */
    private void addExpenseEvent(final Event pEvent) {
        /* Access details */
        JMoney myAmount = pEvent.getAmount();

        /* Determine style of event */
        AccountType myDebitType = AccountType.deriveType(pEvent.getDebit());
        AccountType myCreditType = AccountType.deriveType(pEvent.getCredit());
        TransactionType myActTran = myDebitType.getTransactionType(myCreditType);

        /* Access the counters */
        JMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.Gross);
        myGross = new JMoney(myGross);
        JMoney myNet = theValues.getMoneyValue(TaxBasisAttribute.Net);
        myNet = new JMoney(myNet);

        /* If this is an income */
        if (myActTran.isIncome()) {
            /* Adjust the gross and net */
            myGross.subtractAmount(myAmount);
            myNet.subtractAmount(myAmount);
        } else {
            /* Adjust the gross and net */
            myGross.addAmount(myAmount);
            myNet.addAmount(myAmount);
        }

        /* Set the values */
        setValue(TaxBasisAttribute.Gross, myGross);
        setValue(TaxBasisAttribute.Net, myNet);

        /* Register the event */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Adjust event value.
     * @param pEvent the event
     * @param pValue the value
     */
    private void adjustValue(final Event pEvent,
                             final JMoney pValue) {
        /* Adjust the value */
        adjustValue(pValue);

        /* Register the event */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Adjust value.
     * @param pValue the value
     */
    private void adjustValue(final JMoney pValue) {
        /* Access the counters */
        JMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.Gross);
        myGross = new JMoney(myGross);
        JMoney myNet = theValues.getMoneyValue(TaxBasisAttribute.Net);
        myNet = new JMoney(myNet);

        /* Adjust the gross and net */
        myGross.addAmount(pValue);
        myNet.addAmount(pValue);

        /* Set the values */
        setValue(TaxBasisAttribute.Gross, myGross);
        setValue(TaxBasisAttribute.Net, myNet);
    }

    /**
     * Add values.
     * @param pBucket tax category bucket
     */
    protected void addValues(final TaxBasisBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = theValues.getMoneyValue(TaxBasisAttribute.Gross);
        myAmount.addAmount(pBucket.getMoneyValue(TaxBasisAttribute.Gross));
    }

    /**
     * subtract values.
     * @param pBucket tax category bucket
     */
    protected void subtractValues(final TaxBasisBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = theValues.getMoneyValue(TaxBasisAttribute.Gross);
        myAmount.subtractAmount(pBucket.getMoneyValue(TaxBasisAttribute.Gross));
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
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * TaxBasisValues class.
     */
    public static final class TaxBasisValues
            extends BucketValues<TaxBasisValues, TaxBasisAttribute> {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = -2321116436055439979L;

        /**
         * Constructor.
         */
        private TaxBasisValues() {
            /* Initialise class */
            super(TaxBasisAttribute.class);

            /* Create all possible values */
            put(TaxBasisAttribute.Gross, new JMoney());
            put(TaxBasisAttribute.Net, new JMoney());
            put(TaxBasisAttribute.TaxCredit, new JMoney());
        }

        /**
         * Constructor.
         * @param pSource the source map.
         */
        private TaxBasisValues(final TaxBasisValues pSource) {
            /* Initialise class */
            super(pSource);
        }

        @Override
        protected TaxBasisValues getSnapShot() {
            return new TaxBasisValues(this);
        }

        @Override
        protected void adjustToBaseValues(final TaxBasisValues pBase) {
            /* Adjust gross values */
            JMoney myValue = getMoneyValue(TaxBasisAttribute.Gross);
            myValue = new JMoney(myValue);
            JMoney myBaseValue = pBase.getMoneyValue(TaxBasisAttribute.Gross);
            myValue.subtractAmount(myBaseValue);
            put(TaxBasisAttribute.Gross, myValue);

            /* Adjust net values */
            myValue = getMoneyValue(TaxBasisAttribute.Net);
            myValue = new JMoney(myValue);
            myBaseValue = pBase.getMoneyValue(TaxBasisAttribute.Net);
            myValue.subtractAmount(myBaseValue);
            put(TaxBasisAttribute.Net, myValue);

            /* Adjust tax credit values */
            myValue = getMoneyValue(TaxBasisAttribute.TaxCredit);
            myValue = new JMoney(myValue);
            myBaseValue = pBase.getMoneyValue(TaxBasisAttribute.TaxCredit);
            myValue.subtractAmount(myBaseValue);
            put(TaxBasisAttribute.TaxCredit, myValue);
        }

        @Override
        protected void resetBaseValues() {
            /* Reset Income and expense values */
            put(TaxBasisAttribute.Gross, new JMoney());
            put(TaxBasisAttribute.Net, new JMoney());
            put(TaxBasisAttribute.TaxCredit, new JMoney());
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            JMoney myGross = getMoneyValue(TaxBasisAttribute.Gross);
            JMoney myNet = getMoneyValue(TaxBasisAttribute.Net);
            JMoney myTax = getMoneyValue(TaxBasisAttribute.TaxCredit);
            return ((myGross.isNonZero())
                    || (myNet.isNonZero()) || (myTax.isNonZero()));
        }
    }

    /**
     * TaxBasisBucketList class.
     */
    public static class TaxBasisBucketList
            extends OrderedIdList<Integer, TaxBasisBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"));

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName()
                   + "("
                   + size()
                   + ")";
        }

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataSize"));

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAnalysis"));

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataTotals"));

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
            return JDataFieldValue.UnknownField;
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The data.
         */
        private final FinanceData theData;

        /**
         * The tax basis.
         */
        private final TaxBasisBucket theTotals;

        /**
         * Obtain the Totals.
         * @return the totals bucket
         */
        public TaxBasisBucket getTotals() {
            return theTotals;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public TaxBasisBucketList(final Analysis pAnalysis) {
            super(TaxBasisBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        public TaxBasisBucketList(final Analysis pAnalysis,
                                  final TaxBasisBucketList pBase,
                                  final JDateDay pDate) {
            /* Initialise class */
            super(TaxBasisBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Loop through the buckets */
            Iterator<TaxBasisBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                TaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                TaxBasisBucket myBucket = new TaxBasisBucket(pAnalysis, myCurr, pDate);

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
        public TaxBasisBucketList(final Analysis pAnalysis,
                                  final TaxBasisBucketList pBase,
                                  final JDateDayRange pRange) {
            /* Initialise class */
            super(TaxBasisBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theTotals = allocateTotalsBucket();

            /* Loop through the buckets */
            Iterator<TaxBasisBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                TaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                TaxBasisBucket myBucket = new TaxBasisBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    add(myBucket);
                }
            }
        }

        /**
         * Allocate the Totals EventCategoryBucket.
         * @return the bucket
         */
        private TaxBasisBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new TaxBasisBucket(theAnalysis, null);
        }

        /**
         * Obtain the EventCategoryBucket for a given event category class.
         * @param pClass the event category class
         * @return the bucket
         */
        public TaxBasisBucket getBucket(final TaxCategoryClass pClass) {
            /* Locate the bucket in the list */
            TaxCategory myCategory = theData.getTaxCategories().findItemByClass(pClass);
            TaxBasisBucket myItem = findItemById(myCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TaxBasisBucket(theAnalysis, myCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Adjust basis buckets.
         * @param pEvent the event
         * @param pCategory primary category
         */
        protected void adjustBasis(final Event pEvent,
                                   final EventCategory pCategory) {
            /* Switch on the category type */
            TaxBasisBucket myBucket;
            switch (pCategory.getCategoryTypeClass()) {
                case TaxedIncome:
                case BenefitIncome:
                case RentalIncome:
                    /* Adjust the Gross salary bucket */
                    myBucket = getBucket(TaxCategoryClass.GrossSalary);
                    myBucket.addIncomeEvent(pEvent);
                    break;
                case Interest:
                case TaxedInterest:
                case GrossInterest:
                    /* Adjust the Gross interest bucket */
                    myBucket = getBucket(TaxCategoryClass.GrossInterest);
                    myBucket.addIncomeEvent(pEvent);
                    break;
                case Dividend:
                case ShareDividend:
                    /* Adjust the Gross dividend bucket */
                    myBucket = getBucket(TaxCategoryClass.GrossDividend);
                    myBucket.addIncomeEvent(pEvent);
                    break;
                case UnitTrustDividend:
                    /* Adjust the Gross UT dividend bucket */
                    myBucket = getBucket(TaxCategoryClass.GrossUTDividend);
                    myBucket.addIncomeEvent(pEvent);
                    break;
                case RoomRentalIncome:
                    /* Adjust the Gross rental bucket */
                    myBucket = getBucket(TaxCategoryClass.GrossRental);
                    myBucket.addIncomeEvent(pEvent);
                    break;
                case TaxSettlement:
                    /* Adjust the Tax Paid bucket */
                    myBucket = getBucket(TaxCategoryClass.TaxPaid);
                    myBucket.addExpenseEvent(pEvent);
                    break;
                case TaxFreeInterest:
                case TaxFreeDividend:
                case LoanInterestEarned:
                case GrantIncome:
                case Inherited:
                case GiftedIncome:
                    /* Adjust the Tax Free bucket */
                    myBucket = getBucket(TaxCategoryClass.TaxFree);
                    myBucket.addIncomeEvent(pEvent);
                    break;
                case Expense:
                case LocalTaxes:
                case WriteOff:
                case LoanInterestCharged:
                case CharityDonation:
                case TaxRelief:
                case OtherIncome:
                    /* Adjust the Expense bucket */
                    myBucket = getBucket(TaxCategoryClass.Expense);
                    myBucket.addExpenseEvent(pEvent);
                    break;
                case StockTakeOver:
                case StockSplit:
                case StockDeMerger:
                case StockRightsTaken:
                case StockRightsWaived:
                case OpeningBalance:
                case Transfer:
                default:
                    break;
            }
        }

        /**
         * Adjust basis buckets.
         * @param pEvent the event
         * @param pClass the class
         * @param pIncome the income
         */
        protected void adjustValue(final Event pEvent,
                                   final TaxCategoryClass pClass,
                                   final JMoney pIncome) {
            /* Access the bucket and adjust it */
            TaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pEvent, pIncome);
        }

        /**
         * Adjust autoExpense.
         * @param pEvent the event
         * @param isExpense true/false
         */
        protected void adjustAutoExpense(final Event pEvent,
                                         final boolean isExpense) {
            /* Determine value */
            JMoney myAmount = pEvent.getAmount();
            if (!isExpense) {
                myAmount = new JMoney(myAmount);
                myAmount.negate();
            }

            /* Access the bucket and adjust it */
            TaxBasisBucket myBucket = getBucket(TaxCategoryClass.Expense);
            myBucket.adjustValue(pEvent, myAmount);
        }

        /**
         * Adjust for market growth.
         * @param pIncome the income
         * @param pExpense the expense
         */
        protected void adjustMarket(final JMoney pIncome,
                                    final JMoney pExpense) {
            /* Calculate the delta */
            JMoney myDelta = new JMoney(pIncome);
            myDelta.subtractAmount(pExpense);

            /* Access the bucket and adjust it */
            TaxBasisBucket myBucket = getBucket(TaxCategoryClass.Market);
            myBucket.adjustValue(myDelta);
        }

        /**
         * produce Totals.
         */
        protected void produceTotals() {
            /* Loop through the buckets */
            Iterator<TaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                TaxBasisBucket myBucket = myIterator.next();
                TaxCategory myType = myBucket.getTaxCategory();

                /* Switch on the tax type */
                switch (myType.getTaxClass()) {
                    case GrossSalary:
                    case GrossInterest:
                    case GrossDividend:
                    case GrossUTDividend:
                    case GrossRental:
                    case GrossTaxableGains:
                    case GrossCapitalGains:
                    case Market:
                    case TaxFree:
                        /* Adjust the Total Profit buckets */
                        theTotals.addValues(myBucket);
                        break;
                    case TaxPaid:
                    case Expense:
                    case Virtual:
                        /* Adjust the Total profits buckets */
                        theTotals.subtractValues(myBucket);
                        break;
                    default:
                        break;
                }
            }
        }

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            /* Access the iterator */
            Iterator<TaxBasisBucket> myIterator = listIterator();

            /* Loop through the buckets */
            while (myIterator.hasNext()) {
                TaxBasisBucket myCurr = myIterator.next();

                /* Remove the bucket if it is inactive */
                if (!myCurr.isActive()) {
                    myIterator.remove();
                }
            }
        }
    }

    /**
     * TaxBasisAttribute enumeration.
     */
    public enum TaxBasisAttribute {
        /**
         * Gross Amount.
         */
        Gross,

        /**
         * Net Amount.
         */
        Net,

        /**
         * TaxCredit.
         */
        TaxCredit;
    }
}
