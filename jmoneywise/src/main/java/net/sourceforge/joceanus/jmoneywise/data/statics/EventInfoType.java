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
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * EventInfoType data type.
 * @author Tony Washer
 */
public class EventInfoType
        extends StaticData<EventInfoType, EventInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.EVENTINFOTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.EVENTINFOTYPE.getListName();

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
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private EventInfoType(final EventInfoTypeList pList,
                          final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
    }

    /**
     * Represents a list of {@link EventInfoType} objects.
     */
    public static class EventInfoTypeList
            extends StaticList<EventInfoType, EventInfoClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return EventInfoType.FIELD_DEFS;
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
            super(EventInfoType.class, pData, MoneyWiseDataType.EVENTINFOTYPE, ListStyle.CORE);
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
        public EventInfoType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone an EventInfoType */
            if (!(pItem instanceof EventInfoType)) {
                throw new UnsupportedOperationException();
            }

            EventInfoType myType = new EventInfoType(this, (EventInfoType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public EventInfoType addNewItem() {
            throw new UnsupportedOperationException();
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

        @Override
        public EventInfoType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the type */
            EventInfoType myType = new EventInfoType(this, pValues);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myType);

            /* Return it */
            return myType;
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
