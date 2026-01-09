/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.parser.type;

import com.github.javaparser.ast.type.ArrayType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Array Type Declaration.
 */
public class ThemisXAnalysisTypeArray
        extends ThemisXAnalysisTypeReference<ArrayType> {
    /**
     * The underlying type.
     */
    private final ThemisXAnalysisTypeInstance theUnderlying;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pType the type
     * @throws OceanusException on error
     */
    ThemisXAnalysisTypeArray(final ThemisXAnalysisParserDef pParser,
                             final ArrayType pType) throws OceanusException {
        super(pParser, pType);
        theUnderlying = pParser.parseType(pType.getComponentType());
        theAnnotations = pParser.parseExprList(pType.getAnnotations());
    }

    /**
     * Obtain the underlying type.
     * @return the type
     */
    public ThemisXAnalysisTypeInstance getUnderlying() {
        return theUnderlying;
    }

    /**
     * Obtain the annotations.
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
