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

import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.PrimitiveType.Primitive;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedType;

/**
 * Primitive Type Declaration.
 */
public class ThemisXAnalysisTypePrimitive
        implements ThemisXAnalysisParsedType {
    /**
     * The type.
     */
    private final PrimitiveType theType;

    /**
     * The Primitive type.
     */
    private final Primitive thePrimitive;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pType the type
     */
    public ThemisXAnalysisTypePrimitive(final ThemisXAnalysisParser pParser,
                                        final PrimitiveType pType) {
        theType = pType;
        thePrimitive = theType.getType();
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public PrimitiveType getType() {
        return theType;
    }

    /**
     * Obtain the primitive type.
     * @return the type
     */
    public Primitive getPrimitive() {
        return thePrimitive;
    }

    @Override
    public String toString() {
        return theType.toString();
    }
}
