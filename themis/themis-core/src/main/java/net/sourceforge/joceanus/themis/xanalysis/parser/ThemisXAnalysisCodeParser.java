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
package net.sourceforge.joceanus.themis.xanalysis.parser;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyAnnotation;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyAnnotationMember;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyClass;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyCompact;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyConstructor;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyEnum;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyEnumValue;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyField;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyInitializer;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyInterface;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyMethod;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyParameter;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyRecord;
import net.sourceforge.joceanus.themis.xanalysis.body.ThemisXAnalysisBodyVariable;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprAnnotation;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprArrayAccess;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprArrayCreation;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprArrayInit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprAssign;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprBinary;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprBooleanLit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprCast;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprCharLit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprClass;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprConditional;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprDoubleLit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprEnclosed;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprFieldAccess;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprInstanceOf;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprIntegerLit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprLambda;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprLongLit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprMarkerAnnotation;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprMethodCall;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprMethodRef;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprName;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprNormalAnnotation;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprNullLit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprObjectCreate;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprPattern;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprRecordPattern;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprSingleMemberAnnotation;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprStringLit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprSuper;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprSwitch;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprTextBlockLit;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprThis;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprTypePattern;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprUnary;
import net.sourceforge.joceanus.themis.xanalysis.expr.ThemisXAnalysisExprVarDecl;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtAssert;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtBlock;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtBreak;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtClass;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtConstructor;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtContinue;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtDo;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtEmpty;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtExpression;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtFor;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtForEach;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtIf;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtLabeled;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtRecord;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtReturn;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtSwitch;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtSynch;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtThrow;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtTry;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtWhile;
import net.sourceforge.joceanus.themis.xanalysis.stmt.ThemisXAnalysisStmtYield;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeArray;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeClassInterface;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeIntersection;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeParameter;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypePrimitive;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeReference;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeUnion;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeUnknown;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeVar;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeVoid;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeWildcard;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisBodyType;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisExprType;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisStmtType;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisTypeType;

public class ThemisXAnalysisCodeParser
        implements ThemisXAnalysisParser {
    @Override
    public ThemisXAnalysisParsedStatement parseStatement(final Statement pStatement) throws OceanusException {
        switch (ThemisXAnalysisStmtType.determineStatementType(pStatement)) {
            case ASSERT:       return new ThemisXAnalysisStmtAssert(this, pStatement.asAssertStmt());
            case BLOCK:        return new ThemisXAnalysisStmtBlock(this, pStatement.asBlockStmt());
            case BREAK:        return new ThemisXAnalysisStmtBreak(this, pStatement.asBreakStmt());
            case CONSTRUCTOR:  return new ThemisXAnalysisStmtConstructor(this, pStatement.asExplicitConstructorInvocationStmt());
            case CONTINUE:     return new ThemisXAnalysisStmtContinue(this, pStatement.asContinueStmt());
            case DO:           return new ThemisXAnalysisStmtDo(this, pStatement.asDoStmt());
            case EMPTY:        return new ThemisXAnalysisStmtEmpty(this, pStatement.asEmptyStmt());
            case EXPRESSION:   return new ThemisXAnalysisStmtExpression(this, pStatement.asExpressionStmt());
            case FOR:          return new ThemisXAnalysisStmtFor(this, pStatement.asForStmt());
            case FOREACH:      return new ThemisXAnalysisStmtForEach(this, pStatement.asForEachStmt());
            case IF:           return new ThemisXAnalysisStmtIf(this, pStatement.asIfStmt());
            case LABELED:      return new ThemisXAnalysisStmtLabeled(this, pStatement.asLabeledStmt());
            case LOCALCLASS:   return new ThemisXAnalysisStmtClass(this, pStatement.asLocalClassDeclarationStmt());
            case LOCALRECORD:  return new ThemisXAnalysisStmtRecord(this, pStatement.asLocalRecordDeclarationStmt());
            case RETURN:       return new ThemisXAnalysisStmtReturn(this, pStatement.asReturnStmt());
            case SWITCH:       return new ThemisXAnalysisStmtSwitch(this, pStatement.asSwitchStmt());
            case SYNCHRONIZED: return new ThemisXAnalysisStmtSynch(this, pStatement.asSynchronizedStmt());
            case THROW:        return new ThemisXAnalysisStmtThrow(this, pStatement.asThrowStmt());
            case TRY:          return new ThemisXAnalysisStmtTry(this, pStatement.asTryStmt());
            case WHILE:        return new ThemisXAnalysisStmtWhile(this, pStatement.asWhileStmt());
            case YIELD:        return new ThemisXAnalysisStmtYield(this, pStatement.asYieldStmt());
            default:           throw new ThemisDataException("Unsupported Statement Type");
        }
    }

    @Override
    public ThemisXAnalysisParsedBody parseBody(final BodyDeclaration<?> pBody) throws OceanusException {
        switch (ThemisXAnalysisBodyType.determineBodyType(pBody)) {
            case ANNOTATION:       return new ThemisXAnalysisBodyAnnotation(this, pBody.asAnnotationDeclaration());
            case ANNOTATIONMEMBER: return new ThemisXAnalysisBodyAnnotationMember(this, pBody.asAnnotationMemberDeclaration());
            case CLASS:            return new ThemisXAnalysisBodyClass(this, pBody.asClassOrInterfaceDeclaration());
            case COMPACT:          return new ThemisXAnalysisBodyCompact(this, pBody.asCompactConstructorDeclaration());
            case CONSTRUCTOR:      return new ThemisXAnalysisBodyConstructor(this, pBody.asConstructorDeclaration());
            case ENUM:             return new ThemisXAnalysisBodyEnum(this, pBody.asEnumDeclaration());
            case ENUMVALUE:        return new ThemisXAnalysisBodyEnumValue(this, pBody.asEnumConstantDeclaration());
            case FIELD:            return new ThemisXAnalysisBodyField(this, pBody.asFieldDeclaration());
            case INITIALIZER:      return new ThemisXAnalysisBodyInitializer(this, pBody.asInitializerDeclaration());
            case INTERFACE:        return new ThemisXAnalysisBodyInterface(this, pBody.asClassOrInterfaceDeclaration());
            case METHOD:           return new ThemisXAnalysisBodyMethod(this, pBody.asMethodDeclaration());
            case RECORD:           return new ThemisXAnalysisBodyRecord(this, pBody.asRecordDeclaration());
            default:               throw new ThemisDataException("Unsupported Body Type");
        }
    }

    @Override
    public ThemisXAnalysisParsedParam parseParameter(Parameter pParameter) throws OceanusException {
        return new ThemisXAnalysisBodyParameter(this, pParameter);
    }

    @Override
    public ThemisXAnalysisParsedVar parseVariable(VariableDeclarator pVariable) throws OceanusException {
        return new ThemisXAnalysisBodyVariable(this, pVariable);
    }

    @Override
    public ThemisXAnalysisParsedType parseType(Type pType) throws OceanusException {
        switch (ThemisXAnalysisTypeType.determineTypeType(pType)) {
            case ARRAY:          return new ThemisXAnalysisTypeArray(this, pType.asArrayType());
            case CLASSINTERFACE: return new ThemisXAnalysisTypeClassInterface(this, pType.asClassOrInterfaceType());
            case INTERSECTION:   return new ThemisXAnalysisTypeIntersection(this, pType.asIntersectionType());
            case PARAMETER:      return new ThemisXAnalysisTypeParameter(this, pType.asTypeParameter());
            case PRIMITIVE:      return new ThemisXAnalysisTypePrimitive(this, pType.asPrimitiveType());
            case REFERENCE:      return new ThemisXAnalysisTypeReference(this, pType.asReferenceType());
            case UNION:          return new ThemisXAnalysisTypeUnion(this, pType.asUnionType());
            case UNKNOWN:        return new ThemisXAnalysisTypeUnknown(this, pType.asUnknownType());
            case VAR:            return new ThemisXAnalysisTypeVar(this, pType.asVarType());
            case VOID:           return new ThemisXAnalysisTypeVoid(this, pType.asVoidType());
            case WILDCARD:       return new ThemisXAnalysisTypeWildcard(this, pType.asWildcardType());
            default:             throw new ThemisDataException("Unsupported Type Type");
        }
    }

    @Override
    public ThemisXAnalysisParsedExpr parseExpression(Expression pExpr) throws OceanusException {
        switch (ThemisXAnalysisExprType.determineExprType(pExpr)) {
            case ANNOTATION:      return new ThemisXAnalysisExprAnnotation(this, pExpr.asAnnotationExpr());
            case ARRAYACCESS:     return new ThemisXAnalysisExprArrayAccess(this, pExpr.asArrayAccessExpr());
            case ARRAYCREATION:   return new ThemisXAnalysisExprArrayCreation(this, pExpr.asArrayCreationExpr());
            case ARRAYINIT:       return new ThemisXAnalysisExprArrayInit(this, pExpr.asArrayInitializerExpr());
            case ASSIGN:          return new ThemisXAnalysisExprAssign(this, pExpr.asAssignExpr());
            case BINARY:          return new ThemisXAnalysisExprBinary(this, pExpr.asBinaryExpr());
            case BOOLEAN:         return new ThemisXAnalysisExprBooleanLit(this, pExpr.asBooleanLiteralExpr());
            case CAST:            return new ThemisXAnalysisExprCast(this, pExpr.asCastExpr());
            case CHAR:            return new ThemisXAnalysisExprCharLit(this, pExpr.asCharLiteralExpr());
            case CLASS:           return new ThemisXAnalysisExprClass(this, pExpr.asClassExpr());
            case CONDITIONAL:     return new ThemisXAnalysisExprConditional(this, pExpr.asConditionalExpr());
            case DOUBLE:          return new ThemisXAnalysisExprDoubleLit(this, pExpr.asDoubleLiteralExpr());
            case ENCLOSED:        return new ThemisXAnalysisExprEnclosed(this, pExpr.asEnclosedExpr());
            case FIELDACCESS:     return new ThemisXAnalysisExprFieldAccess(this, pExpr.asFieldAccessExpr());
            case INSTANCEOF:      return new ThemisXAnalysisExprInstanceOf(this, pExpr.asInstanceOfExpr());
            case INTEGER:         return new ThemisXAnalysisExprIntegerLit(this, pExpr.asIntegerLiteralExpr());
            case LAMBDA:          return new ThemisXAnalysisExprLambda(this, pExpr.asLambdaExpr());
            case LONG:            return new ThemisXAnalysisExprLongLit(this, pExpr.asLongLiteralExpr());
            case MARKER:          return new ThemisXAnalysisExprMarkerAnnotation(this, pExpr.asMarkerAnnotationExpr());
            case METHODCALL:      return new ThemisXAnalysisExprMethodCall(this, pExpr.asMethodCallExpr());
            case METHODREFERENCE: return new ThemisXAnalysisExprMethodRef(this, pExpr.asMethodReferenceExpr());
            case NAME:            return new ThemisXAnalysisExprName(this, pExpr.asNameExpr());
            case NORMAL:          return new ThemisXAnalysisExprNormalAnnotation(this, pExpr.asNormalAnnotationExpr());
            case NULL:            return new ThemisXAnalysisExprNullLit(this, pExpr.asNullLiteralExpr());
            case OBJECTCREATE:    return new ThemisXAnalysisExprObjectCreate(this, pExpr.asObjectCreationExpr());
            case PATTERN:         return new ThemisXAnalysisExprPattern(this, pExpr.asPatternExpr());
            case RECORDPATTERN:   return new ThemisXAnalysisExprRecordPattern(this, pExpr.asRecordPatternExpr());
            case SINGLEMEMBER:    return new ThemisXAnalysisExprSingleMemberAnnotation(this, pExpr.asSingleMemberAnnotationExpr());
            case STRING:          return new ThemisXAnalysisExprStringLit(this,pExpr.asStringLiteralExpr());
            case SUPER:           return new ThemisXAnalysisExprSuper(this, pExpr.asSuperExpr());
            case SWITCH:          return new ThemisXAnalysisExprSwitch(this, pExpr.asSwitchExpr());
            case TEXTBLOCK:       return new ThemisXAnalysisExprTextBlockLit(this, pExpr.asTextBlockLiteralExpr());
            case THIS:            return new ThemisXAnalysisExprThis(this, pExpr.asThisExpr());
            case TYPEPATTERN:     return new ThemisXAnalysisExprTypePattern(this, pExpr.asTypePatternExpr());
            case UNARY:           return new ThemisXAnalysisExprUnary(this, pExpr.asUnaryExpr());
            case VARIABLE:        return new ThemisXAnalysisExprVarDecl(this, pExpr.asVariableDeclarationExpr());
            default:              throw new ThemisDataException("Unsupported Expression Type");
        }    }
}
