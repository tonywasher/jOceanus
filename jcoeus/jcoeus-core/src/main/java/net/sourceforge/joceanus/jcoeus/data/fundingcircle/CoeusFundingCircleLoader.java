/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.fundingcircle;

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
 * FundingCircle Loader.
 */
public class CoeusFundingCircleLoader {
    /**
     * The Suffix.
     */
    private static final String SUFFIX = ".csv";

    /**
     * The Date multiplier.
     */
    private static final int MULTIPLIER = 100;

    /**
     * The Loan book name.
     */
    private static final String LOANBOOK = "loan-parts-report";

    /**
     * The Bids book name.
     */
    private static final String BIDSBOOK = "bids-report";

    /**
     * The Debt book name.
     */
    private static final String DEBTBOOK = "baddebts-report";

    /**
     * The StatementPrefix.
     */
    private static final String PREFIX = "statement_";

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
     * @throws OceanusException on error
     */
    public CoeusFundingCircleLoader(final MetisDataFormatter pFormatter,
                                    final String pPath) throws OceanusException {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Adjust and store the path */
        FileSystem mySystem = FileSystems.getDefault();
        String myPath = pPath + mySystem.getSeparator() + CoeusMarketProvider.FUNDINGCIRCLE;
        theBasePath = mySystem.getPath(myPath);
    }

    /**
     * Obtain sorted list of statements.
     * @return the list of statements
     * @throws OceanusException on error
     */
    private List<StatementRecord> listStatements() throws OceanusException {
        /* Create list and formatter */
        List<StatementRecord> myList = new ArrayList<>();
        DateTimeFormatter myFormatter = DateTimeFormatter.ofPattern(DATEPATTERN);

        /* Loop through statement file in the directory */
        try (DirectoryStream<Path> myStream = Files.newDirectoryStream(theBasePath, MASK)) {
            for (Path myFile : myStream) {
                /* Parse the file name */
                String myName = myFile.getFileName().toString();
                String myBase = myName.substring(0, myName.length() - SUFFIX.length());
                myBase = myBase.substring(PREFIX.length());
                TemporalAccessor myTA = myFormatter.parse(myBase);
                int myDate = myTA.get(ChronoField.YEAR) * MULTIPLIER;
                myDate += myTA.get(ChronoField.MONTH_OF_YEAR);

                /* Create a statement record */
                StatementRecord myStatement = new StatementRecord(myDate, myFile);
                myList.add(myStatement);
            }

            /* Catch exceptions */
        } catch (IOException e1) {
            throw new CoeusDataException("Failed to read directory", e1);
        }

        /* Sort and return the list */
        myList.sort((p, q) -> p.theDate - q.theDate);
        return myList;
    }

    /**
     * Load market.
     * @return the market
     * @throws OceanusException on error
     */
    public CoeusFundingCircleMarket loadMarket() throws OceanusException {
        /* Create the market */
        CoeusFundingCircleMarket myMarket = new CoeusFundingCircleMarket(theFormatter);

        /* Parse the loanBook file */
        myMarket.parseLoanBook(theBasePath.resolve(LOANBOOK + SUFFIX));

        /* Parse the bidsBook file */
        myMarket.parseBidBook(theBasePath.resolve(BIDSBOOK + SUFFIX));

        /* Parse the badDebt file */
        myMarket.parseBadDebtBook(theBasePath.resolve(DEBTBOOK + SUFFIX));

        /* Loop through the statements */
        for (StatementRecord myStatement : listStatements()) {
            /* Parse the statement */
            myMarket.parseStatement(myStatement.theStatement);
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
        private StatementRecord(final int pDate,
                                final Path pStatement) {
            theDate = pDate;
            theStatement = pStatement;
        }
    }
}