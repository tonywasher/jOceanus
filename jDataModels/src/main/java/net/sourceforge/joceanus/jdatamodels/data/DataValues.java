/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.joceanus.jdatamodels.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataInfoSet.InfoSetItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Arguments class for DataItem.
 * @author Tony Washer
 */
public class DataValues {
    /**
     * InfoSet Items tag.
     */
    private static final String TAG_INFOSET = "InfoSet";

    /**
     * The item type.
     */
    private final String theItemType;

    /**
     * Field Definitions.
     */
    private final Map<JDataField, Object> theFields;

    /**
     * InfoSet values.
     */
    private final List<InfoItem> theInfoItems;

    /**
     * Obtain Item Type.
     * @return the Item Type
     */
    public String getItemType() {
        return theItemType;
    }

    /**
     * Obtain Field iterator.
     * @return the Field iterator
     */
    public Iterator<Map.Entry<JDataField, Object>> fieldIterator() {
        return theFields.entrySet().iterator();
    }

    /**
     * Does this item has InfoItems?
     * @return true/false
     */
    public boolean hasInfoItems() {
        return theInfoItems != null;
    }

    /**
     * Obtain InfoItems iterator.
     * @return the InfoItems iterator
     */
    public Iterator<InfoItem> infoIterator() {
        return theInfoItems.iterator();
    }

    /**
     * Constructor.
     * @param pItem the Item to obtain values from
     */
    private DataValues(final DataItem pItem) {
        /* Store Item type */
        theItemType = pItem.getDataFields().getName();

        /* Create the map and list */
        theFields = new LinkedHashMap<JDataField, Object>();

        /* Store the id */
        theFields.put(DataItem.FIELD_ID, pItem.getId());

        /* Access values */
        ValueSet myValues = pItem.getValueSet();

        /* Iterate through the IDs */
        Iterator<JDataField> myIterator = pItem.getDataFields().fieldIterator();
        while (myIterator.hasNext()) {
            JDataField myField = myIterator.next();

            /* If the field is an equality valueSet item */
            if (myField.isValueSetField()
                && myField.isEqualityField()) {
                /* Store the value */
                Object myValue = myValues.getValue(myField);
                theFields.put(myField, (myValue == null)
                        ? null
                        : myValue.toString());
            }
        }

        /* If the item is an infoSet item */
        if (pItem instanceof InfoSetItem) {
            /* Allocate infoItems list */
            theInfoItems = new ArrayList<InfoItem>();

            /* Access InfoSet */
            DataInfoSet<?, ?, ?, ?> myInfoSet = ((InfoSetItem) pItem).getInfoSet();

            /* Iterator over the values */
            Iterator<?> myInfoIterator = myInfoSet.iterator();
            while (myInfoIterator.hasNext()) {
                Object myCurr = myInfoIterator.next();

                /* If this is a DataInfo item */
                if (myCurr instanceof DataInfo) {
                    /* Access as DataArguments */
                    DataInfo<?, ?, ?, ?> myItem = (DataInfo<?, ?, ?, ?>) myCurr;

                    /* Add item to the list */
                    InfoItem myInfo = new InfoItem(myItem);
                    theInfoItems.add(myInfo);
                }
            }

            /* Else not that we have no infoItems */
        } else {
            theInfoItems = null;
        }
    }

    /**
     * Constructor.
     * @param pElement the Item to obtain values from
     * @param pFields the field definitions
     */
    public DataValues(final Element pElement,
                      final JDataFields pFields) {
        /* Store Item type */
        theItemType = pElement.getNodeName();

        /* Create the map */
        theFields = new LinkedHashMap<JDataField, Object>();

        /* Declare the id */
        theFields.put(DataItem.FIELD_ID, getId(pElement));

        /* Loop through the children */
        Iterator<JDataField> myIterator = pFields.fieldIterator();
        while (myIterator.hasNext()) {
            JDataField myField = myIterator.next();

            /* If the field is an equality valueSet item */
            if (myField.isValueSetField()
                && myField.isEqualityField()) {
                /* Access element */
                Element myChild = getChild(pElement, myField.getName());
                if (myChild != null) {
                    /* Put value (as id if possible) */
                    Integer myId = getId(myChild);
                    theFields.put(myField, (myId == null)
                            ? myChild.getTextContent()
                            : myId);
                } else {
                    theFields.put(myField, null);
                }
            }
        }

        /* Look for an InfoSet list */
        Element myInfoSet = getChild(pElement, TAG_INFOSET);
        if (myInfoSet != null) {
            /* Allocate infoItems list */
            theInfoItems = new ArrayList<InfoItem>();

            /* Loop through the child values */
            for (Node myCurr = myInfoSet.getFirstChild(); myCurr != null; myCurr = myCurr.getNextSibling()) {
                /* If the child is an element */
                if (myCurr instanceof Element) {
                    /* Access as element */
                    Element myChild = (Element) myCurr;

                    /* Add item to the list */
                    InfoItem myInfo = new InfoItem(myChild);
                    theInfoItems.add(myInfo);
                }
            }

            /* Else not that we have no infoItems */
        } else {
            theInfoItems = null;
        }
    }

    /**
     * Constructor.
     * @param pName the Item type
     */
    public DataValues(final String pName) {
        /* Store Item type */
        this(pName, false);
    }

    /**
     * Constructor.
     * @param pName the Item type
     * @param hasInfoSet does this item have an infoSet?
     */
    public DataValues(final String pName,
                      final boolean hasInfoSet) {
        /* Store Item type */
        theItemType = pName;

        /* Create the map */
        theFields = new LinkedHashMap<JDataField, Object>();

        /* Allocate infoItems list */
        theInfoItems = (hasInfoSet)
                ? new ArrayList<InfoItem>()
                : null;
    }

    /**
     * Add argument.
     * @param pField the Field definition
     * @param pValue the field value
     */
    public void addArg(final JDataField pField,
                       final Object pValue) {
        /* Add the field */
        theFields.put(pField, pValue);
    }

    /**
     * Obtain id from element.
     * @param pElement the element.
     * @return the id
     */
    protected static Integer getId(final Element pElement) {
        /* Access the id */
        String myId = pElement.getAttribute(DataItem.FIELD_ID.getName());
        return (myId.length() > 0)
                ? Integer.parseInt(myId)
                : null;
    }

    /**
     * Obtain child element with given name.
     * @param pParent the parent element
     * @param pName the element name
     * @return the element
     */
    private static Element getChild(final Element pParent,
                                    final String pName) {
        /* Obtain the node list */
        NodeList myList = pParent.getElementsByTagName(pName);
        if (myList.getLength() == 0) {
            return null;
        }

        /* Access first element */
        Node myNode = myList.item(0);
        return (myNode instanceof Element)
                ? (Element) myNode
                : null;
    }

    /**
     * Create XML element for item.
     * @param pDocument the document to hold the item.
     * @return the new element
     */
    private Element createXML(final Document pDocument) {
        /* Create an element for the item */
        Element myElement = pDocument.createElement(theItemType);

        /* Loop through the values */
        for (Map.Entry<JDataField, Object> myEntry : theFields.entrySet()) {
            /* Access parts */
            JDataField myField = myEntry.getKey();
            Object myValue = myEntry.getValue();

            /* If this is the Id */
            if (DataItem.FIELD_ID.equals(myField)) {
                /* Add as an Attribute */
                myElement.setAttribute(myField.getName(), myValue.toString());
                continue;
            }

            /* If this is the Control Id */
            if (EncryptedItem.FIELD_CONTROL.equals(myField)) {
                /* Ignore it */
                continue;
            }

            /* If the value is non-Null */
            if (myValue != null) {
                /* Create the child element */
                Element myChild = pDocument.createElement(myField.getName());
                myElement.appendChild(myChild);

                /* If the value is an instance of a DataItem */
                if (myValue instanceof DataItem) {
                    /* Set id attribute */
                    DataItem myLink = (DataItem) myValue;
                    myElement.setAttribute(DataItem.FIELD_ID.getName(), myLink.getId().toString());
                }

                /* Store the value */
                myChild.setTextContent(myValue.toString());
            }
        }

        /* If we have an InfoSet */
        if (theInfoItems != null) {
            /* Add infoSet */
            Element myInfoSet = pDocument.createElement(TAG_INFOSET);
            myElement.appendChild(myInfoSet);

            /* Loop through the items */
            for (InfoItem myInfo : theInfoItems) {
                /* Create the element */
                Element myItem = pDocument.createElement(myInfo.getName());
                myInfoSet.appendChild(myItem);

                /* Set the id */
                myItem.setAttribute(DataItem.FIELD_ID.getName(), myInfo.getId().toString());

                /* Set the value */
                myItem.setTextContent(myInfo.getValue());
            }
        }

        /* Return the element */
        return myElement;
    }

    /**
     * Create XML for a list.
     * @param pDocument the document to hold the list.
     * @param pList the data list
     * @return the element holding the list
     */
    public static Element createXML(final Document pDocument,
                                    final DataList<?> pList) {
        /* Create an element for the item */
        Element myElement = pDocument.createElement(pList.listName());

        /* Iterate through the list */
        Iterator<?> myIterator = pList.iterator();
        while (myIterator.hasNext()) {
            Object myObject = myIterator.next();

            /* Ignore if not a DataItem */
            if (!(myObject instanceof DataItem)) {
                continue;
            }

            /* Access as DataItem */
            DataItem myItem = (DataItem) myObject;

            /* Create DataArguments for item */
            DataValues myArgs = new DataValues(myItem);

            /* Add the child to the list */
            Element myChild = myArgs.createXML(pDocument);
            myElement.appendChild(myChild);
        }

        /* Return the element */
        return myElement;
    }

    /**
     * Create XML for a list.
     * @param pElement the document to hold the list.
     * @param pFields the data fields
     * @return the element holding the list
     */
    public static List<DataValues> parseXML(final Element pElement,
                                            final JDataFields pFields) {
        /* Create the list */
        List<DataValues> myResult = new ArrayList<DataValues>();

        /* Loop through the children */
        for (Node myChild = pElement.getFirstChild(); myChild != null; myChild = myChild.getNextSibling()) {
            /* Ignore non-elements */
            if (!(myChild instanceof Element)) {
                continue;
            }

            /* Access as Element */
            Element myItem = (Element) myChild;

            /* Create DataArguments for item */
            DataValues myArgs = new DataValues(myItem, pFields);

            /* Add the child to the list */
            myResult.add(myArgs);
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Create XML for a DataSet.
     * @param pDocument the document to hold the list.
     * @param pData the data set
     */
    public static void createXML(final Document pDocument,
                                 final DataSet<?> pData) {
        /* Create an element for the document */
        Element myElement = pDocument.createElement(pData.getClass().getSimpleName());
        pDocument.appendChild(myElement);

        /* Declare data version */
        ControlData myControl = pData.getControl();
        myElement.setAttribute(ControlData.FIELD_VERSION.getName(), myControl.getDataVersion().toString());

        /* Iterate through the list */
        Iterator<DataList<?>> myIterator = pData.iterator();
        while (myIterator.hasNext()) {
            DataList<?> myList = myIterator.next();

            /* If this should be included as DataXML */
            if (myList.includeDataXML()) {
                /* Add the child to the list */
                Element myChild = createXML(pDocument, myList);
                myElement.appendChild(myChild);
            }
        }
    }

    /**
     * InfoItem class.
     */
    public static final class InfoItem {
        /**
         * Name of item.
         */
        private final String theName;

        /**
         * Id of item.
         */
        private final Integer theId;

        /**
         * Value of item.
         */
        private final String theValue;

        /**
         * Obtain name of item.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain id of item.
         * @return the id
         */
        public Integer getId() {
            return theId;
        }

        /**
         * Obtain name of item.
         * @return the name
         */
        public String getValue() {
            return theValue;
        }

        /**
         * Constructor.
         * @param pInfo the info Item
         */
        private InfoItem(final DataInfo<?, ?, ?, ?> pInfo) {
            /* Store values */
            theName = pInfo.getInfoClass().toString();
            theId = pInfo.getId();
            theValue = pInfo.getValue(Object.class).toString();
        }

        /**
         * Constructor.
         * @param pElement the XML element
         */
        private InfoItem(final Element pElement) {
            /* Store values */
            theName = pElement.getNodeName();
            theId = DataValues.getId(pElement);
            theValue = pElement.getTextContent();
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

            /* Make sure that the object is the same class */
            if (pThat.getClass() != getClass()) {
                return false;
            }

            /* Access the object as an InfoItem */
            InfoItem myItem = (InfoItem) pThat;

            if (!theName.equals(myItem.getName())) {
                return false;
            }
            if (!theId.equals(myItem.getId())) {
                return false;
            }
            return theValue.equals(myItem.getValue());
        }

        @Override
        public int hashCode() {
            return theName.hashCode()
                   + theId
                   + theValue.hashCode();
        }
    }
}
