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

import com.github.javaparser.ast.body.MethodDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

import java.util.List;

/**
 * Method Declaration.
 */
public class ThemisXAnalysisDeclMethod
        extends ThemisXAnalysisBaseDeclaration<MethodDeclaration> {
    /**
     * The name.
     */
    private final ThemisXAnalysisNodeInstance theName;

    /**
     * The type.
     */
    private final ThemisXAnalysisTypeInstance theType;

    /**
     * The modifiers.
     */
    private final List<ThemisXAnalysisNodeInstance> theModifiers;

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
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisXAnalysisDeclMethod(final ThemisXAnalysisParser pParser,
                              final MethodDeclaration pDeclaration) throws OceanusException {
        super(pDeclaration);
        theName = pParser.parseNode(pDeclaration.getName());
        theType = pParser.parseType(pDeclaration.getType());
        theModifiers = pParser.parseNodeList(pDeclaration.getModifiers());
        theBody = pParser.parseStatement(pDeclaration.getBody().orElse(null));
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theThrown = pParser.parseTypeList(pDeclaration.getThrownExceptions());
        theParameters = pParser.parseNodeList(pDeclaration.getParameters());
    }

    /**
     * Obtain the modifiers.
     * @return the modifiers
     */
    public List<ThemisXAnalysisNodeInstance> getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public ThemisXAnalysisTypeInstance getType() {
        return theType;
    }

    /**
     * Obtain the parameters.
     * @return the parameters
     */
    public List<ThemisXAnalysisNodeInstance> getParameters() {
        return theParameters;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }

    /**
     * Obtain the type parameters.
     * @return the parameters
     */
    public List<ThemisXAnalysisTypeInstance> getTypeParameters() {
        return theTypeParameters;
    }

    /**
     * Obtain the thrown exceptions.
     * @return the exceptions
     */
    public List<ThemisXAnalysisTypeInstance> getThrown() {
        return theThrown;
    }
}
