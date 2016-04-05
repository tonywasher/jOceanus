/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.newviewer;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Data Viewer Entry.
 * @param <T> the item type
 * @param <N> the Node type
 * @param <I> the Icon type
 */
public abstract class MetisViewerEntry<T extends MetisViewerEntry<T, N, I>, N, I> {
    /**
     * The name of the entry.
     */
    private final String theName;

    /**
     * The object for the entry.
     */
    private Object theObject;

    /**
     * The ViewerManager.
     */
    private final MetisViewerManager<T, N, I> theManager;

    /**
     * Constructor.
     * @param pManager the viewer manager
     * @param pName the entry name
     */
    protected MetisViewerEntry(final MetisViewerManager<T, N, I> pManager,
                               final String pName) {
        /* Store parameters */
        theManager = pManager;
        theName = pName;
    }

    /**
     * Get name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Get object.
     * @return the object
     */
    public Object getObject() {
        return theObject;
    }

    /**
     * Get viewer manager.
     * @return the manager
     */
    protected MetisViewerManager<T, N, I> getManager() {
        return theManager;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Set Focus onto this entry.
     */
    public abstract void setFocus();

    /**
     * Hide the entry.
     */
    public abstract void hideEntry();

    /**
     * Ensure that the entry is visible.
     */
    public abstract void showEntry();

    /**
     * Set the object referred to by the entry.
     * @param pObject the new object
     * @throws OceanusException on error
     */
    public void setObject(final Object pObject) throws OceanusException {
        /* Set the new object */
        theObject = pObject;

        /* Remove all the children */
        removeChildren();

        /* If we have contents */
        if (MetisDataContents.class.isInstance(pObject)) {
            /* Access the object */
            MetisDataContents myContent = (MetisDataContents) pObject;
            Object myValue;
            MetisValueSet myValues = null;

            /* Access valueSet if it exists */
            if (MetisDataValues.class.isInstance(pObject)) {
                myValues = ((MetisDataValues) pObject).getValueSet();
            }

            /* Loop through the data fields */
            MetisFields myFields = myContent.getDataFields();
            Iterator<MetisField> myIterator = myFields.fieldIterator();
            while (myIterator.hasNext()) {
                MetisField myField = myIterator.next();

                /* Access the value */
                if ((myField.isValueSetField())
                    && (myValues != null)) {
                    myValue = myValues.getValue(myField);
                } else {
                    myValue = myContent.getFieldValue(myField);
                }

                /* If the field is a List that has contents */
                if ((myValue instanceof List)
                    && (myValue instanceof MetisDataContents)) {
                    /* Access as list */
                    List<?> myList = (List<?>) myValue;

                    /* If the list is not empty */
                    if (!myList.isEmpty()) {
                        /* Add as a child */
                        T myEntry = theManager.newEntry(this, myField.getName());
                        myEntry.setObject(myValue);
                    }
                }
            }
        }
    }

    /**
     * Remove children of the entry.
     */
    public abstract void removeChildren();
}
