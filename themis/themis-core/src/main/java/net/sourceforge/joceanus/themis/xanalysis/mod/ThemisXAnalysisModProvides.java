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

import com.github.javaparser.ast.modules.ModuleProvidesDirective;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

import java.util.List;

/**
 * Module Exports.
 */
public class ThemisXAnalysisModProvides
        extends ThemisXAnalysisBaseModule<ModuleProvidesDirective> {
    /**
     * The Service Name.
     */
    private final ThemisXAnalysisNodeInstance theService;

    /**
     * The implementations.
     */
    private final List<ThemisXAnalysisNodeInstance> theImplementations;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDirective the directive
     * @throws OceanusException on error
     */
    ThemisXAnalysisModProvides(final ThemisXAnalysisParser pParser,
                               final ModuleProvidesDirective pDirective) throws OceanusException {
        super(pParser, pDirective);
        theService = pParser.parseNode(pDirective.getName());
        theImplementations = pParser.parseNodeList(pDirective.getWith());
    }

    /**
     * Obtain the service.
     * @return the service
     */
    public ThemisXAnalysisNodeInstance getService() {
        return theService;
    }

    /**
     * Obtain the implementations.
     * @return the implementations
     */
    public List<ThemisXAnalysisNodeInstance> getImplementations() {
        return theImplementations;
    }
}
