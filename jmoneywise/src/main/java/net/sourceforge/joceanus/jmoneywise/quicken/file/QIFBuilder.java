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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.Iterator;

import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

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
     * Constructor.
     * @param pFile the QIF File
     * @param pView the view
     */
    protected QIFBuilder(final QIFFile pFile,
                         final View pView) {
        /* Store parameters */
        theFile = pFile;
        theFileType = pFile.getFileType();

        /* Access the data */
        MoneyWiseData myData = pView.getData();

        /* Create portfolio builder */
        thePortBuilder = new QIFPortfolioBuilder(this, pView);

        /* Store Tax account */
        PayeeList myPayees = myData.getPayees();
        theTaxMan = myPayees.getSingularClass(PayeeTypeClass.TAXMAN);

        /* Store categories */
        TransactionCategoryList myCategories = myData.getTransCategories();
        theTaxCategory = myCategories.getEventInfoCategory(TransactionInfoClass.TAXCREDIT);
        theNatInsCategory = myCategories.getEventInfoCategory(TransactionInfoClass.NATINSURANCE);
        theBenefitCategory = myCategories.getEventInfoCategory(TransactionInfoClass.DEEMEDBENEFIT);
        theDonateCategory = myCategories.getEventInfoCategory(TransactionInfoClass.CHARITYDONATION);
        theOpeningCategory = myCategories.getSingularClass(TransactionCategoryClass.INHERITED);
    }

    /**
     * Process event.
     * @param pTrans the transaction
     */
    protected void processEvent(final Transaction pTrans) {
        /* If the event is split */
        if (pTrans.isSplit()) {
            /* Ignore if this is a child transaction */
            if (pTrans.isChild()) {
                return;
            }

            /* Process parent */
            processSingleEvent(pTrans);

            /* Loop through the children */
            Iterator<Transaction> myIterator = pTrans.childIterator();
            while (myIterator.hasNext()) {
                Transaction myChild = myIterator.next();

                /* Process the child */
                processSingleEvent(myChild);
            }

            /* Else handle normally */
        } else {
            /* process the event */
            processSingleEvent(pTrans);
        }
    }

    /**
     * Process event.
     * @param pTrans the transaction
     */
    protected void processSingleEvent(final Transaction pTrans) {
        /* Access debit and credit */
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();

        /* If this is income from a payee */
        if (myDebit instanceof Payee) {
            /* Process Debit Payee */
            processDebitPayee((Payee) myDebit, pTrans);

            /* If this is expense to a payee */
        } else if (myCredit instanceof Payee) {
            /* Process Credit Payee */
            processCreditPayee((Payee) myCredit, pTrans);

        } else {
            /* else process Transfer */
            processTransfer(pTrans);
        }
    }

    /**
     * Process opening balance.
     * @param pDeposit the deposit
     * @param pStartDate the start date
     * @param pBalance the opening balance
     */
    protected void processBalance(final Deposit pDeposit,
                                  final JDateDay pStartDate,
                                  final JMoney pBalance) {
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
     * @param pTrans the transaction
     */
    protected void processDebitPayee(final Payee pPayee,
                                     final Transaction pTrans) {
        /* Access credit */
        AssetBase<?> myCredit = pTrans.getCredit();

        /* If this is a cash recovery */
        if ((myCredit instanceof Cash)
            && ((Cash) myCredit).isAutoExpense()) {
            /* process as cash recovery */
            processCashRecovery(pPayee, (Cash) myCredit, pTrans);

            /* If this is a income to a security */
        } else if (myCredit instanceof Security) {
            /* process as income to security */
            thePortBuilder.processIncomeToSecurity(pPayee, (Security) myCredit, pTrans);

            /* else if we have additional detail */
        } else if (hasXtraDetail(pTrans)) {
            /* process as detailed income */
            processDetailedIncome(pPayee, pTrans);

        } else {
            /* process as standard income */
            processStandardIncome(pPayee, pTrans);
        }
    }

    /**
     * Process credit payee event.
     * @param pPayee the payee
     * @param pTrans the transaction
     */
    protected void processCreditPayee(final Payee pPayee,
                                      final Transaction pTrans) {
        /* Access debit */
        AssetBase<?> myDebit = pTrans.getDebit();

        /* If this is a cash payment */
        if ((myDebit instanceof Cash)
            && ((Cash) myDebit).isAutoExpense()) {
            /* process as cash payment */
            processCashPayment(pPayee, (Cash) myDebit, pTrans);

            /* If this is a income to a security */
        } else if (myDebit instanceof Security) {
            /* process as expense from security */
            thePortBuilder.processExpenseFromSecurity(pPayee, (Security) myDebit, pTrans);

            /* else if we have additional detail */
        } else if (hasXtraDetail(pTrans)) {
            /* process as detailed income */
            processDetailedExpense(pPayee, pTrans);

        } else {
            /* process as standard expense */
            processStandardExpense(pPayee, pTrans);
        }
    }

    /**
     * Process transfer event.
     * @param pTrans the transaction
     */
    protected void processTransfer(final Transaction pTrans) {
        /* Access details */
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        TransactionCategory myCat = pTrans.getCategory();

        /* If this is a cash AutoExpense */
        if ((myCredit instanceof Cash)
            && ((Cash) myCredit).isAutoExpense()) {
            /* Process as standard expense */
            processCashExpense((Cash) myCredit, pTrans);

            /* If this is a cash AutoReceipt */
        } else if ((myDebit instanceof Cash)
                   && ((Cash) myDebit).isAutoExpense()) {
            /* Process as standard expense */
            processCashReceipt((Cash) myDebit, pTrans);

            /* If this is a transfer from a security */
        } else if (myDebit instanceof Security) {
            /* Handle transfer between securities */
            if (myCredit instanceof Security) {
                /* process as transfer between securities */
                thePortBuilder.processTransferBetweenSecurities((Security) myDebit, (Security) myCredit, pTrans);
            } else {
                /* process as transfer from security */
                thePortBuilder.processTransferFromSecurity((Security) myDebit, pTrans);
            }
            /* If this is a transfer to a security */
        } else if (myCredit instanceof Security) {
            /* process as transfer to security */
            thePortBuilder.processTransferToSecurity((Security) myCredit, pTrans);

        } else {
            /* Switch on category class */
            switch (myCat.getCategoryTypeClass()) {
                case INTEREST:
                    /* Process as interest payment */
                    processInterest(pTrans);
                    break;
                case LOANINTERESTEARNED:
                case RENTALINCOME:
                case ROOMRENTALINCOME:
                    /* Process as income from parent of the credit */
                    processStandardIncome((Payee) myCredit.getParent(), pTrans);
                    break;
                case WRITEOFF:
                case LOANINTERESTCHARGED:
                    /* Process as expense to parent of the credit (recursive) */
                    processStandardExpense((Payee) myCredit.getParent(), pTrans);
                    break;
                default:
                    /* Process as standard expense */
                    processStandardTransfer(pTrans);
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
     * @param pTrans the transaction
     */
    protected void processStandardIncome(final Payee pPayee,
                                         final Transaction pTrans) {
        /* Access credit */
        AssetBase<?> myCredit = pTrans.getCredit();

        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(myCredit);

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(pTrans.getAmount());
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process detailed income.
     * @param pPayee the payee
     * @param pTrans the transaction
     */
    protected void processDetailedIncome(final Payee pPayee,
                                         final Transaction pTrans) {
        /* Access credit */
        AssetBase<?> myCredit = pTrans.getCredit();

        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);
        QIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(myCredit);

        /* Obtain basic amount */
        JMoney myAmount = pTrans.getAmount();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordPayee(myPayee);
        myEvent.recordAmount(myAmount);

        /* Add Split event */
        myAmount = new JMoney(myAmount);
        myEvent.recordSplitRecord(myCategory, myAmount, myPayee.getName());

        /* Handle Tax Credit */
        JMoney myTaxCredit = pTrans.getTaxCredit();
        if (myTaxCredit != null) {
            /* Add to amount */
            myAmount.addAmount(myTaxCredit);
            myTaxCredit = new JMoney(myTaxCredit);
            myTaxCredit.negate();

            /* Access the Category details */
            QIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
        }

        /* Handle National Insurance */
        JMoney myNatIns = pTrans.getNatInsurance();
        if (myNatIns != null) {
            /* Add to amount */
            myAmount.addAmount(myNatIns);
            myNatIns = new JMoney(myNatIns);
            myNatIns.negate();

            /* Access the Category details */
            QIFEventCategory myInsCategory = theFile.registerCategory(theNatInsCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myInsCategory, myNatIns, myTaxPayee.getName());
        }

        /* Handle Deemed Benefit */
        JMoney myBenefit = pTrans.getDeemedBenefit();
        if (myBenefit != null) {
            /* Add to amount */
            myAmount.addAmount(myBenefit);
            myBenefit = new JMoney(myBenefit);
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
     * @param pTrans the transaction
     */
    protected void processStandardExpense(final Payee pPayee,
                                          final Transaction pTrans) {
        /* Access debit */
        AssetBase<?> myDebit = pTrans.getDebit();

        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(myDebit);

        /* Access the amount */
        JMoney myAmount = new JMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process detailed expense.
     * @param pPayee the payee
     * @param pTrans the expense
     */
    protected void processDetailedExpense(final Payee pPayee,
                                          final Transaction pTrans) {
        /* Access credit */
        AssetBase<?> myDebit = pTrans.getDebit();

        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pPayee);
        QIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(myDebit);

        /* Obtain basic amount */
        JMoney myAmount = new JMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordPayee(myPayee);
        myEvent.recordAmount(myAmount);

        /* Add Split event */
        myAmount = new JMoney(myAmount);
        myEvent.recordSplitRecord(myCategory, myAmount, myPayee.getName());

        /* Handle Tax Credit */
        JMoney myTaxCredit = pTrans.getTaxCredit();
        if (myTaxCredit != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myTaxCredit);

            /* Access the Category details */
            QIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
        }

        /* Handle National Insurance */
        JMoney myNatIns = pTrans.getNatInsurance();
        if (myNatIns != null) {
            /* Subtract from amount */
            myAmount.subtractAmount(myNatIns);

            /* Access the Category details */
            QIFEventCategory myInsCategory = theFile.registerCategory(theNatInsCategory);

            /* Add Split event */
            myEvent.recordSplitRecord(myInsCategory, myNatIns, myTaxPayee.getName());
        }

        /* Handle Deemed Benefit */
        JMoney myBenefit = pTrans.getDeemedBenefit();
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
     * @param pTrans the transaction
     */
    protected void processStandardTransfer(final Transaction pTrans) {
        /* Access details */
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        JMoney myAmount = pTrans.getAmount();

        /* Access the Account details */
        QIFAccountEvents myDebitAccount = theFile.registerAccount(myDebit);
        QIFAccountEvents myCreditAccount = theFile.registerAccount(myCredit);

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordAccount(myDebitAccount.getAccount());

        /* Build payee description */
        myEvent.recordPayee(buildXferFromPayee(myDebit));

        /* Add event to event list */
        myCreditAccount.addEvent(myEvent);

        /* Build out amount */
        JMoney myOutAmount = new JMoney(myAmount);
        myOutAmount.negate();

        /* Create a new event */
        myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(myOutAmount);
        myEvent.recordAccount(myCreditAccount.getAccount());

        /* Build payee description */
        myEvent.recordPayee(buildXferToPayee(myCredit));

        /* Add event to event list */
        myDebitAccount.addEvent(myEvent);
    }

    /**
     * Build xferFrom payee line.
     * @param pPartner the Transfer Partner
     * @return the line
     */
    protected String buildXferFromPayee(final AssetBase<?> pPartner) {
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
    protected String buildXferToPayee(final AssetBase<?> pPartner) {
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
     * @param pTrans the transaction
     */
    protected void processInterest(final Transaction pTrans) {
        /* Access details */
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();
        JMoney myAmount = pTrans.getAmount();

        /* Determine mode */
        boolean isRecursive = myDebit.equals(myCredit);
        boolean hideBalancingTransfer = theFileType.hideBalancingSplitTransfer();
        boolean hasXtraDetail = hasXtraDetail(pTrans);

        /* Access the Account details */
        QIFAccountEvents myIntAccount = theFile.registerAccount(myDebit);

        /* Access the payee */
        QIFPayee myPayee = theFile.registerPayee((Payee) myDebit.getParent());

        /* Access the category */
        QIFEventCategory myCategory = theFile.registerCategory(pTrans.getCategory());

        /* If this is a simple interest */
        if (isRecursive && !hasXtraDetail) {
            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(myAmount);
            myEvent.recordPayee(myPayee);
            myEvent.recordCategory(myCategory);

            /* Add event to event list */
            myIntAccount.addEvent(myEvent);

            /* Else we need splits */
        } else {
            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

            /* Record basic details */
            myEvent.recordAmount(isRecursive
                                            ? myAmount
                                            : new JMoney());
            myEvent.recordPayee(myPayee);

            /* Add Split event */
            myAmount = new JMoney(myAmount);
            myEvent.recordSplitRecord(myCategory, myAmount, myPayee.getName());

            /* Handle Tax Credit */
            JMoney myTaxCredit = pTrans.getTaxCredit();
            if (myTaxCredit != null) {
                /* Access tax payee */
                QIFPayee myTaxPayee = theFile.registerPayee(theTaxMan);

                /* Add to amount */
                myAmount.addAmount(myTaxCredit);
                myTaxCredit = new JMoney(myTaxCredit);
                myTaxCredit.negate();

                /* Access the Category details */
                QIFEventCategory myTaxCategory = theFile.registerCategory(theTaxCategory);

                /* Add Split event */
                myEvent.recordSplitRecord(myTaxCategory, myTaxCredit, myTaxPayee.getName());
            }

            /* Handle Charity Donation */
            JMoney myDonation = pTrans.getCharityDonation();
            if (myDonation != null) {
                /* Add to amount */
                myAmount.addAmount(myDonation);
                myDonation = new JMoney(myDonation);
                myDonation.negate();

                /* Access the Category details */
                QIFEventCategory myDonCategory = theFile.registerCategory(theDonateCategory);

                /* Add Split event */
                myEvent.recordSplitRecord(myDonCategory, myDonation, myPayee.getName());
            }

            /* Handle Non-Recursion */
            if (!isRecursive) {
                /* Add to amount */
                JMoney myOutAmount = new JMoney(pTrans.getAmount());
                myOutAmount.negate();

                /* Access the Account details */
                QIFAccountEvents myAccount = theFile.registerAccount(myCredit);

                /* Add Split event */
                myEvent.recordSplitRecord(myAccount.getAccount(), myOutAmount, null);
            }

            /* Add event to event list */
            myIntAccount.addEvent(myEvent);
        }

        /* If we need a balancing transfer */
        if (!isRecursive && !hideBalancingTransfer) {
            /* Access the Account details */
            QIFAccountEvents myAccount = theFile.registerAccount(myCredit);

            /* Create a new event */
            QIFEvent myEvent = new QIFEvent(theFile, pTrans);

            /* Build simple event and add it */
            myEvent.recordAmount(pTrans.getAmount());
            myEvent.recordAccount(myIntAccount.getAccount());

            /* Build payee description */
            myEvent.recordPayee(buildXferFromPayee(myDebit));

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

        /* Access the amount */
        JMoney myInAmount = pTrans.getAmount();
        JMoney myOutAmount = new JMoney(myInAmount);
        myOutAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(new JMoney());
        myEvent.recordPayee(myPayee);
        myEvent.recordSplitRecord(myCategory, myInAmount, myPayee.getName());
        myEvent.recordSplitRecord(myAutoCategory, myOutAmount, pCash.getAutoPayee().getName());

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

        /* Access the amount */
        JMoney myInAmount = pTrans.getAmount();
        JMoney myOutAmount = new JMoney(myInAmount);
        myOutAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(new JMoney());
        myEvent.recordPayee(myPayee);
        myEvent.recordSplitRecord(myAutoCategory, myInAmount, pCash.getAutoPayee().getName());
        myEvent.recordSplitRecord(myCategory, myOutAmount, myPayee.getName());

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash expense.
     * @param pCash the cash account
     * @param pTrans the transaction
     */
    protected void processCashExpense(final Cash pCash,
                                      final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pCash.getAutoPayee());

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pTrans.getDebit());

        /* Access the amount */
        JMoney myAmount = new JMoney(pTrans.getAmount());
        myAmount.negate();

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(myAmount);
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process cash receipt.
     * @param pCash the cash account
     * @param pTrans the transaction
     */
    protected void processCashReceipt(final Cash pCash,
                                      final Transaction pTrans) {
        /* Access the Payee details */
        QIFPayee myPayee = theFile.registerPayee(pCash.getAutoPayee());

        /* Access the Category details */
        QIFEventCategory myCategory = theFile.registerCategory(pCash.getAutoExpense());

        /* Access the Account details */
        QIFAccountEvents myAccount = theFile.registerAccount(pTrans.getCredit());

        /* Create a new event */
        QIFEvent myEvent = new QIFEvent(theFile, pTrans);
        myEvent.recordAmount(pTrans.getAmount());
        myEvent.recordPayee(myPayee);
        myEvent.recordCategory(myCategory);

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }
}
