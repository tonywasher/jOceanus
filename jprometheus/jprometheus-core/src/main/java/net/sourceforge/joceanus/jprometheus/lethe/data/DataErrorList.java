/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerErrorList;

/**
 * Report-able error list.
 * @param <E> the data type enum class
 */
public class DataErrorList<E extends Enum<E>>
        implements MetisDataFieldItem, MetisDataList<DataItem<E>> {
    /**
     * Local Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisViewerErrorList.class);

    /**
     * Size Field Id.
     */
    private static final MetisDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE.getValue());

    /**
     * The list.
     */
    private final List<DataItem<E>> theList;

    /**
     * Constructor.
     */
    public DataErrorList() {
        theList = new ArrayList<>();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return getDataFieldSet().getName()
               + "("
               + size()
               + ")";
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_SIZE.equals(pField)) {
            return size();
        }
        return MetisDataFieldValue.UNKNOWN;
    }

    /**
     * Add elements.
     * @param pValues the list of values to add
     */
    public void addList(final DataErrorList<E> pValues) {
        /* Loop through the new values */
        final Iterator<DataItem<E>> myIterator = pValues.iterator();
        while (myIterator.hasNext()) {
            /* Add the value */
            theList.add(myIterator.next());
        }
    }

    @Override
    public List<DataItem<E>> getUnderlyingList() {
        return theList;
    }

    /// **
    // * Obtain the first error
    // * @return the first error (or null)
    // */
    // public DataIteMetisViewerExceptionWrapper getFirst() {
    // return isEmpty()
    // ? null
    // : theList.get(0);
    // }
}
