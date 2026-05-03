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
package io.github.tonywasher.joceanus.themis.parser.expr;

import com.github.javaparser.ast.expr.Expression;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisExpressionInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Analysis Expression Parser.
 */
public final class ThemisExprParser {
    /**
     * Private Constructor.
     */
    private ThemisExprParser() {
    }

    /**
     * Parse an expression.
     *
     * @param pParser the parser
     * @param pExpr   the expression
     * @return the parsed expression
     * @throws OceanusException on error
     */
    public static ThemisExpressionInstance parseExpression(final ThemisParserDef pParser,
                                                           final Expression pExpr) throws OceanusException {
        /* Handle null Expression */
        if (pExpr == null) {
            return null;
        }

        /* Allocate correct Expression */
        return switch (ThemisExpression.determineExpression(pParser, pExpr)) {
            case ARRAYACCESS -> new ThemisExprArrayAccess(pParser, pExpr.asArrayAccessExpr());
            case ARRAYCREATION -> new ThemisExprArrayCreation(pParser, pExpr.asArrayCreationExpr());
            case ARRAYINIT -> new ThemisExprArrayInit(pParser, pExpr.asArrayInitializerExpr());
            case ASSIGN -> new ThemisExprAssign(pParser, pExpr.asAssignExpr());
            case BINARY -> new ThemisExprBinary(pParser, pExpr.asBinaryExpr());
            case BOOLEAN -> new ThemisExprBooleanLit(pParser, pExpr.asBooleanLiteralExpr());
            case CAST -> new ThemisExprCast(pParser, pExpr.asCastExpr());
            case CHAR -> new ThemisExprCharLit(pParser, pExpr.asCharLiteralExpr());
            case CLASS -> new ThemisExprClass(pParser, pExpr.asClassExpr());
            case CONDITIONAL -> new ThemisExprConditional(pParser, pExpr.asConditionalExpr());
            case DOUBLE -> new ThemisExprDoubleLit(pParser, pExpr.asDoubleLiteralExpr());
            case ENCLOSED -> new ThemisExprEnclosed(pParser, pExpr.asEnclosedExpr());
            case FIELDACCESS -> new ThemisExprFieldAccess(pParser, pExpr.asFieldAccessExpr());
            case INSTANCEOF -> new ThemisExprInstanceOf(pParser, pExpr.asInstanceOfExpr());
            case INTEGER -> new ThemisExprIntegerLit(pParser, pExpr.asIntegerLiteralExpr());
            case LAMBDA -> new ThemisExprLambda(pParser, pExpr.asLambdaExpr());
            case LONG -> new ThemisExprLongLit(pParser, pExpr.asLongLiteralExpr());
            case MARKER -> new ThemisExprMarkerAnnotation(pParser, pExpr.asMarkerAnnotationExpr());
            case METHODCALL -> new ThemisExprMethodCall(pParser, pExpr.asMethodCallExpr());
            case METHODREFERENCE -> new ThemisExprMethodRef(pParser, pExpr.asMethodReferenceExpr());
            case NAME -> new ThemisExprName(pParser, pExpr.asNameExpr());
            case NORMAL -> new ThemisExprNormalAnnotation(pParser, pExpr.asNormalAnnotationExpr());
            case NULL -> new ThemisExprNullLit(pParser, pExpr.asNullLiteralExpr());
            case OBJECTCREATE -> new ThemisExprObjectCreate(pParser, pExpr.asObjectCreationExpr());
            case RECORDPATTERN -> new ThemisExprRecordPattern(pParser, pExpr.asRecordPatternExpr());
            case SINGLEMEMBER -> new ThemisExprSingleMemberAnnotation(pParser, pExpr.asSingleMemberAnnotationExpr());
            case STRING -> new ThemisExprStringLit(pParser, pExpr.asStringLiteralExpr());
            case SUPER -> new ThemisExprSuper(pParser, pExpr.asSuperExpr());
            case SWITCH -> new ThemisExprSwitch(pParser, pExpr.asSwitchExpr());
            case TEXTBLOCK -> new ThemisExprTextBlockLit(pParser, pExpr.asTextBlockLiteralExpr());
            case THIS -> new ThemisExprThis(pParser, pExpr.asThisExpr());
            case TYPE -> new ThemisExprType(pParser, pExpr.asTypeExpr());
            case TYPEPATTERN -> new ThemisExprTypePattern(pParser, pExpr.asTypePatternExpr());
            case UNARY -> new ThemisExprUnary(pParser, pExpr.asUnaryExpr());
            case VARIABLE -> new ThemisExprVarDecl(pParser, pExpr.asVariableDeclarationExpr());
            default -> throw pParser.buildException("Unsupported Expression Type", pExpr);
        };
    }
}
