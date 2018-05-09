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
package net.sourceforge.joceanus.jthemis.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.revwalk.RevCommit;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisLogicException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitComponent;
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
        }

        /* Analyse the tags */
        final Iterator<ThemisSvnTagExtractPlan> myTagIterator = theExtract.tagIterator();
        while (myTagIterator.hasNext()) {
            final ThemisSvnTagExtractPlan myPlan = myTagIterator.next();

            /* Analyse the plan */
            analysePlan(myPlan);
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
        ThemisGitRevision myLastCommit = null;

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
                myRevString += '.' + myIteration;
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
                    /* Register these with the last commit */
                    final ThemisGitRevision myNullRevision = myLastCommit;
                    myNullCommits.forEach(p -> p.setGitRevision(myNullRevision));

                    /* Clear the list */
                    myNullCommits.clear();
                }

                /* Record the commit and register as last commit */
                myView.setGitRevision(myGitRevision);
                theAnchorMap.addAnchor(myView.getAnchor(), myGitRevision.getCommit());
                myLastCommit = myGitRevision;
            }
        }
    }

    /**
     * Map of anchor to Git revision.
     */
    public static class ThemisGitAnchorMap
            implements MetisDataMap<ThemisScmOwner, ThemisSvnRevisionCommitList> {
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

        /**
         * Add mapping.
         * @param pAnchor the anchor
         * @param pCommit the commit
         */
        void addAnchor(final ThemisSvnExtractAnchor pAnchor,
                       final RevCommit pCommit) {
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
        RevCommit getCommit(final ThemisSvnExtractAnchor pAnchor) throws OceanusException {
            /* Access the list */
            final ThemisSvnRevisionCommitList myList = theMap.get(pAnchor.getOwner());
            if (myList != null) {
                /* Access the commit */
                return myList.getCommit(pAnchor.getRevision().getNumber());
            }

            /* Not yet extracted */
            return null;
        }

    }

    /**
     * Revision Commit entry.
     */
    private static final class ThemisSvnRevisionCommit {
        /**
         * The revision.
         */
        private final long theRevision;

        /**
         * The commit.
         */
        private final RevCommit theCommit;

        /**
         * Constructor.
         * @param pRevision the revision
         * @param pCommit the commit
         */
        ThemisSvnRevisionCommit(final long pRevision,
                                final RevCommit pCommit) {
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
        RevCommit getCommit() {
            return theCommit;
        }
    }

    /**
     * Revision Commit List.
     */
    private static final class ThemisSvnRevisionCommitList
            implements MetisDataList<ThemisSvnRevisionCommit> {
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

        /**
         * Obtain the commit for the revision.
         * @param pRevision the revision
         * @return the commit
         * @throws OceanusException on error
         */
        RevCommit getCommit(final long pRevision) throws OceanusException {
            /* Note commit */
            RevCommit myCommit = null;

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

            /* If there is no relevant revision */
            if (myCommit == null) {
                throw new ThemisLogicException("Missing anchor" + pRevision);
            }

            /* Return the commit */
            return myCommit;
        }
    }
}
