/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataResource;

/**
 * Report-able object list.
 * @param <T> the object type
 */
public class MetisViewerErrorList<T extends Throwable & MetisDataFieldItem>
        implements MetisDataFieldItem, MetisDataList<T> {
    /**
     * Local Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisDataResource.ERRORLIST_NAME.getValue());

    /**
     * Size Field Id.
     */
    private static final MetisDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE.getValue());

    /**
     * The list.
     */
    private final List<T> theList;

    /**
     * Constructor.
     */
    public MetisViewerErrorList() {
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
    public void addList(final MetisViewerErrorList<T> pValues) {
        /* Loop through the new values */
        Iterator<T> myIterator = pValues.iterator();
        while (myIterator.hasNext()) {
            /* Add the value */
            theList.add(myIterator.next());
        }
    }

    /**
     * Obtain an iterator.
     * @return the iterator
     */
    public Iterator<T> iterator() {
        return theList.iterator();
    }

    @Override
    public List<T> getUnderlyingList() {
        return theList;
    }
}
