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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QAccountLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFStringLine;

/**
 * Class representing a QIF Account record.
 */
public class QIFAccount
        extends QIFRecord<QAccountLineType> {
    /**
     * Item type.
     */
    protected static final String QIF_HDR = "!Account";

    /**
     * Category Map.
     */
    protected static final Map<AccountCategoryClass, String> QIF_ACTCATMAP = createClassMap();

    /**
     * The Account Name.
     */
    private final String theName;

    /**
     * The Account Description.
     */
    private final String theDesc;

    /**
     * The Account CategoryClass.
     */
    private final AccountCategoryClass theClass;

    /**
     * Obtain the Name.
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the description.
     * @return the Name
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Create the CategoryClass to type map.
     * @return the map
     */
    private static Map<AccountCategoryClass, String> createClassMap() {
        /* Create the map */
        Map<AccountCategoryClass, String> myMap = new EnumMap<AccountCategoryClass, String>(AccountCategoryClass.class);

        /* Add the entries */
        myMap.put(AccountCategoryClass.SAVINGS, "Bank");
        myMap.put(AccountCategoryClass.BOND, "Bank");
        myMap.put(AccountCategoryClass.CASH, "Cash");
        myMap.put(AccountCategoryClass.CREDITCARD, "CCard");
        myMap.put(AccountCategoryClass.PORTFOLIO, "Invst");
        myMap.put(AccountCategoryClass.PRIVATELOAN, "Oth A");
        myMap.put(AccountCategoryClass.LOAN, "Oth L");

        /* Return the map */
        return myMap;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pAccount the Account
     */
    public QIFAccount(final QIFFile pFile,
                      final Account pAccount) {
        /* Call super-constructor */
        super(pFile, QAccountLineType.class);

        /* Store data */
        theName = pAccount.getName();
        theDesc = pAccount.getComments();
        theClass = pAccount.getAccountCategoryClass();

        /* Build lines */
        addLine(new QIFAccountNameLine(theName));
        if (theDesc != null) {
            addLine(new QIFAccountDescLine(theDesc));
        }
        addLine(new QIFAccountTypeLine(theClass));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the formatter
     * @param pLines the data lines
     */
    protected QIFAccount(final QIFFile pFile,
                         final JDataFormatter pFormatter,
                         final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, QAccountLineType.class);

        /* Determine details */
        String myName = null;
        String myDesc = null;
        AccountCategoryClass myClass = null;

        /* Loop through the lines */
        Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            String myLine = myIterator.next();

            /* Determine the category */
            QAccountLineType myType = QAccountLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case NAME:
                        addLine(new QIFAccountNameLine(myData));
                        myName = myData;
                        break;
                    case DESCRIPTION:
                        addLine(new QIFAccountDescLine(myData));
                        myDesc = myData;
                        break;
                    case TYPE:
                        QIFAccountTypeLine myQLine = new QIFAccountTypeLine(myData);
                        addLine(myQLine);
                        myClass = myQLine.getAccountClass();
                        break;
                    case CREDITLIMIT:
                        JMoney myMoney = pFormatter.getDecimalParser().parseMoneyValue(myData);
                        addLine(new QIFAccountLimitLine(myMoney));
                        break;
                    default:
                        break;
                }
            }
        }

        /* Build details */
        theName = myName;
        theDesc = myDesc;
        theClass = myClass;
    }

    /**
     * The Account Name line.
     */
    public class QIFAccountNameLine
            extends QIFStringLine<QAccountLineType> {
        @Override
        public QAccountLineType getLineType() {
            return QAccountLineType.NAME;
        }

        /**
         * Obtain name.
         * @return the name
         */
        public String getName() {
            return getValue();
        }

        /**
         * Constructor.
         * @param pName the Name
         */
        protected QIFAccountNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }
    }

    /**
     * The Security Symbol line.
     */
    public class QIFAccountDescLine
            extends QIFStringLine<QAccountLineType> {
        @Override
        public QAccountLineType getLineType() {
            return QAccountLineType.DESCRIPTION;
        }

        /**
         * Obtain description.
         * @return the description
         */
        public String getDesc() {
            return getValue();
        }

        /**
         * Constructor.
         * @param pDesc the Description
         */
        protected QIFAccountDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }
    }

    /**
     * The Account Type line.
     */
    public class QIFAccountTypeLine
            extends QIFStringLine<QAccountLineType> {
        @Override
        public QAccountLineType getLineType() {
            return QAccountLineType.TYPE;
        }

        /**
         * The Account Category Class.
         */
        private final AccountCategoryClass theClass;

        /**
         * Obtain account class.
         * @return the account class
         */
        public AccountCategoryClass getAccountClass() {
            return theClass;
        }

        /**
         * Constructor.
         * @param pClass the Account Class
         */
        protected QIFAccountTypeLine(final AccountCategoryClass pClass) {
            /* Call super-constructor */
            super(QIF_ACTCATMAP.get(pClass));

            /* Record the class */
            theClass = pClass;
        }

        /**
         * Constructor.
         * @param pType the Account Type
         */
        protected QIFAccountTypeLine(final String pType) {
            /* Call super-constructor */
            super(pType);

            /* Loop through the map entries */
            AccountCategoryClass myClass = null;
            Iterator<Map.Entry<AccountCategoryClass, String>> myIterator = QIF_ACTCATMAP.entrySet().iterator();
            while (myIterator.hasNext()) {
                Map.Entry<AccountCategoryClass, String> myEntry = myIterator.next();

                /* If we have a match */
                if (pType.equals(myEntry.getValue())) {
                    myClass = myEntry.getKey();
                    break;
                }
            }

            /* Store the class */
            theClass = myClass;
        }
    }

    /**
     * The Account Credit Limit line.
     */
    public class QIFAccountLimitLine
            extends QIFMoneyLine<QAccountLineType> {
        @Override
        public QAccountLineType getLineType() {
            return QAccountLineType.CREDITLIMIT;
        }

        /**
         * Obtain credit limit.
         * @return the credit limit
         */
        public JMoney getCreditLimit() {
            return getMoney();
        }

        /**
         * Constructor.
         * @param pLimit the Credit Limit
         */
        protected QIFAccountLimitLine(final JMoney pLimit) {
            /* Call super-constructor */
            super(pLimit);
        }
    }
}
