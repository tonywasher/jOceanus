/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QLineType;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * A standard event line in the QIF file.
 * @param <T> the line type
 */
public abstract class QIFLine<T extends QLineType> {
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
    protected abstract void formatData(MetisDataFormatter pFormatter,
                                       StringBuilder pBuilder);

    /**
     * Format lines.
     * @param pFormatter the data formatter
     * @param pBuilder the string builder
     */
    protected void formatLine(final MetisDataFormatter pFormatter,
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
        final QIFLine<?> myLine = (QIFLine<?>) pThat;

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
    protected abstract static class QIFStringLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The value.
         */
        private final String theValue;

        /**
         * Constructor.
         * @param pValue the Value
         */
        protected QIFStringLine(final String pValue) {
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
        protected void formatData(final MetisDataFormatter pFormatter,
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
            final QIFStringLine<?> myLine = (QIFStringLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theValue.equals(myLine.getValue());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theValue.hashCode();
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
        private final TethysMoney theMoney;

        /**
         * Constructor.
         * @param pMoney the Money
         */
        protected QIFMoneyLine(final TethysMoney pMoney) {
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
        protected TethysMoney getMoney() {
            return theMoney;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Convert to Decimal */
            final TethysDecimal myDecimal = new TethysDecimal(theMoney);

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
            final QIFMoneyLine<?> myLine = (QIFMoneyLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theMoney.equals(myLine.getMoney());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theMoney.hashCode();
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
        private final TethysDate theDate;

        /**
         * Constructor.
         * @param pDate the Date
         */
        protected QIFDateLine(final TethysDate pDate) {
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
        public TethysDate getDate() {
            return theDate;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
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
            final QIFDateLine<?> myLine = (QIFDateLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theDate.equals(myLine.getDate());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theDate.hashCode();
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
         * Constructor.
         * @param pSet is the flag set?
         */
        protected QIFFlagLine(final Boolean pSet) {
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
            final QIFFlagLine<?> myLine = (QIFFlagLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return isSet.equals(myLine.isSet());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + isSet.hashCode();
        }
    }

    /**
     * The Cleared line.
     * @param <X> the line type
     */
    protected abstract static class QIFClearedLine<X extends QLineType>
            extends QIFFlagLine<X> {
        /**
         * Constructor.
         * @param pSet is the flag set?
         */
        protected QIFClearedLine(final Boolean pSet) {
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
        protected void formatData(final MetisDataFormatter pFormatter,
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
        private final TethysPrice thePrice;

        /**
         * Constructor.
         * @param pPrice the Price
         */
        protected QIFPriceLine(final TethysPrice pPrice) {
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
        protected TethysPrice getPrice() {
            return thePrice;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Convert to Decimal */
            final TethysDecimal myDecimal = new TethysDecimal(thePrice);

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
            final QIFPriceLine<?> myLine = (QIFPriceLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return thePrice.equals(myLine.getPrice());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + thePrice.hashCode();
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
        private final TethysUnits theUnits;

        /**
         * Constructor.
         * @param pUnits the Units
         */
        protected QIFUnitsLine(final TethysUnits pUnits) {
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
        protected TethysUnits getUnits() {
            return theUnits;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
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
            final QIFUnitsLine<?> myLine = (QIFUnitsLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theUnits.equals(myLine.getUnits());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theUnits.hashCode();
        }
    }

    /**
     * The Rate line.
     * @param <X> the line type
     */
    protected abstract static class QIFRateLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The Rate.
         */
        private final TethysRate theRate;

        /**
         * Constructor.
         * @param pPercent the percentage
         */
        protected QIFRateLine(final TethysRate pPercent) {
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
        protected TethysRate getRate() {
            return theRate;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
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
            final QIFRateLine<?> myLine = (QIFRateLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theRate.equals(myLine.getRate());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theRate.hashCode();
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
        private final TethysRatio theRatio;

        /**
         * Constructor.
         * @param pRatio the Ratio
         */
        protected QIFRatioLine(final TethysRatio pRatio) {
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
        protected TethysRatio getRatio() {
            return theRatio;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
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
            final QIFRatioLine<?> myLine = (QIFRatioLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theRatio.equals(myLine.getRatio());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theRatio.hashCode();
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
         * Constructor.
         * @param pSecurity the Security
         */
        protected QIFSecurityLine(final QIFSecurity pSecurity) {
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
        public QIFSecurity getSecurity() {
            return theSecurity;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
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
            final QIFSecurityLine<?> myLine = (QIFSecurityLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return theSecurity.equals(myLine.getSecurity());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + theSecurity.hashCode();
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
         * The class list.
         */
        private final List<QIFClass> theClasses;

        /**
         * Constructor.
         * @param pAccount the Account
         */
        protected QIFXferAccountLine(final QIFAccount pAccount) {
            this(pAccount, null);
        }

        /**
         * Constructor.
         * @param pAccount the Account
         * @param pClasses the classes
         */
        protected QIFXferAccountLine(final QIFAccount pAccount,
                                     final List<QIFClass> pClasses) {
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
        public QIFAccount getAccount() {
            return theAccount;
        }

        /**
         * Obtain class list.
         * @return the class list
         */
        public List<QIFClass> getClassList() {
            return theClasses;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
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
                final Iterator<QIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final QIFClass myClass = myIterator.next();

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
        protected static QIFAccount parseAccount(final QIFFile pFile,
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
        protected static List<QIFClass> parseAccountClasses(final QIFFile pFile,
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
                final List<QIFClass> myList = new ArrayList<>();
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
            final QIFXferAccountLine<?> myLine = (QIFXferAccountLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check account */
            if (!theAccount.equals(myLine.getAccount())) {
                return false;
            }

            /* Check classes */
            final List<QIFClass> myClasses = myLine.getClassList();
            if (theClasses == null) {
                return myClasses == null;
            } else if (myClasses == null) {
                return true;
            }
            return theClasses.equals(myClasses);
        }

        @Override
        public int hashCode() {
            int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            if (theClasses != null) {
                myResult += theClasses.hashCode();
                myResult *= QIFFile.HASH_BASE;
            }
            return myResult + theAccount.hashCode();
        }
    }

    /**
     * The Payee line.
     * @param <X> the line type
     */
    public abstract static class QIFPayeeLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The payee.
         */
        private final QIFPayee thePayee;

        /**
         * Constructor.
         * @param pPayee the Payee
         */
        protected QIFPayeeLine(final QIFPayee pPayee) {
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
        public QIFPayee getPayee() {
            return thePayee;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
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
            final QIFPayeeLine<?> myLine = (QIFPayeeLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check value */
            return thePayee.equals(myLine.getPayee());
        }

        @Override
        public int hashCode() {
            final int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            return myResult + thePayee.hashCode();
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
         * The class list.
         */
        private final List<QIFClass> theClasses;

        /**
         * Constructor.
         * @param pCategory the Event Category
         */
        protected QIFCategoryLine(final QIFEventCategory pCategory) {
            this(pCategory, null);
        }

        /**
         * Constructor.
         * @param pCategory the Event Category
         * @param pClasses the classes
         */
        protected QIFCategoryLine(final QIFEventCategory pCategory,
                                  final List<QIFClass> pClasses) {
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
        public QIFEventCategory getEventCategory() {
            return theCategory;
        }

        /**
         * Obtain class list.
         * @return the class list
         */
        public List<QIFClass> getClassList() {
            return theClasses;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(theCategory.getName());

            /* If we have classes */
            if (theClasses != null) {
                /* Add class indicator */
                pBuilder.append(QIF_CLASS);

                /* Iterate through the list */
                final Iterator<QIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final QIFClass myClass = myIterator.next();

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
        protected static QIFEventCategory parseCategory(final QIFFile pFile,
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
        protected static List<QIFClass> parseCategoryClasses(final QIFFile pFile,
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
                final List<QIFClass> myList = new ArrayList<>();
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
            final QIFCategoryLine<?> myLine = (QIFCategoryLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check category */
            if (!theCategory.equals(myLine.getEventCategory())) {
                return false;
            }

            /* Check classes */
            final List<QIFClass> myClasses = myLine.getClassList();
            if (theClasses == null) {
                return myClasses == null;
            } else if (myClasses == null) {
                return true;
            }
            return theClasses.equals(myClasses);
        }

        @Override
        public int hashCode() {
            int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            if (theClasses != null) {
                myResult += theClasses.hashCode();
                myResult *= QIFFile.HASH_BASE;
            }
            return myResult + theCategory.hashCode();
        }
    }

    /**
     * The Event Category line.
     * @param <X> the line type
     */
    public abstract static class QIFCategoryAccountLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The event category.
         */
        private final QIFEventCategory theCategory;

        /**
         * The account.
         */
        private final QIFAccount theAccount;

        /**
         * The class list.
         */
        private final List<QIFClass> theClasses;

        /**
         * Constructor.
         * @param pCategory the Event Category
         * @param pAccount the Account
         */
        protected QIFCategoryAccountLine(final QIFEventCategory pCategory,
                                         final QIFAccount pAccount) {
            this(pCategory, pAccount, null);
        }

        /**
         * Constructor.
         * @param pCategory the Event Category
         * @param pAccount the Account
         * @param pClasses the classes
         */
        protected QIFCategoryAccountLine(final QIFEventCategory pCategory,
                                         final QIFAccount pAccount,
                                         final List<QIFClass> pClasses) {
            /* Store data */
            theCategory = pCategory;
            theAccount = pAccount;
            theClasses = pClasses;
        }

        /**
         * Obtain event category.
         * @return the event category
         */
        public QIFEventCategory getEventCategory() {
            return theCategory;
        }

        /**
         * Obtain account.
         * @return the account
         */
        public QIFAccount getAccount() {
            return theAccount;
        }

        /**
         * Obtain class list.
         * @return the class list
         */
        public List<QIFClass> getClassList() {
            return theClasses;
        }

        @Override
        protected void formatData(final MetisDataFormatter pFormatter,
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
                final Iterator<QIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final QIFClass myClass = myIterator.next();

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
            final QIFCategoryAccountLine<?> myLine = (QIFCategoryAccountLine<?>) pThat;

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
            final List<QIFClass> myClasses = myLine.getClassList();
            if (theClasses == null) {
                return myClasses == null;
            } else if (myClasses == null) {
                return true;
            }
            return theClasses.equals(myClasses);
        }

        @Override
        public int hashCode() {
            int myResult = QIFFile.HASH_BASE * getLineType().hashCode();
            if (theClasses != null) {
                myResult += theClasses.hashCode();
                myResult *= QIFFile.HASH_BASE;
            }
            myResult += theAccount.hashCode();
            myResult *= QIFFile.HASH_BASE;
            return myResult + theCategory.hashCode();
        }
    }
}
