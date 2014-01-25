/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.EventClass;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QCategoryLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QClassLineType;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;

/**
 * Quicken Class.
 */
public final class QClass
        extends QElement {
    /**
     * Item type.
     */
    private static final String QIF_ITEM = "Class";

    /**
     * The class.
     */
    private final EventClass theClass;

    /**
     * Obtain the class.
     * @return the class
     */
    public EventClass getEventClass() {
        return theClass;
    }

    /**
     * Obtain the name of the class.
     * @return the name
     */
    public String getName() {
        return theClass.getName();
    }

    /**
     * Obtain the description of the class.
     * @return the description
     */
    public String getDesc() {
        return theClass.getDesc();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pClass the class
     */
    protected QClass(final QAnalysis pAnalysis,
                     final EventClass pClass) {
        /* Call super constructor */
        super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

        /* Store the class */
        theClass = pClass;
    }

    /**
     * build QIF format.
     * @return the QIF format
     */
    protected String buildQIF() {
        /* Reset the builder */
        reset();

        /* Add the Class name */
        addStringLine(QClassLineType.NAME, theClass.getName());

        /* If we have a description */
        String myDesc = theClass.getDesc();
        if (myDesc != null) {
            /* Add the Description */
            addStringLine(QCategoryLineType.DESCRIPTION, myDesc);
        }

        /* Return the result */
        return completeItem();
    }

    @Override
    public String toString() {
        return buildQIF();
    }

    /**
     * Class List class.
     */
    public static class QClassList
            extends QElement {
        /**
         * Class Map.
         */
        private final Map<EventClass, QClass> theClasses;

        /**
         * The analysis.
         */
        private final QAnalysis theAnalysis;

        /**
         * Obtain category list size.
         * @return the size
         */
        protected int size() {
            return theClasses.size();
        }

        /**
         * Constructor.
         * @param pAnalysis the analysis
         */
        protected QClassList(final QAnalysis pAnalysis) {
            /* Call super constructor */
            super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

            /* Store parameters */
            theAnalysis = pAnalysis;

            /* Create the map */
            theClasses = new LinkedHashMap<EventClass, QClass>();
        }

        /**
         * Register class.
         * @param pClass the class
         */
        protected void registerClass(final EventClass pClass) {
            /* Look up the class in the map */
            QClass myClass = theClasses.get(pClass);

            /* If this is a new class */
            if (myClass == null) {
                /* Allocate the class and add to the map */
                myClass = new QClass(theAnalysis, pClass);
                theClasses.put(pClass, myClass);
            }
        }

        /**
         * Output classes.
         * @param pStatus the thread status
         * @param pStream the output stream
         * @return Continue? true/false
         * @throws IOException on error
         */
        protected boolean outputClasses(final ThreadStatus<MoneyWiseData, MoneyWiseDataType> pStatus,
                                        final OutputStreamWriter pStream) throws IOException {
            /* If we have no classes */
            if (theClasses.isEmpty()) {
                return true;
            }

            /* Access the number of reporting steps */
            int mySteps = pStatus.getReportingSteps();
            int myCount = 0;

            /* Update status bar */
            boolean bContinue = pStatus.setNewStage("Writing classes") && pStatus.setNumSteps(size());

            /* Add the Item type */
            reset();
            append(QIF_ITEMTYPE);
            append(QIF_ITEM);
            endLine();

            /* Write Class header */
            pStream.write(getBufferedString());

            /* Loop through the classes */
            Iterator<QClass> myIterator = theClasses.values().iterator();
            while ((bContinue) && (myIterator.hasNext())) {
                QClass myClass = myIterator.next();

                /* Write Class details */
                pStream.write(myClass.buildQIF());

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pStatus.setStepsDone(myCount))) {
                    bContinue = false;
                }
            }

            /* Return success */
            return bContinue;
        }

        /**
         * Obtain class iterator.
         * @return the iterator
         */
        protected Iterator<QClass> classIterator() {
            return theClasses.values().iterator();
        }
    }
}
