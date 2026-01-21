/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.themis.xanalysis.parser.stmt;

import com.github.javaparser.ast.stmt.WhileStmt;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * While Statement.
 */
public class ThemisXAnalysisStmtWhile
        extends ThemisXAnalysisBaseStatement<WhileStmt> {
    /**
     * The while condition.
     */
    private final ThemisXAnalysisExpressionInstance theCondition;

    /**
     * The body.
     */
    private final ThemisXAnalysisStatementInstance theBody;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisStmtWhile(final ThemisXAnalysisParserDef pParser,
                             final WhileStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theCondition = pParser.parseExpression(pStatement.getCondition());
        theBody = pParser.parseStatement(pStatement.getBody());
    }

    /**
     * Obtain the condition.
     *
     * @return the condition
     */
    public ThemisXAnalysisExpressionInstance getCondition() {
        return theCondition;
    }

    /**
     * Obtain the body.
     *
     * @return the body
     */
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }
}
