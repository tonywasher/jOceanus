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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;

/**
 * Enumeration of Security Type Classes.
 */
public enum SecurityTypeClass implements StaticInterface {
    /**
     * Shares.
     * <p>
     * This is a share security and represents stock held in a company.
     */
    SHARES(1, 0),

    /**
     * Unit Trust or OEIC.
     * <p>
     * This is a UnitTrust account and represents a mutual fund.
     */
    UNITTRUST(2, 1),

    /**
     * Life Bond.
     * <p>
     * This is a LifeBond account, which is a specialised form of an {@link #UNITTRUST} security. It simply differs in tax treatment.
     */
    LIFEBOND(3, 2),

    /**
     * Endowment.
     * <p>
     * This is a Endowment account, which is a specialised form of an {@link #UNITTRUST} security. It simply differs in tax treatment.
     */
    ENDOWMENT(4, 3),

    /**
     * Property.
     * <p>
     * This is a Property account, which represents an owned property.
     */
    PROPERTY(5, 4),

    /**
     * Vehicle.
     * <p>
     * This is a Vehicle account, which represents a road vehicle.
     */
    VEHICLE(6, 5),

    /**
     * Generic Asset Account.
     * <p>
     * This is a generic asset account and represents items whose value is determined by the product of the number units held and the most recent unit price.
     */
    ASSET(7, 6);

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
            theName = ResourceMgr.getString(StaticDataResource.getKeyForSecurityType(this));
        }

        /* return the name */
        return theName;
    }

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    private SecurityTypeClass(final int uId,
                              final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JOceanusException on error
     */
    public static SecurityTypeClass fromId(final int id) throws JOceanusException {
        for (SecurityTypeClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.SECURITYTYPE.toString() + ":" + id);
    }

    /**
     * Determine whether the SecurityType is a dividend provider.
     * @return <code>true</code> if the account category type is a dividend provider, <code>false</code> otherwise.
     */
    public boolean isDividend() {
        switch (this) {
            case SHARES:
            case UNITTRUST:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is shares.
     * @return <code>true</code> if the account category type is shares, <code>false</code> otherwise.
     */
    public boolean isShares() {
        return this == SHARES;
    }

    /**
     * Determine whether the AccountCategoryType needs market as a parent.
     * @return <code>true</code> if the account category type needs market as a parent, <code>false</code> otherwise.
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
     * Determine whether the AccountCategoryType can be tax free.
     * @return <code>true</code> if the account category type can be tax free, <code>false</code> otherwise.
     */
    public boolean canTaxFree() {
        switch (this) {
            case SHARES:
            case UNITTRUST:
            case PROPERTY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is subject to Capital Gains.
     * @return <code>true</code> if the account category type is subject to Capital Gains, <code>false</code> otherwise.
     */
    public boolean isCapitalGains() {
        switch (this) {
            case SHARES:
            case UNITTRUST:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the AccountCategoryType is Capital.
     * @return <code>true</code> if the account category type is Capital, <code>false</code> otherwise.
     */
    public boolean isCapital() {
        switch (this) {
            case SHARES:
            case LIFEBOND:
            case UNITTRUST:
                return true;
            default:
                return false;
        }
    }

}
