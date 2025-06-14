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
package net.sourceforge.joceanus.themis.xanalysis.stats.stmt;

import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprArrayAccess;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprArrayCreation;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprArrayInit;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprAssign;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprBinary;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprCast;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprConditional;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprEnclosed;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprInstanceOf;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprLambda;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprMethodCall;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprObjectCreate;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprSwitch;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprUnary;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprVarDecl;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExpression;

/**
 * Statement Counter for Expressions.
 */
public final class ThemisXAnalysisStatsStmtExpr {
    /**
     * Private constructor.
     */
    private ThemisXAnalysisStatsStmtExpr() {
    }

    /**
     * Obtain the statement count for an expression.
     * @param pCounter the counter
     * @param pExpression the expression
     * @return the count
     */
    static int count(final ThemisXAnalysisStatsStmtCounter pCounter,
                     final ThemisXAnalysisExpressionInstance pExpression) {
        /* Handle null Expression */
        if (pExpression == null) {
            return pCounter.fixedCount(0);
        }

        /* Switch on expression id */
        switch ((ThemisXAnalysisExpression) pExpression.getId()) {
            case ARRAYACCESS:     return countArrayAccess(pCounter, (ThemisXAnalysisExprArrayAccess) pExpression);
            case ARRAYCREATION:   return countArrayCreation(pCounter, (ThemisXAnalysisExprArrayCreation) pExpression);
            case ARRAYINIT:       return countArrayInit(pCounter, (ThemisXAnalysisExprArrayInit) pExpression);
            case ASSIGN:          return countAssign(pCounter, (ThemisXAnalysisExprAssign) pExpression);
            case BINARY:          return countBinary(pCounter, (ThemisXAnalysisExprBinary) pExpression);
            case CAST:            return countCast(pCounter, (ThemisXAnalysisExprCast) pExpression);
            case CONDITIONAL:     return countConditional(pCounter, (ThemisXAnalysisExprConditional) pExpression);
            case ENCLOSED:        return countEnclosed(pCounter, (ThemisXAnalysisExprEnclosed) pExpression);
            case INSTANCEOF:      return countInstanceOf(pCounter, (ThemisXAnalysisExprInstanceOf) pExpression);
            case LAMBDA:          return countLambda(pCounter, (ThemisXAnalysisExprLambda) pExpression);
            case METHODCALL:      return countMethodCall(pCounter, (ThemisXAnalysisExprMethodCall) pExpression);
            case OBJECTCREATE:    return countObjectCreate(pCounter, (ThemisXAnalysisExprObjectCreate) pExpression);
            case SWITCH:          return countSwitch(pCounter, (ThemisXAnalysisExprSwitch) pExpression);
            case UNARY:           return countUnary(pCounter, (ThemisXAnalysisExprUnary) pExpression);
            case VARIABLE:        return countVarDecl(pCounter, (ThemisXAnalysisExprVarDecl) pExpression);
            case MARKER:
            case FIELDACCESS:
            case METHODREFERENCE:
            case NORMAL:
            case SINGLEMEMBER:    return pCounter.fixedCount(1);
            case BOOLEAN:
            case CHAR:
            case DOUBLE:
            case INTEGER:
            case LONG:
            case NULL:
            case STRING:
            case TEXTBLOCK:
            case NAME:
            case CLASS:
            case THIS:
            case RECORDPATTERN:
            case TYPEPATTERN:
            case SUPER:
            default:              return pCounter.fixedCount(0);
        }
    }

    /**
     * Obtain the statement count for an ARRAYACCESS expression.
     *
     * @param pCounter the counter
     * @param pArrayAccess the expression
     * @return the count
     */
    private static int countArrayAccess(final ThemisXAnalysisStatsStmtCounter pCounter,
                                        final ThemisXAnalysisExprArrayAccess pArrayAccess) {
        return pCounter.countExpr(pArrayAccess.getIndex());
    }

    /**
     * Obtain the statement count for an ARRAYCREATION expression.
     *
     * @param pCounter the counter
     * @param pArrayCreation the expression
     * @return the count
     */
    private static int countArrayCreation(final ThemisXAnalysisStatsStmtCounter pCounter,
                                          final ThemisXAnalysisExprArrayCreation pArrayCreation) {
        return pCounter.fixedCount(1)
                + pCounter.countExpr(pArrayCreation.getInitializer());
    }

    /**
     * Obtain the statement count for an ARRAYINIT expression.
     *
     * @param pCounter the counter
     * @param pArrayInit the expression
     * @return the count
     */
    private static int countArrayInit(final ThemisXAnalysisStatsStmtCounter pCounter,
                                      final ThemisXAnalysisExprArrayInit pArrayInit) {
        return pCounter.countExprList(pArrayInit.getInitializers());
    }

    /**
     * Obtain the statement count for an ASSIGN expression.
     *
     * @param pCounter the counter
     * @param pAssign the expression
     * @return the count
     */
    private static int countAssign(final ThemisXAnalysisStatsStmtCounter pCounter,
                                   final ThemisXAnalysisExprAssign pAssign) {
        return pCounter.fixedCount(1)
                + pCounter.countExpr(pAssign.getValue());
    }

    /**
     * Obtain the statement count for a BINARY expression.
     *
     * @param pCounter the counter
     * @param pBinary the expression
     * @return the count
     */
    private static int countBinary(final ThemisXAnalysisStatsStmtCounter pCounter,
                                   final ThemisXAnalysisExprBinary pBinary) {
        return pCounter.countExpr(pBinary.getLeft())
                + pCounter.countExpr(pBinary.getRight());
    }

    /**
     * Obtain the statement count for a CAST expression.
     *
     * @param pCounter the counter
     * @param pCast the expression
     * @return the count
     */
    private static int countCast(final ThemisXAnalysisStatsStmtCounter pCounter,
                                 final ThemisXAnalysisExprCast pCast) {
        return pCounter.countExpr(pCast.getValue());
    }

    /**
     * Obtain the statement count for a CONDITIONAL expression.
     *
     * @param pCounter the counter
     * @param pConditional the expression
     * @return the count
     */
    private static int countConditional(final ThemisXAnalysisStatsStmtCounter pCounter,
                                        final ThemisXAnalysisExprConditional pConditional) {
        return pCounter.countExpr(pConditional.getCondition())
                + pCounter.countExpr(pConditional.getThen())
                + pCounter.countExpr(pConditional.getElse());
    }

    /**
     * Obtain the statement count for an ENCLOSED expression.
     *
     * @param pCounter the counter
     * @param pEnclosed the expression
     * @return the count
     */
    private static int countEnclosed(final ThemisXAnalysisStatsStmtCounter pCounter,
                                     final ThemisXAnalysisExprEnclosed pEnclosed) {
        return pCounter.countExpr(pEnclosed.getInner());
    }

    /**
     * Obtain the statement count for an INSTANCEOF expression.
     *
     * @param pCounter the counter
     * @param pInstanceOf the expression
     * @return the count
     */
    private static int countInstanceOf(final ThemisXAnalysisStatsStmtCounter pCounter,
                                       final ThemisXAnalysisExprInstanceOf pInstanceOf) {
        return pCounter.countExpr(pInstanceOf.getValue());
    }

    /**
     * Obtain the statement count for a LAMBDA expression.
     *
     * @param pCounter the counter
     * @param pLambda the expression
     * @return the count
     */
    private static int countLambda(final ThemisXAnalysisStatsStmtCounter pCounter,
                                   final ThemisXAnalysisExprLambda pLambda) {
        return pCounter.countStmt(pLambda.getBody());
    }

    /**
     * Obtain the statement count for a METHODCALL expression.
     *
     * @param pCounter the counter
     * @param pMethodCall the expression
     * @return the count
     */
    private static int countMethodCall(final ThemisXAnalysisStatsStmtCounter pCounter,
                                       final ThemisXAnalysisExprMethodCall pMethodCall) {
        return pCounter.fixedCount(1)
                + pCounter.countExprList(pMethodCall.getArguments());
    }

    /**
     * Obtain the statement count for an OBJECTCREATE expression.
     *
     * @param pCounter the counter
     * @param pObjCreate the expression
     * @return the count
     */
    private static int countObjectCreate(final ThemisXAnalysisStatsStmtCounter pCounter,
                                         final ThemisXAnalysisExprObjectCreate pObjCreate) {
        /* Note that anonymous class is handled elsewhere */
        return pCounter.fixedCount(1)
                + pCounter.countExprList(pObjCreate.getArgs());
    }

    /**
     * Obtain the statement count for a SWITCH statement.
     *
     * @param pCounter the counter
     * @param pSwitch the statement
     * @return the count
     */
    private static int countSwitch(final ThemisXAnalysisStatsStmtCounter pCounter,
                                   final ThemisXAnalysisExprSwitch pSwitch) {
        return pCounter.countExpr(pSwitch.getSelector())
                + pCounter.countNodeList(pSwitch.getCases());
    }

    /**
     * Obtain the statement count for a UNARY statement.
     *
     * @param pCounter the counter
     * @param pUnary the statement
     * @return the count
     */
    private static int countUnary(final ThemisXAnalysisStatsStmtCounter pCounter,
                                 final ThemisXAnalysisExprUnary pUnary) {
        return pCounter.fixedCount(1)
                + pCounter.countExpr(pUnary.getTarget());
    }

    /**
     * Obtain the statement count for a VARDECL statement.
     *
     * @param pCounter the counter
     * @param pVarDecl the statement
     * @return the count
     */
    private static int countVarDecl(final ThemisXAnalysisStatsStmtCounter pCounter,
                                    final ThemisXAnalysisExprVarDecl pVarDecl) {
        return pCounter.countNodeList(pVarDecl.getVariables());
    }
}
