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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ForStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;

import java.util.List;

/**
 * For Statement.
 */
public class ThemisXAnalysisStmtFor
        implements ThemisXAnalysisParsedStatement {
    /**
     * The contents.
     */
    private final ForStmt theStatement;

    /**
     * The body.
     */
    private final ThemisXAnalysisParsedStatement theBody;

    /**
     * The Init.
     */
    private final List<ThemisXAnalysisParsedExpr> theInit;

    /**
     * The compare.
     */
    private final ThemisXAnalysisParsedExpr theCompare;

    /**
     * The Updates.
     */
    private final List<ThemisXAnalysisParsedExpr> theUpdates;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtFor(final ThemisXAnalysisParser pParser,
                                  final ForStmt pStatement) throws OceanusException {
        theStatement = pStatement;
        theInit = pParser.parseExprList(theStatement.getInitialization());
        final Expression myComp = theStatement.getCompare().orElse(null);
        theCompare = myComp == null ? null : pParser.parseExpression(myComp);
        theUpdates = pParser.parseExprList(theStatement.getUpdate());
        theBody = pParser.parseStatement(theStatement.getBody());
    }

    /**
     * Obtain the statement.
     * @return the statement
     */
    public ForStmt getStatement() {
        return theStatement;
    }

    /**
     * Obtain the init.
     * @return the init
     */
    public List<ThemisXAnalysisParsedExpr> getInit() {
        return theInit;
    }

    /**
     * Obtain the compare.
     * @return the compare
     */
    public ThemisXAnalysisParsedExpr getCompare() {
        return theCompare;
    }

    /**
     * Obtain the updates.
     * @return the updates
     */
    public List<ThemisXAnalysisParsedExpr> getUpdates() {
        return theUpdates;
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
