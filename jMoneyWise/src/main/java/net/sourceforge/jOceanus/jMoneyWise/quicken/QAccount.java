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
package net.sourceforge.jOceanus.jMoneyWise.quicken;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataModels.threads.ThreadStatus;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QCategory.QCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QEvent.QEventBaseList;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QEvent.QEventList;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QPortfolioEvent.QPortfolioEventList;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QSecurity.QSecurityList;

/**
 * Quicken Account.
 */
public final class QAccount
        extends QElement {
    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "!Account";

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
     * Number of output stages.
     */
    protected static final int QIF_NUMLISTS = 3;

    /**
     * Number of output stages.
     */
    protected static final int QIF_NUMOUTS = 5;

    /**
     * The account.
     */
    private final Account theAccount;

    /**
     * The events.
     */
    private final QEventBaseList<? extends QEvent> theEvents;

    /**
     * Is this account an autoExpense?
     */
    private final boolean isAutoExpense;

    /**
     * Is this account an portfolio?
     */
    private final boolean isPortfolio;

    /**
     * Constructor.
     * @param pAccount the account
     * @param pFormatter the data formatter
     */
    private QAccount(final Account pAccount,
                     final JDataFormatter pFormatter) {
        /* Call super constructor */
        super(pFormatter);

        /* Store the account */
        theAccount = pAccount;
        isAutoExpense = (pAccount.getAutoExpense() != null);
        isPortfolio = pAccount.isCategoryClass(AccountCategoryClass.Portfolio);
        theEvents = (isPortfolio)
                ? new QPortfolioEventList(this, pFormatter)
                : new QEventList(this, pFormatter);
    }

    /**
     * build QIF format.
     * @return the QIF format
     */
    protected String buildQIF() {
        /* Reset the builder */
        reset();

        /* Add the Account name */
        addAccountLine(QActLineType.Name, theAccount);

        /* Add the AccountType */
        addStringLine(QActLineType.Type, getAccountType());

        /* If we have a description */
        String myDesc = theAccount.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QActLineType.Description, myDesc);
        }

        /* Return the result */
        return completeItem();
    }

    @Override
    public String toString() {
        return buildQIF();
    }

    /**
     * build QIF format header.
     * @param pStartDate the start date
     * @return the QIF format
     */
    protected String buildQIFHeader(final JDateDay pStartDate) {
        /* Obtain the account type */
        String myType = getAccountType();

        /* Reset the builder */
        reset();

        /* Add the Account indicator */
        append(QIF_ITEM);
        endLine();

        /* Add the Account name */
        addAccountLine(QActLineType.Name, theAccount);

        /* If we have a description */
        String myDesc = theAccount.getComments();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QActLineType.Description, myDesc);
        }

        /* Add the Account type */
        addStringLine(QActLineType.Type, myType);

        /* Add the End indicator */
        endItem();

        /* Add the Item type */
        append(QIF_ITEMTYPE);
        append(myType);
        endLine();

        /* If the account has an opening balance */
        JMoney myOpeningBal = theAccount.getOpeningBalance();
        if (myOpeningBal != null) {
            /* Create the opening balance event */
            QEvent myEvent = new QEvent(getFormatter());
            append(myEvent.buildOpeningQIF(theAccount, pStartDate, myOpeningBal));
        }

        /* Return the builder */
        return getBufferedString();
    }

    /**
     * Determine account type.
     * @return the account type
     */
    protected String getAccountType() {
        switch (theAccount.getAccountCategoryClass()) {
            case Savings:
            case Bond:
                return "Bank";
            case Cash:
                return "Cash";
            case CreditCard:
                return "CCard";
            case Portfolio:
                return "Invst";
            case PrivateLoan:
                return "Oth A";
            case Loan:
                return "Oth L";
            default:
                return null;
        }
    }

    /**
     * Process the event.
     * @param pEvent the event
     * @param isCredit is this the credit item?
     */
    private void processEvent(final Event pEvent,
                              final boolean isCredit) {
        /* register the event and add to the list */
        theEvents.registerEvent(pEvent, isCredit);
    }

    /**
     * is the account active.
     * @return true/false
     */
    protected boolean isActive() {
        /* Check size of list */
        return (theEvents.size() > 0);
    }

    /**
     * Output events.
     * @param pStream the output stream
     * @param pStartDate the opening date
     * @throws IOException on error
     */
    protected void outputEvents(final OutputStreamWriter pStream,
                                final JDateDay pStartDate) throws IOException {
        /* If the account is active */
        if (isActive()) {
            /* Output the events */
            if (isAutoExpense) {
                theEvents.outputAutoEvents(pStream, pStartDate);
            } else {
                theEvents.outputEvents(pStream, pStartDate);
            }
        }
    }

    /**
     * Account List class.
     */
    protected static class QAccountList
            extends QElement {
        /**
         * Account Map.
         */
        private final HashMap<Account, QAccount> theAccounts;

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
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected QAccountList(final JDataFormatter pFormatter) {
            /* Call the super-constructor */
            super(pFormatter);

            /* Create the maps */
            theAccounts = new HashMap<Account, QAccount>();
            theCategories = new QCategoryList(pFormatter);
            theSecurities = new QSecurityList(pFormatter);
        }

        /**
         * Access account.
         * @param pAccount the account
         * @return the QIF account
         */
        protected QAccount getAccount(final Account pAccount) {
            /* Note which account we are using */
            Account myLookup = pAccount;

            /* If the account has Units */
            if (pAccount.hasUnits()) {
                /* Register the security */
                theSecurities.registerSecurity(pAccount);

                /* We need to use its parent */
                myLookup = pAccount.getParent();

                /* else ignore if the account is a non-Asset */
            } else if (!pAccount.hasValue()) {
                return null;
            }

            /* Look up the account in the map */
            QAccount myAccount = theAccounts.get(myLookup);

            /* If this is a new account */
            if (myAccount == null) {
                /* Allocate the account and add to the map */
                myAccount = new QAccount(pAccount, getFormatter());
                theAccounts.put(pAccount, myAccount);

                /* If the account is an autoExpense */
                EventCategory myCategory = pAccount.getAutoExpense();
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
         * @param pData the dataSet
         */
        protected void analyseData(final ThreadStatus<FinanceData> pStatus,
                                   final FinanceData pData) {
            /* Access lists */
            EventList myEvents = pData.getEvents();
            AccountList myAccounts = pData.getAccounts();
            TaxYearList myTaxYears = pData.getTaxYears();
            Event myLastEvent = null;

            /* Access the number of reporting steps */
            int mySteps = pStatus.getReportingSteps();
            int myCount = 0;

            /* Determine start date */
            TaxYear myTaxYear = myTaxYears.get(0);
            theStartDate = myTaxYear.getRange().getStart();

            /* Update status bar */
            boolean bContinue = ((pStatus.setNumStages(QIF_NUMLISTS))
                                 && (pStatus.setNewStage("Analysing accounts")) && (pStatus.setNumSteps(myAccounts.size())));

            /* Loop through the accounts */
            Iterator<Account> myActIterator = myAccounts.iterator();
            while ((bContinue)
                   && (myActIterator.hasNext())) {
                Account myAccount = myActIterator.next();

                /* Ignore deleted accounts */
                if (myAccount.isDeleted()) {
                    continue;
                }

                /* If we have an opening balance */
                JMoney myBalance = myAccount.getOpeningBalance();
                if (myBalance != null) {
                    /* Make sure that it is registered */
                    getAccount(myAccount);
                }

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
                    bContinue = false;
                }
            }

            /* Update status bar */
            if (bContinue) {
                bContinue = ((pStatus.setNewStage("Analysing events")) && (pStatus.setNumSteps(myEvents.size())));
            }

            /* Loop through the events */
            myCount = 0;
            Iterator<Event> myIterator = myEvents.iterator();
            while ((bContinue)
                   && (myIterator.hasNext())) {
                Event myEvent = myIterator.next();

                /* Ignore deleted events */
                if (myEvent.isDeleted()) {
                    continue;
                }

                /* Access key details */
                Account myDebit = myEvent.getDebit();
                Account myCredit = myEvent.getCredit();
                EventCategory myCategory = myEvent.getCategory();

                /* If the category is not a transfer */
                if (!myCategory.isTransfer()) {
                    /* Make sure that it is registered */
                    theCategories.registerCategory(myCategory);
                }

                /* Process event for debit and credit */
                myLastEvent = myEvent;
                processEvent(myEvent, myDebit, false);
                if (!myDebit.equals(myCredit)) {
                    processEvent(myEvent, myCredit, true);
                }

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
                    bContinue = false;
                }
            }

            /* If we should continue */
            AccountPriceList myPrices = pData.getPrices();
            if ((bContinue)
                && (myLastEvent != null)) {
                /* Determine Tax Year for last event */
                myTaxYear = myTaxYears.findTaxYearForDate(myLastEvent.getDate());

                /* Update status bar and analyse prices */
                if ((pStatus.setNewStage("Analysing prices"))
                    && (pStatus.setNumSteps(myPrices.size()))) {
                    /* Analyse prices for securities */
                    theSecurities.buildPrices(pStatus, myPrices, myTaxYear.getTaxYear());
                }
            }
        }

        /**
         * Process the event.
         * @param pEvent the event
         * @param pAccount the account
         * @param isCredit is this the credit item?
         */
        private void processEvent(final Event pEvent,
                                  final Account pAccount,
                                  final boolean isCredit) {
            /* Access account */
            QAccount myAccount = getAccount(pAccount);

            /* If we should process the account */
            if (myAccount != null) {
                /* If the account is autoExpense */
                if (myAccount.isAutoExpense) {
                    /* Handle non transfer specially, otherwise ignore */
                    if (!pEvent.getCategory().isTransfer()) {
                        /* Process the event */
                        myAccount.processEvent(pEvent, isCredit);
                        theNumEvents += 2;
                    }

                    /* Normal event */
                } else {
                    /* Process the event */
                    myAccount.processEvent(pEvent, isCredit);
                    theNumEvents++;
                }
            }
        }

        /**
         * Output data.
         * @param pStatus the thread status
         * @param pStream the output stream
         * @return success true/false
         * @throws IOException on error
         */
        protected boolean outputData(final ThreadStatus<FinanceData> pStatus,
                                     final OutputStreamWriter pStream) throws IOException {
            /* Access the number of reporting steps */
            int mySteps = pStatus.getReportingSteps();
            int myCount = 0;

            /* Update status bar */
            boolean bContinue = pStatus.setNumStages(QIF_NUMOUTS);

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
                append(QIF_ITEM);
                endLine();

                /* Write Accounts header */
                pStream.write(getBufferedString());

                /* Update status bar */
                bContinue = ((pStatus.setNewStage("Writing accounts")) && (pStatus.setNumSteps(theAccounts.size())));
            }

            /* Loop through the accounts */
            Iterator<QAccount> myIterator = theAccounts.values().iterator();
            while ((bContinue)
                   && (myIterator.hasNext())) {
                QAccount myAccount = myIterator.next();

                /* If the account is active */
                if (myAccount.isActive()) {
                    /* Write Account details */
                    pStream.write(myAccount.buildQIF());
                }

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
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
            while ((bContinue)
                   && (myIterator.hasNext())) {
                QAccount myAccount = myIterator.next();

                /* Output events */
                myAccount.outputEvents(pStream, theStartDate);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
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
    }

    /**
     * Quicken Account Line Types.
     */
    public enum QActLineType implements QLineType {
        /**
         * Name.
         */
        Name("N"),

        /**
         * Account Type.
         */
        Type("T"),

        /**
         * Description.
         */
        Description("D"),

        /**
         * Credit Limit.
         */
        CreditLimit("L");

        /**
         * The symbol.
         */
        private final String theSymbol;

        @Override
        public String getSymbol() {
            return theSymbol;
        }

        /**
         * Constructor.
         * @param pSymbol the symbol
         */
        private QActLineType(final String pSymbol) {
            /* Store symbol */
            theSymbol = pSymbol;
        }
    }
}
