/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

import java.util.Date;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.PrometheusLogicException;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusEncryptedPair;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Representation of an information extension of a DataItem.
 * @author Tony Washer
 */
public abstract class DataInfoItem
        extends EncryptedItem {
    /**
     * Maximum DataLength.
     */
    public static final int DATALEN = 512;

    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(PrometheusDataResourceX.DATAINFO_NAME.getValue(), EncryptedItem.FIELD_DEFS);

    /**
     * InfoType Field Id.
     */
    public static final MetisLetheField FIELD_INFOTYPE = FIELD_DEFS.declareComparisonValueField(PrometheusDataResourceX.DATAINFO_TYPE.getValue(), MetisDataType.LINK);

    /**
     * Owner Field Id.
     */
    public static final MetisLetheField FIELD_OWNER = FIELD_DEFS.declareComparisonValueField(PrometheusDataResourceX.DATAINFO_OWNER.getValue(), MetisDataType.LINK);

    /**
     * Value Field Id.
     */
    public static final MetisLetheField FIELD_VALUE = FIELD_DEFS.declareEqualityEncryptedField(PrometheusDataResourceX.DATAINFO_VALUE.getValue(), MetisDataType.CONTEXT);

    /**
     * Link Field Id.
     */
    public static final MetisLetheField FIELD_LINK = FIELD_DEFS.declareDerivedValueField(PrometheusDataResourceX.DATAINFO_LINK.getValue());

    /**
     * Invalid Data Type Error.
     */
    protected static final String ERROR_BADDATATYPE = PrometheusDataResourceX.DATAINFO_ERROR_TYPE.getValue();

    /**
     * Invalid Data Error.
     */
    protected static final String ERROR_BADDATA = PrometheusDataResourceX.DATAINFO_ERROR_DATA.getValue();

    /**
     * Invalid Info Class Error.
     */
    protected static final String ERROR_BADINFOCLASS = PrometheusDataResourceX.DATAINFO_ERROR_CLASS.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected DataInfoItem(final DataInfoList<?> pList,
                           final DataInfoItem pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    protected DataInfoItem(final DataInfoList<?> pList) {
        /* Set standard values */
        super(pList, 0);
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param uId the id
     * @param uKeySetId the keySet id
     * @param uInfoTypeId the info id
     * @param uOwnerId the owner id
     * @throws OceanusException on error
     */
    protected DataInfoItem(final DataInfoList<?> pList,
                           final Integer uId,
                           final Integer uKeySetId,
                           final Integer uInfoTypeId,
                           final Integer uOwnerId) throws OceanusException {
        /* Initialise the item */
        super(pList, uId);

        /* Record the Ids */
        setValueInfoType(uInfoTypeId);
        setValueOwner(uOwnerId);

        /* Store the keySetId */
        setDataKeySet(uKeySetId);
    }

    /**
     * Basic constructor.
     * @param pList the list
     * @param uId the id
     * @param pInfoType the info type
     * @param pOwner the owner
     */
    protected DataInfoItem(final DataInfoList<?> pList,
                           final Integer uId,
                           final StaticDataItem pInfoType,
                           final DataItem pOwner) {
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
     * @throws OceanusException on error
     */
    protected DataInfoItem(final DataInfoList<?> pList,
                           final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the InfoType */
        Object myValue = pValues.getValue(FIELD_INFOTYPE);
        if (myValue instanceof Integer) {
            setValueInfoType((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueInfoType((String) myValue);
        } else if (myValue instanceof StaticDataItem) {
            setValueInfoType((StaticDataItem) myValue);
        }

        /* Store the Owner */
        myValue = pValues.getValue(FIELD_OWNER);
        if (myValue instanceof Integer) {
            setValueOwner((Integer) myValue);
        } else if (myValue instanceof String) {
            setValueOwner((String) myValue);
        } else if (myValue instanceof DataItem) {
            setValueOwner((DataItem) myValue);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean skipField(final MetisLetheField pField) {
        if ((FIELD_LINK.equals(pField)) && !isInfoLink()) {
            return true;
        }
        if ((FIELD_VALUE.equals(pField)) && isInfoLink()) {
            return true;
        }
        return super.skipField(pField);
    }

    /**
     * Is this item a link as far as skipField is concerned?
     * @return true/false
     */
    private boolean isInfoLink() {
        /* Access Info Class Value */
        final EncryptedValueSet myValues = getValueSet();
        final Object myType = myValues.getValue(FIELD_INFOTYPE, Object.class);
        if (!(myType instanceof StaticDataItem)) {
            return false;
        }

        /* Access class */
        return getInfoClass().isLink();
    }

    @Override
    public String toString() {
        /* Access Info Type Value */
        final EncryptedValueSet myValues = getValueSet();
        final Object myType = myValues.getValue(FIELD_INFOTYPE, Object.class);
        if (!(myType instanceof StaticDataItem)) {
            return super.toString();
        }

        /* Access InfoType */
        final StaticDataItem myInfoType = (StaticDataItem) myType;

        /* Access class */
        return myInfoType.getName() + "=" + super.toString();
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        /* Access Info Type Value */
        final EncryptedValueSet myValues = getValueSet();
        final Object myType = myValues.getValue(FIELD_INFOTYPE, Object.class);
        if (!(myType instanceof StaticDataItem)) {
            return super.formatObject(pFormatter);
        }

        /* Access InfoType */
        final StaticDataItem myInfoType = getInfoType();

        /* Access formatter */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();
        final DataInfoClass myInfoClass = (DataInfoClass) myInfoType.getStaticClass();

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
    public abstract StaticDataItem getInfoType();

    /**
     * Obtain InfoClass.
     * @return the InfoClass
     */
    public abstract DataInfoClass getInfoClass();

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
    public abstract DataItem getOwner();

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
    public <X extends DataItem> X getLink(final Class<X> pClass) {
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
    protected DataItem getLink() {
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
    public PrometheusEncryptedPair getField() {
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
    protected static <X> X getInfoType(final MetisValueSet pValueSet,
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
    protected static <X> X getOwner(final MetisValueSet pValueSet,
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
    public static PrometheusEncryptedPair getField(final EncryptedValueSet pValueSet) {
        return pValueSet.isDeletion()
                                      ? null
                                      : pValueSet.getValue(FIELD_VALUE, PrometheusEncryptedPair.class);
    }

    /**
     * Obtain Associated Link.
     * @param <X> the link type
     * @param pValueSet the valueSet
     * @param pClass the class of the link
     * @return the Link
     */
    public static <X extends DataItem> X getLink(final MetisValueSet pValueSet,
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
    protected static DataItem getLink(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_LINK, DataItem.class);
    }

    /**
     * Set InfoType.
     * @param pValue the info Type
     */
    protected final void setValueInfoType(final StaticDataItem pValue) {
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
    protected final void setValueOwner(final DataItem pValue) {
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
     * @throws OceanusException on error
     */
    protected void setValueValue(final Object pValue) throws OceanusException {
        getValueSet().setDeletion(false);
        setEncryptedValue(FIELD_VALUE, pValue);
    }

    /**
     * Set value.
     * @param pValue the value
     */
    protected void setValueValue(final PrometheusEncryptedPair pValue) {
        final MetisValueSet myValues = getValueSet();
        myValues.setDeletion(false);
        myValues.setValue(FIELD_VALUE, pValue);
    }

    /**
     * Set Object Value.
     * @param <X> the object type
     * @param pBytes the value
     * @param pClass the object class
     * @throws OceanusException on error
     */
    protected <X> void setValueBytes(final byte[] pBytes,
                                     final Class<X> pClass) throws OceanusException {
        setEncryptedValue(FIELD_VALUE, pBytes, pClass);
    }

    /**
     * Set link.
     * @param pLink the link
     */
    protected void setValueLink(final DataItem pLink) {
        final MetisValueSet myValues = getValueSet();
        myValues.setDeletion(false);
        myValues.setValue(FIELD_LINK, pLink);
    }

    /**
     * Set link id.
     * @param pId the linkId
     */
    private void setValueLink(final Integer pId) {
        final MetisValueSet myValues = getValueSet();
        myValues.setValue(FIELD_LINK, pId);
    }

    /**
     * Set link name.
     * @param pName the linkName
     */
    protected void setValueLink(final String pName) {
        final MetisValueSet myValues = getValueSet();
        myValues.setValue(FIELD_LINK, pName);
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
     * Set Value.
     * @param pValue the Value
     * @throws OceanusException on error
     */
    protected void setValue(final Object pValue) throws OceanusException {
        /* Access the info Type */
        final StaticDataItem myType = getInfoType();
        final DataInfoClass myClass = (DataInfoClass) myType.getStaticClass();

        /* Access the DataSet and parser */
        final DataSet myDataSet = getDataSet();
        final TethysUIDataFormatter myFormatter = myDataSet.getDataFormatter();
        final TethysDecimalParser myParser = myFormatter.getDecimalParser();

        /* Switch on Info Class */
        boolean bValueOK = false;
        switch (myClass.getDataType()) {
            case DATE:
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
            case RATIO:
                bValueOK = setRatioValue(myParser, pValue);
                break;
            default:
                break;
        }

        /* Reject invalid value */
        if (!bValueOK) {
            throw new PrometheusLogicException(this, ERROR_BADDATATYPE);
        }
    }

    /**
     * Set Date Value.
     * @param pFormatter the date formatter
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setDateValue(final TethysDateFormatter pFormatter,
                                 final Object pValue) throws OceanusException {
        try {
            /* Handle various forms */
            if (pValue instanceof Date) {
                setValueValue(new TethysDate((Date) pValue));
                return true;
            } else if (pValue instanceof TethysDate) {
                setValueValue(pValue);
                return true;
            } else if (pValue instanceof byte[]) {
                setValueBytes((byte[]) pValue, TethysDate.class);
                return true;
            } else if (pValue instanceof String) {
                setValueValue(pFormatter.parseDate((String) pValue));
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new PrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set Integer Value.
     * @param pFormatter the data formatter
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setIntegerValue(final TethysUIDataFormatter pFormatter,
                                    final Object pValue) throws OceanusException {
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
            throw new PrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set Link Value.
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setLinkValue(final Object pValue) throws OceanusException {
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
            } else if (pValue instanceof DataItem) {
                final DataItem myItem = (DataItem) pValue;
                setValueValue(myItem.getId());
                setValueLink(myItem);
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new PrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set String Value.
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setStringValue(final Object pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    private boolean setCharArrayValue(final Object pValue) throws OceanusException {
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
     * @throws OceanusException on error
     */
    private boolean setMoneyValue(final TethysDecimalParser pParser,
                                  final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof TethysMoney) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, TethysMoney.class);
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
     * @throws OceanusException on error
     */
    private boolean setRateValue(final TethysDecimalParser pParser,
                                 final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof TethysRate) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, TethysRate.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parseRateValue((String) pValue));
            return true;
        }
        return false;
    }

    /**
     * Set Ratio Value.
     * @param pParser the parser
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setRatioValue(final TethysDecimalParser pParser,
                                  final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof TethysRatio) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, TethysRatio.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parseRatioValue((String) pValue));
            return true;
        }
        return false;
    }

    /**
     * Set Units Value.
     * @param pParser the parser
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setUnitsValue(final TethysDecimalParser pParser,
                                  final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof TethysUnits) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, TethysUnits.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parseUnitsValue((String) pValue));
            return true;
        }
        return false;
    }

    /**
     * Set Price Value.
     * @param pParser the parser
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setPriceValue(final TethysDecimalParser pParser,
                                  final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof TethysPrice) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[]) {
            setValueBytes((byte[]) pValue, TethysPrice.class);
            return true;
        } else if (pValue instanceof String) {
            setValueValue(pParser.parsePriceValue((String) pValue));
            return true;
        }
        return false;
    }

    @Override
    public void validate() {
        final StaticDataItem myType = getInfoType();
        final DataItem myOwner = getOwner();
        final Object myValue = getValue(Object.class);

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

    @Override
    public void rewindToVersion(final int pVersion) {
        /* Do the actual rewind */
        super.rewindToVersion(pVersion);

        /* If this is a linkSet type */
        if (getInfoClass().isLinkSet()) {
            /* Note the rewind */
            rewindInfoLinkSet();
        }
    }

    /**
     * rewind any infoSet links.
     */
    public void rewindInfoLinkSet() {
    }

    @Override
    public int compareValues(final DataItem pThat) {
        /* Compare the owner and infoType */
        final DataInfoItem myThat = (DataInfoItem) pThat;
        int iDiff = getOwner().compareTo(myThat.getOwner());
        if (iDiff == 0) {
            iDiff = getInfoType().compareTo(myThat.getInfoType());
        }
        return iDiff;
    }

    /**
     * List class for DataInfo.
     * @param <T> the DataType
     */
    public abstract static class DataInfoList<T extends DataInfoItem>
            extends EncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(DataInfoList.class);
        }

        /**
         * Construct a generic data info list.
         * @param pBaseClass the class of the underlying object
         * @param pData the dataSet
         * @param pItemType the list type
         * @param pStyle the style of the list
         */
        protected DataInfoList(final Class<T> pBaseClass,
                               final DataSet pData,
                               final PrometheusListKeyX pItemType,
                               final ListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected DataInfoList(final DataInfoList<T> pSource) {
            super(pSource);
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected abstract DataInfoList<T> getEmptyList(ListStyle pStyle);

        /**
         * Add new item to the list.
         * @param pOwner the owner
         * @param pInfoType the information
         * @return the new info item
         */
        protected abstract T addNewItem(DataItem pOwner,
                                        StaticDataItem pInfoType);

        /**
         * Add an info Item to the list.
         * @param pId the Id
         * @param pOwner the owner
         * @param pInfoClass the infoClass
         * @param pValue the value
         * @throws OceanusException on error
         */
        public abstract void addInfoItem(Integer pId,
                                         DataItem pOwner,
                                         DataInfoClass pInfoClass,
                                         Object pValue) throws OceanusException;

        @Override
        public void prepareForAnalysis() {
            /* No activity, managed by owner */
        }

        @Override
        protected DataMapItem allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}
