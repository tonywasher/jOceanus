/*******************************************************************************
 * JDataManager: Java Data Manager
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataManager;

import net.sourceforge.JDataManager.JDataFields.JDataField;

/**
 * Data object formatting and interfaces.
 * @author Tony Washer
 */
public final class JDataObject {
    /**
     * Format object interface.
     */
    public interface JDataFormat {
        /**
         * Obtain Object summary.
         * @return the display summary of the object
         */
        String formatObject();
    }

    /**
     * Detail object interface.
     */
    public interface JDataContents extends JDataFormat {
        /**
         * Obtain the Report Fields.
         * @return the report fields
         */
        JDataFields getDataFields();

        /**
         * Obtain Field value.
         * @param pField the field
         * @return the value of the field
         */
        Object getFieldValue(final JDataField pField);
    }

    /**
     * ValueSet object interface.
     */
    public interface JDataValues extends JDataContents {
        /**
         * Obtain Object ValueSet.
         * @return the ValueSet of the object
         */
        ValueSet getValueSet();

        /**
         * Declare the valueSet as active.
         * @param pValues the active values
         */
        void declareValues(final ValueSet pValues);
    }

    /**
     * Difference interface.
     */
    public interface JDataDiffers {
        /**
         * Test for difference with another object.
         * @param pThat the other object
         * @return the difference
         */
        Difference differs(final Object pThat);
    }

    /**
     * Special values for return by getFieldValue.
     */
    public enum JDataFieldValue {
        /**
         * Field not known.
         */
        UnknownField,

        /**
         * Field to be skipped.
         */
        SkipField;
    }

    /**
     * Difference class.
     */
    public static class JDataDifference {
        /**
         * The object itself.
         */
        private final Object theObject;

        /**
         * The difference.
         */
        private final Difference theDifference;

        /**
         * Obtain the object.
         * @return the object
         */
        public Object getObject() {
            return theObject;
        }

        /**
         * Obtain the difference.
         * @return the difference
         */
        public Difference getDifference() {
            return theDifference;
        }

        /**
         * Constructor.
         * @param pObject the object
         * @param pDifference the difference
         */
        public JDataDifference(final Object pObject,
                               final Difference pDifference) {
            theObject = pObject;
            theDifference = pDifference;
        }
    }
}
