/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.metis.field;

import java.util.Objects;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Metis Data Versioned Field.
 * @param <T> the data type
 */
public class MetisFieldVersioned<T extends MetisFieldVersionedItem>
        extends MetisField<T>
        implements MetisFieldVersionedDef {
    /**
     * Index of value.
     */
    private final Integer theIndex;

    /**
     * The field equality type.
     */
    private final boolean isEquality;

    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the field equality type
     */
    protected MetisFieldVersioned(final MetisFieldVersionedSet<T> pAnchor,
                                  final MetisDataFieldId pId,
                                  final MetisDataType pDataType,
                                  final Integer pMaxLength,
                                  final boolean pEquality) {
        /* initialise underlying class */
        super(pAnchor, pId, pDataType, pMaxLength);

        /* Store equality indication */
        isEquality = pEquality;

        /* Allocate value index */
        theIndex = pAnchor.getNextIndex();
    }

    @Override
    public MetisFieldVersionedSet<T> getAnchor() {
        return (MetisFieldVersionedSet<T>) super.getAnchor();
    }

    @Override
    public Integer getIndex() {
        return theIndex;
    }

    @Override
    public boolean isEquality() {
        return isEquality;
    }

    @Override
    public boolean isCalculated() {
        return false;
    }

    @Override
    public Object getFieldValue(final Object pObject) {
        final T myObject = getAnchor().getFieldClass().cast(pObject);
        final MetisFieldVersionValues myValues = myObject.getValues();
        return myValues.getValue(this);
    }

    @Override
    public void setFieldValue(final Object pObject,
                              final Object pValue) throws OceanusException {
        final T myObject = getAnchor().getFieldClass().cast(pObject);
        final MetisFieldVersionValues myValues = myObject.getValues();
        myValues.setValue(this, pValue);
    }

    @Override
    public void setFieldUncheckedValue(final Object pObject,
                                       final Object pValue) {
        final T myObject = getAnchor().getFieldClass().cast(pObject);
        final MetisFieldVersionValues myValues = myObject.getValues();
        myValues.setUncheckedValue(this, pValue);
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
        if (!(pThat instanceof MetisFieldVersioned<?> myThat)) {
            return false;
        }

        /* Check index and equality */
        return theIndex.equals(myThat.getIndex())
                && isEquality != myThat.isEquality()
                && super.equals(pThat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theIndex, super.hashCode(), isEquality);
    }
}
