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
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisBaseStatement;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Switch Statement.
 */
public class ThemisXAnalysisStmtSwitch
        extends ThemisXAnalysisBaseStatement<SwitchStmt> {
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
        super(pStatement);
        theSelector = pStatement.getSelector();
        theCases = new ArrayList<>();
        for (SwitchEntry myEntry : pStatement.getEntries()) {
            final ThemisXAnalysisStmtCase myCase = new ThemisXAnalysisStmtCase(pParser, myEntry);
            theCases.add(myCase);
        }
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

    /**
     * Switch Case.
     */
    public static final class ThemisXAnalysisStmtCase {
        /**
         * The contents.
         */
        private final SwitchEntry theEntry;

        /**
         * The guard.
         */
        private final ThemisXAnalysisExpressionInstance theGuard;

        /**
         * The labels.
         */
        private final List<ThemisXAnalysisExpressionInstance> theLabels;

        /**
         * The body.
         */
        private final List<ThemisXAnalysisStatementInstance> theBody;

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
        public ThemisXAnalysisExpressionInstance getGuard() {
            return theGuard;
        }

        /**
         * Obtain the labels.
         * @return the labels
         */
        public List<ThemisXAnalysisExpressionInstance> getLabels() {
            return theLabels;
        }

        /**
         * Obtain the body.
         * @return the body
         */
        public List<ThemisXAnalysisStatementInstance> getBody() {
            return theBody;
        }

        @Override
        public String toString() {
            return theEntry.toString();
        }
    }
}
