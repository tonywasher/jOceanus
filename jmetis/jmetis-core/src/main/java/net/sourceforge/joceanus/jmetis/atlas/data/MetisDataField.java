/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.atlas.data;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldEquality;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Metis Data Field.
 */
public class MetisDataField {
    /**
     * Anchor.
     */
    private final MetisDataFieldSet theAnchor;

    /**
     * Index of value.
     */
    private final Integer theIndex;

    /**
     * Id of field.
     */
    private final MetisFieldId theId;

    /**
     * DataType of field.
     */
    private final MetisDataType theDataType;

    /**
     * Maximum Length of field.
     */
    private final Integer theMaxLength;

    /**
     * The field equality type.
     */
    private final MetisDataFieldEquality theEquality;

    /**
     * The field storage type.
     */
    private final MetisDataFieldStorage theStorage;

    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the field equality type
     * @param pStorage the field storage type
     */
    protected MetisDataField(final MetisDataFieldSet pAnchor,
                             final MetisFieldId pId,
                             final MetisDataType pDataType,
                             final Integer pMaxLength,
                             final MetisDataFieldEquality pEquality,
                             final MetisDataFieldStorage pStorage) {
        /* Store parameters */
        theAnchor = pAnchor;
        theId = pId;
        theDataType = pDataType;
        theMaxLength = pMaxLength;
        theEquality = pEquality;
        theStorage = pStorage;

        /* Allocate value index if required */
        theIndex = theStorage.isVersioned()
                                            ? theAnchor.getNextValue()
                                            : null;

        /* Check Validity */
        checkValidity();
    }

    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the fieldId
     * @param pEquality the field equality type
     * @param pStorage the field storage type
     */
    protected MetisDataField(final MetisDataFieldSet pAnchor,
                             final MetisFieldId pId,
                             final MetisDataFieldEquality pEquality,
                             final MetisDataFieldStorage pStorage) {
        this(pAnchor, pId, MetisDataType.OBJECT, MetisDataFieldSet.FIELD_NO_MAXLENGTH, pEquality, pStorage);
    }

    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pName the name of the field
     */
    protected MetisDataField(final MetisDataFieldSet pAnchor,
                             final String pName) {
        /* Store parameters */
        theAnchor = pAnchor;
        theId = new MetisSimpleFieldId(pName);
        theDataType = MetisDataType.OBJECT;
        theMaxLength = MetisDataFieldSet.FIELD_NO_MAXLENGTH;
        theEquality = MetisDataFieldEquality.DERIVED;
        theStorage = MetisDataFieldStorage.LOCAL;

        /* Check Validity */
        checkValidity();

        /* Allocate index */
        theIndex = theAnchor.getNextValue();
    }

    /**
     * Get the index.
     * @return the index
     */
    public int getIndex() {
        return theIndex;
    }

    /**
     * Get the fieldId.
     * @return the id
     */
    public MetisFieldId getFieldId() {
        return theId;
    }

    /**
     * Get the dataType.
     * @return the dataType
     */
    public MetisDataType getDataType() {
        return theDataType;
    }

    /**
     * Get the maximum length.
     * @return the maxLength
     */
    public Integer getMaxLength() {
        return theMaxLength;
    }

    /**
     * Obtain the equality type.
     * @return equalityType
     */
    public MetisDataFieldEquality getEquality() {
        return theEquality;
    }

    /**
     * Obtain the storage type.
     * @return storageType
     */
    public MetisDataFieldStorage getStorage() {
        return theStorage;
    }

    /**
     * Is this a versioned field?
     * @return true/false
     */
    public boolean isVersioned() {
        return theIndex != null;
    }

    /**
     * Obtain the anchor for the field.
     * @return the anchor
     */
    public MetisDataFieldSet getAnchor() {
        return theAnchor;
    }

    /**
     * Check validity of Field Definition.
     */
    protected void checkValidity() {
        /* Check whether length is valid */
        switch (theDataType) {
            case STRING:
            case BYTEARRAY:
            case CHARARRAY:
                if (theMaxLength < 0) {
                    throw new IllegalArgumentException("Length required for String/Array");
                }
                break;
            default:
                if (theMaxLength != MetisDataFieldSet.FIELD_NO_MAXLENGTH) {
                    throw new IllegalArgumentException("Length allowed only for String/Array");
                }
                break;

        }

        /* Disallow object on Storage */
        if (isVersioned()
            && theEquality.isEquality()
            && MetisDataType.OBJECT.equals(theDataType)) {
            throw new IllegalArgumentException("Object DataType not allowed on equality/versioned");
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!(pThat instanceof MetisDataField)) {
            return false;
        }

        /* Access as MetisDataField */
        final MetisDataField myThat = (MetisDataField) pThat;

        /* Must belong to the same anchor */
        if (!theAnchor.equals(myThat.getAnchor())) {
            return false;
        }

        /* Check the name and index is the same */
        return theIndex == myThat.theIndex
               && theId.equals(myThat.theId);
    }

    @Override
    public int hashCode() {
        return theAnchor.hashCode() * MetisDataFieldSet.HASH_PRIME
               + theId.hashCode();
    }

    @Override
    public String toString() {
        return theId.getId();
    }

    /**
     * Simple class to convert a string into a FieldId.
     */
    public static class MetisSimpleFieldId
            implements MetisFieldId {
        /**
         * The Id.
         */
        private final String theId;

        /**
         * Constructor.
         * @param pId the Id
         */
        public MetisSimpleFieldId(final String pId) {
            theId = pId;
        }

        @Override
        public String getId() {
            return theId;
        }

        /**
         * Convert a resourceId to fieldId.
         * @param pId the resourceId
         * @return the fieldId
         */
        public static MetisFieldId convertResource(final TethysResourceId pId) {
            return pId instanceof MetisFieldId
                                               ? (MetisFieldId) pId
                                               : new MetisSimpleFieldId(pId.getValue());
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!(pThat instanceof MetisSimpleFieldId)) {
                return false;
            }

            /* Access as MetisDataField */
            final MetisSimpleFieldId myThat = (MetisSimpleFieldId) pThat;

            /* Check the Id is the same */
            return theId.equals(myThat.theId);
        }

        @Override
        public int hashCode() {
            return theId.hashCode();
        }
    }
}
