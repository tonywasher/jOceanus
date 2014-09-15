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

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedString;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * initialPrice Field Id.
     */
    private static final JDataField FIELD_INITPRC = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.SECURITY_INITIALPRICE.getValue());

    /**
     * SecurityInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseDataResource.SECURITY_NEWACCOUNT.getValue();

    /**
     * Initial Price.
     */
    private SecurityPrice theInitPrice;

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
        if (FIELD_INITPRC.equals(pField)) {
            return (theInitPrice != null)
                                         ? theInitPrice
                                         : JDataFieldValue.SKIP;
        }
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
     * Obtain Initial Price.
     * @return the price
     */
    public SecurityPrice getInitialPrice() {
        return theInitPrice;
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

    /**
     * Obtain Security Currency.
     * @return the currency
     */
    public AccountCurrency getSecurityCurrency() {
        return getSecurityCurrency(getValueSet());
    }

    /**
     * Obtain SecurityCurrencyId.
     * @return the currencyId
     */
    public Integer getSecurityCurrencyId() {
        AccountCurrency myCurrency = getSecurityCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getId();
    }

    /**
     * Obtain SecurityCurrencyName.
     * @return the currencyName
     */
    public String getSecurityCurrencyName() {
        AccountCurrency myCurrency = getSecurityCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getName();
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
    public static AccountCurrency getSecurityCurrency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CURRENCY, AccountCurrency.class);
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
     * @throws JOceanusException on error
     */
    private void setValueSymbol(final String pValue) throws JOceanusException {
        setEncryptedValue(FIELD_SYMBOL, pValue);
    }

    /**
     * Set symbol value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueSymbol(final byte[] pBytes) throws JOceanusException {
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
    private void setValueCurrency(final AccountCurrency pValue) {
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
    public void clearActive() {
        /* Reset flags */
        theInitPrice = null;

        /* Pass call onwards */
        super.clearActive();
    }

    @Override
    public void touchItem(final DataItem<MoneyWiseDataType> pSource) {
        /* Check for initial price */
        if ((pSource instanceof SecurityPrice) && (theInitPrice == null)) {
            theInitPrice = (SecurityPrice) pSource;
        }

        /* Pass call onwards */
        super.touchItem(pSource);
    }

    @Override
    public DataState getState() {
        /* Pop history for self */
        DataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == DataState.CLEAN) && (useInfoSet)) {
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
        if ((myState == EditState.CLEAN) && (useInfoSet)) {
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
        if ((!hasHistory) && (useInfoSet)) {
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
            return (useInfoSet)
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
    public boolean canDividend() {
        switch (getSecurityTypeClass()) {
            case SHARES:
            case UNITTRUST:
                return true;
            default:
                return false;
        }
    }

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
     * @throws JOceanusException on error
     */
    private Security(final SecurityList pList,
                     final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
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
            } else if (myValue instanceof AccountCurrency) {
                setValueCurrency((AccountCurrency) myValue);
            }

            /* Catch Exceptions */
        } catch (JOceanusException e) {
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

    /**
     * Set defaults.
     * @param pUpdateSet the update set
     * @throws JOceanusException on error
     */
    public void setDefaults(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Set values */
        SecurityTypeList myTypes = getDataSet().getSecurityTypes();
        PayeeList myPayees = pUpdateSet.findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        setSecurityType(myTypes.getDefaultSecurityType());
        setSecurityCurrency(getDataSet().getDefaultCurrency());
        setParent(myPayees.getDefaultSecurityParent(getSecurityTypeClass()));
        setName(getList().getUniqueName(NAME_NEWACCOUNT));
        setSymbol(getName());
        setClosed(Boolean.FALSE);
    }

    /**
     * adjust values after category change.
     * @param pUpdateSet the update set
     * @throws JOceanusException on error
     */
    public void adjustForCategory(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Access category class and parent */
        SecurityTypeClass myClass = getSecurityTypeClass();
        Payee myParent = getParent();

        /* Check that parent is valid for category */
        if (!myParent.getPayeeTypeClass().canParentSecurity(myClass)) {
            PayeeList myPayees = pUpdateSet.findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
            setParent(myPayees.getDefaultSecurityParent(myClass));
        }
    }

    @Override
    public int compareTo(final Security pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the security type */
        int iDiff = Difference.compareObject(getSecurityType(), pThat.getSecurityType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying base */
        return super.compareTo(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_SECTYPE, myData.getSecurityTypes());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(FIELD_PARENT, myData.getPayees());
    }

    @Override
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Resolve parent within list */
        PayeeList myPayees = pUpdateSet.findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
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
     * @throws JOceanusException on error
     */
    public void setSymbol(final String pSymbol) throws JOceanusException {
        setValueSymbol(pSymbol);
    }

    /**
     * Set a new security currency.
     * @param pCurrency the new currency
     */
    public void setSecurityCurrency(final AccountCurrency pCurrency) {
        setValueCurrency(pCurrency);
    }

    /**
     * Set a new parent.
     * @param pParent the parent
     * @throws JOceanusException on error
     */
    public void setParent(final Payee pParent) throws JOceanusException {
        setValueParent(pParent);
    }

    /**
     * Set a new Notes.
     * @param pNotes the new notes
     * @throws JOceanusException on error
     */
    public void setNotes(final char[] pNotes) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.NOTES, pNotes);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws JOceanusException on error
     */
    private void setInfoSetValue(final AccountInfoClass pInfoClass,
                                 final Object pValue) throws JOceanusException {
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
        getSecurityCurrency().touchItem(this);
        getParent().touchItem(this);
    }

    @Override
    public void validate() {
        Payee myParent = getParent();
        SecurityType mySecType = getSecurityType();
        AccountCurrency myCurrency = getSecurityCurrency();
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

            /* Check that the symbol is valid */
        } else if (mySymbol.length() > SYMBOLLEN) {
            addError(ERROR_LENGTH, FIELD_SYMBOL);
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
        if (!Difference.isEqual(getSecurityCurrency(), mySecurity.getSecurityCurrency())) {
            setValueCurrency(mySecurity.getSecurityCurrency());
        }

        /* Check for changes */
        return checkForHistory();
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

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The SecurityInfo List.
         */
        private SecurityInfoList theInfoList = null;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return Security.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
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

        /**
         * Construct an empty CORE Security list.
         * @param pData the DataSet for the list
         */
        public SecurityList(final MoneyWiseData pData) {
            super(pData, Security.class, MoneyWiseDataType.SECURITY);
        }

        @Override
        protected SecurityList getEmptyList(final ListStyle pStyle) {
            SecurityList myList = new SecurityList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected SecurityList(final SecurityList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public SecurityList deriveEditList() {
            /* Build an empty List */
            SecurityList myList = getEmptyList(ListStyle.EDIT);

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
                myList.append(mySecurity);
            }

            /* Return the list */
            return myList;
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
        public Security addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
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

        /**
         * Obtain default security for stockOption.
         * @return the default security
         */
        public Security getDefaultStockOption() {
            /* loop through the securities */
            Iterator<Security> myIterator = iterator();
            while (myIterator.hasNext()) {
                Security mySecurity = myIterator.next();

                /* Ignore deleted and closed securities */
                if (mySecurity.isDeleted() || mySecurity.isClosed()) {
                    continue;
                }

                /* Only allow shares */
                if (mySecurity.isSecurityClass(SecurityTypeClass.SHARES)) {
                    return mySecurity;
                }
            }

            /* Return no payee */
            return null;
        }
    }
}
