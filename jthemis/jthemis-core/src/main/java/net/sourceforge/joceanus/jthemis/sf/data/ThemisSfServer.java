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

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraShortIssue;
import net.sourceforge.joceanus.jthemis.sf.data.ThemisSfPreference.ThemisSfPreferenceKey;
import net.sourceforge.joceanus.jthemis.sf.data.ThemisSfPreference.ThemisSfPreferences;

/**
 * SourceForge server.
 */
public class ThemisSfServer {
    /**
     * The Http Client.
     */
    private final ThemisSfClient theClient;

    /**
     * The SourceForge user.
     */
    private final ThemisSfUser theUser;

    /**
     * The TicketSet.
     */
    private final String theTicketSet;

    /**
     * Constructor.
     * @param pManager the preference manager
     * @throws OceanusException on error
     */
    public ThemisSfServer(final MetisPreferenceManager pManager) throws OceanusException {
        /* Access the SourceForge preferences */
        final ThemisSfPreferences myPreferences = pManager.getPreferenceSet(ThemisSfPreferences.class);
        final String myUser = myPreferences.getStringValue(ThemisSfPreferenceKey.USER);
        final String myBearer = myPreferences.getStringValue(ThemisSfPreferenceKey.BEARER);
        theTicketSet = myPreferences.getStringValue(ThemisSfPreferenceKey.TICKETSET);

        /* Access the SourceForge Client */
        theClient = new ThemisSfClient(myBearer);

        /* Access the user */
        theUser = new ThemisSfUser(theClient.getUser(myUser));
        theUser.discoverDetails(theClient);
    }

    /**
     * Obtain the named project (ignoring case).
     * @param pName the project name
     * @return the project
     */
    public ThemisSfProject getProject(final String pName) {
        return theUser.getProject(pName);
    }

    /**
     * Obtain the ticketSet for the project.
     * @param pProject the project
     * @return the active TicketSet
     */
    private ThemisSfTicketSet getTicketSet(final ThemisSfProject pProject) {
        return pProject.getTicketSet(theTicketSet);
    }

    /**
     * Obtain/Create the matching ticket.
     * @param pProject the project
     * @param pIssue the jiraIssue
     * @return the matching sourceForge ticket (created if it did not exist)
     * @throws OceanusException on error
     */
    public ThemisSfTicket matchTicket(final ThemisSfProject pProject,
                                      final ThemisJiraShortIssue pIssue) throws OceanusException {
        return getTicketSet(pProject).matchTicket(theClient, pIssue);
    }

    /**
     * Obtain the matching ticket.
     * @param pProject the project
     * @param pIssue the jiraIssue
     * @return the matching sourceForge ticket (or null)
     */
    public ThemisSfTicket obtainMatchingTicket(final ThemisSfProject pProject,
                                               final ThemisJiraShortIssue pIssue) {
        return getTicketSet(pProject).obtainMatchingTicket(pIssue.getKey());
    }
}
