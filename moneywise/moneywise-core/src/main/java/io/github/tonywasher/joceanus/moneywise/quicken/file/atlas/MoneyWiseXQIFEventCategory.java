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

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQCategoryLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFLine.MoneyWiseXQIFStringLine;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a XQIF Category record.
 */
public class MoneyWiseXQIFEventCategory
        extends MoneyWiseXQIFRecord<MoneyWiseQCategoryLineType>
        implements Comparable<MoneyWiseXQIFEventCategory> {
    /**
     * Item type.
     */
    protected static final String XQIF_ITEM = "Cat";

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
    public MoneyWiseXQIFEventCategory(final MoneyWiseTransCategory pCategory) {
        /* Call super-constructor */
        super(MoneyWiseQCategoryLineType.class);

        /* Store data */
        theName = pCategory.getName();
        theDesc = pCategory.getDesc();

        /* Determine whether this is an income category */
        final MoneyWiseTransCategoryClass myClass = pCategory.getCategoryTypeClass();
        isIncome = myClass.isIncome();

        /* Build lines */
        addLine(new MoneyWiseXQIFCategoryNameLine(theName));
        if (theDesc != null) {
            addLine(new MoneyWiseXQIFCategoryDescLine(theDesc));
        }
        if (isIncome) {
            addLine(new MoneyWiseXQIFCategoryIncomeLine());
        } else {
            addLine(new MoneyWiseXQIFCategoryExpenseLine());
        }
    }

    /**
     * Constructor.
     *
     * @param pLines the data lines
     */
    protected MoneyWiseXQIFEventCategory(final List<String> pLines) {
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
                        addLine(new MoneyWiseXQIFCategoryNameLine(myData));
                        myName = myData;
                        break;
                    case DESCRIPTION:
                        addLine(new MoneyWiseXQIFCategoryDescLine(myData));
                        myDesc = myData;
                        break;
                    case INCOME:
                        addLine(new MoneyWiseXQIFCategoryIncomeLine());
                        bIsIncome = true;
                        break;
                    case EXPENSE:
                        addLine(new MoneyWiseXQIFCategoryExpenseLine());
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
    public int compareTo(final MoneyWiseXQIFEventCategory pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Category Name line.
     */
    public static class MoneyWiseXQIFCategoryNameLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         *
         * @param pName the Name
         */
        protected MoneyWiseXQIFCategoryNameLine(final String pName) {
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
    public static class MoneyWiseXQIFCategoryDescLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         *
         * @param pDesc the Description
         */
        protected MoneyWiseXQIFCategoryDescLine(final String pDesc) {
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
    public static class MoneyWiseXQIFCategoryIncomeLine
            extends MoneyWiseXQIFLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         */
        protected MoneyWiseXQIFCategoryIncomeLine() {
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
    public static class MoneyWiseXQIFCategoryExpenseLine
            extends MoneyWiseXQIFLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         */
        protected MoneyWiseXQIFCategoryExpenseLine() {
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
    public static class MoneyWiseXQIFCategoryTaxLine
            extends MoneyWiseXQIFLine<MoneyWiseQCategoryLineType> {
        /**
         * Constructor.
         */
        protected MoneyWiseXQIFCategoryTaxLine() {
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
    public abstract static class MoneyWiseXQIFCategoryLine<X extends MoneyWiseQLineType>
            extends MoneyWiseXQIFLine<X> {
        /**
         * The event category.
         */
        private final MoneyWiseXQIFEventCategory theCategory;

        /**
         * The class list.
         */
        private final List<MoneyWiseXQIFClass> theClasses;

        /**
         * Constructor.
         *
         * @param pCategory the Event Category
         */
        protected MoneyWiseXQIFCategoryLine(final MoneyWiseXQIFEventCategory pCategory) {
            this(pCategory, null);
        }

        /**
         * Constructor.
         *
         * @param pCategory the Event Category
         * @param pClasses  the classes
         */
        protected MoneyWiseXQIFCategoryLine(final MoneyWiseXQIFEventCategory pCategory,
                                            final List<MoneyWiseXQIFClass> pClasses) {
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
        public MoneyWiseXQIFEventCategory getEventCategory() {
            return theCategory;
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
            pBuilder.append(theCategory.getName());

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
            final MoneyWiseXQIFCategoryLine<?> myLine = (MoneyWiseXQIFCategoryLine<?>) pThat;

            /* Check line type */
            if (!getLineType().equals(myLine.getLineType())) {
                return false;
            }

            /* Check category */
            if (!theCategory.equals(myLine.getEventCategory())) {
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
            return Objects.hash(getLineType(), theClasses, theCategory);
        }
    }

    /**
     * The Event Category line.
     *
     * @param <X> the line type
     */
    public abstract static class MoneyWiseXQIFCategoryAccountLine<X extends MoneyWiseQLineType>
            extends MoneyWiseXQIFLine<X> {
        /**
         * The event category.
         */
        private final MoneyWiseXQIFEventCategory theCategory;

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
         * @param pCategory the Event Category
         * @param pAccount  the Account
         */
        protected MoneyWiseXQIFCategoryAccountLine(final MoneyWiseXQIFEventCategory pCategory,
                                                   final MoneyWiseXQIFAccount pAccount) {
            this(pCategory, pAccount, null);
        }

        /**
         * Constructor.
         *
         * @param pCategory the Event Category
         * @param pAccount  the Account
         * @param pClasses  the classes
         */
        protected MoneyWiseXQIFCategoryAccountLine(final MoneyWiseXQIFEventCategory pCategory,
                                                   final MoneyWiseXQIFAccount pAccount,
                                                   final List<MoneyWiseXQIFClass> pClasses) {
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
        public MoneyWiseXQIFEventCategory getEventCategory() {
            return theCategory;
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
            pBuilder.append(theCategory.getName());
            pBuilder.append(MoneyWiseXQIFConstants.XQIF_CATSEP);
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
            final MoneyWiseXQIFCategoryAccountLine<?> myLine = (MoneyWiseXQIFCategoryAccountLine<?>) pThat;

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
            return Objects.hash(getLineType(), theClasses, theAccount, theCategory);
        }
    }
}
