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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.RemoteRemoveCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.BundleWriter;

import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;

/**
 * Themis gitBundle support.
 */
public final class ThemisGitBundle {
    /**
     * Bundle remote name.
     */
    protected static final String REMOTE_BUNDLE = "bundle";

    /**
     * The Status reporter.
     */
    private final MetisThreadStatusReport theStatus;

    /**
     * Private constructor.
     * @param pStatus the report status
     */
    public ThemisGitBundle(final MetisThreadStatusReport pStatus) {
        theStatus = pStatus;
    }

    /**
     * Create a new gitComponent from Bundle InputStream.
     * @param pRepository the gitRepository
     * @param pName the name of the component
     * @param pSource the source file
     * @throws OceanusException on error
     */
    public void createComponentFromBundle(final ThemisGitRepository pRepository,
                                          final String pName,
                                          final File pSource) throws OceanusException {
        /* Create the component */
        final ThemisGitComponent myComp = pRepository.createComponent(pName, pSource);

        /* Pull all branches */
        try (Git myGit = new Git(myComp.getGitRepo())) {
            /* Access list of remote branches */
            final ListBranchCommand myCommand = myGit.branchList();
            myCommand.setListMode(ListMode.REMOTE);
            final List<Ref> myBranches = myCommand.call();

            /* Loop through the branches */
            final Iterator<Ref> myIterator = myBranches.iterator();
            while (myIterator.hasNext()) {
                final Ref myRef = myIterator.next();

                /* Convert to remote branch name */
                final String myName = ThemisGitBranch.getRemoteBranchName(myRef, REMOTE_BUNDLE);
                if (myName == null) {
                    continue;
                }

                /* Pull the contents of the remote branch */
                final PullCommand myPull = myGit.pull();
                myPull.setRemote(REMOTE_BUNDLE);
                myPull.setRemoteBranchName(myName);
                myPull.call();

                /* Delete the remote branch */
                final DeleteBranchCommand myDelete = myGit.branchDelete();
                myDelete.setBranchNames(REMOTE_BUNDLE + "/" + myName);
                myDelete.call();
            }

            /* Delete the remote configuration */
            final RemoteRemoveCommand myRemove = myGit.remoteRemove();
            myRemove.setName(REMOTE_BUNDLE);
            myRemove.call();

        } catch (GitAPIException e) {
            throw new ThemisIOException("Failed to process bundle", e);
        }
    }

    /**
     * Create a new gitBundle from component.
     * @param pComponent the component
     * @param pTarget the target file
     * @throws OceanusException on error
     */
    public void createBundleFromComponent(final ThemisGitComponent pComponent,
                                          final File pTarget) throws OceanusException {
        /* Create the bundle stream */
        try (FileOutputStream myOutFile = new FileOutputStream(pTarget);
             BufferedOutputStream myStream = new BufferedOutputStream(myOutFile)) {
            /* Create the bundle */
            createBundleFromComponent(pComponent, myStream);

        } catch (IOException e) {
            throw new ThemisIOException("Failed to create bundle", e);
        }
    }

    /**
     * Write a gitBundle for a gitComponent to OutputStream.
     * @param pComponent the gitComponent
     * @param pBundle the bundle output stream
     * @throws OceanusException on error
     */
    private void createBundleFromComponent(final ThemisGitComponent pComponent,
                                           final OutputStream pBundle) throws OceanusException {
        /* Create the BundleWriter */
        final Repository myRepo = pComponent.getGitRepo();
        try (Git myGit = new Git(myRepo)) {
            final BundleWriter myWriter = new BundleWriter(myRepo);
            myWriter.setPackConfig(new PackConfig(myRepo));

            /* Access list of local branches */
            final ListBranchCommand myBrnCommand = myGit.branchList();
            final List<Ref> myBranches = myBrnCommand.call();

            /* Loop through the branches */
            final Iterator<Ref> myIterator = myBranches.iterator();
            while (myIterator.hasNext()) {
                final Ref myRef = myIterator.next();

                /* include any local branch */
                myWriter.include(myRef);
            }

            /* Write to the output stream */
            myWriter.writeBundle(new GitProgressMonitor(), pBundle);

        } catch (IOException
                | GitAPIException e) {
            throw new ThemisIOException("Failed to create bundle", e);
        }
    }

    /**
     * Progress Monitor class.
     */
    protected final class GitProgressMonitor
            implements ProgressMonitor {
        @Override
        public boolean isCancelled() {
            try {
                theStatus.checkForCancellation();
            } catch (OceanusException e) {
                return true;
            }
            return false;
        }

        @Override
        public void beginTask(final String pTitle,
                              final int pTotalWork) {
            /* Not needed */
        }

        @Override
        public void start(final int pTotalTasks) {
            /* Not needed */
        }

        @Override
        public void update(final int pCompleted) {
            /* Not needed */
        }

        @Override
        public void endTask() {
            /* Not needed */
        }
    }
}
