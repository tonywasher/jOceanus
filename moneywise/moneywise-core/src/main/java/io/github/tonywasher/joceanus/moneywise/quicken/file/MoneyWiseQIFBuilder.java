/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.quicken.file;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCash;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Builder class for QIF File.
 */
public class MoneyWiseQIFBuilder {
    /**
     * Quicken Transfer.
     */
    private static final String QIF_XFER = "Transfer";

    /**
     * Quicken Transfer from.
     */
    private static final String QIF_XFERFROM = " from ";

    /**
     * Quicken Transfer to.
     */
    private static final String QIF_XFERTO = " to ";

    /**
     * The QIF File.
     */
    private final MoneyWiseQIFFile theFile;

    /**
     * The QIF File Type.
     */
    private final MoneyWiseQIFType theFileType;

    /**
     * The QIF Portfolio Builder.
     */
    private final MoneyWiseQIFPortfolioBuilder thePortBuilder;

    /**
     * The TaxMan payee.
     */
    private final MoneyWisePayee theTaxMan;

    /**
     * The TaxCredit category.
     */
    private final MoneyWiseTransCategory theTaxCategory;

    /**
     * The NatInsurance category.
     */
    private final MoneyWiseTransCategory theNatInsCategory;

    /**
     * The DeemedBenefit category.
     */
    private final MoneyWiseTransCategory theBenefitCategory;

    /**
     * The Withheld category.
     */
    private final MoneyWiseTransCategory theWithheldCategory;

    /**
     * The Opening category.
     */
    private final MoneyWiseTransCategory theOpeningCategory;

    /**
     * Constructor.
     *
     * @param pFile     the QIF File
     * @param pData     the data
     * @param pAnalysis the analysis
     */
    protected MoneyWiseQIFBuilder(final MoneyWiseQIFFile pFile,
                                  final MoneyWiseDataSet pData,
                                  final MoneyWiseAnalysis pAnalysis) {
        /* Store parameters */
        theFile = pFile;
        theFileType = pFile.getFileType();

        /* Create portfolio builder */
        thePortBuilder = new MoneyWiseQIFPortfolioBuilder(this, pData, pAnalysis);

        /* Store Tax account */
        final MoneyWisePayeeList myPayees = pData.getPayees();
        theTaxMan = myPayees.getSingularClass(MoneyWisePayeeClass.TAXMAN);

        /* Store categories */
        final MoneyWiseTransCategoryList myCategories = pData.getTransCategories();
        theTaxCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.TAXCREDIT);
        theNatInsCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.EMPLOYEENATINS);
        theBenefitCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.DEEMEDBENEFIT);
        theWithheldCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.WITHHELD);
        theOpeningCategory = myCategories.getSingularClass(MoneyWiseTransCategoryClass.OPENINGBALANCE);
    }

    /**
     * Obtain the file.
     *
     * @return the file
     */
    protected MoneyWiseQIFFile getFile() {
        return theFile;
    }

    /**
     * Obtain the tax category.
     *
     * @return the category
     */
    protected MoneyWiseQIFEventCategory getTaxCategory() {
        return theFile.registerCategory(theTaxCategory);
    }

    /**
     * Obtain the tax payee.
     *
     * @return the payee
     */
    protected MoneyWiseQIFPayee getTaxMan() {
        return theFile.registerPayee(theTaxMan);
    }

    /**
     * Process event.
     *
     * @param pTrans the transaction
     */
    protected void processEvent(final MoneyWiseTransaction pTrans) {
        /* Access account and partner */
        final MoneyWiseTransAsset myAccount = pTrans.getAccount();
        final MoneyWiseTransAsset myPartner = pTrans.getPartner();
        final boolean bFrom = pTrans.getDirection().isFrom();

        /* If this deals with a payee */
        if (myPartner instanceof MoneyWisePayee myPayee) {
            /* If this is expense */
            if (bFrom) {
                /* Process Debit Payee */
                processDebitPayee(myPayee, myAccount, pTrans);
            } else {
                /* Process Credit Payee */
                processCreditPayee(myPayee, myAccount, pTrans);
            }

        } else if (bFrom) {
            /* else process Transfer Partner -> Account */
            processTransfer(myPartner, myAccount, pTrans);
        } else {
            /* else process Transfer Account -> Partner */
            processTransfer(myAccount, myPartner, pTrans);
        }
    }

    /**
     * Process opening balance.
     *
     * @param pDeposit   the deposit
     * @param pStartDate the start date
     * @param pBalance   the opening balance
     */
    protected void processBalance(final MoneyWiseDeposit pDeposit,
                                  final OceanusDate pStartDate,
                                  final OceanusMoney pBalance) {
        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pDeposit);

        /* Create the event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pStartDate);
        myEvent.recordAmount(pBalance);

        /* If we are using self-Opening balance */
        if (theFileType.selfOpeningBalance()) {
            /* Record self reference */
            myEvent.recordAccount(myAccount.getAccount());

            /* else use an event */
        } else {
            /* Register category */
            final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(theOpeningCategory);
            myEvent.recordCategory(myCategory);
        }

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process debit payee event.
     *
     * @param pPayee  the payee
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processDebitPayee(final MoneyWisePayee pPayee,
                                     final MoneyWiseTransAsset pCredit,
                                     final MoneyWiseTransaction pTrans) {
        /* If this is a cash recovery */
        if (pCredit instanceof MoneyWiseCash myCash
                && myCash.isAutoExpense()) {
            /* process as cash recovery */
            processCashRecovery(pPayee, myCash, pTrans);

            /* If this is an income to a security */
        } else if (pCredit instanceof MoneyWiseSecurityHolding myHolding) {
            /* process as income to security */
            thePortBuilder.processIncomeToSecurity(pPayee, myHolding, pTrans);

            /* If this is an income to a portfolio */
        } else if (pCredit instanceof MoneyWisePortfolio myPortfolio) {
            /* process as income to portfolio */
            thePortBuilder.processIncomeToPortfolio(pPayee, myPortfolio, pTrans);

            /* else if we have additional detail */
        } else if (hasXtraDetail(pTrans)) {
            /* process as detailed income */
            processDetailedIncome(pPayee, pCredit, pTrans);

        } else {
            /* process as standard income */
            processStandardIncome(pPayee, pCredit, pTrans);
        }
    }

    /**
     * Process credit payee event.
     *
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    protected void processCreditPayee(final MoneyWisePayee pPayee,
                                      final MoneyWiseTransAsset pDebit,
                                      final MoneyWiseTransaction pTrans) {
        /* If this is a cash payment */
        if (pDebit instanceof MoneyWiseCash myCash
                && myCash.isAutoExpense()) {
            /* process as cash payment */
            processCashPayment(pPayee, myCash, pTrans);

            /* If this is an expense from a security */
        } else if (pDebit instanceof MoneyWiseSecurityHolding myHolding) {
            /* process as expense from security */
            thePortBuilder.processExpenseFromSecurity(pPayee, myHolding, pTrans);

            /* If this is an expense from a portfolio */
        } else if (pDebit instanceof MoneyWisePortfolio myPortfolio) {
            /* process as expense from portfolio */
            thePortBuilder.processExpenseFromPortfolio(pPayee, myPortfolio, pTrans);

            /* else if we have additional detail */
        } else if (hasXtraDetail(pTrans)) {
            /* process as detailed income */
            processDetailedExpense(pPayee, pDebit, pTrans);

        } else {
            /* process as standard expense */
            processStandardExpense(pPayee, pDebit, pTrans);
        }
    }

    /**
     * Process transfer event.
     *
     * @param pDebit  the debit account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processTransfer(final MoneyWiseTransAsset pDebit,
                                   final MoneyWiseTransAsset pCredit,
                                   final MoneyWiseTransaction pTrans) {
        /* If this is a cash AutoExpense */
        if (pCredit instanceof MoneyWiseCash myCash
                && myCash.isAutoExpense()) {
            /* Process as standard expense */
            processCashExpense(myCash, pDebit, pTrans);

            /* If this is a cash AutoReceipt */
        } else if (pDebit instanceof MoneyWiseCash myCash
                && myCash.isAutoExpense()) {
            /* Process as standard expense */
            processCashReceipt(myCash, pCredit, pTrans);

            /* If this is a transfer from a security */
        } else if (pDebit instanceof MoneyWiseSecurityHolding myDebitHolding) {
            /* Handle transfer between securities */
            if (pCredit instanceof MoneyWiseSecurityHolding myCreditHolding) {
                /* process as transfer between securities */
                thePortBuilder.processTransferBetweenSecurities(myDebitHolding, myCreditHolding, pTrans);
            } else {
                /* process as transfer from security */
                thePortBuilder.processTransferFromSecurity(myDebitHolding, pCredit, pTrans);
            }
            /* If this is a transfer to a security */
        } else if (pCredit instanceof MoneyWiseSecurityHolding myCreditHolding) {
            /* process as transfer to security */
            thePortBuilder.processTransferToSecurity(myCreditHolding, pDebit, pTrans);

            /* If this is a transfer from a portfolio */
        } else if (pDebit instanceof MoneyWisePortfolio myDebitPortfolio) {
            /* Handle transfer between securities */
            if (pCredit instanceof MoneyWisePortfolio myCreditPortfolio) {
                /* process as transfer between portfolios */
                thePortBuilder.processTransferBetweenPortfolios(myDebitPortfolio, myCreditPortfolio, pTrans);
            } else {
                /* process as transfer from portfolio */
                thePortBuilder.processTransferFromPortfolio(myDebitPortfolio, pCredit, pTrans);
            }
            /* If this is a transfer to a portfolio */
        } else if (pCredit instanceof MoneyWisePortfolio myCreditPortfolio) {
            /* process as transfer to portfolio */
            thePortBuilder.processTransferToPortfolio(myCreditPortfolio, pDebit, pTrans);

        } else {
            /* Access details */
            final MoneyWiseTransCategoryClass myCat = Objects.requireNonNull(pTrans.getCategoryClass());

            /* Switch on category class */
            switch (myCat) {
                case CASHBACK:
                    /* Process as cashBack payment */
                    processCashBack(pDebit, pCredit, pTrans);
                    break;
                case INTEREST:
                case LOYALTYBONUS:
                    /* Process as interest payment */
                    processInterest(pDebit, pCredit, pTrans);
                    break;
                case LOANINTERESTEARNED:
                case RENTALINCOME:
                case ROOMRENTALINCOME:
                    /* Process as income from parent of the credit */
                    processStandardIncome((MoneyWisePayee) pCredit.getParent(), pCredit, pTrans);
                    break;
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                    /* Process as expense to parent of the credit (recursive) */
                    processStandardExpense((MoneyWisePayee) pCredit.getParent(), pDebit, pTrans);
                    break;
                default:
                    /* Process as standard transfer */
                    processStandardTransfer(pDebit, pCredit, pTrans);
                    break;
            }
        }
    }

    /**
     * Does the transaction have extra detail.
     *
     * @param pTrans the transaction
     * @return true/false
     */
    protected static boolean hasXtraDetail(final MoneyWiseTransaction pTrans) {
        if (pTrans.getTaxCredit() != null) {
            return true;
        }
        if (pTrans.getEmployeeNatIns() != null) {
            return true;
        }
        if (pTrans.getDeemedBenefit() != null) {
            return true;
        }
        return pTrans.getWithheld() != null;
    }

    /**
     * Process standard income.
     *
     * @param pPayee  the payee
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processStandardIncome(final MoneyWisePayee pPayee,
                                         final MoneyWiseTransAsset pCredit,
                                         final MoneyWiseTransaction pTrans) {
        /* Access the Payee details */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCredit);

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Create a new event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordAmount(pTrans.getAmount());
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process detailed income.
     *
     * @param pPayee  the payee
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processDetailedIncome(final MoneyWisePayee pPayee,
                                         final MoneyWiseTransAsset pCredit,
                                         final MoneyWiseTransaction pTrans) {
        /* Access the Payee details */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pPayee);
        final MoneyWiseQIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

        /* Access the Category details */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCredit);

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Obtain basic amount */
        OceanusMoney myAmount = pTrans.getAmount();

        /* Create a new event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordPayee(myPayee);
        myEvent.recordAmount(myAmount);

        /* Add Split event */
        myAmount = new OceanusMoney(myAmount);
        myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

        /* Handle Tax Credit */
        OceanusMoney myTaxCredit = pTrans.getTaxCredit();
        if (myTaxCredit != null) {
            /* Add to amount */
            myAmount.addAmount(myTaxCredit);
            myTaxCredit = new OceanusMoney(myTaxCredit);
            myTaxCredit.negate();

            /* Access the Category details */
            final MoneyWiseQIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
        }

        /* Handle National Insurance */
        OceanusMoney myNatIns = pTrans.getEmployeeNatIns();
        if (myNatIns != null) {
            /* Add to amount */
            myAmount.addAmount(myNatIns);
            myNatIns = new OceanusMoney(myNatIns);
            myNatIns.negate();

            /* Access the Category details */
            final MoneyWiseQIFEventCategory myInsCategory = theFile.registerCategory(theNatInsCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myInsCategory, myNatIns, myTaxPayee.getName());
        }

        /* Handle Deemed Benefit */
        OceanusMoney myBenefit = pTrans.getDeemedBenefit();
        if (myBenefit != null) {
            /* Access the Category details */
            final MoneyWiseQIFEventCategory myBenCategory = theFile.registerCategory(theBenefitCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myBenCategory, myBenefit, myPayee.getName());

            /* Add to amount */
            myBenefit = new OceanusMoney(myBenefit);
            myBenefit.negate();

            /* Access the Category details */
            final MoneyWiseQIFEventCategory myWithCategory = theFile.registerCategory(theBenefitCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myWithCategory, myBenefit, myPayee.getName());
        }

        /* Handle Withheld */
        OceanusMoney myWithheld = pTrans.getWithheld();
        if (myWithheld != null) {
            /* Add to amount */
            myAmount.addAmount(myWithheld);
            myWithheld = new OceanusMoney(myWithheld);
            myWithheld.negate();

            /* Access the Category details */
            final MoneyWiseQIFEventCategory myWithCategory = theFile.registerCategory(theWithheldCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myWithCategory, myWithheld, myPayee.getName());
        }

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process standard expense.
     *
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    protected void processStandardExpense(final MoneyWisePayee pPayee,
                                          final MoneyWiseTransAsset pDebit,
                                          final MoneyWiseTransaction pTrans) {
        /* Access the Payee details */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pDebit);

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Access the amount */
        final OceanusMoney myAmount = new OceanusMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process detailed expense.
     *
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the expense
     */
    protected void processDetailedExpense(final MoneyWisePayee pPayee,
                                          final MoneyWiseTransAsset pDebit,
                                          final MoneyWiseTransaction pTrans) {
        /* Access the Payee details */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pPayee);
        final MoneyWiseQIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

        /* Access the Category details */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pDebit);

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Obtain basic amount */
        OceanusMoney myAmount = new OceanusMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordPayee(myPayee);
        myEvent.recordAmount(myAmount);

        /* Add Split event */
        myAmount = new OceanusMoney(myAmount);
        myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

        /* Handle Tax Credit */
        final OceanusMoney myTaxCredit = pTrans.getTaxCredit();
        if (myTaxCredit != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myTaxCredit);

            /* Access the Category details */
            final MoneyWiseQIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
        }

        /* Handle National Insurance */
        final OceanusMoney myNatIns = pTrans.getEmployeeNatIns();
        if (myNatIns != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myNatIns);

            /* Access the Category details */
            final MoneyWiseQIFEventCategory myInsCategory = theFile.registerCategory(theNatInsCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myInsCategory, myNatIns, myTaxPayee.getName());
        }

        /* Handle Deemed Benefit */
        final OceanusMoney myBenefit = pTrans.getDeemedBenefit();
        if (myBenefit != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myBenefit);

            /* Access the Category details */
            final MoneyWiseQIFEventCategory myBenCategory = theFile.registerCategory(theBenefitCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myBenCategory, myBenefit, myPayee.getName());
        }

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process standard transfer.
     *
     * @param pDebit  the debit account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processStandardTransfer(final MoneyWiseTransAsset pDebit,
                                           final MoneyWiseTransAsset pCredit,
                                           final MoneyWiseTransaction pTrans) {
        /* Access details */
        final OceanusMoney myAmount = pTrans.getAmount();

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myDebitAccount = theFile.registerAccount(pDebit);
        final MoneyWiseQIFAccountEvents myCreditAccount = theFile.registerAccount(pCredit);

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Create a new event */
        MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordAccount(myDebitAccount.getAccount(), myList);

        /* Build payee description */
        myEvent.recordPayee(buildXferFromPayee(pDebit));

        /* Add event to event list */
        myCreditAccount.addEvent(myEvent);

        /* Build out amount */
        final OceanusMoney myOutAmount = new OceanusMoney(myAmount);
        myOutAmount.negate();

        /* Create a new event */
        myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordAmount(myOutAmount);
        myEvent.recordAccount(myCreditAccount.getAccount(), myList);

        /* Build payee description */
        myEvent.recordPayee(buildXferToPayee(pCredit));

        /* Add event to event list */
        myDebitAccount.addEvent(myEvent);
    }

    /**
     * Build xferFrom payee line.
     *
     * @param pPartner the Transfer Partner
     * @return the line
     */
    protected String buildXferFromPayee(final MoneyWiseTransAsset pPartner) {
        /* Determine mode */
        final boolean useSimpleTransfer = theFileType.useSimpleTransfer();

        /* Build payee description */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(QIF_XFER);
        if (!useSimpleTransfer) {
            myBuilder.append(QIF_XFERFROM);
            myBuilder.append(pPartner.getName());
        }

        /* Return the payee */
        return myBuilder.toString();
    }

    /**
     * Build xferFrom payee line.
     *
     * @param pPartner the Transfer Partner
     * @return the line
     */
    protected String buildXferToPayee(final MoneyWiseTransAsset pPartner) {
        /* Determine mode */
        final boolean useSimpleTransfer = theFileType.useSimpleTransfer();

        /* Build payee description */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(QIF_XFER);
        if (!useSimpleTransfer) {
            myBuilder.append(QIF_XFERTO);
            myBuilder.append(pPartner.getName());
        }

        /* Return the payee */
        return myBuilder.toString();
    }

    /**
     * Process interest.
     *
     * @param pDebit  the debit account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processInterest(final MoneyWiseTransAsset pDebit,
                                   final MoneyWiseTransAsset pCredit,
                                   final MoneyWiseTransaction pTrans) {
        /* Access details */
        OceanusMoney myAmount = pTrans.getAmount();

        /* Determine mode */
        final boolean isRecursive = pDebit.equals(pCredit);
        final boolean hideBalancingTransfer = theFileType.hideBalancingSplitTransfer();
        final boolean hasXtraDetail = hasXtraDetail(pTrans);

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myIntAccount = theFile.registerAccount(pDebit);

        /* Access the payee */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee((MoneyWisePayee) pDebit.getParent());

        /* Access the category */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* If this is a simple interest */
        if (isRecursive && !hasXtraDetail) {
            /* Create a new event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myPayee);
            myEvent.recordCategory(myCategory, myList);

            /* Add event to event list */
            myIntAccount.addEvent(myEvent);

            /* Else we need splits */
        } else {
            /* Create a new event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);

            /* Record basic details */
            myEvent.recordAmount(isRecursive
                    ? myAmount
                    : new OceanusMoney());
            myEvent.recordPayee(myPayee);

            /* Add Split event */
            myAmount = new OceanusMoney(myAmount);
            myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

            /* Handle Tax Credit */
            OceanusMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                /* Access tax payee */
                final MoneyWiseQIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

                /* Add to amount */
                myAmount.addAmount(myTaxCredit);
                myTaxCredit = new OceanusMoney(myTaxCredit);
                myTaxCredit.negate();

                /* Access the Category details */
                final MoneyWiseQIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

                /* Add Split event */
                myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
            }

            /* Handle Withheld */
            OceanusMoney myWithheld = pTrans.getWithheld();
            if (myWithheld != null) {
                /* Add to amount */
                myAmount.addAmount(myWithheld);
                myWithheld = new OceanusMoney(myWithheld);
                myWithheld.negate();

                /* Access the Category details */
                final MoneyWiseQIFEventCategory myWithCategory = theFile.registerCategory(theWithheldCategory);

                /* Add Split event */
                myEvent.recordSplitRecord(myWithCategory, myWithheld, myPayee.getName());
            }

            /* Handle Non-Recursion */
            if (!isRecursive) {
                /* Add to amount */
                final OceanusMoney myOutAmount = new OceanusMoney(pTrans.getAmount());
                myOutAmount.negate();

                /* Access the Account details */
                final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCredit);

                /* Add Split event */
                myEvent.recordSplitRecord(myAccount.getAccount(), myOutAmount, null);
            }

            /* Add event to event list */
            myIntAccount.addEvent(myEvent);
        }

        /* If we need a balancing transfer */
        if (!isRecursive && !hideBalancingTransfer) {
            /* Access the Account details */
            final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCredit);

            /* Create a new event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(pTrans.getAmount());
            myEvent.recordAccount(myIntAccount.getAccount(), myList);

            /* Build payee description */
            myEvent.recordPayee(buildXferFromPayee(pDebit));

            /* Add event to event list */
            myAccount.addEvent(myEvent);
        }
    }

    /**
     * Process cashBack.
     *
     * @param pDebit  the debit account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processCashBack(final MoneyWiseTransAsset pDebit,
                                   final MoneyWiseTransAsset pCredit,
                                   final MoneyWiseTransaction pTrans) {
        /* Access details */
        OceanusMoney myAmount = pTrans.getAmount();

        /* Determine mode */
        final boolean isRecursive = pDebit.equals(pCredit);
        final boolean hideBalancingTransfer = theFileType.hideBalancingSplitTransfer();

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myBaseAccount = theFile.registerAccount(pDebit);

        /* Access the payee */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee((MoneyWisePayee) pDebit.getParent());

        /* Access the category */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* If this is a simple cashBack */
        if (isRecursive) {
            /* Create a new event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myPayee);
            myEvent.recordCategory(myCategory, myList);

            /* Add event to event list */
            myBaseAccount.addEvent(myEvent);

            /* Else we need splits */
        } else {
            /* Create a new event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);

            /* Record basic details */
            myEvent.recordAmount(new OceanusMoney());
            myEvent.recordPayee(myPayee);

            /* Add Split event */
            myAmount = new OceanusMoney(myAmount);
            myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

            /* Add to amount */
            final OceanusMoney myOutAmount = new OceanusMoney(pTrans.getAmount());
            myOutAmount.negate();

            /* Access the Account details */
            final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCredit);

            /* Add Split event */
            myEvent.recordSplitRecord(myAccount.getAccount(), myOutAmount, null);

            /* Add event to event list */
            myBaseAccount.addEvent(myEvent);
        }

        /* If we need a balancing transfer */
        if (!isRecursive && !hideBalancingTransfer) {
            /* Access the Account details */
            final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCredit);

            /* Create a new event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(pTrans.getAmount());
            myEvent.recordAccount(myBaseAccount.getAccount(), myList);

            /* Build payee description */
            myEvent.recordPayee(buildXferFromPayee(pDebit));

            /* Add event to event list */
            myAccount.addEvent(myEvent);
        }
    }

    /**
     * Process cash recovery.
     *
     * @param pPayee the payee
     * @param pCash  the cash account
     * @param pTrans the transaction
     */
    protected void processCashRecovery(final MoneyWisePayee pPayee,
                                       final MoneyWiseCash pCash,
                                       final MoneyWiseTransaction pTrans) {
        /* Access the Payee details */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        final MoneyWiseQIFEventCategory myAutoCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCash);

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Access the amount */
        final OceanusMoney myInAmount = pTrans.getAmount();
        final OceanusMoney myOutAmount = new OceanusMoney(myInAmount);
        myOutAmount.negate();

        /* Create a new event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordAmount(new OceanusMoney());
        myEvent.recordPayee(myPayee);
        myEvent.recordSplitRecord(myCategory, myList, myInAmount, myPayee.getName());
        myEvent.recordSplitRecord(myAutoCategory, myList, myOutAmount, pCash.getAutoPayee().getName());

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash payment.
     *
     * @param pPayee the payee
     * @param pCash  the cash account
     * @param pTrans the transaction
     */
    protected void processCashPayment(final MoneyWisePayee pPayee,
                                      final MoneyWiseCash pCash,
                                      final MoneyWiseTransaction pTrans) {
        /* Access the Payee details */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        final MoneyWiseQIFEventCategory myAutoCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCash);

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Access the amount */
        final OceanusMoney myInAmount = pTrans.getAmount();
        final OceanusMoney myOutAmount = new OceanusMoney(myInAmount);
        myOutAmount.negate();

        /* Create a new event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordAmount(new OceanusMoney());
        myEvent.recordPayee(myPayee);
        myEvent.recordSplitRecord(myAutoCategory, myList, myInAmount, pCash.getAutoPayee().getName());
        myEvent.recordSplitRecord(myCategory, myList, myOutAmount, myPayee.getName());

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash expense.
     *
     * @param pCash  the cash account
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    protected void processCashExpense(final MoneyWiseCash pCash,
                                      final MoneyWiseTransAsset pDebit,
                                      final MoneyWiseTransaction pTrans) {
        /* Access the Payee details */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pCash.getAutoPayee());

        /* Access the Category details */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pDebit);

        /* Access the amount */
        final OceanusMoney myAmount = new OceanusMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash receipt.
     *
     * @param pCash   the cash account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    protected void processCashReceipt(final MoneyWiseCash pCash,
                                      final MoneyWiseTransAsset pCredit,
                                      final MoneyWiseTransaction pTrans) {
        /* Access the Payee details */
        final MoneyWiseQIFPayee myPayee = theFile.registerPayee(pCash.getAutoPayee());

        /* Access the Category details */
        final MoneyWiseQIFEventCategory myCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        final MoneyWiseQIFAccountEvents myAccount = theFile.registerAccount(pCredit);

        /* Obtain classes */
        final List<MoneyWiseQIFClass> myList = getTransactionClasses(pTrans);

        /* Create a new event */
        final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, pTrans);
        myEvent.recordAmount(pTrans.getAmount());
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Obtain classes for transaction.
     *
     * @param pTrans the transaction
     * @return the class list (or null)
     */
    protected List<MoneyWiseQIFClass> getTransactionClasses(final MoneyWiseTransaction pTrans) {
        /* Create return value */
        List<MoneyWiseQIFClass> myList = null;

        /* Obtain the tags for the transaction */
        final List<MoneyWiseTransTag> myTags = pTrans.getTransactionTags();

        /* If we have tags */
        if (myTags != null) {
            /* Allocate the list */
            myList = new ArrayList<>();

            /* Loop through the tags */
            final Iterator<MoneyWiseTransTag> myIterator = myTags.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseTransTag myTag = myIterator.next();

                /* Access the transaction tag */
                final MoneyWiseQIFClass myClass = theFile.registerClass(myTag);

                /* Add to the list */
                myList.add(myClass);
            }
        }

        /* Return the list */
        return myList;
    }
}
