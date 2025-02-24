/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.base;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.EnumMap;
import java.util.Map;

/**
 * Values for a bucket.
 * @param <T> the values class
 * @param <E> the enum class
 */
public abstract class MoneyWiseXAnalysisValues<T extends MoneyWiseXAnalysisValues<T, E>, E extends Enum<E> & MoneyWiseXAnalysisAttribute>
        implements MetisDataObjectFormat, MetisDataMap<E, Object> {
    /**
     * Map.
     */
    private final Map<E, Object> theMap;

    /**
     * Enum class.
     */
    private final Class<E> theClass;

    /**
     * Constructor.
     * @param pClass the Enum class
     */
    protected MoneyWiseXAnalysisValues(final Class<E> pClass) {
        theMap = new EnumMap<>(pClass);
        theClass = pClass;
    }

    /**
     * Constructor.
     * @param pSource the source values
     */
    protected MoneyWiseXAnalysisValues(final T pSource) {
        theMap = new EnumMap<>(pSource.getUnderlyingMap());
        theClass = pSource.getEnumClass();
    }

    /**
     * Reset nonPreserved items.
     */
    void resetNonPreserved() {
        /* Loop through the constants */
        for (E myKey : theClass.getEnumConstants()) {
            /* If we are copying all or the attribute is preserved */
            if (!myKey.isPreserved()) {
                /* Remove values that are not preserved */
                theMap.remove(myKey);
            }
        }
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return getClass().getSimpleName();
    }

    /**
     * Obtain EnumClass.
     * @return the class.
     */
    protected Class<E> getEnumClass() {
        return theClass;
    }

    /**
     * Obtain new snapShot.
     * @return the snapShot.
     */
    protected abstract T newSnapShot();

    @Override
    public Map<E, Object> getUnderlyingMap() {
        return theMap;
    }

    /**
     * Adjust to base values.
     * @param pBaseValues the base values.
     */
    public void adjustToBaseValues(final T pBaseValues) {
    }

    /**
     * Obtain delta value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected OceanusDecimal getDeltaValue(final T pPrevious,
                                           final E pAttr) {
        switch (pAttr.getDataType()) {
            case MONEY:
                return getDeltaMoneyValue(pPrevious, pAttr);
            case UNITS:
                return getDeltaUnitsValue(pPrevious, pAttr);
            default:
                return null;
        }
    }

    /**
     * Obtain delta money value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected OceanusMoney getDeltaMoneyValue(final T pPrevious,
                                              final E pAttr) {
        /* Access current and previous values */
        OceanusMoney myCurr = getMoneyValue(pAttr);
        if (pPrevious != null) {
            final OceanusMoney myPrev = pPrevious.getMoneyValue(pAttr);

            /* Calculate delta */
            myCurr = new OceanusMoney(myCurr);
            myCurr.subtractAmount(myPrev);
        }
        return myCurr;
    }

    /**
     * Obtain delta units value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected OceanusUnits getDeltaUnitsValue(final T pPrevious,
                                              final E pAttr) {
        /* Access current and previous values */
        OceanusUnits myCurr = getUnitsValue(pAttr);
        if (pPrevious != null) {
            final OceanusUnits myPrev = pPrevious.getUnitsValue(pAttr);

            /* Calculate delta */
            myCurr = new OceanusUnits(myCurr);
            myCurr.subtractUnits(myPrev);
        }
        return myCurr;
    }

    /**
     * Adjust money value relative to base.
     * @param pBase the base values.
     * @param pAttr the attribute to reBase.
     */
    protected void adjustMoneyToBase(final T pBase,
                                     final E pAttr) {
        /* Adjust spend values */
        OceanusMoney myValue = getMoneyValue(pAttr);
        myValue = new OceanusMoney(myValue);
        final OceanusMoney myBaseValue = pBase.getMoneyValue(pAttr);
        myValue.subtractAmount(myBaseValue);
        theMap.put(pAttr, myValue);
    }

    /**
     * Reset base values.
     */
    public void resetBaseValues() {
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    public void setValue(final E pAttr,
                         final Object pValue) {
        /* Set the value into the map */
        theMap.put(pAttr, pValue);
    }

    /**
     * Obtain an attribute value.
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    private <X> X getValue(final E pAttr,
                           final Class<X> pClass) {
        /* Obtain the value */
        return pClass.cast(getValue(pAttr));
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public Object getValue(final E pAttr) {
        /* Obtain the attribute value */
        return theMap.get(pAttr);
    }

    /**
     * Obtain a decimal attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public OceanusDecimal getDecimalValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, OceanusDecimal.class);
    }

    /**
     * Obtain a units attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public OceanusUnits getUnitsValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, OceanusUnits.class);
    }

    /**
     * Obtain a price attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public OceanusPrice getPriceValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, OceanusPrice.class);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public OceanusMoney getMoneyValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, OceanusMoney.class);
    }

    /**
     * Obtain a rate attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public OceanusRate getRateValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, OceanusRate.class);
    }

    /**
     * Obtain a ratio attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public OceanusRatio getRatioValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, OceanusRatio.class);
    }

    /**
     * Obtain a date attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public OceanusDate getDateValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, OceanusDate.class);
    }

    /**
     * Obtain an integer attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public Integer getIntegerValue(final E pAttr) {
        /* Obtain the attribute */
        return getValue(pAttr, Integer.class);
    }

    /**
     * Obtain an enum attribute value.
     * @param <V> the enum type
     * @param pAttr the attribute
     * @param pClass the Class of the enum
     * @return the value of the attribute or null
     */
    public <V extends Enum<V>> V getEnumValue(final E pAttr,
                                              final Class<V> pClass) {
        /* Obtain the attribute */
        return getValue(pAttr, pClass);
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder();
        for (Map.Entry<E, Object> myEntry : theMap.entrySet()) {
            final Object myValue = myEntry.getValue();
            if (!(myValue instanceof OceanusDecimal) || ((OceanusDecimal) myValue).isNonZero()) {
                if (myBuilder.length() > 0) {
                    myBuilder.append(", ");
                }
                myBuilder.append(myEntry.getKey());
                myBuilder.append("=");
                myBuilder.append(myEntry.getValue());
            }
        }
        return myBuilder.toString();
    }
}
