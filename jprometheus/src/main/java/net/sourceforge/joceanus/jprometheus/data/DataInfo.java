/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.data;

import java.util.Date;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.JPrometheusLogicException;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Representation of an information extension of a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 * @param <S> the Info Class that applies to this item
 * @param <E> the data type enum class
 */
public abstract class DataInfo<T extends DataInfo<T, O, I, S, E>, O extends DataItem<E>, I extends StaticData<I, S, E>, S extends Enum<S> & DataInfoClass, E extends Enum<E>>
        extends EncryptedItem<E>
        implements Comparable<T> {
    /**
     * Maximum DataLength.
     */
    public static final int DATALEN = 512;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataInfo.class.getName());

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * InfoType Field Id.
     */
    public static final JDataField FIELD_INFOTYPE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataInfoType"));

    /**
     * Owner Field Id.
     */
    public static final JDataField FIELD_OWNER = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataOwner"));

    /**
     * Value Field Id.
     */
    public static final JDataField FIELD_VALUE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataValue"));

    /**
     * Link Field Id.
     */
    public static final JDataField FIELD_LINK = FIELD_DEFS.declareDerivedValueField(NLS_BUNDLE.getString("DataLink"));

    /**
     * Invalid Data Type Error.
     */
    protected static final String ERROR_BADDATATYPE = NLS_BUNDLE.getString("ErrorDataType");

    /**
     * Invalid Data Error.
     */
    protected static final String ERROR_BADDATA = NLS_BUNDLE.getString("ErrorData");

    /**
     * Invalid Info Class Error.
     */
    protected static final String ERROR_BADINFOCLASS = NLS_BUNDLE.getString("ErrorInfoClass");

    @Override
    public boolean skipField(final JDataField pField) {
        if ((FIELD_LINK.equals(pField)) && !getInfoClass().isLink()) {
            return true;
        }
        if ((FIELD_VALUE.equals(pField)) && getInfoClass().isLink()) {
            return true;
        }
        return super.skipField(pField);
    }

    @Override
    public String toString() {
        /* Access Info Type Value */
        EncryptedValueSet myValues = getValueSet();
        Object myType = myValues.getValue(FIELD_INFOTYPE, Object.class);
        if (!(myType instanceof StaticData)) {
            return super.formatObject();
        }

        /* Access InfoType */
        I myInfoType = (I) myType;

        /* Access class */
        return myInfoType.getName() + "=" + formatObject();
    }

    @Override
    public String formatObject() {
        /* Access Info Type Value */
        EncryptedValueSet myValues = getValueSet();
        Object myType = myValues.getValue(FIELD_INFOTYPE, Object.class);
        if (!(myType instanceof StaticData)) {
            return super.formatObject();
        }

        /* Access InfoType */
        I myInfoType = (I) myType;

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();
        S myInfoClass = myInfoType.getStaticClass();

        /* Switch on type of Data */
        switch (myInfoClass.getDataType()) {
            case LINK:
            case LINKSET:
                return myFormatter.formatObject(getLink());
            default:
                return myFormatter.formatObject(getValue(Object.class));
        }
    }

    /**
     * Obtain InfoType.
     * @return the InfoTypeId
     */
    public abstract I getInfoType();

    /**
     * Obtain InfoClass.
     * @return the InfoClass
     */
    public abstract S getInfoClass();

    /**
     * Obtain InfoTypeId.
     * @return the InfoTypeId
     */
    public Integer getInfoTypeId() {
        return getInfoType(getValueSet(), DataItem.class).getId();
    }

    /**
     * Obtain Owner.
     * @return the Owner
     */
    public abstract O getOwner();

    /**
     * Obtain OwnerId.
     * @return the OwnerId
     */
    public Integer getOwnerId() {
        return getOwner(getValueSet(), DataItem.class).getId();
    }

    /**
     * Obtain Link.
     * @param <X> the link type
     * @param pClass the class of the link
     * @return the Link
     */
    public <X extends DataItem<?>> X getLink(final Class<X> pClass) {
        return getLink(getValueSet(), pClass);
    }

    /**
     * Get Link name.
     * @return the link name
     */
    public String getLinkName() {
        return null;
    }

    /**
     * Obtain Link.
     * @return the Link
     */
    protected Object getLink() {
        return getLink(getValueSet());
    }

    /**
     * Obtain Value as object.
     * @param <X> the object type
     * @param pClass the object class
     * @return the Value
     */
    public <X> X getValue(final Class<X> pClass) {
        return getValue(getValueSet(), pClass);
    }

    /**
     * Obtain Value as underlying object.
     * @return the Value
     */
    public EncryptedField<?> getField() {
        return getField(getValueSet());
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
     * Obtain Encrypted Bytes.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getValueBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_VALUE);
    }

    /**
     * Obtain Value as object.
     * @param <X> the object type
     * @param pValueSet the valueSet
     * @param pClass the object class
     * @return the Value
     */
    public static <X> X getValue(final EncryptedValueSet pValueSet,
                                 final Class<X> pClass) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getEncryptedFieldValue(FIELD_VALUE, pClass);
    }

    /**
     * Obtain Value as encrypted field.
     * @param pValueSet the valueSet
     * @return the Value
     */
    public static EncryptedField<?> getField(final EncryptedValueSet pValueSet) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_VALUE, EncryptedField.class);
    }

    /**
     * Obtain Associated Link.
     * @param <X> the link type
     * @param pValueSet the valueSet
     * @param pClass the class of the link
     * @return the Link
     */
    public static <X extends DataItem<?>> X getLink(final ValueSet pValueSet,
                                                    final Class<X> pClass) {
        return pValueSet.isDeletion()
                                     ? null
                                     : pValueSet.getValue(FIELD_LINK, pClass);
    }

    /**
     * Obtain Associated Link.
     * @param pValueSet the valueSet
     * @return the Link
     */
    protected static Object getLink(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_LINK, Object.class);
    }

    /**
     * Set InfoType.
     * @param pValue the info Type
     */
    protected final void setValueInfoType(final I pValue) {
        getValueSet().setValue(FIELD_INFOTYPE, pValue);
    }

    /**
     * Set InfoType Id.
     * @param pId the info Type id
     */
    protected final void setValueInfoType(final Integer pId) {
        getValueSet().setValue(FIELD_INFOTYPE, pId);
    }

    /**
     * Set InfoType Name.
     * @param pName the info Type name
     */
    protected final void setValueInfoType(final String pName) {
        getValueSet().setValue(FIELD_INFOTYPE, pName);
    }

    /**
     * Set Owner.
     * @param pValue the owner
     */
    protected final void setValueOwner(final O pValue) {
        getValueSet().setValue(FIELD_OWNER, pValue);
    }

    /**
     * Set Owner id.
     * @param pId the owner id
     */
    protected final void setValueOwner(final Integer pId) {
        getValueSet().setValue(FIELD_OWNER, pId);
    }

    /**
     * Set Owner name.
     * @param pName the owner name
     */
    protected final void setValueOwner(final String pName) {
        getValueSet().setValue(FIELD_OWNER, pName);
    }

    /**
     * Set Value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    protected void setValueValue(final Object pValue) throws JOceanusException {
        getValueSet().setDeletion(false);
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set value.
     * @param pValue the value
     */
    protected void setValueValue(final EncryptedField<?> pValue) {
        ValueSet myValues = getValueSet();
        myValues.setDeletion(false);
        myValues.setValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Object Value.
     * @param <X> the object type
     * @param pBytes the value
     * @param pClass the object class
     * @throws JOceanusException on error
     */
    protected <X> void setValueBytes(final byte[] pBytes,
                                     final Class<X> pClass) throws JOceanusException {
        setEncryptedValue(FIELD_VALUE, pBytes, pClass);
    }

    /**
     * Set link.
     * @param pLink the link
     */
    protected void setValueLink(final DataItem<?> pLink) {
        ValueSet myValues = getValueSet();
        myValues.setDeletion(false);
        myValues.setValue(FIELD_LINK, pLink);
    }

    /**
     * Set link id.
     * @param pId the linkId
     */
    private void setValueLink(final Integer pId) {
        ValueSet myValues = getValueSet();
        myValues.setValue(FIELD_LINK, pId);
    }

    /**
     * Set link name.
     * @param pName the linkName
     */
    protected void setValueLink(final String pName) {
        ValueSet myValues = getValueSet();
        myValues.setValue(FIELD_LINK, pName);
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected DataInfo(final DataInfoList<T, O, I, S, E> pList,
                       final DataInfo<T, O, I, S, E> pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    protected DataInfo(final DataInfoList<T, O, I, S, E> pList) {
        /* Set standard values */
        super(pList, 0);
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control id
     * @param uInfoTypeId the info id
     * @param uOwnerId the owner id
     * @throws JOceanusException on error
     */
    protected DataInfo(final DataInfoList<T, O, I, S, E> pList,
                       final Integer uId,
                       final Integer uControlId,
                       final Integer uInfoTypeId,
                       final Integer uOwnerId) throws JOceanusException {
        /* Initialise the item */
        super(pList, uId);

        /* Record the Ids */
        setValueInfoType(uInfoTypeId);
        setValueOwner(uOwnerId);

        /* Store the controlId */
        setControlKey(uControlId);
    }

    /**
     * Mark deleted.
     */
    public void markDeleted() {
        /* Set deletion indication */
        getValueSet().setDeletion(true);
    }

    @Override
    public void touchUnderlyingItems() {
        /* Pass call on */
        super.touchUnderlyingItems();

        /* Touch the info type */
        getInfoType().touchItem(this);
    }

    /**
     * Basic constructor.
     * @param pList the list
     * @param uId the id
     * @param pInfoType the info type
     * @param pOwner the owner
     */
    protected DataInfo(final DataInfoList<T, O, I, S, E> pList,
                       final Integer uId,
                       final I pInfoType,
                       final O pOwner) {
        /* Initialise the item */
        super(pList, uId);

        /* Record the parameters */
        setValueInfoType(pInfoType);
        setValueOwner(pOwner);
    }

    /**
     * Basic constructor.
     * @param pList the list
     * @param pValues the values
     * @throws JOceanusException on error
     */
    protected DataInfo(final DataInfoList<T, O, I, S, E> pList,
                       final DataValues<E> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the InfoType */
        Object myValue = pValues.getValue(FIELD_INFOTYPE);
        if (myValue instanceof Integer) {
            setValueInfoType((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueInfoType((String) myValue);
        } else if (myValue instanceof StaticData) {
            setValueInfoType((I) myValue);
        }

        /* Store the Owner */
        myValue = pValues.getValue(FIELD_OWNER);
        if (myValue instanceof Integer) {
            setValueOwner((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueOwner((String) myValue);
        } else if (myValue instanceof DataItem) {
            setValueOwner((O) myValue);
        }
    }

    /**
     * Set Value.
     * @param pValue the Value
     * @throws JOceanusException on error
     */
    protected void setValue(final Object pValue) throws JOceanusException {
        /* Access the info Type */
        I myType = getInfoType();
        S myClass = myType.getStaticClass();

        /* Access the DataSet and parser */
        DataSet<?, ?> myDataSet = getDataSet();
        JDataFormatter myFormatter = myDataSet.getDataFormatter();
        JDecimalParser myParser = myFormatter.getDecimalParser();

        /* Switch on Info Class */
        boolean bValueOK = false;
        switch (myClass.getDataType()) {
            case DATEDAY:
                bValueOK = setDateValue(myFormatter.getDateFormatter(), pValue);
                break;
            case INTEGER:
                bValueOK = setIntegerValue(myFormatter, pValue);
                break;
            case LINK:
            case LINKSET:
                bValueOK = setLinkValue(pValue);
                break;
            case STRING:
                bValueOK = setStringValue(pValue);
                break;
            case CHARARRAY:
                bValueOK = setCharArrayValue(pValue);
                break;
            case MONEY:
                bValueOK = setMoneyValue(myParser, pValue);
                break;
            case RATE:
                bValueOK = setRateValue(myParser, pValue);
                break;
            case UNITS:
                bValueOK = setUnitsValue(myParser, pValue);
                break;
            case PRICE:
                bValueOK = setPriceValue(myParser, pValue);
                break;
            case DILUTION:
                bValueOK = setDilutionValue(myParser, pValue);
                break;
            default:
                break;
        }

        /* Reject invalid value */
        if (!bValueOK) {
            throw new JPrometheusLogicException(this, ERROR_BADDATATYPE);
        }
    }

    /**
     * Set Date Value.
     * @param pFormatter the date formatter
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setDateValue(final JDateDayFormatter pFormatter,
                                 final Object pValue) throws JOceanusException {
        try {
            /* Handle various forms */
            if (pValue instanceof Date) {
                setValueValue(new JDateDay((Date) pValue));
                return true;
            } else if (pValue instanceof JDateDay) {
                setValueValue(pValue);
                return true;
            } else if (pValue instanceof byte[]) {
                setValueBytes((byte[]) pValue, JDateDay.class);
                return true;
            } else if (pValue instanceof String) {
                setValueValue(pFormatter.parseDateDay((String) pValue));
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new JPrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set Integer Value.
     * @param pFormatter the data formatter
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setIntegerValue(final JDataFormatter pFormatter,
                                    final Object pValue) throws JOceanusException {
        try {
            /* Handle various forms */
            if (pValue instanceof Integer) {
                setValueValue(pValue);
                return true;
            } else if (pValue instanceof byte[]) {
                setValueBytes((byte[]) pValue, Integer.class);
                return true;
            } else if (pValue instanceof String) {
                setValueValue(pFormatter.parseValue((String) pValue, Integer.class));
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new JPrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set Link Value.
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setLinkValue(final Object pValue) throws JOceanusException {
        try {
            /* Handle various forms */
            if (pValue instanceof Integer) {
                setValueValue(pValue);
                setValueLink((Integer) pValue);
                return true;
            } else if (pValue instanceof byte[]) {
                setValueBytes((byte[]) pValue, Integer.class);
                setValueLink(getValue(Integer.class));
                return true;
            } else if (pValue instanceof String) {
                setValueLink((String) pValue);
                return true;
            } else if (pValue instanceof DataItem<?>) {
                DataItem<?> myItem = (DataItem<?>) pValue;
                setValueValue(myItem.getId());
                setValueLink(myItem);
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new JPrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set String Value.
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setStringValue(final Object pValue) throws JOceanusException {
        /* Handle various forms */
        if (pValue instanceof String) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, String.class);
            return true;
        }
        return false;
    }

    /**
     * Set CharArray Value.
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setCharArrayValue(final Object pValue) throws JOceanusException {
        /* Handle various forms */
        if (pValue instanceof char[]) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, char[].class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(((String) pValue).toCharArray());
            return true;
        }
        return false;
    }

    /**
     * Set Money Value.
     * @param pParser the parser
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setMoneyValue(final JDecimalParser pParser,
                                  final Object pValue) throws JOceanusException {
        /* Handle various forms */
        if (pValue instanceof JMoney) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, JMoney.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parseMoneyValue((String) pValue));
            return true;
        }
        return false;
    }

    /**
     * Set Rate Value.
     * @param pParser the parser
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setRateValue(final JDecimalParser pParser,
                                 final Object pValue) throws JOceanusException {
        /* Handle various forms */
        if (pValue instanceof JRate) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, JRate.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parseRateValue((String) pValue));
            return true;
        }
        return false;
    }

    /**
     * Set Rate Value.
     * @param pParser the parser
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setUnitsValue(final JDecimalParser pParser,
                                  final Object pValue) throws JOceanusException {
        /* Handle various forms */
        if (pValue instanceof JUnits) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, JUnits.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parseUnitsValue((String) pValue));
            return true;
        }
        return false;
    }

    /**
     * Set Rate Value.
     * @param pParser the parser
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setPriceValue(final JDecimalParser pParser,
                                  final Object pValue) throws JOceanusException {
        /* Handle various forms */
        if (pValue instanceof JPrice) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, JPrice.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parsePriceValue((String) pValue));
            return true;
        }
        return false;
    }

    /**
     * Set Dilution Value.
     * @param pParser the parser
     * @param pValue the Value
     * @return is value valid true/false
     * @throws JOceanusException on error
     */
    private boolean setDilutionValue(final JDecimalParser pParser,
                                     final Object pValue) throws JOceanusException {
        /* Handle various forms */
        if (pValue instanceof JDilution) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, JDilution.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parseDilutionValue((String) pValue));
            return true;
        }
        return false;
    }

    @Override
    public void validate() {
        I myType = getInfoType();
        O myOwner = getOwner();
        Object myValue = getValue(Object.class);

        /* InfoType must be non-null */
        if (myType == null) {
            addError(ERROR_MISSING, FIELD_INFOTYPE);
        }

        /* Owner must be non-null */
        if (myOwner == null) {
            addError(ERROR_MISSING, FIELD_OWNER);
        }

        /* Value must be non-null */
        if (myValue == null) {
            addError(ERROR_MISSING, FIELD_VALUE);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * List class for DataInfo.
     * @param <T> the DataType
     * @param <O> the Owner Type
     * @param <I> the DataInfo Type
     * @param <S> the Info Class that applies to this item
     * @param <E> the data type enum class
     */
    public abstract static class DataInfoList<T extends DataInfo<T, O, I, S, E> & Comparable<T>, O extends DataItem<E>, I extends StaticData<I, S, E>, S extends Enum<S> & DataInfoClass, E extends Enum<E>>
            extends EncryptedList<T, E> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        /**
         * Construct a generic data info list.
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pItemType the list type
         * @param pStyle the style of the list
         */
        public DataInfoList(final Class<T> pBaseClass,
                            final DataSet<?, ?> pData,
                            final E pItemType,
                            final ListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected DataInfoList(final DataInfoList<T, O, I, S, E> pSource) {
            super(pSource);
        }

        @Override
        protected abstract DataInfoList<T, O, I, S, E> getEmptyList(final ListStyle pStyle);

        /**
         * Add new item to the list.
         * @param pOwner the owner
         * @param pInfoType the information
         * @return the new info item
         */
        protected abstract T addNewItem(final O pOwner,
                                        final I pInfoType);

        /**
         * Add an info Item to the list.
         * @param pId the Id
         * @param pOwner the owner
         * @param pInfoClass the infoClass
         * @param pValue the value
         * @throws JOceanusException on error
         */
        public abstract void addInfoItem(final Integer pId,
                                         final O pOwner,
                                         final S pInfoClass,
                                         final Object pValue) throws JOceanusException;
    }
}
