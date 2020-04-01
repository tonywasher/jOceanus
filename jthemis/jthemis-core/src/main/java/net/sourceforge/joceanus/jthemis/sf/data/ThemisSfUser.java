/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
 * SourceForge User.
 */
public class ThemisSfUser
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSfUser> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSfUser.class);

    /*
     * Repository field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.USER_USERNAME, ThemisSfUser::getUserName);
        FIELD_DEFS.declareLocalField(ThemisResource.USER_NAME, ThemisSfUser::getName);
        FIELD_DEFS.declareLocalField(ThemisResource.USER_PROJECTS, ThemisSfUser::getProjects);
    }

    /**
     * The User Name.
     */
    private final String theUserName;

    /**
     * The Name.
     */
    private final String theName;

    /**
     * The Projects.
     */
    private final List<ThemisSfProject> theProjects;

    /**
     * Constructor.
     * @param pDetails the ticketSet details
     */
    public ThemisSfUser(final JSONObject pDetails) {
        /* Access details */
        theUserName = pDetails.getString("username");
        theName = pDetails.getString("name");

        /* Create the list */
        theProjects = new ArrayList<>();

        /* Access the projects */
        final JSONArray myTools = pDetails.getJSONArray("projects");
        final int iNumEntries = myTools.length();
        for (int i = 0; i < iNumEntries; i++) {
            /* Add the project */
            final JSONObject myProject = myTools.getJSONObject(i);

            /* Add the project */
            theProjects.add(new ThemisSfProject(this, myProject));
        }
    }

    @Override
    public MetisFieldSet<ThemisSfUser> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the userName.
     * @return the name
     */
    public String getUserName() {
        return theUserName;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the projects.
     * @return the projects
     */
    private List<ThemisSfProject> getProjects() {
        return theProjects;
    }

    /**
     * Obtain the ticket iterator.
     * @return the iterator
     */
    public Iterator<ThemisSfProject> projectIterator() {
        return theProjects.iterator();
    }

    /**
     * Discover details of the project.
     * @param pClient the client
     * @throws OceanusException on error
     */
    public void discoverDetails(final ThemisSfClient pClient) throws OceanusException {
        /* Loop through the projects */
        for (ThemisSfProject myProject : theProjects) {
            myProject.discoverDetails(pClient);
        }
    }

    /**
     * Obtain the named project (ignoring case).
     * @param pName the project name
     * @return the project
     */
    public ThemisSfProject getProject(final String pName) {
        /* Loop through the projects */
        for (ThemisSfProject myProject : theProjects) {
            if (pName.equalsIgnoreCase(myProject.getName())) {
                return myProject;
            }
        }
        return null;
    }
}
