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

import com.github.javaparser.ast.stmt.DoStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisBaseStatement;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;

/**
 * Do Statement.
 */
public class ThemisXAnalysisStmtDo
        extends ThemisXAnalysisBaseStatement<DoStmt> {
    /**
     * The while expression.
     */
    private final ThemisXAnalysisExpressionInstance theExpression;

    /**
     * The body.
     */
    private final ThemisXAnalysisStatementInstance theBody;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtDo(final ThemisXAnalysisParser pParser,
                                 final DoStmt pStatement) throws OceanusException {
        /* Store details */
        super(pStatement);
        theExpression = pParser.parseExpression(pStatement.getCondition());
        theBody = pParser.parseStatement(pStatement.getBody());
    }

    /**
     * Obtain the expression.
     * @return the expression
     */
    public ThemisXAnalysisExpressionInstance getExpression() {
        return theExpression;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }
}
