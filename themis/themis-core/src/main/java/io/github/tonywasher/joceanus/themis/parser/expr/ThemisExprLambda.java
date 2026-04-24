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

import com.github.javaparser.ast.expr.LambdaExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Lambda Expression Declaration.
 */
public class ThemisExprLambda
        extends ThemisBaseExpression<LambdaExpr> {
    /**
     * The expression.
     */
    private final List<ThemisNodeInstance> theParams;

    /**
     * The expression.
     */
    private final ThemisStatementInstance theBody;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprLambda(final ThemisParserDef pParser,
                     final LambdaExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theParams = pParser.parseNodeList(pExpression.getParameters());
        theBody = pParser.parseStatement(pExpression.getBody());
    }

    /**
     * Obtain the value.
     *
     * @return the value
     */
    public List<ThemisNodeInstance> getParams() {
        return theParams;
    }

    /**
     * Obtain the body.
     *
     * @return the body
     */
    public ThemisStatementInstance getBody() {
        return theBody;
    }

}
