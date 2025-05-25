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

import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

import java.util.function.Predicate;

/**
 * Analysis TypeType.
 */
public enum ThemisXAnalysisType {
    /**
     * Array.
     */
    ARRAY(Type::isArrayType),

    /**
     * Class/Interface.
     */
    CLASSINTERFACE(Type::isClassOrInterfaceType),

    /**
     * Intersection.
     */
    INTERSECTION(Type::isIntersectionType),

    /**
     * TypeParameter.
     */
    PARAMETER(Type::isTypeParameter),

    /**
     * Primitive.
     */
    PRIMITIVE(Type::isPrimitiveType),

    /**
     * Union.
     */
    UNION(Type::isUnionType),

    /**
     * Unknown.
     */
    UNKNOWN(Type::isUnknownType),

    /**
     * Var.
     */
    VAR(Type::isVarType),

    /**
     * Void.
     */
    VOID(Type::isVoidType),

    /**
     * Wildcard.
     */
    WILDCARD(Type::isWildcardType);

    /**
     * The test.
     */
    private final Predicate<Type> theTester;

    /**
     * Constructor.
     * @param pTester the test method
     */
    ThemisXAnalysisType(final Predicate<Type> pTester) {
        theTester = pTester;
    }

    /**
     * Determine type of type.
     * @param pParser the parser
     * @param pType the type
     * @return the typeType
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisType determineType(final ThemisXAnalysisParser pParser,
                                                    final Type pType) throws OceanusException {
        /* Loop testing each type */
        for (ThemisXAnalysisType myType : values()) {
            if (myType.theTester.test(pType)) {
                return myType;
            }
        }

        /* Unrecognised Type */
        throw pParser.buildException("Unexpected Type", pType);
    }

    /**
     * Parse a type.
     * @param pParser the parser
     * @param pType the type
     * @return the parsed type
     * @throws OceanusException on error
     */
    public static ThemisXAnalysisTypeInstance parseType(final ThemisXAnalysisParser pParser,
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
