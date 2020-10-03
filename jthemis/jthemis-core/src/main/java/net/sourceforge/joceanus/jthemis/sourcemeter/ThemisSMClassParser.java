/* *****************************************************************************
 * Themis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.sourcemeter;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.parser.TethysCSVParser;
import net.sourceforge.joceanus.jthemis.ThemisDataException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisChar;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMClass.ThemisSMClassType;

/**
 * SourceMeter Class Parser.
 */
public class ThemisSMClassParser
        extends TethysCSVParser {
    /**
     * Base for Class file name.
     */
    private static final String CLASS_SUFFIX = "-Class.csv";

    /**
     * Base for interface file name.
     */
    private static final String IFC_SUFFIX = "-Interface.csv";

    /**
     * Base for Enum file name.
     */
    private static final String ENUM_SUFFIX = "-Enum.csv";

    /**
     * Base for Annotation file name.
     */
    private static final String ANNOTATION_SUFFIX = "-Annotation.csv";

    /**
     * Header fields.
     */
    private static final String[] HEADERS = {
            "ID", "Name", "LongName", "Parent", "Component", "Path",
            "Line", "Column", "EndLine", "EndColumn",
            "CC", "CCL", "CCO", "CI", "CLC", "CLLC", "LDC", "LLDC", "LCOM5",
            "NL", "NLE", "WMC", "CBO", "CBOI", "NII", "NOI", "RFC", "AD", "CD",
            "CLOC", "DLOC", "PDA", "PUA", "TCD", "TCLOC", "DIT", "NOA", "NOC",
            "NOD", "NOP", "LLOC", "LOC", "NA", "NG", "NLA", "NLG", "NLM", "NLPA",
            "NLPM", "NLS", "NM", "NOS", "NPA", "NPM", "NS", "TLLOC", "TLOC", "TNA",
            "TNG", "TNLA", "TNLG", "TNLM", "TNLPA", "TNLPM", "TNLS", "TNM", "TNOS",
            "TNPA", "TNPM", "TNS",
            "WarningBlocker", "WarningCritical", "WarningInfo", "WarningMajor", "WarningMinor",
            "Best Practice Rules", "Clone Metric Rules", "Code Style Rules", "Cohesion Metric Rules",
            "Complexity Metric Rules", "Coupling Metric Rules", "Design Rules", "Documentation Metric Rules",
            "Documentation Rules", "Error Prone Rules", "Inheritance Metric Rules", "Multithreading Rules",
            "Performance Rules", "Runtime Rules", "Security Rules", "Size Metric Rules"
    };

    /**
     * Stats.
     */
    private final ThemisSMStatistics theStats;

    /**
     * The Class Type.
     */
    private final ThemisSMClassType theClassType;

    /**
     * Constructor.
     *
     * @param pStats the stats
     * @param pClassType the classType
     */
    ThemisSMClassParser(final ThemisSMStatistics pStats,
                        final ThemisSMClassType pClassType) {
        /* Initialise underlying class */
        super(pStats.getFormatter(), HEADERS);

        /* Store parameters */
        theStats = pStats;
        theClassType = pClassType;
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
        final Path myFile = new File(pBase.toFile(), pProject + getSuffix()).toPath();

        /* Parse the file */
        parseFile(myFile);
    }

    @Override
    protected void resetFields() {
        /* NoOp */
    }

    @Override
    protected void processFields(final List<String> pFields) throws OceanusException {
        /* Obtain the id */
        final Iterator<String> myIterator = pFields.iterator();
        final String myId = myIterator.next();

        /* Obtain the sanitised name */
        myIterator.next();
        final String myBaseName = myIterator.next();
        final String myName = myBaseName.replace('$', ThemisAnalysisChar.PERIOD);

        /* Access the containing fileName */
        final String myParentId = myIterator.next();
        myIterator.next();
        final String myFileName = myIterator.next().replace('\\', '/');

        /* find the containing file. */
        final ThemisSMFile myFile = theStats.getFile(myFileName);
        if (myFile == null) {
            throw new ThemisDataException("Unknown file");
        }

        /* find the parent stats holder, and link via file if parent is package. */
        ThemisSMStatHolder myParent = theStats.getHolder(myParentId);
        if (myParent instanceof ThemisSMPackage) {
            myFile.setParent(myParent);
            myParent = myFile;
        }

        /* Create the class */
        final ThemisSMClass myClass = new ThemisSMClass(myParent, theClassType, myId, myName);
        theStats.registerStatHolder(myClass);
        if (myParent != null) {
            myParent.registerChild(myClass);
        } else {
            theStats.registerOrphan(myId, myParentId);
        }

        /* parse the statistics */
        theStats.processStatistics(myClass, HEADERS, pFields);
    }

    /**
     * Obtain the file suffix.
     * @return the suffix
     */
    private String getSuffix() {
        switch (theClassType) {
            case INTERFACE:
                return IFC_SUFFIX;
            case ENUM:
                return ENUM_SUFFIX;
            case ANNOTATION:
                return ANNOTATION_SUFFIX;
            case CLASS:
            default:
                return CLASS_SUFFIX;
        }
    }
}
