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

import com.github.javaparser.ast.type.IntersectionType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Intersection Type Declaration.
 */
public class ThemisXAnalysisTypeIntersection
        extends ThemisXAnalysisBaseType<IntersectionType> {
    /**
     * The elements.
     */
    private final List<ThemisXAnalysisTypeInstance> theElements;

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
    ThemisXAnalysisTypeIntersection(final ThemisXAnalysisParserDef pParser,
                                    final IntersectionType pType) throws OceanusException {
        super(pParser, pType);
        theElements = pParser.parseTypeList(pType.getElements());
        theAnnotations = pParser.parseExprList(pType.getAnnotations());
    }

    /**
     * Obtain the elements.
     * @return the elements
     */
    public List<ThemisXAnalysisTypeInstance> getElements() {
        return theElements;
    }

    /**
     * Obtain the annotations.
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
