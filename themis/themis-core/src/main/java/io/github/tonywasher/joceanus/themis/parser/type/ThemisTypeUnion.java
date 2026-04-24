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

import com.github.javaparser.ast.type.UnionType;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Union Type Declaration.
 */
public class ThemisTypeUnion
        extends ThemisBaseType<UnionType> {
    /**
     * The elements.
     */
    private final List<ThemisTypeInstance> theElements;

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
    ThemisTypeUnion(final ThemisParserDef pParser,
                    final UnionType pType) throws OceanusException {
        super(pParser, pType);
        theElements = pParser.parseTypeList(pType.getElements());
        theAnnotations = pParser.parseExprList(pType.getAnnotations());
    }

    /**
     * Obtain the elements.
     *
     * @return the elements
     */
    public List<ThemisTypeInstance> getElements() {
        return theElements;
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
