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
package io.github.tonywasher.joceanus.moneywise.quicken.file;

import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Objects;

/**
 * A standard event line in the QIF file.
 *
 * @param <T> the line type
 */
public abstract class MoneyWiseQIFLine<T extends MoneyWiseQLineType> {
    /**
     * Reconciled flag.
     */
    protected static final String QIF_RECONCILED = "X";

    /**
     * Obtain line type.
     *
     * @return the line type
     */
    public abstract T getLineType();

    /**
     * Format the data.
     *
     * @param pFormatter the data formatter
     * @param pBuilder   the string builder
     */
    protected abstract void formatData(OceanusDataFormatter pFormatter,
                                       StringBuilder pBuilder);

    /**
     * Format lines.
     *
     * @param pFormatter the data formatter
     * @param pBuilder   the string builder
     */
    protected void formatLine(final OceanusDataFormatter pFormatter,
                              final StringBuilder pBuilder) {
        /* Add the lineType */
        final T myType = getLineType();
        pBuilder.append(myType.getSymbol());

        /* Format the Data */
        formatData(pFormatter, pBuilder);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Cast correctly */
        final MoneyWiseQIFLine<?> myLine = (MoneyWiseQIFLine<?>) pThat;

        /* Check value */
        return getLineType().equals(myLine.getLineType());
    }

    @Override
    public int hashCode() {
        return getLineType().hashCode();
    }

    /**
     * The String line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFStringLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The value.
         */
        private final String theValue;

        /**
         * Constructor.
         *
         * @param pValue the Value
         */
        protected MoneyWiseQIFStringLine(final String pValue) {
            /* Store the value */
            theValue = pValue;
        }

        @Override
        public String toString() {
            return getValue();
        }

        /**
         * Obtain Value.
         *
         * @return the value
         */
        protected String getValue() {
            return theValue;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(theValue);
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseQIFStringLine<?> myLine = (MoneyWiseQIFStringLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theValue.equals(myLine.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), theValue);
        }
    }

    /**
     * The Money line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFMoneyLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The money.
         */
        private final OceanusMoney theMoney;

        /**
         * Constructor.
         *
         * @param pMoney the Money
         */
        protected MoneyWiseQIFMoneyLine(final OceanusMoney pMoney) {
            /* Store data */
            theMoney = pMoney;
        }

        @Override
        public String toString() {
            return getMoney().toString();
        }

        /**
         * Obtain Money.
         *
         * @return the money
         */
        protected OceanusMoney getMoney() {
            return theMoney;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Convert to Decimal */
            final OceanusDecimal myDecimal = new OceanusDecimal(theMoney);

            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(myDecimal));
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseQIFMoneyLine<?> myLine = (MoneyWiseQIFMoneyLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theMoney.equals(myLine.getMoney());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), theMoney);
        }
    }

    /**
     * The Date line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFDateLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The date.
         */
        private final OceanusDate theDate;

        /**
         * Constructor.
         *
         * @param pDate the Date
         */
        protected MoneyWiseQIFDateLine(final OceanusDate pDate) {
            /* Store the date */
            theDate = pDate;
        }

        @Override
        public String toString() {
            return getDate().toString();
        }

        /**
         * Obtain Date.
         *
         * @return the date
         */
        public OceanusDate getDate() {
            return theDate;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(theDate));
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseQIFDateLine<?> myLine = (MoneyWiseQIFDateLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theDate.equals(myLine.getDate());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), theDate);
        }
    }

    /**
     * The Flag line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFFlagLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The flag status.
         */
        private final boolean isSet;

        /**
         * Constructor.
         *
         * @param pSet is the flag set?
         */
        protected MoneyWiseQIFFlagLine(final Boolean pSet) {
            /* Store data */
            isSet = pSet;
        }

        @Override
        public String toString() {
            return Boolean.toString(isSet);
        }

        /**
         * Obtain Cleared status.
         *
         * @return true/false
         */
        protected boolean isSet() {
            return isSet;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseQIFFlagLine<?> myLine = (MoneyWiseQIFFlagLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return isSet == myLine.isSet();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), isSet);
        }
    }

    /**
     * The Cleared line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFClearedLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFFlagLine<X> {
        /**
         * Constructor.
         *
         * @param pSet is the flag set?
         */
        protected MoneyWiseQIFClearedLine(final Boolean pSet) {
            /* Call super-constructor */
            super(pSet);
        }

        /**
         * Obtain Cleared status.
         *
         * @return true/false
         */
        public boolean isCleared() {
            return isSet();
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* If we should set the flag */
            if (isSet()) {
                /* Add the flag */
                pBuilder.append(QIF_RECONCILED);
            }
        }
    }

    /**
     * The Price line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFPriceLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The price.
         */
        private final OceanusPrice thePrice;

        /**
         * Constructor.
         *
         * @param pPrice the Price
         */
        protected MoneyWiseQIFPriceLine(final OceanusPrice pPrice) {
            /* Store data */
            thePrice = pPrice;
        }

        @Override
        public String toString() {
            return getPrice().toString();
        }

        /**
         * Obtain price.
         *
         * @return the price
         */
        protected OceanusPrice getPrice() {
            return thePrice;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Convert to Decimal */
            final OceanusDecimal myDecimal = new OceanusDecimal(thePrice);

            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(myDecimal));
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseQIFPriceLine<?> myLine = (MoneyWiseQIFPriceLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return thePrice.equals(myLine.getPrice());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), thePrice);
        }
    }

    /**
     * The Units line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFUnitsLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The units.
         */
        private final OceanusUnits theUnits;

        /**
         * Constructor.
         *
         * @param pUnits the Units
         */
        protected MoneyWiseQIFUnitsLine(final OceanusUnits pUnits) {
            /* Store data */
            theUnits = pUnits;
        }

        @Override
        public String toString() {
            return getUnits().toString();
        }

        /**
         * Obtain units.
         *
         * @return the units
         */
        protected OceanusUnits getUnits() {
            return theUnits;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(theUnits));
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseQIFUnitsLine<?> myLine = (MoneyWiseQIFUnitsLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theUnits.equals(myLine.getUnits());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), theUnits);
        }
    }

    /**
     * The Rate line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFRateLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The Rate.
         */
        private final OceanusRate theRate;

        /**
         * Constructor.
         *
         * @param pPercent the percentage
         */
        protected MoneyWiseQIFRateLine(final OceanusRate pPercent) {
            /* Store data */
            theRate = pPercent;
        }

        @Override
        public String toString() {
            return getRate().toString();
        }

        /**
         * Obtain rate.
         *
         * @return the rate
         */
        protected OceanusRate getRate() {
            return theRate;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(theRate));
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseQIFRateLine<?> myLine = (MoneyWiseQIFRateLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theRate.equals(myLine.getRate());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), theRate);
        }
    }

    /**
     * The Ratio line.
     *
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFRatioLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The ratio.
         */
        private final OceanusRatio theRatio;

        /**
         * Constructor.
         *
         * @param pRatio the Ratio
         */
        protected MoneyWiseQIFRatioLine(final OceanusRatio pRatio) {
            /* Store data */
            theRatio = pRatio;
        }

        @Override
        public String toString() {
            return getRatio().toString();
        }

        /**
         * Obtain ratio.
         *
         * @return the ratio
         */
        protected OceanusRatio getRatio() {
            return theRatio;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(theRatio));
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!getClass().equals(pThat.getClass())) {
                return false;
            }

            /* Cast correctly */
            final MoneyWiseQIFRatioLine<?> myLine = (MoneyWiseQIFRatioLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theRatio.equals(myLine.getRatio());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), theRatio);
        }
    }
}
