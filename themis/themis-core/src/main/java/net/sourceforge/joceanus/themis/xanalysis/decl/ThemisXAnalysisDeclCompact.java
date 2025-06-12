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
package net.sourceforge.joceanus.themis.xanalysis.decl;

import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisMethodInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeSimpleName;

import java.util.List;

/**
 * Compact Constructor Declaration.
 */
public class ThemisXAnalysisDeclCompact
        extends ThemisXAnalysisBaseDeclaration<CompactConstructorDeclaration>
        implements ThemisXAnalysisMethodInstance {
    /**
     * The name.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final List<ThemisXAnalysisNodeInstance> theModifiers;

    /**
     * The typeParameters.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypeParameters;

    /**
     * The body.
     */
    private final ThemisXAnalysisStatementInstance theBody;

    /**
     * The thrown exceptions.
     */
    private final List<ThemisXAnalysisTypeInstance> theThrown;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisXAnalysisDeclCompact(final ThemisXAnalysisParserDef pParser,
                               final CompactConstructorDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = ((ThemisXAnalysisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theBody = pParser.parseStatement(pDeclaration.getBody());
        theModifiers = pParser.parseNodeList(pDeclaration.getModifiers());
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theThrown = pParser.parseTypeList(pDeclaration.getThrownExceptions());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public List<ThemisXAnalysisNodeInstance> getModifiers() {
        return theModifiers;
    }

    @Override
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }

    @Override
    public List<ThemisXAnalysisTypeInstance> getTypeParameters() {
        return theTypeParameters;
    }

    @Override
    public List<ThemisXAnalysisTypeInstance> getThrown() {
        return theThrown;
    }

    @Override
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
