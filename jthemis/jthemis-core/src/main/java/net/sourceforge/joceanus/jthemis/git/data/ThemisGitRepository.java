/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jthemis.git.data;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitComponent.GitComponentList;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitPreference.ThemisGitPreferenceKey;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitPreference.ThemisGitPreferences;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmRepository;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectId;
import net.sourceforge.joceanus.jthemis.scm.tasks.ThemisDirectory;

/**
 * Represents a repository.
 * @author Tony Washer
 */
public class ThemisGitRepository
        extends ThemisScmRepository<ThemisGitRepository> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(ThemisGitRepository.class.getSimpleName(), ThemisScmRepository.FIELD_DEFS);

    /**
     * Base field id.
     */
    private static final MetisField FIELD_BASE = FIELD_DEFS.declareEqualityField("Base");

    /**
     * The Preferences.
     */
    private final ThemisGitPreferences thePreferences;

    /**
     * Repository Base.
     */
    private final String theBase;

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @param pReport the report object
     * @throws OceanusException on error
     */
    public ThemisGitRepository(final MetisPreferenceManager pPreferenceMgr,
                               final MetisThreadStatusReport pReport) throws OceanusException {
        /* Call super constructor */
        super(pPreferenceMgr);

        /* Access the Repository base */
        thePreferences = pPreferenceMgr.getPreferenceSet(ThemisGitPreferences.class);

        /* Access the Repository base */
        theBase = thePreferences.getStringValue(ThemisGitPreferenceKey.BASE);
        setName(thePreferences.getStringValue(ThemisGitPreferenceKey.NAME));

        /* Create component list */
        GitComponentList myComponents = new GitComponentList(this);
        setComponents(myComponents);

        /* Report start of analysis */
        pReport.initTask("Analysing components");

        /* Discover components */
        myComponents.discover(pReport);
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_BASE.equals(pField)) {
            return theBase;
        }

        /* pass call on */
        return super.getFieldValue(pField);
    }

    /**
     * Obtain the repository base.
     * @return the name
     */
    public String getBase() {
        return theBase;
    }

    /**
     * Obtain the preferences.
     * @return the preferences
     */
    public ThemisGitPreferences getPreferences() {
        return thePreferences;
    }

    @Override
    public GitComponentList getComponents() {
        return (GitComponentList) super.getComponents();
    }

    @Override
    public int compareTo(final ThemisGitRepository pThat) {
        /* Handle trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare bases */
        int iResult = theBase.compareTo(pThat.theBase);
        if (iResult != 0) {
            return iResult;
        }

        /* Compare names */
        return super.compareTo(pThat);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check that the classes are the same */
        if (!(pThat instanceof ThemisGitRepository)) {
            return false;
        }
        ThemisGitRepository myThat = (ThemisGitRepository) pThat;

        /* Compare fields */
        if (!theBase.equals(myThat.theBase)) {
            return false;
        }
        return super.equals(myThat);
    }

    @Override
    public int hashCode() {
        return (theBase.hashCode() * HASH_PRIME) + super.hashCode();
    }

    @Override
    public ThemisGitComponent locateComponent(final String pName) {
        /* Locate component in component list */
        return (ThemisGitComponent) super.locateComponent(pName);
    }

    @Override
    public ThemisGitBranch locateBranch(final String pComponent,
                                        final String pVersion) {
        /* Locate branch in component list */
        return (ThemisGitBranch) super.locateBranch(pComponent, pVersion);
    }

    @Override
    public ThemisGitTag locateTag(final String pComponent,
                                  final String pVersion,
                                  final int pTag) {
        /* Locate Tag in component list */
        return (ThemisGitTag) super.locateTag(pComponent, pVersion, pTag);
    }

    @Override
    protected ThemisGitBranch locateBranch(final ThemisMvnProjectId pId) {
        /* Lookup mapping */
        return (ThemisGitBranch) super.locateBranch(pId);
    }

    @Override
    protected ThemisGitTag locateTag(final ThemisMvnProjectId pId) {
        /* Lookup mapping */
        return (ThemisGitTag) super.locateTag(pId);
    }

    /**
     * Create repository.
     * @param pName the name of the component
     * @return the new component
     * @throws OceanusException on error
     */
    public ThemisGitComponent createComponent(final String pName) throws OceanusException {
        try {
            /* StringBuilder */
            StringBuilder myPathBuilder = new StringBuilder();
            myPathBuilder.append(theBase);
            myPathBuilder.append(File.separatorChar);
            myPathBuilder.append(pName);
            String myRepoPath = myPathBuilder.toString();

            /* Make sure that the path is deleted */
            ThemisDirectory.removeDirectory(new File(myRepoPath));

            /* Create repository */
            FileRepositoryBuilder myBuilder = new FileRepositoryBuilder();
            myBuilder.setGitDir(new File(myRepoPath, ThemisGitComponent.NAME_GITDIR));
            Repository myRepo = myBuilder.build();
            myRepo.create();
            myRepo.close();

            /* Create the base component */
            return new ThemisGitComponent(this, pName);

        } catch (IOException e) {
            throw new ThemisIOException("Failed to create", e);
        }
    }
}
