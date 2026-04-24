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

import com.github.javaparser.ast.body.EnumConstantDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Class Declaration.
 */
public class ThemisDeclEnumValue
        extends ThemisBaseDeclaration<EnumConstantDeclaration> {
    /**
     * The name.
     */
    private final ThemisNodeInstance theName;

    /**
     * The arguments.
     */
    private final List<ThemisExpressionInstance> theArguments;

    /**
     * The class body.
     */
    private final List<ThemisDeclarationInstance> theBody;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser      the parser
     * @param pDeclaration the declaration
     */
    ThemisDeclEnumValue(final ThemisParserDef pParser,
                        final EnumConstantDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = pParser.parseNode(pDeclaration.getName());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
        theArguments = pParser.parseExprList(pDeclaration.getArguments());
        theBody = pParser.parseDeclarationList(pDeclaration.getClassBody());
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public ThemisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the arguments.
     *
     * @return the arguments
     */
    public List<ThemisExpressionInstance> getArguments() {
        return theArguments;
    }

    /**
     * Obtain the body.
     *
     * @return the body
     */
    public List<ThemisDeclarationInstance> getBody() {
        return theBody;
    }

    /**
     * Obtain the annotations.
     *
     * @return the annotations
     */
    public List<ThemisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
