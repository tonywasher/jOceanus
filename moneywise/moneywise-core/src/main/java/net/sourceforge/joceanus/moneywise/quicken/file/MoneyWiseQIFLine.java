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
package net.sourceforge.joceanus.moneywise.quicken.file;

import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A standard event line in the QIF file.
 * @param <T> the line type
 */
public abstract class MoneyWiseQIFLine<T extends MoneyWiseQLineType> {
    /**
     * Reconciled flag.
     */
    protected static final String QIF_RECONCILED = "X";

    /**
     * Transfer begin char.
     */
    private static final String QIF_XFERSTART = "[";

    /**
     * Transfer end char.
     */
    private static final String QIF_XFEREND = "]";

    /**
     * Class indicator.
     */
    private static final String QIF_CLASS = "/";

    /**
     * Class separator.
     */
    private static final String QIF_CLASSSEP = "-";

    /**
     * Category separator.
     */
    private static final String QIF_CATSEP = "!";

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
    protected abstract void formatData(OceanusDataFormatter pFormatter,
                                       StringBuilder pBuilder);

    /**
     * Format lines.
     * @param pFormatter the data formatter
     * @param pBuilder the string builder
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
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theValue.hashCode();
        }
    }

    /**
     * The Money line.
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
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theMoney.hashCode();
        }
    }

    /**
     * The Date line.
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
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theDate.hashCode();
        }
    }

    /**
     * The Flag line.
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFFlagLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The flag status.
         */
        private final Boolean isSet;

        /**
         * Constructor.
         * @param pSet is the flag set?
         */
        protected MoneyWiseQIFFlagLine(final Boolean pSet) {
            /* Store data */
            isSet = pSet;
        }

        @Override
        public String toString() {
            return isSet().toString();
        }

        /**
         * Obtain Cleared status.
         * @return true/false
         */
        protected Boolean isSet() {
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
            return isSet.equals(myLine.isSet());
        }

        @Override
        public int hashCode() {
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + isSet.hashCode();
        }
    }

    /**
     * The Cleared line.
     * @param <X> the line type
     */
    protected abstract static class MoneyWiseQIFClearedLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFFlagLine<X> {
        /**
         * Constructor.
         * @param pSet is the flag set?
         */
        protected MoneyWiseQIFClearedLine(final Boolean pSet) {
            /* Call super-constructor */
            super(pSet);
        }

        /**
         * Obtain Cleared status.
         * @return true/false
         */
        public Boolean isCleared() {
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
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + thePrice.hashCode();
        }
    }

    /**
     * The Units line.
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
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theUnits.hashCode();
        }
    }

    /**
     * The Rate line.
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
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theRate.hashCode();
        }
    }

    /**
     * The Ratio line.
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
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theRatio.hashCode();
        }
    }

    /**
     * The Security line.
     * @param <X> the line type
     */
    public abstract static class MoneyWiseQIFSecurityLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The security.
         */
        private final MoneyWiseQIFSecurity theSecurity;

        /**
         * Constructor.
         * @param pSecurity the Security
         */
        protected MoneyWiseQIFSecurityLine(final MoneyWiseQIFSecurity pSecurity) {
            /* Store data */
            theSecurity = pSecurity;
        }

        @Override
        public String toString() {
            return theSecurity.toString();
        }

        /**
         * Obtain account.
         * @return the account
         */
        public MoneyWiseQIFSecurity getSecurity() {
            return theSecurity;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the security name */
            pBuilder.append(theSecurity.getName());
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
            final MoneyWiseQIFSecurityLine<?> myLine = (MoneyWiseQIFSecurityLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theSecurity.equals(myLine.getSecurity());
        }

        @Override
        public int hashCode() {
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theSecurity.hashCode();
        }
    }

    /**
     * The Account line.
     * @param <X> the line type
     */
    public abstract static class MoneyWiseQIFXferAccountLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The account.
         */
        private final MoneyWiseQIFAccount theAccount;

        /**
         * The class list.
         */
        private final List<MoneyWiseQIFClass> theClasses;

        /**
         * Constructor.
         * @param pAccount the Account
         */
        protected MoneyWiseQIFXferAccountLine(final MoneyWiseQIFAccount pAccount) {
            this(pAccount, null);
        }

        /**
         * Constructor.
         * @param pAccount the Account
         * @param pClasses the classes
         */
        protected MoneyWiseQIFXferAccountLine(final MoneyWiseQIFAccount pAccount,
                                              final List<MoneyWiseQIFClass> pClasses) {
            /* Store data */
            theAccount = pAccount;
            theClasses = pClasses;
        }

        @Override
        public String toString() {
            return theAccount.toString();
        }

        /**
         * Obtain account.
         * @return the account
         */
        public MoneyWiseQIFAccount getAccount() {
            return theAccount;
        }

        /**
         * Obtain class list.
         * @return the class list
         */
        public List<MoneyWiseQIFClass> getClassList() {
            return theClasses;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(QIF_XFERSTART);
            pBuilder.append(theAccount.getName());
            pBuilder.append(QIF_XFEREND);

            /* If we have classes */
            if (theClasses != null) {
                /* Add class indicator */
                pBuilder.append(QIF_CLASS);

                /* Iterate through the list */
                final Iterator<MoneyWiseQIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseQIFClass myClass = myIterator.next();

                    /* Add to the list */
                    pBuilder.append(myClass.getName());
                    if (myIterator.hasNext()) {
                        pBuilder.append(QIF_CLASSSEP);
                    }
                }
            }
        }

        /**
         * Parse account line.
         * @param pFile the QIF File definitions
         * @param pLine the line.
         * @return the account name (or null)
         */
        protected static MoneyWiseQIFAccount parseAccount(final MoneyWiseQIFFile pFile,
                                                          final String pLine) {
            /* Determine line to use */
            String myLine = pLine;

            /* If the line contains a category separator */
            if (pLine.contains(QIF_CATSEP)) {
                /* Move to data following separator */
                final int i = pLine.indexOf(QIF_CATSEP);
                myLine = pLine.substring(i + 1);
            }

            /* If the line contains classes */
            if (myLine.contains(QIF_CLASS)) {
                /* drop class data */
                final int i = myLine.indexOf(QIF_CLASS);
                myLine = myLine.substring(0, i);
            }

            /* If we have the account delimiters */
            if ((myLine.startsWith(QIF_XFERSTART))
                    && (myLine.endsWith(QIF_XFEREND))) {
                /* Remove account delimiters */
                final int i = QIF_XFERSTART.length();
                final int j = QIF_XFEREND.length();
                final String myAccount = myLine.substring(i, myLine.length()
                        - j);
                return pFile.getAccount(myAccount);
            }

            /* Return no account */
            return null;
        }

        /**
         * Parse account classes.
         * @param pFile the QIF File
         * @param pLine the line.
         * @return the account name (or null)
         */
        protected static List<MoneyWiseQIFClass> parseAccountClasses(final MoneyWiseQIFFile pFile,
                                                                     final String pLine) {
            /* Determine line to use */
            String myLine = pLine;

            /* If the line contains a category separator */
            if (pLine.contains(QIF_CATSEP)) {
                /* Move to data following separator */
                final int i = pLine.indexOf(QIF_CATSEP);
                myLine = pLine.substring(i + 1);
            }

            /* If the line contains classes */
            if (myLine.contains(QIF_CLASS)) {
                /* drop preceding data */
                final int i = myLine.indexOf(QIF_CLASS);
                myLine = myLine.substring(i + 1);

                /* Build list of classes */
                final String[] myClasses = myLine.split(QIF_CLASSSEP);
                final List<MoneyWiseQIFClass> myList = new ArrayList<>();
                for (String myClass : myClasses) {
                    myList.add(pFile.getClass(myClass));
                }

                /* Return the classes */
                return myList;
            }

            /* Return no classes */
            return null;
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
            final MoneyWiseQIFXferAccountLine<?> myLine = (MoneyWiseQIFXferAccountLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check account */
            if (!theAccount.equals(myLine.getAccount())) {
                return false;
            }

            /* Check classes */
            final List<MoneyWiseQIFClass> myClasses = myLine.getClassList();
            if (theClasses == null) {
                return myClasses == null;
            } else if (myClasses == null) {
                return true;
            }
            return theClasses.equals(myClasses);
        }

        @Override
        public int hashCode() {
            int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            if (theClasses != null) {
                myResult += theClasses.hashCode();
                myResult *= MoneyWiseQIFFile.HASH_BASE;
            }
            return myResult + theAccount.hashCode();
        }
    }

    /**
     * The Payee line.
     * @param <X> the line type
     */
    public abstract static class MoneyWiseQIFPayeeLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The payee.
         */
        private final MoneyWiseQIFPayee thePayee;

        /**
         * Constructor.
         * @param pPayee the Payee
         */
        protected MoneyWiseQIFPayeeLine(final MoneyWiseQIFPayee pPayee) {
            /* Store data */
            thePayee = pPayee;
        }

        @Override
        public String toString() {
            return thePayee.toString();
        }

        /**
         * Obtain payee.
         * @return the payee
         */
        public MoneyWiseQIFPayee getPayee() {
            return thePayee;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(thePayee.getName());
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
            final MoneyWiseQIFPayeeLine<?> myLine = (MoneyWiseQIFPayeeLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return thePayee.equals(myLine.getPayee());
        }

        @Override
        public int hashCode() {
            final int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + thePayee.hashCode();
        }
    }

    /**
     * The Event Category line.
     * @param <X> the line type
     */
    public abstract static class MoneyWiseQIFCategoryLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The event category.
         */
        private final MoneyWiseQIFEventCategory theCategory;

        /**
         * The class list.
         */
        private final List<MoneyWiseQIFClass> theClasses;

        /**
         * Constructor.
         * @param pCategory the Event Category
         */
        protected MoneyWiseQIFCategoryLine(final MoneyWiseQIFEventCategory pCategory) {
            this(pCategory, null);
        }

        /**
         * Constructor.
         * @param pCategory the Event Category
         * @param pClasses the classes
         */
        protected MoneyWiseQIFCategoryLine(final MoneyWiseQIFEventCategory pCategory,
                                           final List<MoneyWiseQIFClass> pClasses) {
            /* Store data */
            theCategory = pCategory;
            theClasses = pClasses;
        }

        @Override
        public String toString() {
            return theCategory.toString();
        }

        /**
         * Obtain event category.
         * @return the event category
         */
        public MoneyWiseQIFEventCategory getEventCategory() {
            return theCategory;
        }

        /**
         * Obtain class list.
         * @return the class list
         */
        public List<MoneyWiseQIFClass> getClassList() {
            return theClasses;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(theCategory.getName());

            /* If we have classes */
            if (theClasses != null) {
                /* Add class indicator */
                pBuilder.append(QIF_CLASS);

                /* Iterate through the list */
                final Iterator<MoneyWiseQIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseQIFClass myClass = myIterator.next();

                    /* Add to the list */
                    pBuilder.append(myClass.getName());
                    if (myIterator.hasNext()) {
                        pBuilder.append(QIF_CLASSSEP);
                    }
                }
            }
        }

        /**
         * Parse category line.
         * @param pFile the QIF File
         * @param pLine the line.
         * @return the account name (or null)
         */
        protected static MoneyWiseQIFEventCategory parseCategory(final MoneyWiseQIFFile pFile,
                                                                 final String pLine) {
            /* Determine line to use */
            String myLine = pLine;

            /* If the line contains a category separator */
            if (pLine.contains(QIF_CATSEP)) {
                /* Drop data after separator */
                final int i = pLine.indexOf(QIF_CATSEP);
                myLine = pLine.substring(0, i);
            }

            /* If the line contains classes */
            if (myLine.contains(QIF_CLASS)) {
                /* drop class data */
                final int i = myLine.indexOf(QIF_CLASS);
                myLine = myLine.substring(0, i);
            }

            /* If we have the account delimiters */
            if ((myLine.startsWith(QIF_XFERSTART))
                    && (myLine.endsWith(QIF_XFEREND))) {
                /* This is an account */
                return null;
            }

            /* Return category */
            return pFile.getCategory(myLine);
        }

        /**
         * Parse category classes.
         * @param pFile the QIF File
         * @param pLine the line.
         * @return the account name (or null)
         */
        protected static List<MoneyWiseQIFClass> parseCategoryClasses(final MoneyWiseQIFFile pFile,
                                                                      final String pLine) {
            /* Determine line to use */
            String myLine = pLine;

            /* If the line contains a category separator */
            if (pLine.contains(QIF_CATSEP)) {
                /* Drop data after separator */
                final int i = pLine.indexOf(QIF_CATSEP);
                myLine = pLine.substring(0, i);
            }

            /* If the line contains classes */
            if (myLine.contains(QIF_CLASS)) {
                /* drop preceding data */
                final int i = myLine.indexOf(QIF_CLASS);
                myLine = myLine.substring(i + 1);

                /* Build list of classes */
                final String[] myClasses = myLine.split(QIF_CLASSSEP);
                final List<MoneyWiseQIFClass> myList = new ArrayList<>();
                for (String myClass : myClasses) {
                    myList.add(pFile.getClass(myClass));
                }

                /* Return the classes */
                return myList;
            }

            /* Return no classes */
            return null;
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
            final MoneyWiseQIFCategoryLine<?> myLine = (MoneyWiseQIFCategoryLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check category */
            if (!theCategory.equals(myLine.getEventCategory())) {
                return false;
            }

            /* Check classes */
            final List<MoneyWiseQIFClass> myClasses = myLine.getClassList();
            if (theClasses == null) {
                return myClasses == null;
            } else if (myClasses == null) {
                return true;
            }
            return theClasses.equals(myClasses);
        }

        @Override
        public int hashCode() {
            int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            if (theClasses != null) {
                myResult += theClasses.hashCode();
                myResult *= MoneyWiseQIFFile.HASH_BASE;
            }
            return myResult + theCategory.hashCode();
        }
    }

    /**
     * The Event Category line.
     * @param <X> the line type
     */
    public abstract static class MoneyWiseQIFCategoryAccountLine<X extends MoneyWiseQLineType>
            extends MoneyWiseQIFLine<X> {
        /**
         * The event category.
         */
        private final MoneyWiseQIFEventCategory theCategory;

        /**
         * The account.
         */
        private final MoneyWiseQIFAccount theAccount;

        /**
         * The class list.
         */
        private final List<MoneyWiseQIFClass> theClasses;

        /**
         * Constructor.
         * @param pCategory the Event Category
         * @param pAccount the Account
         */
        protected MoneyWiseQIFCategoryAccountLine(final MoneyWiseQIFEventCategory pCategory,
                                                  final MoneyWiseQIFAccount pAccount) {
            this(pCategory, pAccount, null);
        }

        /**
         * Constructor.
         * @param pCategory the Event Category
         * @param pAccount the Account
         * @param pClasses the classes
         */
        protected MoneyWiseQIFCategoryAccountLine(final MoneyWiseQIFEventCategory pCategory,
                                                  final MoneyWiseQIFAccount pAccount,
                                                  final List<MoneyWiseQIFClass> pClasses) {
            /* Store data */
            theCategory = pCategory;
            theAccount = pAccount;
            theClasses = pClasses;
        }

        /**
         * Obtain event category.
         * @return the event category
         */
        public MoneyWiseQIFEventCategory getEventCategory() {
            return theCategory;
        }

        /**
         * Obtain account.
         * @return the account
         */
        public MoneyWiseQIFAccount getAccount() {
            return theAccount;
        }

        /**
         * Obtain class list.
         * @return the class list
         */
        public List<MoneyWiseQIFClass> getClassList() {
            return theClasses;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(theCategory.getName());
            pBuilder.append(QIF_CATSEP);
            pBuilder.append(QIF_XFERSTART);
            pBuilder.append(theAccount.getName());
            pBuilder.append(QIF_XFEREND);

            /* If we have classes */
            if (theClasses != null) {
                /* Add class indicator */
                pBuilder.append(QIF_CLASS);

                /* Iterate through the list */
                final Iterator<MoneyWiseQIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseQIFClass myClass = myIterator.next();

                    /* Add to the list */
                    pBuilder.append(myClass.getName());
                    if (myIterator.hasNext()) {
                        pBuilder.append(QIF_CLASSSEP);
                    }
                }
            }
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
            final MoneyWiseQIFCategoryAccountLine<?> myLine = (MoneyWiseQIFCategoryAccountLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check category */
            if (!theCategory.equals(myLine.getEventCategory())) {
                return false;
            }

            /* Check account */
            if (!theAccount.equals(myLine.getAccount())) {
                return false;
            }

            /* Check classes */
            final List<MoneyWiseQIFClass> myClasses = myLine.getClassList();
            if (theClasses == null) {
                return myClasses == null;
            } else if (myClasses == null) {
                return true;
            }
            return theClasses.equals(myClasses);
        }

        @Override
        public int hashCode() {
            int myResult = MoneyWiseQIFFile.HASH_BASE * getLineType().hashCode();
            if (theClasses != null) {
                myResult += theClasses.hashCode();
                myResult *= MoneyWiseQIFFile.HASH_BASE;
            }
            myResult += theAccount.hashCode();
            myResult *= MoneyWiseQIFFile.HASH_BASE;
            return myResult + theCategory.hashCode();
        }
    }
}
