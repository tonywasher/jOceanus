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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;

/**
 * Revision History for a Git Component.
 */
public class ThemisGitRevisionHistory
        implements MetisFieldItem {
    /**
     * The SubVersion commit header.
     */
    private static final String SVN_COMMIT_HDR = "svn";

    /**
     * The SubVersion commit separator.
     */
    private static final String SVN_COMMIT_SEP = ":";

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisGitRevisionHistory> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisGitRevisionHistory.class);

    /**
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT, ThemisGitRevisionHistory::getComponent);
        FIELD_DEFS.declareLocalField(ThemisResource.GIT_COMMITMAP, ThemisGitRevisionHistory::getCommitMap);
        FIELD_DEFS.declareLocalField(ThemisResource.GIT_REVISIONMAP, ThemisGitRevisionHistory::getRevisionMap);
    }

    /**
     * The Git Component.
     */
    private final ThemisGitComponent theComponent;

    /**
     * The map of commitIds.
     */
    private final Map<ThemisGitCommitId, ThemisGitRevision> theCommitMap;

    /**
     * The map of revisions.
     */
    private final Map<String, ThemisGitRevision> theRevisionMap;

    /**
     * Constructor.
     * @param pComponent the component
     */
    ThemisGitRevisionHistory(final ThemisGitComponent pComponent) {
        theComponent = pComponent;
        theCommitMap = new HashMap<>();
        theRevisionMap = new HashMap<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the component.
     * @return the component
     */
    private ThemisGitComponent getComponent() {
        return theComponent;
    }

    /**
     * Obtain the commitMap.
     * @return the commitMap
     */
    private Map<ThemisGitCommitId, ThemisGitRevision> getCommitMap() {
        return theCommitMap;
    }

    /**
     * Obtain the commitMap.
     * @return the commitMap
     */
    private Map<String, ThemisGitRevision> getRevisionMap() {
        return theRevisionMap;
    }

    /**
     * Clear the maps.
     */
    public void clearMaps() {
        theCommitMap.clear();
        theRevisionMap.clear();
    }

    /**
     * Parse commit history.
     * @param pOwner the owner of the commit
     * @param pCommitId the commit id
     * @throws OceanusException on error
     */
    public void parseCommitHistory(final ThemisGitOwner pOwner,
                                   final ThemisGitCommitId pCommitId) throws OceanusException {
        /* Protect against exceptions */
        try (RevWalk myRevWalk = new RevWalk(theComponent.getGitRepo())) {
            /* Access the primary commit and initiate the walk using it */
            final RevCommit myCommit = myRevWalk.parseCommit(pCommitId.getCommit());
            myRevWalk.markStart(myCommit);

            /* Walk the revision tree */
            final Iterator<RevCommit> myIterator = myRevWalk.iterator();
            while (myIterator.hasNext()) {
                processCommit(pOwner, myIterator.next());
            }

        } catch (IOException e) {
            throw new ThemisIOException("Unable to read File Object", e);
        }
    }

    /**
     * Process the commit.
     * @param pOwner the owner of the commit
     * @param pCommit the commit
     * @throws OceanusException on error
     */
    public void processCommit(final ThemisGitOwner pOwner,
                              final RevCommit pCommit) throws OceanusException {
        /* Check to see whether the commit is already processed */
        final ThemisGitCommitId myCommitId = new ThemisGitCommitId(pCommit);
        if (theCommitMap.get(myCommitId) != null) {
            return;
        }

        /* Add the commit to the map */
        final ThemisGitRevision myRevision = new ThemisGitRevision(pOwner, myCommitId);
        theCommitMap.put(myCommitId, myRevision);

        /* If this is subVersion */
        if (myRevision.isSubversion()) {
            /* Add to the revision map */
            theRevisionMap.put(myRevision.getRevisionKey(), myRevision);
        }
    }

    @Override
    public String toString() {
        return ThemisGitRevisionHistory.class.getSimpleName();
    }

    /**
     * Obtain commit for revisionKey.
     * @param pOwner the owner
     * @param pRevision the revision
     * @return the commit or null if not found
     */
    protected ThemisGitRevision getGitRevisionForRevisionKey(final ThemisGitOwner pOwner,
                                                             final String pRevision) {
        final String myKey = pOwner.getName() + SVN_COMMIT_SEP + pRevision;
        return theRevisionMap.get(myKey);
    }

    /**
     * Obtain commit for new commit.
     * @param pOwner the owner
     * @param pRevision the revision
     * @param pCommit the new commit
     * @return the commit or null if not found
     */
    protected ThemisGitRevision getGitRevisionForNewCommit(final ThemisGitOwner pOwner,
                                                           final String pRevision,
                                                           final RevCommit pCommit) {
        return new ThemisGitRevision(pOwner, pRevision, pCommit);
    }

    /**
     * create gitLogMessage.
     * @param pRevision the revision
     * @param pLogMessage the subversion log message
     * @return the gitMessage
     */
    public static String createGitLogMessage(final String pRevision,
                                             final String pLogMessage) {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(SVN_COMMIT_HDR)
                .append(pRevision)
                .append(SVN_COMMIT_SEP)
                .append(" ")
                .append(pLogMessage);
        return myBuilder.toString();
    }

    /**
     * gitCommitId.
     */
    public static class ThemisGitCommitId
            implements MetisDataObjectFormat {
        /**
         * The Commit.
         */
        private final RevCommit theCommit;

        /**
         * Constructor.
         * @param pCommit the commit
         */
        public ThemisGitCommitId(final RevCommit pCommit) {
            /* Store parameters */
            theCommit = pCommit;
        }

        /**
         * Obtain the commit.
         * @return the commit
         */
        public RevCommit getCommit() {
            return theCommit;
        }

        /**
         * Obtain the commitId.
         * @return the commitId
         */
        public String getCommitId() {
            return theCommit.getId().name();
        }

        @Override
        public String toString() {
            return getCommitId();
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (pThat == this) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check for correct class */
            if (!(pThat instanceof ThemisGitCommitId)) {
                return false;
            }
            final ThemisGitCommitId myThat = (ThemisGitCommitId) pThat;

            /* Check id */
            return getCommitId().equals(myThat.getCommitId());
        }

        @Override
        public int hashCode() {
            return theCommit.getId().hashCode();
        }
    }

    /**
     * The gitRevision.
     */
    public static class ThemisGitRevision
            implements MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisGitRevision> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisGitRevision.class);

        /**
         * fieldIds.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.GIT_COMMITID, ThemisGitRevision::getCommitId);
            FIELD_DEFS.declareLocalField(ThemisResource.SCM_OWNER, ThemisGitRevision::getOwner);
            FIELD_DEFS.declareLocalField(ThemisResource.GIT_REVISIONNO, ThemisGitRevision::getGitRevisionNo);
            FIELD_DEFS.declareLocalField(ThemisResource.GIT_PARENTS, ThemisGitRevision::getParents);
        }

        /**
         * The CommitId of the revision.
         */
        private final ThemisGitCommitId theCommitId;

        /**
         * The owning branch/tag.
         */
        private final ThemisGitOwner theOwner;

        /**
         * The corresponding subVersion revision.
         */
        private final String theRevision;

        /**
         * The Parent commitIds.
         */
        private final List<ThemisGitCommitId> theParents;

        /**
         * Constructor.
         * @param pOwner the commit owner
         * @param pCommitId the commitId
         */
        ThemisGitRevision(final ThemisGitOwner pOwner,
                          final ThemisGitCommitId pCommitId) {
            /* Store parameters */
            theCommitId = pCommitId;
            theOwner = pOwner;

            /* Add the parents to the list */
            theParents = new ArrayList<>();
            processParents();

            /* Access the commit message */
            theRevision = determineSvnRevision();
        }

        /**
         * Constructor.
         * @param pOwner the commit owner
         * @param pRevision the revision
         * @param pCommit the commit
         */
        ThemisGitRevision(final ThemisGitOwner pOwner,
                          final String pRevision,
                          final RevCommit pCommit) {
            /* Store parameters */
            theCommitId = new ThemisGitCommitId(pCommit);
            theOwner = pOwner;
            theRevision = pRevision;

            /* Add the parents to the list */
            theParents = new ArrayList<>();
            processParents();
        }

        /**
         * Determine svnRevision from commit message.
         * @return the revision (or null)
         */
        private String determineSvnRevision() {
            /* Access the commit message */
            final String myMessage = theCommitId.getCommit().getFullMessage();
            final int myIndex = myMessage.indexOf(SVN_COMMIT_SEP);
            if (myIndex != -1
                && myMessage.startsWith(SVN_COMMIT_HDR)) {
                final String mySvnRev = myMessage.substring(SVN_COMMIT_HDR.length(), myIndex);
                return mySvnRev.matches("\\d+(\\.\\d+)?")
                                                          ? mySvnRev
                                                          : null;
            }
            return null;
        }

        /**
         * Process parents.
         */
        private void processParents() {
            /* Process parents */
            for (RevCommit myCommit : getCommit().getParents()) {
                theParents.add(new ThemisGitCommitId(myCommit));
            }
        }

        @Override
        public String toString() {
            final String myRevision = theRevision == null
                                                          ? ""
                                                          : getRevisionKey();
            return myRevision + "@" + theCommitId.toString();
        }

        /**
         * Obtain the commit.
         * @return the commit
         */
        public RevCommit getCommit() {
            return theCommitId.getCommit();
        }

        /**
         * Obtain the commit.
         * @return the commit
         */
        public ThemisGitCommitId getCommitId() {
            return theCommitId;
        }

        /**
         * Obtain the owner.
         * @return the revision
         */
        public ThemisGitOwner getOwner() {
            return theOwner;
        }

        /**
         * Is the commit a subversion revision.
         * @return true/false
         */
        public boolean isSubversion() {
            return theRevision != null;
        }

        /**
         * Obtain the git revision#.
         * @return the revision
         */
        public String getGitRevisionNo() {
            return theRevision;
        }

        /**
         * Obtain the parent list.
         * @return the parent list
         */
        public List<ThemisGitCommitId> getParents() {
            return theParents;
        }

        /**
         * Does the revision have parents.
         * @return true/false
         */
        public boolean hasParents() {
            return !theParents.isEmpty();
        }

        /**
         * Obtain the revisionKey.
         * @return the revisionKey
         */
        public String getRevisionKey() {
            return isSubversion()
                                  ? theOwner.getName() + SVN_COMMIT_SEP + theRevision
                                  : null;
        }

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
