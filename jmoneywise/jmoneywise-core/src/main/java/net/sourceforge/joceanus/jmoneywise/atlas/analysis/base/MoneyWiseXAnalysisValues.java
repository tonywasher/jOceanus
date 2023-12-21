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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.base;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Analysis Bucket Value.
 * @param <E> the enum class
 */
public class MoneyWiseXAnalysisValues<E extends Enum<E> & MoneyWiseXAnalysisAttribute>
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
     * @param pCurrency the account/reporting currency
     */
    public MoneyWiseXAnalysisValues(final Class<E> pClass,
                                    final AssetCurrency pCurrency) {
        /* Initialise bucket */
        this(pClass, pCurrency, pCurrency);
    }

    /**
     * Constructor.
     * @param pClass the Enum class
     * @param pCurrency the account currency
     * @param pReporting the reporting currency
     */
    public MoneyWiseXAnalysisValues(final Class<E> pClass,
                                    final AssetCurrency pCurrency,
                                    final AssetCurrency pReporting) {
        /* Initialise bucket */
        this(pClass);

        /* Clone values */
        for (E myAttr : theClass.getEnumConstants()) {
            final Object myInitial = getInitialValue(myAttr, pCurrency, pReporting);
            if (myInitial != null) {
                theMap.put(myAttr, myInitial);
            }
        }
    }

    /**
     * Constructor.
     * @param pPrevious the previous bucket
     */
    protected MoneyWiseXAnalysisValues(final MoneyWiseXAnalysisValues<E> pPrevious) {
        /* Initialise bucket */
        this(pPrevious.theClass);

        /* Clone values */
        for (E myAttr : theClass.getEnumConstants()) {
            final Object myInitial = getClonedValue(myAttr);
            if (myInitial != null) {
                theMap.put(myAttr, myInitial);
            }
        }
    }

    /**
     * Constructor.
     * @param pClass the Enum class
     */
    private MoneyWiseXAnalysisValues(final Class<E> pClass) {
        theMap = new EnumMap<>(pClass);
        theClass = pClass;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return getClass().getSimpleName();
    }

    /**
     * Obtain EnumClass.
     * @return the class.
     */
    protected Class<E> getEnumClass() {
        return theClass;
    }

    @Override
    public Map<E, Object> getUnderlyingMap() {
        return theMap;
    }

    /**
     * Obtain delta value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected TethysDecimal getDeltaValue(final MoneyWiseXAnalysisValues<E> pPrevious,
                                          final E pAttr) {
        switch (pAttr.getDataType()) {
            case MONEY:
                return getDeltaMoneyValue(pPrevious, pAttr);
            case UNITS:
                return getDeltaUnitsValue(pPrevious, pAttr);
            case RATIO:
                return getDeltaRatioValue(pPrevious, pAttr);
            default:
                return null;
        }
    }

    /**
     * Obtain delta value (or null if equal).
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected TethysDecimal getDeltaOrNull(final MoneyWiseXAnalysisValues<E> pPrevious,
                                           final E pAttr) {
        final TethysDecimal myDelta = getDeltaValue(pPrevious, pAttr);
        return myDelta != null && myDelta.isNonZero() ? myDelta : null;
    }

    /**
     * Obtain delta money value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected TethysMoney getDeltaMoneyValue(final MoneyWiseXAnalysisValues<E> pPrevious,
                                             final E pAttr) {
        /* Access current and previous values */
        TethysMoney myCurr = getMoneyValue(pAttr);
        if (pPrevious != null) {
            final TethysMoney myPrev = pPrevious.getMoneyValue(pAttr);

            /* Calculate delta */
            myCurr = new TethysMoney(myCurr);
            myCurr.subtractAmount(myPrev);
        }
        return myCurr;
    }

    /**
     * Obtain delta money value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected TethysMoney getDeltaMoneyOrNull(final MoneyWiseXAnalysisValues<E> pPrevious,
                                              final E pAttr) {
        /* Access current and previous values */
        TethysMoney myCurr = getMoneyValue(pAttr);
        if (pPrevious != null) {
            final TethysMoney myPrev = pPrevious.getMoneyValue(pAttr);

            /* Calculate delta */
            myCurr = new TethysMoney(myCurr);
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
    protected TethysUnits getDeltaUnitsValue(final MoneyWiseXAnalysisValues<E> pPrevious,
                                             final E pAttr) {
        /* Access current and previous values */
        TethysUnits myCurr = getUnitsValue(pAttr);
        if (pPrevious != null) {
            final TethysUnits myPrev = pPrevious.getUnitsValue(pAttr);

            /* Calculate delta */
            myCurr = new TethysUnits(myCurr);
            myCurr.subtractUnits(myPrev);
        }
        return myCurr;
    }

    /**
     * Obtain delta units value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected TethysRatio getDeltaRatioValue(final MoneyWiseXAnalysisValues<E> pPrevious,
                                             final E pAttr) {
        /* Access current and previous values */
        TethysRatio myCurr = getRatioValue(pAttr);
        if (pPrevious != null) {
            final TethysRatio myPrev = pPrevious.getRatioValue(pAttr);

            /* Calculate delta */
            myCurr = new TethysRatio(myCurr, myPrev);
        }
        return myCurr;
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final E pAttr,
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
    public TethysDecimal getDecimalValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, TethysDecimal.class);
    }

    /**
     * Obtain a units attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public TethysUnits getUnitsValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, TethysUnits.class);
    }

    /**
     * Obtain a price attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public TethysPrice getPriceValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, TethysPrice.class);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public TethysMoney getMoneyValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, TethysMoney.class);
    }

    /**
     * Obtain a rate attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public TethysRate getRateValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, TethysRate.class);
    }

    /**
     * Obtain a ratio attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public TethysRatio getRatioValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, TethysRatio.class);
    }

    /**
     * Obtain a date attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public TethysDate getDateValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, TethysDate.class);
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

    /**
     * Obtain initial value for attribute.
     * @param pAttr the attribute
     * @param pCurrency the account currency
     * @param pReporting the reporting currency
     * @return the initial value
     */
    private Object getInitialValue(final E pAttr,
                                   final AssetCurrency pCurrency,
                                   final AssetCurrency pReporting) {
        /* Switch on dataType */
        switch (pAttr.getDataType()) {
            case MONEY:
                return pAttr.isForeign()
                        ? new TethysMoney(pCurrency.getCurrency())
                        : new TethysMoney(pReporting.getCurrency());
            case UNITS:
                return new TethysUnits();
            case RATIO:
                return TethysRatio.ONE;
            default:
                return null;
        }
    }

    /**
     * Obtain cloned value for attribute.
     * @param pAttr the attribute
     * @return the initial value
     */
    private Object getClonedValue(final E pAttr) {
        /* Switch on dataType */
        switch (pAttr.getDataType()) {
            case MONEY:
                return new TethysMoney(getMoneyValue(pAttr));
            case UNITS:
                return new TethysUnits(getUnitsValue(pAttr));
            case PRICE:
            case RATIO:
            case DATE:
            case RATE:
                return getValue(pAttr);
            default:
                return null;
        }
    }

    /**
     * Flatten values.
     * @param pPrevious the previous values
     */
    protected void flattenValues(final MoneyWiseXAnalysisValues<E> pPrevious) {
        /* Flatten values */
        for (E myAttr : theClass.getEnumConstants()) {
            final Object myInitial = theMap.get(myAttr);
            final Object myPrev = pPrevious.theMap.get(myAttr);
            if (myInitial != null && !myInitial.equals(myPrev)) {
                theMap.put(myAttr, myPrev);
            }
        }
    }
}
