/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.data;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;

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
    private final List<MetisField> theFields;

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
    public Iterator<MetisField> fieldIterator() {
        return new FieldIterator(this);
    }

    /**
     * Declare local field not used for equality.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareLocalField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisFieldEquality.DERIVED, MetisFieldStorage.LOCAL);
    }

    /**
     * Declare field used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareEqualityField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisFieldEquality.EQUALITY, MetisFieldStorage.LOCAL);
    }

    /**
     * Declare field used for equality and comparison test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareComparisonField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisFieldEquality.COMPARISON, MetisFieldStorage.LOCAL);
    }

    /**
     * Declare valueSet field not used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareDerivedValueField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisFieldEquality.DERIVED, MetisFieldStorage.VALUESET);
    }

    /**
     * Declare valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisField declareEqualityValueField(final String pName,
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
    public MetisField declareEqualityValueField(final String pName,
                                                final MetisDataType pDataType,
                                                final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisFieldEquality.EQUALITY, MetisFieldStorage.VALUESET);
    }

    /**
     * Declare valueSet field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisField declareComparisonValueField(final String pName,
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
    public MetisField declareComparisonValueField(final String pName,
                                                  final MetisDataType pDataType,
                                                  final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisFieldEquality.COMPARISON, MetisFieldStorage.VALUESET);
    }

    /**
     * Declare encrypted valueSet field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisField declareEqualityEncryptedField(final String pName,
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
    public MetisField declareEqualityEncryptedField(final String pName,
                                                    final MetisDataType pDataType,
                                                    final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisFieldEquality.EQUALITY, MetisFieldStorage.ENCRYPTED);
    }

    /**
     * Declare encrypted valueSet field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisField declareComparisonEncryptedField(final String pName,
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
    public MetisField declareComparisonEncryptedField(final String pName,
                                                      final MetisDataType pDataType,
                                                      final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisFieldEquality.COMPARISON, MetisFieldStorage.ENCRYPTED);
    }

    /**
     * Declare field used for calculation.
     * @param pName the name of the field
     * @return the field
     */
    public MetisField declareCalculatedField(final String pName) {
        return declareDataField(pName, MetisDataType.CONTEXT, FIELD_NO_MAXLENGTH, MetisFieldEquality.DERIVED, MetisFieldStorage.CALCULATED);
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
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the equality class
     * @param pStorage the storage class
     * @return the field
     */

    private synchronized MetisField declareDataField(final String pName,
                                                     final MetisDataType pDataType,
                                                     final Integer pMaxLength,
                                                     final MetisFieldEquality pEquality,
                                                     final MetisFieldStorage pStorage) {
        /* Reject if we have indices */
        if (hasIndices) {
            throw new IllegalStateException("Already indexed");
        }

        /* Check the name */
        checkUniqueName(pName);

        /* Create the field */
        final MetisField myField = new MetisField(this, pName, pDataType, pMaxLength, pEquality, pStorage);

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
    private MetisField declareDataField(final String pName) {
        /* Reject if we have versions */
        if (hasVersions) {
            throw new IllegalStateException("Already versioned");
        }

        /* Create the field */
        final MetisField myField = new MetisField(this, pName);

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
        final Iterator<MetisField> myIterator = fieldIterator();
        while (myIterator.hasNext()) {
            final MetisField myField = myIterator.next();

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
        private final MetisFieldEquality theEquality;

        /**
         * The field storage type.
         */
        private final MetisFieldStorage theStorage;

        /**
         * Constructor.
         * @param pAnchor the anchor
         * @param pName the name of the field
         * @param pDataType the dataType of the field
         * @param pMaxLength the maximum length of the field
         * @param pEquality the field equality type
         * @param pStorage the field storage type
         */
        protected MetisField(final MetisFields pAnchor,
                             final String pName,
                             final MetisDataType pDataType,
                             final Integer pMaxLength,
                             final MetisFieldEquality pEquality,
                             final MetisFieldStorage pStorage) {
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
        protected MetisField(final MetisFields pAnchor,
                             final String pName,
                             final MetisFieldEquality pEquality,
                             final MetisFieldStorage pStorage) {
            this(pAnchor, pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, pEquality, pStorage);
        }

        /**
         * Constructor.
         * @param pAnchor the anchor
         * @param pName the name of the field
         */
        protected MetisField(final MetisFields pAnchor,
                             final String pName) {
            /* Store parameters */
            theAnchor = pAnchor;
            theName = pName;
            theDataType = MetisDataType.OBJECT;
            theMaxLength = FIELD_NO_MAXLENGTH;
            theEquality = MetisFieldEquality.DERIVED;
            theStorage = MetisFieldStorage.LOCAL;

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
        public MetisFieldEquality getEquality() {
            return theEquality;
        }

        /**
         * Obtain the storage type.
         * @return true/false
         */
        public MetisFieldStorage getStorage() {
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
                    if (theMaxLength != FIELD_NO_MAXLENGTH) {
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
            if (!(pThat instanceof MetisField)) {
                return false;
            }

            /* Access as MetisField */
            final MetisField myThat = (MetisField) pThat;

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
        final Map<MetisField, E> myMap = new HashMap<>();

        /* Loop through the enum values */
        for (E myValue : pClass.getEnumConstants()) {
            /* Determine name */
            final String myName = (myValue instanceof MetisFieldEnum)
                                                                      ? ((MetisFieldEnum) myValue).getFieldName()
                                                                      : myValue.toString();

            /* Declare a field for the value */
            final MetisField myField = pAnchor.declareLocalField(myName);

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
        final Map<E, MetisField> myMap = new EnumMap<>(pClass);

        /* Loop through the enum values */
        for (Map.Entry<MetisField, E> myEntry : pSourceMap.entrySet()) {
            /* Access Key and Value */
            final MetisField myField = myEntry.getKey();
            final E myEnum = myEntry.getValue();

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

        /**
         * must the field exist?
         * @return true/false
         */
        public boolean mustExist() {
            return this == MUSTEXIST;
        }

        /**
         * is the field notAllowed?
         * @return true/false
         */
        public boolean notAllowed() {
            return this == NOTALLOWED;
        }
    }

    /**
     * Field Storage.
     */
    public enum MetisFieldStorage {
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
    public enum MetisFieldEquality {
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
    public interface MetisFieldEnum {
        /**
         * Get Field name.
         * @return the field name
         */
        String getFieldName();
    }
}
