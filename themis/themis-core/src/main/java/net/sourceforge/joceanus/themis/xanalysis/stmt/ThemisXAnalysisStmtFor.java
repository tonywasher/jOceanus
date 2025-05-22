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

import com.github.javaparser.ast.stmt.ForStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisBaseStatement;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;

import java.util.List;

/**
 * For Statement.
 */
public class ThemisXAnalysisStmtFor
        extends ThemisXAnalysisBaseStatement<ForStmt> {
    /**
     * The body.
     */
    private final ThemisXAnalysisStatementInstance theBody;

    /**
     * The Init.
     */
    private final List<ThemisXAnalysisExpressionInstance> theInit;

    /**
     * The compare.
     */
    private final ThemisXAnalysisExpressionInstance theCompare;

    /**
     * The Updates.
     */
    private final List<ThemisXAnalysisExpressionInstance> theUpdates;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtFor(final ThemisXAnalysisParser pParser,
                                  final ForStmt pStatement) throws OceanusException {
        super(pStatement);
        theInit = pParser.parseExprList(pStatement.getInitialization());
        theCompare = pParser.parseExpression(pStatement.getCompare().orElse(null));
        theUpdates = pParser.parseExprList(pStatement.getUpdate());
        theBody = pParser.parseStatement(pStatement.getBody());
    }

    /**
     * Obtain the init.
     * @return the init
     */
    public List<ThemisXAnalysisExpressionInstance> getInit() {
        return theInit;
    }

    /**
     * Obtain the compare.
     * @return the compare
     */
    public ThemisXAnalysisExpressionInstance getCompare() {
        return theCompare;
    }

    /**
     * Obtain the updates.
     * @return the updates
     */
    public List<ThemisXAnalysisExpressionInstance> getUpdates() {
        return theUpdates;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }
}
