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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.Expression;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisId;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

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
     *
     * @param pTester the test method
     */
    ThemisXAnalysisExpression(final Predicate<Expression> pTester) {
        theTester = pTester;
    }

    /**
     * Determine type of expression.
     *
     * @param pParser the parser
     * @param pExpr   the expression
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
}
