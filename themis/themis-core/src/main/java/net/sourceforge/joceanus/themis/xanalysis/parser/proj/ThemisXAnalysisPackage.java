/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Package.
 */
public class ThemisXAnalysisPackage {
    /**
     * The package-info file.
     */
    private static final String PACKAGE_INFO = "package-info" + ThemisXAnalysisFile.SFX_JAVA;

    /**
     * The package name.
     */
    private final String thePackage;

    /**
     * The location.
     */
    private final File theLocation;

    /**
     * The list of files in this package.
     */
    private final List<ThemisXAnalysisFile> theFiles;

    /**
     * Constructor.
     * @param pLocation the location
     * @param pPackage the package name.
     * @throws OceanusException on error
     */
    ThemisXAnalysisPackage(final File pLocation,
                           final String pPackage) throws OceanusException {
        /* Store package name */
        theLocation = pLocation;
        thePackage = pPackage;

        /* Create directory path and record the location */
        final String myPath = pPackage.replace(ThemisXAnalysisChar.PERIOD, File.separatorChar);
        final File myLocation = new File(pLocation, myPath);

        /* Build list of files */
        theFiles = listFiles(myLocation);
    }

    /**
     * Obtain the package.
     * @return the package
     */
    public String getPackage() {
        return thePackage;
    }

    /**
     * Obtain the location.
     * @return the location
     */
    public File getLocation() {
        return theLocation;
    }

    /**
     * Obtain the files.
     * @return the files
     */
    public List<ThemisXAnalysisFile> getFiles() {
        return theFiles;
    }

    /**
     * Build list of files.
     * @param pLocation the location
     * @return the list of files
     */
    List<ThemisXAnalysisFile> listFiles(final File pLocation) {
        /* Allocate the list */
        final List<ThemisXAnalysisFile> myClasses = new ArrayList<>();

        /* Loop through the entries in the directory */
        for (File myFile: Objects.requireNonNull(pLocation.listFiles())) {
            /* Handle files */
            if (!myFile.isDirectory()) {
                /* Access the name of the file */
                final String myName = myFile.getName();

                /* If this is a .java that is not package-info */
                if (myName.endsWith(ThemisXAnalysisFile.SFX_JAVA)
                        && !PACKAGE_INFO.equals(myName)) {
                    /* Add the class */
                    final ThemisXAnalysisFile myClass = new ThemisXAnalysisFile(myFile);
                    myClasses.add(myClass);
                }
            }
        }

        /* Return the classes */
        return myClasses;
    }

    /**
     * parse the Java Code.
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void parseJavaCode(final ThemisXAnalysisParserDef pParser) throws OceanusException {
        /* Set the current package */
        pParser.setCurrentPackage(thePackage);

        /* Loop through the classes */
        for (ThemisXAnalysisFile myFile : theFiles) {
            /* Process the file */
            myFile.parseJavaCode(pParser);
        }
    }

    @Override
    public String toString() {
        return thePackage;
    }
}
