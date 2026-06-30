/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.quicken.builder.atlas;

import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccount;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventRecord;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFParentCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRecord;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFSecurity;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFSecurityPrices;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.util.Iterator;

/**
 * Writer class for XQIF Files.
 */
public class MoneyWiseXQIFWriter {
    /**
     * Quicken Date Format.
     */
    static final String XQIF_DATEFORMAT = "dd/MM/yy";

    /**
     * AutoSwitch option.
     */
    private static final String XQIF_AUTOSWITCH = "AutoSwitch";

    /**
     * Number of stages.
     */
    private static final int NUM_STAGES = 6;

    /**
     * Thread Status.
     */
    private final TethysUIThreadStatusReport theReport;

    /**
     * XQIF File.
     */
    private final MoneyWiseXQIFFile theFile;

    /**
     * Data formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pReport  the report
     * @param pFile    the XQIF file.
     */
    public MoneyWiseXQIFWriter(final TethysUIFactory<?> pFactory,
                               final TethysUIThreadStatusReport pReport,
                               final MoneyWiseXQIFFile pFile) {
        /* Store parameters */
        theReport = pReport;
        theFile = pFile;

        /* Allocate the formatter and set date format */
        theFormatter = pFactory.newDataFormatter();
        theFormatter.setFormat(XQIF_DATEFORMAT);
    }

    /**
     * Write to Stream.
     *
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    public void writeFile(final MoneyWiseXQIFStreamWriter pStream) throws OceanusException {
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
     *
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeClasses(final MoneyWiseXQIFStreamWriter pStream) throws OceanusException {
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
        MoneyWiseXQIFRecord.formatItemType(MoneyWiseXQIFClass.XQIF_ITEM, myBuilder);

        /* Write Class header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the categories */
        final Iterator<MoneyWiseXQIFClass> myIterator = theFile.classIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFClass myClass = myIterator.next();

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
     *
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeCategories(final MoneyWiseXQIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing categories");
        theReport.setNumSteps(theFile.numCategories());

        /* Format Item Type header */
        MoneyWiseXQIFRecord.formatItemType(MoneyWiseXQIFEventCategory.XQIF_ITEM, myBuilder);

        /* Write Category header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the parent categories */
        final Iterator<MoneyWiseXQIFParentCategory> myIterator = theFile.categoryIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFParentCategory myCategory = myIterator.next();

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
     *
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeAccounts(final MoneyWiseXQIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing accounts");
        theReport.setNumSteps(theFile.numAccounts());

        /* Set AutoSwitch and header */
        MoneyWiseXQIFRecord.setSwitch(XQIF_AUTOSWITCH, myBuilder);
        MoneyWiseXQIFRecord.formatHeader(MoneyWiseXQIFAccount.XQIF_HDR, myBuilder);

        /* Write Accounts header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the accounts */
        final Iterator<MoneyWiseXQIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFAccountEvents myEvents = myIterator.next();
            final MoneyWiseXQIFAccount myAccount = myEvents.getAccount();

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
     *
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeSecurities(final MoneyWiseXQIFStreamWriter pStream) throws OceanusException {
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
        MoneyWiseXQIFRecord.clearSwitch(XQIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);

        /* Loop through the securities */
        final Iterator<MoneyWiseXQIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFSecurityPrices myList = myIterator.next();
            final MoneyWiseXQIFSecurity mySecurity = myList.getSecurity();

            /* Format Item Type header */
            MoneyWiseXQIFRecord.formatItemType(MoneyWiseXQIFSecurity.XQIF_ITEM, myBuilder);

            /* Format the record */
            mySecurity.formatRecord(theFormatter, myBuilder);

            /* Write Security record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Report the progress */
            theReport.setNextStep();
        }

        /* Set AutoSwitch */
        MoneyWiseXQIFRecord.setSwitch(XQIF_AUTOSWITCH, myBuilder);

        /* Write Securities header */
        pStream.write(myBuilder.toString());
        myBuilder.setLength(0);
    }

    /**
     * Write Prices.
     *
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeEvents(final MoneyWiseXQIFStreamWriter pStream) throws OceanusException {
        /* Create string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Update status bar */
        theReport.setNewStage("Writing account events");
        theReport.setNumSteps(theFile.numAccounts());

        /* Loop through the accounts */
        final Iterator<MoneyWiseXQIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFAccountEvents myEvents = myIterator.next();
            final MoneyWiseXQIFAccount myAccount = myEvents.getAccount();

            /* Format Item Type header */
            MoneyWiseXQIFRecord.formatHeader(MoneyWiseXQIFAccount.XQIF_HDR, myBuilder);

            /* Format the record */
            myAccount.formatRecord(theFormatter, myBuilder);

            /* Format Item Type */
            MoneyWiseXQIFRecord.formatItemType(myAccount.getType(), myBuilder);

            /* Write Account record */
            pStream.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Loop through the events */
            final Iterator<MoneyWiseXQIFEventRecord<?>> myEvtIterator = myEvents.eventIterator();
            while (myEvtIterator.hasNext()) {
                final MoneyWiseXQIFEventRecord<?> myEvent = myEvtIterator.next();

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
     *
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writePrices(final MoneyWiseXQIFStreamWriter pStream) throws OceanusException {
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
        final Iterator<MoneyWiseXQIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFSecurityPrices myPriceList = myIterator.next();

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
