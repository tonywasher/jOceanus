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
package io.github.tonywasher.joceanus.moneywise.quicken.file.atlas;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCash;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseLoan;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCashCategoryClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQAccountLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFLine.MoneyWiseXQIFMoneyLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFLine.MoneyWiseXQIFStringLine;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * Class representing a XQIF Account record.
 */
public class MoneyWiseXQIFAccount
        extends MoneyWiseXQIFRecord<MoneyWiseQAccountLineType>
        implements Comparable<MoneyWiseXQIFAccount> {
    /**
     * Item type.
     */
    protected static final String XQIF_HDR = "!Account";

    /**
     * Bank Account Type.
     */
    protected static final String XQIFACT_BANK = "Bank";

    /**
     * Cash Account Type.
     */
    protected static final String XQIFACT_CASH = "Cash";

    /**
     * Investment Account Type.
     */
    protected static final String XQIFACT_INVST = "Invst";

    /**
     * Credit Card Account Type.
     */
    protected static final String XQIFACT_CCARD = "CCard";

    /**
     * Asset Account Type.
     */
    protected static final String XQIFACT_ASSET = "Oth A";

    /**
     * Loan Account Type.
     */
    protected static final String XQIFACT_LOAN = "Oth L";

    /**
     * Category Map.
     */
    protected static final Map<Enum<?>, String> XQIF_ACTCATMAP = createClassMap();

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
     *
     * @param pAccount the Account
     */
    public MoneyWiseXQIFAccount(final MoneyWiseTransAsset pAccount) {
        /* Call super-constructor */
        super(MoneyWiseQAccountLineType.class);

        /* Store data */
        theName = pAccount.getName();

        /* Switch on account */
        switch (pAccount) {
            case MoneyWiseDeposit myDeposit -> {
                theClass = myDeposit.getCategoryClass();
                theDesc = myDeposit.getDesc();
            }
            case MoneyWiseCash myCash -> {
                theClass = myCash.getCategoryClass();
                theDesc = myCash.getDesc();
            }
            case MoneyWiseLoan myLoan -> {
                theClass = myLoan.getCategoryClass();
                theDesc = myLoan.getDesc();
            }
            case MoneyWisePortfolio myPortfolio -> {
                theClass = MoneyWiseBasicDataType.PORTFOLIO;
                theDesc = myPortfolio.getDesc();
            }
            default -> throw new IllegalArgumentException();
        }

        /* Build lines */
        addLine(new MoneyWiseXQIFAccountNameLine(theName));
        if (theDesc != null) {
            addLine(new MoneyWiseXQIFAccountDescLine(theDesc));
        }
        addLine(new MoneyWiseXQIFAccountTypeLine(theClass));
    }

    /**
     * Constructor for holding account.
     *
     * @param pName the Portfolio Name
     */
    protected MoneyWiseXQIFAccount(final String pName) {
        /* Call super-constructor */
        super(MoneyWiseQAccountLineType.class);

        /* Store data */
        theName = pName;
        theClass = MoneyWiseDepositCategoryClass.SAVINGS;
        theDesc = null;

        /* Build lines */
        addLine(new MoneyWiseXQIFAccountNameLine(theName));
        addLine(new MoneyWiseXQIFAccountTypeLine(theClass));
    }

    /**
     * Constructor.
     *
     * @param pFormatter the formatter
     * @param pLines     the data lines
     */
    protected MoneyWiseXQIFAccount(final OceanusDataFormatter pFormatter,
                                   final List<String> pLines) {
        /* Call super-constructor */
        super(MoneyWiseQAccountLineType.class);

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
                        addLine(new MoneyWiseXQIFAccountNameLine(myData));
                        myName = myData;
                        break;
                    case DESCRIPTION:
                        addLine(new MoneyWiseXQIFAccountDescLine(myData));
                        myDesc = myData;
                        break;
                    case TYPE:
                        final MoneyWiseXQIFAccountTypeLine myQLine = new MoneyWiseXQIFAccountTypeLine(myData);
                        addLine(myQLine);
                        myClass = myQLine.getAccountClass();
                        break;
                    case CREDITLIMIT:
                        final OceanusMoney myMoney = pFormatter.getDecimalParser().parseMoneyValue(myData);
                        addLine(new MoneyWiseXQIFAccountLimitLine(myMoney));
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
     *
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the description.
     *
     * @return the Name
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Obtain the account type.
     *
     * @return the Type
     */
    public String getType() {
        return XQIF_ACTCATMAP.get(theClass);
    }

    /**
     * Create the CategoryClass to type map.
     *
     * @return the map
     */
    private static Map<Enum<?>, String> createClassMap() {
        /* Create the map */
        final Map<Enum<?>, String> myMap = new HashMap<>();

        /* Add the entries */
        myMap.put(MoneyWiseDepositCategoryClass.CHECKING, XQIFACT_BANK);
        myMap.put(MoneyWiseDepositCategoryClass.SAVINGS, XQIFACT_BANK);
        myMap.put(MoneyWiseDepositCategoryClass.PEER2PEER, XQIFACT_BANK);
        myMap.put(MoneyWiseDepositCategoryClass.BOND, XQIFACT_BANK);
        myMap.put(MoneyWiseCashCategoryClass.CASH, XQIFACT_CASH);
        myMap.put(MoneyWiseCashCategoryClass.AUTOEXPENSE, XQIFACT_CASH);
        myMap.put(MoneyWiseLoanCategoryClass.CREDITCARD, XQIFACT_CCARD);
        myMap.put(MoneyWiseBasicDataType.PORTFOLIO, XQIFACT_INVST);
        myMap.put(MoneyWiseLoanCategoryClass.PRIVATELOAN, XQIFACT_ASSET);
        myMap.put(MoneyWiseLoanCategoryClass.LOAN, XQIFACT_LOAN);

        /* Return the map */
        return myMap;
    }

    @Override
    public int compareTo(final MoneyWiseXQIFAccount pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Account Name line.
     */
    public static class MoneyWiseXQIFAccountNameLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQAccountLineType> {
        /**
         * Constructor.
         *
         * @param pName the Name
         */
        protected MoneyWiseXQIFAccountNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }

        @Override
        public MoneyWiseQAccountLineType getLineType() {
            return MoneyWiseQAccountLineType.NAME;
        }

        /**
         * Obtain name.
         *
         * @return the name
         */
        public String getName() {
            return getValue();
        }
    }

    /**
     * The Security Symbol line.
     */
    public static class MoneyWiseXQIFAccountDescLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQAccountLineType> {
        /**
         * Constructor.
         *
         * @param pDesc the Description
         */
        protected MoneyWiseXQIFAccountDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }

        @Override
        public MoneyWiseQAccountLineType getLineType() {
            return MoneyWiseQAccountLineType.DESCRIPTION;
        }

        /**
         * Obtain description.
         *
         * @return the description
         */
        public String getDesc() {
            return getValue();
        }
    }

    /**
     * The Account Type line.
     */
    public static class MoneyWiseXQIFAccountTypeLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQAccountLineType> {
        /**
         * The Account Category Class.
         */
        private final Enum<?> theClass;

        /**
         * Constructor.
         *
         * @param pClass the Account Class
         */
        protected MoneyWiseXQIFAccountTypeLine(final Enum<?> pClass) {
            /* Call super-constructor */
            super(XQIF_ACTCATMAP.get(pClass));

            /* Record the class */
            theClass = pClass;
        }

        /**
         * Constructor.
         *
         * @param pType the Account Type
         */
        protected MoneyWiseXQIFAccountTypeLine(final String pType) {
            /* Call super-constructor */
            super(pType);

            /* Loop through the map entries */
            Enum<?> myClass = null;
            for (Entry<Enum<?>, String> myEntry : XQIF_ACTCATMAP.entrySet()) {
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
         *
         * @return the account class
         */
        public Enum<?> getAccountClass() {
            return theClass;
        }
    }

    /**
     * The Account Credit Limit line.
     */
    public static class MoneyWiseXQIFAccountLimitLine
            extends MoneyWiseXQIFMoneyLine<MoneyWiseQAccountLineType> {
        /**
         * Constructor.
         *
         * @param pLimit the Credit Limit
         */
        protected MoneyWiseXQIFAccountLimitLine(final OceanusMoney pLimit) {
            /* Call super-constructor */
            super(pLimit);
        }

        @Override
        public MoneyWiseQAccountLineType getLineType() {
            return MoneyWiseQAccountLineType.CREDITLIMIT;
        }

        /**
         * Obtain credit limit.
         *
         * @return the credit limit
         */
        public OceanusMoney getCreditLimit() {
            return getMoney();
        }
    }

    /**
     * The Account line.
     *
     * @param <X> the line type
     */
    public abstract static class MoneyWiseXQIFXferAccountLine<X extends MoneyWiseQLineType>
            extends MoneyWiseXQIFLine<X> {
        /**
         * The account.
         */
        private final MoneyWiseXQIFAccount theAccount;

        /**
         * The class list.
         */
        private final List<MoneyWiseXQIFClass> theClasses;

        /**
         * Constructor.
         *
         * @param pAccount the Account
         */
        protected MoneyWiseXQIFXferAccountLine(final MoneyWiseXQIFAccount pAccount) {
            this(pAccount, null);
        }

        /**
         * Constructor.
         *
         * @param pAccount the Account
         * @param pClasses the classes
         */
        protected MoneyWiseXQIFXferAccountLine(final MoneyWiseXQIFAccount pAccount,
                                               final List<MoneyWiseXQIFClass> pClasses) {
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
         *
         * @return the account
         */
        public MoneyWiseXQIFAccount getAccount() {
            return theAccount;
        }

        /**
         * Obtain class list.
         *
         * @return the class list
         */
        public List<MoneyWiseXQIFClass> getClassList() {
            return theClasses;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Append the string data */
            pBuilder.append(MoneyWiseXQIFConstants.XQIF_XFERSTART);
            pBuilder.append(theAccount.getName());
            pBuilder.append(MoneyWiseXQIFConstants.XQIF_XFEREND);

            /* If we have classes */
            if (theClasses != null) {
                /* Add class indicator */
                pBuilder.append(MoneyWiseXQIFConstants.XQIF_CLASS);

                /* Iterate through the list */
                final Iterator<MoneyWiseXQIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseXQIFClass myClass = myIterator.next();

                    /* Add to the list */
                    pBuilder.append(myClass.getName());
                    if (myIterator.hasNext()) {
                        pBuilder.append(MoneyWiseXQIFConstants.XQIF_CLASSSEP);
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
            final MoneyWiseXQIFXferAccountLine<?> myLine = (MoneyWiseXQIFXferAccountLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check account */
            if (!theAccount.equals(myLine.getAccount())) {
                return false;
            }

            /* Check classes */
            final List<MoneyWiseXQIFClass> myClasses = myLine.getClassList();
            if (theClasses == null) {
                return myClasses == null;
            } else if (myClasses == null) {
                return true;
            }
            return theClasses.equals(myClasses);
        }

        @Override
        public int hashCode() {
            return Objects.hash(getLineType(), theClasses, theAccount);
        }
    }
}
