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

import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNode;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeArrayLevel;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeCase;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeCatch;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeCompilationUnit;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeVariable;

/**
 * Statement Counter for Nodes.
 */
public final class ThemisXAnalysisStatsStmtNode {
    /**
     * Private constructor.
     */
    private ThemisXAnalysisStatsStmtNode() {
    }

    /**
     * Obtain the statement count for a node.
     *
     * @param pCounter the counter
     * @param pNode the node
     * @return the count
     */
    static int count(final ThemisXAnalysisStatsStmtCounter pCounter,
                     final ThemisXAnalysisNodeInstance pNode) {
        /* Handle null node */
        if (pNode == null) {
            return pCounter.fixedCount(0);
        }

        /* Switch on node id */
        switch ((ThemisXAnalysisNode) pNode.getId()) {
            case ARRAYLEVEL:      return countArrayLevel(pCounter, (ThemisXAnalysisNodeArrayLevel) pNode);
            case CASE:            return countCase(pCounter, (ThemisXAnalysisNodeCase) pNode);
            case CATCH:           return countCatch(pCounter, (ThemisXAnalysisNodeCatch) pNode);
            case COMPILATIONUNIT: return countCompilationUnit(pCounter, (ThemisXAnalysisNodeCompilationUnit) pNode);
            case VARIABLE:        return countVariable(pCounter, (ThemisXAnalysisNodeVariable) pNode);
            case IMPORT:
            case PACKAGE:         return pCounter.fixedCount(1);
            case COMMENT:
            case MODIFIER:
            case NAME:
            case SIMPLENAME:
            case PARAMETER:
            case VALUEPAIR:
            default:              return pCounter.fixedCount(0);
        }
    }

    /**
     * Obtain the statement count for an ARRAYLEVEL node.
     *
     * @param pCounter the counter
     * @param pLevel the node
     * @return the count
     */
    private static int countArrayLevel(final ThemisXAnalysisStatsStmtCounter pCounter,
                                       final ThemisXAnalysisNodeArrayLevel pLevel) {
        return pCounter.countExpr(pLevel.getValue());
    }

    /**
     * Obtain the statement count for a CASE node.
     *
     * @param pCounter the counter
     * @param pCase the node
     * @return the count
     */
    private static int countCase(final ThemisXAnalysisStatsStmtCounter pCounter,
                                 final ThemisXAnalysisNodeCase pCase) {
        return pCounter.fixedCount(1)
                + pCounter.countExpr(pCase.getGuard())
                + pCounter.countStmtList(pCase.getBody());
    }

    /**
     * Obtain the statement count for a CATCH node.
     *
     * @param pCounter the counter
     * @param pCatch the node
     * @return the count
     */
    private static int countCatch(final ThemisXAnalysisStatsStmtCounter pCounter,
                                  final ThemisXAnalysisNodeCatch pCatch) {
        return pCounter.fixedCount(1)
                + pCounter.countStmt(pCatch.getBody());
    }


    /**
     * Obtain the statement count for a COMPILATIONUNIT node.
     *
     * @param pCounter the counter
     * @param pUnit the node
     * @return the count
     */
    private static int countCompilationUnit(final ThemisXAnalysisStatsStmtCounter pCounter,
                                            final ThemisXAnalysisNodeCompilationUnit pUnit) {
        return pCounter.countNode(pUnit.getPackage())
                + pCounter.countNodeList(pUnit.getImports());
        // TODO Add classes
    }

    /**
     * Obtain the statement count for a VARIABLE node.
     *
     * @param pCounter the counter
     * @param pVariable the node
     * @return the count
     */
    private static int countVariable(final ThemisXAnalysisStatsStmtCounter pCounter,
                                     final ThemisXAnalysisNodeVariable pVariable) {
        return pCounter.countExpr(pVariable.getInitializer());
    }
}
