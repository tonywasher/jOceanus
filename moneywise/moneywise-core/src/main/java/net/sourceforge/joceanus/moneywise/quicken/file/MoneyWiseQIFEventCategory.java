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

import java.util.List;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQCategoryLineType;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

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
     * @param pFile the QIF File
     * @param pCategory the Event Category
     */
    public MoneyWiseQIFEventCategory(final MoneyWiseQIFFile pFile,
                                     final MoneyWiseTransCategory pCategory) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQCategoryLineType.class);

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
     * @param pFile the QIF File
     * @param pLines the data lines
     */
    protected MoneyWiseQIFEventCategory(final MoneyWiseQIFFile pFile,
                                        final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQCategoryLineType.class);

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
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the Description.
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Is the Category an income category.
     * @return true/false
     */
    public boolean isIncome() {
        return isIncome;
    }

    /**
     * Is the Category an expense category.
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
        protected void formatData(final TethysUIDataFormatter pFormatter,
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
        protected void formatData(final TethysUIDataFormatter pFormatter,
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
        protected void formatData(final TethysUIDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* No data */
        }
    }
}
