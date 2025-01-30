/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to parse a QIFFile.
 */
public class MoneyWiseQIFParser {
    /**
     * The QIFFile being built.
     */
    private final MoneyWiseQIFFile theFile;

    /**
     * Data formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * Record mode.
     */
    private MoneyWiseQIFSection theSection;

    /**
     * Active account.
     */
    private MoneyWiseQIFAccountEvents theActive;

    /**
     * is active account portfolio?
     */
    private boolean isPortfolio;

    /**
     * Constructor.
     * @param pFactory the gui factory
     * @param pFileType the QIF file type.
     */
    public MoneyWiseQIFParser(final TethysUIFactory<?> pFactory,
                              final MoneyWiseQIFType pFileType) {
        /* Create new file */
        theFile = new MoneyWiseQIFFile(pFileType);

        /* Allocate the formatter and set date format */
        theFormatter = pFactory.newDataFormatter();
        theFormatter.setFormat(MoneyWiseQIFWriter.QIF_DATEFORMAT);
    }

    /**
     * Obtain file.
     * @return the QIF file.
     */
    public MoneyWiseQIFFile getFile() {
        return theFile;
    }

    /**
     * Load from Stream.
     * @param pStream the input stream
     * @return continue true/false
     * @throws IOException on error
     */
    public boolean loadFile(final BufferedReader pStream) throws IOException {
        /* List of lines */
        final List<String> myLines = new ArrayList<>();

        /* Loop through the file */
        for (;;) {
            /* Read the next line and break on EOF */
            final String myLine = pStream.readLine();
            if (myLine == null) {
                break;
            }

            /* If this is a command */
            if (myLine.startsWith(MoneyWiseQIFRecord.QIF_CMD)) {
                /* Process the mode */
                processMode(myLine);
                /* If this is an EOI */
            } else if (myLine.equals(MoneyWiseQIFRecord.QIF_EOI)) {
                /* Process the records */
                processRecord(myLines);

                /* else normal line */
            } else {
                /* Ignore blank lines */
                if (myLine.length() > 0) {
                    myLines.add(myLine);
                }
            }
        }

        /* Sort the file lists */
        theFile.sortLists();

        /* Return to the caller */
        return true;
    }

    /**
     * Process the mode.
     * @param pLine the line to process
     */
    private void processMode(final String pLine) {
        /* If the line is a type record */
        if (pLine.startsWith(MoneyWiseQIFRecord.QIF_ITEMTYPE)) {
            /* Access the type */
            final String myType = pLine.substring(MoneyWiseQIFRecord.QIF_ITEMTYPE.length());

            /* Determine which section this describes */
            final MoneyWiseQIFSection mySection = MoneyWiseQIFSection.determineType(myType);

            /* If we found a section */
            if (mySection != null) {
                /* Switch on the section */
                switch (mySection) {
                    case CLASS:
                    case CATEGORY:
                    case SECURITY:
                    case PRICE:
                        theSection = mySection;
                        theActive = null;
                        break;
                    default:
                        theSection = null;
                        break;
                }

                /* else if we have an active account */
            } else if (theActive != null) {
                /* Make sure that the type matches */
                final String myActiveType = theActive.getAccount().getType();
                if (myActiveType.equals(myType)) {
                    /* Set events */
                    theSection = MoneyWiseQIFSection.EVENT;

                    /* Note portfolio */
                    isPortfolio = myType.equals(MoneyWiseQIFAccount.QIFACT_INVST);

                    /* Not recognised */
                } else {
                    theSection = null;
                }

                /* Not recognised */
            } else {
                theSection = null;
            }

            /* Handle Account call */
        } else if (pLine.startsWith(MoneyWiseQIFAccount.QIF_HDR)) {
            /* Look for account record */
            theSection = MoneyWiseQIFSection.ACCOUNT;
        }
    }

    /**
     * Process the record.
     * @param pLines the lines to process
     */
    private void processRecord(final List<String> pLines) {
        /* Switch on the section */
        switch (theSection) {
            case CLASS:
                processClassRecord(pLines);
                break;
            case CATEGORY:
                processCategoryRecord(pLines);
                break;
            case ACCOUNT:
                processAccountRecord(pLines);
                break;
            case SECURITY:
                processSecurityRecord(pLines);
                break;
            case EVENT:
                processEventRecord(pLines);
                break;
            case PRICE:
                processPriceRecord(pLines);
                break;
            default:
                break;
        }

        /* Clear line list */
        pLines.clear();
    }

    /**
     * Process the class record.
     * @param pLines the lines to process
     */
    private void processClassRecord(final List<String> pLines) {
        /* register the class */
        final MoneyWiseQIFClass myClass = new MoneyWiseQIFClass(theFile, pLines);
        theFile.registerClass(myClass);
    }

    /**
     * Process the category record.
     * @param pLines the lines to process
     */
    private void processCategoryRecord(final List<String> pLines) {
        /* Register the category */
        final MoneyWiseQIFEventCategory myCategory = new MoneyWiseQIFEventCategory(theFile, pLines);
        theFile.registerCategory(myCategory);
    }

    /**
     * Process the account record.
     * @param pLines the lines to process
     */
    private void processAccountRecord(final List<String> pLines) {
        /* Register the account */
        final MoneyWiseQIFAccount myAccount = new MoneyWiseQIFAccount(theFile, theFormatter, pLines);
        theActive = theFile.registerAccount(myAccount);
    }

    /**
     * Process the security record.
     * @param pLines the lines to process
     */
    private void processSecurityRecord(final List<String> pLines) {
        /* Register the security */
        final MoneyWiseQIFSecurity mySecurity = new MoneyWiseQIFSecurity(theFile, pLines);
        theFile.registerSecurity(mySecurity);
    }

    /**
     * Process the event record.
     * @param pLines the lines to process
     */
    private void processEventRecord(final List<String> pLines) {
        /* Switch on portfolio */
        if (isPortfolio) {
            /* Register the event */
            final MoneyWiseQIFPortfolioEvent myEvent = new MoneyWiseQIFPortfolioEvent(theFile, theFormatter, pLines);
            theActive.addEvent(myEvent);
        } else {
            /* Register the event */
            final MoneyWiseQIFEvent myEvent = new MoneyWiseQIFEvent(theFile, theFormatter, pLines);
            theActive.addEvent(myEvent);
        }
    }

    /**
     * Process the price record.
     * @param pLines the lines to process
     */
    private void processPriceRecord(final List<String> pLines) {
        /* Register the price */
        final MoneyWiseQIFPrice myPrice = new MoneyWiseQIFPrice(theFile, theFormatter, pLines);
        theFile.registerPrice(myPrice);
    }

    /**
     * QIF File section.
     */
    private enum MoneyWiseQIFSection {
        /**
         * Classes.
         */
        CLASS("Class"),

        /**
         * Category.
         */
        CATEGORY("Cat"),

        /**
         * Account.
         */
        ACCOUNT("Account"),

        /**
         * Security.
         */
        SECURITY("Security"),

        /**
         * EVENT.
         */
        EVENT(null),

        /**
         * Price.
         */
        PRICE("Prices");

        /**
         * Type.
         */
        private final String theType;

        /**
         * Constructor.
         * @param pType the type
         */
        MoneyWiseQIFSection(final String pType) {
            theType = pType;
        }

        /**
         * Determine section.
         * @param pLine the line to check
         * @return the section
         */
        private static MoneyWiseQIFSection determineType(final String pLine) {
            /* Loop through the values */
            for (MoneyWiseQIFSection mySection : MoneyWiseQIFSection.values()) {
                /* If we have a match */
                if (pLine.equals(mySection.theType)) {
                    return mySection;
                }
            }

            /* Not found */
            return null;
        }
    }
}
