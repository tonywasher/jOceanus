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

import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Module Requires.
 */
public class ThemisXAnalysisModRequires
        extends ThemisXAnalysisBaseModule<ModuleRequiresDirective> {
    /**
     * The Required.
     */
    private final ThemisXAnalysisNodeInstance theRequired;

    /**
     * The modifiers.
     */
    private final List<ThemisXAnalysisNodeInstance> theModifiers;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDirective the directive
     * @throws OceanusException on error
     */
    ThemisXAnalysisModRequires(final ThemisXAnalysisParserDef pParser,
                               final ModuleRequiresDirective pDirective) throws OceanusException {
        super(pParser, pDirective);
        theRequired = pParser.parseNode(pDirective.getName());
        theModifiers = pParser.parseNodeList(pDirective.getModifiers());
    }

    /**
     * Obtain the required.
     * @return the required
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theRequired;
    }

    /**
     * Obtain the modifiers.
     * @return the modifiers
     */
    public List<ThemisXAnalysisNodeInstance> getModifiers() {
        return theModifiers;
    }
}
