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
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisBaseInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisModuleInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;

/**
 * Module Base Class.
 * @param <T> the Node
 */
public abstract class ThemisXAnalysisBaseModule<T extends Node>
        extends ThemisXAnalysisBaseInstance<T>
        implements ThemisXAnalysisModuleInstance {
    /**
     * The nodeId.
     */
    private final ThemisXAnalysisMod theId;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pNode the node
     */
    ThemisXAnalysisBaseModule(final ThemisXAnalysisParserDef pParser,
                              final T pNode) throws OceanusException {
        super(pParser, pNode);
        theId = ThemisXAnalysisMod.determineModule(pParser, pNode);
    }

    @Override
    public ThemisXAnalysisMod getId() {
        return theId;
    }
}
