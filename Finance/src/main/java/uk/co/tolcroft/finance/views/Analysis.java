/*******************************************************************************
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
import uk.co.tolcroft.finance.data.AccountRate;
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

public class Analysis implements JDataContents {
    /**
     * Report fields
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(Analysis.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /* Field IDs */
    public static final JDataField FIELD_STATE = FIELD_DEFS.declareLocalField("State");
    public static final JDataField FIELD_LIST = FIELD_DEFS.declareLocalField("BucketList");
    public static final JDataField FIELD_CHARGES = FIELD_DEFS.declareLocalField("Charges");
    public static final JDataField FIELD_TAXYEAR = FIELD_DEFS.declareLocalField("TaxYear");
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

    @Override
    public Object getFieldValue(JDataField pField) {
        if (pField == FIELD_STATE)
            return theAnalysisState;
        if (pField == FIELD_LIST)
            return theList;
        if (pField == FIELD_CHARGES)
            return theCharges;
        if (pField == FIELD_TAXYEAR)
            return (theYear == null) ? JDataObject.FIELD_SKIP : theYear;
        if (pField == FIELD_DATE)
            return theDate;
        if (pField == FIELD_ACCOUNT)
            return (theAccount == null) ? JDataObject.FIELD_SKIP : theAccount;
        return null;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /* Members */
    private FinanceData theData = null;
    private AnalysisState theAnalysisState = AnalysisState.RAW;
    private BucketList theList = null;
    private ChargeableEventList theCharges = null;
    private TaxYear theYear = null;
    private DateDay theDate = null;
    private Account theAccount = null;
    private boolean hasGainsSlices = false;
    private boolean hasReducedAllow = false;
    private int theAge = 0;

    /* Access methods */
    public FinanceData getData() {
        return theData;
    }

    public AnalysisState getState() {
        return theAnalysisState;
    }

    public BucketList getList() {
        return theList;
    }

    public TaxYear getTaxYear() {
        return theYear;
    }

    public DateDay getDate() {
        return theDate;
    }

    public Account getAccount() {
        return theAccount;
    }

    public ChargeableEventList getCharges() {
        return theCharges;
    }

    public boolean hasReducedAllow() {
        return hasReducedAllow;
    }

    public boolean hasGainsSlices() {
        return hasGainsSlices;
    }

    public int getAge() {
        return theAge;
    }

    /* Set methods */
    protected void setState(AnalysisState pState) {
        theAnalysisState = pState;
    }

    protected void setAge(int pAge) {
        theAge = pAge;
    }

    protected void setHasReducedAllow(boolean hasReduced) {
        hasReducedAllow = hasReduced;
    }

    protected void setHasGainsSlices(boolean hasSlices) {
        hasGainsSlices = hasSlices;
    }

    /**
     * Constructor for a dated analysis
     * @param pData the data to analyse events for
     * @param pDate the Date for the analysis
     */
    public Analysis(FinanceData pData,
                    DateDay pDate) {
        /* Store the data */
        theData = pData;
        theDate = pDate;

        /* Create a new list */
        theList = new BucketList(this);
        theCharges = new ChargeableEventList();
    }

    /**
     * Constructor for a dated account analysis
     * @param pData the data to analyse events for
     * @param pAccount the account to analyse
     * @param pDate the Date for the analysis
     */
    public Analysis(FinanceData pData,
                    Account pAccount,
                    DateDay pDate) {
        /* Store the data */
        theData = pData;
        theDate = pDate;
        theAccount = pAccount;

        /* Create a new list */
        theList = new BucketList(this);
        theCharges = new ChargeableEventList();
    }

    /**
     * Constructor for a dated account analysis
     * @param pData the data to analyse events for
     * @param pYear the year to analyse
     * @param pAnalysis the previous year analysis (if present)
     */
    public Analysis(FinanceData pData,
                    TaxYear pYear,
                    Analysis pAnalysis) {
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

        /* Create a new list */
        theList = new BucketList(this);
        theCharges = new ChargeableEventList();

        /* Return if we are the first analysis */
        if (pAnalysis == null)
            return;

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
            }
        }
    }

    /* The core AnalysisBucket Class */
    protected static abstract class AnalysisBucket extends DataItem<AnalysisBucket> {
        /**
         * Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AnalysisBucket.class.getSimpleName(),
                DataItem.FIELD_DEFS);

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /* Field IDs */
        public static final JDataField FIELD_BUCKETTYPE = FIELD_DEFS.declareEqualityField("BucketType");
        public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_BUCKETTYPE)
                return theBucketType;
            if (pField == FIELD_DATE)
                return theDate;

            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        /* Members */
        private BucketType theBucketType = null;
        private FinanceData theData = null;
        private DateDay theDate = null;

        /* Access methods */
        public BucketType getBucketType() {
            return theBucketType;
        }

        protected FinanceData getData() {
            return theData;
        }

        protected DateDay getDate() {
            return theDate;
        }

        /* Constructor */
        public AnalysisBucket(BucketList pList,
                              BucketType pType,
                              int uId) {
            /* Call super-constructor */
            super(pList, uId + pType.getIdShift());
            theData = pList.theAnalysis.theData;
            theDate = pList.theAnalysis.theDate;

            /* Store the bucket type */
            theBucketType = pType;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * 
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat)
                return 0;
            if (pThat == null)
                return -1;

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket))
                return -1;

            /* Access the object as am Analysis Bucket */
            AnalysisBucket myThat = (AnalysisBucket) pThat;

            /* Compare the bucket order */
            result = getBucketType().compareTo(myThat.getBucketType());
            return result;
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         * @return TRUE/FALSE
         */
        protected abstract boolean isRelevant();
    }

    /* The List class */
    public static class BucketList extends DataList<BucketList, AnalysisBucket> {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(BucketList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_ANALYSIS)
                return theAnalysis;
            return super.getFieldValue(pField);
        }

        /* Members */
        private final Analysis theAnalysis;
        private final FinanceData theData;

        /**
         * The name of the object
         */
        private static final String listName = "AnalysisBuckets";

        @Override
        public String listName() {
            return listName;
        }

        /**
         * Construct a top-level List
         * @param pAnalysis the analysis
         */
        public BucketList(Analysis pAnalysis) {
            super(BucketList.class, AnalysisBucket.class, ListStyle.VIEW, false);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
        }

        /* Obtain extract lists. */
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
        public BucketList getDeepCopy(DataSet<?> pData) {
            return null;
        }

        @Override
        public BucketList getDifferences(BucketList pOld) {
            return null;
        }

        /**
         * Add a new item to the list
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public AnalysisBucket addNewItem(DataItem<?> pItem) {
            return null;
        }

        /**
         * Add a new item to the edit list
         * @return the newly added item
         */
        @Override
        public AnalysisBucket addNewItem() {
            return null;
        }

        /**
         * Obtain the AccountDetail Bucket for a given account
         * @param pAccount the account
         * @return the bucket
         */
        protected ActDetail getAccountDetail(Account pAccount) {
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
         * Obtain the Asset Summary Bucket for a given account type
         * @param pActType the account type
         * @return the bucket
         */
        protected AssetSummary getAssetSummary(AccountType pActType) {
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
         * Obtain the Transaction Detail Bucket for a given transaction class
         * @param pTransClass the transaction class
         * @return the bucket
         */
        protected TransDetail getTransDetail(TransClass pTransClass) {
            /* Calculate the id that we are looking for */
            TransactionType myTrans = theData.getTransTypes().searchFor(pTransClass);

            /* Return the bucket */
            return getTransDetail(myTrans);
        }

        /**
         * Obtain the Transaction Detail Bucket for a given transaction type
         * @param pTransType the transaction type
         * @return the bucket
         */
        protected TransDetail getTransDetail(TransactionType pTransType) {
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
         * Obtain the Transaction Summary Bucket for a given tax type
         * @param pTaxClass the taxation class
         * @return the bucket
         */
        protected TransSummary getTransSummary(TaxClass pTaxClass) {
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
         * Obtain the Taxation Detail Bucket for a given tax type
         * @param pTaxClass the taxation class
         * @return the bucket
         */
        protected TaxDetail getTaxDetail(TaxClass pTaxClass) {
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
         * Obtain the Asset Total Bucket
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
         * Obtain the External Total Bucket
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
         * Obtain the Market Total Bucket
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
         * Obtain the Transaction Total Bucket
         * @param pTaxClass the taxation class
         * @return the bucket
         */
        protected TransTotal getTransTotal(TaxClass pTaxClass) {
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
         * Prune the list to remove irrelevant items
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
                        if (!myCurr.isRelevant())
                            myIterator.remove();
                        break;
                }
            }
        }
    }

    /* The Account Bucket class */
    protected static abstract class ActDetail extends AnalysisBucket {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ActDetail.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField("Account");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_ACCOUNT)
                return theAccount;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Account theAccount = null;

        /* Access methods */
        public String getName() {
            return theAccount.getName();
        }

        public Account getAccount() {
            return theAccount;
        }

        public AccountType getAccountType() {
            return theAccount.getActType();
        }

        /* Constructor */
        private ActDetail(BucketList pList,
                          BucketType pType,
                          Account pAccount) {
            /* Call super-constructor */
            super(pList, pType, pAccount.getId());

            /* Store the account */
            theAccount = pAccount;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * 
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat)
                return 0;
            if (pThat == null)
                return -1;

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket))
                return -1;

            /* Access the object as an Analysis Bucket */
            AnalysisBucket myBucket = (AnalysisBucket) pThat;

            /* Compare the bucket types */
            result = super.compareTo(myBucket);
            if (result != 0)
                return result;

            /* Access the object as an Act Bucket */
            ActDetail myThat = (ActDetail) pThat;

            /* Compare the Accounts */
            result = getAccount().compareTo(myThat.getAccount());
            return result;
        }

        /**
         * Adjust account for debit
         * @param pEvent the event causing the debit
         */
        protected abstract void adjustForDebit(Event pEvent);

        /**
         * Adjust account for credit
         * @param pEvent the event causing the credit
         */
        protected abstract void adjustForCredit(Event pEvent);

        /**
         * Create a save point
         */
        protected abstract void createSavePoint();

        /**
         * Restore a save point
         */
        protected abstract void restoreSavePoint();
    }

    /* The Account Type Bucket class */
    private static abstract class ActType extends AnalysisBucket {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ActType.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_ACTTYPE = FIELD_DEFS.declareEqualityField("AccountType");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_ACTTYPE)
                return theAccountType;
            return super.getFieldValue(pField);
        }

        /* Members */
        private AccountType theAccountType = null;

        /* Access methods */
        public String getName() {
            return theAccountType.getName();
        }

        public AccountType getAccountType() {
            return theAccountType;
        }

        /* Constructor */
        private ActType(BucketList pList,
                        AccountType pAccountType) {
            /* Call super-constructor */
            super(pList, BucketType.ASSETSUMMARY, pAccountType.getId());

            /* Store the account type */
            theAccountType = pAccountType;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * 
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat)
                return 0;
            if (pThat == null)
                return -1;

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket))
                return -1;

            /* Access the object as an Analysis Bucket */
            AnalysisBucket myBucket = (AnalysisBucket) pThat;

            /* Compare the bucket types */
            result = super.compareTo(myBucket);
            if (result != 0)
                return result;

            /* Access the object as an ActType Bucket */
            ActType myThat = (ActType) pThat;

            /* Compare the AccountTypes */
            result = getAccountType().compareTo(myThat.getAccountType());
            return result;
        }
    }

    /* The TransType Bucket class */
    private static abstract class TransType extends AnalysisBucket {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransType.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_TRANSTYPE = FIELD_DEFS.declareEqualityField("TransactionType");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_TRANSTYPE)
                return theTransType;
            return super.getFieldValue(pField);
        }

        /* Members */
        private TransactionType theTransType = null;

        /* Access methods */
        public String getName() {
            return theTransType.getName();
        }

        public TransactionType getTransType() {
            return theTransType;
        }

        /* Constructor */
        private TransType(BucketList pList,
                          TransactionType pTransType) {
            /* Call super-constructor */
            super(pList, BucketType.TRANSDETAIL, pTransType.getId());

            /* Store the transaction type */
            theTransType = pTransType;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * 
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat)
                return 0;
            if (pThat == null)
                return -1;

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket))
                return -1;

            /* Access the object as an Analysis Bucket */
            AnalysisBucket myBucket = (AnalysisBucket) pThat;

            /* Compare the bucket types */
            result = super.compareTo(myBucket);
            if (result != 0)
                return result;

            /* Access the object as an TransType Bucket */
            TransType myThat = (TransType) pThat;

            /* Compare the TransactionTypes */
            result = getTransType().compareTo(myThat.getTransType());
            return result;
        }
    }

    /* The Tax Bucket class */
    private static abstract class Tax extends AnalysisBucket {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(Tax.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_TAXTYPE = FIELD_DEFS.declareEqualityField("TaxType");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_TAXTYPE)
                return theTaxType;
            return super.getFieldValue(pField);
        }

        /* Members */
        private TaxType theTaxType = null;

        /* Access methods */
        public String getName() {
            return theTaxType.getName();
        }

        public TaxType getTaxType() {
            return theTaxType;
        }

        /* Constructor */
        private Tax(BucketList pList,
                    TaxType pTaxType) {
            /* Call super-constructor */
            super(pList, BucketType.getTaxBucketType(pTaxType), pTaxType.getId());

            /* Store the tax type */
            theTaxType = pTaxType;
        }

        /**
         * Compare this Bucket to another to establish sort order.
         * 
         * @param pThat The Bucket to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        @Override
        public int compareTo(Object pThat) {
            int result;

            /* Handle the trivial cases */
            if (this == pThat)
                return 0;
            if (pThat == null)
                return -1;

            /* Make sure that the object is an Analysis Bucket */
            if (!(pThat instanceof AnalysisBucket))
                return -1;

            /* Access the object as an Analysis Bucket */
            AnalysisBucket myBucket = (AnalysisBucket) pThat;

            /* Compare the bucket types */
            result = super.compareTo(myBucket);
            if (result != 0)
                return result;

            /* Access the object as an Tax Bucket */
            Tax myThat = (Tax) pThat;

            /* Compare the TaxTypes */
            result = getTaxType().compareTo(myThat.getTaxType());
            return result;
        }
    }

    /* The ValueAccount Bucket class */
    protected static abstract class ValueAccount extends ActDetail {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ValueAccount.class.getSimpleName(),
                ActDetail.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_VALUE)
                return theValue;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theValue = null;

        /* Override of getBase method */
        @Override
        public ValueAccount getBase() {
            return (ValueAccount) super.getBase();
        }

        /* Access methods */
        public Money getValue() {
            return theValue;
        }

        public Money getPrevValue() {
            return (getBase() != null) ? getBase().getValue() : null;
        }

        protected void setValue(Money pValue) {
            theValue = pValue;
        }

        /* Constructor */
        private ValueAccount(BucketList pList,
                             BucketType pType,
                             Account pAccount) {
            /* Call super-constructor */
            super(pList, pType, pAccount);

            /* Initialise the money values */
            theValue = new Money(0);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            /* Copy if the value is non-zero */
            return theValue.isNonZero();
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theValue.isNonZero() || ((getPrevValue() != null) && (getPrevValue().isNonZero())));
        }

        /**
         * Adjust account for debit
         * @param pEvent the event causing the debit
         */
        @Override
        protected void adjustForDebit(Event pEvent) {
            /* Adjust for debit */
            theValue.subtractAmount(pEvent.getAmount());
        }

        /**
         * Adjust account for credit
         * @param pEvent the event causing the credit
         */
        @Override
        protected void adjustForCredit(Event pEvent) {
            /* Adjust for credit */
            theValue.addAmount(pEvent.getAmount());
        }
    }

    /* The MoneyAccount Bucket class */
    public static class MoneyAccount extends ValueAccount {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(MoneyAccount.class.getSimpleName(),
                ValueAccount.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_RATE = FIELD_DEFS.declareLocalField("Rate");
        public static final JDataField FIELD_MATURITY = FIELD_DEFS.declareLocalField("Maturity");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_RATE)
                return theRate;
            if (pField == FIELD_MATURITY)
                return theMaturity;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Rate theRate = null;
        private DateDay theMaturity = null;
        private MoneyAccount theSavePoint = null;

        /* Override of getBase method */
        @Override
        public MoneyAccount getBase() {
            return (MoneyAccount) super.getBase();
        }

        /* Access methods */
        public Rate getRate() {
            return theRate;
        }

        public DateDay getMaturity() {
            return theMaturity;
        }

        /* Constructor */
        private MoneyAccount(BucketList pList,
                             Account pAccount) {
            /* Call super-constructor */
            super(pList, BucketType.MONEYDETAIL, pAccount);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private MoneyAccount(BucketList pList,
                             MoneyAccount pPrevious) {
            /* Call super-constructor */
            super(pList, BucketType.MONEYDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));

            /* Add the link to the previous item */
            setBase(new MoneyAccount(pPrevious));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private MoneyAccount(MoneyAccount pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), BucketType.MONEYDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));
            if (pPrevious.getRate() != null)
                theRate = new Rate(pPrevious.getRate());
            if (pPrevious.getMaturity() != null)
                theMaturity = new DateDay(pPrevious.getMaturity());

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * record the rate of the account at a given date
         * @param pDate the date of valuation
         */
        protected void recordRate(DateDay pDate) {
            AccountRate.AccountRateList myRates = getData().getRates();
            AccountRate myRate;
            DateDay myDate;

            /* Obtain the appropriate price record */
            myRate = myRates.getLatestRate(getAccount(), getDate());
            myDate = getAccount().getMaturity();

            /* If we have a rate */
            if (myRate != null) {
                /* Use Rate date instead */
                if (myDate == null)
                    myDate = myRate.getDate();

                /* Store the rate */
                theRate = myRate.getRate();
            }

            /* Store the maturity */
            theMaturity = myDate;
        }

        /**
         * Create a Save Point
         */
        @Override
        protected void createSavePoint() {
            /* Create a save of the values */
            theSavePoint = new MoneyAccount(this);
        }

        /**
         * Restore a Save Point
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

    /* The DebtAccount Bucket class */
    public static class DebtAccount extends ValueAccount {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(DebtAccount.class.getSimpleName(),
                ValueAccount.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_SPEND = FIELD_DEFS.declareLocalField("Spend");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_SPEND)
                return theSpend;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theSpend = null;
        private DebtAccount theSavePoint = null;

        /* Override of getBase method */
        @Override
        public DebtAccount getBase() {
            return (DebtAccount) super.getBase();
        }

        /* Access methods */
        public Money getSpend() {
            return theSpend;
        }

        /* Constructor */
        private DebtAccount(BucketList pList,
                            Account pAccount) {
            /* Call super-constructor */
            super(pList, BucketType.DEBTDETAIL, pAccount);

            /* Initialise the money values */
            theSpend = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private DebtAccount(BucketList pList,
                            DebtAccount pPrevious) {
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

        /* Constructor */
        private DebtAccount(DebtAccount pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), BucketType.DEBTDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));
            theSpend = new Money(pPrevious.getSpend());

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * Create a Save Point
         */
        @Override
        protected void createSavePoint() {
            /* Create a save of the values */
            theSavePoint = new DebtAccount(this);
        }

        /**
         * Restore a Save Point
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

    /* The AssetAccount Bucket class */
    public static class AssetAccount extends ValueAccount {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AssetAccount.class.getSimpleName(),
                ValueAccount.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_COST = FIELD_DEFS.declareLocalField("Cost");
        public static final JDataField FIELD_UNITS = FIELD_DEFS.declareLocalField("Units");
        public static final JDataField FIELD_GAINED = FIELD_DEFS.declareLocalField("Gained");
        public static final JDataField FIELD_INVESTED = FIELD_DEFS.declareLocalField("Invested");
        public static final JDataField FIELD_DIVIDEND = FIELD_DEFS.declareLocalField("Cost");
        public static final JDataField FIELD_GAINS = FIELD_DEFS.declareLocalField("Gains");
        public static final JDataField FIELD_PRICE = FIELD_DEFS.declareLocalField("Price");
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_COST)
                return theCost;
            if (pField == FIELD_UNITS)
                return theUnits;
            if (pField == FIELD_GAINED)
                return theGained;
            if (pField == FIELD_INVESTED)
                return theInvested;
            if (pField == FIELD_DIVIDEND)
                return theDividend;
            if (pField == FIELD_GAINS)
                return theGains;
            if (pField == FIELD_PRICE)
                return thePrice;
            if (pField == FIELD_PROFIT)
                return theProfit;
            return super.getFieldValue(pField);
        }

        /* Members */
        private CapitalEventList theEvents = null;
        private Money theCost = null;
        private Units theUnits = null;
        private Money theGained = null;

        private Money theInvested = null;
        private Money theDividend = null;
        private Money theGains = null;

        private Money theProfit = null;
        private Price thePrice = null;
        private AssetAccount theSavePoint = null;

        /* Override of getBase method */
        @Override
        public AssetAccount getBase() {
            return (AssetAccount) super.getBase();
        }

        /* Access methods */
        public Money getCost() {
            return theCost;
        }

        public Units getUnits() {
            return theUnits;
        }

        public Money getGained() {
            return theGained;
        }

        public Money getInvested() {
            return theInvested;
        }

        public Money getDividend() {
            return theDividend;
        }

        public Money getGains() {
            return theGains;
        }

        public Money getProfit() {
            return theProfit;
        }

        public Price getPrice() {
            return thePrice;
        }

        public Money getPrevCost() {
            return (getBase() != null) ? getBase().getCost() : null;
        }

        public Units getPrevUnits() {
            return (getBase() != null) ? getBase().getUnits() : null;
        }

        public Money getPrevGained() {
            return (getBase() != null) ? getBase().getGained() : null;
        }

        public CapitalEventList getCapitalEvents() {
            return theEvents;
        }

        /* Constructor */
        private AssetAccount(BucketList pList,
                             Account pAccount) {
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

        /* Constructor */
        private AssetAccount(BucketList pList,
                             AssetAccount pPrevious) {
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

        /* Constructor */
        private AssetAccount(AssetAccount pPrevious) {
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
            if (pPrevious.getPrice() != null)
                thePrice = new Price(pPrevious.getPrice());

            /* Initialise the Money values */
            setValue(new Money(pPrevious.getValue()));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            /* Copy if the units is non-zero */
            return theUnits.isNonZero();
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theUnits.isNonZero() || ((getPrevUnits() != null) && (getPrevUnits().isNonZero())));
        }

        /**
         * value the asset at a particular date
         * @param pDate the date of valuation
         */
        protected void valueAsset(DateDay pDate) {
            AccountPrice.AccountPriceList myPrices = getData().getPrices();
            AccountPrice myActPrice;

            /* Obtain the appropriate price record */
            myActPrice = myPrices.getLatestPrice(getAccount(), pDate);

            /* If we found a price */
            if (myActPrice != null) {
                /* Store the price */
                thePrice = myActPrice.getPrice();
            }

            /* else assume zero price */
            else
                thePrice = new Price(0);

            /* Calculate the value */
            setValue(theUnits.valueAtPrice(thePrice));
        }

        /**
         * Calculate profit
         */
        protected void calculateProfit() {
            /* Calculate the profit */
            theProfit = new Money(getValue());
            theProfit.subtractAmount(theCost);
            theProfit.addAmount(theGained);
        }

        /**
         * Adjust account for debit
         * @param pEvent the event causing the debit
         */
        @Override
        protected void adjustForDebit(Event pEvent) {
            /* Adjust for debit */
            if (pEvent.getUnits() != null)
                theUnits.subtractUnits(pEvent.getUnits());
        }

        /**
         * Adjust account for credit
         * @param pEvent the event causing the credit
         */
        @Override
        protected void adjustForCredit(Event pEvent) {
            /* Adjust for credit */
            if (pEvent.getUnits() != null)
                theUnits.addUnits(pEvent.getUnits());
        }

        /**
         * Create a Save Point
         */
        @Override
        protected void createSavePoint() {
            /* Create a save of the values */
            theSavePoint = new AssetAccount(this);
        }

        /**
         * Restore a Save Point
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
                if (theSavePoint.getPrice() != null)
                    thePrice = new Price(theSavePoint.getPrice());

                /* Trim back the capital events */
                theEvents.purgeAfterDate(getDate());
            }
        }
    }

    /* The ExternalAccount Bucket class */
    public static class ExternalAccount extends ActDetail {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                ExternalAccount.class.getSimpleName(), ActDetail.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_INCOME = FIELD_DEFS.declareLocalField("Income");
        public static final JDataField FIELD_EXPENSE = FIELD_DEFS.declareLocalField("Expense");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_INCOME)
                return theIncome;
            if (pField == FIELD_EXPENSE)
                return theExpense;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theIncome = null;
        private Money theExpense = null;
        private ExternalAccount theSavePoint = null;

        /* Override of getBase method */
        @Override
        public ExternalAccount getBase() {
            return (ExternalAccount) super.getBase();
        }

        /* Access methods */
        public Money getIncome() {
            return theIncome;
        }

        public Money getExpense() {
            return theExpense;
        }

        public Money getPrevIncome() {
            return (getBase() != null) ? getBase().getIncome() : null;
        }

        public Money getPrevExpense() {
            return (getBase() != null) ? getBase().getExpense() : null;
        }

        /* Constructor */
        private ExternalAccount(BucketList pList,
                                Account pAccount) {
            /* Call super-constructor */
            super(pList, BucketType.EXTERNALDETAIL, pAccount);

            /* Initialise the money values */
            theIncome = new Money(0);
            theExpense = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private ExternalAccount(BucketList pList,
                                ExternalAccount pPrevious) {
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

        /* Constructor */
        private ExternalAccount(ExternalAccount pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), BucketType.EXTERNALDETAIL, pPrevious.getAccount());

            /* Initialise the Money values */
            theIncome = new Money(pPrevious.getIncome());
            theExpense = new Money(pPrevious.getExpense());

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            /* Copy if the income or expense is non-zero */
            return (theIncome.isNonZero() || theExpense.isNonZero());
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theIncome.isNonZero() || theExpense.isNonZero()
                    || ((getPrevIncome() != null) && (getPrevIncome().isNonZero())) || ((getPrevExpense() != null) && (getPrevExpense()
                    .isNonZero())));
        }

        /**
         * Adjust account for debit
         * @param pEvent the event causing the debit
         */
        @Override
        protected void adjustForDebit(Event pEvent) {
            TransactionType myTransType = pEvent.getTransType();
            Money myAmount = pEvent.getAmount();
            Money myTaxCred = pEvent.getTaxCredit();

            /* If this is a recovered transaction */
            if (myTransType.isRecovered()) {
                /* This is a negative expense */
                theExpense.subtractAmount(myAmount);
            }

            /* else this is a standard income */
            else {
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
         * Adjust account for credit
         * @param pEvent the event causing the credit
         */
        @Override
        protected void adjustForCredit(Event pEvent) {
            /* Adjust for expense */
            theExpense.addAmount(pEvent.getAmount());
        }

        /**
         * Adjust account for tax credit
         * @param pEvent the event causing the tax credit
         */
        protected void adjustForTaxCredit(Event pEvent) {
            /* Adjust for expense */
            theExpense.addAmount(pEvent.getTaxCredit());
        }

        /**
         * Adjust account for taxable gain tax credit
         * @param pEvent the event causing the tax credit
         */
        protected void adjustForTaxGainTaxCredit(Event pEvent) {
            /* Adjust for expense */
            theIncome.addAmount(pEvent.getTaxCredit());
        }

        /**
         * Create a Save Point
         */
        @Override
        protected void createSavePoint() {
            /* Create a save of the values */
            theSavePoint = new ExternalAccount(this);
        }

        /**
         * Restore a Save Point
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

    /* The AssetSummary Bucket class */
    public static class AssetSummary extends ActType {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                AssetSummary.class.getSimpleName(), ActType.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_VALUE)
                return theValue;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theValue = null;

        /* Override of getBase method */
        @Override
        public AssetSummary getBase() {
            return (AssetSummary) super.getBase();
        }

        /* Access methods */
        public Money getValue() {
            return theValue;
        }

        public Money getPrevValue() {
            return getBase().getValue();
        }

        /* Constructor */
        private AssetSummary(BucketList pList,
                             AccountType pAccountType) {
            /* Call super-constructor */
            super(pList, pAccountType);

            /* Initialise the Money values */
            theValue = new Money(0);

            /* Create a new base for this total */
            setBase(new AssetSummary(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private AssetSummary(AssetSummary pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), pMaster.getAccountType());

            /* Initialise the Money values */
            theValue = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            return false;
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the summary value
         * @param pBucket the bucket
         */
        protected void addValues(ValueAccount pBucket) {
            ValueAccount myPrevious = pBucket.getBase();

            /* the total */
            theValue.addAmount(pBucket.getValue());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                getBase().addValues(myPrevious);
            }
        }
    }

    /* The AssetTotal Bucket class */
    public static class AssetTotal extends AnalysisBucket {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AssetTotal.class.getSimpleName(),
                AnalysisBucket.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_VALUE)
                return theValue;
            if (pField == FIELD_PROFIT)
                return theProfit;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theValue = null;
        private Money theProfit = null;

        /* Override of getBase method */
        @Override
        public AssetTotal getBase() {
            return (AssetTotal) super.getBase();
        }

        /* Access methods */
        public Money getValue() {
            return theValue;
        }

        public Money getProfit() {
            return theProfit;
        }

        public Money getPrevValue() {
            return getBase().getValue();
        }

        /* Constructor */
        private AssetTotal(BucketList pList) {
            /* Call super-constructor */
            super(pList, BucketType.ASSETTOTAL, 0);

            /* Initialise the Money values */
            theValue = new Money(0);

            /* Create a new base for this total */
            setBase(new AssetTotal(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private AssetTotal(AssetTotal pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), BucketType.ASSETTOTAL, 0);

            /* Initialise the Money values */
            theValue = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            return false;
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value
         * @param pBucket the bucket
         */
        protected void addValues(AssetSummary pBucket) {
            AssetSummary myPrevious = pBucket.getBase();

            /* the total */
            theValue.addAmount(pBucket.getValue());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null) && (getBase() != null)) {
                getBase().addValues(myPrevious);
            }
        }

        /**
         * Calculate profit
         */
        protected void calculateProfit() {
            theProfit = new Money(theValue);
            if (getBase() != null)
                theProfit.subtractAmount(getPrevValue());
        }
    }

    /* The ExternalTotal Bucket class */
    public static class ExternalTotal extends AnalysisBucket {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                ExternalTotal.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_INCOME = FIELD_DEFS.declareLocalField("Income");
        public static final JDataField FIELD_EXPENSE = FIELD_DEFS.declareLocalField("Expense");
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_INCOME)
                return theIncome;
            if (pField == FIELD_EXPENSE)
                return theExpense;
            if (pField == FIELD_PROFIT)
                return theProfit;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theIncome = null;
        private Money theExpense = null;
        private Money theProfit = null;

        /* Override of getBase method */
        @Override
        public ExternalTotal getBase() {
            return (ExternalTotal) super.getBase();
        }

        /* Access methods */
        public Money getIncome() {
            return theIncome;
        }

        public Money getExpense() {
            return theExpense;
        }

        public Money getProfit() {
            return theProfit;
        }

        public Money getPrevIncome() {
            return getBase().getIncome();
        }

        public Money getPrevExpense() {
            return getBase().getExpense();
        }

        public Money getPrevProfit() {
            return getBase().getProfit();
        }

        /* Constructor */
        private ExternalTotal(BucketList pList) {
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

        /* Constructor */
        private ExternalTotal(ExternalTotal pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), BucketType.EXTERNALTOTAL, 0);

            /* Initialise the Money values */
            theIncome = new Money(0);
            theExpense = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            return false;
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value
         * @param pBucket the bucket
         */
        protected void addValues(ExternalAccount pBucket) {
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
         * Calculate profit
         */
        protected void calculateProfit() {
            theProfit = new Money(theIncome);
            theProfit.subtractAmount(theExpense);
            if (getBase() != null)
                getBase().calculateProfit();
        }
    }

    /* The MarketTotal Bucket class */
    public static class MarketTotal extends AnalysisBucket {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                ExternalTotal.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_COST = FIELD_DEFS.declareLocalField("Cost");
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");
        public static final JDataField FIELD_GAINED = FIELD_DEFS.declareLocalField("Gained");
        public static final JDataField FIELD_PROFIT = FIELD_DEFS.declareLocalField("Profit");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_COST)
                return theCost;
            if (pField == FIELD_VALUE)
                return theValue;
            if (pField == FIELD_GAINED)
                return theGained;
            if (pField == FIELD_PROFIT)
                return theProfit;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theCost = null;
        private Money theValue = null;
        private Money theGained = null;
        private Money theProfit = null;

        /* Override of getBase method */
        @Override
        public MarketTotal getBase() {
            return (MarketTotal) super.getBase();
        }

        /* Access methods */
        public Money getCost() {
            return theCost;
        }

        public Money getGained() {
            return theGained;
        }

        public Money getValue() {
            return theValue;
        }

        public Money getProfit() {
            return theProfit;
        }

        /* Constructor */
        private MarketTotal(BucketList pList) {
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

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            return false;
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value
         * @param pBucket the source bucket to add
         */
        protected void addValues(AssetAccount pBucket) {
            theCost.addAmount(pBucket.getCost());
            theGained.addAmount(pBucket.getGained());
            theProfit.addAmount(pBucket.getProfit());
            theValue.addAmount(pBucket.getValue());
        }
    }

    /* The Transaction Detail Bucket class */
    public static class TransDetail extends TransType {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                TransDetail.class.getSimpleName(), TransType.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");
        public static final JDataField FIELD_TAXCREDIT = FIELD_DEFS.declareLocalField("TaxCredit");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_AMOUNT)
                return theAmount;
            if (pField == FIELD_TAXCREDIT)
                return theTaxCredit;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theAmount = null;
        private Money theTaxCredit = null;

        /* Override of getBase method */
        @Override
        public TransDetail getBase() {
            return (TransDetail) super.getBase();
        }

        /* Access methods */
        public Money getAmount() {
            return theAmount;
        }

        public Money getTaxCredit() {
            return theTaxCredit;
        }

        public Money getPrevAmount() {
            return (getBase() != null) ? getBase().getAmount() : null;
        }

        public Money getPrevTax() {
            return (getBase() != null) ? getBase().getTaxCredit() : null;
        }

        /* Constructor */
        private TransDetail(BucketList pList,
                            TransactionType pTransType) {
            /* Call super-constructor */
            super(pList, pTransType);

            /* Initialise the Money values */
            theAmount = new Money(0);
            theTaxCredit = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private TransDetail(BucketList pList,
                            TransDetail pPrevious) {
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

        /* Constructor */
        private TransDetail(TransDetail pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), pPrevious.getTransType());

            /* Initialise the Money values */
            theAmount = new Money(pPrevious.getAmount());
            theTaxCredit = new Money(pPrevious.getTaxCredit());

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            /* Copy if the amount is non-zero */
            return theAmount.isNonZero();
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theAmount.isNonZero() || ((getPrevAmount() != null) && (getPrevAmount().isNonZero())));
        }

        /**
         * Adjust account for transaction
         * @param pEvent the source event
         */
        protected void adjustAmount(Event pEvent) {
            /* Adjust for transaction */
            theAmount.addAmount(pEvent.getAmount());

            /* Adjust for tax credit */
            if (pEvent.getTaxCredit() != null)
                theTaxCredit.addAmount(pEvent.getTaxCredit());
        }

        /**
         * Adjust account for tax credit
         * @param pEvent the source event
         */
        protected void adjustForTaxCredit(Event pEvent) {
            /* Adjust for tax credit */
            theAmount.addAmount(pEvent.getTaxCredit());
        }
    }

    /* The Transaction Summary Bucket class */
    public static class TransSummary extends Tax {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                TransSummary.class.getSimpleName(), Tax.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_AMOUNT)
                return theAmount;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theAmount = null;

        /* Override of getBase method */
        @Override
        public TransSummary getBase() {
            return (TransSummary) super.getBase();
        }

        /* Access methods */
        public Money getAmount() {
            return theAmount;
        }

        public Money getPrevAmount() {
            return getBase().getAmount();
        }

        /* Constructor */
        private TransSummary(BucketList pList,
                             TaxType pTaxType) {
            /* Call super-constructor */
            super(pList, pTaxType);

            /* Initialise the Money values */
            theAmount = new Money(0);

            /* Create a new base for this total */
            setBase(new TransSummary(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private TransSummary(TransSummary pPrevious) {
            /* Call super-constructor */
            super((BucketList) pPrevious.getList(), pPrevious.getTaxType());

            /* Initialise the Money values */
            theAmount = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            return false;
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value
         * @param pBucket the bucket
         */
        protected void addValues(TransDetail pBucket) {
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
         * Subtract values from the total value
         * @param pBucket the bucket
         */
        protected void subtractValues(TransDetail pBucket) {
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

    /* The Transaction Total Bucket class */
    public static class TransTotal extends Tax {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransTotal.class.getSimpleName(),
                Tax.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_AMOUNT)
                return theAmount;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theAmount = null;

        /* Override of getBase method */
        @Override
        public TransTotal getBase() {
            return (TransTotal) super.getBase();
        }

        /* Access methods */
        public Money getAmount() {
            return theAmount;
        }

        public Money getPrevAmount() {
            return getBase().getAmount();
        }

        /* Constructor */
        private TransTotal(BucketList pList,
                           TaxType pTaxType) {
            /* Call super-constructor */
            super(pList, pTaxType);

            /* Initialise the Money values */
            theAmount = new Money(0);

            /* Create a new base for this total */
            setBase(new TransTotal(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private TransTotal(TransTotal pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), pMaster.getTaxType());

            /* Initialise the Money values */
            theAmount = new Money(0);

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            return false;
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value
         * @param pBucket the bucket
         */
        protected void addValues(TransSummary pBucket) {
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
         * Subtract values from the total value
         * @param pBucket the bucket
         */
        protected void subtractValues(TransSummary pBucket) {
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

    /* The Taxation Detail Bucket class */
    public static class TaxDetail extends Tax {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxDetail.class.getSimpleName(),
                Tax.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");
        public static final JDataField FIELD_TAXATION = FIELD_DEFS.declareLocalField("Taxation");
        public static final JDataField FIELD_RATE = FIELD_DEFS.declareLocalField("Rate");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_AMOUNT)
                return theAmount;
            if (pField == FIELD_TAXATION)
                return theTaxation;
            if (pField == FIELD_RATE)
                return theRate;
            return super.getFieldValue(pField);
        }

        /* Members */
        private Money theAmount = null;
        private Money theTaxation = null;
        private Rate theRate = null;
        private TaxDetail theParent = null;

        /* Override of getBase method */
        @Override
        public TaxDetail getBase() {
            return (TaxDetail) super.getBase();
        }

        /* Access methods */
        public Money getAmount() {
            return theAmount;
        }

        public Money getTaxation() {
            return theTaxation;
        }

        public Rate getRate() {
            return theRate;
        }

        public TaxDetail getParent() {
            return theParent;
        }

        public Money getPrevAmount() {
            return (getBase() != null) ? getBase().getAmount() : null;
        }

        public Money getPrevTax() {
            return (getBase() != null) ? getBase().getTaxation() : null;
        }

        public Rate getPrevRate() {
            return (getBase() != null) ? getBase().getRate() : null;
        }

        /* Constructor */
        private TaxDetail(BucketList pList,
                          TaxType pTaxType) {
            /* Call super-constructor */
            super(pList, pTaxType);

            /* Add the link to the previous item */
            setBase(new TaxDetail(this));

            /* Set status */
            setState(DataState.CLEAN);
        }

        /* Constructor */
        private TaxDetail(TaxDetail pMaster) {
            /* Call super-constructor */
            super((BucketList) pMaster.getList(), pMaster.getTaxType());

            /* Set status */
            setState(DataState.CLEAN);
        }

        /**
         * is the bucket active (i.e. should it be copied)
         */
        @Override
        public boolean isActive() {
            return false;
        }

        /**
         * is the bucket relevant (i.e. should it be reported)
         */
        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theAmount.isNonZero() || theTaxation.isNonZero()
                    || ((getPrevAmount() != null) && (getPrevAmount().isNonZero())) || ((getPrevTax() != null) && (getPrevTax()
                    .isNonZero())));
        }

        /**
         * Set a taxation amount and calculate the tax on it
         * 
         * @param pAmount Amount to set
         * @return the taxation on this bucket
         */
        protected Money setAmount(Money pAmount) {
            /* Set the value */
            theAmount = new Money(pAmount);

            /* Calculate the tax if we have a rate */
            theTaxation = (theRate != null) ? theAmount.valueAtRate(theRate) : new Money(0);

            /* Return the taxation amount */
            return theTaxation;
        }

        /**
         * Set explicit taxation value
         * 
         * @param pAmount Amount to set
         */
        protected void setTaxation(Money pAmount) {
            /* Set the value */
            theTaxation = new Money(pAmount);
        }

        /**
         * Set parent bucket for reporting purposes
         * @param pParent the parent bucket
         */
        protected void setParent(TaxDetail pParent) {
            /* Set the value */
            theParent = pParent;
        }

        /**
         * Set a tax rate
         * 
         * @param pRate Amount to set
         */
        protected void setRate(Rate pRate) {
            /* Set the value */
            theRate = pRate;
        }
    }

    /**
     * Bucket Types
     */
    public static enum BucketType {
        /* Enum values */
        MONEYDETAIL, ASSETDETAIL, DEBTDETAIL, EXTERNALDETAIL, ASSETSUMMARY, ASSETTOTAL, MARKETTOTAL, EXTERNALTOTAL, TRANSDETAIL, TRANSSUMMARY, TRANSTOTAL, TAXDETAIL, TAXSUMMARY, TAXTOTAL;

        /**
         * Get the Id shift for this BucketType
         * @return the id shift
         */
        private int getIdShift() {
            switch (this) {
                case MONEYDETAIL:
                case DEBTDETAIL:
                case EXTERNALDETAIL:
                case ASSETDETAIL:
                    return 1000; /* Account IDs */
                case ASSETSUMMARY:
                    return 100; /* AccountType IDs */
                case TRANSDETAIL:
                    return 200; /* TransactionType IDs */
                case TRANSSUMMARY:
                case TRANSTOTAL:
                case TAXDETAIL:
                case TAXSUMMARY:
                case TAXTOTAL:
                    return 300; /* TaxType IDs */
                case ASSETTOTAL:
                    return 1;
                case MARKETTOTAL:
                    return 2;
                case EXTERNALTOTAL:
                    return 3;
                default:
                    return 0;
            }
        }

        /**
         * Get the BucketType for this Account
         * @param pAccount the account
         * @return the Bucket type
         */
        private static BucketType getActBucketType(Account pAccount) {
            /* If this is a external/benefit */
            if (pAccount.isExternal() || pAccount.isBenefit())
                return EXTERNALDETAIL;
            else if (pAccount.isMoney())
                return MONEYDETAIL;
            else if (pAccount.isPriced())
                return ASSETDETAIL;
            else
                return DEBTDETAIL;
        }

        /**
         * Get the BucketType for this TaxType
         * @param pTaxType the tax type
         * @return the id shift
         */
        private static BucketType getTaxBucketType(TaxType pTaxType) {
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

    /* Analysis state */
    protected enum AnalysisState {
        RAW, VALUED, TOTALLED, TAXED;
    }
}
