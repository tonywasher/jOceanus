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
        switch (ThemisExpression.determineExpression(pParser, pExpr)) {
            case ARRAYACCESS:
                return new ThemisExprArrayAccess(pParser, pExpr.asArrayAccessExpr());
            case ARRAYCREATION:
                return new ThemisExprArrayCreation(pParser, pExpr.asArrayCreationExpr());
            case ARRAYINIT:
                return new ThemisExprArrayInit(pParser, pExpr.asArrayInitializerExpr());
            case ASSIGN:
                return new ThemisExprAssign(pParser, pExpr.asAssignExpr());
            case BINARY:
                return new ThemisExprBinary(pParser, pExpr.asBinaryExpr());
            case BOOLEAN:
                return new ThemisExprBooleanLit(pParser, pExpr.asBooleanLiteralExpr());
            case CAST:
                return new ThemisExprCast(pParser, pExpr.asCastExpr());
            case CHAR:
                return new ThemisExprCharLit(pParser, pExpr.asCharLiteralExpr());
            case CLASS:
                return new ThemisExprClass(pParser, pExpr.asClassExpr());
            case CONDITIONAL:
                return new ThemisExprConditional(pParser, pExpr.asConditionalExpr());
            case DOUBLE:
                return new ThemisExprDoubleLit(pParser, pExpr.asDoubleLiteralExpr());
            case ENCLOSED:
                return new ThemisExprEnclosed(pParser, pExpr.asEnclosedExpr());
            case FIELDACCESS:
                return new ThemisExprFieldAccess(pParser, pExpr.asFieldAccessExpr());
            case INSTANCEOF:
                return new ThemisExprInstanceOf(pParser, pExpr.asInstanceOfExpr());
            case INTEGER:
                return new ThemisExprIntegerLit(pParser, pExpr.asIntegerLiteralExpr());
            case LAMBDA:
                return new ThemisExprLambda(pParser, pExpr.asLambdaExpr());
            case LONG:
                return new ThemisExprLongLit(pParser, pExpr.asLongLiteralExpr());
            case MARKER:
                return new ThemisExprMarkerAnnotation(pParser, pExpr.asMarkerAnnotationExpr());
            case METHODCALL:
                return new ThemisExprMethodCall(pParser, pExpr.asMethodCallExpr());
            case METHODREFERENCE:
                return new ThemisExprMethodRef(pParser, pExpr.asMethodReferenceExpr());
            case NAME:
                return new ThemisExprName(pParser, pExpr.asNameExpr());
            case NORMAL:
                return new ThemisExprNormalAnnotation(pParser, pExpr.asNormalAnnotationExpr());
            case NULL:
                return new ThemisExprNullLit(pParser, pExpr.asNullLiteralExpr());
            case OBJECTCREATE:
                return new ThemisExprObjectCreate(pParser, pExpr.asObjectCreationExpr());
            case RECORDPATTERN:
                return new ThemisExprRecordPattern(pParser, pExpr.asRecordPatternExpr());
            case SINGLEMEMBER:
                return new ThemisExprSingleMemberAnnotation(pParser, pExpr.asSingleMemberAnnotationExpr());
            case STRING:
                return new ThemisExprStringLit(pParser, pExpr.asStringLiteralExpr());
            case SUPER:
                return new ThemisExprSuper(pParser, pExpr.asSuperExpr());
            case SWITCH:
                return new ThemisExprSwitch(pParser, pExpr.asSwitchExpr());
            case TEXTBLOCK:
                return new ThemisExprTextBlockLit(pParser, pExpr.asTextBlockLiteralExpr());
            case THIS:
                return new ThemisExprThis(pParser, pExpr.asThisExpr());
            case TYPE:
                return new ThemisExprType(pParser, pExpr.asTypeExpr());
            case TYPEPATTERN:
                return new ThemisExprTypePattern(pParser, pExpr.asTypePatternExpr());
            case UNARY:
                return new ThemisExprUnary(pParser, pExpr.asUnaryExpr());
            case VARIABLE:
                return new ThemisExprVarDecl(pParser, pExpr.asVariableDeclarationExpr());
            default:
                throw pParser.buildException("Unsupported Expression Type", pExpr);
        }
    }
}
