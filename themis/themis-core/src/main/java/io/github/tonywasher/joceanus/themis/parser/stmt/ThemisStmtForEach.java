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

import com.github.javaparser.ast.stmt.ForEachStmt;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * ForEach Statement.
 */
public class ThemisStmtForEach
        extends ThemisBaseStatement<ForEachStmt> {
    /**
     * The body.
     */
    private final ThemisStatementInstance theBody;

    /**
     * The variable.
     */
    private final ThemisExpressionInstance theVariable;

    /**
     * The variable declarator.
     */
    private final ThemisNodeInstance theVariableDeclarator;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisStmtForEach(final ThemisParserDef pParser,
                      final ForEachStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theVariable = pParser.parseExpression(pStatement.getVariable());
        theVariableDeclarator = pParser.parseNode(pStatement.getVariableDeclarator());
        theBody = pParser.parseStatement(pStatement.getBody());
    }

    /**
     * Obtain the variable.
     *
     * @return the variable
     */
    public ThemisExpressionInstance getVariable() {
        return theVariable;
    }

    /**
     * Obtain the variable declarator.
     *
     * @return the declarator
     */
    public ThemisNodeInstance getVariableDeclarator() {
        return theVariableDeclarator;
    }

    /**
     * Obtain the body.
     *
     * @return the body
     */
    public ThemisStatementInstance getBody() {
        return theBody;
    }
}
