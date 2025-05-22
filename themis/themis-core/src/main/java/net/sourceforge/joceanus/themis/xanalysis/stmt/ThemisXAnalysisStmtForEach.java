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

import com.github.javaparser.ast.stmt.ForEachStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedVar;

/**
 * ForEach Statement.
 */
public class ThemisXAnalysisStmtForEach
        implements ThemisXAnalysisParsedStatement {
    /**
     * The contents.
     */
    private final ForEachStmt theStatement;

    /**
     * The body.
     */
    private final ThemisXAnalysisParsedStatement theBody;

    /**
     * The variable.
     */
    private final ThemisXAnalysisParsedExpr theVariable;

    /**
     * The variable declarator.
     */
    private final ThemisXAnalysisParsedVar theVariableDeclarator;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtForEach(final ThemisXAnalysisParser pParser,
                                      final ForEachStmt pStatement) throws OceanusException {
        theStatement = pStatement;
        theBody = pParser.parseStatement(theStatement.getBody());
        theVariable = pParser.parseExpression(theStatement.getVariable());
        theVariableDeclarator = pParser.parseVariable(theStatement.getVariableDeclarator());
    }

    /**
     * Obtain the statement.
     * @return the statement
     */
    public ForEachStmt getStatement() {
        return theStatement;
    }

    /**
     * Obtain the variable.
     * @return the variable
     */
    public ThemisXAnalysisParsedExpr getVariable() {
        return theVariable;
    }

    /**
     * Obtain the variable declarator.
     * @return the declarator
     */
    public ThemisXAnalysisParsedVar getVariableDeclarator() {
        return theVariableDeclarator;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisParsedStatement getBody() {
        return theBody;
    }

    @Override
    public String toString() {
        return theStatement.toString();
    }
}
