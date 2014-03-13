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
package net.sourceforge.joceanus.jmoneywise.newanalysis;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.DataType;

/**
 * AccountAttribute enumeration.
 */
public enum AccountAttribute implements BucketAttribute {
    /**
     * Valuation.
     */
    VALUATION,

    /**
     * Rate.
     */
    RATE,

    /**
     * Valuation Delta.
     */
    DELTA,

    /**
     * Maturity.
     */
    MATURITY,

    /**
     * Spend.
     */
    SPEND;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountAttribute.class.getName());

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean isCounter() {
        switch (this) {
            case VALUATION:
            case SPEND:
                return true;
            case RATE:
            case MATURITY:
            case DELTA:
            default:
                return false;
        }
    }

    @Override
    public DataType getDataType() {
        switch (this) {
            case RATE:
                return DataType.RATE;
            case MATURITY:
                return DataType.DATEDAY;
            case VALUATION:
            case DELTA:
            case SPEND:
            default:
                return DataType.MONEY;
        }
    }
}
