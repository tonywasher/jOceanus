/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;

/**
 * Group class for data item.
 * @param <T> Item comprising group
 * @param <E> the data type enum class
 */
public abstract class DataGroup<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>>
        implements MetisFieldItem, MetisDataList<T> {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<DataGroup> FIELD_DEFS = MetisFieldSet.newFieldSet(DataGroup.class);

    /**
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAGROUP_PARENT, DataGroup::getParent);
    }

    /**
     * The list.
     */
    private final List<T> theList;

    /**
     * Parent Event.
     */
    private final T theParent;

    /**
     * Constructor.
     * @param pParent the parent.
     */
    public DataGroup(final T pParent) {
        /* Store parameter */
        theParent = pParent;
        theList = new ArrayList<>();
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return getDataFieldSet().getName() + "(" + size() + ")";
    }

    @Override
    public List<T> getUnderlyingList() {
        return theList;
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
        theList.add(pChild);
    }
}
