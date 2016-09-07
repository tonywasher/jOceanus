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
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.analysis.CashBucket.CashBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.CashCategoryBucket.CashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositBucket.DepositBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DepositCategoryBucket.DepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.DilutionEvent.DilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanBucket.LoanBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.LoanCategoryBucket.LoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionCategoryBucket.TransactionCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionTagBucket.TransactionTagBucketList;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.AssetType;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceDataMap;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Class to analyse transactions.
 * @author Tony Washer
 */
public class TransactionAnalyser
        implements MetisDataContents {
    /**
     * Local Report fields.
     */
    private static final String ERROR_CATEGORY = "Unexpected Category Type: ";

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.ANALYSIS_ANALYSER.getValue());

    /**
     * Analysis field Id.
     */
    private static final MetisField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * The Amount Tax threshold for "small" transactions (£3000).
     */
    private static final TethysMoney LIMIT_VALUE = TethysMoney.getWholeUnits(3000);

    /**
     * The Rate Tax threshold for "small" transactions (5%).
     */
    private static final TethysRate LIMIT_RATE = TethysRate.getWholePercentage(5);

    /**
     * The security holding map.
     */
    private final SecurityHoldingMap theHoldingMap;

    /**
     * The security price map.
     */
    private final SecurityPriceDataMap<SecurityPrice> thePriceMap;

    /**
     * The analysis.
     */
    private final Analysis theAnalysis;

    /**
     * The transaction helper.
     */
    private final TransactionHelper theHelper;

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
     * The transaction category buckets.
     */
    private final TransactionCategoryBucketList theCategoryBuckets;

    /**
     * The transactionTag buckets.
     */
    private final TransactionTagBucketList theTagBuckets;

    /**
     * The taxBasis buckets.
     */
    private final TaxBasisBucketList theTaxBasisBuckets;

    /**
     * The security transactions.
     */
    private final List<Transaction> theSecurities;

    /**
     * The taxMan account.
     */
    private final PayeeBucket theTaxMan;

    /**
     * The dilutions.
     */
    private final DilutionEventMap theDilutions;

    /**
     * The profile.
     */
    private final MetisProfile theProfile;

    /**
     * Constructor for a complete set of accounts.
     * @param pTask the profiled task
     * @param pData the Data to analyse
     * @param pPreferenceMgr the preference manager
     * @throws OceanusException on error
     */
    public TransactionAnalyser(final MetisProfile pTask,
                               final MoneyWiseData pData,
                               final MetisPreferenceManager pPreferenceMgr) throws OceanusException {
        /* Start a new task */
        theProfile = pTask;
        MetisProfile myTask = theProfile.startTask("analyseTransactions");

        /* Store the parameters */
        theHoldingMap = pData.getSecurityHoldingsMap();
        thePriceMap = pData.getSecurityPriceDataMap();

        /* Access the lists */
        TaxYearList myTaxYears = pData.getTaxYears();
        TransactionList myTrans = pData.getTransactions();

        /* Create a new analysis */
        myTask.startTask("Initialise");
        theAnalysis = new Analysis(pData, pPreferenceMgr);
        theSecurities = theAnalysis.getSecurities();

        /* Create new helper and set opening balances */
        theHelper = new TransactionHelper(pData);
        theAnalysis.addOpeningBalances(theHelper);

        /* Access details from the analysis */
        theDepositBuckets = theAnalysis.getDeposits();
        theCashBuckets = theAnalysis.getCash();
        theLoanBuckets = theAnalysis.getLoans();
        thePortfolioBuckets = theAnalysis.getPortfolios();
        thePayeeBuckets = theAnalysis.getPayees();
        theCategoryBuckets = theAnalysis.getTransCategories();
        theTagBuckets = theAnalysis.getTransactionTags();
        theTaxBasisBuckets = theAnalysis.getTaxBasis();
        theDilutions = theAnalysis.getDilutions();
        theTaxMan = thePayeeBuckets.getBucket(PayeeTypeClass.TAXMAN);

        /* Initialise data */
        TaxYear myTax = null;
        TethysDate myDate = null;
        int myResult = -1;

        /* Loop through the Transactions extracting relevant elements */
        myTask.startTask("Transactions");
        Iterator<Transaction> myIterator = myTrans.listIterator();
        while (myIterator.hasNext()) {
            Transaction myCurr = myIterator.next();
            TethysDate myCurrDay = myCurr.getDate();

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

        /* Complete the task */
        myTask.end();
    }

    /**
     * Constructor for a partially updated view accounts.
     * @param pTask the profiled task
     * @param pAnalysis the base analysis
     * @param pTransactions the edited transactions
     * @throws OceanusException on error
     */
    public TransactionAnalyser(final MetisProfile pTask,
                               final Analysis pAnalysis,
                               final TransactionList pTransactions) throws OceanusException {
        /* Start a new task */
        theProfile = pTask;
        MetisProfile myTask = theProfile.startTask("analyseTransactions");

        /* Store the parameters */
        theAnalysis = new Analysis(pAnalysis);
        MoneyWiseData myData = theAnalysis.getData();
        theHoldingMap = myData.getSecurityHoldingsMap();
        thePriceMap = myData.getSecurityPriceDataMap();

        /* Create new helper */
        theHelper = new TransactionHelper(myData);

        /* Create a new analysis */
        myTask.startTask("Initialise");

        /* Access details from the analysis */
        theDepositBuckets = theAnalysis.getDeposits();
        theCashBuckets = theAnalysis.getCash();
        theLoanBuckets = theAnalysis.getLoans();
        thePortfolioBuckets = theAnalysis.getPortfolios();
        thePayeeBuckets = theAnalysis.getPayees();
        theCategoryBuckets = theAnalysis.getTransCategories();
        theTagBuckets = theAnalysis.getTransactionTags();
        theTaxBasisBuckets = theAnalysis.getTaxBasis();
        theDilutions = theAnalysis.getDilutions();
        theSecurities = theAnalysis.getSecurities();
        theTaxMan = thePayeeBuckets.getBucket(PayeeTypeClass.TAXMAN);

        /* Loop through the Transactions extracting relevant elements */
        myTask.startTask("Transactions");
        Iterator<Transaction> myIterator = pTransactions.iterator();
        while (myIterator.hasNext()) {
            Transaction myCurr = myIterator.next();

            /* Ignore deleted transactions */
            if (myCurr.isDeleted()) {
                continue;
            }

            /* If the event has a dilution factor */
            if (myCurr.getDilution() != null) {
                /* Add to the dilution event list */
                theDilutions.addDilution(myCurr);
            }

            /* Process the transaction in the report set */
            processTransaction(myCurr);
        }

        /* Build category buckets */
        CashCategoryBucketList myCash = theAnalysis.getCashCategories();
        myCash.buildCategories(theCashBuckets);
        DepositCategoryBucketList myDeposits = theAnalysis.getDepositCategories();
        myDeposits.buildCategories(theDepositBuckets);
        LoanCategoryBucketList myLoans = theAnalysis.getLoanCategories();
        myLoans.buildCategories(theLoanBuckets);

        /* Complete the task */
        myTask.end();
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

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    public Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the dilutions.
     * @return the dilutions
     */
    public DilutionEventMap getDilutions() {
        return theDilutions;
    }

    /**
     * Mark active accounts.
     * @throws OceanusException on error
     */
    public void postProcessAnalysis() throws OceanusException {
        /* Start a new task */
        MetisProfile myTask = theProfile.startTask("postProcessAnalysis");
        myTask.startTask("markActiveAccounts");

        /* Mark relevant accounts */
        theDepositBuckets.markActiveAccounts();
        theCashBuckets.markActiveAccounts();
        theLoanBuckets.markActiveAccounts();

        /* Mark relevant securities */
        thePortfolioBuckets.markActiveSecurities();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Process a transaction.
     * @param pTrans the transaction to process
     * @throws OceanusException on error
     */
    private void processTransaction(final Transaction pTrans) throws OceanusException {
        /* Declare to helper */
        theHelper.setTransaction(pTrans);

        /* Access key details */
        TransactionAsset myDebitAsset = theHelper.getDebitAsset();
        TransactionAsset myCreditAsset = theHelper.getCreditAsset();

        /* Look for tags */
        Iterator<TransactionInfo> myIterator = pTrans.tagIterator();
        if (myIterator != null) {
            /* Process the transaction tags */
            theTagBuckets.processTransaction(pTrans, myIterator);
        }

        /* If the event relates to a security item, split out the workings */
        if (myDebitAsset instanceof SecurityHolding) {
            /* Process as a Security transaction */
            processDebitSecurityTransaction((SecurityHolding) myDebitAsset, myCreditAsset);

            /* If the event relates to a security item, split out the workings */
        } else if (myCreditAsset instanceof SecurityHolding) {
            /* Process as a Security transaction */
            processCreditSecurityTransaction(myDebitAsset, (SecurityHolding) myCreditAsset);

            /* Else handle the portfolio transfer */
        } else if ((myDebitAsset instanceof Portfolio)
                   && (myCreditAsset instanceof Portfolio)
                   && (pTrans.getCategoryClass() == TransactionCategoryClass.PORTFOLIOXFER)
                   && !myDebitAsset.equals(myCreditAsset)) {
            /* Process portfolio transfer */
            processPortfolioXfer((Portfolio) myDebitAsset, (Portfolio) myCreditAsset);

            /* Else handle the event normally */
        } else if ((myDebitAsset instanceof AssetBase)
                   && (myCreditAsset instanceof AssetBase)) {
            /* Access correctly */
            AssetBase<?> myDebit = (AssetBase<?>) myDebitAsset;
            AssetBase<?> myCredit = (AssetBase<?>) myCreditAsset;
            AssetBase<?> myChild = null;
            TethysMoney myAmount = pTrans.getAmount();
            TransactionCategory myCat = pTrans.getCategory();

            /* Switch on category class */
            switch (myCat.getCategoryTypeClass()) {
                case INTEREST:
                case LOYALTYBONUS:
                    /* Obtain detailed category */
                    myCat = myDebit.getDetailedCategory(myCat);

                    /* True debit account is the parent */
                    myChild = myDebit.equals(myCredit)
                                                       ? null
                                                       : myDebit;
                    myDebit = myDebit.getParent();
                    break;
                case LOANINTERESTEARNED:
                case CASHBACK:
                    /* True debit account is the parent of the asset */
                    myDebit = myDebit.getParent();
                    break;
                case RENTALINCOME:
                case ROOMRENTALINCOME:
                    /* True debit account is the parent of the security */
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
                myPayee.subtractExpense(theHelper, myAmount);

                /* Subtract expense from Category bucket */
                TransactionCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.subtractExpense(theHelper, myAmount);
                theTaxBasisBuckets.adjustAutoExpense(theHelper, false);

                /* else handle normally */
            } else {
                /* Determine the type of the debit account */
                switch (myDebit.getAssetType()) {
                    case PAYEE:
                        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                        myPayee.adjustForDebit(theHelper);
                        break;
                    default:
                        AccountBucket<?> myBucket = getAccountBucket(myDebit);
                        myBucket.adjustForDebit(theHelper);
                        break;
                }
            }

            /* If the credit account is auto-Expense */
            if ((myCredit instanceof Cash)
                && (myCredit.getAssetType() == AssetType.AUTOEXPENSE)) {
                /* Access credit as cash */
                Cash myCash = (Cash) myCredit;
                TransactionCategory myAuto = myCash.getAutoExpense();
                myCredit = myCash.getAutoPayee();
                myCredit.touchItem(pTrans);

                /* Add expense to Payee bucket */
                PayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                myPayee.addExpense(theHelper, myAmount);

                /* Adjust the relevant category bucket */
                TransactionCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.addExpense(theHelper, myAmount);
                theTaxBasisBuckets.adjustAutoExpense(theHelper, true);

                /* else handle normally */
            } else {
                /* Determine the type of the credit account */
                switch (myCredit.getAssetType()) {
                    case PAYEE:
                        PayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                        myPayee.adjustForCredit(theHelper);
                        break;
                    default:
                        AccountBucket<?> myBucket = getAccountBucket(myCredit);
                        myBucket.adjustForCredit(theHelper);
                        break;
                }
            }

            /* If we should register the event with a child */
            if (myChild != null) {
                /* Access bucket and register it */
                AccountBucket<?> myBucket = getAccountBucket(myChild);
                myBucket.registerTransaction(pTrans);
            }

            /* Adjust the tax payments */
            theTaxMan.adjustForTaxPayments(theHelper);

            /* If the event category is not a transfer */
            if (!myCat.isTransfer()) {
                /* Adjust the relevant category buckets */
                theCategoryBuckets.adjustCategories(theHelper, myCat);
            }

            /* Unknown combination */
        } else {
            throw new MoneyWiseLogicException("Invalid Asset Pair: "
                                              + pTrans.getAssetPair());
        }
    }

    /**
     * Process a debit security transaction.
     * @param pDebit the debit security
     * @param pCredit the credit account
     * @throws OceanusException on error
     */
    private void processDebitSecurityTransaction(final SecurityHolding pDebit,
                                                 final TransactionAsset pCredit) throws OceanusException {
        /* Add to the securities transaction list */
        theSecurities.add(theHelper.getTransaction());

        /* If credit account is also SecurityHolding */
        if (pCredit instanceof SecurityHolding) {
            /* Split out working */
            processDebitCreditSecurityTransaction(pDebit, (SecurityHolding) pCredit);
            return;
        }

        /* Switch on the category */
        TransactionCategory myCat = theHelper.getCategory();
        switch (myCat.getCategoryTypeClass()) {
            /* Process a stock right waived */
            case STOCKRIGHTSISSUE:
                processStockRightWaived(pDebit, (AssetBase<?>) pCredit);
                break;
            /* Process a dividend */
            case DIVIDEND:
                processDividend(pDebit, pCredit);
                break;
            case PORTFOLIOXFER:
                processPortfolioXfer(pDebit, (Portfolio) pCredit);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
                if (pDebit.getSecurity().isSecurityClass(SecurityTypeClass.LIFEBOND)) {
                    processChargeableGain(pDebit, (AssetBase<?>) pCredit);
                } else {
                    processTransferOut(pDebit, (AssetBase<?>) pCredit);
                }
                break;
            /* Throw an Exception */
            default:
                throw new MoneyWiseLogicException(ERROR_CATEGORY
                                                  + myCat.getCategoryTypeClass());
        }
    }

    /**
     * Process a debit+credit security transaction.
     * @param pDebit the debit security
     * @param pCredit the credit security
     * @throws OceanusException on error
     */
    private void processDebitCreditSecurityTransaction(final SecurityHolding pDebit,
                                                       final SecurityHolding pCredit) throws OceanusException {
        /* Switch on the category */
        TransactionCategory myCat = theHelper.getCategory();
        switch (myCat.getCategoryTypeClass()) {
            /* Process a stock split */
            case STOCKSPLIT:
            case UNITSADJUST:
                processUnitsAdjust(pDebit);
                break;
            /* Process a stock DeMerger */
            case STOCKDEMERGER:
                processStockDeMerger(pDebit, pCredit);
                break;
            /* Process a Stock TakeOver */
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
                processStockTakeover(pDebit, pCredit);
                break;
            /* Process a dividend */
            case DIVIDEND:
                processDividend(pDebit, pCredit);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
                processStockXchange(pDebit, (SecurityHolding) pCredit);
                break;
            /* Throw an Exception */
            default:
                throw new MoneyWiseLogicException(ERROR_CATEGORY
                                                  + myCat.getCategoryTypeClass());
        }
    }

    /**
     * Process a credit security transaction.
     * @param pDebit the debit account
     * @param pCredit the credit security holding
     * @throws OceanusException on error
     */
    private void processCreditSecurityTransaction(final TransactionAsset pDebit,
                                                  final SecurityHolding pCredit) throws OceanusException {
        /* Add to the securities transaction list */
        theSecurities.add(theHelper.getTransaction());

        /* Input asset must be AssetBase */
        if (!(pDebit instanceof AssetBase)) {
            throw new MoneyWiseLogicException("Invalid Debit Asset: "
                                              + pDebit.getAssetType());
        }
        AssetBase<?> myDebit = (AssetBase<?>) pDebit;

        /* Switch on the category */
        TransactionCategory myCat = theHelper.getCategory();
        switch (myCat.getCategoryTypeClass()) {
            /* Process standard transfer in/out */
            case STOCKRIGHTSISSUE:
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
                processTransferIn(myDebit, pCredit);
                break;
            /* Throw an Exception */
            default:
                throw new MoneyWiseLogicException(ERROR_CATEGORY
                                                  + myCat.getCategoryTypeClass());
        }
    }

    /**
     * Process a transaction that is a portfolio transfer.
     * <p>
     * This capital event relates only to both Debit and credit accounts.
     * @param pSource the source portfolio
     * @param pTarget the target portfolio
     */
    private void processPortfolioXfer(final Portfolio pSource,
                                      final Portfolio pTarget) {
        /* Add to the securities transaction list */
        theSecurities.add(theHelper.getTransaction());

        /* Access the portfolio buckets */
        PortfolioBucket mySource = thePortfolioBuckets.getBucket(pSource);
        PortfolioBucket myTarget = thePortfolioBuckets.getBucket(pTarget);

        /* Access source cash bucket */
        PortfolioCashBucket mySourceCash = mySource.getPortfolioCash();
        if (mySourceCash.isActive()) {
            /* Transfer any cash element */
            PortfolioCashBucket myTargetCash = myTarget.getPortfolioCash();
            myTargetCash.adjustForXfer(mySourceCash, theHelper);
        }

        /* Loop through the source portfolio */
        Iterator<SecurityBucket> myIterator = mySource.securityIterator();
        while (myIterator.hasNext()) {
            SecurityBucket myBucket = myIterator.next();

            /* If the bucket is active */
            if (myBucket.isActive()) {
                /* Adjust the Target Bucket */
                SecurityHolding myTargetHolding = theHoldingMap.declareHolding(pTarget, myBucket.getSecurity());
                SecurityBucket myTargetBucket = myTarget.getSecurityBucket(myTargetHolding);

                /* Process the Transfer */
                processPortfolioXfer(myBucket, myTargetBucket);
            }
        }

        /* PortfolioXfer is a transfer, so no need to update the categories */
    }

    /**
     * Process a portfolio transfer.
     * @param pSource the source holding
     * @param pTarget the target holding
     */
    private void processPortfolioXfer(final SecurityBucket pSource,
                                      final SecurityBucket pTarget) {
        /* Access source details */
        SecurityValues mySourceValues = pSource.getValues();
        TethysUnits myUnits = mySourceValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysMoney myCost = mySourceValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysMoney myInvested = mySourceValues.getMoneyValue(SecurityAttribute.INVESTED);
        TethysMoney myForeignInvested = mySourceValues.getMoneyValue(SecurityAttribute.FOREIGNINVESTED);
        boolean isForeign = pSource.isForeignCurrency();

        /* Determine value of the stock being transferred */
        TethysPrice myPrice = thePriceMap.getPriceForDate(pSource.getSecurity(), theHelper.getDate());
        TethysMoney myStockValue = myUnits.valueAtPrice(myPrice);
        TethysMoney myForeignValue = null;
        TethysRatio myRate = null;

        /* If we are foreign */
        if (isForeign) {
            /* Determine foreign and local value */
            myRate = theHelper.getDebitExchangeRate();
            myForeignValue = myStockValue;
            myStockValue = myStockValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate.getInverseRatio());
        }

        /* Allocate current profit between the two stocks */
        TethysMoney myProfit = new TethysMoney(myStockValue);
        myProfit.subtractAmount(myCost);
        pSource.adjustCounter(SecurityAttribute.GROWTHADJUST, myProfit);
        myProfit = new TethysMoney(myProfit);
        myProfit.negate();
        pTarget.adjustCounter(SecurityAttribute.GROWTHADJUST, myProfit);

        /* Transfer Units/Cost/Invested to target */
        pTarget.adjustCounter(SecurityAttribute.UNITS, myUnits);
        pTarget.adjustCounter(SecurityAttribute.RESIDUALCOST, myCost);
        pTarget.adjustCounter(SecurityAttribute.INVESTED, myInvested);
        SecurityValues myTargetValues = pTarget.registerTransaction(theHelper);
        myTargetValues.setValue(SecurityAttribute.PRICE, myPrice);
        myTargetValues.setValue(SecurityAttribute.VALUATION, myStockValue);
        if (isForeign) {
            myTargetValues.setValue(SecurityAttribute.FOREIGNVALUE, myForeignValue);
            myTargetValues.setValue(SecurityAttribute.EXCHANGERATE, myRate);
            pTarget.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myForeignInvested);
        }

        /* Adjust the Source Units/Cost/Invested to zero */
        myUnits = new TethysUnits(myUnits);
        myUnits.negate();
        pSource.adjustCounter(SecurityAttribute.UNITS, myUnits);
        myCost = new TethysMoney(myCost);
        myCost.negate();
        pSource.adjustCounter(SecurityAttribute.RESIDUALCOST, myCost);
        myInvested = new TethysMoney(myInvested);
        myInvested.negate();
        pSource.adjustCounter(SecurityAttribute.INVESTED, myInvested);
        mySourceValues = pSource.registerTransaction(theHelper);
        mySourceValues.setValue(SecurityAttribute.PRICE, myPrice);
        mySourceValues.setValue(SecurityAttribute.VALUATION, myStockValue);
        if (isForeign) {
            mySourceValues.setValue(SecurityAttribute.FOREIGNVALUE, myForeignValue);
            mySourceValues.setValue(SecurityAttribute.EXCHANGERATE, myRate);
            myForeignInvested = new TethysMoney(myForeignInvested);
            myForeignInvested.negate();
            pTarget.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myForeignInvested);
        }
    }

    /**
     * Process a transaction that is a portfolio transfer.
     * <p>
     * This capital event relates only to both Debit and credit accounts.
     * @param pSource the source holding
     * @param pTarget the target portfolio
     */
    private void processPortfolioXfer(final SecurityHolding pSource,
                                      final Portfolio pTarget) {
        /* Access the portfolio buckets */
        PortfolioBucket mySource = thePortfolioBuckets.getBucket(pSource.getPortfolio());
        PortfolioBucket myTarget = thePortfolioBuckets.getBucket(pTarget);

        /* Access source security bucket */
        SecurityBucket myBucket = mySource.getSecurityBucket(pSource);

        /* If the bucket is active */
        if (myBucket.isActive()) {
            /* Adjust the Target Bucket */
            SecurityHolding myTargetHolding = theHoldingMap.declareHolding(pTarget, myBucket.getSecurity());
            SecurityBucket myTargetBucket = myTarget.getSecurityBucket(myTargetHolding);

            /* Process the Transfer */
            processPortfolioXfer(myBucket, myTargetBucket);
        }

        /* PortfolioXfer is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is a stock split.
     * <p>
     * This capital event relates only to the Debit Account since the credit account is the same.
     * @param pHolding the security holding
     */
    private void processUnitsAdjust(final SecurityHolding pHolding) {
        /* Access the units */
        TethysUnits myDelta = theHelper.getCreditUnits();
        if (myDelta == null) {
            myDelta = new TethysUnits(theHelper.getDebitUnits());
            myDelta.negate();
        }

        /* Adjust the Security Units */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        myAsset.adjustCounter(SecurityAttribute.UNITS, myDelta);

        /* Register the transaction */
        myAsset.registerTransaction(theHelper);

        /* StockSplit/Adjust is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is a transfer into capital (also StockRightTaken).
     * <p>
     * This capital event relates only to the Credit Account.
     * @param pDebit the debit account
     * @param pCredit the credit security holding
     */
    private void processTransferIn(final AssetBase<?> pDebit,
                                   final SecurityHolding pCredit) {
        /* Access debit account and category */
        TransactionCategory myCat = theHelper.getCategory();

        /* Adjust the credit transfer details */
        processCreditXferIn(pCredit);

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(theHelper);

        /* Determine the type of the debit account */
        switch (pDebit.getAssetType()) {
            case PAYEE:
                PayeeBucket myPayee = thePayeeBuckets.getBucket(pDebit);
                myPayee.adjustForDebit(theHelper);
                break;
            default:
                AccountBucket<?> myAccount = getAccountBucket(pDebit);
                myAccount.adjustForDebit(theHelper);
                break;
        }

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant category buckets */
            theCategoryBuckets.adjustCategories(theHelper, myCat);
        }
    }

    /**
     * Process the credit side of a transfer in transaction.
     * @param pHolding the credit holding
     */
    private void processCreditXferIn(final SecurityHolding pHolding) {
        /* Transfer is to the credit account and may or may not have a change to the units */
        TethysUnits myDeltaUnits = theHelper.getCreditUnits();
        TethysMoney myAmount = theHelper.getCreditAmount();

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);

        /* If this is a foreign currency asset */
        if (myAsset.isForeignCurrency()) {
            /* Adjust foreign invested amount */
            SecurityValues myValues = myAsset.getValues();
            myValues.setValue(SecurityAttribute.EXCHANGERATE, theHelper.getCreditExchangeRate());
            myAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myAmount);

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* Adjust the cost and investment */
        myAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myAmount);
        myAsset.adjustCounter(SecurityAttribute.INVESTED, myAmount);

        /* If we have new units */
        if (myDeltaUnits != null) {
            /* Record change in units */
            myAsset.adjustCounter(SecurityAttribute.UNITS, myDeltaUnits);
        }

        /* Register the transaction */
        myAsset.registerTransaction(theHelper);
    }

    /**
     * Process a dividend transaction.
     * <p>
     * This capital event relates to the only to Debit account, although the Credit account may be
     * identical to the credit account in which case the dividend is re-invested
     * @param pHolding the debit security holding
     * @param pCredit the credit account
     */
    private void processDividend(final SecurityHolding pHolding,
                                 final TransactionAsset pCredit) {
        /* The main security that we are interested in is the debit account */
        Portfolio myPortfolio = pHolding.getPortfolio();
        Security mySecurity = pHolding.getSecurity();
        TethysMoney myAmount = theHelper.getDebitAmount();
        TethysMoney myTaxCredit = theHelper.getTaxCredit();
        TethysUnits myDeltaUnits = theHelper.getCreditUnits();

        /* Obtain detailed category */
        TransactionCategory myCat = myPortfolio.getDetailedCategory(theHelper.getCategory());
        myCat = mySecurity.getDetailedCategory(myCat);

        /* True debit account is the parent */
        AssetBase<?> myDebit = mySecurity.getParent();

        /* Adjust the debit payee bucket */
        PayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        myPayee.adjustForDebit(theHelper);

        /* Access the Asset Account Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        boolean isForeign = myAsset.isForeignCurrency();
        boolean isReInvest = pCredit instanceof SecurityHolding;

        /* If this is a foreign dividend */
        if (isForeign) {
            /* If this is a reInvestment */
            if (isReInvest) {
                /* Adjust counters */
                myAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myAmount);
                myAsset.getValues().setValue(SecurityAttribute.EXCHANGERATE, theHelper.getCreditExchangeRate());
            }

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* If this is a re-investment */
        if (isReInvest) {
            /* This amount is added to the cost, so record as the delta cost */
            myAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myAmount);

            /* Record the investment */
            myAsset.adjustCounter(SecurityAttribute.INVESTED, myAmount);

            /* If we have new units */
            if (myDeltaUnits != null) {
                /* Record delta units */
                myAsset.adjustCounter(SecurityAttribute.UNITS, myDeltaUnits);
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a received dividend from the account */
                myAsset.adjustCounter(SecurityAttribute.DIVIDEND, myTaxCredit);
            }

            /* else we are paying out to another account */
        } else {
            /* Adjust the dividend total for this asset */
            TethysMoney myAdjust = new TethysMoney(myAmount);

            /* Any tax credit is viewed as a realised dividend from the account */
            if (myTaxCredit != null) {
                myAdjust.addAmount(myTaxCredit);
            }

            /* The Dividend is viewed as a dividend from the account */
            myAsset.adjustCounter(SecurityAttribute.DIVIDEND, myAdjust);

            /* Adjust the credit account bucket */
            AccountBucket<?> myBucket = getAccountBucket((AssetBase<?>) pCredit);
            myBucket.adjustForCredit(theHelper);
        }

        /* Register the transaction */
        myAsset.registerTransaction(theHelper);

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(theHelper);

        /* Adjust the relevant category buckets */
        theCategoryBuckets.adjustCategories(theHelper, myCat);
    }

    /**
     * Process a transaction that is a transfer from capital.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pHolding the debit holding
     * @param pCredit the credit account
     */
    private void processTransferOut(final SecurityHolding pHolding,
                                    final AssetBase<?> pCredit) {
        /* Access credit account and category */
        TransactionCategory myCat = theHelper.getCategory();

        /* Adjust the debit transfer details */
        processDebitXferOut(pHolding);

        /* Adjust the credit account bucket */
        AccountBucket<?> myBucket = getAccountBucket(pCredit);
        myBucket.adjustForCredit(theHelper);

        /* If the event category is not a transfer */
        if (!myCat.isTransfer()) {
            /* Adjust the relevant category buckets */
            theCategoryBuckets.adjustCategories(theHelper, myCat);
        }
    }

    /**
     * Process the debit side of a transfer out transaction.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pHolding the debit holding
     */
    private void processDebitXferOut(final SecurityHolding pHolding) {
        /* Transfer out is from the debit account and may or may not have units */
        TethysMoney myAmount = theHelper.getDebitAmount();
        TethysUnits myDeltaUnits = theHelper.getDebitUnits();

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        SecurityValues myValues = myAsset.getValues();

        /* If this is a foreign currency asset */
        if (myAsset.isForeignCurrency()) {
            /* Adjust foreign invested amount */
            TethysMoney myDelta = new TethysMoney(myAmount);
            myDelta.negate();
            myValues.setValue(SecurityAttribute.EXCHANGERATE, theHelper.getDebitExchangeRate());
            myAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myDelta);

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* Record the delta investment */
        TethysMoney myDelta = new TethysMoney(myAmount);
        myDelta.negate();
        myAsset.adjustCounter(SecurityAttribute.INVESTED, myDelta);

        /* Assume the the allowed cost is the full value */
        TethysMoney myAllowedCost = new TethysMoney(myAmount);
        TethysMoney myCost = myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysRatio myCostDilution = TethysRatio.ONE;

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The allowed cost is the relevant fraction of the cost */
            TethysUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
            myAllowedCost = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustCounter(SecurityAttribute.UNITS, myDeltaUnits);

            /* Determine the cost dilution */
            myCostDilution = new TethysRatio(myValues.getRatioValue(SecurityAttribute.UNITS), myUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myAllowedCost.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myAllowedCost = new TethysMoney(myCost);
        }

        /* Determine the delta to the cost */
        TethysMoney myDeltaCost = new TethysMoney(myAllowedCost);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myDeltaCost);
        }

        /* Determine the capital gain */
        TethysMoney myCapitalGain = new TethysMoney(myAmount);
        myCapitalGain.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myCapitalGain.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustCounter(SecurityAttribute.REALISEDGAINS, myCapitalGain);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(theHelper, pHolding, myCapitalGain);
        }

        /* Register the transaction */
        myValues = myAsset.registerTransaction(theHelper);

        /* record details */
        myValues.setValue(SecurityAttribute.COSTDILUTION, myCostDilution);
        if (myAllowedCost.isNonZero()) {
            myValues.setValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost);
        }
        if (myCapitalGain.isNonZero()) {
            myValues.setValue(SecurityAttribute.CAPITALGAIN, myCapitalGain);
        }
    }

    /**
     * Process a transaction that is a exchange between two capital accounts.
     * <p>
     * This represent a transfer out from the debit account and a transfer in to the credit account
     * @param pDebit the debit holding
     * @param pCredit the credit holding
     */
    private void processStockXchange(final SecurityHolding pDebit,
                                     final SecurityHolding pCredit) {
        /* Adjust the debit transfer details */
        processDebitXferOut(pDebit);

        /* Adjust the credit transfer details */
        processCreditXferIn(pCredit);
    }

    /**
     * Process a transaction that is a chargeable gain.
     * <p>
     * This capital event relates only to the Debit Asset
     * @param pHolding the debit security holding
     * @param pCredit the credit account
     */
    private void processChargeableGain(final SecurityHolding pHolding,
                                       final AssetBase<?> pCredit) {
        /* Taxable Gain is from the debit account and may or may not have units */
        Security myDebit = pHolding.getSecurity();
        TethysMoney myAmount = theHelper.getDebitAmount();
        TethysUnits myDeltaUnits = theHelper.getDebitUnits();

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        SecurityValues myValues = myAsset.getValues();

        /* If this is a foreign currency asset */
        if (myAsset.isForeignCurrency()) {
            /* Adjust foreign invested amount */
            TethysMoney myDelta = new TethysMoney(myAmount);
            myDelta.negate();
            myValues.setValue(SecurityAttribute.EXCHANGERATE, theHelper.getDebitExchangeRate());
            myAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myDelta);

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* Record the delta investment */
        TethysMoney myDelta = new TethysMoney(myAmount);
        myDelta.negate();
        myAsset.adjustCounter(SecurityAttribute.INVESTED, myDelta);

        /* Assume the the cost reduction is the full value */
        TethysMoney myReduction = new TethysMoney(myAmount);
        TethysMoney myCost = myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            TethysUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustCounter(SecurityAttribute.UNITS, myDeltaUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new TethysMoney(myCost);
        }

        /* Determine the delta to the cost */
        TethysMoney myDeltaCost = new TethysMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myDeltaCost);
        }

        /* Determine the delta to the gains */
        TethysMoney myDeltaGains = new TethysMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustCounter(SecurityAttribute.REALISEDGAINS, myDeltaGains);
        }

        /* Register the event */
        myAsset.registerTransaction(theHelper);

        /* True debit account is the parent */
        AssetBase<?> myParent = myDebit.getParent();

        /* Adjust the debit account bucket */
        PayeeBucket myPayee = thePayeeBuckets.getBucket(myParent);
        myPayee.adjustForTaxCredit(theHelper);

        /* Adjust the credit account bucket */
        AccountBucket<?> myBucket = getAccountBucket(pCredit);
        myBucket.adjustForCredit(theHelper);

        /* Adjust the chargeableGains category bucket */
        theCategoryBuckets.adjustChargeableGain(theHelper, myReduction);

        /* Adjust the TaxMan account for the tax credit */
        theTaxMan.adjustForTaxPayments(theHelper);

        /* Add the chargeable event */
        theAnalysis.getCharges().addTransaction(theHelper.getTransaction(), myDeltaGains);
    }

    /**
     * Process a transaction that is stock right waived.
     * <p>
     * This capital event relates only to the Debit Account
     * @param pHolding the debit holding
     * @param pCredit the credit account
     */
    private void processStockRightWaived(final SecurityHolding pHolding,
                                         final AssetBase<?> pCredit) {
        /* Stock Right Waived is from the debit account */
        Security myDebit = pHolding.getSecurity();
        TethysMoney myAmount = theHelper.getDebitAmount();
        TethysMoney myAllowedCost;

        /* Access the Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        SecurityValues myValues = myAsset.getValues();
        boolean isForeign = myAsset.isForeignCurrency();

        /* If this is a foreign currency asset */
        if (isForeign) {
            /* Adjust foreign invested amount */
            TethysMoney myDelta = new TethysMoney(myAmount);
            myDelta.negate();
            myValues.setValue(SecurityAttribute.EXCHANGERATE, theHelper.getDebitExchangeRate());
            myAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myDelta);

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* Record the delta investment */
        TethysMoney myDelta = new TethysMoney(myAmount);
        myDelta.negate();
        myAsset.adjustCounter(SecurityAttribute.INVESTED, myDelta);

        /* Access the current cost */
        TethysMoney myCost = myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);

        /* Get the appropriate price for the account */
        TethysPrice myPrice = thePriceMap.getPriceForDate(myDebit, theHelper.getDate());

        /* Determine value of this stock at the current time */
        TethysUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysMoney myValue = myUnits.valueAtPrice(myPrice);
        TethysMoney myForeignValue = null;

        /* If we are foreign */
        if (isForeign) {
            /* Determine foreign and local value */
            TethysRatio myRate = theHelper.getDebitExchangeRate();
            myForeignValue = myValue;
            myValue = myValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate.getInverseRatio());
        }

        /* Determine condition as to whether this is a large cash transaction */
        TethysRatio myCostDilution = TethysRatio.ONE;
        TethysMoney myPortion = myValue.valueAtRate(LIMIT_RATE);
        boolean isLargeCash = (myAmount.compareTo(LIMIT_VALUE) > 0)
                              && (myAmount.compareTo(myPortion) > 0);

        /* If there is a capital gain */
        if (isLargeCash || myAmount.compareTo(myCost) > 0) {
            /* Determine the total value of rights plus share value */
            TethysMoney myTotalValue = new TethysMoney(myAmount);
            myTotalValue.addAmount(myValue);

            /* Determine the allowedCost as a proportion of the total value */
            myAllowedCost = myCost.valueAtWeight(myAmount, myTotalValue);

            /* Determine the cost dilution */
            myCostDilution = new TethysRatio(myValue, myTotalValue);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the reduction to be the entire amount */
            myAllowedCost = new TethysMoney(myAmount);
        }

        /* Calculate the delta cost */
        TethysMoney myDeltaCost = new TethysMoney(myAllowedCost);
        myDeltaCost.negate();

        /* Record the current/delta cost */
        myAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myDeltaCost);

        /* Determine the capital gain */
        TethysMoney myCapitalGain = new TethysMoney(myAmount);
        myCapitalGain.addAmount(myDeltaCost);

        /* If we have some gains */
        if (myCapitalGain.isNonZero()) {
            /* Record the delta gains */
            myAsset.adjustCounter(SecurityAttribute.REALISEDGAINS, myCapitalGain);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(theHelper, pHolding, myCapitalGain);
        }

        /* Register the event */
        myValues = myAsset.registerTransaction(theHelper);

        /* Record additional details to the registered event values */
        myValues.setValue(SecurityAttribute.PRICE, myPrice);
        myValues.setValue(SecurityAttribute.VALUATION, myValue);
        myValues.setValue(SecurityAttribute.COSTDILUTION, myCostDilution);
        if (myCapitalGain.isNonZero()) {
            myValues.setValue(SecurityAttribute.CAPITALGAIN, myCapitalGain);
        }
        if (myAllowedCost.isNonZero()) {
            myValues.setValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost);
        }
        if (isForeign) {
            myValues.setValue(SecurityAttribute.FOREIGNVALUE, myForeignValue);
            myValues.setValue(SecurityAttribute.EXCHANGERATE, theHelper.getDebitExchangeRate());
        }

        /* Adjust the credit account bucket */
        AccountBucket<?> myBucket = getAccountBucket(pCredit);
        myBucket.adjustForCredit(theHelper);

        /* StockRightWaived is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is Stock DeMerger.
     * <p>
     * This capital event relates to both the Credit and Debit accounts
     * @param pDebit the debit account
     * @param pCredit the credit account
     */
    private void processStockDeMerger(final SecurityHolding pDebit,
                                      final SecurityHolding pCredit) {
        TethysDilution myDilution = theHelper.getDilution();
        TethysUnits myDeltaUnits = theHelper.getDebitUnits();

        /* Access the Debit Asset Security Bucket */
        SecurityBucket myAsset = thePortfolioBuckets.getBucket(pDebit);
        SecurityValues myValues = myAsset.getValues();

        /* Obtain current cost and units */
        TethysMoney myCost = myValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysUnits myUnits = myValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysMoney myNewCost;

        /* If we reduced the units */
        if (myDeltaUnits != null) {
            /* Record the delta units */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();
            myAsset.adjustCounter(SecurityAttribute.UNITS, myDeltaUnits);

            /* Calculate the new cost */
            TethysUnits myNewUnits = new TethysUnits(myUnits);
            myNewUnits.addUnits(myDeltaUnits);
            myNewCost = myCost.valueAtWeight(myNewUnits, myUnits);

            /* else we diluted the holding */
        } else {
            /* Calculate the diluted cost */
            myNewCost = myCost.getDilutedMoney(myDilution);
        }

        /* Calculate the cost dilution */
        TethysRatio myCostDilution = new TethysRatio(myNewCost, myCost);

        /* Calculate the delta to the cost */
        TethysMoney myDeltaCost = new TethysMoney(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the delta cost/investment */
        myAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myDeltaCost);
        myAsset.adjustCounter(SecurityAttribute.INVESTED, myDeltaCost);
        if (myAsset.isForeignCurrency()) {
            TethysRatio myRate = theHelper.getDebitExchangeRate();
            TethysMoney myInvested = myDeltaCost.convertCurrency(myAsset.getCurrency().getCurrency(), myRate);
            myAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myInvested);
        }

        /* Register the event */
        myValues = myAsset.registerTransaction(theHelper);

        /* Access the Credit Asset Account Bucket */
        myAsset = thePortfolioBuckets.getBucket(pCredit);

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new TethysMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Record details */
        myValues.setValue(SecurityAttribute.XFERREDCOST, myDeltaCost);
        myValues.setValue(SecurityAttribute.COSTDILUTION, myCostDilution);

        /* Record the delta cost/investment */
        myAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myDeltaCost);
        myAsset.adjustCounter(SecurityAttribute.INVESTED, myDeltaCost);
        if (myAsset.isForeignCurrency()) {
            TethysRatio myRate = theHelper.getCreditExchangeRate();
            TethysMoney myInvested = myDeltaCost.convertCurrency(myAsset.getCurrency().getCurrency(), myRate);
            myAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myInvested);
        }

        /* Record the current/delta units */
        myDeltaUnits = theHelper.getCreditUnits();
        myAsset.adjustCounter(SecurityAttribute.UNITS, myDeltaUnits);

        /* Register the transaction */
        myAsset.registerTransaction(theHelper);

        /* StockDeMerger is a transfer, so no need to update the categories */
    }

    /**
     * Process a transaction that is StockTakeover.
     * <p>
     * This can be accomplished using a cash portion (to a ThirdParty account) and these workings
     * are split out.
     * @param pDebit the debit holding
     * @param pCredit the credit holding
     */
    private void processStockTakeover(final SecurityHolding pDebit,
                                      final SecurityHolding pCredit) {
        TethysMoney myAmount = theHelper.getThirdPartyAmount();
        Deposit myThirdParty = theHelper.getThirdParty();

        /* If we have a ThirdParty cash part of the transaction */
        if ((myThirdParty != null)
            && (myAmount.isNonZero())) {
            /* Process a Stock And Cash TakeOver */
            processStockAndCashTakeOver(pDebit, pCredit);
        } else {
            /* Process a StockOnly TakeOver */
            processStockOnlyTakeOver(pDebit, pCredit);
        }
    }

    /**
     * Process a transaction that is a StockOnlyTakeover.
     * <p>
     * This capital event relates to both the Credit and Debit accounts
     * @param pDebit the debit holding
     * @param pCredit the credit holding
     */
    private void processStockOnlyTakeOver(final SecurityHolding pDebit,
                                          final SecurityHolding pCredit) {
        /* Access details */
        Security myCredit = pCredit.getSecurity();
        Security myDebit = pDebit.getSecurity();

        /* Access the Asset Security Buckets */
        SecurityBucket myDebitAsset = thePortfolioBuckets.getBucket(pDebit);
        SecurityValues myDebitValues = myDebitAsset.getValues();
        SecurityBucket myCreditAsset = thePortfolioBuckets.getBucket(pCredit);
        TethysDate myDate = theHelper.getDate();

        /* Get the appropriate prices/rates for the stock */
        TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myCredit, myDate);
        TethysPrice myDebitPrice = thePriceMap.getPriceForDate(myDebit, myDate);
        TethysRatio myDebitRate = theHelper.getDebitExchangeRate();
        TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
        Currency myCurrency = theAnalysis.getCurrency().getCurrency();

        /* Determine value of the stock in both parts of the takeOver */
        TethysUnits myCreditUnits = theHelper.getCreditUnits();
        TethysMoney myCreditValue = myCreditUnits.valueAtPrice(myCreditPrice);
        TethysUnits myDebitUnits = myDebitValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysMoney myDebitValue = myDebitUnits.valueAtPrice(myDebitPrice);
        TethysMoney myInvested = myDebitValues.getMoneyValue(SecurityAttribute.INVESTED);

        /* Handle foreign debit */
        boolean isForeignDebit = myDebitAsset.isForeignCurrency();
        TethysMoney myForeignDebit = null;
        if (isForeignDebit) {
            myForeignDebit = myDebitValue;
            myDebitValue = myForeignDebit.convertCurrency(myCurrency, myDebitRate);
        }

        /* Handle foreign credit */
        boolean isForeignCredit = myCreditAsset.isForeignCurrency();
        TethysMoney myForeignCredit = null;
        if (isForeignCredit) {
            myForeignCredit = myCreditValue;
            myCreditValue = myForeignCredit.convertCurrency(myCurrency, myCreditRate);
        }

        /* Determine the residual cost of the old stock */
        TethysMoney myDebitCost = myDebitValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);

        /* Allocate current profit between the two stocks */
        TethysMoney myProfit = new TethysMoney(myDebitValue);
        myProfit.subtractAmount(myDebitCost);
        myDebitAsset.adjustCounter(SecurityAttribute.GROWTHADJUST, myProfit);
        myProfit = new TethysMoney(myProfit);
        myProfit.negate();
        myCreditAsset.adjustCounter(SecurityAttribute.GROWTHADJUST, myProfit);

        /* Adjust cost/units/invested of the credit account */
        myCreditAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myDebitCost);
        myCreditAsset.adjustCounter(SecurityAttribute.UNITS, myCreditUnits);
        myCreditAsset.adjustCounter(SecurityAttribute.INVESTED, myInvested);
        if (isForeignCredit) {
            TethysMoney myForeign = myInvested.convertCurrency(myCreditAsset.getCurrency().getCurrency(), myCreditRate);
            myCreditAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myForeign);
        }

        /* Register the transaction */
        SecurityValues myCreditValues = myCreditAsset.registerTransaction(theHelper);
        myCreditValues.setValue(SecurityAttribute.PRICE, myCreditPrice);
        myCreditValues.setValue(SecurityAttribute.VALUATION, myCreditValue);
        if (isForeignCredit) {
            myCreditValues.setValue(SecurityAttribute.EXCHANGERATE, myCreditRate);
            myCreditValues.setValue(SecurityAttribute.FOREIGNVALUE, myForeignCredit);
        }

        /* Drive debit cost down to zero */
        TethysMoney myDeltaCost = new TethysMoney(myDebitCost);
        myDeltaCost.negate();
        myDebitAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myDeltaCost);

        /* Drive debit units down to zero */
        myDebitUnits = new TethysUnits(myDebitUnits);
        myDebitUnits.negate();
        myDebitAsset.adjustCounter(SecurityAttribute.UNITS, myDebitUnits);

        /* Adjust debit Invested amount */
        myInvested = new TethysMoney(myInvested);
        myInvested.negate();
        myDebitAsset.adjustCounter(SecurityAttribute.INVESTED, myInvested);
        if (isForeignDebit) {
            myInvested = new TethysMoney(myDebitValues.getMoneyValue(SecurityAttribute.FOREIGNINVESTED));
            myInvested.negate();
            myDebitAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myInvested);
        }

        /* Register the transaction */
        myDebitValues = myDebitAsset.registerTransaction(theHelper);
        myDebitValues.setValue(SecurityAttribute.PRICE, myDebitPrice);
        myDebitValues.setValue(SecurityAttribute.VALUATION, myDebitValue);
        myDebitValues.setValue(SecurityAttribute.XFERREDCOST, myDeltaCost);
        if (isForeignDebit) {
            myDebitValues.setValue(SecurityAttribute.EXCHANGERATE, myDebitRate);
            myDebitValues.setValue(SecurityAttribute.FOREIGNVALUE, myForeignDebit);
        }
    }

    /**
     * Process a transaction that is StockAndCashTakeover.
     * <p>
     * This capital event relates to both the Credit and Debit accounts. In particular it makes
     * reference to the CashTakeOver aspect of the debit account
     * @param pDebit the debit holding
     * @param pCredit the credit holding
     */
    private void processStockAndCashTakeOver(final SecurityHolding pDebit,
                                             final SecurityHolding pCredit) {
        /* Access details */
        Security myDebit = pDebit.getSecurity();
        Security myCredit = pCredit.getSecurity();
        TethysDate myDate = theHelper.getDate();
        Deposit myThirdParty = theHelper.getThirdParty();
        TethysMoney myAmount = theHelper.getLocalThirdPartyAmount();

        /* Access the Asset Security Buckets */
        SecurityBucket myDebitAsset = thePortfolioBuckets.getBucket(pDebit);
        SecurityValues myDebitValues = myDebitAsset.getValues();
        SecurityBucket myCreditAsset = thePortfolioBuckets.getBucket(pCredit);

        /* Get the appropriate prices for the assets */
        TethysPrice myDebitPrice = thePriceMap.getPriceForDate(myDebit, myDate);
        TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myCredit, myDate);
        TethysRatio myDebitRate = theHelper.getDebitExchangeRate();
        TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
        Currency myCurrency = theAnalysis.getCurrency().getCurrency();

        /* Determine value of the base stock */
        TethysUnits myDebitUnits = myDebitValues.getUnitsValue(SecurityAttribute.UNITS);
        TethysMoney myDebitValue = myDebitUnits.valueAtPrice(myDebitPrice);

        /* Determine value of the stock part of the takeOver */
        TethysUnits myCreditUnits = theHelper.getCreditUnits();
        TethysMoney myCreditValue = myCreditUnits.valueAtPrice(myCreditPrice);

        /* Handle foreign debit */
        boolean isForeignDebit = myDebitAsset.isForeignCurrency();
        TethysMoney myForeignDebit = null;
        if (isForeignDebit) {
            myForeignDebit = myDebitValue;
            myDebitValue = myForeignDebit.convertCurrency(myCurrency, myDebitRate.getInverseRatio());
        }

        /* Handle foreign credit */
        boolean isForeignCredit = myCreditAsset.isForeignCurrency();
        TethysMoney myForeignCredit = null;
        if (isForeignCredit) {
            myForeignCredit = myCreditValue;
            myCreditValue = myForeignCredit.convertCurrency(myCurrency, myCreditRate.getInverseRatio());
        }

        /* Calculate the total consideration */
        TethysMoney myConsideration = new TethysMoney(myAmount);
        myConsideration.addAmount(myCreditValue);

        /* Access the current debit cost */
        TethysMoney myCost = myDebitValues.getMoneyValue(SecurityAttribute.RESIDUALCOST);
        TethysRatio myCostDilution = TethysRatio.ONE;
        TethysMoney myCostXfer;
        TethysMoney myAllowedCost;

        /* Determine condition as to whether this is a large cash transaction */
        TethysMoney myPortion = myDebitValue.valueAtRate(LIMIT_RATE);
        boolean isLargeCash = (myAmount.compareTo(LIMIT_VALUE) > 0)
                              && (myAmount.compareTo(myPortion) > 0);

        /* If there is a capital gain */
        if (isLargeCash || myAmount.compareTo(myCost) > 0) {
            /* Determine the transferable cost */
            myCostXfer = myCost.valueAtWeight(myCreditValue, myConsideration);

            /* Determine the cost dilution */
            myCostDilution = new TethysRatio(myAmount, myConsideration);

            /* Determine the allowed cost */
            myAllowedCost = new TethysMoney(myCost);
            myAllowedCost.subtractAmount(myCostXfer);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Allowed Cost is the amount */
            myAllowedCost = myAmount;

            /* Transferred cost is cost minus the cash amount */
            myCostXfer = new TethysMoney(myCost);
            myCostXfer.subtractAmount(myAllowedCost);
        }

        /* Determine the capital gain */
        TethysMoney myCapitalGain = new TethysMoney(myAmount);
        myCapitalGain.subtractAmount(myAllowedCost);
        if (myCapitalGain.isNonZero()) {
            /* Record the delta gains */
            myDebitAsset.adjustCounter(SecurityAttribute.REALISEDGAINS, myCapitalGain);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(theHelper, pDebit, myCapitalGain);
        }

        /* Allocate current profit between the two stocks */
        TethysMoney myProfit = new TethysMoney(myCreditValue);
        myProfit.subtractAmount(myCostXfer);
        myDebitAsset.adjustCounter(SecurityAttribute.GROWTHADJUST, myProfit);
        myProfit = new TethysMoney(myProfit);
        myProfit.negate();
        myCreditAsset.adjustCounter(SecurityAttribute.GROWTHADJUST, myProfit);

        /* Adjust cost/units/invested of the credit account */
        myCreditAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myCostXfer);
        myCreditAsset.adjustCounter(SecurityAttribute.UNITS, myCreditUnits);
        myCreditAsset.adjustCounter(SecurityAttribute.INVESTED, myCostXfer);
        if (isForeignCredit) {
            TethysMoney myForeign = myCostXfer.convertCurrency(myCreditAsset.getCurrency().getCurrency(), myCreditRate);
            myCreditAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myForeign);
        }

        /* Register the transaction */
        SecurityValues myCreditValues = myCreditAsset.registerTransaction(theHelper);
        myCreditValues.setValue(SecurityAttribute.PRICE, myCreditPrice);
        myCreditValues.setValue(SecurityAttribute.VALUATION, myCreditValue);
        if (isForeignCredit) {
            myCreditValues.setValue(SecurityAttribute.EXCHANGERATE, myCreditRate);
            myCreditValues.setValue(SecurityAttribute.FOREIGNVALUE, myForeignCredit);
        }

        /* Drive debit cost down to zero */
        TethysMoney myDeltaCost = new TethysMoney(myCost);
        myDeltaCost.negate();
        myDebitAsset.adjustCounter(SecurityAttribute.RESIDUALCOST, myDeltaCost);

        /* Drive debit units down to zero */
        myDebitUnits = new TethysUnits(myDebitUnits);
        myDebitUnits.negate();
        myDebitAsset.adjustCounter(SecurityAttribute.UNITS, myDebitUnits);

        /* Adjust debit Invested amount */
        TethysMoney myInvested = myDebitValues.getMoneyValue(SecurityAttribute.INVESTED);
        myInvested = new TethysMoney(myInvested);
        myInvested.setZero();
        myInvested.subtractAmount(myAmount);
        myInvested.subtractAmount(myCostXfer);
        myDebitAsset.adjustCounter(SecurityAttribute.INVESTED, myInvested);
        if (isForeignDebit) {
            myInvested = myInvested.convertCurrency(myDebitAsset.getCurrency().getCurrency(), myDebitRate);
            myInvested.negate();
            myDebitAsset.adjustCounter(SecurityAttribute.FOREIGNINVESTED, myInvested);
        }

        /* Register the transaction */
        myDebitValues = myDebitAsset.registerTransaction(theHelper);
        myDebitValues.setValue(SecurityAttribute.PRICE, myDebitPrice);
        myDebitValues.setValue(SecurityAttribute.VALUATION, myDebitValue);
        myDebitValues.setValue(SecurityAttribute.CONSIDERATION, myConsideration);
        myDebitValues.setValue(SecurityAttribute.CASHCONSIDERATION, myAmount);
        myDebitValues.setValue(SecurityAttribute.STOCKCONSIDERATION, myCreditValue);
        myDebitValues.setValue(SecurityAttribute.XFERREDCOST, myCostXfer);
        myDebitValues.setValue(SecurityAttribute.COSTDILUTION, myCostDilution);
        if (myCapitalGain.isNonZero()) {
            myDebitValues.setValue(SecurityAttribute.CAPITALGAIN, myCapitalGain);
        }
        if (myAllowedCost.isNonZero()) {
            myDebitValues.setValue(SecurityAttribute.ALLOWEDCOST, myAllowedCost);
        }
        if (isForeignDebit) {
            myDebitValues.setValue(SecurityAttribute.EXCHANGERATE, myDebitRate);
            myDebitValues.setValue(SecurityAttribute.FOREIGNVALUE, myForeignDebit);
        }

        /* Adjust the ThirdParty account bucket */
        AccountBucket<?> myBucket = getAccountBucket(myThirdParty);
        myBucket.adjustForThirdPartyCredit(theHelper);
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
            case PORTFOLIO:
                return thePortfolioBuckets.getCashBucket((Portfolio) pAsset);
            default:
                return null;
        }
    }
}
