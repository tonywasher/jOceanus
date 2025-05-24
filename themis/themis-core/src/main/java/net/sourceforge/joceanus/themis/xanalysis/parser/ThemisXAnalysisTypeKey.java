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

import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.WildcardType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TypeKey.
 */
public final class ThemisXAnalysisTypeKey {
    /**
     * The Type.
     */
    private final ThemisXAnalysisType theType;

    /**
     * The Id.
     */
    private final Object theId;

    /**
     * Constructor.
     * @param pType the type
     */
    private ThemisXAnalysisTypeKey(final ThemisXAnalysisType pType) {
        this(pType, null);
    }

    /**
     * Constructor.
     * @param pType the type
     * @param pId the id
     */
    private ThemisXAnalysisTypeKey(final ThemisXAnalysisType pType,
                                   final Object pId) {
        theType = pType;
        theId = pId;
    }

    @Override
    public boolean equals(final Object pThat) {
        if (pThat == this) {
            return true;
        }
        if (!(pThat instanceof ThemisXAnalysisTypeKey myThat)) {
            return false;
        }

        /* Test component parts */
        return theType.equals(myThat.theType)
                && Objects.equals(theId, myThat.theId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theId);
    }

    /**
     * Derive id for type.
     * @param pType the type
     * @return the id
     * @throws OceanusException on error
     */
    static ThemisXAnalysisTypeKey determineKeyForType(final Type pType) throws OceanusException {
        /* Handle null type */
        if (pType == null) {
            return null;
        }

        /* Determine type */
        final ThemisXAnalysisType myType = ThemisXAnalysisType.determineType(pType);
        switch (myType) {
            case UNKNOWN:
            case VOID:
            case VAR:
                return new ThemisXAnalysisTypeKey(myType);
            case PRIMITIVE:
                return new ThemisXAnalysisTypeKey(myType, pType.asPrimitiveType().getType());
            case CLASSINTERFACE:
                return new ThemisXAnalysisTypeKey(myType, pType.asClassOrInterfaceType().getNameAsString());
            case ARRAY:
                return new ThemisXAnalysisTypeKey(myType, determineKeyForType(pType.asArrayType().getComponentType()));
            case INTERSECTION:
                return new ThemisXAnalysisTypeKey(myType, new ThemisXAnalysisTypeIntersectionKey(pType.asIntersectionType()));
            case PARAMETER:
                return new ThemisXAnalysisTypeKey(myType, new ThemisXAnalysisTypeParameterKey(pType.asTypeParameter()));
            case UNION:
                return new ThemisXAnalysisTypeKey(myType, new ThemisXAnalysisTypeUnionKey(pType.asUnionType()));
            case WILDCARD:
                return new ThemisXAnalysisTypeKey(myType, new ThemisXAnalysisTypeWildcardKey(pType.asWildcardType()));
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * IntersectionId.
     */
    private static class ThemisXAnalysisTypeIntersectionKey {
        /**
         * The list of ids.
         */
        private final List<ThemisXAnalysisTypeKey> theIds;

        /**
         * Constructor.
         * @param pType the intersection type.
         * @throws OceanusException on error
         */
        ThemisXAnalysisTypeIntersectionKey(final IntersectionType pType) throws OceanusException {
            theIds = new ArrayList<>();
            for (ReferenceType myElement : pType.getElements()) {
                theIds.add(determineKeyForType(myElement));
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            if (pThat == this) {
                return true;
            }
            if (!(pThat instanceof ThemisXAnalysisTypeIntersectionKey myThat)) {
                return false;
            }

            /* Test component parts */
            return theIds.equals(myThat.theIds);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(theIds);
        }
    }


    /**
     * ParameterId.
     */
    private static class ThemisXAnalysisTypeParameterKey {
        /**
         * The Name.
         */
        private final String theName;

        /**
         * The list of ids.
         */
        private final List<ThemisXAnalysisTypeKey> theIds;

        /**
         * Constructor.
         * @param pType the intersection type.
         * @throws OceanusException on error
         */
        ThemisXAnalysisTypeParameterKey(final TypeParameter pType) throws OceanusException {
            theName = pType.getNameAsString();
            theIds = new ArrayList<>();
            for (ReferenceType myElement : pType.getTypeBound()) {
                theIds.add(determineKeyForType(myElement));
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            if (pThat == this) {
                return true;
            }
            if (!(pThat instanceof ThemisXAnalysisTypeParameterKey myThat)) {
                return false;
            }

            /* Test component parts */
            return Objects.equals(theName, myThat.theName)
                    && Objects.equals(theIds, myThat.theIds);
        }

        @Override
        public int hashCode() {
            return Objects.hash(theName, theIds);
        }
    }

    /**
     * UnionId.
     */
    private static class ThemisXAnalysisTypeUnionKey {
        /**
         * The list of ids.
         */
        private final List<ThemisXAnalysisTypeKey> theIds;

        /**
         * Constructor.
         * @param pType the intersection type.
         * @throws OceanusException on error
         */
        ThemisXAnalysisTypeUnionKey(final UnionType pType) throws OceanusException {
            theIds = new ArrayList<>();
            for (ReferenceType myElement : pType.getElements()) {
                theIds.add(determineKeyForType(myElement));
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            if (pThat == this) {
                return true;
            }
            if (!(pThat instanceof ThemisXAnalysisTypeUnionKey myThat)) {
                return false;
            }

            /* Test component parts */
            return theIds.equals(myThat.theIds);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(theIds);
        }
    }

    /**
     * WildcardId.
     */
    private static class ThemisXAnalysisTypeWildcardKey {
        /**
         * The ExtendedId.
         */
        private final ThemisXAnalysisTypeKey theExtendedId;

        /**
         * The SuperId.
         */
        private final ThemisXAnalysisTypeKey theSuperId;

        /**
         * Constructor.
         * @param pType the intersection type.
         * @throws OceanusException on error
         */
        ThemisXAnalysisTypeWildcardKey(final WildcardType pType) throws OceanusException {
            theExtendedId = determineKeyForType(pType.getExtendedType().orElse(null));
            theSuperId = determineKeyForType(pType.getSuperType().orElse(null));
        }

        @Override
        public boolean equals(final Object pThat) {
            if (pThat == this) {
                return true;
            }
            if (!(pThat instanceof ThemisXAnalysisTypeWildcardKey myThat)) {
                return false;
            }

            /* Test component parts */
            return Objects.equals(theExtendedId, myThat.theExtendedId)
                && Objects.equals(theSuperId, myThat.theSuperId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(theExtendedId, theSuperId);
        }
    }
}
