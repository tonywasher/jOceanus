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
package net.sourceforge.joceanus.themis.xanalysis.parser;

import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisType;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeArray;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeClassInterface;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeIntersection;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeParameter;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypePrimitive;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeUnion;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeUnknown;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeVar;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeVoid;
import net.sourceforge.joceanus.themis.xanalysis.type.ThemisXAnalysisTypeWildcard;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache of Type definitions.
 */
public class ThemisXAnalysisTypeCache {
    /**
     * Parser.
     */
    private final ThemisXAnalysisParser theParser;

    /**
     * Map of type to typeInstance.
     */
    private final Map<ThemisXAnalysisTypeKey, ThemisXAnalysisTypeInstance> theMap;

    /**
     * Constructor.
     * @param pParser the parser
     */
    ThemisXAnalysisTypeCache(final ThemisXAnalysisParser pParser) {
        theParser = pParser;
        theMap = new HashMap<>();
    }

    /**
     * Obtain/create cached id.
     * @param pType the type
     * @return the parsed type
     * @throws OceanusException on error
     */
    ThemisXAnalysisTypeInstance parseType(final Type pType) throws OceanusException {
        /* Determine the key */
        final ThemisXAnalysisTypeKey myKey = ThemisXAnalysisTypeKey.determineKeyForType(pType);

        /* LookUp the type */
        ThemisXAnalysisTypeInstance myParsed = theMap.get(myKey);
        if (myParsed == null) {
            myParsed = theParser.parseType(pType);
            theMap.put(myKey, myParsed);
        }

        /* Return the parsed type */
        return myParsed;
    }

    /**
     * Create new parsed Type.
     * @param pType the type
     * @return the parsed type
     * @throws OceanusException on error
     */
    private ThemisXAnalysisTypeInstance createParsedType(final Type pType) throws OceanusException {
        /* Allocate correct Type */
        switch (ThemisXAnalysisType.determineType(pType)) {
            case ARRAY:          return new ThemisXAnalysisTypeArray(theParser, pType.asArrayType());
            case CLASSINTERFACE: return new ThemisXAnalysisTypeClassInterface(theParser, pType.asClassOrInterfaceType());
            case INTERSECTION:   return new ThemisXAnalysisTypeIntersection(theParser, pType.asIntersectionType());
            case PARAMETER:      return new ThemisXAnalysisTypeParameter(theParser, pType.asTypeParameter());
            case PRIMITIVE:      return new ThemisXAnalysisTypePrimitive(theParser, pType.asPrimitiveType());
            case UNION:          return new ThemisXAnalysisTypeUnion(theParser, pType.asUnionType());
            case UNKNOWN:        return new ThemisXAnalysisTypeUnknown(theParser, pType.asUnknownType());
            case VAR:            return new ThemisXAnalysisTypeVar(theParser, pType.asVarType());
            case VOID:           return new ThemisXAnalysisTypeVoid(theParser, pType.asVoidType());
            case WILDCARD:       return new ThemisXAnalysisTypeWildcard(theParser, pType.asWildcardType());
            default:             throw new ThemisDataException("Unsupported Type Type");
        }
    }
}
