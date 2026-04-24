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

import com.github.javaparser.ast.body.VariableDeclarator;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Class Declaration.
 */
public class ThemisNodeVariable
        extends ThemisBaseNode<VariableDeclarator> {
    /**
     * The name.
     */
    private final ThemisNodeInstance theName;

    /**
     * The type.
     */
    private final ThemisTypeInstance theType;

    /**
     * The initializer.
     */
    private final ThemisExpressionInstance theInitializer;

    /**
     * Constructor.
     *
     * @param pParser      the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisNodeVariable(final ThemisParserDef pParser,
                       final VariableDeclarator pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = pParser.parseNode(pDeclaration.getName());
        theType = pParser.parseType(pDeclaration.getType());
        theInitializer = pParser.parseExpression(pDeclaration.getInitializer().orElse(null));
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
     * Obtain the type.
     *
     * @return the type
     */
    public ThemisTypeInstance getType() {
        return theType;
    }

    /**
     * Obtain the initializer.
     *
     * @return the initializer
     */
    public ThemisExpressionInstance getInitializer() {
        return theInitializer;
    }
}
