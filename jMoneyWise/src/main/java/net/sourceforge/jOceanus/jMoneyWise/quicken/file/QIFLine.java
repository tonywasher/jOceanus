/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.quicken.file;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRatio;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.quicken.definitions.QLineType;

/**
 * A standard event line in the QIF file.
 * @param <T> the line type
 */
public abstract class QIFLine<T extends QLineType> {
    /**
     * Reconciled flag.
     */
    private static final String QIF_RECONCILED = "X";

    /**
     * Transfer begin char.
     */
    private static final String QIF_XFERSTART = "[";

    /**
     * Transfer end char.
     */
    private static final String QIF_XFEREND = "]";

    /**
     * Obtain line type.
     * @return the line type
     */
    public abstract T getLineType();

    /**
     * Format the data.
     * @param pFormatter the data formatter
     * @param pBuilder the string builder
     */
    protected abstract void formatData(final JDataFormatter pFormatter,
                                       final StringBuilder pBuilder);

    /**
     * Format lines.
     * @param pFormatter the data formatter
     * @param pBuilder the string builder
     */
    protected void formatLine(final JDataFormatter pFormatter,
                              final StringBuilder pBuilder) {
        /* Add the lineType */
        T myType = getLineType();
        pBuilder.append(myType.getSymbol());

        /* Format the Data */
        formatData(pFormatter, pBuilder);
    }

    /**
     * The String line.
     * @param <X> the line type
     */
    protected abstract static class QIFStringLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The value.
         */
        private final String theValue;

        /**
         * Obtain Value.
         * @return the value
         */
        protected String getValue() {
            return theValue;
        }

        /**
         * Constructor.
         * @param pValue the Value
         */
        protected QIFStringLine(final String pValue) {
            /* Store the value */
            theValue = pValue;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(theValue);
        }
    }

    /**
     * The Money line.
     * @param <X> the line type
     */
    protected abstract static class QIFMoneyLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The money.
         */
        private final JMoney theMoney;

        /**
         * Obtain Money.
         * @return the money
         */
        protected JMoney getMoney() {
            return theMoney;
        }

        /**
         * Constructor.
         * @param pMoney the Money
         */
        protected QIFMoneyLine(final JMoney pMoney) {
            /* Store data */
            theMoney = pMoney;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Convert to Decimal */
            JDecimal myDecimal = new JDecimal(theMoney);

            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(myDecimal));
        }
    }

    /**
     * The Date line.
     * @param <X> the line type
     */
    protected abstract static class QIFDateLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The date.
         */
        private final JDateDay theDate;

        /**
         * Obtain Date.
         * @return the date
         */
        public JDateDay getDate() {
            return theDate;
        }

        /**
         * Constructor.
         * @param pDate the Date
         */
        protected QIFDateLine(final JDateDay pDate) {
            /* Store the date */
            theDate = pDate;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(theDate));
        }
    }

    /**
     * The Flag line.
     * @param <X> the line type
     */
    protected abstract static class QIFFlagLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The flag status.
         */
        private final Boolean isSet;

        /**
         * Obtain Cleared status.
         * @return true/false
         */
        protected Boolean isSet() {
            return isSet;
        }

        /**
         * Constructor.
         * @param pSet is the flag set?
         */
        protected QIFFlagLine(final Boolean pSet) {
            /* Store data */
            isSet = pSet;
        }
    }

    /**
     * The Cleared line.
     * @param <X> the line type
     */
    protected abstract static class QIFClearedLine<X extends QLineType>
            extends QIFFlagLine<X> {
        /**
         * Obtain Cleared status.
         * @return true/false
         */
        public Boolean isCleared() {
            return isSet();
        }

        /**
         * Constructor.
         * @param pSet is the flag set?
         */
        protected QIFClearedLine(final Boolean pSet) {
            /* Call super-constructor */
            super(pSet);
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
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
     * @param <X> the line type
     */
    protected abstract static class QIFPriceLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The price.
         */
        private final JPrice thePrice;

        /**
         * Obtain price.
         * @return the price
         */
        protected JPrice getPrice() {
            return thePrice;
        }

        /**
         * Constructor.
         * @param pPrice the Price
         */
        protected QIFPriceLine(final JPrice pPrice) {
            /* Store data */
            thePrice = pPrice;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Convert to Decimal */
            JDecimal myDecimal = new JDecimal(thePrice);

            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(myDecimal));
        }
    }

    /**
     * The Units line.
     * @param <X> the line type
     */
    protected abstract static class QIFUnitsLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The units.
         */
        private final JUnits theUnits;

        /**
         * Obtain units.
         * @return the units
         */
        protected JUnits getUnits() {
            return theUnits;
        }

        /**
         * Constructor.
         * @param pUnits the Units
         */
        protected QIFUnitsLine(final JUnits pUnits) {
            /* Store data */
            theUnits = pUnits;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(theUnits));
        }
    }

    /**
     * The Ratio line.
     * @param <X> the line type
     */
    protected abstract static class QIFRatioLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The ratio.
         */
        private final JRatio theRatio;

        /**
         * Obtain ratio.
         * @return the ratio
         */
        protected JRatio getRatio() {
            return theRatio;
        }

        /**
         * Constructor.
         * @param pRatio the Ratio
         */
        protected QIFRatioLine(final JRatio pRatio) {
            /* Store data */
            theRatio = pRatio;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(theRatio));
        }
    }

    /**
     * The Security line.
     * @param <X> the line type
     */
    public abstract static class QIFSecurityLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The security.
         */
        private final QIFSecurity theSecurity;

        /**
         * Obtain account.
         * @return the account
         */
        public QIFSecurity getSecurity() {
            return theSecurity;
        }

        /**
         * Constructor.
         * @param pSecurity the Security
         */
        protected QIFSecurityLine(final QIFSecurity pSecurity) {
            /* Store data */
            theSecurity = pSecurity;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the security name */
            pBuilder.append(theSecurity.getName());
        }
    }

    /**
     * The Account line.
     * @param <X> the line type
     */
    public abstract static class QIFXferAccountLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The account.
         */
        private final QIFAccount theAccount;

        /**
         * Obtain account.
         * @return the account
         */
        public QIFAccount getAccount() {
            return theAccount;
        }

        /**
         * Constructor.
         * @param pAccount the Account
         */
        protected QIFXferAccountLine(final QIFAccount pAccount) {
            /* Store data */
            theAccount = pAccount;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(QIF_XFERSTART);
            pBuilder.append(theAccount.getName());
            pBuilder.append(QIF_XFEREND);
        }

        /**
         * Parse account line.
         * @param pLine the line.
         * @return the account name (or null)
         */
        protected static String parseAccount(final String pLine) {
            /* If we have the account delimiters */
            if (pLine.startsWith(QIF_XFERSTART)) {
                int i = pLine.indexOf(QIF_XFEREND);
                return (i != -1)
                        ? pLine.substring(QIF_XFERSTART.length(), i)
                        : null;
            }
            return null;
        }
    }

    /**
     * The Account line.
     * @param <X> the line type
     */
    public abstract static class QIFPayeeLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The account.
         */
        private final QIFAccount theAccount;

        /**
         * Obtain account.
         * @return the account
         */
        public QIFAccount getAccount() {
            return theAccount;
        }

        /**
         * Constructor.
         * @param pAccount the Account
         */
        protected QIFPayeeLine(final QIFAccount pAccount) {
            /* Store data */
            theAccount = pAccount;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(theAccount.getName());
        }
    }

    /**
     * The Event Category line.
     * @param <X> the line type
     */
    public abstract static class QIFCategoryLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The event category.
         */
        private final QIFEventCategory theCategory;

        /**
         * Obtain event category.
         * @return the event category
         */
        public QIFEventCategory getEventCategory() {
            return theCategory;
        }

        /**
         * Constructor.
         * @param pCategory the Event Category
         */
        protected QIFCategoryLine(final QIFEventCategory pCategory) {
            /* Store data */
            theCategory = pCategory;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(theCategory.getName());
        }
    }
}
