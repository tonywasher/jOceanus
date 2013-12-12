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
package net.sourceforge.joceanus.jmoneywise.quicken;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jdatamodels.threads.ThreadStatus;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice.AccountPriceList;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QSecurityLineType;

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
     * The analysis.
     */
    private final QAnalysis theAnalysis;

    /**
     * The account for this security.
     */
    private final Account theSecurity;

    /**
     * The prices for this security.
     */
    private final List<QPrice> thePrices;

    /**
     * Obtain the security.
     * @return the security
     */
    public Account getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the security name.
     * @return the security name
     */
    public String getName() {
        return theSecurity.getName();
    }

    /**
     * Obtain the security symbol.
     * @return the security symbol
     */
    public String getSymbol() {
        return theSecurity.getSymbol();
    }

    /**
     * Obtain the security class.
     * @return the security class
     */
    public AccountCategoryClass getAccountClass() {
        return theSecurity.getAccountCategoryClass();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pAccount the security account
     */
    protected QSecurity(final QAnalysis pAnalysis,
                        final Account pAccount) {
        /* Call super constructor */
        super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

        /* Store the parameters */
        theAnalysis = pAnalysis;
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
        addStringLine(QSecurityLineType.Name, theSecurity.getName());

        /* If we have a symbol */
        String mySymbol = theSecurity.getSymbol();
        if (mySymbol != null) {
            /* Add the Security symbol */
            addStringLine(QSecurityLineType.Symbol, mySymbol);
        }

        /* Add the Security type */
        addStringLine(QSecurityLineType.SecType, getSecurityType());

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
        QPrice myQIF = new QPrice(theAnalysis, pPrice);
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
     * Obtain price iterator.
     * @return the iterator
     */
    public Iterator<QPrice> priceIterator() {
        return thePrices.iterator();
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
        private final Map<Account, QSecurity> theSecurities;

        /**
         * The analysis.
         */
        private final QAnalysis theAnalysis;

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
            super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

            /* Store parameters */
            theAnalysis = pAnalysis;

            /* Create the map */
            theSecurities = new LinkedHashMap<Account, QSecurity>();
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
                mySecurity = new QSecurity(theAnalysis, pAccount);
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
            if (theSecurities.isEmpty()) {
                return true;
            }

            /* Access the number of reporting steps */
            int mySteps = pStatus.getReportingSteps();
            int myCount = 0;

            /* Update status bar */
            boolean bContinue = pStatus.setNewStage("Writing securities")
                                && pStatus.setNumSteps(theSecurities.size());

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
            boolean bContinue = pStatus.setNewStage("Writing prices")
                                && pStatus.setNumSteps(theNumPrices);

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

        /**
         * Obtain securities iterator.
         * @return the iterator
         */
        public Iterator<QSecurity> securityIterator() {
            return theSecurities.values().iterator();
        }
    }
}
