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
import net.sourceforge.jOceanus.jMoneyWise.data.TransactionType;

/**
 * Quicken Category.
 */
public final class QCategory
        extends QElement {
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
     * @param pFormatter the data formatter
     * @param pCategory the category
     */
    private QCategory(final JDataFormatter pFormatter,
                      final EventCategory pCategory) {
        /* Call super constructor */
        super(pFormatter);

        /* Store the category */
        theCategory = pCategory;
    }

    /**
     * build QIF format.
     * @return the QIF format
     */
    protected String buildQIF() {
        /* Reset the builder */
        reset();

        /* Add the Category name */
        addCategoryLine(QCatLineType.Name, theCategory);

        /* If we have a description */
        String myDesc = theCategory.getDesc();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QCatLineType.Description, myDesc);
        }

        /* Determine Income/Expense flag */
        TransactionType myTranType = TransactionType.deriveType(theCategory);
        addFlag((myTranType.isIncome())
                ? QCatLineType.Income
                : QCatLineType.Expense);

        /* Return the result */
        return completeItem();
    }

    @Override
    public String toString() {
        return buildQIF();
    }

    /**
     * Category List class.
     */
    protected static class QCategoryList
            extends QElement {
        /**
         * Parent Category Map.
         */
        private final HashMap<EventCategory, QCategory> theParents;

        /**
         * Security Map.
         */
        private final HashMap<EventCategory, QCategory> theCategories;

        /**
         * Obtain category list size.
         * @return the size
         */
        protected int size() {
            return theParents.size()
                   + theCategories.size();
        }

        /**
         * Constructor.
         * @param pAnalysis the analysis
         */
        protected QCategoryList(final QAnalysis pAnalysis) {
            /* Call super constructor */
            super(pAnalysis.getFormatter());

            /* Create the map */
            theParents = new HashMap<EventCategory, QCategory>();
            theCategories = new HashMap<EventCategory, QCategory>();
        }

        /**
         * Register category.
         * @param pCategory the category
         */
        protected void registerCategory(final EventCategory pCategory) {
            /* Look up the category in the map */
            QCategory myCategory = theCategories.get(pCategory);

            /* If this is a new category */
            if (myCategory == null) {
                /* Allocate the category and add to the map */
                myCategory = new QCategory(getFormatter(), pCategory);
                theCategories.put(pCategory, myCategory);

                /* Access the parent category */
                EventCategory myParent = pCategory.getParentCategory();

                /* If the parent is unknown */
                myCategory = theParents.get(myParent);
                if (myCategory == null) {
                    /* Allocate the category and add to the map */
                    myCategory = new QCategory(getFormatter(), myParent);
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
            reset();
            append(QIF_ITEMTYPE);
            append(QIF_ITEM);
            endLine();

            /* Write Category header */
            pStream.write(getBufferedString());

            /* Loop through the parents */
            Iterator<QCategory> myIterator = theParents.values().iterator();
            while ((bContinue)
                   && (myIterator.hasNext())) {
                QCategory myCategory = myIterator.next();

                /* Write Category details */
                pStream.write(myCategory.buildQIF());

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
                pStream.write(myCategory.buildQIF());

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
    public enum QCatLineType implements QLineType {
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

        @Override
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
