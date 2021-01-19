/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2021 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.field;

import java.util.function.Function;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;

/**
 * Metis Data Field.
 * @param <T> the data type
 */
public class MetisField<T extends MetisFieldItem>
        implements MetisFieldDef {
    /**
     * Anchor.
     */
    private final MetisFieldSet<T> theAnchor;

    /**
     * Id of field.
     */
    private final MetisDataFieldId theId;

    /**
     * DataType of field.
     */
    private final MetisDataType theDataType;

    /**
     * Maximum Length of field.
     */
    private final Integer theMaxLength;

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
     */
    MetisField(final MetisFieldSet<T> pAnchor,
               final MetisDataFieldId pId,
               final MetisDataType pDataType,
               final Integer pMaxLength) {
        /* Store parameters */
        theAnchor = pAnchor;
        theId = pId;
        theDataType = pDataType;
        theMaxLength = pMaxLength;
        theValue = null;

        /* Check Validity */
        checkValidity();
    }

    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the id of the field
     */
    MetisField(final MetisFieldSet<T> pAnchor,
               final MetisDataFieldId pId) {
        this(pAnchor, pId, null);
    }

    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the id of the field
     * @param pValue the value supplier
     */
    MetisField(final MetisFieldSet<T> pAnchor,
               final MetisDataFieldId pId,
               final Function<T, Object> pValue) {
        /* Store parameters */
        theAnchor = pAnchor;
        theId = pId;
        theDataType = MetisDataType.OBJECT;
        theMaxLength = MetisFieldSet.FIELD_NO_MAXLENGTH;
        theValue = pValue;

        /* Check Validity */
        checkValidity();
    }

    @Override
    public MetisDataFieldId getFieldId() {
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

    /**
     * Obtain the anchor for the field.
     * @return the anchor
     */
    public MetisFieldSet<T> getAnchor() {
        return theAnchor;
    }

    @Override
    public boolean isCalculated() {
        return theValue == null;
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
                if (!theMaxLength.equals(MetisFieldSet.FIELD_NO_MAXLENGTH)) {
                    throw new IllegalArgumentException("Length allowed only for String/Array");
                }
                break;
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
        if (!(pThat instanceof MetisField)) {
            return false;
        }

        /* Access as MetisDataNewField */
        final MetisField<?> myThat = (MetisField<?>) pThat;

        /* Must belong to the same anchor */
        if (!theAnchor.equals(myThat.getAnchor())) {
            return false;
        }

        /* Check the id  is the same */
        return theId.equals(myThat.theId);
    }

    @Override
    public int hashCode() {
        return theAnchor.hashCode() * MetisFieldSet.HASH_PRIME
               + theId.hashCode();
    }

    @Override
    public String toString() {
        return theId.getId();
    }

    @Override
    public Object getFieldValue(final Object pObject) {
        final T myObject = theAnchor.getFieldClass().cast(pObject);
        return theValue.apply(myObject);
    }

    @Override
    public <X> X getFieldValue(final Object pObject,
                               final Class<X> pClazz) {
        final Object myObject = getFieldValue(pObject);
        return pClazz.cast(myObject);
    }
}
