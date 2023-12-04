/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.statics;

import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Frequency data type.
 * @author Tony Washer
 */
public class MoneyWiseFrequency
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.FREQUENCY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.FREQUENCY.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseFrequency> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseFrequency.class);

    /**
     * Copy Constructor.
     * @param pList The list to associate the Frequency with
     * @param pFrequency The frequency to copy
     */
    protected MoneyWiseFrequency(final MoneyWiseFrequencyList pList,
                                 final MoneyWiseFrequency pFrequency) {
        super(pList, pFrequency);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Frequency with
     * @param pName Name of Frequency
     * @throws OceanusException on error
     */
    private MoneyWiseFrequency(final MoneyWiseFrequencyList pList,
                               final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Frequency with
     * @param pClass Class of Frequency
     * @throws OceanusException on error
     */
    private MoneyWiseFrequency(final MoneyWiseFrequencyList pList,
                               final MoneyWiseFrequencyClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseFrequency(final MoneyWiseFrequencyList pList,
                               final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the Frequency class of the Frequency.
     * @return the class
     */
    public MoneyWiseFrequencyClass getFrequency() {
        return (MoneyWiseFrequencyClass) super.getStaticClass();
    }

    /**
     * Is this a base frequency?
     * @return true/false
     */
    public boolean isBaseFrequency() {
        final MoneyWiseFrequencyClass myFreq = getFrequency();
        return myFreq != null
                && myFreq.isBaseFrequency();
    }

    /**
     * Is this a valid repeat for frequency?
     * @param pFrequency the overall frequency
     * @return true/false
     */
    public boolean isValidRepeat(final MoneyWiseFrequency pFrequency) {
        final MoneyWiseFrequencyClass myFreq = getFrequency();
        return myFreq != null
                && myFreq.isValidRepeat(pFrequency.getFrequency());
    }

    /**
     * Do we have a repeat frequency?
     * @return true/false
     */
    public boolean hasRepeatFrequency() {
        final MoneyWiseFrequencyClass myFreq = getFrequency();
        return myFreq != null
                && myFreq.hasRepeatFrequency();
    }

    /**
     * Do we have a repeat interval?
     * @return true/false
     */
    public boolean hasRepeatInterval() {
        final MoneyWiseFrequencyClass myFreq = getFrequency();
        return myFreq != null
                && myFreq.hasRepeatInterval();
    }

    /**
     * Do we have a pattern?
     * @return true/false
     */
    public boolean hasPattern() {
        final MoneyWiseFrequencyClass myFreq = getFrequency();
        return myFreq != null
                && myFreq.hasPattern();
    }

    @Override
    public MoneyWiseFrequency getBase() {
        return (MoneyWiseFrequency) super.getBase();
    }

    @Override
    public MoneyWiseFrequencyList getList() {
        return (MoneyWiseFrequencyList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWiseFrequency} objects.
     */
    public static class MoneyWiseFrequencyList
            extends PrometheusStaticList<MoneyWiseFrequency> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseFrequencyList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseFrequencyList.class);

        /**
         * Construct an empty CORE frequency list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseFrequencyList(final PrometheusDataSet pData) {
            super(MoneyWiseFrequency.class, pData, MoneyWiseStaticDataType.FREQUENCY, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseFrequencyList(final MoneyWiseFrequencyList pSource) {
            super(pSource);
        }

        @Override
        protected MoneyWiseFrequencyList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseFrequencyList myList = new MoneyWiseFrequencyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MetisFieldSet<MoneyWiseFrequencyList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseFrequency.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseFrequencyClass> getEnumClass() {
            return MoneyWiseFrequencyClass.class;
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public MoneyWiseFrequency addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a Frequency */
            if (!(pItem instanceof MoneyWiseFrequency)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseFrequency myFreq = new MoneyWiseFrequency(this, (MoneyWiseFrequency) pItem);
            add(myFreq);
            return myFreq;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public MoneyWiseFrequency addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Add a Frequency.
         * @param pFrequency the Name of the frequency
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pFrequency) throws OceanusException {
            /* Create a new Frequency */
            final MoneyWiseFrequency myFrequency = new MoneyWiseFrequency(this, pFrequency);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFrequency.getIndexedId())) {
                myFrequency.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myFrequency, ERROR_VALIDATION);
            }

            /* Add the Frequency to the list */
            add(myFrequency);
        }

        @Override
        public MoneyWiseFrequency addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the frequency */
            final MoneyWiseFrequency myFreq = new MoneyWiseFrequency(this, pValues);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFreq.getIndexedId())) {
                myFreq.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myFreq);

            /* Return it */
            return myFreq;
        }

        @Override
        protected MoneyWiseFrequency newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the frequency */
            final MoneyWiseFrequency myFreq = new MoneyWiseFrequency(this, (MoneyWiseFrequencyClass) pClass);

            /* Check that this FreqId has not been previously added */
            if (!isIdUnique(myFreq.getIndexedId())) {
                myFreq.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myFreq);

            /* Return it */
            return myFreq;
        }
    }
}
