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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDelta;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;

/**
 * Data Viewer Page.
 */
public class MetisViewerPage {
    /**
     * The Master entry.
     */
    private final MetisViewerEntry theEntry;

    /**
     * The Parent page.
     */
    private final MetisViewerPage theParent;

    /**
     * The Links.
     */
    private final Map<String, Object> theLinkMap;

    /**
     * The StringBuilder.
     */
    private final StringBuilder theBuilder;

    /**
     * The Object.
     */
    private final Object theObject;

    /**
     * The Size.
     */
    private final int theSize;

    /**
     * The Pages.
     */
    private final int thePages;

    /**
     * The mode.
     */
    private MetisViewerMode theMode;

    /**
     * The Index.
     */
    private int theItemNo;

    /**
     * The Page No.
     */
    private int thePageNo;

    /**
     * The NextId.
     */
    private int theNextId;

    /**
     * The HTML.
     */
    private String theHtml;

    /**
     * Constructor for initial page.
     * @param pEntry the master entry
     */
    protected MetisViewerPage(final MetisViewerEntry pEntry) {
        this(pEntry, null, pEntry.getObject());
    }

    /**
     * Constructor.
     * @param pEntry the master entry
     * @param pParent the parent page
     * @param pData the data
     */
    private MetisViewerPage(final MetisViewerEntry pEntry,
                            final MetisViewerPage pParent,
                            final Object pData) {
        /* Record parameters */
        theEntry = pEntry;
        theParent = pParent;
        theObject = pData;

        /* Create the map and the builder */
        theLinkMap = new HashMap<>();
        theBuilder = new StringBuilder();

        /* Determine the size and pages */
        theSize = determineSize();
        thePages = determinePages();

        /* Determine the initial mode */
        theMode = determineInitialMode();
    }

    /**
     * Obtain the master entry.
     * @return the master entry
     */
    protected MetisViewerEntry getMasterEntry() {
        return theEntry;
    }

    /**
     * Obtain the parent page.
     * @return the parent page
     */
    protected MetisViewerPage getParent() {
        return theParent;
    }

    /**
     * Do we have a parent?
     * @return true/false
     */
    protected boolean hasParent() {
        return theParent != null;
    }

    /**
     * Obtain the object.
     * @return the object
     */
    protected Object getObject() {
        return theObject;
    }

    /**
     * Obtain the mode.
     * @return the mode
     */
    protected MetisViewerMode getMode() {
        return theMode;
    }

    /**
     * Obtain the HTML.
     * @return the HTML
     */
    protected String getHtml() {
        return theHtml;
    }

    /**
     * Set the HTML.
     * @param pHtml the HTML
     */
    protected void setHtml(final String pHtml) {
        theHtml = pHtml;
    }

    /**
     * Determine whether the mode is valid.
     * @param pMode the mode
     * @return true/false
     */
    protected boolean validMode(final MetisViewerMode pMode) {
        switch (pMode) {
            case CONTENTS:
                return hasContents(theObject);
            case SUMMARY:
                return isCollection(theObject);
            case ITEMS:
                return isNonEmptyList(theObject);
            default:
                return false;
        }
    }

    /**
     * Obtain the itemNo.
     * @return the index
     */
    protected int getItemNo() {
        switch (theMode) {
            case SUMMARY:
                return thePageNo + 1;
            case ITEMS:
                return theItemNo + 1;
            default:
                return -1;
        }
    }

    /**
     * Obtain the size.
     * @return the size
     */
    protected int getSize() {
        switch (theMode) {
            case SUMMARY:
                return thePages;
            case ITEMS:
                return theSize;
            default:
                return -1;
        }
    }

    /**
     * Have we got a previous item.
     * @return true/false
     */
    protected boolean hasPrevious() {
        return getItemNo() > 1;
    }

    /**
     * Have we got a next item.
     * @return true/false
     */
    protected boolean hasNext() {
        return getItemNo() < getSize();
    }

    /**
     * Move to previous page.
     */
    protected void previous() {
        if (hasPrevious()) {
            if (MetisViewerMode.ITEMS.equals(theMode)) {
                theItemNo--;
            } else {
                thePageNo--;
            }
        }
    }

    /**
     * Move to next page.
     */
    protected void next() {
        if (hasNext()) {
            if (MetisViewerMode.ITEMS.equals(theMode)) {
                theItemNo++;
            } else {
                thePageNo++;
            }
        }
    }

    /**
     * Set the page.
     * @param pPage the page #
     */
    protected void setPageNo(final int pPage) {
        if ((pPage > 0)
            && (pPage <= getSize())) {
            if (MetisViewerMode.ITEMS.equals(theMode)) {
                theItemNo = pPage - 1;
            } else {
                thePageNo = pPage - 1;
            }
        }
    }

    /**
     * Set the mode.
     * @param pMode the mode
     */
    protected void setMode(final MetisViewerMode pMode) {
        if (validMode(pMode)) {
            theMode = pMode;
        }
    }

    /**
     * Reset the page.
     */
    protected void resetPage() {
        theLinkMap.clear();
        theNextId = 0;
    }

    /**
     * Generate a new link for the page.
     * @param pData the object to link to
     * @return the link name
     */
    protected String newLink(final Object pData) {
        /* Generate the new id */
        theBuilder.setLength(0);
        theBuilder.append("Object");
        theBuilder.append(theNextId++);
        final String myId = theBuilder.toString();

        /* Record the id and return it */
        theLinkMap.put(myId, pData);
        return myId;
    }

    /**
     * Obtain the new page for the link.
     * @param pLink the link id
     * @return the new page
     */
    protected MetisViewerPage newPage(final String pLink) {
        /* Lookup the data */
        final Object myData = theLinkMap.get(pLink);
        return myData == null
                              ? this
                              : new MetisViewerPage(theEntry, this, myData);
    }

    /**
     * Determine the size of a collection.
     * @return the size
     */
    private int determineSize() {
        /* handle DataDifference */
        Object myObject = theObject instanceof MetisDataDelta
                                                              ? ((MetisDataDelta) theObject).getObject()
                                                              : theObject;

        /* handle embedded objects */
        if (myObject instanceof MetisDataList) {
            myObject = ((MetisDataList<?>) myObject).getUnderlyingList();
        }
        if (myObject instanceof MetisDataMap) {
            myObject = ((MetisDataMap<?, ?>) myObject).getUnderlyingMap();
        }

        /* Handle multi-page objects */
        if (myObject instanceof List) {
            return ((List<?>) myObject).size();
        } else if (myObject instanceof Map) {
            return ((Map<?, ?>) myObject).size();
        }
        return -1;
    }

    /**
     * Determine the pages of a collection.
     * @return the pages
     */
    private int determinePages() {
        return theSize == -1
                             ? -1
                             : ((theSize - 1) / MetisViewerFormatter.ITEMS_PER_PAGE) + 1;
    }

    /**
     * Determine the initial Mode.
     * @return the initial mode
     */
    private MetisViewerMode determineInitialMode() {
        /* Try Contents */
        if (validMode(MetisViewerMode.CONTENTS)) {
            return MetisViewerMode.CONTENTS;
        }

        /* Try Summary */
        if (validMode(MetisViewerMode.SUMMARY)) {
            return MetisViewerMode.SUMMARY;
        }

        /* Handle null */
        if (theObject == null) {
            return MetisViewerMode.NULL;
        }

        /* Reject the mode */
        throw new IllegalArgumentException("Invalid object: " + theObject);
    }

    /**
     * Determine whether an object is a collection.
     * @param pObject the object
     * @return true/false
     */
    private static boolean isCollection(final Object pObject) {
        /* handle DataDifference */
        final Object myObject = pObject instanceof MetisDataDelta
                                                                  ? ((MetisDataDelta) pObject).getObject()
                                                                  : pObject;

        /* Handle extended Lists/Maps */
        if (myObject instanceof MetisDataList
            || myObject instanceof MetisDataMap) {
            return true;
        }

        /* Handle multi-page objects */
        return myObject instanceof List
               || myObject instanceof Map;
    }

    /**
     * Determine whether an object is a non-empty list.
     * @param pObject the object
     * @return true/false
     */
    private static boolean isNonEmptyList(final Object pObject) {
        /* handle DataDifference */
        Object myObject = pObject instanceof MetisDataDelta
                                                            ? ((MetisDataDelta) pObject).getObject()
                                                            : pObject;

        /* handle embedded objects */
        if (myObject instanceof MetisDataList) {
            myObject = ((MetisDataList<?>) myObject).getUnderlyingList();
        }

        /* Handle non-empty lists */
        return myObject instanceof List
               && !((List<?>) myObject).isEmpty();
    }

    /**
     * Determine whether an object has contents.
     * @param pObject the object
     * @return true/false
     */
    private static boolean hasContents(final Object pObject) {
        /* Handle null */
        if (pObject == null) {
            return false;
        }

        /* handle DataDifference */
        final Object myObject = pObject instanceof MetisDataDelta
                                                                  ? ((MetisDataDelta) pObject).getObject()
                                                                  : pObject;

        /* Handle structured object */
        if (myObject instanceof MetisDataContents) {
            return true;
        }

        /* Handle structured object */
        if (myObject instanceof MetisFieldItem) {
            return true;
        }

        /* Handle simple objects */
        return myObject instanceof Throwable
               || myObject instanceof StackTraceElement[];
    }

    /**
     * Does the object have multiple modes?
     * @return true/false
     */
    protected boolean hasMultiModes() {
        return hasContents(theObject) && isCollection(theObject);
    }

    /**
     * Determine whether an object is link-able.
     * @param pObject the object
     * @return true/false
     */
    protected static boolean isLinkable(final Object pObject) {
        return hasContents(pObject) || isCollection(pObject);
    }
}
