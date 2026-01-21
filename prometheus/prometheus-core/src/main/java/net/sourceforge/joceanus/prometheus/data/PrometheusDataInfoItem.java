/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.prometheus.data;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateFormatter;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimalParser;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.prometheus.exc.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.exc.PrometheusLogicException;

import java.util.Date;

/**
 * Representation of an information extension of a DataItem.
 *
 * @author Tony Washer
 */
public abstract class PrometheusDataInfoItem
        extends PrometheusEncryptedDataItem {
    /**
     * Maximum DataLength.
     */
    public static final int DATALEN = 512;

    /**
     * Report fields.
     */
    private static final PrometheusEncryptedFieldSet<PrometheusDataInfoItem> FIELD_DEFS = PrometheusEncryptedFieldSet.newEncryptedFieldSet(PrometheusDataInfoItem.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLinkField(PrometheusDataResource.DATAINFO_TYPE);
        FIELD_DEFS.declareLinkField(PrometheusDataResource.DATAINFO_OWNER);
        FIELD_DEFS.declareEncryptedContextField(PrometheusDataResource.DATAINFO_VALUE);
        FIELD_DEFS.declareDerivedVersionedField(PrometheusDataResource.DATAINFO_LINK);
    }

    /**
     * Invalid Data Type Error.
     */
    protected static final String ERROR_BADDATATYPE = PrometheusDataResource.DATAINFO_ERROR_TYPE.getValue();

    /**
     * Invalid Data Error.
     */
    protected static final String ERROR_BADDATA = PrometheusDataResource.DATAINFO_ERROR_DATA.getValue();

    /**
     * Invalid Info Class Error.
     */
    protected static final String ERROR_BADINFOCLASS = PrometheusDataResource.DATAINFO_ERROR_CLASS.getValue();

    /**
     * Copy Constructor.
     *
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected PrometheusDataInfoItem(final PrometheusDataInfoList<?> pList,
                                     final PrometheusDataInfoItem pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     *
     * @param pList the list
     */
    protected PrometheusDataInfoItem(final PrometheusDataInfoList<?> pList) {
        /* Set standard values */
        super(pList, 0);
    }

    /**
     * Secure constructor.
     *
     * @param pList       the list
     * @param uId         the id
     * @param uKeySetId   the keySet id
     * @param uInfoTypeId the info id
     * @param uOwnerId    the owner id
     * @throws OceanusException on error
     */
    protected PrometheusDataInfoItem(final PrometheusDataInfoList<?> pList,
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
     *
     * @param pList     the list
     * @param uId       the id
     * @param pInfoType the info type
     * @param pOwner    the owner
     */
    protected PrometheusDataInfoItem(final PrometheusDataInfoList<?> pList,
                                     final Integer uId,
                                     final PrometheusStaticDataItem pInfoType,
                                     final PrometheusDataItem pOwner) {
        /* Initialise the item */
        super(pList, uId);

        /* Record the parameters */
        setValueInfoType(pInfoType);
        setValueOwner(pOwner);
    }

    /**
     * Basic constructor.
     *
     * @param pList   the list
     * @param pValues the values
     * @throws OceanusException on error
     */
    protected PrometheusDataInfoItem(final PrometheusDataInfoList<?> pList,
                                     final PrometheusDataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the InfoType */
        Object myValue = pValues.getValue(PrometheusDataResource.DATAINFO_TYPE);
        if (myValue instanceof Integer i) {
            setValueInfoType(i);
        } else if (myValue instanceof String s) {
            setValueInfoType(s);
        } else if (myValue instanceof PrometheusStaticDataItem myStatic) {
            setValueInfoType(myStatic);
        }

        /* Store the Owner */
        myValue = pValues.getValue(PrometheusDataResource.DATAINFO_OWNER);
        if (myValue instanceof Integer i) {
            setValueOwner(i);
        } else if (myValue instanceof String s) {
            setValueOwner(s);
        } else if (myValue instanceof PrometheusDataItem myItem) {
            setValueOwner(myItem);
        }
    }

    @Override
    public String toString() {
        /* Access Info Type Value */
        final PrometheusEncryptedValues myValues = getValues();
        final Object myType = myValues.getValue(PrometheusDataResource.DATAINFO_TYPE, Object.class);
        if (!(myType instanceof PrometheusStaticDataItem)) {
            return super.toString();
        }

        /* Access InfoType */
        final PrometheusStaticDataItem myInfoType = (PrometheusStaticDataItem) myType;

        /* Access class */
        return myInfoType.getName() + "=" + super.toString();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        /* Access Info Type Value */
        final PrometheusEncryptedValues myValues = getValues();
        final Object myType = myValues.getValue(PrometheusDataResource.DATAINFO_TYPE, Object.class);
        if (!(myType instanceof PrometheusStaticDataItem)) {
            return super.formatObject(pFormatter);
        }

        /* Access InfoType */
        final PrometheusStaticDataItem myInfoType = getInfoType();

        /* Access formatter */
        final OceanusDataFormatter myFormatter = getDataSet().getDataFormatter();
        final PrometheusDataInfoClass myInfoClass = (PrometheusDataInfoClass) myInfoType.getStaticClass();

        /* Switch on type of Data */
        switch (myInfoClass.getDataType()) {
            case LINK:
            case LINKPAIR:
            case LINKSET:
                return myFormatter.formatObject(getLink());
            default:
                return myFormatter.formatObject(getValue(Object.class));
        }
    }

    /**
     * Obtain InfoType.
     *
     * @return the InfoTypeId
     */
    public abstract PrometheusStaticDataItem getInfoType();

    /**
     * Obtain InfoClass.
     *
     * @return the InfoClass
     */
    public abstract PrometheusDataInfoClass getInfoClass();

    /**
     * Obtain InfoTypeId.
     *
     * @return the InfoTypeId
     */
    public Integer getInfoTypeId() {
        return getInfoType().getIndexedId();
    }

    /**
     * Obtain Owner.
     *
     * @return the Owner
     */
    public abstract PrometheusDataItem getOwner();

    /**
     * Obtain OwnerId.
     *
     * @return the OwnerId
     */
    public Integer getOwnerId() {
        return getOwner().getIndexedId();
    }

    /**
     * Obtain Link.
     *
     * @param <X>    the link type
     * @param pClass the class of the link
     * @return the Link
     */
    public <X extends PrometheusDataItem> X getLink(final Class<X> pClass) {
        return getValues().getValue(PrometheusDataResource.DATAINFO_LINK, pClass);
    }

    /**
     * Get Link name.
     *
     * @return the link name
     */
    public String getLinkName() {
        return null;
    }

    /**
     * Obtain Link.
     *
     * @return the Link
     */
    protected PrometheusDataItem getLink() {
        return getValues().getValue(PrometheusDataResource.DATAINFO_LINK, PrometheusDataItem.class);
    }

    /**
     * Obtain Value as object.
     *
     * @param <X>    the object type
     * @param pClass the object class
     * @return the Value
     */
    public <X> X getValue(final Class<X> pClass) {
        return getValues().getValue(PrometheusDataResource.DATAINFO_VALUE, pClass);
    }

    /**
     * Obtain Value as underlying object.
     *
     * @return the Value
     */
    public PrometheusEncryptedPair getField() {
        return getField(getValues());
    }

    /**
     * Obtain Encrypted Bytes.
     *
     * @return the Bytes
     */
    public byte[] getValueBytes() {
        return getValues().getEncryptedBytes(PrometheusDataResource.DATAINFO_VALUE);
    }

    /**
     * Obtain Value as object.
     *
     * @param <X>       the object type
     * @param pValueSet the valueSet
     * @param pClass    the object class
     * @return the Value
     */
    public static <X> X getValue(final PrometheusEncryptedValues pValueSet,
                                 final Class<X> pClass) {
        return pValueSet.isDeletion()
                ? null
                : pValueSet.getValue(PrometheusDataResource.DATAINFO_VALUE, pClass);
    }

    /**
     * Obtain Value as encrypted field.
     *
     * @param pValueSet the valueSet
     * @return the Value
     */
    public static PrometheusEncryptedPair getField(final PrometheusEncryptedValues pValueSet) {
        return pValueSet.isDeletion()
                ? null
                : pValueSet.getEncryptedPair(PrometheusDataResource.DATAINFO_VALUE);
    }


    /**
     * Set InfoType.
     *
     * @param pValue the info Type
     */
    protected final void setValueInfoType(final PrometheusStaticDataItem pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAINFO_TYPE, pValue);
    }

    /**
     * Set InfoType Id.
     *
     * @param pId the info Type id
     */
    protected final void setValueInfoType(final Integer pId) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAINFO_TYPE, pId);
    }

    /**
     * Set InfoType Name.
     *
     * @param pName the info Type name
     */
    protected final void setValueInfoType(final String pName) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAINFO_TYPE, pName);
    }

    /**
     * Set Owner.
     *
     * @param pValue the owner
     */
    protected final void setValueOwner(final PrometheusDataItem pValue) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAINFO_OWNER, pValue);
    }

    /**
     * Set Owner id.
     *
     * @param pId the owner id
     */
    protected final void setValueOwner(final Integer pId) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAINFO_OWNER, pId);
    }

    /**
     * Set Owner name.
     *
     * @param pName the owner name
     */
    protected final void setValueOwner(final String pName) {
        getValues().setUncheckedValue(PrometheusDataResource.DATAINFO_OWNER, pName);
    }

    /**
     * Set Value.
     *
     * @param pValue the value
     * @throws OceanusException on error
     */
    protected void setValueValue(final Object pValue) throws OceanusException {
        getValues().setDeletion(false);
        setEncryptedValue(PrometheusDataResource.DATAINFO_VALUE, pValue);
    }

    /**
     * Set value.
     *
     * @param pValue the value
     */
    protected void setValueValue(final PrometheusEncryptedPair pValue) {
        final PrometheusEncryptedValues myValues = getValues();
        myValues.setDeletion(false);
        myValues.setUncheckedValue(PrometheusDataResource.DATAINFO_VALUE, pValue);
    }

    /**
     * Set Object Value.
     *
     * @param <X>    the object type
     * @param pBytes the value
     * @param pClass the object class
     * @throws OceanusException on error
     */
    protected <X> void setValueBytes(final byte[] pBytes,
                                     final Class<X> pClass) throws OceanusException {
        setEncryptedValue(PrometheusDataResource.DATAINFO_VALUE, pBytes, pClass);
    }

    /**
     * Set link.
     *
     * @param pLink the link
     */
    protected void setValueLink(final PrometheusDataItem pLink) {
        final PrometheusEncryptedValues myValues = getValues();
        myValues.setDeletion(false);
        myValues.setUncheckedValue(PrometheusDataResource.DATAINFO_LINK, pLink);
    }

    /**
     * Set link id.
     *
     * @param pId the linkId
     */
    private void setValueLink(final Integer pId) {
        final PrometheusEncryptedValues myValues = getValues();
        myValues.setUncheckedValue(PrometheusDataResource.DATAINFO_LINK, pId);
    }

    /**
     * Set link id.
     *
     * @param pId the linkId
     */
    private void setValueLink(final Long pId) {
        final PrometheusEncryptedValues myValues = getValues();
        myValues.setUncheckedValue(PrometheusDataResource.DATAINFO_LINK, pId);
    }

    /**
     * Set link name.
     *
     * @param pName the linkName
     */
    protected void setValueLink(final String pName) {
        final PrometheusEncryptedValues myValues = getValues();
        myValues.setUncheckedValue(PrometheusDataResource.DATAINFO_LINK, pName);
    }

    /**
     * Mark deleted.
     */
    public void markDeleted() {
        /* Set deletion indication */
        getValues().setDeletion(true);
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
     *
     * @param pValue the Value
     * @throws OceanusException on error
     */
    protected void setValue(final Object pValue) throws OceanusException {
        /* Access the info Type */
        final PrometheusStaticDataItem myType = getInfoType();
        final PrometheusDataInfoClass myClass = (PrometheusDataInfoClass) myType.getStaticClass();

        /* Access the DataSet and parser */
        final PrometheusDataSet myDataSet = getDataSet();
        final OceanusDataFormatter myFormatter = myDataSet.getDataFormatter();
        final OceanusDecimalParser myParser = myFormatter.getDecimalParser();

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
            case LINKPAIR:
                bValueOK = setLinkPairValue(pValue);
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
     *
     * @param pFormatter the date formatter
     * @param pValue     the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setDateValue(final OceanusDateFormatter pFormatter,
                                 final Object pValue) throws OceanusException {
        try {
            /* Handle various forms */
            if (pValue instanceof Date d) {
                setValueValue(new OceanusDate(d));
                return true;
            } else if (pValue instanceof OceanusDate) {
                setValueValue(pValue);
                return true;
            } else if (pValue instanceof byte[] ba) {
                setValueBytes(ba, OceanusDate.class);
                return true;
            } else if (pValue instanceof String s) {
                setValueValue(pFormatter.parseDate(s));
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new PrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set Integer Value.
     *
     * @param pFormatter the data formatter
     * @param pValue     the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setIntegerValue(final OceanusDataFormatter pFormatter,
                                    final Object pValue) throws OceanusException {
        try {
            /* Handle various forms */
            if (pValue instanceof Integer) {
                setValueValue(pValue);
                return true;
            } else if (pValue instanceof byte[] ba) {
                setValueBytes(ba, Integer.class);
                return true;
            } else if (pValue instanceof String s) {
                setValueValue(pFormatter.parseValue(s, Integer.class));
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new PrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set Link Value.
     *
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setLinkValue(final Object pValue) throws OceanusException {
        try {
            /* Handle various forms */
            if (pValue instanceof Integer i) {
                setValueValue(pValue);
                setValueLink(i);
                return true;
            } else if (pValue instanceof byte[] ba) {
                setValueBytes(ba, Integer.class);
                setValueLink(getValue(Integer.class));
                return true;
            } else if (pValue instanceof String s) {
                setValueLink(s);
                return true;
            } else if (pValue instanceof PrometheusDataItem myItem) {
                setValueValue(myItem.getIndexedId());
                setValueLink(myItem);
                return true;
            }
        } catch (IllegalArgumentException e) {
            throw new PrometheusDataException(pValue, ERROR_BADDATA, e);
        }
        return false;
    }

    /**
     * Set Link Value.
     *
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setLinkPairValue(final Object pValue) throws OceanusException {
        try {
            /* Handle various forms */
            if (pValue instanceof Long l) {
                setValueValue(pValue);
                setValueLink(l);
                return true;
            } else if (pValue instanceof byte[] ba) {
                setValueBytes(ba, Long.class);
                setValueLink(getValue(Long.class));
                return true;
            } else if (pValue instanceof String s) {
                setValueLink(s);
                return true;
            } else if (pValue instanceof PrometheusDataItem myItem) {
                setValueValue(myItem.getIndexedId());
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
     *
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setStringValue(final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof String) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[] ba) {
            setValueBytes(ba, String.class);
            return true;
        }
        return false;
    }

    /**
     * Set CharArray Value.
     *
     * @param pValue the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setCharArrayValue(final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof char[]) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[] ba) {
            setValueBytes(ba, char[].class);
            return true;
        } else if (pValue instanceof String s) {
            setValueValue(s.toCharArray());
            return true;
        }
        return false;
    }

    /**
     * Set Money Value.
     *
     * @param pParser the parser
     * @param pValue  the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setMoneyValue(final OceanusDecimalParser pParser,
                                  final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof OceanusMoney) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[] ba) {
            setValueBytes(ba, OceanusMoney.class);
            return true;
        } else if (pValue instanceof String s) {
            setValueValue(pParser.parseMoneyValue(s));
            return true;
        }
        return false;
    }

    /**
     * Set Rate Value.
     *
     * @param pParser the parser
     * @param pValue  the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setRateValue(final OceanusDecimalParser pParser,
                                 final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof OceanusRate) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[] ba) {
            setValueBytes(ba, OceanusRate.class);
            return true;
        } else if (pValue instanceof String s) {
            setValueValue(pParser.parseRateValue(s));
            return true;
        }
        return false;
    }

    /**
     * Set Ratio Value.
     *
     * @param pParser the parser
     * @param pValue  the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setRatioValue(final OceanusDecimalParser pParser,
                                  final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof OceanusRatio) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[] ba) {
            setValueBytes(ba, OceanusRatio.class);
            return true;
        } else if (pValue instanceof String s) {
            setValueValue(pParser.parseRatioValue(s));
            return true;
        }
        return false;
    }

    /**
     * Set Units Value.
     *
     * @param pParser the parser
     * @param pValue  the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setUnitsValue(final OceanusDecimalParser pParser,
                                  final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof OceanusUnits) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[] ba) {
            setValueBytes(ba, OceanusUnits.class);
            return true;
        } else if (pValue instanceof String s) {
            setValueValue(pParser.parseUnitsValue(s));
            return true;
        }
        return false;
    }

    /**
     * Set Price Value.
     *
     * @param pParser the parser
     * @param pValue  the Value
     * @return is value valid true/false
     * @throws OceanusException on error
     */
    private boolean setPriceValue(final OceanusDecimalParser pParser,
                                  final Object pValue) throws OceanusException {
        /* Handle various forms */
        if (pValue instanceof OceanusPrice) {
            setValueValue(pValue);
            return true;
        } else if (pValue instanceof byte[] ba) {
            setValueBytes(ba, OceanusPrice.class);
            return true;
        } else if (pValue instanceof String s) {
            setValueValue(pParser.parsePriceValue(s));
            return true;
        }
        return false;
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
    public int compareValues(final PrometheusDataItem pThat) {
        /* Compare the owner and infoType */
        final PrometheusDataInfoItem myThat = (PrometheusDataInfoItem) pThat;
        int iDiff = getOwner().compareTo(myThat.getOwner());
        if (iDiff == 0) {
            iDiff = getInfoType().compareTo(myThat.getInfoType());
        }
        return iDiff;
    }

    /**
     * List class for DataInfo.
     *
     * @param <T> the DataType
     */
    public abstract static class PrometheusDataInfoList<T extends PrometheusDataInfoItem>
            extends PrometheusEncryptedList<T> {
        /*
         * Report fields.
         */
        static {
            MetisFieldSet.newFieldSet(PrometheusDataInfoList.class);
        }

        /**
         * Construct a generic data info list.
         *
         * @param pBaseClass the class of the underlying object
         * @param pData      the dataSet
         * @param pItemType  the list type
         * @param pStyle     the style of the list
         */
        protected PrometheusDataInfoList(final Class<T> pBaseClass,
                                         final PrometheusDataSet pData,
                                         final MetisListKey pItemType,
                                         final PrometheusListStyle pStyle) {
            super(pBaseClass, pData, pItemType, pStyle);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        protected PrometheusDataInfoList(final PrometheusDataInfoList<T> pSource) {
            super(pSource);
        }

        @Override
        public boolean includeDataXML() {
            return false;
        }

        @Override
        protected abstract PrometheusDataInfoList<T> getEmptyList(PrometheusListStyle pStyle);

        /**
         * Add new item to the list.
         *
         * @param pOwner    the owner
         * @param pInfoType the information
         * @return the new info item
         */
        protected abstract T addNewItem(PrometheusDataItem pOwner,
                                        PrometheusStaticDataItem pInfoType);

        /**
         * Add an info Item to the list.
         *
         * @param pId        the Id
         * @param pOwner     the owner
         * @param pInfoClass the infoClass
         * @param pValue     the value
         * @throws OceanusException on error
         */
        public abstract void addInfoItem(Integer pId,
                                         PrometheusDataItem pOwner,
                                         PrometheusDataInfoClass pInfoClass,
                                         Object pValue) throws OceanusException;

        @Override
        public void updateMaps() {
            /* No activity, managed by owner */
        }

        @Override
        protected PrometheusDataMapItem allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}
