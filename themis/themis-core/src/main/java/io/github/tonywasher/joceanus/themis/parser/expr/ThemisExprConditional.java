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
package io.github.tonywasher.joceanus.themis.parser.expr;

import com.github.javaparser.ast.expr.ConditionalExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Conditional Expression Declaration.
 */
public class ThemisExprConditional
        extends ThemisBaseExpression<ConditionalExpr> {
    /**
     * The condition.
     */
    private final ThemisExpressionInstance theCondition;

    /**
     * The then.
     */
    private final ThemisExpressionInstance theThen;

    /**
     * The else.
     */
    private final ThemisExpressionInstance theElse;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprConditional(final ThemisParserDef pParser,
                          final ConditionalExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theCondition = pParser.parseExpression(pExpression.getCondition());
        theThen = pParser.parseExpression(pExpression.getThenExpr());
        theElse = pParser.parseExpression(pExpression.getElseExpr());
    }


    /**
     * Obtain the condition.
     *
     * @return the condition
     */
    public ThemisExpressionInstance getCondition() {
        return theCondition;
    }

    /**
     * Obtain the then.
     *
     * @return the then
     */
    public ThemisExpressionInstance getThen() {
        return theThen;
    }

    /**
     * Obtain the else.
     *
     * @return the else
     */
    public ThemisExpressionInstance getElse() {
        return theElse;
    }
}
