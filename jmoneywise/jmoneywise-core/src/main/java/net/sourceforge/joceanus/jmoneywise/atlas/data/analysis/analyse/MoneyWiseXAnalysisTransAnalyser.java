/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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

import java.util.List;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisAccountBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Transaction analyser.
 */
public class MoneyWiseXAnalysisTransAnalyser {
    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The analysis state.
     */
    private final MoneyWiseXAnalysisState theState;

    /**
     * The market analysis.
     */
    private final MoneyWiseXAnalysisMarket theMarket;

    /**
     * The tax analysis.
     */
    private final MoneyWiseXAnalysisTax theTax;

    /**
     * The security analysis.
     */
    private final MoneyWiseXAnalysisSecurity theSecurity;

    /**
     * The portfolioXfer analyser.
     */
    private final MoneyWiseXAnalysisPortfolioXfer thePortfolioXfer;

    /**
     * The current transaction.
     */
    private MoneyWiseXAnalysisTransaction theTrans;

    /**
     * Constructor.
     * @param pAnalyser the analyser
     */
    MoneyWiseXAnalysisTransAnalyser(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        /* Store details */
        theAnalysis = pAnalyser.getAnalysis();
        theState = pAnalyser.getState();
        theMarket = pAnalyser.getMarket();
        theTax = pAnalyser.getTax();
        thePortfolioXfer = new MoneyWiseXAnalysisPortfolioXfer(pAnalyser);

        /* Create the security analyser */
        theSecurity = new MoneyWiseXAnalysisSecurity(pAnalyser);
    }

    /**
     * Process transaction event.
     * @param pEvent the event
     * @throws OceanusException on error
     */
    public void processTransaction(final MoneyWiseXAnalysisEvent pEvent) throws OceanusException {
        /* Parse the event */
        theTrans = new MoneyWiseXAnalysisTransaction(pEvent);
        final MoneyWiseTransaction myTrans = theTrans.getTransaction();

        /* Ignore header transactions */
        if (!myTrans.isHeader()) {
            /* Touch underlying items */
            myTrans.touchUnderlyingItems();

            /* Process tags */
            final List<MoneyWiseTransTag> myTags = myTrans.getTransactionTags();
            if (myTags != null) {
                /* Process the transaction tags */
                theAnalysis.getTransactionTags().processEvent(pEvent, myTags.iterator());
            }

            /* Process the transaction */
            processTransaction();
        }
    }

    /**
     * Process transaction.
     * @throws OceanusException on error
     */
    private void processTransaction() throws OceanusException {
        /* Access account and partner */
        final MoneyWiseTransaction myTrans = theTrans.getTransaction();
        final MoneyWiseTransAsset myDebit = theTrans.getDebitAccount();
        final MoneyWiseTransAsset myCredit = theTrans.getCreditAccount();

        /* If the event relates to a security holding account, split out the workings */
        if (isSecurityHolding(myDebit)) {
            /* Process as a Security transaction */
            theSecurity.processDebitSecurity(theTrans);

            /* If the event relates to a security holding partner, split out the workings */
        } else if (isSecurityHolding(myCredit)) {
            /* Process as a Security transaction */
            theSecurity.processCreditSecurity(theTrans);

            /* If the event is portfolioXfer, split out the workings */
        } else if (isPortfolioXfer(theTrans.getCategoryClass())) {
            /* Process the portfolioXfer */
            thePortfolioXfer.processPortfolioXfer(theTrans);

            /* Else handle the event normally */
        } else if (myDebit instanceof MoneyWiseAssetBase
                && myCredit instanceof MoneyWiseAssetBase) {
            processNonSecurity();

            /* else reject */
        } else {
            throw new MoneyWiseLogicException("Invalid Asset Pair: "
                    + myTrans.getAccount().getAssetType()
                    + " "
                    + myTrans.getDirection()
                    + " "
                    + myTrans.getPartner().getAssetType());
        }

        /* Process taxBasis */
        theTax.adjustTaxBasis(theTrans);

        /* Register the eventBuckets */
        final MoneyWiseXAnalysisEvent myEvent = theTrans.getEvent();
        theMarket.adjustMarketTotals(myEvent);
        theState.registerInterestedBucketsForEvent(myEvent);
    }

    /**
     * process non-security transaction.
     */
    private void processNonSecurity() {
        /* Adjust parent details */
        theTrans.adjustParent();

        /* Process Asset accounts */
        processAssets();

        /* Process Auto-expense accounts */
        processAutoExpense();

        /* Process Payee accounts */
        processPayees();
    }

    /**
     * process assets.
     */
    private void processAssets() {
        /* Access debit and credit accounts and amounts */
        MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) theTrans.getDebitAccount();
        MoneyWiseAssetBase myCredit = (MoneyWiseAssetBase) theTrans.getCreditAccount();
        TethysMoney myDebitAmount = theTrans.getDebitAmount();
        TethysMoney myCreditAmount = theTrans.getCreditAmount();

        /* If the debit account is an asset */
        if (isAsset(myDebit)) {
            /* Process the debit asset bucket */
            myDebitAmount = processDebitAsset(myDebit);
        }

        /* If the credit account is an asset */
        if (isAsset(myCredit)) {
            /* Process the credit asset bucket */
            myCreditAmount = processCreditAsset(myCredit);
        }

        /* Adjust for currencyFluctuation */
        final TethysMoney myFluctuation = new TethysMoney(myDebitAmount);
        myFluctuation.addAmount(myCreditAmount);
        if (myFluctuation.isNonZero()) {
            theMarket.adjustTotalsForCurrencyFluctuation(theTrans.getEvent(), myFluctuation);
        }
    }

    /**
     * process debit Asset.
     * @param pDebit the debit asset
     * @return the debitAmount in reporting currency
     */
    TethysMoney processDebitAsset(final MoneyWiseAssetBase pDebit) {
        /* Adjust the debit asset bucket */
        final MoneyWiseXAnalysisAccountBucket<?> myBucket = getAccountBucket(pDebit);
        TethysMoney myDebitAmount = theTrans.getDebitAmount();
        myBucket.addToBalance(myDebitAmount);
        myBucket.adjustValuation();
        theState.registerBucketInterest(myBucket);

        /* If the asset is foreign, convert debit amount to reporting currency */
        if (Boolean.TRUE.equals(pDebit.isForeign())) {
            /* convert debit amount to reporting currency */
            myDebitAmount = myBucket.getDeltaValuation();
            theTrans.setDebitAmount(myDebitAmount);
        }

        /* Return the debit amount */
        return myDebitAmount;
    }


    /**
     * process credit Asset.
     * @param pCredit the credit asset
     * @return the creditAmount in reporting currency
     */
    TethysMoney processCreditAsset(final MoneyWiseAssetBase pCredit) {
        /* Adjust the credit asset bucket */
        final MoneyWiseXAnalysisAccountBucket<?> myBucket = getAccountBucket(pCredit);
        TethysMoney myCreditAmount = theTrans.getCreditAmount();
        myBucket.addToBalance(myCreditAmount);
        myBucket.adjustValuation();
        theState.registerBucketInterest(myBucket);

        /* If the asset is foreign */
        if (Boolean.TRUE.equals(pCredit.isForeign())) {
            /* convert credit amount to reporting currency */
            myCreditAmount = myBucket.getDeltaValuation();
            theTrans.setCreditAmount(myCreditAmount);
        }

        /* Return the credit amount */
        return myCreditAmount;
    }

    /**
     * process autoExpense assets.
     */
    private void processAutoExpense() {
        /* Access debit and credit accounts and amounts */
        MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) theTrans.getDebitAccount();
        MoneyWiseAssetBase myCredit = (MoneyWiseAssetBase) theTrans.getCreditAccount();

        /* If the debit account is auto-Expense */
        if (isAutoExpense(myDebit)) {
            /* Access debit as cashPayee */
            processDebitAutoExpense((MoneyWiseCash) myDebit);
        }

        /* If the credit account is auto-Expense */
        if (isAutoExpense(myCredit)) {
            /* Access credit as cashPayee */
            processCreditAutoExpense((MoneyWiseCash) myCredit);
        }
    }

    /**
     * process debit autoExpense asset.
     * @param pDebit the debit asset
     */
    private void processDebitAutoExpense(final MoneyWiseCash pDebit) {
        /* Access debit Payee/Category auto-expense */
        final MoneyWiseTransCategory myAuto = pDebit.getAutoExpense();
        final MoneyWisePayee myDebit = pDebit.getAutoPayee();

        /* Adjust expense for autoPayee bucket */
        final TethysMoney myAmount = theTrans.getDebitAmount();
        final MoneyWiseXAnalysisPayeeBucket myPayee = theAnalysis.getPayees().getBucket(myDebit);
        myPayee.addExpense(myAmount);
        theState.registerBucketInterest(myPayee);

        /* Adjust expense for Category bucket */
        final MoneyWiseXAnalysisTransCategoryBucket myCategory = theAnalysis.getTransCategories().getBucket(myAuto);
        myCategory.addExpense(myAmount);
        theState.registerBucketInterest(myCategory);

        /* Adjust expense taxBasis */
        theTax.processAutoExpense(myAmount);
    }


    /**
     * process credit autoExpense asset.
     * @param pCredit the credit asset
     */
    private void processCreditAutoExpense(final MoneyWiseCash pCredit) {
        /* Access credit Payee/Category auto-expense */
        final MoneyWiseTransCategory myAuto = pCredit.getAutoExpense();
        final MoneyWisePayee myCredit = pCredit.getAutoPayee();

        /* Adjust expense for autoPayee bucket */
        final TethysMoney myAmount = theTrans.getCreditAmount();
        final MoneyWiseXAnalysisPayeeBucket myPayee = theAnalysis.getPayees().getBucket(myCredit);
        myPayee.addExpense(myAmount);
        theState.registerBucketInterest(myPayee);

        /* Adjust expense for Category bucket */
        final MoneyWiseXAnalysisTransCategoryBucket myCategory = theAnalysis.getTransCategories().getBucket(myAuto);
        myCategory.addExpense(myAmount);
        theState.registerBucketInterest(myCategory);

        /* Adjust expense taxBasis */
        theTax.processAutoExpense(myAmount);
    }

    /**
     * process payee assets.
     */
    private void processPayees() {
        /* Access debit and credit accounts and amounts */
        final MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) theTrans.getDebitAccount();
        final MoneyWiseAssetBase myCredit = (MoneyWiseAssetBase) theTrans.getCreditAccount();

        /* If the debit account is payee */
        if (isPayee(myDebit)) {
            /* process debit as Payee */
            processDebitPayee((MoneyWisePayee) myDebit);
        }

        /* If the credit account is payee */
        if (isPayee(myCredit)) {
            /* process credit as Payee */
            processCreditPayee((MoneyWisePayee) myCredit);
        }
    }

    /**
     * process debit payee asset.
     * @param pDebit the debit asset
     */
    void processDebitPayee(final MoneyWisePayee pDebit) {
        /* Access category */
        final MoneyWiseTransCategory myCategory = theTrans.getCategory();
        final boolean isExpense = theTrans.isExpenseCategory();

        /* Adjust expense for Payee bucket */
        final TethysMoney myAmount = theTrans.getDebitAmount();
        final MoneyWiseXAnalysisPayeeBucket myPayeeBucket = theAnalysis.getPayees().getBucket(pDebit);
        if (isExpense) {
            myPayeeBucket.addExpense(myAmount);
        } else {
            myPayeeBucket.subtractIncome(myAmount);
        }
        theState.registerBucketInterest(myPayeeBucket);

        /* Adjust expense for Category bucket */
        final MoneyWiseXAnalysisTransCategoryBucket myCatBucket = theAnalysis.getTransCategories().getBucket(myCategory);
        if (isExpense) {
            myCatBucket.addExpense(myAmount);
        } else {
            myCatBucket.subtractIncome(myAmount);
        }
        theState.registerBucketInterest(myCatBucket);

        /* Record payee bucket */
        theTax.recordPayeeBucket(myPayeeBucket);
    }

    /**
     * process credit payee asset.
     * @param pCredit the credit asset
     */
    void processCreditPayee(final MoneyWisePayee pCredit) {
        /* Access category */
        final MoneyWiseTransCategory myCategory = theTrans.getCategory();
        final boolean isExpense = theTrans.isExpenseCategory();

        /* Adjust expense for Payee bucket */
        final TethysMoney myAmount = theTrans.getCreditAmount();
        final MoneyWiseXAnalysisPayeeBucket myPayeeBucket = theAnalysis.getPayees().getBucket(pCredit);
        if (isExpense) {
            myPayeeBucket.addExpense(myAmount);
        } else {
            myPayeeBucket.subtractIncome(myAmount);
        }
        theState.registerBucketInterest(myPayeeBucket);

        /* Adjust expense for Category bucket */
        final MoneyWiseXAnalysisTransCategoryBucket myCatBucket = theAnalysis.getTransCategories().getBucket(myCategory);
        if (isExpense) {
            myCatBucket.addExpense(myAmount);
        } else {
            myCatBucket.subtractIncome(myAmount);
        }
        theState.registerBucketInterest(myCatBucket);

        /* Record payee bucket */
        theTax.recordPayeeBucket(myPayeeBucket);
    }

    /**
     * Obtain Account bucket for asset.
     * @param pAsset the asset
     * @return the bucket
     */
    MoneyWiseXAnalysisAccountBucket<?> getAccountBucket(final MoneyWiseAssetBase pAsset) {
        switch (pAsset.getAssetType()) {
            case DEPOSIT:
                return theAnalysis.getDeposits().getBucket((MoneyWiseDeposit) pAsset);
            case CASH:
                return theAnalysis.getCash().getBucket((MoneyWiseCash) pAsset);
            case LOAN:
                return theAnalysis.getLoans().getBucket((MoneyWiseLoan) pAsset);
            case PORTFOLIO:
                return theAnalysis.getPortfolios().getCashBucket((MoneyWisePortfolio) pAsset);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * is the account an asset?
     * @param pAccount the account
     * @return true/false
     */
    boolean isAsset(final MoneyWiseAssetBase pAccount) {
        switch (pAccount.getAssetType()) {
            case DEPOSIT:
            case LOAN:
            case CASH:
            case PORTFOLIO:
                return true;
            case SECURITY:
            case PAYEE:
            case SECURITYHOLDING:
            case AUTOEXPENSE:
            default:
                return false;
        }
    }

    /**
     * is the account a payee?
     * @param pAccount the account
     * @return true/false
     */
    private boolean isPayee(final MoneyWiseAssetBase pAccount) {
        return pAccount.getAssetType() == MoneyWiseAssetType.PAYEE;
    }

    /**
     * is the account an autoExpense?
     * @param pAccount the account
     * @return true/false
     */
    private boolean isAutoExpense(final MoneyWiseAssetBase pAccount) {
        return pAccount.getAssetType() == MoneyWiseAssetType.AUTOEXPENSE;
    }

    /**
     * is the account a securityHolding?
     * @param pAccount the account
     * @return true/false
     */
    static boolean isSecurityHolding(final MoneyWiseTransAsset pAccount) {
        return pAccount instanceof MoneyWiseSecurityHolding;
    }

    /**
     * is the account a securityHolding?
     * @param pAccount the account
     * @return true/false
     */
    static boolean isPortfolio(final MoneyWiseTransAsset pAccount) {
        return pAccount instanceof MoneyWisePortfolio;
    }

    /**
     * is the category PortfolioXfer?
     * @param pCategoryClass the categoryClass
     * @return true/false
     */
    private static boolean isPortfolioXfer(final MoneyWiseTransCategoryClass pCategoryClass) {
        return pCategoryClass == MoneyWiseTransCategoryClass.PORTFOLIOXFER;
    }
}
