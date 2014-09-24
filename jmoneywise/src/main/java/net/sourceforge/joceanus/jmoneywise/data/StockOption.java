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
import net.sourceforge.joceanus.jmetis.viewer.EncryptedData.EncryptedPrice;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionInfo.StockOptionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;

/**
 * StockOption class.
 */
public class StockOption
        extends AssetBase<StockOption>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.STOCKOPTION.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.STOCKOPTION.getListName();

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * Security Field Id.
     */
    public static final JDataField FIELD_SECURITY = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.SECURITY.getItemName());

    /**
     * Portfolio Field Id.
     */
    public static final JDataField FIELD_PORTFOLIO = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.PORTFOLIO.getItemName());

    /**
     * GrantDate Field Id.
     */
    public static final JDataField FIELD_GRANTDATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.STOCKOPTION_GRANTDATE.getValue());

    /**
     * Portfolio Field Id.
     */
    public static final JDataField FIELD_EXPIREDATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.STOCKOPTION_EXPIRYDATE.getValue());

    /**
     * Price Field Id.
     */
    public static final JDataField FIELD_PRICE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_PRICE.getValue());

    /**
     * StockOptionInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * New Option name.
     */
    private static final String NAME_NEWOPTION = MoneyWiseDataResource.STOCKOPTION_NEWACCOUNT.getValue();

    /**
     * Invalid Expire Date Error Text.
     */
    private static final String ERROR_BADEXPIRE = MoneyWiseDataResource.STOCKOPTION_ERROR_BADEXPIRE.getValue();

    /**
     * Invalid SecurityType Error Text.
     */
    private static final String ERROR_BADSECURITY = MoneyWiseDataResource.STOCKOPTION_ERROR_BADSECURITY.getValue();

    /**
     * Invalid currency error.
     */
    public static final String ERROR_CURRENCY = MoneyWiseDataResource.MONEYWISEDATA_ERROR_CURRENCY.getValue();

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * StockOptionInfoSet.
     */
    private final StockOptionInfoSet theInfoSet;

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_SECURITY.equals(pField)) {
            return true;
        }
        if (FIELD_PORTFOLIO.equals(pField)) {
            return true;
        }
        if (FIELD_GRANTDATE.equals(pField)) {
            return true;
        }
        if (FIELD_EXPIREDATE.equals(pField)) {
            return true;
        }
        if (FIELD_PRICE.equals(pField)) {
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
    public StockOptionInfoSet getInfoSet() {
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

    /**
     * Obtain Portfolio.
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return getPortfolio(getValueSet());
    }

    /**
     * Obtain PortfolioId.
     * @return the portfolioId
     */
    public Integer getPortfolioId() {
        Portfolio myPortfolio = getPortfolio();
        return (myPortfolio == null)
                                    ? null
                                    : myPortfolio.getId();
    }

    /**
     * Obtain PortfolioName.
     * @return the portfolioName
     */
    public String getPortfolioName() {
        Portfolio myPortfolio = getPortfolio();
        return (myPortfolio == null)
                                    ? null
                                    : myPortfolio.getName();
    }

    /**
     * Obtain Security.
     * @return the security
     */
    public Security getSecurity() {
        return getSecurity(getValueSet());
    }

    /**
     * Obtain SecurityId.
     * @return the securityId
     */
    public Integer getSecurityId() {
        Security mySecurity = getSecurity();
        return (mySecurity == null)
                                   ? null
                                   : mySecurity.getId();
    }

    /**
     * Obtain SecurityName.
     * @return the securityName
     */
    public String getSecurityName() {
        Security mySecurity = getSecurity();
        return (mySecurity == null)
                                   ? null
                                   : mySecurity.getName();
    }

    /**
     * Obtain GrantDate.
     * @return the grantDate
     */
    public JDateDay getGrantDate() {
        return getGrantDate(getValueSet());
    }

    /**
     * Obtain ExpiryDate.
     * @return the expiryDate
     */
    public JDateDay getExpiryDate() {
        return getExpiryDate(getValueSet());
    }

    /**
     * Obtain Price.
     * @return the price
     */
    public JPrice getPrice() {
        return getPrice(getValueSet());
    }

    /**
     * Obtain Encrypted price.
     * @return the bytes
     */
    public byte[] getPriceBytes() {
        return getPriceBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Price Field.
     * @return the Field
     */
    private EncryptedPrice getPriceField() {
        return getPriceField(getValueSet());
    }

    /**
     * Obtain Portfolio.
     * @param pValueSet the valueSet
     * @return the portfolio
     */
    public static Portfolio getPortfolio(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PORTFOLIO, Portfolio.class);
    }

    /**
     * Obtain Security.
     * @param pValueSet the valueSet
     * @return the security
     */
    public static Security getSecurity(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SECURITY, Security.class);
    }

    /**
     * Obtain GrantDate.
     * @param pValueSet the valueSet
     * @return the grantDate
     */
    public static JDateDay getGrantDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_GRANTDATE, JDateDay.class);
    }

    /**
     * Obtain ExpiryDate.
     * @param pValueSet the valueSet
     * @return the expiryDate
     */
    public static JDateDay getExpiryDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_EXPIREDATE, JDateDay.class);
    }

    /**
     * Obtain Price.
     * @param pValueSet the valueSet
     * @return the symbol
     */
    public static JPrice getPrice(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_PRICE, JPrice.class);
    }

    /**
     * Obtain Encrypted price.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getPriceBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_PRICE);
    }

    /**
     * Obtain Encrypted price field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedPrice getPriceField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PRICE, EncryptedPrice.class);
    }

    /**
     * Set portfolio value.
     * @param pValue the value
     */
    private void setValuePortfolio(final Portfolio pValue) {
        getValueSet().setValue(FIELD_PORTFOLIO, pValue);
    }

    /**
     * Set portfolio id.
     * @param pValue the value
     */
    private void setValuePortfolio(final Integer pValue) {
        getValueSet().setValue(FIELD_PORTFOLIO, pValue);
    }

    /**
     * Set portfolio name.
     * @param pValue the value
     */
    private void setValuePortfolio(final String pValue) {
        getValueSet().setValue(FIELD_PORTFOLIO, pValue);
    }

    /**
     * Set security value.
     * @param pValue the value
     */
    private void setValueSecurity(final Security pValue) {
        getValueSet().setValue(FIELD_SECURITY, pValue);
    }

    /**
     * Set security id.
     * @param pValue the value
     */
    private void setValueSecurity(final Integer pValue) {
        getValueSet().setValue(FIELD_SECURITY, pValue);
    }

    /**
     * Set security name.
     * @param pValue the value
     */
    private void setValueSecurity(final String pValue) {
        getValueSet().setValue(FIELD_SECURITY, pValue);
    }

    /**
     * Set grantDate value.
     * @param pValue the value
     */
    private void setValueGrantDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_GRANTDATE, pValue);
    }

    /**
     * Set expiryDate value.
     * @param pValue the value
     */
    private void setValueExpiryDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_EXPIREDATE, pValue);
    }

    /**
     * Set price value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValuePrice(final JPrice pValue) throws JOceanusException {
        setEncryptedValue(FIELD_PRICE, pValue);
    }

    /**
     * Set price value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValuePrice(final String pValue) throws JOceanusException {
        getValueSet().setValue(FIELD_PRICE, pValue);
    }

    /**
     * Set price value.
     * @param pValue the value
     */
    private void setValuePrice(final EncryptedPrice pValue) {
        getValueSet().setValue(FIELD_PRICE, pValue);
    }

    /**
     * Set price value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValuePrice(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_PRICE, pBytes, JPrice.class);
    }

    @Override
    public StockOption getBase() {
        return (StockOption) super.getBase();
    }

    @Override
    public StockOptionList getList() {
        return (StockOptionList) super.getList();
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
     * Copy Constructor.
     * @param pList the list
     * @param pOption The Option to copy
     */
    protected StockOption(final StockOptionList pList,
                          final StockOption pOption) {
        /* Set standard values */
        super(pList, pOption);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new StockOptionInfoSet(this, pList.getActInfoTypes(), pList.getStockOptionInfo());
                theInfoSet.cloneDataInfoSet(pOption.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new StockOptionInfoSet(this, pList.getActInfoTypes(), pList.getStockOptionInfo());
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
    private StockOption(final StockOptionList pList,
                        final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Security */
            Object myValue = pValues.getValue(FIELD_SECURITY);
            if (myValue instanceof Integer) {
                setValueSecurity((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueSecurity((String) myValue);
            }

            /* Store the Parent */
            myValue = pValues.getValue(FIELD_PORTFOLIO);
            if (myValue instanceof Integer) {
                setValuePortfolio((Integer) myValue);
            } else if (myValue instanceof String) {
                setValuePortfolio((String) myValue);
            }

            /* Store GrantDate */
            myValue = pValues.getValue(FIELD_GRANTDATE);
            if (myValue instanceof JDateDay) {
                setValueGrantDate((JDateDay) myValue);
            } else if (myValue instanceof String) {
                JDateDayFormatter myParser = myFormatter.getDateFormatter();
                setValueGrantDate(myParser.parseDateDay((String) myValue));
            }

            /* Store ExpiryDate */
            myValue = pValues.getValue(FIELD_EXPIREDATE);
            if (myValue instanceof JDateDay) {
                setValueExpiryDate((JDateDay) myValue);
            } else if (myValue instanceof String) {
                JDateDayFormatter myParser = myFormatter.getDateFormatter();
                setValueExpiryDate(myParser.parseDateDay((String) myValue));
            }

            /* Store the Price */
            myValue = pValues.getValue(FIELD_PRICE);
            if (myValue instanceof JPrice) {
                setValuePrice((JPrice) myValue);
            } else if (myValue instanceof byte[]) {
                setValuePrice((byte[]) myValue);
            } else if (myValue instanceof String) {
                String myString = (String) myValue;
                setValuePrice(myString);
                setValuePrice(myFormatter.parseValue(myString, JPrice.class));
            }

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }

        /* Create the InfoSet */
        theInfoSet = new StockOptionInfoSet(this, pList.getActInfoTypes(), pList.getStockOptionInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public StockOption(final StockOptionList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new StockOptionInfoSet(this, pList.getActInfoTypes(), pList.getStockOptionInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    @Override
    public int compareTo(final StockOption pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the portfolio */
        int iDiff = Difference.compareObject(getPortfolio(), pThat.getPortfolio());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the security */
        iDiff = Difference.compareObject(getSecurity(), pThat.getSecurity());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the grant date */
        iDiff = Difference.compareObject(getGrantDate(), pThat.getGrantDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying base */
        return super.compareTo(pThat);
    }

    /**
     * Set defaults.
     * @param pUpdateSet the update set
     * @throws JOceanusException on error
     */
    public void setDefaults(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Set values */
        SecurityList mySecurities = pUpdateSet.findDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
        PortfolioList myPortfolios = pUpdateSet.findDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
        setPortfolio(myPortfolios.getDefaultPortfolio());
        setSecurity(mySecurities.getDefaultStockOption());
        JDateDay myDate = new JDateDay();
        setGrantDate(myDate);
        myDate = new JDateDay(myDate);
        myDate.adjustYear(1);
        setExpiryDate(myDate);
        setPrice(JPrice.getWholeUnits(1, getSecurity().getSecurityCurrency().getCurrency()));
        setName(getList().getUniqueName(NAME_NEWOPTION));
        setClosed(Boolean.FALSE);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_SECURITY, myData.getSecurities());
        resolveDataLink(FIELD_PORTFOLIO, myData.getPortfolios());
    }

    @Override
    protected void resolveUpdateSetLinks(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
        /* Resolve details */
        SecurityList mySecurities = pUpdateSet.findDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
        PortfolioList myPortfolios = pUpdateSet.findDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
        resolveDataLink(FIELD_SECURITY, mySecurities);
        resolveDataLink(FIELD_PORTFOLIO, myPortfolios);
    }

    /**
     * Set a new portfolio.
     * @param pPortfolio the portfolio
     * @throws JOceanusException on error
     */
    public void setPortfolio(final Portfolio pPortfolio) throws JOceanusException {
        setValuePortfolio(pPortfolio);
    }

    /**
     * Set a new security.
     * @param pSecurity the security
     * @throws JOceanusException on error
     */
    public void setSecurity(final Security pSecurity) throws JOceanusException {
        setValueSecurity(pSecurity);
    }

    /**
     * Set a new grantDate.
     * @param pGrantDate the date
     * @throws JOceanusException on error
     */
    public void setGrantDate(final JDateDay pGrantDate) throws JOceanusException {
        setValueGrantDate(pGrantDate);
    }

    /**
     * Set a new expiryDate.
     * @param pExpiryDate the date
     * @throws JOceanusException on error
     */
    public void setExpiryDate(final JDateDay pExpiryDate) throws JOceanusException {
        setValueExpiryDate(pExpiryDate);
    }

    /**
     * Set a new price.
     * @param pPrice the price
     * @throws JOceanusException on error
     */
    public void setPrice(final JPrice pPrice) throws JOceanusException {
        setValuePrice(pPrice);
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
    public void touchUnderlyingItems() {
        /* touch the security and portfolio */
        getSecurity().touchItem(this);
        getPortfolio().touchItem(this);

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    @Override
    public void touchOnUpdate() {
        /* Reset touches from update set */
        clearTouches(MoneyWiseDataType.STOCKOPTIONVEST);

        /* Touch items */
        getPortfolio().touchItem(this);
        getSecurity().touchItem(this);
    }

    @Override
    public void validate() {
        Portfolio myPortfolio = getPortfolio();
        Security mySecurity = getSecurity();
        JDateDay myGrant = getGrantDate();
        JDateDay myExpiry = getExpiryDate();
        JPrice myPrice = getPrice();
        JDateDayRange myRange = getDataSet().getDateRange();

        /* Validate base components */
        super.validate();

        /* Security must be non-null */
        if (mySecurity == null) {
            addError(ERROR_MISSING, FIELD_SECURITY);
        } else if (!mySecurity.isSecurityClass(SecurityTypeClass.SHARES)) {
            addError(ERROR_BADSECURITY, FIELD_SECURITY);
        }

        /* Portfolio must be non-null */
        if (myPortfolio == null) {
            addError(ERROR_MISSING, FIELD_PORTFOLIO);
        }

        /* GrantDate must be non-null and within range */
        if (myGrant == null) {
            addError(ERROR_MISSING, FIELD_GRANTDATE);
        } else if (myRange.compareTo(myGrant) != 0) {
            addError(ERROR_RANGE, FIELD_GRANTDATE);

            /* ExpiryDate must be non-null and later than grantDate */
            if (myExpiry == null) {
                addError(ERROR_MISSING, FIELD_EXPIREDATE);
            } else if (myExpiry.compareTo(myGrant) <= 0) {
                addError(ERROR_BADEXPIRE, FIELD_EXPIREDATE);
            }
        }

        /* Price must be non-null */
        if (myPrice == null) {
            addError(ERROR_MISSING, FIELD_PRICE);

            /* Check that the units are non-zero and positive */
        } else if (myPrice.isZero()) {
            addError(ERROR_ZERO, FIELD_PRICE);
        } else if (!myPrice.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_PRICE);
        } else {
            /* Ensure that currency is correct */
            AccountCurrency myCurrency = mySecurity == null
                                                           ? null
                                                           : mySecurity.getSecurityCurrency();
            if ((myCurrency != null)
                && !myPrice.getCurrency().equals(myCurrency.getCurrency())) {
                addError(ERROR_CURRENCY, FIELD_PRICE);
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Update base options from an edited option.
     * @param pOption the edited option
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pOption) {
        /* Can only update from an option */
        if (!(pOption instanceof StockOption)) {
            return false;
        }
        StockOption myOption = (StockOption) pOption;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myOption);

        /* Update the security if required */
        if (!Difference.isEqual(getSecurity(), myOption.getSecurity())) {
            setValueSecurity(myOption.getSecurity());
        }

        /* Update the portfolio if required */
        if (!Difference.isEqual(getPortfolio(), myOption.getPortfolio())) {
            setValuePortfolio(myOption.getPortfolio());
        }

        /* Update the grantDate if required */
        if (!Difference.isEqual(getGrantDate(), myOption.getGrantDate())) {
            setValueGrantDate(myOption.getGrantDate());
        }

        /* Update the expiryDate if required */
        if (!Difference.isEqual(getExpiryDate(), myOption.getExpiryDate())) {
            setValueExpiryDate(myOption.getExpiryDate());
        }

        /* Update the price if required */
        if (!Difference.isEqual(getPrice(), myOption.getPrice())) {
            setValuePrice(myOption.getPriceField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void adjustMapForItem() {
        StockOptionList myList = getList();
        StockOptionDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The StockOption List class.
     */
    public static class StockOptionList
            extends AssetBaseList<StockOption> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The StockOptionInfo List.
         */
        private StockOptionInfoList theInfoList = null;

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
            return StockOption.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        @Override
        protected StockOptionDataMap getDataMap() {
            return (StockOptionDataMap) super.getDataMap();
        }

        /**
         * Obtain the stockOptionInfoList.
         * @return the stockOption info list
         */
        public StockOptionInfoList getStockOptionInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getStockOptionInfo();
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
         * Construct an empty CORE StockOption list.
         * @param pData the DataSet for the list
         */
        public StockOptionList(final MoneyWiseData pData) {
            super(pData, StockOption.class, MoneyWiseDataType.STOCKOPTION);
        }

        @Override
        protected StockOptionList getEmptyList(final ListStyle pStyle) {
            StockOptionList myList = new StockOptionList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected StockOptionList(final StockOptionList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @param pUpdateSet the updateSet
         * @return the edit list
         * @throws JOceanusException on error
         */
        public StockOptionList deriveEditList(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws JOceanusException {
            /* Build an empty List */
            StockOptionList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            StockOptionInfoList myOptionInfo = getStockOptionInfo();
            myList.theInfoList = myOptionInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the options */
            Iterator<StockOption> myIterator = iterator();
            while (myIterator.hasNext()) {
                StockOption myCurr = myIterator.next();

                /* Ignore deleted options */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked option and add it to the list */
                StockOption myOption = new StockOption(myList, myCurr);
                myOption.resolveUpdateSetLinks(pUpdateSet);
                myList.append(myOption);

                /* Adjust the map */
                myOption.adjustMapForItem();
            }

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pOption item
         * @return the newly added item
         */
        @Override
        public StockOption addCopyItem(final DataItem<?> pOption) {
            /* Can only clone an Option */
            if (!(pOption instanceof StockOption)) {
                throw new UnsupportedOperationException();
            }

            StockOption myOption = new StockOption(this, (StockOption) pOption);
            add(myOption);
            return myOption;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public StockOption addNewItem() {
            StockOption myOption = new StockOption(this);
            add(myOption);
            return myOption;
        }

        @Override
        public StockOption addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the option */
            StockOption myOption = new StockOption(this, pValues);

            /* Check that this OptionId has not been previously added */
            if (!isIdUnique(myOption.getId())) {
                myOption.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myOption, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myOption);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    DataValues<MoneyWiseDataType> myValues = myItem.getValues(myOption);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myOption;
        }

        @Override
        protected StockOptionDataMap allocateDataMap() {
            return new StockOptionDataMap();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class StockOptionDataMap
            extends DataInstanceMap<StockOption, MoneyWiseDataType, String> {
        @Override
        public void adjustForItem(final StockOption pItem) {
            /* Adjust name count */
            adjustForItem(pItem, pItem.getName());
        }

        /**
         * find item by name.
         * @param pName the name to look up
         * @return the matching item
         */
        public StockOption findItemByName(final String pName) {
            return findItemByKey(pName);
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
