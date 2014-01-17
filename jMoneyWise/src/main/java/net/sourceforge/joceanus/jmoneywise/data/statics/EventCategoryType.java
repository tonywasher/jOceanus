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

import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * EventCategoryType data type.
 * @author Tony Washer
 */
public class EventCategoryType
        extends StaticData<EventCategoryType, EventCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = EventCategoryType.class.getSimpleName();

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
     * Return the Category class of the Category Type.
     * @return the class
     */
    public EventCategoryClass getCategoryClass() {
        return super.getStaticClass();
    }

    @Override
    public boolean isActive() {
        return super.isActive()
               || getCategoryClass().isHiddenType();
    }

    @Override
    public EventCategoryType getBase() {
        return (EventCategoryType) super.getBase();
    }

    @Override
    public EventCategoryTypeList getList() {
        return (EventCategoryTypeList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Category Type with
     * @param pCatType The Category Type to copy
     */
    protected EventCategoryType(final EventCategoryTypeList pList,
                                final EventCategoryType pCatType) {
        super(pList, pCatType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Category Type with
     * @param pName Name of Category Type
     * @throws JOceanusException on error
     */
    private EventCategoryType(final EventCategoryTypeList pList,
                              final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Event Category Type with
     * @param pClass Class of Event Category Type
     * @throws JOceanusException on error
     */
    private EventCategoryType(final EventCategoryTypeList pList,
                              final EventCategoryClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Category Type with
     * @param uId ID of Category Type
     * @param isEnabled is the EventCategoryType enabled
     * @param uOrder the sort order
     * @param pName Name of Category Type
     * @param pDesc Description of Category Type
     * @throws JOceanusException on error
     */
    private EventCategoryType(final EventCategoryTypeList pList,
                              final Integer uId,
                              final Boolean isEnabled,
                              final Integer uOrder,
                              final String pName,
                              final String pDesc) throws JOceanusException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the Category Type with
     * @param uId ID of Category Type
     * @param uControlId the control id of the new item
     * @param isEnabled is the EventCategoryType enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of Category Type
     * @param pDesc Encrypted Description of Category Type
     * @throws JOceanusException on error
     */
    private EventCategoryType(final EventCategoryTypeList pList,
                              final Integer uId,
                              final Integer uControlId,
                              final Boolean isEnabled,
                              final Integer uOrder,
                              final byte[] pName,
                              final byte[] pDesc) throws JOceanusException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link EventCategoryType} objects.
     */
    public static class EventCategoryTypeList
            extends StaticList<EventCategoryType, EventCategoryClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventCategoryTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<EventCategoryClass> getEnumClass() {
            return EventCategoryClass.class;
        }

        /**
         * Construct an empty CORE category type list.
         * @param pData the DataSet for the list
         */
        public EventCategoryTypeList(final DataSet<?, ?> pData) {
            super(EventCategoryType.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private EventCategoryTypeList(final EventCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        protected EventCategoryTypeList getEmptyList(final ListStyle pStyle) {
            EventCategoryTypeList myList = new EventCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventCategoryTypeList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (EventCategoryTypeList) super.cloneList(pDataSet);
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public EventCategoryType addCopyItem(final DataItem pItem) {
            /* Can only clone a EventCategoryType */
            if (!(pItem instanceof EventCategoryType)) {
                return null;
            }

            EventCategoryType myType = new EventCategoryType(this, (EventCategoryType) pItem);
            add(myType);
            return myType;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public EventCategoryType addNewItem() {
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
         * Add a EventCategoryType.
         * @param pEventCategoryType the Name of the category type
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pEventCategoryType) throws JOceanusException {
            /* Create a new Category Type */
            EventCategoryType myCatType = new EventCategoryType(this, pEventCategoryType);

            /* Check that this EventCategoryType has not been previously added */
            if (findItemByName(pEventCategoryType) != null) {
                myCatType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JOceanusException(myCatType, ERROR_VALIDATION);
            }

            /* Check that this EventCategoryTypeId has not been previously added */
            if (!isIdUnique(myCatType.getId())) {
                myCatType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JOceanusException(myCatType, ERROR_VALIDATION);
            }

            /* Add the Category Type to the list */
            append(myCatType);

            /* Validate the EventCategoryType */
            myCatType.validate();

            /* Handle validation failure */
            if (myCatType.hasErrors()) {
                throw new JOceanusException(myCatType, ERROR_VALIDATION);
            }
        }

        /**
         * Add a EventCategoryType to the list.
         * @param pId ID of Category Type
         * @param isEnabled is the EventCategoryType enabled
         * @param pOrder the sort order
         * @param pCatType the Name of the category type
         * @param pDesc the Description of the category type
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pCatType,
                                final String pDesc) throws JOceanusException {
            /* Create a new Category Type */
            EventCategoryType myCatType = new EventCategoryType(this, pId, isEnabled, pOrder, pCatType, pDesc);

            /* Check that this EventCategoryTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myCatType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JOceanusException(myCatType, ERROR_VALIDATION);
            }

            /* Add the Category Type to the list */
            append(myCatType);

            /* Validate the EventCategoryType */
            myCatType.validate();

            /* Handle validation failure */
            if (myCatType.hasErrors()) {
                throw new JOceanusException(myCatType, ERROR_VALIDATION);
            }
        }

        /**
         * Add a EventCategoryType.
         * @param pId the Id of the category type
         * @param pControlId the control id of the new item
         * @param isEnabled is the EventCategoryType enabled
         * @param pOrder the sort order
         * @param pCatType the Encrypted Name of the category type
         * @param pDesc the Encrypted Description of the category type
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pCatType,
                                  final byte[] pDesc) throws JOceanusException {
            /* Create a new Category Type */
            EventCategoryType myCatType = new EventCategoryType(this, pId, pControlId, isEnabled, pOrder, pCatType, pDesc);

            /* Check that this EventCategoryTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myCatType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JOceanusException(myCatType, ERROR_VALIDATION);
            }

            /* Add the Category Type to the list */
            append(myCatType);

            /* Validate the EventCategoryType */
            myCatType.validate();

            /* Handle validation failure */
            if (myCatType.hasErrors()) {
                throw new JOceanusException(myCatType, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
            /* Loop through all elements */
            for (EventCategoryClass myClass : EventCategoryClass.values()) {
                /* Create new element */
                EventCategoryType myType = new EventCategoryType(this, myClass);

                /* Add the EventCategory to the list */
                append(myType);

                /* Validate the EventCategoryType */
                myType.validate();

                /* Handle validation failure */
                if (myType.hasErrors()) {
                    throw new JOceanusException(myType, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
