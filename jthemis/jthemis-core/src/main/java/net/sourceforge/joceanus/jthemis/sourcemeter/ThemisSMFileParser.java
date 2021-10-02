/* *****************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jthemis.sourcemeter;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.parser.MetisCSVParser;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SourceMeter File Parser.
 */
public class ThemisSMFileParser
        extends MetisCSVParser {
    /**
     * Base for file name.
     */
    private static final String SUFFIX = "-File.csv";

    /**
     * Header fields.
     */
    private static final String[] HEADERS = {
            "ID", "Name", "LongName", "Parent",
            "McCC", "CLOC", "PDA", "PUA", "LLOC", "LOC"
    };

    /**
     * Stats.
     */
    private final ThemisSMStatistics theStats;

    /**
     * Constructor.
     *
     * @param pStats the stats
     */
    ThemisSMFileParser(final ThemisSMStatistics pStats) {
        /* Initialise underlying class */
        super(pStats.getFormatter(), HEADERS);

        /* Store the stats */
        theStats = pStats;
    }

    /**
     * Parse the loanBook file.
     * @param pBase the base directory for the stats
     * @param pProject the project name
     * @throws OceanusException on error
     */
    void parseStatistics(final Path pBase,
                         final String pProject) throws OceanusException {
        /* Determine the fileName */
        final Path myFile = new File(pBase.toFile(), pProject + SUFFIX).toPath();

        /* Parse the file */
        parseFile(myFile);
    }

    @Override
    protected void resetFields() {
        /* NoOp */
    }

    @Override
    protected void processFields(final List<String> pFields) throws OceanusException {
        /* Obtain the id, name and path */
        final Iterator<String> myIterator = pFields.iterator();
        final String myId = myIterator.next();
        final String myName = myIterator.next();
        final String myPath = myIterator.next();

        /* Create the package holder */
        final ThemisSMFile myFile = new ThemisSMFile(myId, myName, myPath);
        theStats.registerFile(myFile);

        /* parse the statistics */
        theStats.processStatistics(myFile, HEADERS, pFields);
    }
}
