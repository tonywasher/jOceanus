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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.EventClass;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;

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
     * Map of Classes.
     */
    private final Map<String, QIFClass> theClasses;

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
        theClasses = new LinkedHashMap<String, QIFClass>();
        thePrices = new ArrayList<QIFPrice>();

        /* Allocate the formatter and set date format */
        theFormatter = new JDataFormatter();
        theFormatter.setFormat(QIF_DATEFORMAT);
    }

    /**
     * Register class.
     * @param pClass the class
     */
    public void registerClass(final EventClass pClass) {
        /* Create the new Category and add to the map */
        QIFClass myClass = new QIFClass(this, pClass);
        theClasses.put(myClass.getName(), myClass);
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
     * Obtain class.
     * @param pName the name of the class
     * @return the class
     */
    protected QIFClass getClass(final String pName) {
        /* Lookup the class */
        return theClasses.get(pName);
    }

    /**
     * Write File.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    public boolean writeFile(final ThreadStatus<MoneyWiseData> pStatus,
                             final OutputStreamWriter pStream) throws IOException {
        /* Write the classes */
        boolean bContinue = writeClasses(pStatus, pStream);

        /* Write the categories */
        if (bContinue) {
            bContinue = writeCategories(pStatus, pStream);
        }

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
     * Write Classes.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeClasses(final ThreadStatus<MoneyWiseData> pStatus,
                                 final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = (pStatus.setNewStage("Writing classes"))
                            && (pStatus.setNumSteps(theClasses.size()));

        /* Skip step if we have no classes */
        if (theClasses.isEmpty()) {
            return true;
        }

        /* Format Item Type header */
        QIFRecord.formatItemType(QIFClass.QIF_ITEM, myBuilder);

        /* Write Class header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the categories */
        Iterator<QIFClass> myIterator = theClasses.values().iterator();
        while (myIterator.hasNext()) {
            QIFClass myClass = myIterator.next();

            /* Format the record */
            myClass.formatRecord(theFormatter, myBuilder);

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
     * Write Categories.
     * @param pStatus the thread status
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeCategories(final ThreadStatus<MoneyWiseData> pStatus,
                                    final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = (pStatus.setNewStage("Writing categories"))
                            && (pStatus.setNumSteps(theCategories.size()));

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
    private boolean writeAccounts(final ThreadStatus<MoneyWiseData> pStatus,
                                  final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = (pStatus.setNewStage("Writing accounts"))
                            && (pStatus.setNumSteps(theAccounts.size()));

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
    private boolean writeSecurities(final ThreadStatus<MoneyWiseData> pStatus,
                                    final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = (pStatus.setNewStage("Writing securities"))
                            && (pStatus.setNumSteps(theSecurities.size()));

        /* Skip step if we have no securities */
        if (theSecurities.isEmpty()) {
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
    private boolean writePrices(final ThreadStatus<MoneyWiseData> pStatus,
                                final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Access the number of reporting steps */
        int mySteps = pStatus.getReportingSteps();
        int myCount = 0;

        /* Update status bar */
        boolean bContinue = (pStatus.setNewStage("Writing prices"))
                            && (pStatus.setNumSteps(thePrices.size()));

        /* Skip step if we have no prices */
        if (thePrices.isEmpty()) {
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
