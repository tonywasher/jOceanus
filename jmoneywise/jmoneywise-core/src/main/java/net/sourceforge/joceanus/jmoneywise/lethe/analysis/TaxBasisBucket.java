/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.TaxBasisAccountBucket.TaxBasisAccountBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseChargeableGainSlice.MoneyWiseChargeableGainSliceList;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * The TaxBasis Bucket class.
 */
public class TaxBasisBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<TaxBasisBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(TaxBasisBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, TaxBasisBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TAXBASIS, TaxBasisBucket::getTaxBasis);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES, TaxBasisBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY, TaxBasisBucket::getHistoryMap);
        FIELD_DEFS.declareLocalField(AnalysisResource.TAXBASIS_ACCOUNTLIST, TaxBasisBucket::getAccounts);
        FIELD_DEFS.declareLocalFieldsForEnum(TaxBasisAttribute.class, TaxBasisBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * Tax Basis.
     */
    private final TaxBasis theTaxBasis;

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
    private final BucketHistory<TaxBasisValues, TaxBasisAttribute> theHistory;

    /**
     * Do we have accounts?
     */
    private final boolean hasAccounts;

    /**
     * Are we an expense bucket?
     */
    private final boolean isExpense;

    /**
     * AccountBucketList.
     */
    private final TaxBasisAccountBucketList theAccounts;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTaxBasis the basis
     */
    protected TaxBasisBucket(final Analysis pAnalysis,
                             final TaxBasis pTaxBasis) {
        /* Store the parameters */
        theTaxBasis = pTaxBasis;
        theAnalysis = pAnalysis;
        isExpense = theTaxBasis != null
                    && theTaxBasis.getTaxClass().isExpense();

        /* Create the history map */
        final AssetCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                                                      ? AccountBucket.DEFAULT_CURRENCY
                                                      : myDefault.getCurrency();
        final TaxBasisValues myValues = new TaxBasisValues(myCurrency);
        theHistory = new BucketHistory<>(myValues);

        /* Create the account list */
        hasAccounts = theTaxBasis != null
                      && !(this instanceof TaxBasisAccountBucket)
                      && theTaxBasis.getTaxClass().analyseAccounts();
        theAccounts = hasAccounts
                                  ? new TaxBasisAccountBucketList(theAnalysis, this)
                                  : null;

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
    protected TaxBasisBucket(final Analysis pAnalysis,
                             final TaxBasisBucket pBase,
                             final TethysDate pDate) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pDate);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                                  ? new TaxBasisAccountBucketList(theAnalysis, this, pBase.getAccounts(), pDate)
                                  : null;

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
    protected TaxBasisBucket(final Analysis pAnalysis,
                             final TaxBasisBucket pBase,
                             final TethysDateRange pRange) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new BucketHistory<>(pBase.getHistoryMap(), pRange);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                                  ? new TaxBasisAccountBucketList(theAnalysis, this, pBase.getAccounts(), pRange)
                                  : null;

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<? extends TaxBasisBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Integer getIndexedId() {
        return theTaxBasis.getId();
    }

    /**
     * Obtain name.
     * @return the name
     */
    public String getName() {
        return theTaxBasis == null
                                   ? NAME_TOTALS.getId()
                                   : theTaxBasis.getName();
    }

    /**
     * Obtain tax basis.
     * @return the basis
     */
    public TaxBasis getTaxBasis() {
        return theTaxBasis;
    }

    /**
     * Do we have accounts.
     * @return true/false
     */
    public boolean hasAccounts() {
        return hasAccounts;
    }

    /**
     * Is this an expense bucket.
     * @return true/false
     */
    public boolean isExpense() {
        return isExpense;
    }

    /**
     * Obtain account list.
     * @return the account list
     */
    private TaxBasisAccountBucketList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain account list iterator.
     * @return the iterator
     */
    public Iterator<TaxBasisAccountBucket> accountIterator() {
        return hasAccounts
                           ? theAccounts.iterator()
                           : null;
    }

    /**
     * find an account bucket.
     * @param pAccount the account
     * @return the bucket
     */
    public TaxBasisAccountBucket findAccountBucket(final TransactionAsset pAccount) {
        return hasAccounts
                           ? theAccounts.findBucket(pAccount)
                           : null;
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
    public TethysMoney getMoneyValue(final TaxBasisAttribute pAttr) {
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
     * Obtain values for transaction.
     * @param pTrans the event
     * @return the values (or null)
     */
    public TaxBasisValues getValuesForTransaction(final Transaction pTrans) {
        /* Obtain values for event */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public TaxBasisValues getPreviousValuesForTransaction(final Transaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public TethysDecimal getDeltaForTransaction(final Transaction pTrans,
                                                final TaxBasisAttribute pAttr) {
        /* Obtain delta for event */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     * @return the history map
     */
    private BucketHistory<TaxBasisValues, TaxBasisAttribute> getHistoryMap() {
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
    protected void setValue(final TaxBasisAttribute pAttr,
                            final TethysMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final TaxBasisAttribute pAttr) {
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
    private Object getValue(final TaxBasisAttribute pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * Add income transaction.
     * @param pTrans the transaction
     */
    protected void addIncomeTransaction(final TransactionHelper pTrans) {
        /* Access details */
        final TethysMoney myAmount = pTrans.getCreditAmount();
        final TethysMoney myTaxCredit = pTrans.getTaxCredit();
        final TethysMoney myNatIns = pTrans.getEmployeeNatIns();
        final TethysMoney myBenefit = pTrans.getDeemedBenefit();
        final TethysMoney myWithheld = pTrans.getWithheld();

        /* Determine style of transaction */
        AssetDirection myDir = pTrans.getDirection();

        /* If the account is special */
        final TransactionCategoryClass myClass = pTrans.getCategoryClass();
        if (myClass.isSwitchDirection()) {
            /* switch the direction */
            myDir = myDir.reverse();
        }

        /* Obtain zeroed counters */
        TethysMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myGross = new TethysMoney(myGross);
        myGross.setZero();
        final TethysMoney myNett = new TethysMoney(myGross);
        final TethysMoney myTax = new TethysMoney(myGross);

        /* If this is an expense */
        if (myDir.isTo()) {
            /* Adjust the gross and net */
            myGross.subtractAmount(myAmount);
            myNett.subtractAmount(myAmount);

            /* If we have a tax credit */
            if (myTaxCredit != null
                && myTaxCredit.isNonZero()) {
                /* Adjust the gross */
                myGross.subtractAmount(myTaxCredit);
                myTax.subtractAmount(myTaxCredit);
            }

            /* If we have a natInsurance payment */
            if (myNatIns != null
                && myNatIns.isNonZero()) {
                /* Adjust the gross */
                myGross.subtractAmount(myNatIns);
            }

            /* If we have a Benefit payment */
            if (myBenefit != null
                && myBenefit.isNonZero()) {
                /* Adjust the gross */
                myGross.subtractAmount(myBenefit);
            }

            /* If we have a Withheld */
            if (myWithheld != null
                && myWithheld.isNonZero()) {
                /* Adjust the gross and net */
                myGross.subtractAmount(myWithheld);
                myNett.subtractAmount(myWithheld);
            }

            /* else this is a standard income */
        } else {
            /* Adjust the gross and net */
            myGross.addAmount(myAmount);
            myNett.addAmount(myAmount);

            /* If we have a tax credit */
            if (myTaxCredit != null
                && myTaxCredit.isNonZero()) {
                /* Adjust the values */
                myGross.addAmount(myTaxCredit);
                myTax.addAmount(myTaxCredit);
            }

            /* If we have a natInsurance payment */
            if (myNatIns != null
                && myNatIns.isNonZero()) {
                /* Adjust the gross */
                myGross.addAmount(myNatIns);
            }

            /* If we have a Benefit payment */
            if (myBenefit != null
                && myBenefit.isNonZero()) {
                /* Adjust the gross */
                myGross.addAmount(myBenefit);
            }

            /* If we have a Withheld */
            if (myWithheld != null
                && myWithheld.isNonZero()) {
                /* Adjust the gross and net */
                myGross.addAmount(myWithheld);
                myNett.addAmount(myWithheld);
            }
        }

        /* Register the delta values */
        registerDeltaValues(pTrans, myGross, myNett, myTax);

        /* If we have accounts */
        if (hasAccounts) {
            /* register the changes against the accounts */
            theAccounts.registerDeltaValues(pTrans, myGross, myNett, myTax);
        }
    }

    /**
     * Add expense transaction.
     * @param pTrans the transaction
     */
    protected void addExpenseTransaction(final TransactionHelper pTrans) {
        /* Access details */
        final TethysMoney myAmount = pTrans.getDebitAmount();
        final TethysMoney myTaxCredit = pTrans.getTaxCredit();

        /* Determine style of event */
        final AssetDirection myDir = pTrans.getDirection();

        /* Obtain zeroed counters */
        TethysMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myGross = new TethysMoney(myGross);
        myGross.setZero();
        final TethysMoney myNett = new TethysMoney(myGross);
        final TethysMoney myTax = new TethysMoney(myGross);

        /* If this is a refunded expense */
        if (myDir.isFrom()) {
            /* Adjust the gross and net */
            myGross.addAmount(myAmount);
            myNett.addAmount(myAmount);

            /* If we have a tax relief */
            if (myTaxCredit != null
                && myTaxCredit.isNonZero()) {
                /* Adjust the values */
                myGross.addAmount(myTaxCredit);
                myNett.addAmount(myTaxCredit);
                myTax.addAmount(myTaxCredit);
            }

            /* else this is a standard expense */
        } else {
            /* Adjust the gross and net */
            myGross.subtractAmount(myAmount);
            myNett.subtractAmount(myAmount);

            /* If we have a tax relief */
            if (myTaxCredit != null
                && myTaxCredit.isNonZero()) {
                /* Adjust the values */
                myGross.subtractAmount(myTaxCredit);
                myNett.subtractAmount(myTaxCredit);
                myTax.subtractAmount(myTaxCredit);
            }
        }

        /* Register the delta values */
        registerDeltaValues(pTrans, myGross, myNett, myTax);

        /* If we have accounts */
        if (hasAccounts) {
            /* register the changes against the accounts */
            theAccounts.registerDeltaValues(pTrans, myGross, myNett, myTax);
        }
    }

    /**
     * Register delta transaction value.
     * @param pTrans the transaction helper
     * @param pGross the gross delta value
     * @param pNett the net delta value
     * @param pTax the tax delta value
     */
    protected void registerDeltaValues(final TransactionHelper pTrans,
                                       final TethysMoney pGross,
                                       final TethysMoney pNett,
                                       final TethysMoney pTax) {
        /* If we have a change to the gross value */
        if (pGross.isNonZero()) {
            /* Adjust Gross figure */
            TethysMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
            myGross = new TethysMoney(myGross);
            myGross.addAmount(pGross);
            setValue(TaxBasisAttribute.GROSS, myGross);
        }

        /* If we have a change to the net value */
        if (pNett.isNonZero()) {
            /* Adjust Net figure */
            TethysMoney myNett = theValues.getMoneyValue(TaxBasisAttribute.NETT);
            myNett = new TethysMoney(myNett);
            myNett.addAmount(pNett);
            setValue(TaxBasisAttribute.NETT, myNett);
        }

        /* If we have a change to the tax value */
        if (pTax.isNonZero()) {
            /* Adjust Tax figure */
            TethysMoney myTax = theValues.getMoneyValue(TaxBasisAttribute.TAXCREDIT);
            myTax = new TethysMoney(myTax);
            myTax.addAmount(pTax);
            setValue(TaxBasisAttribute.TAXCREDIT, myTax);
        }

        /* Register the transaction */
        registerTransaction(pTrans);
    }

    /**
     * Adjust transaction value.
     * @param pTrans the transaction
     * @param pValue the value
     * @param pAdjust adjustment control
     */
    protected void adjustValue(final TransactionHelper pTrans,
                               final TethysMoney pValue,
                               final TaxBasisAdjust pAdjust) {
        /* Adjust the value */
        adjustValue(pValue, pAdjust);

        /* Register the transaction */
        registerTransaction(pTrans);

        /* If we have accounts */
        if (hasAccounts) {
            /* register the adjustment against the accounts */
            theAccounts.adjustValue(pTrans, pValue, pAdjust);
        }
    }

    /**
     * Adjust value.
     * @param pValue the value
     * @param pAdjust adjustment control
     */
    protected void adjustValue(final TethysMoney pValue,
                               final TaxBasisAdjust pAdjust) {
        /* If we are adjusting Gross */
        if (pAdjust.adjustGross()) {
            /* Access the existing value */
            TethysMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
            myGross = new TethysMoney(myGross);

            /* Subtract or add the value depending as to whether we are an expense bucket */
            if (isExpense) {
                myGross.subtractAmount(pValue);
            } else {
                myGross.addAmount(pValue);
            }

            /* Record the new value */
            setValue(TaxBasisAttribute.GROSS, myGross);
        }

        /* If we are adjusting Nett */
        if (pAdjust.adjustNett()) {
            /* Access the existing value */
            TethysMoney myNett = theValues.getMoneyValue(TaxBasisAttribute.NETT);
            myNett = new TethysMoney(myNett);

            /* Subtract or add the value depending as to whether we are an expense bucket */
            if (isExpense) {
                myNett.subtractAmount(pValue);
            } else {
                myNett.addAmount(pValue);
            }

            /* Record the new value */
            setValue(TaxBasisAttribute.NETT, myNett);
        }
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
     * Add values.
     * @param pBucket tax category bucket
     */
    protected void addValues(final TaxBasisBucket pBucket) {
        /* Add the values */
        TethysMoney myAmount = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myAmount.addAmount(pBucket.getMoneyValue(TaxBasisAttribute.GROSS));
        myAmount = theValues.getMoneyValue(TaxBasisAttribute.NETT);
        myAmount.addAmount(pBucket.getMoneyValue(TaxBasisAttribute.NETT));
    }

    /**
     * Adjust to base.
     */
    protected void adjustToBase() {
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
     * Value adjust Modes.
     */
    protected enum TaxBasisAdjust {
        /**
         * Adjust both Gross and Nett.
         */
        STANDARD,

        /**
         * Only adjust Nett figure.
         */
        NETT,

        /**
         * Only adjust Gross figure.
         */
        GROSS;

        /**
         * should we adjust Gross?
         * @return true/false
         */
        private boolean adjustGross() {
            return this != NETT;
        }

        /**
         * should we adjust Nett?
         * @return true/false
         */
        private boolean adjustNett() {
            return this != GROSS;
        }
    }

    /**
     * TaxBasisValues class.
     */
    public static final class TaxBasisValues
            extends BucketValues<TaxBasisValues, TaxBasisAttribute> {
        /**
         * Constructor.
         * @param pCurrency the reporting currency
         */
        protected TaxBasisValues(final Currency pCurrency) {
            /* Initialise class */
            super(TaxBasisAttribute.class);

            /* Create all possible values */
            super.setValue(TaxBasisAttribute.GROSS, new TethysMoney(pCurrency));
            super.setValue(TaxBasisAttribute.NETT, new TethysMoney(pCurrency));
            super.setValue(TaxBasisAttribute.TAXCREDIT, new TethysMoney(pCurrency));
        }

        /**
         * Constructor.
         * @param pSource the source map.
         * @param pCountersOnly only copy counters
         */
        private TaxBasisValues(final TaxBasisValues pSource,
                               final boolean pCountersOnly) {
            /* Initialise class */
            super(pSource, pCountersOnly);
        }

        @Override
        protected TaxBasisValues getCounterSnapShot() {
            return new TaxBasisValues(this, true);
        }

        @Override
        protected TaxBasisValues getFullSnapShot() {
            return new TaxBasisValues(this, false);
        }

        @Override
        protected void adjustToBaseValues(final TaxBasisValues pBase) {
            /* Adjust gross/net/tax values */
            adjustMoneyToBase(pBase, TaxBasisAttribute.GROSS);
            adjustMoneyToBase(pBase, TaxBasisAttribute.NETT);
            adjustMoneyToBase(pBase, TaxBasisAttribute.TAXCREDIT);
        }

        @Override
        protected void resetBaseValues() {
            /* Create a zero value in the correct currency */
            TethysMoney myValue = super.getMoneyValue(TaxBasisAttribute.GROSS);
            myValue = new TethysMoney(myValue);
            myValue.setZero();

            /* Reset Income and expense values */
            super.setValue(TaxBasisAttribute.GROSS, myValue);
            super.setValue(TaxBasisAttribute.NETT, new TethysMoney(myValue));
            super.setValue(TaxBasisAttribute.TAXCREDIT, new TethysMoney(myValue));
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            final TethysMoney myGross = super.getMoneyValue(TaxBasisAttribute.GROSS);
            final TethysMoney myNet = super.getMoneyValue(TaxBasisAttribute.NETT);
            final TethysMoney myTax = super.getMoneyValue(TaxBasisAttribute.TAXCREDIT);
            return myGross.isNonZero() || myNet.isNonZero() || myTax.isNonZero();
        }
    }

    /**
     * TaxBasisBucketList class.
     */
    public static class TaxBasisBucketList
            implements MetisFieldItem, MoneyWiseTaxSource, MetisDataList<TaxBasisBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<TaxBasisBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(TaxBasisBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME, TaxBasisBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_CHARGES, TaxBasisBucketList::getGainSlices);
            FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_TOTALS, TaxBasisBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<TaxBasisBucket> theList;

        /**
         * The data.
         */
        private final MoneyWiseData theData;

        /**
         * The chargeableGains.
         */
        private final MoneyWiseChargeableGainSliceList theCharges;

        /**
         * The tax basis.
         */
        private final TaxBasisBucket theTotals;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         * @param pGains the new Gains list
         */
        private TaxBasisBucketList(final Analysis pAnalysis,
                                   final MoneyWiseChargeableGainSliceList pGains) {
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theCharges = pGains;
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getTaxBasis().compareTo(r.getTaxBasis()));
        }

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        protected TaxBasisBucketList(final Analysis pAnalysis) {
            this(pAnalysis, new MoneyWiseChargeableGainSliceList());
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected TaxBasisBucketList(final Analysis pAnalysis,
                                     final TaxBasisBucketList pBase,
                                     final TethysDate pDate) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<TaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final TaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final TaxBasisBucket myBucket = new TaxBasisBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected TaxBasisBucketList(final Analysis pAnalysis,
                                     final TaxBasisBucketList pBase,
                                     final TethysDateRange pRange) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<TaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final TaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final TaxBasisBucket myBucket = new TaxBasisBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<TaxBasisBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<TaxBasisBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         * @return the analysis
         */
        protected Analysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public TaxBasisBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        @Override
        public MoneyWiseChargeableGainSliceList getGainSlices() {
            return theCharges;
        }

        /**
         * Obtain the Totals.
         * @return the totals bucket
         */
        public TaxBasisBucket getTotals() {
            return theTotals;
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
         * Obtain the TaxBasisBucket for a given taxBasis.
         * @param pClass the taxBasis
         * @return the bucket
         */
        public TaxBasisBucket getBucket(final TaxBasisClass pClass) {
            /* Locate the bucket in the list */
            final TaxBasis myBasis = theData.getTaxBases().findItemByClass(pClass);
            TaxBasisBucket myItem = findItemById(myBasis.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TaxBasisBucket(theAnalysis, myBasis);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the matching BasisBucket.
         * @param pTaxBasis the taxBasis
         * @return the matching bucket
         */
        public TaxBasisBucket getMatchingBasis(final TaxBasisBucket pTaxBasis) {
            /* Access the matching taxBasis bucket */
            TaxBasisBucket myBasis = findItemById(pTaxBasis.getTaxBasis().getIndexedId());
            if (myBasis == null) {
                myBasis = new TaxBasisBucket(theAnalysis, pTaxBasis.getTaxBasis());
            }

            /* If we are matching a TaxBasisAccount Bucket */
            if (pTaxBasis instanceof TaxBasisAccountBucket) {
                /* Look up the asset bucket */
                final TransactionAsset myAsset = ((TaxBasisAccountBucket) pTaxBasis).getAccount();
                TaxBasisAccountBucket myAccountBucket = myBasis.findAccountBucket(myAsset);

                /* If there is no such bucket in the analysis */
                if (myAccountBucket == null) {
                    /* Allocate an orphan bucket */
                    myAccountBucket = new TaxBasisAccountBucket(theAnalysis, myBasis, myAsset);
                }

                /* Set bucket as the account bucket */
                myBasis = myAccountBucket;
            }

            /* Return the basis */
            return myBasis;
        }

        /**
         * Obtain the default BasisBucket.
         * @return the default bucket
         */
        public TaxBasisBucket getDefaultBasis() {
            /* Return the first basis in the list if it exists */
            return isEmpty()
                             ? null
                             : theList.getUnderlyingList().get(0);
        }

        /**
         * Adjust basis buckets.
         * @param pTrans the transaction helper
         * @param pCategory primary category
         */
        protected void adjustBasis(final TransactionHelper pTrans,
                                   final TransactionCategory pCategory) {
            /* Switch on the category type */
            switch (pCategory.getCategoryTypeClass()) {
                case TAXEDINCOME:
                case GROSSINCOME:
                    addIncome(pTrans, TaxBasisClass.SALARY);
                    break;
                case OTHERINCOME:
                    addIncome(pTrans, TaxBasisClass.OTHERINCOME);
                    break;
                case INTEREST:
                case TAXEDINTEREST:
                case TAXEDLOYALTYBONUS:
                    addIncome(pTrans, TaxBasisClass.TAXEDINTEREST);
                    break;
                case GROSSINTEREST:
                case GROSSLOYALTYBONUS:
                    addIncome(pTrans, TaxBasisClass.UNTAXEDINTEREST);
                    break;
                case PEER2PEERINTEREST:
                    addIncome(pTrans, TaxBasisClass.PEER2PEERINTEREST);
                    break;
                case DIVIDEND:
                case SHAREDIVIDEND:
                    addIncome(pTrans, TaxBasisClass.DIVIDEND);
                    break;
                case UNITTRUSTDIVIDEND:
                    addIncome(pTrans, TaxBasisClass.UNITTRUSTDIVIDEND);
                    break;
                case FOREIGNDIVIDEND:
                    addIncome(pTrans, TaxBasisClass.FOREIGNDIVIDEND);
                    break;
                case RENTALINCOME:
                    addIncome(pTrans, TaxBasisClass.RENTALINCOME);
                    break;
                case ROOMRENTALINCOME:
                    addIncome(pTrans, TaxBasisClass.ROOMRENTAL);
                    break;
                case INCOMETAX:
                    addExpense(pTrans, TaxBasisClass.TAXPAID);
                    break;
                case TAXFREEINTEREST:
                case TAXFREEDIVIDEND:
                case LOANINTERESTEARNED:
                case INHERITED:
                case CASHBACK:
                case LOYALTYBONUS:
                case TAXFREELOYALTYBONUS:
                case GIFTEDINCOME:
                    addIncome(pTrans, TaxBasisClass.TAXFREE);
                    break;
                case PENSIONCONTRIB:
                    addIncome(pTrans, TaxBasisClass.TAXFREE);
                    break;
                case BADDEBTCAPITAL:
                    addExpense(pTrans, TaxBasisClass.CAPITALGAINS);
                    break;
                case BADDEBTINTEREST:
                    addExpense(pTrans, TaxBasisClass.PEER2PEERINTEREST);
                    break;
                case EXPENSE:
                case LOCALTAXES:
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                case TAXRELIEF:
                case RECOVEREDEXPENSES:
                    addExpense(pTrans, TaxBasisClass.EXPENSE);
                    break;
                case RENTALEXPENSE:
                    addExpense(pTrans, TaxBasisClass.RENTALINCOME);
                    break;
                case UNITSADJUST:
                case SECURITYREPLACE:
                case STOCKTAKEOVER:
                case STOCKSPLIT:
                case STOCKDEMERGER:
                case STOCKRIGHTSISSUE:
                case PORTFOLIOXFER:
                case TRANSFER:
                default:
                    break;
            }
        }

        /**
         * Adjust basis for income.
         * @param pClass the class
         * @param pTrans the transaction
         */
        private void addIncome(final TransactionHelper pTrans,
                               final TaxBasisClass pClass) {
            /* Access the bucket and adjust it */
            final TaxBasisBucket myBucket = getBucket(pClass);
            myBucket.addIncomeTransaction(pTrans);
        }

        /**
         * Adjust basis for expense.
         * @param pClass the class
         * @param pTrans the transaction
         */
        private void addExpense(final TransactionHelper pTrans,
                                final TaxBasisClass pClass) {
            /* Access the bucket and adjust it */
            final TaxBasisBucket myBucket = getBucket(pClass);
            myBucket.addExpenseTransaction(pTrans);
        }

        /**
         * Adjust basis buckets.
         * @param pTrans the transaction
         * @param pClass the class
         * @param pIncome the income
         */
        protected void adjustValue(final TransactionHelper pTrans,
                                   final TaxBasisClass pClass,
                                   final TethysMoney pIncome) {
            /* Access the bucket and adjust it */
            final TaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, TaxBasisAdjust.STANDARD);
        }

        /**
         * Adjust basis buckets for Gross only.
         * @param pTrans the transaction
         * @param pClass the class
         * @param pIncome the income
         */
        protected void adjustGrossValue(final TransactionHelper pTrans,
                                        final TaxBasisClass pClass,
                                        final TethysMoney pIncome) {
            /* Access the bucket and adjust it */
            final TaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, TaxBasisAdjust.GROSS);
        }

        /**
         * Adjust basis buckets for Nett only.
         * @param pTrans the transaction
         * @param pClass the class
         * @param pIncome the income
         */
        protected void adjustNettValue(final TransactionHelper pTrans,
                                       final TaxBasisClass pClass,
                                       final TethysMoney pIncome) {
            /* Access the bucket and adjust it */
            final TaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, TaxBasisAdjust.NETT);
        }

        /**
         * Adjust autoExpense.
         * @param pTrans the transaction
         * @param isExpense true/false
         */
        protected void adjustAutoExpense(final TransactionHelper pTrans,
                                         final boolean isExpense) {
            /* Determine value */
            TethysMoney myAmount = pTrans.getLocalAmount();
            if (!isExpense) {
                myAmount = new TethysMoney(myAmount);
                myAmount.negate();
            }

            /* Access the bucket and adjust it */
            final TaxBasisBucket myBucket = getBucket(TaxBasisClass.EXPENSE);
            myBucket.adjustValue(pTrans, myAmount, TaxBasisAdjust.STANDARD);
        }

        /**
         * Adjust for market growth.
         * @param pIncome the income
         * @param pExpense the expense
         */
        protected void adjustMarket(final TethysMoney pIncome,
                                    final TethysMoney pExpense) {
            /* Calculate the delta */
            final TethysMoney myDelta = new TethysMoney(pIncome);
            myDelta.subtractAmount(pExpense);

            /* Access the bucket and adjust it */
            final TaxBasisBucket myBucket = getBucket(TaxBasisClass.MARKET);
            myBucket.adjustValue(myDelta, TaxBasisAdjust.STANDARD);
        }

        /**
         * record ChargeableGain.
         * @param pTrans the transaction
         * @param pGain the gain
         */
        protected void recordChargeableGain(final Transaction pTrans,
                                            final TethysMoney pGain) {
            /* record the chargeable gain */
            theCharges.addTransaction(pTrans, pGain);
        }

        /**
         * produce Totals.
         */
        protected void produceTotals() {
            /* Loop through the buckets */
            final Iterator<TaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TaxBasisBucket myBucket = myIterator.next();

                /* Sort the accounts */
                if (myBucket.hasAccounts()) {
                    myBucket.getAccounts().sortBuckets();
                }

                /* Adjust the Total Profit buckets */
                theTotals.addValues(myBucket);
            }

            /* Sort the bases */
            theList.sortList();
        }

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            /* Loop through the buckets */
            final Iterator<TaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final TaxBasisBucket myCurr = myIterator.next();

                /* Remove the bucket if it is inactive */
                if (!myCurr.isActive()) {
                    myIterator.remove();
                }
            }
        }

        @Override
        public TethysMoney getAmountForTaxBasis(final TaxBasisClass pBasis) {
            /* Access the bucket */
            final TaxBasisBucket myItem = findItemById(pBasis.getClassId());

            /* If the bucket is not found */
            if (myItem == null) {
                final AssetCurrency myAssetCurrency = theAnalysis.getCurrency();
                final Currency myCurrency = myAssetCurrency == null
                                                                    ? Currency.getInstance(Locale.getDefault())
                                                                    : myAssetCurrency.getCurrency();
                return new TethysMoney(myCurrency);
            }

            return myItem.getMoneyValue(TaxBasisAttribute.GROSS);
        }
    }
}
