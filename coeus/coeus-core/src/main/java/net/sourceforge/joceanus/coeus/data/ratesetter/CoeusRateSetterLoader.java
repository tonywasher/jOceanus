/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.coeus.data.ratesetter;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.coeus.CoeusDataException;
import net.sourceforge.joceanus.coeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * RateSetter Loader.
 */
public class CoeusRateSetterLoader {
    /**
     * The CSV Suffix.
     */
    private static final String CSV_SUFFIX = ".csv";

    /**
     * The HTML Suffix.
     */
    private static final String HTML_SUFFIX = ".html";

    /**
     * The Date multiplier.
     */
    private static final int MULTIPLIER = 100;

    /**
     * The Active Loan book name.
     */
    private static final String LOANBOOK = "ActiveLoans";

    /**
     * The StatementPrefix.
     */
    private static final String STMT_PREFIX = "LenderTransactions";

    /**
     * The StatementMask.
     */
    private static final String STMT_MASK = STMT_PREFIX + "*" + CSV_SUFFIX;

    /**
     * The StatementDatePattern.
     */
    private static final String STMT_DATEPATTERN = "yyyy-MM-dd";

    /**
     * The LoanPrefix.
     */
    private static final String LOAN_PREFIX = "ClosedLoans";

    /**
     * The LoanMask.
     */
    private static final String LOAN_MASK = LOAN_PREFIX + "*" + HTML_SUFFIX;

    /**
     * The LoanDatePattern.
     */
    private static final String LOAN_DATEPATTERN = "yyyy-MM";

    /**
     * The formatter.
     */
    private final TethysUIDataFormatter theFormatter;

    /**
     * The base path.
     */
    private final Path theBasePath;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pPath the path to load from
     */
    public CoeusRateSetterLoader(final TethysUIDataFormatter pFormatter,
                                 final String pPath) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Adjust and store the path */
        final FileSystem mySystem = FileSystems.getDefault();
        final String myPath = pPath + mySystem.getSeparator() + CoeusMarketProvider.RATESETTER;
        theBasePath = mySystem.getPath(myPath);
    }

    /**
     * Obtain sorted list of statements.
     * @return the list of statements
     * @throws OceanusException on error
     */
    private List<FileRecord> listStatements() throws OceanusException {
        /* Create list and formatter */
        final List<FileRecord> myList = new ArrayList<>();
        final DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern(STMT_DATEPATTERN);

        /* Loop through statement file in the directory */
        try (DirectoryStream<Path> myStream = Files.newDirectoryStream(theBasePath, STMT_MASK)) {
            for (final Path myFile : myStream) {
                /* Skip null entries */
                final Path myFileName = myFile.getFileName();
                if (myFileName == null) {
                    continue;
                }

                /* Parse the file name */
                final String myName = myFileName.toString();
                String myBase = myName.substring(0, myName.length() - CSV_SUFFIX.length());
                myBase = myBase.substring(STMT_PREFIX.length());
                final TemporalAccessor myTA = myFormatter.parse(myBase);
                int myDate = myTA.get(ChronoField.YEAR) * MULTIPLIER * MULTIPLIER;
                myDate += myTA.get(ChronoField.MONTH_OF_YEAR) * MULTIPLIER;
                myDate += myTA.get(ChronoField.DAY_OF_MONTH);

                /* Create a statement record */
                final FileRecord myStatement = new FileRecord(myDate, myFile);
                myList.add(myStatement);
            }

            /* Catch exceptions */
        } catch (IOException e1) {
            throw new CoeusDataException("Failed to read directory", e1);
        }

        /* Sort and return the list */
        myList.sort((p, q) -> p.getDate() - q.getDate());
        return myList;
    }

    /**
     * Obtain sorted list of closed loans.
     * @return the list of statements
     * @throws OceanusException on error
     */
    private List<FileRecord> listClosedLoans() throws OceanusException {
        /* Create list and formatter */
        final List<FileRecord> myList = new ArrayList<>();
        final DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern(LOAN_DATEPATTERN);

        /* Loop through statement file in the directory */
        try (DirectoryStream<Path> myStream = Files.newDirectoryStream(theBasePath, LOAN_MASK)) {
            for (final Path myFile : myStream) {
                /* Skip null entries */
                final Path myFileName = myFile.getFileName();
                if (myFileName == null) {
                    continue;
                }

                /* Parse the file name */
                final String myName = myFileName.toString();
                String myBase = myName.substring(0, myName.length() - HTML_SUFFIX.length());
                myBase = myBase.substring(LOAN_PREFIX.length());
                final TemporalAccessor myTA = myFormatter.parse(myBase);
                int myDate = myTA.get(ChronoField.YEAR) * MULTIPLIER;
                myDate += myTA.get(ChronoField.MONTH_OF_YEAR);

                /* Create a statement record */
                final FileRecord myStatement = new FileRecord(myDate, myFile);
                myList.add(myStatement);
            }

            /* Catch exceptions */
        } catch (IOException e1) {
            throw new CoeusDataException("Failed to read directory", e1);
        }

        /* Sort and return the list */
        myList.sort((p, q) -> p.getDate() - q.getDate());
        return myList;
    }

    /**
     * Load market.
     * @return the market
     * @throws OceanusException on error
     */
    public CoeusRateSetterMarket loadMarket() throws OceanusException {
        /* Create the market */
        final CoeusRateSetterMarket myMarket = new CoeusRateSetterMarket(theFormatter);

        /* Parse the loanBook file */
        myMarket.parseLoanBook(theBasePath.resolve(LOANBOOK + HTML_SUFFIX));

        /* Loop through the closed loans */
        for (final FileRecord myLoans : listClosedLoans()) {
            /* Parse the loans */
            myMarket.parseLoanBook(myLoans.getFile());
        }

        /* Loop through the statements */
        for (final FileRecord myStatement : listStatements()) {
            /* Parse the statement */
            myMarket.parseStatement(myStatement.getFile());
        }

        /* Return the market */
        return myMarket;
    }

    /**
     * File record class.
     */
    private static final class FileRecord {
        /**
         * Date.
         */
        private final int theDate;

        /**
         * File.
         */
        private final Path theFile;

        /**
         * Constructor.
         * @param pDate the date
         * @param pFile the file
         */
        FileRecord(final int pDate,
                   final Path pFile) {
            theDate = pDate;
            theFile = pFile;
        }

        /**
         * Obtain the date.
         * @return the date
         */
        int getDate() {
            return theDate;
        }

        /**
         * Obtain the file.
         * @return the file
         */
        Path getFile() {
            return theFile;
        }
    }
}
