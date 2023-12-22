/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.analyse;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisAccountBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket.MoneyWiseAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDataResource;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositBucket.MoneyWiseAnalysisDepositBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket.MoneyWiseAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDilutionEvent.MoneyWiseAnalysisDilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanBucket.MoneyWiseAnalysisLoanBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket.MoneyWiseAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioCashBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityValues;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket.MoneyWiseAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransTagBucket.MoneyWiseAnalysisTransTagBucketList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransactionHelper;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurityHolding.MoneyWiseSecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceDataMap;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseCashType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class to analyse transactions.
 * @author Tony Washer
 */
public class MoneyWiseAnalysisTransAnalyser
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final String ERROR_CATEGORY = "Unexpected Category Type: ";

    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisTransAnalyser> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisTransAnalyser.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisTransAnalyser::getAnalysis);
    }

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
    private final MoneyWiseSecurityHoldingMap theHoldingMap;

    /**
     * The security price map.
     */
    private final MoneyWiseSecurityPriceDataMap thePriceMap;

    /**
     * The analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * The transaction helper.
     */
    private final MoneyWiseAnalysisTransactionHelper theHelper;

    /**
     * The deposit bucket list.
     */
    private final MoneyWiseAnalysisDepositBucketList theDepositBuckets;

    /**
     * The cash bucket list.
     */
    private final MoneyWiseAnalysisCashBucketList theCashBuckets;

    /**
     * The deposit bucket list.
     */
    private final MoneyWiseAnalysisLoanBucketList theLoanBuckets;

    /**
     * The portfolio bucket list.
     */
    private final MoneyWiseAnalysisPortfolioBucketList thePortfolioBuckets;

    /**
     * The payee bucket list.
     */
    private final MoneyWiseAnalysisPayeeBucketList thePayeeBuckets;

    /**
     * The transaction category buckets.
     */
    private final MoneyWiseAnalysisTransCategoryBucketList theCategoryBuckets;

    /**
     * The transactionTag buckets.
     */
    private final MoneyWiseAnalysisTransTagBucketList theTagBuckets;

    /**
     * The taxBasis buckets.
     */
    private final MoneyWiseAnalysisTaxBasisBucketList theTaxBasisBuckets;

    /**
     * The security transactions.
     */
    private final List<MoneyWiseTransaction> theSecurities;

    /**
     * The taxMan account.
     */
    private final MoneyWiseAnalysisPayeeBucket theTaxMan;

    /**
     * The statePension account.
     */
    private final MoneyWiseAnalysisSecurityBucket theStatePension;

    /**
     * The dilutions.
     */
    private final MoneyWiseAnalysisDilutionEventMap theDilutions;

    /**
     * The profile.
     */
    private final TethysProfile theProfile;

    /**
     * Constructor for a complete set of accounts.
     * @param pTask the profiled task
     * @param pData the Data to analyse
     * @param pPreferenceMgr the preference manager
     * @throws OceanusException on error
     */
    public MoneyWiseAnalysisTransAnalyser(final TethysProfile pTask,
                                          final MoneyWiseDataSet pData,
                                          final MetisPreferenceManager pPreferenceMgr) throws OceanusException {
        /* Start a new task */
        theProfile = pTask;
        final TethysProfile myTask = theProfile.startTask("analyseTransactions");

        /* Store the parameters */
        theHoldingMap = pData.getPortfolios().getSecurityHoldingsMap();
        thePriceMap = pData.getSecurityPriceDataMap();

        /* Access the lists */
        final MoneyWiseTransactionList myTrans = pData.getTransactions();

        /* Create a new analysis */
        myTask.startTask("Initialise");
        theAnalysis = new MoneyWiseAnalysis(pData, pPreferenceMgr);
        theSecurities = theAnalysis.getSecurities();

        /* Create new helper and set opening balances */
        theHelper = new MoneyWiseAnalysisTransactionHelper(pData);
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
        theTaxMan = thePayeeBuckets.getBucket(MoneyWisePayeeClass.TAXMAN);

        /* Access the StatePension security holding */
        theStatePension = getStatePension(pData);

        /* Loop through the Transactions extracting relevant elements */
        myTask.startTask("Transactions");
        final Iterator<MoneyWiseTransaction> myIterator = myTrans.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransaction myCurr = myIterator.next();

            /* Ignore deleted transactions */
            if (myCurr.isDeleted()) {
                continue;
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
    public MoneyWiseAnalysisTransAnalyser(final TethysProfile pTask,
                                          final MoneyWiseAnalysis pAnalysis,
                                          final MoneyWiseTransactionList pTransactions) throws OceanusException {
        /* Start a new task */
        theProfile = pTask;
        final TethysProfile myTask = theProfile.startTask("analyseTransactions");

        /* Store the parameters */
        theAnalysis = new MoneyWiseAnalysis(pAnalysis);
        final MoneyWiseDataSet myData = theAnalysis.getData();
        theHoldingMap = myData.getPortfolios().getSecurityHoldingsMap();
        thePriceMap = myData.getSecurityPriceDataMap();

        /* Create new helper */
        theHelper = new MoneyWiseAnalysisTransactionHelper(myData);

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
        theTaxMan = thePayeeBuckets.getBucket(MoneyWisePayeeClass.TAXMAN);

        /* Access the StatePension security holding */
        theStatePension = getStatePension(myData);

        /* Loop through the Transactions extracting relevant elements */
        myTask.startTask("Transactions");
        final Iterator<MoneyWiseTransaction> myIterator = pTransactions.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransaction myCurr = myIterator.next();

            /* Ignore deleted/header transactions */
            if (myCurr.isDeleted() || myCurr.isHeader()) {
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
        final MoneyWiseAnalysisCashCategoryBucketList myCash = theAnalysis.getCashCategories();
        myCash.buildCategories(theCashBuckets);
        final MoneyWiseAnalysisDepositCategoryBucketList myDeposits = theAnalysis.getDepositCategories();
        myDeposits.buildCategories(theDepositBuckets);
        final MoneyWiseAnalysisLoanCategoryBucketList myLoans = theAnalysis.getLoanCategories();
        myLoans.buildCategories(theLoanBuckets);

        /* Complete the task */
        myTask.end();
    }

    /**
     * Obtain statePension bucket.
     * @param pData the dataSet
     * @return the statePension bucket
     */
    private MoneyWiseAnalysisSecurityBucket getStatePension(final MoneyWiseDataSet pData) {
        /* Access the singular portfolio and security */
        final MoneyWisePortfolio myPensionPort = pData.getPortfolios().getSingularClass(MoneyWisePortfolioClass.PENSION);
        final MoneyWiseSecurity myStatePension = pData.getSecurities().getSingularClass(MoneyWiseSecurityClass.STATEPENSION);

        /* If they exist, access the bucket */
        if (myPensionPort != null
                && myStatePension != null) {
            final MoneyWiseSecurityHolding myHolding = pData.getPortfolios().getSecurityHoldingsMap().declareHolding(myPensionPort, myStatePension);
            return thePortfolioBuckets.getBucket(myHolding);
        }

        /* Default to no bucket */
        return null;
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisTransAnalyser> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    /**
     * Obtain the analysis.
     * @return the analysis
     */
    public MoneyWiseAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the dilutions.
     * @return the dilutions
     */
    public MoneyWiseAnalysisDilutionEventMap getDilutions() {
        return theDilutions;
    }

    /**
     * Mark active accounts.
     * @throws OceanusException on error
     */
    public void postProcessAnalysis() throws OceanusException {
        /* Start a new task */
        final TethysProfile myTask = theProfile.startTask("postProcessAnalysis");
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
    private void processTransaction(final MoneyWiseTransaction pTrans) throws OceanusException {
        /* Declare to helper */
        theHelper.setTransaction(pTrans);

        /* Access key details */
        final MoneyWiseTransAsset myDebitAsset = theHelper.getDebitAsset();
        final MoneyWiseTransAsset myCreditAsset = theHelper.getCreditAsset();
        final MoneyWiseTaxCredit myYear = pTrans.getTaxYear();

        /* Look for tags */
        final List<MoneyWiseTransTag> myTags = pTrans.getTransactionTags();
        if (myTags != null) {
            /* Process the transaction tags */
            theTagBuckets.processTransaction(pTrans, myTags.iterator());
        }

        /* If the event relates to a security item, split out the workings */
        if (myDebitAsset instanceof MoneyWiseSecurityHolding) {
            /* Process as a Security transaction */
            processDebitSecurityTransaction((MoneyWiseSecurityHolding) myDebitAsset, myCreditAsset);

            /* If the event relates to a security item, split out the workings */
        } else if (myCreditAsset instanceof MoneyWiseSecurityHolding) {
            /* Process as a Security transaction */
            processCreditSecurityTransaction(myDebitAsset, (MoneyWiseSecurityHolding) myCreditAsset);

            /* Else handle the portfolio transfer */
        } else if (myDebitAsset instanceof MoneyWisePortfolio
                && myCreditAsset instanceof MoneyWisePortfolio
                && pTrans.getCategoryClass() == MoneyWiseTransCategoryClass.PORTFOLIOXFER
                && !myDebitAsset.equals(myCreditAsset)) {
            /* Process portfolio transfer */
            processPortfolioXfer((MoneyWisePortfolio) myDebitAsset, (MoneyWisePortfolio) myCreditAsset);

            /* Else handle the event normally */
        } else if (myDebitAsset instanceof MoneyWiseAssetBase
                && myCreditAsset instanceof MoneyWiseAssetBase) {
            /* Access correctly */
            MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) myDebitAsset;
            MoneyWiseAssetBase myCredit = (MoneyWiseAssetBase) myCreditAsset;
            MoneyWiseAssetBase myChild = null;
            final TethysMoney myAmount = pTrans.getAmount();
            MoneyWiseTransCategory myCat = pTrans.getCategory();

            /* Switch on category class */
            switch (myCat.getCategoryTypeClass()) {
                case INTEREST:
                case LOYALTYBONUS:
                    /* Obtain detailed category */
                    myCat = myDebit.getDetailedCategory(myCat, myYear);

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
            if (myDebit instanceof MoneyWiseCash
                    && myDebit.getAssetType() == MoneyWiseAssetType.AUTOEXPENSE) {
                /* Access debit as cash */
                final MoneyWiseCash myCash = (MoneyWiseCash) myDebit;
                final MoneyWiseTransCategory myAuto = myCash.getAutoExpense();
                myDebit = myCash.getAutoPayee();
                myDebit.touchItem(pTrans);

                /* Subtract expense from Payee bucket */
                final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                myPayee.subtractExpense(theHelper, myAmount);

                /* Subtract expense from Category bucket */
                final MoneyWiseAnalysisTransCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.subtractExpense(theHelper, myAmount);
                theTaxBasisBuckets.adjustAutoExpense(theHelper, false);

                /* handle Payees */
            } else if (MoneyWiseAssetType.PAYEE.equals(myDebit.getAssetType())) {
                final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
                myPayee.adjustForDebit(theHelper);

                /* handle valued assets */
            } else {
                final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket(myDebit);
                myBucket.adjustForDebit(theHelper);
            }

            /* If the credit account is auto-Expense */
            if (myCredit instanceof MoneyWiseCash
                    && myCredit.getAssetType() == MoneyWiseAssetType.AUTOEXPENSE) {
                /* Access credit as cash */
                final MoneyWiseCash myCash = (MoneyWiseCash) myCredit;
                final MoneyWiseTransCategory myAuto = myCash.getAutoExpense();
                myCredit = myCash.getAutoPayee();
                myCredit.touchItem(pTrans);

                /* Add expense to Payee bucket */
                final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                myPayee.addExpense(theHelper, myAmount);

                /* Adjust the relevant category bucket */
                final MoneyWiseAnalysisTransCategoryBucket myCatBucket = theCategoryBuckets.getBucket(myAuto);
                myCatBucket.addExpense(theHelper, myAmount);
                theTaxBasisBuckets.adjustAutoExpense(theHelper, true);

                /* handle Payees */
            } else if (MoneyWiseAssetType.PAYEE.equals(myCredit.getAssetType())) {
                final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myCredit);
                myPayee.adjustForCredit(theHelper);

                /* handle valued assets */
            } else {
                final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket(myCredit);
                myBucket.adjustForCredit(theHelper);
            }

            /* If we should register the event with a child */
            if (myChild != null) {
                /* Access bucket and register it */
                final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket(myChild);
                myBucket.registerTransaction(pTrans);
            }

            /* Adjust the tax and NI payments */
            theTaxMan.adjustForTaxPayments(theHelper);
            theStatePension.adjustForNIPayments(theHelper);

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
    private void processDebitSecurityTransaction(final MoneyWiseSecurityHolding pDebit,
                                                 final MoneyWiseTransAsset pCredit) throws OceanusException {
        /* Add to the securities transaction list */
        theSecurities.add(theHelper.getTransaction());

        /* If credit account is also SecurityHolding */
        if (pCredit instanceof MoneyWiseSecurityHolding) {
            /* Split out working */
            processDebitCreditSecurityTransaction(pDebit, (MoneyWiseSecurityHolding) pCredit);
            return;
        }

        /* Switch on the category */
        final MoneyWiseTransCategory myCat = theHelper.getCategory();
        switch (myCat.getCategoryTypeClass()) {
            /* Process a stock right waived */
            case STOCKRIGHTSISSUE:
                processTransferOut(pDebit, (MoneyWiseAssetBase) pCredit);
                break;
            /* Process a dividend */
            case DIVIDEND:
                processDividend(pDebit, pCredit);
                break;
            case PORTFOLIOXFER:
                processPortfolioXfer(pDebit, (MoneyWisePortfolio) pCredit);
                break;
            /* Process standard transfer in/out */
            case TRANSFER:
            case SECURITYCLOSURE:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
                if (pDebit.getSecurity().isSecurityClass(MoneyWiseSecurityClass.LIFEBOND)) {
                    processChargeableGain(pDebit, (MoneyWiseAssetBase) pCredit);
                } else {
                    processTransferOut(pDebit, (MoneyWiseAssetBase) pCredit);
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
    private void processDebitCreditSecurityTransaction(final MoneyWiseSecurityHolding pDebit,
                                                       final MoneyWiseSecurityHolding pCredit) throws OceanusException {
        /* Switch on the category */
        final MoneyWiseTransCategory myCat = theHelper.getCategory();
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
                processStockXchange(pDebit, pCredit);
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
    private void processCreditSecurityTransaction(final MoneyWiseTransAsset pDebit,
                                                  final MoneyWiseSecurityHolding pCredit) throws OceanusException {
        /* Add to the securities transaction list */
        theSecurities.add(theHelper.getTransaction());

        /* Input asset must be AssetBase */
        if (!(pDebit instanceof MoneyWiseAssetBase)) {
            throw new MoneyWiseLogicException("Invalid Debit Asset: "
                    + pDebit.getAssetType());
        }
        final MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) pDebit;

        /* Switch on the category */
        final MoneyWiseTransCategory myCat = theHelper.getCategory();
        switch (myCat.getCategoryTypeClass()) {
            /* Process standard transfer in/out */
            case STOCKRIGHTSISSUE:
            case TRANSFER:
            case EXPENSE:
            case INHERITED:
            case OTHERINCOME:
            case PENSIONCONTRIB:
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
    private void processPortfolioXfer(final MoneyWisePortfolio pSource,
                                      final MoneyWisePortfolio pTarget) {
        /* Add to the securities transaction list */
        theSecurities.add(theHelper.getTransaction());

        /* Access the portfolio buckets */
        final MoneyWiseAnalysisPortfolioBucket mySource = thePortfolioBuckets.getBucket(pSource);
        final MoneyWiseAnalysisPortfolioBucket myTarget = thePortfolioBuckets.getBucket(pTarget);

        /* Access source cash bucket */
        final MoneyWiseAnalysisPortfolioCashBucket mySourceCash = mySource.getPortfolioCash();
        if (mySourceCash.isActive()) {
            /* Transfer any cash element */
            final MoneyWiseAnalysisPortfolioCashBucket myTargetCash = myTarget.getPortfolioCash();
            myTargetCash.adjustForXfer(mySourceCash, theHelper);
        }

        /* Loop through the source portfolio */
        final Iterator<MoneyWiseAnalysisSecurityBucket> myIterator = mySource.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseAnalysisSecurityBucket myBucket = myIterator.next();

            /* If the bucket is active */
            if (myBucket.isActive()) {
                /* Adjust the Target Bucket */
                final MoneyWiseSecurityHolding myTargetHolding = theHoldingMap.declareHolding(pTarget, myBucket.getSecurity());
                final MoneyWiseAnalysisSecurityBucket myTargetBucket = myTarget.getSecurityBucket(myTargetHolding);
                theHelper.setSecurity(myBucket.getSecurity());

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
    private void processPortfolioXfer(final MoneyWiseAnalysisSecurityBucket pSource,
                                      final MoneyWiseAnalysisSecurityBucket pTarget) {
        /* Access source details */
        MoneyWiseAnalysisSecurityValues mySourceValues = pSource.getValues();
        TethysUnits myUnits = mySourceValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        TethysMoney myCost = mySourceValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        TethysMoney myGains = mySourceValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS);
        TethysMoney myInvested = mySourceValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED);
        TethysMoney myForeignInvested = mySourceValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED);
        final boolean isForeign = pSource.isForeignCurrency();

        /* Determine value of the stock being transferred */
        final TethysPrice myPrice = thePriceMap.getPriceForDate(pSource.getSecurity(), theHelper.getDate());
        TethysMoney myStockValue = myUnits.valueAtPrice(myPrice);
        TethysMoney myForeignValue = null;
        TethysRatio myRate = null;

        /* If we are foreign */
        if (isForeign) {
            /* Determine foreign and local value */
            myRate = theHelper.getDebitExchangeRate();
            myForeignValue = myStockValue;
            myStockValue = myStockValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate);
        }

        /* Allocate current profit between the two stocks */
        TethysMoney myProfit = new TethysMoney(myStockValue);
        myProfit.subtractAmount(myCost);
        pSource.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);
        myProfit = new TethysMoney(myProfit);
        myProfit.negate();
        pTarget.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);

        /* Transfer Units/Cost/Invested to target */
        pTarget.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myUnits);
        pTarget.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myCost);
        pTarget.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myInvested);
        pTarget.adjustCounter(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myGains);
        final MoneyWiseAnalysisSecurityValues myTargetValues = pTarget.registerTransaction(theHelper);
        myTargetValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
        myTargetValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myStockValue);
        myTargetValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCost);
        if (isForeign) {
            myTargetValues.setValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE, myForeignValue);
            myTargetValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myRate);
            pTarget.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myForeignInvested);
        }

        /* Adjust the Source Units/Cost/Invested to zero */
        myUnits = new TethysUnits(myUnits);
        myUnits.negate();
        pSource.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myUnits);
        myCost = new TethysMoney(myCost);
        myCost.negate();
        pSource.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myCost);
        myCost.negate();
        myInvested = new TethysMoney(myInvested);
        myInvested.negate();
        pSource.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myInvested);
        myGains = new TethysMoney(myGains);
        myGains.negate();
        pSource.adjustCounter(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myGains);
        mySourceValues = pSource.registerTransaction(theHelper);
        mySourceValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
        mySourceValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myStockValue);
        mySourceValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCost);
        if (isForeign) {
            mySourceValues.setValue(MoneyWiseAnalysisSecurityAttr.FOREIGNVALUE, myForeignValue);
            mySourceValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myRate);
            myForeignInvested = new TethysMoney(myForeignInvested);
            myForeignInvested.negate();
            pTarget.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myForeignInvested);
        }
    }

    /**
     * Process a transaction that is a portfolio transfer.
     * <p>
     * This capital event relates only to both Debit and credit accounts.
     * @param pSource the source holding
     * @param pTarget the target portfolio
     */
    private void processPortfolioXfer(final MoneyWiseSecurityHolding pSource,
                                      final MoneyWisePortfolio pTarget) {
        /* Access the portfolio buckets */
        final MoneyWiseAnalysisPortfolioBucket mySource = thePortfolioBuckets.getBucket(pSource.getPortfolio());
        final MoneyWiseAnalysisPortfolioBucket myTarget = thePortfolioBuckets.getBucket(pTarget);

        /* Access source security bucket */
        final MoneyWiseAnalysisSecurityBucket myBucket = mySource.getSecurityBucket(pSource);

        /* If the bucket is active */
        if (myBucket.isActive()) {
            /* Adjust the Target Bucket */
            final MoneyWiseSecurityHolding myTargetHolding = theHoldingMap.declareHolding(pTarget, myBucket.getSecurity());
            final MoneyWiseAnalysisSecurityBucket myTargetBucket = myTarget.getSecurityBucket(myTargetHolding);

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
    private void processUnitsAdjust(final MoneyWiseSecurityHolding pHolding) {
        /* Access the units */
        final TethysUnits myDelta = theHelper.getAccountDeltaUnits();

        /* Adjust the Security Units */
        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDelta);

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
    private void processTransferIn(final MoneyWiseAssetBase pDebit,
                                   final MoneyWiseSecurityHolding pCredit) {
        /* Access debit account and category */
        final MoneyWiseTransCategory myCat = theHelper.getCategory();

        /* Adjust the credit transfer details */
        processCreditXferIn(pCredit);

        /* Adjust the tax payments */
        theTaxMan.adjustForTaxPayments(theHelper);

        /* Determine the type of the debit account */
        switch (pDebit.getAssetType()) {
            case PAYEE:
                final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(pDebit);
                myPayee.adjustForDebit(theHelper);
                break;
            default:
                final MoneyWiseAnalysisAccountBucket<?> myAccount = getAccountBucket(pDebit);
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
    private void processCreditXferIn(final MoneyWiseSecurityHolding pHolding) {
        /* Transfer is to the credit account and may or may not have a change to the units */
        TethysMoney myAmount = theHelper.getCreditAmount();
        final TethysRatio myExchangeRate = theHelper.getCreditExchangeRate();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();

        /* Access the Asset Security Bucket */
        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        final boolean isForeign = myAsset.isForeignCurrency();

        /* If this is a foreign currency asset */
        if (isForeign) {
            /* Adjust foreign invested amount */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myAmount);

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* Adjust the cost and investment */
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myAmount);
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount);

        /* Determine the delta units */
        final MoneyWiseSecurityClass mySecClass = mySecurity.getCategoryClass();
        TethysUnits myDeltaUnits = theHelper.getCreditUnits();
        TethysUnits myUnits = myAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        if (mySecClass.isAutoUnits() && myUnits.isZero()) {
            myDeltaUnits = TethysUnits.getWholeUnits(mySecClass.getAutoUnits());
        }

        /* If we have new units */
        if (myDeltaUnits != null) {
            /* Record change in units */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
        }

        /* Adjust for National Insurance */
        myAsset.adjustForNIPayments(theHelper);

        /* Get the appropriate price for the account */
        final TethysPrice myPrice = thePriceMap.getPriceForDate(mySecurity, theHelper.getDate());

        /* Determine value of this stock after the transaction */
        myUnits = myAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        TethysMoney myValue = myUnits.valueAtPrice(myPrice);

        /* If we are foreign */
        if (isForeign) {
            /* Determine local value */
            myValue = myValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myExchangeRate);
        }

        /* Register the transaction */
        final MoneyWiseAnalysisSecurityValues myValues = myAsset.registerTransaction(theHelper);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myValue);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.CASHINVESTED, myAmount);
        if (isForeign) {
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myExchangeRate);
        }
    }

    /**
     * Process a dividend transaction.
     * <p>
     * This capital event relates to the only to Debit account, although the Credit account may be
     * identical to the credit account in which case the dividend is re-invested
     * @param pHolding the debit security holding
     * @param pCredit the credit account
     */
    private void processDividend(final MoneyWiseSecurityHolding pHolding,
                                 final MoneyWiseTransAsset pCredit) {
        /* The main security that we are interested in is the debit account */
        final MoneyWisePortfolio myPortfolio = pHolding.getPortfolio();
        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
        TethysMoney myAmount = theHelper.getDebitAmount();
        final TethysMoney myTaxCredit = theHelper.getTaxCredit();
        final TethysUnits myDeltaUnits = theHelper.getAccountDeltaUnits();
        final MoneyWiseTaxCredit myYear = theHelper.getTransaction().getTaxYear();

        /* Obtain detailed category */
        MoneyWiseTransCategory myCat = myPortfolio.getDetailedCategory(theHelper.getCategory(), myYear);
        myCat = mySecurity.getDetailedCategory(myCat, myYear);

        /* True debit account is the parent */
        final MoneyWiseAssetBase myDebit = mySecurity.getParent();

        /* Adjust the debit payee bucket */
        final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myDebit);
        myPayee.adjustForDebit(theHelper);

        /* Access the Asset Account Bucket */
        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        final boolean isForeign = myAsset.isForeignCurrency();
        final boolean isReInvest = pCredit instanceof MoneyWiseSecurityHolding;

        /* If this is a foreign dividend */
        if (isForeign) {
            /* If this is a reInvestment */
            if (isReInvest) {
                /* Adjust counters */
                myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myAmount);
                myAsset.getValues().setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, theHelper.getCreditExchangeRate());
            }

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* If this is a re-investment */
        if (isReInvest) {
            /* This amount is added to the cost, so record as the delta cost */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myAmount);

            /* Record the investment */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount);

            /* If we have new units */
            if (myDeltaUnits != null) {
                /* Record delta units */
                myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
            }

            /* If we have a tax credit */
            if (myTaxCredit != null) {
                /* The Tax Credit is viewed as a received dividend from the account */
                myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.DIVIDEND, myTaxCredit);
            }

            /* else we are paying out to another account */
        } else {
            /* Adjust the dividend total for this asset */
            final TethysMoney myAdjust = new TethysMoney(myAmount);

            /* Any tax credit is viewed as a realised dividend from the account */
            if (myTaxCredit != null) {
                myAdjust.addAmount(myTaxCredit);
            }

            /* The Dividend is viewed as a dividend from the account */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.DIVIDEND, myAdjust);

            /* Adjust the credit account bucket */
            final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket((MoneyWiseAssetBase) pCredit);
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
    private void processTransferOut(final MoneyWiseSecurityHolding pHolding,
                                    final MoneyWiseAssetBase pCredit) {
        /* Access credit account and category */
        final MoneyWiseTransCategory myCat = theHelper.getCategory();

        /* Adjust the debit transfer details */
        processDebitXferOut(pHolding);

        /* Adjust the credit account bucket */
        final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket(pCredit);
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
    private void processDebitXferOut(final MoneyWiseSecurityHolding pHolding) {
        /* Transfer out is from the debit account and may or may not have units */
        final MoneyWiseSecurity myDebit = pHolding.getSecurity();
        TethysMoney myAmount = theHelper.getDebitAmount();
        boolean isLargeCash = false;

        /* Access the Asset Security Bucket */
        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        MoneyWiseAnalysisSecurityValues myValues = myAsset.getValues();
        final TethysRatio myXchangeRate = theHelper.getDebitExchangeRate();
        final boolean isForeign = myAsset.isForeignCurrency();

        /* If this is a foreign currency asset */
        if (isForeign) {
            /* Adjust foreign invested amount */
            final TethysMoney myDelta = new TethysMoney(myAmount);
            myDelta.negate();
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myDelta);

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* Record the delta investment */
        final TethysMoney myDelta = new TethysMoney(myAmount);
        myDelta.negate();
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDelta);

        /* Get the appropriate price for the account */
        final TethysPrice myPrice = thePriceMap.getPriceForDate(myDebit, theHelper.getDate());

        /* Assume that the allowed cost is the full value */
        TethysUnits myUnits = myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        TethysMoney myAllowedCost = new TethysMoney(myAmount);
        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        TethysRatio myCostDilution = null;
        TethysMoney myConsideration = null;

        /* Determine the delta units */
        TethysUnits myDeltaUnits = theHelper.getCategoryClass().isSecurityClosure()
                ? myUnits
                : theHelper.getDebitUnits();
        final boolean isCapitalDistribution = myDeltaUnits == null;

        /* If this is not a capital distribution */
        if (!isCapitalDistribution) {
            /* The allowed cost is the relevant fraction of the cost */
            myAllowedCost = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
            final TethysUnits myNewUnits = myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);

            /* Determine the cost dilution */
            myCostDilution = new TethysRatio(myNewUnits, myUnits);
            myUnits = myNewUnits;
        }

        /* Determine value of this stock after the transaction */
        TethysMoney myValue = myUnits.valueAtPrice(myPrice);

        /* If we are foreign */
        if (isForeign) {
            /* Determine local value */
            myValue = myValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myXchangeRate);
        }

        /* If we are performing a capital distribution */
        if (isCapitalDistribution) {
            /* Determine condition as to whether this is a large cash transaction */
            final TethysMoney myPortion = myValue.valueAtRate(LIMIT_RATE);
            isLargeCash = (myAmount.compareTo(LIMIT_VALUE) > 0)
                    && (myAmount.compareTo(myPortion) > 0);

            /* If this is large cash */
            if (isLargeCash) {
                /* Determine the total value of rights plus share value */
                myConsideration = new TethysMoney(myAmount);
                myConsideration.addAmount(myValue);

                /* Determine the allowedCost as a proportion of the total value */
                myAllowedCost = myCost.valueAtWeight(myAmount, myConsideration);

                /* Determine the cost dilution */
                myCostDilution = new TethysRatio(myValue, myConsideration);

                /* else this is viewed as small and is taken out of the cost */
            } else {
                /* Set the allowed cost to be the least of the cost or the returned cash */
                myAllowedCost = myAmount.compareTo(myCost) > 0
                        ? new TethysMoney(myCost)
                        : new TethysMoney(myAmount);
            }
        }

        /* Determine the delta to the cost */
        final TethysMoney myDeltaCost = new TethysMoney(myAllowedCost);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
        }

        /* Determine the capital gain */
        final TethysMoney myCapitalGain = new TethysMoney(myAmount);
        myCapitalGain.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myCapitalGain.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myCapitalGain);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(theHelper, pHolding, myCapitalGain);
        }

        /* Register the transaction */
        myValues = myAsset.registerTransaction(theHelper);

        /* record details */
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myValue);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH, myAmount);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
        if (myCostDilution != null) {
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
        }
        if (myConsideration != null) {
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.CONSIDERATION, myConsideration);
        }
        if (myCapitalGain.isNonZero()) {
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN, myCapitalGain);
        }
        if (isForeign) {
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myXchangeRate);
        }
        if (isCapitalDistribution) {
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.CASHTYPE, isLargeCash
                    ? MoneyWiseCashType.LARGECASH
                    : MoneyWiseCashType.SMALLCASH);
        }
    }

    /**
     * Process a transaction that is a exchange between two capital accounts.
     * <p>
     * This represent a transfer out from the debit account and a transfer in to the credit account
     * @param pDebit the debit holding
     * @param pCredit the credit holding
     */
    private void processStockXchange(final MoneyWiseSecurityHolding pDebit,
                                     final MoneyWiseSecurityHolding pCredit) {
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
    private void processChargeableGain(final MoneyWiseSecurityHolding pHolding,
                                       final MoneyWiseAssetBase pCredit) {
        /* Chargeable Gain is from the debit account and may or may not have units */
        final MoneyWiseSecurity myDebit = pHolding.getSecurity();
        TethysMoney myAmount = theHelper.getDebitAmount();
        TethysUnits myDeltaUnits = theHelper.getDebitUnits();

        /* Access the Asset Security Bucket */
        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
        final MoneyWiseAnalysisSecurityValues myValues = myAsset.getValues();

        /* If this is a foreign currency asset */
        if (myAsset.isForeignCurrency()) {
            /* Adjust foreign invested amount */
            final TethysMoney myDelta = new TethysMoney(myAmount);
            myDelta.negate();
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, theHelper.getDebitExchangeRate());
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myDelta);

            /* Switch to local amount */
            myAmount = theHelper.getLocalAmount();
        }

        /* Record the delta investment */
        final TethysMoney myDelta = new TethysMoney(myAmount);
        myDelta.negate();
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDelta);

        /* Assume the cost reduction is the full value */
        TethysMoney myReduction = new TethysMoney(myAmount);
        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);

        /* If we are reducing units in the account */
        if (myDeltaUnits != null) {
            /* The reduction is the relevant fraction of the cost */
            final TethysUnits myUnits = myValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
            myReduction = myCost.valueAtWeight(myDeltaUnits, myUnits);

            /* Access units as negative value */
            myDeltaUnits = new TethysUnits(myDeltaUnits);
            myDeltaUnits.negate();

            /* Record delta to units */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
        }

        /* If the reduction is greater than the total cost */
        if (myReduction.compareTo(myCost) > 0) {
            /* Reduction is the total cost */
            myReduction = new TethysMoney(myCost);
        }

        /* Determine the delta to the cost */
        final TethysMoney myDeltaCost = new TethysMoney(myReduction);
        myDeltaCost.negate();

        /* If we have a delta to the cost */
        if (myDeltaCost.isNonZero()) {
            /* Adjust the cost */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
        }

        /* Determine the delta to the gains */
        final TethysMoney myDeltaGains = new TethysMoney(myAmount);
        myDeltaGains.addAmount(myDeltaCost);

        /* If we have a delta to the gains */
        if (myDeltaGains.isNonZero()) {
            /* Adjust the gains */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myDeltaGains);
        }

        /* Register the event */
        myAsset.registerTransaction(theHelper);

        /* True debit account is the parent */
        final MoneyWiseAssetBase myParent = myDebit.getParent();

        /* Adjust the debit account bucket */
        final MoneyWiseAnalysisPayeeBucket myPayee = thePayeeBuckets.getBucket(myParent);
        myPayee.adjustForTaxCredit(theHelper);

        /* Adjust the credit account bucket */
        final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket(pCredit);
        myBucket.adjustForCredit(theHelper);

        /* Adjust the chargeableGains category bucket */
        theCategoryBuckets.adjustChargeableGain(theHelper, myReduction);

        /* Adjust the TaxMan account for the tax credit */
        theTaxMan.adjustForTaxPayments(theHelper);

        /* Add the chargeable event */
        theTaxBasisBuckets.recordChargeableGain(theHelper.getTransaction(), myDeltaGains);
    }

    /**
     * Process a transaction that is Stock DeMerger.
     * <p>
     * This capital event relates to both the Credit and Debit accounts
     * @param pDebit the debit account
     * @param pCredit the credit account
     */
    private void processStockDeMerger(final MoneyWiseSecurityHolding pDebit,
                                      final MoneyWiseSecurityHolding pCredit) {
        /* Access the Debit Asset Security Bucket */
        MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pDebit);
        MoneyWiseAnalysisSecurityValues myValues = myAsset.getValues();
        final TethysRatio myDebitRate = theHelper.getDebitExchangeRate();

        /* Obtain current cost */
        final TethysMoney myCost = myValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        final TethysRatio myDilution = theHelper.getDilution();
        final TethysUnits myDeltaUnits = theHelper.getAccountDeltaUnits();

        /* If we reduced the units */
        if (myDeltaUnits != null) {
            /* Record the delta units */
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
        }

        /* Calculate the cost dilution */
        final TethysMoney myNewCost = myCost.getDilutedMoney(myDilution);
        final TethysRatio myCostDilution = new TethysRatio(myNewCost, myCost);

        /* Calculate the delta to the cost */
        TethysMoney myDeltaCost = new TethysMoney(myNewCost);
        myDeltaCost.subtractAmount(myCost);

        /* Record the delta cost/investment */
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDeltaCost);
        final boolean isForeignDebit = myAsset.isForeignCurrency();
        if (isForeignDebit) {
            final TethysMoney myInvested = myDeltaCost.convertCurrency(myAsset.getCurrency().getCurrency(), myDebitRate);
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
        }

        /* Register the event */
        myValues = myAsset.registerTransaction(theHelper);

        /* Access the Credit Asset Account Bucket */
        myAsset = thePortfolioBuckets.getBucket(pCredit);

        /* The deltaCost is transferred to the credit account */
        myDeltaCost = new TethysMoney(myDeltaCost);
        myDeltaCost.negate();

        /* Record details */
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
        if (isForeignDebit) {
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myDebitRate);
        }

        /* Record the delta cost/investment */
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myDeltaCost);
        final boolean isForeignCredit = myAsset.isForeignCurrency();
        final TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
        if (isForeignCredit) {
            final TethysMoney myInvested = myDeltaCost.convertCurrency(myAsset.getCurrency().getCurrency(), myCreditRate);
            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
        }

        /* Get the appropriate prices/rates for the stock */
        final TethysDate myDate = theHelper.getDate();
        final TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myAsset.getSecurity(), myDate);
        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();

        /* Determine value of the stock being deMerged */
        final TethysUnits myCreditUnits = theHelper.getPartnerDeltaUnits();
        TethysMoney myCreditXferValue = myCreditUnits.valueAtPrice(myCreditPrice);
        if (isForeignCredit) {
            myCreditXferValue = myCreditXferValue.convertCurrency(myCurrency, myCreditRate);
        }

        /* Record the current/delta units */
        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myCreditUnits);

        /* Register the transaction */
        myValues = myAsset.registerTransaction(theHelper);

        /* Record values */
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myCreditPrice);
        myValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
        if (isForeignCredit) {
            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myCreditRate);
        }

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
    private void processStockTakeover(final MoneyWiseSecurityHolding pDebit,
                                      final MoneyWiseSecurityHolding pCredit) {
        final TethysMoney myAmount = theHelper.getReturnedCash();
        final MoneyWiseTransAsset myReturnedCashAct = theHelper.getReturnedCashAccount();

        /* If we have a returned cash part of the transaction */
        if ((myReturnedCashAct != null)
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
    private void processStockOnlyTakeOver(final MoneyWiseSecurityHolding pDebit,
                                          final MoneyWiseSecurityHolding pCredit) {
        /* Access details */
        final MoneyWiseSecurity myCredit = pCredit.getSecurity();
        final MoneyWiseSecurity myDebit = pDebit.getSecurity();

        /* Access the Asset Security Buckets */
        final MoneyWiseAnalysisSecurityBucket myDebitAsset = thePortfolioBuckets.getBucket(pDebit);
        MoneyWiseAnalysisSecurityValues myDebitValues = myDebitAsset.getValues();
        final MoneyWiseAnalysisSecurityBucket myCreditAsset = thePortfolioBuckets.getBucket(pCredit);
        final TethysDate myDate = theHelper.getDate();

        /* Get the appropriate prices/rates for the stock */
        final TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myCredit, myDate);
        final TethysPrice myDebitPrice = thePriceMap.getPriceForDate(myDebit, myDate);
        final TethysRatio myDebitRate = theHelper.getDebitExchangeRate();
        final TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();

        /* Determine value of the stock in both parts of the takeOver */
        TethysUnits myCreditUnits = theHelper.getPartnerDeltaUnits();
        TethysMoney myCreditXferValue = myCreditUnits.valueAtPrice(myCreditPrice);
        TethysUnits myDebitUnits = myDebitValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        TethysMoney myDebitValue = myDebitUnits.valueAtPrice(myDebitPrice);
        TethysMoney myInvested = myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED);

        /* Handle foreign debit */
        final boolean isForeignDebit = myDebitAsset.isForeignCurrency();
        if (isForeignDebit) {
            myDebitValue = myDebitValue.convertCurrency(myCurrency, myDebitRate);
        }

        /* Handle foreign credit */
        final boolean isForeignCredit = myCreditAsset.isForeignCurrency();
        if (isForeignCredit) {
            myCreditXferValue = myCreditXferValue.convertCurrency(myCurrency, myCreditRate);
        }

        /* Determine the residual cost of the old stock */
        final TethysMoney myDebitCost = myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);

        /* Allocate current profit between the two stocks */
        TethysMoney myProfit = new TethysMoney(myDebitValue);
        myProfit.subtractAmount(myDebitCost);
        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);
        myProfit = new TethysMoney(myProfit);
        myProfit.negate();
        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);

        /* Adjust cost/units/invested of the credit account */
        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDebitCost);
        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myCreditUnits);
        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myInvested);
        if (isForeignCredit) {
            final TethysMoney myForeign = myInvested.convertCurrency(myCreditAsset.getCurrency().getCurrency(), myCreditRate);
            myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myForeign);
        }

        /* Determine final value of the credit stock after the takeOver */
        myCreditUnits = myCreditAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        TethysMoney myCreditValue = myCreditUnits.valueAtPrice(myCreditPrice);
        if (isForeignCredit) {
            myCreditValue = myCreditValue.convertCurrency(myCurrency, myCreditRate);
        }

        /* Register the transaction */
        final MoneyWiseAnalysisSecurityValues myCreditValues = myCreditAsset.registerTransaction(theHelper);
        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myCreditPrice);
        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDebitCost);
        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myCreditValue);
        if (isForeignCredit) {
            myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myCreditRate);
        }

        /* Drive debit cost down to zero */
        final TethysMoney myDeltaCost = new TethysMoney(myDebitCost);
        myDeltaCost.negate();
        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);
        myDeltaCost.negate();

        /* Drive debit units down to zero */
        myDebitUnits = new TethysUnits(myDebitUnits);
        myDebitUnits.negate();
        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDebitUnits);

        /* Adjust debit Invested amount */
        myInvested = new TethysMoney(myInvested);
        myInvested.negate();
        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myInvested);
        if (isForeignDebit) {
            myInvested = new TethysMoney(myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED));
            myInvested.negate();
            myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
        }

        /* Register the transaction */
        myDebitValues = myDebitAsset.registerTransaction(theHelper);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myDebitPrice);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myDebitValue);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myDeltaCost);
        if (isForeignDebit) {
            myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myDebitRate);
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
    private void processStockAndCashTakeOver(final MoneyWiseSecurityHolding pDebit,
                                             final MoneyWiseSecurityHolding pCredit) {
        /* Access details */
        final MoneyWiseSecurity myDebit = pDebit.getSecurity();
        final MoneyWiseSecurity myCredit = pCredit.getSecurity();
        final TethysDate myDate = theHelper.getDate();
        final MoneyWiseTransAsset myReturnedCashAccount = theHelper.getReturnedCashAccount();
        final TethysMoney myAmount = theHelper.getLocalReturnedCash();

        /* Access the Asset Security Buckets */
        final MoneyWiseAnalysisSecurityBucket myDebitAsset = thePortfolioBuckets.getBucket(pDebit);
        MoneyWiseAnalysisSecurityValues myDebitValues = myDebitAsset.getValues();
        final MoneyWiseAnalysisSecurityBucket myCreditAsset = thePortfolioBuckets.getBucket(pCredit);

        /* Get the appropriate prices for the assets */
        final TethysPrice myDebitPrice = thePriceMap.getPriceForDate(myDebit, myDate);
        final TethysPrice myCreditPrice = thePriceMap.getPriceForDate(myCredit, myDate);
        final TethysRatio myDebitRate = theHelper.getDebitExchangeRate();
        final TethysRatio myCreditRate = theHelper.getCreditExchangeRate();
        final Currency myCurrency = theAnalysis.getCurrency().getCurrency();

        /* Determine value of the base stock */
        TethysUnits myDebitUnits = myDebitValues.getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        TethysMoney myDebitValue = myDebitUnits.valueAtPrice(myDebitPrice);

        /* Determine value of the stock part of the takeOver */
        TethysUnits myCreditUnits = theHelper.getPartnerDeltaUnits();
        TethysMoney myCreditXferValue = myCreditUnits.valueAtPrice(myCreditPrice);

        /* Handle foreign debit */
        final boolean isForeignDebit = myDebitAsset.isForeignCurrency();
        if (isForeignDebit) {
            myDebitValue = myDebitValue.convertCurrency(myCurrency, myDebitRate);
        }

        /* Handle foreign credit */
        final boolean isForeignCredit = myCreditAsset.isForeignCurrency();
        if (isForeignCredit) {
            myCreditXferValue = myCreditXferValue.convertCurrency(myCurrency, myCreditRate);
        }

        /* Calculate the total consideration */
        final TethysMoney myConsideration = new TethysMoney(myAmount);
        myConsideration.addAmount(myCreditXferValue);

        /* Access the current debit cost */
        final TethysMoney myCost = myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST);
        TethysRatio myCostDilution = null;
        final TethysMoney myCostXfer;
        final TethysMoney myAllowedCost;

        /* Determine condition as to whether this is a large cash transaction */
        final TethysMoney myPortion = myDebitValue.valueAtRate(LIMIT_RATE);
        final boolean isLargeCash = (myAmount.compareTo(LIMIT_VALUE) > 0)
                && (myAmount.compareTo(myPortion) > 0);

        /* If this is a large cash takeOver */
        if (isLargeCash) {
            /* Determine the transferable cost */
            myCostXfer = myCost.valueAtWeight(myCreditXferValue, myConsideration);

            /* Determine the cost dilution */
            myCostDilution = new TethysRatio(myAmount, myConsideration);

            /* Determine the allowed cost */
            myAllowedCost = new TethysMoney(myCost);
            myAllowedCost.subtractAmount(myCostXfer);

            /* else this is viewed as small and is taken out of the cost */
        } else {
            /* Set the allowed cost to be the least of the cost or the returned cash */
            myAllowedCost = myAmount.compareTo(myCost) > 0
                    ? new TethysMoney(myCost)
                    : new TethysMoney(myAmount);

            /* Transferred cost is cost minus the allowed cost */
            myCostXfer = new TethysMoney(myCost);
            myCostXfer.subtractAmount(myAllowedCost);
        }

        /* Determine the capital gain */
        final TethysMoney myCapitalGain = new TethysMoney(myAmount);
        myCapitalGain.subtractAmount(myAllowedCost);
        if (myCapitalGain.isNonZero()) {
            /* Record the delta gains */
            myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.REALISEDGAINS, myCapitalGain);

            /* Adjust the capitalGains category bucket */
            theCategoryBuckets.adjustStandardGain(theHelper, pDebit, myCapitalGain);
        }

        /* Allocate current profit between the two stocks */
        TethysMoney myProfit = new TethysMoney(myCreditXferValue);
        myProfit.subtractAmount(myCostXfer);
        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);
        myProfit = new TethysMoney(myProfit);
        myProfit.negate();
        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.GROWTHADJUST, myProfit);

        /* Adjust cost/units/invested of the credit account */
        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myCostXfer);
        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myCreditUnits);
        myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myCostXfer);
        if (isForeignCredit) {
            final TethysMoney myForeign = myCostXfer.convertCurrency(myCreditAsset.getCurrency().getCurrency(), myCreditRate);
            myCreditAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myForeign);
        }

        /* Determine final value of the credit stock after the takeOver */
        myCreditUnits = myCreditAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
        TethysMoney myCreditValue = myCreditUnits.valueAtPrice(myCreditPrice);
        if (isForeignCredit) {
            myCreditValue = myCreditValue.convertCurrency(myCurrency, myCreditRate);
        }

        /* Register the transaction */
        final MoneyWiseAnalysisSecurityValues myCreditValues = myCreditAsset.registerTransaction(theHelper);
        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myCreditPrice);
        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCostXfer);
        myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myCreditValue);
        if (isForeignCredit) {
            myCreditValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myCreditRate);
        }

        /* Drive debit cost down to zero */
        final TethysMoney myDeltaCost = new TethysMoney(myCost);
        myDeltaCost.negate();
        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myDeltaCost);

        /* Drive debit units down to zero */
        myDebitUnits = new TethysUnits(myDebitUnits);
        myDebitUnits.negate();
        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDebitUnits);

        /* Adjust debit Invested amount */
        TethysMoney myInvested = myDebitValues.getMoneyValue(MoneyWiseAnalysisSecurityAttr.INVESTED);
        myInvested = new TethysMoney(myInvested);
        myInvested.setZero();
        myInvested.subtractAmount(myAmount);
        myInvested.subtractAmount(myCostXfer);
        myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myInvested);
        if (isForeignDebit) {
            myInvested = myInvested.convertCurrency(myDebitAsset.getCurrency().getCurrency(), myDebitRate);
            myInvested.negate();
            myDebitAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myInvested);
        }

        /* Register the transaction */
        myDebitValues = myDebitAsset.registerTransaction(theHelper);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myDebitPrice);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myDebitValue);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.CONSIDERATION, myConsideration);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.RETURNEDCASH, myAmount);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDVALUE, myCreditXferValue);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.XFERREDCOST, myCostXfer);
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.ALLOWEDCOST, myAllowedCost);
        if (myCostDilution != null) {
            myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.COSTDILUTION, myCostDilution);
        }
        if (myCapitalGain.isNonZero()) {
            myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.CAPITALGAIN, myCapitalGain);
        }
        if (isForeignDebit) {
            myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myDebitRate);
        }
        myDebitValues.setValue(MoneyWiseAnalysisSecurityAttr.CASHTYPE, isLargeCash
                ? MoneyWiseCashType.LARGECASH
                : MoneyWiseCashType.SMALLCASH);

        /* Adjust the ThirdParty account bucket */
        final MoneyWiseAnalysisAccountBucket<?> myBucket = getAccountBucket((MoneyWiseAssetBase) myReturnedCashAccount);
        myBucket.adjustForReturnedCashCredit(theHelper);
    }

    /**
     * Obtain Account bucket for asset.
     * @param pAsset the asset
     * @return the bucket
     */
    private MoneyWiseAnalysisAccountBucket<?> getAccountBucket(final MoneyWiseAssetBase pAsset) {
        switch (pAsset.getAssetType()) {
            case DEPOSIT:
                return theDepositBuckets.getBucket((MoneyWiseDeposit) pAsset);
            case CASH:
                return theCashBuckets.getBucket((MoneyWiseCash) pAsset);
            case LOAN:
                return theLoanBuckets.getBucket((MoneyWiseLoan) pAsset);
            case PORTFOLIO:
                return thePortfolioBuckets.getCashBucket((MoneyWisePortfolio) pAsset);
            default:
                throw new IllegalArgumentException();
        }
    }
}
