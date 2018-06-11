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
 * Sourceforge Ticket.
 */
public class ThemisSfTicket
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSfTicket> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSfTicket.class);

    /**
     * Repository field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_OWNER, ThemisSfTicket::getTicketSet);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKET_ID, ThemisSfTicket::getNumber);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKET_SUMMARY, ThemisSfTicket::getSummary);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKET_DESC, ThemisSfTicket::getDescription);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKET_LABELS, ThemisSfTicket::getLabels);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKET_STATUS, ThemisSfTicket::getStatus);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKET_REPORTEDBY, ThemisSfTicket::getReportedBy);
        FIELD_DEFS.declareLocalField(ThemisResource.TICKET_ASSIGNEDTO, ThemisSfTicket::getAssignedTo);
    }

    /**
     * The ticketSet.
     */
    private final ThemisSfTicketSet theTicketSet;

    /**
     * The ticket #.
     */
    private final int theTicketNo;

    /**
     * The summary.
     */
    private final String theSummary;

    /**
     * The URL.
     */
    private final String theURL;

    /**
     * The Labels.
     */
    private final List<String> theLabels;

    /**
     * The discussionURL.
     */
    private String theDiscussionURL;

    /**
     * The Description.
     */
    private String theDesc;

    /**
     * The Status.
     */
    private String theStatus;

    /**
     * The ReportedBy.
     */
    private String theReportedBy;

    /**
     * The AssignedTo.
     */
    private String theAssignedTo;

    /**
     * Constructor.
     * @param pTicketSet the ticketSet
     * @param pDetails the ticket details
     */
    public ThemisSfTicket(final ThemisSfTicketSet pTicketSet,
                          final JSONObject pDetails) {
        /* Access details */
        theTicketSet = pTicketSet;
        theTicketNo = pDetails.getInt("ticket_num");
        theSummary = pDetails.getString("summary");
        theURL = pTicketSet.getURL() + "/" + theTicketNo;

        /* Allocate the labels */
        theLabels = new ArrayList<>();

        /* Access optional details */
        theDesc = pDetails.optString("description", null);
        theStatus = pDetails.optString("status", null);
        theReportedBy = pDetails.optString("reported_by", null);
        theAssignedTo = pDetails.optString("assigned_to", null);
        theDiscussionURL = getDiscussionURL(pDetails);

        /* Process the labels */
        processLabels(pDetails);
    }

    @Override
    public MetisFieldSet<ThemisSfTicket> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        /* Build the ticket# */
        final StringBuilder myBuilder = new StringBuilder();
        if (!theTicketSet.isSingleton()) {
            myBuilder.append(theTicketSet.getMount());
            myBuilder.append(":");
        }
        myBuilder.append("#");
        myBuilder.append(theTicketNo);
        return myBuilder.toString();
    }

    /**
     * Obtain the project.
     * @return the project
     */
    public ThemisSfTicketSet getTicketSet() {
        return theTicketSet;
    }

    /**
     * Obtain the number of the ticket.
     * @return the number
     */
    public int getNumber() {
        return theTicketNo;
    }

    /**
     * Obtain the summary of the ticket.
     * @return the summary
     */
    public String getSummary() {
        return theSummary;
    }

    /**
     * Obtain the description of the ticket.
     * @return the description
     */
    public String getDescription() {
        return theDesc;
    }

    /**
     * Obtain the status of the ticket.
     * @return the status
     */
    public String getStatus() {
        return theStatus;
    }

    /**
     * Obtain the reportedBy id.
     * @return the reportedBy
     */
    public String getReportedBy() {
        return theReportedBy;
    }

    /**
     * Obtain the assignedTo id.
     * @return the assignedTo
     */
    public String getAssignedTo() {
        return theAssignedTo;
    }

    /**
     * Obtain the URL of the ticket.
     * @return the URL
     */
    public String getURL() {
        return theURL;
    }

    /**
     * Obtain the discussionURL of the ticket.
     * @return the URL
     */
    public String getDiscussionURL() {
        return theDiscussionURL;
    }

    /**
     * Obtain the labels.
     * @return the labels
     */
    private List<String> getLabels() {
        return theLabels;
    }

    /**
     * Obtain the label iterator.
     * @return the iterator
     */
    public Iterator<String> labelIterator() {
        return theLabels.iterator();
    }

    /**
     * Does the ticket have the required label?
     * @param pLabel the required label
     * @return true/false
     */
    public boolean hasLabel(final String pLabel) {
        return theLabels.contains(pLabel);
    }

    /**
     * Obtain reference.
     * @return the reference.
     */
    public String getReference() {
        /* Build the reference */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("[");
        if (!theTicketSet.isSingleton()) {
            myBuilder.append(theTicketSet.getName());
            myBuilder.append(":");
        }
        myBuilder.append("#");
        myBuilder.append(theTicketNo);
        myBuilder.append("]");
        return myBuilder.toString();
    }

    /**
     * Discover details of the ticket.
     * @param pClient the client
     * @throws OceanusException on error
     */
    protected void discoverDetails(final ThemisSfClient pClient) throws OceanusException {
        /* Access the ticket */
        final JSONObject myDetails = pClient.getTicket(this);
        final JSONObject myTicket = myDetails.getJSONObject("ticket");

        /* Update the details */
        theDesc = myTicket.getString("description");
        theStatus = myTicket.getString("status");
        theReportedBy = myTicket.getString("reported_by");
        theAssignedTo = myTicket.optString("assigned_to");
        theDiscussionURL = getDiscussionURL(myTicket);

        /* Null some fields out if they are blank */
        if (theDesc.length() == 0) {
            theDesc = null;
        }
        if (theAssignedTo.length() == 0) {
            theAssignedTo = null;
        }

        /* Process the labels */
        processLabels(myTicket);
    }

    /**
     * Obtain the discussionURL.
     * @param pDetails the ticket details
     * @return the URL
     */
    private String getDiscussionURL(final JSONObject pDetails) {
        /* Access the discussion */
        final JSONObject myDetails = pDetails.optJSONObject("discussion_thread");
        return myDetails != null
                                 ? theURL + "/_discuss/thread/" + myDetails.getString("_id")
                                 : null;
    }

    /**
     * process the labels.
     * @param pDetails the ticket details
     */
    private void processLabels(final JSONObject pDetails) {
        /* Access the discussion */
        final JSONArray myLabels = pDetails.optJSONArray("labels");
        if (myLabels != null) {
            theLabels.clear();
            final int iNumEntries = myLabels.length();
            for (int i = 0; i < iNumEntries; i++) {
                final String myLabel = myLabels.getString(i);

                /* Add the label */
                theLabels.add(myLabel);
            }
        }
    }
}
