/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.lendingworks;

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

import net.sourceforge.joceanus.jcoeus.CoeusDataException;
import net.sourceforge.joceanus.jcoeus.data.CoeusMarketProvider;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * LendingWorks Loader.
 */
public class CoeusLendingWorksLoader {
    /**
     * The Suffix.
     */
    private static final String SUFFIX = ".csv";

    /**
     * The Statement name.
     */
    private static final String PREFIX = "transactions-";

    /**
     * The StatementMask.
     */
    private static final String MASK = PREFIX + "*" + SUFFIX;

    /**
     * The StatementDatePattern.
     */
    private static final String DATEPATTERN = "yyyy-MM";

    /**
     * The formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * The base path.
     */
    private final Path theBasePath;

    /**
     * Constructor.
     * @param pFormatter the formatter
     * @param pPath the path to load from
     */
    public CoeusLendingWorksLoader(final MetisDataFormatter pFormatter,
                                   final String pPath) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Adjust and store the path */
        final FileSystem mySystem = FileSystems.getDefault();
        final String myPath = pPath + mySystem.getSeparator() + CoeusMarketProvider.LENDINGWORKS;
        theBasePath = mySystem.getPath(myPath);
    }

    /**
     * Obtain sorted list of statements.
     * @return the list of statements
     * @throws OceanusException on error
     */
    private List<StatementRecord> listStatements() throws OceanusException {
        /* Create list and formatter */
        final List<StatementRecord> myList = new ArrayList<>();
        final DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern(DATEPATTERN);

        /* Loop through statement file in the directory */
        try (DirectoryStream<Path> myStream = Files.newDirectoryStream(theBasePath, MASK)) {
            for (final Path myFile : myStream) {
                /* Skip null entries */
                final Path myFileName = myFile.getFileName();
                if (myFileName == null) {
                    continue;
                }

                /* Parse the file name */
                final String myName = myFileName.toString();
                String myBase = myName.substring(0, myName.length() - SUFFIX.length());
                myBase = myBase.substring(PREFIX.length());
                final TemporalAccessor myTA = myFormatter.parse(myBase);
                final int myDate = myTA.get(ChronoField.YEAR);

                /* Create a statement record */
                final StatementRecord myStatement = new StatementRecord(myDate, myFile);
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
    public CoeusLendingWorksMarket loadMarket() throws OceanusException {
        /* Create the market */
        final CoeusLendingWorksMarket myMarket = new CoeusLendingWorksMarket(theFormatter);

        /* Loop through the statements */
        for (final StatementRecord myStatement : listStatements()) {
            /* Parse the statement */
            myMarket.parseStatement(myStatement.getStatement());
        }

        /* Return the market */
        return myMarket;
    }

    /**
     * Statement record class.
     */
    private static final class StatementRecord {
        /**
         * Date.
         */
        private final int theDate;

        /**
         * Statement.
         */
        private final Path theStatement;

        /**
         * Constructor.
         * @param pDate the date
         * @param pStatement the statement
         */
        StatementRecord(final int pDate,
                        final Path pStatement) {
            theDate = pDate;
            theStatement = pStatement;
        }

        /**
         * Obtain the date.
         * @return the date
         */
        int getDate() {
            return theDate;
        }

        /**
         * Obtain the statement.
         * @return the statement
         */
        Path getStatement() {
            return theStatement;
        }
    }
}
