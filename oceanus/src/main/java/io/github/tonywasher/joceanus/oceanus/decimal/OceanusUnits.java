/*
 * Oceanus: Java Utilities
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
package io.github.tonywasher.joceanus.oceanus.decimal;

/**
 * Represents a Units object.
 */
public class OceanusUnits
        extends OceanusDecimal {
    /**
     * Standard number of decimals for Units.
     */
    protected static final int NUM_DECIMALS = 4;

    /**
     * Construct a new Units.
     */
    public OceanusUnits() {
        recordScale(NUM_DECIMALS);
    }

    /**
     * Construct a new Units by copying another units.
     *
     * @param pUnits the Units to copy
     */
    public OceanusUnits(final OceanusUnits pUnits) {
        super(pUnits.unscaledValue(), pUnits.scale());
    }

    /**
     * Create the units from a byte array.
     *
     * @param pBuffer the buffer
     */
    public OceanusUnits(final byte[] pBuffer) {
        super(pBuffer);
    }

    /**
     * Construct a new Units by setting the value explicitly.
     *
     * @param pValue the unscaled value
     * @return the new Rate
     */
    public static OceanusUnits getWholeUnits(final long pValue) {
        final OceanusUnits myUnits = new OceanusUnits();
        myUnits.setValue(adjustDecimals(pValue, NUM_DECIMALS), NUM_DECIMALS);
        return myUnits;
    }

    /**
     * Add units to the value.
     *
     * @param pValue The units to add to this one.
     */
    public void addUnits(final OceanusUnits pValue) {
        /* Add the value */
        super.addValue(pValue);
    }

    /**
     * Subtract units from the value.
     *
     * @param pValue The units to subtract from this one.
     */
    public void subtractUnits(final OceanusUnits pValue) {
        /* Subtract the value */
        super.subtractValue(pValue);
    }

    @Override
    public void addValue(final OceanusDecimal pValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subtractValue(final OceanusDecimal pValue) {
        throw new UnsupportedOperationException();
    }
}
