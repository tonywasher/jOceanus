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

import com.github.javaparser.ast.expr.ClassExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;

/**
 * Class Expression Declaration.
 */
public class ThemisXAnalysisExprClass
        implements ThemisXAnalysisParsedExpr {
    /**
     * The type.
     */
    private final ClassExpr theExpression;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     */
    public ThemisXAnalysisExprClass(final ThemisXAnalysisParser pParser,
                                    final ClassExpr pExpression) {
        theExpression = pExpression;
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public ClassExpr getExpression() {
        return theExpression;
    }

    @Override
    public String toString() {
        return theExpression.toString();
    }
}
