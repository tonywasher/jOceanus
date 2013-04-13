/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;

/**
 * EventInfoType data type.
 * @author Tony Washer
 */
public class EventInfoType
        extends StaticData<EventInfoType, EventInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventInfoType.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

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

    /**
     * Return the Data Type of the EventInfoType.
     * @return the data type
     */
    public DataType getDataType() {
        return getInfoClass().getDataType();
    }

    /**
     * is this a Link?
     * @return true/false
     */
    public boolean isLink() {
        return getInfoClass().isLink();
    }

    @Override
    public EventInfoType getBase() {
        return (EventInfoType) super.getBase();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the EventInfoType with
     * @param pType The InfoType to copy
     */
    protected EventInfoType(final EventInfoTypeList pList,
                            final EventInfoType pType) {
        super(pList, pType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the EventInfoType with
     * @param pName Name of InfoType
     * @throws JDataException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final String pName) throws JDataException {
        super(pList, pName);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Event Info Type with
     * @param pClass Class of Event Info Type
     * @throws JDataException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final EventInfoClass pClass) throws JDataException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the InfoType with
     * @param pId the id of the new item
     * @param isEnabled is the type enabled
     * @param pOrder the sort order
     * @param pName Name of InfoType
     * @param pDesc Description of InfoType
     * @throws JDataException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final Integer pId,
                          final Boolean isEnabled,
                          final Integer pOrder,
                          final String pName,
                          final String pDesc) throws JDataException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the InfoType with
     * @param pId ID of InfoType
     * @param pControlId the control id of the new item
     * @param isEnabled is the InfoType enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of InfoType
     * @param pDesc Encrypted Description of InfoType
     * @throws JDataException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final Integer pId,
                          final Integer pControlId,
                          final Boolean isEnabled,
                          final Integer pOrder,
                          final byte[] pName,
                          final byte[] pDesc) throws JDataException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link EventInfoType} objects.
     */
    public static class EventInfoTypeList
            extends StaticList<EventInfoType, EventInfoClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventInfoTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<EventInfoClass> getEnumClass() {
            return EventInfoClass.class;
        }

        /**
         * Construct an empty CORE eventInfo list.
         * @param pData the DataSet for the list
         */
        public EventInfoTypeList(final DataSet<?> pData) {
            super(EventInfoType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private EventInfoTypeList(final EventInfoTypeList pSource) {
            super(pSource);
        }

        @Override
        protected EventInfoTypeList getEmptyList(final ListStyle pStyle) {
            EventInfoTypeList myList = new EventInfoTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventInfoTypeList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (EventInfoTypeList) super.cloneList(pDataSet);
        }

        @Override
        public EventInfoTypeList deriveList(final ListStyle pStyle) throws JDataException {
            return (EventInfoTypeList) super.deriveList(pStyle);
        }

        @Override
        public EventInfoTypeList deriveDifferences(final DataList<EventInfoType> pOld) {
            return (EventInfoTypeList) super.deriveDifferences(pOld);
        }

        @Override
        public EventInfoType addCopyItem(final DataItem pItem) {
            /* Can only clone an EventInfoType */
            if (!(pItem instanceof EventInfoType)) {
                return null;
            }

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
        public void addBasicItem(final String pType) throws JDataException {
            /* Create a new InfoType */
            EventInfoType myType = new EventInfoType(this, pType);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myType, "Duplicate EventInfoTypeId");
            }

            /* Check that this InfoType has not been previously added */
            if (findItemByName(pType) != null) {
                throw new JDataException(ExceptionClass.DATA, myType, "Duplicate EventInfoType");
            }

            /* Add the Type to the list */
            append(myType);

            /* Validate the EventInfoType */
            myType.validate();

            /* Handle validation failure */
            if (myType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myType, "Failed validation");
            }
        }

        /**
         * Add an InfoType to the list.
         * @param pId the id of the new item
         * @param isEnabled is the type enabled
         * @param pOrder the sort order
         * @param pInfoType the Name of the InfoType
         * @param pDesc the Description of the InfoType
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pInfoType,
                                final String pDesc) throws JDataException {
            /* Create a new InfoType */
            EventInfoType myType = new EventInfoType(this, pId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(pId)) {
                throw new JDataException(ExceptionClass.DATA, myType, "Duplicate EventInfoTypeId");
            }

            /* Add the InfoType to the list */
            append(myType);

            /* Validate the TaxRegime */
            myType.validate();

            /* Handle validation failure */
            if (myType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myType, "Failed validation");
            }
        }

        /**
         * Add an InfoType.
         * @param pId the Id of the InfoType
         * @param pControlId the control id of the new item
         * @param isEnabled is the regime enabled
         * @param pOrder the sort order
         * @param pInfoType the Encrypted Name of the InfoType
         * @param pDesc the Encrypted Description of the InfoType
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pInfoType,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new InfoType */
            EventInfoType myType = new EventInfoType(this, pId, pControlId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                throw new JDataException(ExceptionClass.DATA, myType, "Duplicate EventInfoTypeId");
            }

            /* Add the InfoType to the list */
            append(myType);

            /* Validate the InfoType */
            myType.validate();

            /* Handle validation failure */
            if (myType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myType, "Failed validation");
            }
        }

        /**
         * Populate default values.
         * @throws JDataException on error
         */
        public void populateDefaults() throws JDataException {
            /* Loop through all elements */
            for (EventInfoClass myClass : EventInfoClass.values()) {
                /* Create new element */
                EventInfoType myType = new EventInfoType(this, myClass);

                /* Add the EventInfoType to the list */
                append(myType);

                /* Validate the EventInfoType */
                myType.validate();

                /* Handle validation failure */
                if (myType.hasErrors()) {
                    throw new JDataException(ExceptionClass.VALIDATE, myType, "Failed validation");
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
