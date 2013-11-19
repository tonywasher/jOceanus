/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Provides functionality to hide and restore sections of an HTML document. This is useful for displaying HTML documents in a jEditorPane, allowing a click to
 * open/close sections of the document.
 */
public class ReportManager {
    /**
     * The id attribute.
     */
    private static final String ATTR_ID = HTMLBuilder.ATTR_ID;

    /**
     * The Transformer.
     */
    private final Transformer theXformer;

    /**
     * Report formatter.
     */
    private final HTMLBuilder theBuilder;

    /**
     * The Current document.
     */
    private Document theDocument = null;

    /**
     * The Current report.
     */
    private BasicReport theReport = null;

    /**
     * The Current text.
     */
    private String theText = null;

    /**
     * The hidden element map.
     */
    private final Map<String, HiddenElement> theHiddenMap;

    /**
     * Obtain the builder.
     * @return the HTML builder
     */
    public HTMLBuilder getBuilder() {
        return theBuilder;
    }

    /**
     * Constructor.
     * @throws JDataException on error
     */
    public ReportManager() throws JDataException {
        /* Create the builder */
        theBuilder = new HTMLBuilder();

        /* Allocate the hashMaps */
        theHiddenMap = new HashMap<String, HiddenElement>();

        /* Protect against exceptions */
        try {
            /* Create the transformer */
            TransformerFactory myXformFactory = TransformerFactory.newInstance();
            theXformer = myXformFactory.newTransformer();
            theXformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        } catch (Exception e) {
            throw new JDataException(ExceptionClass.XML, "Failed to create", e);
        }
    }

    /**
     * Set Report.
     * @param pReport the document
     */
    public void setReport(final BasicReport pReport) {
        /* Store the report */
        theReport = pReport;

        /* Clear the maps */
        theHiddenMap.clear();
    }

    /**
     * Set Document.
     * @param pDocument the document
     */
    public void setDocument(final Document pDocument) {
        /* Store the document */
        theDocument = pDocument;
    }

    /**
     * Process link reference.
     * @param pId the id of the reference.
     * @param pWindow the window to update
     */
    public void processReference(final String pId,
                                 final JEditorPane pWindow) {
        String myText = null;

        try {
            /* If this is a table reference */
            if (pId.startsWith(HTMLBuilder.REF_TAB)) {

                /* If the section is hidden */
                if (theHiddenMap.get(pId) != null) {
                    /* Restore the section and access text */
                    myText = restoreSection(pId);

                    /* else try to hide the section */
                } else {
                    myText = hideSection(pId);
                }
                /* else if this is a delayed table reference */
            } else if (pId.startsWith(HTMLBuilder.REF_DELAY)) {
                /* Process the delayed reference and return if no change */
                if (!theReport.processDelayedReference(theBuilder, pId)) {
                    return;
                }

                /* Format the text */
                myText = formatXML();

                /* else if this is a filter reference */
            } else if (pId.startsWith(HTMLBuilder.REF_FILTER)) {
                /* Process the filter reference */
                theReport.processFilterReference(pId);
                return;
            }
        } catch (JDataException e) {
            myText = null;
        }

        /* If we have new text */
        if (myText != null) {
            /* Set it into the window and adjust the scroll */
            pWindow.setText(myText);
            String myId = HTMLBuilder.REF_ID
                          + pId.substring(HTMLBuilder.REF_TAB.length());
            pWindow.scrollToReference(myId);
        }
    }

    /**
     * Hide section.
     * @param pId the id of the section to hide.
     * @return the modified text
     * @throws JDataException on error
     */
    private String hideSection(final String pId) throws JDataException {
        /* Ignore if we have no document or transformer */
        if ((theDocument == null)
            || (theXformer == null)) {
            /* Return no change */
            return null;
        }

        /* Ignore if section is already hidden */
        if (theHiddenMap.get(pId) != null) {
            return null;
        }

        /* Locate the section */
        Element mySection = getElementById(pId);
        if (mySection != null) {
            /* Hide the element */
            HiddenElement myElement = new HiddenElement(mySection);
            myElement.hide();

            /* Put the old element into the map */
            theHiddenMap.put(pId, myElement);

            /* Return the new text */
            return formatXML();
        }

        /* Return no change */
        return null;
    }

    /**
     * Obtain element with the given id attribute.
     * @param pId the id
     * @return the relevant element (or null)
     */
    private Element getElementById(final String pId) {
        /* Access root element */
        Element myElement = theDocument.getDocumentElement();

        /* Loop through the nodes */
        for (Node myNode = myElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            Element myChild = (Element) myNode;
            Element myResult = checkElementForId(myChild, pId);
            if (myResult != null) {
                return myResult;
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Obtain element with the given id attribute.
     * @param pElement the element to search
     * @param pId the id
     * @return the element or null if not found
     */
    private Element checkElementForId(final Element pElement,
                                      final String pId) {
        /* Check the element for the id */
        if (pElement.getAttribute(ATTR_ID).equals(pId)) {
            return pElement;
        }

        /* Loop through the child nodes */
        for (Node myNode = pElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            Element myChild = (Element) myNode;

            /* Pass check on to child */
            Element myResult = checkElementForId(myChild, pId);
            if (myResult != null) {
                return myResult;
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Restore section.
     * @param pId the id of the section to restore.
     * @return the modified text
     * @throws JDataException on error
     */
    public String restoreSection(final String pId) throws JDataException {
        /* Ignore if we have no document or transformer */
        if ((theDocument == null)
            || (theXformer == null)) {
            /* Return current text */
            return theText;
        }

        /* Obtain the hidden element */
        HiddenElement myHidden = theHiddenMap.get(pId);

        /* If we have hidden an element */
        if (myHidden != null) {
            /* Restore the element */
            myHidden.restore();
            theHiddenMap.remove(pId);

            /* Return the new text */
            return formatXML();
        }

        /* Just return the current text */
        return theText;
    }

    /**
     * Format XML.
     * @return the formatted XML
     * @throws JDataException on error
     */
    public String formatXML() throws JDataException {
        /* Protect against exceptions */
        try {
            /* Transform the new document */
            StringWriter myWriter = new StringWriter();
            theXformer.transform(new DOMSource(theDocument), new StreamResult(myWriter));
            theText = myWriter.getBuffer().toString().replaceAll("\n|\r", "");

            /* Return the new text */
            return theText;
        } catch (TransformerException e) {
            throw new JDataException(ExceptionClass.XML, "Failed to format", e);
        }
    }

    /**
     * Simple element class for hidden elements.
     */
    private static final class HiddenElement {
        /**
         * The element that is hidden.
         */
        private final Element theElement;

        /**
         * Its parent.
         */
        private final Node theParent;

        /**
         * Its previous sibling.
         */
        private final Node thePrevious;

        /**
         * Constructor.
         * @param pElement the element.
         */
        private HiddenElement(final Element pElement) {
            /* Store details */
            theElement = pElement;
            theParent = pElement.getParentNode();
            thePrevious = pElement.getPreviousSibling();
        }

        /**
         * Hide the element.
         */
        private void hide() {
            /* Remove the child from the parent */
            theParent.removeChild(theElement);
        }

        /**
         * Restore the element.
         */
        private void restore() {
            /* Determine next sibling */
            Node myNextSibling = thePrevious.getNextSibling();

            /* Restore the element */
            if (myNextSibling == null) {
                theParent.appendChild(theElement);
            } else {
                theParent.insertBefore(theElement, myNextSibling);
            }
        }
    }
}
