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

import com.github.javaparser.ast.stmt.AssertStmt;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Assert Statement.
 */
public class ThemisStmtAssert
        extends ThemisBaseStatement<AssertStmt> {
    /**
     * The check.
     */
    private final ThemisExpressionInstance theCheck;

    /**
     * The message.
     */
    private final ThemisExpressionInstance theMessage;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisStmtAssert(final ThemisParserDef pParser,
                     final AssertStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theCheck = pParser.parseExpression(pStatement.getCheck());
        theMessage = pParser.parseExpression(pStatement.getMessage().orElse(null));
    }

    /**
     * Obtain the check.
     *
     * @return the check
     */
    public ThemisExpressionInstance getCheck() {
        return theCheck;
    }

    /**
     * Obtain the message.
     *
     * @return the message
     */
    public ThemisExpressionInstance getMessage() {
        return theMessage;
    }
}
