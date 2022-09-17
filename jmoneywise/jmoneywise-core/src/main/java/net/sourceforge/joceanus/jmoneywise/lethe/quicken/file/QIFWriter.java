/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

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
    private final TethysUIThreadStatusReport theReport;

    /**
     * QIF File.
     */
    private final QIFFile theFile;

    /**
     * Data formatter.
     */
    private final TethysDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pReport the report
     * @param pFile the QIF file.
     */
    public QIFWriter(final TethysUIThreadStatusReport pReport,
                     final QIFFile pFile) {
        /* Store parameters */
        theReport = pReport;
        theFile = pFile;

        /* Allocate the formatter and set date format */
        theFormatter = new TethysDataFormatter();
        theFormatter.setFormat(QIF_DATEFORMAT);
    }

    /**
     * Write to Stream.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    public void writeFile(final QIFStreamWriter pStream) throws OceanusException {
        /* Declare the stages */
        theReport.setNumStages(NUM_STAGES);

        /* Write the classes */
        writeClasses(pStream);

        /* Write the categories */
        writeCategories(pStream);

        /* Write the accounts */
        writeAccounts(pStream);

        /* Write the securities */
        writeSecurities(pStream);

        /* Write the events */
        writeEvents(pStream);

        /* Write the prices */
        writePrices(pStream);
    }

    /**
     * Write Classes.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeClasses(final QIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing classes");
        theReport.setNumSteps(theFile.numClasses());

        /* Skip stage if we have no classes */
        if (!theFile.hasClasses()) {
            return;
        }

        /* Format Item Type header */
        QIFRecord.formatItemType(QIFClass.QIF_ITEM, myBuilder);

        /* Write Class header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the categories */
        final Iterator<QIFClass> myIterator = theFile.classIterator();
        while (myIterator.hasNext()) {
            final QIFClass myClass = myIterator.next();

            /* Format the record */
            myClass.formatRecord(theFormatter, myBuilder);

            /* Write Category record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            theReport.setNextStep();
        }
    }

    /**
     * Write Categories.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeCategories(final QIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing categories");
        theReport.setNumSteps(theFile.numCategories());

        /* Format Item Type header */
        QIFRecord.formatItemType(QIFEventCategory.QIF_ITEM, myBuilder);

        /* Write Category header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the parent categories */
        final Iterator<QIFParentCategory> myIterator = theFile.categoryIterator();
        while (myIterator.hasNext()) {
            final QIFParentCategory myCategory = myIterator.next();

            /* Format the record */
            myCategory.formatRecord(theFormatter, myBuilder);

            /* Write Category records */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            theReport.setStepsDone(myCategory.numChildren());
        }
    }

    /**
     * Write Accounts.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeAccounts(final QIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing accounts");
        theReport.setNumSteps(theFile.numAccounts());

        /* Set AutoSwitch and header */
        QIFRecord.setSwitch(QIF_AUTOSWITCH, myBuilder);
        QIFRecord.formatHeader(QIFAccount.QIF_HDR, myBuilder);

        /* Write Accounts header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the accounts */
        final Iterator<QIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            final QIFAccountEvents myEvents = myIterator.next();
            final QIFAccount myAccount = myEvents.getAccount();

            /* Format the record */
            myAccount.formatRecord(theFormatter, myBuilder);

            /* Write Account record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            theReport.setNextStep();
        }
    }

    /**
     * Write Securities.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeSecurities(final QIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing securities");
        theReport.setNumSteps(theFile.numSecurities());

        /* Skip step if we have no securities */
        if (!theFile.hasSecurities()) {
            return;
        }

        /* Clear AutoSwitch */
        QIFRecord.clearSwitch(QIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the securities */
        final Iterator<QIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            final QIFSecurityPrices myList = myIterator.next();
            final QIFSecurity mySecurity = myList.getSecurity();

            /* Format Item Type header */
            QIFRecord.formatItemType(QIFSecurity.QIF_ITEM, myBuilder);

            /* Format the record */
            mySecurity.formatRecord(theFormatter, myBuilder);

            /* Write Security record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            theReport.setNextStep();
        }

        /* Set AutoSwitch */
        QIFRecord.setSwitch(QIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);
    }

    /**
     * Write Prices.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeEvents(final QIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing account events");
        theReport.setNumSteps(theFile.numAccounts());

        /* Loop through the accounts */
        final Iterator<QIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            final QIFAccountEvents myEvents = myIterator.next();
            final QIFAccount myAccount = myEvents.getAccount();

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
            final Iterator<QIFEventRecord<?>> myEvtIterator = myEvents.eventIterator();
            while (myEvtIterator.hasNext()) {
                final QIFEventRecord<?> myEvent = myEvtIterator.next();

                /* Format the record */
                myEvent.formatRecord(theFormatter, myBuilder);

                /* Write Event record */
                pStream.write(myBuilder.toString());
                myBuilder.setLength(0);
            }

            /* Report the progress */
            theReport.setNextStep();
        }
    }

    /**
     * Write Prices.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writePrices(final QIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing prices");
        theReport.setNumSteps(theFile.numSecurities());

        /* Skip step if we have no prices */
        if (!theFile.hasSecurities()) {
            return;
        }

        /* Loop through the prices */
        final Iterator<QIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            final QIFSecurityPrices myPriceList = myIterator.next();

            /* Format Prices */
            myPriceList.formatPrices(theFormatter, myBuilder);

            /* Write Price record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            theReport.setNextStep();
        }
    }
}
