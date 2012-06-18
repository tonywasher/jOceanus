/*******************************************************************************
 * JDataManager: Java Data Manager
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataManager;

import net.sourceforge.JDataManager.JDataObject.JDataContents;

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
    private StringBuilder theBuilder = null;

    /**
     * Get the Debug Detail.
     * @return the Detail
     */
    protected StringBuilder getJDataDetail() {
        return theBuilder;
    }

    /**
     * Constructor.
     * @param pObject the object represented
     */
    protected JDataDetail(final Object pObject) {
        /* Obtain the detail for this object */
        if (pObject != null) {
            theBuilder = JDataObject.formatHTMLObject(this, pObject);
        }
    }

    /**
     * Obtain History Links.
     * @return the formatted links
     */
    public StringBuilder getHistoryLinks() {
        /* Ignore if no history */
        if ((theBackward == null) && (theForward == null)) {
            return null;
        }

        /* Create the StringBuilder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the links */
        myBuilder.append("<table border=\"1\" width=\"90%\" align=\"center\">");
        myBuilder.append("<thead><th>Links</th>");

        /* Handle Backward Link */
        if (theBackward != null) {
            myBuilder.append("<th><a href=\"#");
            myBuilder.append(LINK_BACKWARD);
            myBuilder.append("\">Backwards</a></th>");
        }

        /* Handle Forward Link */
        if (theForward != null) {
            myBuilder.append("<th><a href=\"#");
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
        if (myName.compareTo(LINK_FORWARD) == 0) {
            return theForward;
        }
        if (myName.compareTo(LINK_BACKWARD) == 0) {
            return theBackward;
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
            theForward = new JDataDetail(myLink.theObject);
            theForward.theBackward = this;
            return theForward;
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
        /* Return text if item is null */
        if (pItem == null) {
            return pText;
        }

        /* If the item is a ReportDetail */
        if (pItem instanceof JDataContents) {
            StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Create the Debug Link */
            JDataLink myLink = new JDataLink((JDataContents) pItem);

            /* Add the link into the buffer */
            myBuilder.append("<a href=\"#");
            myBuilder.append(myLink.theName);
            myBuilder.append("\">");

            /* Add the text into the buffer */
            myBuilder.append(pText);

            /* Close link and return */
            myBuilder.append("</a>");
            return myBuilder.toString();
        }

        /* Else just return the text */
        return pText;
    }

    /**
     * Link Class.
     */
    private final class JDataLink {
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
         * @param pObject the object linked to
         */
        private JDataLink(final Object pObject) {
            /* Store object */
            theObject = pObject;

            /* Assign name */
            theName = "Object" + ++theNextId;

            /* Add to the links */
            theNext = theLinks;
            theLinks = this;
        }
    }
}
