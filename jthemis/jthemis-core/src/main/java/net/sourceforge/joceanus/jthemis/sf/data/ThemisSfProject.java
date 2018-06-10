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

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisResource;

/**
 * SourceForge Project.
 */
public class ThemisSfProject
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSfProject> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSfProject.class);

    /**
     * Repository field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_OWNER, ThemisSfProject::getOwner);
        FIELD_DEFS.declareLocalField(ThemisResource.PROJECT_NAME, ThemisSfProject::getName);
        FIELD_DEFS.declareLocalField(ThemisResource.PROJECT_SUMMARY, ThemisSfProject::getSummary);
        FIELD_DEFS.declareLocalField(ThemisResource.PROJECT_DESC, ThemisSfProject::getDescription);
        FIELD_DEFS.declareLocalField(ThemisResource.PROJECT_TICKETSETS, ThemisSfProject::getTicketSets);
    }

    /**
     * The owner of the project.
     */
    private final ThemisSfUser theOwner;

    /**
     * The name of the project.
     */
    private final String theName;

    /**
     * The summary of the project.
     */
    private final String theSummary;

    /**
     * The URL of the project.
     */
    private final String theURL;

    /**
     * The description of the project.
     */
    private String theDesc;

    /**
     * The id of the project.
     */
    private String theId;

    /**
     * The List of TicketSets.
     */
    private final List<ThemisSfTicketSet> theTicketSets;

    /**
     * Constructor.
     * @param pOwner the owner
     * @param pDetails the project details
     */
    public ThemisSfProject(final ThemisSfUser pOwner,
                           final JSONObject pDetails) {
        /* Store parameters */
        theOwner = pOwner;

        /* Access details */
        theName = pDetails.getString("name");
        theSummary = pDetails.getString("summary");
        theURL = pDetails.getString("url");

        /* Create the list */
        theTicketSets = new ArrayList<>();
    }

    @Override
    public MetisFieldSet<ThemisSfProject> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the owner of the project.
     * @return the id
     */
    public ThemisSfUser getOwner() {
        return theOwner;
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
    public void discoverDetails(final ThemisSfClient pClient) throws OceanusException {
        /* Access the ticket */
        final JSONObject myDetails = pClient.getProject(this);

        /* Update the details */
        theDesc = myDetails.getString("short_description");
        theId = myDetails.getString("_id");

        /* Access the tools */
        final JSONArray myTools = myDetails.getJSONArray("tools");
        final int iNumEntries = myTools.length();
        for (int i = 0; i < iNumEntries; i++) {
            /* Only interested in Ticket Tools */
            final JSONObject myTool = myTools.getJSONObject(i);
            final String myType = myTool.getString("name");
            if ("tickets".equals(myType)) {
                /* Add the ticketSet */
                final ThemisSfTicketSet mySet = new ThemisSfTicketSet(this, myTool);
                mySet.discoverDetails(pClient);
                theTicketSets.add(mySet);
            }
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
     * Obtain the ticketSets.
     * @return the ticketSets
     */
    private List<ThemisSfTicketSet> getTicketSets() {
        return theTicketSets;
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
            if (pName.equals(mySet.getName())) {
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
