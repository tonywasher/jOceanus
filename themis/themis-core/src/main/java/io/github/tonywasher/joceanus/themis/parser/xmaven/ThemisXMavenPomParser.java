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

package io.github.tonywasher.joceanus.themis.parser.xmaven;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pom Parser.
 */
public class ThemisXMavenPomParser {
    /**
     * Document name.
     */
    private static final String DOC_NAME = "project";

    /**
     * Parent name.
     */
    private static final String PARENT_NAME = "parent";

    /**
     * GroupId name.
     */
    private static final String GROUP_NAME = "groupId";

    /**
     * Version name.
     */
    private static final String VERSION_NAME = "version";

    /**
     * Document path prefix.
     */
    private static final String DOC_PREFIX = "/" + DOC_NAME;

    /**
     * Properties XPath.
     */
    private static final String XPATH_PROPERTIES = DOC_PREFIX + "/properties";

    /**
     * Parent XPath.
     */
    private static final String XPATH_PARENT = DOC_PREFIX + ThemisChar.COMMENT + PARENT_NAME;

    /**
     * Modules XPath.
     */
    private static final String XPATH_MODULES = DOC_PREFIX + "/modules";

    /**
     * DependencyManagement XPath.
     */
    private static final String XPATH_DEPENDENCYMGMT = DOC_PREFIX + "/dependencyManagement/dependencies";

    /**
     * Dependencies XPath.
     */
    private static final String XPATH_DEPENDENCIES = DOC_PREFIX + "/dependencies";

    /**
     * Packaging XPath.
     */
    private static final String XPATH_PACKAGING = DOC_PREFIX + "/packaging";

    /**
     * XtraDirs XPath.
     */
    private static final String XPATH_XTRADIRS = DOC_PREFIX
            + "/build/plugins/plugin[artifactId='build-helper-maven-plugin']"
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
     * Parent groupId indication.
     */
    private static final String PARENT_GROUP = PARENT_NAME + ThemisChar.PERIOD + DOC_NAME + ThemisChar.PERIOD + GROUP_NAME;

    /**
     * Parent version indication.
     */
    private static final String PARENT_VERSION = PARENT_NAME + ThemisChar.PERIOD + DOC_NAME + ThemisChar.PERIOD + VERSION_NAME;

    /**
     * Project groupId indication.
     */
    private static final String PROJECT_GROUP = DOC_NAME + ThemisChar.PERIOD + GROUP_NAME;

    /**
     * Project version indication.
     */
    private static final String PROJECT_VERSION = DOC_NAME + ThemisChar.PERIOD + VERSION_NAME;

    /**
     * The parsed document.
     */
    private final Document theDoc;

    /**
     * The XPath.
     */
    private final XPath theXPath;

    /**
     * The PropertyCache.
     */
    private ThemisXMavenPropertyCache theProperties;

    /**
     * Constructor.
     *
     * @param pLocation   the pomFile location      the parent pom
     * @param pProperties the properties cache
     * @throws OceanusException on error
     */
    public ThemisXMavenPomParser(final File pLocation,
                                 final ThemisXMavenPropertyCache pProperties) throws OceanusException {
        /* Record the cache */
        theProperties = pProperties;

        /* Protect against exceptions */
        try (InputStream myInputStream = new FileInputStream(pLocation);
             BufferedInputStream myInBuffer = new BufferedInputStream(myInputStream)) {
            final DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            myFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            final DocumentBuilder myBuilder = myFactory.newDocumentBuilder();

            /* Create the XPath */
            theXPath = XPathFactory.newInstance().newXPath();

            /* Build the document from the input stream */
            theDoc = myBuilder.parse(myInBuffer);

            /* Access the document element */
            final Element myDoc = theDoc.getDocumentElement();

            /* Check that the document name is correct */
            if (!Objects.equals(myDoc.getNodeName(), DOC_NAME)) {
                throw new ThemisDataException("Invalid document type");
            }

            /* Handle exceptions */
        } catch (IOException
                 | ParserConfigurationException
                 | SAXException e) {
            throw new ThemisIOException("Exception accessing Pom file", e);
        }
    }

    /**
     * Read the properties into the cache.
     *
     * @throws OceanusException on error
     */
    void readProperties() throws OceanusException {
        /* Store any properties */
        final Node myProps = findNode(XPATH_PROPERTIES);
        if (myProps != null) {
            for (Node myNode = myProps.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
                if (myNode instanceof Element myElement) {
                    theProperties.setProperty(myElement.getNodeName(),
                            myElement.getTextContent());
                }
            }
        }
    }

    /**
     * Is this jar packaging?
     *
     * @return true/false
     * @throws OceanusException on error
     */
    boolean isJarPackaging() throws OceanusException {
        /* Obtain packaging definition if any */
        final Element myPackageEl = (Element) findNode(XPATH_PACKAGING);
        return myPackageEl == null || "jar".equals(myPackageEl.getTextContent());
    }

    /**
     * Obtain the parentId.
     *
     * @return the parentId (or null)
     * @throws OceanusException on error
     */
    ThemisXMavenId getParent() throws OceanusException {
        /* Obtain parent definition if any */
        final Element myParentEl = (Element) findNode(XPATH_PARENT);
        final ThemisXMavenId myParent = myParentEl == null
                ? null
                : new ThemisXMavenId(theProperties, myParentEl);

        /* Store parent properties if we have a parent */
        if (myParent != null) {
            storeParentProperties(myParent);
        }

        /* Return the parent */
        return myParent;
    }

    /**
     * Store parent properties.
     *
     * @param pParent the parent
     */
    private void storeParentProperties(final ThemisXMavenId pParent) {
        /* Store parent groupId and version */
        theProperties.setProperty(PARENT_GROUP, pParent.getGroupId());
        theProperties.setProperty(PARENT_VERSION, pParent.getVersion());
    }

    /**
     * Obtain the Id.
     *
     * @param pParent the parent id
     * @return the Id (or null)
     * @throws OceanusException on error
     */
    ThemisXMavenId getId(final ThemisXMavenId pParent) throws OceanusException {
        /* Obtain definition */
        final ThemisXMavenId myProject = new ThemisXMavenId(theProperties, theDoc.getDocumentElement(), pParent);

        /* Store self properties */
        storeSelfProperties(myProject);

        /* Return the id */
        return myProject;
    }

    /**
     * Store self properties.
     *
     * @param pProject the project
     */
    private void storeSelfProperties(final ThemisXMavenId pProject) {
        /* Determine project groupId */
        String myGroupId = pProject.getGroupId();
        myGroupId = myGroupId != null ? myGroupId : theProperties.getProperty(PARENT_GROUP);

        /* Determine project version */
        String myVersion = pProject.getVersion();
        myVersion = myVersion != null ? myVersion : theProperties.getProperty(PARENT_VERSION);

        /* Store project details */
        theProperties.setProperty(PROJECT_GROUP, myGroupId);
        theProperties.setProperty(PROJECT_VERSION, myVersion);
    }

    /**
     * Obtain modules.
     *
     * @return the list of modules
     * @throws OceanusException on error
     */
    List<String> getModules() throws OceanusException {
        /* Process any modules */
        final List<String> myList = new ArrayList<>();
        final Node myModules = findNode(XPATH_MODULES);
        if (myModules != null) {
            /* Loop through the children */
            for (Node myChild = myModules.getFirstChild();
                 myChild != null;
                 myChild = myChild.getNextSibling()) {
                /* Return result if we have a match */
                if (myChild instanceof Element
                        && EL_MODULE.equals(myChild.getNodeName())) {
                    myList.add(myChild.getTextContent());
                }
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * Get dependencyManagement.
     *
     * @return the list
     * @throws OceanusException on error
     */
    List<ThemisXMavenId> getDependencyManagement() throws OceanusException {
        return getDependencies(XPATH_DEPENDENCYMGMT);
    }

    /**
     * Get dependencies.
     *
     * @return the list
     * @throws OceanusException on error
     */
    List<ThemisXMavenId> getDependencies() throws OceanusException {
        return getDependencies(XPATH_DEPENDENCIES);
    }

    /**
     * Get dependencies.
     *
     * @param pPath XPath to dependencies
     * @return the list
     * @throws OceanusException on error
     */
    private List<ThemisXMavenId> getDependencies(final String pPath) throws OceanusException {
        /* Process any dependencies */
        final List<ThemisXMavenId> myList = new ArrayList<>();
        final Node myDependencies = findNode(pPath);
        if (myDependencies != null) {
            /* Loop through the children */
            for (Node myChild = myDependencies.getFirstChild();
                 myChild != null;
                 myChild = myChild.getNextSibling()) {
                /* Return result if we have a match */
                if (myChild instanceof Element myElement
                        && EL_DEPENDENCY.equals(myChild.getNodeName())) {
                    final ThemisXMavenId myId = new ThemisXMavenId(theProperties, myElement);
                    if (!myId.isSkippable()) {
                        myList.add(myId);
                    }
                }
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * Get extra directories.
     *
     * @return the list
     * @throws OceanusException on error
     */
    List<String> getXtraDirs() throws OceanusException {
        /* Process any modules */
        final List<String> myList = new ArrayList<>();
        final Node myXtraDirs = findNode(XPATH_XTRADIRS);
        if (myXtraDirs != null) {
            /* Loop through the children */
            for (Node myChild = myXtraDirs.getFirstChild();
                 myChild != null;
                 myChild = myChild.getNextSibling()) {
                /* Return result if we have a match */
                if (myChild instanceof Element
                        && EL_SOURCE.equals(myChild.getNodeName())) {
                    myList.add(myChild.getTextContent());
                }
            }
        }

        /* Return the list */
        return myList;
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
}
