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

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisMaps.SecurityPriceMap;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DilutionEvent.DilutionEventList;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.EventCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.AssetType;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Class to analyse transactions.
 * @author Tony Washer
 */
public class TransactionAnalyser
        implements JDataContents {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TransactionAnalyser.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Analysis field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAnalysis"));

    /**
     * Analysis Manager field Id.
     */
    private static final JDataField FIELD_MANAGER = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataManager"));

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        if (FIELD_MANAGER.equals(pField)) {
            return theManager;
        }

        /* Unknown */
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The Amount Tax threshold for "small" transactions (�3000).
     */
    private static final JMoney LIMIT_VALUE = JMoney.getWholeUnits(3000);

    /**
     * The Rate Tax threshold for "small" transactions (5%).
     */
    private static final JRate LIMIT_RATE = JRate.getWholePercentage(5);

    /**
     * The dataSet being analysed.
     */
    private final MoneyWiseData theData;

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The analysis manager.
     */
    private final AnalysisManager theManager;

    /**
     * The deposit bucket list.
     */
    private final DepositBucketList theDepositBuckets;

    /**
     * The cash bucket list.
     */
    private final CashBucketList theCashBuckets;

    /**
     * The deposit bucket list.
     */
    private final LoanBucketList theLoanBuckets;

    /**
     * The portfolio bucket list.
     */
    private final PortfolioBucketList thePortfolioBuckets;

    /**
     * The payee bucket list.
     */
    private final PayeeBucketList thePayeeBuckets;

    /**
     * The event category buckets.
     */
    private final EventCategoryBucketList theCategoryBuckets;

    /**
     * The taxBasis buckets.
     */
    private final TaxBasisBucketList theTaxBasisBuckets;

    /**
     * The taxMan account.
     */
    private final PayeeBucket theTaxMan;

    /**
     * The dilutions.
     */
    private final DilutionEventList theDilutions;

    /**
     * Obtain the analysis manager.
     * @return the analysis manager
     */
    public AnalysisManager getAnalysisManager() {
        return theManager;
    }

    /**
     * Obtain the dilutions.
     * @return the dilutions
     */
    public DilutionEventList getDilutions() {
        return theDilutions;
    }

    /**
     * Constructor for a full year set of accounts.
     * @param pData the Data to analyse
     * @param pPreferenceMgr the preference manager
     * @throws JOceanusException on error
     */
    public TransactionAnalyser(final MoneyWiseData pData,
                               final PreferenceManager pPreferenceMgr) throws JOceanusException {
        /* Store the parameters */
        theData = pData;

        /* Access the lists */
        TaxYearList myTaxYears = theData.getTaxYears();
        TransactionList myTrans = theData.getTransactions();

        /* Create a new analysis */
        theAnalysis = new Analysis(theData, pPreferenceMgr);
        theManager = new AnalysisManager(theAnalysis, pPreferenceMgr.getLogger());

        /* Access details from the analysis */
        theDepositBuckets = theAnalysis.getDeposits();
        theCashBuckets = theAnalysis.getCash();
        theLoanBuckets = theAnalysis.getLoans();
        thePortfolioBuckets = theAnalysis.getPortfolios();
        thePayeeBuckets = theAnalysis.getPayees();
        theCategoryBuckets = theAnalysis.getEventCategories();
        theTaxBasisBuckets = theAnalysis.getTaxBasis();
        theDilutions = theAnalysis.getDilutions();
        theTaxMan = thePayeeBuckets.getBucket(PayeeTypeClass.TAXMAN);

        /* Access the Transaction iterator */
        Iterator<Transaction> myIterator = myTrans.listIterator();
        TaxYear myTax = null;
        JDateDay myDate = null;
        int myResult = -1;

        /* reset groups */
        myTrans.resetGroups();

        /* Loop through the Transactions extracting relevant elements */
        while (myIterator.hasNext()) {
            Transaction myCurr = myIterator.next();
            JDateDay myCurrDay = myCurr.getDate();

            /* Ignore deleted transactions */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* If we have a current tax year */
            if (myDate != null) {
                /* Check that this event is still in the tax year */
                myResult = myDate.compareTo(myCurrDay);
            }

            /* If we have exhausted the tax year or else this is the first tax year */
            if (myResult < 0) {
                /* Access the relevant tax year */
                myTax = myTaxYears.findTaxYearForDate(myCurrDay);
                myDate = myTax.getTaxYear();
            }

            /* Touch underlying items */
            myCurr.touchUnderlyingItems();

            /* If the transaction has a parent */
            Transaction myParent = myCurr.getParent();
            if (myParent != null) {
                /* Register child against parent */
                myTrans.registerChild(myCurr);
            }

            /* If the event has a dilution factor */
            if (myCurr.getDilution() != null) {
                /* Add to the dilution event list */
                theDilutions.addDilution(myCurr);
            }

            /* Process the transaction in the report set */
            processTransaction(myCurr);

            /* Touch tax year */
            myTax.touchItem(myCurr);
        }

        /* Analyse the basic ranged analysis */
        theManager.analyseBase();
    }

    /**
     * Mark active accounts.
     * @throws JOceanusException on error
     */
    public void markActiveAccounts() throws JOceanusException {
        /* Mark relevant accounts */
        theDepositBuckets.markActiveAccounts();
        theCashBuckets.markActiveAccounts();
        theLoanBuckets.markActiveAccounts();

        /* Mark relevant securities */
        thePortfolioBuckets.markActiveSecurities();
    }

    /**
     * Process a transaction.
     * @param pTrans the transaction to process
     * @throws JOceanusException on error
     */
    private void processTransaction(final Transaction pTrans) throws JOceanusException {
        /* Access key details */
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        AssetBase<?> myChild = null;
        JMoney myAmount = pTrans.getAmount();

        /* If the event relates to a security item, split out the workings */
        if ((myDebit instanceof Security)
            || (myCredit instanceof Security)) {
            /* Process as a Security transaction */
            processSecurityTransaction(pTrans);

            /* Else handle the event normally */
        } else {
            TransactionCategory myCat = pTrans.getCategory();

            /* Switch on category class */
            switch (myCat.getCategoryTypeClass()) {
                case INTEREST:
                    /* Obtain detailed category */
                    myCat = myDebit.getDetailedCategory(myCat);

                    /* True debit account is the parent */
                    myChild = myDebit.equals(myCredit)
                                                      ? null
                                                      : myDebit;
                    myDebit = myDebit.getParent();
                    break;
                case LOANINTERESTEARNED:
                    /* True debit account is the parent of the loan */
                    myDebit = myDebit.getParent();
                    break;
                case RENTALINCOME:
                case ROOMRENTALINCOME:
                    /* True debit account is the parent of the loan */
                    myChild = myDebit.equals(myCredit)
                                                      ? null
                                                      : myDebit;
                    myDebit = myCredit.getParent();
                    break;
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                    /* True credit account is the parent of the loan */
                    myCredit = myCredit.getParent();
                    break;
                default:
                    break;
            }

            /* If the debit account is auto-Expense */
            if ((myDebit instanceof Cash) && (myDebit.getAssetType() == AssetType.AUTOEXPENSE)) {
                /* Access debit as cash */
                Cash myCash = (Cash) myDebit;
                TransactionCategory myAuto = myCash.getAutoExpense();
                myDebit = myCash.getAutoPayee();
                myDebit.touchItem(pTrans);

                /* Subtract expense from Payee bucket */
                PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                myPayee.subtractExpense(pTrans, myAmount);

                /* Subtract expense from Category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.subtractExpense(pTrans, myAmount);
                theTaxBasisBuckets.adjustAutoExpense(pTrans, false);

                /* else handle normally */
            } else {
                /* Determine the type of the debit account */
                switch (myDebit.getAssetType()) {
                    case PAYEE:
                        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                        myPayee.adjustForDebit(pTrans);
                        break;
                    default:
                        AccountBucket<?> myAccount = getAccountBucket(myDebit);
                        myAccount.adjustForDebit(pTrans);
                        break;
                }
            }

            /* If the credit account is auto-Expense */
            if ((myCredit instanceof Cash) && (myCredit.getAssetType() == AssetType.AUTOEXPENSE)) {
                /* Access credit as cash */
                Cash myCash = (Cash) myCredit;
                TransactionCategory myAuto = myCash.getAutoExpense();
                myCredit = myCash.getAutoPayee();
                myCredit.touchItem(pTrans);

                /* Add expense to Payee bucket */
                PayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                myPayee.addExpense(pTrans, myAmount);

                /* Adjust the relevant category bucket */
                EventCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.addExpense(pTrans, myAmount);
                theTaxBasisBuckets.adjustAutoExpense(pTrans, true);

                /* else handle normally */
            } else {
                /* Determine the type of the credit account */
                switch (myCredit.getAssetType()) {
                    case PAYEE:
                        PayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                        myPayee.adjustForCredit(pTrans);
                        break;
                    default:
                        AccountBucket<?> myAccount = getAccountBucket(myCredit);
                        myAccount.adjustForCredit(pTrans);
                        break;
                }
            }

            /* If we should register the event with a child */
            if (myChild != null) {
                /* Access bucket and register it */
                AccountBucket<?> myAccount = getAccountBucket(myChild);
                myAccount.registerTransaction(pTrans);
            }

            /* Adjust the tax payments */
            theTaxMan.adjustForTaxPayments(pTrans);

            /* If the event category is not a transfer */
            if (!myCat.isTransfer()) {
                /* Adjust the relevant category buckets */
                theCategoryBuckets.adjustCategories(pTrans, myCat);
            }
        }
    }

    /**
     * Process a security transaction.
     * @param pTrans the transaction to process
     * @throws JOceanusException on error
     */
    private void processSecurityTransaction(final Transaction pTrans) throws JOceanusException {
        /* Switch on the category */
        TransactionCategory myCat = pTrans.getCategory();
        switch (myCat.getCategoryTypeClass()) {
        /* Process a stock split */
            case STOCKSPLIT:
            case STOCKADJUST:
                processStockSplit(pTrans);
                break;
            /* Process a stock right taken */
            case STOCKRIGHTSTAKEN:
                processTransferIn(pTrans);
                break;
            /* Process a stock right taken */
            case STOCKRIGHTSWAIVED:
                processStockRightWaived(pTrans);
                break;
            /* Process a stock DeMerger */
            case STOCKDEMERGER:
                processStockDeMerger(pTrans);
                break;
            /* Process a Stock TakeOver */
            case STOCKTAKEOVER:
                processStockTakeover(pTrans);
                break;
            /* Process a dividend */
            case DIVIDEND:
                processDividend(pTrans);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
                AssetBase<?> myDebit = pTrans.getDebit();
                AssetBase<?> myCredit = pTrans.getCredit();
                if ((myDebit instanceof Security)
                    && (((Security) myDebit).isSecurityClass(SecurityTypeClass.LIFEBOND))) {
                    processTaxableGain(pTrans);
                } else if (!(myDebit instanceof Security)) {
                    processTransferIn(pTrans);
                } else if (myCredit instanceof Security) {
                    processStockXchange(pTrans);
                } else {
                    processTransferOut(pTrans);
                }
                break;
            /* Throw an Exception */
            default:
                throw new JMoneyWiseLogicException("Unexpected category type: "
                                                   + myCat.getCategoryTypeClass());
        }
    }

    /**
     * Process a transaction that is a stock split.
     * <p>
     * This capital event relates only to the Credit Account since the debit account is the same.
     * @param pTrans the transaction
     */
    private void processStockSplit(final Transaction pTrans) {
        /* Stock split has identical credit/debit so just obtain credit account */
        AssetBase<?> myCredit = pTrans.getCredit();
        Portfolio myPortfolio = pTrans.getPortfolio();
        JUnits myDelta = pTrans.getCreditUnits();
        if (myDelta == null) {
            myDelta = new JUnits(pTrans.getDebitUnits());
            myDelta.negate();
        }

        /* Adjust the Security Units */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(myPortfolio, myCredit);
        myAsset.adjustUnits(myDelta);

        /* Register the transaction */
        myAsset.registerTransaction(pTrans);

        /* StockSplit/Adjust is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is a transfer into capital (also StockRightTaken).
     * <p>
     * This capital event relates only to the Credit Account.
     * @param pTrans the transaction
     */
    private void processTransferIn(final Transaction pTrans) {
        /* Access debit account and category */
        AssetBase<?> myDebit = pTrans.getDebit();
        TransactionCategory myCat = pTrans.getCategory();

        /* Adjust the credit transfer details */
        processCreditXferIn(pTrans);

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(pTrans);

        /* Determine the type of the debit account */
        switch (myDebit.getAssetType()) {
            case PAYEE:
                PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                myPayee.adjustForDebit(pTrans);
                break;
            default:
                AccountBucket<?> myAccount = getAccountBucket(myDebit);
                myAccount.adjustForDebit(pTrans);
                break;
        }

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant category buckets */
            theCategoryBuckets.adjustCategories(pTrans, myCat);
        }
    }

    /**
     * Process the credit side of a transfer in transaction.
     * @param pTrans the transaction
     */
    private void processCreditXferIn(final Transaction pTrans) {
        /* Transfer is to the credit account and may or may not have a change to the units */
        AssetBase<?> myCredit = pTrans.getCredit();
        Portfolio myPortfolio = pTrans.getPortfolio();
        JUnits myDeltaUnits = pTrans.getCreditUnits();
        JMoney myAmount = pTrans.getAmount();

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(myPortfolio, myCredit);

        /* Adjust the cost and investment */
        myAsset.adjustCost(myAmount);
        myAsset.adjustInvested(myAmount);

        /* If we have new units */
        if (myDeltaUnits != null) {
            /* Record change in units */
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* Register the transaction */
        myAsset.registerTransaction(pTrans);
    }

    /**
     * Process a dividend transaction.
     * <p>
     * This capital event relates to the only to Debit account, although the Credit account may be identical to the credit account in which case the dividend is
     * re-invested
     * @param pTrans the transaction
     */
    private void processDividend(final Transaction pTrans) {
        /* The main security that we are interested in is the debit account */
        AssetBase<?> mySecurity = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        Portfolio myPortfolio = pTrans.getPortfolio();
        JMoney myAmount = pTrans.getAmount();
        JMoney myTaxCredit = pTrans.getTaxCredit();
        JUnits myDeltaUnits = pTrans.getCreditUnits();

        /* Obtain detailed category */
        TransactionCategory myCat = myPortfolio.getDetailedCategory(pTrans.getCategory());
        myCat = mySecurity.getDetailedCategory(myCat);

        /* True debit account is the parent */
        AssetBase<?> myDebit = mySecurity.getParent();

        /* Adjust the debit payee bucket */
        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        myPayee.adjustForDebit(pTrans);

        /* Access the Asset Account Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(myPortfolio, mySecurity);

        /* If this is a re-investment */
        if (mySecurity.equals(myCredit)) {
            /* This amount is added to the cost, so record as the delta cost */
            myAsset.adjustCost(myAmount);

            /* Record the investment */
            myAsset.adjustInvested(myAmount);

            /* If we have new units */
            if (myDeltaUnits != null) {
                /* Record delta units */
                myAsset.adjustUnits(myDeltaUnits);
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a received dividend from the account */
                myAsset.adjustDividend(myTaxCredit);
            }

            /* else we are paying out to another account */
        } else {
            /* Adjust the dividend total for this asset */
            JMoney myAdjust = new JMoney(myAmount);

            /* Any tax credit is viewed as a realised dividend from the account */
            if (myTaxCredit != null) {
                myAdjust.addAmount(myTaxCredit);
            }

            /* The Dividend is viewed as a dividend from the account */
            myAsset.adjustDividend(myAdjust);

            /* Adjust the credit account bucket */
            AccountBucket<?> myBucket = getAccountBucket(myCredit);
            myBucket.adjustForCredit(pTrans);
        }

        /* Register the transaction */
        myAsset.registerTransaction(pTrans);

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(pTrans);

        /* Adjust the relevant category buckets */
        theCategoryBuckets.adjustCategories(pTrans, myCat);
    }

    /**
     * Process a transaction that is a transfer from capital.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pTrans the transaction
     */
    private void processTransferOut(final Transaction pTrans) {
        /* Access credit account and category */
        AssetBase<?> myCredit = pTrans.getCredit();
        TransactionCategory myCat = pTrans.getCategory();

        /* Adjust the debit transfer details */
        processDebitXferOut(pTrans);

        /* Adjust the credit account bucket */
        AccountBucket<?> myBucket = getAccountBucket(myCredit);
        myBucket.adjustForCredit(pTrans);

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant category buckets */
            theCategoryBuckets.adjustCategories(pTrans, myCat);
        }
    }

    /**
     * Process the debit side of a transfer out transaction.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pTrans the transaction
     */
    private void processDebitXferOut(final Transaction pTrans) {
        /* Transfer out is from the debit account and may or may not have units */
        AssetBase<?> myDebit = pTrans.getDebit();
        Portfolio myPortfolio = pTrans.getPortfolio();
        JMoney myAmount = pTrans.getAmount();
        JUnits myDeltaUnits = pTrans.getDebitUnits();

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(myPortfolio, myDebit);
        SecurityValues myValues = myAsset.getValues();

        /* Record the delta investment */
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAsset.adjustInvested(myDelta);

        /* Assume the the cost reduction is the full value */
        JMoney myReduction = new JMoney(myAmount);
        JMoney myCost = myValues.getMoneyValue(SecurityAttribute.COST);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            JUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Determine the delta to the cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustCost(myDeltaCost);
        }

        /* Determine the delta to the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustGains(myDeltaGains);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(pTrans, myDeltaGains);
        }

        /* Register the transaction */
        myAsset.registerTransaction(pTrans);
    }

    /**
     * Process a transaction that is a exchange between two capital accounts.
     * <p>
     * This represent a transfer out from the debit account and a transfer in to the credit account
     * @param pTrans the transaction
     */
    private void processStockXchange(final Transaction pTrans) {
        /* Adjust the debit transfer details */
        processDebitXferOut(pTrans);

        /* Adjust the credit transfer details */
        processCreditXferIn(pTrans);
    }

    /**
     * Process a transaction that is a taxable gain.
     * <p>
     * This capital event relates only to the Debit Asset
     * @param pTrans the transaction
     */
    private void processTaxableGain(final Transaction pTrans) {
        /* Taxable Gain is from the debit account and may or may not have units */
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        Portfolio myPortfolio = pTrans.getPortfolio();
        JMoney myAmount = pTrans.getAmount();
        JUnits myDeltaUnits = pTrans.getDebitUnits();

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(myPortfolio, myDebit);
        SecurityValues myValues = myAsset.getValues();

        /* Record the delta investment */
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAsset.adjustInvested(myDelta);

        /* Assume the the cost reduction is the full value */
        JMoney myReduction = new JMoney(myAmount);
        JMoney myCost = myValues.getMoneyValue(SecurityAttribute.COST);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            JUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Determine the delta to the cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustCost(myDeltaCost);
        }

        /* Determine the delta to the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustGains(myDeltaGains);
        }

        /* Register the event */
        myAsset.registerTransaction(pTrans);

        /* True debit account is the parent */
        myDebit = myDebit.getParent();

        /* Adjust the debit account bucket */
        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        myPayee.adjustForTaxCredit(pTrans);

        /* Adjust the credit account bucket */
        AccountBucket<?> myBucket = getAccountBucket(myCredit);
        myBucket.adjustForCredit(pTrans);

        /* Adjust the taxableGains category bucket */
        theCategoryBuckets.adjustTaxableGain(pTrans, myReduction);

        /* Adjust the TaxMan account for the tax credit */
        theTaxMan.adjustForTaxPayments(pTrans);

        /* Add the chargeable event */
        theAnalysis.getCharges().addTransaction(pTrans, myDeltaGains);
    }

    /**
     * Process a transaction that is stock right waived.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pTrans the transaction
     */
    private void processStockRightWaived(final Transaction pTrans) {
        /* Stock Right Waived is from the debit account */
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        Portfolio myPortfolio = pTrans.getPortfolio();
        SecurityPriceList myPrices = theData.getSecurityPrices();
        JMoney myAmount = pTrans.getAmount();
        JMoney myReduction;

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(myPortfolio, myDebit);
        SecurityValues myValues = myAsset.getValues();

        /* Record the delta investment */
        JMoney myDelta = new JMoney(myAmount);
        myDelta.negate();
        myAsset.adjustInvested(myDelta);

        /* Access the current cost */
        JMoney myCost = myValues.getMoneyValue(SecurityAttribute.COST);

        /* Get the appropriate price for the account */
        SecurityPrice myActPrice = myPrices.getLatestPrice((Security) myDebit, pTrans.getDate());
        JPrice myPrice = myActPrice.getPrice();

        /* Determine value of this stock at the current time */
        JUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
        JMoney myValue = myUnits.valueAtPrice(myPrice);

        /* Calculate the portion of the value that creates a large transaction */
        JMoney myPortion = myValue.valueAtRate(LIMIT_RATE);

        /* If this is a large stock waiver (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* Determine the total value of rights plus share value */
            JMoney myTotalValue = new JMoney(myAmount);
            myTotalValue.addAmount(myValue);

            /* Determine the reduction as a proportion of the total value */
            myReduction = myCost.valueAtWeight(myAmount, myTotalValue);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the reduction to be the entire amount */
            myReduction = new JMoney(myAmount);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new JMoney(myCost);
        }

        /* Calculate the delta cost */
        JMoney myDeltaCost = new JMoney(myReduction);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myAsset.adjustCost(myDeltaCost);

        /* Determine the delta gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have some gains */
        if (myDeltaGains.isNonZero()) {
            /* Record the delta gains */
            myAsset.adjustGains(myDeltaGains);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(pTrans, myDeltaGains);
        }

        /* Register the event */
        myValues = myAsset.registerTransaction(pTrans);

        /* Record additional details to the registered event values */
        myValues.setValue(SecurityAttribute.PRICE, myPrice);
        myValues.setValue(SecurityAttribute.VALUATION, myValue);

        /* Adjust the credit account bucket */
        AccountBucket<?> myBucket = getAccountBucket(myCredit);
        myBucket.adjustForCredit(pTrans);

        /* StockRightWaived is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is Stock DeMerger.
     * <p>
     * This capital event relates to both the Credit and Debit accounts
     * @param pTrans the transaction
     */
    private void processStockDeMerger(final Transaction pTrans) {
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        Portfolio myPortfolio = pTrans.getPortfolio();
        JDilution myDilution = pTrans.getDilution();
        JUnits myDeltaUnits = pTrans.getDebitUnits();

        /* Access the Debit Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(myPortfolio, myDebit);
        SecurityValues myValues = myAsset.getValues();

        /* Calculate the diluted value of the Debit account */
        JMoney myCost = myValues.getMoneyValue(SecurityAttribute.COST);
        JMoney myNewCost = myCost.getDilutedMoney(myDilution);

        /* Calculate the delta to the cost */
        JMoney myDeltaCost = new JMoney(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the delta cost/investment */
        myAsset.adjustCost(myDeltaCost);
        myAsset.adjustInvested(myDeltaCost);

        /* If we reduced the units */
        if (myDeltaUnits != null) {
            /* Record the delta units */
            myDeltaUnits = new JUnits(myDeltaUnits);
            myDeltaUnits.negate();
            myAsset.adjustUnits(myDeltaUnits);
        }

        /* Register the event */
        myAsset.registerTransaction(pTrans);

        /* Access the Credit Asset Account Bucket */
        myAsset = thePortfolioBuckets.getBucket(myPortfolio, myCredit);

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new JMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Record the delta cost/investment */
        myAsset.adjustCost(myDeltaCost);
        myAsset.adjustInvested(myDeltaCost);

        /* Record the current/delta units */
        myDeltaUnits = pTrans.getCreditUnits();
        myAsset.adjustUnits(myDeltaUnits);

        /* Register the transaction */
        myAsset.registerTransaction(pTrans);

        /* StockDeMerger is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is StockTakeover.
     * <p>
     * This can be accomplished using a cash portion (to a ThirdParty account) and these workings are split out.
     * @param pTrans the transaction
     */
    private void processStockTakeover(final Transaction pTrans) {
        JMoney myAmount = pTrans.getAmount();
        Deposit myThirdParty = pTrans.getThirdParty();

        /* If we have a ThirdParty cash part of the transaction */
        if ((myThirdParty != null)
            && (myAmount.isNonZero())) {
            /* Process a Stock And Cash TakeOver */
            processStockAndCashTakeOver(pTrans);
        } else {
            /* Process a StockOnly TakeOver */
            processStockOnlyTakeOver(pTrans);
        }
    }

    /**
     * Process a transaction that is a StockOnlyTakeover.
     * <p>
     * This capital event relates to both the Credit and Debit accounts
     * @param pTrans the transaction
     */
    private void processStockOnlyTakeOver(final Transaction pTrans) {
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        Portfolio myPortfolio = pTrans.getPortfolio();

        /* Access the Asset Security Buckets */
        SecurityBucket myDebitAsset = thePortfolioBuckets.getBucket(myPortfolio, myDebit);
        SecurityValues myDebitValues = myDebitAsset.getValues();
        SecurityBucket myCreditAsset = thePortfolioBuckets.getBucket(myPortfolio, myCredit);
        SecurityValues myCreditValues = myCreditAsset.getValues();

        /* Get the appropriate price for the credit account */
        SecurityPriceMap myPriceMap = theAnalysis.getPrices();
        JPrice myPrice = myPriceMap.getPriceForDate(myCredit, pTrans.getDate());

        /* Determine value of the stock part of the takeOver */
        JUnits myDeltaUnits = pTrans.getCreditUnits();
        JMoney myStockValue = myDeltaUnits.valueAtPrice(myPrice);

        /* Determine the residual cost of the old stock */
        JMoney myStockCost = myDebitValues.getMoneyValue(SecurityAttribute.COST);

        /* Adjust cost/units/invested of the credit account */
        myCreditAsset.adjustCost(myStockCost);
        myCreditAsset.adjustUnits(myDeltaUnits);
        myCreditAsset.adjustInvested(myStockValue);

        /* Register the transaction */
        myCreditValues = myCreditAsset.registerTransaction(pTrans);
        myCreditValues.setValue(SecurityAttribute.PRICE, myPrice);
        myCreditValues.setValue(SecurityAttribute.VALUATION, myStockValue);

        /* Drive debit cost down to zero */
        JMoney myCost = myDebitValues.getMoneyValue(SecurityAttribute.COST);
        JMoney myDeltaCost = new JMoney(myCost);
        myDeltaCost.negate();
        myDebitAsset.adjustCost(myDeltaCost);

        /* Drive debit units down to zero */
        JUnits myUnits = myDebitValues.getUnitsValue(SecurityAttribute.UNITS);
        myDeltaUnits = new JUnits(myUnits);
        myDeltaUnits.negate();
        myDebitAsset.adjustUnits(myDeltaUnits);

        /* Adjust debit Invested amount */
        myStockValue = new JMoney(myStockValue);
        myStockValue.negate();
        myDebitAsset.adjustInvested(myStockValue);

        /* Register the transaction */
        myDebitAsset.registerTransaction(pTrans);
    }

    /**
     * Process a transaction that is StockAndCashTakeover.
     * <p>
     * This capital event relates to both the Credit and Debit accounts. In particular it makes reference to the CashTakeOver aspect of the debit account
     * @param pTrans the transaction
     */
    private void processStockAndCashTakeOver(final Transaction pTrans) {
        JDateDay myDate = pTrans.getDate();
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        Portfolio myPortfolio = pTrans.getPortfolio();
        Deposit myThirdParty = pTrans.getThirdParty();
        JMoney myAmount = pTrans.getAmount();

        /* Access the Asset Security Buckets */
        SecurityBucket myDebitAsset = thePortfolioBuckets.getBucket(myPortfolio, myDebit);
        SecurityValues myDebitValues = myDebitAsset.getValues();
        SecurityBucket myCreditAsset = thePortfolioBuckets.getBucket(myPortfolio, myCredit);
        SecurityValues myCreditValues = myCreditAsset.getValues();

        /* Get the appropriate prices for the assets */
        SecurityPriceMap myPriceMap = theAnalysis.getPrices();
        JPrice myDebitPrice = myPriceMap.getPriceForDate(myDebit, myDate);
        JPrice myCreditPrice = myPriceMap.getPriceForDate(myCredit, myDate);

        /* Determine value of the base stock */
        JUnits myBaseUnits = myDebitValues.getUnitsValue(SecurityAttribute.UNITS);
        JMoney myBaseValue = myBaseUnits.valueAtPrice(myDebitPrice);

        /* Determine value of the stock part of the takeOver */
        JUnits myDeltaUnits = pTrans.getCreditUnits();
        JMoney myStockValue = myDeltaUnits.valueAtPrice(myCreditPrice);

        /* Access the current debit cost */
        JMoney myCost = myDebitValues.getMoneyValue(SecurityAttribute.COST);
        JMoney myCostXfer;

        /* Calculate the portion of the value that creates a large transaction */
        JMoney myPortion = myBaseValue.valueAtRate(LIMIT_RATE);

        /* If this is a large cash takeOver portion (> both valueLimit and rateLimit of value) */
        if ((myAmount.compareTo(LIMIT_VALUE) > 0)
            && (myAmount.compareTo(myPortion) > 0)) {
            /* Calculate the total cost of the takeOver */
            JMoney myTotalCost = new JMoney(myAmount);
            myTotalCost.addAmount(myStockValue);

            /* Determine the transferable cost */
            myCostXfer = myCost.valueAtWeight(myStockValue, myTotalCost);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* If the cash amount is greater than the total cost */
            if (myAmount.compareTo(myCost) > 0) {
                /* No Cost is transferred to the credit asset */
                myCostXfer = new JMoney();
            } else {
                /* Transferred cost is cost minus the cash amount */
                myCostXfer = new JMoney(myCost);
                myCostXfer.subtractAmount(myAmount);
            }
        }

        /* Calculate the gains */
        JMoney myDeltaGains = new JMoney(myAmount);
        myDeltaGains.subtractAmount(myCost);
        myDeltaGains.addAmount(myCostXfer);

        /* If we have some gains */
        if (myDeltaGains.isNonZero()) {
            /* Record the delta gains */
            myDebitAsset.adjustGains(myDeltaGains);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(pTrans, myDeltaGains);
        }

        /* Adjust cost/units/invested of the credit account */
        myCreditAsset.adjustCost(myCostXfer);
        myCreditAsset.adjustUnits(myDeltaUnits);
        myCreditAsset.adjustInvested(myStockValue);

        /* Register the transaction */
        myCreditValues = myCreditAsset.registerTransaction(pTrans);
        myCreditValues.setValue(SecurityAttribute.PRICE, myCreditPrice);
        myCreditValues.setValue(SecurityAttribute.VALUATION, myStockValue);

        /* Drive debit cost down to zero */
        JMoney myDeltaCost = new JMoney(myCost);
        myDeltaCost.negate();
        myDebitAsset.adjustCost(myDeltaCost);

        /* Drive debit units down to zero */
        JUnits myUnits = myDebitValues.getUnitsValue(SecurityAttribute.UNITS);
        myDeltaUnits = new JUnits(myUnits);
        myDeltaUnits.negate();
        myDebitAsset.adjustUnits(myDeltaUnits);

        /* Adjust debit Invested amount */
        myStockValue = new JMoney(myStockValue);
        myStockValue.addAmount(pTrans.getAmount());
        myStockValue.negate();
        myDebitAsset.adjustInvested(myStockValue);

        /* Register the transaction */
        myDebitValues = myDebitAsset.registerTransaction(pTrans);
        myDebitValues.setValue(SecurityAttribute.PRICE, myDebitPrice);
        myCreditValues.setValue(SecurityAttribute.VALUATION, myBaseValue);

        /* Adjust the ThirdParty account bucket */
        AccountBucket<?> myBucket = getAccountBucket(myThirdParty);
        myBucket.adjustForCredit(pTrans);
    }

    /**
     * Obtain Account bucket for asset.
     * @param pAsset the asset
     * @return the bucket
     */
    private AccountBucket<?> getAccountBucket(final AssetBase<?> pAsset) {
        switch (pAsset.getAssetType()) {
            case DEPOSIT:
                return theDepositBuckets.getBucket((Deposit) pAsset);
            case CASH:
                return theCashBuckets.getBucket((Cash) pAsset);
            case LOAN:
                return theLoanBuckets.getBucket((Loan) pAsset);
            default:
                return null;
        }
    }
}