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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.node;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.TypeDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Compilation Unit.
 */
public class ThemisXAnalysisNodeCompilationUnit
        extends ThemisXAnalysisBaseNode<CompilationUnit> {
    /**
     * The Package.
     */
    private final ThemisXAnalysisNodeInstance thePackageDef;

    /**
     * The Imports.
     */
    private final List<ThemisXAnalysisNodeInstance> theImports;

    /**
     * The Contents.
     */
    private final ThemisXAnalysisClassInstance theContents;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pUnit   the unit
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodeCompilationUnit(final ThemisXAnalysisParserDef pParser,
                                       final CompilationUnit pUnit) throws OceanusException {
        super(pParser, pUnit);
        thePackageDef = pParser.parseNode(pUnit.getPackageDeclaration().orElse(null));
        theImports = pParser.parseNodeList(pUnit.getImports());
        final NodeList<TypeDeclaration<?>> myTypes = pUnit.getTypes();
        if (myTypes.size() > 1) {
            throw pParser.buildException("More than one class definition in file", pUnit);
        }
        if (myTypes.isEmpty()) {
            throw pParser.buildException("No class definition found in file", pUnit);
        }
        theContents = (ThemisXAnalysisClassInstance) pParser.parseDeclaration(pUnit.getType(0));
    }

    /**
     * Obtain the package.
     *
     * @return the package
     */
    public ThemisXAnalysisNodeInstance getPackage() {
        return thePackageDef;
    }

    /**
     * Obtain the imports.
     *
     * @return the imports
     */
    public List<ThemisXAnalysisNodeInstance> getImports() {
        return theImports;
    }

    /**
     * Obtain the contents.
     *
     * @return the contents
     */
    public ThemisXAnalysisClassInstance getContents() {
        return theContents;
    }
}
