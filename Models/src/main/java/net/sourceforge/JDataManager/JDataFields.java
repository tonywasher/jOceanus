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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Data Fields.
 * @author Tony Washer
 */
public class JDataFields {
    /**
     * Self reference.
     */
    private final JDataFields theSelf = this;

    /**
     * Name of Item.
     */
    private final String theName;

    /**
     * Next value.
     */
    private int theNextValue;

    /**
     * List of fields.
     */
    private final List<JDataField> theFields;

    /**
     * Underlying fields.
     */
    private final JDataFields theUnderlying;

    /**
     * Constructor.
     * @param pName the name of the item
     */
    public JDataFields(final String pName) {
        /* Initialise the list */
        theName = pName;
        theUnderlying = null;
        theNextValue = 0;
        theFields = new ArrayList<JDataField>();
    }

    /**
     * Constructor.
     * @param pUnderlying the underlying fields
     */
    public JDataFields(final JDataFields pUnderlying) {
        this(pUnderlying.getName(), pUnderlying);
    }

    /**
     * Constructor.
     * @param pName the name of the item
     * @param pUnderlying the underlying fields
     */
    public JDataFields(final String pName,
                       final JDataFields pUnderlying) {
        /* Initialise the list */
        theName = pName;
        theUnderlying = pUnderlying;
        theNextValue = (theUnderlying == null) ? 0 : theUnderlying.getNumValues();
        theFields = new ArrayList<JDataField>();
    }

    /**
     * Obtain the name of the item.
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
     * Obtain the number of values.
     * @return the number of values
     */
    public int getNumValues() {
        return theNextValue;
    }

    /**
     * Obtain an iterator for the fields.
     * @return the field iterator
     */
    public Iterator<JDataField> fieldIterator() {
        return new FieldIterator();
    }

    /**
     * Declare field used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public JDataField declareEqualityField(final String pName) {
        return declareDataField(pName, true, false);
    }

    /**
     * Declare local field not used for equality.
     * @param pName the name of the field
     * @return the field
     */
    public JDataField declareLocalField(final String pName) {
        return declareDataField(pName, false, false);
    }

    /**
     * Declare valueSet field used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public JDataField declareEqualityValueField(final String pName) {
        return declareDataField(pName, true, true);
    }

    /**
     * Declare valueSet field not used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public JDataField declareDerivedValueField(final String pName) {
        return declareDataField(pName, false, true);
    }

    /**
     * Declare field.
     * @param pName the name of the field
     * @param isEqualityField is the field used in equality test
     * @param isValueSetField is the field held in a ValueSet
     * @return the field
     */
    private JDataField declareDataField(final String pName,
                                        final boolean isEqualityField,
                                        final boolean isValueSetField) {
        /* Create the field */
        JDataField myField = new JDataField(pName, isEqualityField, isValueSetField);

        /* Add it to the list */
        theFields.add(myField);

        /* Return the index */
        return myField;
    }

    /**
     * Iterator class.
     */
    private final class FieldIterator implements Iterator<JDataField> {
        /**
         * Preceding iterator.
         */
        private final Iterator<JDataField> thePreceding;

        /**
         * Local iterator.
         */
        private final Iterator<JDataField> theIterator;

        /**
         * Constructor.
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
            if ((thePreceding != null) && (thePreceding.hasNext())) {
                return true;
            }

            /* Handle call here */
            return theIterator.hasNext();
        }

        @Override
        public JDataField next() {
            /* Check for preceding entry */
            if ((thePreceding != null) && (thePreceding.hasNext())) {
                return thePreceding.next();
            }

            /* Handle call here */
            return theIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Individual fields.
     */
    public class JDataField {
        /**
         * Index of value.
         */
        private final int theIndex;

        /**
         * Name of field.
         */
        private final String theName;

        /**
         * Is the field used in equality test.
         */
        private final boolean isEqualityField;

        /**
         * Is the field held in a valueSet.
         */
        private final boolean isValueSetField;

        /**
         * Get the index.
         * @return the index
         */
        public int getIndex() {
            return theIndex;
        }

        /**
         * Get the name.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Is this an equality field.
         * @return true/false
         */
        public boolean isEqualityField() {
            return isEqualityField;
        }

        /**
         * Is this a valueSet field.
         * @return true/false
         */
        public boolean isValueSetField() {
            return isValueSetField;
        }

        /**
         * Obtain the anchor for the field.
         * @return the anchor
         */
        public JDataFields getAnchor() {
            return theSelf;
        }

        /**
         * Constructor.
         * @param pName the name of the field
         * @param isEquality is the field used in equality test
         * @param isValueSet is the field held in a ValueSet
         */
        public JDataField(final String pName,
                          final boolean isEquality,
                          final boolean isValueSet) {
            /* Store parameters */
            theName = pName;
            isEqualityField = isEquality;
            isValueSetField = isValueSet;

            /* Allocate value index if required */
            theIndex = isValueSetField ? theNextValue++ : -1;
        }
    }
}