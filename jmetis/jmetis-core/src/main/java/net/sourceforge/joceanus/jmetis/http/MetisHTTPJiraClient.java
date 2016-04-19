/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.http;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jira REST Client.
 * @author Tony Washer
 */
public class MetisHTTPJiraClient
        extends MetisHTTPDataClient {
    /**
     * Server location.
     */
    private static final String JIRA_WEBLOC = "/rest/api/2/";

    /**
     * Project name.
     */
    public static final String JIRANAME_PROJECT = "project";

    /**
     * Status name.
     */
    public static final String JIRANAME_STATUS = "status";

    /**
     * Priority name.
     */
    public static final String JIRANAME_PRIORITY = "priority";

    /**
     * Resolution name.
     */
    public static final String JIRANAME_RESOLUTION = "resolution";

    /**
     * IssueType name.
     */
    public static final String JIRANAME_ISSUETYPE = "issuetype";

    /**
     * Project prefix.
     */
    private static final String JIRAPFX_PROJECT = JIRANAME_PROJECT + "/";

    /**
     * Maximum items in search.
     */
    private static final int MAX_SEARCH = 50;

    /**
     * Constructor.
     * @param pWebLoc the web location
     * @param pAuth the authorisation string
     * @throws OceanusException on error
     */
    public MetisHTTPJiraClient(final String pWebLoc,
                               final String pAuth) throws OceanusException {
        /* Initialise underlying class */
        super(pWebLoc + JIRA_WEBLOC, pAuth);
    }

    /**
     * Obtain project list.
     * @return the project list.
     * @throws OceanusException on error
     */
    public JSONArray getProjects() throws OceanusException {
        return getJSONArray(JIRANAME_PROJECT);
    }

    /**
     * Obtain project detail.
     * @param pProjectKey the project key
     * @return the project detail.
     * @throws OceanusException on error
     */
    public JSONObject getProject(final String pProjectKey) throws OceanusException {
        return getJSONObject(JIRAPFX_PROJECT + pProjectKey);
    }

    /**
     * Obtain statuses.
     * @return the statuses.
     * @throws OceanusException on error
     */
    public JSONArray getStatuses() throws OceanusException {
        return getJSONArray(JIRANAME_STATUS);
    }

    /**
     * Obtain statusCategories.
     * @return the statusCategories.
     * @throws OceanusException on error
     */
    public JSONArray getStatusCategories() throws OceanusException {
        return getJSONArray("statuscategory");
    }

    /**
     * Obtain resolutions.
     * @return the resolutions.
     * @throws OceanusException on error
     */
    public JSONArray getResolutions() throws OceanusException {
        return getJSONArray(JIRANAME_RESOLUTION);
    }

    /**
     * Obtain priorities.
     * @return the priorities.
     * @throws OceanusException on error
     */
    public JSONArray getPriorities() throws OceanusException {
        return getJSONArray(JIRANAME_PRIORITY);
    }

    /**
     * Obtain issueTypes.
     * @return the issueTypes.
     * @throws OceanusException on error
     */
    public JSONArray getIssueTypes() throws OceanusException {
        return getJSONArray(JIRANAME_ISSUETYPE);
    }

    /**
     * Obtain issueLinkTypes.
     * @return the issueLinkTypes.
     * @throws OceanusException on error
     */
    public JSONObject getIssueLinkTypes() throws OceanusException {
        return getJSONObject("issueLinkType");
    }

    /**
     * Obtain roles for project.
     * @param pURL the explicit URL
     * @return the roles.
     * @throws OceanusException on error
     */
    public JSONObject getProjectRoleFromURL(final String pURL) throws OceanusException {
        return getAbsoluteJSONObject(pURL);
    }

    /**
     * Obtain statuses for project.
     * @param pProjectKey the project key
     * @return the statuses.
     * @throws OceanusException on error
     */
    public JSONArray getStatusesForProject(final String pProjectKey) throws OceanusException {
        return getJSONArray(JIRAPFX_PROJECT + pProjectKey + "/statuses");
    }

    /**
     * Obtain components for project.
     * @param pProjectKey the project key
     * @return the components.
     * @throws OceanusException on error
     */
    public JSONArray getComponentsForProject(final String pProjectKey) throws OceanusException {
        return getJSONArray(JIRAPFX_PROJECT + pProjectKey + "/components");
    }

    /**
     * Obtain versions for project.
     * @param pProjectKey the project key
     * @return the versions.
     * @throws OceanusException on error
     */
    public JSONArray getVersionsForProject(final String pProjectKey) throws OceanusException {
        return getJSONArray(JIRAPFX_PROJECT + pProjectKey + "/versions");
    }

    /**
     * Obtain role for project.
     * @param pProjectKey the project key
     * @param pRoleId the roleId
     * @return the role.
     * @throws OceanusException on error
     */
    public JSONObject getRoleForProject(final String pProjectKey,
                                        final String pRoleId) throws OceanusException {
        return getJSONObject(JIRAPFX_PROJECT + pProjectKey + "/role/" + pRoleId);
    }

    /**
     * Obtain group.
     * @param pName the group name
     * @return the group.
     * @throws OceanusException on error
     */
    public JSONObject getGroup(final String pName) throws OceanusException {
        return getJSONObject("group?groupname=" + pName);
    }

    /**
     * Obtain group users.
     * @param pName the group name
     * @return the group.
     * @throws OceanusException on error
     */
    public JSONObject getGroupUsers(final String pName) throws OceanusException {
        return getJSONObject("group?groupname=" + pName + "&expand=users");
    }

    /**
     * Obtain user.
     * @param pName the user name
     * @return the user.
     * @throws OceanusException on error
     */
    public JSONObject getUser(final String pName) throws OceanusException {
        return getJSONObject("user?username=" + pName);
    }

    /**
     * Obtain user groups.
     * @param pName the user name
     * @return the user.
     * @throws OceanusException on error
     */
    public JSONObject getUserGroups(final String pName) throws OceanusException {
        return getJSONObject("user?username=" + pName + "&expand=groups");
    }

    /**
     * Obtain list of issues in project.
     * @param pProjectKey the project Key
     * @return the list of issues in the project.
     * @throws OceanusException on error
     */
    public List<String> getIssueKeysForProject(final String pProjectKey) throws OceanusException {
        /* Create the list to return */
        List<String> myList = new ArrayList<>();

        /* Enter loop */
        int iMaxSearch = MAX_SEARCH;
        int iStartAt = 0;
        int myTotalIssues;
        do {
            /* Access the next portion of the search */
            JSONObject myResponse = queryJSONObjectWithHeaderAndTrailer("search?jql=",
                    "project=\"" + pProjectKey + "\"",
                    "&startAt=" + iStartAt + "&maxResults=" + iMaxSearch + "&fields=summary");

            /* process the response */
            JSONArray myArray = myResponse.getJSONArray("issues");
            int myNumIssues = myArray.length();
            for (int i = 0; i < myNumIssues; i++) {
                /* Access issue key */
                JSONObject myIssue = myArray.getJSONObject(i);
                myList.add(myIssue.getString("key"));
            }

            /* determine the number of issues */
            iStartAt += myNumIssues;
            myTotalIssues = myResponse.getInt("total");

            /* Re-loop if there are more issues */
        } while (iStartAt < myTotalIssues);

        /* return the list */
        return myList;
    }

    /**
     * Obtain issue detail.
     * @param pIssueKey the issue Key
     * @return the user.
     * @throws OceanusException on error
     */
    public JSONObject getIssue(final String pIssueKey) throws OceanusException {
        return getJSONObject("issue/" + pIssueKey);
    }
}