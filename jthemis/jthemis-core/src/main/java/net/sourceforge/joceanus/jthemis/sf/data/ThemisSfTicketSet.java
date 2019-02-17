/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraShortIssue;

/**
 * SourceForge TickeSet.
 */
public class ThemisSfTicketSet
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSfTicketSet> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSfTicketSet.class);

    /**
     * Repository field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_OWNER, ThemisSfTicketSet::getProject);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKETSET_NAME, ThemisSfTicketSet::getName);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKETSET_MOUNT, ThemisSfTicketSet::getMount);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKETSET_TICKETS, ThemisSfTicketSet::getTickets);
    }

    /**
     * The max number of tickets at a time.
     */
    private static final int MAX_TICKETS = 50;

    /**
     * The project.
     */
    private final ThemisSfProject theProject;

    /**
     * The name of the ticketSet.
     */
    private final String theName;

    /**
     * The mount of the ticketSet.
     */
    private final String theMount;

    /**
     * The URL.
     */
    private final String theURL;

    /**
     * The Tickets.
     */
    private final List<ThemisSfTicket> theTickets;

    /**
     * Constructor.
     * @param pProject the project
     * @param pDetails the ticketSet details
     */
    public ThemisSfTicketSet(final ThemisSfProject pProject,
                             final JSONObject pDetails) {
        /* Access details */
        theProject = pProject;
        theName = pDetails.getString("mount_label");
        theMount = pDetails.getString("mount_point");
        theURL = pProject.getURL() + theMount;

        /* Create the list */
        theTickets = new ArrayList<>();
    }

    @Override
    public MetisFieldSet<ThemisSfTicketSet> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the project.
     * @return the project
     */
    public ThemisSfProject getProject() {
        return theProject;
    }

    /**
     * Obtain the name of the ticketSet.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the mount of the ticketSet.
     * @return the mount
     */
    public String getMount() {
        return theMount;
    }

    /**
     * Obtain the URL of the ticketSet.
     * @return the URL
     */
    public String getURL() {
        return theURL;
    }

    /**
     * Obtain the tickets.
     * @return the tickets
     */
    private List<ThemisSfTicket> getTickets() {
        return theTickets;
    }

    /**
     * Obtain the ticket iterator.
     * @return the iterator
     */
    public Iterator<ThemisSfTicket> ticketIterator() {
        return theTickets.iterator();
    }

    /**
     * Is this ticketSet a singleton?
     * @return true/false
     */
    public boolean isSingleton() {
        return !theProject.hasMultiTicketSets();
    }

    /**
     * Discover details of the ticketSet.
     * @param pClient the client
     * @throws OceanusException on error
     */
    protected void discoverDetails(final ThemisSfClient pClient) throws OceanusException {
        /* Access the initial ticket query */
        final JSONObject myFirst = pClient.getTicketSet(this, MAX_TICKETS);
        updateDetails(myFirst);
        int myCount = myFirst.getInt("count");
        myCount -= MAX_TICKETS;

        /* While we have more tickets */
        int myPage = 1;
        while (myCount > 0) {
            /* Access and process the next page */
            final JSONObject myNext = pClient.getTicketSet(this, myPage, MAX_TICKETS);
            updateDetails(myNext);
            myCount -= MAX_TICKETS;
            myPage++;
        }

        /* Loop through the tickets */
        for (ThemisSfTicket myTicket : theTickets) {
            myTicket.discoverDetails(pClient);
        }
    }

    /**
     * Update details.
     * @param pDetails the details
     */
    public void updateDetails(final JSONObject pDetails) {
        /* Access the tools */
        final JSONArray myTickets = pDetails.getJSONArray("tickets");
        final int iNumEntries = myTickets.length();
        for (int i = 0; i < iNumEntries; i++) {
            final JSONObject myTicket = myTickets.getJSONObject(i);

            /* Add the ticketSet */
            theTickets.add(new ThemisSfTicket(this, myTicket));
        }
    }

    /**
     * Obtain the matching ticket.
     * @param pClient the client
     * @param pIssue the jiraIssue
     * @return the matching sourceForge ticket
     * @throws OceanusException on error
     */
    public ThemisSfTicket matchTicket(final ThemisSfClient pClient,
                                      final ThemisJiraShortIssue pIssue) throws OceanusException {
        /* Look for a matching ticket and create a new ticket if there is no match */
        final ThemisSfTicket myTicket = obtainMatchingTicket(pIssue.getKey());
        return myTicket == null
                                ? createTicket(pClient, pIssue)
                                : myTicket;
    }

    /**
     * Obtain the ticket that matches thisCreate a new ticket.
     * @param pLabel the label
     * @return the matching ticket (or null)
     */
    public ThemisSfTicket obtainMatchingTicket(final String pLabel) {
        /* Loop through the tickets */
        final Iterator<ThemisSfTicket> myIterator = theTickets.iterator();
        while (myIterator.hasNext()) {
            final ThemisSfTicket myTicket = myIterator.next();
            if (myTicket.hasLabel(pLabel)) {
                return myTicket;
            }
        }

        /* Not found */
        return null;
    }

    /**
     * Create a new ticket.
     * @param pClient the client
     * @param pIssue the jiraIssue
     * @return the new ticket
     * @throws OceanusException on error
     */
    private ThemisSfTicket createTicket(final ThemisSfClient pClient,
                                        final ThemisJiraShortIssue pIssue) throws OceanusException {
        /* Create the ticket */
        final JSONObject myDetails = pClient.createTicket(this, pIssue);
        final ThemisSfTicket myTicket = new ThemisSfTicket(this, myDetails.getJSONObject("ticket"));
        theTickets.add(myTicket);
        return myTicket;
    }
}
