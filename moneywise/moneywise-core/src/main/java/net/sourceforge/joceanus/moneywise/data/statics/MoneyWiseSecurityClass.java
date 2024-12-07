/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.statics;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * Enumeration of Security Type Classes.
 */
public enum MoneyWiseSecurityClass
        implements PrometheusStaticDataClass {
    /**
     * Shares.
     * <p>
     * This is a share security and represents stock held in a company.
     */
    SHARES(1, 0),

    /**
     * Growth Unit Trust or OEIC.
     * <p>
     * This is a UnitTrust account and represents a mutual fund that reinvests income.
     */
    GROWTHUNITTRUST(2, 1),

    /**
     * Income Unit Trust or OEIC.
     * <p>
     * This is a UnitTrust account and represents a mutual fund that provides income.
     */
    INCOMEUNITTRUST(3, 2),

    /**
     * Life Bond.
     * <p>
     * This is a LifeBond account, which is a specialised form of an {@link #GROWTHUNITTRUST}
     * security. It simply differs in tax treatment.
     */
    LIFEBOND(4, 3),

    /**
     * Endowment.
     * <p>
     * This is a Endowment account, which is a specialised form of an {@link #GROWTHUNITTRUST}
     * security. It simply differs in tax treatment.
     */
    ENDOWMENT(5, 4),

    /**
     * Property.
     * <p>
     * This is a Property account, which represents an owned property.
     */
    PROPERTY(6, 5),

    /**
     * Vehicle.
     * <p>
     * This is a Vehicle account, which represents a road vehicle.
     */
    VEHICLE(7, 6),

    /**
     * Defined Contribution Pension Pot.
     * <p>
     * This is a defined contribution PensionPot. TaxFree contributions can be made to this Pot via
     * an Income:Pension transaction. It should have a single unit valued at the size of the
     * PensionPot.
     */
    DEFINEDCONTRIBUTION(8, 7),

    /**
     * DefinedBenefit PensionPot.
     * <p>
     * This is a defined Benefit Pension Pot. TaxFree contributions can be made to this Pot via an
     * Income:Pension transaction. It should have a single unit valued at the annual income value.
     * Its valuation is the annual income multiplied by 20.
     */
    DEFINEDBENEFIT(9, 8),

    /**
     * StatePension PensionPot.
     * <p>
     * This is a state Pension Pot. It should have a single unit valued at the weekly income value.
     * Its valuation is the weekly income multiplied by 52*20. It is a singular Security.
     */
    STATEPENSION(10, 9),

    /**
     * StockOption.
     * <p>
     * This is stockOption. It relates to an option to buy a particular stock at a particular price
     * at a later date.
     */
    STOCKOPTION(11, 10),

    /**
     * Generic Asset Account.
     * <p>
     * This is a generic asset account and represents items whose value is determined by the product
     * of the number units held and the most recent unit price.
     */
    ASSET(12, 11);

    /**
     * Number of Pension Years for valuation.
     */
    private static final int PENSION_YEARS = 20;

    /**
     * Number of Pension Weeks for valuation.
     */
    private static final int PENSION_WEEKS = 52;

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
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    MoneyWiseSecurityClass(final int uId,
                           final int uOrder) {
        theId = uId;
        theOrder = uOrder;
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
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseStaticResource.getKeyForSecurityType(this).getValue();
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
    public static MoneyWiseSecurityClass fromId(final int id) throws OceanusException {
        for (MoneyWiseSecurityClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseStaticDataType.SECURITYTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the SecurityType is a pension.
     * @return <code>true</code> if the security type is a pension, <code>false</code> otherwise.
     */
    public boolean isPension() {
        switch (this) {
            case DEFINEDBENEFIT:
            case DEFINEDCONTRIBUTION:
            case STATEPENSION:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the SecurityType is a dividend provider.
     * @return <code>true</code> if the security type is a dividend provider, <code>false</code>
     * otherwise.
     */
    public boolean isDividend() {
        switch (this) {
            case SHARES:
            case INCOMEUNITTRUST:
            case GROWTHUNITTRUST:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the SecurityType is shares.
     * @return <code>true</code> if the security type is shares, <code>false</code> otherwise.
     */
    public boolean isShares() {
        return this == SHARES;
    }

    /**
     * Determine whether the SecurityType is option.
     * @return <code>true</code> if the security type is option, <code>false</code> otherwise.
     */
    public boolean isOption() {
        return this == STOCKOPTION;
    }

    /**
     * Determine whether the SecurityType needs a symbol.
     * @return <code>true</code> if the security type needs a symbol, <code>false</code> otherwise.
     */
    public boolean needsSymbol() {
        switch (this) {
            case SHARES:
            case GROWTHUNITTRUST:
            case INCOMEUNITTRUST:
            case LIFEBOND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the SecurityType needs a region.
     * @return <code>true</code> if the security type needs a region, <code>false</code> otherwise.
     */
    public boolean needsRegion() {
        switch (this) {
            case INCOMEUNITTRUST:
            case GROWTHUNITTRUST:
            case LIFEBOND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the SecurityType needs market as a parent.
     * @return <code>true</code> if the security type needs market as a parent, <code>false</code>
     * otherwise.
     */
    public boolean needsMarketParent() {
        switch (this) {
            case ASSET:
            case PROPERTY:
            case VEHICLE:
            case ENDOWMENT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the SecurityType can be tax free.
     * @return <code>true</code> if the security type can be tax free, <code>false</code> otherwise.
     */
    public boolean canTaxFree() {
        switch (this) {
            case SHARES:
            case INCOMEUNITTRUST:
            case GROWTHUNITTRUST:
            case PROPERTY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the SecurityType is subject to Capital Gains.
     * @return <code>true</code> if the security type is subject to Capital Gains,
     * <code>false</code> otherwise.
     */
    public boolean isCapitalGains() {
        switch (this) {
            case SHARES:
            case INCOMEUNITTRUST:
            case GROWTHUNITTRUST:
            case PROPERTY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the SecurityType is subject to Residential Gains.
     * @return <code>true</code> if the security type is subject to Residential Gains,
     * <code>false</code> otherwise.
     */
    public boolean isResidentialGains() {
        return this == PROPERTY;
    }

    /**
     * Determine whether the SecurityType is subject to Chargeable Gains.
     * @return <code>true</code> if the security type is subject to Chargeable Gains,
     * <code>false</code> otherwise.
     */
    public boolean isChargeableGains() {
        return this == LIFEBOND;
    }

    /**
     * Determine whether the SecurityType is Capital.
     * @return <code>true</code> if the security type is Capital, <code>false</code> otherwise.
     */
    public boolean isCapital() {
        switch (this) {
            case SHARES:
            case LIFEBOND:
            case INCOMEUNITTRUST:
            case GROWTHUNITTRUST:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the SecurityType is UnitTrust.
     * @return <code>true</code> if the security type is Capital, <code>false</code> otherwise.
     */
    public boolean isUnitTrust() {
        switch (this) {
            case INCOMEUNITTRUST:
            case GROWTHUNITTRUST:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this a statePension?
     * @return <code>true</code> if the SecurityType is statePension, <code>false</code> otherwise.
     */
    public boolean isStatePension() {
        return this == STATEPENSION;
    }

    /**
     * Is this a singular security?.
     * @return <code>true</code> if the SecurityType is singular, <code>false</code> otherwise.
     */
    public boolean isSingular() {
        return isStatePension();
    }

    /**
     * Is this an autoUnits?
     * @return <code>true</code> if the SecurityType is an autoUnits, <code>false</code> otherwise.
     */
    public boolean isAutoUnits() {
        switch (this) {
            case ENDOWMENT:
            case STATEPENSION:
            case DEFINEDCONTRIBUTION:
            case DEFINEDBENEFIT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain autoUnits.
     * @return the number of units for this security if active.
     */
    public int getAutoUnits() {
        switch (this) {
            case ENDOWMENT:
            case DEFINEDCONTRIBUTION:
                return 1;
            case STATEPENSION:
                return PENSION_YEARS * PENSION_WEEKS;
            case DEFINEDBENEFIT:
                return PENSION_YEARS;
            default:
                return 0;
        }
    }
}
