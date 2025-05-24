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
package net.sourceforge.joceanus.themis.xanalysis.node;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.TypeDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisBaseNode;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

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
    private final ThemisXAnalysisDeclarationInstance theContents;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pUnit the unit
     * @throws OceanusException on error
     */
    public ThemisXAnalysisNodeCompilationUnit(final ThemisXAnalysisParser pParser,
                                              final CompilationUnit pUnit) throws OceanusException {
        super(pUnit);
        thePackageDef = pParser.parseNode(pUnit.getPackageDeclaration().orElse(null));
        theImports = pParser.parseNodeList(pUnit.getImports());
        final NodeList<TypeDeclaration<?>> myTypes = pUnit.getTypes();
        if (myTypes.size() != 1) {
            throw pParser.buildException("More than one class definition in file", pUnit);
        }
        theContents = pParser.parseDeclaration(pUnit.getType(0));
    }

    /**
     * Obtain the package.
     * @return the package
     */
    public ThemisXAnalysisNodeInstance getPackage() {
        return thePackageDef;
    }

    /**
     * Obtain the imports.
     * @return the imports
     */
    public List<ThemisXAnalysisNodeInstance> getImports() {
        return theImports;
    }

    /**
     * Obtain the contents.
     * @return the contents
     */
    public ThemisXAnalysisDeclarationInstance getContents() {
        return theContents;
    }
}
