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

import com.github.javaparser.ast.expr.EnclosedExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Enclosed Expression Declaration.
 */
public class ThemisXAnalysisExprEnclosed
        extends ThemisXAnalysisBaseExpression<EnclosedExpr> {
    /**
     * The inner.
     */
    private final ThemisXAnalysisExpressionInstance theInner;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprEnclosed(final ThemisXAnalysisParserDef pParser,
                                final EnclosedExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theInner = pParser.parseExpression(pExpression.getInner());
    }

    /**
     * Obtain the value.
     *
     * @return the value
     */
    public ThemisXAnalysisExpressionInstance getInner() {
        return theInner;
    }
}
