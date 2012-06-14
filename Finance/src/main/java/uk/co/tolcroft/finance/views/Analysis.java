/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.views;

import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.data.AccountPrice.AccountPriceList;
import uk.co.tolcroft.finance.data.AccountRate;
import uk.co.tolcroft.finance.data.AccountRate.AccountRateList;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.StaticClass.TaxBucket;
import uk.co.tolcroft.finance.data.StaticClass.TaxClass;
import uk.co.tolcroft.finance.data.StaticClass.TransClass;
import uk.co.tolcroft.finance.data.TaxType;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.views.CapitalEvent.CapitalEventList;
import uk.co.tolcroft.finance.views.ChargeableEvent.ChargeableEventList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;

/**
 * Data Analysis.
 * @author Tony Washer
 */
public class Analysis implements JDataContents {
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
            return theList;
        }
        if (FIELD_CHARGES.equals(pField)) {
            return theCharges;
        }
        if (FIELD_TAXYEAR.equals(pField)) {
            return (theYear == null) ? JDataObject.FIELD_SKIP : theYear;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return (theAccount == null) ? JDataObject.FIELD_SKIP : theAccount;
        }
        return null;
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
    private final DateDay theDate;

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
    public DateDay getDate() {
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
                    final DateDay pDate) {
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
                    final DateDay pDate) {
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
     * Constructor for a dated account analysis.
     * @param pData the data to analyse events for
     * @param pYear the year to analyse
     * @param pAnalysis the previous year analysis (if present)
     */
    public Analysis(final FinanceData pData,
                    final TaxYear pYear,
                    final Analysis pAnalysis) {
        /* Local variables */
        DataListIterator<AnalysisBucket> myIterator;
        AnalysisBucket myCurr;
        AssetAccount myAsset;
        DebtAccount myDebt;
        MoneyAccount myMoney;
        ExternalAccount myExternal;
        TransDetail myTrans;

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
        myIterator = pAnalysis.getList().listIterator();

        /* Loop through the buckets */
        while ((myCurr = myIterator.next()) != null) {
            /* Switch on the bucket type */
            switch (myCurr.getBucketType()) {
                case ASSETDETAIL:
                    if (myCurr.isActive()) {
                        myAsset = new AssetAccount(theList, (AssetAccount) myCurr);
                        theList.add(myAsset);
                    }
                    break;
                case DEBTDETAIL:
                    if (myCurr.isActive()) {
                        myDebt = new DebtAccount(theList, (DebtAccount) myCurr);
                        theList.add(myDebt);
                    }
                    break;
                case MONEYDETAIL:
                    if (myCurr.isActive()) {
                        myMoney = new MoneyAccount(theList, (MoneyAccount) myCurr);
                        theList.add(myMoney);
                    }
                    break;
                case EXTERNALDETAIL:
                    if (myCurr.isActive()) {
                        myExternal = new ExternalAccount(theList, (ExternalAccount) myCurr);
                        theList.add(myExternal);
                    }
                    break;
                case TRANSDETAIL:
                    if (myCurr.isActive()) {
                        myTrans = new TransDetail(theList, (TransDetail) myCurr);
                        theList.add(myTrans);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * AnalysisBucket Class.
     */
    protected abstract static class AnalysisBucket extends DataItem<AnalysisBucket> {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AnalysisBucket.class.getSimpleName(),
                DataItem.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * Bucket type field id.
         */
        public static final JDataField FIELD_BUCKETTYPE = FIELD_DEFS.declareEqualityField("BucketType");

        /**
         * Date field id.
         */
        public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKETTYPE.equals(pField)) {
                return theBucketType;
            }
            if (FIELD_DATE.equals(pField)) {
                return theDate;
            }
            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        /**
         * The bucket type.
         */
        private final BucketType theBucketType;

        /**
         * The data.
         */
        private final FinanceData theData;

        /**
         * The date.
         */
        private final DateDay theDate;

        /**
         * Obtain the bucket type.
         * @return the type
         */
        public BucketType getBucketType() {
            return theBucketType;
        }

        /**
         * Obtain the dataSet.
         * @return the data
         */
        protected FinanceData getData() {
            return theData;
        }

        /**
         * Obtain the date.
         * @return the date
         */
        protected DateDay getDate() {
            return theDate;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pType the bucket type
         * @param uId the id
         */
        public AnalysisBucket(final BucketList pList,
                              final BucketType pType,
                              final int uId) {
            /* Call super-constructor */
            super(pList, uId + pType.getIdShift());
            theData = pList.theAnalysis.theData;
            theDate = pList.theAnalysis.theDate;

            /* Store the bucket type */
            theBucketType = pType;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(final Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket)) {
                return -1;
            }

            /* Access the object as am Analysis Bucket */
            AnalysisBucket myThat = (AnalysisBucket) pThat;

            /* Compare the bucket order */
            return getBucketType().compareTo(myThat.getBucketType());
        }

        /**
         * is the bucket relevant (i.e. should it be reported)?
         * @return TRUE/FALSE
         */
        protected abstract boolean isRelevant();
    }

    /**
     * The Bucket List class.
     * @author Tony Washer
     */
    public static class BucketList extends DataList<BucketList, AnalysisBucket> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(BucketList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        /**
         * Analysis field Id.
         */
        public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            return super.getFieldValue(pField);
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
         * The name of the object.
         */
        private static final String LIST_NAME = "AnalysisBuckets";

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public BucketList(final Analysis pAnalysis) {
            super(BucketList.class, AnalysisBucket.class, ListStyle.VIEW, false);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
        }

        @Override
        public BucketList getUpdateList() {
            return null;
        }

        @Override
        public BucketList getEditList() {
            return null;
        }

        @Override
        public BucketList getShallowCopy() {
            return null;
        }

        @Override
        public BucketList getDeepCopy(final DataSet<?> pData) {
            return null;
        }

        @Override
        public BucketList getDifferences(final BucketList pOld) {
            return null;
        }

        /**
         * Add a new item to the list.
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public AnalysisBucket addNewItem(final DataItem<?> pItem) {
            return null;
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public AnalysisBucket addNewItem() {
            return null;
        }

        /**
         * Obtain the AccountDetail Bucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected ActDetail getAccountDetail(final Account pAccount) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.MONEYDETAIL;
            int uId = pAccount.getId() + myBucket.getIdShift();

            /* Locate the bucket in the list */
            ActDetail myItem = (ActDetail) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Determine the bucket type */
                myBucket = BucketType.getActBucketType(pAccount);

                /* Switch on the bucket type */
                switch (myBucket) {
                    case MONEYDETAIL:
                        myItem = new MoneyAccount(this, pAccount);
                        break;
                    case ASSETDETAIL:
                        myItem = new AssetAccount(this, pAccount);
                        break;
                    case EXTERNALDETAIL:
                        myItem = new ExternalAccount(this, pAccount);
                        break;
                    case DEBTDETAIL:
                    default:
                        myItem = new DebtAccount(this, pAccount);
                        break;
                }

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Asset Summary Bucket for a given account type.
         * @param pActType the account type
         * @return the bucket
         */
        protected AssetSummary getAssetSummary(final AccountType pActType) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.ASSETSUMMARY;
            int uId = pActType.getId() + myBucket.getIdShift();

            /* Locate the bucket in the list */
            AssetSummary myItem = (AssetSummary) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new AssetSummary(this, pActType);
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the Transaction Detail Bucket for a given transaction class.
         * @param pTransClass the transaction class
         * @return the bucket
         */
        protected TransDetail getTransDetail(final TransClass pTransClass) {
            /* Calculate the id that we are looking for */
            TransactionType myTrans = theData.getTransTypes().searchFor(pTransClass);

            /* Return the bucket */
            return getTransDetail(myTrans);
        }

        /**
         * Obtain the Transaction Detail Bucket for a given transaction type.
         * @param pTransType the transaction type
         * @return the bucket
         */
        protected TransDetail getTransDetail(final TransactionType pTransType) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.TRANSDETAIL;
            int uId = pTransType.getId() + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TransDetail myItem = (TransDetail) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TransDetail(this, pTransType);
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
        protected TransSummary getTransSummary(final TaxClass pTaxClass) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.TRANSSUMMARY;
            TaxType myTaxType = theData.getTaxTypes().searchFor(pTaxClass);
            int uId = myTaxType.getId() + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TransSummary myItem = (TransSummary) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TransSummary(this, myTaxType);
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
        protected TaxDetail getTaxDetail(final TaxClass pTaxClass) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.TAXDETAIL;
            TaxType myTaxType = theData.getTaxTypes().searchFor(pTaxClass);
            int uId = myTaxType.getId() + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TaxDetail myItem = (TaxDetail) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TaxDetail(this, myTaxType);
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
            AssetTotal myItem = (AssetTotal) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new AssetTotal(this);
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the External Total Bucket.
         * @return the bucket
         */
        protected ExternalTotal getExternalTotal() {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.EXTERNALTOTAL;
            int uId = myBucket.getIdShift();

            /* Locate the bucket in the list */
            ExternalTotal myItem = (ExternalTotal) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new ExternalTotal(this);
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
            MarketTotal myItem = (MarketTotal) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new MarketTotal(this);
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
        protected TransTotal getTransTotal(final TaxClass pTaxClass) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.TRANSTOTAL;
            TaxType myTaxType = theData.getTaxTypes().searchFor(pTaxClass);
            int uId = myTaxType.getId() + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TransTotal myItem = (TransTotal) searchFor(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TransTotal(this, myTaxType);
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            DataListIterator<AnalysisBucket> myIterator;
            AnalysisBucket myCurr;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the buckets */
            while ((myCurr = myIterator.next()) != null) {
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
     * The Account Bucket class.
     */
    protected abstract static class ActDetail extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ActDetail.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /**
         * Account Field Id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField("Account");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACCOUNT.equals(pField)) {
                return theAccount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The account.
         */
        private final Account theAccount;

        /**
         * Obtain the name.
         * @return the name
         */
        public String getName() {
            return theAccount.getName();
        }

        /**
         * Obtain the account.
         * @return the account
         */
        public Account getAccount() {
            return theAccount;
        }

        /**
         * Obtain the account type.
         * @return the account type
         */
        public AccountType getAccountType() {
            return theAccount.getActType();
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pType the type
         * @param pAccount the account
         */
        private ActDetail(final BucketList pList,
                          final BucketType pType,
                          final Account pAccount) {
            /* Call super-constructor */
            super(pList, pType, pAccount.getId());

            /* Store the account */
            theAccount = pAccount;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(final Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket)) {
                return -1;
            }

            /* Access the object as an Analysis Bucket */
            AnalysisBucket myBucket = (AnalysisBucket) pThat;

            /* Compare the bucket types */
            result = super.compareTo(myBucket);
            if (result != 0) {
                return result;
            }

            /* Access the object as an Act Bucket */
            ActDetail myThat = (ActDetail) pThat;

            /* Compare the Accounts */
            return getAccount().compareTo(myThat.getAccount());
        }

        /**
         * Adjust account for debit.
         * @param pEvent the event causing the debit
         */
        protected abstract void adjustForDebit(final Event pEvent);

        /**
         * Adjust account for credit.
         * @param pEvent the event causing the credit
         */
        protected abstract void adjustForCredit(final Event pEvent);

        /**
         * Create a save point.
         */
        protected abstract void createSavePoint();

        /**
         * Restore a save point.
         */
        protected abstract void restoreSavePoint();
    }

    /**
     * The Account Type Bucket class.
     */
    private abstract static class ActType extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ActType.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /**
         * Account Type Field Id.
         */
        public static final JDataField FIELD_ACTTYPE = FIELD_DEFS.declareEqualityField("AccountType");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACTTYPE.equals(pField)) {
                return theAccountType;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The AccountType.
         */
        private final AccountType theAccountType;

        /**
         * Obtain name.
         * @return the name
         */
        public String getName() {
            return theAccountType.getName();
        }

        /**
         * Obtain account type.
         * @return the type
         */
        public AccountType getAccountType() {
            return theAccountType;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pAccountType the account type
         */
        private ActType(final BucketList pList,
                        final AccountType pAccountType) {
            /* Call super-constructor */
            super(pList, BucketType.ASSETSUMMARY, pAccountType.getId());

            /* Store the account type */
            theAccountType = pAccountType;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(final Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket)) {
                return -1;
            }

            /* Access the object as an Analysis Bucket */
            AnalysisBucket myBucket = (AnalysisBucket) pThat;

            /* Compare the bucket types */
            result = super.compareTo(myBucket);
            if (result != 0) {
                return result;
            }

            /* Access the object as an ActType Bucket */
            ActType myThat = (ActType) pThat;

            /* Compare the AccountTypes */
            return getAccountType().compareTo(myThat.getAccountType());
        }
    }

    /**
     * The TransType Bucket class.
     */
    private abstract static class TransType extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransType.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /**
         * TransactionType Field Id.
         */
        public static final JDataField FIELD_TRANSTYPE = FIELD_DEFS.declareEqualityField("TransactionType");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_TRANSTYPE.equals(pField)) {
                return theTransType;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The Transaction Type.
         */
        private final TransactionType theTransType;

        /**
         * Obtain name.
         * @return the name
         */
        public String getName() {
            return theTransType.getName();
        }

        /**
         * Obtain transaction type.
         * @return the type
         */
        public TransactionType getTransType() {
            return theTransType;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pTransType the type
         */
        private TransType(final BucketList pList,
                          final TransactionType pTransType) {
            /* Call super-constructor */
            super(pList, BucketType.TRANSDETAIL, pTransType.getId());

            /* Store the transaction type */
            theTransType = pTransType;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(final Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket)) {
                return -1;
            }

            /* Access the object as an Analysis Bucket */
            AnalysisBucket myBucket = (AnalysisBucket) pThat;

            /* Compare the bucket types */
            result = super.compareTo(myBucket);
            if (result != 0) {
                return result;
            }

            /* Access the object as an TransType Bucket */
            TransType myThat = (TransType) pThat;

            /* Compare the TransactionTypes */
            return getTransType().compareTo(myThat.getTransType());
        }
    }

    /**
     * The Tax Bucket class.
     */
    private abstract static class Tax extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(Tax.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /**
         * Tax Type Field Id.
         */
        public static final JDataField FIELD_TAXTYPE = FIELD_DEFS.declareEqualityField("TaxType");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_TAXTYPE.equals(pField)) {
                return theTaxType;
            }
            return super.getFieldValue(pField);
        }

        /**
         * Tax Type.
         */
        private final TaxType theTaxType;

        /**
         * Obtain name.
         * @return the name
         */
        public String getName() {
            return theTaxType.getName();
        }

        /**
         * Obtain tax type.
         * @return the type
         */
        public TaxType getTaxType() {
            return theTaxType;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pTaxType the type
         */
        private Tax(final BucketList pList,
                    final TaxType pTaxType) {
            /* Call super-constructor */
            super(pList, BucketType.getTaxBucketType(pTaxType), pTaxType.getId());

            /* Store the tax type */
            theTaxType = pTaxType;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(final Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket)) {
                return -1;
            }

            /* Access the object as an Analysis Bucket */
            AnalysisBucket myBucket = (AnalysisBucket) pThat;

            /* Compare the bucket types */
            result = super.compareTo(myBucket);
            if (result != 0) {
                return result;
            }

            /* Access the object as an Tax Bucket */
            Tax myThat = (Tax) pThat;

            /* Compare the TaxTypes */
            return getTaxType().compareTo(myThat.getTaxType());
        }
    }

    /**
     * The ValueAccount Bucket class.
     */
    protected abstract static class ValueAccount extends ActDetail {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ValueAccount.class.getSimpleName(),
                ActDetail.FIELD_DEFS);

        /**
         * Value Field Id.
         */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_VALUE.equals(pField)) {
                return theValue;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The value.
         */
        private Money theValue = null;

        @Override
        public ValueAccount getBase() {
            return (ValueAccount) super.getBase();
        }

        /**
         * Obtain the value.
         * @return the value
         */
        public Money getValue() {
            return theValue;
        }

        /**
         * Obtain the previous value.
         * @return the value
         */
        public Money getPrevValue() {
            return (getBase() != null) ? getBase().getValue() : null;
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final Money pValue) {
            theValue = pValue;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pType the type
         * @param pAccount the account
         */
        private ValueAccount(final BucketList pList,
                             final BucketType pType,
                             final Account pAccount) {
            /* Call super-constructor */
            super(pList, pType, pAccount);

            /* Initialise the money values */
            theValue = new Money(0);
        }

        @Override
        public boolean isActive() {
            /* Copy if the value is non-zero */
            return theValue.isNonZero();
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theValue.isNonZero() || ((getPrevValue() != null) && (getPrevValue().isNonZero())));
        }

        /**
         * Adjust account for debit.
         * @param pEvent the event causing the debit
         */
        @Override
        protected void adjustForDebit(final Event pEvent) {
            /* Adjust for debit */
            theValue.subtractAmount(pEvent.getAmount());
        }

        /**
         * Adjust account for credit.
         * @param pEvent the event causing the credit
         */
        @Override
        protected void adjustForCredit(final Event pEvent) {
            /* Adjust for credit */
            theValue.addAmount(pEvent.getAmount());
        }
    }

    /**
     * The MoneyAccount Bucket class.
     */
    public static final class MoneyAccount extends ValueAccount {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(MoneyAccount.class.getSimpleName(),
                ValueAccount.FIELD_DEFS);

        /**
         * Rate field Id.
         */
        public static final JDataField FIELD_RATE = FIELD_DEFS.declareLocalField("Rate");

        /**
         * Maturity field Id.
         */
        public static final JDataField FIELD_MATURITY = FIELD_DEFS.declareLocalField("Maturity");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_RATE.equals(pField)) {
                return theRate;
            }
            if (FIELD_MATURITY.equals(pField)) {
                return theMaturity;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The rate.
         */
        private Rate theRate = null;

        /**
         * The maturity.
         */
        private DateDay theMaturity = null;

        /**
         * The savepoint.
         */
        private MoneyAccount theSavePoint = null;

        @Override
        public MoneyAccount getBase() {
            return (MoneyAccount) super.getBase();
        }

        /**
         * Obtain the rate.
         * @return the rate
         */
        public Rate getRate() {
            return theRate;
        }

        /**
         * Obtain the maturity.
         * @return the maturity
         */
        public DateDay getMaturity() {
            return theMaturity;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pAccount the account
         */
        private MoneyAccount(final BucketList pList,
                             final Account pAccount) {
            /* Call super-constructor */
            super(pList, BucketType.MONEYDETAIL, pAccount);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pPrevious the previous
         */
        private MoneyAccount(final BucketList pList,
                             final MoneyAccount pPrevious) {
            /* Call super-constructor */
            super(pList, BucketType.MONEYDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));

            /* Add the link to the previous item */
            setBase(new MoneyAccount(pPrevious));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pPrevious the previous.
         */
        private MoneyAccount(final MoneyAccount pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), BucketType.MONEYDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));
            if (pPrevious.getRate() != null) {
                theRate = new Rate(pPrevious.getRate());
            }
            if (pPrevious.getMaturity() != null) {
                theMaturity = new DateDay(pPrevious.getMaturity());
            }

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * record the rate of the account at a given date.
         * @param pDate the date of valuation
         */
        protected void recordRate(final DateDay pDate) {
            AccountRateList myRates = getData().getRates();
            AccountRate myRate;
            DateDay myDate;

            /* Obtain the appropriate price record */
            myRate = myRates.getLatestRate(getAccount(), getDate());
            myDate = getAccount().getMaturity();

            /* If we have a rate */
            if (myRate != null) {
                /* Use Rate date instead */
                if (myDate == null) {
                    myDate = myRate.getDate();
                }

                /* Store the rate */
                theRate = myRate.getRate();
            }

            /* Store the maturity */
            theMaturity = myDate;
        }

        /**
         * Create a Save Point.
         */
        @Override
        protected void createSavePoint() {
            /* Create a save of the values */
            theSavePoint = new MoneyAccount(this);
        }

        /**
         * Restore a Save Point.
         */
        @Override
        protected void restoreSavePoint() {
            /* If we have a Save point */
            if (theSavePoint != null) {
                /* Restore original value */
                setValue(new Money(theSavePoint.getValue()));
            }
        }
    }

    /**
     * The DebtAccount Bucket class.
     */
    public static final class DebtAccount extends ValueAccount {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(DebtAccount.class.getSimpleName(),
                ValueAccount.FIELD_DEFS);

        /**
         * Spend Field Id.
         */
        public static final JDataField FIELD_SPEND = FIELD_DEFS.declareLocalField("Spend");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SPEND.equals(pField)) {
                return theSpend;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The spend.
         */
        private Money theSpend = null;

        /**
         * The savePoint.
         */
        private DebtAccount theSavePoint = null;

        @Override
        public DebtAccount getBase() {
            return (DebtAccount) super.getBase();
        }

        /**
         * Obtain the spend.
         * @return the spend
         */
        public Money getSpend() {
            return theSpend;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pAccount the account
         */
        private DebtAccount(final BucketList pList,
                            final Account pAccount) {
            /* Call super-constructor */
            super(pList, BucketType.DEBTDETAIL, pAccount);

            /* Initialise the money values */
            theSpend = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pPrevious the previous
         */
        private DebtAccount(final BucketList pList,
                            final DebtAccount pPrevious) {
            /* Call super-constructor */
            super(pList, BucketType.DEBTDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));
            theSpend = new Money(0);

            /* Add the link to the previous item */
            setBase(new DebtAccount(pPrevious));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pPrevious the previous
         */
        private DebtAccount(final DebtAccount pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), BucketType.DEBTDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));
            theSpend = new Money(pPrevious.getSpend());

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Create a Save Point.
         */
        @Override
        protected void createSavePoint() {
            /* Create a save of the values */
            theSavePoint = new DebtAccount(this);
        }

        /**
         * Restore a Save Point.
         */
        @Override
        protected void restoreSavePoint() {
            /* If we have a Save point */
            if (theSavePoint != null) {
                /* Restore original value */
                setValue(new Money(theSavePoint.getValue()));
                theSpend = new Money(theSavePoint.getSpend());
            }
        }
    }

    /**
     * The AssetAccount Bucket class.
     */
    public static final class AssetAccount extends ValueAccount {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AssetAccount.class.getSimpleName(),
                ValueAccount.FIELD_DEFS);

        /**
         * Cost field id.
         */
        public static final JDataField FIELD_COST = FIELD_DEFS.declareLocalField("Cost");

        /**
         * Units field id.
         */
        public static final JDataField FIELD_UNITS = FIELD_DEFS.declareLocalField("Units");

        /**
         * Gained field id.
         */
        public static final JDataField FIELD_GAINED = FIELD_DEFS.declareLocalField("Gained");

        /**
         * Invested field id.
         */
        public static final JDataField FIELD_INVESTED = FIELD_DEFS.declareLocalField("Invested");

        /**
         * Dividend field id.
         */
        public static final JDataField FIELD_DIVIDEND = FIELD_DEFS.declareLocalField("Cost");

        /**
         * Gains field id.
         */
        public static final JDataField FIELD_GAINS = FIELD_DEFS.declareLocalField("Gains");

        /**
         * Price field id.
         */
        public static final JDataField FIELD_PRICE = FIELD_DEFS.declareLocalField("Price");

        /**
         * Profit field id.
         */
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_COST.equals(pField)) {
                return theCost;
            }
            if (FIELD_UNITS.equals(pField)) {
                return theUnits;
            }
            if (FIELD_GAINED.equals(pField)) {
                return theGained;
            }
            if (FIELD_INVESTED.equals(pField)) {
                return theInvested;
            }
            if (FIELD_DIVIDEND.equals(pField)) {
                return theDividend;
            }
            if (FIELD_GAINS.equals(pField)) {
                return theGains;
            }
            if (FIELD_PRICE.equals(pField)) {
                return thePrice;
            }
            if (FIELD_PROFIT.equals(pField)) {
                return theProfit;
            }
            return super.getFieldValue(pField);
        }

        /**
         * CapitalEvent list.
         */
        private CapitalEventList theEvents = null;

        /**
         * The cost.
         */
        private Money theCost = null;

        /**
         * The units.
         */
        private Units theUnits = null;

        /**
         * The gained.
         */
        private Money theGained = null;

        /**
         * The invested.
         */
        private Money theInvested = null;

        /**
         * The dividend.
         */
        private Money theDividend = null;

        /**
         * The gains.
         */
        private Money theGains = null;

        /**
         * The profit.
         */
        private Money theProfit = null;

        /**
         * The price.
         */
        private Price thePrice = null;

        /**
         * The savePoint.
         */
        private AssetAccount theSavePoint = null;

        @Override
        public AssetAccount getBase() {
            return (AssetAccount) super.getBase();
        }

        /**
         * Obtain cost.
         * @return the cost
         */
        public Money getCost() {
            return theCost;
        }

        /**
         * Obtain units.
         * @return the units
         */
        public Units getUnits() {
            return theUnits;
        }

        /**
         * Obtain gained.
         * @return the gained
         */
        public Money getGained() {
            return theGained;
        }

        /**
         * Obtain invested.
         * @return the invested
         */
        public Money getInvested() {
            return theInvested;
        }

        /**
         * Obtain dividend.
         * @return the dividend
         */
        public Money getDividend() {
            return theDividend;
        }

        /**
         * Obtain gains.
         * @return the gains
         */
        public Money getGains() {
            return theGains;
        }

        /**
         * Obtain profit.
         * @return the profit
         */
        public Money getProfit() {
            return theProfit;
        }

        /**
         * Obtain price.
         * @return the price
         */
        public Price getPrice() {
            return thePrice;
        }

        /**
         * Obtain previous cost.
         * @return the cost
         */
        public Money getPrevCost() {
            return (getBase() != null) ? getBase().getCost() : null;
        }

        /**
         * Obtain previous units.
         * @return the units
         */
        public Units getPrevUnits() {
            return (getBase() != null) ? getBase().getUnits() : null;
        }

        /**
         * Obtain previous gained.
         * @return the gained
         */
        public Money getPrevGained() {
            return (getBase() != null) ? getBase().getGained() : null;
        }

        /**
         * Obtain capital events.
         * @return the events
         */
        public CapitalEventList getCapitalEvents() {
            return theEvents;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pAccount the account
         */
        private AssetAccount(final BucketList pList,
                             final Account pAccount) {
            /* Call super-constructor */
            super(pList, BucketType.ASSETDETAIL, pAccount);

            /* Initialise the values */
            theUnits = new Units(0);
            theCost = new Money(0);
            theGained = new Money(0);

            theInvested = new Money(0);
            theDividend = new Money(0);
            theGains = new Money(0);

            /* allocate the Capital events */
            theEvents = new CapitalEventList(getData(), pAccount);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pPrevious the previous
         */
        private AssetAccount(final BucketList pList,
                             final AssetAccount pPrevious) {
            /* Call super-constructor */
            super(pList, BucketType.ASSETDETAIL, pPrevious.getAccount());

            /* Initialise the values */
            theUnits = new Units(pPrevious.getUnits());
            theCost = new Money(pPrevious.getCost());
            theGained = new Money(pPrevious.getGained());
            theInvested = new Money(0);
            theGains = new Money(0);
            theDividend = new Money(0);

            /* Copy the Capital Events */
            theEvents = pPrevious.getCapitalEvents();

            /* Add the link to the previous item */
            setBase(new AssetAccount(pPrevious));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pPrevious the previous
         */
        private AssetAccount(final AssetAccount pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), BucketType.ASSETDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            theUnits = new Units(pPrevious.getUnits());
            theCost = new Money(pPrevious.getCost());
            theGained = new Money(pPrevious.getGained());
            theInvested = new Money(pPrevious.getInvested());
            theDividend = new Money(pPrevious.getDividend());
            theGains = new Money(pPrevious.getGains());

            /* Copy price if available */
            if (pPrevious.getPrice() != null) {
                thePrice = new Price(pPrevious.getPrice());
            }

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));

            /* Set status */
            setState(DataState.CLEAN);
        }

        @Override
        public boolean isActive() {
            /* Copy if the units is non-zero */
            return theUnits.isNonZero();
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theUnits.isNonZero() || ((getPrevUnits() != null) && (getPrevUnits().isNonZero())));
        }

        /**
         * value the asset at a particular date.
         * @param pDate the date of valuation
         */
        protected void valueAsset(final DateDay pDate) {
            AccountPriceList myPrices = getData().getPrices();
            AccountPrice myActPrice;

            /* Obtain the appropriate price record */
            myActPrice = myPrices.getLatestPrice(getAccount(), pDate);

            /* If we found a price */
            if (myActPrice != null) {
                /* Store the price */
                thePrice = myActPrice.getPrice();

                /* else assume zero price */
            } else {
                thePrice = new Price(0);
            }

            /* Calculate the value */
            setValue(theUnits.valueAtPrice(thePrice));
        }

        /**
         * Calculate profit.
         */
        protected void calculateProfit() {
            /* Calculate the profit */
            theProfit = new Money(getValue());
            theProfit.subtractAmount(theCost);
            theProfit.addAmount(theGained);
        }

        /**
         * Adjust account for debit.
         * @param pEvent the event causing the debit
         */
        @Override
        protected void adjustForDebit(final Event pEvent) {
            /* Adjust for debit */
            if (pEvent.getUnits() != null) {
                theUnits.subtractUnits(pEvent.getUnits());
            }
        }

        /**
         * Adjust account for credit.
         * @param pEvent the event causing the credit
         */
        @Override
        protected void adjustForCredit(final Event pEvent) {
            /* Adjust for credit */
            if (pEvent.getUnits() != null) {
                theUnits.addUnits(pEvent.getUnits());
            }
        }

        /**
         * Create a Save Point.
         */
        @Override
        protected void createSavePoint() {
            /* Create a save of the values */
            theSavePoint = new AssetAccount(this);
        }

        /**
         * Restore a Save Point.
         */
        @Override
        protected void restoreSavePoint() {
            /* If we have a Save point */
            if (theSavePoint != null) {
                /* Restore original value */
                setValue(new Money(theSavePoint.getValue()));

                /* Initialise the Money values */
                theUnits = new Units(theSavePoint.getUnits());
                theCost = new Money(theSavePoint.getCost());
                theGained = new Money(theSavePoint.getGained());
                theInvested = new Money(theSavePoint.getInvested());
                theDividend = new Money(theSavePoint.getDividend());
                theGains = new Money(theSavePoint.getGains());

                /* Copy price if available */
                if (theSavePoint.getPrice() != null) {
                    thePrice = new Price(theSavePoint.getPrice());
                }

                /* Trim back the capital events */
                theEvents.purgeAfterDate(getDate());
            }
        }
    }

    /**
     * The ExternalAccount Bucket class.
     */
    public static final class ExternalAccount extends ActDetail {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                ExternalAccount.class.getSimpleName(), ActDetail.FIELD_DEFS);

        /**
         * Income Field Id.
         */
        public static final JDataField FIELD_INCOME = FIELD_DEFS.declareLocalField("Income");

        /**
         * Expense Field Id.
         */
        public static final JDataField FIELD_EXPENSE = FIELD_DEFS.declareLocalField("Expense");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_INCOME.equals(pField)) {
                return theIncome;
            }
            if (FIELD_EXPENSE.equals(pField)) {
                return theExpense;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The income.
         */
        private Money theIncome = null;

        /**
         * The expense.
         */
        private Money theExpense = null;

        /**
         * The save point.
         */
        private ExternalAccount theSavePoint = null;

        @Override
        public ExternalAccount getBase() {
            return (ExternalAccount) super.getBase();
        }

        /**
         * Obtain income.
         * @return the income
         */
        public Money getIncome() {
            return theIncome;
        }

        /**
         * Obtain expense.
         * @return the expense
         */
        public Money getExpense() {
            return theExpense;
        }

        /**
         * Obtain previous income.
         * @return the income
         */
        public Money getPrevIncome() {
            return (getBase() != null) ? getBase().getIncome() : null;
        }

        /**
         * Obtain previous expense.
         * @return the expense
         */
        public Money getPrevExpense() {
            return (getBase() != null) ? getBase().getExpense() : null;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pAccount the account
         */
        private ExternalAccount(final BucketList pList,
                                final Account pAccount) {
            /* Call super-constructor */
            super(pList, BucketType.EXTERNALDETAIL, pAccount);

            /* Initialise the money values */
            theIncome = new Money(0);
            theExpense = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pPrevious the previous
         */
        private ExternalAccount(final BucketList pList,
                                final ExternalAccount pPrevious) {
            /* Call super-constructor */
            super(pList, BucketType.EXTERNALDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            theIncome = new Money(0);
            theExpense = new Money(0);

            /* Add the link to the previous item */
            setBase(new ExternalAccount(pPrevious));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pPrevious the previous
         */
        private ExternalAccount(final ExternalAccount pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), BucketType.EXTERNALDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            theIncome = new Money(pPrevious.getIncome());
            theExpense = new Money(pPrevious.getExpense());

            /* Set status */
            setState(DataState.CLEAN);
        }

        @Override
        public boolean isActive() {
            /* Copy if the income or expense is non-zero */
            return (theIncome.isNonZero() || theExpense.isNonZero());
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theIncome.isNonZero() || theExpense.isNonZero()
                    || ((getPrevIncome() != null) && (getPrevIncome().isNonZero())) || ((getPrevExpense() != null) && (getPrevExpense()
                    .isNonZero())));
        }

        /**
         * Adjust account for debit.
         * @param pEvent the event causing the debit
         */
        @Override
        protected void adjustForDebit(final Event pEvent) {
            TransactionType myTransType = pEvent.getTransType();
            Money myAmount = pEvent.getAmount();
            Money myTaxCred = pEvent.getTaxCredit();

            /* If this is a recovered transaction */
            if (myTransType.isRecovered()) {
                /* This is a negative expense */
                theExpense.subtractAmount(myAmount);

                /* else this is a standard income */
            } else {
                /* Adjust for income */
                theIncome.addAmount(myAmount);

                /* If there is a TaxCredit */
                if (myTaxCred != null) {
                    /* Adjust for Tax Credit */
                    theIncome.addAmount(myTaxCred);
                }
            }
        }

        /**
         * Adjust account for credit.
         * @param pEvent the event causing the credit
         */
        @Override
        protected void adjustForCredit(final Event pEvent) {
            /* Adjust for expense */
            theExpense.addAmount(pEvent.getAmount());
        }

        /**
         * Adjust account for tax credit.
         * @param pEvent the event causing the tax credit
         */
        protected void adjustForTaxCredit(final Event pEvent) {
            /* Adjust for expense */
            theExpense.addAmount(pEvent.getTaxCredit());
        }

        /**
         * Adjust account for taxable gain tax credit.
         * @param pEvent the event causing the tax credit
         */
        protected void adjustForTaxGainTaxCredit(final Event pEvent) {
            /* Adjust for expense */
            theIncome.addAmount(pEvent.getTaxCredit());
        }

        /**
         * Create a Save Point.
         */
        @Override
        protected void createSavePoint() {
            /* Create a save of the values */
            theSavePoint = new ExternalAccount(this);
        }

        /**
         * Restore a Save Point.
         */
        @Override
        protected void restoreSavePoint() {
            /* If we have a Save point */
            if (theSavePoint != null) {
                /* Restore original value */
                theIncome = new Money(theSavePoint.getIncome());
                theExpense = new Money(theSavePoint.getExpense());
            }
        }
    }

    /**
     * The AssetSummary Bucket class.
     */
    public static final class AssetSummary extends ActType {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AssetSummary.class.getSimpleName(),
                ActType.FIELD_DEFS);

        /**
         * Value Field Id.
         */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_VALUE.equals(pField)) {
                return theValue;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The value.
         */
        private Money theValue = null;

        @Override
        public AssetSummary getBase() {
            return (AssetSummary) super.getBase();
        }

        /**
         * Obtain value.
         * @return the value
         */
        public Money getValue() {
            return theValue;
        }

        /**
         * Obtain previous value.
         * @return the value
         */
        public Money getPrevValue() {
            return getBase().getValue();
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pAccountType the account type
         */
        private AssetSummary(final BucketList pList,
                             final AccountType pAccountType) {
            /* Call super-constructor */
            super(pList, pAccountType);

            /* Initialise the Money values */
            theValue = new Money(0);

            /* Create a new base for this total */
            setBase(new AssetSummary(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pMaster the master
         */
        private AssetSummary(final AssetSummary pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), pMaster.getAccountType());

            /* Initialise the Money values */
            theValue = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
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
        protected void addValues(final ValueAccount pBucket) {
            ValueAccount myPrevious = pBucket.getBase();

            /* the total */
            theValue.addAmount(pBucket.getValue());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                getBase().addValues(myPrevious);
            }
        }
    }

    /**
     * The AssetTotal Bucket class.
     */
    public static final class AssetTotal extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AssetTotal.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /**
         * Value Field Id.
         */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        /**
         * Profit Field Id.
         */
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_VALUE.equals(pField)) {
                return theValue;
            }
            if (FIELD_PROFIT.equals(pField)) {
                return theProfit;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The value.
         */
        private Money theValue = null;

        /**
         * The profit.
         */
        private Money theProfit = null;

        @Override
        public AssetTotal getBase() {
            return (AssetTotal) super.getBase();
        }

        /**
         * Obtain value.
         * @return the value
         */
        public Money getValue() {
            return theValue;
        }

        /**
         * Obtain profit.
         * @return the profit
         */
        public Money getProfit() {
            return theProfit;
        }

        /**
         * Obtain previous value.
         * @return the value
         */
        public Money getPrevValue() {
            return getBase().getValue();
        }

        /**
         * Constructor.
         * @param pList the list
         */
        private AssetTotal(final BucketList pList) {
            /* Call super-constructor */
            super(pList, BucketType.ASSETTOTAL, 0);

            /* Initialise the Money values */
            theValue = new Money(0);

            /* Create a new base for this total */
            setBase(new AssetTotal(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pMaster the master total
         */
        private AssetTotal(final AssetTotal pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), BucketType.ASSETTOTAL, 0);

            /* Initialise the Money values */
            theValue = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
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
            AssetSummary myPrevious = pBucket.getBase();

            /* the total */
            theValue.addAmount(pBucket.getValue());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                getBase().addValues(myPrevious);
            }
        }

        /**
         * Calculate profit.
         */
        protected void calculateProfit() {
            theProfit = new Money(theValue);
            if (getBase() != null) {
                theProfit.subtractAmount(getPrevValue());
            }
        }
    }

    /**
     * The ExternalTotal Bucket class.
     */
    public static final class ExternalTotal extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ExternalTotal.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

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

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

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
            return super.getFieldValue(pField);
        }

        /**
         * The income.
         */
        private Money theIncome = null;

        /**
         * The expense.
         */
        private Money theExpense = null;

        /**
         * The profit.
         */
        private Money theProfit = null;

        @Override
        public ExternalTotal getBase() {
            return (ExternalTotal) super.getBase();
        }

        /**
         * Obtain income.
         * @return the income
         */
        public Money getIncome() {
            return theIncome;
        }

        /**
         * Obtain expense.
         * @return the expense
         */
        public Money getExpense() {
            return theExpense;
        }

        /**
         * Obtain profit.
         * @return the profit
         */
        public Money getProfit() {
            return theProfit;
        }

        /**
         * Obtain previous income.
         * @return the income
         */
        public Money getPrevIncome() {
            return getBase().getIncome();
        }

        /**
         * Obtain previous expense.
         * @return the expense
         */
        public Money getPrevExpense() {
            return getBase().getExpense();
        }

        /**
         * Obtain previous profit.
         * @return the profit
         */
        public Money getPrevProfit() {
            return getBase().getProfit();
        }

        /**
         * Constructor.
         * @param pList the list
         */
        private ExternalTotal(final BucketList pList) {
            /* Call super-constructor */
            super(pList, BucketType.EXTERNALTOTAL, 0);

            /* Initialise the Money values */
            theIncome = new Money(0);
            theExpense = new Money(0);

            /* Create a new base for this total */
            setBase(new ExternalTotal(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pMaster the master
         */
        private ExternalTotal(final ExternalTotal pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), BucketType.EXTERNALTOTAL, 0);

            /* Initialise the Money values */
            theIncome = new Money(0);
            theExpense = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
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
        protected void addValues(final ExternalAccount pBucket) {
            ExternalAccount myPrevious = pBucket.getBase();

            /* Add the values */
            theIncome.addAmount(pBucket.getIncome());
            theExpense.addAmount(pBucket.getExpense());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                /* Add previous values */
                getBase().addValues(myPrevious);
            }
        }

        /**
         * Calculate profit.
         */
        protected void calculateProfit() {
            theProfit = new Money(theIncome);
            theProfit.subtractAmount(theExpense);
            if (getBase() != null) {
                getBase().calculateProfit();
            }
        }
    }

    /**
     * The MarketTotal Bucket class.
     */
    public static final class MarketTotal extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ExternalTotal.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

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
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

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
        private Money theCost = null;

        /**
         * The value.
         */
        private Money theValue = null;

        /**
         * The gained.
         */
        private Money theGained = null;

        /**
         * The profit.
         */
        private Money theProfit = null;

        @Override
        public MarketTotal getBase() {
            return (MarketTotal) super.getBase();
        }

        /**
         * Obtain cost.
         * @return the cost
         */
        public Money getCost() {
            return theCost;
        }

        /**
         * Obtain gained.
         * @return the gained
         */
        public Money getGained() {
            return theGained;
        }

        /**
         * Obtain value.
         * @return the value
         */
        public Money getValue() {
            return theValue;
        }

        /**
         * Obtain profit.
         * @return the profit
         */
        public Money getProfit() {
            return theProfit;
        }

        /**
         * Constructor.
         * @param pList the list
         */
        private MarketTotal(final BucketList pList) {
            /* Call super-constructor */
            super(pList, BucketType.MARKETTOTAL, 0);

            /* Initialise the Money values */
            theCost = new Money(0);
            theValue = new Money(0);
            theGained = new Money(0);
            theProfit = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
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
        protected void addValues(final AssetAccount pBucket) {
            theCost.addAmount(pBucket.getCost());
            theGained.addAmount(pBucket.getGained());
            theProfit.addAmount(pBucket.getProfit());
            theValue.addAmount(pBucket.getValue());
        }
    }

    /**
     * The Transaction Detail Bucket class.
     */
    public static final class TransDetail extends TransType {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransDetail.class.getSimpleName(),
                TransType.FIELD_DEFS);

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * TaxCredit Field Id.
         */
        public static final JDataField FIELD_TAXCREDIT = FIELD_DEFS.declareLocalField("TaxCredit");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_TAXCREDIT.equals(pField)) {
                return theTaxCredit;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private Money theAmount = null;

        /**
         * The tax credit.
         */
        private Money theTaxCredit = null;

        @Override
        public TransDetail getBase() {
            return (TransDetail) super.getBase();
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public Money getAmount() {
            return theAmount;
        }

        /**
         * Obtain the tax credit.
         * @return the tax credit
         */
        public Money getTaxCredit() {
            return theTaxCredit;
        }

        /**
         * Obtain the previous amount.
         * @return the amount
         */
        public Money getPrevAmount() {
            return (getBase() != null) ? getBase().getAmount() : null;
        }

        /**
         * Obtain the previous tax credit.
         * @return the tax credit
         */
        public Money getPrevTax() {
            return (getBase() != null) ? getBase().getTaxCredit() : null;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pTransType the transaction type
         */
        private TransDetail(final BucketList pList,
                            final TransactionType pTransType) {
            /* Call super-constructor */
            super(pList, pTransType);

            /* Initialise the Money values */
            theAmount = new Money(0);
            theTaxCredit = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pPrevious the previous
         */
        private TransDetail(final BucketList pList,
                            final TransDetail pPrevious) {
            /* Call super-constructor */
            super(pList, pPrevious.getTransType());

            /* Initialise the Money values */
            theAmount = new Money(0);
            theTaxCredit = new Money(0);

            /* Add the link to the previous item */
            setBase(new TransDetail(pPrevious));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pPrevious the previous
         */
        private TransDetail(final TransDetail pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), pPrevious.getTransType());

            /* Initialise the Money values */
            theAmount = new Money(pPrevious.getAmount());
            theTaxCredit = new Money(pPrevious.getTaxCredit());

            /* Set status */
            setState(DataState.CLEAN);
        }

        @Override
        public boolean isActive() {
            /* Copy if the amount is non-zero */
            return theAmount.isNonZero();
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theAmount.isNonZero() || ((getPrevAmount() != null) && (getPrevAmount().isNonZero())));
        }

        /**
         * Adjust account for transaction.
         * @param pEvent the source event
         */
        protected void adjustAmount(final Event pEvent) {
            /* Adjust for transaction */
            theAmount.addAmount(pEvent.getAmount());

            /* Adjust for tax credit */
            if (pEvent.getTaxCredit() != null) {
                theTaxCredit.addAmount(pEvent.getTaxCredit());
            }
        }

        /**
         * Adjust account for tax credit.
         * @param pEvent the source event
         */
        protected void adjustForTaxCredit(final Event pEvent) {
            /* Adjust for tax credit */
            theAmount.addAmount(pEvent.getTaxCredit());
        }
    }

    /**
     * The Transaction Summary Bucket class.
     */
    public static final class TransSummary extends Tax {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransSummary.class.getSimpleName(),
                Tax.FIELD_DEFS);

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private Money theAmount = null;

        @Override
        public TransSummary getBase() {
            return (TransSummary) super.getBase();
        }

        /**
         * Obtain the amount.
         * @return the amount.
         */
        public Money getAmount() {
            return theAmount;
        }

        /**
         * Obtain the previous amount.
         * @return the amount.
         */
        public Money getPrevAmount() {
            return getBase().getAmount();
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pTaxType the tax type
         */
        private TransSummary(final BucketList pList,
                             final TaxType pTaxType) {
            /* Call super-constructor */
            super(pList, pTaxType);

            /* Initialise the Money values */
            theAmount = new Money(0);

            /* Create a new base for this total */
            setBase(new TransSummary(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pPrevious the previous
         */
        private TransSummary(final TransSummary pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), pPrevious.getTaxType());

            /* Initialise the Money values */
            theAmount = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
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
        protected void addValues(final TransDetail pBucket) {
            TransDetail myPrevious = pBucket.getBase();

            /* Add the values */
            theAmount.addAmount(pBucket.getAmount());
            theAmount.addAmount(pBucket.getTaxCredit());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                /* Add previous values */
                getBase().addValues(myPrevious);
            }
        }

        /**
         * Subtract values from the total value.
         * @param pBucket the bucket
         */
        protected void subtractValues(final TransDetail pBucket) {
            TransDetail myPrevious = pBucket.getBase();

            /* Add the values */
            theAmount.subtractAmount(pBucket.getAmount());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                /* Add previous values */
                getBase().subtractValues(myPrevious);
            }
        }
    }

    /**
     * The Transaction Total Bucket class.
     */
    public static final class TransTotal extends Tax {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransTotal.class.getSimpleName(),
                Tax.FIELD_DEFS);

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private Money theAmount = null;

        @Override
        public TransTotal getBase() {
            return (TransTotal) super.getBase();
        }

        /**
         * Obtain amount.
         * @return the amount
         */
        public Money getAmount() {
            return theAmount;
        }

        /**
         * Obtain previous amount.
         * @return the amount
         */
        public Money getPrevAmount() {
            return getBase().getAmount();
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pTaxType the tax type
         */
        private TransTotal(final BucketList pList,
                           final TaxType pTaxType) {
            /* Call super-constructor */
            super(pList, pTaxType);

            /* Initialise the Money values */
            theAmount = new Money(0);

            /* Create a new base for this total */
            setBase(new TransTotal(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pMaster the master
         */
        private TransTotal(final TransTotal pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), pMaster.getTaxType());

            /* Initialise the Money values */
            theAmount = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
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
        protected void addValues(final TransSummary pBucket) {
            TransSummary myPrevious = pBucket.getBase();

            /* Add the values */
            theAmount.addAmount(pBucket.getAmount());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                /* Add previous values */
                getBase().addValues(myPrevious);
            }
        }

        /**
         * Subtract values from the total value.
         * @param pBucket the bucket
         */
        protected void subtractValues(final TransSummary pBucket) {
            TransSummary myPrevious = pBucket.getBase();

            /* Add the values */
            theAmount.subtractAmount(pBucket.getAmount());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                /* Add previous values */
                getBase().subtractValues(myPrevious);
            }
        }
    }

    /**
     * The Taxation Detail Bucket class.
     */
    public static final class TaxDetail extends Tax {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxDetail.class.getSimpleName(),
                Tax.FIELD_DEFS);

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * Taxation Field Id.
         */
        public static final JDataField FIELD_TAXATION = FIELD_DEFS.declareLocalField("Taxation");

        /**
         * Rate Field Id.
         */
        public static final JDataField FIELD_RATE = FIELD_DEFS.declareLocalField("Rate");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_TAXATION.equals(pField)) {
                return theTaxation;
            }
            if (FIELD_RATE.equals(pField)) {
                return theRate;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private Money theAmount = null;

        /**
         * The taxation.
         */
        private Money theTaxation = null;

        /**
         * The rate.
         */
        private Rate theRate = null;

        /**
         * The parent.
         */
        private TaxDetail theParent = null;

        @Override
        public TaxDetail getBase() {
            return (TaxDetail) super.getBase();
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public Money getAmount() {
            return theAmount;
        }

        /**
         * Obtain the taxation.
         * @return the taxation
         */
        public Money getTaxation() {
            return theTaxation;
        }

        /**
         * Obtain the rate.
         * @return the rate
         */
        public Rate getRate() {
            return theRate;
        }

        /**
         * Obtain the parent.
         * @return the parent
         */
        public TaxDetail getParent() {
            return theParent;
        }

        /**
         * Obtain the previous amount.
         * @return the amount
         */
        public Money getPrevAmount() {
            return (getBase() != null) ? getBase().getAmount() : null;
        }

        /**
         * Obtain the previous taxation.
         * @return the taxation
         */
        public Money getPrevTax() {
            return (getBase() != null) ? getBase().getTaxation() : null;
        }

        /**
         * Obtain the previous rate.
         * @return the rate
         */
        public Rate getPrevRate() {
            return (getBase() != null) ? getBase().getRate() : null;
        }

        /**
         * Constructor.
         * @param pList the list
         * @param pTaxType the tax type
         */
        private TaxDetail(final BucketList pList,
                          final TaxType pTaxType) {
            /* Call super-constructor */
            super(pList, pTaxType);

            /* Add the link to the previous item */
            setBase(new TaxDetail(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Constructor.
         * @param pMaster the master
         */
        private TaxDetail(final TaxDetail pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), pMaster.getTaxType());

            /* Set status */
            setState(DataState.CLEAN);
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theAmount.isNonZero() || theTaxation.isNonZero()
                    || ((getPrevAmount() != null) && (getPrevAmount().isNonZero())) || ((getPrevTax() != null) && (getPrevTax()
                    .isNonZero())));
        }

        /**
         * Set a taxation amount and calculate the tax on it.
         * @param pAmount Amount to set
         * @return the taxation on this bucket
         */
        protected Money setAmount(final Money pAmount) {
            /* Set the value */
            theAmount = new Money(pAmount);

            /* Calculate the tax if we have a rate */
            theTaxation = (theRate != null) ? theAmount.valueAtRate(theRate) : new Money(0);

            /* Return the taxation amount */
            return theTaxation;
        }

        /**
         * Set explicit taxation value.
         * @param pAmount Amount to set
         */
        protected void setTaxation(final Money pAmount) {
            /* Set the value */
            theTaxation = new Money(pAmount);
        }

        /**
         * Set parent bucket for reporting purposes.
         * @param pParent the parent bucket
         */
        protected void setParent(final TaxDetail pParent) {
            /* Set the value */
            theParent = pParent;
        }

        /**
         * Set a tax rate.
         * @param pRate Amount to set
         */
        protected void setRate(final Rate pRate) {
            /* Set the value */
            theRate = pRate;
        }
    }

    /**
     * Bucket Types.
     */
    public static enum BucketType {
        /**
         * Money Detail.
         */
        MONEYDETAIL(1000),

        /**
         * Asset Detail.
         */
        ASSETDETAIL(1000),

        /**
         * Debt Detail.
         */
        DEBTDETAIL(1000),

        /**
         * External Detail.
         */
        EXTERNALDETAIL(1000),

        /**
         * Asset Summary.
         */
        ASSETSUMMARY(100),

        /**
         * Asset Total.
         */
        ASSETTOTAL(1),

        /**
         * Market Total.
         */
        MARKETTOTAL(2),

        /**
         * External total.
         */
        EXTERNALTOTAL(3),

        /**
         * Transaction detail.
         */
        TRANSDETAIL(200),

        /**
         * Transaction Summary.
         */
        TRANSSUMMARY(300),

        /**
         * Transaction Total.
         */
        TRANSTOTAL(300),

        /**
         * Tax Detail.
         */
        TAXDETAIL(300),

        /**
         * Tax Summary.
         */
        TAXSUMMARY(300),

        /**
         * Tax Total.
         */
        TAXTOTAL(300);

        /**
         * The id shift.
         */
        private final int theShift;

        /**
         * Get the Id shift for this BucketType.
         * @return the id shift
         */
        private int getIdShift() {
            return theShift;
        }

        /**
         * Constructor.
         * @param pShift he id shift
         */
        private BucketType(final int pShift) {
            theShift = pShift;
        }

        /**
         * Get the BucketType for this Account.
         * @param pAccount the account
         * @return the Bucket type
         */
        private static BucketType getActBucketType(final Account pAccount) {
            /* If this is a external/benefit */
            if (pAccount.isExternal() || pAccount.isBenefit()) {
                return EXTERNALDETAIL;
            } else if (pAccount.isMoney()) {
                return MONEYDETAIL;
            } else if (pAccount.isPriced()) {
                return ASSETDETAIL;
            } else {
                return DEBTDETAIL;
            }
        }

        /**
         * Get the BucketType for this TaxType.
         * @param pTaxType the tax type
         * @return the id shift
         */
        private static BucketType getTaxBucketType(final TaxType pTaxType) {
            TaxBucket myBucket = pTaxType.getTaxClass().getClassBucket();
            switch (myBucket) {
                case TRANSTOTAL:
                    return TRANSTOTAL;
                case TAXDETAIL:
                    return TAXDETAIL;
                case TAXSUMM:
                    return TAXSUMMARY;
                case TAXTOTAL:
                    return TAXTOTAL;
                default:
                    return TRANSSUMMARY;
            }
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
