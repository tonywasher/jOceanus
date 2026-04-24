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

import com.github.javaparser.ast.body.Parameter;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Class Declaration.
 */
public class ThemisNodeParameter
        extends ThemisBaseNode<Parameter> {
    /**
     * The name.
     */
    private final ThemisNodeInstance theName;

    /**
     * The type.
     */
    private final ThemisTypeInstance theType;

    /**
     * The modifiers.
     */
    private final ThemisModifierList theModifiers;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pParameter the parameter
     * @throws OceanusException on error
     */
    ThemisNodeParameter(final ThemisParserDef pParser,
                        final Parameter pParameter) throws OceanusException {
        super(pParser, pParameter);
        theModifiers = pParser.parseModifierList(pParameter.getModifiers());
        theName = pParser.parseNode(pParameter.getName());
        theType = pParser.parseType(pParameter.getType());
        theAnnotations = pParser.parseExprList(pParameter.getAnnotations());
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
     * Obtain the modifiers.
     *
     * @return the modifiers
     */
    public ThemisModifierList getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the annotations.
     *
     * @return the annotations
     */
    public List<ThemisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }

    /**
     * Is the parameter varArgs?
     *
     * @return true/false
     */
    public boolean isVarArgs() {
        return getNode().isVarArgs();
    }
}
