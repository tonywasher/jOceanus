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
package net.sourceforge.joceanus.themis.xanalysis;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.ThemisXAnalysisCodeParser;

import java.io.File;

/**
 * Analysis representation of a java file.
 */
public class ThemisXAnalysisFile {
    /**
     * The location of the file.
     */
    private final File theLocation;

    /**
     * The name of the file.
     */
    private final String theName;

    /**
     * The package file.
     */
    private final ThemisXAnalysisPackage thePackage;

    /**
     * The dataMap.
     */
    private final ThemisXAnalysisDataMap theDataMap;

    /**
     * The contents.
     */
    private ThemisXAnalysisNodeInstance theContents;

    /**
     * Constructor.
     * @param pPackage the package
     * @param pFile the file to analyse
     */
    ThemisXAnalysisFile(final ThemisXAnalysisPackage pPackage,
                        final File pFile) {
        /* Store the parameters */
        thePackage = pPackage;
        theLocation = pFile;
        theName = pFile.getName().replace(ThemisXAnalysisPackage.SFX_JAVA, "");
        theDataMap = new ThemisXAnalysisDataMap(thePackage.getDataMap());
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
    public ThemisXAnalysisNodeInstance getContents() {
        return theContents;
    }

    @Override
    public String toString() {
        return theContents == null ? null : theContents.toString();
    }

    /**
     * Process the file.
     * @throws OceanusException on error
     */
    void processFile() throws OceanusException {
        final ThemisXAnalysisCodeParser myParser = new ThemisXAnalysisCodeParser(theLocation, thePackage.getPackage());
        theContents = myParser.parseFile();
    }
}
