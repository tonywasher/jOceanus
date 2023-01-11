/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.service.sheet.odf;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.joceanus.jprometheus.service.sheet.odf.PrometheusOdfNameSpace.PrometheusOdfItem;

/**
 * Parser for Document.
 */
class PrometheusOdfParser {
    /**
     * The document.
     */
    private final Document theDocument;

    /**
     * Constructor.
     * @param pDocument the document
     */
    PrometheusOdfParser(final Document pDocument) {
        /* Store parameters */
        theDocument = pDocument;
    }

    /**
     * Obtain the document.
     * @return the document
     */
    Document getDocument() {
        return theDocument;
    }

    /**
     * Obtain the first matching named child.
     * @param pParent the parent
     * @param pItem the item
     * @return the element (or null)
     */
    Element getFirstNamedChild(final Element pParent,
                               final PrometheusOdfItem pItem) {
        /* Handle null parent */
        if (pParent == null) {
            return null;
        }

        /* Access the details */
        final String myName = pItem.getQualifiedName();

        /* Loop through all children */
        Node myNode = pParent.getFirstChild();
        while (myNode != null) {
            /* Break loop if this is a matching child */
            if (myName.equals(myNode.getNodeName())) {
                break;
            }

            /* Next child */
            myNode = myNode.getNextSibling();
        }

        /* Return the element */
        return (Element) myNode;
    }

    /**
     * Obtain all matching named children.
     * @param pParent the parent
     * @param pItem the item
     * @return the list of children
     */
    List<Element> getAllNamedChildren(final Element pParent,
                                      final PrometheusOdfItem pItem) {
        /* Create list and handle null parent */
        final List<Element> myList = new ArrayList<>();
        if (pParent == null) {
            return myList;
        }

        /* Access the details */
        final String myName = pItem.getQualifiedName();

        /* Loop through all children */
        Node myNode = pParent.getFirstChild();
        while (myNode != null) {
            /* Add to list if this is a matching child */
            if (myName.equals(myNode.getNodeName())) {
                myList.add((Element) myNode);
            }

            /* Next child */
            myNode = myNode.getNextSibling();
        }

        /* Return the list */
        return myList;
    }

    /**
     * Obtain the attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @return the attribute value (or null)
     */
    String getAttribute(final Element pElement,
                        final PrometheusOdfItem pItem) {
        /* Handle null element */
        if (pElement == null) {
            return null;
        }

        /* Access the details */
        final String myName = pItem.getQualifiedName();

        /* Return the element */
        final String myAttr = pElement.getAttribute(myName);
        return myAttr.length() == 0
               ? null
               : myAttr;
    }

    /**
     * Obtain the long attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @return the attribute value (or null)
     */
    Long getLongAttribute(final Element pElement,
                          final PrometheusOdfItem pItem) {
        /* Handle null element */
        final String myValue = getAttribute(pElement, pItem);
        return myValue == null
               ? null
               : Long.parseLong(myValue);
    }

    /**
     * Obtain the integer attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @return the attribute value (or null)
     */
    Integer getIntegerAttribute(final Element pElement,
                                final PrometheusOdfItem pItem) {
        /* Handle null element */
        final String myValue = getAttribute(pElement, pItem);
        return myValue == null
               ? null
               : Integer.parseInt(myValue);
    }

    /**
     * Obtain the boolean attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @return the attribute value (or null)
     */
    Boolean getBooleanAttribute(final Element pElement,
                                final PrometheusOdfItem pItem) {
        /* Handle null element */
        final String myValue = getAttribute(pElement, pItem);
        return myValue == null
               ? null
               : Boolean.parseBoolean(myValue);
    }

    /**
     * Obtain the double attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @return the attribute value (or null)
     */
    Double getDoubleAttribute(final Element pElement,
                              final PrometheusOdfItem pItem) {
        /* Handle null element */
        final String myValue = getAttribute(pElement, pItem);
        return myValue == null
               ? null
               : Double.parseDouble(myValue);
    }

    /**
     * Set the attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @param pValue the value
     */
    void setAttribute(final Element pElement,
                      final PrometheusOdfItem pItem,
                      final String pValue) {
        /* Access the details */
        final String mySpace = pItem.getNameSpace().getNameSpace();
        final String myName = pItem.getQualifiedName();

        /* Set the attribute */
        pElement.setAttributeNS(mySpace, myName, pValue);
    }

    /**
     * Set the attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @param pValue the value
     */
    void setAttribute(final Element pElement,
                      final PrometheusOdfItem pItem,
                      final PrometheusOdfValue pValue) {
        /* Set the attribute */
        setAttribute(pElement, pItem, pValue.getValue());
    }

    /**
     * Set the attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @param pValue the value
     */
    void setAttribute(final Element pElement,
                      final PrometheusOdfItem pItem,
                      final int pValue) {
        /* Set the attribute */
        setAttribute(pElement, pItem, Integer.toString(pValue));
    }

    /**
     * Set the attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @param pValue the value
     */
    void setAttribute(final Element pElement,
                      final PrometheusOdfItem pItem,
                      final double pValue) {
        /* Set the attribute */
        setAttribute(pElement, pItem, Double.toString(pValue));
    }

    /**
     * Set the attribute.
     * @param pElement the element
     * @param pItem the attribute
     * @param pValue the value
     */
    void setAttribute(final Element pElement,
                      final PrometheusOdfItem pItem,
                      final boolean pValue) {
        /* Set the attribute */
        setAttribute(pElement, pItem, Boolean.toString(pValue));
    }

    /**
     * Remove the attribute.
     * @param pElement the element
     * @param pItem the attribute
     */
    void removeAttribute(final Element pElement,
                         final PrometheusOdfItem pItem) {
        /* Access the details */
        final String mySpace = pItem.getNameSpace().getNameSpace();
        final String myName = pItem.getName();

        /* Remove the attribute */
        pElement.removeAttributeNS(mySpace, myName);
    }

    /**
     * Create a new element.
     * @param pItem the item
     * @return the new element
     */
    Element newElement(final PrometheusOdfItem pItem) {
        /* Access the details */
        final String mySpace = pItem.getNameSpace().getNameSpace();
        final String myName = pItem.getQualifiedName();

        /* Create the element */
        return theDocument.createElementNS(mySpace, myName);
    }

    /**
     * Add element as next sibling of reference node.
     * @param pNew the node to add
     * @param pRef the node to add after
     */
    static void addAsNextSibling(final Node pNew,
                                 final Node pRef) {
        /* Obtain parent of reference node */
        final Node myParent = pRef.getParentNode();

        /* Obtain the next element */
        final Node myNextElement = pRef.getNextSibling();
        if (myNextElement != null) {
            myParent.insertBefore(pNew, myNextElement);
        } else {
            myParent.appendChild(pNew);
        }
    }

    /**
     * Add element as prior sibling of reference node.
     * @param pNew the node to add
     * @param pRef the node to add before
     */
    static void addAsPriorSibling(final Node pNew,
                                  final Node pRef) {
        /* Obtain parent of reference node */
        final Node myParent = pRef.getParentNode();

        /* Obtain the next element */
        myParent.insertBefore(pNew, pRef);
    }

    /**
     * Is the node an element of the correct type?.
     * @param pNode the node
     * @param pItems the item types
     * @return true/false
     */
    boolean isElementOfType(final Node pNode,
                            final PrometheusOdfItem... pItems) {
        /* Loop through the items */
        for (PrometheusOdfItem myItem : pItems) {
            /* Access the details */
            final String myName = myItem.getQualifiedName();

            /* Check the element */
            if (myName.equals(pNode.getNodeName())) {
               return true;
            }
        }

        /* Not valid element type */
        return false;
    }
}
