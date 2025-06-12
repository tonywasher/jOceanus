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
package net.sourceforge.joceanus.themis.xanalysis.parser.stmt;

import com.github.javaparser.ast.stmt.AssertStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Assert Statement.
 */
public class ThemisXAnalysisStmtAssert
        extends ThemisXAnalysisBaseStatement<AssertStmt> {
    /**
     * The check.
     */
    private final ThemisXAnalysisExpressionInstance theCheck;

    /**
     * The message.
     */
    private final ThemisXAnalysisExpressionInstance theMessage;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisStmtAssert(final ThemisXAnalysisParserDef pParser,
                              final AssertStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theCheck = pParser.parseExpression(pStatement.getCheck());
        theMessage = pParser.parseExpression(pStatement.getMessage().orElse(null));
    }

    /**
     * Obtain the check.
     * @return the check
     */
    public ThemisXAnalysisExpressionInstance getCheck() {
        return theCheck;
    }

    /**
     * Obtain the message.
     * @return the message
     */
    public ThemisXAnalysisExpressionInstance getMessage() {
        return theMessage;
    }
}
