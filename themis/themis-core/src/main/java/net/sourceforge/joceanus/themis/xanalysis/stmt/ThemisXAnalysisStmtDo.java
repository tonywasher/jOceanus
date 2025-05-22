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
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;

/**
 * Do Statement.
 */
public class ThemisXAnalysisStmtDo
        implements ThemisXAnalysisParsedStatement {
    /**
     * The contents.
     */
    private final DoStmt theStatement;

    /**
     * The while expression.
     */
    private final ThemisXAnalysisParsedExpr theExpression;

    /**
     * The body.
     */
    private final ThemisXAnalysisParsedStatement theBody;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtDo(final ThemisXAnalysisParser pParser,
                                 final DoStmt pStatement) throws OceanusException {
        /* Store details */
        theStatement = pStatement;
        theExpression = pParser.parseExpression(theStatement.getCondition());
        theBody = pParser.parseStatement(theStatement.getBody());
    }

    /**
     * Obtain the statement.
     * @return the statement
     */
    public DoStmt getStatement() {
        return theStatement;
    }

    /**
     * Obtain the expression.
     * @return the expression
     */
    public ThemisXAnalysisParsedExpr getExpression() {
        return theExpression;
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
