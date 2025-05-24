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
package net.sourceforge.joceanus.themis.xanalysis.base;

import com.github.javaparser.ast.stmt.Statement;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;

import java.util.function.Predicate;

/**
 * Analysis StatementType.
 */
public enum ThemisXAnalysisStatement {
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
     * @param pStatement the statement
     * @return the StatementType
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisStatement determineStatement(final Statement pStatement) throws OceanusException {
        /* Loop testing each statement type */
        for (ThemisXAnalysisStatement myType : values()) {
            if (myType.theTester.test(pStatement)) {
                return myType;
            }
        }

        /* Unrecognised statementType */
        throw new ThemisDataException("Unexpected Statement " +  pStatement.getClass().getCanonicalName());
    }
}
