/*******************************************************************************
 * JDataModels: Data models
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
package net.sourceforge.JDataModels.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JDecimal.JDilution;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JDecimal.JPrice;
import net.sourceforge.JDecimal.JRate;
import net.sourceforge.JDecimal.JUnits;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedCharArray;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDateDay;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDilution;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedInteger;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedMoney;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedRate;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedUnits;
import net.sourceforge.JGordianKnot.EncryptedValueSet;

/**
 * Representation of an information extension of a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 */
public abstract class DataInfo<T extends DataInfo<T, O, I>, O extends DataItem, I extends StaticData<?, ?>>
        extends EncryptedItem implements Comparable<T> {
    /**
     * Maximum DataLength.
     */
    public static final int DATALEN = 512;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(DataInfo.class.getSimpleName(),
            EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * InfoType Field Id.
     */
    public static final JDataField FIELD_INFOTYPE = FIELD_DEFS.declareEqualityValueField("InfoType");

    /**
     * Owner Field Id.
     */
    public static final JDataField FIELD_OWNER = FIELD_DEFS.declareEqualityValueField("Owner");

    /**
     * Value Field Id.
     */
    public static final JDataField FIELD_VALUE = FIELD_DEFS.declareEqualityValueField("Value");

    /**
     * Obtain InfoTypeId.
     * @return the InfoTypeId
     */
    public Integer getInfoTypeId() {
        return getInfoType(getValueSet(), DataItem.class).getId();
    }

    /**
     * Obtain OwnerId.
     * @return the OwnerId
     */
    public Integer getOwnerId() {
        return getOwner(getValueSet(), DataItem.class).getId();
    }

    /**
     * Obtain Encrypted Bytes.
     * @return the Bytes
     */
    public byte[] getValueBytes() {
        return getValueBytes(getValueSet());
    }

    /**
     * Obtain InfoType.
     * @param <X> the infoType
     * @param pValueSet the valueSet
     * @param pClass the class of the infoType
     * @return the Info types
     */
    protected static <X> X getInfoType(final ValueSet pValueSet,
                                       final Class<X> pClass) {
        return pValueSet.getValue(FIELD_INFOTYPE, pClass);
    }

    /**
     * Obtain Owner.
     * @param <X> the infoType
     * @param pValueSet the valueSet
     * @param pClass the class of the owner
     * @return the Owner
     */
    protected static <X> X getOwner(final ValueSet pValueSet,
                                    final Class<X> pClass) {
        return pValueSet.getValue(FIELD_OWNER, pClass);
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static JDateDay getDate(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedDateDay)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, JDateDay.class);
    }

    /**
     * Obtain CharArray.
     * @param pValueSet the valueSet
     * @return the CharArray
     */
    public static char[] getCharArray(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedCharArray)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, char[].class);
    }

    /**
     * Obtain Integer.
     * @param pValueSet the valueSet
     * @return the Integer
     */
    public static Integer getInteger(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedInteger)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, Integer.class);
    }

    /**
     * Obtain Money.
     * @param pValueSet the valueSet
     * @return the Money
     */
    public static JMoney getMoney(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedMoney)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, JMoney.class);
    }

    /**
     * Obtain Rate.
     * @param pValueSet the valueSet
     * @return the Rate
     */
    public static JRate getRate(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedRate)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, JRate.class);
    }

    /**
     * Obtain Units.
     * @param pValueSet the valueSet
     * @return the Units
     */
    public static JUnits getUnits(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedUnits)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, JUnits.class);
    }

    /**
     * Obtain Dilution.
     * @param pValueSet the valueSet
     * @return the Dilution
     */
    public static JDilution getDilution(final EncryptedValueSet pValueSet) {
        Object myField = pValueSet.getValue(FIELD_VALUE, Object.class);
        if (!(myField instanceof EncryptedDilution)) {
            return null;
        }
        return pValueSet.getEncryptedFieldValue(FIELD_VALUE, JDilution.class);
    }

    /**
     * Obtain Encrypted Bytes.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getValueBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_VALUE);
    }

    /**
     * Set InfoType.
     * @param pValue the info Type
     */
    protected void setValueInfoType(final I pValue) {
        getValueSet().setValue(FIELD_INFOTYPE, pValue);
    }

    /**
     * Set InfoType Id.
     * @param pId the info Type id
     */
    private void setValueInfoType(final Integer pId) {
        getValueSet().setValue(FIELD_INFOTYPE, pId);
    }

    /**
     * Set Owner.
     * @param pValue the owner
     */
    protected void setValueOwner(final O pValue) {
        getValueSet().setValue(FIELD_OWNER, pValue);
    }

    /**
     * Set Owner id.
     * @param pId the owner id
     */
    private void setValueOwner(final Integer pId) {
        getValueSet().setValue(FIELD_OWNER, pId);
    }

    /**
     * Set Money.
     * @param pValue the money
     * @throws JDataException on error
     */
    protected void setValueMoney(final JMoney pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Rate.
     * @param pValue the rate
     * @throws JDataException on error
     */
    protected void setValueRate(final JRate pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Units.
     * @param pValue the units
     * @throws JDataException on error
     */
    protected void setValueUnits(final JUnits pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Price.
     * @param pValue the price
     * @throws JDataException on error
     */
    protected void setValuePrice(final JPrice pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Dilution.
     * @param pValue the dilution
     * @throws JDataException on error
     */
    protected void setValueDilution(final JDilution pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Integer.
     * @param pValue the integer
     * @throws JDataException on error
     */
    protected void setValueInteger(final Integer pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Date.
     * @param pValue the date
     * @throws JDataException on error
     */
    protected void setValueDateDay(final JDateDay pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set charArray.
     * @param pValue the charArray
     * @throws JDataException on error
     */
    protected void setValueCharArray(final char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Money Value.
     * @param pBytes the money
     * @throws JDataException on error
     */
    protected void setValueMoney(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JMoney.class);
    }

    /**
     * Set Rate Value.
     * @param pBytes the rate
     * @throws JDataException on error
     */
    protected void setValueRate(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JRate.class);
    }

    /**
     * Set Units Value.
     * @param pBytes the units
     * @throws JDataException on error
     */
    protected void setValueUnits(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JUnits.class);
    }

    /**
     * Set Price Value.
     * @param pBytes the price
     * @throws JDataException on error
     */
    protected void setValuePrice(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JPrice.class);
    }

    /**
     * Set Dilution Value.
     * @param pBytes the dilution
     * @throws JDataException on error
     */
    protected void setValueDilution(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JDilution.class);
    }

    /**
     * Set Integer Value.
     * @param pBytes the integer
     * @throws JDataException on error
     */
    protected void setValueInteger(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, Integer.class);
    }

    /**
     * Set Date Value.
     * @param pBytes the Date
     * @throws JDataException on error
     */
    protected void setValueDateDay(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, JDateDay.class);
    }

    /**
     * Set charArray Value.
     * @param pBytes the charArray
     * @throws JDataException on error
     */
    protected void setValueCharArray(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, char[].class);
    }

    /**
     * Construct a copy of a DataInfo.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected DataInfo(final DataInfoList<?, O, I> pList,
                       final DataInfo<T, O, I> pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Encrypted constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control id
     * @param uInfoTypeId the info id
     * @param uOwnerId the owner id
     * @throws JDataException on error
     */
    protected DataInfo(final DataInfoList<?, O, I> pList,
                       final int uId,
                       final int uControlId,
                       final int uInfoTypeId,
                       final int uOwnerId) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Record the Ids */
        setValueInfoType(uInfoTypeId);
        setValueOwner(uOwnerId);

        /* Store the controlId */
        setControlKey(uControlId);
    }

    /**
     * Plain Text constructor.
     * @param pList the list
     * @param uId the id
     * @param pInfoType the info type
     * @param pOwner the owner
     */
    protected DataInfo(final DataInfoList<?, O, I> pList,
                       final int uId,
                       final I pInfoType,
                       final O pOwner) {
        /* Initialise the item */
        super(pList, uId);

        /* Record the parameters */
        setValueInfoType(pInfoType);
        setValueOwner(pOwner);
    }

    /**
     * List class for DataInfo.
     * @param <T> the DataType
     * @param <O> the Owner Type
     * @param <I> the DataInfo Type
     */
    protected abstract static class DataInfoList<T extends DataInfo<T, O, I> & Comparable<T>, O extends DataItem, I extends StaticData<?, ?>>
            extends EncryptedList<T> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(DataInfoList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * Construct a generic data info list.
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pStyle the style of the list
         */
        public DataInfoList(final Class<T> pBaseClass,
                            final DataSet<?> pData,
                            final ListStyle pStyle) {
            super(pBaseClass, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected DataInfoList(final DataInfoList<T, O, I> pSource) {
            super(pSource);
        }
    }
}
