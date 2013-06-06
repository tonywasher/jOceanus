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

import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QEvent.QEventBaseList;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QEvent.QEventList;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QPortfolioEvent.QPortfolioEventList;

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
     * Is the account using autoExpense?
     * @return true/false
     */
    protected boolean isAutoExpense() {
        return isAutoExpense;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pAccount the account
     */
    protected QAccount(final QAnalysis pAnalysis,
                       final Account pAccount) {
        /* Call super constructor */
        super(pAnalysis.getFormatter());

        /* Store the account */
        theAccount = pAccount;
        isAutoExpense = (pAccount.getAutoExpense() != null);
        isPortfolio = pAccount.isCategoryClass(AccountCategoryClass.Portfolio);
        theEvents = (isPortfolio)
                ? new QPortfolioEventList(pAnalysis, this)
                : new QEventList(pAnalysis, this);
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
    protected void processEvent(final Event pEvent,
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
        return ((theEvents.size() > 0) || (theAccount.getOpeningBalance() != null));
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
