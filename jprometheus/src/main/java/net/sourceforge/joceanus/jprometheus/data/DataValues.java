/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Arguments class for DataItem.
 * @author Tony Washer
 * @param <E> the data type enum class
 */
public class DataValues<E extends Enum<E>> {
    /**
     * Interface for an infoSet item.
     * @param <E> the data type enum class
     */
    public interface InfoSetItem<E extends Enum<E>> {
        /**
         * Obtain infoSet.
         * @return the infoSet
         */
        DataInfoSet<?, ?, ?, ?, E> getInfoSet();
    }

    /**
     * Interface for a grouped item.
     * @param <E> the data type enum class
     */
    public interface GroupedItem<E extends Enum<E>> {
        /**
         * Is the item a child.
         * @return true/false
         */
        boolean isChild();

        /**
         * Obtain the child iterator.
         * @return the iterator
         */
        Iterator<? extends DataItem<E>> childIterator();
    }

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataValues.class.getName());

    /**
     * InfoSet Items tag.
     */
    private static final String TAG_INFOSET = NLS_BUNDLE.getString("DataInfoSet");

    /**
     * Children Items tag.
     */
    private static final String TAG_CHILDREN = NLS_BUNDLE.getString("DataChildren");

    /**
     * Child Item tag.
     */
    private static final String TAG_CHILD = NLS_BUNDLE.getString("DataChild");

    /**
     * List type attribute.
     */
    protected static final String ATTR_TYPE = NLS_BUNDLE.getString("DataAttrType");

    /**
     * List size attribute.
     */
    protected static final String ATTR_SIZE = NLS_BUNDLE.getString("DataAttrSize");

    /**
     * Data Version attribute.
     */
    protected static final String ATTR_VERS = NLS_BUNDLE.getString("DataAttrVersion");

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
    private final List<InfoItem<E>> theInfoItems;

    /**
     * Child values.
     */
    private final List<DataValues<E>> theChildren;

    /**
     * Obtain Item Type.
     * @return the Item Type
     */
    public final String getItemType() {
        return theItemType;
    }

    /**
     * Obtain Field iterator.
     * @return the Field iterator
     */
    public final Iterator<Map.Entry<JDataField, Object>> fieldIterator() {
        return theFields.entrySet().iterator();
    }

    /**
     * Does this item have InfoItems?
     * @return true/false
     */
    public final boolean hasInfoItems() {
        return theInfoItems != null;
    }

    /**
     * Obtain InfoItems iterator.
     * @return the iterator
     */
    public final Iterator<InfoItem<E>> infoIterator() {
        return theInfoItems.iterator();
    }

    /**
     * Does this item have children?
     * @return true/false
     */
    public final boolean hasChildren() {
        return theChildren != null;
    }

    /**
     * Obtain Child iterator.
     * @return the iterator
     */
    public final Iterator<DataValues<E>> childIterator() {
        return theChildren.iterator();
    }

    /**
     * Constructor.
     * @param pItem the Item to obtain values from
     */
    public DataValues(final DataItem<E> pItem) {
        this(pItem, pItem.getDataFields().getName());
    }

    /**
     * Constructor.
     * @param pItem the Item to obtain values from
     * @param pItemName the item name
     */
    protected DataValues(final DataItem<?> pItem,
                         final String pItemName) {
        /* Store Item type */
        theItemType = pItemName;

        /* Create the map and list */
        theFields = new LinkedHashMap<JDataField, Object>();

        /* Store the id */
        theFields.put(DataItem.FIELD_ID, pItem.getId());

        /* Access values */
        ValueSet myValues = pItem.getValueSet();

        /* Iterate through the fields */
        Iterator<JDataField> myIterator = pItem.getDataFields().fieldIterator();
        while (myIterator.hasNext()) {
            JDataField myField = myIterator.next();

            /* Ignore field if it is irrelevant */
            if (!myField.isValueSetField() || !myField.isEqualityField()) {
                continue;
            }

            /* If the field is to be included */
            if (pItem.includeXmlField(myField)) {
                /* Store the value if it is non-null */
                theFields.put(myField, myValues.getValue(myField));
            }
        }

        /* If the item is an infoSet item */
        if (pItem instanceof InfoSetItem) {
            /* Access InfoSet */
            DataInfoSet<?, ?, ?, ?, E> myInfoSet = ((InfoSetItem<E>) pItem).getInfoSet();

            /* If the InfoSet is non-empty */
            if (myInfoSet.isEmpty()) {
                /* No infoSet items */
                theInfoItems = null;
            } else {
                /* Allocate infoItems list */
                theInfoItems = new ArrayList<InfoItem<E>>();

                /* Iterator over the values */
                Iterator<?> myInfoIterator = myInfoSet.iterator();
                while (myInfoIterator.hasNext()) {
                    Object myCurr = myInfoIterator.next();

                    /* If this is a DataInfo item */
                    if (myCurr instanceof DataInfo) {
                        /* Access as DataArguments */
                        DataInfo<?, ?, ?, ?, E> myItem = (DataInfo<?, ?, ?, ?, E>) myCurr;

                        /* Add item to the list */
                        InfoItem<E> myInfo = new InfoItem<E>(myItem);
                        theInfoItems.add(myInfo);
                    }
                }
            }

            /* Else not that we have no infoItems */
        } else {
            theInfoItems = null;
        }

        /* If the item is an grouped item */
        if (pItem instanceof GroupedItem) {
            /* Access child iterator */
            Iterator<? extends DataItem<E>> myChildIterator = ((GroupedItem<E>) pItem).childIterator();

            /* If there are no children */
            if (myChildIterator == null) {
                theChildren = null;
            } else {
                /* Allocate child list */
                theChildren = new ArrayList<DataValues<E>>();

                /* Iterator over the values */
                while (myChildIterator.hasNext()) {
                    DataItem<?> myCurr = myChildIterator.next();

                    /* Add child to the list */
                    DataValues<E> myChild = new DataValues<E>(myCurr, TAG_CHILD);
                    theChildren.add(myChild);
                }
            }

            /* Else note that we have no children */
        } else {
            theChildren = null;
        }
    }

    /**
     * Constructor.
     * @param pOwner the Owner of the DataInfo Item
     * @param pInfo the values of the DataInfo Item
     */
    private DataValues(final DataItem<E> pOwner,
                       final InfoItem<E> pInfo) {
        /* Store Item type */
        theItemType = "";

        /* Create the map and list */
        theFields = new LinkedHashMap<JDataField, Object>();

        /* Store the id if available */
        Integer myId = pInfo.getId();
        if (myId != null) {
            theFields.put(DataItem.FIELD_ID, myId);
        }

        /* Store the Info Type */
        theFields.put(DataInfo.FIELD_INFOTYPE, pInfo.getName());

        /* Store the Owner */
        theFields.put(DataInfo.FIELD_OWNER, pOwner.getId());

        /* Store the value */
        theFields.put(DataInfo.FIELD_VALUE, pInfo.getValue());

        /* Set other fields to null */
        theInfoItems = null;
        theChildren = null;
    }

    /**
     * Constructor.
     * @param pElement the Item to obtain values from
     * @param pFields the field definitions
     */
    public DataValues(final Element pElement,
                      final JDataFields pFields) {
        this(pElement, pFields, pElement.getNodeName());
    }

    /**
     * Constructor.
     * @param pElement the Item to obtain values from
     * @param pFields the field definitions
     * @param pItemName the item name
     */
    protected DataValues(final Element pElement,
                         final JDataFields pFields,
                         final String pItemName) {
        /* Store Item type */
        theItemType = pItemName;

        /* Create the map */
        theFields = new LinkedHashMap<JDataField, Object>();

        /* Declare the id if it exists */
        Integer myId = getId(pElement);
        if (myId != null) {
            theFields.put(DataItem.FIELD_ID, myId);
        }

        /* Loop through the children */
        Iterator<JDataField> myIterator = pFields.fieldIterator();
        while (myIterator.hasNext()) {
            JDataField myField = myIterator.next();

            /* If the field is an equality valueSet item */
            if (myField.isValueSetField() && myField.isEqualityField()) {
                /* Access element */
                Element myChild = getChild(pElement, myField.getName());
                if (myChild != null) {
                    /* Put value */
                    theFields.put(myField, myChild.getTextContent());
                }
            }
        }

        /* Look for an InfoSet list */
        Element myInfoSet = getChild(pElement, TAG_INFOSET);
        if (myInfoSet != null) {
            /* Allocate infoItems list */
            theInfoItems = new ArrayList<InfoItem<E>>();

            /* Loop through the child values */
            for (Node myCurr = myInfoSet.getFirstChild(); myCurr != null; myCurr = myCurr.getNextSibling()) {
                /* If the child is an element */
                if (myCurr instanceof Element) {
                    /* Access as element */
                    Element myChild = (Element) myCurr;

                    /* Add item to the list */
                    InfoItem<E> myInfo = new InfoItem<E>(myChild);
                    theInfoItems.add(myInfo);
                }
            }

            /* Else not that we have no infoItems */
        } else {
            theInfoItems = null;
        }

        /* Look for children */
        Element myChildren = getChild(pElement, TAG_CHILDREN);
        if (myChildren != null) {
            /* Allocate infoItems list */
            theChildren = new ArrayList<DataValues<E>>();

            /* Loop through the child values */
            for (Node myCurr = myChildren.getFirstChild(); myCurr != null; myCurr = myCurr.getNextSibling()) {
                /* If the child is the correct element */
                if ((myCurr instanceof Element) && TAG_CHILD.equals(myCurr.getNodeName())) {
                    /* Access as element */
                    Element myChild = (Element) myCurr;

                    /* Add item to the list */
                    DataValues<E> myValues = new DataValues<E>(myChild, pFields, theItemType);
                    theChildren.add(myValues);
                }
            }

            /* Else not that we have no children */
        } else {
            theChildren = null;
        }
    }

    /**
     * Constructor.
     * @param pName the Item type
     */
    public DataValues(final String pName) {
        /* Store Item type */
        theItemType = pName;

        /* Create the map */
        theFields = new LinkedHashMap<JDataField, Object>();

        /* No underlying arrays */
        theInfoItems = null;
        theChildren = null;
    }

    /**
     * Add value.
     * @param pField the Field definition
     * @param pValue the field value
     */
    public void addValue(final JDataField pField,
                         final Object pValue) {
        /* Add the field */
        theFields.put(pField, pValue);
    }

    /**
     * Obtain value.
     * @param pField the Field definition
     * @return the field value
     */
    public Object getValue(final JDataField pField) {
        /* Return the field */
        return theFields.get(pField);
    }

    /**
     * Obtain value of specified class.
     * @param pField the Field definition
     * @param pClass the class
     * @param <T> the item type
     * @return the field value
     */
    public <T> T getValue(final JDataField pField,
                          final Class<T> pClass) {
        /* Return the properly cast field */
        return pClass.cast(getValue(pField));
    }

    /**
     * Obtain id from element.
     * @param pElement the element.
     * @return the id
     */
    private static Integer getId(final Element pElement) {
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
        /* Loop through the child values */
        for (Node myCurr = pParent.getFirstChild(); myCurr != null; myCurr = myCurr.getNextSibling()) {
            /* If the child is the correct element */
            if ((myCurr instanceof Element) && pName.equals(myCurr.getNodeName())) {
                /* Return the element */
                return (Element) myCurr;
            }
        }

        /* Not found */
        return null;
    }

    /**
     * Create XML element for item.
     * @param pDocument the document to hold the item.
     * @param pFormatter the data formatter
     * @param pStoreIds do we include IDs in XML
     * @return the new element
     */
    protected Element createXML(final Document pDocument,
                                final JDataFormatter pFormatter,
                                final boolean pStoreIds) {
        /* Create an element for the item */
        Element myElement = pDocument.createElement(theItemType);

        /* Loop through the values */
        for (Map.Entry<JDataField, Object> myEntry : theFields.entrySet()) {
            /* Access parts */
            JDataField myField = myEntry.getKey();
            Object myValue = myEntry.getValue();

            /* If this is the Id */
            if (DataItem.FIELD_ID.equals(myField)) {
                /* Add as an Attribute if required */
                if (pStoreIds) {
                    myElement.setAttribute(myField.getName(), myValue.toString());
                }

                /* Skip to next field */
                continue;
            }

            /* Create the child element */
            Element myChild = pDocument.createElement(myField.getName());
            myElement.appendChild(myChild);

            /* Store the value */
            myChild.setTextContent(pFormatter.formatObject(myValue));
        }

        /* If we have InfoSet items */
        if (theInfoItems != null) {
            /* Add infoSet */
            Element myInfoSet = pDocument.createElement(TAG_INFOSET);
            myElement.appendChild(myInfoSet);

            /* Loop through the items */
            for (InfoItem<E> myInfo : theInfoItems) {
                /* Create the element */
                Element myItem = pDocument.createElement(myInfo.getName());
                myInfoSet.appendChild(myItem);

                /* Set the id if required */
                if (pStoreIds) {
                    myItem.setAttribute(DataItem.FIELD_ID.getName(), myInfo.getId().toString());
                }

                /* Set the value */
                myItem.setTextContent(pFormatter.formatObject(myInfo.getValue()));
            }
        }

        /* If we have children */
        if (theChildren != null) {
            /* Add children */
            Element myChildren = pDocument.createElement(TAG_CHILDREN);
            myElement.appendChild(myChildren);

            /* Loop through the children */
            Iterator<DataValues<E>> myIterator = theChildren.iterator();
            while (myIterator.hasNext()) {
                DataValues<E> myValues = myIterator.next();

                /* Create the subElement and append */
                Element myChild = myValues.createXML(pDocument, pFormatter, pStoreIds);
                myChildren.appendChild(myChild);
            }
        }

        /* Return the element */
        return myElement;
    }

    /**
     * InfoItem class.
     * @param <E> the data type enum class
     */
    public static final class InfoItem<E extends Enum<E>> {
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
        private final Object theValue;

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
         * Obtain value of item.
         * @return the value
         */
        public Object getValue() {
            return theValue;
        }

        /**
         * Constructor.
         * @param pInfo the info Item
         */
        private InfoItem(final DataInfo<?, ?, ?, ?, ?> pInfo) {
            /* Access the infoClass */
            DataInfoClass myClass = pInfo.getInfoClass();

            /* Store values */
            theName = myClass.toString();
            theId = pInfo.getId();
            theValue = myClass.isLink()
                                       ? pInfo.getLink(DataItem.class)
                                       : pInfo.getValue(Object.class);
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
            InfoItem<E> myItem = (InfoItem<E>) pThat;

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
            return theName.hashCode() + theId + theValue.hashCode();
        }

        @Override
        public String toString() {
            return theName + "=" + theValue;
        }

        /**
         * Obtain DataValues.
         * @param pOwner the owner
         * @return the dataValues
         */
        public DataValues<E> getValues(final DataItem<E> pOwner) {
            return new DataValues<E>(pOwner, this);
        }
    }
}
