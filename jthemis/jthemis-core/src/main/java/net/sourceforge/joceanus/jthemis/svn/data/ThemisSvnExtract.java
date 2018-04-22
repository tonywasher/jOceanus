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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisDataException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmTag;
import net.sourceforge.joceanus.jthemis.scm.tasks.ThemisDirectory;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistory.ThemisSvnRevisionKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistory.ThemisSvnSourceDir;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistoryMap.ThemisSvnRevisionPath;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

/**
 * Extract plan for path.
 * @author Tony Washer
 */
public class ThemisSvnExtract
        implements MetisFieldItem {
    /**
     * DataFields.
     */
    private static final MetisFieldSet<ThemisSvnExtract> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnExtract.class);

    /**
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT, ThemisSvnExtract::getComponent);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_TRUNK, ThemisSvnExtract::getTrunkPlan);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_BRANCHES, ThemisSvnExtract::getBranches);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_TAGS, ThemisSvnExtract::getTags);
    }

    /**
     * Component.
     */
    private final ThemisSvnComponent theComponent;

    /**
     * Trunk extract list.
     */
    private final ThemisSvnBranchExtractPlan theTrunk;

    /**
     * List of non-trunk Branch Extracts.
     */
    private final ThemisSvnBranchExtractPlanList theBranches;

    /**
     * List of tag Extracts.
     */
    private final ThemisSvnTagExtractPlanList theTags;

    /**
     * Constructor.
     * @param pComponent the component.
     * @throws OceanusException on error
     */
    public ThemisSvnExtract(final ThemisSvnComponent pComponent) throws OceanusException {
        /* Store parameters */
        theComponent = pComponent;

        /* Allocate lists */
        theBranches = new ThemisSvnBranchExtractPlanList();
        theTags = new ThemisSvnTagExtractPlanList();

        /* Obtain the trunk branch */
        final ThemisSvnBranch myTrunk = theComponent.getTrunk();
        theTrunk = new ThemisSvnBranchExtractPlan(myTrunk);
        theTrunk.buildView();

        /* Build tags */
        buildTags(myTrunk);

        /* Loop through the branches in reverse order */
        final ListIterator<ThemisScmBranch> myIterator = theComponent.branchListIterator();
        while (myIterator.hasPrevious()) {
            final ThemisSvnBranch myBranch = (ThemisSvnBranch) myIterator.previous();

            /* Ignore if trunk */
            if (myBranch.isTrunk()) {
                continue;
            }

            /* If non-virtual */
            if (!myBranch.isVirtual()) {
                /* Build the plan */
                final ThemisSvnBranchExtractPlan myPlan = new ThemisSvnBranchExtractPlan(myBranch);
                theBranches.add(myPlan);
                myPlan.buildView();

                /* If the plan is not anchored */
                if (!myPlan.isAnchored()) {
                    /* Obtain the origin view of this and the trunk */
                    final ThemisSvnExtractView myView = myPlan.viewIterator().next();
                    final ThemisSvnExtractView myTrunkView = theTrunk.viewIterator().next();

                    /* Reject if not possible to repair */
                    final SVNRevision myRev = myView.getRevision();
                    if (!myRev.equals(myTrunkView.getRevision())) {
                        //throw new ThemisDataException(myPlan, "Branch Plan is not anchored");
                    }

                    /* Migrate the view to the trunk */
                    myPlan.migrateView(myView, theTrunk);
                }
            }

            /* Build tags */
            buildTags(myBranch);
        }
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public MetisFieldSet<ThemisSvnExtract> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the plan name.
     * @return the name
     */
    public String getName() {
        return theComponent.getName();
    }

    /**
     * Obtain the component.
     * @return the component
     */
    private ThemisSvnComponent getComponent() {
        return theComponent;
    }

    /**
     * Obtain the trunk extract plan.
     * @return the trunk plan
     */
    public ThemisSvnBranchExtractPlan getTrunkPlan() {
        return theTrunk;
    }

    /**
     * Obtain the branch extract plan iterator.
     * @return the iterator
     */
    public Iterator<ThemisSvnBranchExtractPlan> branchIterator() {
        return theBranches.iterator();
    }

    /**
     * Obtain the branch extract plan list.
     * @return the list
     */
    private ThemisSvnBranchExtractPlanList getBranches() {
        return theBranches;
    }

    /**
     * Obtain the tag extract plan iterator.
     * @return the iterator
     */
    public Iterator<ThemisSvnTagExtractPlan> tagIterator() {
        return theTags.iterator();
    }

    /**
     * Obtain the tag extract plan list.
     * @return the list
     */
    private ThemisSvnTagExtractPlanList getTags() {
        return theTags;
    }

    /**
     * Obtain the number of plans.
     * @return the number of plans
     */
    public int numPlans() {
        return 1 + theBranches.size() + theTags.size();
    }

    /**
     * Build tags.
     * @param pBranch the branch
     * @throws OceanusException on error
     */
    private void buildTags(final ThemisSvnBranch pBranch) throws OceanusException {
        /* Loop through the tags */
        final Iterator<ThemisScmTag> myIterator = pBranch.tagIterator();
        while (myIterator.hasNext()) {
            final ThemisSvnTag myTag = (ThemisSvnTag) myIterator.next();

            /* Build the plan */
            final ThemisSvnTagExtractPlan myPlan = new ThemisSvnTagExtractPlan(myTag);
            theTags.add(myPlan);
            myPlan.buildView();

            /* Make sure that the plan is anchored */
            if (!myPlan.isAnchored()) {
                throw new ThemisDataException(myPlan, "Tag Plan is not anchored");
            }
        }
    }

    @Override
    public String toString() {
        /* Create a stringBuilder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Output the title */
        myBuilder.append("\n\nComponent ")
                .append(theComponent.toString())
                .append("\nTrunk ")
                .append(theTrunk.toString());

        /* Add Branches */
        final Iterator<ThemisSvnBranchExtractPlan> myBrnIterator = theBranches.iterator();
        while (myBrnIterator.hasNext()) {
            final ThemisSvnBranchExtractPlan myBranch = myBrnIterator.next();

            /* Add to output */
            myBuilder.append(myBranch.toString());
        }

        /* Add Tags */
        final Iterator<ThemisSvnTagExtractPlan> myTagIterator = theTags.iterator();
        while (myTagIterator.hasNext()) {
            final ThemisSvnTagExtractPlan myTag = myTagIterator.next();

            /* Add to output */
            myBuilder.append(myTag.toString());
        }

        /* Return the details */
        return myBuilder.toString();
    }

    /**
     * Extract List.
     */
    public static final class ThemisSvnExtractAnchor
            implements MetisDataObjectFormat {
        /**
         * The owner.
         */
        private final Object theOwner;

        /**
         * The revision.
         */
        private final SVNRevision theRevision;

        /**
         * Constructor.
         * @param pOwner the Owner
         * @param pRevision the revision
         */
        public ThemisSvnExtractAnchor(final Object pOwner,
                                      final SVNRevision pRevision) {
            /* store parameters */
            theOwner = pOwner;
            theRevision = pRevision;
        }

        /**
         * Obtain owner.
         * @return the owner
         */
        public Object getOwner() {
            return theOwner;
        }

        /**
         * Obtain revision.
         * @return the revision
         */
        public SVNRevision getRevision() {
            return theRevision;
        }

        @Override
        public String toString() {
            return theOwner.toString() + ":" + theRevision.toString();
        }
    }

    /**
     * Branch Extract Plan list.
     */
    public static final class ThemisSvnBranchExtractPlanList
            implements MetisFieldItem, MetisDataList<ThemisSvnBranchExtractPlan> {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnBranchExtractPlanList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnBranchExtractPlanList.class);

        /**
         * Size field.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisSvnBranchExtractPlanList::size);
        }

        /**
         * Plan List.
         */
        private final List<ThemisSvnBranchExtractPlan> thePlanList;

        /**
         * Constructor.
         */
        private ThemisSvnBranchExtractPlanList() {
            thePlanList = new ArrayList<>();
        }

        @Override
        public List<ThemisSvnBranchExtractPlan> getUnderlyingList() {
            return thePlanList;
        }

        @Override
        public String toString() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisSvnBranchExtractPlanList> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Branch Extract Plan.
     */
    public static class ThemisSvnBranchExtractPlan
            extends ThemisSvnExtractPlan<ThemisSvnBranch> {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnBranchExtractPlan> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnBranchExtractPlan.class);

        /**
         * Constructor.
         * @param pBranch the branch
         */
        public ThemisSvnBranchExtractPlan(final ThemisSvnBranch pBranch) {
            /* Call super-constructor */
            super(pBranch.getRepository(), pBranch);
        }

        @Override
        public MetisFieldSet<ThemisSvnBranchExtractPlan> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Build view.
         * @throws OceanusException on error
         */
        public void buildView() throws OceanusException {
            /* Access the revision path */
            buildView(getOwner().getRevisionPath());
        }
    }

    /**
     * Tag Extract Plan list.
     */
    public static final class ThemisSvnTagExtractPlanList
            implements MetisFieldItem, MetisDataList<ThemisSvnTagExtractPlan> {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnTagExtractPlanList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnTagExtractPlanList.class);

        /**
         * Size field.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisSvnTagExtractPlanList::size);
        }

        /**
         * Plan List.
         */
        private final List<ThemisSvnTagExtractPlan> thePlanList;

        /**
         * Constructor.
         */
        private ThemisSvnTagExtractPlanList() {
            thePlanList = new ArrayList<>();
        }

        @Override
        public List<ThemisSvnTagExtractPlan> getUnderlyingList() {
            return thePlanList;
        }

        @Override
        public String toString() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisSvnTagExtractPlanList> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Tag Extract Plan.
     */
    public static class ThemisSvnTagExtractPlan
            extends ThemisSvnExtractPlan<ThemisSvnTag> {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnTagExtractPlan> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnTagExtractPlan.class);

        /**
         * Constructor.
         * @param pTag the tag
         */
        public ThemisSvnTagExtractPlan(final ThemisSvnTag pTag) {
            /* Call super-constructor */
            super(pTag.getRepository(), pTag);
        }

        @Override
        public String toString() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisSvnTagExtractPlan> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Build view.
         * @throws OceanusException on error
         */
        public void buildView() throws OceanusException {
            /* Access the revision path */
            buildView(getOwner().getRevisionPath());
        }
    }

    /**
     * Extract Plan.
     * @param <T> owner data type
     */
    private abstract static class ThemisSvnExtractPlan<T>
            implements MetisFieldItem {
        /**
         * DataFields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<ThemisSvnExtractPlan> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnExtractPlan.class);

        /**
         * fieldIds.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_OWNER, ThemisSvnExtractPlan::getOwner);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_ANCHOR, ThemisSvnExtractPlan::getAnchor);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_VIEWS, ThemisSvnExtractPlan::getViews);
        }

        /**
         * Repository.
         */
        private final ThemisSvnRepository theRepo;

        /**
         * The owner for this extract.
         */
        private final T theOwner;

        /**
         * The list of views.
         */
        private final ThemisSvnExtractViewList theViews;

        /**
         * The anchor point.
         */
        private ThemisSvnExtractAnchor theAnchor;

        /**
         * Have we extracted this plan.
         */
        private boolean haveExtracted;

        /**
         * Constructor.
         * @param pRepo the repository
         * @param pOwner the owner
         */
        protected ThemisSvnExtractPlan(final ThemisSvnRepository pRepo,
                                       final T pOwner) {
            /* Store parameters */
            theOwner = pOwner;
            theRepo = pRepo;
            haveExtracted = false;

            /* Create the list */
            theViews = new ThemisSvnExtractViewList();
        }

        /**
         * Obtain the owner.
         * @return the owner
         */
        public T getOwner() {
            return theOwner;
        }

        /**
         * Obtain the anchor.
         * @return the anchor
         */
        public ThemisSvnExtractAnchor getAnchor() {
            return theAnchor;
        }

        /**
         * Is the plan anchored.
         * @return true/false
         */
        public boolean isAnchored() {
            return theAnchor != null;
        }

        /**
         * Have we extracted this plan?
         * @return true/false
         */
        public boolean haveExtracted() {
            return haveExtracted;
        }

        /**
         * Mark plan as extracted.
         */
        public void markExtracted() {
            haveExtracted = true;
        }

        /**
         * Obtain the element iterator.
         * @return the iterator
         */
        public Iterator<ThemisSvnExtractView> viewIterator() {
            return theViews.iterator();
        }

        /**
         * Obtain the element list.
         * @return the list
         */
        private ThemisSvnExtractViewList getViews() {
            return theViews;
        }

        /**
         * Obtain the number of views.
         * @return the number of views
         */
        public int numViews() {
            return theViews.size();
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Output the title */
            myBuilder.append("\n\nOwner ");
            myBuilder.append(theOwner.toString());

            /* If we have an anchor */
            if (theAnchor != null) {
                myBuilder.append("\nDiverge from ");
                myBuilder.append(theAnchor.toString());
            }

            /* Add Elements */
            final Iterator<ThemisSvnExtractView> myIterator = theViews.iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnExtractView myView = myIterator.next();

                /* Add to output */
                myBuilder.append(myView.toString());
            }

            /* Return the details */
            return myBuilder.toString();
        }

        /**
         * Build view.
         * @param pPath the revision path
         * @throws OceanusException on error
         */
        protected void buildView(final ThemisSvnRevisionPath pPath) throws OceanusException {
            /* Create a sourceDirs list */
            final List<ThemisSvnRevisionHistory> mySourceDirs = new ArrayList<>();

            /* Access first entry */
            ThemisSvnRevisionHistory myEntry = pPath.getBasedOn();
            while (myEntry != null) {
                /* If the entry is not based on this owner */
                if (!theOwner.equals(myEntry.getOwner())) {
                    /* Create anchor point */
                    final ThemisSvnRevisionKey myKey = myEntry.getRevisionKey();
                    theAnchor = new ThemisSvnExtractAnchor(myEntry.getOwner(), myKey.getRevision());

                    /* Break the loop */
                    break;
                }

                /* If we have source directories */
                if (myEntry.hasSourceDirs()) {
                    /* Add entry for later processing */
                    mySourceDirs.add(myEntry);
                }

                /* Declare the view */
                final ThemisSvnRevisionKey myKey = myEntry.getRevisionKey();
                final ThemisSvnExtractView myView = new ThemisSvnExtractView(theRepo, myKey.getRevision(), myEntry);
                final String myBase = myKey.getPath();
                myView.setBaseDir(theRepo.getURL(myBase));
                theViews.add(0, myView);

                /* Move to next entry */
                myEntry = myEntry.getBasedOn();
            }

            /* If we have source directories */
            if (!mySourceDirs.isEmpty()) {
                /* Process them */
                processSourceDirs(mySourceDirs);
            }
        }

        /**
         * Process source directories.
         * @param pSourceList the source directories.
         * @throws OceanusException on error
         */
        private void processSourceDirs(final List<ThemisSvnRevisionHistory> pSourceList) throws OceanusException {
            /* Loop through the copy directories */
            final Iterator<ThemisSvnRevisionHistory> myHistIterator = pSourceList.iterator();
            while (myHistIterator.hasNext()) {
                final ThemisSvnRevisionHistory myHist = myHistIterator.next();

                /* Access the revision */
                final SVNRevision myRev = myHist.getRevision();

                /* Loop through the source directories */
                final Iterator<ThemisSvnSourceDir> myIterator = myHist.sourceDirIterator();
                while (myIterator.hasNext()) {
                    final ThemisSvnSourceDir myDir = myIterator.next();

                    /* Process the source directory */
                    processSourceDir(myRev, myDir, myHist);
                }
            }
        }

        /**
         * Process source directory.
         * @param pStartRev the starting revision
         * @param pDir the sourceDirectory.
         * @param pEntry the history details
         * @throws OceanusException on error
         */
        private void processSourceDir(final SVNRevision pStartRev,
                                      final ThemisSvnSourceDir pDir,
                                      final ThemisSvnRevisionHistory pEntry) throws OceanusException {
            /* Access the required view */
            ThemisSvnRevisionKey mySource = pDir.getSource();
            String myComp = pDir.getComponent();
            adjustView(pStartRev, myComp, mySource, pEntry);

            /* Create a sourceDirs list */
            final List<ThemisSvnRevisionHistory> mySourceDirs = new ArrayList<>();

            /* Obtain the initial revision (which we have already handled) */
            ThemisSvnRevisionHistory myEntry = pDir.getBasedOn();

            /* If we have source directories */
            if (myEntry.hasSourceDirs()) {
                /* Add entry for later processing */
                mySourceDirs.add(myEntry);
            }

            /* If we have an origin */
            if (myEntry.isOrigin()) {
                /* Adjust component */
                myComp = myEntry.getOriginDefinition().getComponent();
            }

            /* Loop through the entries */
            myEntry = myEntry.getBasedOn();
            while (myEntry != null) {
                /* If the entry is not based on this owner */
                if (!theOwner.equals(myEntry.getOwner())) {
                    /* Create anchor point */
                    final ThemisSvnRevisionKey myKey = myEntry.getRevisionKey();
                    theAnchor = new ThemisSvnExtractAnchor(myEntry.getOwner(), myKey.getRevision());

                    /* Break the loop */
                    break;
                }

                /* If we have source directories */
                if (myEntry.hasSourceDirs()) {
                    /* Add entry for later processing */
                    mySourceDirs.add(myEntry);
                }

                /* Access the view */
                final ThemisSvnRevisionKey myKey = myEntry.getRevisionKey();
                adjustView(mySource.getRevision(), myComp, myKey, myEntry);

                /* If we have an origin */
                if (myEntry.isOrigin()) {
                    /* Adjust component */
                    myComp = myEntry.getOriginDefinition().getComponent();
                }

                /* Access next element */
                mySource = myKey;
                myEntry = myEntry.getBasedOn();
            }

            /* If we have source directories */
            if (!mySourceDirs.isEmpty()) {
                /* Process them */
                processSourceDirs(mySourceDirs);
            }
        }

        /**
         * Adjust view.
         * @param pStartRev the starting revision
         * @param pComp the component.
         * @param pKey the revisionKey
         * @param pEntry the history details
         * @throws OceanusException on error
         */
        private void adjustView(final SVNRevision pStartRev,
                                final String pComp,
                                final ThemisSvnRevisionKey pKey,
                                final ThemisSvnRevisionHistory pEntry) throws OceanusException {
            /* Determine the required revision */
            final long myStart = pStartRev.getNumber();
            final SVNRevision myRevision = pKey.getRevision();
            final long myRev = myRevision.getNumber();
            final String myBase = pKey.getPath();

            /* Loop through the views in descending order */
            final int mySize = theViews.size();
            final ListIterator<ThemisSvnExtractView> myIterator = theViews.listIterator(mySize);
            while (myIterator.hasPrevious()) {
                ThemisSvnExtractView myView = myIterator.previous();

                /* Obtain revision and skip over if too recent */
                final long myCurr = myView.getRevision().getNumber();
                if (myCurr >= myStart) {
                    continue;
                }

                /* If this is an intermediate node (one between our start and destination) */
                if (myCurr > myRev) {
                    /* Ensure that directory is registered */
                    myView.addDirectory(pComp, theRepo.getURL(myBase));
                    continue;
                }

                /* If this is beyond the desired revision */
                if (myCurr < myRev) {
                    /* Need an intermediate view, so allocate new view */
                    myView = new ThemisSvnExtractView(myView, myRevision, pEntry);
                    myIterator.next();
                    myIterator.add(myView);
                }

                /* Add the directory and return */
                myView.addDirectory(pComp, theRepo.getURL(myBase));
                return;
            }

            /* None found before end of list, so allocate new view */
            final ThemisSvnExtractView myView = new ThemisSvnExtractView(theRepo, myRevision, pEntry);
            theViews.add(0, myView);
            myView.addDirectory(pComp, theRepo.getURL(myBase));
        }

        /**
         * Migrate the view.
         * @param pView the view to migrate
         * @param pTarget the target plan to migrate to
         * @throws OceanusException on error
         */
        protected void migrateView(final ThemisSvnExtractView pView,
                                   final ThemisSvnExtractPlan<T> pTarget) throws OceanusException {
            /* Remove from view list */
            theViews.remove(pView);

            /* Set as anchor */
            theAnchor = new ThemisSvnExtractAnchor(theOwner, pView.getRevision());

            /* Add to target plan */
            final ThemisSvnExtractMigratedView myMigrate = new ThemisSvnExtractMigratedView(theOwner, pView);
            pTarget.theViews.add(0, myMigrate);
        }
    }

    /**
     * Extract View list.
     */
    public static final class ThemisSvnExtractViewList
            implements MetisFieldItem, MetisDataList<ThemisSvnExtractView> {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnExtractViewList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnExtractViewList.class);

        /**
         * Size field.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisSvnExtractViewList::size);
        }

        /**
         * View List.
         */
        private final List<ThemisSvnExtractView> theViewList;

        /**
         * Constructor.
         */
        private ThemisSvnExtractViewList() {
            theViewList = new ArrayList<>();
        }

        @Override
        public List<ThemisSvnExtractView> getUnderlyingList() {
            return theViewList;
        }

        @Override
        public String toString() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisSvnExtractViewList> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Migrated Extract View.
     */
    public static final class ThemisSvnExtractMigratedView
            extends ThemisSvnExtractView {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnExtractMigratedView> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnExtractMigratedView.class);

        /**
         * Owner field.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_OWNER, ThemisSvnExtractMigratedView::getOwner);
        }

        /**
         * Original owner.
         */
        private final Object theOwner;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pSource the original view
         * @throws OceanusException on error
         */
        private ThemisSvnExtractMigratedView(final Object pOwner,
                                             final ThemisSvnExtractView pSource) throws OceanusException {
            /* Call the super-constructor */
            super(pSource);

            /* Store the owner */
            theOwner = pOwner;
        }

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the owner.
         * @return the owner
         */
        public Object getOwner() {
            return theOwner;
        }
    }

    /**
     * Extract View.
     */
    public static class ThemisSvnExtractView
            implements MetisFieldItem {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnExtractView> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnExtractView.class);

        /**
         * fieldIds.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_REVISION, ThemisSvnExtractView::getRevisionNo);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_DATE, ThemisSvnExtractView::getDate);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_LOGMSG, ThemisSvnExtractView::getLogMessage);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_ITEMS, ThemisSvnExtractView::getItems);
        }

        /**
         * Repository.
         */
        private final ThemisSvnRepository theRepo;

        /**
         * Revision.
         */
        private final SVNRevision theRevision;

        /**
         * Log Message.
         */
        private final String theLogMessage;

        /**
         * Date.
         */
        private final Date theDate;

        /**
         * Extract item list.
         */
        private final ThemisSvnExtractItemList theItems;

        /**
         * Constructor.
         * @param pRepo the repository
         * @param pRevision the revision
         * @param pEntry the history details
         */
        private ThemisSvnExtractView(final ThemisSvnRepository pRepo,
                                     final SVNRevision pRevision,
                                     final ThemisSvnRevisionHistory pEntry) {
            /* Store parameters */
            theRepo = pRepo;
            theRevision = pRevision;

            /* Obtain details from the entry */
            theDate = pEntry.getDate();
            theLogMessage = pEntry.getLogMessage();

            /* Create the list */
            theItems = new ThemisSvnExtractItemList();
        }

        /**
         * Constructor.
         * @param pView the view to copy from
         * @param pRevision the revision
         * @param pEntry the history details
         * @throws OceanusException on error
         */
        private ThemisSvnExtractView(final ThemisSvnExtractView pView,
                                     final SVNRevision pRevision,
                                     final ThemisSvnRevisionHistory pEntry) throws OceanusException {
            /* Initialise item */
            this(pView.theRepo, pRevision, pEntry);

            /* Loop through the underlying items */
            final Iterator<ThemisSvnExtractItem> myIterator = pView.elementIterator();
            while (myIterator.hasNext()) {
                final ThemisSvnExtractItem myItem = myIterator.next();

                /* Add the item */
                theItems.addItem(myItem);
            }
        }

        /**
         * Constructor.
         * @param pView the view to copy from
         * @throws OceanusException on error
         */
        protected ThemisSvnExtractView(final ThemisSvnExtractView pView) throws OceanusException {
            /* Store parameters */
            theRepo = pView.theRepo;
            theRevision = pView.getRevision();

            /* Obtain details from the source view */
            theDate = pView.getDate();
            theLogMessage = pView.getLogMessage();

            /* Create the list */
            theItems = new ThemisSvnExtractItemList();

            /* Loop through the underlying items */
            final Iterator<ThemisSvnExtractItem> myIterator = pView.elementIterator();
            while (myIterator.hasNext()) {
                final ThemisSvnExtractItem myItem = myIterator.next();

                /* Add the item */
                theItems.addItem(myItem);
            }
        }

        @Override
        public MetisFieldSetDef getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return theRevision.toString();
        }

        /**
         * Obtain the revision.
         * @return the revision
         */
        public SVNRevision getRevision() {
            return theRevision;
        }

        /**
         * Obtain the revision#.
         * @return the revision
         */
        private long getRevisionNo() {
            return theRevision.getNumber();
        }

        /**
         * Obtain the log message.
         * @return the log message
         */
        public String getLogMessage() {
            return theLogMessage;
        }

        /**
         * Obtain the date.
         * @return the date
         */
        public Date getDate() {
            final Date myDate = new Date();
            myDate.setTime(theDate.getTime());
            return myDate;
        }

        /**
         * Obtain the item iterator.
         * @return the iterator
         */
        public Iterator<ThemisSvnExtractItem> elementIterator() {
            return theItems.iterator();
        }

        /**
         * Obtain the item list.
         * @return the list
         */
        private ThemisSvnExtractItemList getItems() {
            return theItems;
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Output the title */
            myBuilder.append("\n\nRevision ");
            myBuilder.append(theRevision.toString());

            /* Add Elements */
            final Iterator<ThemisSvnExtractItem> myIterator = theItems.iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnExtractItem myEl = myIterator.next();

                /* Add to output */
                myBuilder.append("\n");
                myBuilder.append(myEl.toString());
            }

            /* Return the details */
            return myBuilder.toString();
        }

        /**
         * Set base directory.
         * @param pBaseDir the base directory
         * @throws OceanusException on error
         */
        private void setBaseDir(final SVNURL pBaseDir) throws OceanusException {
            final ThemisSvnExtractItem myItem = new ThemisSvnExtractItem(pBaseDir);
            theItems.addItem(myItem);
        }

        /**
         * Add directory.
         * @param pTarget the target
         * @param pBaseDir the base directory
         * @throws OceanusException on error
         */
        private void addDirectory(final String pTarget,
                                  final SVNURL pBaseDir) throws OceanusException {
            final ThemisSvnExtractItem myItem = new ThemisSvnExtractItem(pTarget, pBaseDir);
            theItems.addItem(myItem);
        }

        /**
         * Extract item.
         * @param pTarget the target location
         * @param pKeep the name of a file/directory to preserve
         * @throws OceanusException on error
         */
        public void extractItem(final File pTarget,
                                final String pKeep) throws OceanusException {
            /* Clear the target directory */
            ThemisDirectory.clearDirectory(pTarget, pKeep);

            /* Access update client */
            final SVNClientManager myMgr = theRepo.getClientManager();
            final SVNUpdateClient myUpdate = myMgr.getUpdateClient();
            myUpdate.setExportExpandsKeywords(false);

            /* Protect against exceptions */
            try {
                /* Loop through the items */
                final Iterator<ThemisSvnExtractItem> myIterator = elementIterator();
                while (myIterator.hasNext()) {
                    final ThemisSvnExtractItem myItem = myIterator.next();

                    /* Determine the target */
                    final File myTarget = myItem.getTarget(pTarget);

                    /* Export the item */
                    myUpdate.doExport(myItem.getSource(), myTarget, theRevision, theRevision, null, true, SVNDepth.INFINITY);
                }

            } catch (SVNException e) {
                throw new ThemisIOException("Failed to export View", e);
            } finally {
                theRepo.releaseClientManager(myMgr);
            }
        }
    }

    /**
     * Extract Item.
     */
    public static final class ThemisSvnExtractItem
            implements MetisFieldItem {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnExtractItem> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnExtractItem.class);

        /**
         * fieldIds.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_TARGET, ThemisSvnExtractItem::getTarget);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_SOURCE, ThemisSvnExtractItem::getSourceName);
        }

        /**
         * Target path.
         */
        private final String theTarget;

        /**
         * Source path.
         */
        private final SVNURL theSource;

        /**
         * Constructor.
         * @param pTarget the target for the element
         * @param pSource the source for the element
         */
        private ThemisSvnExtractItem(final String pTarget,
                                     final SVNURL pSource) {
            /* Store parameters */
            theTarget = pTarget;
            theSource = pSource;
        }

        /**
         * Constructor.
         * @param pSource the source for the element
         */
        private ThemisSvnExtractItem(final SVNURL pSource) {
            this(null, pSource);
        }

        @Override
        public MetisFieldSet<ThemisSvnExtractItem> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the target.
         * @return the target
         */
        public String getTarget() {
            return theTarget;
        }

        /**
         * Obtain the source.
         * @return the source
         */
        public SVNURL getSource() {
            return theSource;
        }

        /**
         * Obtain the source.
         * @return the source
         */
        private String getSourceName() {
            return theSource.toString();
        }

        /**
         * Obtain the target path.
         * @param pBase the base target directory
         * @return the target directory
         */
        private File getTarget(final File pBase) {
            return theTarget == null
                                     ? pBase
                                     : new File(pBase, theTarget);
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Output the title */
            if (theTarget != null) {
                myBuilder.append(theTarget);
            }
            myBuilder.append("<-");
            myBuilder.append(theSource.toString());

            /* Return the details */
            return myBuilder.toString();
        }
    }

    /**
     * Extract Item list.
     */
    public static final class ThemisSvnExtractItemList
            implements MetisFieldItem, MetisDataList<ThemisSvnExtractItem> {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnExtractItemList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnExtractItemList.class);

        /**
         * Size field.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisSvnExtractItemList::size);
        }

        /**
         * Item List.
         */
        private final List<ThemisSvnExtractItem> theItemList;

        /**
         * Constructor.
         */
        private ThemisSvnExtractItemList() {
            theItemList = new ArrayList<>();
        }

        @Override
        public List<ThemisSvnExtractItem> getUnderlyingList() {
            return theItemList;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Add Branches */
            final Iterator<ThemisSvnExtractItem> myIterator = theItemList.iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnExtractItem myItem = myIterator.next();

                /* Add to output */
                myBuilder.append('\n');
                myBuilder.append(myItem.toString());
            }

            /* Return the details */
            return myBuilder.toString();
        }

        @Override
        public MetisFieldSet<ThemisSvnExtractItemList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Add item to list (discarding duplicates).
         * @param pItem the item to add.
         * @throws OceanusException on error
         */
        private void addItem(final ThemisSvnExtractItem pItem) throws OceanusException {
            /* Loop through the existing items */
            final Iterator<ThemisSvnExtractItem> myIterator = theItemList.iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnExtractItem myEntry = myIterator.next();

                /* If we have matching target */
                if (MetisDataDifference.isEqual(myEntry.getTarget(), pItem.getTarget())) {
                    /* Reject if different path */
                    if (!MetisDataDifference.isEqual(myEntry.getSource(), pItem.getSource())) {
                        throw new ThemisDataException(myEntry, "Conflicting sources");
                    }

                    /* Discard the duplicate */
                    return;
                }
            }

            /* Add the unique entry */
            theItemList.add(pItem);
        }
    }
}
