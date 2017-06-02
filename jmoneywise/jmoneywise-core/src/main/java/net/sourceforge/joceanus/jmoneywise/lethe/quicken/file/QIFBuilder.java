/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Builder class for QIF File.
 */
public class QIFBuilder {
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
    private final QIFFile theFile;

    /**
     * The QIF File Type.
     */
    private final QIFType theFileType;

    /**
     * The QIF Portfolio Builder.
     */
    private final QIFPortfolioBuilder thePortBuilder;

    /**
     * The TaxMan payee.
     */
    private final Payee theTaxMan;

    /**
     * The TaxCredit category.
     */
    private final TransactionCategory theTaxCategory;

    /**
     * The NatInsurance category.
     */
    private final TransactionCategory theNatInsCategory;

    /**
     * The DeemedBenefit category.
     */
    private final TransactionCategory theBenefitCategory;

    /**
     * The CharityDonation category.
     */
    private final TransactionCategory theDonateCategory;

    /**
     * The Opening category.
     */
    private final TransactionCategory theOpeningCategory;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pData the data
     * @param pAnalysis the analysis
     */
    protected QIFBuilder(final QIFFile pFile,
                         final MoneyWiseData pData,
                         final Analysis pAnalysis) {
        /* Store parameters */
        theFile = pFile;
        theFileType = pFile.getFileType();

        /* Create portfolio builder */
        thePortBuilder = new QIFPortfolioBuilder(this, pData, pAnalysis);

        /* Store Tax account */
        PayeeList myPayees = pData.getPayees();
        theTaxMan = myPayees.getSingularClass(PayeeTypeClass.TAXMAN);

        /* Store categories */
        TransactionCategoryList myCategories = pData.getTransCategories();
        theTaxCategory = myCategories.getEventInfoCategory(TransactionInfoClass.TAXCREDIT);
        theNatInsCategory = myCategories.getEventInfoCategory(TransactionInfoClass.NATINSURANCE);
        theBenefitCategory = myCategories.getEventInfoCategory(TransactionInfoClass.DEEMEDBENEFIT);
        theDonateCategory = myCategories.getEventInfoCategory(TransactionInfoClass.CHARITYDONATION);
        theOpeningCategory = myCategories.getSingularClass(TransactionCategoryClass.OPENINGBALANCE);
    }

    /**
     * Obtain the file.
     * @return the file
     */
    protected QIFFile getFile() {
        return theFile;
    }

    /**
     * Obtain the tax category.
     * @return the category
     */
    protected QIFEventCategory getTaxCategory() {
        return theFile.registerCategory(theTaxCategory);
    }

    /**
     * Obtain the tax payee.
     * @return the payee
     */
    protected QIFPayee getTaxMan() {
        return theFile.registerPayee(theTaxMan);
    }

    /**
     * Process event.
     * @param pTrans the transaction
     */
    protected void processEvent(final Transaction pTrans) {
        /* Access account and partner */
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionAsset myPartner = pTrans.getPartner();
        boolean bFrom = pTrans.getDirection().isFrom();

        /* If this deals with a payee */
        if (myPartner instanceof Payee) {
            /* If this is expense */
            if (bFrom) {
                /* Process Debit Payee */
                processDebitPayee((Payee) myPartner, myAccount, pTrans);
            } else {
                /* Process Credit Payee */
                processCreditPayee((Payee) myPartner, myAccount, pTrans);
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
     * @param pDeposit the deposit
     * @param pStartDate the start date
     * @param pBalance the opening balance
     */
    protected void processBalance(final Deposit pDeposit,
                                  final TethysDate pStartDate,
                                  final TethysMoney pBalance) {
        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pDeposit);

        /* Create the event */
        QIFEvent myEvent = new QIFEvent(theFile, pStartDate);
        myEvent.recordAmount(pBalance);

        /* If we are using self-Opening balance */
        if (theFileType.selfOpeningBalance()) {
            /* Record self reference */
            myEvent.recordAccount(myAccount.getAccount());

            /* else use an event */
        } else {
            /* Register category */
            QIFEventCategory myCategory = theFile.registerCategory(theOpeningCategory);
            myEvent.recordCategory(myCategory);
        }

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process debit payee event.
     * @param pPayee the payee
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processDebitPayee(final Payee pPayee,
                                     final TransactionAsset pCredit,
                                     final Transaction pTrans) {
        /* If this is a cash recovery */
        if ((pCredit instanceof Cash)
            && ((Cash) pCredit).isAutoExpense()) {
            /* process as cash recovery */
            processCashRecovery(pPayee, (Cash) pCredit, pTrans);

            /* If this is an income to a security */
        } else if (pCredit instanceof SecurityHolding) {
            /* process as income to security */
            thePortBuilder.processIncomeToSecurity(pPayee, (SecurityHolding) pCredit, pTrans);

            /* If this is an income to a portfolio */
        } else if (pCredit instanceof Portfolio) {
            /* process as income to portfolio */
            thePortBuilder.processIncomeToPortfolio(pPayee, (Portfolio) pCredit, pTrans);

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
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    protected void processCreditPayee(final Payee pPayee,
                                      final TransactionAsset pDebit,
                                      final Transaction pTrans) {
        /* If this is a cash payment */
        if ((pDebit instanceof Cash)
            && ((Cash) pDebit).isAutoExpense()) {
            /* process as cash payment */
            processCashPayment(pPayee, (Cash) pDebit, pTrans);

            /* If this is an expense from a security */
        } else if (pDebit instanceof SecurityHolding) {
            /* process as expense from security */
            thePortBuilder.processExpenseFromSecurity(pPayee, (SecurityHolding) pDebit, pTrans);

            /* If this is an expense from a portfolio */
        } else if (pDebit instanceof Portfolio) {
            /* process as expense from portfolio */
            thePortBuilder.processExpenseFromPortfolio(pPayee, (Portfolio) pDebit, pTrans);

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
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processTransfer(final TransactionAsset pDebit,
                                   final TransactionAsset pCredit,
                                   final Transaction pTrans) {
        /* Access details */
        TransactionCategory myCat = pTrans.getCategory();

        /* If this is a cash AutoExpense */
        if ((pCredit instanceof Cash)
            && ((Cash) pCredit).isAutoExpense()) {
            /* Process as standard expense */
            processCashExpense((Cash) pCredit, pDebit, pTrans);

            /* If this is a cash AutoReceipt */
        } else if ((pDebit instanceof Cash)
                   && ((Cash) pDebit).isAutoExpense()) {
            /* Process as standard expense */
            processCashReceipt((Cash) pDebit, pCredit, pTrans);

            /* If this is a transfer from a security */
        } else if (pDebit instanceof SecurityHolding) {
            /* Handle transfer between securities */
            if (pCredit instanceof SecurityHolding) {
                /* process as transfer between securities */

                thePortBuilder.processTransferBetweenSecurities((SecurityHolding) pDebit, (SecurityHolding) pCredit, pTrans);
            } else {
                /* process as transfer from security */
                thePortBuilder.processTransferFromSecurity((SecurityHolding) pDebit, pCredit, pTrans);
            }
            /* If this is a transfer to a security */
        } else if (pCredit instanceof SecurityHolding) {
            /* process as transfer to security */
            thePortBuilder.processTransferToSecurity((SecurityHolding) pCredit, pDebit, pTrans);

            /* If this is a transfer from a portfolio */
        } else if (pDebit instanceof Portfolio) {
            /* Handle transfer between securities */
            if (pCredit instanceof Portfolio) {
                /* process as transfer between portfolios */
                thePortBuilder.processTransferBetweenPortfolios((Portfolio) pDebit, (Portfolio) pCredit, pTrans);
            } else {
                /* process as transfer from portfolio */
                thePortBuilder.processTransferFromPortfolio((Portfolio) pDebit, pCredit, pTrans);
            }
            /* If this is a transfer to a portfolio */
        } else if (pCredit instanceof Portfolio) {
            /* process as transfer to portfolio */
            thePortBuilder.processTransferToPortfolio((Portfolio) pCredit, pDebit, pTrans);

        } else {
            /* Switch on category class */
            switch (myCat.getCategoryTypeClass()) {
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
                    processStandardIncome((Payee) pCredit.getParent(), pCredit, pTrans);
                    break;
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                    /* Process as expense to parent of the credit (recursive) */
                    processStandardExpense((Payee) pCredit.getParent(), pDebit, pTrans);
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
     * @param pTrans the transaction
     * @return true/false
     */
    protected static boolean hasXtraDetail(final Transaction pTrans) {
        if (pTrans.getTaxCredit() != null) {
            return true;
        }
        if (pTrans.getNatInsurance() != null) {
            return true;
        }
        if (pTrans.getDeemedBenefit() != null) {
            return true;
        }
        if (pTrans.getCharityDonation() != null) {
            return true;
        }
        return false;
    }

    /**
     * Process standard income.
     * @param pPayee the payee
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processStandardIncome(final Payee pPayee,
                                         final TransactionAsset pCredit,
                                         final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pCredit);

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(pTrans.getAmount());
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process detailed income.
     * @param pPayee the payee
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processDetailedIncome(final Payee pPayee,
                                         final TransactionAsset pCredit,
                                         final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);
        QIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pCredit);

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Obtain basic amount */
        TethysMoney myAmount = pTrans.getAmount();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordPayee(myPayee);
        myEvent.recordAmount(myAmount);

        /* Add Split event */
        myAmount = new TethysMoney(myAmount);
        myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

        /* Handle Tax Credit */
        TethysMoney myTaxCredit = pTrans.getTaxCredit();
        if (myTaxCredit != null) {
            /* Add to amount */
            myAmount.addAmount(myTaxCredit);
            myTaxCredit = new TethysMoney(myTaxCredit);
            myTaxCredit.negate();

            /* Access the Category details */
            QIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
        }

        /* Handle National Insurance */
        TethysMoney myNatIns = pTrans.getNatInsurance();
        if (myNatIns != null) {
            /* Add to amount */
            myAmount.addAmount(myNatIns);
            myNatIns = new TethysMoney(myNatIns);
            myNatIns.negate();

            /* Access the Category details */
            QIFEventCategory myInsCategory = theFile.registerCategory(theNatInsCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myInsCategory, myNatIns, myTaxPayee.getName());
        }

        /* Handle Deemed Benefit */
        TethysMoney myBenefit = pTrans.getDeemedBenefit();
        if (myBenefit != null) {
            /* Add to amount */
            myAmount.addAmount(myBenefit);
            myBenefit = new TethysMoney(myBenefit);
            myBenefit.negate();

            /* Access the Category details */
            QIFEventCategory myBenCategory = theFile.registerCategory(theBenefitCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myBenCategory, myBenefit, myPayee.getName());
        }

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process standard expense.
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    protected void processStandardExpense(final Payee pPayee,
                                          final TransactionAsset pDebit,
                                          final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pDebit);

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Access the amount */
        TethysMoney myAmount = new TethysMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process detailed expense.
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the expense
     */
    protected void processDetailedExpense(final Payee pPayee,
                                          final TransactionAsset pDebit,
                                          final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);
        QIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pDebit);

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Obtain basic amount */
        TethysMoney myAmount = new TethysMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordPayee(myPayee);
        myEvent.recordAmount(myAmount);

        /* Add Split event */
        myAmount = new TethysMoney(myAmount);
        myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

        /* Handle Tax Credit */
        TethysMoney myTaxCredit = pTrans.getTaxCredit();
        if (myTaxCredit != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myTaxCredit);

            /* Access the Category details */
            QIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
        }

        /* Handle National Insurance */
        TethysMoney myNatIns = pTrans.getNatInsurance();
        if (myNatIns != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myNatIns);

            /* Access the Category details */
            QIFEventCategory myInsCategory = theFile.registerCategory(theNatInsCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myInsCategory, myNatIns, myTaxPayee.getName());
        }

        /* Handle Deemed Benefit */
        TethysMoney myBenefit = pTrans.getDeemedBenefit();
        if (myBenefit != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myBenefit);

            /* Access the Category details */
            QIFEventCategory myBenCategory = theFile.registerCategory(theBenefitCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myBenCategory, myBenefit, myPayee.getName());
        }

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process standard transfer.
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processStandardTransfer(final TransactionAsset pDebit,
                                           final TransactionAsset pCredit,
                                           final Transaction pTrans) {
        /* Access details */
        TethysMoney myAmount = pTrans.getAmount();

        /* Access the Account details */
        QIFAccountEvents myDebitAccount = theFile.registerAccount(pDebit);
        QIFAccountEvents myCreditAccount = theFile.registerAccount(pCredit);

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordAccount(myDebitAccount.getAccount(), myList);

        /* Build payee description */
        myEvent.recordPayee(buildXferFromPayee(pDebit));

        /* Add event to event list */
        myCreditAccount.addEvent(myEvent);

        /* Build out amount */
        TethysMoney myOutAmount = new TethysMoney(myAmount);
        myOutAmount.negate();

        /* Create a new event */
        myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(myOutAmount);
        myEvent.recordAccount(myCreditAccount.getAccount(), myList);

        /* Build payee description */
        myEvent.recordPayee(buildXferToPayee(pCredit));

        /* Add event to event list */
        myDebitAccount.addEvent(myEvent);
    }

    /**
     * Build xferFrom payee line.
     * @param pPartner the Transfer Partner
     * @return the line
     */
    protected String buildXferFromPayee(final TransactionAsset pPartner) {
        /* Determine mode */
        boolean useSimpleTransfer = theFileType.useSimpleTransfer();

        /* Build payee description */
        StringBuilder myBuilder = new StringBuilder();
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
     * @param pPartner the Transfer Partner
     * @return the line
     */
    protected String buildXferToPayee(final TransactionAsset pPartner) {
        /* Determine mode */
        boolean useSimpleTransfer = theFileType.useSimpleTransfer();

        /* Build payee description */
        StringBuilder myBuilder = new StringBuilder();
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
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processInterest(final TransactionAsset pDebit,
                                   final TransactionAsset pCredit,
                                   final Transaction pTrans) {
        /* Access details */
        TethysMoney myAmount = pTrans.getAmount();

        /* Determine mode */
        boolean isRecursive = pDebit.equals(pCredit);
        boolean hideBalancingTransfer = theFileType.hideBalancingSplitTransfer();
        boolean hasXtraDetail = hasXtraDetail(pTrans);

        /* Access the Account details */
        QIFAccountEvents myIntAccount = theFile.registerAccount(pDebit);

        /* Access the payee */
        QIFPayee myPayee = theFile.registerPayee((Payee) pDebit.getParent());

        /* Access the category */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* If this is a simple interest */
        if (isRecursive && !hasXtraDetail) {
            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myPayee);
            myEvent.recordCategory(myCategory, myList);

            /* Add event to event list */
            myIntAccount.addEvent(myEvent);

            /* Else we need splits */
        } else {
            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

            /* Record basic details */
            myEvent.recordAmount(isRecursive
                                             ? myAmount
                                             : new TethysMoney());
            myEvent.recordPayee(myPayee);

            /* Add Split event */
            myAmount = new TethysMoney(myAmount);
            myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

            /* Handle Tax Credit */
            TethysMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                /* Access tax payee */
                QIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

                /* Add to amount */
                myAmount.addAmount(myTaxCredit);
                myTaxCredit = new TethysMoney(myTaxCredit);
                myTaxCredit.negate();

                /* Access the Category details */
                QIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

                /* Add Split event */
                myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
            }

            /* Handle Charity Donation */
            TethysMoney myDonation = pTrans.getCharityDonation();
            if (myDonation != null) {
                /* Add to amount */
                myAmount.addAmount(myDonation);
                myDonation = new TethysMoney(myDonation);
                myDonation.negate();

                /* Access the Category details */
                QIFEventCategory myDonCategory = theFile.registerCategory(theDonateCategory);

                /* Add Split event */
                myEvent.recordSplitRecord(myDonCategory, myDonation, myPayee.getName());
            }

            /* Handle Non-Recursion */
            if (!isRecursive) {
                /* Add to amount */
                TethysMoney myOutAmount = new TethysMoney(pTrans.getAmount());
                myOutAmount.negate();

                /* Access the Account details */
                QIFAccountEvents myAccount = theFile.registerAccount(pCredit);

                /* Add Split event */
                myEvent.recordSplitRecord(myAccount.getAccount(), myOutAmount, null);
            }

            /* Add event to event list */
            myIntAccount.addEvent(myEvent);
        }

        /* If we need a balancing transfer */
        if (!isRecursive && !hideBalancingTransfer) {
            /* Access the Account details */
            QIFAccountEvents myAccount = theFile.registerAccount(pCredit);

            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

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
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processCashBack(final TransactionAsset pDebit,
                                   final TransactionAsset pCredit,
                                   final Transaction pTrans) {
        /* Access details */
        TethysMoney myAmount = pTrans.getAmount();

        /* Determine mode */
        boolean isRecursive = pDebit.equals(pCredit);
        boolean hideBalancingTransfer = theFileType.hideBalancingSplitTransfer();

        /* Access the Account details */
        QIFAccountEvents myBaseAccount = theFile.registerAccount(pDebit);

        /* Access the payee */
        QIFPayee myPayee = theFile.registerPayee((Payee) pDebit.getParent());

        /* Access the category */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* If this is a simple cashBack */
        if (isRecursive) {
            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myPayee);
            myEvent.recordCategory(myCategory, myList);

            /* Add event to event list */
            myBaseAccount.addEvent(myEvent);

            /* Else we need splits */
        } else {
            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

            /* Record basic details */
            myEvent.recordAmount(new TethysMoney());
            myEvent.recordPayee(myPayee);

            /* Add Split event */
            myAmount = new TethysMoney(myAmount);
            myEvent.recordSplitRecord(myCategory, myList, myAmount, myPayee.getName());

            /* Add to amount */
            TethysMoney myOutAmount = new TethysMoney(pTrans.getAmount());
            myOutAmount.negate();

            /* Access the Account details */
            QIFAccountEvents myAccount = theFile.registerAccount(pCredit);

            /* Add Split event */
            myEvent.recordSplitRecord(myAccount.getAccount(), myOutAmount, null);

            /* Add event to event list */
            myBaseAccount.addEvent(myEvent);
        }

        /* If we need a balancing transfer */
        if (!isRecursive && !hideBalancingTransfer) {
            /* Access the Account details */
            QIFAccountEvents myAccount = theFile.registerAccount(pCredit);

            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

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
     * @param pPayee the payee
     * @param pCash the cash account
     * @param pTrans the transaction
     */
    protected void processCashRecovery(final Payee pPayee,
                                       final Cash pCash,
                                       final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        QIFEventCategory myAutoCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pCash);

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Access the amount */
        TethysMoney myInAmount = pTrans.getAmount();
        TethysMoney myOutAmount = new TethysMoney(myInAmount);
        myOutAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(new TethysMoney());
        myEvent.recordPayee(myPayee);
        myEvent.recordSplitRecord(myCategory, myList, myInAmount, myPayee.getName());
        myEvent.recordSplitRecord(myAutoCategory, myList, myOutAmount, pCash.getAutoPayee().getName());

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash payment.
     * @param pPayee the payee
     * @param pCash the cash account
     * @param pTrans the transaction
     */
    protected void processCashPayment(final Payee pPayee,
                                      final Cash pCash,
                                      final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());
        QIFEventCategory myAutoCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pCash);

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Access the amount */
        TethysMoney myInAmount = pTrans.getAmount();
        TethysMoney myOutAmount = new TethysMoney(myInAmount);
        myOutAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(new TethysMoney());
        myEvent.recordPayee(myPayee);
        myEvent.recordSplitRecord(myAutoCategory, myList, myInAmount, pCash.getAutoPayee().getName());
        myEvent.recordSplitRecord(myCategory, myList, myOutAmount, myPayee.getName());

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash expense.
     * @param pCash the cash account
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    protected void processCashExpense(final Cash pCash,
                                      final TransactionAsset pDebit,
                                      final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pCash.getAutoPayee());

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pDebit);

        /* Access the amount */
        TethysMoney myAmount = new TethysMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash receipt.
     * @param pCash the cash account
     * @param pCredit the credit account
     * @param pTrans the transaction
     */
    protected void processCashReceipt(final Cash pCash,
                                      final TransactionAsset pCredit,
                                      final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pCash.getAutoPayee());

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pCredit);

        /* Obtain classes */
        List<QIFClass> myList = getTransactionClasses(pTrans);

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(pTrans.getAmount());
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory, myList);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Obtain classes for transaction.
     * @param pTrans the transaction
     * @return the class list (or null)
     */
    protected List<QIFClass> getTransactionClasses(final Transaction pTrans) {
        /* Create return value */
        List<QIFClass> myList = null;

        /* Obtain the iterator for the transaction */
        Iterator<TransactionInfo> myIterator = pTrans.tagIterator();

        /* If we have tags */
        if (myIterator != null) {
            /* Allocate the list */
            myList = new ArrayList<>();

            /* Loop through the classes */
            while (myIterator.hasNext()) {
                TransactionInfo myInfo = myIterator.next();

                /* Access the transaction tag */
                TransactionTag myTag = myInfo.getTransactionTag();
                QIFClass myClass = theFile.registerClass(myTag);

                /* Add to the list */
                myList.add(myClass);
            }
        }

        /* Return the list */
        return myList;
    }
}
