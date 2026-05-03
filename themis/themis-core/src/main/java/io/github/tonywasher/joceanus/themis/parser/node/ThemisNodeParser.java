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
package io.github.tonywasher.joceanus.themis.parser.node;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.SwitchEntry;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Analysis Node Parser.
 */
public final class ThemisNodeParser {
    /**
     * Private Constructor.
     */
    private ThemisNodeParser() {
    }

    /**
     * Parse a node.
     *
     * @param pParser the parser
     * @param pNode   the node
     * @return the parsed node
     * @throws OceanusException on error
     */
    public static ThemisNodeInstance parseNode(final ThemisParserDef pParser,
                                               final Node pNode) throws OceanusException {
        /* Handle null Node */
        if (pNode == null) {
            return null;
        }

        /* Create appropriate node */
        return switch (ThemisNode.determineNode(pParser, pNode)) {
            case ARRAYLEVEL -> new ThemisNodeArrayLevel(pParser, (ArrayCreationLevel) pNode);
            case CASE -> new ThemisNodeCase(pParser, (SwitchEntry) pNode);
            case CATCH -> new ThemisNodeCatch(pParser, (CatchClause) pNode);
            case COMPILATIONUNIT -> new ThemisNodeCompilationUnit(pParser, (CompilationUnit) pNode);
            case COMMENT -> new ThemisNodeComment(pParser, (Comment) pNode);
            case IMPORT -> new ThemisNodeImport(pParser, (ImportDeclaration) pNode);
            case MODIFIER -> new ThemisNodeModifier(pParser, (Modifier) pNode);
            case NAME -> new ThemisNodeName(pParser, (Name) pNode);
            case PACKAGE -> new ThemisNodePackage(pParser, (PackageDeclaration) pNode);
            case PARAMETER -> new ThemisNodeParameter(pParser, (Parameter) pNode);
            case SIMPLENAME -> new ThemisNodeSimpleName(pParser, (SimpleName) pNode);
            case VALUEPAIR -> new ThemisNodeValuePair(pParser, (MemberValuePair) pNode);
            case VARIABLE -> new ThemisNodeVariable(pParser, (VariableDeclarator) pNode);
            default -> throw pParser.buildException("Unsupported Node Type", pNode);
        };
    }

    /**
     * parse a list of modifiers.
     *
     * @param pParser   the parser
     * @param pNodeList the list of Modifiers
     * @return the list of parsed modifiers
     * @throws OceanusException on error
     */
    public static ThemisModifierList parseModifierList(final ThemisParserDef pParser,
                                                       final NodeList<? extends Node> pNodeList) throws OceanusException {
        final List<ThemisNodeInstance> myList = pParser.parseNodeList(pNodeList);
        return new ThemisNodeModifierList(myList);
    }
}
