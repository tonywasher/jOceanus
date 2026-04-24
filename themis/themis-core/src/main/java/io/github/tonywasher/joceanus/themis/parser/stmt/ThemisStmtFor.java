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

import com.github.javaparser.ast.stmt.ForStmt;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * For Statement.
 */
public class ThemisStmtFor
        extends ThemisBaseStatement<ForStmt> {
    /**
     * The body.
     */
    private final ThemisStatementInstance theBody;

    /**
     * The Init.
     */
    private final List<ThemisExpressionInstance> theInit;

    /**
     * The compare.
     */
    private final ThemisExpressionInstance theCompare;

    /**
     * The Updates.
     */
    private final List<ThemisExpressionInstance> theUpdates;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisStmtFor(final ThemisParserDef pParser,
                  final ForStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theInit = pParser.parseExprList(pStatement.getInitialization());
        theCompare = pParser.parseExpression(pStatement.getCompare().orElse(null));
        theUpdates = pParser.parseExprList(pStatement.getUpdate());
        theBody = pParser.parseStatement(pStatement.getBody());
    }

    /**
     * Obtain the init.
     *
     * @return the init
     */
    public List<ThemisExpressionInstance> getInit() {
        return theInit;
    }

    /**
     * Obtain the compare.
     *
     * @return the compare
     */
    public ThemisExpressionInstance getCompare() {
        return theCompare;
    }

    /**
     * Obtain the updates.
     *
     * @return the updates
     */
    public List<ThemisExpressionInstance> getUpdates() {
        return theUpdates;
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
