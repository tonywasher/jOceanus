/*******************************************************************************
 * JSvnManager: Java SubVersion Management
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jArgo.jSvnManager.project;

import java.util.ArrayList;

import net.sourceforge.jArgo.jDataManager.JDataException;
import net.sourceforge.jArgo.jDataManager.JDataFields;
import net.sourceforge.jArgo.jDataManager.JDataFields.JDataField;
import net.sourceforge.jArgo.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jArgo.jDataManager.JDataObject.JDataFieldValue;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/**
 * Project element of POM.
 * @author Tony Washer
 */
public final class ProjectId implements JDataContents {
    /**
     * SnapShot indication.
     */
    public static final String SUFFIX_SNAPSHOT = "-SNAPSHOT";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(ProjectId.class.getSimpleName());

    /**
     * Group field id.
     */
    private static final JDataField FIELD_GROUP = FIELD_DEFS.declareEqualityField("Group");

    /**
     * Artifact field id.
     */
    private static final JDataField FIELD_ARTIFACT = FIELD_DEFS.declareEqualityField("Artifact");

    /**
     * Version field id.
     */
    private static final JDataField FIELD_VERSION = FIELD_DEFS.declareEqualityField("Version");

    @Override
    public String formatObject() {
        return theGroupId + "_" + theArtifactId + "_" + theVersion;
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_GROUP.equals(pField)) {
            return theGroupId;
        }
        if (FIELD_ARTIFACT.equals(pField)) {
            return theArtifactId;
        }
        if (FIELD_VERSION.equals(pField)) {
            return theVersion;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * The groupId.
     */
    private String theGroupId;

    /**
     * The artifactId.
     */
    private String theArtifactId;

    /**
     * The version.
     */
    private String theVersion;

    /**
     * The version text.
     */
    private String theVersionText;

    /**
     * The project model.
     */
    private final Model theModel;

    /**
     * The project dependency.
     */
    private final Dependency theDependency;

    /**
     * Get GroupId.
     * @return the groupId
     */
    public String getGroupId() {
        return theGroupId;
    }

    /**
     * Get ArtifactId.
     * @return the artifactId
     */
    public String getArtifactId() {
        return theArtifactId;
    }

    /**
     * Get Version.
     * @return the version
     */
    public String getVersion() {
        return theVersion;
    }

    /**
     * Get Version Text.
     * @return the version text
     */
    public String getVersionText() {
        return theVersionText;
    }

    /**
     * Constructor.
     * @param pModel the project model
     * @throws JDataException on error
     */
    protected ProjectId(final Model pModel) throws JDataException {
        /* Store model */
        theModel = pModel;
        theDependency = null;

        /* Access IDs */
        theGroupId = theModel.getGroupId();
        theArtifactId = theModel.getArtifactId();
        theVersionText = theModel.getVersion();
        theVersion = parseVersion();
    }

    /**
     * Constructor.
     * @param pDependency the project dependency
     * @throws JDataException on error
     */
    protected ProjectId(final Dependency pDependency) throws JDataException {
        /* Store dependency */
        theModel = null;
        theDependency = pDependency;

        /* Access IDs */
        theGroupId = theDependency.getGroupId();
        theArtifactId = theDependency.getArtifactId();
        theVersionText = theDependency.getVersion();
        theVersion = parseVersion();
    }

    /**
     * Parse Version.
     * @return the version
     */
    private String parseVersion() {
        /* Strip off SNAPSHOT indication */
        if (theVersionText.endsWith(SUFFIX_SNAPSHOT)) {
            /* Strip it off */
            return theVersionText.substring(0, theVersionText.length() - SUFFIX_SNAPSHOT.length());
        }

        /* Default to versionText */
        return theVersionText;
    }

    /**
     * Set new version.
     * @param pVersion the version
     */
    protected void setSnapshotVersion(final String pVersion) {
        /* Update version and text */
        theVersion = pVersion;
        theVersionText = pVersion + SUFFIX_SNAPSHOT;

        /* Update version */
        updateVersion();
    }

    /**
     * Set new version.
     * @param pVersion the version
     */
    protected void setVersion(final String pVersion) {
        /* Update version and text */
        theVersion = pVersion;
        theVersionText = pVersion;

        /* Update version */
        updateVersion();
    }

    /**
     * Set new version.
     * @param pId the project Id
     */
    protected void setNewVersion(final ProjectId pId) {
        /* Ignore if wrong groupId/versionId */
        if (!theGroupId.equals(pId.getGroupId())) {
            return;
        }
        if (!theArtifactId.equals(pId.getArtifactId())) {
            return;
        }

        /* Set version */
        setVersion(pId.getVersionText());
    }

    /**
     * Update version.
     */
    protected void updateVersion() {
        /* If we are top-level */
        if (theModel != null) {
            /* Set values into model */
            theModel.setGroupId(theGroupId);
            theModel.setArtifactId(theArtifactId);
            theModel.setVersion(theVersionText);

            /* else we are a dependency */
        } else {
            /* Set versions into dependency */
            theDependency.setGroupId(theGroupId);
            theDependency.setArtifactId(theArtifactId);
            theDependency.setVersion(theVersionText);
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Ignore if wrong class */
        if (getClass() != pThat.getClass()) {
            return false;
        }

        /* Access as ProjectId */
        ProjectId myThat = (ProjectId) pThat;

        /* Check the attributes */
        if (!theGroupId.equals(myThat.getGroupId())) {
            return false;
        }
        if (!theArtifactId.equals(myThat.getArtifactId())) {
            return false;
        }
        if (!theVersion.equals(myThat.getVersion())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return theGroupId.hashCode() + theArtifactId.hashCode() + theVersion.hashCode();
    }

    /**
     * Project list.
     */
    public static final class ProjectList extends ArrayList<ProjectId> implements JDataContents {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -5283059941533163228L;

        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(ProjectList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareEqualityField("Size");

        @Override
        public String formatObject() {
            return "ProjectList(" + size() + ")";
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }

            /* Unknown */
            return JDataFieldValue.UnknownField;
        }
    }

    /**
     * Project status.
     */
    public enum ProjectStatus {
        /**
         * Raw. Loaded from project but not merged.
         */
        RAW,

        /**
         * Merging. In the process of merging
         */
        MERGING,

        /**
         * Final. Fully resolved.
         */
        FINAL;
    }
}
