/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a portfolio.
 * @author Tony Washer
 */
public class PortfolioInfo
        extends DataInfo<PortfolioInfo, Portfolio, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.PORTFOLIOINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.PORTFOLIOINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected PortfolioInfo(final PortfolioInfoList pList,
                            final PortfolioInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pPortfolio the portfolio
     * @param pType the type
     */
    private PortfolioInfo(final PortfolioInfoList pList,
                          final Portfolio pPortfolio,
                          final AccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pPortfolio);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private PortfolioInfo(final PortfolioInfoList pList,
                          final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getPortfolios());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Access the PortfolioInfoSet and register this data */
            PortfolioInfoSet mySet = getOwner().getInfoSet();
            mySet.registerInfo(this);

        } catch (OceanusException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public AccountInfoType getInfoType() {
        return getInfoType(getValueSet(), AccountInfoType.class);
    }

    @Override
    public AccountInfoClass getInfoClass() {
        return getInfoType().getInfoClass();
    }

    @Override
    public Portfolio getOwner() {
        return getOwner(getValueSet(), Portfolio.class);
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the InfoType
     */
    public static AccountInfoType getInfoType(final MetisValueSet pValueSet) {
        return getInfoType(pValueSet, AccountInfoType.class);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public PortfolioInfo getBase() {
        return (PortfolioInfo) super.getBase();
    }

    @Override
    public PortfolioInfoList getList() {
        return (PortfolioInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the PortfolioInfoSet and register this value */
        PortfolioInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final DataInfo<PortfolioInfo, Portfolio, AccountInfoType, AccountInfoClass, MoneyWiseDataType> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Portfolios */
        int iDiff = getOwner().compareTo(pThat.getOwner());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the Info Types */
        iDiff = getInfoType().compareTo(pThat.getInfoType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
        resolveDataLink(FIELD_OWNER, myData.getPortfolios());

        /* Access the PortfolioInfoSet and register this data */
        PortfolioInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Update portfolioInfo from a portfolioInfo extract.
     * @param pInfo the changed portfolioInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pInfo) {
        /* Can only update from PortfolioInfo */
        if (!(pInfo instanceof PortfolioInfo)) {
            return false;
        }

        /* Access as PortfolioInfo */
        PortfolioInfo myPortInfo = (PortfolioInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDifference.isEqual(getField(), myPortInfo.getField())) {
            setValueValue(myPortInfo.getField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * PortfolioInfoList.
     */
    public static class PortfolioInfoList
            extends DataInfoList<PortfolioInfo, Portfolio, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, DataInfoList.FIELD_DEFS);

        /**
         * Construct an empty CORE info list.
         * @param pData the DataSet for the list
         */
        protected PortfolioInfoList(final MoneyWiseData pData) {
            super(PortfolioInfo.class, pData, MoneyWiseDataType.PORTFOLIOINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PortfolioInfoList(final PortfolioInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return PortfolioInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final PortfolioInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected PortfolioInfoList getEmptyList(final ListStyle pStyle) {
            PortfolioInfoList myList = new PortfolioInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PortfolioInfo addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a PortfolioInfo */
            if (!(pItem instanceof PortfolioInfo)) {
                throw new UnsupportedOperationException();
            }

            PortfolioInfo myInfo = new PortfolioInfo(this, (PortfolioInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public PortfolioInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected PortfolioInfo addNewItem(final Portfolio pOwner,
                                           final AccountInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            PortfolioInfo myInfo = new PortfolioInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Portfolio pPortfolio,
                                final AccountInfoClass pInfoClass,
                                final Object pValue) throws OceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            MoneyWiseData myData = getDataSet();

            /* Look up the Info Type */
            AccountInfoType myInfoType = myData.getActInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new MoneyWiseDataException(pPortfolio, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<>(PortfolioInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pPortfolio);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Portfolio Info */
            PortfolioInfo myInfo = new PortfolioInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            append(myInfo);
        }

        @Override
        public PortfolioInfo addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the info */
            PortfolioInfo myInfo = new PortfolioInfo(this, pValues);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myInfo);

            /* Return it */
            return myInfo;
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Validate the PortfolioInfo */
            validateOnLoad();

            /* Map and Validate the Portfolios */
            PortfolioList myPortfolios = getDataSet().getPortfolios();
            myPortfolios.mapData();
            myPortfolios.validateOnLoad();
        }
    }
}
