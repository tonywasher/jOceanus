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

import com.github.javaparser.ast.expr.MemberValuePair;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;

/**
 * Member Value Pair.
 */
public class ThemisXAnalysisNodeValuePair
        extends ThemisXAnalysisBaseNode<MemberValuePair> {
    /**
     * The Name.
     */
    private final ThemisXAnalysisNodeInstance theName;

    /**
     * The Value.
     */
    private final ThemisXAnalysisExpressionInstance theValue;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pPair the valuePair
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodeValuePair(final ThemisXAnalysisParserDef pParser,
                                 final MemberValuePair pPair) throws OceanusException {
        super(pParser, pPair);
        theName = pParser.parseNode(pPair.getName());
        theValue = pParser.parseExpression(pPair.getValue());
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public ThemisXAnalysisExpressionInstance getValue() {
        return theValue;
    }
}
