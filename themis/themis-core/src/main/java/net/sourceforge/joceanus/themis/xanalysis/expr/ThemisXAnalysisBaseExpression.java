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

import com.github.javaparser.ast.expr.Expression;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisBaseInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;

/**
 * Expression Base Class.
 * @param <T> the Expression
 */
public abstract class ThemisXAnalysisBaseExpression<T extends Expression>
        extends ThemisXAnalysisBaseInstance<T>
        implements ThemisXAnalysisExpressionInstance {
    /**
     * The expressionId.
     */
    private final ThemisXAnalysisExpression theId;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisBaseExpression(final ThemisXAnalysisParserDef pParser,
                                  final T pExpression) throws OceanusException {
        super(pParser, pExpression);
        theId = ThemisXAnalysisExpression.determineExpression(pParser, pExpression);
    }

    @Override
    public ThemisXAnalysisExpression getId() {
        return theId;
    }
}
