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
package net.sourceforge.joceanus.themis.xanalysis.parser.type;

import com.github.javaparser.ast.type.WildcardType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Wildcard Type Declaration.
 */
public class ThemisXAnalysisTypeWildcard
        extends ThemisXAnalysisBaseType<WildcardType> {
    /**
     * The extended type.
     */
    private final ThemisXAnalysisTypeInstance theExtended;

    /**
     * The super type.
     */
    private final ThemisXAnalysisTypeInstance theSuper;

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
    ThemisXAnalysisTypeWildcard(final ThemisXAnalysisParserDef pParser,
                                final WildcardType pType) throws OceanusException {
        super(pParser, pType);
        theExtended = pParser.parseType(pType.getExtendedType().orElse(null));
        theSuper = pParser.parseType(pType.getSuperType().orElse(null));
        theAnnotations = pParser.parseExprList(pType.getAnnotations());
    }

    /**
     * Obtain the extended.
     * @return the extended
     */
    public ThemisXAnalysisTypeInstance getExtended() {
        return theExtended;
    }

    /**
     * Obtain the super.
     * @return the super
     */
    public ThemisXAnalysisTypeInstance getSuper() {
        return theSuper;
    }

    /**
     * Obtain the annotations.
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
