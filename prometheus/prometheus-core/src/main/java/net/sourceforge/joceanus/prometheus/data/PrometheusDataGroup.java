/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.data;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Group class for data item.
 * @param <T> Item comprising group
 */
public abstract class PrometheusDataGroup<T extends PrometheusDataItem>
        implements MetisFieldItem, MetisDataList<T> {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<PrometheusDataGroup> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusDataGroup.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAGROUP_PARENT, PrometheusDataGroup::getParent);
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
    protected PrometheusDataGroup(final T pParent) {
        /* Store parameter */
        theParent = pParent;
        theList = new ArrayList<>();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
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
