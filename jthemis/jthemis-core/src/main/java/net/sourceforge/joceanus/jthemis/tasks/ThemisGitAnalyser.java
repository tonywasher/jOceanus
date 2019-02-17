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
package net.sourceforge.joceanus.jthemis.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisLogicException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitComponent;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitOwner;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitCommitId;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitRevision;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmOwner;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnBranchExtractPlan;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnExtractAnchor;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnExtractPlan;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnExtractView;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnTagExtractPlan;

/**
 * Git Plan analyser.
 */
public class ThemisGitAnalyser {
    /**
     * The extract.
     */
    private final ThemisSvnExtract theExtract;

    /**
     * GitComponent.
     */
    private final ThemisGitComponent theGitComponent;

    /**
     * The Anchor Map.
     */
    private final ThemisGitAnchorMap theAnchorMap;

    /**
     * Constructor.
     * @param pExtract the extract
     */
    protected ThemisGitAnalyser(final ThemisSvnExtract pExtract) {
        theExtract = pExtract;
        theGitComponent = theExtract.getGitComponent();
        theAnchorMap = theExtract.getGitAnchorMap();
    }

    /**
     * Analyse plans.
     */
    protected void analysePlans() {
        /* Analyse the trunk plan */
        analysePlan(theExtract.getTrunkPlan());

        /* Analyse the branches */
        final Iterator<ThemisSvnBranchExtractPlan> myBranchIterator = theExtract.branchIterator();
        while (myBranchIterator.hasNext()) {
            final ThemisSvnBranchExtractPlan myPlan = myBranchIterator.next();

            /* Analyse the plan */
            analysePlan(myPlan);

            /* Remove plan if it is complete */
            if (myPlan.isComplete()) {
                myBranchIterator.remove();
            }
        }

        /* Analyse the tags */
        final Iterator<ThemisSvnTagExtractPlan> myTagIterator = theExtract.tagIterator();
        while (myTagIterator.hasNext()) {
            final ThemisSvnTagExtractPlan myPlan = myTagIterator.next();

            /* Analyse the plan */
            analysePlan(myPlan);

            /* Remove plan if it is complete */
            if (myPlan.isComplete()) {
                myTagIterator.remove();
            }
        }
    }

    /**
     * Analyse a plan.
     * @param pPlan the plan
     */
    private void analysePlan(final ThemisSvnExtractPlan<?> pPlan) {
        /* Create a list of potential null commits */
        final List<ThemisSvnExtractView> myNullCommits = new ArrayList<>();

        /* Initialise counters */
        long myLastRevision = -1;
        int myIteration = 0;

        /* Record the last commit */
        ThemisGitRevision myLastCommit = theAnchorMap.findCommit(pPlan.getAnchor());

        /* Iterate through the elements */
        final Iterator<ThemisSvnExtractView> myIterator = pPlan.viewIterator();
        while (myIterator.hasNext()) {
            final ThemisSvnExtractView myView = myIterator.next();

            /* Check for duplicate revision */
            final long myRevision = myView.getRevision().getNumber();
            if (myRevision == myLastRevision) {
                myIteration++;
            } else {
                myIteration = 0;
            }
            myLastRevision = myRevision;

            /* Determine revision string */
            String myRevString = Long.toString(myRevision);
            if (myIteration != 0) {
                myRevString += "." + myIteration;
            }

            /* Store the revision string */
            myView.setGitRevisionNo(myRevString);

            /* Just continue if we do not have a gitComponent */
            if (theGitComponent == null) {
                continue;
            }

            /* Look for the existing gitCommit */
            final ThemisScmOwner myOwner = pPlan.getOwner();
            final ThemisGitRevision myGitRevision = theGitComponent.getGitRevisionForRevisionKey(myOwner, myRevString);

            /* If we did not find a revision */
            if (myGitRevision == null) {
                /* Add to list of potential null commits */
                myNullCommits.add(myView);

                /* else we found a matching commit */
            } else {
                /* If we have some null commits */
                if (!myNullCommits.isEmpty()) {
                    /* Mark as a null view */
                    myNullCommits.forEach(ThemisSvnExtractView::setNullView);

                    /* Clear the list */
                    myNullCommits.clear();
                }

                /* Validate the parentage */
                if (checkForBadParentage(myGitRevision, myLastCommit)) {
                    pPlan.markBadParent(myView);
                }

                /* Record the commit and register as last commit */
                myView.setGitRevision(myGitRevision);
                theAnchorMap.addAnchor(myView.getAnchor(), myGitRevision);
                myLastCommit = myGitRevision;
            }
        }

        /* Prune the plan */
        prunePlan(pPlan);

        /* Check completeness */
        checkCompleteness(pPlan);

        /* Report errors */
        if (pPlan.isError()) {
            theExtract.noteErrors();
        }
    }

    /**
     * Check parentage.
     * @param pCommit the commit
     * @param pParent the parent
     * @return has badParentage true/false
     */
    private boolean checkForBadParentage(final ThemisGitRevision pCommit,
                                         final ThemisGitRevision pParent) {
        /* Check for orphans */
        final boolean isExpectedOrphan = pParent == null;
        final boolean isOrphan = pCommit.getParents().size() == 0;

        /* Check for matching orphan status */
        if (isOrphan != isExpectedOrphan) {
            return true;
        }

        /* Check for matching parentage */
        if (!isOrphan) {
            final ThemisGitCommitId myCommitId = pCommit.getParents().get(0);
            if (!myCommitId.equals(pParent.getCommitId())) {
                return true;
            }
        }

        /* Parentage is OK */
        return false;
    }

    /**
     * Check completeness.
     * @param pPlan the plan
     */
    private void checkCompleteness(final ThemisSvnExtractPlan<?> pPlan) {
        /* If we are existing and have good parentage */
        if (pPlan.isExisting()
            && !pPlan.isBadParent()) {
            /* Obtain the anchor commitId */
            final ThemisGitRevision myRevision = theAnchorMap.findCommit(pPlan.getAnchor());
            final ThemisGitCommitId myAnchorCommitId = myRevision.getCommitId();

            /* Obtain the owner latest commitId */
            final ThemisGitOwner myOwner = pPlan.getTarget();
            final ThemisGitCommitId myLatestCommitId = myOwner.getCommitId();

            /* Check for conflicting commits */
            if (!myLatestCommitId.equals(myAnchorCommitId)) {
                pPlan.noteConflicts();
            }
        }
    }

    /**
     * Prune a plan.
     * @param pPlan the plan
     */
    private void prunePlan(final ThemisSvnExtractPlan<?> pPlan) {
        /* Don't prune the plan if it has a bad parent */
        if (pPlan.isBadParent()) {
            return;
        }

        /* Iterate through the elements */
        final Iterator<ThemisSvnExtractView> myIterator = pPlan.viewIterator();
        while (myIterator.hasNext()) {
            final ThemisSvnExtractView myView = myIterator.next();

            /* If we do not have a commit */
            if (!myView.isNull()
                && myView.getGitRevision() == null) {
                /* Break the loop */
                break;
            }

            /* We have already applied this commit, so remove it */
            myIterator.remove();

            /* Adjust the anchor */
            pPlan.adjustAnchor(myView);
        }
    }

    /**
     * Map of anchor to Git revision.
     */
    public static class ThemisGitAnchorMap
            implements MetisDataObjectFormat, MetisDataMap<ThemisScmOwner, ThemisSvnRevisionCommitList> {
        /**
         * The map.
         */
        private final Map<ThemisScmOwner, ThemisSvnRevisionCommitList> theMap;

        /**
         * Constructor.
         */
        protected ThemisGitAnchorMap() {
            theMap = new HashMap<>();
        }

        @Override
        public Map<ThemisScmOwner, ThemisSvnRevisionCommitList> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return ThemisGitAnchorMap.class.getSimpleName();
        }

        /**
         * Add mapping.
         * @param pAnchor the anchor
         * @param pCommit the commit
         */
        void addAnchor(final ThemisSvnExtractAnchor pAnchor,
                       final ThemisGitRevision pCommit) {
            /* Access the list */
            final ThemisScmOwner myOwner = pAnchor.getOwner();
            final ThemisSvnRevisionCommitList myList = theMap.computeIfAbsent(myOwner, o -> new ThemisSvnRevisionCommitList());

            /* Add item */
            myList.add(new ThemisSvnRevisionCommit(pAnchor.getRevision().getNumber(), pCommit));
        }

        /**
         * Obtain the commit for the anchor.
         * @param pAnchor the anchor
         * @return the commit
         * @throws OceanusException on error
         */
        ThemisGitRevision getCommit(final ThemisSvnExtractAnchor pAnchor) throws OceanusException {
            /* Access the list */
            final ThemisSvnRevisionCommitList myList = theMap.get(pAnchor.getOwner());
            if (myList != null) {
                /* Access the commit */
                return myList.getCommit(pAnchor.getRevision().getNumber());
            }

            /* Not yet extracted */
            return null;
        }

        /**
         * Obtain the commit for the anchor.
         * @param pAnchor the anchor
         * @return the commit
         */
        ThemisGitRevision findCommit(final ThemisSvnExtractAnchor pAnchor) {
            /* Access the list */
            final ThemisSvnRevisionCommitList myList = pAnchor == null
                                                                       ? null
                                                                       : theMap.get(pAnchor.getOwner());
            if (myList != null) {
                /* Access the commit */
                return myList.findCommit(pAnchor.getRevision().getNumber());
            }

            /* Not yet extracted */
            return null;
        }
    }

    /**
     * Revision Commit entry.
     */
    private static final class ThemisSvnRevisionCommit
            implements MetisDataObjectFormat {
        /**
         * The revision.
         */
        private final long theRevision;

        /**
         * The commit.
         */
        private final ThemisGitRevision theCommit;

        /**
         * Constructor.
         * @param pRevision the revision
         * @param pCommit the commit
         */
        ThemisSvnRevisionCommit(final long pRevision,
                                final ThemisGitRevision pCommit) {
            theRevision = pRevision;
            theCommit = pCommit;
        }

        /**
         * Obtain the revision.
         * @return the revision
         */
        long getRevision() {
            return theRevision;
        }

        /**
         * Obtain the commit.
         * @return the commit
         */
        ThemisGitRevision getCommit() {
            return theCommit;
        }

        @Override
        public String toString() {
            return Long.toString(theRevision) + "=" + theCommit.toString();
        }
    }

    /**
     * Revision Commit List.
     */
    private static final class ThemisSvnRevisionCommitList
            implements MetisDataObjectFormat, MetisDataList<ThemisSvnRevisionCommit> {
        /**
         * The list.
         */
        private final List<ThemisSvnRevisionCommit> theList;

        /**
         * Constructor.
         */
        private ThemisSvnRevisionCommitList() {
            theList = new ArrayList<>();
        }

        @Override
        public List<ThemisSvnRevisionCommit> getUnderlyingList() {
            return theList;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return ThemisSvnRevisionCommitList.class.getSimpleName();
        }

        /**
         * Find the commit for the revision.
         * @param pRevision the revision
         * @return the commit
         */
        ThemisGitRevision findCommit(final long pRevision) {
            /* Note commit */
            ThemisGitRevision myCommit = null;

            /* Loop through the revisions */
            final Iterator<ThemisSvnRevisionCommit> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnRevisionCommit myRevision = myIterator.next();

                /* If this revision is past the required revision */
                if (myRevision.getRevision() > pRevision) {
                    break;
                }

                /* Note the commit */
                myCommit = myRevision.getCommit();
            }

            /* Return the commit */
            return myCommit;
        }

        /**
         * Obtain the commit for the revision.
         * @param pRevision the revision
         * @return the commit
         * @throws OceanusException on error
         */
        ThemisGitRevision getCommit(final long pRevision) throws OceanusException {
            /* Note commit */
            final ThemisGitRevision myCommit = findCommit(pRevision);

            /* If there is no relevant revision */
            if (myCommit == null) {
                throw new ThemisLogicException("Missing anchor" + pRevision);
            }

            /* Return the commit */
            return myCommit;
        }
    }
}
