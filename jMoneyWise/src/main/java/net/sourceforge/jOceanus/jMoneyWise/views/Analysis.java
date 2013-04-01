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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.AssetAccountDetail;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.LoanAccountDetail;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.MoneyAccountDetail;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.PayeeAccountDetail;
import net.sourceforge.jOceanus.jMoneyWise.views.AccountBucket.ValueBucket;
import net.sourceforge.jOceanus.jMoneyWise.views.AnalysisBucket.BucketType;
import net.sourceforge.jOceanus.jMoneyWise.views.ChargeableEvent.ChargeableEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryDetail;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxBucket.CategorySummary;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxBucket.CategoryTotal;
import net.sourceforge.jOceanus.jMoneyWise.views.TaxBucket.TaxDetail;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * Data Analysis.
 * @author Tony Washer
 */
public class Analysis
        implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(Analysis.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * State Field Id.
     */
    public static final JDataField FIELD_STATE = FIELD_DEFS.declareLocalField("State");

    /**
     * Buckets Field Id.
     */
    public static final JDataField FIELD_LIST = FIELD_DEFS.declareLocalField("BucketList");

    /**
     * Charges Field Id.
     */
    public static final JDataField FIELD_CHARGES = FIELD_DEFS.declareLocalField("Charges");

    /**
     * TaxYear Field Id.
     */
    public static final JDataField FIELD_TAXYEAR = FIELD_DEFS.declareLocalField("TaxYear");

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_STATE.equals(pField)) {
            return theAnalysisState;
        }
        if (FIELD_LIST.equals(pField)) {
            return (theList.size() > 0)
                    ? theList
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_CHARGES.equals(pField)) {
            return (theCharges.size() > 0)
                    ? theCharges
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXYEAR.equals(pField)) {
            return (theYear == null)
                    ? JDataFieldValue.SkipField
                    : theYear;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return (theAccount == null)
                    ? JDataFieldValue.SkipField
                    : theAccount;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The DataSet.
     */
    private final FinanceData theData;

    /**
     * The State.
     */
    private AnalysisState theAnalysisState = AnalysisState.RAW;

    /**
     * The buckets.
     */
    private final BucketList theList;

    /**
     * The charges.
     */
    private final ChargeableEventList theCharges;

    /**
     * The taxYear.
     */
    private final TaxYear theYear;

    /**
     * The Date.
     */
    private final JDateDay theDate;

    /**
     * The account.
     */
    private final Account theAccount;

    /**
     * Are there Gains slices.
     */
    private boolean hasGainsSlices = false;

    /**
     * Is there a reduced allowance?
     */
    private boolean hasReducedAllow = false;

    /**
     * User age.
     */
    private int theAge = 0;

    /**
     * Obtain the data.
     * @return the data
     */
    public FinanceData getData() {
        return theData;
    }

    /**
     * Obtain the state.
     * @return the state
     */
    public AnalysisState getState() {
        return theAnalysisState;
    }

    /**
     * Obtain the bucket list.
     * @return the list
     */
    public BucketList getList() {
        return theList;
    }

    /**
     * Obtain the taxYear.
     * @return the year
     */
    public TaxYear getTaxYear() {
        return theYear;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public JDateDay getDate() {
        return theDate;
    }

    /**
     * Obtain the account.
     * @return the account
     */
    public Account getAccount() {
        return theAccount;
    }

    /**
     * Obtain the charges.
     * @return the charges
     */
    public ChargeableEventList getCharges() {
        return theCharges;
    }

    /**
     * Have we a reduced allowance?
     * @return true/false
     */
    public boolean hasReducedAllow() {
        return hasReducedAllow;
    }

    /**
     * Do we have gains slices?
     * @return true/false
     */
    public boolean hasGainsSlices() {
        return hasGainsSlices;
    }

    /**
     * Obtain the user age.
     * @return the age
     */
    public int getAge() {
        return theAge;
    }

    /**
     * Set the state.
     * @param pState the state
     */
    protected void setState(final AnalysisState pState) {
        theAnalysisState = pState;
    }

    /**
     * Set the age.
     * @param pAge the age
     */
    protected void setAge(final int pAge) {
        theAge = pAge;
    }

    /**
     * Set whether the allowance is reduced.
     * @param hasReduced true/false
     */
    protected void setHasReducedAllow(final boolean hasReduced) {
        hasReducedAllow = hasReduced;
    }

    /**
     * Set whether we have gains slices.
     * @param hasSlices true/false
     */
    protected void setHasGainsSlices(final boolean hasSlices) {
        hasGainsSlices = hasSlices;
    }

    /**
     * Constructor for a dated analysis.
     * @param pData the data to analyse events for
     * @param pDate the Date for the analysis
     */
    public Analysis(final FinanceData pData,
                    final JDateDay pDate) {
        /* Store the data */
        theData = pData;
        theDate = pDate;
        theYear = null;
        theAccount = null;

        /* Create a new list */
        theList = new BucketList(this);
        theCharges = new ChargeableEventList();
    }

    /**
     * Constructor for a dated account analysis.
     * @param pData the data to analyse events for
     * @param pAccount the account to analyse
     * @param pDate the Date for the analysis
     */
    public Analysis(final FinanceData pData,
                    final Account pAccount,
                    final JDateDay pDate) {
        /* Store the data */
        theData = pData;
        theDate = pDate;
        theAccount = pAccount;
        theYear = null;

        /* Create a new list */
        theList = new BucketList(this);
        theCharges = new ChargeableEventList();
    }

    /**
     * Constructor for a taxYear analysis.
     * @param pData the data to analyse events for
     * @param pYear the year to analyse
     * @param pAnalysis the previous year analysis (if present)
     */
    public Analysis(final FinanceData pData,
                    final TaxYear pYear,
                    final Analysis pAnalysis) {
        /* Store the data */
        theData = pData;
        theYear = pYear;
        theDate = pYear.getTaxYear();
        theAccount = null;

        /* Create a new list */
        theList = new BucketList(this);
        theCharges = new ChargeableEventList();

        /* Return if we are the first analysis */
        if (pAnalysis == null) {
            return;
        }

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = pAnalysis.getList().listIterator();

        /* Loop through the buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myCurr = myIterator.next();

            /* Switch on the bucket type */
            switch (myCurr.getBucketType()) {
                case ASSETDETAIL:
                    if (myCurr.isActive()) {
                        AssetAccountDetail myAsset = new AssetAccountDetail((AssetAccountDetail) myCurr);
                        theList.add(myAsset);
                    }
                    break;
                case LOANDETAIL:
                    if (myCurr.isActive()) {
                        LoanAccountDetail myDebt = new LoanAccountDetail((LoanAccountDetail) myCurr);
                        theList.add(myDebt);
                    }
                    break;
                case BANKDETAIL:
                    if (myCurr.isActive()) {
                        MoneyAccountDetail myMoney = new MoneyAccountDetail((MoneyAccountDetail) myCurr);
                        theList.add(myMoney);
                    }
                    break;
                case PAYEEDETAIL:
                    if (myCurr.isActive()) {
                        PayeeAccountDetail myExternal = new PayeeAccountDetail((PayeeAccountDetail) myCurr);
                        theList.add(myExternal);
                    }
                    break;
                case CATDETAIL:
                    if (myCurr.isActive()) {
                        EventCategoryDetail myCategory = new EventCategoryDetail((EventCategoryDetail) myCurr);
                        theList.add(myCategory);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * The Bucket List class.
     */
    public static class BucketList
            extends OrderedIdList<Integer, AnalysisBucket>
            implements JDataContents {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(BucketList.class.getSimpleName());

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
        public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Analysis field Id.
         */
        public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
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
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public BucketList(final Analysis pAnalysis) {
            super(AnalysisBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
        }

        /**
         * Obtain the AccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected AccountBucket getAccountBucket(final Account pAccount) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.BANKDETAIL;
            int uId = pAccount.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            AccountBucket myItem = (AccountBucket) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Determine the bucket type */
                myBucket = BucketType.getAccountBucketType(pAccount);

                /* Switch on the bucket type */
                switch (myBucket) {
                    case BANKDETAIL:
                        myItem = new MoneyAccountDetail(pAccount);
                        break;
                    case ASSETDETAIL:
                        myItem = new AssetAccountDetail(theData, pAccount);
                        break;
                    case PAYEEDETAIL:
                        myItem = new PayeeAccountDetail(pAccount);
                        break;
                    case LOANDETAIL:
                    default:
                        myItem = new LoanAccountDetail(pAccount);
                        break;
                }

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Asset Summary Bucket for a given account category.
         * @param pCategory the account category
         * @return the bucket
         */
        protected AssetSummary getAssetSummary(final AccountCategory pCategory) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.ASSETSUMMARY;
            int uId = pCategory.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            AssetSummary myItem = (AssetSummary) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new AssetSummary(pCategory);
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Transaction Detail Bucket for a given transaction class.
         * @param pCategoryClass the category class
         * @return the bucket
         */
        protected EventCategoryDetail getCategoryDetail(final EventCategoryClass pCategoryClass) {
            /* Calculate the id that we are looking for */
            EventCategory myCat = theData.getEventCategories().getSingularClass(pCategoryClass);

            /* Return the bucket */
            return getCategoryDetail(myCat);
        }

        /**
         * Obtain the category Detail Bucket for a given category.
         * @param pCategory the category
         * @return the bucket
         */
        protected EventCategoryDetail getCategoryDetail(final EventCategory pCategory) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.CATDETAIL;
            int uId = pCategory.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            EventCategoryDetail myItem = (EventCategoryDetail) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new EventCategoryDetail(pCategory);
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Transaction Summary Bucket for a given tax type.
         * @param pTaxClass the taxation class
         * @return the bucket
         */
        protected CategorySummary getCategorySummary(final TaxCategoryClass pTaxClass) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.CATSUMMARY;
            TaxCategory myTaxBucket = theData.getTaxCategories().findItemByClass(pTaxClass);
            int uId = myTaxBucket.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            CategorySummary myItem = (CategorySummary) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new CategorySummary(myTaxBucket);
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Taxation Detail Bucket for a given tax type.
         * @param pTaxClass the taxation class
         * @return the bucket
         */
        protected TaxDetail getTaxDetail(final TaxCategoryClass pTaxClass) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.TAXDETAIL;
            TaxCategory myTaxBucket = theData.getTaxCategories().findItemByClass(pTaxClass);
            int uId = myTaxBucket.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TaxDetail myItem = (TaxDetail) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TaxDetail(myTaxBucket);
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Asset Total Bucket.
         * @return the bucket
         */
        protected AssetTotal getAssetTotal() {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.ASSETTOTAL;
            int uId = myBucket.getIdShift();

            /* Locate the bucket in the list */
            AssetTotal myItem = (AssetTotal) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new AssetTotal();
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Payee Total Bucket.
         * @return the bucket
         */
        protected PayeeTotal getPayeeTotal() {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.PAYEETOTAL;
            int uId = myBucket.getIdShift();

            /* Locate the bucket in the list */
            PayeeTotal myItem = (PayeeTotal) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new PayeeTotal();
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Market Total Bucket.
         * @return the bucket
         */
        protected MarketTotal getMarketTotal() {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.MARKETTOTAL;
            int uId = myBucket.getIdShift();

            /* Locate the bucket in the list */
            MarketTotal myItem = (MarketTotal) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new MarketTotal();
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Transaction Total Bucket.
         * @param pTaxClass the taxation class
         * @return the bucket
         */
        protected CategoryTotal getCategoryTotal(final TaxCategoryClass pTaxClass) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.CATTOTAL;
            TaxCategory myTaxBucket = theData.getTaxCategories().findItemByClass(pTaxClass);
            int uId = myTaxBucket.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            CategoryTotal myItem = (CategoryTotal) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new CategoryTotal(myTaxBucket);
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            /* Access the iterator */
            Iterator<AnalysisBucket> myIterator = listIterator();

            /* Loop through the buckets */
            while (myIterator.hasNext()) {
                AnalysisBucket myCurr = myIterator.next();

                /* Switch on the bucket type */
                switch (myCurr.getBucketType()) {
                /* Always keep asset details */
                    case ASSETDETAIL:
                        break;
                    /* Remove item if it is irrelevant */
                    default:
                        if (!myCurr.isRelevant()) {
                            myIterator.remove();
                        }
                        break;
                }
            }
        }
    }

    /**
     * The Account Category Bucket class.
     */
    private abstract static class AccountCategoryBucket
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountCategoryBucket.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        @Override
        public String formatObject() {
            return getName();
        }

        /**
         * Account Category Field Id.
         */
        public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityField("AccountCategory");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_CATEGORY.equals(pField)) {
                return theAccountCategory;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The AccountCategory.
         */
        private final AccountCategory theAccountCategory;

        /**
         * Obtain name.
         * @return the name
         */
        public String getName() {
            return theAccountCategory.getName();
        }

        /**
         * Obtain account category.
         * @return the type
         */
        public AccountCategory getAccountCategory() {
            return theAccountCategory;
        }

        /**
         * Constructor.
         * @param pAccountCategory the account category
         */
        private AccountCategoryBucket(final AccountCategory pAccountCategory) {
            /* Call super-constructor */
            super(BucketType.ASSETSUMMARY, pAccountCategory.getId());

            /* Store the account category */
            theAccountCategory = pAccountCategory;
        }

        @Override
        public int compareTo(final AnalysisBucket pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Compare the super class */
            int result = super.compareTo(pThat);
            if (result != 0) {
                return result;
            }

            /* Access the object as an Account Category Bucket */
            AccountCategoryBucket myThat = (AccountCategoryBucket) pThat;

            /* Compare the AccountCategories */
            return getAccountCategory().compareTo(myThat.getAccountCategory());
        }
    }

    /**
     * The AssetSummary Bucket class.
     */
    public static final class AssetSummary
            extends AccountCategoryBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AssetSummary.class.getSimpleName(), AccountCategoryBucket.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Value Field Id.
         */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        /**
         * Previous Value Field Id.
         */
        public static final JDataField FIELD_PREVVALUE = FIELD_DEFS.declareLocalField("PreviousValue");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_VALUE.equals(pField)) {
                return theValue;
            }
            if (FIELD_PREVVALUE.equals(pField)) {
                return thePrevValue;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The value.
         */
        private JMoney theValue = null;

        /**
         * The previous value.
         */
        private JMoney thePrevValue = null;

        @Override
        public AssetSummary getBase() {
            return (AssetSummary) super.getBase();
        }

        /**
         * Obtain value.
         * @return the value
         */
        public JMoney getValue() {
            return theValue;
        }

        /**
         * Obtain previous value.
         * @return the value
         */
        public JMoney getPrevValue() {
            return thePrevValue;
        }

        /**
         * Constructor.
         * @param pAccountCategory the account category
         */
        private AssetSummary(final AccountCategory pAccountCategory) {
            /* Call super-constructor */
            super(pAccountCategory);

            /* Initialise the Money values */
            theValue = new JMoney();
            thePrevValue = new JMoney();
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the summary value.
         * @param pBucket the bucket
         */
        protected void addValues(final ValueBucket pBucket) {
            ValueBucket myPrevious = pBucket.getBase();

            /* the total */
            theValue.addAmount(pBucket.getValue());

            /* If there are previous values, and them to totals */
            if (myPrevious != null) {
                thePrevValue.addAmount(myPrevious.getValue());
            }
        }
    }

    /**
     * The AssetTotal Bucket class.
     */
    public static final class AssetTotal
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AssetTotal.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Value Field Id.
         */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        /**
         * Profit Field Id.
         */
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        /**
         * Previous Value Field Id.
         */
        public static final JDataField FIELD_PREVVALUE = FIELD_DEFS.declareLocalField("PreviousValue");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_VALUE.equals(pField)) {
                return theValue;
            }
            if (FIELD_PROFIT.equals(pField)) {
                return theProfit;
            }
            if (FIELD_PREVVALUE.equals(pField)) {
                return theValue;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The value.
         */
        private JMoney theValue = null;

        /**
         * The profit.
         */
        private JMoney theProfit = null;

        /**
         * The previous value.
         */
        private JMoney thePrevValue = null;

        @Override
        public AssetTotal getBase() {
            return (AssetTotal) super.getBase();
        }

        /**
         * Obtain value.
         * @return the value
         */
        public JMoney getValue() {
            return theValue;
        }

        /**
         * Obtain profit.
         * @return the profit
         */
        public JMoney getProfit() {
            return theProfit;
        }

        /**
         * Obtain previous value.
         * @return the value
         */
        public JMoney getPrevValue() {
            return thePrevValue;
        }

        /**
         * Constructor.
         */
        private AssetTotal() {
            /* Call super-constructor */
            super(BucketType.ASSETTOTAL, 0);

            /* Initialise the Money values */
            theValue = new JMoney();
            thePrevValue = new JMoney();
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value.
         * @param pBucket the bucket
         */
        protected void addValues(final AssetSummary pBucket) {
            /* the total */
            theValue.addAmount(pBucket.getValue());
            thePrevValue.addAmount(pBucket.getPrevValue());
        }

        /**
         * Calculate profit.
         */
        protected void calculateProfit() {
            theProfit = new JMoney(theValue);
            theProfit.subtractAmount(thePrevValue);
        }
    }

    /**
     * The PayeeTotal Bucket class.
     */
    public static final class PayeeTotal
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(PayeeTotal.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Income Field Id.
         */
        public static final JDataField FIELD_INCOME = FIELD_DEFS.declareLocalField("Income");

        /**
         * Expense Field Id.
         */
        public static final JDataField FIELD_EXPENSE = FIELD_DEFS.declareLocalField("Expense");

        /**
         * Profit Field Id.
         */
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        /**
         * Previous Income Field Id.
         */
        public static final JDataField FIELD_PREVINCOME = FIELD_DEFS.declareLocalField("PreviousIncome");

        /**
         * Previous Expense Field Id.
         */
        public static final JDataField FIELD_PREVEXPENSE = FIELD_DEFS.declareLocalField("PreviousExpense");

        /**
         * Previous Profit Field Id.
         */
        public static final JDataField FIELD_PREVPROFIT = FIELD_DEFS.declareLocalField("PreviousProfit");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_INCOME.equals(pField)) {
                return theIncome;
            }
            if (FIELD_EXPENSE.equals(pField)) {
                return theExpense;
            }
            if (FIELD_PROFIT.equals(pField)) {
                return theProfit;
            }
            if (FIELD_PREVINCOME.equals(pField)) {
                return thePrevIncome;
            }
            if (FIELD_PREVEXPENSE.equals(pField)) {
                return thePrevExpense;
            }
            if (FIELD_PREVPROFIT.equals(pField)) {
                return thePrevProfit;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The income.
         */
        private JMoney theIncome = null;

        /**
         * The expense.
         */
        private JMoney theExpense = null;

        /**
         * The profit.
         */
        private JMoney theProfit = null;

        /**
         * The previous income.
         */
        private JMoney thePrevIncome = null;

        /**
         * The previous expense.
         */
        private JMoney thePrevExpense = null;

        /**
         * The previous profit.
         */
        private JMoney thePrevProfit = null;

        @Override
        public PayeeTotal getBase() {
            return (PayeeTotal) super.getBase();
        }

        /**
         * Obtain income.
         * @return the income
         */
        public JMoney getIncome() {
            return theIncome;
        }

        /**
         * Obtain expense.
         * @return the expense
         */
        public JMoney getExpense() {
            return theExpense;
        }

        /**
         * Obtain profit.
         * @return the profit
         */
        public JMoney getProfit() {
            return theProfit;
        }

        /**
         * Obtain previous income.
         * @return the income
         */
        public JMoney getPrevIncome() {
            return thePrevIncome;
        }

        /**
         * Obtain previous expense.
         * @return the expense
         */
        public JMoney getPrevExpense() {
            return thePrevExpense;
        }

        /**
         * Obtain previous profit.
         * @return the profit
         */
        public JMoney getPrevProfit() {
            return thePrevProfit;
        }

        /**
         * Constructor.
         */
        private PayeeTotal() {
            /* Call super-constructor */
            super(BucketType.PAYEETOTAL, 0);

            /* Initialise the Money values */
            theIncome = new JMoney();
            theExpense = new JMoney();
            thePrevIncome = new JMoney();
            thePrevExpense = new JMoney();
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value.
         * @param pBucket the bucket
         */
        protected void addValues(final PayeeAccountDetail pBucket) {
            PayeeAccountDetail myPrevious = pBucket.getBase();

            /* Add the values */
            theIncome.addAmount(pBucket.getIncome());
            theExpense.addAmount(pBucket.getExpense());

            /* If there are previous totals and we have previous totals */
            if (myPrevious != null) {
                /* Add previous values */
                thePrevIncome.addAmount(myPrevious.getIncome());
                thePrevExpense.addAmount(myPrevious.getExpense());
            }
        }

        /**
         * Calculate profit.
         */
        protected void calculateProfit() {
            theProfit = new JMoney(theIncome);
            theProfit.subtractAmount(theExpense);
            thePrevProfit = new JMoney(thePrevIncome);
            thePrevProfit.subtractAmount(thePrevExpense);
        }
    }

    /**
     * The MarketTotal Bucket class.
     */
    public static final class MarketTotal
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(MarketTotal.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Cost field Id.
         */
        public static final JDataField FIELD_COST = FIELD_DEFS.declareLocalField("Cost");

        /**
         * Value field Id.
         */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        /**
         * Gained field Id.
         */
        public static final JDataField FIELD_GAINED = FIELD_DEFS.declareLocalField("Gained");

        /**
         * Profit field Id.
         */
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_COST.equals(pField)) {
                return theCost;
            }
            if (FIELD_VALUE.equals(pField)) {
                return theValue;
            }
            if (FIELD_GAINED.equals(pField)) {
                return theGained;
            }
            if (FIELD_PROFIT.equals(pField)) {
                return theProfit;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The cost.
         */
        private JMoney theCost = null;

        /**
         * The value.
         */
        private JMoney theValue = null;

        /**
         * The gained.
         */
        private JMoney theGained = null;

        /**
         * The profit.
         */
        private JMoney theProfit = null;

        @Override
        public MarketTotal getBase() {
            return (MarketTotal) super.getBase();
        }

        /**
         * Obtain cost.
         * @return the cost
         */
        public JMoney getCost() {
            return theCost;
        }

        /**
         * Obtain gained.
         * @return the gained
         */
        public JMoney getGained() {
            return theGained;
        }

        /**
         * Obtain value.
         * @return the value
         */
        public JMoney getValue() {
            return theValue;
        }

        /**
         * Obtain profit.
         * @return the profit
         */
        public JMoney getProfit() {
            return theProfit;
        }

        /**
         * Constructor.
         */
        private MarketTotal() {
            /* Call super-constructor */
            super(BucketType.MARKETTOTAL, 0);

            /* Initialise the Money values */
            theCost = new JMoney();
            theValue = new JMoney();
            theGained = new JMoney();
            theProfit = new JMoney();
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value.
         * @param pBucket the source bucket to add
         */
        protected void addValues(final AssetAccountDetail pBucket) {
            theCost.addAmount(pBucket.getCost());
            theGained.addAmount(pBucket.getGained());
            theProfit.addAmount(pBucket.getProfit());
            theValue.addAmount(pBucket.getValue());
        }
    }

    /**
     * Analysis state.
     */
    protected enum AnalysisState {
        /**
         * Raw.
         */
        RAW,

        /**
         * Valued.
         */
        VALUED,

        /**
         * Totalled.
         */
        TOTALLED,

        /**
         * Taxed.
         */
        TAXED;
    }
}
