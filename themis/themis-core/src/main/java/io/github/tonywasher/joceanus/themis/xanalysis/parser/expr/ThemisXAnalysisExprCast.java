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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.CastExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Cast Expression Declaration.
 */
public class ThemisXAnalysisExprCast
        extends ThemisXAnalysisBaseExpression<CastExpr> {
    /**
     * The type.
     */
    private final ThemisXAnalysisTypeInstance theType;

    /**
     * The expression.
     */
    private final ThemisXAnalysisExpressionInstance theValue;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprCast(final ThemisXAnalysisParserDef pParser,
                            final CastExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theType = pParser.parseType(pExpression.getType());
        theValue = pParser.parseExpression(pExpression.getExpression());
    }

    /**
     * Obtain the Type.
     *
     * @return the type
     */
    public ThemisXAnalysisTypeInstance getType() {
        return theType;
    }

    /**
     * Obtain the value.
     *
     * @return the value
     */
    public ThemisXAnalysisExpressionInstance getValue() {
        return theValue;
    }
}
