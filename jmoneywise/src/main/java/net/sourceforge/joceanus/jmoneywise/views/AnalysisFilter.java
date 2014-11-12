/**
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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisType;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketValues;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.EventAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.CategoryValues;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeValues;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionTagBucket;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionGroup;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Analysis Filter Classes.
 * @param <T> the attribute for the filter
 */
public abstract class AnalysisFilter<T extends Enum<T> & BucketAttribute>
        implements JDataContents {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseViewResource.FILTER_NAME.getValue());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Bucket Field Id.
     */
    private static final JDataField FIELD_BUCKET = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_BUCKET.getValue());

    /**
     * Attribute Field Id.
     */
    private static final JDataField FIELD_ATTR = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.FILTER_ATTR.getValue());

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ATTR.equals(pField)) {
            return theAttr;
        }
        /* Unknown */
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    /**
     * The Current Attribute.
     */
    private T theAttr;

    /**
     * The Attribute class.
     */
    private final Class<T> theClass;

    /**
     * Set attribute.
     * @param pAttr the attribute
     */
    public void setCurrentAttribute(final BucketAttribute pAttr) {
        theAttr = theClass.cast(pAttr);
    }

    /**
     * Obtain current attribute.
     * @return the current attribute
     */
    public T getCurrentAttribute() {
        return theAttr;
    }

    /**
     * Get Analysis Type.
     * @return the Analysis Type
     */
    public abstract AnalysisType getAnalysisType();

    /**
     * Constructor.
     * @param pClass the attribute class
     */
    protected AnalysisFilter(final Class<T> pClass) {
        theClass = pClass;
    }

    /**
     * Should we filter this transaction out?
     * @param pTrans the transaction to check
     * @return true/false
     */
    public boolean filterTransaction(final Transaction pTrans) {
        /* If this is a split event */
        if (pTrans.isSplit()) {
            /* Filter out children */
            if (pTrans.isChild()) {
                return true;
            }

            /* Access the group */
            TransactionList myList = pTrans.getList();
            TransactionGroup myGroup = myList.getGroup(pTrans);

            /* Check parent */
            if (!filterSingleTransaction(pTrans)) {
                return false;
            }

            /* Loop through the children */
            Iterator<Transaction> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Transaction myTrans = myIterator.next();

                /* Check transaction */
                if (!filterSingleTransaction(myTrans)) {
                    return false;
                }
            }

            /* Ignore Transaction Group */
            return true;
        }

        /* Check as a single transaction */
        return filterSingleTransaction(pTrans);
    }

    /**
     * Should we filter this transaction out?
     * @param pTrans the transaction to check
     * @return true/false
     */
    public boolean filterSingleTransaction(final Transaction pTrans) {
        /* Check whether this transaction is registered */
        return !pTrans.isHeader()
               && getValuesForTransaction(pTrans) == null;
    }

    /**
     * Obtain base bucket values.
     * @return the value
     */
    protected abstract BucketValues<?, T> getBaseValues();

    /**
     * Obtain values for transaction.
     * @param pTrans the transaction
     * @return the values
     */
    public abstract BucketValues<?, T> getValuesForTransaction(final Transaction pTrans);

    /**
     * Obtain delta value for transaction.
     * @param pTrans the transaction
     * @return the delta value
     */
    public abstract JDecimal getDeltaForTransaction(final Transaction pTrans);

    /**
     * Obtain starting value for attribute.
     * @return the value
     */
    public JDecimal getStartingBalance() {
        BucketValues<?, T> myValues = getBaseValues();
        return myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain total money value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public JDecimal getBalanceForTransaction(final Transaction pTrans) {
        /* If this is a split transaction */
        if (pTrans.isSplit()) {
            /* Access the group */
            TransactionList myList = pTrans.getList();
            TransactionGroup myGroup = myList.getGroup(pTrans);

            /* Initialise return as the balance for the parent */
            JDecimal myBalance = getSingleBalanceForTransaction(pTrans);

            /* Loop through the children */
            Iterator<Transaction> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Transaction myTrans = myIterator.next();

                /* Access Balance for transaction */
                JDecimal myValue = getSingleBalanceForTransaction(myTrans);
                if (myValue != null) {
                    /* We need the latest value */
                    myBalance = myValue;
                }
            }

            /* Return the balance */
            return myBalance;
        }

        /* Obtain single transaction value */
        return getSingleBalanceForTransaction(pTrans);
    }

    /**
     * Obtain delta debit value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public JDecimal getDebitForTransaction(final Transaction pTrans) {
        JDecimal myValue = getDeltaValueForTransaction(pTrans);
        if (myValue != null) {
            if (myValue.isPositive()
                || myValue.isZero()) {
                myValue = null;
            } else {
                myValue.negate();
            }
        }
        return (myValue != null)
                                ? myValue
                                : null;
    }

    /**
     * Obtain delta credit value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public JDecimal getCreditForTransaction(final Transaction pTrans) {
        JDecimal myValue = getDeltaValueForTransaction(pTrans);
        return ((myValue != null)
                && myValue.isPositive() && myValue.isNonZero())
                                                               ? myValue
                                                               : null;
    }

    /**
     * Obtain delta value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    private JDecimal getDeltaValueForTransaction(final Transaction pTrans) {
        /* If this is a split transaction */
        if (pTrans.isSplit()) {
            /* Access the group */
            TransactionList myList = pTrans.getList();
            TransactionGroup myGroup = myList.getGroup(pTrans);

            /* Initialise return value as delta for parent */
            JDecimal myTotal = getDeltaForTransaction(pTrans);

            /* Loop through the children */
            Iterator<Transaction> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Transaction myTrans = myIterator.next();

                /* Access Delta for transaction */
                JDecimal myDelta = getDeltaForTransaction(myTrans);
                if (myDelta != null) {
                    /* If this is the first value */
                    if (myTotal == null) {
                        /* Record as value */
                        myTotal = myDelta;

                        /* else need to add values */
                    } else {
                        /* add values appropriately */
                        myTotal = addDecimals(myTotal, myDelta);
                    }
                }
            }

            /* Return the total */
            return myTotal;
        }

        /* Obtain single transaction value */
        return getDeltaForTransaction(pTrans);
    }

    /**
     * Add decimal values.
     * @param pFirst the first decimal
     * @param pSecond the second decimal
     * @return the sum
     */
    private JDecimal addDecimals(final JDecimal pFirst,
                                 final JDecimal pSecond) {
        switch (theAttr.getDataType()) {
            case MONEY:
                ((JMoney) pFirst).addAmount((JMoney) pSecond);
                return pFirst;
            case UNITS:
                ((JUnits) pFirst).addUnits((JUnits) pSecond);
                return pFirst;
            default:
                return null;
        }
    }

    /**
     * Obtain total money value for attribute.
     * @param pTrans the transaction to check
     * @return the value
     */
    public JDecimal getSingleBalanceForTransaction(final Transaction pTrans) {
        BucketValues<?, T> myValues = getValuesForTransaction(pTrans);
        return (myValues == null)
                                 ? null
                                 : myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain analysis name.
     * @return the name
     */
    public abstract String getName();

    /**
     * Loan Bucket filter class.
     * @param <T> the account data type
     */
    public abstract static class AccountFilter<T extends AssetBase<T>>
            extends AnalysisFilter<AccountAttribute> {
        /**
         * The Account bucket.
         */
        private final AccountBucket<T> theAccount;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theAccount;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public AccountBucket<T> getBucket() {
            return theAccount;
        }

        @Override
        public String getName() {
            return theAccount.getName();
        }

        /**
         * Constructor.
         * @param pAccount the account bucket
         */
        public AccountFilter(final AccountBucket<T> pAccount) {
            /* Store parameter */
            super(AccountAttribute.class);
            theAccount = pAccount;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected AccountValues getBaseValues() {
            return theAccount.getBaseValues();
        }

        @Override
        public AccountValues getValuesForTransaction(final Transaction pTrans) {
            return theAccount.getValuesForTransaction(pTrans);
        }

        @Override
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return theAccount.getDeltaForTransaction(pTrans, getCurrentAttribute());
        }
    }

    /**
     * Deposit Bucket filter class.
     */
    public static class DepositFilter
            extends AccountFilter<Deposit> {
        @Override
        public DepositBucket getBucket() {
            return (DepositBucket) super.getBucket();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.DEPOSIT;
        }

        /**
         * Constructor.
         * @param pDeposit the deposit bucket
         */
        public DepositFilter(final DepositBucket pDeposit) {
            /* Call super-constructor */
            super(pDeposit);
        }
    }

    /**
     * Cash Bucket filter class.
     */
    public static class CashFilter
            extends AccountFilter<Cash> {
        @Override
        public CashBucket getBucket() {
            return (CashBucket) super.getBucket();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.CASH;
        }

        /**
         * Constructor.
         * @param pCash the cash bucket
         */
        public CashFilter(final CashBucket pCash) {
            /* Call super-constructor */
            super(pCash);
        }
    }

    /**
     * Loan Bucket filter class.
     */
    public static class LoanFilter
            extends AccountFilter<Loan> {
        @Override
        public LoanBucket getBucket() {
            return (LoanBucket) super.getBucket();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.LOAN;
        }

        /**
         * Constructor.
         * @param pLoan the loan bucket
         */
        public LoanFilter(final LoanBucket pLoan) {
            /* Call super-constructor */
            super(pLoan);
        }
    }

    /**
     * Security Bucket filter class.
     */
    public static class SecurityFilter
            extends AnalysisFilter<SecurityAttribute> {
        /**
         * The security bucket.
         */
        private final SecurityBucket theSecurity;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theSecurity;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public SecurityBucket getBucket() {
            return theSecurity;
        }

        @Override
        public String getName() {
            return theSecurity.getDecoratedName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.SECURITY;
        }

        /**
         * Constructor.
         * @param pSecurity the security bucket
         */
        public SecurityFilter(final SecurityBucket pSecurity) {
            /* Store parameter */
            super(SecurityAttribute.class);
            theSecurity = pSecurity;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected SecurityValues getBaseValues() {
            return theSecurity.getBaseValues();
        }

        @Override
        public SecurityValues getValuesForTransaction(final Transaction pTrans) {
            return theSecurity.getValuesForTransaction(pTrans);
        }

        @Override
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return theSecurity.getDeltaForTransaction(pTrans, getCurrentAttribute());
        }
    }

    /**
     * Portfolio Bucket filter class.
     */
    public static class PortfolioFilter
            extends AnalysisFilter<AccountAttribute> {
        /**
         * The portfolio bucket.
         */
        private final PortfolioBucket thePortfolio;

        /**
         * The portfolio cash bucket.
         */
        private final PortfolioCashBucket theCash;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return thePortfolio;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public PortfolioBucket getBucket() {
            return thePortfolio;
        }

        @Override
        public String getName() {
            return thePortfolio.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.PORTFOLIO;
        }

        /**
         * Constructor.
         * @param pPortfolio the portfolio bucket
         */
        public PortfolioFilter(final PortfolioBucket pPortfolio) {
            /* Store parameter */
            super(AccountAttribute.class);
            thePortfolio = pPortfolio;
            theCash = thePortfolio.getPortfolioCash();
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected AccountValues getBaseValues() {
            return theCash.getBaseValues();
        }

        @Override
        public AccountValues getValuesForTransaction(final Transaction pTrans) {
            return theCash.getValuesForTransaction(pTrans);
        }

        @Override
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return theCash.getDeltaForTransaction(pTrans, getCurrentAttribute());
        }
    }

    /**
     * Payee Bucket filter class.
     */
    public static class PayeeFilter
            extends AnalysisFilter<PayeeAttribute> {
        /**
         * The payee bucket.
         */
        private final PayeeBucket thePayee;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return thePayee;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public PayeeBucket getBucket() {
            return thePayee;
        }

        @Override
        public String getName() {
            return thePayee.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.PAYEE;
        }

        /**
         * Constructor.
         * @param pPayee the payee bucket
         */
        public PayeeFilter(final PayeeBucket pPayee) {
            /* Store parameter */
            super(PayeeAttribute.class);
            thePayee = pPayee;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected PayeeValues getBaseValues() {
            return thePayee.getBaseValues();
        }

        @Override
        public PayeeValues getValuesForTransaction(final Transaction pTrans) {
            return thePayee.getValuesForTransaction(pTrans);
        }

        @Override
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return thePayee.getDeltaForTransaction(pTrans, getCurrentAttribute());
        }
    }

    /**
     * EventCategory Bucket filter class.
     */
    public static class EventCategoryFilter
            extends AnalysisFilter<EventAttribute> {
        /**
         * The event category bucket.
         */
        private final EventCategoryBucket theCategory;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theCategory;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public EventCategoryBucket getBucket() {
            return theCategory;
        }

        @Override
        public String getName() {
            return theCategory.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.CATEGORY;
        }

        /**
         * Constructor.
         * @param pCategory the category bucket
         */
        public EventCategoryFilter(final EventCategoryBucket pCategory) {
            /* Store parameter */
            super(EventAttribute.class);
            theCategory = pCategory;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected CategoryValues getBaseValues() {
            return theCategory.getBaseValues();
        }

        @Override
        public CategoryValues getValuesForTransaction(final Transaction pTrans) {
            return theCategory.getValuesForTransaction(pTrans);
        }

        @Override
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return theCategory.getDeltaForTransaction(pTrans, getCurrentAttribute());
        }
    }

    /**
     * TaxBasis Bucket filter class.
     */
    public static class TaxBasisFilter
            extends AnalysisFilter<TaxBasisAttribute> {
        /**
         * The taxBasis bucket.
         */
        private final TaxBasisBucket theTaxBasis;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theTaxBasis;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public TaxBasisBucket getBucket() {
            return theTaxBasis;
        }

        @Override
        public String getName() {
            return theTaxBasis.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.TAXBASIS;
        }

        /**
         * Constructor.
         * @param pTaxBasis the taxBasis bucket
         */
        public TaxBasisFilter(final TaxBasisBucket pTaxBasis) {
            /* Store parameter */
            super(TaxBasisAttribute.class);
            theTaxBasis = pTaxBasis;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected TaxBasisValues getBaseValues() {
            return theTaxBasis.getBaseValues();
        }

        @Override
        public TaxBasisValues getValuesForTransaction(final Transaction pTrans) {
            return theTaxBasis.getValuesForTransaction(pTrans);
        }

        @Override
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return theTaxBasis.getDeltaForTransaction(pTrans, getCurrentAttribute());
        }
    }

    /**
     * TransactionTag filter class.
     */
    public static class TagFilter
            extends AnalysisFilter<AccountAttribute> {
        /**
         * The tag.
         */
        private final TransactionTagBucket theTransTag;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theTransTag;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public TransactionTagBucket getTag() {
            return theTransTag;
        }

        @Override
        public String getName() {
            return theTransTag.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.TRANSTAG;
        }

        /**
         * Constructor.
         * @param pTag the transactionTag
         */
        public TagFilter(final TransactionTagBucket pTag) {
            /* Store parameter */
            super(AccountAttribute.class);
            theTransTag = pTag;
            setCurrentAttribute(null);
        }

        @Override
        public boolean filterSingleTransaction(final Transaction pTrans) {
            return pTrans.isHeader() || !theTransTag.hasTransaction(pTrans);
        }

        @Override
        protected AccountValues getBaseValues() {
            return null;
        }

        @Override
        public AccountValues getValuesForTransaction(final Transaction pTrans) {
            return null;
        }

        @Override
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return null;
        }
    }

    /**
     * All filter class.
     */
    public static class AllFilter
            extends AnalysisFilter<AccountAttribute> {
        @Override
        public String getName() {
            return AnalysisType.ALL.toString();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.ALL;
        }

        /**
         * Constructor.
         */
        public AllFilter() {
            /* Store parameter */
            super(AccountAttribute.class);
            setCurrentAttribute(null);
        }

        @Override
        public boolean filterSingleTransaction(final Transaction pTrans) {
            return pTrans.isHeader();
        }

        @Override
        protected AccountValues getBaseValues() {
            return null;
        }

        @Override
        public AccountValues getValuesForTransaction(final Transaction pTrans) {
            return null;
        }

        @Override
        public JDecimal getDeltaForTransaction(final Transaction pTrans) {
            return null;
        }
    }
}
