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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

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
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JSvnManager.project.ProjectId.ProjectList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Represents the definition of a project.
 * @author Tony Washer
 */
public class ProjectDefinition implements JDataContents {
    /**
     * POM name.
     */
    public static final String POM_NAME = "pom.xml";

    /**
     * project document name.
     */
    private static final String DOCNAME_PROJECT = "project";

    /**
     * Dependencies node name.
     */
    private static final String NODENAME_DEPENDENCIES = "dependencies";

    /**
     * Group node name.
     */
    private static final String NODENAME_DEPENDENCY = "dependency";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(ProjectDefinition.class.getSimpleName());

    /**
     * Id field id.
     */
    private static final JDataField FIELD_ID = FIELD_DEFS.declareEqualityField("Id");

    /**
     * Dependencies field id.
     */
    private static final JDataField FIELD_DEPS = FIELD_DEFS.declareEqualityField("Dependencies");

    @Override
    public String formatObject() {
        return theDefinition.formatObject();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_ID.equals(pField)) {
            return theDefinition;
        }
        if (FIELD_DEPS.equals(pField)) {
            return theDependencies;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * XML POM representation.
     */
    private final Document theDocument;

    /**
     * Main module identity.
     */
    private final ProjectId theDefinition;

    /**
     * Dependency identities.
     */
    private final ProjectList theDependencies;

    /**
     * Get Document.
     * @return the document
     */
    private Document getDocument() {
        return theDocument;
    }

    /**
     * Get Project Identity.
     * @return the project identity
     */
    public ProjectId getDefinition() {
        return theDefinition;
    }

    /**
     * Get Dependencies.
     * @return the project dependencies
     */
    public ProjectList getDependencies() {
        return theDependencies;
    }

    /**
     * Parse disk POM file.
     * @param pFile file to load
     * @return project definition.
     * @throws JDataException on error
     */
    public static ProjectDefinition parseProjectFile(final File pFile) throws JDataException {
        FileInputStream myInFile;
        BufferedInputStream myInBuffer = null;

        /* Protect against exceptions */
        try {
            /* Read the file */
            myInFile = new FileInputStream(pFile);
            myInBuffer = new BufferedInputStream(myInFile);

            /* Parse the project definition */
            ProjectDefinition myProject = new ProjectDefinition(myInBuffer);
            return myProject;

            /* Catch exceptions */
        } catch (JDataException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to load Project file for "
                    + pFile.getAbsolutePath(), e);

        } catch (IOException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to load Project file for "
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
     * Parse project definition file.
     * @param pInput the project definition file as input stream
     * @throws JDataException on error
     */
    public ProjectDefinition(final InputStream pInput) throws JDataException {
        DocumentBuilderFactory myFactory;
        DocumentBuilder myBuilder;
        Element myElement;

        /* Protect against exceptions */
        try {
            /* Create the document builder */
            myFactory = DocumentBuilderFactory.newInstance();
            myBuilder = myFactory.newDocumentBuilder();

            /* Access the XML document element */
            theDocument = myBuilder.parse(pInput);
            myElement = theDocument.getDocumentElement();

            /* Reject if this is not a Pom file */
            if (!myElement.getNodeName().equals(DOCNAME_PROJECT)) {
                throw new JDataException(ExceptionClass.DATA, "Invalid document name: "
                        + myElement.getNodeName());
            }

            /* Obtain the major definition */
            theDefinition = new ProjectId(myElement);

            /* Create the dependency list */
            theDependencies = new ProjectList();

            /* Parse the dependencies */
            parseDependencies(myElement);

            /* Catch exceptions */
        } catch (IOException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to parse Project file", e);
        } catch (ParserConfigurationException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to parse Project file", e);
        } catch (SAXException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to parse Project file", e);
        }
    }

    /**
     * Parse dependencies.
     * @param pElement the top level element
     * @throws JDataException on error
     */
    private void parseDependencies(final Node pElement) throws JDataException {
        /* Loop through the nodes */
        for (Node myNode = pElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Ignore non-elements */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access dependencies */
            if (myNode.getNodeName().equals(NODENAME_DEPENDENCIES)) {
                /* Loop through the dependency nodes */
                for (Node myDepNode = myNode.getFirstChild(); myDepNode != null; myDepNode = myDepNode
                        .getNextSibling()) {
                    /* Ignore non-elements */
                    if (myDepNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }

                    /* Access dependency */
                    if (myDepNode.getNodeName().equals(NODENAME_DEPENDENCY)) {
                        /* Add dependency to list */
                        ProjectId myDef = new ProjectId(myDepNode);
                        theDependencies.add(myDef);
                    }
                }
            }
        }
    }

    /**
     * Constructor for a duplicate project definition.
     * @param pDefinition the definition to copy
     * @throws JDataException on error
     */
    public ProjectDefinition(final ProjectDefinition pDefinition) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Create a document builder */
            DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder myBuilder = myFactory.newDocumentBuilder();

            /* Copy the document */
            theDocument = myBuilder.newDocument();
            Document myOldDoc = pDefinition.getDocument();
            Node myOldRoot = myOldDoc.getDocumentElement();
            Node myNewRoot = theDocument.importNode(myOldRoot, true);
            theDocument.appendChild(myNewRoot);

            /* Copy project Id and dependencies */
            theDefinition = new ProjectId(myNewRoot);
            theDependencies = new ProjectList();
            parseDependencies(myNewRoot);

        } catch (ParserConfigurationException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to parse Project file", e);
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
     * Write to file stream.
     * @param pFile the file to write to
     * @throws JDataException on error
     */
    public void writeToFile(final File pFile) throws JDataException {
        /* Protect against exceptions */
        try {
            /* delete the file if it exists */
            if (pFile.exists()) {
                pFile.delete();
            }

            /* Prepare to write the document */
            TransformerFactory myFactory = TransformerFactory.newInstance();
            Transformer myXformer = myFactory.newTransformer();
            DOMSource mySource = new DOMSource(theDocument);
            StreamResult myResult = new StreamResult(pFile);

            /* Output the XML */
            myXformer.transform(mySource, myResult);

            /* Catch exceptions */
        } catch (TransformerException e) {
            /* Throw Exception */
            throw new JDataException(ExceptionClass.DATA, "Failed to write Project file to "
                    + pFile.getName(), e);
        }
    }

    /**
     * Set Snapshot Version.
     * @param pVersion the version
     */
    public void setSnapshotVersion(final String pVersion) {
        theDefinition.setSnapshotVersion(pVersion);
    }

    /**
     * Set New Version.
     * @param pVersion the version
     */
    public void setVersion(final String pVersion) {
        theDefinition.setVersion(pVersion);
    }

    /**
     * Set New Version for dependencies.
     * @param pProjectId the project Id
     */
    public void setNewVersion(final ProjectId pProjectId) {
        /* Loop through dependencies */
        Iterator<ProjectId> myIterator = theDependencies.iterator();
        while (myIterator.hasNext()) {
            /* Access dependency and set version */
            ProjectId myRef = myIterator.next();
            myRef.setNewVersion(pProjectId);
        }
    }
}
