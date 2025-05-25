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

import com.github.javaparser.ast.stmt.SynchronizedStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

/**
 * Synchronized Statement.
 */
public class ThemisXAnalysisStmtSynch
        extends ThemisXAnalysisBaseStatement<SynchronizedStmt> {
    /**
     * The synched.
     */
    private final ThemisXAnalysisExpressionInstance theSynched;

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
    ThemisXAnalysisStmtSynch(final ThemisXAnalysisParser pParser,
                             final SynchronizedStmt pStatement) throws OceanusException {
        super(pStatement);
        theBody = pParser.parseStatement(pStatement.getBody());
        theSynched = pParser.parseExpression(pStatement.getExpression());
    }

    /**
     * Obtain the synched.
     * @return the synched
     */
    public ThemisXAnalysisExpressionInstance getSynched() {
        return theSynched;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }
}
