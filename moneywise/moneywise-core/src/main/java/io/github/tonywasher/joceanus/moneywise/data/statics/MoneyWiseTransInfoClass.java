/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.data.MetisDataType;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInfoClass;

/**
 * Enumeration of TransactionInfo Classes..
 */
public enum MoneyWiseTransInfoClass
        implements PrometheusDataInfoClass, MetisDataFieldId {
    /**
     * Tax Credit.
     */
    TAXCREDIT(1, 0, MetisDataType.MONEY),

    /**
     * Employer National Insurance.
     */
    EMPLOYERNATINS(2, 1, MetisDataType.MONEY),

    /**
     * Employee National Insurance.
     */
    EMPLOYEENATINS(3, 2, MetisDataType.MONEY),

    /**
     * Deemed Benefit.
     */
    DEEMEDBENEFIT(4, 3, MetisDataType.MONEY),

    /**
     * QualifyingYears.
     */
    QUALIFYYEARS(5, 4, MetisDataType.INTEGER),

    /**
     * Account Delta Units.
     */
    ACCOUNTDELTAUNITS(6, 5, MetisDataType.UNITS),

    /**
     * Partner Delta Units.
     */
    PARTNERDELTAUNITS(7, 6, MetisDataType.UNITS),

    /**
     * Dilution.
     */
    DILUTION(8, 7, MetisDataType.RATIO),

    /**
     * Reference.
     */
    REFERENCE(9, 8, MetisDataType.STRING),

    /**
     * Withheld.
     */
    WITHHELD(10, 9, MetisDataType.MONEY),

    /**
     * Partner Amount.
     */
    PARTNERAMOUNT(11, 10, MetisDataType.MONEY),

    /**
     * ThirdParty Amount.
     */
    RETURNEDCASH(12, 11, MetisDataType.MONEY),

    /**
     * ReturnedCashAccount.
     */
    RETURNEDCASHACCOUNT(13, 12, MetisDataType.LINKPAIR),

    /**
     * Comments.
     */
    COMMENTS(14, 13, MetisDataType.STRING),

    /**
     * Price.
     */
    PRICE(15, 14, MetisDataType.PRICE),

    /**
     * XchangeRate.
     */
    XCHANGERATE(16, 15, MetisDataType.RATIO),

    /**
     * Commission.
     */
    COMMISSION(17, 16, MetisDataType.MONEY),

    /**
     * TransactionTag.
     */
    TRANSTAG(18, 17, MetisDataType.LINKSET);

    /**
     * Data length.
     */
    private static final int DATA_LEN = 20;

    /**
     * Comment length.
     */
    private static final int COMMENT_LEN = 50;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Data Type.
     */
    private final MetisDataType theDataType;

    /**
     * Constructor.
     *
     * @param uId       the id
     * @param uOrder    the default order
     * @param pDataType the data type
     */
    MoneyWiseTransInfoClass(final int uId,
                            final int uOrder,
                            final MetisDataType pDataType) {
        theId = uId;
        theOrder = uOrder;
        theDataType = pDataType;
    }

    @Override
    public int getClassId() {
        return theId;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    @Override
    public MetisDataType getDataType() {
        return theDataType;
    }

    @Override
    public boolean isLink() {
        return switch (theDataType) {
            case LINK, LINKPAIR, LINKSET -> true;
            default -> false;
        };
    }

    @Override
    public boolean isLinkSet() {
        return theDataType == MetisDataType.LINKSET;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForInfoClass(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     *
     * @param id the id value
     * @return the corresponding enum object
     * @throws OceanusException on error
     */
    public static MoneyWiseTransInfoClass fromId(final int id) throws OceanusException {
        for (MoneyWiseTransInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseStaticDataType.TRANSINFOTYPE.toString() + ":" + id);
    }

    /**
     * Obtain maximum length for infoType.
     *
     * @return the maximum length
     */
    public int getMaximumLength() {
        return switch (this) {
            case REFERENCE -> DATA_LEN;
            case COMMENTS -> COMMENT_LEN;
            default -> 0;
        };
    }

    @Override
    public String getId() {
        return toString();
    }

    /**
     * Obtain the resource bundleId for the info class.
     *
     * @param pClass the info class
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForInfoClass(final MoneyWiseTransInfoClass pClass) {
        /* Create the map and return it */
        return switch (pClass) {
            case TAXCREDIT -> MoneyWiseStaticResource.TRANSINFO_TAXCREDIT;
            case EMPLOYERNATINS -> MoneyWiseStaticResource.TRANSTYPE_EMPLOYERNATINS;
            case EMPLOYEENATINS -> MoneyWiseStaticResource.TRANSTYPE_EMPLOYEENATINS;
            case DEEMEDBENEFIT -> MoneyWiseStaticResource.TRANSINFO_BENEFIT;
            case WITHHELD -> MoneyWiseStaticResource.TRANSTYPE_WITHHELD;
            case ACCOUNTDELTAUNITS -> MoneyWiseStaticResource.TRANSINFO_ACCOUNTDELTAUNITS;
            case PARTNERDELTAUNITS -> MoneyWiseStaticResource.TRANSINFO_PARTNERDELTAUNITS;
            case PARTNERAMOUNT -> MoneyWiseStaticResource.TRANSINFO_PARTNERAMOUNT;
            case RETURNEDCASH -> MoneyWiseStaticResource.TRANSINFO_RETURNEDCASH;
            case DILUTION -> MoneyWiseStaticResource.TRANSINFO_DILUTION;
            case QUALIFYYEARS -> MoneyWiseStaticResource.TRANSINFO_QUALYEARS;
            case REFERENCE -> MoneyWiseStaticResource.TRANSINFO_REFERENCE;
            case COMMENTS -> MoneyWiseStaticResource.TRANSINFO_COMMENTS;
            case RETURNEDCASHACCOUNT -> MoneyWiseStaticResource.TRANSINFO_RETURNEDCASHACCOUNT;
            case PRICE -> MoneyWiseStaticResource.TRANSINFO_PRICE;
            case XCHANGERATE -> MoneyWiseStaticResource.TRANSINFO_XCHANGERATE;
            case COMMISSION -> MoneyWiseStaticResource.TRANSINFO_COMMISSION;
            case TRANSTAG -> MoneyWiseStaticResource.TRANSINFO_TRANSTAG;
            default -> throw new IllegalArgumentException();
        };
    }
}
