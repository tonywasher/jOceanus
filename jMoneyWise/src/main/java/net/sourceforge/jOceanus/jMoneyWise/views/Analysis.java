/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate.AccountRateList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxBucket;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType;
import net.sourceforge.jOceanus.jMoneyWise.views.CapitalEvent.CapitalEventList;
import net.sourceforge.jOceanus.jMoneyWise.views.ChargeableEvent.ChargeableEventList;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
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
            return (theList.size() > 0) ? theList : JDataFieldValue.SkipField;
        }
        if (FIELD_CHARGES.equals(pField)) {
            return (theCharges.size() > 0) ? theCharges : JDataFieldValue.SkipField;
        }
        if (FIELD_TAXYEAR.equals(pField)) {
            return (theYear == null) ? JDataFieldValue.SkipField : theYear;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return (theAccount == null) ? JDataFieldValue.SkipField : theAccount;
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
                        AssetAccount myAsset = new AssetAccount((AssetAccount) myCurr);
                        theList.add(myAsset);
                    }
                    break;
                case DEBTDETAIL:
                    if (myCurr.isActive()) {
                        DebtAccount myDebt = new DebtAccount((DebtAccount) myCurr);
                        theList.add(myDebt);
                    }
                    break;
                case MONEYDETAIL:
                    if (myCurr.isActive()) {
                        MoneyAccount myMoney = new MoneyAccount((MoneyAccount) myCurr);
                        theList.add(myMoney);
                    }
                    break;
                case EXTERNALDETAIL:
                    if (myCurr.isActive()) {
                        ExternalAccount myExternal = new ExternalAccount((ExternalAccount) myCurr);
                        theList.add(myExternal);
                    }
                    break;
                case TRANSDETAIL:
                    if (myCurr.isActive()) {
                        TransDetail myTrans = new TransDetail((TransDetail) myCurr);
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
    protected abstract static class AnalysisBucket
            implements OrderedIdItem<Integer>, JDataContents, Comparable<AnalysisBucket> {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AnalysisBucket.class.getSimpleName());

        /**
         * Bucket type field id.
         */
        public static final JDataField FIELD_BUCKETTYPE = FIELD_DEFS.declareEqualityField("BucketType");

        /**
         * Id field id.
         */
        public static final JDataField FIELD_ID = FIELD_DEFS.declareLocalField("Id");

        /**
         * Base Field Id.
         */
        public static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField("Base");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKETTYPE.equals(pField)) {
                return theBucketType;
            }
            if (FIELD_ID.equals(pField)) {
                return theId;
            }
            if (FIELD_BASE.equals(pField)) {
                return (theBase != null) ? theBase : JDataFieldValue.SkipField;
            }

            /* Unknown */
            return JDataFieldValue.UnknownField;
        }

        @Override
        public String formatObject() {
            return theBucketType.toString();
        }

        /**
         * The bucket type.
         */
        private final BucketType theBucketType;

        /**
         * The base bucket.
         */
        private AnalysisBucket theBase;

        /**
         * The id.
         */
        private final Integer theId;

        /**
         * Obtain the bucket type.
         * @return the type
         */
        public BucketType getBucketType() {
            return theBucketType;
        }

        /**
         * Obtain the base.
         * @return the base
         */
        protected AnalysisBucket getBase() {
            return theBase;
        }

        @Override
        public Integer getOrderedId() {
            /* This is the id of the event, or in the case where there is no event, the negative Date id */
            return theId;
        }

        /**
         * Constructor.
         * @param pType the bucket type
         * @param uId the id
         */
        public AnalysisBucket(final BucketType pType,
                              final int uId) {
            /* Store info */
            theId = uId
                    + pType.getIdShift();
            theBase = null;
            theBucketType = pType;
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        public AnalysisBucket(final AnalysisBucket pBase) {
            /* Store info */
            theId = pBase.theId;
            theBase = pBase;
            theBucketType = pBase.theBucketType;
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

            /* Compare the bucket order */
            return getBucketType().compareTo(pThat.getBucketType());
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

            /* Check class */
            if (getClass() != pThat.getClass()) {
                return false;
            }

            /* Access as AnalysisBucket */
            AnalysisBucket myThat = (AnalysisBucket) pThat;

            /* Check equality */
            return (getBucketType() == myThat.getBucketType())
                   && (theId.equals(myThat.theId));
        }

        @Override
        public int hashCode() {
            return getBucketType().hashCode()
                   ^ theId;
        }

        /**
         * is the bucket active?
         * @return TRUE/FALSE
         */
        public abstract boolean isActive();

        /**
         * is the bucket relevant (i.e. should it be reported)?
         * @return TRUE/FALSE
         */
        protected abstract boolean isRelevant();
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
         * Obtain the AccountDetail Bucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected ActDetail getAccountDetail(final Account pAccount) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.MONEYDETAIL;
            int uId = pAccount.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            ActDetail myItem = (ActDetail) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Determine the bucket type */
                myBucket = BucketType.getActBucketType(pAccount);

                /* Switch on the bucket type */
                switch (myBucket) {
                    case MONEYDETAIL:
                        myItem = new MoneyAccount(pAccount);
                        break;
                    case ASSETDETAIL:
                        myItem = new AssetAccount(theData, pAccount);
                        break;
                    case EXTERNALDETAIL:
                        myItem = new ExternalAccount(pAccount);
                        break;
                    case DEBTDETAIL:
                    default:
                        myItem = new DebtAccount(pAccount);
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
            int uId = pActType.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            AssetSummary myItem = (AssetSummary) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new AssetSummary(pActType);
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
            TransactionType myTrans = theData.getTransTypes().findItemByClass(pTransClass);

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
            int uId = pTransType.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TransDetail myItem = (TransDetail) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TransDetail(pTransType);
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
            TaxType myTaxType = theData.getTaxTypes().findItemByClass(pTaxClass);
            int uId = myTaxType.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TransSummary myItem = (TransSummary) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TransSummary(myTaxType);
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
            TaxType myTaxType = theData.getTaxTypes().findItemByClass(pTaxClass);
            int uId = myTaxType.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TaxDetail myItem = (TaxDetail) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TaxDetail(myTaxType);
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
         * Obtain the External Total Bucket.
         * @return the bucket
         */
        protected ExternalTotal getExternalTotal() {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.EXTERNALTOTAL;
            int uId = myBucket.getIdShift();

            /* Locate the bucket in the list */
            ExternalTotal myItem = (ExternalTotal) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new ExternalTotal();
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
        protected TransTotal getTransTotal(final TaxClass pTaxClass) {
            /* Calculate the id that we are looking for */
            BucketType myBucket = BucketType.TRANSTOTAL;
            TaxType myTaxType = theData.getTaxTypes().findItemByClass(pTaxClass);
            int uId = myTaxType.getId()
                      + myBucket.getIdShift();

            /* Locate the bucket in the list */
            TransTotal myItem = (TransTotal) findItemById(uId);

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Allocate it and add to the list */
                myItem = new TransTotal(myTaxType);
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
     * The Account Bucket class.
     */
    protected abstract static class ActDetail
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ActDetail.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        /**
         * Account Field Id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField("Account");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACCOUNT.equals(pField)) {
                return theAccount;
            }
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject() {
            return getName();
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
         * @param pType the type
         * @param pAccount the account
         */
        private ActDetail(final BucketType pType,
                          final Account pAccount) {
            /* Call super-constructor */
            super(pType, pAccount.getId());

            /* Store the account */
            theAccount = pAccount;
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        private ActDetail(final ActDetail pBase) {
            /* Call super-constructor */
            super(pBase);

            /* Store the account */
            theAccount = pBase.theAccount;
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

            /* Access the object as an Account Bucket */
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

        /**
         * Restore a Save Point.
         * @param pDate the date to restore.
         */
        protected void restoreSavePoint(final JDateDay pDate) {
            restoreSavePoint();
        }
    }

    /**
     * The Account Type Bucket class.
     */
    private abstract static class ActType
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ActType.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        @Override
        public String formatObject() {
            return getName();
        }

        /**
         * Account Type Field Id.
         */
        public static final JDataField FIELD_ACTTYPE = FIELD_DEFS.declareEqualityField("AccountType");

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
         * @param pAccountType the account type
         */
        private ActType(final AccountType pAccountType) {
            /* Call super-constructor */
            super(BucketType.ASSETSUMMARY, pAccountType.getId());

            /* Store the account type */
            theAccountType = pAccountType;
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

            /* Access the object as an ActType Bucket */
            ActType myThat = (ActType) pThat;

            /* Compare the AccountTypes */
            return getAccountType().compareTo(myThat.getAccountType());
        }
    }

    /**
     * The TransType Bucket class.
     */
    private abstract static class TransType
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TransType.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        /**
         * TransactionType Field Id.
         */
        public static final JDataField FIELD_TRANSTYPE = FIELD_DEFS.declareEqualityField("TransactionType");

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
         * @param pTransType the type
         */
        private TransType(final TransactionType pTransType) {
            /* Call super-constructor */
            super(BucketType.TRANSDETAIL, pTransType.getId());

            /* Store the transaction type */
            theTransType = pTransType;
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        private TransType(final TransType pBase) {
            /* Call super-constructor */
            super(pBase);

            /* Store the transaction type */
            theTransType = pBase.theTransType;
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

            /* Compare the super-class */
            int result = super.compareTo(pThat);
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
    private abstract static class Tax
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(Tax.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

        /**
         * Tax Type Field Id.
         */
        public static final JDataField FIELD_TAXTYPE = FIELD_DEFS.declareEqualityField("TaxType");

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
         * @param pTaxType the type
         */
        private Tax(final TaxType pTaxType) {
            /* Call super-constructor */
            super(BucketType.getTaxBucketType(pTaxType), pTaxType.getId());

            /* Store the tax type */
            theTaxType = pTaxType;
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

            /* Compare the super-class */
            int result = super.compareTo(pThat);
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
    protected abstract static class ValueAccount
            extends ActDetail {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ValueAccount.class.getSimpleName(), ActDetail.FIELD_DEFS);

        /**
         * Value Field Id.
         */
        public static final JDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

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
        private JMoney theValue = null;

        @Override
        public ValueAccount getBase() {
            return (ValueAccount) super.getBase();
        }

        /**
         * Obtain the value.
         * @return the value
         */
        public JMoney getValue() {
            return theValue;
        }

        /**
         * Obtain the previous value.
         * @return the value
         */
        public JMoney getPrevValue() {
            return (getBase() != null) ? getBase().getValue() : null;
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setValue(final JMoney pValue) {
            theValue = pValue;
        }

        /**
         * Constructor.
         * @param pType the type
         * @param pAccount the account
         */
        private ValueAccount(final BucketType pType,
                             final Account pAccount) {
            /* Call super-constructor */
            super(pType, pAccount);

            /* Initialise the money values */
            theValue = new JMoney();
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        private ValueAccount(final ValueAccount pBase) {
            /* Call super-constructor */
            super(pBase);

            /* Initialise the money values */
            theValue = new JMoney();
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
    public static final class MoneyAccount
            extends ValueAccount {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(MoneyAccount.class.getSimpleName(), ValueAccount.FIELD_DEFS);

        /**
         * Rate field Id.
         */
        public static final JDataField FIELD_RATE = FIELD_DEFS.declareLocalField("Rate");

        /**
         * Maturity field Id.
         */
        public static final JDataField FIELD_MATURITY = FIELD_DEFS.declareLocalField("Maturity");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_RATE.equals(pField)) {
                return (theRate != null) ? theRate : JDataFieldValue.SkipField;
            }
            if (FIELD_MATURITY.equals(pField)) {
                return (theMaturity != null) ? theMaturity : JDataFieldValue.SkipField;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The rate.
         */
        private JRate theRate = null;

        /**
         * The maturity.
         */
        private JDateDay theMaturity = null;

        /**
         * The savePoint.
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
        public JRate getRate() {
            return theRate;
        }

        /**
         * Obtain the maturity.
         * @return the maturity
         */
        public JDateDay getMaturity() {
            return theMaturity;
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        private MoneyAccount(final Account pAccount) {
            /* Call super-constructor */
            super(BucketType.MONEYDETAIL, pAccount);
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket.
         */
        private MoneyAccount(final MoneyAccount pBase) {
            /* Call super-constructor */
            super(pBase.cloneIt());

            /* Initialise the Money values */
            setValue(new JMoney(pBase.getValue()));
        }

        /**
         * Create a clone of the money account.
         * @return the cloned MoneyAccount.
         */
        private MoneyAccount cloneIt() {
            /* Call super-constructor */
            MoneyAccount myClone = new MoneyAccount(getAccount());

            /* Copy the Money values */
            myClone.setValue(new JMoney(getValue()));
            if (getRate() != null) {
                myClone.theRate = new JRate(getRate());
            }
            if (getMaturity() != null) {
                myClone.theMaturity = new JDateDay(getMaturity());
            }

            /* Return the clone */
            return myClone;
        }

        /**
         * record the rate of the account at a given date.
         * @param pData the dataSet
         * @param pDate the date of valuation
         */
        protected void recordRate(final FinanceData pData,
                                  final JDateDay pDate) {
            /* Obtain the appropriate price record */
            AccountRateList myRates = pData.getRates();
            AccountRate myRate = myRates.getLatestRate(getAccount(), pDate);
            JDateDay myDate = getAccount().getMaturity();

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
                setValue(new JMoney(theSavePoint.getValue()));
            }
        }
    }

    /**
     * The DebtAccount Bucket class.
     */
    public static final class DebtAccount
            extends ValueAccount {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(DebtAccount.class.getSimpleName(), ValueAccount.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Spend Field Id.
         */
        public static final JDataField FIELD_SPEND = FIELD_DEFS.declareLocalField("Spend");

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
        private JMoney theSpend = null;

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
        public JMoney getSpend() {
            return theSpend;
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        private DebtAccount(final Account pAccount) {
            /* Call super-constructor */
            super(BucketType.DEBTDETAIL, pAccount);

            /* Initialise the money values */
            theSpend = new JMoney();
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        private DebtAccount(final DebtAccount pBase) {
            /* Call super-constructor */
            super(pBase.cloneIt());

            /* Initialise the Money values */
            setValue(new JMoney(pBase.getValue()));
            theSpend = new JMoney();
        }

        /**
         * Adjust account for debit.
         * @param pEvent the event causing the debit
         */
        @Override
        protected void adjustForDebit(final Event pEvent) {
            /* Adjust value */
            super.adjustForDebit(pEvent);

            /* Adjust for spend */
            theSpend.addAmount(pEvent.getAmount());
        }

        /**
         * Create a clone of the debt account.
         * @return the cloned DebtAccount.
         */
        private DebtAccount cloneIt() {
            /* Call super-constructor */
            DebtAccount myClone = new DebtAccount(getAccount());

            /* Copy the Debt values */
            myClone.setValue(new JMoney(getValue()));
            myClone.theSpend = new JMoney(theSpend);

            /* Return the clone */
            return myClone;
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
                setValue(new JMoney(theSavePoint.getValue()));
                theSpend = new JMoney(theSavePoint.getSpend());
            }
        }
    }

    /**
     * The AssetAccount Bucket class.
     */
    public static final class AssetAccount
            extends ValueAccount {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AssetAccount.class.getSimpleName(), ValueAccount.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

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
         * DataSet.
         */
        private final FinanceData theData;

        /**
         * CapitalEvent list.
         */
        private CapitalEventList theEvents = null;

        /**
         * The cost.
         */
        private JMoney theCost = null;

        /**
         * The units.
         */
        private JUnits theUnits = null;

        /**
         * The gained.
         */
        private JMoney theGained = null;

        /**
         * The invested.
         */
        private JMoney theInvested = null;

        /**
         * The dividend.
         */
        private JMoney theDividend = null;

        /**
         * The gains.
         */
        private JMoney theGains = null;

        /**
         * The profit.
         */
        private JMoney theProfit = null;

        /**
         * The price.
         */
        private JPrice thePrice = null;

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
        public JMoney getCost() {
            return theCost;
        }

        /**
         * Obtain units.
         * @return the units
         */
        public JUnits getUnits() {
            return theUnits;
        }

        /**
         * Obtain gained.
         * @return the gained
         */
        public JMoney getGained() {
            return theGained;
        }

        /**
         * Obtain invested.
         * @return the invested
         */
        public JMoney getInvested() {
            return theInvested;
        }

        /**
         * Obtain dividend.
         * @return the dividend
         */
        public JMoney getDividend() {
            return theDividend;
        }

        /**
         * Obtain gains.
         * @return the gains
         */
        public JMoney getGains() {
            return theGains;
        }

        /**
         * Obtain profit.
         * @return the profit
         */
        public JMoney getProfit() {
            return theProfit;
        }

        /**
         * Obtain price.
         * @return the price
         */
        public JPrice getPrice() {
            return thePrice;
        }

        /**
         * Obtain previous cost.
         * @return the cost
         */
        public JMoney getPrevCost() {
            return (getBase() != null) ? getBase().getCost() : null;
        }

        /**
         * Obtain previous units.
         * @return the units
         */
        public JUnits getPrevUnits() {
            return (getBase() != null) ? getBase().getUnits() : null;
        }

        /**
         * Obtain previous gained.
         * @return the gained
         */
        public JMoney getPrevGained() {
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
         * @param pData the dataSet
         * @param pAccount the account
         */
        private AssetAccount(final FinanceData pData,
                             final Account pAccount) {
            /* Call super-constructor */
            super(BucketType.ASSETDETAIL, pAccount);

            /* Initialise the values */
            theData = pData;
            theUnits = new JUnits();
            theCost = new JMoney();
            theGained = new JMoney();

            theInvested = new JMoney();
            theDividend = new JMoney();
            theGains = new JMoney();

            /* allocate the Capital events */
            theEvents = new CapitalEventList(pData, pAccount);
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        private AssetAccount(final AssetAccount pBase) {
            /* Call super-constructor */
            super(pBase.cloneIt());

            /* Initialise the values */
            theData = pBase.theData;
            theUnits = new JUnits(pBase.getUnits());
            theCost = new JMoney(pBase.getCost());
            theGained = new JMoney(pBase.getGained());
            theInvested = new JMoney();
            theGains = new JMoney();
            theDividend = new JMoney();

            /* Copy the Capital Events */
            theEvents = pBase.getCapitalEvents();
        }

        /**
         * Create a clone of the asset account.
         * @return the cloned AssetAccount.
         */
        private AssetAccount cloneIt() {
            /* Call super-constructor */
            AssetAccount myClone = new AssetAccount(theData, getAccount());

            /* Copy the Asset values */
            myClone.setValue(new JMoney(getValue()));
            myClone.theUnits = new JUnits(theUnits);
            myClone.theCost = new JMoney(theCost);
            myClone.theGained = new JMoney(theGained);
            myClone.theInvested = new JMoney(theInvested);
            myClone.theGains = new JMoney(theGains);
            myClone.theDividend = new JMoney(theDividend);

            /* Copy price if available */
            if (thePrice != null) {
                myClone.thePrice = new JPrice(thePrice);
            }

            /* Return the clone */
            return myClone;
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
        protected void valueAsset(final JDateDay pDate) {
            AccountPriceList myPrices = theData.getPrices();
            AccountPrice myActPrice;

            /* Obtain the appropriate price record */
            myActPrice = myPrices.getLatestPrice(getAccount(), pDate);

            /* If we found a price */
            if (myActPrice != null) {
                /* Store the price */
                thePrice = myActPrice.getPrice();

                /* else assume zero price */
            } else {
                thePrice = new JPrice();
            }

            /* Calculate the value */
            setValue(theUnits.valueAtPrice(thePrice));
        }

        /**
         * Calculate profit.
         */
        protected void calculateProfit() {
            /* Calculate the profit */
            theProfit = new JMoney(getValue());
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
            if (pEvent.getDebitUnits() != null) {
                theUnits.subtractUnits(pEvent.getDebitUnits());
            }
        }

        /**
         * Adjust account for credit.
         * @param pEvent the event causing the credit
         */
        @Override
        protected void adjustForCredit(final Event pEvent) {
            /* Adjust for credit */
            if (pEvent.getCreditUnits() != null) {
                theUnits.addUnits(pEvent.getCreditUnits());
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

        @Override
        protected void restoreSavePoint() {
            restoreSavePoint(null);
        }

        @Override
        protected void restoreSavePoint(final JDateDay pDate) {
            /* If we have a Save point */
            if (theSavePoint != null) {
                /* Restore original value */
                setValue(new JMoney(theSavePoint.getValue()));

                /* Initialise the Money values */
                theUnits = new JUnits(theSavePoint.getUnits());
                theCost = new JMoney(theSavePoint.getCost());
                theGained = new JMoney(theSavePoint.getGained());
                theInvested = new JMoney(theSavePoint.getInvested());
                theDividend = new JMoney(theSavePoint.getDividend());
                theGains = new JMoney(theSavePoint.getGains());

                /* Copy price if available */
                if (theSavePoint.getPrice() != null) {
                    thePrice = new JPrice(theSavePoint.getPrice());
                }

                /* Trim back the capital events */
                if (pDate != null) {
                    theEvents.purgeAfterDate(pDate);
                }
            }
        }
    }

    /**
     * The ExternalAccount Bucket class.
     */
    public static final class ExternalAccount
            extends ActDetail {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(ExternalAccount.class.getSimpleName(), ActDetail.FIELD_DEFS);

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
        private JMoney theIncome = null;

        /**
         * The expense.
         */
        private JMoney theExpense = null;

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
         * Obtain previous income.
         * @return the income
         */
        public JMoney getPrevIncome() {
            return (getBase() != null) ? getBase().getIncome() : null;
        }

        /**
         * Obtain previous expense.
         * @return the expense
         */
        public JMoney getPrevExpense() {
            return (getBase() != null) ? getBase().getExpense() : null;
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        private ExternalAccount(final Account pAccount) {
            /* Call super-constructor */
            super(BucketType.EXTERNALDETAIL, pAccount);

            /* Initialise the money values */
            theIncome = new JMoney();
            theExpense = new JMoney();
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        private ExternalAccount(final ExternalAccount pBase) {
            /* Call super-constructor */
            super(pBase.cloneIt());

            /* Initialise the Money values */
            theIncome = new JMoney();
            theExpense = new JMoney();
        }

        /**
         * Create a clone of the External account.
         * @return the cloned ExternalAccount.
         */
        private ExternalAccount cloneIt() {
            /* Call super-constructor */
            ExternalAccount myClone = new ExternalAccount(getAccount());

            /* Copy the External values */
            myClone.theIncome = new JMoney(theIncome);
            myClone.theExpense = new JMoney(theExpense);

            /* Return the clone */
            return myClone;
        }

        @Override
        public boolean isActive() {
            /* Copy if the income or expense is non-zero */
            return (theIncome.isNonZero() || theExpense.isNonZero());
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or previous value is non-zero */
            boolean bResult = (theIncome.isNonZero() || theExpense.isNonZero());
            bResult |= ((getPrevIncome() != null) && (getPrevIncome().isNonZero()));
            bResult |= ((getPrevExpense() != null) && (getPrevExpense().isNonZero()));
            return bResult;
        }

        /**
         * Adjust account for debit.
         * @param pEvent the event causing the debit
         */
        @Override
        protected void adjustForDebit(final Event pEvent) {
            TransactionType myTransType = pEvent.getTransType();
            JMoney myAmount = pEvent.getAmount();
            JMoney myTaxCred = pEvent.getTaxCredit();

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
                theIncome = new JMoney(theSavePoint.getIncome());
                theExpense = new JMoney(theSavePoint.getExpense());
            }
        }
    }

    /**
     * The AssetSummary Bucket class.
     */
    public static final class AssetSummary
            extends ActType {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AssetSummary.class.getSimpleName(), ActType.FIELD_DEFS);

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
         * @param pAccountType the account type
         */
        private AssetSummary(final AccountType pAccountType) {
            /* Call super-constructor */
            super(pAccountType);

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
        protected void addValues(final ValueAccount pBucket) {
            ValueAccount myPrevious = pBucket.getBase();

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
     * The ExternalTotal Bucket class.
     */
    public static final class ExternalTotal
            extends AnalysisBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(ExternalTotal.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

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
        public ExternalTotal getBase() {
            return (ExternalTotal) super.getBase();
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
        private ExternalTotal() {
            /* Call super-constructor */
            super(BucketType.EXTERNALTOTAL, 0);

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
        protected void addValues(final ExternalAccount pBucket) {
            ExternalAccount myPrevious = pBucket.getBase();

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
        private static final JDataFields FIELD_DEFS = new JDataFields(ExternalTotal.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

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
    public static final class TransDetail
            extends TransType {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(TransDetail.class.getSimpleName(), TransType.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * TaxCredit Field Id.
         */
        public static final JDataField FIELD_TAXCREDIT = FIELD_DEFS.declareLocalField("TaxCredit");

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
        private JMoney theAmount = null;

        /**
         * The tax credit.
         */
        private JMoney theTaxCredit = null;

        @Override
        public TransDetail getBase() {
            return (TransDetail) super.getBase();
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public JMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the tax credit.
         * @return the tax credit
         */
        public JMoney getTaxCredit() {
            return theTaxCredit;
        }

        /**
         * Obtain the previous amount.
         * @return the amount
         */
        public JMoney getPrevAmount() {
            return (getBase() != null) ? getBase().getAmount() : null;
        }

        /**
         * Obtain the previous tax credit.
         * @return the tax credit
         */
        public JMoney getPrevTax() {
            return (getBase() != null) ? getBase().getTaxCredit() : null;
        }

        /**
         * Constructor.
         * @param pTransType the transaction type
         */
        private TransDetail(final TransactionType pTransType) {
            /* Call super-constructor */
            super(pTransType);

            /* Initialise the Money values */
            theAmount = new JMoney();
            theTaxCredit = new JMoney();
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        private TransDetail(final TransDetail pBase) {
            /* Call super-constructor */
            super(pBase.cloneIt());

            /* Initialise the Money values */
            theAmount = new JMoney();
            theTaxCredit = new JMoney();
        }

        /**
         * Create a clone of the Transaction Detail.
         * @return the cloned TransDetail.
         */
        private TransDetail cloneIt() {
            /* Create clone */
            TransDetail myClone = new TransDetail(getTransType());

            /* Copy the External values */
            myClone.theAmount = new JMoney(theAmount);
            myClone.theTaxCredit = new JMoney(theTaxCredit);

            /* Return the clone */
            return myClone;
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
    public static final class TransSummary
            extends Tax {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(TransSummary.class.getSimpleName(), Tax.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_PREVAMOUNT = FIELD_DEFS.declareLocalField("PreviousAmount");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_PREVAMOUNT.equals(pField)) {
                return thePrevAmount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private JMoney theAmount = null;

        /**
         * The previous amount.
         */
        private JMoney thePrevAmount = null;

        @Override
        public TransSummary getBase() {
            return (TransSummary) super.getBase();
        }

        /**
         * Obtain the amount.
         * @return the amount.
         */
        public JMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the previous amount.
         * @return the amount.
         */
        public JMoney getPrevAmount() {
            return thePrevAmount;
        }

        /**
         * Constructor.
         * @param pTaxType the tax type
         */
        private TransSummary(final TaxType pTaxType) {
            /* Call super-constructor */
            super(pTaxType);

            /* Initialise the Money values */
            theAmount = new JMoney();
            thePrevAmount = new JMoney();
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
            if (myPrevious != null) {
                /* Add previous values */
                thePrevAmount.addAmount(myPrevious.getAmount());
                thePrevAmount.addAmount(myPrevious.getTaxCredit());
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
            if ((myPrevious != null)
                && (getBase() != null)) {
                /* Add previous values */
                getBase().subtractValues(myPrevious);
            }
        }
    }

    /**
     * The Transaction Total Bucket class.
     */
    public static final class TransTotal
            extends Tax {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(TransTotal.class.getSimpleName(), Tax.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_PREVAMOUNT = FIELD_DEFS.declareLocalField("PreviousAmount");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_PREVAMOUNT.equals(pField)) {
                return thePrevAmount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private JMoney theAmount = null;

        /**
         * The previous amount.
         */
        private JMoney thePrevAmount = null;

        @Override
        public TransTotal getBase() {
            return (TransTotal) super.getBase();
        }

        /**
         * Obtain amount.
         * @return the amount
         */
        public JMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain previous amount.
         * @return the amount
         */
        public JMoney getPrevAmount() {
            return thePrevAmount;
        }

        /**
         * Constructor.
         * @param pTaxType the tax type
         */
        private TransTotal(final TaxType pTaxType) {
            /* Call super-constructor */
            super(pTaxType);

            /* Initialise the Money values */
            theAmount = new JMoney();
            thePrevAmount = new JMoney();
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
            if (myPrevious != null) {
                /* Add previous values */
                thePrevAmount.addAmount(myPrevious.getAmount());
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
            if ((myPrevious != null)
                && (getBase() != null)) {
                /* Add previous values */
                getBase().subtractValues(myPrevious);
            }
        }
    }

    /**
     * The Taxation Detail Bucket class.
     */
    public static final class TaxDetail
            extends Tax {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(TaxDetail.class.getSimpleName(), Tax.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

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
        private JMoney theAmount = null;

        /**
         * The taxation.
         */
        private JMoney theTaxation = null;

        /**
         * The rate.
         */
        private JRate theRate = null;

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
        public JMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the taxation.
         * @return the taxation
         */
        public JMoney getTaxation() {
            return theTaxation;
        }

        /**
         * Obtain the rate.
         * @return the rate
         */
        public JRate getRate() {
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
        // public Money getPrevAmount() {
        // return (getBase() != null) ? getBase().getAmount() : null;
        // }

        /**
         * Obtain the previous taxation.
         * @return the taxation
         */
        // public Money getPrevTax() {
        // return (getBase() != null) ? getBase().getTaxation() : null;
        // }

        /**
         * Obtain the previous rate.
         * @return the rate
         */
        // public Rate getPrevRate() {
        // return (getBase() != null) ? getBase().getRate() : null;
        // }

        /**
         * Constructor.
         * @param pTaxType the tax type
         */
        private TaxDetail(final TaxType pTaxType) {
            /* Call super-constructor */
            super(pTaxType);

            /* Add the link to the previous item */
            // setBase(new TaxDetail(this));
        }

        // /**
        // * Constructor.
        // * @param pMaster the master
        // */
        // private TaxDetail(final TaxDetail pMaster) {
        /* Call super-constructor */
        // super((BucketList) pMaster.getList(), pMaster.getTaxType());
        // }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theAmount.isNonZero() || theTaxation.isNonZero());
        }

        /**
         * Set a taxation amount and calculate the tax on it.
         * @param pAmount Amount to set
         * @return the taxation on this bucket
         */
        protected JMoney setAmount(final JMoney pAmount) {
            /* Set the value */
            theAmount = new JMoney(pAmount);

            /* Calculate the tax if we have a rate */
            theTaxation = (theRate != null) ? theAmount.valueAtRate(theRate) : new JMoney();

            /* Return the taxation amount */
            return theTaxation;
        }

        /**
         * Set explicit taxation value.
         * @param pAmount Amount to set
         */
        protected void setTaxation(final JMoney pAmount) {
            /* Set the value */
            theTaxation = new JMoney(pAmount);
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
        protected void setRate(final JRate pRate) {
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
            if (pAccount.isExternal()
                || pAccount.isBenefit()) {
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
