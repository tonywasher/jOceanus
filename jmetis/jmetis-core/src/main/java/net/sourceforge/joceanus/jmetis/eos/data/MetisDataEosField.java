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
package net.sourceforge.joceanus.jmetis.eos.data;

import java.util.function.Function;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldEquality;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldDef;

/**
 * Metis Data Field.
 * @param <T> the data type
 */
public class MetisDataEosField<T extends MetisDataEosFieldItem>
        implements MetisDataEosFieldDef {
    /**
     * Anchor.
     */
    private final MetisDataEosFieldSet<T> theAnchor;

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
     * The field value function.
     */
    private final Function<T, Object> theValue;

    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the id of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the field equality type
     * @param pStorage the field storage type
     */
    MetisDataEosField(final MetisDataEosFieldSet<T> pAnchor,
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
        theValue = null;

        /* Allocate value index if required */
        theIndex = theStorage.isVersioned()
                                            ? theAnchor.getNextIndex()
                                            : null;

        /* Check Validity */
        checkValidity();
    }

    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the id of the field
     * @param pValue the value supplier
     * @param pStorage the field storage type
     */
    MetisDataEosField(final MetisDataEosFieldSet<T> pAnchor,
                      final MetisFieldId pId,
                      final Function<T, Object> pValue,
                      final MetisDataFieldStorage pStorage) {
        /* Store parameters */
        theAnchor = pAnchor;
        theId = pId;
        theDataType = MetisDataType.OBJECT;
        theMaxLength = MetisDataEosFieldSet.FIELD_NO_MAXLENGTH;
        theEquality = MetisDataFieldEquality.DERIVED;
        theStorage = pStorage;
        theValue = pValue;
        theIndex = null;

        /* Check Validity */
        checkValidity();
    }

    /**
     * Get the index.
     * @return the index
     */
    public Integer getIndex() {
        return theIndex;
    }

    @Override
    public MetisFieldId getFieldId() {
        return theId;
    }

    @Override
    public MetisDataType getDataType() {
        return theDataType;
    }

    @Override
    public Integer getMaxLength() {
        return theMaxLength;
    }

    @Override
    public MetisDataFieldEquality getEquality() {
        return theEquality;
    }

    @Override
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
    public MetisDataEosFieldSet<T> getAnchor() {
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
                if (theMaxLength != MetisDataEosFieldSet.FIELD_NO_MAXLENGTH) {
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
        if (!(pThat instanceof MetisDataEosField)) {
            return false;
        }

        /* Access as MetisDataNewField */
        final MetisDataEosField<?> myThat = (MetisDataEosField<?>) pThat;

        /* Must belong to the same anchor */
        if (!theAnchor.equals(myThat.getAnchor())) {
            return false;
        }

        /* Check the id and index is the same */
        return theIndex == myThat.theIndex
               && theId.equals(myThat.theId);
    }

    @Override
    public int hashCode() {
        return theAnchor.hashCode() * MetisDataEosFieldSet.HASH_PRIME
               + theId.hashCode();
    }

    @Override
    public String toString() {
        return theId.getId();
    }

    @Override
    public Object getFieldValue(final Object pObject) {
        final T myObject = theAnchor.getFieldClass().cast(pObject);
        return isVersioned()
                             ? MetisDataFieldValue.UNKNOWN
                             : theValue.apply(myObject);
    }

    @Override
    public <X> X getFieldValue(final Object pObject,
                               final Class<X> pClazz) {
        final Object myObject = getFieldValue(pObject);
        return pClazz.cast(myObject);
    }
}
