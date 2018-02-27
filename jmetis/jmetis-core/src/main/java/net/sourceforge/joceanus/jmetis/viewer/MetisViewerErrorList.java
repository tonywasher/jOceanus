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
package net.sourceforge.joceanus.jmetis.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;

/**
 * Report-able error list.
 */
public class MetisViewerErrorList
        implements MetisFieldItem, MetisDataList<MetisViewerExceptionWrapper> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MetisViewerErrorList> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisViewerErrorList.class);

    /**
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisDataResource.LIST_SIZE, MetisViewerErrorList::size);
    }

    /**
     * The list.
     */
    private final List<MetisViewerExceptionWrapper> theList;

    /**
     * Constructor.
     */
    public MetisViewerErrorList() {
        theList = new ArrayList<>();
    }

    @Override
    public MetisFieldSet<MetisViewerErrorList> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return getDataFieldSet().getName();
    }

    /**
     * Add elements.
     * @param pValues the list of values to add
     */
    public void addList(final MetisViewerErrorList pValues) {
        /* Loop through the new values */
        final Iterator<MetisViewerExceptionWrapper> myIterator = pValues.iterator();
        while (myIterator.hasNext()) {
            /* Add the value */
            theList.add(myIterator.next());
        }
    }

    @Override
    public List<MetisViewerExceptionWrapper> getUnderlyingList() {
        return theList;
    }

    /**
     * Obtain the first error.
     * @return the first error (or null)
     */
    public MetisViewerExceptionWrapper getFirst() {
        return isEmpty()
                         ? null
                         : theList.get(0);
    }
}
