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
package net.sourceforge.joceanus.jthemis;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for Themis.
 */
public enum ThemisResource
        implements TethysBundleId, MetisDataFieldId {
    /**
     * List Size.
     */
    LIST_SIZE("list.size"),

    /**
     * SCM Name.
     */
    SCM_NAME("scm.name"),

    /**
     * SCM Repository.
     */
    SCM_REPOSITORY("scm.repository"),

    /**
     * SCM Component.
     */
    SCM_COMPONENT("scm.component"),

    /**
     * SCM Components.
     */
    SCM_COMPONENTS("scm.components"),

    /**
     * SCM Branch.
     */
    SCM_BRANCH("scm.branch"),

    /**
     * SCM Branches.
     */
    SCM_BRANCHES("scm.branches"),

    /**
     * SCM Tag.
     */
    SCM_TAG("scm.tag"),

    /**
     * SCM Tags.
     */
    SCM_TAGS("scm.tags"),

    /**
     * SCM Project.
     */
    SCM_PROJECT("scm.project"),

    /**
     * SCM Base.
     */
    SCM_BASE("scm.base"),

    /**
     * SCM Owner.
     */
    SCM_OWNER("scm.owner"),

    /**
     * SVN Trunk.
     */
    SVN_TRUNK("svn.trunk"),

    /**
     * SVN History.
     */
    SVN_HISTORY("svn.history"),

    /**
     * SVN RevisionPath.
     */
    SVN_REVISIONPATH("svn.revisionPath"),

    /**
     * SVN Revision.
     */
    SVN_REVISION("svn.revision"),

    /**
     * SVN NodeKind.
     */
    SVN_NODEKIND("svn.nodeKind"),

    /**
     * SVN Alias.
     */
    SVN_ALIAS("svn.alias"),

    /**
     * SVN Updates.
     */
    SVN_UPDATES("svn.updates"),

    /**
     * SVN Location.
     */
    SVN_LOCATION("svn.location"),

    /**
     * SVN Status.
     */
    SVN_STATUS("svn.status"),

    /**
     * SVN PropStatus.
     */
    SVN_PROPSTATUS("svn.propStatus"),

    /**
     * SVN CopyFrom.
     */
    SVN_COPYFROM("svn.copyFrom"),

    /**
     * SVN CopyRevision.
     */
    SVN_COPYREVISION("svn.copyRevision"),

    /**
     * SVN Date.
     */
    SVN_DATE("svn.date"),

    /**
     * SVN Source.
     */
    SVN_SOURCE("svn.source"),

    /**
     * SVN Target.
     */
    SVN_TARGET("svn.target"),

    /**
     * SVN BasedOn.
     */
    SVN_BASEDON("svn.basedOn"),

    /**
     * SVN LogMsg.
     */
    SVN_LOGMSG("svn.logMessage"),

    /**
     * SVN Issues.
     */
    SVN_ISSUES("svn.issues"),

    /**
     * SVN Comment.
     */
    SVN_COMMENT("svn.comment"),

    /**
     * SVN Type.
     */
    SVN_TYPE("svn.type"),

    /**
     * SVN Path.
     */
    SVN_PATH("svn.path"),

    /**
     * SVN Origin.
     */
    SVN_ORIGIN("svn.origin"),

    /**
     * SVN OriginDef.
     */
    SVN_ORIGINDEF("svn.originDef"),

    /**
     * SVN LogMsg.
     */
    SVN_SOURCEDIRS("svn.sourceDirs"),

    /**
     * SVN Items.
     */
    SVN_ITEMS("svn.items"),

    /**
     * SVN Anchor.
     */
    SVN_ANCHOR("svn.anchor"),

    /**
     * SVN Views.
     */
    SVN_VIEWS("svn.views"),

    /**
     * SVN AnchorMap.
     */
    SVN_ANCHORMAP("svn.anchorMap"),

    /**
     * SVN Revisions.
     */
    SVN_REVISIONS("svn.revisions"),

    /**
     * SVN KeyMap.
     */
    SVN_KEYMAP("svn.keyMap"),

    /**
     * Git CommitMap.
     */
    GIT_COMMITMAP("git.commitMap"),

    /**
     * Git RevisionMap.
     */
    GIT_REVISIONMAP("git.revisionMap"),

    /**
     * Git CommitId.
     */
    GIT_COMMITID("git.commitId"),

    /**
     * Git Parents.
     */
    GIT_PARENTS("git.parents"),

    /**
     * Git Revision.
     */
    GIT_REVISION("git.revision"),

    /**
     * Git Revision#.
     */
    GIT_REVISIONNO("git.revisionNo"),

    /**
     * Git Revision.
     */
    GIT_REVISIONKEY("git.revisionKey"),

    /**
     * Git Remote.
     */
    GIT_REMOTE("git.remote"),

    /**
     * Git Target.
     */
    GIT_TARGET("git.target"),

    /**
     * Git Null.
     */
    GIT_NULL("git.null"),

    /**
     * Git BadParent.
     */
    GIT_BADPARENT("git.badParent"),

    /**
     * Git Conflicts.
     */
    GIT_CONFLICTS("git.hasConflicts"),

    /**
     * Project Name.
     */
    USER_USERNAME("user.userName"),

    /**
     * User Name.
     */
    USER_NAME("user.name"),

    /**
     * User Projects.
     */
    USER_PROJECTS("user.projects"),

    /**
     * Project Name.
     */
    PROJECT_NAME(USER_NAME),

    /**
     * Project Summary.
     */
    PROJECT_SUMMARY("project.summary"),

    /**
     * Project Description.
     */
    PROJECT_DESC("project.desc"),

    /**
     * Project TicketSets.
     */
    PROJECT_TICKETSETS("project.ticketSets"),

    /**
     * TicketSet Name.
     */
    TICKETSET_NAME(PROJECT_NAME),

    /**
     * TicketSet Mount.
     */
    TICKETSET_MOUNT("ticketSet.mount"),

    /**
     * TicketSet Tickets.
     */
    TICKETSET_TICKETS("ticketSet.tickets"),

    /**
     * Ticket Id.
     */
    TICKET_ID("ticket.id"),

    /**
     * Ticket Summary.
     */
    TICKET_SUMMARY(PROJECT_SUMMARY),

    /**
     * Ticket Description.
     */
    TICKET_DESC(PROJECT_DESC),

    /**
     * Ticket Labels.
     */
    TICKET_LABELS("ticket.labels"),

    /**
     * Ticket Status.
     */
    TICKET_STATUS(SVN_STATUS),

    /**
     * Ticket ReportedBy.
     */
    TICKET_REPORTEDBY("ticket.reportedBy"),

    /**
     * Ticket ReportedBy.
     */
    TICKET_ASSIGNEDTO("ticket.assignedTo"),

    /**
     * MAVEN Group.
     */
    MAVEN_GROUP("maven.group"),

    /**
     * MAVEN Artifact.
     */
    MAVEN_ARTIFACT("maven.artifact"),

    /**
     * MAVEN Version.
     */
    MAVEN_VERSION("maven.version"),

    /**
     * MAVEN Id.
     */
    MAVEN_ID("maven.id"),

    /**
     * MAVEN Dependencies.
     */
    MAVEN_DEPENDENCIES("maven.dependencies"),

    /**
     * MAVEN subModules.
     */
    MAVEN_SUBMODULES("maven.subModules");

    /**
     * The Resource Builder.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(ThemisDataException.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    ThemisResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    /**
     * Constructor.
     * @param pSource the source key
     */
    ThemisResource(final TethysBundleId pSource) {
        theKeyName = pSource.getKeyName();
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "themis";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }
}
