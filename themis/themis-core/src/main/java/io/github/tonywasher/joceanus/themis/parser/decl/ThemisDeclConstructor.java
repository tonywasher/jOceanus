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
package io.github.tonywasher.joceanus.themis.parser.decl;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisMethodInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeSimpleName;

import java.util.List;

/**
 * Constructor Declaration.
 */
public class ThemisDeclConstructor
        extends ThemisBaseDeclaration<ConstructorDeclaration>
        implements ThemisDeclarationInstance, ThemisMethodInstance {
    /**
     * The name.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final ThemisModifierList theModifiers;

    /**
     * The parameters.
     */
    private final List<ThemisNodeInstance> theParameters;

    /**
     * The body.
     */
    private final ThemisStatementInstance theBody;

    /**
     * The typeParameters.
     */
    private final List<ThemisTypeInstance> theTypeParameters;

    /**
     * The thrown exceptions.
     */
    private final List<ThemisTypeInstance> theThrown;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser      the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisDeclConstructor(final ThemisParserDef pParser,
                          final ConstructorDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = ((ThemisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
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
    public ThemisModifierList getModifiers() {
        return theModifiers;
    }

    @Override
    public List<ThemisNodeInstance> getParameters() {
        return theParameters;
    }

    @Override
    public ThemisStatementInstance getBody() {
        return theBody;
    }

    @Override
    public List<ThemisTypeInstance> getTypeParameters() {
        return theTypeParameters;
    }

    @Override
    public List<ThemisTypeInstance> getThrown() {
        return theThrown;
    }

    @Override
    public List<ThemisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
