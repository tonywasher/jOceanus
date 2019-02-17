/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType.PortfolioTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Portfolio class.
 */
public class Portfolio
        extends AssetBase<Portfolio>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.PORTFOLIO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.PORTFOLIO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * PortfolioType Field Id.
     */
    public static final MetisField FIELD_PORTTYPE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.PORTFOLIOTYPE.getItemName(), MetisDataType.LINK);

    /**
     * Parent Field Id.
     */
    public static final MetisField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.ASSET_PARENT.getValue(), MetisDataType.LINK);

    /**
     * Currency Field Id.
     */
    public static final MetisField FIELD_CURRENCY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.CURRENCY.getItemName(), MetisDataType.LINK);

    /**
     * PortfolioInfoSet field Id.
     */
    private static final MetisField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseDataResource.PORTFOLIO_NEWACCOUNT.getValue();

    /**
     * Portfolio Cash account.
     */
    public static final String NAME_CASHACCOUNT = MoneyWiseDataResource.PORTFOLIO_CASHACCOUNT.getValue();

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * PortfolioInfoSet.
     */
    private final PortfolioInfoSet theInfoSet;

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPortfolio The Portfolio to copy
     */
    protected Portfolio(final PortfolioList pList,
                        final Portfolio pPortfolio) {
        /* Set standard values */
        super(pList, pPortfolio);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new PortfolioInfoSet(this, pList.getActInfoTypes(), pList.getPortfolioInfo());
                theInfoSet.cloneDataInfoSet(pPortfolio.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new PortfolioInfoSet(this, pList.getActInfoTypes(), pList.getPortfolioInfo());
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
    private Portfolio(final PortfolioList pList,
                      final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Store the PortfolioType */
            Object myValue = pValues.getValue(FIELD_PORTTYPE);
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
        } catch (NumberFormatException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }

        /* Create the InfoSet */
        theInfoSet = new PortfolioInfoSet(this, pList.getActInfoTypes(), pList.getPortfolioInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Portfolio(final PortfolioList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new PortfolioInfoSet(this, pList.getActInfoTypes(), pList.getPortfolioInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisField pField) {
        /* Determine whether fields should be included */
        if (FIELD_PORTTYPE.equals(pField)) {
            return true;
        }
        if (FIELD_PARENT.equals(pField)) {
            return true;
        }
        if (FIELD_CURRENCY.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                              ? theInfoSet
                              : MetisDataFieldValue.SKIP;
        }

        /* Handle infoSet fields */
        final AccountInfoClass myClass = PortfolioInfoSet.getClassForField(pField);
        if (theInfoSet != null
            && myClass != null) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public PortfolioInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain WebSite.
     * @return the webSite
     */
    public char[] getWebSite() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.WEBSITE, char[].class)
                          : null;
    }

    /**
     * Obtain CustNo.
     * @return the customer #
     */
    public char[] getCustNo() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.CUSTOMERNO, char[].class)
                          : null;
    }

    /**
     * Obtain UserId.
     * @return the userId
     */
    public char[] getUserId() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.USERID, char[].class)
                          : null;
    }

    /**
     * Obtain Password.
     * @return the password
     */
    public char[] getPassword() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.PASSWORD, char[].class)
                          : null;
    }

    /**
     * Obtain SortCode.
     * @return the sort code
     */
    public char[] getSortCode() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.SORTCODE, char[].class)
                          : null;
    }

    /**
     * Obtain Reference.
     * @return the reference
     */
    public char[] getReference() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.REFERENCE, char[].class)
                          : null;
    }

    /**
     * Obtain Account.
     * @return the account
     */
    public char[] getAccount() {
        return hasInfoSet
                          ? theInfoSet.getValue(AccountInfoClass.ACCOUNT, char[].class)
                          : null;
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
        final Payee myParent = getParent();
        return myParent == null
                                ? null
                                : myParent.getId();
    }

    /**
     * Obtain ParentName.
     * @return the parentName
     */
    public String getParentName() {
        final Payee myParent = getParent();
        return myParent == null
                                ? null
                                : myParent.getName();
    }

    /**
     * Obtain Portfolio Type.
     * @return the type
     */
    public PortfolioType getPortfolioType() {
        return getPortfolioType(getValueSet());
    }

    /**
     * Obtain PortfolioTypeId.
     * @return the portfolioTypeId
     */
    public Integer getPortfolioTypeId() {
        final PortfolioType myType = getPortfolioType();
        return myType == null
                              ? null
                              : myType.getId();
    }

    /**
     * Obtain PortfolioTypeName.
     * @return the portfolioTypeName
     */
    public String getPortfolioTypeName() {
        final PortfolioType myType = getPortfolioType();
        return myType == null
                              ? null
                              : myType.getName();
    }

    /**
     * Obtain PortfolioTypeClass.
     * @return the portfolioTypeClass
     */
    public PortfolioTypeClass getPortfolioTypeClass() {
        final PortfolioType myType = getPortfolioType();
        return myType == null
                              ? null
                              : myType.getPortfolioClass();
    }

    @Override
    public AssetCurrency getAssetCurrency() {
        return getAssetCurrency(getValueSet());
    }

    @Override
    public Boolean isTaxFree() {
        final PortfolioType myType = getPortfolioType();
        return myType == null
                              ? Boolean.FALSE
                              : myType.getPortfolioClass().isTaxFree();
    }

    @Override
    public Boolean isForeign() {
        final AssetCurrency myDefault = getDataSet().getDefaultCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    /**
     * Obtain Parent.
     * @param pValueSet the valueSet
     * @return the Parent
     */
    public static Payee getParent(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, Payee.class);
    }

    /**
     * Obtain PortfolioType.
     * @param pValueSet the valueSet
     * @return the PortfolioType
     */
    public static PortfolioType getPortfolioType(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PORTTYPE, PortfolioType.class);
    }

    /**
     * Obtain PortfolioCurrency.
     * @param pValueSet the valueSet
     * @return the PortfolioCurrency
     */
    public static AssetCurrency getAssetCurrency(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CURRENCY, AssetCurrency.class);
    }

    /**
     * Is this portfolio the required class.
     * @param pClass the required portfolio class.
     * @return true/false
     */
    public boolean isPortfolioClass(final PortfolioTypeClass pClass) {
        /* Check for match */
        return getPortfolioTypeClass() == pClass;
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
     * Set portfolio type value.
     * @param pValue the value
     */
    private void setValueType(final PortfolioType pValue) {
        getValueSet().setValue(FIELD_PORTTYPE, pValue);
    }

    /**
     * Set portfolio type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_PORTTYPE, pValue);
    }

    /**
     * Set portfolio type name.
     * @param pValue the value
     */
    private void setValueType(final String pValue) {
        getValueSet().setValue(FIELD_PORTTYPE, pValue);
    }

    /**
     * Set portfolio currency value.
     * @param pValue the value
     */
    private void setValueCurrency(final AssetCurrency pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set portfolio currency id.
     * @param pValue the value
     */
    private void setValueCurrency(final Integer pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    /**
     * Set portfolio currency name.
     * @param pValue the value
     */
    private void setValueCurrency(final String pValue) {
        getValueSet().setValue(FIELD_CURRENCY, pValue);
    }

    @Override
    public Portfolio getBase() {
        return (Portfolio) super.getBase();
    }

    @Override
    public PortfolioList getList() {
        return (PortfolioList) super.getList();
    }

    @Override
    public MetisDataState getState() {
        /* Pop history for self */
        MetisDataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == MetisDataState.CLEAN) && useInfoSet) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public MetisDataEditState getEditState() {
        /* Pop history for self */
        MetisDataEditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if (myState == MetisDataEditState.CLEAN
            && useInfoSet) {
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
    public MetisDataDifference fieldChanged(final MetisField pField) {
        /* Handle InfoSet fields */
        final AccountInfoClass myClass = PortfolioInfoSet.getClassForField(pField);
        if (myClass != null) {
            return useInfoSet
                              ? theInfoSet.fieldChanged(myClass)
                              : MetisDataDifference.IDENTICAL;
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

    @Override
    public void deRegister() {
        final SecurityHoldingMap myMap = getDataSet().getSecurityHoldingsMap();
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
        setPortfolioType(getDefaultPortfolioType());
        setParent(getDefaultParent(pUpdateSet));
        setAssetCurrency(getDataSet().getDefaultCurrency());
        setClosed(Boolean.FALSE);
    }

    /**
     * Obtain default parent for portfolio.
     * @param pUpdateSet the update set
     * @return the default parent
     */
    private static Payee getDefaultParent(final UpdateSet<MoneyWiseDataType> pUpdateSet) {
        /* loop through the payees */
        final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted and closed payees and those that cannot parent this portfolio */
            boolean bIgnore = myPayee.isDeleted() || myPayee.isClosed();
            bIgnore |= !myPayee.getPayeeTypeClass().canParentPortfolio();
            if (!bIgnore) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }

    /**
     * Obtain portfolio type for new portfolio account.
     * @return the security type
     */
    public PortfolioType getDefaultPortfolioType() {
        /* loop through the portfolio types */
        final PortfolioTypeList myTypes = getDataSet().getPortfolioTypes();
        final Iterator<PortfolioType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final PortfolioType myType = myIterator.next();

            /* Ignore deleted types */
            if (!myType.isDeleted()) {
                return myType;
            }
        }

        /* Return no category */
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
            && (pThat instanceof Portfolio)) {
            /* Check the portfolio type */
            final Portfolio myThat = (Portfolio) pThat;
            iDiff = MetisDataDifference.compareObject(getPortfolioType(), myThat.getPortfolioType());
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
        /* Update the Base details */
        super.resolveDataSetLinks();

        /* Resolve holding account */
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_PARENT, myData.getPayees());
        resolveDataLink(FIELD_PORTTYPE, myData.getPortfolioTypes());
        resolveDataLink(FIELD_CURRENCY, myData.getAccountCurrencies());
    }

    @Override
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* Resolve parent/holding within list */
        final MoneyWiseData myData = getDataSet();
        final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
        resolveDataLink(FIELD_PORTTYPE, myData.getPortfolioTypes());
        resolveDataLink(FIELD_PARENT, myPayees);
    }

    /**
     * Set a new parent.
     * @param pParent the parent
     */
    public void setParent(final Payee pParent) {
        setValueParent(pParent);
    }

    /**
     * Set a new portfolio type.
     * @param pType the new type
     */
    public void setPortfolioType(final PortfolioType pType) {
        setValueType(pType);
    }

    /**
     * Set a new portfolio currency.
     * @param pCurrency the new currency
     */
    public void setAssetCurrency(final AssetCurrency pCurrency) {
        setValueCurrency(pCurrency);
    }

    /**
     * Set a new WebSite.
     * @param pWebSite the new webSite
     * @throws OceanusException on error
     */
    public void setWebSite(final char[] pWebSite) throws OceanusException {
        setInfoSetValue(AccountInfoClass.WEBSITE, pWebSite);
    }

    /**
     * Set a new CustNo.
     * @param pCustNo the new custNo
     * @throws OceanusException on error
     */
    public void setCustNo(final char[] pCustNo) throws OceanusException {
        setInfoSetValue(AccountInfoClass.CUSTOMERNO, pCustNo);
    }

    /**
     * Set a new UserId.
     * @param pUserId the new userId
     * @throws OceanusException on error
     */
    public void setUserId(final char[] pUserId) throws OceanusException {
        setInfoSetValue(AccountInfoClass.USERID, pUserId);
    }

    /**
     * Set a new Password.
     * @param pPassword the new password
     * @throws OceanusException on error
     */
    public void setPassword(final char[] pPassword) throws OceanusException {
        setInfoSetValue(AccountInfoClass.PASSWORD, pPassword);
    }

    /**
     * Set a new SortCode.
     * @param pSortCode the new sort code
     * @throws OceanusException on error
     */
    public void setSortCode(final char[] pSortCode) throws OceanusException {
        setInfoSetValue(AccountInfoClass.SORTCODE, pSortCode);
    }

    /**
     * Set a new Account.
     * @param pAccount the new account
     * @throws OceanusException on error
     */
    public void setAccount(final char[] pAccount) throws OceanusException {
        setInfoSetValue(AccountInfoClass.ACCOUNT, pAccount);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws OceanusException on error
     */
    public void setReference(final char[] pReference) throws OceanusException {
        setInfoSetValue(AccountInfoClass.REFERENCE, pReference);
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
            throw new MoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    @Override
    public TransactionCategory getDetailedCategory(final TransactionCategory pCategory,
                                                   final MoneyWiseTaxCredit pYear) {
        /* Switch on category type */
        final TransactionCategoryList myCategories = getDataSet().getTransCategories();
        switch (pCategory.getCategoryTypeClass()) {
            case INTEREST:
                if (isTaxFree()) {
                    return myCategories.getSingularClass(TransactionCategoryClass.TAXFREEINTEREST);
                }
                return myCategories.getSingularClass(isGross()
                                                     || !pYear.isTaxCreditRequired()
                                                                                     ? TransactionCategoryClass.GROSSINTEREST
                                                                                     : TransactionCategoryClass.TAXEDINTEREST);
            case LOYALTYBONUS:
                if (isTaxFree()) {
                    return myCategories.getSingularClass(TransactionCategoryClass.TAXFREELOYALTYBONUS);
                }
                return myCategories.getSingularClass(isGross()
                                                               ? TransactionCategoryClass.GROSSLOYALTYBONUS
                                                               : TransactionCategoryClass.TAXEDLOYALTYBONUS);
            case DIVIDEND:
                return isTaxFree()
                                   ? myCategories.getSingularClass(TransactionCategoryClass.TAXFREEDIVIDEND)
                                   : pCategory;
            default:
                return pCategory;
        }
    }

    @Override
    public void touchUnderlyingItems() {
        /* Touch parent and currency */
        getPortfolioType().touchItem(this);
        getParent().touchItem(this);
        getAssetCurrency().touchItem(this);

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    @Override
    public void touchOnUpdate() {
        /* Touch parent */
        getParent().touchItem(this);
    }

    @Override
    public void validate() {
        final PortfolioList myList = getList();
        final Payee myParent = getParent();
        final PortfolioType myPortType = getPortfolioType();
        final AssetCurrency myCurrency = getAssetCurrency();

        /* Validate base components */
        super.validate();

        /* PortfolioType must be non-null */
        if (myPortType == null) {
            addError(ERROR_MISSING, FIELD_PORTTYPE);
        } else {
            /* Access the class */
            final PortfolioTypeClass myClass = myPortType.getPortfolioClass();

            /* PortfolioType must be enabled */
            if (!myPortType.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_PORTTYPE);
            }

            /* If the PortfolioType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                final PortfolioDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
                    addError(ERROR_MULT, FIELD_PORTTYPE);
                }
            }
        }

        /* Parent account must exist */
        if (myParent == null) {
            addError(ERROR_MISSING, FIELD_PARENT);
        } else {
            /* Parent must be suitable */
            final PayeeTypeClass myParClass = myParent.getPayeeTypeClass();
            if (!myParClass.canParentPortfolio()) {
                addError(ERROR_BADPARENT, FIELD_PARENT);
            }

            /* If we are open then parent must be open */
            if (!isClosed() && myParent.isClosed()) {
                addError(ERROR_PARCLOSED, FIELD_CLOSED);
            }
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            addError(ERROR_MISSING, FIELD_CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_CURRENCY);
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

        /* Check that the name does not contain invalid characters */
        if (pName.contains(SecurityHolding.SECURITYHOLDING_SEP)) {
            addError(ERROR_INVALIDCHAR, FIELD_NAME);
        }
    }

    /**
     * Update base portfolio from an edited portfolio.
     * @param pPortfolio the edited portfolio
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pPortfolio) {
        /* Can only update from a portfolio */
        if (!(pPortfolio instanceof Portfolio)) {
            return false;
        }
        final Portfolio myPortfolio = (Portfolio) pPortfolio;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myPortfolio);

        /* Update the category type if required */
        if (!MetisDataDifference.isEqual(getPortfolioType(), myPortfolio.getPortfolioType())) {
            setValueType(myPortfolio.getPortfolioType());
        }

        /* Update the parent account if required */
        if (!MetisDataDifference.isEqual(getParent(), myPortfolio.getParent())) {
            setValueParent(myPortfolio.getParent());
        }

        /* Update the portfolio currency if required */
        if (!MetisDataDifference.isEqual(getAssetCurrency(), myPortfolio.getAssetCurrency())) {
            setValueCurrency(myPortfolio.getAssetCurrency());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        final PortfolioList myList = getList();
        final PortfolioDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Portfolio List class.
     */
    public static class PortfolioList
            extends AssetBaseList<Portfolio> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PortfolioList> FIELD_DEFS = MetisFieldSet.newFieldSet(PortfolioList.class);

        /**
         * The PortfolioInfo List.
         */
        private PortfolioInfoList theInfoList;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList;

        /**
         * Construct an empty CORE Portfolio list.
         * @param pData the DataSet for the list
         */
        public PortfolioList(final MoneyWiseData pData) {
            super(pData, Portfolio.class, MoneyWiseDataType.PORTFOLIO);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected PortfolioList(final PortfolioList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<PortfolioList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return Portfolio.FIELD_DEFS;
        }

        @Override
        protected PortfolioDataMap getDataMap() {
            return (PortfolioDataMap) super.getDataMap();
        }

        /**
         * Obtain the portfolioInfoList.
         * @return the portfolio info list
         */
        public PortfolioInfoList getPortfolioInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getPortfolioInfo();
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
        protected PortfolioList getEmptyList(final ListStyle pStyle) {
            final PortfolioList myList = new PortfolioList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws OceanusException on error
         */
        public PortfolioList deriveEditList(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
            /* Build an empty List */
            final PortfolioList myList = getEmptyList(ListStyle.EDIT);
            final PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);
            myList.ensureMap(myPayees);

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            final PortfolioInfoList myPortInfo = getPortfolioInfo();
            myList.theInfoList = myPortInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the portfolios */
            final Iterator<Portfolio> myIterator = iterator();
            while (myIterator.hasNext()) {
                final Portfolio myCurr = myIterator.next();

                /* Ignore deleted portfolios */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked portfolio and add it to the list */
                final Portfolio myPortfolio = new Portfolio(myList, myCurr);
                myPortfolio.resolveUpdateSetLinks(pUpdateSet);
                myList.add(myPortfolio);

                /* Adjust the map */
                myPortfolio.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        @Override
        public Portfolio findItemByName(final String pName) {
            /* look up the name in the map */
            return getDataMap().findItemByName(pName);
        }

        @Override
        protected boolean checkAvailableName(final String pName) {
            /* check availability in map */
            return getDataMap().availableName(pName);
        }

        @Override
        protected boolean validNameCount(final String pName) {
            /* check availability in map */
            return getDataMap().validNameCount(pName);
        }

        /**
         * Add a new item to the core list.
         * @param pPortfolio item
         * @return the newly added item
         */
        @Override
        public Portfolio addCopyItem(final DataItem<?> pPortfolio) {
            /* Can only clone a Portfolio */
            if (!(pPortfolio instanceof Portfolio)) {
                throw new UnsupportedOperationException();
            }

            final Portfolio myPortfolio = new Portfolio(this, (Portfolio) pPortfolio);
            add(myPortfolio);
            return myPortfolio;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public Portfolio addNewItem() {
            final Portfolio myPortfolio = new Portfolio(this);
            add(myPortfolio);
            return myPortfolio;
        }

        /**
         * Obtain the first portfolio for the specified class.
         * @param pClass the portfolio class
         * @return the portfolio
         */
        public Portfolio getSingularClass(final PortfolioTypeClass pClass) {
            /* Lookup in the map */
            return getDataMap().findSingularItem(pClass);
        }

        @Override
        public Portfolio addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the portfolio */
            final Portfolio myPortfolio = new Portfolio(this, pValues);

            /* Check that this PortfolioId has not been previously added */
            if (!isIdUnique(myPortfolio.getId())) {
                myPortfolio.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myPortfolio, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myPortfolio);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                final Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    final InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    final DataValues<MoneyWiseDataType> myValues = myItem.getValues(myPortfolio);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myPortfolio;
        }

        /**
         * Ensure Map based on the payee list.
         * @param pPayees the payee list
         */
        private void ensureMap(final PayeeList pPayees) {
            setDataMap(new PortfolioDataMap(pPayees));
        }

        @Override
        protected PortfolioDataMap allocateDataMap() {
            return new PortfolioDataMap(getDataSet().getPayees());
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Resolve links and sort the data */
            super.resolveDataSetLinks();
            reSort();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class PortfolioDataMap
            implements DataMapItem<Portfolio, MoneyWiseDataType>, MetisFieldItem {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PortfolioDataMap> FIELD_DEFS = MetisFieldSet.newFieldSet(PortfolioDataMap.class);

        /**
         * UnderlyingMap Field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_UNDERLYING, PortfolioDataMap::getUnderlyingMap);
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_SINGULARMAP, PortfolioDataMap::getPortfolioMap);
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_MAP_SINGULARCOUNTS, PortfolioDataMap::getPortfolioCountMap);
        }

        /**
         * The assetMap.
         */
        private AssetDataMap theUnderlyingMap;

        /**
         * Map of category counts.
         */
        private final Map<Integer, Integer> thePortfolioCountMap;

        /**
         * Map of singular categories.
         */
        private final Map<Integer, Portfolio> thePortfolioMap;

        /**
         * Constructor.
         * @param pPayees the payee list
         */
        protected PortfolioDataMap(final PayeeList pPayees) {
            /* Access underlying nameMap */
            theUnderlyingMap = pPayees.getDataMap().getUnderlyingMap();

            /* Create the maps */
            thePortfolioCountMap = new HashMap<>();
            thePortfolioMap = new HashMap<>();
        }

        @Override
        public MetisFieldSet<PortfolioDataMap> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        /**
         * Obtain the underlying map.
         * @return the underlying map
         */
        private AssetDataMap getUnderlyingMap() {
            return theUnderlyingMap;
        }

        /**
         * Obtain the underlying map.
         * @return the underlying map
         */
        private Map<Integer, Portfolio> getPortfolioMap() {
            return thePortfolioMap;
        }

        /**
         * Obtain the underlying map.
         * @return the underlying map
         */
        private Map<Integer, Integer> getPortfolioCountMap() {
            return thePortfolioCountMap;
        }

        @Override
        public void resetMap() {
            thePortfolioCountMap.clear();
            thePortfolioMap.clear();
        }

        @Override
        public void adjustForItem(final Portfolio pItem) {
            /* If the class is singular */
            final PortfolioTypeClass myClass = pItem.getPortfolioTypeClass();
            if (myClass.isSingular()) {
                /* Adjust category count */
                final Integer myId = myClass.getClassId();
                final Integer myCount = thePortfolioCountMap.get(myId);
                if (myCount == null) {
                    thePortfolioCountMap.put(myId, DataInstanceMap.ONE);
                } else {
                    thePortfolioCountMap.put(myId, myCount + 1);
                }

                /* Adjust portfolio map */
                thePortfolioMap.put(myId, pItem);
            }

            /* Adjust name count */
            theUnderlyingMap.adjustForItem(pItem);
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public Portfolio findItemByName(final String pName) {
            final AssetBase<?> myAsset = theUnderlyingMap.findAssetByName(pName);
            return myAsset instanceof Portfolio
                                                ? (Portfolio) myAsset
                                                : null;
        }

        /**
         * Check validity of name.
         * @param pName the name to look up
         * @return true/false
         */
        public boolean validNameCount(final String pName) {
            return theUnderlyingMap.validNameCount(pName);
        }

        /**
         * Check availability of name.
         * @param pName the key to look up
         * @return true/false
         */
        public boolean availableName(final String pName) {
            return theUnderlyingMap.availableKey(pName);
        }

        /**
         * find singular item.
         * @param pClass the class to look up
         * @return the matching item
         */
        public Portfolio findSingularItem(final PortfolioTypeClass pClass) {
            return thePortfolioMap.get(pClass.getClassId());
        }

        /**
         * Check validity of singular count.
         * @param pClass the class to look up
         * @return true/false
         */
        public boolean validSingularCount(final PortfolioTypeClass pClass) {
            final Integer myResult = thePortfolioCountMap.get(pClass.getClassId());
            return DataInstanceMap.ONE.equals(myResult);
        }
    }
}
