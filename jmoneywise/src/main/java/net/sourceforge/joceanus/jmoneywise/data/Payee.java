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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Payee class.
 */
public class Payee
        extends AssetBase<Payee> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.PAYEE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.PAYEE.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * PayeeType Field Id.
     */
    public static final JDataField FIELD_PAYEETYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.PAYEETYPE.getItemName());

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_PAYEETYPE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Payee Type.
     * @return the type
     */
    public PayeeType getPayeeType() {
        return getPayeeType(getValueSet());
    }

    /**
     * Obtain PayeeTypeId.
     * @return the categoryTypeId
     */
    public Integer getPayeeTypeId() {
        PayeeType myType = getPayeeType();
        return (myType == null)
                               ? null
                               : myType.getId();
    }

    /**
     * Obtain PayeeTypeName.
     * @return the payeeTypeName
     */
    public String getPayeeTypeName() {
        PayeeType myType = getPayeeType();
        return (myType == null)
                               ? null
                               : myType.getName();
    }

    /**
     * Obtain PayeeTypeClass.
     * @return the payeeTypeClass
     */
    public PayeeTypeClass getPayeeTypeClass() {
        PayeeType myType = getPayeeType();
        return (myType == null)
                               ? null
                               : myType.getPayeeClass();
    }

    /**
     * Obtain PayeeType.
     * @param pValueSet the valueSet
     * @return the PayeeType
     */
    public static PayeeType getPayeeType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PAYEETYPE, PayeeType.class);
    }

    /**
     * Set payee type value.
     * @param pValue the value
     */
    private void setValueType(final PayeeType pValue) {
        getValueSet().setValue(FIELD_PAYEETYPE, pValue);
    }

    /**
     * Set payee type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_PAYEETYPE, pValue);
    }

    /**
     * Set payee type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValueSet().setValue(FIELD_PAYEETYPE, pValue);
    }

    @Override
    public Payee getBase() {
        return (Payee) super.getBase();
    }

    @Override
    public PayeeList getList() {
        return (PayeeList) super.getList();
    }

    /**
     * Is this payee the required class.
     * @param pClass the required payee class.
     * @return true/false
     */
    public boolean isPayeeClass(final PayeeTypeClass pClass) {
        /* Check for match */
        return getPayeeTypeClass() == pClass;
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPayee The Payee to copy
     */
    protected Payee(final PayeeList pList,
                    final Payee pPayee) {
        /* Set standard values */
        super(pList, pPayee);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private Payee(final PayeeList pList,
                  final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the PayeeType */
        Object myValue = pValues.getValue(FIELD_PAYEETYPE);
        if (myValue instanceof Integer) {
            setValueType((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueType((String) myValue);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Payee(final PayeeList pList) {
        super(pList);
    }

    @Override
    public int compareTo(final Payee pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the payee type */
        int iDiff = Difference.compareObject(getPayeeType(), pThat.getPayeeType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying base */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Base details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_PAYEETYPE, myData.getPayeeTypes());
    }

    /**
     * Set a new payee type.
     * @param pType the new type
     */
    public void setPayeeType(final PayeeType pType) {
        setValueType(pType);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the payee type */
        getPayeeType().touchItem(this);
    }

    @Override
    public void validate() {
        PayeeList myList = getList();
        PayeeType myPayeeType = getPayeeType();

        /* Validate base components */
        super.validate();

        /* PayeeType must be non-null */
        if (myPayeeType == null) {
            addError(ERROR_MISSING, FIELD_PAYEETYPE);
        } else {
            /* Access the class */
            PayeeTypeClass myClass = myPayeeType.getPayeeClass();

            /* PayeeType must be enabled */
            if (!myPayeeType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_PAYEETYPE);
            }

            /* If the PayeeType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                int myCount = myList.countInstances(myClass);
                if (myCount > 1) {
                    addError(ERROR_MULT, FIELD_PAYEETYPE);
                }
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base payee from an edited payee.
     * @param pPayee the edited payee
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pPayee) {
        /* Can only update from a payee */
        if (!(pPayee instanceof Payee)) {
            return false;
        }
        Payee myPayee = (Payee) pPayee;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myPayee);

        /* Update the category type if required */
        if (!Difference.isEqual(getPayeeType(), myPayee.getPayeeType())) {
            setValueType(myPayee.getPayeeType());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Payee List class.
     */
    public static class PayeeList
            extends AssetBaseList<Payee> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

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
            return Payee.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Payee list.
         * @param pData the DataSet for the list
         */
        public PayeeList(final MoneyWiseData pData) {
            super(pData, Payee.class, MoneyWiseDataType.PAYEE);
        }

        @Override
        protected PayeeList getEmptyList(final ListStyle pStyle) {
            PayeeList myList = new PayeeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PayeeList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (PayeeList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected PayeeList(final PayeeList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public PayeeList deriveEditList() {
            /* Build an empty List */
            PayeeList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the payees */
            Iterator<Payee> myIterator = iterator();
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();

                /* Ignore deleted payees */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked payee and add it to the list */
                Payee myPayee = new Payee(myList, myCurr);
                myList.append(myPayee);
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pPayee item
         * @return the newly added item
         */
        @Override
        public Payee addCopyItem(final DataItem<?> pPayee) {
            /* Can only clone a Payee */
            if (!(pPayee instanceof Payee)) {
                throw new UnsupportedOperationException();
            }

            Payee myPayee = new Payee(this, (Payee) pPayee);
            add(myPayee);
            return myPayee;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public Payee addNewItem() {
            Payee myPayee = new Payee(this);
            add(myPayee);
            return myPayee;
        }

        /**
         * Count the instances of a class.
         * @param pClass the event category class
         * @return The # of instances of the class
         */
        protected int countInstances(final PayeeTypeClass pClass) {
            /* Access the iterator */
            Iterator<Payee> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();
                if (pClass == myCurr.getPayeeTypeClass()) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Obtain the first payee for the specified class.
         * @param pClass the payee class
         * @return the payee
         */
        public Payee getSingularClass(final PayeeTypeClass pClass) {
            /* Access the iterator */
            Iterator<Payee> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Payee myCurr = myIterator.next();
                if (myCurr.getPayeeTypeClass() == pClass) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        @Override
        public Payee addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the payee */
            Payee myPayee = new Payee(this, pValues);

            /* Check that this PayeeId has not been previously added */
            if (!isIdUnique(myPayee.getId())) {
                myPayee.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPayee, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPayee);

            /* Return it */
            return myPayee;
        }
    }
}
