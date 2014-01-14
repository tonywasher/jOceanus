/*******************************************************************************
 * jDataManager: Java Data Manager
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
package net.sourceforge.joceanus.jdatamanager;

import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataDifference;

/**
 * Debug Detail class that holds details of links to other objects.
 * @author Tony Washer
 */
public class JDataDetail {
    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * anchor link.
     */
    private static final String HTML_LINK = "<th><a href=\"#";

    /**
     * Forward link.
     */
    private static final String LINK_FORWARD = "Forward";

    /**
     * Backward link.
     */
    private static final String LINK_BACKWARD = "Backward";

    /**
     * Next Id.
     */
    private int theNextId = 0;

    /**
     * List index.
     */
    private final int theIndex;

    /**
     * Is this data a list?
     */
    private final boolean isList;

    /**
     * Is this data a list child?
     */
    private final boolean isChild;

    /**
     * the list item.
     */
    private final List<?> theList;

    /**
     * the partner detail.
     */
    private JDataDetail thePartnerDetail;

    /**
     * The links.
     */
    private JDataLink theLinks = null;

    /**
     * The forward link.
     */
    private JDataDetail theForward = null;

    /**
     * The backwards link.
     */
    private JDataDetail theBackward = null;

    /**
     * The builder.
     */
    private final StringBuilder theBuilder;

    /**
     * HTML formatter.
     */
    private JDataHTML theFormatter = null;

    /**
     * The iterator.
     */
    private final ListIterator<?> theIterator;

    /**
     * Get the Data Detail.
     * @return the Detail
     */
    protected StringBuilder getJDataDetail() {
        return theBuilder;
    }

    /**
     * Get the Partner Detail.
     * @return the Partner Detail
     */
    protected JDataDetail getPartnerDetail() {
        return thePartnerDetail;
    }

    /**
     * Obtain the index.
     * @return the Index
     */
    protected int getIndex() {
        return theIndex;
    }

    /**
     * Is the entry a list.
     * @return true/false
     */
    protected final boolean isList() {
        return isList
               && !theList.isEmpty();
    }

    /**
     * Is the entry a child.
     * @return true/false
     */
    protected boolean isChild() {
        return isChild;
    }

    /**
     * Get the list.
     * @return the list
     */
    protected List<?> getList() {
        return theList;
    }

    /**
     * Set forward link.
     * @param pLink the forward link
     * @return the forward link
     */
    private JDataDetail setForwardLink(final JDataDetail pLink) {
        /* Pass call on if we are a child */
        if (isChild) {
            return thePartnerDetail.setForwardLink(pLink);
        }

        /* Set forward link and return it */
        theForward = pLink;
        return theForward;
    }

    /**
     * Set backward link.
     * @param pLink the backward link
     */
    private void setBackwardLink(final JDataDetail pLink) {
        /* Pass call on if we are a child */
        if (isChild) {
            thePartnerDetail.setBackwardLink(pLink);
            return;
        }

        /* Set backward link */
        theBackward = pLink;
    }

    /**
     * Constructor for standard object.
     * @param pFormatter the formatter
     * @param pObject the object represented
     */
    protected JDataDetail(final JDataHTML pFormatter,
                          final Object pObject) {
        /* Clear child fields */
        thePartnerDetail = null;
        isChild = false;
        theIndex = -1;

        /* Record the formatter */
        theFormatter = pFormatter;

        /* Obtain the detail for this object */
        if (pObject != null) {
            theBuilder = theFormatter.formatHTMLObject(this, pObject);
            isList = (pObject instanceof List);
        } else {
            theBuilder = null;
            isList = false;
        }

        /* Allocate iterator if needed */
        theList = (isList)
                ? (List<?>) pObject
                : null;
        theIterator = (isList())
                ? theList.listIterator()
                : null;
        thePartnerDetail = (isList())
                ? new JDataDetail(this, theIterator.next())
                : null;
    }

    /**
     * Constructor for child object.
     * @param pList the list to which the item belongs
     * @param pObject the object represented
     */
    protected JDataDetail(final JDataDetail pList,
                          final Object pObject) {
        /* Obtain the detail for this object */
        thePartnerDetail = pList;
        theFormatter = pList.theFormatter;
        theLinks = pList.theLinks;
        theList = pList.theList;
        theIndex = theList.indexOf(pObject);
        theIterator = null;
        isChild = true;
        theBuilder = theFormatter.formatHTMLObject(this, pObject);
        isList = true;
    }

    /**
     * Shift the iterator a number of steps.
     * @param iNumSteps the number of steps to shift (positive or negative)
     */
    protected void shiftIterator(final int iNumSteps) {
        Object myNext = null;
        int myNumSteps = iNumSteps;

        /* Ignore if we are a child */
        if (isChild) {
            return;
        }

        /* If we are stepping forwards */
        if (myNumSteps > 0) {
            /* Loop through the steps */
            while ((myNumSteps-- > 0)
                   && (theIterator.hasNext())) {
                /* Shift to next element */
                myNext = theIterator.next();
            }

            /* Build the new partner detail */
            thePartnerDetail = new JDataDetail(this, myNext);

            /* else we are stepping backwards */
        } else if (myNumSteps < 0) {
            /* Shift back one step */
            theIterator.previous();

            /* Loop through the steps */
            while ((myNumSteps++ < 0)
                   && (theIterator.hasPrevious())) {
                /* Shift to previous element */
                myNext = theIterator.previous();
            }

            /* Shift forward one step */
            theIterator.next();

            /* Build the new partner detail */
            thePartnerDetail = new JDataDetail(this, myNext);
        }
    }

    /**
     * Obtain History Links.
     * @return the formatted links
     */
    public StringBuilder getHistoryLinks() {
        /* If we are a child */
        if (isChild) {
            /* Pass call on to parent */
            return thePartnerDetail.getHistoryLinks();
        }

        /* Ignore if no history */
        if ((theBackward == null)
            && (theForward == null)) {
            return null;
        }

        /* Create the StringBuilder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the links */
        myBuilder.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myBuilder.append("<thead><th>Links</th>");

        /* Handle Backward Link */
        if (theBackward != null) {
            myBuilder.append(HTML_LINK);
            myBuilder.append(LINK_BACKWARD);
            myBuilder.append("\">Backwards</a></th>");
        }

        /* Handle Forward Link */
        if (theForward != null) {
            myBuilder.append(HTML_LINK);
            myBuilder.append(LINK_FORWARD);
            myBuilder.append("\">Forwards</a></th>");
        }

        /* Return the details */
        myBuilder.append("</thead></table>");
        return myBuilder;
    }

    /**
     * Obtain Data Link.
     * @param pName the Name
     * @return the corresponding object
     */
    protected JDataDetail getDataLink(final String pName) {
        JDataLink myLink = theLinks;
        String myName = pName;

        /* Shift over # */
        if (myName.startsWith("#")) {
            myName = myName.substring(1);
        }

        /* Handle forward/backward links */
        if (myName.equals(LINK_FORWARD)) {
            return (isChild)
                    ? thePartnerDetail.theForward
                    : theForward;
        }
        if (myName.equals(LINK_BACKWARD)) {
            return (isChild)
                    ? thePartnerDetail.theBackward
                    : theBackward;
        }

        /* Loop through the links */
        while (myLink != null) {
            /* If we have the link, break the loop */
            if (myName.compareTo(myLink.theName) == 0) {
                break;
            }

            /* Move to next link */
            myLink = myLink.theNext;
        }

        /* If we have a forward link */
        if (myLink != null) {
            /* Record and return the object */
            JDataDetail myDetail = setForwardLink(new JDataDetail(theFormatter, myLink.theObject));
            myDetail.setBackwardLink(this);
            return myDetail;
        }

        /* Return no link */
        return null;
    }

    /**
     * Format a data link.
     * @param pItem the object for the link
     * @param pText the text for the link
     * @return the data link
     */
    public String addDataLink(final Object pItem,
                              final String pText) {
        /* If the item is a JDataDifference */
        Object myItem = pItem;
        Difference myDifference = Difference.IDENTICAL;
        if (myItem instanceof JDataDifference) {
            /* Access the difference */
            JDataDifference myDiffer = (JDataDifference) pItem;
            myDifference = myDiffer.getDifference();
            myItem = myDiffer.getObject();
        }

        /* Return text if item is null */
        if (myItem == null) {
            return JDataHTML.formatHTMLChange(pText, myDifference);
        }

        /* Allocate the string builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Create the Debug Link */
        JDataLink myLink = new JDataLink(this, myItem);

        /* Add the link into the buffer */
        myBuilder.append("<a href=\"#");
        myBuilder.append(myLink.theName);
        myBuilder.append("\"");

        /* If we have a difference */
        if (myDifference.isDifferent()) {
            /* Add class details */
            myBuilder.append(" class=\"");
            myBuilder.append(JDataHTML.CLASS_CHANGED);
            myBuilder.append("\"");
        }
        myBuilder.append(">");

        /* Add the text into the buffer */
        myBuilder.append(pText);

        /* Close link and return */
        myBuilder.append("</a>");
        return myBuilder.toString();
    }

    /**
     * Link Class.
     */
    private static final class JDataLink {
        /**
         * The object itself.
         */
        private final Object theObject;

        /**
         * The name.
         */
        private final String theName;

        /**
         * The next link.
         */
        private final JDataLink theNext;

        /**
         * Create standard object link.
         * @param pDetail the detail
         * @param pObject the object linked to
         */
        private JDataLink(final JDataDetail pDetail,
                          final Object pObject) {
            /* Store object */
            theObject = pObject;

            /* Assign name */
            theName = "Object"
                      + ++pDetail.theNextId;

            /* Add to the links */
            theNext = pDetail.theLinks;
            pDetail.theLinks = this;
        }
    }
}
