/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.themis.xanalysis.parser.proj;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.mod.ThemisXAnalysisModModule;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Module.
 */
public class ThemisXAnalysisModule {
    /**
     * The path xtra.
     */
    static final String PATH_XTRA = ".src.main.java".replace(ThemisXAnalysisChar.PERIOD, ThemisXAnalysisChar.COMMENT);

    /**
     * The module-info file.
     */
    private static final String MODULE_INFO = "module-info" + ThemisXAnalysisFile.SFX_JAVA;

    /**
     * The module name.
     */
    private final String theName;

    /**
     * The locations.
     */
    private final File theLocation;

    /**
     * The package list.
     */
    private final List<ThemisXAnalysisPackage> thePackages;

    /**
     * The module-info declaration.
     */
    private ThemisXAnalysisModModule theModuleInfo;

    /**
     * Constructor.
     *
     * @param pLocation the location of the module
     * @param pPom      the module Pom
     * @throws OceanusException on error
     */
    ThemisXAnalysisModule(final File pLocation,
                          final ThemisXAnalysisMaven pPom) throws OceanusException {
        /* Create the list */
        thePackages = new ArrayList<>();

        /* Store the name and location */
        theLocation = new File(pLocation, PATH_XTRA);
        theName = pLocation.getName();

        /* Initiate search for packages */
        checkForPackage(theLocation, null);

        /* Add any extraDirs */
        for (String myXtra : pPom.getXtraDirs()) {
            final File myXtraDir = new File(pLocation, myXtra);
            checkForPackage(myXtraDir, null);
        }
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public String getName() {
        return theName;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the packages.
     *
     * @return the packages
     */
    public List<ThemisXAnalysisPackage> getPackages() {
        return thePackages;
    }

    /**
     * Obtain the module-info.
     *
     * @return the module-info
     */
    public ThemisXAnalysisModModule getModuleInfo() {
        return theModuleInfo;
    }

    /**
     * Check for package.
     *
     * @param pLocation the location to search
     * @param pPackage  the package name
     * @throws OceanusException on error
     */
    void checkForPackage(final File pLocation,
                         final String pPackage) throws OceanusException {
        /* Assume not a package */
        boolean isPackage = false;

        /* Determine the location to search */
        final File myLocation = pPackage == null
                ? pLocation
                : new File(pLocation, pPackage.replace(ThemisXAnalysisChar.PERIOD, ThemisXAnalysisChar.COMMENT));

        /* Look for java files or further packages */
        for (File myFile : Objects.requireNonNull(myLocation.listFiles())) {
            /* Access file name */
            final String myName = myFile.getName();

            /* If this is a directory */
            if (myFile.isDirectory()) {
                final String myPackage = pPackage == null
                        ? myName
                        : pPackage + ThemisXAnalysisChar.PERIOD + myName;
                checkForPackage(pLocation, myPackage);
            }

            /* If this is a Java file name */
            if (myName.endsWith(ThemisXAnalysisFile.SFX_JAVA)
                    && !MODULE_INFO.equals(myName)) {
                isPackage = pPackage != null;
            }
        }

        /* If this is a package */
        if (isPackage) {
            /* Add the package to the list */
            thePackages.add(new ThemisXAnalysisPackage(pLocation, pPackage));
        }
    }

    /**
     * Parse java code.
     *
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void parseJavaCode(final ThemisXAnalysisParserDef pParser) throws OceanusException {
        /* Loop through the packages */
        for (ThemisXAnalysisPackage myPackage : thePackages) {
            /* Process the package */
            myPackage.parseJavaCode(pParser);
        }

        /* Check for and load the module-info file if found */
        final File myModuleInfo = new File(theLocation, MODULE_INFO);
        if (myModuleInfo.exists()) {
            theModuleInfo = (ThemisXAnalysisModModule) pParser.parseModuleInfo(myModuleInfo);
        }
    }
}
