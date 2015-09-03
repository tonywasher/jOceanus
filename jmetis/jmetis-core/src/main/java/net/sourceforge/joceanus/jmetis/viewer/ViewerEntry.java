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
package net.sourceforge.joceanus.jmetis.viewer;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataValues;
import net.sourceforge.joceanus.jmetis.data.ValueSet;

/**
 * Data Manager.
 * @author Tony Washer
 */
public abstract class ViewerEntry {
    /**
     * The name of the entry.
     */
    private final String theName;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The object for the entry.
     */
    private Object theObject = null;

    /**
     * The ViewerManager.
     */
    private final ViewerManager theManager;

    /**
     * Constructor.
     * @param pManager the viewer manager
     * @param pName the entry name
     * @param pId the entry id
     */
    protected ViewerEntry(final ViewerManager pManager,
                          final String pName,
                          final Integer pId) {
        /* Store parameters */
        theManager = pManager;
        theName = pName;
        theId = pId;
    }

    /**
     * Get name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the index.
     * @return the index
     */
    public Integer getId() {
        return theId;
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
    protected ViewerManager getManager() {
        return theManager;
    }

    /**
     * Add as a child into the tree.
     * @param pParent the parent object
     */
    public abstract void addAsChildOf(final ViewerEntry pParent);

    /**
     * Add as a child into the tree.
     * @param pParent the parent object
     */
    public abstract void addAsFirstChildOf(final ViewerEntry pParent);

    /**
     * Add as a root child into the tree.
     */
    public abstract void addAsRootChild();

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Set Focus onto this entry.
     */
    public void setFocus() {
        /* Record the focus */
        theManager.setFocus(this);
    }

    /**
     * Set Focus onto a child of this entry.
     * @param pName the name of the child
     */
    public abstract void setFocus(final String pName);

    /**
     * Hide the entry.
     */
    public abstract void hideEntry();

    /**
     * Ensure that the entry is visible.
     */
    public abstract void showEntry();

    /**
     * Ensure that the entry is visible.
     */
    public abstract void showPrimeEntry();

    /**
     * Set the object referred to by the entry.
     * @param pObject the new object
     */
    public void setObject(final Object pObject) {
        /* Set the new object */
        theObject = pObject;

        /* Remove all the children */
        removeChildren();

        /* If we have contents */
        if (JDataContents.class.isInstance(pObject)) {
            /* Access the object */
            JDataContents myContent = (JDataContents) pObject;
            Object myValue;
            ValueSet myValues = null;

            /* Access valueSet if it exists */
            if (JDataValues.class.isInstance(pObject)) {
                myValues = ((JDataValues) pObject).getValueSet();
            }

            /* Loop through the data fields */
            JDataFields myFields = myContent.getDataFields();
            Iterator<JDataField> myIterator = myFields.fieldIterator();
            while (myIterator.hasNext()) {
                JDataField myField = myIterator.next();

                /* Access the value */
                if ((myField.isValueSetField())
                    && (myValues != null)) {
                    myValue = myValues.getValue(myField);
                } else {
                    myValue = myContent.getFieldValue(myField);
                }

                /* If the field is a List that has contents */
                if ((myValue instanceof List)
                    && (myValue instanceof JDataContents)) {
                    /* Access as list */
                    List<?> myList = (List<?>) myValue;

                    /* If the list is not empty */
                    if (!myList.isEmpty()) {
                        /* Add as a child */
                        ViewerEntry myEntry = theManager.newEntry(myField.getName());
                        myEntry.addAsChildOf(this);
                        myEntry.setObject(myValue);
                    }
                }
            }
        }
    }

    /**
     * Note that the object has been changed.
     */
    public abstract void setChanged();

    /**
     * Remove children of an object.
     */
    public abstract void removeChildren();
}
