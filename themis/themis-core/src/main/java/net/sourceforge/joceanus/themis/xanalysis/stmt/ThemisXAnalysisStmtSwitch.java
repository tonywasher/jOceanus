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
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Switch Statement.
 */
public class ThemisXAnalysisStmtSwitch
        implements ThemisXAnalysisParsedStatement {
    /**
     * The contents.
     */
    private final SwitchStmt theStatement;

    /**
     * The selector.
     */
    private final Expression theSelector;

    /**
     * The entries.
     */
    private final List<ThemisXAnalysisStmtCase> theCases;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtSwitch(final ThemisXAnalysisParser pParser,
                                     final SwitchStmt pStatement) throws OceanusException {
        theStatement = pStatement;
        theSelector = theStatement.getSelector();
        theCases = new ArrayList<>();
        for (SwitchEntry myEntry : theStatement.getEntries()) {
            final ThemisXAnalysisStmtCase myCase = new ThemisXAnalysisStmtCase(pParser, myEntry);
            theCases.add(myCase);
        }
    }

    /**
     * Obtain the statement.
     *
     * @return the statement
     */
    public SwitchStmt getStatement() {
        return theStatement;
    }

    /**
     * Obtain the selector.
     * @return the selector
     */
    public Expression getSelector() {
        return theSelector;
    }

    /**
     * Obtain the cases.
     * @return the cases
     */
    public List<ThemisXAnalysisStmtCase> getCases() {
        return theCases;
    }

    @Override
    public String toString() {
        return theStatement.toString();
    }

    /**
     * Switch Case.
     */
    public static class ThemisXAnalysisStmtCase
            implements ThemisXAnalysisParsedStatement {
        /**
         * The contents.
         */
        private final SwitchEntry theEntry;

        /**
         * The guard.
         */
        private final ThemisXAnalysisParsedExpr theGuard;

        /**
         * The labels.
         */
        private final List<ThemisXAnalysisParsedExpr> theLabels;

        /**
         * The body.
         */
        private final List<ThemisXAnalysisParsedStatement> theBody;

        /**
         * Constructor.
         *
         * @param pParser the parser
         * @param pEntry the entry
         * @throws OceanusException on error
         */
        private ThemisXAnalysisStmtCase(final ThemisXAnalysisParser pParser,
                                        final SwitchEntry pEntry) throws OceanusException {
            theEntry = pEntry;
            final Expression myGuard = theEntry.getGuard().orElse(null);
            theGuard = myGuard == null ? null : pParser.parseExpression(myGuard);
            theLabels = pParser.parseExprList(theEntry.getLabels());
            theBody = pParser.parseStatementList(theEntry.getStatements());
        }

        /**
         * Obtain the statement.
         *
         * @return the statement
         */
        public SwitchEntry getStatement() {
            return theEntry;
        }

        /**
         * Obtain the guard.
         * @return the guard
         */
        public ThemisXAnalysisParsedExpr getGuard() {
            return theGuard;
        }

        /**
         * Obtain the labels.
         * @return the labels
         */
        public List<ThemisXAnalysisParsedExpr> getLabels() {
            return theLabels;
        }

        /**
         * Obtain the body.
         * @return the body
         */
        public List<ThemisXAnalysisParsedStatement> getBody() {
            return theBody;
        }

        @Override
        public String toString() {
            return theEntry.toString();
        }
    }
}