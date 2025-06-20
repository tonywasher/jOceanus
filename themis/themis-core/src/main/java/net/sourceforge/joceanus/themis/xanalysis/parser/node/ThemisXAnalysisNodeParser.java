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
package net.sourceforge.joceanus.themis.xanalysis.parser.node;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.SwitchEntry;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Analysis Node Parser.
 */
public final class ThemisXAnalysisNodeParser {
    /**
     * Private Constructor.
     */
    private ThemisXAnalysisNodeParser() {
    }

    /**
     * Parse a node.
     * @param pParser the parser
     * @param pNode the node
     * @return the parsed node
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisNodeInstance parseNode(final ThemisXAnalysisParserDef pParser,
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
            case COMMENT:         return new ThemisXAnalysisNodeComment(pParser, (Comment) pNode);
            case IMPORT:          return new ThemisXAnalysisNodeImport(pParser, (ImportDeclaration) pNode);
            case MODIFIER:        return new ThemisXAnalysisNodeModifier(pParser, (Modifier) pNode);
            case NAME:            return new ThemisXAnalysisNodeName(pParser, (Name) pNode);
            case PACKAGE:         return new ThemisXAnalysisNodePackage(pParser, (PackageDeclaration) pNode);
            case PARAMETER:       return new ThemisXAnalysisNodeParameter(pParser, (Parameter) pNode);
            case SIMPLENAME:      return new ThemisXAnalysisNodeSimpleName(pParser, (SimpleName) pNode);
            case VALUEPAIR:       return new ThemisXAnalysisNodeValuePair(pParser, (MemberValuePair) pNode);
            case VARIABLE:        return new ThemisXAnalysisNodeVariable(pParser, (VariableDeclarator) pNode);
            default:              throw pParser.buildException("Unsupported Node Type", pNode);
        }
    }
}
