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
package net.sourceforge.jOceanus.jMoneyWise.quicken.file;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataModels.threads.ThreadStatus;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * QIF File representation.
 */
public class QIFFile {
    /**
     * Quicken Date Format.
     */
    private static final String QIF_DATEFORMAT = "dd/MM/yy";

    /**
     * AutoSwitch option.
     */
    protected static final String QIF_AUTOSWITCH = "AutoSwitch";

    /**
     * The formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Map of Accounts.
     */
    private final Map<String, QIFAccount> theAccounts;

    /**
     * Map of Securities.
     */
    private final Map<String, QIFSecurity> theSecurities;

    /**
     * Map of Categories.
     */
    private final Map<String, QIFEventCategory> theCategories;

    /**
     * List of prices.
     */
    private final List<QIFPrice> thePrices;

    /**
     * Constructor.
     */
    public QIFFile() {
        /* Allocate lists */
        theAccounts = new LinkedHashMap<String, QIFAccount>();
        theSecurities = new LinkedHashMap<String, QIFSecurity>();
        theCategories = new LinkedHashMap<String, QIFEventCategory>();
        thePrices = new ArrayList<QIFPrice>();

        /* Allocate the formatter and set date format */
        theFormatter = new JDataFormatter();
        theFormatter.setFormat(QIF_DATEFORMAT);
    }

    /**
     * Register category.
     * @param pCategory the category
     */
    public void registerCategory(final EventCategory pCategory) {
        /* Create the new Category and add to the map */
        QIFEventCategory myCat = new QIFEventCategory(this, pCategory);
        theCategories.put(myCat.getName(), myCat);
    }

    /**
     * Register account.
     * @param pAccount the account
     */
    public void registerAccount(final Account pAccount) {
        /* Create the new Account and add to the map */
        QIFAccount myAct = new QIFAccount(this, pAccount);
        theAccounts.put(myAct.getName(), myAct);
    }

    /**
     * Register security.
     * @param pSecurity the security
     */
    public void registerSecurity(final Account pSecurity) {
        /* Create the new Security and add to the map */
        QIFSecurity mySec = new QIFSecurity(this, pSecurity);
        theSecurities.put(mySec.getName(), mySec);
    }

    /**
     * Register price.
     * @param pPrice the price
     */
    public void registerPrice(final AccountPrice pPrice) {
        /* Allocate price and add to list */
        QIFPrice myPrice = new QIFPrice(this, pPrice);
        thePrices.add(myPrice);
    }

    /**
     * Obtain category.
     * @param pName the name of the category
     * @return the category
     */
    protected QIFEventCategory getCategory(final String pName) {
        /* Lookup the category */
        return theCategories.get(pName);
    }

    /**
     * Obtain account.
     * @param pName the name of the account
     * @return the account
     */
    protected QIFAccount getAccount(final String pName) {
        /* Lookup the account */
        return theAccounts.get(pName);
    }

    /**
     * Obtain security.
     * @param pName the name of the security
     * @return the security
     */
    protected QIFSecurity getSecurity(final String pName) {
        /* Lookup the security */
        return theSecurities.get(pName);
    }

    /**
     * Write File.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    public boolean writeFile(final ThreadStatus<FinanceData> pStatus,
                             final OutputStreamWriter pStream) throws IOException {
        /* Write the categories */
        boolean bContinue = writeCategories(pStatus, pStream);

        /* Write the accounts */
        if (bContinue) {
            bContinue = writeAccounts(pStatus, pStream);
        }

        /* Write the securities */
        if (bContinue) {
            bContinue = writeSecurities(pStatus, pStream);
        }

        /* Write the prices */
        if (bContinue) {
            bContinue = writePrices(pStatus, pStream);
        }

        /* Return success */
        return bContinue;
    }

    /**
     * Write Categories.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeCategories(final ThreadStatus<FinanceData> pStatus,
                                    final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = ((pStatus.setNewStage("Writing categories")) && (pStatus.setNumSteps(theCategories.size())));

        /* Format Item Type header */
        QIFRecord.formatItemType(QIFEventCategory.QIF_ITEM, myBuilder);

        /* Write Category header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the categories */
        Iterator<QIFEventCategory> myIterator = theCategories.values().iterator();
        while (myIterator.hasNext()) {
            QIFEventCategory myCategory = myIterator.next();

            /* Format the record */
            myCategory.formatRecord(theFormatter, myBuilder);

            /* Write Category record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

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
     * Write Accounts.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeAccounts(final ThreadStatus<FinanceData> pStatus,
                                  final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = ((pStatus.setNewStage("Writing accounts")) && (pStatus.setNumSteps(theAccounts.size())));

        /* Set AutoSwitch and header */
        QIFRecord.setSwitch(QIF_AUTOSWITCH, myBuilder);
        QIFRecord.formatHeader(QIFAccount.QIF_HDR, myBuilder);

        /* Write Accounts header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the accounts */
        Iterator<QIFAccount> myIterator = theAccounts.values().iterator();
        while (myIterator.hasNext()) {
            QIFAccount myAccount = myIterator.next();

            /* Format the record */
            myAccount.formatRecord(theFormatter, myBuilder);

            /* Write Account record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

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
     * Write Securities.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeSecurities(final ThreadStatus<FinanceData> pStatus,
                                    final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = ((pStatus.setNewStage("Writing securities")) && (pStatus.setNumSteps(theSecurities.size())));

        /* Skip step if we have no securities */
        if (theSecurities.size() == 0) {
            return true;
        }

        /* Clear AutoSwitch */
        QIFRecord.clearSwitch(QIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the securities */
        Iterator<QIFSecurity> myIterator = theSecurities.values().iterator();
        while (myIterator.hasNext()) {
            QIFSecurity mySecurity = myIterator.next();

            /* Format Item Type header */
            QIFRecord.formatItemType(QIFSecurity.QIF_ITEM, myBuilder);

            /* Format the record */
            mySecurity.formatRecord(theFormatter, myBuilder);

            /* Write Security record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            myCount++;
            if (((myCount % mySteps) == 0)
                && (!pStatus.setStepsDone(myCount))) {
                bContinue = false;
            }
        }

        /* If we should continue */
        if (bContinue) {
            /* Set AutoSwitch */
            QIFRecord.setSwitch(QIF_AUTOSWITCH, myBuilder);

            /* Write Securities header */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);
        }

        /* Return success */
        return bContinue;
    }

    /**
     * Write Prices.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writePrices(final ThreadStatus<FinanceData> pStatus,
                                final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = ((pStatus.setNewStage("Writing prices")) && (pStatus.setNumSteps(thePrices.size())));

        /* Skip step if we have no prices */
        if (thePrices.size() == 0) {
            return true;
        }

        /* Loop through the prices */
        Iterator<QIFPrice> myIterator = thePrices.iterator();
        while (myIterator.hasNext()) {
            QIFPrice myPrice = myIterator.next();

            /* Format Item Type header */
            QIFRecord.formatItemType(QIFPrice.QIF_ITEM, myBuilder);

            /* Format the record */
            myPrice.formatRecord(theFormatter, myBuilder);

            /* Write Security record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

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
