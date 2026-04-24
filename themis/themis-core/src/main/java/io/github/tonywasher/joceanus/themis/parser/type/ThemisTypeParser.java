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

import com.github.javaparser.ast.type.Type;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Analysis Type Parser.
 */
public final class ThemisTypeParser {
    /**
     * Private Constructor.
     */
    private ThemisTypeParser() {
    }

    /**
     * Parse a type.
     *
     * @param pParser the parser
     * @param pType   the type
     * @return the parsed type
     * @throws OceanusException on error
     */
    public static ThemisTypeInstance parseType(final ThemisParserDef pParser,
                                               final Type pType) throws OceanusException {
        /* Handle null Type */
        if (pType == null) {
            return null;
        }

        /* Allocate correct Type */
        switch (ThemisType.determineType(pParser, pType)) {
            case ARRAY:
                return new ThemisTypeArray(pParser, pType.asArrayType());
            case CLASSINTERFACE:
                return new ThemisTypeClassInterface(pParser, pType.asClassOrInterfaceType());
            case INTERSECTION:
                return new ThemisTypeIntersection(pParser, pType.asIntersectionType());
            case PARAMETER:
                return new ThemisTypeParameter(pParser, pType.asTypeParameter());
            case PRIMITIVE:
                return new ThemisTypePrimitive(pParser, pType.asPrimitiveType());
            case UNION:
                return new ThemisTypeUnion(pParser, pType.asUnionType());
            case UNKNOWN:
                return new ThemisTypeUnknown(pParser, pType.asUnknownType());
            case VAR:
                return new ThemisTypeVar(pParser, pType.asVarType());
            case VOID:
                return new ThemisTypeVoid(pParser, pType.asVoidType());
            case WILDCARD:
                return new ThemisTypeWildcard(pParser, pType.asWildcardType());
            default:
                throw pParser.buildException("Unsupported Type", pType);
        }
    }
}
