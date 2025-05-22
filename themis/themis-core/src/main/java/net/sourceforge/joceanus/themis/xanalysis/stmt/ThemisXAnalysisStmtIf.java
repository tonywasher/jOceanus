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

import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;

/**
 * If Statement.
 */
public class ThemisXAnalysisStmtIf
        implements ThemisXAnalysisParsedStatement {
    /**
     * The contents.
     */
    private final IfStmt theStatement;

    /**
     * The condition.
     */
    private final ThemisXAnalysisParsedExpr theCondition;

    /**
     * The then statement.
     */
    private final ThemisXAnalysisParsedStatement theThen;

    /**
     * The else statement.
     */
    private final ThemisXAnalysisParsedStatement theElse;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtIf(final ThemisXAnalysisParser pParser,
                                 final IfStmt pStatement) throws OceanusException {
        theStatement = pStatement;
        theCondition = pParser.parseExpression(theStatement.getCondition());
        theThen = pParser.parseStatement(theStatement.getThenStmt());
        final Statement myElse = theStatement.getElseStmt().orElse(null);
        theElse = myElse == null ? null : pParser.parseStatement(myElse);
    }

    /**
     * Obtain the statement.
     * @return the statement
     */
    public IfStmt getStatement() {
        return theStatement;
    }

    /**
     * Obtain the condition.
     * @return the condition
     */
    public ThemisXAnalysisParsedExpr getCondition() {
        return theCondition;
    }

    /**
     * Obtain the then.
     * @return the then
     */
    public ThemisXAnalysisParsedStatement getThen() {
        return theThen;
    }

    /**
     * Obtain the else.
     * @return the else
     */
    public ThemisXAnalysisParsedStatement getElse() {
        return theElse;
    }

    @Override
    public String toString() {
        return theStatement.toString();
    }
}
