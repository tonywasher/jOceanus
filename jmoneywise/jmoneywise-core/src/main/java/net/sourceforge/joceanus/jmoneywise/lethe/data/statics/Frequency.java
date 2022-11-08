/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Frequency data type.
 * @author Tony Washer
 */
public class Frequency
        extends StaticDataItem<Frequency, FrequencyClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.FREQUENCY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.FREQUENCY.getListName();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, StaticDataItem.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the Frequency with
     * @param pFrequency The frequency to copy
     */
    protected Frequency(final FrequencyList pList,
                        final Frequency pFrequency) {
        super(pList, pFrequency);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Frequency with
     * @param pName Name of Frequency
     * @throws OceanusException on error
     */
    private Frequency(final FrequencyList pList,
                      final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Frequency with
     * @param pClass Class of Frequency
     * @throws OceanusException on error
     */
    private Frequency(final FrequencyList pList,
                      final FrequencyClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private Frequency(final FrequencyList pList,
                      final DataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Frequency class of the Frequency.
     * @return the class
     */
    public FrequencyClass getFrequency() {
        return super.getStaticClass();
    }

    /**
     * Is this a base frequency?
     * @return true/false
     */
    public boolean isBaseFrequency() {
        final FrequencyClass myFreq = getFrequency();
        return myFreq != null
               && myFreq.isBaseFrequency();
    }

    /**
     * Is this a valid repeat for frequency?
     * @param pFrequency the overall frequency
     * @return true/false
     */
    public boolean isValidRepeat(final Frequency pFrequency) {
        final FrequencyClass myFreq = getFrequency();
        return myFreq != null
               && myFreq.isValidRepeat(pFrequency.getFrequency());
    }

    /**
     * Do we have a repeat frequency?
     * @return true/false
     */
    public boolean hasRepeatFrequency() {
        final FrequencyClass myFreq = getFrequency();
        return myFreq != null
               && myFreq.hasRepeatFrequency();
    }

    /**
     * Do we have a repeat interval?
     * @return true/false
     */
    public boolean hasRepeatInterval() {
        final FrequencyClass myFreq = getFrequency();
        return myFreq != null
               && myFreq.hasRepeatInterval();
    }

    /**
     * Do we have a pattern?
     * @return true/false
     */
    public boolean hasPattern() {
        final FrequencyClass myFreq = getFrequency();
        return myFreq != null
               && myFreq.hasPattern();
    }

    @Override
    public Frequency getBase() {
        return (Frequency) super.getBase();
    }

    @Override
    public FrequencyList getList() {
        return (FrequencyList) super.getList();
    }

    /**
     * Represents a list of {@link Frequency} objects.
     */
    public static class FrequencyList
            extends StaticList<Frequency, FrequencyClass> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<FrequencyList> FIELD_DEFS = MetisFieldSet.newFieldSet(FrequencyList.class);

        /**
         * Construct an empty CORE frequency list.
         * @param pData the DataSet for the list
         */
        public FrequencyList(final DataSet<?> pData) {
            super(Frequency.class, pData, MoneyWiseDataType.FREQUENCY, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private FrequencyList(final FrequencyList pSource) {
            super(pSource);
        }

        @Override
        protected FrequencyList getEmptyList(final ListStyle pStyle) {
            final FrequencyList myList = new FrequencyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MetisFieldSet<FrequencyList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return Frequency.FIELD_DEFS;
        }

        @Override
        protected Class<FrequencyClass> getEnumClass() {
            return FrequencyClass.class;
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
        public Frequency addCopyItem(final DataItem pItem) {
            /* Can only clone a Frequency */
            if (!(pItem instanceof Frequency)) {
                throw new UnsupportedOperationException();
            }

            final Frequency myFreq = new Frequency(this, (Frequency) pItem);
            add(myFreq);
            return myFreq;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public Frequency addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Add a Frequency.
         * @param pFrequency the Name of the frequency
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pFrequency) throws OceanusException {
            /* Create a new Frequency */
            final Frequency myFrequency = new Frequency(this, pFrequency);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFrequency.getId())) {
                myFrequency.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myFrequency, ERROR_VALIDATION);
            }

            /* Add the Frequency to the list */
            add(myFrequency);
        }

        @Override
        public Frequency addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the frequency */
            final Frequency myFreq = new Frequency(this, pValues);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFreq.getId())) {
                myFreq.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myFreq);

            /* Return it */
            return myFreq;
        }

        @Override
        protected Frequency newItem(final FrequencyClass pClass) throws OceanusException {
            /* Create the frequency */
            final Frequency myFreq = new Frequency(this, pClass);

            /* Check that this FreqId has not been previously added */
            if (!isIdUnique(myFreq.getId())) {
                myFreq.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myFreq);

            /* Return it */
            return myFreq;
        }
    }
}
