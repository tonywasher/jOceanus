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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.pack.PackConfig;
import org.eclipse.jgit.transport.BundleWriter;
import org.eclipse.jgit.transport.FetchConnection;
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
        /* Create the bundle stream */
        try (FileInputStream myInFile = new FileInputStream(pSource);
             BufferedInputStream myStream = new BufferedInputStream(myInFile)) {
            final URIish myURI = new URIish("file:" + pSource.getAbsolutePath());

            /* Create the component */
            createComponentFromBundle(pRepository, pName, myURI, myStream);

        } catch (IOException
                | URISyntaxException e) {
            throw new ThemisIOException("Failed to process bundle", e);
        }
    }

    /**
     * Create a new gitComponent from Bundle InputStream.
     * @param pRepository the gitRepository
     * @param pName the name of the component
     * @param pURI the URI of the input file
     * @param pBundle the bundle input stream
     * @throws OceanusException on error
     */
    private void createComponentFromBundle(final ThemisGitRepository pRepository,
                                           final String pName,
                                           final URIish pURI,
                                           final InputStream pBundle) throws OceanusException {
        /* Create the target component */
        final ThemisGitComponent myTarget = pRepository.createComponent(pName);

        /* Create the bundle stream */
        try (TransportBundleStream myStream = new TransportBundleStream(myTarget.getGitRepo(), pURI, pBundle)) {
            final FetchConnection myFetch = myStream.openFetch();
            final Collection<Ref> myRefs = myFetch.getRefs();

            /* Fetch what we can */
            myFetch.fetch(new GitProgressMonitor(), myRefs, Collections.emptySet());
        } catch (TransportException e) {
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

            /* Access list of branches */
            final ListBranchCommand myBrnCommand = myGit.branchList();
            myBrnCommand.setListMode(ListMode.ALL);
            final List<Ref> myBranches = myBrnCommand.call();

            /* Loop through the branches */
            Iterator<Ref> myIterator = myBranches.iterator();
            while (myIterator.hasNext()) {
                final Ref myRef = myIterator.next();

                /* Ask to include the branch */
                myWriter.include(myRef);
            }

            /* Access list of tags */
            final ListTagCommand myTagCommand = myGit.tagList();
            final List<Ref> myTags = myTagCommand.call();

            /* Loop through the tags */
            myIterator = myTags.iterator();
            while (myIterator.hasNext()) {
                final Ref myRef = myIterator.next();

                /* Ask to include the tag */
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
