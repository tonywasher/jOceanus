/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmetis.field;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldItemType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldVersionedDef;

/**
 * Metis Data FieldSet.
 * @param <T> the data type
 */
public class MetisFieldSet<T extends MetisFieldItem>
        implements MetisFieldSetDef {
    /**
     * Hash Prime.
     */
    public static final int HASH_PRIME = 19;

    /**
     * No Maximum Length.
     */
    protected static final Integer FIELD_NO_MAXLENGTH = -1;

    /**
     * Map of ClassName to FieldSet.
     */
    private static final Map<String, MetisFieldSet<?>> FIELDSET_MAP = new HashMap<>();

    /**
     * The Next anchorId.
     */
    private static final AtomicInteger NEXT_ANCHORID = new AtomicInteger(1);

    /**
     * Id of this anchor.
     */
    private final Integer theAnchorId;

    /**
     * Class of Item.
     */
    private final Class<T> theClazz;

    /**
     * List of fields.
     */
    private final List<MetisField<T>> theFields;

    /**
     * Parent fields.
     */
    private final MetisFieldSetDef theParent;

    /**
     * is this a static fieldSet?
     */
    private final boolean isStatic;

    /**
     * Next index.
     */
    private Integer theNextIndex;

    /**
     * Does the fieldSet have versioned fields?
     */
    private boolean hasVersions;

    /**
     * Is this fieldSet locked?
     */
    private boolean isLocked;

    /**
     * The itemType.
     */
    private MetisFieldItemType theItemType;

    /**
     * Constructor.
     * @param pClazz the class of the item
     * @param pStatic is this a static fieldSet?
     * @param pParent the parent fields
     */
    MetisFieldSet(final Class<T> pClazz,
                  final MetisFieldSetDef pParent,
                  final boolean pStatic) {
        /* Store the parameters */
        theClazz = pClazz;
        theParent = pParent;
        isStatic = pStatic;

        /* Initialise the set */
        theFields = new ArrayList<>();
        if (pParent != null) {
            theNextIndex = pParent.getNumVersioned();
            hasVersions = pParent.hasVersions();
            theParent.setLocked();
            theItemType = isStatic
                                   ? null
                                   : pParent.getItemType();
        } else {
            theNextIndex = 0;
        }

        /* Store the anchorId */
        theAnchorId = NEXT_ANCHORID.getAndIncrement();
    }

    /**
     * Declare a static fieldSet.
     * @param <T> the itemType
     * @param pClazz the class of the fieldSet
     * @return the fieldSet.
     */
    public static <T extends MetisFieldItem> MetisFieldSet<T> newFieldSet(final Class<T> pClazz) {
        /* Synchronise on class */
        synchronized (MetisFieldSet.class) {
            /* Locate the parent fieldSet if it exists */
            final MetisFieldSetDef myParent = lookUpParentFieldSet(pClazz);

            /* Create the new fieldSet and store into map */
            final MetisFieldSet<T> myFieldSet = new MetisFieldSet<>(pClazz, myParent, true);
            registerFieldSet(pClazz, myFieldSet);

            /* Return the new fieldSet */
            return myFieldSet;
        }
    }

    /**
     * LookUp a Parent FieldSet.
     * @param pClazz the class of the fieldSet
     * @return the fieldSet.
     */
    public static MetisFieldSetDef lookUpParentFieldSet(final Class<?> pClazz) {
        /* Check that the class does not already exist */
        final String myClassName = pClazz.getCanonicalName();
        if (myClassName == null || pClazz.isArray()) {
            throw new IllegalStateException("Invalid Class " + pClazz.getName());
        }

        /* Check that the class does not already exist */
        if (FIELDSET_MAP.get(myClassName) != null) {
            throw new IllegalStateException("FieldSet already exists for " + myClassName);
        }

        /* Locate the parent fieldSet if it exists */
        final Class<?> myParentClass = pClazz.getSuperclass();
        return myParentClass == null
                                     ? null
                                     : FIELDSET_MAP.get(myParentClass.getCanonicalName());
    }

    /**
     * LookUp a static FieldSet.
     * @param <T> the itemType
     * @param pClazz the class of the fieldSet
     * @return the fieldSet.
     */
    public static <T extends MetisFieldItem> MetisFieldSet<T> lookUpFieldSet(final Class<T> pClazz) {
        /* Look up the definition */
        final String myClassName = pClazz.getCanonicalName();
        try {
            Class.forName(myClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Invalid class " + myClassName, e);
        }

        /* Look up the FieldSet for the class and return it */
        @SuppressWarnings("unchecked")
        final MetisFieldSet<T> myFieldSet = (MetisFieldSet<T>) FIELDSET_MAP.get(myClassName);
        if (myFieldSet == null) {
            throw new IllegalStateException("FieldSet not found " + myClassName);
        }
        return myFieldSet;
    }

    /**
     * Register a fieldSet.
     * @param pClazz the class of the fieldSet
     * @param pFieldSet the fieldSet
     */
    protected static void registerFieldSet(final Class<?> pClazz,
                                           final MetisFieldSet<?> pFieldSet) {
        /* Check that the class does not already exist */
        final String myClassName = pClazz.getCanonicalName();
        FIELDSET_MAP.put(myClassName, pFieldSet);
    }

    /**
     * Declare a local fieldSet.
     * @param <T> the itemType
     * @param pObject the object
     * @return the fieldSet.
     */
    @SuppressWarnings("unchecked")
    public static <T extends MetisFieldItem> MetisFieldSet<T> newFieldSet(final T pObject) {
        /* Locate the static fieldSet for this class */
        final Class<T> myClazz = (Class<T>) pObject.getClass();
        final String myClassName = myClazz.getCanonicalName();

        /* Synchronise on class */
        synchronized (MetisFieldSet.class) {
            MetisFieldSet<?> myParent = FIELDSET_MAP.get(myClassName);
            if (myParent == null) {
                myParent = newFieldSet(myClazz);
            }

            /* Create the new fieldSet */
            return new MetisFieldSet<>(myClazz, myParent, false);
        }
    }

    /**
     * Obtain the anchorId.
     * @return the id
     */
    public Integer getAnchorId() {
        return theAnchorId;
    }

    @Override
    public String getName() {
        return theItemType != null
                                   ? theItemType.toString()
                                   : theClazz.getSimpleName();
    }

    /**
     * Obtain the field class.
     * @return the field class
     */
    protected Class<T> getFieldClass() {
        return theClazz;
    }

    /**
     * Obtain next value index.
     * @return the next index
     */
    protected Integer getNextIndex() {
        /* return the new index id */
        final Integer myIndex = theNextIndex;
        theNextIndex = theNextIndex + 1;
        return myIndex;
    }

    @Override
    public Integer getNumVersioned() {
        return theNextIndex;
    }

    @Override
    public boolean hasVersions() {
        return hasVersions;
    }

    /**
     * Is this a static fieldSet?
     * @return true/false
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Is this fieldSet locked?
     * @return true/false
     */
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public void setLocked() {
        isLocked = true;
    }

    @Override
    public MetisFieldItemType getItemType() {
        return theItemType;
    }

    /**
     * Set the itemType.
     * @param pItemType the itemType
     */
    public void setItemType(final MetisFieldItemType pItemType) {
        theItemType = pItemType;
    }

    @Override
    public Iterator<MetisFieldDef> fieldIterator() {
        return new MetisFieldIterator<>(this);
    }

    /**
     * Declare local field not used for equality.
     * @param pId the id of the field
     * @param pValue the value supplier
     * @return the field
     */
    public MetisField<T> declareLocalField(final MetisDataFieldId pId,
                                           final Function<T, Object> pValue) {
        return declareDataField(pId, pValue);
    }

    /**
     * Declare local field not used for equality.
     * @param pId the id of the field
     * @param pValue the value supplier
     * @return the field
     */
    public MetisField<T> declareLocalField(final MetisDataFieldId pId,
                                           final BiFunction<T, MetisDataFieldId, Object> pValue) {
        return declareDataField(pId, pValue);
    }

    /**
     * Declare local non-equality fields one for each Enum.
     * @param <E> the Enum
     * @param pClazz the class of the Enum
     * @param pValue the value supplier
     * @return map of class to fields
     */
    public <E extends Enum<E>> Map<E, MetisFieldDef> declareLocalFieldsForEnum(final Class<E> pClazz,
                                                                               final BiFunction<T, E, Object> pValue) {
        /* Loop through the enum constants */
        final Map<E, MetisFieldDef> myMap = new EnumMap<>(pClazz);
        for (E myValue : pClazz.getEnumConstants()) {
            /* Create an id and callback for the value */
            final MetisDataFieldId myId = myValue instanceof MetisDataFieldId ? (MetisDataFieldId) myValue : new MetisFieldSimpleId(myValue.toString());
            final MetisField<T> myField = declareDataField(myId, t -> pValue.apply(t, myValue));

            /* Store into the map */
            myMap.put(myValue, myField);
        }

        /* Return the map */
        return myMap;
    }

    /**
     * Declare field used for calculation.
     * @param pId the fieldId
     * @return the field
     */
    public MetisField<T> declareCalculatedField(final MetisDataFieldId pId) {
        return declareDataField(pId, (Function<T, Object>) null);
    }

    /**
     * Declare local field not used for equality.
     * @param pName the name of the field
     * @param pValue the value supplier
     * @return the field
     */
    public MetisField<T> declareLocalField(final String pName,
                                           final Function<T, Object> pValue) {
        /* Only allowed for dynamic fieldSet */
        if (isStatic) {
            throw new IllegalArgumentException("Only allowed for dynamic fieldSets");
        }
        return declareLocalField(new MetisFieldSimpleId(pName), pValue);
    }

    /**
     * Declare non-versioned field.
     * @param pId the fieldId
     * @param pValue the value supplier
     * @return the field
     */
    private MetisField<T> declareDataField(final MetisDataFieldId pId,
                                           final Function<T, Object> pValue) {
        /* Create the field */
        final MetisField<T> myField = new MetisField<>(this, pId, pValue);

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }

    /**
     * Declare non-versioned field.
     * @param pId the fieldId
     * @param pValue the value supplier
     * @return the field
     */
    private MetisField<T> declareDataField(final MetisDataFieldId pId,
                                           final BiFunction<T, MetisDataFieldId, Object> pValue) {
        /* Create the field */
        final MetisField<T> myField = new MetisField<>(this, pId, pValue);

        /* Register the field */
        registerField(myField);

        /* Return the index */
        return myField;
    }

    /**
     * Register the field.
     * @param pField the field
     */
    protected void registerField(final MetisField<T> pField) {
        /* Reject if we are locked */
        if (isLocked) {
            throw new IllegalStateException("Already locked");
        }

        /* Reject if we are local and versioned */
        if (!isStatic && pField instanceof MetisFieldVersionedDef) {
            throw new IllegalStateException("Can't declare versioned field on local fieldSet");
        }

        /* Synchronise */
        synchronized (this) {
            /* Check the id for uniqueness */
            checkUniqueName(pField.getFieldId());

            /* Add it to the list */
            theFields.add(pField);

            /* Adjust indications */
            if (pField instanceof MetisFieldVersionedDef) {
                hasVersions = true;
            }
        }
    }

    /**
     * Check uniqueness of Id.
     * @param pId the id to check.
     * @throws IllegalArgumentException if name is present
     */
    protected void checkUniqueName(final MetisDataFieldId pId) {
        /* Obtain the name to check */
        final String myName = pId.getId();

        /* Loop through existing iDs */
        final Iterator<MetisFieldDef> myIterator = fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();

            /* If the name exists, throw an exception */
            if (myName.equals(myField.getFieldId().getId())) {
                throw new IllegalArgumentException("Duplicate field name: " + myName);
            }
        }
    }

    @Override
    public MetisFieldDef getField(final MetisDataFieldId pId) {
        /* Loop through existing iDs */
        final Iterator<MetisFieldDef> myIterator = fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();

            /* If we have the id, return it */
            if (pId.equals(myField.getFieldId())) {
                return myField;
            }
        }

        /* Not found */
        throw new IllegalArgumentException("Unknown field: " + pId.getId());
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
        if (!(pThat instanceof MetisFieldSet)) {
            return false;
        }

        /* Access as MetisFieldSet */
        final MetisFieldSet<?> myThat = (MetisFieldSet<?>) pThat;

        /* Must have same anchor id */
        return theAnchorId.equals(myThat.getAnchorId());
    }

    @Override
    public int hashCode() {
        return theAnchorId;
    }

    /**
     * Build field set for enum class.
     * @param <E> the enum type
     * @param pClass the enum class
     * @param pValueLookup the Lookup Function
     * @return the map from field to enum.
     */
    public <E extends Enum<E> & MetisDataFieldId> Map<MetisDataFieldId, E> buildFieldMap(final Class<E> pClass,
                                                                                         final BiFunction<T, MetisDataFieldId, Object> pValueLookup) {
        /* Create the map */
        final Map<MetisDataFieldId, E> myMap = new HashMap<>();

        /* Loop through the enum values */
        for (E myValue : pClass.getEnumConstants()) {
            /* Declare a field for the value */
            declareLocalField(myValue, pValueLookup);

            /* Add to the map */
            myMap.put(myValue, myValue);
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
    public static <E extends Enum<E>> Map<E, MetisDataFieldId> reverseFieldMap(final Map<MetisDataFieldId, E> pSourceMap,
                                                                               final Class<E> pClass) {
        /* Create the map */
        final Map<E, MetisDataFieldId> myMap = new EnumMap<>(pClass);

        /* Loop through the enum values */
        for (Entry<MetisDataFieldId, E> myEntry : pSourceMap.entrySet()) {
            /* Access Key and Value */
            final MetisDataFieldId myFieldId = myEntry.getKey();
            final E myEnum = myEntry.getValue();

            /* Add to the map */
            myMap.put(myEnum, myFieldId);
        }

        /* Return the map */
        return myMap;
    }

    /**
     * Iterator class.
     * @param <T> the item type
     */
    private static final class MetisFieldIterator<T extends MetisFieldItem>
            implements Iterator<MetisFieldDef> {
        /**
         * Preceding iterator.
         */
        private final Iterator<MetisFieldDef> thePreceding;

        /**
         * Local iterator.
         */
        private final Iterator<MetisField<T>> theIterator;

        /**
         * Constructor.
         * @param pFields the fields
         */
        private MetisFieldIterator(final MetisFieldSet<T> pFields) {
            /* Allocate iterator */
            theIterator = pFields.theFields.iterator();

            /* Allocate preceding iterator */
            final MetisFieldSetDef myParent = pFields.theParent;
            thePreceding = myParent == null
                                            ? null
                                            : myParent.fieldIterator();
        }

        @Override
        public boolean hasNext() {
            /* Check for preceding entry */
            if (thePreceding != null
                && thePreceding.hasNext()) {
                return true;
            }

            /* Handle call here */
            return theIterator.hasNext();
        }

        @Override
        public MetisFieldDef next() {
            /* Check for preceding entry */
            if (thePreceding != null
                && thePreceding.hasNext()) {
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
        public void forEachRemaining(final Consumer<? super MetisFieldDef> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }
    }
}
