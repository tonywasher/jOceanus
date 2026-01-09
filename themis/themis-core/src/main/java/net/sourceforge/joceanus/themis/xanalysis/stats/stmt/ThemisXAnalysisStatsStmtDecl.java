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

import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclCompact;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclConstructor;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclEnumValue;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclField;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclInitializer;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclMethod;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclaration;

/**
 * Statement Counter for Declarations.
 */
public final class ThemisXAnalysisStatsStmtDecl {
    /**
     * Private constructor.
     */
    private ThemisXAnalysisStatsStmtDecl() {
    }

    /**
     * Obtain the statement count for a declaration.
     *
     * @param pCounter the counter
     * @param pDecl the declaration
     * @return the count
     */
    static int count(final ThemisXAnalysisStatsStmtCounter pCounter,
                     final ThemisXAnalysisDeclarationInstance pDecl) {
        /* Handle null declaration */
        if (pDecl == null) {
            return pCounter.fixedCount(0);
        }

        /* Switch on declaration id */
        switch ((ThemisXAnalysisDeclaration) pDecl.getId()) {
            case ANNOTATION:
            case CLASSINTERFACE:
            case ENUM:
            case RECORD:           return countClass(pCounter, (ThemisXAnalysisClassInstance) pDecl);
            case COMPACT:          return countCompact(pCounter, (ThemisXAnalysisDeclCompact) pDecl);
            case CONSTRUCTOR:      return countConstructor(pCounter, (ThemisXAnalysisDeclConstructor) pDecl);
            case ENUMVALUE:        return countEnumValue(pCounter, (ThemisXAnalysisDeclEnumValue) pDecl);
            case FIELD:            return countField(pCounter, (ThemisXAnalysisDeclField) pDecl);
            case INITIALIZER:      return countInit(pCounter, (ThemisXAnalysisDeclInitializer) pDecl);
            case METHOD:           return countMethod(pCounter, (ThemisXAnalysisDeclMethod) pDecl);
            case ANNOTATIONMEMBER:
            default:               return pCounter.fixedCount(0);
        }
    }

    /**
     * Obtain the statement count for a CLASS declaration.
     *
     * @param pCounter the counter
     * @param pClass the declaration
     * @return the count
     */
    private static int countClass(final ThemisXAnalysisStatsStmtCounter pCounter,
                                  final ThemisXAnalysisClassInstance pClass) {
        /* Ignore nested classes, they will be handled elsewhere */
        int myCount = 0;
        for (ThemisXAnalysisDeclarationInstance myMember : pClass.getBody()) {
            if (!(myMember instanceof ThemisXAnalysisClassInstance)) {
                myCount += pCounter.countDecl(myMember);
            }
        }
        return myCount;
    }

    /**
     * Obtain the statement count for a COMPACT declaration.
     *
     * @param pCounter the counter
     * @param pCompact the declaration
     * @return the count
     */
    private static int countCompact(final ThemisXAnalysisStatsStmtCounter pCounter,
                                    final ThemisXAnalysisDeclCompact pCompact) {
        return pCounter.countStmt(pCompact.getBody());
    }

    /**
     * Obtain the statement count for a CONSTRUCTOR declaration.
     *
     * @param pCounter the counter
     * @param pConstructor the declaration
     * @return the count
     */
    private static int countConstructor(final ThemisXAnalysisStatsStmtCounter pCounter,
                                        final ThemisXAnalysisDeclConstructor pConstructor) {
        return pCounter.countStmt(pConstructor.getBody());
    }

    /**
     * Obtain the statement count for an ENUMVALUE declaration.
     *
     * @param pCounter the counter
     * @param pValue the declaration
     * @return the count
     */
    private static int countEnumValue(final ThemisXAnalysisStatsStmtCounter pCounter,
                                      final ThemisXAnalysisDeclEnumValue pValue) {
        return pCounter.countExprList(pValue.getArguments())
                + pCounter.countDeclList(pValue.getBody());
    }

    /**
     * Obtain the statement count for a FIELD declaration.
     *
     * @param pCounter the counter
     * @param pField the declaration
     * @return the count
     */
    private static int countField(final ThemisXAnalysisStatsStmtCounter pCounter,
                                  final ThemisXAnalysisDeclField pField) {
        return pCounter.countNodeList(pField.getVariables());
    }

    /**
     * Obtain the statement count for an INITIALIZER declaration.
     *
     * @param pCounter the counter
     * @param pInit the declaration
     * @return the count
     */
    private static int countInit(final ThemisXAnalysisStatsStmtCounter pCounter,
                                 final ThemisXAnalysisDeclInitializer pInit) {
        return pCounter.countStmt(pInit.getBody());
    }

    /**
     * Obtain the statement count for a METHOD declaration.
     *
     * @param pCounter the counter
     * @param pMethod the declaration
     * @return the count
     */
    private static int countMethod(final ThemisXAnalysisStatsStmtCounter pCounter,
                                   final ThemisXAnalysisDeclMethod pMethod) {
        return pCounter.countStmt(pMethod.getBody());
    }
}
