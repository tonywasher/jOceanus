/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.parser.proj;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeCompilationUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Analysis representation of a java file.
 */
public class ThemisXAnalysisFile {
    /**
     * The java suffix.
     */
    public static final String SFX_JAVA = ".java";

    /**
     * The location of the file.
     */
    private final File theLocation;

    /**
     * The name of the file.
     */
    private final String theName;

    /**
     * The contents.
     */
    private ThemisXAnalysisNodeCompilationUnit theContents;

    /**
     * The class list.
     */
    private List<ThemisXAnalysisClassInstance> theClasses;

    /**
     * Constructor.
     * @param pFile the file to analyse
     */
    ThemisXAnalysisFile(final File pFile) {
        /* Store the parameters */
        theLocation = pFile;
        theName = pFile.getName().replace(SFX_JAVA, "");
        theClasses = new ArrayList<>();
    }

    /**
     * Obtain the name of the fileClass.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the location of the fileClass.
     * @return the location
     */
    public String getLocation() {
        return theLocation.getAbsolutePath();
    }

    /**
     * Obtain the contents.
     * @return the contents
     */
    public ThemisXAnalysisNodeCompilationUnit getContents() {
        return theContents;
    }

    /**
     * Obtain the classList.
     * @return the classList
     */
    public List<ThemisXAnalysisClassInstance> getClasses() {
        return theClasses;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Process the file.
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void parseJavaCode(final ThemisXAnalysisParserDef pParser) throws OceanusException {
        /* Set the current file */
        pParser.setCurrentFile(theLocation);

        /* Parse the file */
        theContents = (ThemisXAnalysisNodeCompilationUnit) pParser.parseJavaFile();

        /* Check that we have a class that is the same name as the file */
        final ThemisXAnalysisClassInstance myClass = theContents.getContents();
        if (!theName.equals(myClass.getName())) {
            throw pParser.buildException("Incorrect name for class in file", ((ThemisXAnalysisNodeInstance) myClass).getNode());
        }

        /* Obtain a copy of the classList from the parser */
        theClasses.addAll(pParser.getClasses());
     }
}
