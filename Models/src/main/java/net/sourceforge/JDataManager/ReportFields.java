/*******************************************************************************
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReportFields {
    /**
     * Self reference
     */
    private final ReportFields theSelf = this;

    /**
     * Name of Item
     */
    private final String theName;

    /**
     * Next value
     */
    private int theNextValue;

    /**
     * List of fields
     */
    private final List<ReportField> theFields;

    /**
     * Underlying fields
     */
    private final ReportFields theUnderlying;

    /**
     * Constructor
     * @param pName the name of the item
     */
    public ReportFields(String pName) {
        /* Initialise the list */
        theName = pName;
        theUnderlying = null;
        theNextValue = 0;
        theFields = new ArrayList<ReportField>();
    }

    /**
     * Constructor
     * @param pUnderlying the underlying fields
     */
    public ReportFields(ReportFields pUnderlying) {
        this(pUnderlying.getName(), pUnderlying);
    }

    /**
     * Constructor
     * @param pName the name of the item
     * @param pUnderlying the underlying fields
     */
    public ReportFields(String pName,
                        ReportFields pUnderlying) {
        /* Initialise the list */
        theName = pName;
        theUnderlying = pUnderlying;
        theNextValue = (theUnderlying == null) ? 0 : theUnderlying.getNumValues();
        theFields = new ArrayList<ReportField>();
    }

    /**
     * Obtain the name of the item
     * @return the name
     */
    public String getName() {
        return theName;
    }

    @Override
    public int hashCode() {
        return theName.hashCode();
    }

    /**
     * Obtain the number of values
     * @return the number of values
     */
    public int getNumValues() {
        return theNextValue;
    }

    /**
     * Obtain an iterator for the fields
     * @return the field iterator
     */
    public Iterator<ReportField> fieldIterator() {
        return new FieldIterator();
    }

    /**
     * Declare field used for equality test
     * @param pName the name of the field
     * @return the field
     */
    public ReportField declareEqualityField(String pName) {
        return declareReportField(pName, true, false);
    }

    /**
     * Declare local field not used for equality
     * @param pName the name of the field
     * @return the field
     */
    public ReportField declareLocalField(String pName) {
        return declareReportField(pName, false, false);
    }

    /**
     * Declare valueSet field used for equality test
     * @param pName the name of the field
     * @return the field
     */
    public ReportField declareEqualityValueField(String pName) {
        return declareReportField(pName, true, true);
    }

    /**
     * Declare valueSet field not used for equality test
     * @param pName the name of the field
     * @return the field
     */
    public ReportField declareDerivedValueField(String pName) {
        return declareReportField(pName, false, true);
    }

    /**
     * Declare field
     * @param pName the name of the field
     * @param isEqualityField is the field used in equality test
     * @param isValueSetField is the field held in a ValueSet
     * @return the field
     */
    private ReportField declareReportField(String pName,
                                           boolean isEqualityField,
                                           boolean isValueSetField) {
        /* Create the field */
        ReportField myField = new ReportField(pName, isEqualityField, isValueSetField);

        /* Add it to the list */
        theFields.add(myField);

        /* Return the index */
        return myField;
    }

    /**
     * Iterator class
     */
    private class FieldIterator implements Iterator<ReportField> {
        /**
         * Preceding iterator
         */
        private final Iterator<ReportField> thePreceding;

        /**
         * Local iterator
         */
        private final Iterator<ReportField> theIterator;

        /**
         * Constructor
         */
        private FieldIterator() {
            /* Allocate iterator */
            theIterator = theFields.iterator();

            /* Allocate preceding iterator */
            thePreceding = (theUnderlying == null) ? null : theUnderlying.fieldIterator();
        }

        @Override
        public boolean hasNext() {
            /* Check for preceding entry */
            if ((thePreceding != null) && (thePreceding.hasNext()))
                return true;

            /* Handle call here */
            return theIterator.hasNext();
        }

        @Override
        public ReportField next() {
            /* Check for preceding entry */
            if ((thePreceding != null) && (thePreceding.hasNext()))
                return thePreceding.next();

            /* Handle call here */
            return theIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Individual fields
     */
    public class ReportField {
        /**
         * Index of value
         */
        private final int theIndex;

        /**
         * Name of field
         */
        private final String theName;

        /**
         * Is the field used in equality test
         */
        private final boolean isEqualityField;

        /**
         * Is the field held in a valueSet
         */
        private final boolean isValueSetField;

        /* Access methods */
        public int getIndex() {
            return theIndex;
        }

        public String getName() {
            return theName;
        }

        public boolean isEqualityField() {
            return isEqualityField;
        }

        public boolean isValueSetField() {
            return isValueSetField;
        }

        /**
         * Obtain the anchor for the field
         * 
         * @return the anchor
         */
        public ReportFields getAnchor() {
            return theSelf;
        }

        /**
         * Constructor
         * 
         * @param pName the name of the field
         * @param isEquality is the field used in equality test
         * @param isValueSet is the field held in a ValueSet
         */
        public ReportField(String pName,
                           boolean isEquality,
                           boolean isValueSet) {
            /* Store parameters */
            theName = pName;
            isEqualityField = isEquality;
            isValueSetField = isValueSet;

            /* Allocate value index if required */
            theIndex = isValueSetField ? theNextValue++ : -1;
        }
    }
}
