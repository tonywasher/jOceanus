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
package net.sourceforge.joceanus.jthemis.svn.tasks;

import java.io.File;
import java.util.Collection;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;

import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnTag;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy.SvnWorkingCopySet;

/**
 * Handles checkOut of a URL to a new working copy directory.
 * @author Tony Washer
 */
public class ThemisCheckOut {
    /**
     * Repository.
     */
    private final ThemisSvnRepository theRepository;

    /**
     * Report object.
     */
    private final MetisThreadStatusReport theReport;

    /**
     * Client manager.
     */
    private final SVNClientManager theMgr;

    /**
     * Event Handler.
     */
    private final CheckOutHandler theHandler = new CheckOutHandler();

    /**
     * Constructor.
     * @param pRepository the repository
     * @param pReport the report object
     */
    public ThemisCheckOut(final ThemisSvnRepository pRepository,
                          final MetisThreadStatusReport pReport) {
        /* Store parameters */
        theRepository = pRepository;
        theReport = pReport;
        theMgr = theRepository.getClientManager();
        theMgr.setEventHandler(theHandler);
    }

    /**
     * Dispose of resources.
     */
    public void dispose() {
        theRepository.releaseClientManager(theMgr);
    }

    /**
     * CheckOut the Branch to the specified directory.
     * @param pBranch the branch to check out.
     * @param pRevision the revision to check out
     * @param pPath the path to check out to.
     * @throws OceanusException on error
     */
    private void checkOutBranch(final ThemisSvnBranch pBranch,
                                final SVNRevision pRevision,
                                final File pPath) throws OceanusException {
        /* Access update client */
        SVNUpdateClient myUpdate = theMgr.getUpdateClient();

        /* Protect against exceptions */
        try {
            /* Checkout the branch */
            SVNURL myURL = pBranch.getURL();
            theReport.setNewStage(pBranch.getComponent().getName());
            // theReport.setNumStages(pBranch.getNumElements());
            myUpdate.doCheckout(myURL, pPath, SVNRevision.HEAD, pRevision, SVNDepth.INFINITY, false);
        } catch (SVNException e) {
            throw new ThemisIOException("Failed to checkOut branch "
                                        + pBranch.getBranchName(), e);
        }
    }

    /**
     * Switch the Branch at the specified directory.
     * @param pBranch the branch to check out.
     * @param pPath the path to check out to.
     * @throws OceanusException on error
     */
    private void switchBranch(final ThemisSvnBranch pBranch,
                              final File pPath) throws OceanusException {
        /* Access update client */
        SVNUpdateClient myUpdate = theMgr.getUpdateClient();

        /* Protect against exceptions */
        try {
            /* Checkout the branch */
            SVNURL myURL = pBranch.getURL();
            myUpdate.doSwitch(pPath, myURL, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false, true);
        } catch (SVNException e) {
            throw new ThemisIOException("Failed to switch branch "
                                        + pBranch.getBranchName(), e);
        }
    }

    /**
     * Update working copy.
     * @param pPath the path to update.
     * @throws OceanusException on error
     */
    private void updateWorkingCopy(final File pPath) throws OceanusException {
        /* Access update client */
        SVNUpdateClient myUpdate = theMgr.getUpdateClient();

        /* Protect against exceptions */
        try {
            /* Update the working copy */
            myUpdate.doUpdate(pPath, SVNRevision.HEAD, SVNDepth.INFINITY, true, true);
        } catch (SVNException e) {
            throw new ThemisIOException("Failed to update path "
                                        + pPath.getPath(), e);
        }
    }

    /**
     * Revert working copy.
     * @param pPath the path to revert.
     * @throws OceanusException on error
     */
    private void revertWorkingCopy(final File pPath) throws OceanusException {
        /* Access update client */
        SVNWCClient myClient = theMgr.getWCClient();

        /* Allocate single path array */
        File[] myPaths =
        { pPath };

        /* Protect against exceptions */
        try {
            /* Revert the working copy */
            myClient.doRevert(myPaths, SVNDepth.INFINITY, null);
        } catch (SVNException e) {
            throw new ThemisIOException("Failed to revert path "
                                        + pPath.getPath(), e);
        }
    }

    /**
     * Export the Tag to the specified directory.
     * @param pTag the tag to export.
     * @param pPath the path to export to.
     * @throws OceanusException on error
     */
    private void exportTag(final ThemisSvnTag pTag,
                           final File pPath) throws OceanusException {
        /* Access update client */
        SVNUpdateClient myUpdate = theMgr.getUpdateClient();

        /* Protect against exceptions */
        try {
            /* Export the tag */
            SVNURL myURL = pTag.getURL();
            myUpdate.doExport(myURL, pPath, SVNRevision.HEAD, SVNRevision.HEAD, null, false, SVNDepth.INFINITY);
        } catch (SVNException e) {
            throw new ThemisIOException("Failed to export tag "
                                        + pTag.getTagName(), e);
        }
    }

    /**
     * Check Out the Branches to the specified directory.
     * @param pBranches the branches to check out.
     * @param pRevision the revision to check out
     * @param pPath the path to export to.
     * @throws OceanusException on error
     */
    public void checkOutBranches(final Collection<ThemisSvnBranch> pBranches,
                                 final SVNRevision pRevision,
                                 final File pPath) throws OceanusException {
        /* Loop through branches */
        for (ThemisSvnBranch myBranch : pBranches) {
            /* Check for cancellation */
            theReport.checkForCancellation();

            /* Build path */
            File myPath = new File(pPath, myBranch.getComponent().getName());

            /* CheckOut the branch */
            checkOutBranch(myBranch, pRevision, myPath);
        }
    }

    /**
     * Switch the Branches in the specified directory.
     * @param pBranches the branches to switch.
     * @param pPath the path to export to.
     * @throws OceanusException on error
     */
    public void switchBranches(final Collection<ThemisSvnBranch> pBranches,
                               final File pPath) throws OceanusException {
        /* Loop through branches */
        for (ThemisSvnBranch myBranch : pBranches) {
            /* Build path */
            File myPath = new File(pPath, myBranch.getComponent().getName());

            /* Switch the branch */
            switchBranch(myBranch, myPath);
        }
    }

    /**
     * Update Working Set.
     * @param pSet the working set to update.
     * @throws OceanusException on error
     */
    public void updateWorkingCopySet(final SvnWorkingCopySet pSet) throws OceanusException {
        /* Loop through copies */
        for (ThemisSvnWorkingCopy myCopy : pSet) {
            /* Access path */
            File myPath = myCopy.getLocation();

            /* update the working copy */
            updateWorkingCopy(myPath);
        }
    }

    /**
     * Revert Working Set.
     * @param pSet the working set to revert.
     * @throws OceanusException on error
     */
    public void revertWorkingCopySet(final SvnWorkingCopySet pSet) throws OceanusException {
        /* Loop through copies */
        for (ThemisSvnWorkingCopy myCopy : pSet) {
            /* Access path */
            File myPath = myCopy.getLocation();

            /* revert the working copy */
            revertWorkingCopy(myPath);
        }
    }

    /**
     * Export the Tags to the specified directory.
     * @param pTags the tags to export.
     * @param pPath the path to export to.
     * @throws OceanusException on error
     */
    public void exportTags(final Collection<ThemisSvnTag> pTags,
                           final File pPath) throws OceanusException {
        /* Loop through tags */
        for (ThemisSvnTag myTag : pTags) {
            /* Check for cancellation */
            theReport.checkForCancellation();

            /* Build path */
            File myPath = new File(pPath, myTag.getComponent().getName());

            /* Export the tag */
            exportTag(myTag, myPath);
        }
    }

    /**
     * EventHandler.
     */
    private final class CheckOutHandler
            implements ISVNEventHandler {
        /**
         * Prefix.
         */
        private String thePrefix = null;

        @Override
        public void checkCancelled() throws SVNCancelException {
            try {
                theReport.checkForCancellation();
            } catch (OceanusException e) {
                throw new SVNCancelException();
            }
        }

        /**
         * Adjust file name.
         * @param pEvent the event.
         * @return the adjusted name
         */
        private String adjustName(final SVNEvent pEvent) {
            /* Obtain the name */
            String myName = pEvent.getFile().getPath();
            if ((thePrefix != null)
                && (myName.startsWith(thePrefix))) {
                myName = myName.substring(thePrefix.length());
            }
            return myName;
        }

        @Override
        public void handleEvent(final SVNEvent pEvent,
                                final double pProgress) throws SVNException {
            try {
                /* Access the Action */
                SVNEventAction myAction = pEvent.getAction();

                /* If this is the start of the checkOut */
                if (myAction.equals(SVNEventAction.UPDATE_STARTED)) {
                    /* Record the prefix */
                    thePrefix = pEvent.getFile().getPath();

                } else if (myAction.equals(SVNEventAction.UPDATE_COMPLETED)) {
                    /* Clear the prefix */
                    thePrefix = null;

                } else if (myAction.equals(SVNEventAction.UPDATE_ADD)) {
                    /* Report activity */
                    theReport.setNewStage("A "
                                          + adjustName(pEvent));

                } else if (myAction.equals(SVNEventAction.UPDATE_UPDATE)) {
                    /* Report activity */
                    theReport.setNewStage("U "
                                          + adjustName(pEvent));

                } else if (myAction.equals(SVNEventAction.UPDATE_DELETE)) {
                    /* Report activity */
                    theReport.setNewStage("D "
                                          + adjustName(pEvent));

                } else {
                    theReport.setNewStage(pEvent.getFile().getPath());
                }
            } catch (OceanusException e) {
                throw new SVNCancelException();
            }
        }
    }
}