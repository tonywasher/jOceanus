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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisDataException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.tasks.Directory;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRevisionHistory.SvnRevisionKey;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRevisionHistory.SvnSourceDir;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRevisionHistoryMap.SvnRevisionPath;

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
public class SvnExtract
        implements JDataContents {
    /**
     * DataFields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(SvnExtract.class.getSimpleName());

    /**
     * Component field.
     */
    private static final JDataField FIELD_COMP = FIELD_DEFS.declareLocalField("Component");

    /**
     * Trunk field.
     */
    private static final JDataField FIELD_TRUNK = FIELD_DEFS.declareLocalField("Trunk");

    /**
     * Branches field.
     */
    private static final JDataField FIELD_BRANCHES = FIELD_DEFS.declareLocalField("Branches");

    /**
     * Tags field.
     */
    private static final JDataField FIELD_TAGS = FIELD_DEFS.declareLocalField("Tags");

    @Override
    public String formatObject() {
        return toString();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_COMP.equals(pField)) {
            return theComponent;
        }
        if (FIELD_TRUNK.equals(pField)) {
            return theTrunk;
        }
        if (FIELD_BRANCHES.equals(pField)) {
            return theBranches;
        }
        if (FIELD_TAGS.equals(pField)) {
            return theTags;
        }
        return JDataFieldValue.UNKNOWN;
    }

    /**
     * Obtain the plan name.
     * @return the name
     */
    public String getName() {
        return theComponent.getName();
    }

    /**
     * Component.
     */
    private final SvnComponent theComponent;

    /**
     * Trunk extract list.
     */
    private SvnBranchExtractPlan theTrunk;

    /**
     * List of non-trunk Branch Extracts.
     */
    private final SvnBranchExtractPlanList theBranches;

    /**
     * List of tag Extracts.
     */
    private final SvnTagExtractPlanList theTags;

    /**
     * Constructor.
     * @param pComponent the component.
     * @throws JOceanusException on error
     */
    public SvnExtract(final SvnComponent pComponent) throws JOceanusException {
        /* Store parameters */
        theComponent = pComponent;

        /* Allocate lists */
        theBranches = new SvnBranchExtractPlanList();
        theTags = new SvnTagExtractPlanList();

        /* Obtain the trunk branch */
        SvnBranch myTrunk = theComponent.getTrunk();
        theTrunk = new SvnBranchExtractPlan(myTrunk);
        theTrunk.buildView();

        /* Build tags */
        buildTags(myTrunk);

        /* Loop through the branches */
        Iterator<SvnBranch> myIterator = theComponent.branchIterator();
        while (myIterator.hasNext()) {
            SvnBranch myBranch = myIterator.next();

            /* Ignore if trunk */
            if (myBranch.isTrunk()) {
                continue;
            }

            /* If non-virtual */
            if (!myBranch.isVirtual()) {
                /* Build the plan */
                SvnBranchExtractPlan myPlan = new SvnBranchExtractPlan(myBranch);
                theBranches.add(myPlan);
                myPlan.buildView();
            }

            /* Build tags */
            buildTags(myBranch);
        }
    }

    /**
     * Build tags.
     * @param pBranch the branch
     * @throws JOceanusException on error
     */
    private void buildTags(final SvnBranch pBranch) throws JOceanusException {
        /* Loop through the tags */
        Iterator<SvnTag> myIterator = pBranch.tagIterator();
        while (myIterator.hasNext()) {
            SvnTag myTag = myIterator.next();

            /* Build the plan */
            SvnTagExtractPlan myPlan = new SvnTagExtractPlan(myTag);
            theTags.add(myPlan);
            myPlan.buildView();
        }
    }

    @Override
    public String toString() {
        /* Create a stringBuilder */
        StringBuilder myBuilder = new StringBuilder();

        /* Output the title */
        myBuilder.append("\n\nComponent ");
        myBuilder.append(theComponent.toString());

        /* Output the trunk */
        myBuilder.append("\nTrunk ");
        myBuilder.append(theTrunk.toString());

        /* Add Branches */
        Iterator<SvnBranchExtractPlan> myBrnIterator = theBranches.iterator();
        while (myBrnIterator.hasNext()) {
            SvnBranchExtractPlan myBranch = myBrnIterator.next();

            /* Add to output */
            myBuilder.append(myBranch.toString());
        }

        /* Add Tags */
        Iterator<SvnTagExtractPlan> myTagIterator = theTags.iterator();
        while (myTagIterator.hasNext()) {
            SvnTagExtractPlan myTag = myTagIterator.next();

            /* Add to output */
            myBuilder.append(myTag.toString());
        }

        /* Return the details */
        return myBuilder.toString();
    }

    /**
     * Extract List.
     */
    public static final class SvnExtractAnchor
            implements JDataFormat {
        /**
         * The owner.
         */
        private final Object theOwner;

        /**
         * The revision.
         */
        private final SVNRevision theRevision;

        @Override
        public String formatObject() {
            return toString();
        }

        /**
         * Constructor.
         * @param pOwner the Owner
         * @param pRevision the revision
         */
        private SvnExtractAnchor(final Object pOwner,
                                 final SVNRevision pRevision) {
            /* store parameters */
            theOwner = pOwner;
            theRevision = pRevision;
        }

        @Override
        public String toString() {
            return theOwner.toString() + ":" + theRevision.toString();
        }
    }

    /**
     * Branch Extract Plan list.
     */
    public static class SvnBranchExtractPlanList
            extends ArrayList<SvnBranchExtractPlan>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -815070505489452279L;

        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnBranchExtractPlanList.class.getSimpleName());

        /**
         * Size field.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }
    }

    /**
     * Branch Extract Plan.
     */
    public static class SvnBranchExtractPlan
            extends SvnExtractPlan<SvnBranch> {
        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnBranchExtractPlan.class.getSimpleName(), SvnExtractPlan.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Constructor.
         * @param pBranch the branch
         */
        public SvnBranchExtractPlan(final SvnBranch pBranch) {
            /* Call super-constructor */
            super(pBranch.getRepository(), pBranch);
        }

        /**
         * Build view.
         * @throws JOceanusException on error
         */
        public void buildView() throws JOceanusException {
            /* Access the revision path */
            buildView(getOwner().getRevisionPath());
        }
    }

    /**
     * Tag Extract Plan list.
     */
    public static class SvnTagExtractPlanList
            extends ArrayList<SvnTagExtractPlan>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 9153708720874356472L;

        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnTagExtractPlanList.class.getSimpleName());

        /**
         * Size field.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }
    }

    /**
     * Tag Extract Plan.
     */
    public static class SvnTagExtractPlan
            extends SvnExtractPlan<SvnTag> {
        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnTagExtractPlan.class.getSimpleName(), SvnExtractPlan.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Constructor.
         * @param pTag the tag
         */
        public SvnTagExtractPlan(final SvnTag pTag) {
            /* Call super-constructor */
            super(pTag.getRepository(), pTag);
        }

        /**
         * Build view.
         * @throws JOceanusException on error
         */
        public void buildView() throws JOceanusException {
            /* Access the revision path */
            buildView(getOwner().getRevisionPath());
        }
    }

    /**
     * Extract Plan.
     * @param <T> owner data type
     */
    private abstract static class SvnExtractPlan<T>
            implements JDataContents {
        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnExtractPlan.class.getSimpleName());

        /**
         * Owner field.
         */
        private static final JDataField FIELD_OWNER = FIELD_DEFS.declareLocalField("Owner");

        /**
         * Anchor field.
         */
        private static final JDataField FIELD_ANCHOR = FIELD_DEFS.declareLocalField("Anchor");

        /**
         * Views field.
         */
        private static final JDataField FIELD_VIEWS = FIELD_DEFS.declareLocalField("Views");

        @Override
        public String formatObject() {
            return toString();
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_OWNER.equals(pField)) {
                return theOwner;
            }
            if (FIELD_ANCHOR.equals(pField)) {
                return theAnchor;
            }
            if (FIELD_VIEWS.equals(pField)) {
                return theViews;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Repository.
         */
        private final SvnRepository theRepo;

        /**
         * The owner for this extract.
         */
        private final T theOwner;

        /**
         * The list of views.
         */
        private final SvnExtractViewList theViews;

        /**
         * The anchor point.
         */
        private SvnExtractAnchor theAnchor;

        /**
         * Obtain the owner.
         * @return the owner
         */
        public T getOwner() {
            return theOwner;
        }

        /**
         * Obtain the element iterator.
         * @return the iterator
         */
        public Iterator<SvnExtractView> viewIterator() {
            return theViews.iterator();
        }

        /**
         * Constructor.
         * @param pRepo the repository
         * @param pOwner the owner
         */
        protected SvnExtractPlan(final SvnRepository pRepo,
                                 final T pOwner) {
            /* Store parameters */
            theOwner = pOwner;
            theRepo = pRepo;

            /* Create the list */
            theViews = new SvnExtractViewList();
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            StringBuilder myBuilder = new StringBuilder();

            /* Output the title */
            myBuilder.append("\n\nOwner ");
            myBuilder.append(theOwner.toString());

            /* If we have an anchor */
            if (theAnchor != null) {
                myBuilder.append("\nDiverge from ");
                myBuilder.append(theAnchor.toString());
            }

            /* Add Elements */
            Iterator<SvnExtractView> myIterator = theViews.iterator();
            while (myIterator.hasNext()) {
                SvnExtractView myView = myIterator.next();

                /* Add to output */
                myBuilder.append(myView.toString());
            }

            /* Return the details */
            return myBuilder.toString();
        }

        /**
         * Build view.
         * @param pPath the revision path
         * @throws JOceanusException on error
         */
        protected void buildView(final SvnRevisionPath pPath) throws JOceanusException {
            /* Create a sourceDirs list */
            List<SvnRevisionHistory> mySourceDirs = new ArrayList<SvnRevisionHistory>();

            /* Access first entry */
            SvnRevisionHistory myEntry = pPath.getBasedOn();
            while (myEntry != null) {
                /* If the entry is not based on this owner */
                if (!theOwner.equals(myEntry.getOwner())) {
                    /* Create anchor point */
                    SvnRevisionKey myKey = myEntry.getRevisionKey();
                    theAnchor = new SvnExtractAnchor(myEntry.getOwner(), myKey.getRevision());

                    /* Break the loop */
                    break;
                }

                /* If we have source directories */
                if (myEntry.hasSourceDirs()) {
                    /* Add entry for later processing */
                    mySourceDirs.add(myEntry);
                }

                /* Declare the view */
                SvnRevisionKey myKey = myEntry.getRevisionKey();
                SvnExtractView myView = new SvnExtractView(theRepo, myKey.getRevision());
                String myBase = myKey.getPath();
                myView.setBaseDir(theRepo.getURL(myBase));
                theViews.add(0, myView);

                /* Move to next entry */
                myEntry = myEntry.getBasedOn();
            }

            /* Process any source directories */
            if (!mySourceDirs.isEmpty()) {
                processSourceDirs(mySourceDirs);
            }
        }

        /**
         * Process source directories.
         * @param pSourceList the source directories.
         * @throws JOceanusException on error
         */
        private void processSourceDirs(final List<SvnRevisionHistory> pSourceList) throws JOceanusException {
            /* Loop through the copy directories */
            Iterator<SvnRevisionHistory> myHistIterator = pSourceList.iterator();
            while (myHistIterator.hasNext()) {
                SvnRevisionHistory myHist = myHistIterator.next();

                /* Access the revision */
                SVNRevision myRev = myHist.getRevision();

                /* Loop through the source directories */
                Iterator<SvnSourceDir> myIterator = myHist.sourceDirIterator();
                while (myIterator.hasNext()) {
                    SvnSourceDir myDir = myIterator.next();

                    /* Process the source directory */
                    processSourceDir(myRev, myDir);
                }
            }
        }

        /**
         * Process source directory.
         * @param pStartRev the starting revision
         * @param pDir the sourceDirectory.
         * @throws JOceanusException on error
         */
        private void processSourceDir(final SVNRevision pStartRev,
                                      final SvnSourceDir pDir) throws JOceanusException {
            /* Access the required view */
            SvnRevisionKey mySource = pDir.getSource();
            String myComp = pDir.getComponent();
            adjustView(pStartRev, myComp, mySource);

            /* Create a sourceDirs list */
            List<SvnRevisionHistory> mySourceDirs = new ArrayList<SvnRevisionHistory>();

            /* Obtain the initial revision (which we have already handled) */
            SvnRevisionHistory myEntry = pDir.getBasedOn();

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
                    SvnRevisionKey myKey = myEntry.getRevisionKey();
                    theAnchor = new SvnExtractAnchor(myEntry.getOwner(), myKey.getRevision());

                    /* Break the loop */
                    break;
                }

                /* If we have source directories */
                if (myEntry.hasSourceDirs()) {
                    /* Add entry for later processing */
                    mySourceDirs.add(myEntry);
                }

                /* Access the view */
                SvnRevisionKey myKey = myEntry.getRevisionKey();
                adjustView(mySource.getRevision(), myComp, myKey);

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
                processSourceDirs(mySourceDirs);
            }
        }

        /**
         * Adjust view.
         * @param pStartRev the starting revision
         * @param pComp the component.
         * @param pKey the revisionKey
         * @throws JOceanusException on error
         */
        private void adjustView(final SVNRevision pStartRev,
                                final String pComp,
                                final SvnRevisionKey pKey) throws JOceanusException {
            /* Determine the required revision */
            long myStart = pStartRev.getNumber();
            SVNRevision myRevision = pKey.getRevision();
            long myRev = myRevision.getNumber();
            String myBase = pKey.getPath();

            /* Loop through the views in descending order */
            int mySize = theViews.size();
            ListIterator<SvnExtractView> myIterator = theViews.listIterator(mySize);
            while (myIterator.hasPrevious()) {
                SvnExtractView myView = myIterator.previous();

                /* Obtain revision and skip over if too recent */
                long myCurr = myView.getRevision().getNumber();
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
                    myView = new SvnExtractView(myView, myRevision);
                    myIterator.next();
                    myIterator.add(myView);
                }

                /* Add the directory and return */
                myView.addDirectory(pComp, theRepo.getURL(myBase));
                return;
            }

            /* None found before end of list, so allocate new view */
            SvnExtractView myView = new SvnExtractView(theRepo, myRevision);
            theViews.add(0, myView);
            myView.addDirectory(pComp, theRepo.getURL(myBase));
        }
    }

    /**
     * Extract View list.
     */
    public static class SvnExtractViewList
            extends ArrayList<SvnExtractView>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -3370006885632304998L;

        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnExtractViewList.class.getSimpleName());

        /**
         * Size field.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }
    }

    /**
     * Extract View.
     */
    public static final class SvnExtractView
            implements JDataContents {
        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnExtractItem.class.getSimpleName());

        /**
         * Revision field.
         */
        private static final JDataField FIELD_REV = FIELD_DEFS.declareLocalField("Revision");

        /**
         * Items field.
         */
        private static final JDataField FIELD_ITEMS = FIELD_DEFS.declareLocalField("Items");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return theRevision.toString();
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_REV.equals(pField)) {
                return theRevision.toString();
            }
            if (FIELD_ITEMS.equals(pField)) {
                return theItems;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Repository.
         */
        private final SvnRepository theRepo;

        /**
         * Revision.
         */
        private final SVNRevision theRevision;

        /**
         * Extract item list.
         */
        private final SvnExtractItemList theItems;

        /**
         * Obtain the revision.
         * @return the revision
         */
        public SVNRevision getRevision() {
            return theRevision;
        }

        /**
         * Obtain the item iterator.
         * @return the iterator
         */
        public Iterator<SvnExtractItem> elementIterator() {
            return theItems.iterator();
        }

        /**
         * Constructor.
         * @param pRepo the repository
         * @param pRevision the revision
         */
        private SvnExtractView(final SvnRepository pRepo,
                               final SVNRevision pRevision) {
            /* Store parameters */
            theRepo = pRepo;
            theRevision = pRevision;

            /* Create the list */
            theItems = new SvnExtractItemList();
        }

        /**
         * Constructor.
         * @param pView the view to copy from
         * @param pRevision the revision
         * @throws JOceanusException on error
         */
        private SvnExtractView(final SvnExtractView pView,
                               final SVNRevision pRevision) throws JOceanusException {
            /* Initialise item */
            this(pView.theRepo, pRevision);

            /* Loop through the underlying items */
            Iterator<SvnExtractItem> myIterator = pView.elementIterator();
            while (myIterator.hasNext()) {
                SvnExtractItem myItem = myIterator.next();

                /* Add the item */
                theItems.addItem(myItem);
            }
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            StringBuilder myBuilder = new StringBuilder();

            /* Output the title */
            myBuilder.append("\n\nRevision ");
            myBuilder.append(theRevision.toString());

            /* Add Elements */
            Iterator<SvnExtractItem> myIterator = theItems.iterator();
            while (myIterator.hasNext()) {
                SvnExtractItem myEl = myIterator.next();

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
         * @throws JOceanusException on error
         */
        private void setBaseDir(final SVNURL pBaseDir) throws JOceanusException {
            SvnExtractItem myItem = new SvnExtractItem(pBaseDir);
            theItems.addItem(myItem);
        }

        /**
         * Add directory.
         * @param pTarget the target
         * @param pBaseDir the base directory
         * @throws JOceanusException on error
         */
        private void addDirectory(final String pTarget,
                                  final SVNURL pBaseDir) throws JOceanusException {
            SvnExtractItem myItem = new SvnExtractItem(pTarget, pBaseDir);
            theItems.addItem(myItem);
        }

        /**
         * Extract item.
         * @param pTarget the target location
         * @throws JOceanusException on error
         */
        public void extractItem(final File pTarget) throws JOceanusException {
            /* Clear the target directory */
            Directory.clearDirectory(pTarget);

            /* Access update client */
            SVNClientManager myMgr = theRepo.getClientManager();
            SVNUpdateClient myUpdate = myMgr.getUpdateClient();
            myUpdate.setExportExpandsKeywords(false);

            /* Protect against exceptions */
            try {
                /* Loop through the items */
                Iterator<SvnExtractItem> myIterator = elementIterator();
                while (myIterator.hasNext()) {
                    SvnExtractItem myItem = myIterator.next();

                    /* Determine the target */
                    File myTarget = myItem.getTarget(pTarget);

                    /* Export the item */
                    myUpdate.doExport(myItem.getSource(), myTarget, theRevision, theRevision, null, false, SVNDepth.INFINITY);
                }

            } catch (SVNException e) {
                throw new JThemisIOException("Failed to export View", e);
            } finally {
                theRepo.releaseClientManager(myMgr);
            }
        }
    }

    /**
     * Extract Item.
     */
    public static final class SvnExtractItem
            implements JDataContents {
        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnExtractItem.class.getSimpleName());

        /**
         * Target field.
         */
        private static final JDataField FIELD_TARGET = FIELD_DEFS.declareLocalField("Target");

        /**
         * Source field.
         */
        private static final JDataField FIELD_SOURCE = FIELD_DEFS.declareLocalField("Source");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return toString();
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_TARGET.equals(pField)) {
                return theTarget;
            }
            if (FIELD_SOURCE.equals(pField)) {
                return theSource.toString();
            }
            return JDataFieldValue.UNKNOWN;
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
         * Constructor.
         * @param pSource the source for the element
         */
        private SvnExtractItem(final SVNURL pSource) {
            this(null, pSource);
        }

        /**
         * Constructor.
         * @param pTarget the target for the element
         * @param pSource the source for the element
         */
        private SvnExtractItem(final String pTarget,
                               final SVNURL pSource) {
            /* Store parameters */
            theTarget = pTarget;
            theSource = pSource;
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
            StringBuilder myBuilder = new StringBuilder();

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
    public static class SvnExtractItemList
            extends ArrayList<SvnExtractItem>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6879688737974444297L;

        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnExtractItemList.class.getSimpleName());

        /**
         * Size field.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            /* Create a stringBuilder */
            StringBuilder myBuilder = new StringBuilder();

            /* Add Branches */
            Iterator<SvnExtractItem> myIterator = iterator();
            while (myIterator.hasNext()) {
                SvnExtractItem myItem = myIterator.next();

                /* Add to output */
                myBuilder.append('\n');
                myBuilder.append(myItem.toString());
            }

            /* Return the details */
            return myBuilder.toString();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Add item to list (discarding duplicates).
         * @param pItem the item to add.
         * @throws JOceanusException on error
         */
        private void addItem(final SvnExtractItem pItem) throws JOceanusException {
            /* Loop through the existing items */
            Iterator<SvnExtractItem> myIterator = iterator();
            while (myIterator.hasNext()) {
                SvnExtractItem myEntry = myIterator.next();

                /* If we have matching target */
                if (Difference.isEqual(myEntry.getTarget(), pItem.getTarget())) {
                    /* Reject if different path */
                    if (!Difference.isEqual(myEntry.getSource(), pItem.getSource())) {
                        throw new JThemisDataException(myEntry, "Conflicting sources");
                    }

                    /* Discard the duplicate */
                    return;
                }
            }

            /* Add the unique entry */
            add(pItem);
        }
    }
}
