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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class representing an account that can be part of a transaction.
 * @param <T> the actual dataType
 */
public abstract class AssetBase<T extends AssetBase<T>>
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<T> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AssetBase.class.getName());

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AssetBase.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataName"));

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDesc"));

    /**
     * isClosed Field Id.
     */
    public static final JDataField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataClosed"));

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_NAME.equals(pField)) {
            return true;
        }
        if (FIELD_DESC.equals(pField)) {
            return getDesc() != null;
        }
        if (FIELD_CLOSED.equals(pField)) {
            return isClosed();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Name.
     * @return the name
     */
    public String getName() {
        return getName(getValueSet());
    }

    /**
     * Obtain Encrypted name.
     * @return the bytes
     */
    public byte[] getNameBytes() {
        return getNameBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Name Field.
     * @return the Field
     */
    private EncryptedString getNameField() {
        return getNameField(getValueSet());
    }

    /**
     * Obtain Description.
     * @return the description
     */
    public String getDesc() {
        return getDesc(getValueSet());
    }

    /**
     * Obtain Encrypted description.
     * @return the bytes
     */
    public byte[] getDescBytes() {
        return getDescBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Description Field.
     * @return the Field
     */
    private EncryptedString getDescField() {
        return getDescField(getValueSet());
    }

    /**
     * Is the asset closed?
     * @return true/false
     */
    public Boolean isClosed() {
        return isClosed(getValueSet());
    }

    /**
     * Obtain Name.
     * @param pValueSet the valueSet
     * @return the Name
     */
    public static String getName(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted Name.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getNameBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Obtain Encrypted name field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedString getNameField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, EncryptedString.class);
    }

    /**
     * Obtain Description.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getDescBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Obtain Encrypted description field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedString getDescField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    /**
     * Is the asset closed?
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isClosed(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLOSED, Boolean.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueName(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueName(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueDesc(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueDesc(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
    }

    /**
     * Set closed indication.
     * @param pValue the value
     */
    private void setValueClosed(final Boolean pValue) {
        getValueSet().setValue(FIELD_CLOSED, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public AssetBaseList<T> getList() {
        return (AssetBaseList<T>) super.getList();
    }

    @Override
    public boolean isLocked() {
        return isClosed();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pAsset The Asset to copy
     */
    protected AssetBase(final AssetBaseList<T> pList,
                        final AssetBase<T> pAsset) {
        /* Set standard values */
        super(pList, pAsset);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    protected AssetBase(final AssetBaseList<T> pList,
                        final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the name */
            Object myValue = pValues.getValue(FIELD_NAME);
            if (myValue instanceof String) {
                setValueName((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueName((byte[]) myValue);
            }

            /* Store the Description */
            myValue = pValues.getValue(FIELD_DESC);
            if (myValue instanceof String) {
                setValueDesc((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueDesc((byte[]) myValue);
            }

            /* Store the closed flag */
            myValue = pValues.getValue(FIELD_CLOSED);
            if (myValue instanceof Boolean) {
                setValueClosed((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueClosed(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public AssetBase(final AssetBaseList<T> pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final T pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the names */
        int iDiff = Difference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        ValueSet myValues = getValueSet();

        /* Adjust Closed */
        Object myClosed = myValues.getValue(FIELD_CLOSED);
        if (myClosed == null) {
            setValueClosed(Boolean.FALSE);
        }
    }

    /**
     * Set a new name.
     * @param pName the new name
     * @throws JOceanusException on error
     */
    public void setName(final String pName) throws JOceanusException {
        setValueName(pName);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JOceanusException on error
     */
    public void setDescription(final String pDesc) throws JOceanusException {
        setValueDesc(pDesc);
    }

    /**
     * Set a new closed indication.
     * @param isClosed the new closed indication
     */
    public void setClosed(final Boolean isClosed) {
        setValueClosed(isClosed);
    }

    /**
     * Validate the account.
     */
    @Override
    public void validate() {
        String myName = getName();
        String myDesc = getDesc();
        AssetBaseList<?> myList = getList();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* The name must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            if (myList.countInstances(myName) > 1) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_DESC);
        }
    }

    /**
     * Update base asset from an edited asset.
     * @param pAsset the edited asset
     */
    protected void applyBasicChanges(final AssetBase<T> pAsset) {
        /* Update the name if required */
        if (!Difference.isEqual(getName(), pAsset.getName())) {
            setValueName(pAsset.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), pAsset.getDesc())) {
            setValueDesc(pAsset.getDescField());
        }

        /* Update the closed indication if required */
        if (!Difference.isEqual(isClosed(), pAsset.isClosed())) {
            setValueClosed(pAsset.isClosed());
        }
    }

    /**
     * The Asset List class.
     * @param <T> the dataType
     */
    public abstract static class AssetBaseList<T extends AssetBase<T>>
            extends EncryptedList<T, MoneyWiseDataType> {
        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         * @param pItemType the item type
         */
        protected AssetBaseList(final MoneyWiseData pData,
                                final Class<T> pClass,
                                final MoneyWiseDataType pItemType) {
            super(pClass, pData, pItemType, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected AssetBaseList(final AssetBaseList<T> pSource) {
            super(pSource);
        }

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The Item if present (or null)
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Search for a particular item by Name.
         * @param pName Name of item
         * @return The Item if present (or null)
         */
        public T findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<T> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                T myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }
    }
}
