/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAccountBucket.TaxBasisAccountBucketList;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseChargeableGainSlice.MoneyWiseChargeableGainSliceList;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * The TaxBasis Bucket class.
 */
public class TaxBasisBucket
        implements MetisDataContents, Comparable<TaxBasisBucket>, MetisOrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.TAXBASIS_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Tax Basis Field Id.
     */
    private static final MetisField FIELD_TAXBASIS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TAXBASIS.getItemName());

    /**
     * Base Field Id.
     */
    private static final MetisField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * History Field Id.
     */
    private static final MetisField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

    /**
     * AccountList Field Id.
     */
    private static final MetisField FIELD_ACCOUNTS = FIELD_DEFS.declareLocalField(AnalysisResource.TAXBASIS_ACCOUNTLIST.getValue());

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS.getValue();

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, TaxBasisAttribute> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, TaxBasisAttribute.class);

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
        AssetCurrency myDefault = theAnalysis.getCurrency();
        Currency myCurrency = myDefault == null
                                                ? AccountBucket.DEFAULT_CURRENCY
                                                : myDefault.getCurrency();
        TaxBasisValues myValues = new TaxBasisValues(myCurrency);
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
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_TAXBASIS.equals(pField)) {
            return theTaxBasis;
        }
        if (FIELD_HISTORY.equals(pField)) {
            return theHistory;
        }
        if (FIELD_ACCOUNTS.equals(pField)) {
            return hasAccounts
                               ? theAccounts
                               : MetisFieldValue.SKIP;
        }
        if (FIELD_BASE.equals(pField)) {
            return theBaseValues;
        }

        /* Handle Attribute fields */
        TaxBasisAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof TethysDecimal) {
                return ((TethysDecimal) myValue).isNonZero()
                                                             ? myValue
                                                             : MetisFieldValue.SKIP;
            }
            return myValue;
        }

        return MetisFieldValue.UNKNOWN;
    }

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
        return theTaxBasis.getId();
    }

    /**
     * Obtain name.
     * @return the name
     */
    public String getName() {
        return (theTaxBasis == null)
                                     ? NAME_TOTALS
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
     * Obtain an orphan TaxBasisAccountBucket for a given account.
     * @param pAccount the account
     * @return the bucket
     */
    public TaxBasisAccountBucket getOrphanAccountBucket(final TransactionAsset pAccount) {
        /* Allocate an orphan bucket */
        return hasAccounts
                           ? theAccounts.getOrphanBucket(pAccount)
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
        Object myValue = getValue(pAttr);

        /* Return the value */
        return (myValue != null)
                                 ? myValue
                                 : MetisFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static TaxBasisAttribute getClassForField(final MetisField pField) {
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
        return theValues.getValue(pAttr);
    }

    @Override
    public int compareTo(final TaxBasisBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the TaxBases */
        return getTaxBasis().compareTo(pThat.getTaxBasis());
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

        /* Compare the Tax Bases */
        TaxBasisBucket myThat = (TaxBasisBucket) pThat;
        if (!getTaxBasis().equals(myThat.getTaxBasis())) {
            return false;
        }

        /* Compare the date ranges */
        return getDateRange().equals(myThat.getDateRange());
    }

    @Override
    public int hashCode() {
        return getTaxBasis().hashCode();
    }

    /**
     * Add income transaction.
     * @param pTrans the transaction
     */
    private void addIncomeTransaction(final TransactionHelper pTrans) {
        /* Access details */
        TethysMoney myAmount = pTrans.getCreditAmount();
        TethysMoney myTaxCredit = pTrans.getTaxCredit();
        TethysMoney myNatIns = pTrans.getNatInsurance();
        TethysMoney myBenefit = pTrans.getDeemedBenefit();
        TethysMoney myDonation = pTrans.getCharityDonation();

        /* Determine style of transaction */
        AssetDirection myDir = pTrans.getDirection();

        /* If the account is special */
        TransactionCategoryClass myClass = pTrans.getCategoryClass();
        if (myClass.isSwitchDirection()) {
            /* switch the direction */
            myDir = myDir.reverse();
        }

        /* Obtain zeroed counters */
        TethysMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myGross = new TethysMoney(myGross);
        myGross.setZero();
        TethysMoney myNett = new TethysMoney(myGross);
        TethysMoney myTax = new TethysMoney(myGross);

        /* If this is an expense */
        if (myDir.isTo()) {
            /* Adjust the gross and net */
            myGross.subtractAmount(myAmount);
            myNett.subtractAmount(myAmount);

            /* If we have a tax credit */
            if ((myTaxCredit != null) && (myTaxCredit.isNonZero())) {
                /* Adjust the gross */
                myGross.subtractAmount(myTaxCredit);
                myTax.subtractAmount(myTaxCredit);
            }

            /* If we have a natInsurance payment */
            if ((myNatIns != null) && (myNatIns.isNonZero())) {
                /* Adjust the gross */
                myGross.subtractAmount(myNatIns);
            }

            /* If we have a Benefit payment */
            if ((myBenefit != null) && (myBenefit.isNonZero())) {
                /* Adjust the gross */
                myGross.subtractAmount(myBenefit);
            }

            /* If we have a Charity donation */
            if ((myDonation != null) && (myDonation.isNonZero())) {
                /* Adjust the gross and net */
                myGross.subtractAmount(myDonation);
                myNett.subtractAmount(myDonation);
            }

            /* else this is a standard income */
        } else {
            /* Adjust the gross and net */
            myGross.addAmount(myAmount);
            myNett.addAmount(myAmount);

            /* If we have a tax credit */
            if ((myTaxCredit != null) && (myTaxCredit.isNonZero())) {
                /* Adjust the values */
                myGross.addAmount(myTaxCredit);
                myTax.addAmount(myTaxCredit);
            }

            /* If we have a natInsurance payment */
            if ((myNatIns != null) && (myNatIns.isNonZero())) {
                /* Adjust the gross */
                myGross.addAmount(myNatIns);
            }

            /* If we have a Benefit payment */
            if ((myBenefit != null) && (myBenefit.isNonZero())) {
                /* Adjust the gross */
                myGross.addAmount(myBenefit);
            }

            /* If we have a Charity donation */
            if ((myDonation != null) && (myDonation.isNonZero())) {
                /* Adjust the gross and net */
                myGross.addAmount(myDonation);
                myNett.addAmount(myDonation);
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
    private void addExpenseTransaction(final TransactionHelper pTrans) {
        /* Access details */
        TethysMoney myAmount = pTrans.getDebitAmount();
        TethysMoney myTaxCredit = pTrans.getTaxCredit();

        /* Determine style of event */
        AssetDirection myDir = pTrans.getDirection();

        /* Obtain zeroed counters */
        TethysMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myGross = new TethysMoney(myGross);
        myGross.setZero();
        TethysMoney myNett = new TethysMoney(myGross);
        TethysMoney myTax = new TethysMoney(myGross);

        /* If this is a refunded expense */
        if (myDir.isFrom()) {
            /* Adjust the gross and net */
            myGross.addAmount(myAmount);
            myNett.addAmount(myAmount);

            /* If we have a tax relief */
            if ((myTaxCredit != null) && (myTaxCredit.isNonZero())) {
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
            if ((myTaxCredit != null) && (myTaxCredit.isNonZero())) {
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
     */
    protected void adjustValue(final TransactionHelper pTrans,
                               final TethysMoney pValue) {
        /* Adjust the value */
        adjustValue(pValue);

        /* Register the transaction */
        registerTransaction(pTrans);

        /* If we have accounts */
        if (hasAccounts) {
            /* register the adjustment against the accounts */
            theAccounts.adjustValue(pTrans, pValue);
        }
    }

    /**
     * Adjust value.
     * @param pValue the value
     */
    private void adjustValue(final TethysMoney pValue) {
        /* Access the counters */
        TethysMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myGross = new TethysMoney(myGross);
        TethysMoney myNet = theValues.getMoneyValue(TaxBasisAttribute.NETT);
        myNet = new TethysMoney(myNet);

        /* If we are an expense bucket */
        if (isExpense) {
            /* Adjust the gross and net */
            myGross.subtractAmount(pValue);
            myNet.subtractAmount(pValue);
        } else {
            /* Adjust the gross and net */
            myGross.addAmount(pValue);
            myNet.addAmount(pValue);
        }

        /* Set the values */
        setValue(TaxBasisAttribute.GROSS, myGross);
        setValue(TaxBasisAttribute.NETT, myNet);
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
            setValue(TaxBasisAttribute.GROSS, new TethysMoney(pCurrency));
            setValue(TaxBasisAttribute.NETT, new TethysMoney(pCurrency));
            setValue(TaxBasisAttribute.TAXCREDIT, new TethysMoney(pCurrency));
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
            TethysMoney myValue = getMoneyValue(TaxBasisAttribute.GROSS);
            myValue = new TethysMoney(myValue);
            myValue.setZero();

            /* Reset Income and expense values */
            setValue(TaxBasisAttribute.GROSS, myValue);
            setValue(TaxBasisAttribute.NETT, new TethysMoney(myValue));
            setValue(TaxBasisAttribute.TAXCREDIT, new TethysMoney(myValue));
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            TethysMoney myGross = getMoneyValue(TaxBasisAttribute.GROSS);
            TethysMoney myNet = getMoneyValue(TaxBasisAttribute.NETT);
            TethysMoney myTax = getMoneyValue(TaxBasisAttribute.TAXCREDIT);
            return (myGross.isNonZero()) || (myNet.isNonZero()) || (myTax.isNonZero());
        }
    }

    /**
     * TaxBasisBucketList class.
     */
    public static class TaxBasisBucketList
            extends MetisOrderedIdList<Integer, TaxBasisBucket>
            implements MetisDataContents, MoneyWiseTaxSource {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.TAXBASIS_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * ChargeableGains Field Id.
         */
        private static final MetisField FIELD_CHARGES = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_CHARGES.getValue());

        /**
         * Totals field Id.
         */
        private static final MetisField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NAME_TOTALS);

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

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
         */
        protected TaxBasisBucketList(final Analysis pAnalysis) {
            super(TaxBasisBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theCharges = new MoneyWiseChargeableGainSliceList();
            theTotals = allocateTotalsBucket();
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
            super(TaxBasisBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theCharges = new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange());
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
        protected TaxBasisBucketList(final Analysis pAnalysis,
                                     final TaxBasisBucketList pBase,
                                     final TethysDateRange pRange) {
            /* Initialise class */
            super(TaxBasisBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
            theCharges = new MoneyWiseChargeableGainSliceList(pBase.getGainSlices(), pAnalysis.getDateRange());
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

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_CHARGES.equals(pField)) {
                return theCharges.isEmpty()
                                            ? MetisFieldValue.SKIP
                                            : theCharges;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Obtain the charges.
         * @return the charges
         */
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
            TaxBasis myBasis = theData.getTaxBases().findItemByClass(pClass);
            TaxBasisBucket myItem = findItemById(myBasis.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TaxBasisBucket(theAnalysis, myBasis);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Obtain an orphan TaxBasisBucket for a given taxBasis.
         * @param pBasis the taxBasis
         * @return the bucket
         */
        public TaxBasisBucket getOrphanBucket(final TaxBasis pBasis) {
            /* Allocate an orphan bucket */
            return new TaxBasisBucket(theAnalysis, pBasis);
        }

        /**
         * Adjust basis buckets.
         * @param pTrans the transaction helper
         * @param pCategory primary category
         */
        protected void adjustBasis(final TransactionHelper pTrans,
                                   final TransactionCategory pCategory) {
            /* Switch on the category type */
            TaxBasisBucket myBucket;
            switch (pCategory.getCategoryTypeClass()) {
                case TAXEDINCOME:
                case BENEFITINCOME:
                    /* Adjust the Gross salary bucket */
                    myBucket = getBucket(TaxBasisClass.SALARY);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case OTHERINCOME:
                    /* Adjust the Gross salary bucket */
                    myBucket = getBucket(TaxBasisClass.OTHERINCOME);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case INTEREST:
                case TAXEDINTEREST:
                case TAXEDLOYALTYBONUS:
                    /* Adjust the Gross interest bucket */
                    myBucket = getBucket(TaxBasisClass.TAXEDINTEREST);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case GROSSINTEREST:
                case GROSSLOYALTYBONUS:
                    /* Adjust the Gross interest bucket */
                    myBucket = getBucket(TaxBasisClass.UNTAXEDINTEREST);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case DIVIDEND:
                case SHAREDIVIDEND:
                    /* Adjust the Gross dividend bucket */
                    myBucket = getBucket(TaxBasisClass.DIVIDEND);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case UNITTRUSTDIVIDEND:
                    /* Adjust the Gross UT dividend bucket */
                    myBucket = getBucket(TaxBasisClass.UNITTRUSTDIVIDEND);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case FOREIGNDIVIDEND:
                    /* Adjust the Gross Foreign dividend bucket */
                    myBucket = getBucket(TaxBasisClass.FOREIGNDIVIDEND);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case RENTALINCOME:
                    /* Adjust the Gross rental bucket */
                    myBucket = getBucket(TaxBasisClass.RENTALINCOME);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case ROOMRENTALINCOME:
                    /* Adjust the Gross roomrental bucket */
                    myBucket = getBucket(TaxBasisClass.ROOMRENTAL);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case TAXSETTLEMENT:
                    /* Adjust the Tax Paid bucket */
                    myBucket = getBucket(TaxBasisClass.TAXPAID);
                    myBucket.addExpenseTransaction(pTrans);
                    break;
                case TAXFREEINTEREST:
                case TAXFREEDIVIDEND:
                case LOANINTERESTEARNED:
                case GRANTINCOME:
                case INHERITED:
                case CASHBACK:
                case LOYALTYBONUS:
                case TAXFREELOYALTYBONUS:
                case GIFTEDINCOME:
                    /* Adjust the Tax Free bucket */
                    myBucket = getBucket(TaxBasisClass.TAXFREE);
                    myBucket.addIncomeTransaction(pTrans);
                    break;
                case BADDEBT:
                    /* Adjust the BadDebt bucket */
                    myBucket = getBucket(TaxBasisClass.BADDEBT);
                    myBucket.addExpenseTransaction(pTrans);
                    break;
                case EXPENSE:
                case LOCALTAXES:
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                case CHARITYDONATION:
                case TAXRELIEF:
                case RECOVEREDEXPENSES:
                    /* Adjust the Expense bucket */
                    myBucket = getBucket(TaxBasisClass.EXPENSE);
                    myBucket.addExpenseTransaction(pTrans);
                    break;
                case RENTALEXPENSE:
                    /* Adjust the RentalIncome bucket */
                    myBucket = getBucket(TaxBasisClass.RENTALINCOME);
                    myBucket.addExpenseTransaction(pTrans);
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
         * Adjust basis buckets.
         * @param pTrans the transaction
         * @param pClass the class
         * @param pIncome the income
         */
        protected void adjustValue(final TransactionHelper pTrans,
                                   final TaxBasisClass pClass,
                                   final TethysMoney pIncome) {
            /* Access the bucket and adjust it */
            TaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome);
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
            TaxBasisBucket myBucket = getBucket(TaxBasisClass.EXPENSE);
            myBucket.adjustValue(pTrans, myAmount);
        }

        /**
         * Adjust for market growth.
         * @param pIncome the income
         * @param pExpense the expense
         */
        protected void adjustMarket(final TethysMoney pIncome,
                                    final TethysMoney pExpense) {
            /* Calculate the delta */
            TethysMoney myDelta = new TethysMoney(pIncome);
            myDelta.subtractAmount(pExpense);

            /* Access the bucket and adjust it */
            TaxBasisBucket myBucket = getBucket(TaxBasisClass.MARKET);
            myBucket.adjustValue(myDelta);
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
            Iterator<TaxBasisBucket> myIterator = iterator();
            while (myIterator.hasNext()) {
                TaxBasisBucket myBucket = myIterator.next();

                /* Adjust the Total Profit buckets */
                theTotals.addValues(myBucket);
            }
        }

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            /* Loop through the buckets */
            Iterator<TaxBasisBucket> myIterator = listIterator();
            while (myIterator.hasNext()) {
                TaxBasisBucket myCurr = myIterator.next();

                /* Remove the bucket if it is inactive */
                if (!myCurr.isActive()) {
                    myIterator.remove();
                }
            }
        }

        @Override
        public TethysMoney getAmountForTaxBasis(final TaxBasisClass pBasis) {
            TaxBasisBucket myItem = findItemById(pBasis.getClassId());
            return myItem == null
                                  ? new TethysMoney(theAnalysis.getCurrency().getCurrency())
                                  : myItem.getMoneyValue(TaxBasisAttribute.GROSS);
        }
    }
}
