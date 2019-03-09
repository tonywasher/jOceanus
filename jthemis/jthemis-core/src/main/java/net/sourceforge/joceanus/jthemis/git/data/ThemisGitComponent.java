/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
import java.io.InputStream;
import java.util.Iterator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitBranch.ThemisGitBranchList;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitCommitId;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitRevision;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent.ThemisScmComponentList;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmOwner;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition.ThemisMvnSubModule;

/**
 * Represents a component in the repository.
 * @author Tony Washer
 */
public final class ThemisGitComponent
        extends ThemisScmComponent {
    /**
     * URL separator character.
     */
    public static final char SEP_URL = '/';

    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * The git directory name.
     */
    public static final String NAME_GITDIR = ".git";

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisGitComponent> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisGitComponent.class);

    /**
     * Base field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_HISTORY, ThemisGitComponent::getRevisionHistory);
    }

    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(ThemisGitComponent.class);

    /**
     * The local fields.
     */
    private final MetisFieldSet<ThemisGitComponent> theLocalFields;

    /**
     * The revision history.
     */
    private final ThemisGitRevisionHistory theHistory;

    /**
     * jGit repository access object.
     */
    private final Repository theGitRepo;

    /**
     * Constructor.
     *
     * @param pParent the Parent repository
     * @param pName   the component name
     * @throws OceanusException on error
     */
    protected ThemisGitComponent(final ThemisGitRepository pParent,
                                 final String pName) throws OceanusException {
        /* Call super constructor */
        super(pParent, pName);

        /* Access the repository */
        theGitRepo = getRepositoryAccess();

        /* Allocate the local fields */
        theLocalFields = MetisFieldSet.newFieldSet(this);

        /* Create the revision history */
        theHistory = new ThemisGitRevisionHistory(this);

        /* Create branch list */
        final ThemisGitBranchList myBranches = new ThemisGitBranchList(this);
        setBranches(myBranches);
    }

    @Override
    public MetisFieldSet<ThemisGitComponent> getDataFieldSet() {
        return theLocalFields;
    }

    @Override
    public ThemisGitRepository getRepository() {
        return (ThemisGitRepository) super.getRepository();
    }

    /**
     * Obtain GitRepository access.
     *
     * @return the access object
     */
    public Repository getGitRepo() {
        return theGitRepo;
    }

    /**
     * Obtain GitRepository access.
     *
     * @return the access object
     */
    public ThemisGitRevisionHistory getRevisionHistory() {
        return theHistory;
    }

    /**
     * Obtain Working directory.
     *
     * @return the working directory
     */
    public File getWorkingDir() {
        final File myBase = new File(getRepository().getBase());
        return new File(myBase, getName());
    }

    @Override
    public ThemisGitBranchList getBranches() {
        return (ThemisGitBranchList) super.getBranches();
    }

    /**
     * Declare a branch.
     * @param pBranch the branch
     */
    void declareBranch(final ThemisGitBranch pBranch) {
        /* Determine the name of the field */
        final ThemisGitBranchList myList = getBranches();
        String myName = ThemisResource.SCM_BRANCH.getValue();
        if (!myList.isEmpty()) {
            myName += (myList.size() + 1);
        }

        /* Declare the field and add the branch */
        theLocalFields.declareLocalField(myName, f -> pBranch);
        myList.add(pBranch);
    }

    /**
     * reDiscover history.
     *
     * @param pReport the thread report
     * @throws OceanusException on error
     */
    public void reDiscover(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Access the branches */
        final ThemisGitBranchList myBranches = getBranches();

        /* Clear maps and lists */
        theHistory.clearMaps();
        myBranches.clear();

        /* reDiscover branches */
        myBranches.discover(pReport);
    }

    /**
     * Build repository access.
     *
     * @return the Git repository
     * @throws OceanusException on error
     */
    private Repository getRepositoryAccess() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* StringBuilder */
            final StringBuilder myPathBuilder = new StringBuilder(BUFFER_LEN);
            myPathBuilder.append(getRepository().getBase())
                    .append(File.separatorChar)
                    .append(getName());

            /* Describe repository */
            final FileRepositoryBuilder myBuilder = new FileRepositoryBuilder();
            myBuilder.setWorkTree(new File(myPathBuilder.toString()))
                    .readEnvironment();

            /* Build the repository access object */
            return myBuilder.build();
        } catch (IOException e) {
            throw new ThemisIOException("Failed to access repository", e);
        }
    }

    /**
     * Get FileURL as input stream.
     *
     * @param pCommitId the commit id within which the file exists
     * @param pPath     the base path
     * @return the stream of null if file does not exists
     * @throws OceanusException on error
     */
    public ThemisMvnProjectDefinition parseProjectObject(final ThemisGitCommitId pCommitId,
                                                         final String pPath) throws OceanusException {
        InputStream myInput = null;

        /* Protect against exceptions */
        try {
            /* Build the underlying string */
            final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the initial path and POM Name */
            if (pPath.length() > 0) {
                myBuilder.append(pPath)
                        .append(SEP_URL);
            }
            myBuilder.append(ThemisMvnProjectDefinition.POM_NAME);

            /* Access object as input stream */
            myInput = getFileObjectAsInputStream(pCommitId, myBuilder.toString());
            if (myInput == null) {
                return null;
            }

            /* Parse the project definition and return it */
            final ThemisMvnProjectDefinition myProject = new ThemisMvnProjectDefinition(myInput);

            /* Loop through the subModules */
            final Iterator<ThemisMvnSubModule> myIterator = myProject.subIterator();
            while (myIterator.hasNext()) {
                final ThemisMvnSubModule myModule = myIterator.next();

                /* Reset the string buffer */
                myBuilder.setLength(0);

                /* Build the path name */
                if (pPath.length() > 0) {
                    myBuilder.append(pPath)
                            .append(SEP_URL);
                }
                myBuilder.append(myModule.getName());

                /* Parse the project Object */
                final ThemisMvnProjectDefinition mySubDef = parseProjectObject(pCommitId, myBuilder.toString());
                myModule.setProjectDefinition(mySubDef);
            }

            /* Return the definition */
            return myProject;

        } finally {
            if (myInput != null) {
                try {
                    myInput.close();
                } catch (IOException e) {
                    LOGGER.error("Close Failure", e);
                }
            }
        }
    }

    /**
     * Get FileObject as input stream.
     *
     * @param pCommitId the commit id within which the file exists
     * @param pPath     the file to stream
     * @return the stream of null if file does not exists
     * @throws OceanusException on error
     */
    public InputStream getFileObjectAsInputStream(final ThemisGitCommitId pCommitId,
                                                  final String pPath) throws OceanusException {
        /* Protect against exceptions */
        try (RevWalk myRevWalk = new RevWalk(theGitRepo);
             TreeWalk myTreeWalk = new TreeWalk(theGitRepo)) {
            /* Access the tree associated with the commit as part of a Revision Walk */
            final RevCommit myCommit = myRevWalk.parseCommit(pCommitId.getCommit());
            final RevTree myTree = myCommit.getTree();

            /* Look for the file matching the path */
            myTreeWalk.addTree(myTree);
            myTreeWalk.setRecursive(true);
            myTreeWalk.setFilter(PathFilter.create(pPath));
            if (!myTreeWalk.next()) {
                return null;
            }

            /* Prepare to load object */
            final ObjectId myId = myTreeWalk.getObjectId(0);
            final ObjectLoader myLoader = theGitRepo.open(myId);
            return myLoader.openStream();
        } catch (IOException e) {
            throw new ThemisIOException("Unable to read File Object", e);
        }
    }

    /**
     * Obtain status for component.
     *
     * @return the status
     * @throws OceanusException on error
     */
    public Status getStatus() throws OceanusException {
        /* Protect against exceptions */
        try (Git myGit = new Git(theGitRepo)) {
            final StatusCommand myCmd = myGit.status();
            return myCmd.call();
        } catch (NoWorkTreeException
                | GitAPIException e) {
            throw new ThemisIOException("Failed to get status", e);
        }
    }

    /**
     * Locate Branch.
     *
     * @param pOwner the owner to locate
     * @return the relevant branch or Null
     */
    @Override
    public ThemisGitOwner locateOwner(final ThemisScmOwner pOwner) {
        return (ThemisGitOwner) super.locateOwner(pOwner);
    }

    /**
     * Obtain gitRevision for new commit.
     *
     * @param pOwner    the owner
     * @param pRevision the revision
     * @param pCommit   the commit
     * @return the gitRevision
     */
    public ThemisGitRevision getGitRevisionForNewCommit(final ThemisScmOwner pOwner,
                                                        final String pRevision,
                                                        final RevCommit pCommit) {
        final ThemisGitOwner myOwner = locateOwner(pOwner);
        return myOwner == null
               ? null
               : theHistory.getGitRevisionForNewCommit(myOwner, pRevision, pCommit);
    }


    /**
     * Process history.
     */
    void processHistory() {
        /* Process the history */
        theHistory.processHistory();
    }

    /**
     * List of components.
     */
    public static final class ThemisGitComponentList
            extends ThemisScmComponentList {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisGitComponentList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisGitComponentList.class);

        /**
         * Parent Repository.
         */
        private final ThemisGitRepository theRepository;

        /**
         * Constructor.
         * @param pParent the parent repository
         */
        public ThemisGitComponentList(final ThemisGitRepository pParent) {
            /* Store parent/manager for use by entry handler */
            theRepository = pParent;
        }

        @Override
        public MetisFieldSet<ThemisGitComponentList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Discover component list from repository.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        public void discover(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Reset the list */
            clear();

            /* Locate files */
            final File myBaseDir = new File(theRepository.getBase());
            for (final File mySubDir : myBaseDir.listFiles()) {
                /* Ignore if not a directory */
                if (!mySubDir.isDirectory()) {
                    continue;
                }

                /* Look to see if there is a Git subDirectory */
                final File myGitDir = new File(mySubDir, NAME_GITDIR);
                if (myGitDir.isDirectory()) {
                    /* Create the component and add to the list */
                    final ThemisGitComponent myComp = new ThemisGitComponent(theRepository, mySubDir.getName());
                    theRepository.declareComponent(myComp);
                }
            }

            /* Report number of stages */
            pReport.setNumStages(size() + 2);

            /* Loop through the components */
            final Iterator<ThemisScmComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the Component */
                final ThemisGitComponent myComponent = (ThemisGitComponent) myIterator.next();
                final ThemisGitBranchList myBranches = myComponent.getBranches();

                /* Report discovery of component */
                pReport.setNewStage("Analysing component " + myComponent.getName());

                /* Discover branches for the component */
                myBranches.discover(pReport);
            }
        }

        @Override
        protected ThemisGitBranch locateBranch(final String pComponent,
                                               final String pVersion) {
            /* Pass call on */
            return (ThemisGitBranch) super.locateBranch(pComponent, pVersion);
        }

        @Override
        protected ThemisGitTag locateTag(final String pComponent,
                                         final String pVersion,
                                         final int pTag) {
            /* Pass call on */
            return (ThemisGitTag) super.locateTag(pComponent, pVersion, pTag);
        }
    }
}
