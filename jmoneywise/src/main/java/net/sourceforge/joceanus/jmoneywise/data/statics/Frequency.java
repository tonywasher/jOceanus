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
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
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
    public static final String OBJECT_NAME = Frequency.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = "Frequencies";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

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
     * Open Constructor.
     * @param pList The list to associate the Frequency with
     * @param pId ID of Frequency
     * @param isEnabled is the frequency enabled
     * @param pOrder the sort order
     * @param pName Name of Frequency
     * @param pDesc Description of Frequency
     * @throws JOceanusException on error
     */
    private Frequency(final FrequencyList pList,
                      final Integer pId,
                      final Boolean isEnabled,
                      final Integer pOrder,
                      final String pName,
                      final String pDesc) throws JOceanusException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the Frequency with
     * @param pId ID of Frequency
     * @param pControlId the control id of the new item
     * @param isEnabled is the frequency enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of Frequency
     * @param pDesc Encrypted Description of TaxRegime
     * @throws JOceanusException on error
     */
    private Frequency(final FrequencyList pList,
                      final Integer pId,
                      final Integer pControlId,
                      final Boolean isEnabled,
                      final Integer pOrder,
                      final byte[] pName,
                      final byte[] pDesc) throws JOceanusException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link Frequency} objects.
     */
    public static class FrequencyList
            extends StaticList<Frequency, FrequencyClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(FrequencyList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<FrequencyClass> getEnumClass() {
            return FrequencyClass.class;
        }

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
        public FrequencyList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (FrequencyList) super.cloneList(pDataSet);
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
                return null;
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

            /* Check that this Frequency has not been previously added */
            if (findItemByName(pFrequency) != null) {
                myFrequency.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myFrequency, ERROR_VALIDATION);
            }

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFrequency.getId())) {
                myFrequency.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myFrequency, ERROR_VALIDATION);
            }

            /* Add the Frequency to the list */
            append(myFrequency);

            /* Validate the Frequency */
            myFrequency.validate();

            /* Handle validation failure */
            if (myFrequency.hasErrors()) {
                throw new JMoneyWiseDataException(myFrequency, ERROR_VALIDATION);
            }
        }

        /**
         * Add a Frequency to the list.
         * @param pId ID of Frequency
         * @param isEnabled is the frequency enabled
         * @param pOrder the sort order
         * @param pFrequency the Name of the frequency
         * @param pDesc the Description of the frequency
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pFrequency,
                                final String pDesc) throws JOceanusException {
            /* Create a new Frequency */
            Frequency myFreq = new Frequency(this, pId, isEnabled, pOrder, pFrequency, pDesc);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(pId)) {
                myFreq.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }

            /* Add the Frequency to the list */
            append(myFreq);

            /* Validate the Frequency */
            myFreq.validate();

            /* Handle validation failure */
            if (myFreq.hasErrors()) {
                throw new JMoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }
        }

        /**
         * Add a Frequency.
         * @param pId the Id of the frequency
         * @param pControlId the control id of the new item
         * @param isEnabled is the frequency enabled
         * @param pOrder the sort order
         * @param pFrequency the Encrypted Name of the frequency
         * @param pDesc the Encrypted Description of the frequency
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pFrequency,
                                  final byte[] pDesc) throws JOceanusException {
            /* Create a new Frequency */
            Frequency myFreq = new Frequency(this, pId, pControlId, isEnabled, pOrder, pFrequency, pDesc);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(pId)) {
                myFreq.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }

            /* Add the Frequency to the list */
            append(myFreq);

            /* Validate the Frequency */
            myFreq.validate();

            /* Handle validation failure */
            if (myFreq.hasErrors()) {
                throw new JMoneyWiseDataException(myFreq, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
            /* Loop through all elements */
            for (FrequencyClass myClass : FrequencyClass.values()) {
                /* Create new element */
                Frequency myFreq = new Frequency(this, myClass);

                /* Add the Frequency to the list */
                append(myFreq);

                /* Validate the Frequency */
                myFreq.validate();

                /* Handle validation failure */
                if (myFreq.hasErrors()) {
                    throw new JMoneyWiseDataException(myFreq, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
