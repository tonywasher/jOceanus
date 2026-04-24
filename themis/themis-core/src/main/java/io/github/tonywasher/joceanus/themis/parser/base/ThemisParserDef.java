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
package io.github.tonywasher.joceanus.themis.parser.base;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.modules.ModuleDirective;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisExpressionInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisModuleInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisStatementInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parser interface.
 */
public interface ThemisParserDef {
    /**
     * Process the file as javaCode.
     *
     * @return the parsed compilation unit
     * @throws OceanusException on error
     */
    ThemisNodeInstance parseJavaFile() throws OceanusException;

    /**
     * Process the file as a module-info instance.
     *
     * @param pInfoFile the module-info file
     * @return the parsed moduleInfo
     * @throws OceanusException on error
     */
    ThemisModuleInstance parseModuleInfo(File pInfoFile) throws OceanusException;

    /**
     * Set the current package.
     *
     * @param pPackage the package
     */
    void setCurrentPackage(String pPackage);

    /**
     * Set the current file.
     *
     * @param pFile the file
     */
    void setCurrentFile(File pFile);

    /**
     * Obtain the classList.
     *
     * @return the classList
     */
    List<ThemisClassInstance> getClasses();

    /**
     * Build exception.
     *
     * @param pMessage the message
     * @param pNode    the failing node
     * @return the built exception
     */
    OceanusException buildException(String pMessage,
                                    Node pNode);

    /**
     * Check the package name.
     *
     * @param pPackage the package name
     * @throws OceanusException on error
     */
    void checkPackage(PackageDeclaration pPackage) throws OceanusException;

    /**
     * Register Instance.
     *
     * @param pInstance the instance
     * @return the parent node
     * @throws OceanusException on error
     */
    ThemisInstance registerInstance(ThemisInstance pInstance) throws OceanusException;

    /**
     * Register Class.
     *
     * @param pClass the class
     * @return the class name
     */
    String registerClass(ThemisClassInstance pClass) throws OceanusException;

    /**
     * Parse a declaration.
     *
     * @param pDecl the declaration
     * @return the parsed declaration
     * @throws OceanusException on error
     */
    ThemisDeclarationInstance parseDeclaration(BodyDeclaration<?> pDecl) throws OceanusException;

    /**
     * parse a list of declarations.
     *
     * @param pDeclList the list of Declarations
     * @return the list of parsed declarations
     * @throws OceanusException on error
     */
    default List<ThemisDeclarationInstance> parseDeclarationList(final NodeList<? extends BodyDeclaration<?>> pDeclList) throws OceanusException {
        /* Handle null list */
        if (pDeclList == null) {
            return Collections.emptyList();
        }

        /* Create list of declarations */
        final List<ThemisDeclarationInstance> myList = new ArrayList<>();
        for (BodyDeclaration<?> myDecl : pDeclList) {
            final ThemisDeclarationInstance myParsed = parseDeclaration(myDecl);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * Parse a node.
     *
     * @param pNode the node
     * @return the parsed node
     * @throws OceanusException on error
     */
    ThemisNodeInstance parseNode(Node pNode) throws OceanusException;

    /**
     * parse a list of nodes.
     *
     * @param pNodeList the list of Nodes
     * @return the list of parsed nodes
     * @throws OceanusException on error
     */
    default List<ThemisNodeInstance> parseNodeList(final NodeList<? extends Node> pNodeList) throws OceanusException {
        /* Handle null list */
        if (pNodeList == null) {
            return Collections.emptyList();
        }

        /* Create list of nodes */
        final List<ThemisNodeInstance> myList = new ArrayList<>();
        for (Node myNode : pNodeList) {
            final ThemisNodeInstance myParsed = parseNode(myNode);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * parse a list of modifiers.
     *
     * @param pNodeList the list of Modifiers
     * @return the list of parsed modifiers
     * @throws OceanusException on error
     */
    ThemisModifierList parseModifierList(NodeList<? extends Node> pNodeList) throws OceanusException;

    /**
     * Parse a type.
     *
     * @param pType the type
     * @return the parsed type
     * @throws OceanusException on error
     */
    ThemisTypeInstance parseType(Type pType) throws OceanusException;

    /**
     * parse a list of types.
     *
     * @param pTypeList the list of Types
     * @return the list of parsed types
     * @throws OceanusException on error
     */
    default List<ThemisTypeInstance> parseTypeList(final NodeList<? extends Type> pTypeList) throws OceanusException {
        /* Handle null list */
        if (pTypeList == null) {
            return Collections.emptyList();
        }

        /* Create list of nodes */
        final List<ThemisTypeInstance> myList = new ArrayList<>();
        for (Type myType : pTypeList) {
            final ThemisTypeInstance myParsed = parseType(myType);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * Parse a statement.
     *
     * @param pStatement the statement
     * @return the parsed statement
     * @throws OceanusException on error
     */
    ThemisStatementInstance parseStatement(Statement pStatement) throws OceanusException;

    /**
     * parse a list of statements.
     *
     * @param pStatementList the list of Statements
     * @return the list of parsed statements
     * @throws OceanusException on error
     */
    default List<ThemisStatementInstance> parseStatementList(final NodeList<? extends Statement> pStatementList) throws OceanusException {
        /* Handle null list */
        if (pStatementList == null) {
            return Collections.emptyList();
        }

        /* Create list of statements */
        final List<ThemisStatementInstance> myList = new ArrayList<>();
        for (Statement myStatement : pStatementList) {
            final ThemisStatementInstance myParsed = parseStatement(myStatement);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * Parse an expression.
     *
     * @param pExpression the expression
     * @return the parsed expression
     * @throws OceanusException on error
     */
    ThemisExpressionInstance parseExpression(Expression pExpression) throws OceanusException;

    /**
     * parse a list of expressions.
     *
     * @param pExprList the list of Expressions
     * @return the list of parsed expressions
     * @throws OceanusException on error
     */
    default List<ThemisExpressionInstance> parseExprList(final NodeList<? extends Expression> pExprList) throws OceanusException {
        /* Handle null list */
        if (pExprList == null) {
            return Collections.emptyList();
        }

        /* Create list of expressions */
        final List<ThemisExpressionInstance> myList = new ArrayList<>();
        for (Expression myExpr : pExprList) {
            final ThemisExpressionInstance myParsed = parseExpression(myExpr);
            myList.add(myParsed);
        }
        return myList;
    }

    /**
     * Parse a module.
     *
     * @param pDeclaration the module declaration/directive
     * @return the parsed declaration/directive
     * @throws OceanusException on error
     */
    ThemisModuleInstance parseModule(Node pDeclaration) throws OceanusException;

    /**
     * parse a list of module directives.
     *
     * @param pDirList the list of Directives
     * @return the list of parsed directives
     * @throws OceanusException on error
     */
    default List<ThemisModuleInstance> parseModuleList(final NodeList<? extends ModuleDirective> pDirList) throws OceanusException {
        /* Handle null list */
        if (pDirList == null) {
            return Collections.emptyList();
        }

        /* Create list of directives */
        final List<ThemisModuleInstance> myList = new ArrayList<>();
        for (ModuleDirective myDir : pDirList) {
            final ThemisModuleInstance myParsed = parseModule(myDir);
            myList.add(myParsed);
        }
        return myList;
    }
}
