/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jthemis.analysis;

import org.w3c.dom.Element;

/**
 * Maven Module Id.
 */
public class ThemisAnalysisMavenId {
    /**
     * GroupId element.
     */
    private static final String EL_GROUPID = "groupId";

    /**
     * ArtifactId element.
     */
    private static final String EL_ARTIFACTID = "artifactId";

    /**
     * Version element.
     */
    private static final String EL_VERSION = "version";

    /**
     * Parent groupId indication.
     */
    private static final String PARENT_GROUP = "${project.groupId}";

    /**
     * Parent version indication.
     */
    private static final String PARENT_VERSION = "${project.version}";

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
     * Constructor.
     * @param pElement the element containing the values
     */
    ThemisAnalysisMavenId(final Element pElement) {
        /* Access the values */
        theGroupId = ThemisAnalysisMaven.getElementValue(pElement, EL_GROUPID);
        theArtifactId = ThemisAnalysisMaven.getElementValue(pElement, EL_ARTIFACTID);
        theVersion = ThemisAnalysisMaven.getElementValue(pElement, EL_VERSION);
    }

    /**
     * Constructor.
     * @param pElement the element containing the values
     * @param pParent the parent Id
     */
    ThemisAnalysisMavenId(final Element pElement,
                          final ThemisAnalysisMavenId pParent) {
        /* Process as much as we can */
        this(pElement);

        /* Handle missing groupId/version */
        if (theGroupId == null || PARENT_GROUP.equals(theGroupId)) {
            theGroupId = pParent.getGroupId();
        }
        if (theVersion == null || PARENT_VERSION.equals(theVersion)) {
            theVersion = pParent.getVersion();
        }
    }

    /**
     * Obtain the groupId.
     * @return the groupId
     */
    public String getGroupId() {
        return theGroupId;
    }

    /**
     * Obtain the artifactId.
     * @return the artifactId
     */
    public String getArtifactId() {
        return theArtifactId;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public String getVersion() {
        return theVersion;
    }

    @Override
    public String toString() {
        return theGroupId + ":" + theArtifactId + ":" + theVersion;
    }
}
