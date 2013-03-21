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

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
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
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.views.CapitalEvent.CapitalEventList;

/**
 * The Account Bucket class.
 */
public abstract class AccountBucket
        extends AnalysisBucket {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountBucket.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

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
    public AccountCategoryType getAccountType() {
        return theAccount.getActType();
    }

    /**
     * Constructor.
     * @param pType the type
     * @param pAccount the account
     */
    private AccountBucket(final BucketType pType,
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
    private AccountBucket(final AccountBucket pBase) {
        /* Call super-constructor */
        super(pBase);

        /* Store the account */
        theAccount = pBase.getAccount();
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
        AccountBucket myThat = (AccountBucket) pThat;

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

    /**
     * The ValueBucket class.
     */
    protected abstract static class ValueBucket
            extends AccountBucket {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ValueBucket.class.getSimpleName(), AccountBucket.FIELD_DEFS);

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
        public ValueBucket getBase() {
            return (ValueBucket) super.getBase();
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
            return (getBase() != null)
                    ? getBase().getValue()
                    : null;
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
        private ValueBucket(final BucketType pType,
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
        private ValueBucket(final ValueBucket pBase) {
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
     * The MoneyAccountDetail Bucket class.
     */
    public static final class MoneyAccountDetail
            extends ValueBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(MoneyAccountDetail.class.getSimpleName(), ValueBucket.FIELD_DEFS);

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
                return (theRate != null)
                        ? theRate
                        : JDataFieldValue.SkipField;
            }
            if (FIELD_MATURITY.equals(pField)) {
                return (theMaturity != null)
                        ? theMaturity
                        : JDataFieldValue.SkipField;
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
        private MoneyAccountDetail theSavePoint = null;

        @Override
        public MoneyAccountDetail getBase() {
            return (MoneyAccountDetail) super.getBase();
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
        protected MoneyAccountDetail(final Account pAccount) {
            /* Call super-constructor */
            super(BucketType.BANKDETAIL, pAccount);
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket.
         */
        protected MoneyAccountDetail(final MoneyAccountDetail pBase) {
            /* Call super-constructor */
            super(pBase.cloneIt());

            /* Initialise the Money values */
            setValue(new JMoney(pBase.getValue()));
        }

        /**
         * Create a clone of the money account.
         * @return the cloned MoneyAccount.
         */
        private MoneyAccountDetail cloneIt() {
            /* Call super-constructor */
            MoneyAccountDetail myClone = new MoneyAccountDetail(getAccount());

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
            theSavePoint = new MoneyAccountDetail(this);
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
     * The DebtAccountDetail Bucket class.
     */
    public static final class DebtAccountDetail
            extends ValueBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(DebtAccountDetail.class.getSimpleName(), ValueBucket.FIELD_DEFS);

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
        private DebtAccountDetail theSavePoint = null;

        @Override
        public DebtAccountDetail getBase() {
            return (DebtAccountDetail) super.getBase();
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
        protected DebtAccountDetail(final Account pAccount) {
            /* Call super-constructor */
            super(BucketType.DEBTDETAIL, pAccount);

            /* Initialise the money values */
            theSpend = new JMoney();
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        protected DebtAccountDetail(final DebtAccountDetail pBase) {
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
        private DebtAccountDetail cloneIt() {
            /* Call super-constructor */
            DebtAccountDetail myClone = new DebtAccountDetail(getAccount());

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
            theSavePoint = new DebtAccountDetail(this);
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
     * The AssetAccountDetail Bucket class.
     */
    public static final class AssetAccountDetail
            extends ValueBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AssetAccountDetail.class.getSimpleName(), ValueBucket.FIELD_DEFS);

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
        private AssetAccountDetail theSavePoint = null;

        @Override
        public AssetAccountDetail getBase() {
            return (AssetAccountDetail) super.getBase();
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
            return (getBase() != null)
                    ? getBase().getCost()
                    : null;
        }

        /**
         * Obtain previous units.
         * @return the units
         */
        public JUnits getPrevUnits() {
            return (getBase() != null)
                    ? getBase().getUnits()
                    : null;
        }

        /**
         * Obtain previous gained.
         * @return the gained
         */
        public JMoney getPrevGained() {
            return (getBase() != null)
                    ? getBase().getGained()
                    : null;
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
        protected AssetAccountDetail(final FinanceData pData,
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
        protected AssetAccountDetail(final AssetAccountDetail pBase) {
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
        private AssetAccountDetail cloneIt() {
            /* Call super-constructor */
            AssetAccountDetail myClone = new AssetAccountDetail(theData, getAccount());

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
            theSavePoint = new AssetAccountDetail(this);
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
     * The PayeeAccountDetail Bucket class.
     */
    public static final class PayeeAccountDetail
            extends AccountBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(PayeeAccountDetail.class.getSimpleName(), AccountBucket.FIELD_DEFS);

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
        private PayeeAccountDetail theSavePoint = null;

        @Override
        public PayeeAccountDetail getBase() {
            return (PayeeAccountDetail) super.getBase();
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
            return (getBase() != null)
                    ? getBase().getIncome()
                    : null;
        }

        /**
         * Obtain previous expense.
         * @return the expense
         */
        public JMoney getPrevExpense() {
            return (getBase() != null)
                    ? getBase().getExpense()
                    : null;
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        protected PayeeAccountDetail(final Account pAccount) {
            /* Call super-constructor */
            super(BucketType.PAYEEDETAIL, pAccount);

            /* Initialise the money values */
            theIncome = new JMoney();
            theExpense = new JMoney();
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        protected PayeeAccountDetail(final PayeeAccountDetail pBase) {
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
        private PayeeAccountDetail cloneIt() {
            /* Call super-constructor */
            PayeeAccountDetail myClone = new PayeeAccountDetail(getAccount());

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
            EventCategoryType myCategory = pEvent.getCategoryType();
            JMoney myAmount = pEvent.getAmount();
            JMoney myTaxCred = pEvent.getTaxCredit();

            /* If this is a recovered transaction */
            if (myCategory.isRecovered()) {
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
            theSavePoint = new PayeeAccountDetail(this);
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
}
