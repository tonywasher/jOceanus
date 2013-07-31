/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.data;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * Group class for data item.
 * @param <T> Item comprising group
 */
public abstract class DataGroup<T extends DataItem & Comparable<? super T>>
        extends OrderedIdList<Integer, T>
        implements JDataContents {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(DataGroup.class.getSimpleName());

    /**
     * Parent field id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareLocalField("Parent");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_PARENT.equals(pField)) {
            return theParent;
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName()
               + "("
               + size()
               + ")";
    }

    /**
     * Parent Event.
     */
    private final T theParent;

    /**
     * Obtain parent.
     * @return the parent
     */
    public T getParent() {
        return theParent;
    }

    /**
     * Constructor.
     * @param pParent the parent.
     * @param pClass the class
     */
    public DataGroup(final T pParent,
                     final Class<T> pClass) {
        /* Call super-constructor */
        super(pClass);

        /* Store parameter */
        theParent = pParent;

        /* Add the parent to the list */
        add(theParent);
    }

    /**
     * Register a child to the group.
     * @param pChild the child event
     */
    public void registerChild(final T pChild) {
        /* Add the child to the list */
        add(pChild);
    }
}
