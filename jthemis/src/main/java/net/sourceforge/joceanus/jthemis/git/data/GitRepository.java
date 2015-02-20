/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.git.data;

import java.io.File;
import java.io.IOException;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.git.data.GitComponent.GitComponentList;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.data.ScmRepository;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectId;
import net.sourceforge.joceanus.jthemis.scm.tasks.Directory2;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Represents a repository.
 * @author Tony Washer
 */
public class GitRepository
        extends ScmRepository<GitRepository> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(GitRepository.class.getSimpleName(), ScmRepository.FIELD_DEFS);

    /**
     * Base field id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareEqualityField("Base");

    /**
     * The Preferences.
     */
    private final GitPreferences thePreferences;

    /**
     * Repository Base.
     */
    private final String theBase;

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @param pReport the report object
     * @throws JOceanusException on error
     */
    public GitRepository(final PreferenceManager pPreferenceMgr,
                         final ReportStatus pReport) throws JOceanusException {
        /* Call super constructor */
        super(pPreferenceMgr);

        /* Access the Repository base */
        thePreferences = pPreferenceMgr.getPreferenceSet(GitPreferences.class);

        /* Access the Repository base */
        theBase = thePreferences.getStringValue(GitPreferences.NAME_GIT_REPO);
        setName(thePreferences.getStringValue(GitPreferences.NAME_GIT_NAME));

        /* Create component list */
        GitComponentList myComponents = new GitComponentList(this);
        setComponents(myComponents);

        /* Report start of analysis */
        if (pReport.initTask("Analysing components")) {

            /* Discover components */
            myComponents.discover(pReport);

            /* Report completion of pass */
            if (!pReport.isCancelled()) {
                pReport.initTask("Component Analysis complete");
            }
        }
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
    public GitPreferences getPreferences() {
        return thePreferences;
    }

    @Override
    public GitComponentList getComponents() {
        return (GitComponentList) super.getComponents();
    }

    @Override
    public int compareTo(final GitRepository pThat) {
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
        if (!(pThat instanceof GitRepository)) {
            return false;
        }
        GitRepository myThat = (GitRepository) pThat;

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
    public GitComponent locateComponent(final String pName) {
        /* Locate component in component list */
        return (GitComponent) super.locateComponent(pName);
    }

    @Override
    public GitBranch locateBranch(final String pComponent,
                                  final String pVersion) {
        /* Locate branch in component list */
        return (GitBranch) super.locateBranch(pComponent, pVersion);
    }

    @Override
    public GitTag locateTag(final String pComponent,
                            final String pVersion,
                            final int pTag) {
        /* Locate Tag in component list */
        return (GitTag) super.locateTag(pComponent, pVersion, pTag);
    }

    @Override
    protected GitBranch locateBranch(final MvnProjectId pId) {
        /* Lookup mapping */
        return (GitBranch) super.locateBranch(pId);
    }

    @Override
    protected GitTag locateTag(final MvnProjectId pId) {
        /* Lookup mapping */
        return (GitTag) super.locateTag(pId);
    }

    /**
     * Create repository.
     * @param pName the name of the component
     * @return the new component
     * @throws JOceanusException on error
     */
    public GitComponent createComponent(final String pName) throws JOceanusException {
        try {
            /* StringBuilder */
            StringBuilder myPathBuilder = new StringBuilder();
            myPathBuilder.append(theBase);
            myPathBuilder.append(File.separatorChar);
            myPathBuilder.append(pName);
            String myRepoPath = myPathBuilder.toString();

            /* Make sure that the path is deleted */
            Directory2.removeDirectory(new File(myRepoPath));

            /* Create repository */
            FileRepositoryBuilder myBuilder = new FileRepositoryBuilder();
            myBuilder.setGitDir(new File(myRepoPath, GitComponent.NAME_GITDIR));
            Repository myRepo = myBuilder.build();
            myRepo.create();
            myRepo.close();

            /* Create the base component */
            return new GitComponent(this, pName);

        } catch (IOException e) {
            throw new JThemisIOException("Failed to create", e);
        }
    }
}
