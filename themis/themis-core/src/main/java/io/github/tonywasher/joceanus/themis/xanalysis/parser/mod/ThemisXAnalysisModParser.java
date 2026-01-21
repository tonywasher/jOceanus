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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.mod;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsDirective;
import com.github.javaparser.ast.modules.ModuleOpensDirective;
import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import com.github.javaparser.ast.modules.ModuleUsesDirective;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisModuleInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Analysis Module Parser.
 */
public final class ThemisXAnalysisModParser {
    /**
     * Private Constructor.
     */
    private ThemisXAnalysisModParser() {
    }

    /**
     * Parse a module.
     *
     * @param pParser the parser
     * @param pMod    the module
     * @return the parsed module
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisModuleInstance parseModule(final ThemisXAnalysisParserDef pParser,
                                                            final Node pMod) throws OceanusException {
        /* Handle null Module */
        if (pMod == null) {
            return null;
        }

        /* Create appropriate node */
        switch (ThemisXAnalysisMod.determineModule(pParser, pMod)) {
            case EXPORTS:
                return new ThemisXAnalysisModExports(pParser, (ModuleExportsDirective) pMod);
            case MODULE:
                return new ThemisXAnalysisModModule(pParser, (ModuleDeclaration) pMod);
            case OPENS:
                return new ThemisXAnalysisModOpens(pParser, (ModuleOpensDirective) pMod);
            case PROVIDES:
                return new ThemisXAnalysisModProvides(pParser, (ModuleProvidesDirective) pMod);
            case REQUIRES:
                return new ThemisXAnalysisModRequires(pParser, (ModuleRequiresDirective) pMod);
            case USES:
                return new ThemisXAnalysisModUses(pParser, (ModuleUsesDirective) pMod);
            default:
                throw pParser.buildException("Unsupported Module Type", pMod);
        }
    }
}
