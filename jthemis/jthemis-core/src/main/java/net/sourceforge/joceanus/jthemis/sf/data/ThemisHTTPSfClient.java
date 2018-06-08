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

import org.json.JSONObject;

import net.sourceforge.joceanus.jmetis.http.MetisHTTPDataClient;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraShortIssue;

/**
 * HTTP Client for Sourceforge.
 */
public class ThemisHTTPSfClient
        extends MetisHTTPDataClient {
    /**
     * Server location.
     */
    private static final String SF_WEBLOC = "https://sourceforge.net/rest/";

    /**
     * Constructor.
     * @param pAuth the authorisation string
     * @throws OceanusException on error
     */
    public ThemisHTTPSfClient(final String pAuth) throws OceanusException {
        /* Initialise underlying class */
        super(SF_WEBLOC, MetisHTTPAuthType.BEARER, pAuth);
    }

    /**
     * Obtain project details.
     * @param pName the name of the project
     * @return the details
     * @throws OceanusException on error
     */
    public JSONObject getProject(final String pName) throws OceanusException {
        return getJSONObject("p/" + pName);
    }

    /**
     * Obtain ticketSet details.
     * @param pTicketSet the ticketSet
     * @param pMaxItems the maxItems
     * @return the details
     * @throws OceanusException on error
     */
    public JSONObject getTicketSet(final ThemisSfTicketSet pTicketSet,
                                   final int pMaxItems) throws OceanusException {
        return getJSONObject(pTicketSet.getURL() + "?limit=" + pMaxItems);
    }

    /**
     * Obtain ticketSet details.
     * @param pTicketSet the ticketSet
     * @param pPageNo the pageNo
     * @param pMaxItems the maxItems
     * @return the details
     * @throws OceanusException on error
     */
    public JSONObject getTicketSet(final ThemisSfTicketSet pTicketSet,
                                   final int pPageNo,
                                   final int pMaxItems) throws OceanusException {
        return getJSONObject(pTicketSet.getURL() + "?page=" + pPageNo + "&limit=" + pMaxItems);
    }

    /**
     * Obtain ticket details.
     * @param pTicket the ticket
     * @return the details
     * @throws OceanusException on error
     */
    public JSONObject getTicket(final ThemisSfTicket pTicket) throws OceanusException {
        return getJSONObject(pTicket.getURL());
    }

    /**
     * Obtain ticketDiscussion details.
     * @param pTicket the ticket
     * @param pMaxItems the maxItems
     * @return the details
     * @throws OceanusException on error
     */
    public JSONObject getTicketDiscussion(final ThemisSfTicket pTicket,
                                          final int pMaxItems) throws OceanusException {
        return getJSONObject(pTicket.getDiscussionURL() + "?limit=" + pMaxItems);
    }

    /**
     * Obtain ticketDiscussion details.
     * @param pTicket the ticket
     * @param pPageNo the pageNo
     * @param pMaxItems the maxItems
     * @return the details
     * @throws OceanusException on error
     */
    public JSONObject getTicketDiscussion(final ThemisSfTicket pTicket,
                                          final int pPageNo,
                                          final int pMaxItems) throws OceanusException {
        return getJSONObject(pTicket.getDiscussionURL() + "?page=" + pPageNo + "&limit=" + pMaxItems);
    }

    /**
     * Create a new ticket.
     * @param pTicketSet the ticketSet
     * @param pIssue the jiraIssue
     * @return the ticket details
     * @throws OceanusException on error
     */
    public JSONObject createTicket(final ThemisSfTicketSet pTicketSet,
                                   final ThemisJiraShortIssue pIssue) throws OceanusException {
        /* Create the JSON object */
        final JSONObject myRequest = new JSONObject();
        myRequest.put("ticket_form.summary", pIssue.getSummary());
        myRequest.put("ticket_form.description", pIssue.getDesc());
        myRequest.put("ticket_form.labels", pIssue.getKey());
        myRequest.put("ticket_form.status", "CLOSED".equals(pIssue.getStatus().getName())
                                                                                          ? "closed"
                                                                                          : "open");

        /* Post the JSON Object */
        return postRequest(pTicketSet.getURL() + "/new", myRequest);
    }
}
