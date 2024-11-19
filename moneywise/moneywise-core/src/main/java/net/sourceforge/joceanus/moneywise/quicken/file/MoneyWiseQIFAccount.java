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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQAccountLineType;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFMoneyLine;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class representing a QIF Account record.
 */
public class MoneyWiseQIFAccount
        extends MoneyWiseQIFRecord<MoneyWiseQAccountLineType>
        implements Comparable<MoneyWiseQIFAccount> {
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
    public MoneyWiseQIFAccount(final MoneyWiseQIFFile pFile,
                               final MoneyWiseTransAsset pAccount) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQAccountLineType.class);

        /* Store data */
        theName = pAccount.getName();

        /* Handle deposit */
        if (pAccount instanceof MoneyWiseDeposit) {
            final MoneyWiseDeposit myDeposit = (MoneyWiseDeposit) pAccount;
            theClass = myDeposit.getCategoryClass();
            theDesc = myDeposit.getDesc();

            /* Handle cash */
        } else if (pAccount instanceof MoneyWiseCash) {
            final MoneyWiseCash myCash = (MoneyWiseCash) pAccount;
            theClass = myCash.getCategoryClass();
            theDesc = myCash.getDesc();

            /* Handle loan */
        } else if (pAccount instanceof MoneyWiseLoan) {
            final MoneyWiseLoan myLoan = (MoneyWiseLoan) pAccount;
            theClass = myLoan.getCategoryClass();
            theDesc = myLoan.getDesc();

            /* Handle portfolio */
        } else if (pAccount instanceof MoneyWisePortfolio) {
            final MoneyWisePortfolio myPortfolio = (MoneyWisePortfolio) pAccount;
            theClass = MoneyWiseBasicDataType.PORTFOLIO;
            theDesc = myPortfolio.getDesc();
        } else {
            throw new IllegalArgumentException();
        }

        /* Build lines */
        addLine(new MoneyWiseQIFAccountNameLine(theName));
        if (theDesc != null) {
            addLine(new MoneyWiseQIFAccountDescLine(theDesc));
        }
        addLine(new MoneyWiseQIFAccountTypeLine(theClass));
    }

    /**
     * Constructor for holding account.
     * @param pFile the QIF File
     * @param pName the Portfolio Name
     */
    protected MoneyWiseQIFAccount(final MoneyWiseQIFFile pFile,
                                  final String pName) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQAccountLineType.class);

        /* Store data */
        theName = pName;
        theClass = MoneyWiseDepositCategoryClass.SAVINGS;
        theDesc = null;

        /* Build lines */
        addLine(new MoneyWiseQIFAccountNameLine(theName));
        addLine(new MoneyWiseQIFAccountTypeLine(theClass));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the formatter
     * @param pLines the data lines
     */
    protected MoneyWiseQIFAccount(final MoneyWiseQIFFile pFile,
                                  final TethysUIDataFormatter pFormatter,
                                  final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQAccountLineType.class);

        /* Determine details */
        String myName = null;
        String myDesc = null;
        Enum<?> myClass = null;

        /* Loop through the lines */
        for (String myLine : pLines) {
            /* Determine the category */
            final MoneyWiseQAccountLineType myType = MoneyWiseQAccountLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case NAME:
                        addLine(new MoneyWiseQIFAccountNameLine(myData));
                        myName = myData;
                        break;
                    case DESCRIPTION:
                        addLine(new MoneyWiseQIFAccountDescLine(myData));
                        myDesc = myData;
                        break;
                    case TYPE:
                        final MoneyWiseQIFAccountTypeLine myQLine = new MoneyWiseQIFAccountTypeLine(myData);
                        addLine(myQLine);
                        myClass = myQLine.getAccountClass();
                        break;
                    case CREDITLIMIT:
                        final OceanusMoney myMoney = pFormatter.getDecimalParser().parseMoneyValue(myData);
                        addLine(new MoneyWiseQIFAccountLimitLine(myMoney));
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
        myMap.put(MoneyWiseDepositCategoryClass.CHECKING, QIFACT_BANK);
        myMap.put(MoneyWiseDepositCategoryClass.SAVINGS, QIFACT_BANK);
        myMap.put(MoneyWiseDepositCategoryClass.PEER2PEER, QIFACT_BANK);
        myMap.put(MoneyWiseDepositCategoryClass.BOND, QIFACT_BANK);
        myMap.put(MoneyWiseCashCategoryClass.CASH, QIFACT_CASH);
        myMap.put(MoneyWiseCashCategoryClass.AUTOEXPENSE, QIFACT_CASH);
        myMap.put(MoneyWiseLoanCategoryClass.CREDITCARD, QIFACT_CCARD);
        myMap.put(MoneyWiseBasicDataType.PORTFOLIO, QIFACT_INVST);
        myMap.put(MoneyWiseLoanCategoryClass.PRIVATELOAN, QIFACT_ASSET);
        myMap.put(MoneyWiseLoanCategoryClass.LOAN, QIFACT_LOAN);

        /* Return the map */
        return myMap;
    }

    @Override
    public int compareTo(final MoneyWiseQIFAccount pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Account Name line.
     */
    public static class MoneyWiseQIFAccountNameLine
            extends MoneyWiseQIFStringLine<MoneyWiseQAccountLineType> {
        /**
         * Constructor.
         * @param pName the Name
         */
        protected MoneyWiseQIFAccountNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }

        @Override
        public MoneyWiseQAccountLineType getLineType() {
            return MoneyWiseQAccountLineType.NAME;
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
    public static class MoneyWiseQIFAccountDescLine
            extends MoneyWiseQIFStringLine<MoneyWiseQAccountLineType> {
        /**
         * Constructor.
         * @param pDesc the Description
         */
        protected MoneyWiseQIFAccountDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }

        @Override
        public MoneyWiseQAccountLineType getLineType() {
            return MoneyWiseQAccountLineType.DESCRIPTION;
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
    public static class MoneyWiseQIFAccountTypeLine
            extends MoneyWiseQIFStringLine<MoneyWiseQAccountLineType> {
        /**
         * The Account Category Class.
         */
        private final Enum<?> theClass;

        /**
         * Constructor.
         * @param pClass the Account Class
         */
        protected MoneyWiseQIFAccountTypeLine(final Enum<?> pClass) {
            /* Call super-constructor */
            super(QIF_ACTCATMAP.get(pClass));

            /* Record the class */
            theClass = pClass;
        }

        /**
         * Constructor.
         * @param pType the Account Type
         */
        protected MoneyWiseQIFAccountTypeLine(final String pType) {
            /* Call super-constructor */
            super(pType);

            /* Loop through the map entries */
            Enum<?> myClass = null;
            for (Entry<Enum<?>, String> myEntry : QIF_ACTCATMAP.entrySet()) {
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
        public MoneyWiseQAccountLineType getLineType() {
            return MoneyWiseQAccountLineType.TYPE;
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
    public static class MoneyWiseQIFAccountLimitLine
            extends MoneyWiseQIFMoneyLine<MoneyWiseQAccountLineType> {
        /**
         * Constructor.
         * @param pLimit the Credit Limit
         */
        protected MoneyWiseQIFAccountLimitLine(final OceanusMoney pLimit) {
            /* Call super-constructor */
            super(pLimit);
        }

        @Override
        public MoneyWiseQAccountLineType getLineType() {
            return MoneyWiseQAccountLineType.CREDITLIMIT;
        }

        /**
         * Obtain credit limit.
         * @return the credit limit
         */
        public OceanusMoney getCreditLimit() {
            return getMoney();
        }
    }
}
