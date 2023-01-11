/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.report;

import net.sourceforge.joceanus.jmetis.MetisIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIHTMLManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides functionality to hide and restore sections of an HTML document. This is useful for
 * displaying HTML documents in a jEditorPane, allowing a click to open/close sections of the
 * document.
 * @param <F> the filter type
 */
public class MetisReportManager<F>
        implements TethysEventProvider<MetisReportEvent> {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MetisReportManager.class);

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisReportEvent> theEventManager;

    /**
     * The Transformer.
     */
    private final Transformer theXformer;

    /**
     * Report formatter.
     */
    private final MetisReportHTMLBuilder theBuilder;

    /**
     * The Current document.
     */
    private Document theDocument;

    /**
     * The Reference Manager.
     */
    private MetisReportReferenceManager<F> theReferenceMgr;

    /**
     * The Current text.
     */
    private String theText;

    /**
     * The hidden element map.
     */
    private final Map<String, HiddenElement> theHiddenMap;

    /**
     * Constructor.
     * @param pBuilder the HTML builder
     * @throws OceanusException on error
     */
    public MetisReportManager(final MetisReportHTMLBuilder pBuilder) throws OceanusException {
        /* Record parameters */
        theBuilder = pBuilder;

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Allocate the hashMaps */
        theHiddenMap = new HashMap<>();

        /* Protect against exceptions */
        try {
            /* Create the transformer */
            final TransformerFactory myXformFactory = TransformerFactory.newInstance();
            myXformFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            myXformFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            theXformer = myXformFactory.newTransformer();
            theXformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        } catch (Exception e) {
            throw new MetisIOException("Failed to create", e);
        }
    }

    @Override
    public TethysEventRegistrar<MetisReportEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the builder.
     * @return the HTML builder
     */
    public MetisReportHTMLBuilder getBuilder() {
        return theBuilder;
    }

    /**
     * Set Report.
     * @param pReport the report
     */
    public void setReport(final MetisReportBase<?, F> pReport) {
        /* Clear the maps */
        theHiddenMap.clear();

        /* Store the reference manager */
        theReferenceMgr = pReport.getReferenceMgr();
        theReferenceMgr.clearMaps();
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
    protected void fireActionEvent(final F pFilter) {
        theEventManager.fireEvent(MetisReportEvent.FILTER, pFilter);
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
     * @throws OceanusException on error
     */
    protected String hideSection(final String pId) throws OceanusException {
        /* Ignore if we have no document or transformer */
        if (theDocument == null
            || theXformer == null) {
            /* Return no change */
            return null;
        }

        /* Ignore if section is already hidden */
        if (theHiddenMap.get(pId) != null) {
            return null;
        }

        /* Locate the section */
        final Element mySection = getElementById(pId);
        if (mySection != null) {
            /* Hide the element */
            final HiddenElement myElement = new HiddenElement(mySection);
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
        final Element myElement = theDocument.getDocumentElement();

        /* Loop through the nodes */
        for (Node myNode = myElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            final Element myChild = (Element) myNode;
            final Element myResult = checkElementForId(myChild, pId);
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
    private static Element checkElementForId(final Element pElement,
                                             final String pId) {
        /* Check the element for the id */
        if (pElement.getAttribute(MetisReportHTMLBuilder.ATTR_ID).equals(pId)) {
            return pElement;
        }

        /* Loop through the child nodes */
        for (Node myNode = pElement.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Skip nonElement nodes */
            if (myNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            /* Access node as element */
            final Element myChild = (Element) myNode;

            /* Pass check on to child */
            final Element myResult = checkElementForId(myChild, pId);
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
     * @throws OceanusException on error
     */
    String restoreSection(final String pId) throws OceanusException {
        /* Ignore if we have no document or transformer */
        if (theDocument == null
            || theXformer == null) {
            /* Return current text */
            return theText;
        }

        /* Obtain the hidden element */
        final HiddenElement myHidden = theHiddenMap.get(pId);

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
     * @throws OceanusException on error
     */
    public String formatXML() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Transform the new document */
            final StringWriter myWriter = new StringWriter();
            theXformer.transform(new DOMSource(theDocument), new StreamResult(myWriter));
            theText = myWriter.getBuffer().toString().replaceAll("[\n\r]", "");

            /* Return the new text */
            return theText;
        } catch (TransformerException e) {
            throw new MetisIOException("Failed to format", e);
        }
    }

    /**
     * Process link reference.
     * @param pId the id of the reference.
     * @param pHTMLPane the HTML pane
     */
    public void processReference(final String pId,
                                 final TethysUIHTMLManager pHTMLPane) {
        /* Process the reference */
        final String myText = processReference(pId);

        /* If we have new text */
        if (myText != null) {
            /* Set it into the window and adjust the scroll */
            pHTMLPane.setHTMLContent(myText, "");
            final String myId = MetisReportHTMLBuilder.REF_ID
                                + pId.substring(MetisReportHTMLBuilder.REF_TAB.length());
            pHTMLPane.scrollToReference(myId);
        }
    }

    /**
     * Process link reference.
     * @param pId the id of the reference.
     * @return the new text
     */
    private String processReference(final String pId) {
        /* Allocate the text */
        String myText = null;

        /* Protected against exceptions */
        try {
            /* If this is a table reference */
            if (pId.startsWith(MetisReportHTMLBuilder.REF_TAB)) {
                /* If the section is hidden */
                if (isHiddenId(pId)) {
                    /* Restore the section and access text */
                    myText = restoreSection(pId);

                    /* else try to hide the section */
                } else {
                    myText = hideSection(pId);
                }

                /* else if this is a delayed table reference */
            } else if (pId.startsWith(MetisReportHTMLBuilder.REF_DELAY)) {
                /* Process the delayed reference and format text */
                if (theReferenceMgr.processDelayedReference(getBuilder(), pId)) {
                    /* Format the text */
                    myText = formatXML();
                }

                /* else if this is a filter reference */
            } else if (pId.startsWith(MetisReportHTMLBuilder.REF_FILTER)) {
                /* Process the filter reference */
                final F myFilter = theReferenceMgr.processFilterReference(pId);

                /* Fire Action event if necessary */
                if (myFilter != null) {
                    fireActionEvent(myFilter);
                }
            }
        } catch (OceanusException e) {
            LOGGER.error("Failed to process reference", e);
            myText = null;
        }

        /* Return the new text */
        return myText;
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
            final Node myNextSibling = thePrevious.getNextSibling();

            /* Restore the element */
            if (myNextSibling == null) {
                theParent.appendChild(theElement);
            } else {
                theParent.insertBefore(theElement, myNextSibling);
            }
        }
    }
}
