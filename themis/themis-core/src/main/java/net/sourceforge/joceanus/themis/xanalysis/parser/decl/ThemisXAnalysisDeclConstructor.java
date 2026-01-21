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
package net.sourceforge.joceanus.themis.xanalysis.parser.decl;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisMethodInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisModifierList;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeSimpleName;

import java.util.List;

/**
 * Constructor Declaration.
 */
public class ThemisXAnalysisDeclConstructor
        extends ThemisXAnalysisBaseDeclaration<ConstructorDeclaration>
        implements ThemisXAnalysisDeclarationInstance, ThemisXAnalysisMethodInstance {
    /**
     * The name.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final ThemisXAnalysisModifierList theModifiers;

    /**
     * The parameters.
     */
    private final List<ThemisXAnalysisNodeInstance> theParameters;

    /**
     * The body.
     */
    private final ThemisXAnalysisStatementInstance theBody;

    /**
     * The typeParameters.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypeParameters;

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
     *
     * @param pParser      the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisXAnalysisDeclConstructor(final ThemisXAnalysisParserDef pParser,
                                   final ConstructorDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = ((ThemisXAnalysisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theModifiers = pParser.parseModifierList(pDeclaration.getModifiers());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theParameters = pParser.parseNodeList(pDeclaration.getParameters());
        theThrown = pParser.parseTypeList(pDeclaration.getThrownExceptions());
        theBody = pParser.parseStatement(pDeclaration.getBody());
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public ThemisXAnalysisModifierList getModifiers() {
        return theModifiers;
    }

    @Override
    public List<ThemisXAnalysisNodeInstance> getParameters() {
        return theParameters;
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
