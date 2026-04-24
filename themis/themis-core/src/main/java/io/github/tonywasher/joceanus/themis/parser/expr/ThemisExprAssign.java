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

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Assign Expression Declaration.
 */
public class ThemisExprAssign
        extends ThemisBaseExpression<AssignExpr> {
    /**
     * The target.
     */
    private final ThemisExpressionInstance theTarget;

    /**
     * The operator.
     */
    private final Operator theOperator;

    /**
     * The value.
     */
    private final ThemisExpressionInstance theValue;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprAssign(final ThemisParserDef pParser,
                     final AssignExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theTarget = pParser.parseExpression(pExpression.getTarget());
        theOperator = pExpression.getOperator();
        theValue = pParser.parseExpression(pExpression.getValue());
    }

    /**
     * Obtain the target.
     *
     * @return the target
     */
    public ThemisExpressionInstance getTarget() {
        return theTarget;
    }

    /**
     * Obtain the operator.
     *
     * @return the operator
     */
    public Operator getOperator() {
        return theOperator;
    }

    /**
     * Obtain the value.
     *
     * @return the value
     */
    public ThemisExpressionInstance getValue() {
        return theValue;
    }
}
