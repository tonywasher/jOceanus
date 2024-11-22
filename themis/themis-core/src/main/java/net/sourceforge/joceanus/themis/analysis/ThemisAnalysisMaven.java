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
package net.sourceforge.joceanus.themis.analysis;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.themis.ThemisDataException;
import net.sourceforge.joceanus.themis.ThemisIOException;

/**
 * Maven pom.xml parser.
 */
public class ThemisAnalysisMaven {
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
     * The Id.
     */
    private final ThemisAnalysisMavenId theId;

    /**
     * The modules.
     */
    private final List<String> theModules;

    /**
     * The dependencies.
     */
    private final List<ThemisAnalysisMavenId> theDependencies;

    /**
     * Constructor.
     * @param pInputStream the input stream to read
     * @throws OceanusException on error
     */
    public ThemisAnalysisMaven(final InputStream pInputStream) throws OceanusException {
        /* Create the module list */
        theModules = new ArrayList<>();
        theDependencies = new ArrayList<>();

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
    public List<String> getModules() {
        return theModules;
    }

    /**
     * Parse the project file.
     * @param pDocument the document
     * @return the MavenId
     * @throws OceanusException on error
     */
    public ThemisAnalysisMavenId parseProjectFile(final Document pDocument) throws OceanusException {
        /* Access the document element */
        final Element myDoc = pDocument.getDocumentElement();

        /* Check that the document name is correct */
        if (!Objects.equals(myDoc.getNodeName(), DOC_NAME)) {
            throw new ThemisDataException("Invalid document type");
        }

        /* Obtain parent definition if any */
        final Element myParentEl = getElement(myDoc, EL_PARENT);
        final ThemisAnalysisMavenId myParent = myParentEl == null
                ? null
                : new ThemisAnalysisMavenId(myParentEl);

        /* Obtain our mavenId */
        final ThemisAnalysisMavenId myId = new ThemisAnalysisMavenId(myDoc, myParent);

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
    static String getElementValue(final Element pElement,
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
                return myChild.getTextContent();
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
            if (myChild instanceof Element
                    && pValue.equals(myChild.getNodeName())) {
                return (Element) myChild;
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
                                     final ThemisAnalysisMavenId pParent) {
        /* Return if no element */
        if (pDependencies == null) {
            return;
        }

        /* Loop through the children */
        for (Node myChild = pDependencies.getFirstChild();
             myChild != null;
             myChild = myChild.getNextSibling()) {
            /* Return result if we have a match */
            if (myChild instanceof Element
                    && EL_DEPENDENCY.equals(myChild.getNodeName())) {
                theDependencies.add(new ThemisAnalysisMavenId((Element) myChild, pParent));
            }
        }
    }
}
