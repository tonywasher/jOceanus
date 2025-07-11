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

import com.github.javaparser.ast.expr.LambdaExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Lambda Expression Declaration.
 */
public class ThemisXAnalysisExprLambda
        extends ThemisXAnalysisBaseExpression<LambdaExpr> {
    /**
     * The expression.
     */
    private final List<ThemisXAnalysisNodeInstance> theParams;

    /**
     * The expression.
     */
    private final ThemisXAnalysisStatementInstance theBody;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprLambda(final ThemisXAnalysisParserDef pParser,
                              final LambdaExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theParams = pParser.parseNodeList(pExpression.getParameters());
        theBody = pParser.parseStatement(pExpression.getBody());
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public List<ThemisXAnalysisNodeInstance> getParams() {
        return theParams;
    }
    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }

}
