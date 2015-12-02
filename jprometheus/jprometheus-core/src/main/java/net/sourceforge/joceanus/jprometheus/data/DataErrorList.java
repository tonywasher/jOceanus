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

import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;

/**
 * Report-able object list.
 * @param <T> the object type
 */
public class DataErrorList<T extends MetisDataContents>
        extends ArrayList<T>
        implements MetisDataContents {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6574043212647066938L;

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResource.ERRORLIST_NAME.getValue());

    /**
     * Size Field Id.
     */
    public static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName()
               + "("
               + size()
               + ")";
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_SIZE.equals(pField)) {
            return size();
        }
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * Add elements.
     * @param pValues the list of values to add
     */
    public void addList(final DataErrorList<T> pValues) {
        /* Loop through the new values */
        Iterator<T> myIterator = pValues.iterator();
        while (myIterator.hasNext()) {
            /* Add the value */
            add(myIterator.next());
        }
    }
}
