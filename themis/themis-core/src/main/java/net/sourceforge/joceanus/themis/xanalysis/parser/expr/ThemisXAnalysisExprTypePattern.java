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
package net.sourceforge.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.TypePatternExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisModifierList;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * TypePattern Expression.
 */
public class ThemisXAnalysisExprTypePattern
        extends ThemisXAnalysisExprPattern<TypePatternExpr> {
    /**
     * The name.
     */
    private final ThemisXAnalysisNodeInstance theName;

    /**
     * The modifiers.
     */
    private final ThemisXAnalysisModifierList theModifiers;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprTypePattern(final ThemisXAnalysisParserDef pParser,
                                   final TypePatternExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseNode(pExpression.getName());
        theModifiers = pParser.parseModifierList(pExpression.getModifiers());
    }

    /**
     * Obtain the Name.
     * @return the name
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the Modifiers.
     * @return the modifiers
     */
    public ThemisXAnalysisModifierList getModifiers() {
        return theModifiers;
    }
}
