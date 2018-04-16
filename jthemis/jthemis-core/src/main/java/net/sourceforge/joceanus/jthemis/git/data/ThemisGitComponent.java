/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2017 Tony Washer
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

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitBranch.ThemisGitBranchList;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition.MvnSubModule;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Represents a component in the repository.
 * @author Tony Washer
 */
public final class ThemisGitComponent
        extends ThemisScmComponent<ThemisGitComponent, ThemisGitRepository> {
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
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ThemisGitComponent.class);

    /**
     * jGit repository access object.
     */
    private final Repository theGitRepo;

    /**
     * Constructor.
     * @param pParent the Parent repository
     * @param pName the component name
     * @throws OceanusException on error
     */
    protected ThemisGitComponent(final ThemisGitRepository pParent,
                                 final String pName) throws OceanusException {
        /* Call super constructor */
        super(pParent, pName);

        /* Access the repository */
        theGitRepo = getRepositoryAccess();

        /* Create branch list */
        final ThemisGitBranchList myBranches = new ThemisGitBranchList(this);
        setBranches(myBranches);
    }

    @Override
    public MetisFieldSet<ThemisGitComponent> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain GitRepository access.
     * @return the access object
     */
    public Repository getGitRepo() {
        return theGitRepo;
    }

    /**
     * Obtain Working directory.
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
     * Build repository access.
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
            myBuilder.setWorkTree(new File(myPathBuilder.toString()));
            myBuilder.readEnvironment();
            myBuilder.findGitDir();

            /* Build the repository access object */
            return myBuilder.build();
        } catch (IOException e) {
            throw new ThemisIOException("Failed to access repository", e);
        }
    }

    /**
     * Get FileURL as input stream.
     * @param pCommitId the commit id within which the file exists
     * @param pPath the base path
     * @return the stream of null if file does not exists
     * @throws OceanusException on error
     */
    public ThemisMvnProjectDefinition parseProjectObject(final ObjectId pCommitId,
                                                         final String pPath) throws OceanusException {
        InputStream myInput = null;

        /* Protect against exceptions */
        try {
            /* Build the underlying string */
            final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the initial path and POM Name */
            myBuilder.append(pPath)
                    .append(File.separatorChar)
                    .append(ThemisMvnProjectDefinition.POM_NAME);

            /* Access object as input stream */
            myInput = getFileObjectAsInputStream(pCommitId, myBuilder.toString());
            if (myInput == null) {
                return null;
            }

            /* Parse the project definition and return it */
            final ThemisMvnProjectDefinition myProject = new ThemisMvnProjectDefinition(myInput);

            /* Loop through the subModules */
            final Iterator<MvnSubModule> myIterator = myProject.subIterator();
            while (myIterator.hasNext()) {
                final MvnSubModule myModule = myIterator.next();

                /* Reset the string buffer */
                myBuilder.setLength(0);

                /* Build the path name */
                myBuilder.append(pPath);
                myBuilder.append(ThemisSvnRepository.SEP_URL);
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
     * @param pCommitId the commit id within which the file exists
     * @param pPath the file to stream
     * @return the stream of null if file does not exists
     * @throws OceanusException on error
     */
    public InputStream getFileObjectAsInputStream(final ObjectId pCommitId,
                                                  final String pPath) throws OceanusException {
        /* Protect against exceptions */
        try (RevWalk myRevWalk = new RevWalk(theGitRepo);
             TreeWalk myTreeWalk = new TreeWalk(theGitRepo)) {
            /* Access the tree associated with the commit as part of a Revision Walk */
            final RevCommit myCommit = myRevWalk.parseCommit(pCommitId);
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
     * List of components.
     */
    public static final class ThemisGitComponentList
            extends ThemisScmComponentList<ThemisGitComponent, ThemisGitRepository> {
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
                    add(myComp);
                }
            }

            /* Report number of stages */
            pReport.setNumStages(size() + 2);

            /* Loop through the components */
            final Iterator<ThemisGitComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the Component */
                final ThemisGitComponent myComponent = myIterator.next();
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
