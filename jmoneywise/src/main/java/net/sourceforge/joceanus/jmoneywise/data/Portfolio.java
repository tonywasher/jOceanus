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
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Portfolio class.
 */
public class Portfolio
        extends EncryptedItem<MoneyWiseDataType>
        implements Comparable<Portfolio> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.PORTFOLIO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.PORTFOLIO.getListName();

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Portfolio.class.getName());

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
     * Holding Field Id.
     */
    public static final JDataField FIELD_HOLDING = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataHolding"));

    /**
     * isTaxFree Field Id.
     */
    public static final JDataField FIELD_TAXFREE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataTaxFree"));

    /**
     * isClosed Field Id.
     */
    public static final JDataField FIELD_CLOSED = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataClosed"));

    /**
     * Holding Invalid Error Text.
     */
    private static final String ERROR_BADHOLD = NLS_BUNDLE.getString("ErrorBadHolding");

    /**
     * Holding Closed Error Text.
     */
    private static final String ERROR_HOLDCLOSED = NLS_BUNDLE.getString("ErrorHoldClosed");

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
        if (FIELD_HOLDING.equals(pField)) {
            return true;
        }
        if (FIELD_TAXFREE.equals(pField)) {
            return isTaxFree();
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
     * Obtain Holding.
     * @return the holding account
     */
    public Account getHolding() {
        return getHolding(getValueSet());
    }

    /**
     * Obtain HoldingId.
     * @return the holdingId
     */
    public Integer getHoldingId() {
        Account myHolding = getHolding();
        return (myHolding == null)
                                  ? null
                                  : myHolding.getId();
    }

    /**
     * Obtain HoldingName.
     * @return the holdingName
     */
    public String getHoldingName() {
        Account myHolding = getHolding();
        return (myHolding == null)
                                  ? null
                                  : myHolding.getName();
    }

    /**
     * Is the portfolio taxFree.
     * @return true/false
     */
    public Boolean isTaxFree() {
        return isTaxFree(getValueSet());
    }

    /**
     * Is the portfolio closed?
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
     * Obtain Holding.
     * @param pValueSet the valueSet
     * @return the Holding Account
     */
    public static Account getHolding(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HOLDING, Account.class);
    }

    /**
     * Is the portfolio taxFree.
     * @param pValueSet the valueSet
     * @return true/false
     */
    public static Boolean isTaxFree(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXFREE, Boolean.class);
    }

    /**
     * Is the portfolio closed?
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
     * Set holding value.
     * @param pValue the value
     */
    private void setValueHolding(final Account pValue) {
        getValueSet().setValue(FIELD_HOLDING, pValue);
    }

    /**
     * Set holding id.
     * @param pValue the value
     */
    private void setValueHolding(final Integer pValue) {
        getValueSet().setValue(FIELD_HOLDING, pValue);
    }

    /**
     * Set holding name.
     * @param pValue the value
     */
    private void setValueHolding(final String pValue) {
        getValueSet().setValue(FIELD_HOLDING, pValue);
    }

    /**
     * Set taxFree indication.
     * @param pValue the value
     */
    private void setValueTaxFree(final Boolean pValue) {
        getValueSet().setValue(FIELD_TAXFREE, pValue);
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
    public Portfolio getBase() {
        return (Portfolio) super.getBase();
    }

    @Override
    public PortfolioList getList() {
        return (PortfolioList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPortfolio The Portfolio to copy
     */
    protected Portfolio(final PortfolioList pList,
                        final Portfolio pPortfolio) {
        /* Set standard values */
        super(pList, pPortfolio);
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param pId the portfolio id
     * @param pControlId the control id
     * @param pName the Encrypted Name of the portfolio
     * @param pDesc the Encrypted Description of the portfolio
     * @param pHoldingId the Holding account id
     * @param pTaxFree is the portfolio taxFree?
     * @param pClosed is the portfolio closed?
     * @throws JOceanusException on error
     */
    protected Portfolio(final PortfolioList pList,
                        final Integer pId,
                        final Integer pControlId,
                        final byte[] pName,
                        final byte[] pDesc,
                        final Integer pHoldingId,
                        final Boolean pTaxFree,
                        final Boolean pClosed) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Set ControlId */
            setControlKey(pControlId);
            setValueHolding(pHoldingId);

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);

            /* Store flags */
            setValueClosed(pClosed);
            setValueTaxFree(pTaxFree);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param pId the id
     * @param pName the Name of the event category
     * @param pDesc the description of the category
     * @param pHolding the Holding account id
     * @param pTaxFree is the portfolio taxFree?
     * @param pClosed is the portfolio closed?
     * @throws JOceanusException on error
     */
    protected Portfolio(final PortfolioList pList,
                        final Integer pId,
                        final String pName,
                        final String pDesc,
                        final String pHolding,
                        final Boolean pTaxFree,
                        final Boolean pClosed) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Record the string values */
            setValueName(pName);
            setValueDesc(pDesc);
            setValueHolding(pHolding);

            /* Store flags */
            setValueClosed(pClosed);
            setValueTaxFree(pTaxFree);

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
    private Portfolio(final PortfolioList pList,
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

            /* Store the Holding */
            myValue = pValues.getValue(FIELD_HOLDING);
            if (myValue instanceof Integer) {
                setValueHolding((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueHolding((String) myValue);
            }

            /* Store the taxFree flag */
            myValue = pValues.getValue(FIELD_TAXFREE);
            if (myValue instanceof Boolean) {
                setValueTaxFree((Boolean) myValue);
            }

            /* Store the closed flag */
            myValue = pValues.getValue(FIELD_CLOSED);
            if (myValue instanceof Boolean) {
                setValueClosed((Boolean) myValue);
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
    public Portfolio(final PortfolioList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final Portfolio pThat) {
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

        /* Resolve holding account */
        MoneyWiseData myData = getDataSet();
        ValueSet myValues = getValueSet();
        resolveDataLink(FIELD_HOLDING, myData.getAccounts());

        /* Adjust TaxFree */
        Object myTaxFree = myValues.getValue(FIELD_TAXFREE);
        if (myTaxFree == null) {
            setValueTaxFree(Boolean.FALSE);
        }

        /* Adjust Closed */
        Object myClosed = myValues.getValue(FIELD_CLOSED);
        if (myClosed == null) {
            setValueClosed(Boolean.FALSE);
        }
    }

    /**
     * Set a new portfolio name.
     * @param pName the new name
     * @throws JOceanusException on error
     */
    public void setPortfolioName(final String pName) throws JOceanusException {
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
     * Set a new holding account.
     * @param pHolding the holding
     * @throws JOceanusException on error
     */
    public void setHolding(final Account pHolding) throws JOceanusException {
        setValueHolding(pHolding);
    }

    /**
     * Set a new closed indication.
     * @param isClosed the new closed indication
     */
    public void setClosed(final Boolean isClosed) {
        setValueClosed(isClosed);
    }

    /**
     * Set a new taxFree indication.
     * @param isTaxFree the new taxFree indication
     */
    public void setTaxFree(final Boolean isTaxFree) {
        setValueTaxFree(isTaxFree);
    }

    @Override
    public void validate() {
        PortfolioList myList = getList();
        Account myHolding = getHolding();
        String myName = getName();
        String myDesc = getDesc();

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

        /* Holding account must exist */
        if (myHolding == null) {
            addError(ERROR_MISSING, FIELD_HOLDING);
        } else {
            /* check that holding account is deposit */
            if (!myHolding.isDeposit()) {
                addError(ERROR_BADHOLD, FIELD_HOLDING);
            }

            /* If we are open then holding account must be open */
            if (!isClosed() && myHolding.isClosed()) {
                addError(ERROR_HOLDCLOSED, FIELD_HOLDING);
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
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
        Portfolio myPortfolio = (Portfolio) pPortfolio;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Name if required */
        if (!Difference.isEqual(getName(), myPortfolio.getName())) {
            setValueName(myPortfolio.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myPortfolio.getDesc())) {
            setValueDesc(myPortfolio.getDescField());
        }

        /* Update the holding account if required */
        if (!Difference.isEqual(getHolding(), myPortfolio.getHolding())) {
            setValueHolding(myPortfolio.getHolding());
        }

        /* Update the taxFree status if required */
        if (!Difference.isEqual(isTaxFree(), myPortfolio.isTaxFree())) {
            setValueTaxFree(myPortfolio.isTaxFree());
        }

        /* Update the closed status if required */
        if (!Difference.isEqual(isClosed(), myPortfolio.isClosed())) {
            setValueClosed(myPortfolio.isClosed());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Portfolio List class.
     */
    public static class PortfolioList
            extends EncryptedList<Portfolio, MoneyWiseDataType> {
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
            return Portfolio.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Construct an empty CORE Portfolio list.
         * @param pData the DataSet for the list
         */
        public PortfolioList(final MoneyWiseData pData) {
            super(Portfolio.class, pData, MoneyWiseDataType.PORTFOLIO, ListStyle.CORE);
        }

        @Override
        protected PortfolioList getEmptyList(final ListStyle pStyle) {
            PortfolioList myList = new PortfolioList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PortfolioList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (PortfolioList) super.cloneList(pDataSet);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected PortfolioList(final PortfolioList pSource) {
            super(pSource);
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public PortfolioList deriveEditList() {
            /* Build an empty List */
            PortfolioList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the portfolios */
            Iterator<Portfolio> myIterator = iterator();
            while (myIterator.hasNext()) {
                Portfolio myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked portfolio and add it to the list */
                Portfolio myPortfolio = new Portfolio(myList, myCurr);
                myList.append(myPortfolio);
            }

            /* Return the list */
            return myList;
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
                return null;
            }

            Portfolio myPortfolio = new Portfolio(this, (Portfolio) pPortfolio);
            add(myPortfolio);
            return myPortfolio;
        }

        /**
         * Add a new item to the edit list.
         * @return the new item
         */
        @Override
        public Portfolio addNewItem() {
            Portfolio myPortfolio = new Portfolio(this);
            add(myPortfolio);
            return myPortfolio;
        }

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The # of instances of the name
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<Portfolio> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Portfolio myCurr = myIterator.next();
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
        public Portfolio findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<Portfolio> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Portfolio myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Allow a portfolio to be added.
         * @param pId the id
         * @param pName the name
         * @param pDesc the description
         * @param pHolding the Holding account
         * @param pTaxFree is the portfolio taxFree?
         * @param pClosed is the portfolio closed?
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final String pName,
                                final String pDesc,
                                final String pHolding,
                                final Boolean pTaxFree,
                                final Boolean pClosed) throws JOceanusException {
            /* Create the portfolio */
            Portfolio myPortfolio = new Portfolio(this, pId, pName, pDesc, pHolding, pTaxFree, pClosed);

            /* Check that this PortfolioId has not been previously added */
            if (!isIdUnique(pId)) {
                myPortfolio.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPortfolio, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPortfolio);
        }

        /**
         * Load an Encrypted Portfolio.
         * @param pId the id
         * @param pControlId the control id
         * @param pName the encrypted name
         * @param pDesc the encrypted description
         * @param pHoldingId the Holding account id
         * @param pTaxFree is the portfolio taxFree?
         * @param pClosed is the portfolio closed?
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final byte[] pName,
                                  final byte[] pDesc,
                                  final Integer pHoldingId,
                                  final Boolean pTaxFree,
                                  final Boolean pClosed) throws JOceanusException {
            /* Create the portfolio */
            Portfolio myPortfolio = new Portfolio(this, pId, pControlId, pName, pDesc, pHoldingId, pTaxFree, pClosed);

            /* Check that this PortfolioId has not been previously added */
            if (!isIdUnique(pId)) {
                myPortfolio.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPortfolio, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPortfolio);
        }

        @Override
        public Portfolio addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the portfolio */
            Portfolio myPortfolio = new Portfolio(this, pValues);

            /* Check that this PortfolioId has not been previously added */
            if (!isIdUnique(myPortfolio.getId())) {
                myPortfolio.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myPortfolio, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPortfolio);

            /* Return it */
            return myPortfolio;
        }
    }
}
