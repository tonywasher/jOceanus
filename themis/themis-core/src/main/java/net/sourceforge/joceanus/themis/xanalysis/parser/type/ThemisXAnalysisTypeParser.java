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

import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Analysis Type Parser.
 */
public final class ThemisXAnalysisTypeParser {
    /**
     * Private Constructor.
     */
    private ThemisXAnalysisTypeParser() {
    }

    /**
     * Parse a type.
     * @param pParser the parser
     * @param pType the type
     * @return the parsed type
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisTypeInstance parseType(final ThemisXAnalysisParserDef pParser,
                                                        final Type pType) throws OceanusException {
        /* Handle null Type */
        if (pType == null) {
            return null;
        }

        /* Allocate correct Type */
        switch (ThemisXAnalysisType.determineType(pParser, pType)) {
            case ARRAY:          return new ThemisXAnalysisTypeArray(pParser, pType.asArrayType());
            case CLASSINTERFACE: return new ThemisXAnalysisTypeClassInterface(pParser, pType.asClassOrInterfaceType());
            case INTERSECTION:   return new ThemisXAnalysisTypeIntersection(pParser, pType.asIntersectionType());
            case PARAMETER:      return new ThemisXAnalysisTypeParameter(pParser, pType.asTypeParameter());
            case PRIMITIVE:      return new ThemisXAnalysisTypePrimitive(pParser, pType.asPrimitiveType());
            case UNION:          return new ThemisXAnalysisTypeUnion(pParser, pType.asUnionType());
            case UNKNOWN:        return new ThemisXAnalysisTypeUnknown(pParser, pType.asUnknownType());
            case VAR:            return new ThemisXAnalysisTypeVar(pParser, pType.asVarType());
            case VOID:           return new ThemisXAnalysisTypeVoid(pParser, pType.asVoidType());
            case WILDCARD:       return new ThemisXAnalysisTypeWildcard(pParser, pType.asWildcardType());
            default:             throw pParser.buildException("Unsupported Type", pType);
        }
    }
}
