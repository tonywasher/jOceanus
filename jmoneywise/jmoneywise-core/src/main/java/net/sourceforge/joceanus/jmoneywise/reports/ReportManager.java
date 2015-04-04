/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.JOceanusUtilitySet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Provides functionality to hide and restore sections of an HTML document. This is useful for displaying HTML documents in a jEditorPane, allowing a click to
 * open/close sections of the document.
 */
public abstract class ReportManager
        implements JOceanusEventProvider {
    /**
     * The id attribute.
     */
    private static final String ATTR_ID = HTMLBuilder.ATTR_ID;

    /**
     * The Event Manager.
     */
    private final JOceanusEventManager theEventManager;

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
     * The Current text.
     */
    private String theText = null;

    /**
     * The hidden element map.
     */
    private final Map<String, HiddenElement> theHiddenMap;

    /**
     * Constructor.
     * @param pView the view
     * @param pUtilitySet the utility set
     * @param pBuilder the HTML builder
     * @throws JOceanusException on error
     */
    public ReportManager(final View pView,
                         final JOceanusUtilitySet pUtilitySet,
                         final HTMLBuilder pBuilder) throws JOceanusException {
        /* Create the builder */
        theBuilder = pBuilder;

        /* Create event manager */
        theEventManager = new JOceanusEventManager();

        /* Allocate the hashMaps */
        theHiddenMap = new HashMap<String, HiddenElement>();

        /* Protect against exceptions */
        try {
            /* Create the transformer */
            TransformerFactory myXformFactory = TransformerFactory.newInstance();
            theXformer = myXformFactory.newTransformer();
            theXformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        } catch (Exception e) {
            throw new JMoneyWiseIOException("Failed to create", e);
        }
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the builder.
     * @return the HTML builder
     */
    public HTMLBuilder getBuilder() {
        return theBuilder;
    }

    /**
     * Set Report.
     * @param pReport the document
     */
    public void setReport(final BasicReport pReport) {
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
     * fire action event.
     * @param pFilter the filter
     */
    protected void fireActionEvent(final AnalysisFilter<?, ?> pFilter) {
        theEventManager.fireActionEvent(pFilter);
    }

    /**
     * check whether id is currently hidden.
     * @param pId the id to check
     * @return true/false
     */
    protected boolean isHiddenId(final String pId) {
        return theHiddenMap.get(pId) != null;
    }

    /**
     * Hide section.
     * @param pId the id of the section to hide.
     * @return the modified text
     * @throws JOceanusException on error
     */
    protected String hideSection(final String pId) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    public String restoreSection(final String pId) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    public String formatXML() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Transform the new document */
            StringWriter myWriter = new StringWriter();
            theXformer.transform(new DOMSource(theDocument), new StreamResult(myWriter));
            theText = myWriter.getBuffer().toString().replaceAll("\n|\r", "");

            /* Return the new text */
            return theText;
        } catch (TransformerException e) {
            throw new JMoneyWiseIOException("Failed to format", e);
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
