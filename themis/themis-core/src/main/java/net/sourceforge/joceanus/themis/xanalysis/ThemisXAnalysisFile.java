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

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private CompilationUnit theContents;

    /**
     * The comments.
     */
    private Comment theComment;

    /**
     * The package definition.
     */
    private ThemisXAnalysisPackageDef thePackageDef;

    /**
     * The imports.
     */
    private ThemisXAnalysisImports theImports;

    /**
     * The types.
     */
    private ThemisXAnalysisObject theType;

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
     * Obtain the package declaration.
     * @return the package declaration
     */
    ThemisXAnalysisPackageDef getPackageDef() {
        return thePackageDef;
    }

    /**
     * Obtain the imports.
     * @return the imports
     */
    ThemisXAnalysisImports getImports() {
        return theImports;
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
        /* Protect against exceptions */
        try (InputStream myStream = new FileInputStream(theLocation);
             InputStreamReader myInputReader = new InputStreamReader(myStream, StandardCharsets.UTF_8);
             BufferedReader myReader = new BufferedReader(myInputReader)) {

            /* Parse the contents */
            theContents = StaticJavaParser.parse(myStream);
            theComment = theContents.getComment().orElse(null);

            /* Obtain details about package and imports */
            final Optional<PackageDeclaration> myPackage = theContents.getPackageDeclaration();
            thePackageDef = myPackage.isEmpty() ? null : new ThemisXAnalysisPackageDef(myPackage.get());
            theImports = new ThemisXAnalysisImports(theContents.getImports());

            /* Obtain details about classes */
            final NodeList<TypeDeclaration<?>> myTypes = theContents.getTypes();
            if (myTypes.size() != 1) {
                throw new ThemisDataException("More than one class definition in file");
            }
            theType = new ThemisXAnalysisObject(myTypes.get(0));

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw an exception */
            throw new ThemisIOException("Failed to load file "
                    + theLocation.getAbsolutePath(), e);
        }
    }

    /**
     * The package declaration class.
     */
    private final class ThemisXAnalysisPackageDef {
        /**
         * The contents.
         */
        private final PackageDeclaration theContents;

        /**
         * Constructor.
         * @param pDeclaration the optional package declaration.
         * @throws OceanusException on error
         */
        private ThemisXAnalysisPackageDef(final PackageDeclaration pDeclaration) throws OceanusException {
            /* Store contents */
            theContents = pDeclaration;

            /* Check that the package matches */
            if (!thePackage.getPackage().equals(theContents.getNameAsString())) {
                throw new ThemisDataException("Bad package");
            }
        }

        /**
         * Obtain the contents.
         * @return the contents
         */
        PackageDeclaration getContents() {
            return theContents;
        }
    }

    /**
     * Import declarations.
     */
    public static class ThemisXAnalysisImports {
        /**
         * The Imports.
         */
        private final List<ThemisXAnalysisImport> theImports;

        /**
         * Constructor.
         *
         * @param pImports the import declarations
         * @throws OceanusException on error
         */
        ThemisXAnalysisImports(final NodeList<ImportDeclaration> pImports) throws OceanusException {
            /* Create the import list */
            theImports = new ArrayList<>();

            /* Loop through the imports */
            for (ImportDeclaration myImportDef : pImports) {
                final ThemisXAnalysisImport myImport = new ThemisXAnalysisImport(myImportDef);
                theImports.add(myImport);
            }
        }

        /**
         * Obtain the imports.
         * @return the imports
         */
        List<ThemisXAnalysisImport> getImports() {
            return theImports;
        }
    }

    /**
     * Import declaration.
     */
    public static final class ThemisXAnalysisImport {
        /**
         * The contents.
         */
        private final ImportDeclaration theContents;

        /**
         * Constructor.
         * @param pImport the import declaration
         * @throws OceanusException on error
         */
        private ThemisXAnalysisImport(final ImportDeclaration pImport) throws OceanusException  {
            /* Store the contents */
            theContents = pImport;

            /* Reject imports of wildcards */
            if (pImport.isAsterisk()) {
                throw new ThemisDataException("Wildcard in import");
            }
        }

        /**
         * Obtain the contents.
         * @return the contents
         */
        ImportDeclaration getContents() {
            return theContents;
        }
    }
}
