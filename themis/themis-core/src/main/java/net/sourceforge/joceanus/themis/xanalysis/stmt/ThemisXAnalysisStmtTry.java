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

import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.TryStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedParam;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Try Statement.
 */
public class ThemisXAnalysisStmtTry
        implements ThemisXAnalysisParsedStatement {
    /**
     * The contents.
     */
    private final TryStmt theStatement;

    /**
     * The contents.
     */
    private final List<ThemisXAnalysisParsedExpr> theResources;

    /**
     * The tryBlock.
     */
    private final ThemisXAnalysisParsedStatement theTry;

    /**
     * The catchClauses.
     */
    private final List<ThemisXAnalysisStmtCatch> theCatches;

    /**
     * The contents.
     */
    private final ThemisXAnalysisParsedStatement theFinally;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtTry(final ThemisXAnalysisParser pParser,
                                  final TryStmt pStatement) throws OceanusException {
        theStatement = pStatement;
        theResources = pParser.parseExprList(theStatement.getResources());
        theTry = pParser.parseStatement(theStatement.getTryBlock());
        final Statement myFinally = theStatement.getFinallyBlock().orElse(null);
        theFinally = myFinally == null ? null : pParser.parseStatement(myFinally);
        theCatches = new ArrayList<>();
        for (CatchClause myClause : theStatement.getCatchClauses()) {
            final ThemisXAnalysisStmtCatch myCatch = new ThemisXAnalysisStmtCatch(pParser, myClause);
            theCatches.add(myCatch);
        }
    }

    /**
     * Obtain the statement.
     * @return the statement
     */
    public TryStmt getStatement() {
        return theStatement;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public List<ThemisXAnalysisParsedExpr> getResources() {
        return theResources;
    }

    /**
     * Obtain the try.
     * @return the try
     */
    public ThemisXAnalysisParsedStatement getTry() {
        return theTry;
    }

    /**
     * Obtain the catches.
     * @return the catches
     */
    public List<ThemisXAnalysisStmtCatch> getCatches() {
        return theCatches;
    }

    /**
     * Obtain the finally.
     * @return the finally
     */
    public ThemisXAnalysisParsedStatement getFinally() {
        return theFinally;
    }

    @Override
    public String toString() {
        return theStatement.toString();
    }

    /**
     * Try Catch.
     */
    public static class ThemisXAnalysisStmtCatch
            implements ThemisXAnalysisParsedStatement {
        /**
         * The contents.
         */
        private final CatchClause theClause;

        /**
         * The parameter.
         */
        private final ThemisXAnalysisParsedParam theParameter;

        /**
         * The body.
         */
        private final ThemisXAnalysisParsedStatement theBody;

        /**
         * Constructor.
         *
         * @param pParser the parser
         * @param pClause the clause
         * @throws OceanusException on error
         */
        private ThemisXAnalysisStmtCatch(final ThemisXAnalysisParser pParser,
                                         final CatchClause pClause) throws OceanusException {
            theClause = pClause;
            theBody = pParser.parseStatement(theClause.getBody());
            theParameter = pParser.parseParameter(theClause.getParameter());
        }

        /**
         * Obtain the clause.
         * @return the clause
         */
        public CatchClause getClause() {
            return theClause;
        }

        /**
         * Obtain the parameter.
         * @return the parameter
         */
        public ThemisXAnalysisParsedParam getParameter() {
            return theParameter;
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
            return theClause.toString();
        }
    }
}
