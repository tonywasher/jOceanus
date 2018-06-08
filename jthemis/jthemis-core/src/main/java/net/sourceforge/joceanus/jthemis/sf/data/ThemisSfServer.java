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
    private final ThemisHTTPSfClient theClient;

    /**
     * The SourceForge project.
     */
    private final ThemisSfProject theProject;

    /**
     * The SourceForge ticketSet.
     */
    private final ThemisSfTicketSet theTicketSet;

    /**
     * Constructor.
     * @param pManager the preference manager
     * @throws OceanusException on error
     */
    public ThemisSfServer(final MetisPreferenceManager pManager) throws OceanusException {
        /* Access the SourceForge preferences */
        final ThemisSfPreferences myPreferences = pManager.getPreferenceSet(ThemisSfPreferences.class);
        final String myProject = myPreferences.getStringValue(ThemisSfPreferenceKey.PROJECT);
        final String myTicketSet = myPreferences.getStringValue(ThemisSfPreferenceKey.TICKETSET);
        final String myBearer = myPreferences.getStringValue(ThemisSfPreferenceKey.BEARER);

        /* Access the SourceForge Client */
        theClient = new ThemisHTTPSfClient(myBearer);

        /* Access the project */
        theProject = new ThemisSfProject(theClient.getProject(myProject));
        theProject.discoverDetails(theClient);

        /* Access the ticketSet */
        theTicketSet = theProject.getTicketSet(myTicketSet);
    }

    /**
     * Obtain the matching ticket.
     * @param pIssue the jiraIssue
     * @return the matching sourceForge ticket
     * @throws OceanusException on error
     */
    public ThemisSfTicket matchTicket(final ThemisJiraShortIssue pIssue) throws OceanusException {
        return theTicketSet.matchTicket(theClient, pIssue);
    }
}
