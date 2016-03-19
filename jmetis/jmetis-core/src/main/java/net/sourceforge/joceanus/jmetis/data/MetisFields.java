/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Data Fields.
 * @author Tony Washer
 */
public final class MetisFields {
    /**
     * Hash Prime.
     */
    public static final int HASH_PRIME = 19;

    /**
     * The Next anchorId.
     */
    private static AtomicInteger theNextAnchorId = new AtomicInteger(1);

    /**
     * Id of this anchor.
     */
    private final Integer theAnchorId;

    /**
     * Name of Item.
     */
    private final String theName;

    /**
     * Next value.
     */
    private Integer theNextValue;

    /**
     * List of fields.
     */
    private final List<MetisField> theFields;

    /**
     * Parent fields.
     */
    private final MetisFields theParent;

    /**
     * Constructor.
     * @param pName the name of the item
     */
    public MetisFields(final String pName) {
        this(pName, null);
    }

    /**
     * Constructor.
     * @param pParent the parent fields
     */
    public MetisFields(final MetisFields pParent) {
        this(pParent.getName(), pParent);
    }

    /**
     * Constructor.
     * @param pName the name of the item
     * @param pParent the parent fields
     */
    public MetisFields(final String pName,
                       final MetisFields pParent) {
        /* Initialise the list */
        theName = pName;
        theParent = pParent;
        theNextValue = (theParent == null)
                                           ? 0
                                           : theParent.getNumValues();
        theFields = new ArrayList<>();

        /* Store the anchorId */
        theAnchorId = theNextAnchorId.getAndIncrement();
    }

    /**
     * Obtain next value index.
     * @return the next index
     */
    private Integer getNextValue() {
        /* return the new anchor id */
        Integer myValue = theNextValue;
        theNextValue = theNextValue + 1;
        return myValue;
    }

    /**
     * Obtain the anchorId.
     * @return the id
     */
    public Integer getAnchorId() {
        return theAnchorId;
    }

    /**
     * Obtain the name of the item.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the number of values.
     * @return the number of values
     */
    public Integer getNumValues() {
        return theNextValue;
    }

    /**
     * Obtain an iterator for the fields.
     * @return the field iterator
     */
    public Iterator<MetisField> fieldIterator() {
        return new FieldIterator(this);
    }

    /**
     * Declare field used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareEqualityField(final String pName) {
        return declareDataField(pName, true, false);
    }

    /**
     * Declare local field not used for equality.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareLocalField(final String pName) {
        return declareDataField(pName, false, false);
    }

    /**
     * Declare valueSet field used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareEqualityValueField(final String pName) {
        return declareDataField(pName, true, true);
    }

    /**
     * Declare valueSet field not used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareDerivedValueField(final String pName) {
        return declareDataField(pName, false, true);
    }

    /**
     * Declare local field referenced by index.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareIndexField(final String pName) {
        return declareDataField(pName);
    }

    /**
     * Declare field.
     * @param pName the name of the field
     * @param isEqualityField is the field used in equality test
     * @param isValueSetField is the field held in a ValueSet
     * @return the field
     */

    private synchronized MetisField declareDataField(final String pName,
                                                     final boolean isEqualityField,
                                                     final boolean isValueSetField) {
        /* Check the name */
        checkUniqueName(pName);

        /* Create the field */
        MetisField myField = new MetisField(this, pName, isEqualityField, isValueSetField);

        /* Add it to the list */
        theFields.add(myField);

        /* Return the index */
        return myField;
    }

    /**
     * Declare index-only field.
     * @param pName the name of the field
     * @return the field
     */
    private MetisField declareDataField(final String pName) {
        /* Create the field */
        MetisField myField = new MetisField(this, pName);

        /* Add it to the list */
        theFields.add(myField);

        /* Return the index */
        return myField;
    }

    /**
     * Check unique name.
     * @param pName the name to check.
     * @throws IllegalArgumentException if name is present
     */
    private void checkUniqueName(final String pName) {
        Iterator<MetisField> myIterator = fieldIterator();
        while (myIterator.hasNext()) {
            MetisField myField = myIterator.next();

            /* If the name exists, throw an exception */
            if (pName.equals(myField.getName())) {
                throw new IllegalArgumentException("Duplicate field name: " + pName);
            }
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!(pThat instanceof MetisFields)) {
            return false;
        }

        /* Access as MetisFields */
        MetisFields myThat = (MetisFields) pThat;

        /* Must have same anchor id */
        return theAnchorId == myThat.getAnchorId();
    }

    @Override
    public int hashCode() {
        return theAnchorId;
    }

    /**
     * Iterator class.
     */
    private static final class FieldIterator
            implements Iterator<MetisField> {
        /**
         * Preceding iterator.
         */
        private final Iterator<MetisField> thePreceding;

        /**
         * Local iterator.
         */
        private final Iterator<MetisField> theIterator;

        /**
         * Constructor.
         * @param pFields the fields
         */
        private FieldIterator(final MetisFields pFields) {
            /* Allocate iterator */
            theIterator = pFields.theFields.iterator();

            /* Allocate preceding iterator */
            MetisFields myParent = pFields.theParent;
            thePreceding = (myParent == null)
                                              ? null
                                              : myParent.fieldIterator();
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
        public MetisField next() {
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

        @Override
        public void forEachRemaining(final Consumer<? super MetisField> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }
    }

    /**
     * Individual fields.
     */
    public static final class MetisField {
        /**
         * Anchor.
         */
        private final MetisFields theAnchor;

        /**
         * Index of value.
         */
        private final Integer theIndex;

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
         * Constructor.
         * @param pAnchor the anchor
         * @param pName the name of the field
         * @param isEquality is the field used in equality test
         * @param isValueSet is the field held in a ValueSet
         */
        public MetisField(final MetisFields pAnchor,
                          final String pName,
                          final boolean isEquality,
                          final boolean isValueSet) {
            /* Store parameters */
            theAnchor = pAnchor;
            theName = pName;
            isEqualityField = isEquality;
            isValueSetField = isValueSet;

            /* Allocate value index if required */
            theIndex = isValueSetField
                                       ? theAnchor.getNextValue()
                                       : -1;
        }

        /**
         * Constructor.
         * @param pAnchor the anchor
         * @param pName the name of the field
         */
        public MetisField(final MetisFields pAnchor,
                          final String pName) {
            /* Store parameters */
            theAnchor = pAnchor;
            theName = pName;
            isEqualityField = false;
            isValueSetField = false;

            /* Allocate value index */
            theIndex = theAnchor.getNextValue();
        }

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
        public MetisFields getAnchor() {
            return theAnchor;
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial cases */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Check class */
            if (!(pThat instanceof MetisField)) {
                return false;
            }

            /* Access as MetisField */
            MetisField myThat = (MetisField) pThat;

            /* Must belong to the same anchor */
            if (!theAnchor.equals(myThat.getAnchor())) {
                return false;
            }

            /* Check the name and index is the same */
            return (theIndex == myThat.theIndex)
                   && theName.equals(myThat.theName);
        }

        @Override
        public int hashCode() {
            return theAnchor.hashCode() * HASH_PRIME
                   + theName.hashCode();
        }

        @Override
        public String toString() {
            return theName;
        }
    }

    /**
     * Build field set for enum class.
     * @param <E> the enum type
     * @param pAnchor the field anchor
     * @param pClass the enum class
     * @return the map from field to enum.
     */
    public static <E extends Enum<E>> Map<MetisField, E> buildFieldMap(final MetisFields pAnchor,
                                                                       final Class<E> pClass) {
        /* Create the map */
        Map<MetisField, E> myMap = new HashMap<>();

        /* Loop through the enum values */
        for (E myValue : pClass.getEnumConstants()) {
            /* Determine name */
            String myName = (myValue instanceof MetisFieldEnum)
                                                                ? ((MetisFieldEnum) myValue).getFieldName()
                                                                : myValue.toString();

            /* Declare a field for the value */
            MetisField myField = pAnchor.declareLocalField(myName);

            /* Add to the map */
            myMap.put(myField, myValue);
        }

        /* Return the map */
        return myMap;
    }

    /**
     * Reverse field set to enum map.
     * @param <E> the enum type
     * @param pSourceMap the source map
     * @param pClass the enum class
     * @return the map from field to enum.
     */
    public static <E extends Enum<E>> Map<E, MetisField> reverseFieldMap(final Map<MetisField, E> pSourceMap,
                                                                         final Class<E> pClass) {
        /* Create the map */
        Map<E, MetisField> myMap = new EnumMap<>(pClass);

        /* Loop through the enum values */
        for (Map.Entry<MetisField, E> myEntry : pSourceMap.entrySet()) {
            /* Access Key and Value */
            MetisField myField = myEntry.getKey();
            E myEnum = myEntry.getValue();

            /* Add to the map */
            myMap.put(myEnum, myField);
        }

        /* Return the map */
        return myMap;
    }

    /**
     * Field presence status.
     */
    public enum MetisFieldRequired {
        /**
         * Must exist.
         */
        MUSTEXIST,

        /**
         * Can exist.
         */
        CANEXIST,

        /**
         * Not Allowed.
         */
        NOTALLOWED;
    }

    /**
     * Enum naming interface.
     */
    @FunctionalInterface
    public interface MetisFieldEnum {
        /**
         * Get Field name.
         * @return the field name
         */
        String getFieldName();
    }
}
