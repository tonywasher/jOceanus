/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.parser.proj;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.exc.ThemisIOException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Maven pom.xml parser.
 */
public class ThemisXAnalysisMaven {
    /**
     * Project filename.
     */
    public static final String POM = "pom.xml";

    /**
     * Document name.
     */
    private static final String DOC_NAME = "project";

    /**
     * Parent element.
     */
    private static final String EL_PARENT = "parent";

    /**
     * Modules element.
     */
    private static final String EL_MODULES = "modules";

    /**
     * Module element.
     */
    private static final String EL_MODULE = "module";

    /**
     * Dependencies element.
     */
    private static final String EL_DEPENDENCIES = "dependencies";

    /**
     * Dependency element.
     */
    private static final String EL_DEPENDENCY = "dependency";

    /**
     * Properties element.
     */
    private static final String EL_PROPERTIES = "properties";

    /**
     * The Id.
     */
    private final ThemisXAnalysisMavenId theId;

    /**
     * The modules.
     */
    private final List<String> theModules;

    /**
     * The dependencies.
     */
    private final List<ThemisXAnalysisMavenId> theDependencies;

    /**
     * The parent.
     */
    private final ThemisXAnalysisMaven theParent;

    /**
     * The properties.
     */
    private final Map<String, String> theProperties;

    /**
     * Constructor.
     * @param pParent the parent pom
     * @param pInputStream the input stream to read
     * @throws OceanusException on error
     */
    ThemisXAnalysisMaven(final ThemisXAnalysisMaven pParent,
                         final InputStream pInputStream) throws OceanusException {
        /* Store the parent */
        theParent = pParent;

        /* Create the module list */
        theModules = new ArrayList<>();
        theDependencies = new ArrayList<>();
        theProperties = new LinkedHashMap<>();

        /* Protect against exceptions */
        try (BufferedInputStream myInBuffer = new BufferedInputStream(pInputStream)) {
            final DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            myFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            final DocumentBuilder myBuilder = myFactory.newDocumentBuilder();

            /* Build the document from the input stream */
            final Document myDocument = myBuilder.parse(myInBuffer);
            theId = parseProjectFile(myDocument);

            /* Handle exceptions */
        } catch (IOException
                 | ParserConfigurationException
                 | SAXException e) {
            throw new ThemisIOException("Exception accessing Pom file", e);
        }
    }

    @Override
    public String toString() {
        return theId.toString();
    }

    /**
     * Obtain the list of modules.
     * @return the list
     */
    public ThemisXAnalysisMavenId getMavenId() {
        return theId;
    }

    /**
     * Obtain the list of modules.
     * @return the modules
     */
    public List<String> getModules() {
        return theModules;
    }

    /**
     * Obtain the list of dependencies.
     * @return the dependencies
     */
    public List<ThemisXAnalysisMavenId> getDependencies() {
        return theDependencies;
    }

    /**
     * Parse the project file.
     * @param pDocument the document
     * @return the MavenId
     * @throws OceanusException on error
     */
    public ThemisXAnalysisMavenId parseProjectFile(final Document pDocument) throws OceanusException {
        /* Access the document element */
        final Element myDoc = pDocument.getDocumentElement();

        /* Check that the document name is correct */
        if (!Objects.equals(myDoc.getNodeName(), DOC_NAME)) {
            throw new ThemisDataException("Invalid document type");
        }

        /* Process any properties */
        final Element myProperties = getElement(myDoc, EL_PROPERTIES);
        if (myProperties != null) {
            for (Node myChild = myProperties.getFirstChild();
                myChild != null;
                myChild = myChild.getNextSibling()) {
                if (myChild instanceof Element myElement) {
                    theProperties.put("${" + myElement.getNodeName() + "}", myElement.getTextContent());
                }
            }
        }

        /* Obtain parent definition if any */
        final Element myParentEl = getElement(myDoc, EL_PARENT);
        final ThemisXAnalysisMavenId myParent = myParentEl == null
                ? null
                : new ThemisXAnalysisMavenId(myParentEl);

        /* Obtain our mavenId */
        final ThemisXAnalysisMavenId myId = new ThemisXAnalysisMavenId(myDoc, myParent);

        /* Process modules */
        final Element myModules = getElement(myDoc, EL_MODULES);
        processModules(myModules);

        /* Process dependencies */
        final Element myDependencies = getElement(myDoc, EL_DEPENDENCIES);
        processDependencies(myDependencies, myId);

        /* Return the Id */
        return myId;
    }

    /**
     * Obtain element value.
     * @param pElement the element
     * @param pValue the value name
     * @return the value
     */
    String getElementValue(final Element pElement,
                           final String pValue) {
        /* Return null if no element */
        if (pElement == null) {
            return null;
        }

        /* Loop through the children */
        for (Node myChild = pElement.getFirstChild();
             myChild != null;
             myChild = myChild.getNextSibling()) {
            /* Return result if we have a match */
            if (myChild instanceof Element
                    && pValue.equals(myChild.getNodeName())) {
                return replaceProperty(myChild.getTextContent());
            }
        }

        /* Not found */
        return null;
    }

    /**
     * Obtain element value.
     * @param pElement the element
     * @param pValue the value name
     * @return the value
     */
    static Element getElement(final Element pElement,
                              final String pValue) {
        /* Return null if no element */
        if (pElement == null) {
            return null;
        }

        /* Loop through the children */
        for (Node myChild = pElement.getFirstChild();
             myChild != null;
             myChild = myChild.getNextSibling()) {
            /* Return result if we have a match */
            if (myChild instanceof Element myElement
                    && pValue.equals(myChild.getNodeName())) {
                return myElement;
            }
        }

        /* Not found */
        return null;
    }

    /**
     * Process modules.
     * @param pModules the modules
     */
    private void processModules(final Element pModules) {
        /* Return if no element */
        if (pModules == null) {
            return;
        }

        /* Loop through the children */
        for (Node myChild = pModules.getFirstChild();
             myChild != null;
             myChild = myChild.getNextSibling()) {
            /* Return result if we have a match */
            if (myChild instanceof Element
                    && EL_MODULE.equals(myChild.getNodeName())) {
                theModules.add(myChild.getTextContent());
            }
        }
    }

    /**
     * Process dependencies.
     * @param pDependencies the dependencies
     * @param pParent the parentId
     */
    private void processDependencies(final Element pDependencies,
                                     final ThemisXAnalysisMavenId pParent) {
        /* Return if no element */
        if (pDependencies == null) {
            return;
        }

        /* Loop through the children */
        for (Node myChild = pDependencies.getFirstChild();
             myChild != null;
             myChild = myChild.getNextSibling()) {
            /* Return result if we have a match */
            if (myChild instanceof Element myElement
                    && EL_DEPENDENCY.equals(myChild.getNodeName())) {
                theDependencies.add(new ThemisXAnalysisMavenId(myElement, pParent));
            }
        }
    }

    /**
     * Replace property.
     * @param pValue the value
     * @return the value or the replaced property
     */
    private String replaceProperty(final String pValue) {
        final String myValue = theProperties.get(pValue);
        if (myValue != null) {
            return myValue;
        }
        return theParent != null ? theParent.replaceProperty(pValue) : pValue;
    }

    /**
     * Maven Module Id.
     */
    public final class ThemisXAnalysisMavenId {
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
         * Constructor.
         * @param pElement the element containing the values
         */
        private ThemisXAnalysisMavenId(final Element pElement) {
            /* Access the values */
            theGroupId = getElementValue(pElement, EL_GROUPID);
            theArtifactId = getElementValue(pElement, EL_ARTIFACTID);
            theVersion = getElementValue(pElement, EL_VERSION);
        }

        /**
         * Constructor.
         * @param pElement the element containing the values
         * @param pParent the parent Id
         */
       private ThemisXAnalysisMavenId(final Element pElement,
                                      final ThemisXAnalysisMavenId pParent) {
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
        public boolean equals(final Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Make sure that the object is a MavenId */
            if (!(pThat instanceof ThemisXAnalysisMavenId myThat)) {
                return false;
            }

            /* Check components */
            return Objects.equals(theGroupId, myThat.getGroupId())
                    && Objects.equals(theArtifactId, myThat.getArtifactId())
                    && Objects.equals(theVersion, myThat.getVersion());
        }

        @Override
        public int hashCode() {
            return Objects.hash(theGroupId, theArtifactId, theVersion);
        }

        @Override
        public String toString() {
            return theGroupId + ThemisXAnalysisChar.COLON + theArtifactId + ThemisXAnalysisChar.COLON + theVersion;
        }

        /**
         * Obtain the mavenJar.
         * @return the mavenJar path
         */
        public File getMavenJarPath() {
            /* Determine the repository base */
            File myBase = new File(System.getProperty("user.home"));
            myBase = new File(myBase, ".m2");
            myBase = new File(myBase, "repository");
            myBase = new File(myBase, theGroupId.replace(ThemisXAnalysisChar.PERIOD, ThemisXAnalysisChar.COMMENT));
            myBase = new File(myBase, theArtifactId);
            myBase = new File(myBase, theVersion);
            myBase = new File(myBase, theArtifactId + '-' + theVersion + ".jar");
            return myBase;
        }
    }
}
