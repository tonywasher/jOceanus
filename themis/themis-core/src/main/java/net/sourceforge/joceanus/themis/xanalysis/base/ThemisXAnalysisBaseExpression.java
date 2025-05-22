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
package net.sourceforge.joceanus.themis.xanalysis.base;

import com.github.javaparser.ast.expr.Expression;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;

/**
 * Expression Base Class.
 * @param <T> the Expression
 */
public abstract class ThemisXAnalysisBaseExpression<T extends Expression>
        implements ThemisXAnalysisExpressionInstance {
    /**
     * The expression.
     */
    private final T theExpression;

    /**
     * Constructor.
     * @param pExpression the expression
     */
    protected ThemisXAnalysisBaseExpression(final T pExpression) {
        theExpression = pExpression;
    }

    /**
     * Obtain the expression.
     * @return the expression
     */
    public T getExpression() {
        return theExpression;
    }

    @Override
    public String toString() {
        return theExpression.toString();
    }
}
