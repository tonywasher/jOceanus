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
package net.sourceforge.joceanus.themis.xanalysis.parser.mod;

import com.github.javaparser.ast.modules.ModuleExportsDirective;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Module Exports.
 */
public class ThemisXAnalysisModExports
        extends ThemisXAnalysisBaseModule<ModuleExportsDirective> {
    /**
     * The Export.
     */
    private final ThemisXAnalysisNodeInstance theExport;

    /**
     * The Targets.
     */
    private final List<ThemisXAnalysisNodeInstance> theTargets;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDirective the directive
     * @throws OceanusException on error
     */
    ThemisXAnalysisModExports(final ThemisXAnalysisParserDef pParser,
                              final ModuleExportsDirective pDirective) throws OceanusException {
        super(pParser, pDirective);
        theExport = pParser.parseNode(pDirective.getName());
        theTargets = pParser.parseNodeList(pDirective.getModuleNames());
    }

    /**
     * Obtain the export.
     * @return the export
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theExport;
    }

    /**
     * Obtain the targets.
     * @return the targets
     */
    public List<ThemisXAnalysisNodeInstance> getNames() {
        return theTargets;
    }
}
