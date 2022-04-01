/*******************************************************************************
 * Metis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.lethe.data;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;

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
     * No Maximum Length.
     */
    public static final Integer FIELD_NO_MAXLENGTH = -1;

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
    private final List<MetisLetheField> theFields;

    /**
     * Parent fields.
     */
    private final MetisFields theParent;

    /**
     * Is the item encrypted?
     */
    private boolean isEncrypted;

    /**
     * has comparisons?
     */
    private boolean hasComparisons;

    /**
     * has versions?
     */
    private boolean hasVersions;

    /**
     * has indices?
     */
    private boolean hasIndices;

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
        theFields = new ArrayList<>();
        if (theParent != null) {
            theNextValue = theParent.getNumValues();
            isEncrypted = theParent.isEncrypted();
            hasComparisons = theParent.hasComparisons();
            hasVersions = theParent.hasVersions();
            hasIndices = theParent.hasIndices();
        } else {
            theNextValue = Integer.valueOf(0);
        }

        /* Store the anchorId */
        theAnchorId = theNextAnchorId.getAndIncrement();
    }

    /**
     * Obtain next value index.
     * @return the next index
     */
    private Integer getNextValue() {
        /* return the new anchor id */
        final Integer myValue = theNextValue;
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
     * Is the item encrypted?
     * @return true/false
     */
    public boolean isEncrypted() {
        return isEncrypted;
    }

    /**
     * Does the item have comparisons?
     * @return true/false
     */
    public boolean hasComparisons() {
        return hasComparisons;
    }

    /**
     * Does the item have versioned values?
     * @return true/false
     */
    public boolean hasVersions() {
        return hasVersions;
    }

    /**
     * Does the item have indexed values?
     * @return true/false
     */
    public boolean hasIndices() {
        return hasIndices;
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
    public Iterator<MetisLetheField> fieldIterator() {
        return new FieldIterator(this);
    }

    /**
     * Declare local field not used for equality.
     * @param pName the name of the field
     * @return the field
     */
    public MetisLetheField declareLocalField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisLetheFieldEquality.DERIVED, MetisLetheFieldStorage.LOCAL);
    }

    /**
     * Declare field used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisLetheField declareEqualityField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisLetheFieldEquality.EQUALITY, MetisLetheFieldStorage.LOCAL);
    }

    /**
     * Declare field used for equality and comparison test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisLetheField declareComparisonField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisLetheFieldEquality.COMPARISON, MetisLetheFieldStorage.LOCAL);
    }

    /**
     * Declare valueSet field not used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisLetheField declareDerivedValueField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisLetheFieldEquality.DERIVED, MetisLetheFieldStorage.VALUESET);
    }

    /**
     * Declare valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisLetheField declareEqualityValueField(final String pName,
                                                     final MetisDataType pDataType) {
        return declareEqualityValueField(pName, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisLetheField declareEqualityValueField(final String pName,
                                                     final MetisDataType pDataType,
                                                     final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisLetheFieldEquality.EQUALITY, MetisLetheFieldStorage.VALUESET);
    }

    /**
     * Declare valueSet field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisLetheField declareComparisonValueField(final String pName,
                                                       final MetisDataType pDataType) {
        return declareComparisonValueField(pName, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare valueSet field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisLetheField declareComparisonValueField(final String pName,
                                                       final MetisDataType pDataType,
                                                       final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisLetheFieldEquality.COMPARISON, MetisLetheFieldStorage.VALUESET);
    }

    /**
     * Declare encrypted valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisLetheField declareEqualityEncryptedField(final String pName,
                                                         final MetisDataType pDataType) {
        return declareEqualityEncryptedField(pName, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisLetheField declareEqualityEncryptedField(final String pName,
                                                         final MetisDataType pDataType,
                                                         final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisLetheFieldEquality.EQUALITY, MetisLetheFieldStorage.ENCRYPTED);
    }

    /**
     * Declare encrypted valueSet field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisLetheField declareComparisonEncryptedField(final String pName,
                                                           final MetisDataType pDataType) {
        return declareComparisonEncryptedField(pName, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare encrypted valueSet field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisLetheField declareComparisonEncryptedField(final String pName,
                                                           final MetisDataType pDataType,
                                                           final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisLetheFieldEquality.COMPARISON, MetisLetheFieldStorage.ENCRYPTED);
    }

    /**
     * Declare field used for calculation.
     * @param pName the name of the field
     * @return the field
     */
    public MetisLetheField declareCalculatedField(final String pName) {
        return declareDataField(pName, MetisDataType.CONTEXT, FIELD_NO_MAXLENGTH, MetisLetheFieldEquality.DERIVED, MetisLetheFieldStorage.CALCULATED);
    }

    /**
     * Declare local field referenced by index.
     * @param pName the name of the field
     * @return the field
     */
    public MetisLetheField declareIndexField(final String pName) {
        return declareDataField(pName);
    }

    /**
     * Declare field.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the equality class
     * @param pStorage the storage class
     * @return the field
     */

    private synchronized MetisLetheField declareDataField(final String pName,
                                                          final MetisDataType pDataType,
                                                          final Integer pMaxLength,
                                                          final MetisLetheFieldEquality pEquality,
                                                          final MetisLetheFieldStorage pStorage) {
        /* Reject if we have indices */
        if (hasIndices) {
            throw new IllegalStateException("Already indexed");
        }

        /* Check the name */
        checkUniqueName(pName);

        /* Create the field */
        final MetisLetheField myField = new MetisLetheField(this, pName, pDataType, pMaxLength, pEquality, pStorage);

        /* Add it to the list */
        theFields.add(myField);

        /* Adjust indications */
        if (pEquality.isComparison()) {
            hasComparisons = true;
        }
        if (pStorage.isEncrypted()) {
            isEncrypted = true;
        }
        if (pStorage.isValueSet()) {
            hasVersions = true;
        }

        /* Return the index */
        return myField;
    }

    /**
     * Declare index-only field.
     * @param pName the name of the field
     * @return the field
     */
    private MetisLetheField declareDataField(final String pName) {
        /* Reject if we have versions */
        if (hasVersions) {
            throw new IllegalStateException("Already versioned");
        }

        /* Create the field */
        final MetisLetheField myField = new MetisLetheField(this, pName);

        /* Add it to the list */
        theFields.add(myField);

        /* Note that we have indices */
        hasIndices = true;

        /* Return the index */
        return myField;
    }

    /**
     * Check unique name.
     * @param pName the name to check.
     * @throws IllegalArgumentException if name is present
     */
    private void checkUniqueName(final String pName) {
        final Iterator<MetisLetheField> myIterator = fieldIterator();
        while (myIterator.hasNext()) {
            final MetisLetheField myField = myIterator.next();

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
        final MetisFields myThat = (MetisFields) pThat;

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
            implements Iterator<MetisLetheField> {
        /**
         * Preceding iterator.
         */
        private final Iterator<MetisLetheField> thePreceding;

        /**
         * Local iterator.
         */
        private final Iterator<MetisLetheField> theIterator;

        /**
         * Constructor.
         * @param pFields the fields
         */
        private FieldIterator(final MetisFields pFields) {
            /* Allocate iterator */
            theIterator = pFields.theFields.iterator();

            /* Allocate preceding iterator */
            final MetisFields myParent = pFields.theParent;
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
        public MetisLetheField next() {
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
        public void forEachRemaining(final Consumer<? super MetisLetheField> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }
    }

    /**
     * Individual fields.
     */
    public static final class MetisLetheField {
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
         * DataType of field.
         */
        private final MetisDataType theDataType;

        /**
         * Maximum Length of field.
         */
        private final Integer theMaxLength;

        /**
         * The field equality type.
         */
        private final MetisLetheFieldEquality theEquality;

        /**
         * The field storage type.
         */
        private final MetisLetheFieldStorage theStorage;

        /**
         * Constructor.
         * @param pAnchor the anchor
         * @param pName the name of the field
         * @param pDataType the dataType of the field
         * @param pMaxLength the maximum length of the field
         * @param pEquality the field equality type
         * @param pStorage the field storage type
         */
        protected MetisLetheField(final MetisFields pAnchor,
                                  final String pName,
                                  final MetisDataType pDataType,
                                  final Integer pMaxLength,
                                  final MetisLetheFieldEquality pEquality,
                                  final MetisLetheFieldStorage pStorage) {
            /* Store parameters */
            theAnchor = pAnchor;
            theName = pName;
            theDataType = pDataType;
            theMaxLength = pMaxLength;
            theEquality = pEquality;
            theStorage = pStorage;

            /* Check Validity */
            checkValidity();

            /* Allocate value index if required */
            theIndex = theStorage.isValueSet()
                                               ? theAnchor.getNextValue()
                                               : -1;
        }

        /**
         * Constructor.
         * @param pAnchor the anchor
         * @param pName the name of the field
         * @param pEquality the field equality type
         * @param pStorage the field storage type
         */
        protected MetisLetheField(final MetisFields pAnchor,
                                  final String pName,
                                  final MetisLetheFieldEquality pEquality,
                                  final MetisLetheFieldStorage pStorage) {
            this(pAnchor, pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, pEquality, pStorage);
        }

        /**
         * Constructor.
         * @param pAnchor the anchor
         * @param pName the name of the field
         */
        protected MetisLetheField(final MetisFields pAnchor,
                                  final String pName) {
            /* Store parameters */
            theAnchor = pAnchor;
            theName = pName;
            theDataType = MetisDataType.OBJECT;
            theMaxLength = FIELD_NO_MAXLENGTH;
            theEquality = MetisLetheFieldEquality.DERIVED;
            theStorage = MetisLetheFieldStorage.LOCAL;

            /* Check Validity */
            checkValidity();

            /* Allocate index */
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
         * Get the dataType.
         * @return the dataType
         */
        public MetisDataType getDataType() {
            return theDataType;
        }

        /**
         * Get the maximum length.
         * @return the maxLength
         */
        public Integer getMaxLength() {
            return theMaxLength;
        }

        /**
         * Obtain the equality type.
         * @return true/false
         */
        public MetisLetheFieldEquality getEquality() {
            return theEquality;
        }

        /**
         * Obtain the storage type.
         * @return true/false
         */
        public MetisLetheFieldStorage getStorage() {
            return theStorage;
        }

        /**
         * Obtain the anchor for the field.
         * @return the anchor
         */
        public MetisFields getAnchor() {
            return theAnchor;
        }

        /**
         * Check validity of Field Definition.
         */
        private void checkValidity() {
            /* Check whether length is valid */
            switch (theDataType) {
                case STRING:
                case BYTEARRAY:
                case CHARARRAY:
                    if (theMaxLength < 0) {
                        throw new InvalidParameterException("Length required for String/Array");
                    }
                    break;
                default:
                    if (!theMaxLength.equals(FIELD_NO_MAXLENGTH)) {
                        throw new InvalidParameterException("Length allowed only for String/Array");
                    }
                    break;

            }

            /* Disallow object on Storage */
            if (theStorage.isValueSet()
                && theEquality.isEquality()
                && MetisDataType.OBJECT.equals(theDataType)) {
                throw new InvalidParameterException("Object DataType not allowed on equality/valueSet");
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
            if (!(pThat instanceof MetisLetheField)) {
                return false;
            }

            /* Access as MetisField */
            final MetisLetheField myThat = (MetisLetheField) pThat;

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
    public static <E extends Enum<E>> Map<MetisLetheField, E> buildFieldMap(final MetisFields pAnchor,
                                                                            final Class<E> pClass) {
        /* Create the map */
        final Map<MetisLetheField, E> myMap = new HashMap<>();

        /* Loop through the enum values */
        for (E myValue : pClass.getEnumConstants()) {
            /* Determine name */
            final String myName = (myValue instanceof MetisLetheFieldEnum)
                                                                           ? ((MetisLetheFieldEnum) myValue).getFieldName()
                                                                           : myValue.toString();

            /* Declare a field for the value */
            final MetisLetheField myField = pAnchor.declareLocalField(myName);

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
    public static <E extends Enum<E>> Map<E, MetisLetheField> reverseFieldMap(final Map<MetisLetheField, E> pSourceMap,
                                                                              final Class<E> pClass) {
        /* Create the map */
        final Map<E, MetisLetheField> myMap = new EnumMap<>(pClass);

        /* Loop through the enum values */
        for (Entry<MetisLetheField, E> myEntry : pSourceMap.entrySet()) {
            /* Access Key and Value */
            final MetisLetheField myField = myEntry.getKey();
            final E myEnum = myEntry.getValue();

            /* Add to the map */
            myMap.put(myEnum, myField);
        }

        /* Return the map */
        return myMap;
    }

    /**
     * Field Storage.
     */
    public enum MetisLetheFieldStorage {
        /**
         * Local.
         */
        LOCAL,

        /**
         * ValueSet.
         */
        VALUESET,

        /**
         * Encrypted.
         */
        ENCRYPTED,

        /**
         * Calculated.
         */
        CALCULATED;

        /**
         * Is the field stored in valueSet?
         * @return true/false
         */
        public boolean isValueSet() {
            return this == VALUESET
                   || this == ENCRYPTED;
        }

        /**
         * Is the field encrypted?
         * @return true/false
         */
        public boolean isEncrypted() {
            return this == ENCRYPTED;
        }

        /**
         * Is the field calculated?
         * @return true/false
         */
        public boolean isCalculated() {
            return this == CALCULATED;
        }
    }

    /**
     * Field Equality.
     */
    public enum MetisLetheFieldEquality {
        /**
         * Derived.
         */
        DERIVED,

        /**
         * Equality.
         */
        EQUALITY,

        /**
         * Comparison.
         */
        COMPARISON;

        /**
         * Is the field used in equality?
         * @return true/false
         */
        public boolean isEquality() {
            return this != DERIVED;
        }

        /**
         * Is the field used in comparison?
         * @return true/false
         */
        public boolean isComparison() {
            return this == COMPARISON;
        }
    }

    /**
     * Enum naming interface.
     */
    @FunctionalInterface
    public interface MetisLetheFieldEnum {
        /**
         * Get Field name.
         * @return the field name
         */
        String getFieldName();
    }
}
