/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.data.zopa;

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
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Zopa Loader.
 */
public class CoeusZopaLoader {
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
    private static final String LOANBOOK = "my_all_time_loan_book";

    /**
     * The Bad debt book name.
     */
    private static final String DEBTBOOK = "my_baddebt_loan_book";

    /**
     * The StatementPrefix.
     */
    private static final String PREFIX = "ZopaStatement";

    /**
     * The StatementMask.
     */
    private static final String MASK = PREFIX + "*" + SUFFIX;

    /**
     * The StatementDatePattern.
     */
    private static final String DATEPATTERN = " MMMM yyyy";

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
    public CoeusZopaLoader(final MetisDataFormatter pFormatter,
                           final String pPath) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Adjust and store the path */
        final FileSystem mySystem = FileSystems.getDefault();
        final String myPath = pPath + mySystem.getSeparator() + CoeusMarketProvider.ZOPA;
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
            for (Path myFile : myStream) {
                /* Parse the file name */
                final String myName = myFile.getFileName().toString();
                String myBase = myName.substring(0, myName.length() - SUFFIX.length());
                myBase = myBase.substring(PREFIX.length());
                final TemporalAccessor myTA = myFormatter.parse(myBase);
                int myDate = myTA.get(ChronoField.YEAR) * MULTIPLIER;
                myDate += myTA.get(ChronoField.MONTH_OF_YEAR);

                /* Create a statement record */
                final StatementRecord myStatement = new StatementRecord(myDate, myFile);
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
    public CoeusZopaMarket loadMarket() throws OceanusException {
        /* Create the market */
        final CoeusZopaMarket myMarket = new CoeusZopaMarket(theFormatter);

        /* Parse the loanBook file */
        myMarket.parseLoanBook(theBasePath.resolve(LOANBOOK + SUFFIX));

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
