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
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Deposit class.
 */
public class Deposit
        extends AssetBase<Deposit> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.DEPOSIT.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.DEPOSIT.getListName();

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Deposit.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * AccountCategory Field Id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.ACCOUNTCATEGORY.getItemName());

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataParent"));

    /**
     * Currency Field Id.
     */
    public static final JDataField FIELD_CURRENCY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CURRENCY.getItemName());

    /**
     * isGross Field Id.
     */
    public static final JDataField FIELD_GROSS = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataGross"));

    /**
     * isTaxFree Field Id.
     */
    public static final JDataField FIELD_TAXFREE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataTaxFree"));

    /**
     * TaxFree Error Text.
     */
    private static final String ERROR_TAXFREE = NLS_BUNDLE.getString("ErrorTaxFree");

    /**
     * GrossInterest Error Text.
     */
    private static final String ERROR_GROSS = NLS_BUNDLE.getString("ErrorGross");

    /**
     * taxFree And GrossInterest Error Text.
     */
    private static final String ERROR_TAXFREEGROSS = NLS_BUNDLE.getString("ErrorTaxFreeGross");

    /**
     * Parent Closed Error Text.
     */
    private static final String ERROR_PARCLOSED = NLS_BUNDLE.getString("ErrorParentClosed");

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_CATEGORY.equals(pField)) {
            return true;
        }
        if (FIELD_CURRENCY.equals(pField)) {
            return true;
        }
        if (FIELD_PARENT.equals(pField)) {
            return getParent() != null;
        }
        if (FIELD_GROSS.equals(pField)) {
            return isGross();
        }
        if (FIELD_TAXFREE.equals(pField)) {
            return isTaxFree();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
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
     * Obtain AccountCategory.
     * @return the category
     */
    public AccountCategory getCategory() {
        return getCategory(getValueSet());
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        AccountCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getId();
    }

    /**
     * Obtain CategoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        AccountCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getName();
    }

    /**
     * Obtain AccountCategoryClass.
     * @return the actCategoryClass
     */
    public AccountCategoryClass getCategoryClass() {
        AccountCategory myCategory = getCategory();
        return (myCategory == null)
                                   ? null
                                   : myCategory.getCategoryTypeClass();
    }

    /**
     * Obtain Deposit Currency.
     * @return the currency
     */
    public AccountCurrency getDepositCurrency() {
        return getDepositCurrency(getValueSet());
    }

    /**
     * Obtain DepositCurrencyId.
     * @return the currencyId
     */
    public Integer getDepositCurrencyId() {
        AccountCurrency myCurrency = getDepositCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getId();
    }

    /**
     * Obtain DepositCurrencyName.
     * @return the currencyName
     */
    public String getDepositCurrencyName() {
        AccountCurrency myCurrency = getDepositCurrency();
        return (myCurrency == null)
                                   ? null
                                   : myCurrency.getName();
    }

    /**
     * Is the deposit gross.
     * @return true/false
     */
    public Boolean isGross() {
        return isGross(getValueSet());
    }

    /**
     * Is the deposit taxFree.
     * @return true/false
     */
    public Boolean isTaxFree() {
        return isTaxFree(getValueSet());
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
    public static AccountCategory getCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, AccountCategory.class);
    }

    /**
     * Obtain DepositCurrency.
     * @param pValueSet the valueSet
     * @return the SecurityCurrency
     */
    public static AccountCurrency getDepositCurrency(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CURRENCY, AccountCurrency.class);
    }

    /**
     * Is the deposit gross.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isGross(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_GROSS, Boolean.class);
    }

    /**
     * Is the deposit taxFree.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isTaxFree(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXFREE, Boolean.class);
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
     * Set account category value.
     * @param pValue the value
     */
    private void setValueCategory(final AccountCategory pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set account category id.
     * @param pValue the value
     */
    private void setValueCategory(final Integer pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set account category name.
     * @param pValue the value
     */
    private void setValueCategory(final String pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set deposit currency value.
     * @param pValue the value
     */
    private void setValueCurrency(final AccountCurrency pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set deposit currency id.
     * @param pValue the value
     */
    private void setValueCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set deposit currency name.
     * @param pValue the value
     */
    private void setValueCurrency(final String pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set gross indication.
     * @param pValue the value
     */
    private void setValueGross(final Boolean pValue) {
        getValueSet().setValue(FIELD_GROSS, pValue);
    }

    /**
     * Set taxFree indication.
     * @param pValue the value
     */
    private void setValueTaxFree(final Boolean pValue) {
        getValueSet().setValue(FIELD_TAXFREE, pValue);
    }

    /**
     * Determines whether an deposit is deposit.
     * @return savings true/false
     */
    public boolean isDeposit() {
        /* Check for units */
        AccountCategory myCat = getCategory();
        return (myCat != null) && (myCat.getCategoryTypeClass().isDeposit());
    }

    @Override
    public Deposit getBase() {
        return (Deposit) super.getBase();
    }

    @Override
    public DepositList getList() {
        return (DepositList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pDeposit The Deposit to copy
     */
    protected Deposit(final DepositList pList,
                      final Deposit pDeposit) {
        /* Set standard values */
        super(pList, pDeposit);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private Deposit(final DepositList pList,
                    final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Category */
            Object myValue = pValues.getValue(FIELD_CATEGORY);
            if (myValue instanceof Integer) {
                setValueCategory((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueCategory((String) myValue);
            }

            /* Store the Parent */
            myValue = pValues.getValue(FIELD_PARENT);
            if (myValue instanceof Integer) {
                setValueParent((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueParent((String) myValue);
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

            /* Store the gross flag */
            myValue = pValues.getValue(FIELD_GROSS);
            if (myValue instanceof Boolean) {
                setValueGross((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueGross(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Store the taxFree flag */
            myValue = pValues.getValue(FIELD_TAXFREE);
            if (myValue instanceof Boolean) {
                setValueTaxFree((Boolean) myValue);
            } else if (myValue instanceof String) {
                setValueTaxFree(myFormatter.parseValue((String) myValue, Boolean.class));
            }

            /* Catch Exceptions */
        } catch (NumberFormatException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Deposit(final DepositList pList) {
        super(pList);
    }

    @Override
    public int compareTo(final Deposit pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the category */
        int iDiff = Difference.compareObject(getCategory(), pThat.getCategory());
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
        ValueSet myValues = getValueSet();
        resolveDataLink(FIELD_CATEGORY, myData.getAccountCategories());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
        resolveDataLink(FIELD_PARENT, myData.getPayees());

        /* Adjust Gross */
        Object myGross = myValues.getValue(FIELD_GROSS);
        if (myGross == null) {
            setValueGross(Boolean.FALSE);
        }

        /* Adjust TaxFree */
        Object myTaxFree = myValues.getValue(FIELD_TAXFREE);
        if (myTaxFree == null) {
            setValueTaxFree(Boolean.FALSE);
        }
    }

    /**
     * Set a new account category.
     * @param pCategory the new category
     */
    public void setAccountCategory(final AccountCategory pCategory) {
        setValueCategory(pCategory);
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

    @Override
    public void touchUnderlyingItems() {
        /* touch the category and currency */
        getCategory().touchItem(this);
        getDepositCurrency().touchItem(this);

        /* Touch parent if it exists */
        Payee myParent = getParent();
        if (myParent != null) {
            getParent().touchItem(this);
        }
    }

    @Override
    public void validate() {
        Payee myParent = getParent();
        AccountCategory myCategory = getCategory();
        AccountCurrency myCurrency = getDepositCurrency();
        AccountCategoryClass myClass = getCategoryClass();

        /* Validate base components */
        super.validate();

        /* Category must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            addError(ERROR_MISSING, FIELD_CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_CURRENCY);
        }

        /* Parent must be non-null */
        if (myParent == null) {
            if (myClass.isChild()) {
                addError(ERROR_MISSING, FIELD_PARENT);
            }

        } else if (!myClass.isChild()) {
            addError(ERROR_EXIST, FIELD_PARENT);

            /* If we are open then parent must be open */
        } else if (!isClosed() && myParent.isClosed()) {
            addError(ERROR_PARCLOSED, FIELD_CLOSED);
        }

        /* If the account is tax free, check that it is allowed */
        if ((isTaxFree()) && (!myClass.canTaxFree())) {
            addError(ERROR_TAXFREE, FIELD_TAXFREE);
        }

        /* If the account is gross interest, check that it is allowed */
        if ((isGross()) && (!myClass.canTaxFree())) {
            addError(ERROR_GROSS, FIELD_GROSS);
        }

        /* Cannot be both gross interest and taxFree */
        if ((isGross()) && (isTaxFree())) {
            addError(ERROR_TAXFREEGROSS, FIELD_TAXFREE);
            addError(ERROR_TAXFREEGROSS, FIELD_GROSS);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base deposit from an edited deposit.
     * @param pDeposit the edited deposit
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pDeposit) {
        /* Can only update from a deposit */
        if (!(pDeposit instanceof Deposit)) {
            return false;
        }
        Deposit myDeposit = (Deposit) pDeposit;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myDeposit);

        /* Update the category if required */
        if (!Difference.isEqual(getCategory(), myDeposit.getCategory())) {
            setValueCategory(myDeposit.getCategory());
        }

        /* Update the parent if required */
        if (!Difference.isEqual(getParent(), myDeposit.getParent())) {
            setValueParent(myDeposit.getParent());
        }

        /* Update the deposit currency if required */
        if (!Difference.isEqual(getDepositCurrency(), myDeposit.getDepositCurrency())) {
            setValueCurrency(myDeposit.getDepositCurrency());
        }

        /* Update the gross status if required */
        if (!Difference.isEqual(isGross(), myDeposit.isGross())) {
            setValueGross(myDeposit.isGross());
        }

        /* Update the taxFree status if required */
        if (!Difference.isEqual(isTaxFree(), myDeposit.isTaxFree())) {
            setValueTaxFree(myDeposit.isTaxFree());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Deposit List class.
     */
    public static class DepositList
            extends AssetBaseList<Deposit> {
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
            return Deposit.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Security list.
         * @param pData the DataSet for the list
         */
        public DepositList(final MoneyWiseData pData) {
            super(pData, Deposit.class, MoneyWiseDataType.DEPOSIT);
        }

        @Override
        protected DepositList getEmptyList(final ListStyle pStyle) {
            DepositList myList = new DepositList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DepositList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (DepositList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected DepositList(final DepositList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public DepositList deriveEditList() {
            /* Build an empty List */
            DepositList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the deposits */
            Iterator<Deposit> myIterator = iterator();
            while (myIterator.hasNext()) {
                Deposit myCurr = myIterator.next();

                /* Ignore deleted deposits */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked deposit and add it to the list */
                Deposit myDeposit = new Deposit(myList, myCurr);
                myList.append(myDeposit);
            }

            /* Return the list */
            return myList;
        }

        @Override
        public Deposit addCopyItem(final DataItem<?> pDeposit) {
            /* Can only clone a Deposit */
            if (!(pDeposit instanceof Deposit)) {
                throw new UnsupportedOperationException();
            }

            Deposit myDeposit = new Deposit(this, (Deposit) pDeposit);
            add(myDeposit);
            return myDeposit;
        }

        @Override
        public Deposit addNewItem() {
            Deposit myDeposit = new Deposit(this);
            add(myDeposit);
            return myDeposit;
        }

        @Override
        public Deposit addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the deposit */
            Deposit myDeposit = new Deposit(this, pValues);

            /* Check that this DepositId has not been previously added */
            if (!isIdUnique(myDeposit.getId())) {
                myDeposit.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myDeposit, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myDeposit);

            /* Return it */
            return myDeposit;
        }
    }
}
