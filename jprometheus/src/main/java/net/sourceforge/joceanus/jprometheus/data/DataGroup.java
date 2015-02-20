/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.data;

import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;

/**
 * Group class for data item.
 * @param <T> Item comprising group
 * @param <E> the data type enum class
 */
public abstract class DataGroup<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends OrderedIdList<Integer, T>
        implements JDataContents {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(PrometheusDataResource.DATAGROUP_NAME.getValue());

    /**
     * Parent field id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAGROUP_PARENT.getValue());

    /**
     * Parent Event.
     */
    private final T theParent;

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
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_PARENT.equals(pField)) {
            return theParent;
        }
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName() + "(" + size() + ")";
    }

    /**
     * Obtain parent.
     * @return the parent
     */
    public T getParent() {
        return theParent;
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
