/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.tonywasher.joceanus.themis.parser.maven;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import org.w3c.dom.Element;

import java.util.Objects;

/**
 * Maven Module Id.
 */
public final class ThemisMavenId {
    /**
     * ElementParser.
     */
    interface ThemisElementParser {
        /**
         * Obtain element value.
         *
         * @param pElement the element
         * @param pValue   the value name
         * @return the value
         */
        String getElementValue(Element pElement,
                               String pValue);
    }

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
     * Scope element.
     */
    private static final String EL_SCOPE = "scope";

    /**
     * Classifier element.
     */
    private static final String EL_CLASSIFIER = "classifier";

    /**
     * Optional element.
     */
    private static final String EL_OPTIONAL = "optional";

    /**
     * The artifactId.
     */
    private final String theArtifactId;

    /**
     * The groupId.
     */
    private String theGroupId;

    /**
     * The version.
     */
    private String theVersion;

    /**
     * The scope.
     */
    private final String theScope;

    /**
     * The classifier.
     */
    private final String theClassifier;

    /**
     * Optional.
     */
    private final String isOptional;

    /**
     * Constructor.
     *
     * @param pParser  the parser
     * @param pElement the element containing the values
     */
    ThemisMavenId(final ThemisElementParser pParser,
                  final Element pElement) {
        /* Access the values */
        theGroupId = pParser.getElementValue(pElement, EL_GROUPID);
        theArtifactId = pParser.getElementValue(pElement, EL_ARTIFACTID);
        theVersion = pParser.getElementValue(pElement, EL_VERSION);
        theScope = pParser.getElementValue(pElement, EL_SCOPE);
        theClassifier = pParser.getElementValue(pElement, EL_CLASSIFIER);
        isOptional = pParser.getElementValue(pElement, EL_OPTIONAL);
    }

    /**
     * Constructor.
     *
     * @param pParser  the parser
     * @param pElement the element containing the values
     * @param pParent  the parentId
     */
    ThemisMavenId(final ThemisElementParser pParser,
                  final Element pElement,
                  final ThemisMavenId pParent) {
        /* Process as much as we can */
        this(pParser, pElement);

        /* Handle missing groupId/version */
        if (theGroupId == null) {
            theGroupId = pParent.getGroupId();
        }
        if (theVersion == null) {
            theVersion = pParent.getVersion();
        }

        /* If we have a ranged version, set to null */
        if (theVersion != null
                && theVersion.startsWith(String.valueOf(ThemisChar.ARRAY_OPEN))) {
            theVersion = null;
        }
    }

    /**
     * Obtain the groupId.
     *
     * @return the groupId
     */
    public String getGroupId() {
        return theGroupId;
    }

    /**
     * Obtain the artifactId.
     *
     * @return the artifactId
     */
    public String getArtifactId() {
        return theArtifactId;
    }

    /**
     * Obtain the version.
     *
     * @return the version
     */
    public String getVersion() {
        return theVersion;
    }

    /**
     * Obtain the scope.
     *
     * @return the scope
     */
    public String getScope() {
        return theScope;
    }

    /**
     * Obtain the classifier.
     *
     * @return the classifier
     */
    public String getClassifier() {
        return theClassifier;
    }

    /**
     * Obtain the optional.
     *
     * @return the optional
     */
    public String isOptional() {
        return isOptional;
    }

    /**
     * is the dependency skippable?
     *
     * @return true/false
     */
    public boolean isSkippable() {
        return "test".equals(theScope)
                || "runtime".equals(theScope)
                || "provided".equals(theScope)
                || isOptional != null;
    }

    /**
     * Adjust the version
     *
     * @param pVersion the new version.
     */
    void adjustVersion(final String pVersion) {
        theVersion = pVersion;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a MavenId */
        if (!(pThat instanceof ThemisMavenId myThat)) {
            return false;
        }

        /* Check components */
        return Objects.equals(theGroupId, myThat.getGroupId())
                && Objects.equals(theArtifactId, myThat.getArtifactId())
                && Objects.equals(theVersion, myThat.getVersion())
                && Objects.equals(theScope, myThat.getScope())
                && Objects.equals(theClassifier, myThat.getClassifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theGroupId, theArtifactId, theVersion, theScope, theClassifier);
    }

    @Override
    public String toString() {
        final String myName = theGroupId + ThemisChar.COLON + theArtifactId + ThemisChar.COLON + theVersion;
        return theClassifier == null ? myName : myName + ThemisChar.COLON + theClassifier;
    }
}

