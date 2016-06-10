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
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;

/**
 * Writer class for QIF Files.
 */
public class QIFWriter {
    /**
     * Quicken Date Format.
     */
    protected static final String QIF_DATEFORMAT = "dd/MM/yy";

    /**
     * Quicken BaseYear.
     */
    protected static final int QIF_BASEYEAR = 1970;

    /**
     * AutoSwitch option.
     */
    protected static final String QIF_AUTOSWITCH = "AutoSwitch";

    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "!Account";

    /**
     * Number of stages.
     */
    protected static final int NUM_STAGES = 6;

    /**
     * Thread Status.
     */
    private final MetisThreadStatusReport theReport;

    /**
     * QIF File.
     */
    private final QIFFile theFile;

    /**
     * Data formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pReport the report
     * @param pFile the QIF file.
     */
    public QIFWriter(final MetisThreadStatusReport pReport,
                     final QIFFile pFile) {
        /* Store parameters */
        theReport = pReport;
        theFile = pFile;

        /* Allocate the formatter and set date format */
        theFormatter = new MetisDataFormatter();
        theFormatter.setFormat(QIF_DATEFORMAT);
    }

    /**
     * Write to Stream.
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    public boolean writeFile(final OutputStreamWriter pStream) throws IOException {
        /* Declare the stages */
        boolean bContinue = theReport.setNumStages(NUM_STAGES);

        /* Write the classes */
        if (bContinue) {
            bContinue = writeClasses(pStream);
        }

        /* Write the categories */
        if (bContinue) {
            bContinue = writeCategories(pStream);
        }

        /* Write the accounts */
        if (bContinue) {
            bContinue = writeAccounts(pStream);
        }

        /* Write the securities */
        if (bContinue) {
            bContinue = writeSecurities(pStream);
        }

        /* Write the events */
        if (bContinue) {
            bContinue = writeEvents(pStream);
        }

        /* Write the prices */
        if (bContinue) {
            bContinue = writePrices(pStream);
        }

        /* Return success */
        return bContinue;
    }

    /**
     * Write Classes.
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeClasses(final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        boolean bContinue = theReport.setNewStage("Writing classes")
                            && theReport.setNumSteps(theFile.numClasses());

        /* Skip stage if we have no classes */
        if (!theFile.hasClasses()) {
            return true;
        } else if (!bContinue) {
            return false;
        }

        /* Format Item Type header */
        QIFRecord.formatItemType(QIFClass.QIF_ITEM, myBuilder);

        /* Write Class header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the categories */
        Iterator<QIFClass> myIterator = theFile.classIterator();
        while (myIterator.hasNext()) {
            QIFClass myClass = myIterator.next();

            /* Format the record */
            myClass.formatRecord(theFormatter, myBuilder);

            /* Write Category record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            if (!theReport.setNextStep()) {
                return false;
            }
        }

        /* Return success */
        return true;
    }

    /**
     * Write Categories.
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeCategories(final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        boolean bContinue = theReport.setNewStage("Writing categories")
                            && theReport.setNumSteps(theFile.numCategories());
        if (!bContinue) {
            return false;
        }

        /* Format Item Type header */
        QIFRecord.formatItemType(QIFEventCategory.QIF_ITEM, myBuilder);

        /* Write Category header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the parent categories */
        Iterator<QIFParentCategory> myIterator = theFile.categoryIterator();
        while (myIterator.hasNext()) {
            QIFParentCategory myCategory = myIterator.next();

            /* Format the record */
            myCategory.formatRecord(theFormatter, myBuilder);

            /* Write Category records */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            if (!theReport.setStepsDone(myCategory.numChildren())) {
                return false;
            }
        }

        /* Return success */
        return true;
    }

    /**
     * Write Accounts.
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeAccounts(final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        boolean bContinue = theReport.setNewStage("Writing accounts")
                            && theReport.setNumSteps(theFile.numAccounts());
        if (!bContinue) {
            return false;
        }

        /* Set AutoSwitch and header */
        QIFRecord.setSwitch(QIF_AUTOSWITCH, myBuilder);
        QIFRecord.formatHeader(QIFAccount.QIF_HDR, myBuilder);

        /* Write Accounts header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the accounts */
        Iterator<QIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            QIFAccountEvents myEvents = myIterator.next();
            QIFAccount myAccount = myEvents.getAccount();

            /* Format the record */
            myAccount.formatRecord(theFormatter, myBuilder);

            /* Write Account record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            if (!theReport.setNextStep()) {
                return false;
            }
        }

        /* Return success */
        return true;
    }

    /**
     * Write Securities.
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeSecurities(final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        boolean bContinue = theReport.setNewStage("Writing securities")
                            && theReport.setNumSteps(theFile.numSecurities());

        /* Skip step if we have no securities */
        if (!theFile.hasSecurities()) {
            return true;
        } else if (!bContinue) {
            return false;
        }

        /* Clear AutoSwitch */
        QIFRecord.clearSwitch(QIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the securities */
        Iterator<QIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            QIFSecurityPrices myList = myIterator.next();
            QIFSecurity mySecurity = myList.getSecurity();

            /* Format Item Type header */
            QIFRecord.formatItemType(QIFSecurity.QIF_ITEM, myBuilder);

            /* Format the record */
            mySecurity.formatRecord(theFormatter, myBuilder);

            /* Write Security record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            if (!theReport.setNextStep()) {
                return false;
            }
        }

        /* Set AutoSwitch */
        QIFRecord.setSwitch(QIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Return success */
        return true;
    }

    /**
     * Write Prices.
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writeEvents(final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        boolean bContinue = theReport.setNewStage("Writing account events")
                            && theReport.setNumSteps(theFile.numAccounts());
        if (!bContinue) {
            return false;
        }

        /* Loop through the accounts */
        Iterator<QIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            QIFAccountEvents myEvents = myIterator.next();
            QIFAccount myAccount = myEvents.getAccount();

            /* Format Item Type header */
            QIFRecord.formatHeader(QIFAccount.QIF_HDR, myBuilder);

            /* Format the record */
            myAccount.formatRecord(theFormatter, myBuilder);

            /* Format Item Type */
            QIFRecord.formatItemType(myAccount.getType(), myBuilder);

            /* Write Account record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Loop through the events */
            Iterator<QIFEventRecord<?>> myEvtIterator = myEvents.eventIterator();
            while (myEvtIterator.hasNext()) {
                QIFEventRecord<?> myEvent = myEvtIterator.next();

                /* Format the record */
                myEvent.formatRecord(theFormatter, myBuilder);

                /* Write Event record */
                pStream.write(myBuilder.toString());
                myBuilder.setLength(0);
            }

            /* Report the progress */
            if (!theReport.setNextStep()) {
                return false;
            }
        }

        /* Return success */
        return true;
    }

    /**
     * Write Prices.
     * @param pStream the output stream
     * @return continue true/false
     * @throws IOException on error
     */
    private boolean writePrices(final OutputStreamWriter pStream) throws IOException {
        /* Create string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        boolean bContinue = theReport.setNewStage("Writing prices")
                            && theReport.setNumSteps(theFile.numSecurities());

        /* Skip step if we have no prices */
        if (!theFile.hasSecurities()) {
            return true;
        } else if (!bContinue) {
            return false;
        }

        /* Loop through the prices */
        Iterator<QIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            QIFSecurityPrices myPriceList = myIterator.next();

            /* Format Prices */
            myPriceList.formatPrices(theFormatter, myBuilder);

            /* Write Price record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            if (!theReport.setNextStep()) {
                return false;
            }
        }

        /* Return success */
        return true;
    }
}
