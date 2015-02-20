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

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Frequency data type.
 * @author Tony Washer
 */
public class Frequency
        extends StaticData<Frequency, FrequencyClass, MoneyWiseDataType> {
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
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

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
     * @throws JOceanusException on error
     */
    private Frequency(final FrequencyList pList,
                      final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Frequency with
     * @param pClass Class of Frequency
     * @throws JOceanusException on error
     */
    private Frequency(final FrequencyList pList,
                      final FrequencyClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private Frequency(final FrequencyList pList,
                      final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Frequency class of the Frequency.
     * @return the class
     */
    public FrequencyClass getFrequency() {
        return super.getStaticClass();
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
            extends StaticList<Frequency, FrequencyClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, StaticList.FIELD_DEFS);

        /**
         * Construct an empty CORE frequency list.
         * @param pData the DataSet for the list
         */
        public FrequencyList(final DataSet<?, ?> pData) {
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
            FrequencyList myList = new FrequencyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

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
        public Frequency addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a Frequency */
            if (!(pItem instanceof Frequency)) {
                throw new UnsupportedOperationException();
            }

            Frequency myFreq = new Frequency(this, (Frequency) pItem);
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
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pFrequency) throws JOceanusException {
            /* Create a new Frequency */
            Frequency myFrequency = new Frequency(this, pFrequency);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFrequency.getId())) {
                myFrequency.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myFrequency, ERROR_VALIDATION);
            }

            /* Add the Frequency to the list */
            append(myFrequency);
        }

        @Override
        public Frequency addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the frequency */
            Frequency myFreq = new Frequency(this, pValues);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFreq.getId())) {
                myFreq.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myFreq);

            /* Return it */
            return myFreq;
        }

        @Override
        protected Frequency newItem(final FrequencyClass pClass) throws JOceanusException {
            /* Create the frequency */
            Frequency myFreq = new Frequency(this, pClass);

            /* Check that this FreqId has not been previously added */
            if (!isIdUnique(myFreq.getId())) {
                myFreq.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myFreq);

            /* Return it */
            return myFreq;
        }
    }
}
