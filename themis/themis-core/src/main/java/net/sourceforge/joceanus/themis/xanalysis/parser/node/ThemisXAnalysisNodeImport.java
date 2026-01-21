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
package net.sourceforge.joceanus.themis.xanalysis.parser.node;

import com.github.javaparser.ast.ImportDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Import.
 */
public class ThemisXAnalysisNodeImport
        extends ThemisXAnalysisBaseNode<ImportDeclaration> {
    /**
     * The shortName of the import.
     */
    private final String theShortName;

    /**
     * The fullName of the import.
     */
    private final String theFullName;

    /**
     * The package of the import.
     */
    private final String thePackage;

    /**
     * The Import.
     */
    private final ThemisXAnalysisNodeInstance theImport;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pImport the import
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodeImport(final ThemisXAnalysisParserDef pParser,
                              final ImportDeclaration pImport) throws OceanusException {
        super(pParser, pImport);
        theImport = pParser.parseNode(pImport.getName());
        final ThemisXAnalysisNodeName myName = (ThemisXAnalysisNodeName) theImport;
        theShortName = myName.getName();
        final ThemisXAnalysisNodeName myQualifier = ((ThemisXAnalysisNodeName) myName.getQualifier());
        thePackage = myQualifier.getNode().asString();
        theFullName = myName.getNode().asString();

        /* Reject imports of wildcards */
        if (pImport.isAsterisk()) {
            throw pParser.buildException("Wildcard in import", pImport);
        }
    }

    /**
     * Obtain the shortName.
     *
     * @return the shortName
     */
    public String getShortName() {
        return theShortName;
    }

    /**
     * Obtain the fullName.
     *
     * @return the fullName
     */
    public String getFullName() {
        return theFullName;
    }

    /**
     * Obtain the package.
     *
     * @return the package
     */
    public String getPackage() {
        return thePackage;
    }

    /**
     * Obtain the import.
     *
     * @return the import
     */
    public ThemisXAnalysisNodeInstance getImport() {
        return theImport;
    }
}
