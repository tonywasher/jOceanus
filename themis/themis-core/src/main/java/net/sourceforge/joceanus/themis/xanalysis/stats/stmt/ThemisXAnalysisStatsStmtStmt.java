/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.stats.stmt;

import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStatement;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtAssert;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtBlock;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtConstructor;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtDo;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtExpression;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtFor;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtForEach;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtIf;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtLabeled;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtReturn;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtSwitch;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtSynch;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtThrow;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtTry;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtWhile;
import net.sourceforge.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStmtYield;

/**
 * Statement Counter for Statements.
 */
public final class ThemisXAnalysisStatsStmtStmt {
    /**
     * Private constructor.
     */
    private ThemisXAnalysisStatsStmtStmt() {
    }

    /**
     * Obtain the statement count for a statement.
     *
     * @param pCounter the counter
     * @param pStatement the statement
     * @return the count
     */
    static int count(final ThemisXAnalysisStatsStmtCounter pCounter,
                     final ThemisXAnalysisStatementInstance pStatement) {
        /* Handle null statement */
        if (pStatement == null) {
            return pCounter.fixedCount(0);
        }

        /* Switch on statement id */
        switch ((ThemisXAnalysisStatement) pStatement.getId()) {
            case ASSERT:       return countAssert(pCounter, (ThemisXAnalysisStmtAssert) pStatement);
            case BLOCK:        return countBlock(pCounter, (ThemisXAnalysisStmtBlock) pStatement);
            case CONSTRUCTOR:  return countConstructor(pCounter, (ThemisXAnalysisStmtConstructor) pStatement);
            case DO:           return countDo(pCounter, (ThemisXAnalysisStmtDo) pStatement);
            case EXPRESSION:   return countExpression(pCounter, (ThemisXAnalysisStmtExpression) pStatement);
            case FOR:          return countFor(pCounter, (ThemisXAnalysisStmtFor) pStatement);
            case FOREACH:      return countForEach(pCounter, (ThemisXAnalysisStmtForEach) pStatement);
            case IF:           return countIf(pCounter, (ThemisXAnalysisStmtIf) pStatement);
            case LABELED:      return countLabeled(pCounter, (ThemisXAnalysisStmtLabeled) pStatement);
            case RETURN:       return countReturn(pCounter, (ThemisXAnalysisStmtReturn) pStatement);
            case SWITCH:       return countSwitch(pCounter, (ThemisXAnalysisStmtSwitch) pStatement);
            case SYNCHRONIZED: return countSynch(pCounter, (ThemisXAnalysisStmtSynch) pStatement);
            case THROW:        return countThrow(pCounter, (ThemisXAnalysisStmtThrow) pStatement);
            case TRY:          return countTry(pCounter, (ThemisXAnalysisStmtTry) pStatement);
            case WHILE:        return countWhile(pCounter, (ThemisXAnalysisStmtWhile) pStatement);
            case YIELD:        return countYield(pCounter, (ThemisXAnalysisStmtYield) pStatement);
            case BREAK:
            case CONTINUE:     return pCounter.fixedCount(1);
            case EMPTY:
            case LOCALCLASS:
            case LOCALRECORD:
            default:           return pCounter.fixedCount(0);
        }
    }

    /**
     * Obtain the statement count for an ASSERT statement.
     *
     * @param pCounter the counter
     * @param pAssert the statement
     * @return the count
     */
    private static int countAssert(final ThemisXAnalysisStatsStmtCounter pCounter,
                                   final ThemisXAnalysisStmtAssert pAssert) {
        int myCount = pCounter.countExpr(pAssert.getCheck());
        myCount += pCounter.countExpr(pAssert.getMessage());
        return myCount;
    }

    /**
     * Obtain the statement count for a BLOCK statement.
     *
     * @param pCounter the counter
     * @param pBlock the statement
     * @return the count
     */
    private static int countBlock(final ThemisXAnalysisStatsStmtCounter pCounter,
                                  final ThemisXAnalysisStmtBlock pBlock) {
        return pCounter.countStmtList(pBlock.getBody());
    }

    /**
     * Obtain the statement count for a CONSTRUCTOR statement.
     *
     * @param pCounter the counter
     * @param pConstructor the statement
     * @return the count
     */
    private static int countConstructor(final ThemisXAnalysisStatsStmtCounter pCounter,
                                        final ThemisXAnalysisStmtConstructor pConstructor) {
        return pCounter.countExpr(pConstructor.getExpression())
                + pCounter.countExprList(pConstructor.getArguments());
    }

    /**
     * Obtain the statement count for a DO statement.
     *
     * @param pCounter the counter
     * @param pDo the statement
     * @return the count
     */
    private static int countDo(final ThemisXAnalysisStatsStmtCounter pCounter,
                               final ThemisXAnalysisStmtDo pDo) {
        return pCounter.countExpr(pDo.getCondition())
                + pCounter.countStmt(pDo.getBody());
    }

    /**
     * Obtain the statement count for an EXPRESSION statement.
     *
     * @param pCounter the counter
     * @param pExpression the statement
     * @return the count
     */
    private static int countExpression(final ThemisXAnalysisStatsStmtCounter pCounter,
                                       final ThemisXAnalysisStmtExpression pExpression) {
        return pCounter.countExpr(pExpression.getExpression());
    }

    /**
     * Obtain the statement count for a FOR statement.
     *
     * @param pCounter the counter
     * @param pFor the statement
     * @return the count
     */
    private static int countFor(final ThemisXAnalysisStatsStmtCounter pCounter,
                                final ThemisXAnalysisStmtFor pFor) {
        return pCounter.countExprList(pFor.getInit())
                + pCounter.countExpr(pFor.getCompare())
                + pCounter.countExprList(pFor.getUpdates())
                + pCounter.countStmt(pFor.getBody());
    }

    /**
     * Obtain the statement count for a FOREACH statement.
     *
     * @param pCounter the counter
     * @param pFor the statement
     * @return the count
     */
    private static int countForEach(final ThemisXAnalysisStatsStmtCounter pCounter,
                                    final ThemisXAnalysisStmtForEach pFor) {
        return pCounter.countNode(pFor.getVariableDeclarator())
                + pCounter.countStmt(pFor.getBody());
    }

    /**
     * Obtain the statement count for an IF statement.
     *
     * @param pCounter the counter
     * @param pIf the statement
     * @return the count
     */
    private static int countIf(final ThemisXAnalysisStatsStmtCounter pCounter,
                               final ThemisXAnalysisStmtIf pIf) {
        return pCounter.countExpr(pIf.getCondition())
                + pCounter.countStmt(pIf.getThen())
                + pCounter.countStmt(pIf.getElse());
    }

    /**
     * Obtain the statement count for a LABELED statement.
     *
     * @param pCounter the counter
     * @param pLabeled the statement
     * @return the count
     */
    private static int countLabeled(final ThemisXAnalysisStatsStmtCounter pCounter,
                                    final ThemisXAnalysisStmtLabeled pLabeled) {
        return pCounter.countStmt(pLabeled.getLabeled());
    }

    /**
     * Obtain the statement count for a RETURN statement.
     *
     * @param pCounter the counter
     * @param pReturn the statement
     * @return the count
     */
    private static int countReturn(final ThemisXAnalysisStatsStmtCounter pCounter,
                                   final ThemisXAnalysisStmtReturn pReturn) {
        return pCounter.fixedCount(1)
                + pCounter.countExpr(pReturn.getExpression());
    }

    /**
     * Obtain the statement count for a SWITCH statement.
     *
     * @param pCounter the counter
     * @param pSwitch the statement
     * @return the count
     */
    private static int countSwitch(final ThemisXAnalysisStatsStmtCounter pCounter,
                                   final ThemisXAnalysisStmtSwitch pSwitch) {
        return pCounter.countExpr(pSwitch.getSelector())
                + pCounter.countNodeList(pSwitch.getCases());
    }

    /**
     * Obtain the statement count for a Synchronised statement.
     *
     * @param pCounter the counter
     * @param pSync the statement
     * @return the count
     */
    private static int countSynch(final ThemisXAnalysisStatsStmtCounter pCounter,
                                  final ThemisXAnalysisStmtSynch pSync) {
        return pCounter.countExpr(pSync.getSynched())
                + pCounter.countStmt(pSync.getBody());
    }

    /**
     * Obtain the statement count for a THROW statement.
     *
     * @param pCounter the counter
     * @param pThrow the statement
     * @return the count
     */
    private static int countThrow(final ThemisXAnalysisStatsStmtCounter pCounter,
                                  final ThemisXAnalysisStmtThrow pThrow) {
        return pCounter.fixedCount(1)
                + pCounter.countExpr(pThrow.getThrown());
    }

    /**
     * Obtain the statement count for a TRY statement.
     *
     * @param pCounter the counter
     * @param pTry the statement
     * @return the count
     */
    private static int countTry(final ThemisXAnalysisStatsStmtCounter pCounter,
                                final ThemisXAnalysisStmtTry pTry) {
        return pCounter.countExprList(pTry.getResources())
                + pCounter.countStmt(pTry.getTry())
                + pCounter.countNodeList(pTry.getCatches())
                + pCounter.countStmt(pTry.getFinally());
    }

    /**
     * Obtain the statement count for a WHILE statement.
     *
     * @param pCounter the counter
     * @param pWhile the statement
     * @return the count
     */
    private static int countWhile(final ThemisXAnalysisStatsStmtCounter pCounter,
                                  final ThemisXAnalysisStmtWhile pWhile) {
        return pCounter.countExpr(pWhile.getCondition())
                + pCounter.countStmt(pWhile.getBody());
    }

    /**
     * Obtain the statement count for a YIELD statement.
     *
     * @param pCounter the counter
     * @param pYield the statement
     * @return the count
     */
    private static int countYield(final ThemisXAnalysisStatsStmtCounter pCounter,
                                  final ThemisXAnalysisStmtYield pYield) {
        return pCounter.fixedCount(1)
                + pCounter.countExpr(pYield.getExpression());
    }
}
