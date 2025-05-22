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
package net.sourceforge.joceanus.themis.xanalysis.type;

import com.github.javaparser.ast.type.IntersectionType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedType;

import java.util.List;

/**
 * Intersection Type Declaration.
 */
public class ThemisXAnalysisTypeIntersection
        implements ThemisXAnalysisParsedType {
    /**
     * The type.
     */
    private final IntersectionType theType;

    /**
     * The elements.
     */
    private final List<ThemisXAnalysisParsedType> theElements;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pType the type
     * @throws OceanusException on error
     */
    public ThemisXAnalysisTypeIntersection(final ThemisXAnalysisParser pParser,
                                           final IntersectionType pType) throws OceanusException {
        theType = pType;
        theElements = pParser.parseTypeList(theType.getElements());
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public IntersectionType getType() {
        return theType;
    }

    /**
     * Obtain the elements.
     * @return the elements
     */
    public List<ThemisXAnalysisParsedType> getElements() {
        return theElements;
    }

    @Override
    public String toString() {
        return theType.toString();
    }
}
