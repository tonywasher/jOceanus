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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.DataState;
import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.EditState;
import net.sourceforge.joceanus.jmetis.data.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.data.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security class.
 */
public class Security
        extends AssetBase<Security>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.SECURITY.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.SECURITY.getListName();

    /**
     * Symbol length.
     */
    public static final int SYMBOLLEN = 30;

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * SecurityType Field Id.
     */
    public static final JDataField FIELD_SECTYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.SECURITYTYPE.getItemName());

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.ASSET_PARENT.getValue());

    /**
     * Symbol Field Id.
     */
    public static final JDataField FIELD_SYMBOL = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.SECURITY_SYMBOL.getValue());

    /**
     * Currency Field Id.
     */
    public static final JDataField FIELD_CURRENCY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CURRENCY.getItemName());

    /**
     * SecurityInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseDataResource.SECURITY_NEWACCOUNT.getValue();

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * SecurityInfoSet.
     */
    private final SecurityInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pSecurity The Security to copy
     */
    protected Security(final SecurityList pList,
                       final Security pSecurity) {
        /* Set standard values */
        super(pList, pSecurity);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new SecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
                theInfoSet.cloneDataInfoSet(pSecurity.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new SecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
                hasInfoSet = true;
                useInfoSet = false;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                useInfoSet = false;
                break;
        }
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private Security(final SecurityList pList,
                     final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Store the SecurityType */
            Object myValue = pValues.getValue(FIELD_SECTYPE);
            if (myValue instanceof Integer) {
                setValueType((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueType((String) myValue);
            }

            /* Store the Parent */
            myValue = pValues.getValue(FIELD_PARENT);
            if (myValue instanceof Integer) {
                setValueParent((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueParent((String) myValue);
            }

            /* Store the Symbol */
            myValue = pValues.getValue(FIELD_SYMBOL);
            if (myValue instanceof String) {
                setValueSymbol((String) myValue);
            } else if (myValue instanceof byte[]) {
                setValueSymbol((byte[]) myValue);
            }

            /* Store the Currency */
            myValue = pValues.getValue(FIELD_CURRENCY);
            if (myValue instanceof Integer) {
                setValueCurrency((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCurrency((String) myValue);
            } else if (myValue instanceof AssetCurrency) {
                setValueCurrency((AssetCurrency) myValue);
            }

            /* Catch Exceptions */
        } catch (OceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }

        /* Create the InfoSet */
        theInfoSet = new SecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Security(final SecurityList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new SecurityInfoSet(this, pList.getActInfoTypes(), pList.getSecurityInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_SECTYPE.equals(pField)) {
            return true;
        }
        if (FIELD_SYMBOL.equals(pField)) {
            return true;
        }
        if (FIELD_CURRENCY.equals(pField)) {
            return true;
        }
        if (FIELD_PARENT.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                              ? theInfoSet
                              : JDataFieldValue.SKIP;
        }

        /* Handle infoSet fields */
        AccountInfoClass myClass = SecurityInfoSet.getClassForField(pField);
        if ((theInfoSet != null) && (myClass != null)) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public SecurityInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Notes.
     * @return the notes
     */
    public char[] getNotes() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.NOTES, char[].class)
                          : null;
    }

    @Override
    public Payee getParent() {
        return getParent(getValueSet());
    }

    /**
     * Obtain ParentId.
     * @return the parentId
     */
    public Integer getParentId() {
        Payee myParent = getParent();
        return (myParent == null)
                                  ? null
                                  : myParent.getId();
    }

    /**
     * Obtain ParentName.
     * @return the parentName
     */
    public String getParentName() {
        Payee myParent = getParent();
        return (myParent == null)
                                  ? null
                                  : myParent.getName();
    }

    /**
     * Obtain Security Type.
     * @return the type
     */
    public SecurityType getSecurityType() {
        return getSecurityType(getValueSet());
    }

    /**
     * Obtain SecurityTypeId.
     * @return the securityTypeId
     */
    public Integer getSecurityTypeId() {
        SecurityType myType = getSecurityType();
        return (myType == null)
                                ? null
                                : myType.getId();
    }

    /**
     * Obtain SecurityTypeName.
     * @return the securityTypeName
     */
    public String getSecurityTypeName() {
        SecurityType myType = getSecurityType();
        return (myType == null)
                                ? null
                                : myType.getName();
    }

    /**
     * Obtain SecurityTypeClass.
     * @return the securityTypeClass
     */
    public SecurityTypeClass getSecurityTypeClass() {
        SecurityType myType = getSecurityType();
        return (myType == null)
                                ? null
                                : myType.getSecurityClass();
    }

    /**
     * Obtain Symbol.
     * @return the symbol
     */
    public String getSymbol() {
        return getSymbol(getValueSet());
    }

    /**
     * Obtain Encrypted symbol.
     * @return the bytes
     */
    public byte[] getSymbolBytes() {
        return getSymbolBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Symbol Field.
     * @return the Field
     */
    private EncryptedString getSymbolField() {
        return getSymbolField(getValueSet());
    }

    @Override
    public AssetCurrency getAssetCurrency() {
        return getAssetCurrency(getValueSet());
    }

    /**
     * Obtain Parent.
     * @param pValueSet the valueSet
     * @return the Parent
     */
    public static Payee getParent(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, Payee.class);
    }

    /**
     * Obtain SecurityType.
     * @param pValueSet the valueSet
     * @return the SecurityType
     */
    public static SecurityType getSecurityType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SECTYPE, SecurityType.class);
    }

    /**
     * Obtain Symbol.
     * @param pValueSet the valueSet
     * @return the symbol
     */
    public static String getSymbol(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_SYMBOL, String.class);
    }

    /**
     * Obtain Encrypted symbol.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getSymbolBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_SYMBOL);
    }

    /**
     * Obtain Encrypted symbol field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedString getSymbolField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SYMBOL, EncryptedString.class);
    }

    /**
     * Obtain SecurityCurrency.
     * @param pValueSet the valueSet
     * @return the SecurityCurrency
     */
    public static AssetCurrency getAssetCurrency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CURRENCY, AssetCurrency.class);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    private void setValueParent(final Payee pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent id.
     * @param pValue the value
     */
    private void setValueParent(final Integer pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent name.
     * @param pValue the value
     */
    private void setValueParent(final String pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set security type value.
     * @param pValue the value
     */
    private void setValueType(final SecurityType pValue) {
        getValueSet().setValue(FIELD_SECTYPE, pValue);
    }

    /**
     * Set security type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_SECTYPE, pValue);
    }

    /**
     * Set security type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValueSet().setValue(FIELD_SECTYPE, pValue);
    }

    /**
     * Set symbol value.
     * @param pValue the value
     * @throws OceanusException on error
     */
    private void setValueSymbol(final String pValue) throws OceanusException {
        setEncryptedValue(FIELD_SYMBOL, pValue);
    }

    /**
     * Set symbol value.
     * @param pBytes the value
     * @throws OceanusException on error
     */
    private void setValueSymbol(final byte[] pBytes) throws OceanusException {
        setEncryptedValue(FIELD_SYMBOL, pBytes, String.class);
    }

    /**
     * Set symbol value.
     * @param pValue the value
     */
    private void setValueSymbol(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_SYMBOL, pValue);
    }

    /**
     * Set security currency value.
     * @param pValue the value
     */
    private void setValueCurrency(final AssetCurrency pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set security currency id.
     * @param pValue the value
     */
    private void setValueCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set security currency name.
     * @param pValue the value
     */
    private void setValueCurrency(final String pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    @Override
    public Security getBase() {
        return (Security) super.getBase();
    }

    @Override
    public SecurityList getList() {
        return (SecurityList) super.getList();
    }

    @Override
    public DataState getState() {
        /* Pop history for self */
        DataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == DataState.CLEAN) && useInfoSet) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public EditState getEditState() {
        /* Pop history for self */
        EditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if ((myState == EditState.CLEAN) && useInfoSet) {
            /* Get state for infoSet */
            myState = theInfoSet.getEditState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public boolean hasHistory() {
        /* Check for history for self */
        boolean hasHistory = super.hasHistory();

        /* If we should use the InfoSet */
        if (!hasHistory && useInfoSet) {
            /* Check history for infoSet */
            hasHistory = theInfoSet.hasHistory();
        }

        /* Return details */
        return hasHistory;
    }

    @Override
    public void pushHistory() {
        /* Push history for self */
        super.pushHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Push history for infoSet */
            theInfoSet.pushHistory();
        }
    }

    @Override
    public void popHistory() {
        /* Pop history for self */
        super.popHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Pop history for infoSet */
            theInfoSet.popHistory();
        }
    }

    @Override
    public boolean checkForHistory() {
        /* Check for history for self */
        boolean bChanges = super.checkForHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Check for history for infoSet */
            bChanges |= theInfoSet.checkForHistory();
        }

        /* return result */
        return bChanges;
    }

    @Override
    public Difference fieldChanged(final JDataField pField) {
        /* Handle InfoSet fields */
        AccountInfoClass myClass = SecurityInfoSet.getClassForField(pField);
        if (myClass != null) {
            return useInfoSet
                              ? theInfoSet.fieldChanged(myClass)
                              : Difference.IDENTICAL;
        }

        /* Check super fields */
        return super.fieldChanged(pField);
    }

    @Override
    public void setDeleted(final boolean bDeleted) {
        /* Pass call to infoSet if required */
        if (useInfoSet) {
            theInfoSet.setDeleted(bDeleted);
        }

        /* Pass call onwards */
        super.setDeleted(bDeleted);
    }

    /**
     * Is this security the required class.
     * @param pClass the required security class.
     * @return true/false
     */
    public boolean isSecurityClass(final SecurityTypeClass pClass) {
        /* Check for match */
        return getSecurityTypeClass() == pClass;
    }

    @Override
    public boolean isShares() {
        return isSecurityClass(SecurityTypeClass.SHARES);
    }

    @Override
    public boolean isCapital() {
        switch (getSecurityTypeClass()) {
            case UNITTRUST:
            case LIFEBOND:
            case SHARES:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void deRegister() {
        SecurityHoldingMap myMap = getDataSet().getSecurityHoldingsMap();
        myMap.deRegister(this);
    }

    /**
     * Set defaults.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void setDefaults(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Set values */
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setSecurityType(getDefaultSecurityType());
        setAssetCurrency(getDataSet().getDefaultCurrency());
        setSymbol(getName());
        setClosed(Boolean.FALSE);
        autoCorrect(pUpdateSet);
    }

    /**
     * autoCorrect values after change.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void autoCorrect(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Access category class and parent */
        SecurityTypeClass myClass = getSecurityTypeClass();
        Payee myParent = getParent();

        /* Ensure that we have a valid parent */
        if ((myParent == null)
            || myParent.getPayeeTypeClass().canParentSecurity(myClass)) {
            setParent(getDefaultParent(pUpdateSet));
        }
    }

    /**
     * Obtain security type for new security account.
     * @return the security type
     */
    public SecurityType getDefaultSecurityType() {
        /* loop through the security types */
        SecurityTypeList myTypes = getDataSet().getSecurityTypes();
        Iterator<SecurityType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            SecurityType myType = myIterator.next();

            /* Ignore deleted types */
            if (!myType.isDeleted()) {
                return myType;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default parent for new security.
     * @param pUpdateSet the update set
     * @return the default parent
     */
    private Payee getDefaultParent(final UpdateSet<MoneyWiseDataType> pUpdateSet) {
        /* Access details */
        PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        SecurityTypeClass myClass = getSecurityTypeClass();

        /* loop through the payees */
        Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            Payee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || myPayee.isClosed()) {
                continue;
            }

            /* If the payee can parent */
            if (myPayee.getPayeeTypeClass().canParentSecurity(myClass)) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }

    @Override
    public int compareTo(final TransactionAsset pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare types of asset */
        int iDiff = super.compareTo(pThat);
        if ((iDiff == 0)
            && (pThat instanceof Security)) {
            /* Check the security type */
            Security myThat = (Security) pThat;
            iDiff = Difference.compareObject(getSecurityType(), myThat.getSecurityType());
            if (iDiff == 0) {
                /* Check the underlying base */
                iDiff = super.compareAsset(myThat);
            }
        }

        /* Return the result */
        return iDiff;
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_SECTYPE, myData.getSecurityTypes());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(FIELD_PARENT, myData.getPayees());
    }

    @Override
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Resolve parent within list */
        PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        resolveDataLink(FIELD_PARENT, myPayees);
    }

    /**
     * Set a new security type.
     * @param pType the new type
     */
    public void setSecurityType(final SecurityType pType) {
        setValueType(pType);
    }

    /**
     * Set a new symbol.
     * @param pSymbol the symbol
     * @throws OceanusException on error
     */
    public void setSymbol(final String pSymbol) throws OceanusException {
        setValueSymbol(pSymbol);
    }

    /**
     * Set a new security currency.
     * @param pCurrency the new currency
     */
    public void setAssetCurrency(final AssetCurrency pCurrency) {
        setValueCurrency(pCurrency);
    }

    /**
     * Set a new parent.
     * @param pParent the parent
     * @throws OceanusException on error
     */
    public void setParent(final Payee pParent) throws OceanusException {
        setValueParent(pParent);
    }

    /**
     * Set a new Notes.
     * @param pNotes the new notes
     * @throws OceanusException on error
     */
    public void setNotes(final char[] pNotes) throws OceanusException {
        setInfoSetValue(AccountInfoClass.NOTES, pNotes);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws OceanusException on error
     */
    private void setInfoSetValue(final AccountInfoClass pInfoClass,
                                 final Object pValue) throws OceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JMoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    @Override
    public TransactionCategory getDetailedCategory(final TransactionCategory pCategory) {
        /* Switch on category type */
        switch (pCategory.getCategoryTypeClass()) {
            case DIVIDEND:
                TransactionCategoryList myCategories = getDataSet().getTransCategories();
                return myCategories.getSingularClass(isSecurityClass(SecurityTypeClass.UNITTRUST)
                                                                                                  ? TransactionCategoryClass.UNITTRUSTDIVIDEND
                                                                                                  : TransactionCategoryClass.SHAREDIVIDEND);
            default:
                return pCategory;
        }
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the security type, currency and parent */
        getSecurityType().touchItem(this);
        getAssetCurrency().touchItem(this);
        getParent().touchItem(this);

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    @Override
    public void touchOnUpdate() {
        /* Reset touches from update set */
        clearTouches(MoneyWiseDataType.SECURITYPRICE);
        clearTouches(MoneyWiseDataType.STOCKOPTION);

        /* Touch parent */
        getParent().touchItem(this);
    }

    @Override
    public void validate() {
        Payee myParent = getParent();
        SecurityType mySecType = getSecurityType();
        AssetCurrency myCurrency = getAssetCurrency();
        String mySymbol = getSymbol();

        /* Validate base components */
        super.validate();

        /* SecurityType must be non-null */
        if (mySecType == null) {
            addError(ERROR_MISSING, FIELD_SECTYPE);
        } else {
            /* SecurityType must be enabled */
            if (!mySecType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_SECTYPE);
            }
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            addError(ERROR_MISSING, FIELD_CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_CURRENCY);
        }

        /* Parent must be non-null */
        if (myParent == null) {
            addError(ERROR_MISSING, FIELD_PARENT);
        } else {
            /* Access the classes */
            SecurityTypeClass myClass = mySecType.getSecurityClass();
            PayeeTypeClass myParClass = myParent.getPayeeTypeClass();

            /* Parent must be suitable */
            if (!myParClass.canParentSecurity(myClass)) {
                addError(ERROR_BADPARENT, FIELD_PARENT);
            }

            /* If we are open then parent must be open */
            if (!isClosed() && myParent.isClosed()) {
                addError(ERROR_PARCLOSED, FIELD_CLOSED);
            }
        }

        /* Symbol must be non-null */
        if (mySymbol == null) {
            addError(ERROR_MISSING, FIELD_SYMBOL);

            /* Check symbol validity */
        } else {
            /* Check length of symbol */
            if (mySymbol.length() > SYMBOLLEN) {
                addError(ERROR_LENGTH, FIELD_SYMBOL);
            }

            /* Check symbol count */
            if (!getList().validSymbolCount(mySymbol)) {
                addError(ERROR_DUPLICATE, FIELD_SYMBOL);
            }
        }

        /* If we have an infoSet */
        if (theInfoSet != null) {
            /* Validate the InfoSet */
            theInfoSet.validate();
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    @Override
    protected void validateName(final String pName) {
        /* Perform basic checks */
        super.validateName(pName);

        /* Check that the name is not a reserved name */
        if (pName.equals(SecurityHolding.SECURITYHOLDING_NEW)
            || pName.equals(Portfolio.NAME_CASHACCOUNT)) {
            addError(ERROR_RESERVED, FIELD_NAME);
        }

        /* Check that the name does not contain invalid characters */
        if (pName.contains(SecurityHolding.SECURITYHOLDING_SEP)) {
            addError(ERROR_INVALIDCHAR, FIELD_NAME);
        }
    }

    /**
     * Update base security from an edited security.
     * @param pSecurity the edited security
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pSecurity) {
        /* Can only update from a security */
        if (!(pSecurity instanceof Security)) {
            return false;
        }
        Security mySecurity = (Security) pSecurity;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(mySecurity);

        /* Update the category type if required */
        if (!Difference.isEqual(getSecurityType(), mySecurity.getSecurityType())) {
            setValueType(mySecurity.getSecurityType());
        }

        /* Update the parent if required */
        if (!Difference.isEqual(getParent(), mySecurity.getParent())) {
            setValueParent(mySecurity.getParent());
        }

        /* Update the symbol if required */
        if (!Difference.isEqual(getSymbol(), mySecurity.getSymbol())) {
            setValueSymbol(mySecurity.getSymbolField());
        }

        /* Update the security currency if required */
        if (!Difference.isEqual(getAssetCurrency(), mySecurity.getAssetCurrency())) {
            setValueCurrency(mySecurity.getAssetCurrency());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        SecurityList myList = getList();
        SecurityDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Security List class.
     */
    public static class SecurityList
            extends AssetBaseList<Security> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        /**
         * The SecurityInfo List.
         */
        private SecurityInfoList theInfoList = null;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList = null;

        /**
         * Construct an empty CORE Security list.
         * @param pData the DataSet for the list
         */
        public SecurityList(final MoneyWiseData pData) {
            super(pData, Security.class, MoneyWiseDataType.SECURITY);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected SecurityList(final SecurityList pSource) {
            super(pSource);
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
            return Security.FIELD_DEFS;
        }

        @Override
        protected SecurityDataMap getDataMap() {
            return (SecurityDataMap) super.getDataMap();
        }

        /**
         * Obtain the securityInfoList.
         * @return the security info list
         */
        public SecurityInfoList getSecurityInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getSecurityInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the accountInfoTypeList.
         * @return the account info type list
         */
        public AccountInfoTypeList getActInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getActInfoTypes();
            }
            return theInfoTypeList;
        }

        @Override
        protected SecurityList getEmptyList(final ListStyle pStyle) {
            SecurityList myList = new SecurityList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public SecurityList deriveEditList(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
            /* Build an empty List */
            SecurityList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            SecurityInfoList mySecInfo = getSecurityInfo();
            myList.theInfoList = mySecInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the securities */
            Iterator<Security> myIterator = iterator();
            while (myIterator.hasNext()) {
                Security myCurr = myIterator.next();

                /* Ignore deleted securities */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked security and add it to the list */
                Security mySecurity = new Security(myList, myCurr);
                mySecurity.resolveUpdateSetLinks(pUpdateSet);
                myList.append(mySecurity);

                /* Adjust the map */
                mySecurity.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public Security findItemByName(final String pName) {
            /* look up the name in the map */
            return getDataMap().findItemByName(pName);
        }

        @Override
        protected boolean checkAvailableName(final String pName) {
            /* check availability */
            return findItemByName(pName) == null;
        }

        @Override
        protected boolean validNameCount(final String pName) {
            /* check availability in map */
            return getDataMap().validNameCount(pName);
        }

        /**
         * Find the item that uses the symbol.
         * @param pSymbol the symbol to lookup
         * @return the item (or null)
         */
        public Security findItemBySymbol(final String pSymbol) {
            /* look up the symbol in the map */
            return getDataMap().findItemBySymbol(pSymbol);
        }

        /**
         * check that symbol is unique.
         * @param pSymbol the symbol
         * @return true/false
         */
        protected boolean validSymbolCount(final String pSymbol) {
            /* check availability in map */
            return getDataMap().validSymbolCount(pSymbol);
        }

        /**
         * Add a new item to the core list.
         * @param pSecurity item
         * @return the newly added item
         */
        @Override
        public Security addCopyItem(final DataItem<?> pSecurity) {
            /* Can only clone an Security */
            if (!(pSecurity instanceof Security)) {
                throw new UnsupportedOperationException();
            }

            Security mySecurity = new Security(this, (Security) pSecurity);
            add(mySecurity);
            return mySecurity;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public Security addNewItem() {
            Security mySecurity = new Security(this);
            add(mySecurity);
            return mySecurity;
        }

        @Override
        public Security addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the security */
            Security mySecurity = new Security(this, pValues);

            /* Check that this SecurityId has not been previously added */
            if (!isIdUnique(mySecurity.getId())) {
                mySecurity.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(mySecurity, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(mySecurity);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    DataValues<MoneyWiseDataType> myValues = myItem.getValues(mySecurity);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return mySecurity;
        }

        @Override
        protected SecurityDataMap allocateDataMap() {
            return new SecurityDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class SecurityDataMap
            extends DataInstanceMap<Security, MoneyWiseDataType, String> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(PrometheusDataResource.DATAMAP_NAME.getValue(), DataInstanceMap.FIELD_DEFS);

        /**
         * SymbolMap Field Id.
         */
        private static final JDataField FIELD_SYMMAP = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.SECURITY_SYMBOLMAP.getValue());

        /**
         * SymbolCountMap Field Id.
         */
        private static final JDataField FIELD_SYMCOUNT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.SECURITY_SYMBOLCOUNTMAP.getValue());

        /**
         * Map of symbol counts.
         */
        private final Map<String, Integer> theSymbolCountMap;

        /**
         * Map of symbols.
         */
        private final Map<String, Security> theSymbolMap;

        /**
         * Constructor.
         */
        public SecurityDataMap() {
            /* Create the maps */
            theSymbolCountMap = new HashMap<String, Integer>();
            theSymbolMap = new HashMap<String, Security>();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_SYMMAP.equals(pField)) {
                return theSymbolMap;
            }
            if (FIELD_SYMCOUNT.equals(pField)) {
                return theSymbolCountMap;
            }

            /* Unknown */
            return super.getFieldValue(pField);
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public void resetMap() {
            super.resetMap();
            theSymbolCountMap.clear();
            theSymbolMap.clear();
        }

        @Override
        public void adjustForItem(final Security pItem) {
            /* Adjust symbol count */
            String mySymbol = pItem.getSymbol();
            Integer myCount = theSymbolCountMap.get(mySymbol);
            if (myCount == null) {
                theSymbolCountMap.put(mySymbol, ONE);
            } else {
                theSymbolCountMap.put(mySymbol, myCount + 1);
            }

            /* Adjust symbol map */
            theSymbolMap.put(mySymbol, pItem);

            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
        }

        /**
         * find item by symbol.
         * @param pSymbol the symbol to look up
         * @return the matching item
         */
        public Security findItemBySymbol(final String pSymbol) {
            return theSymbolMap.get(pSymbol);
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public Security findItemByName(final String pName) {
            return findItemByKey(pName);
        }

        /**
         * Check validity of symbol count.
         * @param pSymbol the symbol to look up
         * @return true/false
         */
        public boolean validSymbolCount(final String pSymbol) {
            Integer myResult = theSymbolCountMap.get(pSymbol);
            return ONE.equals(myResult);
        }

        /**
         * Check validity of name.
         * @param pName the name to look up
         * @return true/false
         */
        public boolean validNameCount(final String pName) {
            return validKeyCount(pName);
        }

        /**
         * Check availability of name.
         * @param pName the key to look up
         * @return true/false
         */
        public boolean availableName(final String pName) {
            return availableKey(pName);
        }
    }
}
