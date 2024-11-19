/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.quicken.file;

import java.util.Iterator;

import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * Writer class for QIF Files.
 */
public class MoneyWiseQIFWriter {
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
    private final MoneyWiseQIFFile theFile;

    /**
     * Data formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pReport the report
     * @param pFile the QIF file.
     */
    public MoneyWiseQIFWriter(final TethysUIFactory<?> pFactory,
                              final TethysUIThreadStatusReport pReport,
                              final MoneyWiseQIFFile pFile) {
        /* Store parameters */
        theReport = pReport;
        theFile = pFile;

        /* Allocate the formatter and set date format */
        theFormatter = pFactory.newDataFormatter();
        theFormatter.setFormat(QIF_DATEFORMAT);
    }

    /**
     * Write to Stream.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    public void writeFile(final MoneyWiseQIFStreamWriter pStream) throws OceanusException {
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
    private void writeClasses(final MoneyWiseQIFStreamWriter pStream) throws OceanusException {
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
        MoneyWiseQIFRecord.formatItemType(MoneyWiseQIFClass.QIF_ITEM, myBuilder);

        /* Write Class header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the categories */
        final Iterator<MoneyWiseQIFClass> myIterator = theFile.classIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseQIFClass myClass = myIterator.next();

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
    private void writeCategories(final MoneyWiseQIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing categories");
        theReport.setNumSteps(theFile.numCategories());

        /* Format Item Type header */
        MoneyWiseQIFRecord.formatItemType(MoneyWiseQIFEventCategory.QIF_ITEM, myBuilder);

        /* Write Category header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the parent categories */
        final Iterator<MoneyWiseQIFParentCategory> myIterator = theFile.categoryIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseQIFParentCategory myCategory = myIterator.next();

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
    private void writeAccounts(final MoneyWiseQIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing accounts");
        theReport.setNumSteps(theFile.numAccounts());

        /* Set AutoSwitch and header */
        MoneyWiseQIFRecord.setSwitch(QIF_AUTOSWITCH, myBuilder);
        MoneyWiseQIFRecord.formatHeader(MoneyWiseQIFAccount.QIF_HDR, myBuilder);

        /* Write Accounts header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the accounts */
        final Iterator<MoneyWiseQIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseQIFAccountEvents myEvents = myIterator.next();
            final MoneyWiseQIFAccount myAccount = myEvents.getAccount();

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
    private void writeSecurities(final MoneyWiseQIFStreamWriter pStream) throws OceanusException {
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
        MoneyWiseQIFRecord.clearSwitch(QIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the securities */
        final Iterator<MoneyWiseQIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseQIFSecurityPrices myList = myIterator.next();
            final MoneyWiseQIFSecurity mySecurity = myList.getSecurity();

            /* Format Item Type header */
            MoneyWiseQIFRecord.formatItemType(MoneyWiseQIFSecurity.QIF_ITEM, myBuilder);

            /* Format the record */
            mySecurity.formatRecord(theFormatter, myBuilder);

            /* Write Security record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            theReport.setNextStep();
        }

        /* Set AutoSwitch */
        MoneyWiseQIFRecord.setSwitch(QIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);
    }

    /**
     * Write Prices.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeEvents(final MoneyWiseQIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing account events");
        theReport.setNumSteps(theFile.numAccounts());

        /* Loop through the accounts */
        final Iterator<MoneyWiseQIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseQIFAccountEvents myEvents = myIterator.next();
            final MoneyWiseQIFAccount myAccount = myEvents.getAccount();

            /* Format Item Type header */
            MoneyWiseQIFRecord.formatHeader(MoneyWiseQIFAccount.QIF_HDR, myBuilder);

            /* Format the record */
            myAccount.formatRecord(theFormatter, myBuilder);

            /* Format Item Type */
            MoneyWiseQIFRecord.formatItemType(myAccount.getType(), myBuilder);

            /* Write Account record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Loop through the events */
            final Iterator<MoneyWiseQIFEventRecord<?>> myEvtIterator = myEvents.eventIterator();
            while (myEvtIterator.hasNext()) {
                final MoneyWiseQIFEventRecord<?> myEvent = myEvtIterator.next();

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
    private void writePrices(final MoneyWiseQIFStreamWriter pStream) throws OceanusException {
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
        final Iterator<MoneyWiseQIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseQIFSecurityPrices myPriceList = myIterator.next();

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
