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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.sf.data.ThemisSfTicket;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnBranchExtractPlan;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnExtractPlan;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnExtractView;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnTagExtractPlan;

/**
 * A record of Subversion comments per Revision.
 * <p>
 * This is used to parse the comments to extract and remove the Jira reference in preparation for
 * migration to SourceForge tickets
 */
public class ThemisSvnRevisions
        implements MetisFieldItem {
    /**
     * Issue prefix.
     */
    static final String ISSUE_PFX = "Issue #:";

    /**
     * BugId prefix.
     */
    static final String BUGID_PFX = "bugid:";

    /**
     * Issue prefix.
     */
    static final String WORKING_PFX = "Working ";

    /**
     * Colon string.
     */
    static final String STR_COLON = ":";

    /**
     * NewLine string.
     */
    static final String STR_NEWLINE = "\n";

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSvnRevisions> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnRevisions.class);

    /**
     * Repository field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.GIT_REVISIONMAP, ThemisSvnRevisions::getRevisionMap);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_KEYMAP, ThemisSvnRevisions::getKeyMap);
    }

    /**
     * The map of revision# to revision.
     */
    private final Map<Long, ThemisSvnRevision> theRevisionMap;

    /**
     * The map of jiraKey to revisionList.
     */
    private final Map<String, List<ThemisSvnRevision>> theKeyMap;

    /**
     * Constructor.
     * @param pExtract the extract to use as source
     */
    public ThemisSvnRevisions(final ThemisSvnExtract pExtract) {
        /* Create the hashMaps */
        theRevisionMap = new HashMap<>();
        theKeyMap = new LinkedHashMap<>();

        /* Process the extractPlans */
        processPlans(pExtract);
    }

    @Override
    public MetisFieldSet<ThemisSvnRevisions> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String toString() {
        return FIELD_DEFS.getName();
    }

    /**
     * Obtain the revision map.
     * @return the revision map
     */
    private Map<Long, ThemisSvnRevision> getRevisionMap() {
        return theRevisionMap;
    }

    /**
     * Obtain the key map.
     * @return the key map
     */
    private Map<String, List<ThemisSvnRevision>> getKeyMap() {
        return theKeyMap;
    }

    /**
     * Obtain the keyIterator.
     * @return the key iterator
     */
    public Iterator<String> keyIterator() {
        return theKeyMap.keySet().iterator();
    }

    /**
     * Register SourceForge Ticket.
     * @param pIssueKey the issue
     * @param pTicket the ticket
     */
    public void registerTicket(final String pIssueKey,
                               final ThemisSfTicket pTicket) {
        /* Register ticket with each revision */
        final List<ThemisSvnRevision> myList = theKeyMap.get(pIssueKey);
        for (ThemisSvnRevision myRevision : myList) {
            myRevision.registerTicket(pTicket);
        }
    }

    /**
     * Obtain comment for View.
     * @param pView the view
     * @return the comment
     */
    public String getCommentForView(final ThemisSvnExtractView pView) {
        /* Access revision# */
        final Long myRevNo = pView.getRevision().getNumber();

        /* Access the stored revision comment */
        final ThemisSvnRevision myRevision = theRevisionMap.get(myRevNo);
        return myRevision == null
                                  ? null
                                  : myRevision.getComment();
    }

    /**
     * Process plans.
     * @param pExtract the extract to use as source
     */
    private void processPlans(final ThemisSvnExtract pExtract) {
        /* Process the trunk plan */
        processPlan(pExtract.getTrunkPlan());

        /* Process the branches */
        final Iterator<ThemisSvnBranchExtractPlan> myBranchIterator = pExtract.branchIterator();
        while (myBranchIterator.hasNext()) {
            final ThemisSvnBranchExtractPlan myPlan = myBranchIterator.next();

            /* Process the plan */
            processPlan(myPlan);
        }

        /* Process the tags */
        final Iterator<ThemisSvnTagExtractPlan> myTagIterator = pExtract.tagIterator();
        while (myTagIterator.hasNext()) {
            final ThemisSvnTagExtractPlan myPlan = myTagIterator.next();

            /* Process the plan */
            processPlan(myPlan);
        }
    }

    /**
     * Process a plan.
     * @param pPlan the plan
     */
    private void processPlan(final ThemisSvnExtractPlan<?> pPlan) {
        /* Iterate through the elements */
        final Iterator<ThemisSvnExtractView> myIterator = pPlan.viewIterator();
        while (myIterator.hasNext()) {
            final ThemisSvnExtractView myView = myIterator.next();

            /* Register the view */
            registerView(myView);
        }
    }

    /**
     * Register the comment.
     * @param pView the view
     */
    private void registerView(final ThemisSvnExtractView pView) {
        /* Access revision and comment */
        final Long myRevision = pView.getRevision().getNumber();
        final String myComment = pView.getLogMessage().trim();

        /* Update the revision map */
        theRevisionMap.computeIfAbsent(myRevision, r -> new ThemisSvnRevision(this, r, myComment));
    }

    /**
     * Register the issue to the revision.
     * @param pIssue the issue
     * @param pRevision the revision
     */
    void registerIssue(final String pIssue,
                       final ThemisSvnRevision pRevision) {
        /* Add this revision to the list of revisions referred to by the issue */
        final List<ThemisSvnRevision> myList = theKeyMap.computeIfAbsent(pIssue, i -> new ArrayList<>());
        myList.add(pRevision);
    }

    /**
     * The revision.
     */
    private static class ThemisSvnRevision
            implements MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisSvnRevision> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnRevision.class);

        /**
         * Repository field id.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_LOGMSG, ThemisSvnRevision::getOriginal);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_REVISION, ThemisSvnRevision::getRevision);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_ISSUES, ThemisSvnRevision::getJiraIssues);
            FIELD_DEFS.declareLocalField(ThemisResource.TICKETSET_TICKETS, ThemisSvnRevision::getSfTickets);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_COMMENT, ThemisSvnRevision::getComment);
        }

        /**
         * The revisionsSet.
         */
        private final ThemisSvnRevisions theRevisions;

        /**
         * The original comment.
         */
        private final String theOriginal;

        /**
         * The Subversion revision.
         */
        private final Long theRevision;

        /**
         * The Jira issues.
         */
        private final List<String> theJiraIssues;

        /**
         * The SourceForge tickets.
         */
        private final List<ThemisSfTicket> theSfTickets;

        /**
         * The core comment.
         */
        private String theComment;

        /**
         * Constructor.
         * @param pRevisions the revisionSet
         * @param pRevision the revision
         * @param pComment the log comment
         */
        ThemisSvnRevision(final ThemisSvnRevisions pRevisions,
                          final Long pRevision,
                          final String pComment) {
            /* Store values */
            theRevisions = pRevisions;
            theOriginal = pComment.trim();
            theComment = theOriginal;
            theRevision = pRevision;

            /* Initialise lists */
            theJiraIssues = new ArrayList<>();
            theSfTickets = new ArrayList<>();

            /* Process the various parts of the comment */
            processIssueNo();
            processBugId();
            processWorking();
        }

        @Override
        public MetisFieldSet<ThemisSvnRevision> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String toString() {
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(theRevision)
                    .append(STR_COLON)
                    .append(theJiraIssues)
                    .append(STR_COLON)
                    .append(theComment);
            return myBuilder.toString();
        }

        /**
         * Register the sourceForge Ticket.
         * @param pTicket the ticket
         */
        private void registerTicket(final ThemisSfTicket pTicket) {
            theSfTickets.add(pTicket);
        }

        /**
         * Obtain the original.
         * @return the original
         */
        private String getOriginal() {
            return theOriginal;
        }

        /**
         * Obtain the revision.
         * @return the revision
         */
        private Long getRevision() {
            return theRevision;
        }

        /**
         * Obtain the list of jiraIssues.
         * @return the issues
         */
        private List<String> getJiraIssues() {
            return theJiraIssues;
        }

        /**
         * Obtain the list of sourceForge tickets.
         * @return the issues
         */
        private List<ThemisSfTicket> getSfTickets() {
            return theSfTickets;
        }

        /**
         * Obtain the comment.
         * @return the comment
         */
        private String getComment() {
            final StringBuilder myBuilder = new StringBuilder();

            /* Add references */
            for (ThemisSfTicket myTicket : theSfTickets) {
                myBuilder.append(myTicket.getReference())
                        .append(' ');
            }

            /* Add comment and return the full comment */
            myBuilder.append(theComment);
            return myBuilder.toString();
        }

        /**
         * Process the issueNo indication.
         */
        private void processIssueNo() {
            /* Look for "Issue #:" at start */
            if (theComment.startsWith(ISSUE_PFX)) {
                /* Strip out issue details (there is always an EOL in this case) */
                final int myEOL = theComment.indexOf(STR_NEWLINE);
                final String myIssue = theComment.substring(ISSUE_PFX.length(), myEOL).trim();
                theComment = theOriginal.substring(myEOL).trim();

                /* Register the issue */
                registerIssue(myIssue);
            }
        }

        /**
         * Process the bugId indication.
         */
        private void processBugId() {
            /* Look for "bugId:" */
            final int myIndex = theComment.indexOf(BUGID_PFX);
            if (myIndex != -1) {
                /* Look for end of line */
                final int myEOL = theComment.indexOf(STR_NEWLINE, myIndex);

                /* Rebuild the comment */
                String myComment = theComment.substring(0, myIndex);
                if (myEOL != -1) {
                    myComment += theComment.substring(myEOL + 1);
                }
                theComment = myComment.trim();
            }
        }

        /**
         * Process the working indication.
         */
        private void processWorking() {
            /* Look for "Working xxxxx :" */
            final int myIndex = theComment.indexOf(WORKING_PFX);
            if (myIndex != -1) {
                /* Look for colon */
                final int myColon = theComment.indexOf(STR_COLON, myIndex);

                /* If we have a colon */
                if (myColon != -1) {
                    /* Obtain the issue # */
                    final String myIssue = theComment.substring(myIndex + WORKING_PFX.length(), myColon).trim();

                    /* Register the issue */
                    registerIssue(myIssue);

                    /* Look for end of line */
                    final int myEOL = theComment.indexOf(STR_NEWLINE, myColon);

                    /* Rebuild the comment */
                    String myComment = theComment.substring(0, myIndex);
                    if (myEOL != -1) {
                        myComment += theComment.substring(myEOL + 1);
                    }

                    /* Check for null comment */
                    myComment = myComment.trim();
                    if (myComment.length() == 0
                        || myComment.equals(myIssue)) {
                        myComment = theComment.substring(myColon + 1).trim();
                    }

                    /* Store the comment and look for further issues */
                    theComment = myComment.trim();
                    processWorking();
                }
            }
        }

        /**
         * Register the issue.
         * @param pIssue the issue
         */
        private void registerIssue(final String pIssue) {
            /* If the issue is not known */
            if (!theJiraIssues.contains(pIssue)) {
                /* Add a reference to the issue */
                theJiraIssues.add(pIssue);

                /* Add this revision to the list of revisions referred to by the issue */
                theRevisions.registerIssue(pIssue, this);
            }
        }
    }
}
