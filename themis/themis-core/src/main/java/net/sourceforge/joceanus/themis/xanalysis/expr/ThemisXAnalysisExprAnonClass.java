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

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * Anonymous class JavaParser representation.
 */
public class ThemisXAnalysisExprAnonClass
        extends ClassOrInterfaceDeclaration {
    /**
     * The anonymous class name.
     */
    private static final String ANON = "$Anon$";

    /**
     * Constructor.
     * @param pExpression the object creation instance
     */
    ThemisXAnalysisExprAnonClass(final ObjectCreationExpr pExpression) {
        /* Set the anonymous class name */
        setName(new SimpleName(ANON));

        /* Set the expression type as the extended type */
        final NodeList<ClassOrInterfaceType> myExtended = new NodeList<>();
        myExtended.add(pExpression.getType().clone());
        setExtendedTypes(myExtended);

        /* Initialise ranges */
        Range myRange = null;
        TokenRange myTokens = TokenRange.INVALID;

        /* Set the members */
        for (BodyDeclaration<?> myBody : pExpression.getAnonymousClassBody().orElse(new NodeList<>())) {
            /* Add clone to list and adjust ranges */
            addMember(myBody.clone());
            myRange = adjustRange(myRange, myBody);
            myTokens = adjustTokenRange(myTokens, myBody);
        }

        /* Record range details */
        if (myRange != null) {
            setRange(myRange);
        }
        if (!TokenRange.INVALID.equals(myTokens)) {
            setTokenRange(myTokens);
        }
    }

    /**
     * Adjust range.
     * @param pRange existing range
     * @param pNode the node to add
     * @return the new range
     */
    private Range adjustRange(final Range pRange,
                              final Node pNode) {
        /* Handle the case where the new node does not have a range */
        final Range myRange = pNode.getRange().orElse(null);
        if (myRange == null) {
            return pRange;
        }

        /* Handle initial range */
        if (pRange == null) {
            return myRange;
        }

        /* Extend range as necessary */
        Range myResult = pRange;
        if (myResult.begin.isAfter(myRange.begin)) {
            myResult = myResult.withBegin(myRange.begin);
        }
        if (myResult.end.isBefore(myRange.end)) {
            myResult = myResult.withEnd(myRange.end);
        }
        return myResult;
    }

    /**
     * Adjust token range.
     * @param pRange existing range
     * @param pNode the node to add
     * @return the new range
     */
    private TokenRange adjustTokenRange(final TokenRange pRange,
                                        final Node pNode) {
        /* Handle the case where the new node does not have a tokenRange */
        final TokenRange myRange = pNode.getTokenRange().orElse(TokenRange.INVALID);
        if (TokenRange.INVALID.equals(myRange)) {
            return pRange;
        }

        /* Handle initial range */
        if (TokenRange.INVALID.equals(pRange)) {
            return myRange;
        }

        /* Extend range as necessary */
        TokenRange myResult = pRange;
        if (badOrder(myResult.getBegin(), myRange.getBegin())) {
            myResult = myResult.withBegin(myRange.getBegin());
        }
        if (badOrder(myRange.getEnd(), myResult.getEnd())) {
            myResult = myResult.withEnd(myRange.getEnd());
        }
        return myResult;
    }

    /**
     * Compare two tokens for order.
     * @param pFirst the first token
     * @param pSecond the second token
     * @return are the two tokens out of order true/false
     */
    private boolean badOrder(final JavaToken pFirst,
                             final JavaToken pSecond) {
        final Range myFirst = pFirst.getRange().orElse(null);
        final Range mySecond = pSecond.getRange().orElse(null);
        if (mySecond == null) {
            return true;
        } else if (myFirst == null) {
            return false;
        }
        return myFirst.isAfter(mySecond);
    }
}
