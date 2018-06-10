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
package net.sourceforge.joceanus.jthemis.jira.data;

import org.json.JSONException;
import org.json.JSONObject;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraIssueType;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraKeyedIdObject;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraNamedDescIdObject;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraPriority;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraResolution;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraServer.JiraStatus;

/**
 * Short format version of an issue.
 */
public class ThemisJiraShortIssue
        extends JiraKeyedIdObject {
    /**
     * Server.
     */
    private final ThemisJiraServer theServer;

    /**
     * Project of Issue.
     */
    private final ThemisJiraProject theProject;

    /**
     * Summary of Issue.
     */
    private final String theSummary;

    /**
     * Description of Issue.
     */
    private final String theDesc;

    /**
     * Type of Issue.
     */
    private final JiraIssueType theIssueType;

    /**
     * Status of Issue.
     */
    private final JiraStatus theStatus;

    /**
     * Resolution of Issue.
     */
    private final JiraResolution theResolution;

    /**
     * Priority of Issue.
     */
    private final JiraPriority thePriority;

    /**
     * Constructor.
     * @param pServer the server
     * @param pProject the project
     * @param pIssue the underlying issue
     * @throws OceanusException on error
     */
    protected ThemisJiraShortIssue(final ThemisJiraServer pServer,
                                   final ThemisJiraProject pProject,
                                   final JSONObject pIssue) throws OceanusException {
        /* Store parameters */
        super(pIssue);
        theServer = pServer;
        theProject = pProject;

        /* Protect against exceptions */
        try {
            /* Access fields */
            final JSONObject myFields = pIssue.getJSONObject("fields");
            theSummary = myFields.getString(ThemisJiraClient.JIRANAME_SUMMARY);
            theDesc = myFields.optString(JiraNamedDescIdObject.FIELD_DESC, null);

            /* Determine Status etc */
            JSONObject myObject = myFields.getJSONObject(ThemisJiraClient.JIRANAME_ISSUETYPE);
            theIssueType = theServer.getIssueType(myObject.getString(JiraIssueType.FIELD_NAME));
            myObject = myFields.getJSONObject(ThemisJiraClient.JIRANAME_STATUS);
            theStatus = theServer.getStatus(myObject.getString(JiraStatus.FIELD_NAME));
            myObject = myFields.getJSONObject(ThemisJiraClient.JIRANAME_PRIORITY);
            thePriority = theServer.getPriority(myObject.getString(JiraStatus.FIELD_NAME));
            myObject = myFields.optJSONObject(ThemisJiraClient.JIRANAME_RESOLUTION);
            theResolution = myObject == null
                                             ? null
                                             : theServer.getResolution(myObject.getString(JiraResolution.FIELD_NAME));

        } catch (JSONException e) {
            /* Pass the exception on */
            throw new ThemisIOException("Failed to parse issue", e);
        }
    }

    /**
     * Get the server.
     * @return the server
     */
    ThemisJiraServer getServer() {
        return theServer;
    }

    /**
     * Get the summary.
     * @return the summary
     */
    public String getSummary() {
        return theSummary;
    }

    /**
     * Get the description of the issue.
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Get the project of the issue.
     * @return the project
     */
    public ThemisJiraProject getProject() {
        return theProject;
    }

    /**
     * Get the issue type of the issue.
     * @return the issue type
     */
    public JiraIssueType getIssueType() {
        return theIssueType;
    }

    /**
     * Get the status of the issue.
     * @return the status
     */
    public JiraStatus getStatus() {
        return theStatus;
    }

    /**
     * Get the resolution of the issue.
     * @return the resolution
     */
    public JiraResolution getResolution() {
        return theResolution;
    }

    /**
     * Get the priority of the issue.
     * @return the priority
     */
    public JiraPriority getPriority() {
        return thePriority;
    }
}
