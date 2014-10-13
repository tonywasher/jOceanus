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
import java.io.InputStream;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.git.data.GitBranch.GitBranchList;
import net.sourceforge.joceanus.jthemis.scm.data.ScmComponent;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectDefinition.MvnSubModule;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRepository;

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

/**
 * Represents a component in the repository.
 * @author Tony Washer
 */
public final class GitComponent
        extends ScmComponent<GitComponent, GitRepository> {
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
    private static final JDataFields FIELD_DEFS = new JDataFields(GitComponent.class.getSimpleName(), ScmComponent.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * jGit repository access object.
     */
    private final Repository theGitRepo;

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
        File myBase = new File(getRepository().getBase());
        return new File(myBase, getName());
    }

    @Override
    public GitBranchList getBranches() {
        return (GitBranchList) super.getBranches();
    }

    /**
     * Constructor.
     * @param pParent the Parent repository
     * @param pName the component name
     * @throws JOceanusException on error
     */
    protected GitComponent(final GitRepository pParent,
                           final String pName) throws JOceanusException {
        /* Call super constructor */
        super(pParent, pName);

        /* Access the repository */
        theGitRepo = getRepositoryAccess();

        /* Create branch list */
        GitBranchList myBranches = new GitBranchList(this);
        setBranches(myBranches);
    }

    /**
     * Build repository access.
     * @return the Git repository
     * @throws JOceanusException on error
     */
    private Repository getRepositoryAccess() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* StringBuilder */
            StringBuilder myPathBuilder = new StringBuilder(BUFFER_LEN);
            myPathBuilder.append(getRepository().getBase());
            myPathBuilder.append(File.separatorChar);
            myPathBuilder.append(getName());

            /* Describe repository */
            FileRepositoryBuilder myBuilder = new FileRepositoryBuilder();
            myBuilder.setWorkTree(new File(myPathBuilder.toString()));
            myBuilder.readEnvironment();
            myBuilder.findGitDir();

            /* Build the repository access object */
            return myBuilder.build();
        } catch (IOException e) {
            throw new JThemisIOException("Failed to access repository", e);
        }
    }

    /**
     * Get FileURL as input stream.
     * @param pCommitId the commit id within which the file exists
     * @param pPath the base path
     * @return the stream of null if file does not exists
     * @throws JOceanusException on error
     */
    public MvnProjectDefinition parseProjectObject(final ObjectId pCommitId,
                                                   final String pPath) throws JOceanusException {
        InputStream myInput = null;

        /* Protect against exceptions */
        try {
            /* Build the underlying string */
            StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the initial path */
            myBuilder.append(pPath);
            myBuilder.append(File.separatorChar);

            /* Build the POM name */
            myBuilder.append(MvnProjectDefinition.POM_NAME);

            /* Access object as input stream */
            myInput = getFileObjectAsInputStream(pCommitId, myBuilder.toString());
            if (myInput == null) {
                return null;
            }

            /* Parse the project definition and return it */
            MvnProjectDefinition myProject = new MvnProjectDefinition(myInput);

            /* Loop through the subModules */
            Iterator<MvnSubModule> myIterator = myProject.subIterator();
            while (myIterator.hasNext()) {
                MvnSubModule myModule = myIterator.next();

                /* Reset the string buffer */
                myBuilder.setLength(0);

                /* Build the path name */
                myBuilder.append(pPath);
                myBuilder.append(SvnRepository.SEP_URL);
                myBuilder.append(myModule.getName());

                /* Parse the project Object */
                MvnProjectDefinition mySubDef = parseProjectObject(pCommitId, myBuilder.toString());
                myModule.setProjectDefinition(mySubDef);
            }

            /* Return the definition */
            return myProject;

        } finally {
            if (myInput != null) {
                try {
                    myInput.close();
                } catch (IOException e) {
                    getLogger().error("Close Failure", e);
                }
            }
        }
    }

    /**
     * Get FileObject as input stream.
     * @param pCommitId the commit id within which the file exists
     * @param pPath the file to stream
     * @return the stream of null if file does not exists
     * @throws JOceanusException on error
     */
    public InputStream getFileObjectAsInputStream(final ObjectId pCommitId,
                                                  final String pPath) throws JOceanusException {
        /* Allocate walkers */
        RevWalk myRevWalk = new RevWalk(theGitRepo);
        TreeWalk myTreeWalk = new TreeWalk(theGitRepo);

        /* Protect against exceptions */
        try {
            /* Access the tree associated with the commit as part of a Revision Walk */
            RevCommit myCommit = myRevWalk.parseCommit(pCommitId);
            RevTree myTree = myCommit.getTree();

            /* Look for the file matching the path */
            myTreeWalk.addTree(myTree);
            myTreeWalk.setRecursive(true);
            myTreeWalk.setFilter(PathFilter.create(pPath));
            if (!myTreeWalk.next()) {
                return null;
            }

            /* Prepare to load object */
            ObjectId myId = myTreeWalk.getObjectId(0);
            ObjectLoader myLoader = theGitRepo.open(myId);
            return myLoader.openStream();
        } catch (IOException e) {
            throw new JThemisIOException("Unable to read File Object", e);
        } finally {
            myRevWalk.release();
            myTreeWalk.release();
        }
    }

    /**
     * Obtain status for component.
     * @return the status
     * @throws JOceanusException on error
     */
    public Status getStatus() throws JOceanusException {
        /* Protect against exceptions */
        try {
            Git myGit = new Git(theGitRepo);
            StatusCommand myCmd = myGit.status();
            return myCmd.call();
        } catch (NoWorkTreeException
                | GitAPIException e) {
            throw new JThemisIOException("Failed to get status", e);
        }
    }

    /**
     * List of components.
     */
    public static final class GitComponentList
            extends ScmComponentList<GitComponent, GitRepository> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(GitComponentList.class.getSimpleName(), ScmComponentList.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Parent Repository.
         */
        private final GitRepository theRepository;

        /**
         * Constructor.
         * @param pParent the parent repository
         */
        public GitComponentList(final GitRepository pParent) {
            /* Call super constructor */
            super(GitComponent.class);

            /* Store parent/manager for use by entry handler */
            theRepository = pParent;
        }

        /**
         * Discover component list from repository.
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        public void discover(final ReportStatus pReport) throws JOceanusException {
            /* Reset the list */
            clear();

            /* Locate files */
            File myBaseDir = new File(theRepository.getBase());
            for (File mySubDir : myBaseDir.listFiles()) {
                /* Ignore if not a directory */
                if (!mySubDir.isDirectory()) {
                    continue;
                }

                /* Look to see if there is a Git subDirectory */
                File myGitDir = new File(mySubDir, NAME_GITDIR);
                if (myGitDir.isDirectory()) {
                    /* Create the component and add to the list */
                    GitComponent myComp = new GitComponent(theRepository, mySubDir.getName());
                    add(myComp);
                }
            }

            /* Report number of stages */
            if (!pReport.setNumStages(size() + 2)) {
                return;
            }

            /* Loop through the components */
            Iterator<GitComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the Component */
                GitComponent myComponent = myIterator.next();
                GitBranchList myBranches = myComponent.getBranches();

                /* Report discovery of component */
                if (!pReport.setNewStage("Analysing component " + myComponent.getName())) {
                    break;
                }

                /* Discover branches for the component */
                myBranches.discover(pReport);
            }
        }

        @Override
        protected GitBranch locateBranch(final String pComponent,
                                         final String pVersion) {
            /* Pass call on */
            return (GitBranch) super.locateBranch(pComponent, pVersion);
        }

        @Override
        protected GitTag locateTag(final String pComponent,
                                   final String pVersion,
                                   final int pTag) {
            /* Pass call on */
            return (GitTag) super.locateTag(pComponent, pVersion, pTag);
        }
    }
}
