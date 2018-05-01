/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QIFType;

/**
 * Class to parse a QIFFile.
 */
public class QIFParser {
    /**
     * The QIFFile being built.
     */
    private final QIFFile theFile;

    /**
     * Data formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Record mode.
     */
    private QIFSection theSection;

    /**
     * Active account.
     */
    private QIFAccountEvents theActive;

    /**
     * is active account portfolio?
     */
    private boolean isPortfolio;

    /**
     * Constructor.
     * @param pFileType the QIF file type.
     */
    public QIFParser(final QIFType pFileType) {
        /* Create new file */
        theFile = new QIFFile(pFileType);

        /* Allocate the formatter and set date format */
        theFormatter = new MetisDataFormatter();
        theFormatter.setFormat(QIFWriter.QIF_DATEFORMAT);
    }

    /**
     * Obtain file.
     * @return the QIF file.
     */
    public QIFFile getFile() {
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
            if (myLine.startsWith(QIFRecord.QIF_CMD)) {
                /* Process the mode */
                processMode(myLine);
                /* If this is an EOI */
            } else if (myLine.equals(QIFRecord.QIF_EOI)) {
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
        if (pLine.startsWith(QIFRecord.QIF_ITEMTYPE)) {
            /* Access the type */
            final String myType = pLine.substring(QIFRecord.QIF_ITEMTYPE.length());

            /* Determine which section this describes */
            final QIFSection mySection = QIFSection.determineType(myType);

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
                    theSection = QIFSection.EVENT;

                    /* Note portfolio */
                    isPortfolio = myType.equals(QIFAccount.QIFACT_INVST);

                    /* Not recognised */
                } else {
                    theSection = null;
                }

                /* Not recognised */
            } else {
                theSection = null;
            }

            /* Handle Account call */
        } else if (pLine.startsWith(QIFAccount.QIF_HDR)) {
            /* Look for account record */
            theSection = QIFSection.ACCOUNT;
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
        final QIFClass myClass = new QIFClass(theFile, pLines);
        theFile.registerClass(myClass);
    }

    /**
     * Process the category record.
     * @param pLines the lines to process
     */
    private void processCategoryRecord(final List<String> pLines) {
        /* Register the category */
        final QIFEventCategory myCategory = new QIFEventCategory(theFile, pLines);
        theFile.registerCategory(myCategory);
    }

    /**
     * Process the account record.
     * @param pLines the lines to process
     */
    private void processAccountRecord(final List<String> pLines) {
        /* Register the account */
        final QIFAccount myAccount = new QIFAccount(theFile, theFormatter, pLines);
        theActive = theFile.registerAccount(myAccount);
    }

    /**
     * Process the security record.
     * @param pLines the lines to process
     */
    private void processSecurityRecord(final List<String> pLines) {
        /* Register the security */
        final QIFSecurity mySecurity = new QIFSecurity(theFile, pLines);
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
            final QIFPortfolioEvent myEvent = new QIFPortfolioEvent(theFile, theFormatter, pLines);
            theActive.addEvent(myEvent);
        } else {
            /* Register the event */
            final QIFEvent myEvent = new QIFEvent(theFile, theFormatter, pLines);
            theActive.addEvent(myEvent);
        }
    }

    /**
     * Process the price record.
     * @param pLines the lines to process
     */
    private void processPriceRecord(final List<String> pLines) {
        /* Register the price */
        final QIFPrice myPrice = new QIFPrice(theFile, theFormatter, pLines);
        theFile.registerPrice(myPrice);
    }

    /**
     * QIF File section.
     */
    private enum QIFSection {
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
        QIFSection(final String pType) {
            theType = pType;
        }

        /**
         * Determine section.
         * @param pLine the line to check
         * @return the section
         */
        private static QIFSection determineType(final String pLine) {
            /* Loop through the values */
            for (QIFSection mySection : QIFSection.values()) {
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
