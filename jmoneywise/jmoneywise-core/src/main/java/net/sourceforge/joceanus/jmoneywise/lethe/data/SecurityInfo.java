/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Representation of an information extension of a security.
 * @author Tony Washer
 */
public class SecurityInfo
        extends DataInfo<SecurityInfo, Security, AccountInfoType, AccountInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.SECURITYINFO.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.SECURITYINFO.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataInfo.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pInfo The Info to copy
     */
    protected SecurityInfo(final SecurityInfoList pList,
                           final SecurityInfo pInfo) {
        /* Set standard values */
        super(pList, pInfo);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pSecurity the security
     * @param pType the type
     */
    private SecurityInfo(final SecurityInfoList pList,
                         final Security pSecurity,
                         final AccountInfoType pType) {
        /* Initialise the item */
        super(pList);
        setNextDataKeySet();

        /* Record the Detail */
        setValueInfoType(pType);
        setValueOwner(pSecurity);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private SecurityInfo(final SecurityInfoList pList,
                         final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Protect against exceptions */
        try {
            /* Resolve links */
            final MoneyWiseData myData = getDataSet();
            resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
            resolveDataLink(FIELD_OWNER, myData.getSecurities());

            /* Set the value */
            setValue(pValues.getValue(FIELD_VALUE));

            /* Resolve any link value */
            resolveLink();

            /* Access the SecurityInfoSet and register this data */
            final SecurityInfoSet mySet = getOwner().getInfoSet();
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
    public Security getOwner() {
        return getOwner(getValueSet(), Security.class);
    }

    /**
     * Obtain Region.
     * @return the Region
     */
    public Region getRegion() {
        return getRegion(getValueSet());
    }

    /**
     * Obtain Security.
     * @return the Security
     */
    public Security getSecurity() {
        return getSecurity(getValueSet());
    }

    /**
     * Obtain InfoType.
     * @param pValueSet the valueSet
     * @return the InfoType
     */
    public static AccountInfoType getInfoType(final MetisValueSet pValueSet) {
        return getInfoType(pValueSet, AccountInfoType.class);
    }

    /**
     * Obtain Linked Region.
     * @param pValueSet the valueSet
     * @return the Region
     */
    public static Region getRegion(final MetisValueSet pValueSet) {
        return pValueSet.isDeletion()
                                      ? null
                                      : pValueSet.getValue(FIELD_LINK, Region.class);
    }

    /**
     * Obtain Linked Security.
     * @param pValueSet the valueSet
     * @return the Security
     */
    public static Security getSecurity(final MetisValueSet pValueSet) {
        return pValueSet.isDeletion()
                                      ? null
                                      : pValueSet.getValue(FIELD_LINK, Security.class);
    }

    @Override
    public String getLinkName() {
        final DataItem myItem = getLink();
        if (myItem instanceof Region) {
            return ((Region) myItem).getName();
        }
        if (myItem instanceof Security) {
            return ((Security) myItem).getName();
        }
        return null;
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public SecurityInfo getBase() {
        return (SecurityInfo) super.getBase();
    }

    @Override
    public SecurityInfoList getList() {
        return (SecurityInfoList) super.getList();
    }

    @Override
    public void deRegister() {
        /* Access the SecurityInfoSet and register this value */
        final SecurityInfoSet mySet = getOwner().getInfoSet();
        mySet.deRegisterInfo(this);
    }

    /**
     * Compare this data to another to establish sort order.
     * @param pThat The AccountInfo to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed
     * object in the sort order
     */
    @Override
    public int compareTo(final DataInfo<SecurityInfo, Security, AccountInfoType, AccountInfoClass> pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the Securities */
        int iDiff = getOwner().compareTo(pThat.getOwner());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the Info Types */
        final AccountInfoType myType = getInfoType();
        iDiff = myType.compareTo(pThat.getInfoType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* If this is a linkSet */
        if (myType.getInfoClass().isLinkSet()) {
            /* Compare names */
            iDiff = MetisDataDifference.compareObject(getLinkName(), pThat.getLinkName());
            if (iDiff != 0) {
                return iDiff;
            }
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        final MoneyWiseData myData = getDataSet();
        resolveDataLink(FIELD_INFOTYPE, myData.getActInfoTypes());
        resolveDataLink(FIELD_OWNER, myData.getSecurities());

        /* Resolve any link value */
        resolveLink();

        /* Access the SecurityInfoSet and register this data */
        final SecurityInfoSet mySet = getOwner().getInfoSet();
        mySet.registerInfo(this);
    }

    /**
     * Resolve link reference.
     * @throws OceanusException on error
     */
    private void resolveLink() throws OceanusException {
        /* If we have a link */
        final AccountInfoType myType = getInfoType();
        if (myType.isLink()) {
            /* Access data */
            final MoneyWiseData myData = getDataSet();
            final MetisValueSet myValues = getValueSet();
            final Object myLinkId = myValues.getValue(FIELD_VALUE);

            /* Switch on link type */
            switch (myType.getInfoClass()) {
                case REGION:
                    resolveDataLink(FIELD_LINK, myData.getRegions());
                    if (myLinkId == null) {
                        setValueValue(getRegion().getId());
                    }
                    break;
                case UNDERLYINGSTOCK:
                    resolveDataLink(FIELD_LINK, myData.getSecurities());
                    if (myLinkId == null) {
                        setValueValue(getSecurity().getId());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Update securityInfo from a securityInfo extract.
     * @param pInfo the changed securityInfo
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pInfo) {
        /* Can only update from SecurityInfo */
        if (!(pInfo instanceof SecurityInfo)) {
            return false;
        }

        /* Access as SecurityInfo */
        final SecurityInfo mySecInfo = (SecurityInfo) pInfo;

        /* Store the current detail into history */
        pushHistory();

        /* Update the value if required */
        if (!MetisDataDifference.isEqual(getField(), mySecInfo.getField())) {
            setValueValue(mySecInfo.getField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch info class */
        super.touchUnderlyingItems();

        /* Switch on info class */
        switch (getInfoClass()) {
            case UNDERLYINGSTOCK:
                getSecurity().touchItem(getOwner());
                break;
            case REGION:
                getRegion().touchItem(getOwner());
                break;
            default:
                break;
        }
    }

    /**
     * SecurityInfoList.
     */
    public static class SecurityInfoList
            extends DataInfoList<SecurityInfo, Security, AccountInfoType, AccountInfoClass> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<SecurityInfoList> FIELD_DEFS = MetisFieldSet.newFieldSet(SecurityInfoList.class);

        /**
         * Construct an empty CORE info list.
         * @param pData the DataSet for the list
         */
        protected SecurityInfoList(final MoneyWiseData pData) {
            super(SecurityInfo.class, pData, MoneyWiseDataType.SECURITYINFO, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private SecurityInfoList(final SecurityInfoList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<SecurityInfoList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return SecurityInfo.FIELD_DEFS;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Set base list for Edit InfoList.
         * @param pBase the base list
         */
        protected void setBase(final SecurityInfoList pBase) {
            /* Set the style and base */
            setStyle(ListStyle.EDIT);
            super.setBase(pBase);
        }

        @Override
        protected SecurityInfoList getEmptyList(final ListStyle pStyle) {
            final SecurityInfoList myList = new SecurityInfoList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public SecurityInfo addCopyItem(final DataItem pItem) {
            /* Can only clone a SecurityInfo */
            if (!(pItem instanceof SecurityInfo)) {
                throw new UnsupportedOperationException();
            }

            final SecurityInfo myInfo = new SecurityInfo(this, (SecurityInfo) pItem);
            add(myInfo);
            return myInfo;
        }

        @Override
        public SecurityInfo addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected SecurityInfo addNewItem(final Security pOwner,
                                          final AccountInfoType pInfoType) {
            /* Allocate the new entry and add to list */
            final SecurityInfo myInfo = new SecurityInfo(this, pOwner, pInfoType);
            add(myInfo);

            /* return it */
            return myInfo;
        }

        @Override
        public void addInfoItem(final Integer pId,
                                final Security pSecurity,
                                final AccountInfoClass pInfoClass,
                                final Object pValue) throws OceanusException {
            /* Ignore item if it is null */
            if (pValue == null) {
                return;
            }

            /* Access the data set */
            final MoneyWiseData myData = getDataSet();

            /* Look up the Info Type */
            final AccountInfoType myInfoType = myData.getActInfoTypes().findItemByClass(pInfoClass);
            if (myInfoType == null) {
                throw new MoneyWiseDataException(pSecurity, ERROR_BADINFOCLASS + " [" + pInfoClass + "]");
            }

            /* Create the values */
            final DataValues myValues = new DataValues(SecurityInfo.OBJECT_NAME);
            myValues.addValue(FIELD_ID, pId);
            myValues.addValue(FIELD_INFOTYPE, myInfoType);
            myValues.addValue(FIELD_OWNER, pSecurity);
            myValues.addValue(FIELD_VALUE, pValue);

            /* Create a new Security Info */
            final SecurityInfo myInfo = new SecurityInfo(this, myValues);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add the Info to the list */
            add(myInfo);
        }

        @Override
        public SecurityInfo addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the info */
            final SecurityInfo myInfo = new SecurityInfo(this, pValues);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myInfo.getId())) {
                myInfo.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myInfo, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myInfo);

            /* Return it */
            return myInfo;
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Validate the SecurityInfo */
            validateOnLoad();

            /* Map and Validate the Securities */
            final SecurityList mySecurities = getDataSet().getSecurities();
            mySecurities.mapData();
            mySecurities.validateOnLoad();
        }
    }
}
