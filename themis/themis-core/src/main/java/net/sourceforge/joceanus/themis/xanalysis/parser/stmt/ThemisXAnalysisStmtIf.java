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
package net.sourceforge.joceanus.themis.xanalysis.parser.stmt;

import com.github.javaparser.ast.stmt.IfStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * If Statement.
 */
public class ThemisXAnalysisStmtIf
        extends ThemisXAnalysisBaseStatement<IfStmt> {
    /**
     * The condition.
     */
    private final ThemisXAnalysisExpressionInstance theCondition;

    /**
     * The then statement.
     */
    private final ThemisXAnalysisStatementInstance theThen;

    /**
     * The else statement.
     */
    private final ThemisXAnalysisStatementInstance theElse;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisStmtIf(final ThemisXAnalysisParserDef pParser,
                          final IfStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theCondition = pParser.parseExpression(pStatement.getCondition());
        theThen = pParser.parseStatement(pStatement.getThenStmt());
        theElse = pParser.parseStatement(pStatement.getElseStmt().orElse(null));
    }

    /**
     * Obtain the condition.
     * @return the condition
     */
    public ThemisXAnalysisExpressionInstance getCondition() {
        return theCondition;
    }

    /**
     * Obtain the then.
     * @return the then
     */
    public ThemisXAnalysisStatementInstance getThen() {
        return theThen;
    }

    /**
     * Obtain the else.
     * @return the else
     */
    public ThemisXAnalysisStatementInstance getElse() {
        return theElse;
    }
}
