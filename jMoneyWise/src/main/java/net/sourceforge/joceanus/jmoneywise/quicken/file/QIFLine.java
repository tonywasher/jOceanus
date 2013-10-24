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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jdecimal.JRatio;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QLineType;

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
     * The Rate line.
     * @param <X> the line type
     */
    protected abstract static class QIFRateLine<X extends QLineType>
            extends QIFLine<X> {
        /**
         * The Rate.
         */
        private final JRate theRate;

        /**
         * Obtain rate.
         * @return the rate
         */
        protected JRate getRate() {
            return theRate;
        }

        /**
         * Constructor.
         * @param pPercent the percentage
         */
        protected QIFRateLine(final JRate pPercent) {
            /* Store data */
            theRate = pPercent;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(pFormatter.formatObject(theRate));
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
         * The class list.
         */
        private final List<QIFClass> theClasses;

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
        protected void formatData(final JDataFormatter pFormatter,
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
                Iterator<QIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    QIFClass myClass = myIterator.next();

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
                int i = pLine.indexOf(QIF_CATSEP);
                myLine = pLine.substring(i + 1);
            }

            /* If the line contains classes */
            if (myLine.contains(QIF_CLASS)) {
                /* drop class data */
                int i = myLine.indexOf(QIF_CLASS);
                myLine = myLine.substring(0, i);
            }

            /* If we have the account delimiters */
            if ((myLine.startsWith(QIF_XFERSTART))
                && (myLine.endsWith(QIF_XFEREND))) {
                /* Remove account delimiters */
                int i = QIF_XFERSTART.length();
                int j = QIF_XFEREND.length();
                String myAccount = myLine.substring(i, myLine.length()
                                                       - i
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
                int i = pLine.indexOf(QIF_CATSEP);
                myLine = pLine.substring(i + 1);
            }

            /* If the line contains classes */
            if (myLine.contains(QIF_CLASS)) {
                /* drop preceding data */
                int i = myLine.indexOf(QIF_CLASS);
                myLine = myLine.substring(i + 1);

                /* Build list of classes */
                String[] myClasses = myLine.split(QIF_CLASSSEP);
                List<QIFClass> myList = new ArrayList<QIFClass>();
                for (String myClass : myClasses) {
                    myList.add(pFile.getClass(myClass));
                }

                /* Return the classes */
                return myList;
            }

            /* Return no classes */
            return null;
        }
    }

    /**
     * The Payee line.
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
         * The class list.
         */
        private final List<QIFClass> theClasses;

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
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(theCategory.getName());

            /* If we have classes */
            if (theClasses != null) {
                /* Add class indicator */
                pBuilder.append(QIF_CLASS);

                /* Iterate through the list */
                Iterator<QIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    QIFClass myClass = myIterator.next();

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
                int i = pLine.indexOf(QIF_CATSEP);
                myLine = pLine.substring(0, i);
            }

            /* If the line contains classes */
            if (myLine.contains(QIF_CLASS)) {
                /* drop class data */
                int i = myLine.indexOf(QIF_CLASS);
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
                int i = pLine.indexOf(QIF_CATSEP);
                myLine = pLine.substring(0, i);
            }

            /* If the line contains classes */
            if (myLine.contains(QIF_CLASS)) {
                /* drop preceding data */
                int i = myLine.indexOf(QIF_CLASS);
                myLine = myLine.substring(i + 1);

                /* Build list of classes */
                String[] myClasses = myLine.split(QIF_CLASSSEP);
                List<QIFClass> myList = new ArrayList<QIFClass>();
                for (String myClass : myClasses) {
                    myList.add(pFile.getClass(myClass));
                }

                /* Return the classes */
                return myList;
            }

            /* Return no classes */
            return null;
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

        @Override
        protected void formatData(final JDataFormatter pFormatter,
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
                Iterator<QIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    QIFClass myClass = myIterator.next();

                    /* Add to the list */
                    pBuilder.append(myClass.getName());
                    if (myIterator.hasNext()) {
                        pBuilder.append(QIF_CLASSSEP);
                    }
                }
            }
        }
    }
}
