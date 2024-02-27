/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * @param uId the id
     * @param uOrder the default order
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
        switch (theDataType) {
            case LINK:
            case LINKPAIR:
            case LINKSET:
                return true;
            default:
                return false;
        }
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
            theName = MoneyWiseStaticResource.getKeyForTransInfo(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
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
     * @return the maximum length
     */
    public int getMaximumLength() {
        switch (this) {
            case REFERENCE:
                return MoneyWiseTransInfoType.DATA_LEN;
            case COMMENTS:
                return MoneyWiseTransInfoType.COMMENT_LEN;
            default:
                return 0;
        }
    }

    @Override
    public String getId() {
        return toString();
    }
}
