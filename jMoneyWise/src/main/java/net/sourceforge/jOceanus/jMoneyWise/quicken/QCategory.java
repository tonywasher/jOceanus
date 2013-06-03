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
package net.sourceforge.jOceanus.jMoneyWise.quicken;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataModels.threads.ThreadStatus;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * Quicken Category.
 */
public class QCategory {
    /**
     * Item type.
     */
    private static final String QIF_ITEM = "Cat";

    /**
     * The event category.
     */
    private final EventCategory theCategory;

    /**
     * Constructor.
     * @param pCategory the category
     */
    private QCategory(final EventCategory pCategory) {
        /* Store the category */
        theCategory = pCategory;
    }

    /**
     * build QIF format.
     * @param pFormatter the formatter
     */
    protected String buildQIF(final JDataFormatter pFormatter) {
        StringBuilder myBuilder = new StringBuilder();

        /* Add the Category name */
        myBuilder.append(QCatLineType.Name.getSymbol());
        myBuilder.append(theCategory.getName());
        myBuilder.append(QDataSet.QIF_EOL);

        /* If we have a description */
        String myDesc = theCategory.getDesc();
        if (myDesc != null) {
            /* Add the Description */
            myBuilder.append(QCatLineType.Description.getSymbol());
            myBuilder.append(myDesc);
            myBuilder.append(QDataSet.QIF_EOL);
        }

        /* Determine Income/Expense flag */
        switch (theCategory.getCategoryTypeClass()) {
            case TaxedIncome:
            case RentalIncome:
            case OtherIncome:
            case Interest:
            case Dividend:
            case Inherited:
                myBuilder.append(QCatLineType.Income.getSymbol());
                myBuilder.append(QDataSet.QIF_EOL);
                break;
            default:
                myBuilder.append(QCatLineType.Expense.getSymbol());
                myBuilder.append(QDataSet.QIF_EOL);
                break;
        }

        /* Add the End indicator */
        myBuilder.append(QDataSet.QIF_EOI);
        myBuilder.append(QDataSet.QIF_EOL);

        /* Return the builder */
        return myBuilder.toString();
    }

    /**
     * Category List class.
     */
    protected static class QCategoryList {
        /**
         * Parent Category Map.
         */
        private final HashMap<EventCategory, QCategory> theParents;

        /**
         * Security Map.
         */
        private final HashMap<EventCategory, QCategory> theCategories;

        /**
         * Data Formatter.
         */
        private final JDataFormatter theFormatter;

        /**
         * Obtain category list size
         * @return the size
         */
        protected int size() {
            return theParents.size()
                   + theCategories.size();
        }

        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected QCategoryList(final JDataFormatter pFormatter) {
            /* Create the map */
            theParents = new HashMap<EventCategory, QCategory>();
            theCategories = new HashMap<EventCategory, QCategory>();
            theFormatter = pFormatter;
        }

        /**
         * Register category
         * @param pCategory the category
         */
        protected void registerCategory(final EventCategory pCategory) {
            /* Look up the category in the map */
            QCategory myCategory = theCategories.get(pCategory);

            /* If this is a new category */
            if (myCategory == null) {
                /* Allocate the category and add to the map */
                myCategory = new QCategory(pCategory);
                theCategories.put(pCategory, myCategory);

                /* Access the parent category */
                EventCategory myParent = pCategory.getParentCategory();

                /* If the parent is unknown */
                myCategory = theParents.get(myParent);
                if (myCategory == null) {
                    /* Allocate the category and add to the map */
                    myCategory = new QCategory(myParent);
                    theParents.put(myParent, myCategory);
                }
            }
        }

        /**
         * Output categories.
         * @param pStatus the thread status
         * @param pStream the output stream
         * @return Continue? true/false
         * @throws IOException on error
         */
        protected boolean outputCategories(final ThreadStatus<FinanceData> pStatus,
                                           final OutputStreamWriter pStream) throws IOException {
            StringBuilder myBuilder = new StringBuilder();

            /* If we have no categories */
            if ((theParents.size() == 0)
                && (theCategories.size() == 0)) {
                return true;
            }

            /* Access the number of reporting steps */
            int mySteps = pStatus.getReportingSteps();
            int myCount = 0;

            /* Update status bar */
            boolean bContinue = ((pStatus.setNewStage("Writing categories")) && (pStatus.setNumSteps(size())));

            /* Add the Item type */
            myBuilder.append(QDataSet.QIF_ITEMTYPE);
            myBuilder.append(QIF_ITEM);
            myBuilder.append(QDataSet.QIF_EOL);

            /* Write Category header */
            pStream.write(myBuilder.toString());

            /* Loop through the parents */
            Iterator<QCategory> myIterator = theParents.values().iterator();
            while ((bContinue)
                   && (myIterator.hasNext())) {
                QCategory myCategory = myIterator.next();

                /* Write Category details */
                pStream.write(myCategory.buildQIF(theFormatter));

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
                    bContinue = false;
                }
            }

            /* Loop through the categories */
            myIterator = theCategories.values().iterator();
            while ((bContinue)
                   && (myIterator.hasNext())) {
                QCategory myCategory = myIterator.next();

                /* Write Category details */
                pStream.write(myCategory.buildQIF(theFormatter));

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pStatus.setStepsDone(myCount))) {
                    bContinue = false;
                }
            }

            /* Return success */
            return bContinue;
        }
    }

    /**
     * Quicken Category Line Types.
     */
    public enum QCatLineType {
        /**
         * Name.
         */
        Name("N"),

        /**
         * Description.
         */
        Description("D"),

        /**
         * Income flag.
         */
        Income("I"),

        /**
         * Expense flag.
         */
        Expense("E"),

        /**
         * Tax flag.
         */
        Tax("T");

        /**
         * The symbol.
         */
        private final String theSymbol;

        /**
         * Obtain the symbol.
         * @return the symbol
         */
        public String getSymbol() {
            return theSymbol;
        }

        /**
         * Constructor.
         * @param pSymbol the symbol
         */
        private QCatLineType(final String pSymbol) {
            /* Store symbol */
            theSymbol = pSymbol;
        }
    }
}
