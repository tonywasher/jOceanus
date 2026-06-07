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

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQCategoryLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a QIF Category record.
 */
public class MoneyWiseQIFEventCategory
        extends MoneyWiseQIFRecord<MoneyWiseQCategoryLineType>
        implements Comparable<MoneyWiseQIFEventCategory> {
    /**
     * Item type.
     */
    protected static final String QIF_ITEM = "Cat";

    /**
     * The Category Name.
     */
    private final String theName;

    /**
     * The Category Description.
     */
    private final String theDesc;

    /**
     * The Category Type.
     */
    private final boolean isIncome;

    /**
     * Constructor.
     *
     * @param pCategory the Event Category
     */
    public MoneyWiseQIFEventCategory(final MoneyWiseTransCategory pCategory) {
        /* Call super-constructor */
        super(MoneyWiseQCategoryLineType.class);

        /* Store data */
        theName = pCategory.getName();
        theDesc = pCategory.getDesc();

        /* Determine whether this is an income category */
        final MoneyWiseTransCategoryClass myClass = pCategory.getCategoryTypeClass();
        isIncome = myClass.isIncome();

        /* Build lines */
        addLine(new MoneyWiseQIFCategoryNameLine(theName));
        if (theDesc != null) {
            addLine(new MoneyWiseQIFCategoryDescLine(theDesc));
        }
        if (isIncome) {
            addLine(new MoneyWiseQIFCategoryIncomeLine());
        } else {
            addLine(new MoneyWiseQIFCategoryExpenseLine());
        }
    }

    /**
     * Constructor.
     *
     * @param pLines the data lines
     */
    protected MoneyWiseQIFEventCategory(final List<String> pLines) {
        /* Call super-constructor */
        super(MoneyWiseQCategoryLineType.class);

        /* Determine details */
        String myName = null;
        String myDesc = null;
        boolean bIsIncome = false;

        /* Loop through the lines */
        for (String myLine : pLines) {
            /* Determine the category */
            final MoneyWiseQCategoryLineType myType = MoneyWiseQCategoryLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case NAME:
                        addLine(new MoneyWiseQIFCategoryNameLine(myData));
                        myName = myData;
                        break;
                    case DESCRIPTION:
                        addLine(new MoneyWiseQIFCategoryDescLine(myData));
                        myDesc = myData;
                        break;
                    case INCOME:
                        addLine(new MoneyWiseQIFCategoryIncomeLine());
                        bIsIncome = true;
                        break;
                    case EXPENSE:
                        addLine(new MoneyWiseQIFCategoryExpenseLine());
                        bIsIncome = false;
                        break;
                    case TAX:
                    default:
                        break;
                }
            }
        }

        /* Build details */
        theName = myName;
        theDesc = myDesc;
        isIncome = bIsIncome;
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
     * Obtain the Description.
     *
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Is the Category an income category.
     *
     * @return true/false
     */
    public boolean isIncome() {
        return isIncome;
    }

    /**
     * Is the Category an expense category.
     *
     * @return true/false
     */
    public boolean isExpense() {
        return !isIncome;
    }

    @Override
    public int compareTo(final MoneyWiseQIFEventCategory pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Category Name line.
     */
    public static class MoneyWiseQIFCategoryNameLine
            extends MoneyWiseQIFStringLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         *
         * @param pName the Name
         */
        protected MoneyWiseQIFCategoryNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }

        @Override
        public MoneyWiseQCategoryLineType getLineType() {
            return MoneyWiseQCategoryLineType.NAME;
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
     * The Category Description line.
     */
    public static class MoneyWiseQIFCategoryDescLine
            extends MoneyWiseQIFStringLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         *
         * @param pDesc the Description
         */
        protected MoneyWiseQIFCategoryDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }

        @Override
        public MoneyWiseQCategoryLineType getLineType() {
            return MoneyWiseQCategoryLineType.DESCRIPTION;
        }

        /**
         * Obtain description.
         *
         * @return the description
         */
        public String getDescription() {
            return getValue();
        }
    }

    /**
     * The Category Income line.
     */
    public static class MoneyWiseQIFCategoryIncomeLine
            extends MoneyWiseQIFLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         */
        protected MoneyWiseQIFCategoryIncomeLine() {
        }

        @Override
        public MoneyWiseQCategoryLineType getLineType() {
            return MoneyWiseQCategoryLineType.INCOME;
        }

        @Override
        public String toString() {
            return getLineType().getSymbol();
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* No data */
        }
    }

    /**
     * The Category Expense line.
     */
    public static class MoneyWiseQIFCategoryExpenseLine
            extends MoneyWiseQIFLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         */
        protected MoneyWiseQIFCategoryExpenseLine() {
        }

        @Override
        public MoneyWiseQCategoryLineType getLineType() {
            return MoneyWiseQCategoryLineType.EXPENSE;
        }

        @Override
        public String toString() {
            return getLineType().getSymbol();
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* No data */
        }
    }

    /**
     * The Category Tax line.
     */
    public static class MoneyWiseQIFCategoryTaxLine
            extends MoneyWiseQIFLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         */
        protected MoneyWiseQIFCategoryTaxLine() {
        }

        @Override
        public MoneyWiseQCategoryLineType getLineType() {
            return MoneyWiseQCategoryLineType.TAX;
        }

        @Override
        public String toString() {
            return getLineType().getSymbol();
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* No data */
        }
    }

    /**
     * The Event Category line.
     *
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
         *
         * @param pCategory the Event Category
         */
        protected MoneyWiseQIFCategoryLine(final MoneyWiseQIFEventCategory pCategory) {
            this(pCategory, null);
        }

        /**
         * Constructor.
         *
         * @param pCategory the Event Category
         * @param pClasses  the classes
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
         *
         * @return the event category
         */
        public MoneyWiseQIFEventCategory getEventCategory() {
            return theCategory;
        }

        /**
         * Obtain class list.
         *
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
                pBuilder.append(MoneyWiseQIFConstants.QIF_CLASS);

                /* Iterate through the list */
                final Iterator<MoneyWiseQIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseQIFClass myClass = myIterator.next();

                    /* Add to the list */
                    pBuilder.append(myClass.getName());
                    if (myIterator.hasNext()) {
                        pBuilder.append(MoneyWiseQIFConstants.QIF_CLASSSEP);
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
            return Objects.hash(getLineType(), theClasses, theCategory);
        }
    }

    /**
     * The Event Category line.
     *
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
         *
         * @param pCategory the Event Category
         * @param pAccount  the Account
         */
        protected MoneyWiseQIFCategoryAccountLine(final MoneyWiseQIFEventCategory pCategory,
                                                  final MoneyWiseQIFAccount pAccount) {
            this(pCategory, pAccount, null);
        }

        /**
         * Constructor.
         *
         * @param pCategory the Event Category
         * @param pAccount  the Account
         * @param pClasses  the classes
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
         *
         * @return the event category
         */
        public MoneyWiseQIFEventCategory getEventCategory() {
            return theCategory;
        }

        /**
         * Obtain account.
         *
         * @return the account
         */
        public MoneyWiseQIFAccount getAccount() {
            return theAccount;
        }

        /**
         * Obtain class list.
         *
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
            pBuilder.append(MoneyWiseQIFConstants.QIF_CATSEP);
            pBuilder.append(MoneyWiseQIFConstants.QIF_XFERSTART);
            pBuilder.append(theAccount.getName());
            pBuilder.append(MoneyWiseQIFConstants.QIF_XFEREND);

            /* If we have classes */
            if (theClasses != null) {
                /* Add class indicator */
                pBuilder.append(MoneyWiseQIFConstants.QIF_CLASS);

                /* Iterate through the list */
                final Iterator<MoneyWiseQIFClass> myIterator = theClasses.iterator();
                while (myIterator.hasNext()) {
                    final MoneyWiseQIFClass myClass = myIterator.next();

                    /* Add to the list */
                    pBuilder.append(myClass.getName());
                    if (myIterator.hasNext()) {
                        pBuilder.append(MoneyWiseQIFConstants.QIF_CLASSSEP);
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
            return Objects.hash(getLineType(), theClasses, theAccount, theCategory);
        }
    }
}
