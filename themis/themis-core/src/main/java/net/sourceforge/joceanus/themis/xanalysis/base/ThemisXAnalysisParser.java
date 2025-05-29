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
package net.sourceforge.joceanus.themis.xanalysis.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parser interface.
 */
public interface ThemisXAnalysisParser {
    /**
     * Build exception.
     * @param pMessage the message
     * @param pNode the failing node
     * @return the built exception
     */
    OceanusException buildException(String pMessage,
                                    Node pNode);

    /**
     * Check the package name.
     * @param pPackage the package name
     * @throws OceanusException on error
     */
    void checkPackage(PackageDeclaration pPackage) throws OceanusException;

    /**
     * Register Instance.
     * @param pInstance the instance
     * @throws OceanusException on error
     */
    void registerInstance(ThemisXAnalysisInstance pInstance) throws OceanusException;

    /**
     * Parse a declaration.
     * @param pDecl the declaration
     * @return the parsed declaration
     * @throws OceanusException on error
     */
    ThemisXAnalysisDeclarationInstance parseDeclaration(BodyDeclaration<?> pDecl) throws OceanusException;

    /**
     * parse a list of declarations.
     * @param pDeclList the list of Declarations
     * @return the list of parsed declarations
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisDeclarationInstance> parseDeclarationList(final NodeList<? extends BodyDeclaration<?>> pDeclList) throws OceanusException {
        /* Handle null list */
        if (pDeclList == null) {
            return Collections.emptyList();
        }

        /* Create list of declarations */
        final List<ThemisXAnalysisDeclarationInstance> myList = new ArrayList<>();
        for (BodyDeclaration<?> myDecl : pDeclList) {
            final ThemisXAnalysisDeclarationInstance myParsed = parseDeclaration(myDecl);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * Parse a node.
     * @param pNode the node
     * @return the parsed node
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodeInstance parseNode(Node pNode) throws OceanusException;

    /**
     * parse a list of nodes.
     * @param pNodeList the list of Nodes
     * @return the list of parsed nodes
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisNodeInstance> parseNodeList(final NodeList<? extends Node> pNodeList) throws OceanusException {
        /* Handle null list */
        if (pNodeList == null) {
            return Collections.emptyList();
        }

        /* Create list of nodes */
        final List<ThemisXAnalysisNodeInstance> myList = new ArrayList<>();
        for (Node myNode : pNodeList) {
            final ThemisXAnalysisNodeInstance myParsed = parseNode(myNode);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * Parse a type.
     * @param pType the type
     * @return the parsed type
     * @throws OceanusException on error
     */
    ThemisXAnalysisTypeInstance parseType(Type pType) throws OceanusException;

    /**
     * parse a list of types.
     * @param pTypeList the list of Types
     * @return the list of parsed types
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisTypeInstance> parseTypeList(final NodeList<? extends Type> pTypeList) throws OceanusException {
        /* Handle null list */
        if (pTypeList == null) {
            return Collections.emptyList();
        }

        /* Create list of nodes */
        final List<ThemisXAnalysisTypeInstance> myList = new ArrayList<>();
        for (Type myType : pTypeList) {
            final ThemisXAnalysisTypeInstance myParsed = parseType(myType);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * Parse a statement.
     * @param pStatement the statement
     * @return the parsed statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisStatementInstance parseStatement(Statement pStatement) throws OceanusException;

    /**
     * parse a list of statements.
     * @param pStatementList the list of Statements
     * @return the list of parsed statements
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisStatementInstance> parseStatementList(final NodeList<? extends Statement> pStatementList) throws OceanusException {
        /* Handle null list */
        if (pStatementList == null) {
            return Collections.emptyList();
        }

        /* Create list of statements */
        final List<ThemisXAnalysisStatementInstance> myList = new ArrayList<>();
        for (Statement myStatement : pStatementList) {
            final ThemisXAnalysisStatementInstance myParsed = parseStatement(myStatement);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * Parse an expression.
     * @param pExpression the expression
     * @return the parsed expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExpressionInstance parseExpression(Expression pExpression) throws OceanusException;

    /**
     * parse a list of expressions.
     * @param pExprList the list of Expressions
     * @return the list of parsed expressions
     * @throws OceanusException on error
     */
    default List<ThemisXAnalysisExpressionInstance> parseExprList(final NodeList<? extends Expression> pExprList) throws OceanusException {
        /* Handle null list */
        if (pExprList == null) {
            return Collections.emptyList();
        }

        /* Create list of expressions */
        final List<ThemisXAnalysisExpressionInstance> myList = new ArrayList<>();
        for (Expression myExpr : pExprList) {
            final ThemisXAnalysisExpressionInstance myParsed = parseExpression(myExpr);
            myList.add(myParsed);
        }
        return myList;
    }
}
