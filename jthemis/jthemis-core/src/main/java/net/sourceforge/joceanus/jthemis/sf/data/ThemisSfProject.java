/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.sf.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SourceForge Project.
 */
public class ThemisSfProject {
    /**
     * The id of the project.
     */
    private final String theId;

    /**
     * The name of the project.
     */
    private final String theName;

    /**
     * The short name of the project.
     */
    private final String theShortName;

    /**
     * The summary of the project.
     */
    private final String theSummary;

    /**
     * The description of the project.
     */
    private final String theDesc;

    /**
     * The URL of the project.
     */
    private final String theURL;

    /**
     * The List of TicketSets.
     */
    private final List<ThemisSfTicketSet> theTicketSets;

    /**
     * Constructor.
     * @param pDetails the project details
     */
    public ThemisSfProject(final JSONObject pDetails) {
        /* Access details */
        theId = pDetails.getString("_id");
        theName = pDetails.getString("name");
        theShortName = pDetails.getString("shortname");
        theSummary = pDetails.getString("summary");
        theDesc = pDetails.getString("short_description");
        theURL = "p/" + theShortName;

        /* Create the list */
        theTicketSets = new ArrayList<>();

        /* Access the tools */
        final JSONArray myTools = pDetails.getJSONArray("tools");
        final int iNumEntries = myTools.length();
        for (int i = 0; i < iNumEntries; i++) {
            /* Only interested in Ticket Tools */
            final JSONObject myTool = myTools.getJSONObject(i);
            final String myType = myTool.getString("name");
            if ("tickets".equals(myType)) {
                /* Add the ticketSet */
                theTicketSets.add(new ThemisSfTicketSet(this, myTool));
            }
        }
    }

    /**
     * Obtain the id of the project.
     * @return the id
     */
    public String getId() {
        return theId;
    }

    /**
     * Obtain the name of the project.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the shortName of the project.
     * @return the name
     */
    public String getShortName() {
        return theShortName;
    }

    /**
     * Obtain the summary of the project.
     * @return the summary
     */
    public String getSummary() {
        return theSummary;
    }

    /**
     * Obtain the description of the project.
     * @return the name
     */
    public String getDescription() {
        return theDesc;
    }

    /**
     * Discover details of the project.
     * @param pClient the client
     * @throws OceanusException on error
     */
    public void discoverDetails(final ThemisHTTPSfClient pClient) throws OceanusException {
        /* Loop through the ticketSets */
        for (ThemisSfTicketSet mySet : theTicketSets) {
            mySet.discoverDetails(pClient);
        }
    }

    /**
     * Obtain the URL of the project.
     * @return the URL
     */
    public String getURL() {
        return theURL;
    }

    /**
     * Obtain the ticketSet iterator.
     * @return the iterator
     */
    public Iterator<ThemisSfTicketSet> ticketSetIterator() {
        return theTicketSets.iterator();
    }

    /**
     * Obtain the named ticketSet.
     * @param pName the ticketSet name
     * @return the ticketSet
     */
    public ThemisSfTicketSet getTicketSet(final String pName) {
        /* Loop through the ticketSets */
        for (ThemisSfTicketSet mySet : theTicketSets) {
            if (pName.endsWith(mySet.getName())) {
                return mySet;
            }
        }
        return null;
    }

    /**
     * Does this project have multiple ticketSets?
     * @return true/false
     */
    public boolean hasMultiTicketSets() {
        return theTicketSets.size() > 1;
    }
}
