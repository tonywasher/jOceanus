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
package net.sourceforge.joceanus.jthemis.git.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.api.DeleteBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NotSupportedException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.NullProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.BundleWriter;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.TransportBundleStream;
import org.eclipse.jgit.transport.URIish;

import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;

/**
 * Themis gitBundle support.
 */
public final class ThemisGitBundle {
    /**
     * Bundle load error.
     */
    private static final String ERROR_CREATE = "Failed to create bundle";

    /**
     * Bundle remote name.
     */
    static final String REMOTE_ORIGIN = "origin";

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
                final String myName = ThemisGitBranch.getRemoteBranchName(myRef, REMOTE_ORIGIN);
                if (myName == null || myName.equals(ThemisGitBranch.BRN_MASTER)) {
                    continue;
                }

                /* Pull the contents of the remote branch */
                final PullCommand myPull = myGit.pull();
                myPull.setRemote(REMOTE_ORIGIN);
                myPull.setRemoteBranchName(myName);
                myPull.call();

                /* Delete the remote branch */
                final DeleteBranchCommand myDelete = myGit.branchDelete();
                myDelete.setBranchNames(REMOTE_ORIGIN + "/" + myName);
                myDelete.call();
            }

            /* Delete the remote configuration */
            //final RemoteRemoveCommand myRemove = myGit.remoteRemove();
            //myRemove.setName(REMOTE_BUNDLE);
            //myRemove.call();

        } catch (GitAPIException e) {
            throw new ThemisIOException("Failed to process bundle", e);
        }
    }

    /**
     * Create a new gitComponent from Bundle.
     * @param pRepository the gitRepository
     * @param pSource the source file
     * @throws OceanusException on error
     */
    public void createComponentFromBundle(final ThemisGitRepository pRepository,
                                          final File pSource) throws OceanusException {
        /* Create the bundle stream */
        try (FileInputStream myInFile = new FileInputStream(pSource);
             BufferedInputStream myStream = new BufferedInputStream(myInFile)) {
            /* Create the component */
            createComponentFromBundle(pRepository, "Test", myStream);

        } catch (IOException e) {
            throw new ThemisIOException(ERROR_CREATE, e);
        }
    }

    /**
     * Create a new gitComponent from Bundle InputStream.
     * @param pRepository the gitRepository
     * @param pName the name of the component
     * @param pBundle the bundle input stream
     * @throws OceanusException on error
     */
    public void createComponentFromBundle(final ThemisGitRepository pRepository,
                                          final String pName,
                                          final InputStream pBundle) throws OceanusException {
        /* Create the target component */
        final ThemisGitComponent myTarget = pRepository.createComponent(pName);
        final URIish myURI = getURI(myTarget);

        /* Create the bundle stream */
        try (TransportBundleStream myStream = new TransportBundleStream(myTarget.getGitRepo(), myURI, pBundle)) {
            /* Define all refs */
            final RefSpec rs = new RefSpec("refs/heads/*:refs/heads/*");
            final Set<RefSpec> myRefs = Collections.singleton(rs);

            /* Fetch what we can */
            myStream.fetch(NullProgressMonitor.INSTANCE, myRefs);

        } catch (TransportException
                 | NotSupportedException e) {
            throw new ThemisIOException("Failed to process bundle", e);
        }
    }

    /**
     * Create URI for a bundled component.
     * @param pComponent the component
     * @throws OceanusException on error
     * @return the URI
     */
    public URIish getURI(final ThemisGitComponent pComponent) throws OceanusException {

        /* Protect against exceptions */
        try {
            /* Obtain the full name of the file */
            return new URIish(pComponent.getWorkingDir().getAbsolutePath());

        } catch (URISyntaxException e) {
            throw new ThemisIOException("URI error", e);
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
            throw new ThemisIOException(ERROR_CREATE, e);
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
            Iterator<Ref> myIterator = myBranches.iterator();
            while (myIterator.hasNext()) {
                final Ref myRef = myIterator.next();

                /* include any local branch */
                myWriter.include(myRef);
            }

            /* Access list of local tags */
            final ListTagCommand myTagCommand = myGit.tagList();
            final List<Ref> myTags = myTagCommand.call();

            /* Loop through the tags */
            myIterator = myTags.iterator();
            while (myIterator.hasNext()) {
                final Ref myRef = myIterator.next();

                /* include any tag */
                myWriter.include(myRef);
            }

            /* Write to the output stream */
            myWriter.writeBundle(NullProgressMonitor.INSTANCE, pBundle);

        } catch (IOException
                | GitAPIException e) {
            throw new ThemisIOException(ERROR_CREATE, e);
        }
    }
}
