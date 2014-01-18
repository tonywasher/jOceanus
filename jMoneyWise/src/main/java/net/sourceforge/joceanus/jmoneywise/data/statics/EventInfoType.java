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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * Data length.
     */
    protected static final int DATA_LEN = 20;

    /**
     * Comment length.
     */
    protected static final int COMMENT_LEN = 50;

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

    @Override
    public EventInfoTypeList getList() {
        return (EventInfoTypeList) super.getList();
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
     * @throws JOceanusException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Event Info Type with
     * @param pClass Class of Event Info Type
     * @throws JOceanusException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final EventInfoClass pClass) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final Integer pId,
                          final Boolean isEnabled,
                          final Integer pOrder,
                          final String pName,
                          final String pDesc) throws JOceanusException {
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
     * @throws JOceanusException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final Integer pId,
                          final Integer pControlId,
                          final Boolean isEnabled,
                          final Integer pOrder,
                          final byte[] pName,
                          final byte[] pDesc) throws JOceanusException {
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
        public EventInfoTypeList(final DataSet<?, ?> pData) {
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
        public EventInfoTypeList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (EventInfoTypeList) super.cloneList(pDataSet);
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
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pType) throws JOceanusException {
            /* Create a new InfoType */
            EventInfoType myType = new EventInfoType(this, pType);

            /* Check that this InfoType has not been previously added */
            if (findItemByName(pType) != null) {
                myType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add the Type to the list */
            append(myType);

            /* Validate the EventInfoType */
            myType.validate();

            /* Handle validation failure */
            if (myType.hasErrors()) {
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }
        }

        /**
         * Add an InfoType to the list.
         * @param pId the id of the new item
         * @param isEnabled is the type enabled
         * @param pOrder the sort order
         * @param pInfoType the Name of the InfoType
         * @param pDesc the Description of the InfoType
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pInfoType,
                                final String pDesc) throws JOceanusException {
            /* Create a new InfoType */
            EventInfoType myType = new EventInfoType(this, pId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(pId)) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add the InfoType to the list */
            append(myType);

            /* Validate the TaxRegime */
            myType.validate();

            /* Handle validation failure */
            if (myType.hasErrors()) {
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
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
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pInfoType,
                                  final byte[] pDesc) throws JOceanusException {
            /* Create a new InfoType */
            EventInfoType myType = new EventInfoType(this, pId, pControlId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add the InfoType to the list */
            append(myType);

            /* Validate the InfoType */
            myType.validate();

            /* Handle validation failure */
            if (myType.hasErrors()) {
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
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
                    throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
