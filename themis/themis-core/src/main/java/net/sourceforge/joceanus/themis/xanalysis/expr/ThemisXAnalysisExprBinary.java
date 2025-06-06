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
package net.sourceforge.joceanus.themis.xanalysis.expr;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

/**
 * Binary Expression Declaration.
 */
public class ThemisXAnalysisExprBinary
        extends ThemisXAnalysisBaseExpression<BinaryExpr> {
    /**
     * The left.
     */
    private final ThemisXAnalysisExpressionInstance theLeft;

    /**
     * The operator.
     */
    private final Operator theOperator;

    /**
     * The right.
     */
    private final ThemisXAnalysisExpressionInstance theRight;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprBinary(final ThemisXAnalysisParser pParser,
                              final BinaryExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theLeft = pParser.parseExpression(pExpression.getLeft());
        theOperator = pExpression.getOperator();
        theRight = pParser.parseExpression(pExpression.getRight());
    }
    /**
     * Obtain the left.
     * @return the right
     */
    public ThemisXAnalysisExpressionInstance getLeft() {
        return theLeft;
    }

    /**
     * Obtain the operator.
     * @return the operator
     */
    public Operator getOperator() {
        return theOperator;
    }

    /**
     * Obtain the right.
     * @return the right
     */
    public ThemisXAnalysisExpressionInstance getRight() {
        return theRight;
    }
}
