/*******************************************************************************
 * Subversion: Java SubVersion Management
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
package net.sourceforge.JSvnManager.project;

import java.util.ArrayList;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Project element of POM.
 * @author Tony Washer
 */
public final class ProjectId implements JDataContents {
    /**
     * Group node name.
     */
    private static final String NODENAME_GROUP = "groupId";

    /**
     * Artifact node name.
     */
    private static final String NODENAME_ARTIFACT = "artifactId";

    /**
     * Version node name.
     */
    private static final String NODENAME_VERSION = "version";

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
     * XML POM representation.
     */
    private final Document theDocument;

    /**
     * The groupId text.
     */
    private String theGroupId;

    /**
     * The artifactId text.
     */
    private String theArtifactId;

    /**
     * The version text.
     */
    private String theVersion;

    /**
     * The version node.
     */
    private Element theVersionNode;

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
        return theVersionNode.getTextContent();
    }

    /**
     * Constructor.
     * @param pParent the parent node
     * @throws JDataException on error
     */
    protected ProjectId(final Node pParent) throws JDataException {
        /* Store document */
        theDocument = pParent.getOwnerDocument();

        /* Loop through the nodes */
        for (Node myNode = pParent.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Ignore non-elements */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String myName = myNode.getNodeName();

            /* Access group id */
            if (myName.equals(NODENAME_GROUP)) {
                /* Access the text */
                theGroupId = myNode.getTextContent();

                /* Access artifact id */
            } else if (myName.equals(NODENAME_ARTIFACT)) {
                /* Access the text */
                theArtifactId = myNode.getTextContent();

                /* Access version */
            } else if (myName.equals(NODENAME_VERSION)) {
                /* Access the element and text */
                theVersionNode = (Element) myNode;
                theVersion = theVersionNode.getTextContent();
            }
        }

        /* Handle missing elements */
        if ((theGroupId == null) || (theArtifactId == null) || (theVersion == null)) {
            throw new JDataException(ExceptionClass.DATA, "Invalid version definition");
        }

        /* Strip off SNAPSHOT indication */
        if (theVersion.endsWith(SUFFIX_SNAPSHOT)) {
            /* Strip it off */
            theVersion = theVersion.substring(0, theVersion.length() - SUFFIX_SNAPSHOT.length());
        }
    }

    /**
     * Set new version.
     * @param pVersion the version
     */
    protected void setSnapshotVersion(final String pVersion) {
        /* Delete child elements of the version node */
        clearChildren(theVersionNode);

        /* create a new text node and add to the node */
        theVersion = pVersion;
        Text myText = theDocument.createTextNode(pVersion + SUFFIX_SNAPSHOT);
        theVersionNode.appendChild(myText);
    }

    /**
     * Set new version.
     * @param pVersion the version
     */
    protected void setVersion(final String pVersion) {
        /* Delete child elements of the version node */
        clearChildren(theVersionNode);

        /* create a new text node and add to the node */
        theVersion = pVersion;
        Text myText = theDocument.createTextNode(pVersion);
        theVersionNode.appendChild(myText);
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

        /* Delete child elements of the version node */
        clearChildren(theVersionNode);

        /* create a new text node and add to the node */
        theVersion = pId.getVersion();
        Text myText = theDocument.createTextNode(pId.getVersion());
        theVersionNode.appendChild(myText);
    }

    /**
     * Clear children.
     * @param pNode the node
     */
    private void clearChildren(final Node pNode) {
        /* Delete child elements of the version node */
        Node myNode = pNode.getFirstChild();
        while (myNode != null) {
            /* Determine next node */
            Node myNext = myNode.getNextSibling();

            /* remove any children */
            if (myNode.hasChildNodes()) {
                clearChildren(myNode);
            }
            pNode.removeChild(myNode);

            /* Move to next node */
            myNode = myNext;
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
