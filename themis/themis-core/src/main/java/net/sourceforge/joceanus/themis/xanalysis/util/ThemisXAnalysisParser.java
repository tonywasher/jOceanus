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
package net.sourceforge.joceanus.themis.xanalysis.util;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser interface.
 */
public interface ThemisXAnalysisParser {
    /**
     * The parsed statement interface.
     */
    interface ThemisXAnalysisParsedStatement {
    }

    /**
     * Parse a statement.
     * @param pStatement the statement
     * @return the parsed statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisParsedStatement parseStatement(Statement pStatement) throws OceanusException;

    /**
     * parse a list of statements.
     * @param pStatementList the list of Statements
     * @return the list of parsed statements
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisParsedStatement> parseStatementList(final NodeList<Statement> pStatementList) throws OceanusException {
        final List<ThemisXAnalysisParsedStatement> myList = new ArrayList<>();
        for (Statement myStatement : pStatementList) {
            final ThemisXAnalysisParsedStatement myParsed = parseStatement(myStatement);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * The parsed body interface.
     */
    interface ThemisXAnalysisParsedBody {
    }

    /**
     * Parse a bodyPart.
     * @param pBody the bodyPart
     * @return the parsed bodyPart
     * @throws OceanusException on error
     */
    ThemisXAnalysisParsedBody parseBody(BodyDeclaration<?> pBody) throws OceanusException;

    /**
     * parse a list of members.
     * @param pMemberList the list of Members
     * @return the list of parsed members
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisParsedBody> parseMemberList(final NodeList<? extends BodyDeclaration<?>> pMemberList) throws OceanusException {
        final List<ThemisXAnalysisParsedBody> myList = new ArrayList<>();
        for (BodyDeclaration<?> myMember : pMemberList) {
            final ThemisXAnalysisParsedBody myParsed = parseBody(myMember);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * The parsed parameter interface.
     */
    interface ThemisXAnalysisParsedParam {
    }

    /**
     * Parse a parameter.
     * @param pParameter the parameter
     * @return the parsed parameter
     * @throws OceanusException on error
     */
    ThemisXAnalysisParsedParam parseParameter(Parameter pParameter) throws OceanusException;

    /**
     * parse a list of parameters.
     * @param pParamList the list of Parameters
     * @return the list of parsed params
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisParsedParam> parseParamList(final NodeList<Parameter> pParamList) throws OceanusException {
        final List<ThemisXAnalysisParsedParam> myList = new ArrayList<>();
        for (Parameter myParam : pParamList) {
            final ThemisXAnalysisParsedParam myParsed = parseParameter(myParam);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * The parsed variable interface.
     */
    interface ThemisXAnalysisParsedVar {
    }

    /**
     * Parse a variable.
     * @param pVariable the variable
     * @return the parsed variable
     * @throws OceanusException on error
     */
    ThemisXAnalysisParsedVar parseVariable(VariableDeclarator pVariable) throws OceanusException;

    /**
     * parse a list of variables.
     * @param pVarList the list of Variables
     * @return the list of parsed variables
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisParsedVar> parseVarList(final NodeList<VariableDeclarator> pVarList) throws OceanusException {
        final List<ThemisXAnalysisParsedVar> myList = new ArrayList<>();
        for (VariableDeclarator myVar : pVarList) {
            final ThemisXAnalysisParsedVar myParsed = parseVariable(myVar);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * The parsed type interface.
     */
    interface ThemisXAnalysisParsedType {
    }

    /**
     * Parse a type.
     * @param pType the type
     * @return the parsed type
     * @throws OceanusException on error
     */
    ThemisXAnalysisParsedType parseType(Type pType) throws OceanusException;

    /**
     * parse a list of types.
     * @param pTypeList the list of Types
     * @return the list of parsed types
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisParsedType> parseTypeList(final NodeList<? extends Type> pTypeList) throws OceanusException {
        final List<ThemisXAnalysisParsedType> myList = new ArrayList<>();
        for (Type myType : pTypeList) {
            final ThemisXAnalysisParsedType myParsed = parseType(myType);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * The parsed expression interface.
     */
    interface ThemisXAnalysisParsedExpr {
    }

    /**
     * Parse an expression.
     * @param pExpression the expression
     * @return the parsed expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisParsedExpr parseExpression(Expression pExpression) throws OceanusException;

    /**
     * parse a list of expressions.
     * @param pExprList the list of Expressions
     * @return the list of parsed expressions
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisParsedExpr> parseExprList(final NodeList<Expression> pExprList) throws OceanusException {
        final List<ThemisXAnalysisParsedExpr> myList = new ArrayList<>();
        for (Expression myExpr : pExprList) {
            final ThemisXAnalysisParsedExpr myParsed = parseExpression(myExpr);
            myList.add(myParsed);
        }
        return myList;
    }
}
