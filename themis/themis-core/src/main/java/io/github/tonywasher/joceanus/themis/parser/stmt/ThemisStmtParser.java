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

import com.github.javaparser.ast.stmt.Statement;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisStatementInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Analysis Statement Parser.
 */
public final class ThemisStmtParser {
    /**
     * Private Constructor.
     */
    private ThemisStmtParser() {
    }

    /**
     * Parse a statement.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @return the parsed statement
     * @throws OceanusException on error
     */
    public static ThemisStatementInstance parseStatement(final ThemisParserDef pParser,
                                                         final Statement pStatement) throws OceanusException {
        /* Handle null Statement */
        if (pStatement == null) {
            return null;
        }

        /* Allocate correct Statement */
        switch (ThemisStatement.determineStatement(pParser, pStatement)) {
            case ASSERT:
                return new ThemisStmtAssert(pParser, pStatement.asAssertStmt());
            case BLOCK:
                return new ThemisStmtBlock(pParser, pStatement.asBlockStmt());
            case BREAK:
                return new ThemisStmtBreak(pParser, pStatement.asBreakStmt());
            case CONSTRUCTOR:
                return new ThemisStmtConstructor(pParser, pStatement.asExplicitConstructorInvocationStmt());
            case CONTINUE:
                return new ThemisStmtContinue(pParser, pStatement.asContinueStmt());
            case DO:
                return new ThemisStmtDo(pParser, pStatement.asDoStmt());
            case EMPTY:
                return new ThemisStmtEmpty(pParser, pStatement.asEmptyStmt());
            case EXPRESSION:
                return new ThemisStmtExpression(pParser, pStatement.asExpressionStmt());
            case FOR:
                return new ThemisStmtFor(pParser, pStatement.asForStmt());
            case FOREACH:
                return new ThemisStmtForEach(pParser, pStatement.asForEachStmt());
            case IF:
                return new ThemisStmtIf(pParser, pStatement.asIfStmt());
            case LABELED:
                return new ThemisStmtLabeled(pParser, pStatement.asLabeledStmt());
            case LOCALCLASS:
                return new ThemisStmtClass(pParser, pStatement.asLocalClassDeclarationStmt());
            case LOCALRECORD:
                return new ThemisStmtRecord(pParser, pStatement.asLocalRecordDeclarationStmt());
            case RETURN:
                return new ThemisStmtReturn(pParser, pStatement.asReturnStmt());
            case SWITCH:
                return new ThemisStmtSwitch(pParser, pStatement.asSwitchStmt());
            case SYNCHRONIZED:
                return new ThemisStmtSynch(pParser, pStatement.asSynchronizedStmt());
            case THROW:
                return new ThemisStmtThrow(pParser, pStatement.asThrowStmt());
            case TRY:
                return new ThemisStmtTry(pParser, pStatement.asTryStmt());
            case WHILE:
                return new ThemisStmtWhile(pParser, pStatement.asWhileStmt());
            case YIELD:
                return new ThemisStmtYield(pParser, pStatement.asYieldStmt());
            default:
                throw pParser.buildException("Unsupported Statement Type", pStatement);
        }
    }
}
