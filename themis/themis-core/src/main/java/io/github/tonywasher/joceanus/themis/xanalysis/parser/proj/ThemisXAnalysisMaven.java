/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.proj;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.base.OceanusSystem;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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
     * Properties XPath.
     */
    private static final String XPATH_PROPERTIES = "/project/properties";

    /**
     * Parent XPath.
     */
    private static final String XPATH_PARENT = "/project/parent";

    /**
     * Modules XPath.
     */
    private static final String XPATH_MODULES = "/project/modules";

    /**
     * Dependencies XPath.
     */
    private static final String XPATH_DEPENDENCIES = "/project/dependencies";

    /**
     * XtraDirs XPath.
     */
    private static final String XPATH_XTRADIRS = "/project/build/plugins/plugin[artifactId='build-helper-maven-plugin']"
            + "/executions/execution/configuration/sources";

    /**
     * Module element.
     */
    private static final String EL_MODULE = "module";

    /**
     * Dependency element.
     */
    private static final String EL_DEPENDENCY = "dependency";

    /**
     * Source element.
     */
    private static final String EL_SOURCE = "source";

    /**
     * The XPath.
     */
    private final XPath theXPath;

    /**
     * The Document.
     */
    private final Document theDoc;

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
     * The xtraDirs.
     */
    private final List<String> theXtraDirs;

    /**
     * The parent.
     */
    private final ThemisXAnalysisMaven theParent;

    /**
     * The properties.
     */
    private final Map<String, String> theProperties;

    /**
     * Has the id been found?
     */
    private boolean idFound;

    /**
     * Constructor.
     *
     * @param pParent      the parent pom
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
        theXtraDirs = new ArrayList<>();
        theProperties = new LinkedHashMap<>();
        theProperties.put("${javafx.platform}", OceanusSystem.determineSystem().getClassifier());

        /* Protect against exceptions */
        try (BufferedInputStream myInBuffer = new BufferedInputStream(pInputStream)) {
            final DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            myFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            final DocumentBuilder myBuilder = myFactory.newDocumentBuilder();

            /* Create the XPath */
            theXPath = XPathFactory.newInstance().newXPath();

            /* Build the document from the input stream */
            theDoc = myBuilder.parse(myInBuffer);
            theId = parseProjectFile();

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
     *
     * @return the list
     */
    public ThemisXAnalysisMavenId getMavenId() {
        return theId;
    }

    /**
     * Obtain the list of modules.
     *
     * @return the modules
     */
    public List<String> getModules() {
        return theModules;
    }

    /**
     * Obtain the list of dependencies.
     *
     * @return the dependencies
     */
    public List<ThemisXAnalysisMavenId> getDependencies() {
        return theDependencies;
    }

    /**
     * Obtain the list of extra directories.
     *
     * @return the modules
     */
    public List<String> getXtraDirs() {
        return theXtraDirs;
    }

    /**
     * Parse the project file.
     *
     * @return the MavenId
     * @throws OceanusException on error
     */
    public ThemisXAnalysisMavenId parseProjectFile() throws OceanusException {
        /* Access the document element */
        final Element myDoc = theDoc.getDocumentElement();

        /* Check that the document name is correct */
        if (!Objects.equals(myDoc.getNodeName(), DOC_NAME)) {
            throw new ThemisDataException("Invalid document type");
        }

        /* Process any properties */
        processProperties();

        /* Obtain parent definition if any */
        final Element myParentEl = (Element) findNode(XPATH_PARENT);
        final ThemisXAnalysisMavenId myParent = myParentEl == null
                ? null
                : new ThemisXAnalysisMavenId(myParentEl);

        /* Obtain our mavenId */
        final ThemisXAnalysisMavenId myId = new ThemisXAnalysisMavenId(myDoc, myParent);
        idFound = true;

        /* Process modules */
        processModules();

        /* Process dependencies */
        processDependencies(myId);

        /* Process extra directories */
        processXtraDirs();

        /* Return the Id */
        return myId;
    }

    /**
     * Obtain element value.
     *
     * @param pElement the element
     * @param pValue   the value name
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
     * Obtain the XPath node.
     *
     * @param pPath the Path
     * @return the Node (or null if not found)
     * @throws OceanusException on error
     */
    private Node findNode(final String pPath) throws OceanusException {
        /* Protect against exceptions */
        try {
            return (Node) theXPath.compile(pPath).evaluate(theDoc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            throw new ThemisDataException("Exception locating XPath: " + pPath, e);
        }
    }

    /**
     * Process properties.
     *
     * @throws OceanusException on error
     */
    private void processProperties() throws OceanusException {
        /* Process any properties */
        final Node myProps = findNode(XPATH_PROPERTIES);
        if (myProps != null) {
            for (Node myNode = myProps.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
                if (myNode instanceof Element myElement) {
                    theProperties.put("${" + myElement.getNodeName() + "}", myElement.getTextContent());
                }
            }
        }
    }

    /**
     * Process modules.
     *
     * @throws OceanusException on error
     */
    private void processModules() throws OceanusException {
        /* Process any modules */
        final Node myModules = findNode(XPATH_MODULES);
        if (myModules != null) {
            /* Loop through the children */
            for (Node myChild = myModules.getFirstChild();
                 myChild != null;
                 myChild = myChild.getNextSibling()) {
                /* Return result if we have a match */
                if (myChild instanceof Element
                        && EL_MODULE.equals(myChild.getNodeName())) {
                    theModules.add(myChild.getTextContent());
                }
            }
        }
    }

    /**
     * Process dependencies.
     *
     * @param pParent the parentId
     * @throws OceanusException on error
     */
    private void processDependencies(final ThemisXAnalysisMavenId pParent) throws OceanusException {
        /* Process any dependencies */
        final Node myDependencies = findNode(XPATH_DEPENDENCIES);
        if (myDependencies != null) {
            /* Loop through the children */
            for (Node myChild = myDependencies.getFirstChild();
                 myChild != null;
                 myChild = myChild.getNextSibling()) {
                /* Return result if we have a match */
                if (myChild instanceof Element myElement
                        && EL_DEPENDENCY.equals(myChild.getNodeName())) {
                    final ThemisXAnalysisMavenId myId = new ThemisXAnalysisMavenId(myElement, pParent);
                    if (!myId.isSkippable()) {
                        theDependencies.add(new ThemisXAnalysisMavenId(myElement, pParent));
                    }
                }
            }
        }
    }

    /**
     * Process extra directories.
     *
     * @throws OceanusException on error
     */
    private void processXtraDirs() throws OceanusException {
        /* Process any modules */
        final Node myXtraDirs = findNode(XPATH_XTRADIRS);
        if (myXtraDirs != null) {
            /* Loop through the children */
            for (Node myChild = myXtraDirs.getFirstChild();
                 myChild != null;
                 myChild = myChild.getNextSibling()) {
                /* Return result if we have a match */
                if (myChild instanceof Element
                        && EL_SOURCE.equals(myChild.getNodeName())) {
                    theXtraDirs.add(myChild.getTextContent());
                }
            }
        }
    }

    /**
     * Replace property.
     *
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
         * @param pElement the element containing the values
         */
        private ThemisXAnalysisMavenId(final Element pElement) {
            /* Access the values */
            theGroupId = getElementValue(pElement, EL_GROUPID);
            theArtifactId = getElementValue(pElement, EL_ARTIFACTID);
            theVersion = getElementValue(pElement, EL_VERSION);
            theScope = getElementValue(pElement, EL_SCOPE);
            theClassifier = getElementValue(pElement, EL_CLASSIFIER);
            isOptional = getElementValue(pElement, EL_OPTIONAL);
        }

        /**
         * Constructor.
         *
         * @param pElement the element containing the values
         * @param pParent  the parentId
         */
        private ThemisXAnalysisMavenId(final Element pElement,
                                       final ThemisXAnalysisMavenId pParent) {
            /* Process as much as we can */
            this(pElement);

            /* Handle missing groupId/version */
            if (PARENT_GROUP.equals(theGroupId)
                    || (!idFound && theGroupId == null)) {
                theGroupId = pParent.getGroupId();
            }
            if (PARENT_VERSION.equals(theVersion)
                    || (!idFound && theVersion == null)) {
                theVersion = pParent.getVersion();
            }

            /* If we have a ranged version set to null */
            if (theVersion != null
                    && theVersion.startsWith(String.valueOf(ThemisXAnalysisChar.ARRAY_OPEN))) {
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
                    || theVersion == null
                    || isOptional != null;
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
            final String myName = theGroupId + ThemisXAnalysisChar.COLON + theArtifactId + ThemisXAnalysisChar.COLON + theVersion;
            return theClassifier == null ? myName : myName + ThemisXAnalysisChar.COLON + theClassifier;
        }

        /**
         * Obtain the mavenBase.
         *
         * @return the mavenBase path
         */
        private File getMavenBasePath() {
            /* Determine the repository base */
            File myBase = new File(System.getProperty("user.home"));
            myBase = new File(myBase, ".m2");
            myBase = new File(myBase, "repository");
            myBase = new File(myBase, theGroupId.replace(ThemisXAnalysisChar.PERIOD, ThemisXAnalysisChar.COMMENT));
            myBase = new File(myBase, theArtifactId);
            myBase = new File(myBase, theVersion);
            return myBase;
        }

        /**
         * Obtain the mavenJar.
         *
         * @return the mavenJar path
         */
        public File getMavenJarPath() {
            /* Determine the repository base */
            File myBase = getMavenBasePath();
            String myName = theArtifactId + ThemisXAnalysisChar.HYPHEN + theVersion;
            if (theClassifier != null) {
                myName += ThemisXAnalysisChar.HYPHEN + theClassifier;
            }
            myBase = new File(myBase, myName + ".jar");
            return myBase;
        }

        /**
         * Obtain the mavenJar.
         *
         * @return the mavenJar path
         */
        public File getMavenPomPath() {
            /* Determine the repository base */
            File myBase = getMavenBasePath();
            myBase = new File(myBase, theArtifactId + ThemisXAnalysisChar.HYPHEN + theVersion + ".pom");
            return myBase;
        }
    }
}
