/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QCategoryLineType;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Class representing a QIF Category record.
 */
public class QIFEventCategory
        extends QIFRecord<QCategoryLineType>
        implements Comparable<QIFEventCategory> {
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
    public QIFEventCategory(final QIFFile pFile,
                            final TransactionCategory pCategory) {
        /* Call super-constructor */
        super(pFile, QCategoryLineType.class);

        /* Store data */
        theName = pCategory.getName();
        theDesc = pCategory.getDesc();

        /* Determine whether this is an income category */
        final TransactionCategoryClass myClass = pCategory.getCategoryTypeClass();
        isIncome = myClass.isIncome();

        /* Build lines */
        addLine(new QIFCategoryNameLine(theName));
        if (theDesc != null) {
            addLine(new QIFCategoryDescLine(theDesc));
        }
        if (isIncome) {
            addLine(new QIFCategoryIncomeLine());
        } else {
            addLine(new QIFCategoryExpenseLine());
        }
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pLines the data lines
     */
    protected QIFEventCategory(final QIFFile pFile,
                               final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, QCategoryLineType.class);

        /* Determine details */
        String myName = null;
        String myDesc = null;
        boolean bIsIncome = false;

        /* Loop through the lines */
        final Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            final String myLine = myIterator.next();

            /* Determine the category */
            final QCategoryLineType myType = QCategoryLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case NAME:
                        addLine(new QIFCategoryNameLine(myData));
                        myName = myData;
                        break;
                    case DESCRIPTION:
                        addLine(new QIFCategoryDescLine(myData));
                        myDesc = myData;
                        break;
                    case INCOME:
                        addLine(new QIFCategoryIncomeLine());
                        bIsIncome = true;
                        break;
                    case EXPENSE:
                        addLine(new QIFCategoryExpenseLine());
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
    public int compareTo(final QIFEventCategory pThat) {
        return theName.compareTo(pThat.getName());
    }

    /**
     * The Category Name line.
     */
    public class QIFCategoryNameLine
            extends QIFStringLine<QCategoryLineType> {
        /**
         * Constructor.
         * @param pName the Name
         */
        protected QIFCategoryNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }

        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.NAME;
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
    public class QIFCategoryDescLine
            extends QIFStringLine<QCategoryLineType> {
        /**
         * Constructor.
         * @param pDesc the Description
         */
        protected QIFCategoryDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }

        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.DESCRIPTION;
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
    public static class QIFCategoryIncomeLine
            extends QIFLine<QCategoryLineType> {
        /**
         * Constructor.
         */
        protected QIFCategoryIncomeLine() {
        }

        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.INCOME;
        }

        @Override
        public String toString() {
            return getLineType().getSymbol();
        }

        @Override
        protected void formatData(final TethysDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* No data */
        }
    }

    /**
     * The Category Expense line.
     */
    public static class QIFCategoryExpenseLine
            extends QIFLine<QCategoryLineType> {
        /**
         * Constructor.
         */
        protected QIFCategoryExpenseLine() {
        }

        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.EXPENSE;
        }

        @Override
        public String toString() {
            return getLineType().getSymbol();
        }

        @Override
        protected void formatData(final TethysDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* No data */
        }
    }

    /**
     * The Category Tax line.
     */
    public static class QIFCategoryTaxLine
            extends QIFLine<QCategoryLineType> {
        /**
         * Constructor.
         */
        protected QIFCategoryTaxLine() {
        }

        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.TAX;
        }

        @Override
        public String toString() {
            return getLineType().getSymbol();
        }

        @Override
        protected void formatData(final TethysDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* No data */
        }
    }
}
