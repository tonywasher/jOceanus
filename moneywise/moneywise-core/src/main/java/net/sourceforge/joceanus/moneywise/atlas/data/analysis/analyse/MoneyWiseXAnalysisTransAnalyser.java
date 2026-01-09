/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisInterfaces.MoneyWiseXAnalysisCursor;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseLogicException;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisAccountBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;

import java.util.List;

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
     * The reporting currency.
     */
    private final MoneyWiseCurrency theCurrency;

    /**
     * The current transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Constructor.
     * @param pAnalyser the analyser
     * @throws OceanusException on error
     */
    MoneyWiseXAnalysisTransAnalyser(final MoneyWiseXAnalysisEventAnalyser pAnalyser) throws OceanusException {
        /* Store details */
        theAnalysis = pAnalyser.getAnalysis();
        theState = pAnalyser.getState();
        theMarket = pAnalyser.getMarket();
        theTax = pAnalyser.getTax();
        theCurrency = theAnalysis.getCurrency();

        /* Create the security analyser */
        theSecurity = new MoneyWiseXAnalysisSecurity(pAnalyser, this);
        thePortfolioXfer = new MoneyWiseXAnalysisPortfolioXfer(pAnalyser, theSecurity);
        theTax.declareSecurityAnalyser(theSecurity);
    }

    /**
     * Obtain the currency.
     * @return the currency
     */
    MoneyWiseCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Process transaction event.
     * @param pEvent the event
     * @throws OceanusException on error
     */
    public void processTransaction(final MoneyWiseXAnalysisEvent pEvent) throws OceanusException {
        /* Parse the event */
        theTransaction = new MoneyWiseXAnalysisTransaction(pEvent);
        final MoneyWiseTransaction myTrans = theTransaction.getTransaction();

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
        final MoneyWiseTransaction myTrans = theTransaction.getTransaction();
        final MoneyWiseTransAsset myDebit = theTransaction.getDebitAccount();
        final MoneyWiseTransAsset myCredit = theTransaction.getCreditAccount();

        /* If the event relates to a security holding account, split out the workings */
        if (isSecurityHolding(myDebit)) {
            /* Process as a Security transaction */
            theSecurity.processDebitSecurity(theTransaction);

            /* If the event relates to a security holding partner, split out the workings */
        } else if (isSecurityHolding(myCredit)) {
            /* Process as a Security transaction */
            theSecurity.processCreditSecurity(theTransaction);

            /* If the event is portfolioXfer, split out the workings */
        } else if (isPortfolioXfer(theTransaction.getCategoryClass())) {
            /* Process the portfolioXfer */
            thePortfolioXfer.processPortfolioXfer(theTransaction);

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

        /* adjust category bucket */
        adjustCategoryBucket();

        /* Process taxBasis */
        theTax.adjustTaxBasis(theTransaction);

        /* Register the eventBuckets */
        final MoneyWiseXAnalysisEvent myEvent = theTransaction.getEvent();
        theMarket.adjustMarketTotals(myEvent);
        theState.registerInterestedBucketsForEvent(myEvent);
    }

    /**
     * process non-security transaction.
     */
    private void processNonSecurity() {
        /* Adjust parent details */
        theTransaction.adjustParent();

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
        final MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) theTransaction.getDebitAccount();
        final MoneyWiseAssetBase myCredit = (MoneyWiseAssetBase) theTransaction.getCreditAccount();

        /* If the debit account is an asset */
        OceanusMoney myDebitAmount = theTransaction.getDebitAmount();
        if (isAsset(myDebit)) {
            /* Process the debit asset bucket */
            myDebitAmount = processDebitAsset(myDebit);
        }

        /* If the credit account is an asset */
        OceanusMoney myCreditAmount = theTransaction.getCreditAmount();
        if (isAsset(myCredit)) {
            /* Process the credit asset bucket */
            myCreditAmount = processCreditAsset(myCredit);

            /* Reload the debitAmount which changes if debit is payee and credit is foreign */
            myDebitAmount = theTransaction.getDebitAmount();
        }

        /* Adjust for currencyFluctuation */
        final OceanusMoney myFluctuation = new OceanusMoney(myDebitAmount);
        myFluctuation.addAmount(myCreditAmount);
        if (myFluctuation.isNonZero()) {
            theMarket.adjustTotalsForCurrencyFluctuation(theTransaction.getEvent(), myFluctuation);
        }
    }

    /**
     * process debit Asset.
     * @param pDebit the debit asset
     * @return the debitAmount in reporting currency
     */
    OceanusMoney processDebitAsset(final MoneyWiseAssetBase pDebit) {
        /* Adjust the debit asset bucket */
        final MoneyWiseXAnalysisAccountBucket<?> myBucket = getAccountBucket(pDebit);
        OceanusMoney myDebitAmount = theTransaction.getDebitAmount();
        myBucket.addToBalance(myDebitAmount);
        myBucket.adjustValuation();
        theState.registerBucketInterest(myBucket);

        /* If the asset is foreign, convert debit amount to reporting currency */
        if (pDebit.isForeign()) {
            /* convert debit amount to reporting currency */
            myDebitAmount = myBucket.getDeltaValuation();
            theTransaction.setDebitAmount(myDebitAmount);
        }

        /* Return the debit amount */
        return myDebitAmount;
    }


    /**
     * process credit Asset.
     * @param pCredit the credit asset
     * @return the creditAmount in reporting currency
     */
    OceanusMoney processCreditAsset(final MoneyWiseAssetBase pCredit) {
        /* Adjust the credit asset bucket */
        final MoneyWiseXAnalysisAccountBucket<?> myBucket = getAccountBucket(pCredit);
        OceanusMoney myCreditAmount = theTransaction.getCreditAmount();
        myBucket.addToBalance(myCreditAmount);
        myBucket.adjustValuation();
        theState.registerBucketInterest(myBucket);

        /* If the asset is foreign */
        if (pCredit.isForeign()) {
            /* convert credit amount to reporting currency */
            myCreditAmount = myBucket.getDeltaValuation();
            theTransaction.setCreditAmount(myCreditAmount);
        }

        /* Return the credit amount */
        return myCreditAmount;
    }

    /**
     * process autoExpense assets.
     */
    private void processAutoExpense() {
        /* Access debit and credit accounts and amounts */
        final MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) theTransaction.getDebitAccount();
        final MoneyWiseAssetBase myCredit = (MoneyWiseAssetBase) theTransaction.getCreditAccount();

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

        /* If the debit is foreign and the credit is a payee */
        if (pDebit.isForeign()) {
            final MoneyWiseTransAsset myCredit = theTransaction.getCreditAccount();
            if (myCredit instanceof MoneyWisePayee) {
                /* Convert the debit amount to reporting currency */
                final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
                final OceanusRatio myRate = myCursor.getCurrentXchgRate(pDebit.getAssetCurrency());
                final OceanusMoney myAmount = theTransaction.getDebitAmount();
                theTransaction.setDebitAmount(myAmount.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate));
            }
        }

        /* Adjust expense for autoPayee bucket */
        final OceanusMoney myAmount = theTransaction.getDebitAmount();
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

        /* If the credit is foreign and the debit is a payee */
        if (pCredit.isForeign()) {
            final MoneyWiseTransAsset myDebit = theTransaction.getDebitAccount();
            if (myDebit instanceof MoneyWisePayee) {
                /* Convert the debit amount to reporting currency */
                final MoneyWiseXAnalysisCursor myCursor = theAnalysis.getCursor();
                final OceanusRatio myRate = myCursor.getCurrentXchgRate(pCredit.getAssetCurrency());
                final OceanusMoney myAmount = theTransaction.getCreditAmount();
                theTransaction.setCreditAmount(myAmount.convertCurrency(theAnalysis.getCurrency().getCurrency(), myRate));
            }
        }

        /* Adjust expense for autoPayee bucket */
        final OceanusMoney myAmount = theTransaction.getCreditAmount();
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
        final MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) theTransaction.getDebitAccount();
        final MoneyWiseAssetBase myCredit = (MoneyWiseAssetBase) theTransaction.getCreditAccount();

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
        /* Determine income/expense */
        final boolean isExpense = theTransaction.isExpenseCategory();

        /* Adjust expense for Payee bucket */
        final OceanusMoney myAmount = theTransaction.getDebitAmount();
        final MoneyWiseXAnalysisPayeeBucket myPayeeBucket = theAnalysis.getPayees().getBucket(pDebit);
        if (isExpense) {
            myPayeeBucket.addExpense(myAmount);
        } else {
            myPayeeBucket.subtractIncome(myAmount);
        }
        theState.registerBucketInterest(myPayeeBucket);

        /* Record payee bucket */
        theTax.recordPayeeBucket(myPayeeBucket);
    }

    /**
     * process credit payee asset.
     * @param pCredit the credit asset
     */
    void processCreditPayee(final MoneyWisePayee pCredit) {
        /* Determine income/expense */
        final boolean isExpense = theTransaction.isExpenseCategory();

        /* Adjust expense for Payee bucket */
        final OceanusMoney myAmount = theTransaction.getCreditAmount();
        final MoneyWiseXAnalysisPayeeBucket myPayeeBucket = theAnalysis.getPayees().getBucket(pCredit);
        if (isExpense) {
            myPayeeBucket.addExpense(myAmount);
        } else {
            myPayeeBucket.subtractIncome(myAmount);
        }
        theState.registerBucketInterest(myPayeeBucket);

        /* Record payee bucket */
        theTax.recordPayeeBucket(myPayeeBucket);
    }

    /**
     * Adjust category buckets.
     */
    void adjustCategoryBucket() {
        /* Access the credit amount and category */
        final OceanusMoney myAmount = theTransaction.isTo()
                ? theTransaction.getCreditAmount()
                : theTransaction.getDebitAmount();
        final MoneyWiseTransCategory myCategory = theTransaction.getCategory();

        /* Ignore transfers */
        if (myCategory.isTransfer()) {
            return;
        }

        /* Adjust income/expense for Category bucket */
        final MoneyWiseXAnalysisTransCategoryBucket myCatBucket = theAnalysis.getTransCategories().getBucket(myCategory);
        if (theTransaction.isExpenseCategory()) {
            myCatBucket.addExpense(myAmount);
        } else if (theTransaction.isIncomeCategory()) {
            myCatBucket.subtractIncome(myAmount);
        }
        theState.registerBucketInterest(myCatBucket);
    }

    /**
     * adjustForeignDebit.
     * @param pExchangeRate the exchangeRate
     * @return the adjusted debitAmount
     */
    OceanusMoney adjustForeignAssetDebit(final OceanusRatio pExchangeRate) {
        /* Calculate the value in the local currency */
        OceanusMoney myAmount = theTransaction.getDebitAmount();
        myAmount = myAmount.convertCurrency(theCurrency.getCurrency(), pExchangeRate);
        theTransaction.setDebitAmount(myAmount);

        /* Adjust for currencyFluctuation */
        final OceanusMoney myCreditAmount = theTransaction.getCreditAmount();
        final OceanusMoney myFluctuation = new OceanusMoney(myCreditAmount);
        myFluctuation.addAmount(myAmount);
        if (myFluctuation.isNonZero()) {
            theMarket.adjustTotalsForCurrencyFluctuation(theTransaction.getEvent(), myFluctuation);
        }

        /* return the new debit amount */
        return myAmount;
    }

    /**
     * adjustForeignCredit.
     * @param pExchangeRate the exchangeRate
     * @return the adjusted creditAmount
     */
    OceanusMoney adjustForeignAssetCredit(final OceanusRatio pExchangeRate) {
        /* Calculate the value in the local currency */
        OceanusMoney myAmount = theTransaction.getCreditAmount();
        myAmount = myAmount.convertCurrency(theCurrency.getCurrency(), pExchangeRate);
        theTransaction.setCreditAmount(myAmount);

        /* Adjust for currencyFluctuation */
        final OceanusMoney myDebitAmount = theTransaction.getDebitAmount();
        final OceanusMoney myFluctuation = new OceanusMoney(myDebitAmount);
        myFluctuation.addAmount(myAmount);
        if (myFluctuation.isNonZero()) {
            theMarket.adjustTotalsForCurrencyFluctuation(theTransaction.getEvent(), myFluctuation);
        }

        /* return the new credit amount */
        return myAmount;
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
    boolean isPayee(final MoneyWiseAssetBase pAccount) {
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
