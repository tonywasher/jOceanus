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

import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;

import java.util.List;

/**
 * Statement Counter.
 */
public interface ThemisXAnalysisStatsStmtCounter {
    /**
     * Obtain the statement count for a declaration.
     *
     * @param pDecl the declaration
     * @return the count
     */
    int countDecl(final ThemisXAnalysisDeclarationInstance pDecl);

    /**
     * Obtain the statement count for a list of declarations.
     *
     * @param pDeclList the declaration list
     * @return the count
     */
    default int countDeclList(final List<ThemisXAnalysisDeclarationInstance> pDeclList) {
        return pDeclList.stream().mapToInt(this::countDecl).sum();
    }

    /**
     * Obtain the statement count for a node.
     *
     * @param pNode the node
     * @return the count
     */
    int countNode(final ThemisXAnalysisNodeInstance pNode);

    /**
     * Obtain the statement count for a list of nodes.
     *
     * @param pNodeList the node list
     * @return the count
     */
    default int countNodeList(final List<ThemisXAnalysisNodeInstance> pNodeList) {
        return pNodeList.stream().mapToInt(this::countNode).sum();
    }

    /**
     * Obtain the statement count for an expression.
     *
     * @param pExpression the expression
     * @return the count
     */
    int countExpr(final ThemisXAnalysisExpressionInstance pExpression);

    /**
     * Obtain the statement count for a list of expressions.
     *
     * @param pExprList the expression list
     * @return the count
     */
    default int countExprList(final List<ThemisXAnalysisExpressionInstance> pExprList) {
        return pExprList.stream().mapToInt(this::countExpr).sum();
    }

    /**
     * Obtain the statement count for a statement.
     *
     * @param pStatement the statement
     * @return the count
     */
    int countStmt(final ThemisXAnalysisStatementInstance pStatement);

    /**
     * Obtain the statement count for a list of statements.
     *
     * @param pStmtList the instance list
     * @return the count
     */
    default int countStmtList(final List<ThemisXAnalysisStatementInstance> pStmtList) {
        return pStmtList.stream().mapToInt(this::countStmt).sum();
    }

    /**
     * Obtain the fixed statement count.
     *
     * @param pCount the count
     * @return the count
     */
    default int fixedCount(final int pCount) {
        return pCount;
    }
}
