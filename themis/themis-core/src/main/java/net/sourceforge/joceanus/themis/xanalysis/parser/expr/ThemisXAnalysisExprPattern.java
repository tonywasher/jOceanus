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
package net.sourceforge.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.PatternExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Pattern Expression Declaration.
 *
 * @param <T> the expression
 */
public abstract class ThemisXAnalysisExprPattern<T extends PatternExpr>
        extends ThemisXAnalysisBaseExpression<T> {
    /**
     * The type.
     */
    private final ThemisXAnalysisTypeInstance theType;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprPattern(final ThemisXAnalysisParserDef pParser,
                               final T pExpression) throws OceanusException {
        super(pParser, pExpression);
        theType = pParser.parseType(pExpression.getType());
    }

    /**
     * Obtain the Type.
     *
     * @return the type
     */
    public ThemisXAnalysisTypeInstance getType() {
        return theType;
    }
}
