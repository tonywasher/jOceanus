/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.joceanus.jdatamodels.data;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jgordianknot.EncryptedValueSet;

/**
 * Representation of an information extension of a DataItem.
 * @author Tony Washer
 * @param <T> the data type
 * @param <O> the Owner DataItem that is extended by this item
 * @param <I> the Info Type that applies to this item
 * @param <E> the Info Class that applies to this item
 */
public abstract class DataInfo<T extends DataInfo<T, O, I, E>, O extends DataItem, I extends StaticData<I, E>, E extends Enum<E> & DataInfoClass>
        extends EncryptedItem
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
    public static final JDataField FIELD_INFOTYPE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataType"));

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
     * Invalid Info Class Error.
     */
    protected static final String ERROR_BADINFOCLASS = NLS_BUNDLE.getString("ErrorInfoClass");

    @Override
    public boolean skipField(final JDataField pField) {
        if ((FIELD_LINK.equals(pField))
            && !getInfoClass().isLink()) {
            return true;
        }
        if ((FIELD_VALUE.equals(pField))
            && getInfoClass().isLink()) {
            return true;
        }
        return super.skipField(pField);
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
    public abstract E getInfoClass();

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
     * @return the Account
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
    public static <X extends DataItem> X getLink(final ValueSet pValueSet,
                                                 final Class<X> pClass) {
        return pValueSet.isDeletion()
                ? null
                : pValueSet.getValue(FIELD_LINK, pClass);
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
    private void setValueInfoType(final Integer pId) {
        getValueSet().setValue(FIELD_INFOTYPE, pId);
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
    private void setValueOwner(final Integer pId) {
        getValueSet().setValue(FIELD_OWNER, pId);
    }

    /**
     * Set Value.
     * @param pValue the value
     * @throws JDataException on error
     */
    protected void setValueValue(final Object pValue) throws JDataException {
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
     * @throws JDataException on error
     */
    protected <X> void setValueBytes(final byte[] pBytes,
                                     final Class<X> pClass) throws JDataException {
        setEncryptedValue(FIELD_VALUE, pBytes, pClass);
    }

    /**
     * Set link.
     * @param pLink the link
     */
    protected void setValueLink(final DataItem pLink) {
        ValueSet myValues = getValueSet();
        myValues.setDeletion(false);
        myValues.setValue(FIELD_LINK, pLink);
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected DataInfo(final DataInfoList<T, O, I, E> pList,
                       final DataInfo<T, O, I, E> pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    protected DataInfo(final DataInfoList<T, O, I, E> pList) {
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
     * @throws JDataException on error
     */
    protected DataInfo(final DataInfoList<T, O, I, E> pList,
                       final Integer uId,
                       final Integer uControlId,
                       final Integer uInfoTypeId,
                       final Integer uOwnerId) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Record the Ids */
        setValueInfoType(uInfoTypeId);
        setValueOwner(uOwnerId);

        /* Store the controlId */
        setControlKey(uControlId);
    }

    /**
     * Set value.
     * @param pValue the value
     * @throws JDataException on error
     */
    protected abstract void setValue(final Object pValue) throws JDataException;

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
    protected DataInfo(final DataInfoList<T, O, I, E> pList,
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
     * List class for DataInfo.
     * @param <T> the DataType
     * @param <O> the Owner Type
     * @param <I> the DataInfo Type
     * @param <E> the Info Class that applies to this item
     */
    public abstract static class DataInfoList<T extends DataInfo<T, O, I, E> & Comparable<T>, O extends DataItem, I extends StaticData<I, E>, E extends Enum<E> & DataInfoClass>
            extends EncryptedList<T> {
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
         * @param pStyle the style of the list
         */
        public DataInfoList(final Class<T> pBaseClass,
                            final DataSet<?, ?> pData,
                            final ListStyle pStyle) {
            super(pBaseClass, pData, pStyle);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected DataInfoList(final DataInfoList<T, O, I, E> pSource) {
            super(pSource);
        }

        @Override
        protected abstract DataInfoList<T, O, I, E> getEmptyList(final ListStyle pStyle);

        /**
         * Add new item to the list.
         * @param pOwner the owner
         * @param pInfoType the information
         * @return the new info item
         */
        protected abstract T addNewItem(final O pOwner,
                                        final I pInfoType);

        /**
         * Add a DataInfo to the list.
         * @param uId the Id of the info
         * @param pOwner the owner of the info
         * @param pInfoClass the Class of the data info type
         * @param pValue the value of the data info
         * @throws JDataException on error
         */
        public abstract void addOpenItem(final Integer uId,
                                         final O pOwner,
                                         final E pInfoClass,
                                         final Object pValue) throws JDataException;
    }
}
