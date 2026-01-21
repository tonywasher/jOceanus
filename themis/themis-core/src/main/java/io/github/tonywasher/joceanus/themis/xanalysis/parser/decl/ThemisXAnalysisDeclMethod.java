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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.decl;

import com.github.javaparser.ast.body.MethodDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisMethodInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisModifierList;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeSimpleName;

import java.util.List;

/**
 * Method Declaration.
 */
public class ThemisXAnalysisDeclMethod
        extends ThemisXAnalysisBaseDeclaration<MethodDeclaration>
        implements ThemisXAnalysisDeclarationInstance, ThemisXAnalysisMethodInstance {
    /**
     * The override annotation.
     */
    private static final String OVERRIDE_ANNOTATION = "Override";

    /**
     * The name.
     */
    private final String theName;

    /**
     * The type.
     */
    private final ThemisXAnalysisTypeInstance theType;

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
    ThemisXAnalysisDeclMethod(final ThemisXAnalysisParserDef pParser,
                              final MethodDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = ((ThemisXAnalysisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theType = pParser.parseType(pDeclaration.getType());
        theModifiers = pParser.parseModifierList(pDeclaration.getModifiers());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theParameters = pParser.parseNodeList(pDeclaration.getParameters());
        theThrown = pParser.parseTypeList(pDeclaration.getThrownExceptions());
        theBody = pParser.parseStatement(pDeclaration.getBody().orElse(null));
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public ThemisXAnalysisModifierList getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public ThemisXAnalysisTypeInstance getType() {
        return theType;
    }

    /**
     * Does the method override a previous definition.
     *
     * @return true/false
     */
    public boolean isOverride() {
        return getNode().isAnnotationPresent(OVERRIDE_ANNOTATION);
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
