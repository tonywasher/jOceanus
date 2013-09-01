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
package net.sourceforge.jOceanus.jMoneyWise.quicken.file;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jMoneyWise.data.TransactionType;
import net.sourceforge.jOceanus.jMoneyWise.quicken.QCategory;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFStringLine;

/**
 * Class representing a QIF Category record.
 */
public class QIFEventCategory
        extends QIFRecord<QCategoryLineType> {
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
    private final TransactionType theType;

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
        return theType.isIncome();
    }

    /**
     * Is the Category an expense category.
     * @return true/false
     */
    public boolean isExpense() {
        return theType.isExpense();
    }

    /**
     * Constructor.
     * @param pCategory the Event Category
     */
    public QIFEventCategory(final QCategory pCategory) {
        /* Call super-constructor */
        super(QCategoryLineType.class);

        /* Store data */
        theName = pCategory.getName();
        theDesc = pCategory.getDesc();
        theType = pCategory.getType();

        /* Build lines */
        addLine(new QIFCategoryNameLine(theName));
        if (theDesc != null) {
            addLine(new QIFCategoryDescLine(theDesc));
        }
        if (theType.isIncome()) {
            addLine(new QIFCategoryIncomeLine());
        } else {
            addLine(new QIFCategoryExpenseLine());
        }
    }

    /**
     * Constructor.
     * @param pLines the data lines
     */
    protected QIFEventCategory(final List<String> pLines) {
        /* Call super-constructor */
        super(QCategoryLineType.class);

        /* Determine details */
        String myName = null;
        String myDesc = null;
        TransactionType myTType = null;

        /* Loop through the lines */
        Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            String myLine = myIterator.next();

            /* Determine the category */
            QCategoryLineType myType = QCategoryLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case Name:
                        addLine(new QIFCategoryNameLine(myData));
                        myName = myData;
                        break;
                    case Description:
                        addLine(new QIFCategoryDescLine(myData));
                        myDesc = myData;
                        break;
                    case Income:
                        addLine(new QIFCategoryIncomeLine());
                        myTType = TransactionType.Income;
                        break;
                    case Expense:
                        addLine(new QIFCategoryExpenseLine());
                        myTType = TransactionType.Expense;
                        break;
                    case Tax:
                    default:
                }
            }
        }

        /* Build details */
        theName = myName;
        theDesc = myDesc;
        theType = myTType;
    }

    /**
     * The Category Name line.
     */
    public class QIFCategoryNameLine
            extends QIFStringLine<QCategoryLineType> {
        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.Name;
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
        protected QIFCategoryNameLine(final String pName) {
            /* Call super-constructor */
            super(pName);
        }
    }

    /**
     * The Category Description line.
     */
    public class QIFCategoryDescLine
            extends QIFStringLine<QCategoryLineType> {
        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.Description;
        }

        /**
         * Obtain description.
         * @return the description
         */
        public String getDescription() {
            return getValue();
        }

        /**
         * Constructor.
         * @param pDesc the Description
         */
        protected QIFCategoryDescLine(final String pDesc) {
            /* Call super-constructor */
            super(pDesc);
        }
    }

    /**
     * The Category Income line.
     */
    public class QIFCategoryIncomeLine
            extends QIFLine<QCategoryLineType> {
        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.Income;
        }

        /**
         * Constructor.
         */
        protected QIFCategoryIncomeLine() {
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
        }
    }

    /**
     * The Category Expense line.
     */
    public class QIFCategoryExpenseLine
            extends QIFLine<QCategoryLineType> {
        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.Expense;
        }

        /**
         * Constructor.
         */
        protected QIFCategoryExpenseLine() {
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
        }
    }

    /**
     * The Category Tax line.
     */
    public class QIFCategoryTaxLine
            extends QIFLine<QCategoryLineType> {
        @Override
        public QCategoryLineType getLineType() {
            return QCategoryLineType.Income;
        }

        /**
         * Constructor.
         */
        protected QIFCategoryTaxLine() {
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
        }
    }
}
