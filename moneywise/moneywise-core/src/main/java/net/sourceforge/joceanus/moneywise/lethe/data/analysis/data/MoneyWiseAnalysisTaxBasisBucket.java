/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataFieldValue;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataList;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.list.MetisListIndexed;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxBasis;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxBasis.MoneyWiseTaxBasisList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisHistory;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisAccountBucket.MoneyWiseAnalysisTaxBasisAccountBucketList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisValues;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseChargeableGainSlice.MoneyWiseChargeableGainSliceList;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;

/**
 * The TaxBasis Bucket class.
 */
public class MoneyWiseAnalysisTaxBasisBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisTaxBasisBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTaxBasisBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTaxBasisBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseAnalysisTaxBasisBucket::getTaxBasis);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_BASEVALUES, MoneyWiseAnalysisTaxBasisBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_HISTORY, MoneyWiseAnalysisTaxBasisBucket::getHistoryMap);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.TAXBASIS_ACCOUNTLIST, MoneyWiseAnalysisTaxBasisBucket::getAccounts);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseAnalysisTaxBasisAttr.class, MoneyWiseAnalysisTaxBasisBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * Tax Basis.
     */
    private final MoneyWiseTaxBasis theTaxBasis;

    /**
     * Values.
     */
    private final MoneyWiseAnalysisTaxBasisValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseAnalysisTaxBasisValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseAnalysisHistory<MoneyWiseAnalysisTaxBasisValues, MoneyWiseAnalysisTaxBasisAttr> theHistory;

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
    private final MoneyWiseAnalysisTaxBasisAccountBucketList theAccounts;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pTaxBasis the basis
     */
    protected MoneyWiseAnalysisTaxBasisBucket(final MoneyWiseAnalysis pAnalysis,
                                              final MoneyWiseTaxBasis pTaxBasis) {
        /* Store the parameters */
        theTaxBasis = pTaxBasis;
        theAnalysis = pAnalysis;
        isExpense = theTaxBasis != null
                && theTaxBasis.getTaxClass().isExpense();

        /* Create the history map */
        final MoneyWiseCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                ? MoneyWiseAnalysisAccountBucket.DEFAULT_CURRENCY
                : myDefault.getCurrency();
        final MoneyWiseAnalysisTaxBasisValues myValues = new MoneyWiseAnalysisTaxBasisValues(myCurrency);
        theHistory = new MoneyWiseAnalysisHistory<>(myValues);

        /* Create the account list */
        hasAccounts = theTaxBasis != null
                && !(this instanceof MoneyWiseAnalysisTaxBasisAccountBucket)
                && theTaxBasis.getTaxClass().analyseAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseAnalysisTaxBasisAccountBucketList(theAnalysis, this)
                : null;

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pDate     the date for the bucket
     */
    protected MoneyWiseAnalysisTaxBasisBucket(final MoneyWiseAnalysis pAnalysis,
                                              final MoneyWiseAnalysisTaxBasisBucket pBase,
                                              final OceanusDate pDate) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pDate);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseAnalysisTaxBasisAccountBucketList(theAnalysis, this, pBase.getAccounts(), pDate)
                : null;

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pBase     the underlying bucket
     * @param pRange    the range for the bucket
     */
    protected MoneyWiseAnalysisTaxBasisBucket(final MoneyWiseAnalysis pAnalysis,
                                              final MoneyWiseAnalysisTaxBasisBucket pBase,
                                              final OceanusDateRange pRange) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Create the account list */
        hasAccounts = pBase.hasAccounts();
        theAccounts = hasAccounts
                ? new MoneyWiseAnalysisTaxBasisAccountBucketList(theAnalysis, this, pBase.getAccounts(), pRange)
                : null;

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseAnalysisTaxBasisBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Integer getIndexedId() {
        return theTaxBasis.getIndexedId();
    }

    /**
     * Obtain name.
     *
     * @return the name
     */
    public String getName() {
        return theTaxBasis == null
                ? NAME_TOTALS.getId()
                : theTaxBasis.getName();
    }

    /**
     * Obtain tax basis.
     *
     * @return the basis
     */
    public MoneyWiseTaxBasis getTaxBasis() {
        return theTaxBasis;
    }

    /**
     * Do we have accounts.
     *
     * @return true/false
     */
    public boolean hasAccounts() {
        return hasAccounts;
    }

    /**
     * Is this an expense bucket.
     *
     * @return true/false
     */
    public boolean isExpense() {
        return isExpense;
    }

    /**
     * Obtain account list.
     *
     * @return the account list
     */
    private MoneyWiseAnalysisTaxBasisAccountBucketList getAccounts() {
        return theAccounts;
    }

    /**
     * Obtain account list iterator.
     *
     * @return the iterator
     */
    public Iterator<MoneyWiseAnalysisTaxBasisAccountBucket> accountIterator() {
        return hasAccounts
                ? theAccounts.iterator()
                : null;
    }

    /**
     * find an account bucket.
     *
     * @param pAccount the account
     * @return the bucket
     */
    public MoneyWiseAnalysisTaxBasisAccountBucket findAccountBucket(final MoneyWiseTransAsset pAccount) {
        return hasAccounts
                ? theAccounts.findBucket(pAccount)
                : null;
    }

    /**
     * Is this bucket idle?
     *
     * @return true/false
     */
    public Boolean isIdle() {
        return theHistory.isIdle();
    }

    /**
     * Obtain the value map.
     *
     * @return the value map
     */
    public MoneyWiseAnalysisTaxBasisValues getValues() {
        return theValues;
    }

    /**
     * Obtain the value for a particular attribute.
     *
     * @param pAttr the attribute
     * @return the value
     */
    public OceanusMoney getMoneyValue(final MoneyWiseAnalysisTaxBasisAttr pAttr) {
        return theValues.getMoneyValue(pAttr);
    }

    /**
     * Obtain the base value map.
     *
     * @return the base value map
     */
    public MoneyWiseAnalysisTaxBasisValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for transaction.
     *
     * @param pTrans the event
     * @return the values (or null)
     */
    public MoneyWiseAnalysisTaxBasisValues getValuesForTransaction(final MoneyWiseTransaction pTrans) {
        /* Obtain values for event */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain previous values for transaction.
     *
     * @param pTrans the transaction
     * @return the values (or null)
     */
    public MoneyWiseAnalysisTaxBasisValues getPreviousValuesForTransaction(final MoneyWiseTransaction pTrans) {
        return theHistory.getPreviousValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     *
     * @param pTrans the transaction
     * @param pAttr  the attribute
     * @return the delta (or null)
     */
    public OceanusDecimal getDeltaForTransaction(final MoneyWiseTransaction pTrans,
                                                 final MoneyWiseAnalysisTaxBasisAttr pAttr) {
        /* Obtain delta for event */
        return theHistory.getDeltaValue(pTrans, pAttr);
    }

    /**
     * Obtain the history map.
     *
     * @return the history map
     */
    private MoneyWiseAnalysisHistory<MoneyWiseAnalysisTaxBasisValues, MoneyWiseAnalysisTaxBasisAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Obtain the analysis.
     *
     * @return the analysis
     */
    protected MoneyWiseAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain date range.
     *
     * @return the range
     */
    public OceanusDateRange getDateRange() {
        return theAnalysis.getDateRange();
    }

    /**
     * Set Attribute.
     *
     * @param pAttr  the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final MoneyWiseAnalysisTaxBasisAttr pAttr,
                            final OceanusMoney pValue) {
        /* Set the value into the list */
        theValues.setValue(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     *
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final MoneyWiseAnalysisTaxBasisAttr pAttr) {
        /* Access value of object */
        final Object myValue = getValue(pAttr);

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain an attribute value.
     *
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getValue(final MoneyWiseAnalysisTaxBasisAttr pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * Add income transaction.
     *
     * @param pTrans the transaction
     */
    protected void addIncomeTransaction(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Access details */
        final OceanusMoney myAmount = pTrans.getCreditAmount();
        final OceanusMoney myTaxCredit = pTrans.getTaxCredit();
        final OceanusMoney myNatIns = pTrans.getEmployeeNatIns();
        final OceanusMoney myBenefit = pTrans.getDeemedBenefit();
        final OceanusMoney myWithheld = pTrans.getWithheld();

        /* Determine style of transaction */
        MoneyWiseAssetDirection myDir = pTrans.getDirection();

        /* If the account is special */
        final MoneyWiseTransCategoryClass myClass = pTrans.getCategoryClass();
        if (myClass.isSwitchDirection()) {
            /* switch the direction */
            myDir = myDir.reverse();
        }

        /* Obtain zeroed counters */
        OceanusMoney myGross = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        myGross = new OceanusMoney(myGross);
        myGross.setZero();
        final OceanusMoney myNett = new OceanusMoney(myGross);
        final OceanusMoney myTax = new OceanusMoney(myGross);

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
     *
     * @param pTrans the transaction
     */
    protected void addExpenseTransaction(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Access details */
        final OceanusMoney myAmount = pTrans.getDebitAmount();
        final OceanusMoney myTaxCredit = pTrans.getTaxCredit();

        /* Determine style of event */
        final MoneyWiseAssetDirection myDir = pTrans.getDirection();

        /* Obtain zeroed counters */
        OceanusMoney myGross = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        myGross = new OceanusMoney(myGross);
        myGross.setZero();
        final OceanusMoney myNett = new OceanusMoney(myGross);
        final OceanusMoney myTax = new OceanusMoney(myGross);

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
     *
     * @param pTrans the transaction helper
     * @param pGross the gross delta value
     * @param pNett  the net delta value
     * @param pTax   the tax delta value
     */
    protected void registerDeltaValues(final MoneyWiseAnalysisTransactionHelper pTrans,
                                       final OceanusMoney pGross,
                                       final OceanusMoney pNett,
                                       final OceanusMoney pTax) {
        /* If we have a change to the gross value */
        if (pGross.isNonZero()) {
            /* Adjust Gross figure */
            OceanusMoney myGross = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
            myGross = new OceanusMoney(myGross);
            myGross.addAmount(pGross);
            setValue(MoneyWiseAnalysisTaxBasisAttr.GROSS, myGross);
        }

        /* If we have a change to the net value */
        if (pNett.isNonZero()) {
            /* Adjust Net figure */
            OceanusMoney myNett = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.NETT);
            myNett = new OceanusMoney(myNett);
            myNett.addAmount(pNett);
            setValue(MoneyWiseAnalysisTaxBasisAttr.NETT, myNett);
        }

        /* If we have a change to the tax value */
        if (pTax.isNonZero()) {
            /* Adjust Tax figure */
            OceanusMoney myTax = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.TAXCREDIT);
            myTax = new OceanusMoney(myTax);
            myTax.addAmount(pTax);
            setValue(MoneyWiseAnalysisTaxBasisAttr.TAXCREDIT, myTax);
        }

        /* Register the transaction */
        registerTransaction(pTrans);
    }

    /**
     * Adjust transaction value.
     *
     * @param pTrans  the transaction
     * @param pValue  the value
     * @param pAdjust adjustment control
     */
    protected void adjustValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                               final OceanusMoney pValue,
                               final MoneyWiseTaxBasisAdjust pAdjust) {
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
     *
     * @param pValue  the value
     * @param pAdjust adjustment control
     */
    protected void adjustValue(final OceanusMoney pValue,
                               final MoneyWiseTaxBasisAdjust pAdjust) {
        /* If we are adjusting Gross */
        if (pAdjust.adjustGross()) {
            /* Access the existing value */
            OceanusMoney myGross = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
            myGross = new OceanusMoney(myGross);

            /* Subtract or add the value depending as to whether we are an expense bucket */
            if (isExpense) {
                myGross.subtractAmount(pValue);
            } else {
                myGross.addAmount(pValue);
            }

            /* Record the new value */
            setValue(MoneyWiseAnalysisTaxBasisAttr.GROSS, myGross);
        }

        /* If we are adjusting Nett */
        if (pAdjust.adjustNett()) {
            /* Access the existing value */
            OceanusMoney myNett = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.NETT);
            myNett = new OceanusMoney(myNett);

            /* Subtract or add the value depending as to whether we are an expense bucket */
            if (isExpense) {
                myNett.subtractAmount(pValue);
            } else {
                myNett.addAmount(pValue);
            }

            /* Record the new value */
            setValue(MoneyWiseAnalysisTaxBasisAttr.NETT, myNett);
        }
    }

    /**
     * Register the transaction.
     *
     * @param pTrans the transaction helper
     */
    protected void registerTransaction(final MoneyWiseAnalysisTransactionHelper pTrans) {
        /* Register the transaction in the history */
        theHistory.registerTransaction(pTrans.getTransaction(), theValues);
    }

    /**
     * Add values.
     *
     * @param pBucket tax category bucket
     */
    protected void addValues(final MoneyWiseAnalysisTaxBasisBucket pBucket) {
        /* Add the values */
        OceanusMoney myAmount = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        myAmount.addAmount(pBucket.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS));
        myAmount = theValues.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.NETT);
        myAmount.addAmount(pBucket.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.NETT));
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
     *
     * @return true/false
     */
    public boolean isActive() {
        return theValues.isActive();
    }

    /**
     * Value adjust Modes.
     */
    protected enum MoneyWiseTaxBasisAdjust {
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
         *
         * @return true/false
         */
        private boolean adjustGross() {
            return this != NETT;
        }

        /**
         * should we adjust Nett?
         *
         * @return true/false
         */
        private boolean adjustNett() {
            return this != GROSS;
        }
    }

    /**
     * TaxBasisBucketList class.
     */
    public static class MoneyWiseAnalysisTaxBasisBucketList
            implements MetisFieldItem, MoneyWiseTaxSource, MetisDataList<MoneyWiseAnalysisTaxBasisBucket> {
        /**
         * Local Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAnalysisTaxBasisBucketList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTaxBasisBucketList.class);

        /*
         * Declare Fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTaxBasisBucketList::getAnalysis);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_CHARGES, MoneyWiseAnalysisTaxBasisBucketList::getGainSlices);
            FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS, MoneyWiseAnalysisTaxBasisBucketList::getTotals);
        }

        /**
         * The analysis.
         */
        private final MoneyWiseAnalysis theAnalysis;

        /**
         * The list.
         */
        private final MetisListIndexed<MoneyWiseAnalysisTaxBasisBucket> theList;

        /**
         * The editSet.
         */
        private final PrometheusEditSet theEditSet;

        /**
         * The chargeableGains.
         */
        private final MoneyWiseChargeableGainSliceList theCharges;

        /**
         * The tax basis.
         */
        private final MoneyWiseAnalysisTaxBasisBucket theTotals;

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         * @param pGains    the new Gains list
         */
        private MoneyWiseAnalysisTaxBasisBucketList(final MoneyWiseAnalysis pAnalysis,
                                                    final MoneyWiseChargeableGainSliceList pGains) {
            theAnalysis = pAnalysis;
            theEditSet = theAnalysis.getEditSet();
            theCharges = pGains;
            theTotals = allocateTotalsBucket();
            theList = new MetisListIndexed<>();
            theList.setComparator((l, r) -> l.getTaxBasis().compareTo(r.getTaxBasis()));
        }

        /**
         * Construct a top-level List.
         *
         * @param pAnalysis the analysis
         */
        protected MoneyWiseAnalysisTaxBasisBucketList(final MoneyWiseAnalysis pAnalysis) {
            this(pAnalysis, new MoneyWiseChargeableGainSliceList());
        }

        /**
         * Construct a dated List.
         *
         * @param pAnalysis the analysis
         * @param pBase     the base list
         * @param pDate     the Date
         */
        protected MoneyWiseAnalysisTaxBasisBucketList(final MoneyWiseAnalysis pAnalysis,
                                                      final MoneyWiseAnalysisTaxBasisBucketList pBase,
                                                      final OceanusDate pDate) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                final MoneyWiseAnalysisTaxBasisBucket myBucket = new MoneyWiseAnalysisTaxBasisBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Calculate the delta and add to the list */
                    theList.add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         *
         * @param pAnalysis the analysis
         * @param pBase     the base list
         * @param pRange    the Date Range
         */
        protected MoneyWiseAnalysisTaxBasisBucketList(final MoneyWiseAnalysis pAnalysis,
                                                      final MoneyWiseAnalysisTaxBasisBucketList pBase,
                                                      final OceanusDateRange pRange) {
            /* Initialise class */
            this(pAnalysis, new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange()));

            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = pBase.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                final MoneyWiseAnalysisTaxBasisBucket myBucket = new MoneyWiseAnalysisTaxBasisBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (Boolean.FALSE.equals(myBucket.isIdle())) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    theList.add(myBucket);
                }
            }
        }

        @Override
        public MetisFieldSet<MoneyWiseAnalysisTaxBasisBucketList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public List<MoneyWiseAnalysisTaxBasisBucket> getUnderlyingList() {
            return theList.getUnderlyingList();
        }

        @Override
        public String formatObject(final OceanusDataFormatter pFormatter) {
            return getDataFieldSet().getName();
        }

        /**
         * Obtain the analysis.
         *
         * @return the analysis
         */
        protected MoneyWiseAnalysis getAnalysis() {
            return theAnalysis;
        }

        /**
         * Obtain item by id.
         *
         * @param pId the id to lookup
         * @return the item (or null if not present)
         */
        public MoneyWiseAnalysisTaxBasisBucket findItemById(final Integer pId) {
            /* Return results */
            return theList.getItemById(pId);
        }

        @Override
        public MoneyWiseChargeableGainSliceList getGainSlices() {
            return theCharges;
        }

        /**
         * Obtain the Totals.
         *
         * @return the totals bucket
         */
        public MoneyWiseAnalysisTaxBasisBucket getTotals() {
            return theTotals;
        }

        /**
         * Allocate the Totals EventCategoryBucket.
         *
         * @return the bucket
         */
        private MoneyWiseAnalysisTaxBasisBucket allocateTotalsBucket() {
            /* Obtain the totals category */
            return new MoneyWiseAnalysisTaxBasisBucket(theAnalysis, null);
        }

        /**
         * Obtain the TaxBasisBucket for a given taxBasis.
         *
         * @param pClass the taxBasis
         * @return the bucket
         */
        public MoneyWiseAnalysisTaxBasisBucket getBucket(final MoneyWiseTaxClass pClass) {
            /* Locate the bucket in the list */
            final MoneyWiseTaxBasis myBasis = theEditSet.getDataList(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseTaxBasisList.class).findItemByClass(pClass);
            MoneyWiseAnalysisTaxBasisBucket myItem = findItemById(myBasis.getIndexedId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new MoneyWiseAnalysisTaxBasisBucket(theAnalysis, myBasis);

                /* Add to the list */
                theList.add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain the matching BasisBucket.
         *
         * @param pTaxBasis the taxBasis
         * @return the matching bucket
         */
        public MoneyWiseAnalysisTaxBasisBucket getMatchingBasis(final MoneyWiseAnalysisTaxBasisBucket pTaxBasis) {
            /* Access the matching taxBasis bucket */
            MoneyWiseAnalysisTaxBasisBucket myBasis = findItemById(pTaxBasis.getTaxBasis().getIndexedId());
            if (myBasis == null) {
                myBasis = new MoneyWiseAnalysisTaxBasisBucket(theAnalysis, pTaxBasis.getTaxBasis());
            }

            /* If we are matching a TaxBasisAccount Bucket */
            if (pTaxBasis instanceof MoneyWiseAnalysisTaxBasisAccountBucket) {
                /* Look up the asset bucket */
                final MoneyWiseTransAsset myAsset = ((MoneyWiseAnalysisTaxBasisAccountBucket) pTaxBasis).getAccount();
                MoneyWiseAnalysisTaxBasisAccountBucket myAccountBucket = myBasis.findAccountBucket(myAsset);

                /* If there is no such bucket in the analysis */
                if (myAccountBucket == null) {
                    /* Allocate an orphan bucket */
                    myAccountBucket = new MoneyWiseAnalysisTaxBasisAccountBucket(theAnalysis, myBasis, myAsset);
                }

                /* Set bucket as the account bucket */
                myBasis = myAccountBucket;
            }

            /* Return the basis */
            return myBasis;
        }

        /**
         * Obtain the default BasisBucket.
         *
         * @return the default bucket
         */
        public MoneyWiseAnalysisTaxBasisBucket getDefaultBasis() {
            /* Return the first basis in the list if it exists */
            return isEmpty()
                    ? null
                    : theList.getUnderlyingList().get(0);
        }

        /**
         * Adjust basis buckets.
         *
         * @param pTrans    the transaction helper
         * @param pCategory primary category
         */
        protected void adjustBasis(final MoneyWiseAnalysisTransactionHelper pTrans,
                                   final MoneyWiseTransCategory pCategory) {
            /* Switch on the category type */
            switch (pCategory.getCategoryTypeClass()) {
                case TAXEDINCOME:
                case GROSSINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.SALARY);
                    break;
                case OTHERINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.OTHERINCOME);
                    break;
                case INTEREST:
                case TAXEDINTEREST:
                case TAXEDLOYALTYBONUS:
                    addIncome(pTrans, MoneyWiseTaxClass.TAXEDINTEREST);
                    break;
                case GROSSINTEREST:
                case GROSSLOYALTYBONUS:
                    addIncome(pTrans, MoneyWiseTaxClass.UNTAXEDINTEREST);
                    break;
                case PEER2PEERINTEREST:
                    addIncome(pTrans, MoneyWiseTaxClass.PEER2PEERINTEREST);
                    break;
                case DIVIDEND:
                case SHAREDIVIDEND:
                    addIncome(pTrans, MoneyWiseTaxClass.DIVIDEND);
                    break;
                case UNITTRUSTDIVIDEND:
                    addIncome(pTrans, MoneyWiseTaxClass.UNITTRUSTDIVIDEND);
                    break;
                case FOREIGNDIVIDEND:
                    addIncome(pTrans, MoneyWiseTaxClass.FOREIGNDIVIDEND);
                    break;
                case RENTALINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.RENTALINCOME);
                    break;
                case ROOMRENTALINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.ROOMRENTAL);
                    break;
                case INCOMETAX:
                    addExpense(pTrans, MoneyWiseTaxClass.TAXPAID);
                    break;
                case TAXFREEINTEREST:
                case TAXFREEDIVIDEND:
                case LOANINTERESTEARNED:
                case INHERITED:
                case CASHBACK:
                case LOYALTYBONUS:
                case TAXFREELOYALTYBONUS:
                case GIFTEDINCOME:
                    addIncome(pTrans, MoneyWiseTaxClass.TAXFREE);
                    break;
                case PENSIONCONTRIB:
                    addIncome(pTrans, MoneyWiseTaxClass.TAXFREE);
                    break;
                case BADDEBTCAPITAL:
                    addExpense(pTrans, MoneyWiseTaxClass.CAPITALGAINS);
                    break;
                case BADDEBTINTEREST:
                    addExpense(pTrans, MoneyWiseTaxClass.PEER2PEERINTEREST);
                    break;
                case EXPENSE:
                case LOCALTAXES:
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                case TAXRELIEF:
                case RECOVEREDEXPENSES:
                    addExpense(pTrans, MoneyWiseTaxClass.EXPENSE);
                    break;
                case RENTALEXPENSE:
                    addExpense(pTrans, MoneyWiseTaxClass.RENTALINCOME);
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
         *
         * @param pClass the class
         * @param pTrans the transaction
         */
        private void addIncome(final MoneyWiseAnalysisTransactionHelper pTrans,
                               final MoneyWiseTaxClass pClass) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.addIncomeTransaction(pTrans);
        }

        /**
         * Adjust basis for expense.
         *
         * @param pClass the class
         * @param pTrans the transaction
         */
        private void addExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                                final MoneyWiseTaxClass pClass) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.addExpenseTransaction(pTrans);
        }

        /**
         * Adjust basis buckets.
         *
         * @param pTrans  the transaction
         * @param pClass  the class
         * @param pIncome the income
         */
        protected void adjustValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                                   final MoneyWiseTaxClass pClass,
                                   final OceanusMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, MoneyWiseTaxBasisAdjust.STANDARD);
        }

        /**
         * Adjust basis buckets for Gross only.
         *
         * @param pTrans  the transaction
         * @param pClass  the class
         * @param pIncome the income
         */
        protected void adjustGrossValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                                        final MoneyWiseTaxClass pClass,
                                        final OceanusMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, MoneyWiseTaxBasisAdjust.GROSS);
        }

        /**
         * Adjust basis buckets for Nett only.
         *
         * @param pTrans  the transaction
         * @param pClass  the class
         * @param pIncome the income
         */
        protected void adjustNettValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                                       final MoneyWiseTaxClass pClass,
                                       final OceanusMoney pIncome) {
            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome, MoneyWiseTaxBasisAdjust.NETT);
        }

        /**
         * Adjust autoExpense.
         *
         * @param pTrans    the transaction
         * @param isExpense true/false
         */
        public void adjustAutoExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                                      final boolean isExpense) {
            /* Determine value */
            OceanusMoney myAmount = pTrans.getLocalAmount();
            if (!isExpense) {
                myAmount = new OceanusMoney(myAmount);
                myAmount.negate();
            }

            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(MoneyWiseTaxClass.EXPENSE);
            myBucket.adjustValue(pTrans, myAmount, MoneyWiseTaxBasisAdjust.STANDARD);
        }

        /**
         * Adjust for market growth.
         *
         * @param pIncome  the income
         * @param pExpense the expense
         */
        protected void adjustMarket(final OceanusMoney pIncome,
                                    final OceanusMoney pExpense) {
            /* Calculate the delta */
            final OceanusMoney myDelta = new OceanusMoney(pIncome);
            myDelta.subtractAmount(pExpense);

            /* Access the bucket and adjust it */
            final MoneyWiseAnalysisTaxBasisBucket myBucket = getBucket(MoneyWiseTaxClass.MARKET);
            myBucket.adjustValue(myDelta, MoneyWiseTaxBasisAdjust.STANDARD);
        }

        /**
         * record ChargeableGain.
         *
         * @param pTrans the transaction
         * @param pGain  the gain
         */
        public void recordChargeableGain(final MoneyWiseTransaction pTrans,
                                         final OceanusMoney pGain) {
            /* record the chargeable gain */
            theCharges.addTransaction(pTrans, pGain);
        }

        /**
         * produce Totals.
         */
        protected void produceTotals() {
            /* Loop through the buckets */
            final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisBucket myBucket = myIterator.next();

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
            final Iterator<MoneyWiseAnalysisTaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseAnalysisTaxBasisBucket myCurr = myIterator.next();

                /* Remove the bucket if it is inactive */
                if (!myCurr.isActive()) {
                    myIterator.remove();
                }
            }
        }

        @Override
        public OceanusMoney getAmountForTaxBasis(final MoneyWiseTaxClass pBasis) {
            /* Access the bucket */
            final MoneyWiseAnalysisTaxBasisBucket myItem = findItemById(pBasis.getClassId());

            /* If the bucket is not found */
            if (myItem == null) {
                final MoneyWiseCurrency myAssetCurrency = theAnalysis.getCurrency();
                final Currency myCurrency = myAssetCurrency == null
                        ? OceanusMoney.getDefaultCurrency()
                        : myAssetCurrency.getCurrency();
                return new OceanusMoney(myCurrency);
            }

            return myItem.getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        }
    }
}
