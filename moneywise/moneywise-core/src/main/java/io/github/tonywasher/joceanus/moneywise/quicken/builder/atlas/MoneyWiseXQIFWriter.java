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

import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseIOException;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccount;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFCurrency;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFCurrencyRates;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventRecord;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFParentCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPrice;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRate;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRecord;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFSecurity;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFSecurityPrices;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * CSV Separator.
     */
    private static final String XCSV_SEPARATOR = "\",\"";

    /**
     * AutoSwitch option.
     */
    private static final String XQIF_AUTOSWITCH = "AutoSwitch";

    /**
     * Number of stages.
     */
    private static final int NUM_STAGES = 4;

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
     * Create the QIF Files.
     *
     * @param pLocation pLocation
     * @param pName     the name of the QIF fileSet
     * @throws OceanusException on error
     */
    public void writeFiles(final File pLocation,
                           final String pName) throws OceanusException {
        /* Update status bar */
        theReport.setNumStages(NUM_STAGES);

        /* Ensure that the directory exists */
        final File myLocation = new File(pLocation, pName);
        ensureDirectory(myLocation);

        /* Write the base file */
        writeBaseFile(myLocation);

        /* Write the account files */
        writeAccountEvents(myLocation);

        /* Write the price files */
        if (theFile.numSecurities() > 0) {
            writePrices(myLocation);
        }

        /* Write the rates files */
        if (theFile.numCurrencies() > 0) {
            writeRates(myLocation);
        }
    }

    /**
     * Create the Base QIF File.
     *
     * @param pLocation pLocation
     * @throws OceanusException on error
     */
    private void writeBaseFile(final File pLocation) throws OceanusException {
        /* Ensure that the directory exists */
        final File myLocation = new File(pLocation, "base");
        ensureDirectory(myLocation);

        /* Determine name of the file */
        final File myFile = new File(myLocation, "Base.qif");

        /* Protect against exceptions */
        try (MoneyWiseXQIFStreamWriter myWriter = new MoneyWiseXQIFStreamWriter(myFile)) {
            /* Update status bar */
            theReport.setNewStage("Writing base details");
            theReport.setNumSteps(1);

            /* Write the classes */
            writeClasses(myWriter);

            /* Write the categories */
            writeCategories(myWriter);

            /* Write the accounts */
            writeAccounts(myWriter);

            /* Write the securities */
            writeSecurities(myWriter);

            /* Report the progress */
            theReport.setNextStep();

        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to write to file: " + myFile.getName(), e);
        }
    }

    /**
     * Create the Account QIF Files.
     *
     * @param pLocation pLocation
     * @throws OceanusException on error
     */
    private void writeAccountEvents(final File pLocation) throws OceanusException {
        /* Ensure that the directory exists */
        final File myLocation = new File(pLocation, "accounts");
        ensureDirectory(myLocation);

        /* Update status bar */
        theReport.setNewStage("Writing account events");
        theReport.setNumSteps(theFile.numAccounts());

        /* Loop through the accounts */
        final Iterator<MoneyWiseXQIFAccountEvents> myIterator = theFile.accountIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFAccountEvents myEvents = myIterator.next();

            /* Create the account QIF file */
            writeAccountEvents(myLocation, myEvents);

            /* Report the progress */
            theReport.setNextStep();
        }
    }

    /**
     * Create the Account QIF File.
     *
     * @param pLocation pLocation
     * @param pEvents   the events
     * @throws OceanusException on error
     */
    private void writeAccountEvents(final File pLocation,
                                    final MoneyWiseXQIFAccountEvents pEvents) throws OceanusException {
        /* Determine the name of the file */
        final MoneyWiseXQIFAccount myAccount = pEvents.getAccount();
        final File myFile = new File(pLocation, myAccount.getName() + ".qif");

        /* Protect against exceptions */
        try (MoneyWiseXQIFStreamWriter myWriter = new MoneyWiseXQIFStreamWriter(myFile)) {
            /* Create string builder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Format Item Type header */
            MoneyWiseXQIFRecord.formatHeader(MoneyWiseXQIFAccount.XQIF_HDR, myBuilder);

            /* Format the record */
            myAccount.formatRecord(theFormatter, myBuilder);

            /* Format Item Type */
            MoneyWiseXQIFRecord.formatItemType(myAccount.getType(), myBuilder);

            /* Write Account record */
            myWriter.write(myBuilder.toString());
            myBuilder.setLength(0);

            /* Loop through the events */
            final Iterator<MoneyWiseXQIFEventRecord<?>> myEvtIterator = pEvents.eventIterator();
            while (myEvtIterator.hasNext()) {
                final MoneyWiseXQIFEventRecord<?> myEvent = myEvtIterator.next();

                /* Format the record */
                myEvent.formatRecord(theFormatter, myBuilder);

                /* Write Event record */
                myWriter.write(myBuilder.toString());
                myBuilder.setLength(0);
            }

        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to write to file: " + myFile.getName(), e);
        }
    }

    /**
     * Create the Prices QIF Files.
     *
     * @param pLocation pLocation
     * @throws OceanusException on error
     */
    private void writePrices(final File pLocation) throws OceanusException {
        /* Ensure that the directory exists */
        final File myLocation = new File(pLocation, "prices");
        ensureDirectory(myLocation);

        /* Update status bar */
        theReport.setNewStage("Writing prices");
        theReport.setNumSteps(theFile.numSecurities());

        /* Loop through the prices */
        final Iterator<MoneyWiseXQIFSecurityPrices> myIterator = theFile.securityIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFSecurityPrices myPrices = myIterator.next();

            /* Create the prices CSV file */
            writePrices(myLocation, myPrices);

            /* Report the progress */
            theReport.setNextStep();
        }
    }

    /**
     * Create the Price CSV File.
     *
     * @param pLocation pLocation
     * @param pPrices   the prices
     * @throws OceanusException on error
     */
    private void writePrices(final File pLocation,
                             final MoneyWiseXQIFSecurityPrices pPrices) throws OceanusException {
        /* Determine the name of the file */
        final MoneyWiseXQIFSecurity mySecurity = pPrices.getSecurity();
        final File myFile = new File(pLocation, mySecurity.getName() + ".csv");

        /* Protect against exceptions */
        try (MoneyWiseXQIFStreamWriter myWriter = new MoneyWiseXQIFStreamWriter(myFile)) {
            /* Create string builder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Write the header */
            writeHeaderToCSV(myWriter);

            /* Loop through the prices */
            for (MoneyWiseXQIFPrice myPrice : pPrices.getPrices()) {
                /* Build the line */
                writeValueToCSV(myBuilder, myPrice.getDate(), myPrice.getPrice());

                /* Write the line */
                myWriter.write(myBuilder.toString());
                myBuilder.setLength(0);
            }


        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to write to file: " + myFile.getName(), e);
        }
    }

    /**
     * Create the Rates QIF Files.
     *
     * @param pLocation pLocation
     * @throws OceanusException on error
     */
    private void writeRates(final File pLocation) throws OceanusException {
        /* Ensure that the directory exists */
        final File myLocation = new File(pLocation, "rates");
        ensureDirectory(myLocation);

        /* Update status bar */
        theReport.setNewStage("Writing rates");
        theReport.setNumSteps(theFile.numSecurities());

        /* Loop through the rates */
        final Iterator<MoneyWiseXQIFCurrencyRates> myIterator = theFile.currencyIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXQIFCurrencyRates myRates = myIterator.next();

            /* Create the rates CSV file */
            writeRates(myLocation, myRates);

            /* Report the progress */
            theReport.setNextStep();
        }
    }

    /**
     * Create the Rates CSV File.
     *
     * @param pLocation pLocation
     * @param pRates    the rates
     * @throws OceanusException on error
     */
    private void writeRates(final File pLocation,
                            final MoneyWiseXQIFCurrencyRates pRates) throws OceanusException {
        /* Determine the name of the file */
        final MoneyWiseXQIFCurrency myCurrency = pRates.getCurrency();
        final File myFile = new File(pLocation, myCurrency.getName() + ".csv");

        /* Protect against exceptions */
        try (MoneyWiseXQIFStreamWriter myWriter = new MoneyWiseXQIFStreamWriter(myFile)) {
            /* Create string builder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Write the header */
            writeHeaderToCSV(myWriter);

            /* Loop through the rates */
            for (MoneyWiseXQIFRate myRate : pRates.getRates()) {
                /* Build the line */
                writeValueToCSV(myBuilder, myRate.getDate(), myRate.getRate());

                /* Write the line */
                myWriter.write(myBuilder.toString());
                myBuilder.setLength(0);
            }


        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to write to file: " + myFile.getName(), e);
        }
    }

    /**
     * Write price/rate value to CSV.
     *
     * @param pWriter the writer
     * @throws OceanusException on error
     */
    private void writeHeaderToCSV(final MoneyWiseXQIFStreamWriter pWriter) throws OceanusException {
        pWriter.write("\"Date\", \"High\", \"Low\", \"Close\", \"Volume\"\n");
    }

    /**
     * Write price/rate value to CSV.
     *
     * @param pBuilder the Builder
     * @param pDate    the date
     * @param pValue   the value
     */
    private void writeValueToCSV(final StringBuilder pBuilder,
                                 final OceanusDate pDate,
                                 final OceanusDecimal pValue) {
        pBuilder.append("\"");
        pBuilder.append(theFormatter.formatObject(pDate));
        pBuilder.append(XCSV_SEPARATOR);
        pBuilder.append(pValue);
        pBuilder.append(XCSV_SEPARATOR);
        pBuilder.append(pValue);
        pBuilder.append(XCSV_SEPARATOR);
        pBuilder.append(pValue);
        pBuilder.append(XCSV_SEPARATOR + "0.0\"\n");
    }

    /**
     * Ensure output directory.
     *
     * @param pDirectory the directory
     * @throws OceanusException on error
     */
    private static void ensureDirectory(final File pDirectory) throws OceanusException {
        try {
            final Path myDir = pDirectory.toPath();
            if (!myDir.toFile().exists()) {
                Files.createDirectories(myDir);
            }
        } catch (IOException e) {
            throw new MoneyWiseIOException("Failed to create directory: " + pDirectory.getAbsolutePath(), e);
        }
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
}
