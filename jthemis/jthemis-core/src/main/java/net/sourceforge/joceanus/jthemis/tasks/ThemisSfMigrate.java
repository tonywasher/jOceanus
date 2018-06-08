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
package net.sourceforge.joceanus.jthemis.tasks;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraProject;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraShortIssue;
import net.sourceforge.joceanus.jthemis.sf.data.ThemisSfServer;
import net.sourceforge.joceanus.jthemis.sf.data.ThemisSfTicket;

/**
 * Migrate Jira Issues to Sourceforge.
 */
public final class ThemisSfMigrate {
    /**
     * Private constructor.
     */
    private ThemisSfMigrate() {
    }

    /**
     * Migrate.
     * @param pPreferences the preferences
     * @param pExtract the extract
     * @throws OceanusException on error
     */
    public static void migrate(final MetisPreferenceManager pPreferences,
                               final ThemisSvnExtract pExtract) throws OceanusException {
        /* Obtain the revisions */
        final ThemisSvnRevisions myRevisions = pExtract.getSvnRevisions();

        /* Create the SourceForge Client */
        final ThemisSfServer mySf = new ThemisSfServer(pPreferences);

        /* Create the Jira Client and obtain the list of issues */
        final ThemisJiraServer myJira = new ThemisJiraServer(pPreferences);
        final ThemisJiraProject myProject = myJira.getProject("FIN");

        /* Loop through the issues */
        final Iterator<ThemisJiraShortIssue> myIterator = myProject.issueSummariesIterator();
        while (myIterator.hasNext()) {
            final ThemisJiraShortIssue myIssue = myIterator.next();

            /* If the issue is open or referenced */
            if ("OPEN".equals(myIssue.getStatus().getName())
                || myRevisions.isIssueReferenced(myIssue.getKey())) {
                /* Access or create the sourceForge revision */
                final ThemisSfTicket myTicket = mySf.matchTicket(myIssue);
                myRevisions.registerTicket(myIssue.getKey(), myTicket);
            }
        }
    }
}
