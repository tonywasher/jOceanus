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
package net.sourceforge.joceanus.themis.xanalysis.mod;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisId;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisModuleInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

import java.util.function.Predicate;

/**
 * Analysis Node.
 */
public enum ThemisXAnalysisMod
        implements ThemisXAnalysisId {
    /**
     * Exports.
     */
    EXPORTS(ModuleExportsDirective.class::isInstance),

    /**
     * Module.
     */
    MODULE(ModuleDeclaration.class::isInstance),

    /**
     * Opens.
     */
    OPENS(ModuleOpensDirective.class::isInstance),

    /**
     * Provides.
     */
    PROVIDES(ModuleProvidesDirective.class::isInstance),

    /**
     * Requires.
     */
    REQUIRES(ModuleRequiresDirective.class::isInstance),

    /**
     * Uses.
     */
    USES(ModuleUsesDirective.class::isInstance);

    /**
     * The test.
     */
    private final Predicate<Node> theTester;

    /**
     * Constructor.
     * @param pTester the test method
     */
    ThemisXAnalysisMod(final Predicate<Node> pTester) {
        theTester = pTester;
    }

    /**
     * Determine type of node.
     * @param pParser the parser
     * @param pNode the node
     * @return the nodeType
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisMod determineModule(final ThemisXAnalysisParser pParser,
                                                     final Node pNode) throws OceanusException {
        /* Loop testing each node type */
        for (ThemisXAnalysisMod myNode : values()) {
            if (myNode.theTester.test(pNode)) {
                return myNode;
            }
        }

        /* Unrecognised nodeType */
        throw pParser.buildException("Unexpected Node", pNode);
    }

    /**
     * Parse a node.
     * @param pParser the parser
     * @param pNode the node
     * @return the parsed node
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisModuleInstance parseModule(final ThemisXAnalysisParser pParser,
                                                            final Node pNode) throws OceanusException {
        /* Handle null Node */
        if (pNode == null) {
            return null;
        }

        /* Create appropriate node */
        switch (ThemisXAnalysisMod.determineModule(pParser, pNode)) {
            case EXPORTS:   return new ThemisXAnalysisModExports(pParser, (ModuleExportsDirective) pNode);
            case MODULE:    return new ThemisXAnalysisModModule(pParser, (ModuleDeclaration) pNode);
            case OPENS:     return new ThemisXAnalysisModOpens(pParser, (ModuleOpensDirective) pNode);
            case PROVIDES:  return new ThemisXAnalysisModProvides(pParser, (ModuleProvidesDirective) pNode);
            case REQUIRES:  return new ThemisXAnalysisModRequires(pParser, (ModuleRequiresDirective) pNode);
            case USES:      return new ThemisXAnalysisModUses(pParser, (ModuleUsesDirective) pNode);
            default:        throw pParser.buildException("Unsupported Module Type", pNode);
        }
    }
}
