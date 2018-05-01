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
package net.sourceforge.joceanus.jthemis.git.data;

import java.io.File;
import java.io.IOException;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitComponent.ThemisGitComponentList;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitPreference.ThemisGitPreferenceKey;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitPreference.ThemisGitPreferences;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmRepository;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectId;
import net.sourceforge.joceanus.jthemis.scm.tasks.ThemisDirectory;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Represents a repository.
 * @author Tony Washer
 */
public class ThemisGitRepository
        extends ThemisScmRepository {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisGitRepository> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisGitRepository.class);

    /**
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_BASE, ThemisGitRepository::getBase);
    }

    /**
     * The Preferences.
     */
    private final ThemisGitPreferences thePreferences;

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
        setBase(thePreferences.getStringValue(ThemisGitPreferenceKey.BASE));
        setName(thePreferences.getStringValue(ThemisGitPreferenceKey.NAME));

        /* Create component list */
        final ThemisGitComponentList myComponents = new ThemisGitComponentList(this);
        setComponents(myComponents);

        /* Report start of analysis */
        pReport.initTask("Analysing components");

        /* Discover components */
        myComponents.discover(pReport);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public MetisFieldSet<ThemisGitRepository> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the preferences.
     * @return the preferences
     */
    public ThemisGitPreferences getPreferences() {
        return thePreferences;
    }

    @Override
    public ThemisGitComponentList getComponents() {
        return (ThemisGitComponentList) super.getComponents();
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
            final StringBuilder myPathBuilder = new StringBuilder();
            myPathBuilder.append(getBase())
                    .append(File.separatorChar)
                    .append(pName);
            final String myRepoPath = myPathBuilder.toString();

            /* Make sure that the path is deleted */
            ThemisDirectory.removeDirectory(new File(myRepoPath));

            /* Create repository */
            final FileRepositoryBuilder myBuilder = new FileRepositoryBuilder();
            myBuilder.setGitDir(new File(myRepoPath, ThemisGitComponent.NAME_GITDIR));
            final Repository myRepo = myBuilder.build();
            myRepo.create();
            myRepo.close();

            /* Create the base component */
            return new ThemisGitComponent(this, pName);

        } catch (IOException e) {
            throw new ThemisIOException("Failed to create", e);
        }
    }
}
