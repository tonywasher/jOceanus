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
package io.github.tonywasher.joceanus.themis.parser.type;

import com.github.javaparser.ast.type.TypeParameter;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeSimpleName;

import java.util.List;

/**
 * TypeParameter Declaration.
 */
public class ThemisTypeParameter
        extends ThemisBaseType<TypeParameter> {
    /**
     * The name of the parameter.
     */
    private final String theName;

    /**
     * The bounds.
     */
    private final List<ThemisTypeInstance> theBounds;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pType   the type
     * @throws OceanusException on error
     */
    ThemisTypeParameter(final ThemisParserDef pParser,
                        final TypeParameter pType) throws OceanusException {
        super(pParser, pType);
        theName = ((ThemisNodeSimpleName) pParser.parseNode(pType.getName())).getName();
        theBounds = pParser.parseTypeList(pType.getTypeBound());
        theAnnotations = pParser.parseExprList(pType.getAnnotations());
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the bounds.
     *
     * @return the bounds
     */
    public List<ThemisTypeInstance> getBounds() {
        return theBounds;
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
