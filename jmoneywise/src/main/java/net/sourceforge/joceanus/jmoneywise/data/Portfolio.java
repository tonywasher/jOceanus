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
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Portfolio class.
 */
public class Portfolio
        extends AssetBase<Portfolio> {
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
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AssetBase.FIELD_DEFS);

    /**
     * Holding Field Id.
     */
    public static final JDataField FIELD_HOLDING = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataHolding"));

    /**
     * isTaxFree Field Id.
     */
    public static final JDataField FIELD_TAXFREE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataTaxFree"));

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
    public boolean includeXmlField(final JDataField pField) {
        /* Determine whether fields should be included */
        if (FIELD_HOLDING.equals(pField)) {
            return true;
        }
        if (FIELD_TAXFREE.equals(pField)) {
            return isTaxFree();
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    /**
     * Obtain Holding.
     * @return the holding account
     */
    public Deposit getHolding() {
        return getHolding(getValueSet());
    }

    /**
     * Obtain HoldingId.
     * @return the holdingId
     */
    public Integer getHoldingId() {
        Deposit myHolding = getHolding();
        return (myHolding == null)
                                  ? null
                                  : myHolding.getId();
    }

    /**
     * Obtain HoldingName.
     * @return the holdingName
     */
    public String getHoldingName() {
        Deposit myHolding = getHolding();
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
     * Obtain Holding.
     * @param pValueSet the valueSet
     * @return the Holding Account
     */
    public static Deposit getHolding(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HOLDING, Deposit.class);
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
     * Set holding value.
     * @param pValue the value
     */
    private void setValueHolding(final Deposit pValue) {
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
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws JOceanusException on error
     */
    private Portfolio(final PortfolioList pList,
                      final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access formatter */
        JDataFormatter myFormatter = getDataSet().getDataFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Holding */
            Object myValue = pValues.getValue(FIELD_HOLDING);
            if (myValue instanceof Integer) {
                setValueHolding((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueHolding((String) myValue);
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
    public Portfolio(final PortfolioList pList) {
        super(pList);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Base details */
        super.resolveDataSetLinks();

        /* Resolve holding account */
        MoneyWiseData myData = getDataSet();
        ValueSet myValues = getValueSet();
        resolveDataLink(FIELD_HOLDING, myData.getDeposits());

        /* Adjust TaxFree */
        Object myTaxFree = myValues.getValue(FIELD_TAXFREE);
        if (myTaxFree == null) {
            setValueTaxFree(Boolean.FALSE);
        }
    }

    /**
     * Set a new holding account.
     * @param pHolding the holding
     * @throws JOceanusException on error
     */
    public void setHolding(final Deposit pHolding) throws JOceanusException {
        setValueHolding(pHolding);
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
        Deposit myHolding = getHolding();

        /* Validate base components */
        super.validate();

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

        /* Apply basic changes */
        applyBasicChanges(myPortfolio);

        /* Update the holding account if required */
        if (!Difference.isEqual(getHolding(), myPortfolio.getHolding())) {
            setValueHolding(myPortfolio.getHolding());
        }

        /* Update the taxFree status if required */
        if (!Difference.isEqual(isTaxFree(), myPortfolio.isTaxFree())) {
            setValueTaxFree(myPortfolio.isTaxFree());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Portfolio List class.
     */
    public static class PortfolioList
            extends AssetBaseList<Portfolio> {
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
            super(pData, Portfolio.class, MoneyWiseDataType.PORTFOLIO);
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

                /* Ignore deleted portfolios */
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
                throw new UnsupportedOperationException();
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
