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

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.parser.TethysCSVParser;

/**
 * SourceMeter Package parser.
 */
public class ThemisSMPackageParser
        extends TethysCSVParser {
    /**
     * Base for file name.
     */
    private static final String SUFFIX = "-Package.csv";

    /**
     * RootPackage.
     */
    private static final String ROOT = "<root_package>";

    /**
     * Header fields.
     */
    private static final String[] HEADERS = {
            "ID", "Name", "LongName", "Parent", "Component",
            "CC", "CCL", "CCO", "CI", "CLC", "CLLC", "LDC", "LLDC", "AD", "CD",
            "CLOC", "PDA", "PUA", "TAD", "TCD", "TCLOC", "TPDA", "TPUA", "LLOC",
            "LOC", "NA", "NCL", "NEN", "NG", "NIN", "NM", "NPA", "NPKG", "NPM", "NS",
            "TLLOC", "TLOC", "TNA", "TNCL", "TNDI", "TNEN", "TNFI", "TNG", "TNIN",
            "TNM", "TNOS", "TNPA", "TNPCL", "TNPEN", "TNPIN", "TNPKG", "TNPM", "TNS",
            "WarningBlocker", "WarningCritical", "WarningInfo", "WarningMajor", "WarningMinor",
            "Best Practice Rules", "Code Style Rules", "Design Rules", "Documentation Rules",
            "Error Prone Rules", "Multithreading Rules", "Performance Rules", "Runtime Rules",
            "Security Rules"
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
    ThemisSMPackageParser(final ThemisSMStatistics pStats) {
        /* Initialise underlying class */
        super(pStats.getFormatter(), HEADERS);

        /* Store the stats */
        theStats = pStats;
    }

    /**
     * Parse the statistics file.
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
        /* Obtain the id and name */
        final Iterator<String> myIterator = pFields.iterator();
        final String myId = myIterator.next();
        myIterator.next();
        final String myName = myIterator.next();

        /* Ignore the root package */
        if (!ROOT.equals(myName)) {
            /* Create the package holder */
            final ThemisSMPackage myPackage = new ThemisSMPackage(myId, myName);
            theStats.registerPackage(myPackage);

            /* parse the statistics */
            theStats.processStatistics(myPackage, HEADERS, pFields);
        }
    }
}
