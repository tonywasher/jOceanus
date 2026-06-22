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
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets;

import io.github.tonywasher.joceanus.metis.data.MetisDataFieldValue;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem.MetisFieldTableItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisHistory;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisBucketRegister;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisValues;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseAssetType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxBasis;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Currency;

/**
 * The TaxBasis Bucket class.
 */
public abstract class MoneyWiseXAnalysisTaxBasisBaseBucket
        implements MetisFieldTableItem, MoneyWiseXAnalysisBucketRegister {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisTaxBasisBaseBucket> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisTaxBasisBaseBucket.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisTaxBasisBaseBucket::getAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseXAnalysisTaxBasisBaseBucket::getTaxBasis);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_BASEVALUES, MoneyWiseXAnalysisTaxBasisBaseBucket::getBaseValues);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.BUCKET_HISTORY, MoneyWiseXAnalysisTaxBasisBaseBucket::getHistoryMap);
        FIELD_DEFS.declareLocalFieldsForEnum(MoneyWiseXAnalysisTaxBasisAttr.class, MoneyWiseXAnalysisTaxBasisBaseBucket::getAttributeValue);
    }

    /**
     * Totals bucket name.
     */
    private static final MetisDataFieldId NAME_TOTALS = MoneyWiseXAnalysisBucketResource.ANALYSIS_TOTALS;

    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysisHolder theAnalysis;

    /**
     * Tax Basis.
     */
    private final MoneyWiseTaxBasis theTaxBasis;

    /**
     * Values.
     */
    private final MoneyWiseXAnalysisTaxBasisValues theValues;

    /**
     * The base values.
     */
    private final MoneyWiseXAnalysisTaxBasisValues theBaseValues;

    /**
     * History Map.
     */
    private final MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisTaxBasisValues, MoneyWiseXAnalysisTaxBasisAttr> theHistory;

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
    protected MoneyWiseXAnalysisTaxBasisBaseBucket(final MoneyWiseXAnalysisHolder pAnalysis,
                                                   final MoneyWiseTaxBasis pTaxBasis) {
        /* Store the parameters */
        theTaxBasis = pTaxBasis;
        theAnalysis = pAnalysis;
        isExpense = theTaxBasis != null
                && theTaxBasis.getTaxClass().isExpense();

        /* Create the history map */
        final MoneyWiseCurrency myDefault = theAnalysis.getCurrency();
        final Currency myCurrency = myDefault == null
                ? MoneyWiseXAnalysisAccountBucket.DEFAULT_CURRENCY
                : myDefault.getCurrency();
        final MoneyWiseXAnalysisTaxBasisValues myValues = new MoneyWiseXAnalysisTaxBasisValues(myCurrency);
        theHistory = new MoneyWiseXAnalysisHistory<>(myValues);

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
    protected MoneyWiseXAnalysisTaxBasisBaseBucket(final MoneyWiseXAnalysisHolder pAnalysis,
                                                   final MoneyWiseXAnalysisTaxBasisBaseBucket pBase,
                                                   final OceanusDate pDate) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pDate);

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
    protected MoneyWiseXAnalysisTaxBasisBaseBucket(final MoneyWiseXAnalysisHolder pAnalysis,
                                                   final MoneyWiseXAnalysisTaxBasisBaseBucket pBase,
                                                   final OceanusDateRange pRange) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;
        isExpense = pBase.isExpense();

        /* Access the relevant history */
        theHistory = new MoneyWiseXAnalysisHistory<>(pBase.getHistoryMap(), pRange);

        /* Access the key value maps */
        theValues = theHistory.getValues();
        theBaseValues = theHistory.getBaseValues();
    }

    @Override
    public MetisFieldSet<? extends MoneyWiseXAnalysisTaxBasisBaseBucket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName() + " " + theValues;
    }

    @Override
    public Integer getIndexedId() {
        return theTaxBasis.getIndexedId();
    }

    @Override
    public Long getBucketId() {
        return MoneyWiseAssetType.createExternalId(MoneyWiseAssetType.TAXBASIS, getIndexedId());
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
    public MoneyWiseXAnalysisTaxBasisValues getValues() {
        return theValues;
    }

    /**
     * Obtain the value for a particular attribute.
     *
     * @param pAttr the attribute
     * @return the value
     */
    public OceanusMoney getMoneyValue(final MoneyWiseXAnalysisTaxBasisAttr pAttr) {
        return theValues.getMoneyValue(pAttr);
    }

    /**
     * Obtain the base value map.
     *
     * @return the base value map
     */
    public MoneyWiseXAnalysisTaxBasisValues getBaseValues() {
        return theBaseValues;
    }

    /**
     * Obtain values for event.
     *
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisTaxBasisValues getValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Obtain values for event */
        return theHistory.getValuesForEvent(pEvent);
    }

    /**
     * Obtain previous values for event.
     *
     * @param pEvent the event
     * @return the values (or null)
     */
    public MoneyWiseXAnalysisTaxBasisValues getPreviousValuesForEvent(final MoneyWiseXAnalysisEvent pEvent) {
        return theHistory.getPreviousValuesForEvent(pEvent);
    }

    /**
     * Obtain delta for event.
     *
     * @param pEvent the event
     * @param pAttr  the attribute
     * @return the delta (or null)
     */
    public OceanusDecimal getDeltaForEvent(final MoneyWiseXAnalysisEvent pEvent,
                                           final MoneyWiseXAnalysisTaxBasisAttr pAttr) {
        /* Obtain delta for event */
        return theHistory.getDeltaValue(pEvent, pAttr);
    }

    /**
     * Obtain the history map.
     *
     * @return the history map
     */
    private MoneyWiseXAnalysisHistory<MoneyWiseXAnalysisTaxBasisValues, MoneyWiseXAnalysisTaxBasisAttr> getHistoryMap() {
        return theHistory;
    }

    /**
     * Obtain the analysis.
     *
     * @return the analysis
     */
    protected MoneyWiseXAnalysisHolder getAnalysis() {
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
    protected void setValue(final MoneyWiseXAnalysisTaxBasisAttr pAttr,
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
    private Object getAttributeValue(final MoneyWiseXAnalysisTaxBasisAttr pAttr) {
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
    private Object getValue(final MoneyWiseXAnalysisTaxBasisAttr pAttr) {
        /* Obtain the attribute */
        return theValues.getValue(pAttr);
    }

    /**
     * Adjust Gross and Nett values by amount.
     *
     * @param pAmount the amount
     */
    public void adjustGrossAndNett(final OceanusMoney pAmount) {
        adjustGrossAndNett(null, pAmount);
    }

    /**
     * Adjust Gross value by amount.
     *
     * @param pAmount the amount
     */
    public void adjustGross(final OceanusMoney pAmount) {
        adjustGross(null, pAmount);
    }

    /**
     * Adjust Gross and Tax values by amount.
     *
     * @param pAmount the amount
     */
    public void adjustGrossAndTax(final OceanusMoney pAmount) {
        adjustGrossAndTax(null, pAmount);
    }

    /**
     * Adjust Gross and Nett values by amount.
     *
     * @param pAccount the relevant account
     * @param pAmount  the amount
     * @return the adjusted taxBasisAccountBucket (or null)
     */
    public MoneyWiseXAnalysisTaxBasisBaseBucket adjustGrossAndNett(final MoneyWiseTransAsset pAccount,
                                                                   final OceanusMoney pAmount) {
        return adjustValue(pAccount, pAmount, MoneyWiseXTaxBasisAdjust.STANDARD);
    }

    /**
     * Adjust Gross value by amount.
     *
     * @param pAccount the relevant account
     * @param pAmount  the amount
     * @return the adjusted taxBasisAccountBucket (or null)
     */
    public MoneyWiseXAnalysisTaxBasisBaseBucket adjustGross(final MoneyWiseTransAsset pAccount,
                                                            final OceanusMoney pAmount) {
        return adjustValue(pAccount, pAmount, MoneyWiseXTaxBasisAdjust.GROSS);
    }

    /**
     * Adjust Gross and Tax values by amount.
     *
     * @param pAccount the relevant account
     * @param pAmount  the amount
     * @return the adjusted taxBasisAccountBucket (or null)
     */
    public MoneyWiseXAnalysisTaxBasisBaseBucket adjustGrossAndTax(final MoneyWiseTransAsset pAccount,
                                                                  final OceanusMoney pAmount) {
        return adjustValue(pAccount, pAmount, MoneyWiseXTaxBasisAdjust.TAXCREDIT);
    }

    /**
     * Adjust value.
     *
     * @param pAccount the relevant account
     * @param pValue   the value
     * @param pAdjust  adjustment control
     * @return the adjusted taxBasisAccountBucket (or null)
     */
    MoneyWiseXAnalysisTaxBasisBaseBucket adjustValue(final MoneyWiseTransAsset pAccount,
                                                     final OceanusMoney pValue,
                                                     final MoneyWiseXTaxBasisAdjust pAdjust) {
        /* Access the existing value */
        OceanusMoney myGross = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
        myGross = new OceanusMoney(myGross);

        /* Subtract or add the value depending as to whether we are an expense bucket */
        if (isExpense) {
            myGross.subtractAmount(pValue);
        } else {
            myGross.addAmount(pValue);
        }

        /* Record the new value */
        setValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS, myGross);

        /* If we are adjusting Nett */
        if (pAdjust.adjustNett()) {
            /* Access the existing value */
            OceanusMoney myNett = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT);
            myNett = new OceanusMoney(myNett);

            /* Subtract or add the value if we are an expense/income bucket */
            if (isExpense) {
                myNett.subtractAmount(pValue);
            } else {
                myNett.addAmount(pValue);
            }

            /* Record the new value */
            setValue(MoneyWiseXAnalysisTaxBasisAttr.NETT, myNett);
        }

        /* If we are adjusting TaxCredit */
        if (pAdjust.adjustTaxCredit()) {
            /* Access the existing value */
            OceanusMoney myTax = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.TAXCREDIT);
            myTax = new OceanusMoney(myTax);

            /* Subtract or add the value if we are an expense/income bucket */
            if (isExpense) {
                myTax.subtractAmount(pValue);
            } else {
                myTax.addAmount(pValue);
            }

            /* Record the new value */
            setValue(MoneyWiseXAnalysisTaxBasisAttr.TAXCREDIT, myTax);
        }

        /* Adjust the account value */
        return adjustAccountValue(pAccount, pValue, pAdjust);
    }

    /**
     * Adjust account value.
     *
     * @param pAccount the relevant account
     * @param pValue   the value
     * @param pAdjust  adjustment control
     * @return the adjusted taxBasisAccountBucket (or null)
     */
    MoneyWiseXAnalysisTaxBasisBaseBucket adjustAccountValue(final MoneyWiseTransAsset pAccount,
                                                            final OceanusMoney pValue,
                                                            final MoneyWiseXTaxBasisAdjust pAdjust) {
        return null;
    }

    @Override
    public void registerEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Register the transaction in the history */
        theHistory.registerEvent(pEvent, theValues);
    }

    /**
     * Add values.
     *
     * @param pBucket tax category bucket
     */
    protected void addValues(final MoneyWiseXAnalysisTaxBasisBaseBucket pBucket) {
        /* Add the values */
        OceanusMoney myAmount = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);
        myAmount.addAmount(pBucket.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS));
        myAmount = theValues.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT);
        myAmount.addAmount(pBucket.getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT));
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
     * Value adjust Modes.
     */
    public enum MoneyWiseXTaxBasisAdjust {
        /**
         * Adjust both Gross and Nett.
         */
        STANDARD,

        /**
         * Adjust Gross only.
         */
        GROSS,

        /**
         * Adjust Gross and Tax.
         */
        TAXCREDIT;

        /**
         * should we adjust Nett?
         *
         * @return true/false
         */
        private boolean adjustNett() {
            return this == STANDARD;
        }

        /**
         * should we adjust TaxCredit?
         *
         * @return true/false
         */
        private boolean adjustTaxCredit() {
            return this == TAXCREDIT;
        }
    }
}
