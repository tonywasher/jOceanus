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

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldEquality;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldStorage;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisFieldId;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosVersionedFieldDef;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Metis Data Versioned Field.
 * @param <T> the data type
 */
public class MetisDataEosVersionedField<T extends MetisDataEosVersionedItem>
        extends MetisDataEosField<T>
        implements MetisDataEosVersionedFieldDef {
    /**
     * Constructor.
     * @param pAnchor the anchor
     * @param pId the fieldId
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the field equality type
     * @param pStorage the field storage type
     */
    protected MetisDataEosVersionedField(final MetisDataEosVersionedFieldSet<T> pAnchor,
                                         final MetisFieldId pId,
                                         final MetisDataType pDataType,
                                         final Integer pMaxLength,
                                         final MetisDataFieldEquality pEquality,
                                         final MetisDataFieldStorage pStorage) {
        /* initialise underlying class */
        super(pAnchor, pId, pDataType, pMaxLength, pEquality, pStorage);
    }

    @Override
    public MetisDataEosVersionedFieldSet<T> getAnchor() {
        return (MetisDataEosVersionedFieldSet<T>) super.getAnchor();
    }

    @Override
    public Object getFieldValue(final Object pObject) {
        final T myObject = getAnchor().getFieldClass().cast(pObject);
        final MetisDataEosVersionValues myValues = myObject.getValueSet();
        return myValues.getValue(this);
    }

    @Override
    public void setFieldValue(final Object pObject,
                              final Object pValue) throws OceanusException {
        final T myObject = getAnchor().getFieldClass().cast(pObject);
        final MetisDataEosVersionValues myValues = myObject.getValueSet();
        myValues.setValue(this, pValue);
    }

    @Override
    public void setFieldUncheckedValue(final Object pObject,
                                       final Object pValue) {
        final T myObject = getAnchor().getFieldClass().cast(pObject);
        final MetisDataEosVersionValues myValues = myObject.getValueSet();
        myValues.setUncheckedValue(this, pValue);
    }
}
