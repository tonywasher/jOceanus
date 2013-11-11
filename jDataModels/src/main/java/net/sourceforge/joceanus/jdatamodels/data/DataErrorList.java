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
package net.sourceforge.joceanus.jdatamodels.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;

/**
 * Report-able object list.
 * @param <T> the object type
 */
public class DataErrorList<T extends JDataContents>
        extends ArrayList<T>
        implements JDataContents {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6574043212647066938L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataErrorList.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Size Field Id.
     */
    public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataSize"));

    @Override
    public JDataFields getDataFields() {
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
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_SIZE.equals(pField)) {
            return size();
        }
        return JDataFieldValue.UNKNOWN;
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
