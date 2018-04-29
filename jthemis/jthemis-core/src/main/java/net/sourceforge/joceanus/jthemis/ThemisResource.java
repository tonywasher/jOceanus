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

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for Themis.
 */
public enum ThemisResource
        implements TethysResourceId, MetisDataFieldId {
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
     * SVN Owner.
     */
    SVN_OWNER("svn.owner"),

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
     * Git CommitId.
     */
    GIT_COMMITID("git.commitId"),

    /**
     * Git Parents.
     */
    GIT_PARENTS("git.parents"),

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
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(ThemisDataException.class.getCanonicalName());

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
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }
}
