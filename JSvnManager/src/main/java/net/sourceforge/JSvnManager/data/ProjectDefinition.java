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
package net.sourceforge.JSvnManager.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * Represents the definition of a project.
 * @author Tony Washer
 */
public class ProjectDefinition {
    /**
     * POM name.
     */
    private static final String POM_NAME = "pom.xml";

    /**
     * File for POM.
     */
    private File theFile = null;

    /**
     * XML POM representation.
     */
    private Document theDocument = null;

    /**
     * Main module identity.
     */
    private ProjectId theDefinition = null;

    /**
     * Dependency identities.
     */
    private List<ProjectId> theDependencies = null;

    /**
     * Get Project Identity.
     * @return the project identity
     */
    public ProjectId getDefinition() {
        return theDefinition;
    }

    /**
     * Parse project definition file.
     * @param pFile the project definition file
     * @throws JDataException on error
     */
    public ProjectDefinition(final File pFile) throws JDataException {
        FileInputStream myInFile;
        DocumentBuilderFactory myFactory;
        DocumentBuilder myBuilder;
        Element myElement;

        /* Store the file name */
        theFile = pFile;
        BufferedInputStream myInBuffer = null;

        /* Protect against exceptions */
        try {
            /* Read the file */
            myInFile = new FileInputStream(pFile);
            myInBuffer = new BufferedInputStream(myInFile);

            /* Create the document builder */
            myFactory = DocumentBuilderFactory.newInstance();
            myBuilder = myFactory.newDocumentBuilder();

            /* Access the XML document element */
            theDocument = myBuilder.parse(myInBuffer);
            myElement = theDocument.getDocumentElement();

            /* Reject if this is not a Pom file */
            if (!myElement.getNodeName().equals("project")) {
                throw new JDataException(ExceptionClass.DATA, "Invalid document name: "
                        + myElement.getNodeName());
            }

            /* Obtain the major definition */
            theDefinition = new ProjectId(myElement);

            /* Create the dependency list */
            theDependencies = new ArrayList<ProjectId>();

            /* Loop through the nodes */
            for (Node myNode = myElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
                /* Ignore non-elements */
                if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                /* Access dependencies */
                if (myNode.getNodeName().equals("dependencies")) {
                    /* Loop through the dependency nodes */
                    for (Node myDepNode = myNode.getFirstChild(); myDepNode != null; myDepNode = myDepNode
                            .getNextSibling()) {
                        /* Ignore non-elements */
                        if (myDepNode.getNodeType() != Node.ELEMENT_NODE) {
                            continue;
                        }

                        /* Access dependency */
                        if (myDepNode.getNodeName().equals("dependency")) {
                            /* Add dependency to list */
                            ProjectId myDef = new ProjectId(myDepNode);
                            theDependencies.add(myDef);
                        }
                    }
                }
            }

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to load Project file for "
                    + pFile.getAbsolutePath(), e);
        } catch (ParserConfigurationException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to parse Project file for "
                    + pFile.getAbsolutePath(), e);
        } catch (SAXException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to parse Project file for "
                    + pFile.getAbsolutePath(), e);
        } finally {
            if (myInBuffer != null) {
                try {
                    myInBuffer.close();
                } catch (IOException i) {
                    myInBuffer = null;
                }
            }
        }

    }

    /**
     * Obtain project definition file for location.
     * @param pLocation the location of the project
     * @return the project definition file or null
     */
    public static File getProjectDefFile(final File pLocation) {
        /* Build the file */
        File myFile = new File(pLocation, POM_NAME);

        /* Return the file */
        return (myFile.exists()) ? myFile : null;
    }

    /**
     * Write to file.
     * @throws JDataException on error
     */
    public void writeToFile() throws JDataException {
        /* Protect against exceptions */
        try {
            /* delete the file if it exists */
            if (theFile.exists()) {
                theFile.delete();
            }

            /* Prepare to write the document */
            TransformerFactory myFactory = TransformerFactory.newInstance();
            Transformer myXformer = myFactory.newTransformer();
            DOMSource mySource = new DOMSource(theDocument);
            StreamResult myResult = new StreamResult(theFile);

            /* Output the XML */
            myXformer.transform(mySource, myResult);

            /* Catch exceptions */
        } catch (TransformerException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to write Project file to "
                    + theFile.getName(), e);
        }
    }

    /**
     * Set New Version.
     * @param pGroupId the groupId
     * @param pArtifactId the ArtifactId
     * @param pVersion the Version
     */
    public void setNewVersion(final String pGroupId,
                              final String pArtifactId,
                              final String pVersion) {
        /* Update own version */
        theDefinition.setNewVersion(pGroupId, pArtifactId, pVersion);

        /* Loop through dependencies */
        Iterator<ProjectId> myIterator = theDependencies.iterator();
        while (myIterator.hasNext()) {
            /* Access dependency and set version */
            ProjectId myRef = myIterator.next();
            myRef.setNewVersion(pGroupId, pArtifactId, pVersion);
        }
    }

    /**
     * Project Reference class.
     */
    public final class ProjectId {
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
         * Constructor.
         * @param pParent the parent node
         * @throws JDataException on error
         */
        private ProjectId(final Node pParent) throws JDataException {
            /* Loop through the nodes */
            for (Node myNode = pParent.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
                /* Ignore non-elements */
                if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                String myName = myNode.getNodeName();

                /* Access group id */
                if (myName.equals("groupId")) {
                    /* Access the text */
                    theGroupId = myNode.getTextContent();

                    /* Access artifact id */
                } else if (myName.equals("artifactId")) {
                    /* Access the text */
                    theArtifactId = myNode.getTextContent();

                    /* Access version */
                } else if (myName.equals("version")) {
                    /* Access the element and text */
                    theVersionNode = (Element) myNode;
                    theVersion = theVersionNode.getTextContent();
                }
            }

            /* Handle missing elements */
            if ((theGroupId == null) || (theArtifactId == null) || (theVersion == null)) {
                throw new JDataException(ExceptionClass.DATA, "Invalid version definition");
            }
        }

        /**
         * Set new version.
         * @param pGroupId the groupId
         * @param pArtifactId the ArtifactId
         * @param pVersion the Version
         */
        private void setNewVersion(final String pGroupId,
                                   final String pArtifactId,
                                   final String pVersion) {
            /* Ignore if wrong groupId/versionId */
            if (!theGroupId.equals(pGroupId)) {
                return;
            }
            if (!theArtifactId.equals(pArtifactId)) {
                return;
            }

            /* Delete child elements of the version node */
            clearChildren(theVersionNode);

            /* create a new text node and add to the node */
            Text myText = theDocument.createTextNode(pVersion);
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
    }
}
