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

import com.github.javaparser.ast.type.ArrayType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedType;

/**
 * Array Type Declaration.
 */
public class ThemisXAnalysisTypeArray
        implements ThemisXAnalysisParsedType {
    /**
     * The type.
     */
    private final ArrayType theType;

    /**
     * The underlying type.
     */
    private final ThemisXAnalysisParsedType theUnderlying;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pType the type
     * @throws OceanusException on error
     */
    public ThemisXAnalysisTypeArray(final ThemisXAnalysisParser pParser,
                                    final ArrayType pType) throws OceanusException {
        theType = pType;
        theUnderlying = pParser.parseType(theType.getComponentType());
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public ArrayType getType() {
        return theType;
    }

    /**
     * Obtain the underlying type.
     * @return the type
     */
    public ThemisXAnalysisParsedType getUnderlying() {
        return theUnderlying;
    }

    @Override
    public String toString() {
        return theType.toString();
    }
}
