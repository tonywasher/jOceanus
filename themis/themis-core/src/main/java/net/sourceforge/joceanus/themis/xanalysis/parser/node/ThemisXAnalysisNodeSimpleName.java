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
package net.sourceforge.joceanus.themis.xanalysis.parser.node;

import com.github.javaparser.ast.expr.SimpleName;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Name.
 */
public class ThemisXAnalysisNodeSimpleName
        extends ThemisXAnalysisBaseNode<SimpleName> {
    /**
     * The Name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pName the name
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodeSimpleName(final ThemisXAnalysisParserDef pParser,
                                  final SimpleName pName) throws OceanusException {
        super(pParser, pName);
        theName = pName.getIdentifier();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }
}
