/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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

import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr.Operator;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Unary Expression Declaration.
 */
public class ThemisXAnalysisExprUnary
        extends ThemisXAnalysisBaseExpression<UnaryExpr> {
    /**
     * The target.
     */
    private final ThemisXAnalysisExpressionInstance theTarget;

    /**
     * The operator.
     */
    private final Operator theOperator;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprUnary(final ThemisXAnalysisParserDef pParser,
                             final UnaryExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theTarget = pParser.parseExpression(pExpression.getExpression());
        theOperator = pExpression.getOperator();
    }

    /**
     * Obtain the target.
     * @return the target
     */
    public ThemisXAnalysisExpressionInstance getTarget() {
        return theTarget;
    }

    /**
     * Obtain the operator.
     * @return the operator
     */
    public Operator getOperator() {
        return theOperator;
    }
}
