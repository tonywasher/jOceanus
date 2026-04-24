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
package io.github.tonywasher.joceanus.themis.parser.proj;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeCompilationUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Analysis representation of a java file.
 */
public class ThemisFile {
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
     * The class list.
     */
    private final List<ThemisClassInstance> theClasses;

    /**
     * The contents.
     */
    private ThemisNodeCompilationUnit theContents;

    /**
     * Constructor.
     *
     * @param pFile the file to analyse
     */
    ThemisFile(final File pFile) {
        /* Store the parameters */
        theLocation = pFile;
        theName = pFile.getName().replace(SFX_JAVA, "");
        theClasses = new ArrayList<>();
    }

    /**
     * Obtain the name of the fileClass.
     *
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the location of the fileClass.
     *
     * @return the location
     */
    public String getLocation() {
        return theLocation.getAbsolutePath();
    }

    /**
     * Obtain the contents.
     *
     * @return the contents
     */
    public ThemisNodeCompilationUnit getContents() {
        return theContents;
    }

    /**
     * Obtain the classList.
     *
     * @return the classList
     */
    public List<ThemisClassInstance> getClasses() {
        return theClasses;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Process the file.
     *
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void parseJavaCode(final ThemisParserDef pParser) throws OceanusException {
        /* Set the current file */
        pParser.setCurrentFile(theLocation);

        /* Parse the file */
        theContents = (ThemisNodeCompilationUnit) pParser.parseJavaFile();

        /* Check that we have a class that is the same name as the file */
        final ThemisClassInstance myClass = theContents.getContents();
        if (!theName.equals(myClass.getName())) {
            throw pParser.buildException("Incorrect name for class in file", ((ThemisNodeInstance) myClass).getNode());
        }

        /* Obtain a copy of the classList from the parser */
        theClasses.addAll(pParser.getClasses());
    }
}
