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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.EnumMap;

import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Values for a bucket.
 * @param <T> the values class
 * @param <E> the enum class
 */
public abstract class BucketValues<T extends BucketValues<T, E>, E extends Enum<E> & BucketAttribute>
        extends EnumMap<E, Object>
        implements JDataFormat {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 9160258241035845846L;

    /**
     * Constructor.
     * @param pClass the Enum class
     */
    protected BucketValues(final Class<E> pClass) {
        super(pClass);
    }

    /**
     * Constructor.
     * @param pSource the source values
     */
    protected BucketValues(final T pSource) {
        super(pSource);
    }

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

    /**
     * Obtain snapShot.
     * @return the snapShot.
     */
    protected abstract T getSnapShot();

    /**
     * Adjust to base values.
     * @param pBaseValues the base values.
     */
    protected void adjustToBaseValues(final T pBaseValues) {
    }

    /**
     * Obtain delta value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected JDecimal getDeltaValue(final T pPrevious,
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
    protected JMoney getDeltaMoneyValue(final T pPrevious,
                                        final E pAttr) {
        /* Access current and previous values */
        JMoney myCurr = getMoneyValue(pAttr);
        JMoney myPrev = pPrevious.getMoneyValue(pAttr);

        /* Calculate delta */
        myCurr = new JMoney(myCurr);
        myCurr.subtractAmount(myPrev);
        return myCurr;
    }

    /**
     * Obtain delta units value.
     * @param pPrevious the previous values.
     * @param pAttr the attribute
     * @return the delta
     */
    protected JUnits getDeltaUnitsValue(final T pPrevious,
                                        final E pAttr) {
        /* Access current and previous values */
        JUnits myCurr = getUnitsValue(pAttr);
        JUnits myPrev = pPrevious.getUnitsValue(pAttr);

        /* Calculate delta */
        myCurr = new JUnits(myCurr);
        myCurr.subtractUnits(myPrev);
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
        JMoney myValue = getMoneyValue(pAttr);
        myValue = new JMoney(myValue);
        JMoney myBaseValue = pBase.getMoneyValue(pAttr);
        myValue.subtractAmount(myBaseValue);
        put(pAttr, myValue);
    }

    /**
     * Reset base values.
     */
    protected void resetBaseValues() {
    }

    /**
     * Set Value.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setValue(final E pAttr,
                            final Object pValue) {
        /* Set the value into the map */
        put(pAttr, pValue);
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
    private Object getValue(final E pAttr) {
        /* Obtain the attribute value */
        return get(pAttr);
    }

    /**
     * Obtain a decimal attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JDecimal getDecimalValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, JDecimal.class);
    }

    /**
     * Obtain a units attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JUnits getUnitsValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, JUnits.class);
    }

    /**
     * Obtain a price attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JPrice getPriceValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, JPrice.class);
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getMoneyValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, JMoney.class);
    }

    /**
     * Obtain a rate attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JRate getRateValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, JRate.class);
    }

    /**
     * Obtain a date attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JDateDay getDateValue(final E pAttr) {
        /* Obtain the attribute value */
        return getValue(pAttr, JDateDay.class);
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
}
