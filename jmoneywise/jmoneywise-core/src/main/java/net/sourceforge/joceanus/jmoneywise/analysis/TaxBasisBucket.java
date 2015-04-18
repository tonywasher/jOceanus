/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.list.OrderedIdItem;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
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
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * The TaxBasis Bucket class.
 */
public class TaxBasisBucket
        implements JDataContents, Comparable<TaxBasisBucket>, OrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.TAXBASIS_NAME.getValue());

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Tax Basis Field Id.
     */
    private static final JDataField FIELD_TAXBASIS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TAXBASIS.getItemName());

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_BASEVALUES.getValue());

    /**
     * History Field Id.
     */
    private static final JDataField FIELD_HISTORY = FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_HISTORY.getValue());

    /**
     * History Field Id.
     */
    private static final JDataField FIELD_ACCOUNTS = FIELD_DEFS.declareLocalField(AnalysisResource.TAXBASIS_ACCOUNTLIST.getValue());

    /**
     * Totals bucket name.
     */
    private static final String NAME_TOTALS = AnalysisResource.ANALYSIS_TOTALS.getValue();

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, TaxBasisAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, TaxBasisAttribute.class);

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
     * Do we have accounts.
     */
    private final boolean hasAccounts;

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

        /* Create the history map */
        AssetCurrency myDefault = theAnalysis.getCurrency();
        Currency myCurrency = myDefault == null
                                               ? AccountBucket.DEFAULT_CURRENCY
                                               : myDefault.getCurrency();
        TaxBasisValues myValues = new TaxBasisValues(myCurrency);
        theHistory = new BucketHistory<TaxBasisValues, TaxBasisAttribute>(myValues);

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
                             final JDateDay pDate) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new BucketHistory<TaxBasisValues, TaxBasisAttribute>(pBase.getHistoryMap(), pDate);

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
                             final JDateDayRange pRange) {
        /* Copy details from base */
        theTaxBasis = pBase.getTaxBasis();
        theAnalysis = pAnalysis;

        /* Access the relevant history */
        theHistory = new BucketHistory<TaxBasisValues, TaxBasisAttribute>(pBase.getHistoryMap(), pRange);

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
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
                              : JDataFieldValue.SKIP;
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
                                                       : JDataFieldValue.SKIP;
            }
            return myValue;
        }

        return JDataFieldValue.UNKNOWN;
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
     * Obtain values for transaction.
     * @param pTrans the event
     * @return the values (or null)
     */
    public TaxBasisValues getValuesForTransaction(final Transaction pTrans) {
        /* Obtain values for event */
        return theHistory.getValuesForTransaction(pTrans);
    }

    /**
     * Obtain delta for transaction.
     * @param pTrans the transaction
     * @param pAttr the attribute
     * @return the delta (or null)
     */
    public JDecimal getDeltaForTransaction(final Transaction pTrans,
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
    public JDateDayRange getDateRange() {
        return theAnalysis.getDateRange();
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
                                : JDataFieldValue.SKIP;
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
    private void addIncomeTransaction(final Transaction pTrans) {
        /* Access details */
        JMoney myAmount = pTrans.getAmount();
        JMoney myTaxCredit = pTrans.getTaxCredit();
        JMoney myNatIns = pTrans.getNatInsurance();
        JMoney myBenefit = pTrans.getDeemedBenefit();
        JMoney myDonation = pTrans.getCharityDonation();

        /* Determine style of transaction */
        AssetDirection myDir = pTrans.getDirection();

        /* If the account is special */
        TransactionCategoryClass myClass = pTrans.getCategoryClass();
        if (myClass.isSwitchDirection()) {
            /* switch the direction */
            myDir = myDir.reverse();
        }

        /* Obtain zeroed counters */
        JMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myGross = new JMoney(myGross);
        myGross.setZero();
        JMoney myNett = new JMoney(myGross);
        JMoney myTax = new JMoney(myGross);

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
    private void addExpenseTransaction(final Transaction pTrans) {
        /* Access details */
        JMoney myAmount = pTrans.getAmount();
        JMoney myTaxCredit = pTrans.getTaxCredit();

        /* Determine style of event */
        AssetDirection myDir = pTrans.getDirection();

        /* Obtain zeroed counters */
        JMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myGross = new JMoney(myGross);
        myGross.setZero();
        JMoney myNett = new JMoney(myGross);
        JMoney myTax = new JMoney(myGross);

        /* If this is a refunded expense */
        if (myDir.isFrom()) {
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

            /* else this is a standard expense */
        } else {
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
     * @param pTrans the transaction
     * @param pGross the gross delta value
     * @param pNett the net delta value
     * @param pTax the tax delta value
     */
    protected void registerDeltaValues(final Transaction pTrans,
                                       final JMoney pGross,
                                       final JMoney pNett,
                                       final JMoney pTax) {
        /* If we have a change to the gross value */
        if (pGross.isNonZero()) {
            /* Adjust Gross figure */
            JMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
            myGross = new JMoney(myGross);
            myGross.addAmount(pGross);
            setValue(TaxBasisAttribute.GROSS, myGross);
        }

        /* If we have a change to the net value */
        if (pNett.isNonZero()) {
            /* Adjust Net figure */
            JMoney myNett = theValues.getMoneyValue(TaxBasisAttribute.NETT);
            myNett = new JMoney(myNett);
            myNett.addAmount(pNett);
            setValue(TaxBasisAttribute.NETT, myNett);
        }

        /* If we have a change to the tax value */
        if (pTax.isNonZero()) {
            /* Adjust Tax figure */
            JMoney myTax = theValues.getMoneyValue(TaxBasisAttribute.TAXCREDIT);
            myTax = new JMoney(myTax);
            myTax.addAmount(pTax);
            setValue(TaxBasisAttribute.TAXCREDIT, myTax);
        }

        /* Register the transaction */
        theHistory.registerTransaction(pTrans, theValues);
    }

    /**
     * Adjust transaction value.
     * @param pTrans the transaction
     * @param pValue the value
     */
    private void adjustValue(final Transaction pTrans,
                             final JMoney pValue) {
        /* Adjust the value */
        adjustValue(pValue);

        /* Register the transaction */
        theHistory.registerTransaction(pTrans, theValues);
    }

    /**
     * Adjust value.
     * @param pValue the value
     */
    private void adjustValue(final JMoney pValue) {
        /* Access the counters */
        JMoney myGross = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myGross = new JMoney(myGross);
        JMoney myNet = theValues.getMoneyValue(TaxBasisAttribute.NETT);
        myNet = new JMoney(myNet);

        /* Adjust the gross and net */
        myGross.addAmount(pValue);
        myNet.addAmount(pValue);

        /* Set the values */
        setValue(TaxBasisAttribute.GROSS, myGross);
        setValue(TaxBasisAttribute.NETT, myNet);
    }

    /**
     * Add values.
     * @param pBucket tax category bucket
     */
    protected void addValues(final TaxBasisBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myAmount.addAmount(pBucket.getMoneyValue(TaxBasisAttribute.GROSS));
    }

    /**
     * subtract values.
     * @param pBucket tax category bucket
     */
    protected void subtractValues(final TaxBasisBucket pBucket) {
        /* Adjust the value */
        JMoney myAmount = theValues.getMoneyValue(TaxBasisAttribute.GROSS);
        myAmount.subtractAmount(pBucket.getMoneyValue(TaxBasisAttribute.GROSS));
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
         * SerialId.
         */
        private static final long serialVersionUID = 4649078250661638369L;

        /**
         * Constructor.
         * @param pCurrency the reporting currency
         */
        protected TaxBasisValues(final Currency pCurrency) {
            /* Initialise class */
            super(TaxBasisAttribute.class);

            /* Create all possible values */
            put(TaxBasisAttribute.GROSS, new JMoney(pCurrency));
            put(TaxBasisAttribute.NETT, new JMoney(pCurrency));
            put(TaxBasisAttribute.TAXCREDIT, new JMoney(pCurrency));
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
            /* Adjust gross/net/tax values */
            adjustMoneyToBase(pBase, TaxBasisAttribute.GROSS);
            adjustMoneyToBase(pBase, TaxBasisAttribute.NETT);
            adjustMoneyToBase(pBase, TaxBasisAttribute.TAXCREDIT);
        }

        @Override
        protected void resetBaseValues() {
            /* Create a zero value in the correct currency */
            JMoney myValue = getMoneyValue(TaxBasisAttribute.GROSS);
            myValue = new JMoney(myValue);
            myValue.setZero();

            /* Reset Income and expense values */
            put(TaxBasisAttribute.GROSS, myValue);
            put(TaxBasisAttribute.NETT, new JMoney(myValue));
            put(TaxBasisAttribute.TAXCREDIT, new JMoney(myValue));
        }

        /**
         * Are the values?
         * @return true/false
         */
        public boolean isActive() {
            JMoney myGross = getMoneyValue(TaxBasisAttribute.GROSS);
            JMoney myNet = getMoneyValue(TaxBasisAttribute.NETT);
            JMoney myTax = getMoneyValue(TaxBasisAttribute.TAXCREDIT);
            return (myGross.isNonZero()) || (myNet.isNonZero()) || (myTax.isNonZero());
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
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.TAXBASIS_LIST.getValue());

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Totals field Id.
         */
        private static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField(NAME_TOTALS);

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The data.
         */
        private final MoneyWiseData theData;

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
        protected TaxBasisBucketList(final Analysis pAnalysis,
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

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

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
            return JDataFieldValue.UNKNOWN;
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
         * @param pTrans the transaction
         * @param pCategory primary category
         */
        protected void adjustBasis(final Transaction pTrans,
                                   final TransactionCategory pCategory) {
            /* Switch on the category type */
            TaxBasisBucket myBucket;
            switch (pCategory.getCategoryTypeClass()) {
                case TAXEDINCOME:
                case BENEFITINCOME:
                case RENTALINCOME:
                    /* Adjust the Gross salary bucket */
                    myBucket = getBucket(TaxBasisClass.SALARY);
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
                case ROOMRENTALINCOME:
                    /* Adjust the Gross rental bucket */
                    myBucket = getBucket(TaxBasisClass.RENTALINCOME);
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
                case EXPENSE:
                case BADDEBT:
                case LOCALTAXES:
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                case CHARITYDONATION:
                case TAXRELIEF:
                case OTHERINCOME:
                    /* Adjust the Expense bucket */
                    myBucket = getBucket(TaxBasisClass.EXPENSE);
                    myBucket.addExpenseTransaction(pTrans);
                    break;
                case UNITSADJUST:
                case SECURITYREPLACE:
                case STOCKTAKEOVER:
                case STOCKSPLIT:
                case STOCKDEMERGER:
                case STOCKRIGHTSTAKEN:
                case STOCKRIGHTSWAIVED:
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
        protected void adjustValue(final Transaction pTrans,
                                   final TaxBasisClass pClass,
                                   final JMoney pIncome) {
            /* Access the bucket and adjust it */
            TaxBasisBucket myBucket = getBucket(pClass);
            myBucket.adjustValue(pTrans, pIncome);
        }

        /**
         * Adjust autoExpense.
         * @param pTrans the transaction
         * @param isExpense true/false
         */
        protected void adjustAutoExpense(final Transaction pTrans,
                                         final boolean isExpense) {
            /* Determine value */
            JMoney myAmount = pTrans.getAmount();
            if (!isExpense) {
                myAmount = new JMoney(myAmount);
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
        protected void adjustMarket(final JMoney pIncome,
                                    final JMoney pExpense) {
            /* Calculate the delta */
            JMoney myDelta = new JMoney(pIncome);
            myDelta.subtractAmount(pExpense);

            /* Access the bucket and adjust it */
            TaxBasisBucket myBucket = getBucket(TaxBasisClass.MARKET);
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
                TaxBasis myBasis = myBucket.getTaxBasis();

                /* Switch on the tax basis */
                switch (myBasis.getTaxClass()) {
                    case SALARY:
                    case TAXEDINTEREST:
                    case UNTAXEDINTEREST:
                    case DIVIDEND:
                    case UNITTRUSTDIVIDEND:
                    case RENTALINCOME:
                    case TAXABLEGAINS:
                    case CAPITALGAINS:
                    case MARKET:
                    case TAXFREE:
                        /* Adjust the Total Profit buckets */
                        theTotals.addValues(myBucket);
                        break;
                    case TAXPAID:
                    case EXPENSE:
                    case VIRTUAL:
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
    }
}
