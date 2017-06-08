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
package net.sourceforge.joceanus.jmetis.atlas.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues.MetisDataVersionedItem;

/**
 * Metis Data FieldSet.
 */
public class MetisDataFieldSet {
    /**
     * Data Field Item.
     */
    public interface MetisDataFieldItem
            extends MetisDataObjectFormat {
        /**
         * Obtain the Data FieldSet.
         * @return the report fields
         */
        MetisDataFieldSet getDataFieldSet();

        /**
         * Obtain Field value.
         * @param pField the field
         * @return the value of the field
         */
        Object getFieldValue(MetisDataField pField);
    }

    /**
     * Hash Prime.
     */
    protected static final int HASH_PRIME = 19;

    /**
     * No Maximum Length.
     */
    protected static final Integer FIELD_NO_MAXLENGTH = -1;

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
    private final List<MetisDataField> theFields;

    /**
     * Parent fields.
     */
    private final MetisDataFieldSet theParent;

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
     * @param pClazz the class of the item
     */
    public MetisDataFieldSet(final Class<?> pClazz) {
        this(pClazz, null);
    }

    /**
     * Constructor.
     * @param pClazz the class of the item
     * @param pParent the parent fields
     */
    public MetisDataFieldSet(final Class<?> pClazz,
                             final MetisDataFieldSet pParent) {
        /* Initialise the list */
        theName = pClazz.getSimpleName();
        theParent = pParent;
        theFields = new ArrayList<>();
        if (theParent != null) {
            theNextValue = theParent.getNumValues();
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
    protected Integer getNextValue() {
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
     * Obtain a new version control.
     * @param pItem the owning item
     * @return the version control
     */
    public MetisDataVersionControl newVersionControl(final MetisDataVersionedItem pItem) {
        return new MetisDataVersionControl(pItem);
    }

    /**
     * Obtain a new version values.
     * @param pItem the owning item
     * @return the version values
     */
    protected MetisDataVersionValues newVersionValues(final MetisDataVersionedItem pItem) {
        return new MetisDataVersionValues(pItem);
    }

    /**
     * Obtain an iterator for the fields.
     * @return the field iterator
     */
    public Iterator<MetisDataField> fieldIterator() {
        return new FieldIterator(this);
    }

    /**
     * Declare local field not used for equality.
     * @param pName the name of the field
     * @return the field
     */
    public MetisDataField declareLocalField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisFieldEquality.DERIVED, MetisFieldStorage.LOCAL);
    }

    /**
     * Declare field used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisDataField declareEqualityField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisFieldEquality.EQUALITY, MetisFieldStorage.LOCAL);
    }

    /**
     * Declare field used for equality and comparison test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisDataField declareComparisonField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisFieldEquality.COMPARISON, MetisFieldStorage.LOCAL);
    }

    /**
     * Declare versioned field not used for equality test.
     * @param pName the name of the field
     * @return the field
     */
    public MetisDataField declareDerivedVersionedField(final String pName) {
        return declareDataField(pName, MetisDataType.OBJECT, FIELD_NO_MAXLENGTH, MetisFieldEquality.DERIVED, MetisFieldStorage.VERSIONED);
    }

    /**
     * Declare versioned field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisDataField declareEqualityVersionedField(final String pName,
                                                        final MetisDataType pDataType) {
        return declareEqualityVersionedField(pName, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned field used for equality test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisDataField declareEqualityVersionedField(final String pName,
                                                        final MetisDataType pDataType,
                                                        final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisFieldEquality.EQUALITY, MetisFieldStorage.VERSIONED);
    }

    /**
     * Declare versioned field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @return the field
     */
    public MetisDataField declareComparisonVersionedField(final String pName,
                                                          final MetisDataType pDataType) {
        return declareComparisonVersionedField(pName, pDataType, FIELD_NO_MAXLENGTH);
    }

    /**
     * Declare versioned field used for equality and comparison test.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @return the field
     */
    public MetisDataField declareComparisonVersionedField(final String pName,
                                                          final MetisDataType pDataType,
                                                          final Integer pMaxLength) {
        return declareDataField(pName, pDataType, pMaxLength, MetisFieldEquality.COMPARISON, MetisFieldStorage.VERSIONED);
    }

    /**
     * Declare field used for calculation.
     * @param pName the name of the field
     * @return the field
     */
    public MetisDataField declareCalculatedField(final String pName) {
        return declareDataField(pName, MetisDataType.CONTEXT, FIELD_NO_MAXLENGTH, MetisFieldEquality.DERIVED, MetisFieldStorage.CALCULATED);
    }

    /**
     * Declare local field referenced by index.
     * @param pName the name of the field
     * @return the field
     */
    public MetisDataField declareIndexField(final String pName) {
        return declareDataField(pName);
    }

    /**
     * Declare field.
     * @param pName the name of the field
     * @param pDataType the dataType of the field
     * @param pMaxLength the maximum length of the field
     * @param pEquality the equality class
     * @param pStorage the field storage type
     * @return the field
     */

    private synchronized MetisDataField declareDataField(final String pName,
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
        MetisDataField myField = new MetisDataField(this, pName, pDataType, pMaxLength, pEquality, pStorage);

        /* Note that we have indices */
        hasIndices = true;

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }

    /**
     * Register the field.
     * @param pField the field
     */

    protected void registerField(final MetisDataField pField) {
        /* Add it to the list */
        theFields.add(pField);

        /* Adjust indications */
        if (pField.getEquality().isComparison()) {
            hasComparisons = true;
        }
        if (pField.getStorage().isVersioned()) {
            hasVersions = true;
        }
    }

    /**
     * Declare index-only field.
     * @param pName the name of the field
     * @return the field
     */
    private MetisDataField declareDataField(final String pName) {
        /* Reject if we have versions */
        if (hasVersions) {
            throw new IllegalStateException("Already versioned");
        }

        /* Create the field */
        MetisDataField myField = new MetisDataField(this, pName);

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
    protected void checkUniqueName(final String pName) {
        Iterator<MetisDataField> myIterator = fieldIterator();
        while (myIterator.hasNext()) {
            MetisDataField myField = myIterator.next();

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
        if (!(pThat instanceof MetisDataFieldSet)) {
            return false;
        }

        /* Access as MetisDataFieldSet */
        MetisDataFieldSet myThat = (MetisDataFieldSet) pThat;

        /* Must have same anchor id */
        return theAnchorId == myThat.getAnchorId();
    }

    @Override
    public int hashCode() {
        return theAnchorId;
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
     * Field Storage.
     */
    public enum MetisFieldStorage {
        /**
         * Local.
         */
        LOCAL,

        /**
         * Versioned.
         */
        VERSIONED,

        /**
         * Calculated.
         */
        CALCULATED;

        /**
         * Is the field versioned?
         * @return true/false
         */
        public boolean isVersioned() {
            return this == VERSIONED;
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
     * Iterator class.
     */
    private static final class FieldIterator
            implements Iterator<MetisDataField> {
        /**
         * Preceding iterator.
         */
        private final Iterator<MetisDataField> thePreceding;

        /**
         * Local iterator.
         */
        private final Iterator<MetisDataField> theIterator;

        /**
         * Constructor.
         * @param pFields the fields
         */
        private FieldIterator(final MetisDataFieldSet pFields) {
            /* Allocate iterator */
            theIterator = pFields.theFields.iterator();

            /* Allocate preceding iterator */
            MetisDataFieldSet myParent = pFields.theParent;
            thePreceding = (myParent == null)
                                              ? null
                                              : myParent.fieldIterator();
        }

        @Override
        public boolean hasNext() {
            /* Check for preceding entry */
            if ((thePreceding != null)
                && (thePreceding.hasNext())) {
                return true;
            }

            /* Handle call here */
            return theIterator.hasNext();
        }

        @Override
        public MetisDataField next() {
            /* Check for preceding entry */
            if ((thePreceding != null)
                && (thePreceding.hasNext())) {
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
        public void forEachRemaining(final Consumer<? super MetisDataField> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }
    }
}
