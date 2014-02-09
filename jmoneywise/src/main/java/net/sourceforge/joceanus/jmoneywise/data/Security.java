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
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Security class.
 */
public class Security
                     extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<Security> {
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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Security.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataName"));

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDesc"));

    /**
     * SecurityType Field Id.
     */
    public static final JDataField FIELD_SECTYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.SECURITYTYPE.getItemName());

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataParent"));

    /**
     * Symbol Field Id.
     */
    public static final JDataField FIELD_SYMBOL = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataSymbol"));

    /**
     * Currency Field Id.
     */
    public static final JDataField FIELD_CURRENCY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CURRENCY.getItemName());

    /**
     * isClosed Field Id.
     */
    public static final JDataField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataClosed"));

    /**
     * Parent Not Market Error Text.
     */
    private static final String ERROR_PARMARKET = NLS_BUNDLE.getString("ErrorParentMarket");

    /**
     * Parent Invalid Error Text.
     */
    private static final String ERROR_PARBAD = NLS_BUNDLE.getString("ErrorBadParent");

    /**
     * Parent Closed Error Text.
     */
    private static final String ERROR_PARCLOSED = NLS_BUNDLE.getString("ErrorParentClosed");

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

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
     * Obtain Parent.
     * @return the parent
     */
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
     * @return the category
     */
    public AccountCurrency getSecurityCurrency() {
        return getSecurityCurrency(getValueSet());
    }

    /**
     * Obtain SecurityCurrencyId.
     * @return the secCurrencyId
     */
    public Integer getSecurityCurrencyId() {
        AccountCurrency myCurrency = getSecurityCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getId();
    }

    /**
     * Obtain SecurityCurrencyName.
     * @return the actCurrencyName
     */
    public String getSecurityCurrencyName() {
        AccountCurrency myCurrency = getSecurityCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getName();
    }

    /**
     * Is the security closed?
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
     * Is the security closed?
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
    public Security getBase() {
        return (Security) super.getBase();
    }

    @Override
    public SecurityList getList() {
        return (SecurityList) super.getList();
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

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pSecurity The Security to copy
     */
    protected Security(final SecurityList pList,
                       final Security pSecurity) {
        /* Set standard values */
        super(pList, pSecurity);
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param pId the id
     * @param pName the Name of the security
     * @param pDesc the description of the security
     * @param pSecType the Security type
     * @param pParent the Parent
     * @param pSymbol the security symbol
     * @param pClosed is the security closed?
     * @param pCurrency the Security currency
     * @throws JOceanusException on error
     */
    protected Security(final SecurityList pList,
                       final Integer pId,
                       final String pName,
                       final String pDesc,
                       final String pSecType,
                       final String pParent,
                       final String pSymbol,
                       final String pCurrency,
                       final Boolean pClosed) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the links */
            setValueType(pSecType);
            setValueParent(pParent);

            /* Store the currency */
            setValueCurrency(pCurrency);

            /* Record the string values */
            setValueName(pName);
            setValueDesc(pDesc);
            setValueSymbol(pSymbol);

            /* Store closed flag */
            setValueClosed(pClosed);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
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
            /* Store the Name */
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

            /* Store the SecurityType */
            myValue = pValues.getValue(FIELD_SECTYPE);
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
            }

            /* Store the closed flag */
            myValue = pValues.getValue(FIELD_CLOSED);
            if (myValue instanceof Boolean) {
                setValueClosed((Boolean) myValue);
            } else if (myValue instanceof String) {
                JDataFormatter myFormatter = getDataSet().getDataFormatter();
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
    public Security(final SecurityList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final Security pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
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

        /* Check the names */
        iDiff = Difference.compareObject(getName(), pThat.getName());
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
        MoneyWiseData myData = getDataSet();
        ValueSet myValues = getValueSet();
        resolveDataLink(FIELD_SECTYPE, myData.getSecurityTypes());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(FIELD_PARENT, myData.getPayees());

        /* Adjust Closed */
        Object myClosed = myValues.getValue(FIELD_CLOSED);
        if (myClosed == null) {
            setValueClosed(Boolean.FALSE);
        }
    }

    /**
     * Set a new security name.
     * @param pName the new name
     * @throws JOceanusException on error
     */
    public void setSecurityName(final String pName) throws JOceanusException {
        setValueName(pName);
    }

    /**
     * Set a new security type.
     * @param pType the new type
     */
    public void setSecurityType(final SecurityType pType) {
        setValueType(pType);
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
     * Set a new closed indication.
     * @param isClosed the new closed indication
     */
    public void setClosed(final Boolean isClosed) {
        setValueClosed(isClosed);
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
        SecurityList myList = getList();
        Payee myParent = getParent();
        SecurityType mySecType = getSecurityType();
        AccountCurrency myCurrency = getSecurityCurrency();
        String myName = getName();
        String myDesc = getDesc();
        String mySymbol = getSymbol();

        /* Name must be non-null */
        if (myName == null) {
            addError(ERROR_MISSING, FIELD_NAME);

            /* Check that the name is valid */
        } else {
            /* The name must not be too long */
            if (myName.length() > NAMELEN) {
                addError(ERROR_LENGTH, FIELD_NAME);
            }

            /* The name must be unique */
            if (myList.countInstances(myName) > 1) {
                addError(ERROR_DUPLICATE, FIELD_NAME);
            }
        }

        /* Check description length */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError(ERROR_LENGTH, FIELD_DESC);
        }

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

            /* If the account needs a market parent */
            if ((myClass.needsMarketParent()) && (myParClass != PayeeTypeClass.MARKET)) {
                addError(ERROR_PARMARKET, FIELD_PARENT);

                /* else check that any parent is owner */
            } else if (!myParClass.canParentAccount()) {
                addError(ERROR_PARBAD, FIELD_PARENT);
            }
        }

        /* Name must be non-null */
        if (mySymbol == null) {
            addError(ERROR_MISSING, FIELD_SYMBOL);

            /* Check that the symbol is valid */
        } else if (mySymbol.length() > SYMBOLLEN) {
            addError(ERROR_LENGTH, FIELD_SYMBOL);
        }

        /* If we are open then parent must be open */
        if (!isClosed() && myParent.isClosed()) {
            addError(ERROR_PARCLOSED, FIELD_CLOSED);
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

        /* Update the Name if required */
        if (!Difference.isEqual(getName(), mySecurity.getName())) {
            setValueName(mySecurity.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), mySecurity.getDesc())) {
            setValueDesc(mySecurity.getDescField());
        }

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

        /* Update the closed status if required */
        if (!Difference.isEqual(isClosed(), mySecurity.isClosed())) {
            setValueClosed(mySecurity.isClosed());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Security List class.
     */
    public static class SecurityList
                                    extends EncryptedList<Security, MoneyWiseDataType> {
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
            return Security.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Security list.
         * @param pData the DataSet for the list
         */
        public SecurityList(final MoneyWiseData pData) {
            super(Security.class, pData, MoneyWiseDataType.SECURITY, ListStyle.CORE);
        }

        @Override
        protected SecurityList getEmptyList(final ListStyle pStyle) {
            SecurityList myList = new SecurityList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public SecurityList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (SecurityList) super.cloneList(pDataSet);
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

            /* Loop through the securities */
            Iterator<Security> myIterator = iterator();
            while (myIterator.hasNext()) {
                Security myCurr = myIterator.next();

                /* Ignore deleted events */
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
                return null;
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

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The # of instances of the name
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<Security> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Security myCurr = myIterator.next();
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
        public Security findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<Security> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Security myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Allow a security to be added.
         * @param pId the id
         * @param pName the name
         * @param pDesc the description
         * @param pSecType the security type
         * @param pParent the parent
         * @param pSymbol the security symbol
         * @param pCurrency the security currency
         * @param pClosed is the security closed?
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final String pName,
                                final String pDesc,
                                final String pSecType,
                                final String pParent,
                                final String pSymbol,
                                final String pCurrency,
                                final Boolean pClosed) throws JOceanusException {
            /* Create the security */
            Security mySecurity = new Security(this, pId, pName, pDesc, pSecType, pParent, pSymbol, pCurrency, pClosed);

            /* Check that this SecurityId has not been previously added */
            if (!isIdUnique(pId)) {
                mySecurity.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(mySecurity, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(mySecurity);
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

            /* Return it */
            return mySecurity;
        }
    }
}
