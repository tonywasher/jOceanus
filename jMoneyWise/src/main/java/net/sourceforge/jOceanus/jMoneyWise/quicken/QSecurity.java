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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataModels.threads.ThreadStatus;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * Quicken Security.
 */
public final class QSecurity
        extends QElement {
    /**
     * Item type.
     */
    private static final String QIF_ITEM = "Security";

    /**
     * The account for this security.
     */
    private final Account theSecurity;

    /**
     * The prices for this security.
     */
    private final List<QPrice> thePrices;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @param pAccount the security account
     */
    private QSecurity(final JDataFormatter pFormatter,
                      final Account pAccount) {
        /* Call super constructor */
        super(pFormatter);

        /* Store the account */
        theSecurity = pAccount;

        /* Create the price list */
        thePrices = new ArrayList<QPrice>();
    }

    /**
     * build QIF format.
     * @return the QIF format
     */
    protected String buildQIF() {
        /* Reset the builder */
        reset();

        /* Add the Item type */
        append(QIF_ITEMTYPE);
        append(QIF_ITEM);
        endLine();

        /* Add the Security name */
        addStringLine(QSecLineType.Name, theSecurity.getName());

        /* If we have a symbol */
        String mySymbol = theSecurity.getSymbol();
        if (mySymbol != null) {
            /* Add the Security symbol */
            addStringLine(QSecLineType.Symbol, mySymbol);
        }

        /* Add the Security type */
        addStringLine(QSecLineType.SecType, getSecurityType());

        /* Return the result */
        return completeItem();
    }

    @Override
    public String toString() {
        return buildQIF();
    }

    /**
     * Add Price.
     * @param pPrice the price to add
     */
    protected void addPrice(final AccountPrice pPrice) {
        /* Add the price */
        QPrice myQIF = new QPrice(getFormatter(), pPrice);
        thePrices.add(myQIF);
    }

    /**
     * Output prices.
     * @param pStream the output stream
     * @throws IOException on error
     */
    protected void outputPrices(final OutputStreamWriter pStream) throws IOException {
        /* Loop through the securities */
        for (QPrice myPrice : thePrices) {
            /* Write price details */
            pStream.write(myPrice.buildQIF());
        }
    }

    /**
     * Determine security type.
     * @return the security type
     */
    private String getSecurityType() {
        switch (theSecurity.getAccountCategoryClass()) {
            case Shares:
                return "Share";
            case UnitTrust:
                return "Unit/Inv. Trust";
            case LifeBond:
                return "Bond";
            case Endowment:
                return "Trust";
            case Asset:
                return "Real Estate";
            default:
                return "Other";
        }
    }

    /**
     * Security List class.
     */
    protected static class QSecurityList
            extends QElement {
        /**
         * Security Map.
         */
        private final HashMap<Account, QSecurity> theSecurities;

        /**
         * Number of prices.
         */
        private int theNumPrices = 0;

        /**
         * Constructor.
         * @param pAnalysis the analysis
         */
        protected QSecurityList(final QAnalysis pAnalysis) {
            /* Call super constructor */
            super(pAnalysis.getFormatter());

            /* Create the map */
            theSecurities = new HashMap<Account, QSecurity>();
        }

        /**
         * Register security.
         * @param pAccount the security account
         */
        protected void registerSecurity(final Account pAccount) {
            /* Look up the security in the map */
            QSecurity mySecurity = theSecurities.get(pAccount);

            /* If this is a new security */
            if (mySecurity == null) {
                /* Allocate the security and add to the map */
                mySecurity = new QSecurity(getFormatter(), pAccount);
                theSecurities.put(pAccount, mySecurity);
            }
        }

        /**
         * Build prices.
         * @param pStatus the thread status
         * @param pPrices the price list
         * @param pDate the latest date for prices
         */
        protected void buildPrices(final ThreadStatus<FinanceData> pStatus,
                                   final AccountPriceList pPrices,
                                   final JDateDay pDate) {
            /* Access the number of reporting steps */
            int mySteps = pStatus.getReportingSteps();
            int myCount = 0;

            /* Loop through the account prices */
            Iterator<AccountPrice> myIterator = pPrices.iterator();
            while (myIterator.hasNext()) {
                AccountPrice myPrice = myIterator.next();

                /* If the price is too late */
                if (pDate.compareTo(myPrice.getDate()) < 0) {
                    /* Break the loop */
                    break;
                }

                /* Ignore deleted prices */
                if (myPrice.isDeleted()) {
                    continue;
                }

                /* Access the security */
                QSecurity mySecurity = theSecurities.get(myPrice.getAccount());

                /* If we have a security */
                if (mySecurity != null) {
                    /* Add the price to the security */
                    mySecurity.addPrice(myPrice);
                    theNumPrices++;
                }

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
                    break;
                }
            }
        }

        /**
         * Output securities.
         * @param pStatus the thread status
         * @param pStream the output stream
         * @return success true/false
         * @throws IOException on error
         */
        protected boolean outputSecurities(final ThreadStatus<FinanceData> pStatus,
                                           final OutputStreamWriter pStream) throws IOException {
            /* Ignore if there are no securities */
            if (theSecurities.size() == 0) {
                return true;
            }

            /* Access the number of reporting steps */
            int mySteps = pStatus.getReportingSteps();
            int myCount = 0;

            /* Update status bar */
            boolean bContinue = ((pStatus.setNewStage("Writing securities")) && (pStatus.setNumSteps(theSecurities.size())));

            /* Clear AutoSwitch */
            reset();
            append(QAnalysis.QIF_CLROPT);
            append(QAnalysis.QIF_AUTOSWITCH);
            endLine();
            pStream.write(getBufferedString());

            /* Loop through the securities */
            Iterator<QSecurity> myIterator = theSecurities.values().iterator();
            while ((bContinue)
                   && (myIterator.hasNext())) {
                QSecurity mySecurity = myIterator.next();

                /* Write Security details */
                pStream.write(mySecurity.buildQIF());

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
                    bContinue = false;
                }
            }

            /* Set AutoSwitch */
            reset();
            append(QAnalysis.QIF_SETOPT);
            append(QAnalysis.QIF_AUTOSWITCH);
            endLine();
            pStream.write(getBufferedString());

            /* Return success */
            return bContinue;
        }

        /**
         * Output prices.
         * @param pStatus the thread status
         * @param pStream the output stream
         * @return success true/false
         * @throws IOException on error
         */
        protected boolean outputPrices(final ThreadStatus<FinanceData> pStatus,
                                       final OutputStreamWriter pStream) throws IOException {
            /* Access the number of reporting steps */
            int mySteps = pStatus.getReportingSteps();
            int myCount = 0;

            /* Update status bar */
            boolean bContinue = ((pStatus.setNewStage("Writing prices")) && (pStatus.setNumSteps(theNumPrices)));

            /* Loop through the securities */
            Iterator<QSecurity> myIterator = theSecurities.values().iterator();
            while ((bContinue)
                   && (myIterator.hasNext())) {
                QSecurity mySecurity = myIterator.next();

                /* Write price details */
                mySecurity.outputPrices(pStream);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
                    bContinue = false;
                }
            }

            /* Return success */
            return bContinue;
        }
    }

    /**
     * Quicken Security Line Types.
     */
    public enum QSecLineType implements QLineType {
        /**
         * Name.
         */
        Name("N"),

        /**
         * Symbol.
         */
        Symbol("S"),

        /**
         * Security Type.
         */
        SecType("T");

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
        private QSecLineType(final String pSymbol) {
            /* Store symbol */
            theSymbol = pSymbol;
        }
    }
}
