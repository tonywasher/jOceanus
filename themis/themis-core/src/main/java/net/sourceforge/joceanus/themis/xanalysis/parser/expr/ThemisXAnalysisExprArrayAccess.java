/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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

import com.github.javaparser.ast.expr.ArrayAccessExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Array Access Expression Declaration.
 */
public class ThemisXAnalysisExprArrayAccess
        extends ThemisXAnalysisBaseExpression<ArrayAccessExpr> {
    /**
     * The name.
     */
    private final ThemisXAnalysisExpressionInstance theName;

    /**
     * The index.
     */
    private final ThemisXAnalysisExpressionInstance theIndex;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprArrayAccess(final ThemisXAnalysisParserDef pParser,
                                   final ArrayAccessExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseExpression(pExpression.getName());
        theIndex = pParser.parseExpression(pExpression.getIndex());
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public ThemisXAnalysisExpressionInstance getName() {
        return theName;
    }

    /**
     * Obtain the index.
     * @return the index
     */
    public ThemisXAnalysisExpressionInstance getIndex() {
        return theIndex;
    }
}
