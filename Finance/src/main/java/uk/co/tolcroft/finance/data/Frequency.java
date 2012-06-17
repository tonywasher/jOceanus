/*******************************************************************************
 * JFinanceApp: Finance Application
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
import uk.co.tolcroft.finance.data.StaticClass.FreqClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.StaticData;

/**
 * Frequency data type.
 * @author Tony Washer
 */
public class Frequency extends StaticData<Frequency, FreqClass> {
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
    public FreqClass getFrequency() {
        return super.getStaticClass();
    }

    @Override
    public Frequency getBase() {
        return (Frequency) super.getBase();
    }

    /**
     * Construct a copy of a Frequency.
     * @param pList The list to associate the Frequency with
     * @param pFrequency The frequency to copy
     */
    protected Frequency(final FrequencyList pList,
                        final Frequency pFrequency) {
        super(pList, pFrequency);
    }

    /**
     * Construct a standard Frequency on load.
     * @param pList The list to associate the Frequency with
     * @param sName Name of Frequency
     * @throws JDataException on error
     */
    private Frequency(final FrequencyList pList,
                      final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Construct a standard frequency on load.
     * @param pList The list to associate the Frequency with
     * @param uId ID of Frequency
     * @param isEnabled is the frequency enabled
     * @param uOrder the sort order
     * @param pName Name of Frequency
     * @param pDesc Description of Frequency
     * @throws JDataException on error
     */
    private Frequency(final FrequencyList pList,
                      final int uId,
                      final boolean isEnabled,
                      final int uOrder,
                      final String pName,
                      final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Construct a standard Frequency on load.
     * @param pList The list to associate the Frequency with
     * @param uId ID of Frequency
     * @param uControlId the control id of the new item
     * @param isEnabled is the frequency enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of Frequency
     * @param pDesc Encrypted Description of TaxRegime
     * @throws JDataException on error
     */
    private Frequency(final FrequencyList pList,
                      final int uId,
                      final int uControlId,
                      final boolean isEnabled,
                      final int uOrder,
                      final byte[] pName,
                      final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link Frequency} objects.
     */
    public static class FrequencyList extends StaticList<FrequencyList, Frequency, FreqClass> {
        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<FreqClass> getEnumClass() {
            return FreqClass.class;
        }

        /**
         * Construct an empty CORE frequency list.
         * @param pData the DataSet for the list
         */
        protected FrequencyList(final FinanceData pData) {
            super(FrequencyList.class, Frequency.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private FrequencyList(final FrequencyList pSource) {
            super(pSource);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private FrequencyList getExtractList(final ListStyle pStyle) {
            /* Build an empty Extract List */
            FrequencyList myList = new FrequencyList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        @Override
        public FrequencyList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public FrequencyList getEditList() {
            return getExtractList(ListStyle.EDIT);
        }

        @Override
        public FrequencyList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public FrequencyList getDeepCopy(final DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            FrequencyList myList = new FrequencyList(this);
            myList.setData(pDataSet);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        @Override
        protected FrequencyList getDifferences(final FrequencyList pOld) {
            /* Build an empty Difference List */
            FrequencyList myList = new FrequencyList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
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
        public Frequency addNewItem(final DataItem pItem) {
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
            return null;
        }

        /**
         * Add a Frequency.
         * @param pFrequency the Name of the frequency
         * @throws JDataException on error
         */
        public void addItem(final String pFrequency) throws JDataException {
            Frequency myFrequency;

            /* Create a new Frequency */
            myFrequency = new Frequency(this, pFrequency);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFrequency.getId())) {
                throw new JDataException(ExceptionClass.DATA, myFrequency, "Duplicate FrequencyId");
            }

            /* Check that this Frequency has not been previously added */
            if (findItemByName(pFrequency) != null) {
                throw new JDataException(ExceptionClass.DATA, myFrequency, "Duplicate Frequency");
            }

            /* Add the Frequency to the list */
            add(myFrequency);
        }

        /**
         * Add a Frequency to the list.
         * @param uId ID of Frequency
         * @param isEnabled is the frequency enabled
         * @param uOrder the sort order
         * @param pFrequency the Name of the frequency
         * @param pDesc the Description of the frequency
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final boolean isEnabled,
                            final int uOrder,
                            final String pFrequency,
                            final String pDesc) throws JDataException {
            Frequency myFreq;

            /* Create a new Frequency */
            myFreq = new Frequency(this, uId, isEnabled, uOrder, pFrequency, pDesc);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(myFreq.getId())) {
                throw new JDataException(ExceptionClass.DATA, myFreq, "Duplicate FrequencyId");
            }

            /* Add the Frequency to the list */
            add(myFreq);

            /* Validate the Frequency */
            myFreq.validate();

            /* Handle validation failure */
            if (myFreq.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myFreq, "Failed validation");
            }
        }

        /**
         * Add a Frequency.
         * @param uId the Id of the frequency
         * @param uControlId the control id of the new item
         * @param isEnabled is the frequency enabled
         * @param uOrder the sort order
         * @param pFrequency the Encrypted Name of the frequency
         * @param pDesc the Encrypted Description of the frequency
         * @throws JDataException on error
         */
        public void addItem(final int uId,
                            final int uControlId,
                            final boolean isEnabled,
                            final int uOrder,
                            final byte[] pFrequency,
                            final byte[] pDesc) throws JDataException {
            Frequency myFreq;

            /* Create a new Frequency */
            myFreq = new Frequency(this, uId, uControlId, isEnabled, uOrder, pFrequency, pDesc);

            /* Check that this FrequencyId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myFreq, "Duplicate FrequencyId");
            }

            /* Add the Frequency to the list */
            addAtEnd(myFreq);

            /* Validate the Frequency */
            myFreq.validate();

            /* Handle validation failure */
            if (myFreq.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myFreq, "Failed validation");
            }
        }
    }
}
