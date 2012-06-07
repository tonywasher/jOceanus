/*******************************************************************************
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
package uk.co.tolcroft.finance.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import uk.co.tolcroft.finance.data.StaticClass.EventInfoClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.StaticData;

public class EventInfoType extends StaticData<EventInfoType, EventInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventInfoType.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the EventInfo class of the EventInfoType.
     * @return the class
     */
    public EventInfoClass getInfoClass() {
        return super.getStaticClass();
    }

    @Override
    public EventInfoType getBase() {
        return (EventInfoType) super.getBase();
    }

    /* Override the isActive method */
    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Construct a copy of a InfoType.
     * @param pList The list to associate the EventInfoType with
     * @param pType The InfoType to copy
     */
    protected EventInfoType(final EventInfoTypeList pList,
                            final EventInfoType pType) {
        super(pList, pType);
    }

    /**
     * Construct a standard InfoType on load.
     * @param pList The list to associate the EventInfoType with
     * @param sName Name of InfoType
     * @throws JDataException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Construct an InfoType on load.
     * @param pList The list to associate the InfoType with
     * @param uId the id of the new item
     * @param isEnabled is the type enabled
     * @param uOrder the sort order
     * @param pName Name of InfoType
     * @param pDesc Description of InfoType
     * @throws JDataException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final int uId,
                          final boolean isEnabled,
                          final int uOrder,
                          final String pName,
                          final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Construct a standard InfoType on load.
     * @param pList The list to associate the InfoType with
     * @param uId ID of InfoType
     * @param uControlId the control id of the new item
     * @param isEnabled is the InfoType enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of InfoType
     * @param pDesc Encrypted Description of InfoType
     * @throws JDataException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final int uId,
                          final int uControlId,
                          final boolean isEnabled,
                          final int uOrder,
                          final byte[] pName,
                          final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link EventInfoType} objects.
     */
    public static class EventInfoTypeList extends
            StaticList<EventInfoTypeList, EventInfoType, EventInfoClass> {
        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<EventInfoClass> getEnumClass() {
            return EventInfoClass.class;
        }

        /**
         * Construct an empty CORE eventInfo list
         * @param pData the DataSet for the list
         */
        protected EventInfoTypeList(final FinanceData pData) {
            super(EventInfoTypeList.class, EventInfoType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private EventInfoTypeList(final EventInfoTypeList pSource) {
            super(pSource);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private EventInfoTypeList getExtractList(final ListStyle pStyle) {
            /* Build an empty Extract List */
            EventInfoTypeList myList = new EventInfoTypeList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        @Override
        public EventInfoTypeList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public EventInfoTypeList getEditList() {
            return getExtractList(ListStyle.EDIT);
        }

        @Override
        public EventInfoTypeList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public EventInfoTypeList getDeepCopy(final DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            EventInfoTypeList myList = new EventInfoTypeList(this);
            myList.setData(pDataSet);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        @Override
        protected EventInfoTypeList getDifferences(final EventInfoTypeList pOld) {
            /* Build an empty Difference List */
            EventInfoTypeList myList = new EventInfoTypeList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        @Override
        public EventInfoType addNewItem(final DataItem<?> pItem) {
            EventInfoType myType = new EventInfoType(this, (EventInfoType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public EventInfoType addNewItem() {
            return null;
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add an InfoType.
         * @param pType the Name of the InfoType
         * @throws JDataException on error
         */
        public void addItem(final String pType) throws JDataException {
            EventInfoType myType;

            /* Create a new InfoType */
            myType = new EventInfoType(this, pType);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myType, "Duplicate EventInfoTypeId");
            }

            /* Check that this InfoType has not been previously added */
            if (searchFor(pType) != null) {
                throw new JDataException(ExceptionClass.DATA, myType, "Duplicate EventInfoType");
            }

            /* Add the Type to the list */
            add(myType);
        }

        /**
         * Add an InfoType to the list.
         * @param uId the id of the new item
         * @param isEnabled is the type enabled
         * @param uOrder the sort order
         * @param pInfoType the Name of the InfoType
         * @param pDesc the Description of the InfoType
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final boolean isEnabled,
                            final int uOrder,
                            final String pInfoType,
                            final String pDesc) throws JDataException {
            EventInfoType myType;

            /* Create a new InfoType */
            myType = new EventInfoType(this, uId, isEnabled, uOrder, pInfoType, pDesc);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myType, "Duplicate EventInfoTypeId");
            }

            /* Add the InfoType to the list */
            add(myType);

            /* Validate the TaxRegime */
            myType.validate();

            /* Handle validation failure */
            if (myType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myType, "Failed validation");
            }
        }

        /**
         * Add an InfoType.
         * @param uId the Id of the InfoType
         * @param uControlId the control id of the new item
         * @param isEnabled is the regime enabled
         * @param uOrder the sort order
         * @param pInfoType the Encrypted Name of the InfoType
         * @param pDesc the Encrypted Description of the InfoType
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final int uControlId,
                            final boolean isEnabled,
                            final int uOrder,
                            final byte[] pInfoType,
                            final byte[] pDesc) throws JDataException {
            EventInfoType myType;

            /* Create a new InfoType */
            myType = new EventInfoType(this, uId, uControlId, isEnabled, uOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myType, "Duplicate EventInfoTypeId");
            }

            /* Add the InfoType to the list */
            add(myType);

            /* Validate the InfoType */
            myType.validate();

            /* Handle validation failure */
            if (myType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myType, "Failed validation");
            }
        }
    }
}
