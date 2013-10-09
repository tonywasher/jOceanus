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

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TransactionType;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * The Payee Bucket class.
 */
public final class PayeeBucket
        implements JDataContents, Comparable<PayeeBucket>, OrderedIdItem<Integer> {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountBucket.class.getSimpleName());

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareEqualityField(Analysis.class.getSimpleName());

    /**
     * Account Field Id.
     */
    private static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField(Account.class.getSimpleName());

    /**
     * Base Field Id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField("Base");

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, PayeeAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, PayeeAttribute.class);

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The account.
     */
    private final Account theAccount;

    /**
     * The dataSet.
     */
    private final FinanceData theData;

    /**
     * The base.
     */
    private final PayeeBucket theBase;

    /**
     * Attribute Map.
     */
    private final Map<PayeeAttribute, Object> theAttributes;

    /**
     * SavePoint.
     */
    private final Map<PayeeAttribute, Object> theSavePoint;

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBase != null)
                    ? theBase
                    : JDataFieldValue.SkipField;
        }

        /* Handle Attribute fields */
        PayeeAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            Object myValue = getAttributeValue(myClass);
            if (myValue instanceof JDecimal) {
                return ((JDecimal) myValue).isNonZero()
                        ? myValue
                        : JDataFieldValue.SkipField;
            }
            return myValue;
        }

        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return getName();
    }

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

    @Override
    public Integer getOrderedId() {
        return theAccount.getId();
    }

    /**
     * Obtain the base.
     * @return the base
     */
    public PayeeBucket getBase() {
        return theBase;
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    protected FinanceData getDataSet() {
        return theData;
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    protected Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the attribute map.
     * @return the attribute map
     */
    protected Map<PayeeAttribute, Object> getAttributes() {
        return theAttributes;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setAttribute(final PayeeAttribute pAttr,
                                final Object pValue) {
        /* Set the value into the list */
        theAttributes.put(pAttr, pValue);
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final PayeeAttribute pAttr) {
        /* Access value of object */
        Object myValue = getAttribute(pAttr);

        /* Return the value */
        return (myValue != null)
                ? myValue
                : JDataFieldValue.SkipField;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static PayeeAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain an attribute value.
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X> X getAttribute(final PayeeAttribute pAttr,
                               final Class<X> pClass) {
        /* Obtain the attribute */
        return pClass.cast(getAttribute(pAttr));
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final PayeeAttribute pAttr) {
        /* Obtain the attribute */
        return theAttributes.get(pAttr);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getMoneyAttribute(final PayeeAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JMoney.class);
    }

    /**
     * Obtain an attribute value from the base.
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X extends JDecimal> X getBaseAttribute(final PayeeAttribute pAttr,
                                                    final Class<X> pClass) {
        /* Obtain the attribute */
        return (theBase == null)
                ? null
                : theBase.getAttribute(pAttr, pClass);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getBaseMoneyAttribute(final PayeeAttribute pAttr) {
        /* Obtain the attribute */
        return getBaseAttribute(pAttr, JMoney.class);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pAccount the account
     */
    private PayeeBucket(final Analysis pAnalysis,
                        final Account pAccount) {
        /* Store the details */
        theAccount = pAccount;
        theAnalysis = pAnalysis;
        theData = theAnalysis.getData();
        theBase = null;

        /* Create the attribute map and savePoint */
        theAttributes = new EnumMap<PayeeAttribute, Object>(PayeeAttribute.class);
        theSavePoint = new EnumMap<PayeeAttribute, Object>(PayeeAttribute.class);

        /* Initialise income/expense to zero */
        setAttribute(PayeeAttribute.Income, new JMoney());
        setAttribute(PayeeAttribute.Expense, new JMoney());
    }

    @Override
    public int compareTo(final PayeeBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Accounts */
        return getAccount().compareTo(pThat.getAccount());
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
        if (!(pThat instanceof PayeeBucket)) {
            return false;
        }

        /* Compare the Accounts */
        PayeeBucket myThat = (PayeeBucket) pThat;
        return getAccount().equals(myThat.getAccount());
    }

    @Override
    public int hashCode() {
        return getAccount().hashCode();
    }

    /**
     * Adjust account for debit.
     * @param pEvent the event causing the debit
     */
    protected void adjustForDebit(final Event pEvent) {
        /* Analyse the event */
        TransactionType myCatTran = TransactionType.deriveType(pEvent.getCategory());

        /* Access values */
        JMoney myIncome = getMoneyAttribute(PayeeAttribute.Income);
        JMoney myExpense = getMoneyAttribute(PayeeAttribute.Expense);
        if (myCatTran.isExpense()) {
            myExpense.subtractAmount(pEvent.getAmount());
        } else {
            myIncome.addAmount(pEvent.getAmount());
        }

        /* If there is a TaxCredit */
        JMoney myTaxCred = pEvent.getTaxCredit();
        if (myTaxCred != null) {
            /* Adjust for Tax Credit */
            myIncome.addAmount(myTaxCred);
        }

        /* If there is National Insurance */
        JMoney myNatIns = pEvent.getNatInsurance();
        if (myNatIns != null) {
            /* Adjust for National Insurance */
            myIncome.addAmount(myNatIns);
        }

        /* If there is Charity Donation */
        JMoney myDonation = pEvent.getCharityDonation();
        if (myDonation != null) {
            /* Adjust for Charity Donation */
            myIncome.addAmount(myDonation);
            myExpense.addAmount(myDonation);
        }
    }

    /**
     * Adjust account for credit.
     * @param pEvent the event causing the credit
     */
    protected void adjustForCredit(final Event pEvent) {
        JMoney myExpense = getMoneyAttribute(PayeeAttribute.Expense);
        myExpense.addAmount(pEvent.getAmount());
    }

    /**
     * Adjust account for tax payments.
     * @param pEvent the event causing the payments
     */
    protected void adjustForTaxPayments(final Event pEvent) {
        JMoney myExpense = getMoneyAttribute(PayeeAttribute.Expense);
        JMoney myTaxCredit = pEvent.getTaxCredit();
        JMoney myNatInsurance = pEvent.getNatInsurance();
        if (myTaxCredit != null) {
            myExpense.addAmount(myTaxCredit);
        }
        if (myNatInsurance != null) {
            myExpense.addAmount(myNatInsurance);
        }
    }

    /**
     * Add income value.
     * @param pValue the value to add
     */
    protected void addIncome(final JMoney pValue) {
        JMoney myIncome = getMoneyAttribute(PayeeAttribute.Income);
        myIncome.addAmount(pValue);
    }

    /**
     * Subtract income value.
     * @param pValue the value to subtract
     */
    protected void subtractIncome(final JMoney pValue) {
        JMoney myIncome = getMoneyAttribute(PayeeAttribute.Income);
        myIncome.subtractAmount(pValue);
    }

    /**
     * Add expense value.
     * @param pValue the value to add
     */
    protected void addExpense(final JMoney pValue) {
        JMoney myExpense = getMoneyAttribute(PayeeAttribute.Expense);
        myExpense.addAmount(pValue);
    }

    /**
     * Subtract expense value.
     * @param pValue the value to subtract
     */
    protected void subtractExpense(final JMoney pValue) {
        JMoney myExpense = getMoneyAttribute(PayeeAttribute.Expense);
        myExpense.subtractAmount(pValue);
    }

    /**
     * Create a save point.
     */
    protected void createSavePoint() {
        /* Copy attribute map to SavePoint */
        copyMap(theAttributes, theSavePoint);
    }

    /**
     * Restore a Save Point.
     * @param pDate the date to restore.
     */
    protected void restoreSavePoint(final JDateDay pDate) {
        /* Copy attribute map to SavePoint */
        copyMap(theSavePoint, theAttributes);
    }

    /**
     * Copy a map.
     * @param pSource the source map
     * @param pTarget the target map
     */
    protected void copyMap(final Map<PayeeAttribute, Object> pSource,
                           final Map<PayeeAttribute, Object> pTarget) {
        /* Clear the target map */
        pTarget.clear();

        /* For each entry in the source map */
        for (Map.Entry<PayeeAttribute, Object> myEntry : pSource.entrySet()) {
            /* Access key and object */
            PayeeAttribute myAttr = myEntry.getKey();
            Object myObject = myEntry.getValue();

            /* Create copy of object in map */
            if (myObject instanceof JMoney) {
                pTarget.put(myAttr, new JMoney((JMoney) myObject));
            }
        }
    }

    /**
     * Calculate delta.
     */
    private void calculateDelta() {
        /* Obtain a copy of the value */
        JMoney myDelta = new JMoney(getMoneyAttribute(PayeeAttribute.Income));

        /* Subtract the expense value */
        myDelta.subtractAmount(getMoneyAttribute(PayeeAttribute.Expense));

        /* Set the delta */
        setAttribute(PayeeAttribute.IncomeDelta, myDelta);
    }

    /**
     * analyse bucket.
     * @param pDate the date for analysis
     */
    protected void analyseBucket(final JDateDay pDate) {
        calculateDelta();
    }

    /**
     * Is the bucket active?
     * @return true/false
     */
    public boolean isActive() {
        JMoney myIncome = getMoneyAttribute(PayeeAttribute.Income);
        JMoney myExpense = getMoneyAttribute(PayeeAttribute.Expense);
        if ((myIncome != null)
            && (myIncome.isNonZero())) {
            return true;
        }
        return ((myExpense != null) && (myExpense.isNonZero()));
    }

    /**
     * Is the bucket relevant? That is to say is either this bucket or it's base active?
     * @return true/false
     */
    public boolean isRelevant() {
        /* Relevant if this value or the previous value is non-zero */
        if (isActive()) {
            return true;
        }
        return (theBase != null)
               && (theBase.isActive());
    }

    /**
     * PayeeBucket list class.
     */
    public static class PayeeBucketList
            extends OrderedIdList<Integer, PayeeBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(PayeeBucketList.class.getSimpleName());

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
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public PayeeBucketList(final Analysis pAnalysis) {
            super(PayeeBucket.class);
            theAnalysis = pAnalysis;
        }

        /**
         * Obtain the PayeeBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected PayeeBucket getBucket(final Account pAccount) {
            /* Locate the bucket in the list */
            PayeeBucket myItem = findItemById(pAccount.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new PayeeBucket(theAnalysis, pAccount);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }
    }

    /**
     * PayeeAttribute enumeration.
     */
    public enum PayeeAttribute {
        /**
         * Income.
         */
        Income,

        /**
         * Expense.
         */
        Expense,

        /**
         * Income Delta.
         */
        IncomeDelta;
    }
}
