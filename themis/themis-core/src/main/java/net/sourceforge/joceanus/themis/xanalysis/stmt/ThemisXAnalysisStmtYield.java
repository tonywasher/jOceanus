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
package net.sourceforge.joceanus.themis.xanalysis.stmt;

import com.github.javaparser.ast.stmt.YieldStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

/**
 * Yield Statement.
 */
public class ThemisXAnalysisStmtYield
        extends ThemisXAnalysisBaseStatement<YieldStmt> {
    /**
     * The expression.
     */
    private final ThemisXAnalysisExpressionInstance theExpression;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisStmtYield(final ThemisXAnalysisParser pParser,
                             final YieldStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theExpression = pParser.parseExpression(pStatement.getExpression());
    }

    /**
     * Obtain the expression.
     * @return the expression
     */
    public ThemisXAnalysisExpressionInstance getExpression() {
        return theExpression;
    }
}
