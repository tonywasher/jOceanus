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
package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.metis.data.MetisDataFieldValue;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxBasis;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.base.MoneyWiseAnalysisHistory;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisValues;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Currency;

/**
 * The TaxBasis Bucket class.
 */
public abstract class MoneyWiseAnalysisTaxBasisBaseBucket
        implements MetisFieldTableItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisTaxBasisBaseBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTaxBasisBaseBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTaxBasisBaseBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseAnalysisTaxBasisBaseBucket::getTaxBasis);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_BASEVALUES, MoneyWiseAnalysisTaxBasisBaseBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.BUCKET_HISTORY, MoneyWiseAnalysisTaxBasisBaseBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseAnalysisTaxBasisAttr.class, MoneyWiseAnalysisTaxBasisBaseBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final MoneyWiseAnalysisControl theAnalysis;

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
     * Are we an expense bucket?
     */
    private final boolean isExpense;

    /**
     * Constructor.
     *
     * @param pAnalysis the analysis
     * @param pTaxBasis the basis
     */
    protected MoneyWiseAnalysisTaxBasisBaseBucket(final MoneyWiseAnalysisControl pAnalysis,
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
    protected MoneyWiseAnalysisTaxBasisBaseBucket(final MoneyWiseAnalysisControl pAnalysis,
                                                  final MoneyWiseAnalysisTaxBasisBaseBucket pBase,
                                                  final OceanusDate pDate) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pDate);

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
    protected MoneyWiseAnalysisTaxBasisBaseBucket(final MoneyWiseAnalysisControl pAnalysis,
                                                  final MoneyWiseAnalysisTaxBasisBaseBucket pBase,
                                                  final OceanusDateRange pRange) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new MoneyWiseAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseAnalysisTaxBasisBaseBucket> getDataFieldSet() {
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
     * Is this an expense bucket.
     *
     * @return true/false
     */
    public boolean isExpense() {
        return isExpense;
    }

    /**
     * Is this bucket idle?
     *
     * @return true/false
     */
    public boolean isIdle() {
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
    protected MoneyWiseAnalysisControl getAnalysis() {
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
    void addIncomeTransaction(final MoneyWiseAnalysisTransactionHelper pTrans) {
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

        /* Adjust accounts income */
        adjustAccountsIncome(pTrans, myGross, myNett, myTax);
    }

    /**
     * Adjust accounts for expense transaction.
     *
     * @param pTrans the transaction
     * @param pGross the gross
     * @param pNett  the nett
     * @param pTax   the Tax
     */
    void adjustAccountsIncome(final MoneyWiseAnalysisTransactionHelper pTrans,
                              final OceanusMoney pGross,
                              final OceanusMoney pNett,
                              final OceanusMoney pTax) {
        /* NoOp */
    }

    /**
     * Add expense transaction.
     *
     * @param pTrans the transaction
     */
    void addExpenseTransaction(final MoneyWiseAnalysisTransactionHelper pTrans) {
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

        /* Adjust accounts expense */
        adjustAccountsExpense(pTrans, myGross, myNett, myTax);
    }

    /**
     * Adjust accounts for expense transaction.
     *
     * @param pTrans the transaction
     * @param pGross the gross
     * @param pNett  the nett
     * @param pTax   the Tax
     */
    void adjustAccountsExpense(final MoneyWiseAnalysisTransactionHelper pTrans,
                               final OceanusMoney pGross,
                               final OceanusMoney pNett,
                               final OceanusMoney pTax) {
        /* NoOp */
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

        /* Adjust accounts value */
        adjustAccountsValue(pTrans, pValue, pAdjust);
    }

    /**
     * Adjust accounts transaction value.
     *
     * @param pTrans  the transaction
     * @param pValue  the value
     * @param pAdjust adjustment control
     */
    void adjustAccountsValue(final MoneyWiseAnalysisTransactionHelper pTrans,
                             final OceanusMoney pValue,
                             final MoneyWiseTaxBasisAdjust pAdjust) {
        /* NoOp */
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
    protected void addValues(final MoneyWiseAnalysisTaxBasisBaseBucket pBucket) {
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
}
