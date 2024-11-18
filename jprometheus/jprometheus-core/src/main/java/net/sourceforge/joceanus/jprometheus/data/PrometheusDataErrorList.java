/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Report-able error list.
 */
public class PrometheusDataErrorList
        implements MetisFieldItem, MetisDataList<PrometheusDataItem> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusDataErrorList> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusDataErrorList.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, PrometheusDataErrorList::size);
    }

    /**
     * The list.
     */
    private final List<PrometheusDataItem> theList;

    /**
     * Constructor.
     */
    public PrometheusDataErrorList() {
        theList = new ArrayList<>();
    }

    @Override
    public MetisFieldSet<PrometheusDataErrorList> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return getDataFieldSet().getName()
                + "("
                + size()
                + ")";
    }

    /**
     * Add elements.
     * @param pValues the list of values to add
     */
    public void addList(final PrometheusDataErrorList pValues) {
        /* Loop through the new values */
        final Iterator<PrometheusDataItem> myIterator = pValues.iterator();
        while (myIterator.hasNext()) {
            /* Add the value */
            theList.add(myIterator.next());
        }
    }

    @Override
    public List<PrometheusDataItem> getUnderlyingList() {
        return theList;
    }
}
