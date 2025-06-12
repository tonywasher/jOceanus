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

import com.github.javaparser.ast.stmt.Statement;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisId;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;

import java.util.function.Predicate;

/**
 * Analysis StatementType.
 */
public enum ThemisXAnalysisStatement
        implements ThemisXAnalysisId {
    /**
     * Assert.
     */
    ASSERT(Statement::isAssertStmt),

    /**
     * Block.
     */
    BLOCK(Statement::isBlockStmt),

    /**
     * Break.
     */
    BREAK(Statement::isBreakStmt),

    /**
     * Continue.
     */
    CONTINUE(Statement::isContinueStmt),

    /**
     * Constructor.
     */
    CONSTRUCTOR(Statement::isExplicitConstructorInvocationStmt),

    /**
     * Do.
     */
    DO(Statement::isDoStmt),

    /**
     * Empty.
     */
    EMPTY(Statement::isEmptyStmt),

    /**
     * Expression.
     */
    EXPRESSION(Statement::isExpressionStmt),

    /**
     * For.
     */
    FOR(Statement::isForStmt),

    /**
     * ForEach.
     */
    FOREACH(Statement::isForEachStmt),

    /**
     * If.
     */
    IF(Statement::isIfStmt),

    /**
     * Labeled.
     */
    LABELED(Statement::isLabeledStmt),

    /**
     * LocalClass.
     */
    LOCALCLASS(Statement::isLocalClassDeclarationStmt),

    /**
     * LocalRecord.
     */
    LOCALRECORD(Statement::isLocalRecordDeclarationStmt),

    /**
     * Return.
     */
    RETURN(Statement::isReturnStmt),

    /**
     * ForEach.
     */
    SWITCH(Statement::isSwitchStmt),

    /**
     * Synchronized.
     */
    SYNCHRONIZED(Statement::isSynchronizedStmt),

    /**
     * Throw.
     */
    THROW(Statement::isThrowStmt),

    /**
     * Try.
     */
    TRY(Statement::isTryStmt),

    /**
     * While.
     */
    WHILE(Statement::isWhileStmt),

    /**
     * Yield.
     */
    YIELD(Statement::isYieldStmt);

    /**
     * The test.
     */
    private final Predicate<Statement> theTester;

    /**
     * Constructor.
     * @param pTester the test method
     */
    ThemisXAnalysisStatement(final Predicate<Statement> pTester) {
        theTester = pTester;
    }

    /**
     * Determine type of statement.
     * @param pParser the parser
     * @param pStatement the statement
     * @return the StatementType
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisStatement determineStatement(final ThemisXAnalysisParserDef pParser,
                                                              final Statement pStatement) throws OceanusException {
        /* Loop testing each statement type */
        for (ThemisXAnalysisStatement myType : values()) {
            if (myType.theTester.test(pStatement)) {
                return myType;
            }
        }

        /* Unrecognised statementType */
        throw pParser.buildException("Unexpected Statement", pStatement);
    }

    /**
     * Parse a statement.
     * @param pParser the parser
     * @param pStatement the statement
     * @return the parsed statement
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisStatementInstance parseStatement(final ThemisXAnalysisParserDef pParser,
                                                                  final Statement pStatement) throws OceanusException {
        /* Handle null Statement */
        if (pStatement == null) {
            return null;
        }

        /* Allocate correct Statement */
        switch (ThemisXAnalysisStatement.determineStatement(pParser, pStatement)) {
            case ASSERT:       return new ThemisXAnalysisStmtAssert(pParser, pStatement.asAssertStmt());
            case BLOCK:        return new ThemisXAnalysisStmtBlock(pParser, pStatement.asBlockStmt());
            case BREAK:        return new ThemisXAnalysisStmtBreak(pParser, pStatement.asBreakStmt());
            case CONSTRUCTOR:  return new ThemisXAnalysisStmtConstructor(pParser, pStatement.asExplicitConstructorInvocationStmt());
            case CONTINUE:     return new ThemisXAnalysisStmtContinue(pParser, pStatement.asContinueStmt());
            case DO:           return new ThemisXAnalysisStmtDo(pParser, pStatement.asDoStmt());
            case EMPTY:        return new ThemisXAnalysisStmtEmpty(pParser, pStatement.asEmptyStmt());
            case EXPRESSION:   return new ThemisXAnalysisStmtExpression(pParser, pStatement.asExpressionStmt());
            case FOR:          return new ThemisXAnalysisStmtFor(pParser, pStatement.asForStmt());
            case FOREACH:      return new ThemisXAnalysisStmtForEach(pParser, pStatement.asForEachStmt());
            case IF:           return new ThemisXAnalysisStmtIf(pParser, pStatement.asIfStmt());
            case LABELED:      return new ThemisXAnalysisStmtLabeled(pParser, pStatement.asLabeledStmt());
            case LOCALCLASS:   return new ThemisXAnalysisStmtClass(pParser, pStatement.asLocalClassDeclarationStmt());
            case LOCALRECORD:  return new ThemisXAnalysisStmtRecord(pParser, pStatement.asLocalRecordDeclarationStmt());
            case RETURN:       return new ThemisXAnalysisStmtReturn(pParser, pStatement.asReturnStmt());
            case SWITCH:       return new ThemisXAnalysisStmtSwitch(pParser, pStatement.asSwitchStmt());
            case SYNCHRONIZED: return new ThemisXAnalysisStmtSynch(pParser, pStatement.asSynchronizedStmt());
            case THROW:        return new ThemisXAnalysisStmtThrow(pParser, pStatement.asThrowStmt());
            case TRY:          return new ThemisXAnalysisStmtTry(pParser, pStatement.asTryStmt());
            case WHILE:        return new ThemisXAnalysisStmtWhile(pParser, pStatement.asWhileStmt());
            case YIELD:        return new ThemisXAnalysisStmtYield(pParser, pStatement.asYieldStmt());
            default:           throw pParser.buildException("Unsupported Statement Type", pStatement);
        }
    }
}
