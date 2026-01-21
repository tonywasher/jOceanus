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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.type;

import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Primitive Type Declaration.
 */
public class ThemisXAnalysisTypePrimitive
        extends ThemisXAnalysisBaseType<PrimitiveType> {
    /**
     * The Primitive type.
     */
    private final Primitive thePrimitive;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pType   the type
     * @throws OceanusException on error
     */
    ThemisXAnalysisTypePrimitive(final ThemisXAnalysisParserDef pParser,
                                 final PrimitiveType pType) throws OceanusException {
        super(pParser, pType);
        thePrimitive = pType.getType();
        theAnnotations = pParser.parseExprList(pType.getAnnotations());
    }

    /**
     * Obtain the primitive type.
     *
     * @return the type
     */
    public Primitive getPrimitive() {
        return thePrimitive;
    }

    /**
     * Obtain the annotations.
     *
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
