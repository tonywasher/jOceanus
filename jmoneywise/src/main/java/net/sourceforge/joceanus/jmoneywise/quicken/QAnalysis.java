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
package net.sourceforge.joceanus.jmoneywise.quicken;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.quicken.QCategory.QCategoryList;
import net.sourceforge.joceanus.jmoneywise.quicken.QClass.QClassList;
import net.sourceforge.joceanus.jmoneywise.quicken.QSecurity.QSecurityList;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFFile;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Quicken analysis.
 */
public class QAnalysis
        extends QElement {
    /**
     * Set option.
     */
    protected static final String QIF_SETOPT = "!Option:";

    /**
     * Clear option.
     */
    protected static final String QIF_CLROPT = "!Clear:";

    /**
     * AutoSwitch option.
     */
    protected static final String QIF_AUTOSWITCH = "AutoSwitch";

    /**
     * Number of analysis stages.
     */
    protected static final int QIF_NUMLISTS = 3;

    /**
     * Number of output stages.
     */
    protected static final int QIF_NUMOUTS = 6;

    /**
     * Class List.
     */
    private final QClassList theClasses;

    /**
     * Account Map.
     */
    private final Map<AssetBase<?>, QAccount> theAccounts;

    /**
     * Category List.
     */
    private final QCategoryList theCategories;

    /**
     * Security List.
     */
    private final QSecurityList theSecurities;

    /**
     * Number of events.
     */
    private int theNumEvents = 0;

    /**
     * Start date.
     */
    private JDateDay theStartDate = null;

    /**
     * DataSet.
     */
    private MoneyWiseData theData = null;

    /**
     * Analysis.
     */
    private Analysis theAnalysis = null;

    /**
     * Obtain account list iterator.
     * @return the iterator
     */
    protected Iterator<QAccount> getAccountIterator() {
        return theAccounts.values().iterator();
    }

    /**
     * Obtain start date.
     * @return the start date
     */
    protected JDateDay getStartDate() {
        return theStartDate;
    }

    /**
     * Obtain data.
     * @return the data
     */
    protected MoneyWiseData getDataSet() {
        return theData;
    }

    /**
     * Obtain class iterator.
     * @return the iterator
     */
    public Iterator<QClass> classIterator() {
        return theClasses.classIterator();
    }

    /**
     * Obtain categories iterator.
     * @return the iterator
     */
    public Iterator<QCategory> categoryIterator() {
        return theCategories.categoryIterator();
    }

    /**
     * Obtain accounts iterator.
     * @return the iterator
     */
    public Iterator<QAccount> accountIterator() {
        return theAccounts.values().iterator();
    }

    /**
     * Obtain securities iterator.
     * @return the iterator
     */
    public Iterator<QSecurity> securityIterator() {
        return theSecurities.securityIterator();
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @param pType the QIF file type
     */
    protected QAnalysis(final JDataFormatter pFormatter,
                        final QIFType pType) {
        /* Call the super-constructor */
        super(pFormatter, pType);

        /* Create the maps */
        theClasses = new QClassList(this);
        theAccounts = new LinkedHashMap<AssetBase<?>, QAccount>();
        theCategories = new QCategoryList(this);
        theSecurities = new QSecurityList(this);
    }

    /**
     * Access interest category.
     * @param pTrans the transaction
     * @return the category
     */
    protected TransactionCategory getInterestCategory(final Transaction pTrans) {
        /* No change needed if there is a tax credit */
        if (pTrans.getTaxCredit() != null) {
            return pTrans.getCategory();
        }

        /* Access category */
        TransactionCategoryList myList = theData.getTransCategories();
        TransactionCategory myCategory = myList.getSingularClass(TransactionCategoryClass.TAXFREEINTEREST);

        /* Register category and return */
        theCategories.registerCategory(myCategory);
        return myCategory;
    }

    /**
     * Access EventCategory for info class.
     * @param pInfoClass the info class
     * @return the category
     */
    protected TransactionCategory getCategory(final TransactionInfoClass pInfoClass) {
        /* Access category */
        TransactionCategoryList myList = theData.getTransCategories();
        TransactionCategory myCategory = myList.getEventInfoCategory(pInfoClass);

        /* Register category and return */
        theCategories.registerCategory(myCategory);
        return myCategory;
    }

    /**
     * Access EventCategory for explicit class.
     * @param pEventClass the event class
     * @return the category
     */
    protected TransactionCategory getCategory(final TransactionCategoryClass pEventClass) {
        /* Access category */
        TransactionCategoryList myList = theData.getTransCategories();
        TransactionCategory myCategory = myList.getSingularClass(pEventClass);

        /* Register category and return */
        theCategories.registerCategory(myCategory);
        return myCategory;
    }

    /**
     * Access account.
     * @param pAccount the account
     * @param pPortfolio the portfolio
     * @return the QIF account
     */
    protected QAccount getAccount(final AssetBase<?> pAccount,
                                  final Portfolio pPortfolio) {
        /* Note which account we are using */
        AssetBase<?> myLookup = pAccount;

        /* If the account is a security */
        if (pAccount instanceof Security) {
            /* Register the security */
            theSecurities.registerSecurity((Security) pAccount);

            /* We need to use the portfolio */
            myLookup = pPortfolio;

            /* else ignore if the account is a non-Asset */
        } else if (pAccount instanceof Payee) {
            return null;
        }

        /* Look up the account in the map */
        QAccount myAccount = theAccounts.get(myLookup);

        /* If this is a new account */
        if (myAccount == null) {
            /* Allocate the account and add to the map */
            myAccount = new QAccount(this, myLookup);
            theAccounts.put(myLookup, myAccount);

            /* If the account is an autoExpense */
            TransactionCategory myCategory = myAccount.getAutoExpense();
            if (myCategory != null) {
                /* Make sure that it is registered */
                theCategories.registerCategory(myCategory);
            }
        }

        /* Return the account */
        return myAccount;
    }

    /**
     * Analyse the data.
     * @param pStatus the thread status
     * @param pView the dataView
     * @param pLastEvent the date of the last event to process
     */
    protected void analyseData(final ThreadStatus<MoneyWiseData, MoneyWiseDataType> pStatus,
                               final View pView,
                               final JDateDay pLastEvent) {
        /* Store data and analysis */
        theData = pView.getData();
        AnalysisManager myManager = pView.getAnalysisManager();
        theAnalysis = myManager.getAnalysis(pLastEvent);

        /* Access lists */
        TransactionList myTransactions = theData.getTransactions();
        DepositList myDeposits = theData.getDeposits();
        TaxYearList myTaxYears = theData.getTaxYears();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Determine start date */
        TaxYear myTaxYear = myTaxYears.get(0);
        theStartDate = myTaxYear.getDateRange().getStart();

        /* Update status bar */
        boolean bContinue = pStatus.setNumStages(QIF_NUMLISTS)
                            && pStatus.setNewStage("Analysing deposits")
                            && pStatus.setNumSteps(myDeposits.size());

        /* Loop through the deposits */
        Iterator<Deposit> myDepIterator = myDeposits.iterator();
        while ((bContinue) && (myDepIterator.hasNext())) {
            Deposit myDeposit = myDepIterator.next();

            /* Ignore deleted deposits */
            if (myDeposit.isDeleted()) {
                continue;
            }

            /* If we have an opening balance */
            JMoney myBalance = myDeposit.getOpeningBalance();
            if (myBalance != null) {
                /* Make sure that it is registered */
                getAccount(myDeposit, null);
            }

            /* Report the progress */
            myCount++;
            if (((myCount % mySteps) == 0) && (!pStatus.setStepsDone(myCount))) {
                bContinue = false;
            }
        }

        /* Update status bar */
        if (bContinue) {
            bContinue = ((pStatus.setNewStage("Analysing transactions"))
                    && (pStatus.setNumSteps(myTransactions.size())));
        }

        /* Loop through the transactions */
        myCount = 0;
        Iterator<Transaction> myIterator = myTransactions.iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            Transaction myTrans = myIterator.next();

            /* If the transaction is too late */
            if (pLastEvent.compareTo(myTrans.getDate()) < 0) {
                /* Break the loop */
                break;
            }

            /* Ignore deleted transactions */
            if (myTrans.isDeleted()) {
                continue;
            }

            /* Access key details */
            AssetBase<?> myDebit = myTrans.getDebit();
            AssetBase<?> myCredit = myTrans.getCredit();
            TransactionCategory myCategory = myTrans.getCategory();

            /* If the category is not a transfer */
            if (!myCategory.isTransfer()) {
                /* Make sure that it is registered */
                theCategories.registerCategory(myCategory);
            }

            /* Process event for debit and credit (avoiding case where debit == credit) */
            processTransaction(myTrans, myDebit, false);
            if (!myDebit.equals(myCredit)) {
                processTransaction(myTrans, myCredit, true);
            }

            /* Report the progress */
            myCount++;
            if (((myCount % mySteps) == 0) && (!pStatus.setStepsDone(myCount))) {
                bContinue = false;
            }
        }

        /* Update status bar and analyse prices */
        SecurityPriceList myPrices = theData.getSecurityPrices();
        if ((bContinue) && (pStatus.setNewStage("Analysing prices")) && (pStatus.setNumSteps(myPrices.size()))) {
            /* Analyse prices for securities */
            theSecurities.buildPrices(pStatus, myPrices, pLastEvent);
        }
    }

    /**
     * Process the transaction.
     * @param pTrans the transaction
     * @param pAccount the account
     * @param isCredit is this the credit item?
     */
    private void processTransaction(final Transaction pTrans,
                                    final AssetBase<?> pAccount,
                                    final boolean isCredit) {
        /* Access account */
        Portfolio myPortfolio = pTrans.getPortfolio();
        QAccount myAccount = getAccount(pAccount, myPortfolio);

        /* If we should process the account */
        if (myAccount != null) {
            /* If the account is autoExpense */
            if (myAccount.isAutoExpense()) {
                /* Handle non transfer specially, otherwise ignore */
                if (!pTrans.getCategory().isTransfer()) {
                    /* Process the transaction */
                    myAccount.processTransaction(pTrans, isCredit);
                    theNumEvents += 2;
                }

                /* If we should not ignore this transaction */
            } else if (!ignoreTransaction(pTrans, isCredit)) {
                /* Process the event */
                myAccount.processTransaction(pTrans, isCredit);
                theNumEvents++;
            }
        }
    }

    /**
     * Determine whether we should ignore the event.
     * @param pTrans the transaction
     * @param isCredit is this the credit item?
     * @return true/false
     */
    private boolean ignoreTransaction(final Transaction pTrans,
                                      final boolean isCredit) {
        /* Switch on category */
        QIFType myType = getQIFType();
        AssetBase<?> mySource = pTrans.getDebit();
        AssetBase<?> myTarget = pTrans.getCredit();

        /* If this is a debit event */
        if (!isCredit) {
            switch (pTrans.getCategoryClass()) {
                case TRANSFER:
                    /* Transfer from Money to Units */
                    if ((!(mySource instanceof Security))
                        && (myTarget instanceof Security)) {
                        /* Needs a debit line if we cannot use BuyX */
                        return myType.canXferPortfolioLinked();
                    }
                    /* All other elements need a debit line */
                    return false;

                default:
                    return false;
            }
        }

        switch (pTrans.getCategoryClass()) {
            case INTEREST:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
                return true;
            case DIVIDEND:
                return mySource instanceof Security;
            case TRANSFER:
                /* Transfer from Units to Money */
                if ((mySource instanceof Security)
                    && !(myTarget instanceof Security)) {
                    /* Needs a credit line if we cannot use SellX */
                    return myType.canXferPortfolioLinked();
                }

                /* All other elements need a credit line */
                return false;
            default:
                return false;
        }
    }

    /**
     * Output data.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return success true/false
     * @throws IOException on error
     */
    protected boolean outputData(final ThreadStatus<MoneyWiseData, MoneyWiseDataType> pStatus,
                                 final OutputStreamWriter pStream) throws IOException {
        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = pStatus.setNumStages(QIF_NUMOUTS);

        /* If we should continue */
        if (bContinue) {
            /* Output the classes */
            bContinue = theClasses.outputClasses(pStatus, pStream);
        }

        /* If we should continue */
        if (bContinue) {
            /* Output the categories */
            bContinue = theCategories.outputCategories(pStatus, pStream);
        }

        /* If we should continue */
        if (bContinue) {
            /* Reset the builder */
            reset();

            /* Set AutoSwitch */
            append(QIF_SETOPT);
            append(QIF_AUTOSWITCH);
            endLine();

            /* Add the Item type */
            append(QAccount.QIF_ITEM);
            endLine();

            /* Write Accounts header */
            pStream.write(getBufferedString());

            /* Update status bar */
            bContinue = ((pStatus.setNewStage("Writing accounts")) && (pStatus.setNumSteps(theAccounts.size())));
        }

        /* Loop through the accounts */
        Iterator<QAccount> myIterator = theAccounts.values().iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            QAccount myAccount = myIterator.next();

            /* If the account is active */
            if (myAccount.isActive()) {
                /* Write Account details */
                pStream.write(myAccount.buildQIF());
            }

            /* Report the progress */
            myCount++;
            if (((myCount % mySteps) == 0) && (!pStatus.setStepsDone(myCount))) {
                bContinue = false;
            }
        }

        /* If we should continue */
        if (bContinue) {
            /* Output the securities */
            bContinue = theSecurities.outputSecurities(pStatus, pStream);
        }

        /* If we should continue */
        if (bContinue) {
            /* Update status bar */
            bContinue = ((pStatus.setNewStage("Writing events")) && (pStatus.setNumSteps(theNumEvents)));
        }

        /* Loop through the accounts */
        myCount = 0;
        myIterator = theAccounts.values().iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            QAccount myAccount = myIterator.next();

            /* Output events */
            myAccount.outputEvents(pStream, theStartDate);

            /* Report the progress */
            myCount++;
            if (((myCount % mySteps) == 0) && (!pStatus.setStepsDone(myCount))) {
                bContinue = false;
            }
        }

        /* If we should continue */
        if (bContinue) {
            /* Output the prices */
            bContinue = theSecurities.outputPrices(pStatus, pStream);
        }

        /* Return success */
        return bContinue;
    }

    /**
     * Obtain SecurityBucket for Security.
     * @param pPortfolio the portfolio
     * @param pSecurity the security
     * @return the bucket
     */
    protected SecurityBucket getSecurityBucket(final Portfolio pPortfolio,
                                               final AssetBase<?> pSecurity) {
        /* Locate the security bucket */
        return theAnalysis.getPortfolios().getBucket(pPortfolio, pSecurity);
    }

    /**
     * Build QIF File from list.
     * @return the QIF File
     */
    protected QIFFile buildQIFFile() {
        /* Create new QIF File */
        QIFFile myFile = new QIFFile();

        /* Loop through the classes */
        Iterator<QClass> myClassIterator = classIterator();
        while (myClassIterator.hasNext()) {
            QClass myClass = myClassIterator.next();

            /* Register Class details */
            myFile.registerClass(myClass.getEventClass());
        }

        /* Loop through the categories */
        Iterator<QCategory> myCatIterator = categoryIterator();
        while (myCatIterator.hasNext()) {
            QCategory myCategory = myCatIterator.next();

            /* Register Category details */
            myFile.registerCategory(myCategory.getCategory());
        }

        /* Loop through the parents */
        Iterator<QAccount> myIterator = accountIterator();
        while (myIterator.hasNext()) {
            QAccount myAccount = myIterator.next();

            /* Register Account details */
            myFile.registerAccount(myAccount.getAccount());
        }

        /* Loop through the securities */
        Iterator<QSecurity> mySecIterator = securityIterator();
        while (mySecIterator.hasNext()) {
            QSecurity mySecurity = mySecIterator.next();

            /* Register Security details */
            myFile.registerSecurity(mySecurity.getSecurity());

            /* Obtain the price iterator */
            Iterator<QPrice> myPrcIterator = mySecurity.priceIterator();
            while (myPrcIterator.hasNext()) {
                QPrice myCurr = myPrcIterator.next();

                /* Register price details */
                myFile.registerPrice(myCurr.getPrice());
            }
        }

        /* Return the QIF File */
        return myFile;
    }
}
