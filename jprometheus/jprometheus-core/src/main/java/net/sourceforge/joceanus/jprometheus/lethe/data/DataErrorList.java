/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Report-able error list.
 */
public class DataErrorList
        implements MetisFieldItem, MetisDataList<DataItem> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<DataErrorList> FIELD_DEFS = MetisFieldSet.newFieldSet(DataErrorList.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, DataErrorList::size);
    }

    /**
     * The list.
     */
    private final List<DataItem> theList;

    /**
     * Constructor.
     */
    public DataErrorList() {
        theList = new ArrayList<>();
    }

    @Override
    public MetisFieldSet<DataErrorList> getDataFieldSet() {
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
    public void addList(final DataErrorList pValues) {
        /* Loop through the new values */
        final Iterator<DataItem> myIterator = pValues.iterator();
        while (myIterator.hasNext()) {
            /* Add the value */
            theList.add(myIterator.next());
        }
    }

    @Override
    public List<DataItem> getUnderlyingList() {
        return theList;
    }

    /// **
    // * Obtain the first error
    // * @return the first error (or null)
    // */
    // public DataItem<E> getFirst() {
    // return isEmpty()
    // ? null
    // : theList.get(0);
    // }
}
