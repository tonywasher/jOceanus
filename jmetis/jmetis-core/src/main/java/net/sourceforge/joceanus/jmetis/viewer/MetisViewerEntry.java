/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;

/**
 * Data Viewer Entry.
 */
public class MetisViewerEntry {
    /**
     * Entry name prefix.
     */
    private static final String ENTRY_PREFIX = "TreeItem";

    /**
     * The ViewerManager.
     */
    private final MetisViewerManager theManager;

    /**
     * The Parent.
     */
    private final MetisViewerEntry theParent;

    /**
     * The id of the entry.
     */
    private final int theId;

    /**
     * The Child List.
     */
    private List<MetisViewerEntry> theChildList;

    /**
     * The unique name of the entry.
     */
    private final String theUniqueName;

    /**
     * The name of the entry.
     */
    private final String theDisplayName;

    /**
     * The object for the entry.
     */
    private Object theObject;

    /**
     * Is the entry visible?.
     */
    private boolean isVisible;

    /**
     * Constructor.
     * @param pManager the viewer manager
     * @param pParent the parent entry
     * @param pName the entry display name
     */
    protected MetisViewerEntry(final MetisViewerManager pManager,
                               final MetisViewerEntry pParent,
                               final String pName) {
        /* Store parameters */
        theManager = pManager;
        theParent = pParent;
        theDisplayName = pName;

        /* Obtain entry id. */
        theId = pManager.getNextId();

        /* Determine unique name */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(ENTRY_PREFIX);
        myBuilder.append(theId);
        theUniqueName = myBuilder.toString();

        /* Initialise to visible */
        isVisible = true;

        /* If we have a parent */
        if (pParent != null) {
            /* Add the entry to the child list */
            pParent.addChild(this);
        }
    }

    /**
     * Get viewer manager.
     * @return the manager
     */
    protected MetisViewerManager getManager() {
        return theManager;
    }

    /**
     * Get parent.
     * @return the parent
     */
    protected MetisViewerEntry getParent() {
        return theParent;
    }

    /**
     * Get unique name.
     * @return the name
     */
    public String getUniqueName() {
        return theUniqueName;
    }

    /**
     * Get display name.
     * @return the name
     */
    public String getDisplayName() {
        return theDisplayName;
    }

    /**
     * Get id.
     * @return the id
     */
    public int getId() {
        return theId;
    }

    /**
     * Get object.
     * @return the object
     */
    public Object getObject() {
        return theObject;
    }

    @Override
    public String toString() {
        return theDisplayName;
    }

    /**
     * Get child iterator.
     * @return the iterator
     */
    public Iterator<MetisViewerEntry> childIterator() {
        return theChildList == null
                                    ? Collections.emptyIterator()
                                    : theChildList.iterator();
    }

    /**
     * Add child.
     * @param pChild the child to add
     */
    private void addChild(final MetisViewerEntry pChild) {
        if (theChildList == null) {
            theChildList = new ArrayList<>();
        }
        theChildList.add(pChild);
    }

    /**
     * Is the entry visible?.
     * @return true/false
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Set entry visibility.
     * @param pVisible true/false
     */
    public void setVisible(final boolean pVisible) {
        /* If this is a change in status */
        if (isVisible != pVisible) {
            /* Change status and report it */
            isVisible = pVisible;
            theManager.fireEvent(MetisViewerEvent.VISIBILITY, this);
        }
    }

    /**
     * Set Focus onto this entry.
     */
    public void setFocus() {
        theManager.setFocus(this);
    }

    /**
     * Set Focus onto child of this entry.
     * @param pName the name of the child
     */
    public void setFocus(final String pName) {
        /* Loop through the children */
        final Iterator<MetisViewerEntry> myIterator = theChildList.iterator();
        while (myIterator.hasNext()) {
            final MetisViewerEntry myEntry = myIterator.next();

            /* If we match the object */
            if (pName.equals(myEntry.getDisplayName())) {
                /* Set the focus and return */
                myEntry.setFocus();
                return;
            }
        }
    }

    /**
     * Set the object referred to by the entry.
     * @param pObject the new object
     */
    public void setObject(final Object pObject) {
        /* Set the new object */
        setTheObject(pObject);

        /* Notify regarding the data change */
        theManager.fireEvent(MetisViewerEvent.VALUE, this);
    }

    /**
     * Set the tree object referred to by the entry.
     * @param pObject the new object
     */
    public void setTreeObject(final Object pObject) {
        /* Set the new object */
        setTheObject(pObject);

        /* Create child elements if required */
        if (pObject instanceof MetisFieldItem) {
            createChildElements((MetisFieldItem) pObject);
        }

        /* Notify regarding the data change */
        theManager.fireEvent(MetisViewerEvent.VALUE, this);
    }

    /**
     * Set the object referred to by the entry.
     * @param pObject the new object
     */
    private void setTheObject(final Object pObject) {
        /* Set the new object */
        theObject = pObject;

        /* Clear the childList */
        if (theChildList != null) {
            theChildList.clear();
        }
    }

    /**
     * Create child elements.
     * @param pItem the item
     */
    private void createChildElements(final MetisFieldItem pItem) {
        /* Loop through the fields */
        final Iterator<MetisFieldDef> myIterator = pItem.getDataFieldSet().fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();

            /* Obtain the value */
            final Object myValue = myField.getFieldValue(pItem);
            boolean addChild = false;

            /* Determine whether to add the child */
            if (myValue instanceof List) {
                addChild = !((List<?>) myValue).isEmpty();
            } else if (myValue instanceof MetisDataList) {
                addChild = !((MetisDataList<?>) myValue).isEmpty();
            } else if (myValue instanceof Map) {
                addChild = !((Map<?, ?>) myValue).isEmpty();
            } else if (myValue instanceof MetisDataMap) {
                addChild = !((MetisDataMap<?, ?>) myValue).isEmpty();
            }

            /* If we should add the child */
            if (addChild) {
                /* Create it */
                final MetisViewerEntry myChild = theManager.newEntry(this, myField.getFieldId().getId());
                myChild.setObject(myValue);
            }
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!(pThat instanceof MetisViewerEntry)) {
            return false;
        }

        /* Access as MetisViewerEntry */
        final MetisViewerEntry myThat = (MetisViewerEntry) pThat;

        /* Must have same id */
        return theId == myThat.theId;
    }

    @Override
    public int hashCode() {
        return theId;
    }
}
