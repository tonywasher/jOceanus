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

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitCommitId;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitRevision;

/**
 * Revision History for a Git Component.
 */
public class ThemisGitRevisionHistory
        implements MetisDataObjectFormat, MetisDataMap<ThemisGitCommitId, ThemisGitRevision> {
    /**
     * The Git Component.
     */
    private final ThemisGitComponent theComponent;

    /**
     * The map of commitIds.
     */
    private final Map<ThemisGitCommitId, ThemisGitRevision> theCommitMap;

    /**
     * The queue of commitIds.
     */
    private final Queue<ObjectId> theQueue;

    /**
     * Constructor.
     * @param pComponent the component
     */
    ThemisGitRevisionHistory(final ThemisGitComponent pComponent) {
        theComponent = pComponent;
        theCommitMap = new HashMap<>();
        theQueue = new ArrayDeque<>();
    }

    @Override
    public Map<ThemisGitCommitId, ThemisGitRevision> getUnderlyingMap() {
        return theCommitMap;
    }

    /**
     * Parse commit history.
     * @param pCommitId the commit id
     * @throws OceanusException on error
     */
    public void parseCommitHistory(final ThemisGitCommitId pCommitId) throws OceanusException {
        /* Protect against exceptions */
        try (RevWalk myRevWalk = new RevWalk(theComponent.getGitRepo())) {
            /* Access the primary commit and process it */
            final RevCommit myCommit = myRevWalk.parseCommit(pCommitId.getCommit());
            processCommit(myCommit);

            /* While there are parent commits in the queue */
            while (!theQueue.isEmpty()) {
                /* Process the next commit in the queue */
                final ObjectId myId = theQueue.poll();
                processCommit(myRevWalk.parseCommit(myId));
            }

        } catch (IOException e) {
            throw new ThemisIOException("Unable to read File Object", e);
        }
    }

    /**
     * Process the commit.
     * @param pCommit the commit
     * @throws OceanusException on error
     */
    public void processCommit(final RevCommit pCommit) throws OceanusException {
        /* Check to see whether the commit is already processed */
        final ThemisGitCommitId myCommitId = new ThemisGitCommitId(pCommit);
        if (theCommitMap.get(myCommitId) != null) {
            return;
        }

        /* Add the commit to the map */
        final ThemisGitRevision myRevision = new ThemisGitRevision(myCommitId);
        theCommitMap.put(myCommitId, myRevision);

        /* Add all parents to the input queue */
        for (RevCommit myCommit : pCommit.getParents()) {
            theQueue.add(myCommit.getId());
        }
    }

    @Override
    public String toString() {
        return ThemisGitRevisionHistory.class.getSimpleName();
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
            FIELD_DEFS.declareLocalField(ThemisResource.GIT_PARENTS, ThemisGitRevision::getParents);
        }

        /**
         * The CommitId of the revision.
         */
        private final ThemisGitCommitId theCommitId;

        /**
         * The Parent commitIds.
         */
        private final List<ThemisGitCommitId> theParents;

        /**
         * Constructor.
         * @param pCommitId the commitId
         */
        ThemisGitRevision(final ThemisGitCommitId pCommitId) {
            /* Store parameters */
            theCommitId = pCommitId;

            /* Add the parents to the list */
            theParents = new ArrayList<>();
            for (RevCommit myCommit : getCommit().getParents()) {
                theParents.add(new ThemisGitCommitId(myCommit));
            }
        }

        @Override
        public String toString() {
            return theCommitId.toString();
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

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
