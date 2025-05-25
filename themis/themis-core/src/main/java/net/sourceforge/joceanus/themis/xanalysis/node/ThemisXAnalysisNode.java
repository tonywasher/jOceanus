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
package net.sourceforge.joceanus.themis.xanalysis.node;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.SwitchEntry;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

import java.util.function.Predicate;

/**
 * Analysis Node.
 */
public enum ThemisXAnalysisNode {
    /**
     * ArrayLevel.
     */
    ARRAYLEVEL(n -> n instanceof ArrayCreationLevel),

    /**
     * Case.
     */
    CASE(n -> n instanceof SwitchEntry),

    /**
     * Catch.
     */
    CATCH(n -> n instanceof CatchClause),

    /**
     * Compilation Unit.
     */
    COMPILATIONUNIT(n -> n instanceof CompilationUnit),

    /**
     * Import.
     */
    IMPORT(n -> n instanceof ImportDeclaration),

    /**
     * Modifier.
     */
    MODIFIER(n -> n instanceof Modifier),

    /**
     * Name.
     */
    NAME(n -> n instanceof Name),

    /**
     * Parameter.
     */
    PACKAGE(n -> n instanceof PackageDeclaration),

    /**
     * Parameter.
     */
    PARAMETER(n -> n instanceof Parameter),

    /**
     * SimpleName.
     */
    SIMPLENAME(n -> n instanceof SimpleName),

    /**
     * ValuePair.
     */
    VALUEPAIR(n -> n instanceof MemberValuePair),

    /**
     * Variable.
     */
    VARIABLE(n -> n instanceof VariableDeclarator);

    /**
     * The test.
     */
    private final Predicate<Node> theTester;

    /**
     * Constructor.
     * @param pTester the test method
     */
    ThemisXAnalysisNode(final Predicate<Node> pTester) {
        theTester = pTester;
    }

    /**
     * Determine type of node.
     * @param pParser the parser
     * @param pNode the node
     * @return the nodeType
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisNode determineNode(final ThemisXAnalysisParser pParser,
                                                    final Node pNode) throws OceanusException {
        /* Loop testing each node type */
        for (ThemisXAnalysisNode myNode : values()) {
            if (myNode.theTester.test(pNode)) {
                return myNode;
            }
        }

        /* Unrecognised nodeType */
        throw pParser.buildException("Unexpected Node", pNode);
    }

    /**
     * Parse a node.
     * @param pParser the parser
     * @param pNode the node
     * @return the parsed node
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisNodeInstance parseNode(final ThemisXAnalysisParser pParser,
                                                        final Node pNode) throws OceanusException {
        /* Handle null Node */
        if (pNode == null) {
            return null;
        }

        /* Create appropriate node */
        switch (ThemisXAnalysisNode.determineNode(pParser, pNode)) {
            case ARRAYLEVEL:      return new ThemisXAnalysisNodeArrayLevel(pParser, (ArrayCreationLevel) pNode);
            case CASE:            return new ThemisXAnalysisNodeCase(pParser, (SwitchEntry) pNode);
            case CATCH:           return new ThemisXAnalysisNodeCatch(pParser, (CatchClause) pNode);
            case COMPILATIONUNIT: return new ThemisXAnalysisNodeCompilationUnit(pParser, (CompilationUnit) pNode);
            case IMPORT:          return new ThemisXAnalysisNodeImport(pParser, (ImportDeclaration) pNode);
            case MODIFIER:        return new ThemisXAnalysisNodeModifier((Modifier) pNode);
            case NAME:            return new ThemisXAnalysisNodeName(pParser, (Name) pNode);
            case PACKAGE:         return new ThemisXAnalysisNodePackage(pParser, (PackageDeclaration) pNode);
            case PARAMETER:       return new ThemisXAnalysisNodeParameter(pParser, (Parameter) pNode);
            case SIMPLENAME:      return new ThemisXAnalysisNodeSimpleName((SimpleName) pNode);
            case VALUEPAIR:       return new ThemisXAnalysisNodeValuePair(pParser, (MemberValuePair) pNode);
            case VARIABLE:        return new ThemisXAnalysisNodeVariable(pParser, (VariableDeclarator) pNode);
            default:              throw pParser.buildException("Unsupported Node Type", pNode);
        }
    }
}
