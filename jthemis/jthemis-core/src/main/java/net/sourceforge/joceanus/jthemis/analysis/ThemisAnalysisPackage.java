/*******************************************************************************
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
package net.sourceforge.joceanus.jthemis.analysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Package line.
 */
public class ThemisAnalysisPackage
    implements ThemisAnalysisElement {
    /**
     * The java suffix.
     */
    static final String SFX_JAVA = ".java";

    /**
     * The package-info file.
     */
    private static final String PACKAGE_INFO = "package-info" + SFX_JAVA;

    /**
     * The package name.
     */
    private final String thePackage;

    /**
     * The list of files in this package.
     */
    private final List<ThemisAnalysisFile> theFiles;

    /**
     * The initial dataMap.
     */
    private final ThemisAnalysisDataMap theDataMap;

    /**
     * Constructor.
     * @param pLocation the base location
     * @param pPackage the package name.
     * @throws OceanusException on error
     */
    ThemisAnalysisPackage(final File pLocation,
                          final String pPackage) throws OceanusException {
        /* Store package name */
        thePackage = pPackage;

        /* Create directory path and record the location */
        final String myPath = pPackage.replace(ThemisAnalysisChar.PERIOD, File.separatorChar);
        final File myLocation = new File(pLocation, myPath);

        /* Create the dataMap */
        theDataMap = new ThemisAnalysisDataMap();

        /* Build list of files */
        theFiles = listFiles(myLocation);

        /* Process the files */
        processFiles();

        /* Update from the class map */
        updateFromClassMap();
    }

    /**
     * Obtain the files.
     * @return the files
     */
    List<ThemisAnalysisFile> getFiles() {
        return theFiles;
    }

    /**
     * Obtain the dataMap.
     * @return the map
     */
    ThemisAnalysisDataMap getDataMap() {
        return theDataMap;
    }

    /**
     * Build list of files.
     * @param pLocation the location
     * @return the list of files
     * @throws OceanusException on error
     */
    List<ThemisAnalysisFile> listFiles(final File pLocation) throws OceanusException  {
        /* Allocate the list */
        final List<ThemisAnalysisFile> myClasses = new ArrayList<>();

        /* Loop through the entries in the directory */
        for (File myFile: Objects.requireNonNull(pLocation.listFiles())) {
            /* Handle files */
            if (!myFile.isDirectory()) {
                /* Access the name of the file */
                final String myName = myFile.getName();

                /* If this is a .java that is not package-info */
                if (myName.endsWith(SFX_JAVA)
                        && !myName.equals(PACKAGE_INFO)) {
                    /* Add the class */
                    final ThemisAnalysisFile myClass = new ThemisAnalysisFile(this, myFile);
                    myClasses.add(myClass);
                }
            }
        }

        /* Return the classes */
        return myClasses;
    }

    /**
     * process files.
     * @throws OceanusException on error
     */
    private void processFiles() throws OceanusException {
        /* Loop through the classes */
        for (ThemisAnalysisFile myFile : theFiles) {
            /* Process the class */
            myFile.processFile();
        }
    }

    /**
     * Obtain the package.
     * @return the package
     */
    public String getPackage() {
        return thePackage;
    }

    /**
     * Update from classMap.
     */
    void updateFromClassMap() {
        /* Loop through the files */
        for (ThemisAnalysisFile myFile : theFiles) {
            /* Update the file */
            myFile.updateFromClassMap();
        }
    }

    /**
     * Is the line a package?
     * @param pLine the line
     * @return true/false
     */
    static boolean isPackage(final ThemisAnalysisLine pLine) {
        /* If we are ended by a semi-colon and this is a package line */
        if (pLine.endsWithChar(ThemisAnalysisChar.SEMICOLON)
             && pLine.isStartedBy(ThemisAnalysisKeyWord.PACKAGE.getKeyWord())) {
            /* Strip the semi-colon and return true */
            pLine.stripEndChar(ThemisAnalysisChar.SEMICOLON);
            return true;
        }

        /* return false */
        return false;
    }

    @Override
    public String toString() {
        return thePackage;
    }
}
