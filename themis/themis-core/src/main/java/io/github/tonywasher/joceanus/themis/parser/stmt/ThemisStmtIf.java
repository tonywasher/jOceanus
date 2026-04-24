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
package io.github.tonywasher.joceanus.themis.parser.stmt;

import com.github.javaparser.ast.stmt.IfStmt;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * If Statement.
 */
public class ThemisStmtIf
        extends ThemisBaseStatement<IfStmt> {
    /**
     * The condition.
     */
    private final ThemisExpressionInstance theCondition;

    /**
     * The then statement.
     */
    private final ThemisStatementInstance theThen;

    /**
     * The else statement.
     */
    private final ThemisStatementInstance theElse;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisStmtIf(final ThemisParserDef pParser,
                 final IfStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theCondition = pParser.parseExpression(pStatement.getCondition());
        theThen = pParser.parseStatement(pStatement.getThenStmt());
        theElse = pParser.parseStatement(pStatement.getElseStmt().orElse(null));
    }

    /**
     * Obtain the condition.
     *
     * @return the condition
     */
    public ThemisExpressionInstance getCondition() {
        return theCondition;
    }

    /**
     * Obtain the then.
     *
     * @return the then
     */
    public ThemisStatementInstance getThen() {
        return theThen;
    }

    /**
     * Obtain the else.
     *
     * @return the else
     */
    public ThemisStatementInstance getElse() {
        return theElse;
    }
}
