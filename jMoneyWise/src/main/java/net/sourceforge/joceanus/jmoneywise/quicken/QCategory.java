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
package net.sourceforge.joceanus.jmoneywise.quicken;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jdatamodels.threads.ThreadStatus;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.TransactionType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QCategoryLineType;

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
     * The transaction type.
     */
    private final TransactionType theType;

    /**
     * Obtain the category.
     * @return the name
     */
    public EventCategory getCategory() {
        return theCategory;
    }

    /**
     * Obtain the name of the category.
     * @return the name
     */
    public String getName() {
        return theCategory.getName();
    }

    /**
     * Obtain the description of the category.
     * @return the description
     */
    public String getDesc() {
        return theCategory.getDesc();
    }

    /**
     * Obtain the type of the category.
     * @return the description
     */
    public TransactionType getType() {
        return theType;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCategory the category
     */
    protected QCategory(final QAnalysis pAnalysis,
                        final EventCategory pCategory) {
        /* Call super constructor */
        super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

        /* Store the category and type */
        theCategory = pCategory;
        theType = TransactionType.deriveType(theCategory);
    }

    /**
     * build QIF format.
     * @return the QIF format
     */
    protected String buildQIF() {
        /* Reset the builder */
        reset();

        /* Add the Category name */
        addCategoryLine(QCategoryLineType.Name, theCategory);

        /* If we have a description */
        String myDesc = theCategory.getDesc();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QCategoryLineType.Description, myDesc);
        }

        /* Determine Income/Expense flag */
        addFlag((theType.isIncome())
                ? QCategoryLineType.Income
                : QCategoryLineType.Expense);

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
    public static class QCategoryList
            extends QElement {
        /**
         * Parent Category Map.
         */
        private final Map<EventCategory, QCategory> theParents;

        /**
         * Security Map.
         */
        private final Map<EventCategory, QCategory> theCategories;

        /**
         * The analysis.
         */
        private final QAnalysis theAnalysis;

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
            super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

            /* Store parameters */
            theAnalysis = pAnalysis;

            /* Create the map */
            theParents = new LinkedHashMap<EventCategory, QCategory>();
            theCategories = new LinkedHashMap<EventCategory, QCategory>();
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
                myCategory = new QCategory(theAnalysis, pCategory);
                theCategories.put(pCategory, myCategory);

                /* Access the parent category */
                EventCategory myParent = pCategory.getParentCategory();

                /* If the parent is unknown */
                myCategory = theParents.get(myParent);
                if (myCategory == null) {
                    /* Allocate the category and add to the map */
                    myCategory = new QCategory(theAnalysis, myParent);
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

        /**
         * Obtain category iterator.
         * @return the iterator
         */
        protected Iterator<QCategory> categoryIterator() {
            return new CategoryIterator();
        }

        /**
         * Category Iterator class.
         */
        private final class CategoryIterator
                implements Iterator<QCategory> {
            /**
             * Parents iterator.
             */
            private final Iterator<QCategory> theParentIterator;

            /**
             * Category iterator.
             */
            private final Iterator<QCategory> theCategoryIterator;

            /**
             * Use parents.
             */
            private boolean useParents = true;

            /**
             * Constructor.
             */
            private CategoryIterator() {
                /* Allocate iterators */
                theParentIterator = theParents.values().iterator();
                theCategoryIterator = theCategories.values().iterator();
            }

            @Override
            public boolean hasNext() {
                /* If we are looking for parents */
                if (useParents) {
                    /* Check parents */
                    if (theParentIterator.hasNext()) {
                        return true;
                    }

                    /* Note parents are finished */
                    useParents = false;
                }

                /* Handle call here */
                return theCategoryIterator.hasNext();
            }

            @Override
            public QCategory next() {
                /* Check for parents entry */
                return (useParents)
                        ? theParentIterator.next()
                        : theCategoryIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
