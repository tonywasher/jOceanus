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
package net.sourceforge.joceanus.themis.xanalysis.expr;

import com.github.javaparser.ast.expr.Expression;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisId;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;

import java.util.function.Predicate;

/**
 * Analysis ExpressionType.
 */
public enum ThemisXAnalysisExpression
        implements ThemisXAnalysisId {
    /**
     * ArrayAccess.
     */
    ARRAYACCESS(Expression::isArrayAccessExpr),

    /**
     * ArrayCreation.
     */
    ARRAYCREATION(Expression::isArrayCreationExpr),

    /**
     * ArrayInitializer.
     */
    ARRAYINIT(Expression::isArrayInitializerExpr),

    /**
     * Assign.
     */
    ASSIGN(Expression::isAssignExpr),

    /**
     * Binary.
     */
    BINARY(Expression::isBinaryExpr),

    /**
     * Boolean.
     */
    BOOLEAN(Expression::isBooleanLiteralExpr),

    /**
     * Cast.
     */
    CAST(Expression::isCastExpr),

    /**
     * CharLiteral.
     */
    CHAR(Expression::isCharLiteralExpr),

    /**
     * Class.
     */
    CLASS(Expression::isClassExpr),

    /**
     * Conditional.
     */
    CONDITIONAL(Expression::isConditionalExpr),

    /**
     * DoubleLiteral.
     */
    DOUBLE(Expression::isDoubleLiteralExpr),

    /**
     * Enclosed.
     */
    ENCLOSED(Expression::isEnclosedExpr),

    /**
     * FieldAccess.
     */
    FIELDACCESS(Expression::isFieldAccessExpr),

    /**
     * Void.
     */
    INSTANCEOF(Expression::isInstanceOfExpr),

    /**
     * IntegerLiteral.
     */
    INTEGER(Expression::isIntegerLiteralExpr),

    /**
     * Lambda.
     */
    LAMBDA(Expression::isLambdaExpr),

    /**
     * Long.
     */
    LONG(Expression::isLongLiteralExpr),

    /**
     * MarkerAnnotation.
     */
    MARKER(Expression::isMarkerAnnotationExpr),

    /**
     * MethodCall.
     */
    METHODCALL(Expression::isMethodCallExpr),

    /**
     * MethodReference.
     */
    METHODREFERENCE(Expression::isMethodReferenceExpr),

    /**
     * Name.
     */
    NAME(Expression::isNameExpr),

    /**
     * NormalAnnotation.
     */
    NORMAL(Expression::isNormalAnnotationExpr),

    /**
     * Null.
     */
    NULL(Expression::isNullLiteralExpr),

    /**
     * ObjectCreation.
     */
    OBJECTCREATE(Expression::isObjectCreationExpr),

    /**
     * RecordPattern.
     */
    RECORDPATTERN(Expression::isRecordPatternExpr),

    /**
     * SingleMemberAnnotation.
     */
    SINGLEMEMBER(Expression::isSingleMemberAnnotationExpr),

    /**
     * StringLiteral.
     */
    STRING(Expression::isStringLiteralExpr),

    /**
     * Super.
     */
    SUPER(Expression::isSuperExpr),

    /**
     * Switch.
     */
    SWITCH(Expression::isSwitchExpr),

    /**
     * TextBlock.
     */
    TEXTBLOCK(Expression::isTextBlockLiteralExpr),

    /**
     * This.
     */
    THIS(Expression::isThisExpr),

    /**
     * Type.
     */
    TYPE(Expression::isTypeExpr),

    /**
     * TypePattern.
     */
    TYPEPATTERN(Expression::isTypePatternExpr),

    /**
     * Unary.
     */
    UNARY(Expression::isUnaryExpr),

    /**
     * Variable.
     */
    VARIABLE(Expression::isVariableDeclarationExpr);

    /**
     * The test.
     */
    private final Predicate<Expression> theTester;

    /**
     * Constructor.
     * @param pTester the test method
     */
    ThemisXAnalysisExpression(final Predicate<Expression> pTester) {
        theTester = pTester;
    }

    /**
     * Determine type of expression.
     * @param pParser the parser
     * @param pExpr the expression
     * @return the exprType
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisExpression determineExpression(final ThemisXAnalysisParserDef pParser,
                                                                final Expression pExpr) throws OceanusException {
        /* Loop testing each expr type */
        for (ThemisXAnalysisExpression myExpr : values()) {
            if (myExpr.theTester.test(pExpr)) {
                return myExpr;
            }
        }

        /* Unrecognised exprType */
        throw pParser.buildException("Unexpected Expression", pExpr);
    }

    /**
     * Parse an expression.
     * @param pParser the parser
     * @param pExpr the expression
     * @return the parsed expression
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisExpressionInstance parseExpression(final ThemisXAnalysisParserDef pParser,
                                                                    final Expression pExpr) throws OceanusException {
        /* Handle null Expression */
        if (pExpr == null) {
            return null;
        }

        /* Allocate correct Expression */
        switch (ThemisXAnalysisExpression.determineExpression(pParser, pExpr)) {
            case ARRAYACCESS:     return new ThemisXAnalysisExprArrayAccess(pParser, pExpr.asArrayAccessExpr());
            case ARRAYCREATION:   return new ThemisXAnalysisExprArrayCreation(pParser, pExpr.asArrayCreationExpr());
            case ARRAYINIT:       return new ThemisXAnalysisExprArrayInit(pParser, pExpr.asArrayInitializerExpr());
            case ASSIGN:          return new ThemisXAnalysisExprAssign(pParser, pExpr.asAssignExpr());
            case BINARY:          return new ThemisXAnalysisExprBinary(pParser, pExpr.asBinaryExpr());
            case BOOLEAN:         return new ThemisXAnalysisExprBooleanLit(pParser, pExpr.asBooleanLiteralExpr());
            case CAST:            return new ThemisXAnalysisExprCast(pParser, pExpr.asCastExpr());
            case CHAR:            return new ThemisXAnalysisExprCharLit(pParser, pExpr.asCharLiteralExpr());
            case CLASS:           return new ThemisXAnalysisExprClass(pParser, pExpr.asClassExpr());
            case CONDITIONAL:     return new ThemisXAnalysisExprConditional(pParser, pExpr.asConditionalExpr());
            case DOUBLE:          return new ThemisXAnalysisExprDoubleLit(pParser, pExpr.asDoubleLiteralExpr());
            case ENCLOSED:        return new ThemisXAnalysisExprEnclosed(pParser, pExpr.asEnclosedExpr());
            case FIELDACCESS:     return new ThemisXAnalysisExprFieldAccess(pParser, pExpr.asFieldAccessExpr());
            case INSTANCEOF:      return new ThemisXAnalysisExprInstanceOf(pParser, pExpr.asInstanceOfExpr());
            case INTEGER:         return new ThemisXAnalysisExprIntegerLit(pParser, pExpr.asIntegerLiteralExpr());
            case LAMBDA:          return new ThemisXAnalysisExprLambda(pParser, pExpr.asLambdaExpr());
            case LONG:            return new ThemisXAnalysisExprLongLit(pParser, pExpr.asLongLiteralExpr());
            case MARKER:          return new ThemisXAnalysisExprMarkerAnnotation(pParser, pExpr.asMarkerAnnotationExpr());
            case METHODCALL:      return new ThemisXAnalysisExprMethodCall(pParser, pExpr.asMethodCallExpr());
            case METHODREFERENCE: return new ThemisXAnalysisExprMethodRef(pParser, pExpr.asMethodReferenceExpr());
            case NAME:            return new ThemisXAnalysisExprName(pParser, pExpr.asNameExpr());
            case NORMAL:          return new ThemisXAnalysisExprNormalAnnotation(pParser, pExpr.asNormalAnnotationExpr());
            case NULL:            return new ThemisXAnalysisExprNullLit(pParser, pExpr.asNullLiteralExpr());
            case OBJECTCREATE:    return new ThemisXAnalysisExprObjectCreate(pParser, pExpr.asObjectCreationExpr());
            case RECORDPATTERN:   return new ThemisXAnalysisExprRecordPattern(pParser, pExpr.asRecordPatternExpr());
            case SINGLEMEMBER:    return new ThemisXAnalysisExprSingleMemberAnnotation(pParser, pExpr.asSingleMemberAnnotationExpr());
            case STRING:          return new ThemisXAnalysisExprStringLit(pParser, pExpr.asStringLiteralExpr());
            case SUPER:           return new ThemisXAnalysisExprSuper(pParser, pExpr.asSuperExpr());
            case SWITCH:          return new ThemisXAnalysisExprSwitch(pParser, pExpr.asSwitchExpr());
            case TEXTBLOCK:       return new ThemisXAnalysisExprTextBlockLit(pParser, pExpr.asTextBlockLiteralExpr());
            case THIS:            return new ThemisXAnalysisExprThis(pParser, pExpr.asThisExpr());
            case TYPE:            return new ThemisXAnalysisExprType(pParser, pExpr.asTypeExpr());
            case TYPEPATTERN:     return new ThemisXAnalysisExprTypePattern(pParser, pExpr.asTypePatternExpr());
            case UNARY:           return new ThemisXAnalysisExprUnary(pParser, pExpr.asUnaryExpr());
            case VARIABLE:        return new ThemisXAnalysisExprVarDecl(pParser, pExpr.asVariableDeclarationExpr());
            default:              throw pParser.buildException("Unsupported Expression Type", pExpr);
        }
    }
}
