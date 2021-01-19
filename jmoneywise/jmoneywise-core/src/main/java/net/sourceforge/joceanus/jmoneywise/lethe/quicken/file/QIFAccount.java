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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QAccountLineType;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Class representing a QIF Account record.
 */
public class QIFAccount
        extends QIFRecord<QAccountLineType>
        implements Comparable<QIFAccount> {
    /**
     * Item type.
     */
    protected static final String QIF_HDR = "!Account";

    /**
     * Bank Account Type.
     */
    protected static final String QIFACT_BANK = "Bank";

    /**
     * Cash Account Type.
     */
    protected static final String QIFACT_CASH = "Cash";

    /**
     * Investment Account Type.
     */
    protected static final String QIFACT_INVST = "Invst";

    /**
     * Credit Card Account Type.
     */
    protected static final String QIFACT_CCARD = "CCard";

    /**
     * Asset Account Type.
     */
    protected static final String QIFACT_ASSET = "Oth A";

    /**
     * Loan Account Type.
     */
    protected static final String QIFACT_LOAN = "Oth L";

    /**
     * Category Map.
     */
    protected static final Map<Enum<?>, String> QIF_ACTCATMAP = createClassMap();

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
    private final Enum<?> theClass;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pAccount the Account
     */
    public QIFAccount(final QIFFile pFile,
                      final TransactionAsset pAccount) {
        /* Call super-constructor */
        super(pFile, QAccountLineType.class);

        /* Store data */
        theName = pAccount.getName();

        /* Handle deposit */
        if (pAccount instanceof Deposit) {
            final Deposit myDeposit = (Deposit) pAccount;
            theClass = myDeposit.getCategoryClass();
            theDesc = myDeposit.getDesc();

            /* Handle cash */
        } else if (pAccount instanceof Cash) {
            final Cash myCash = (Cash) pAccount;
            theClass = myCash.getCategoryClass();
            theDesc = myCash.getDesc();

            /* Handle loan */
        } else if (pAccount instanceof Loan) {
            final Loan myLoan = (Loan) pAccount;
            theClass = myLoan.getCategoryClass();
            theDesc = myLoan.getDesc();

            /* Handle portfolio */
        } else if (pAccount instanceof Portfolio) {
            final Portfolio myPortfolio = (Portfolio) pAccount;
            theClass = MoneyWiseDataType.PORTFOLIO;
            theDesc = myPortfolio.getDesc();
        } else {
            throw new IllegalArgumentException();
        }

        /* Build lines */
        addLine(new QIFAccountNameLine(theName));
        if (theDesc != null) {
            addLine(new QIFAccountDescLine(theDesc));
        }
        addLine(new QIFAccountTypeLine(theClass));
    }

    /**
     * Constructor for holding account.
     * @param pFile the QIF File
     * @param pName the Portfolio Name
     */
    protected QIFAccount(final QIFFile pFile,
                         final String pName) {
        /* Call super-constructor */
        super(pFile, QAccountLineType.class);

        /* Store data */
        theName = pName;
        theClass = DepositCategoryClass.SAVINGS;
        theDesc = null;

        /* Build lines */
        addLine(new QIFAccountNameLine(theName));
        addLine(new QIFAccountTypeLine(theClass));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the formatter
     * @param pLines the data lines
     */
    protected QIFAccount(final QIFFile pFile,
                         final MetisDataFormatter pFormatter,
                         final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, QAccountLineType.class);

        /* Determine details */
        String myName = null;
        String myDesc = null;
        Enum<?> myClass = null;

        /* Loop through the lines */
        final Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            final String myLine = myIterator.next();

            /* Determine the category */
            final QAccountLineType myType = QAccountLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

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
                        final QIFAccountTypeLine myQLine = new QIFAccountTypeLine(myData);
                        addLine(myQLine);
                        myClass = myQLine.getAccountClass();
                        break;
                    case CREDITLIMIT:
                        final TethysMoney myMoney = pFormatter.getDecimalParser().parseMoneyValue(myData);
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

    @Override
    public String toString() {
        return getName();
    }

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
     * Obtain the account type.
     * @return the Type
     */
    public String getType() {
        return QIF_ACTCATMAP.get(theClass);
    }

    /**
     * Create the CategoryClass to type map.
     * @return the map
     */
    private static Map<Enum<?>, String> createClassMap() {
        /* Create the map */
        final Map<Enum<?>, String> myMap = new HashMap<>();

        /* Add the entries */
        myMap.put(DepositCategoryClass.CHECKING, QIFACT_BANK);
        myMap.put(DepositCategoryClass.SAVINGS, QIFACT_BANK);
        myMap.put(DepositCategoryClass.PEER2PEER, QIFACT_BANK);
        myMap.put(DepositCategoryClass.BOND, QIFACT_BANK);
        myMap.put(CashCategoryClass.CASH, QIFACT_CASH);
        myMap.put(CashCategoryClass.AUTOEXPENSE, QIFACT_CASH);
        myMap.put(LoanCategoryClass.CREDITCARD, QIFACT_CCARD);
        myMap.put(MoneyWiseDataType.PORTFOLIO, QIFACT_INVST);
        myMap.put(LoanCategoryClass.PRIVATELOAN, QIFACT_ASSET);
        myMap.put(LoanCategoryClass.LOAN, QIFACT_LOAN);

        /* Return the map */
        return myMap;
    }

    @Override
    public int compareTo(final QIFAccount pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Account Name line.
     */
    public class QIFAccountNameLine
            extends QIFStringLine<QAccountLineType> {
        /**
         * Constructor.
         * @param pName the Name
         */
        protected QIFAccountNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }

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
    }

    /**
     * The Security Symbol line.
     */
    public class QIFAccountDescLine
            extends QIFStringLine<QAccountLineType> {
        /**
         * Constructor.
         * @param pDesc the Description
         */
        protected QIFAccountDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }

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
    }

    /**
     * The Account Type line.
     */
    public class QIFAccountTypeLine
            extends QIFStringLine<QAccountLineType> {
        /**
         * The Account Category Class.
         */
        private final Enum<?> theClass;

        /**
         * Constructor.
         * @param pClass the Account Class
         */
        protected QIFAccountTypeLine(final Enum<?> pClass) {
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
            Enum<?> myClass = null;
            final Iterator<Entry<Enum<?>, String>> myIterator = QIF_ACTCATMAP.entrySet().iterator();
            while (myIterator.hasNext()) {
                final Entry<Enum<?>, String> myEntry = myIterator.next();

                /* If we have a match */
                if (pType.equals(myEntry.getValue())) {
                    myClass = myEntry.getKey();
                    break;
                }
            }

            /* Store the class */
            theClass = myClass;
        }

        @Override
        public QAccountLineType getLineType() {
            return QAccountLineType.TYPE;
        }

        @Override
        public String toString() {
            return theClass.toString();
        }

        /**
         * Obtain account class.
         * @return the account class
         */
        public Enum<?> getAccountClass() {
            return theClass;
        }
    }

    /**
     * The Account Credit Limit line.
     */
    public class QIFAccountLimitLine
            extends QIFMoneyLine<QAccountLineType> {
        /**
         * Constructor.
         * @param pLimit the Credit Limit
         */
        protected QIFAccountLimitLine(final TethysMoney pLimit) {
            /* Call super-constructor */
            super(pLimit);
        }

        @Override
        public QAccountLineType getLineType() {
            return QAccountLineType.CREDITLIMIT;
        }

        /**
         * Obtain credit limit.
         * @return the credit limit
         */
        public TethysMoney getCreditLimit() {
            return getMoney();
        }
    }
}
