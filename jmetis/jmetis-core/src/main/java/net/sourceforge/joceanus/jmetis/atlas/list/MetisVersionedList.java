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
package net.sourceforge.joceanus.jmetis.atlas.list;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataVersionedItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionControl;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues;
import net.sourceforge.joceanus.jmetis.atlas.list.MetisListChange.MetisListEvent;

/**
 * Versioned List implementation.
 * @param <T> the item type
 */
public abstract class MetisVersionedList<T extends MetisDataVersionedItem>
        extends MetisIndexedList<T> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisVersionedList.class);

    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(MetisVersionedList.class, MetisIndexedList.getBaseFields());

    /**
     * ListType Field Id.
     */
    private static final MetisDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_TYPE.getValue());

    /**
     * Class Field Id.
     */
    private static final MetisDataField FIELD_CLASS = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_CLASS.getValue());

    /**
     * Version Field Id.
     */
    private static final MetisDataField FIELD_VERSION = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION.getValue());

    /**
     * The list type.
     */
    private final MetisListType theListType;

    /**
     * The class of the list.
     */
    private final Class<T> theClass;

    /**
     * The constructor for the item.
     */
    private final Constructor<T> theConstructor;

    /**
     * The fields.
     */
    private final MetisDataFieldSet theItemFields;

    /**
     * Is this a readOnly list.
     */
    private final boolean isReadOnly;

    /**
     * The version of the list.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pListType the list type
     * @param pClazz the class of the item
     */
    protected MetisVersionedList(final MetisListType pListType,
                                 final Class<T> pClazz) {
        /* Store parameters */
        this(pListType, pClazz, null);
    }

    /**
     * Constructor.
     * @param pListType the list type
     * @param pClazz the class of the item
     * @param pFields the item fields
     */
    protected MetisVersionedList(final MetisListType pListType,
                                 final Class<T> pClazz,
                                 final MetisDataFieldSet pFields) {
        /* Store parameters */
        theListType = pListType;
        theClass = pClazz;
        theItemFields = pFields;

        /* Determine whether this is a readOnly list */
        isReadOnly = !pFields.hasVersions();

        /* Obtain the constructor */
        theConstructor = getConstructor();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_TYPE.equals(pField)) {
            return theListType;
        }
        if (FIELD_CLASS.equals(pField)) {
            return theClass;
        }
        if (FIELD_VERSION.equals(pField)) {
            return !isReadOnly && theVersion != 0
                                                  ? theVersion
                                                  : MetisDataFieldValue.SKIP;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFields() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the dataFields for an item.
     * @return the dataFields
     */
    public MetisDataFieldSet getItemFieldSet() {
        return theItemFields;
    }

    /**
     * Obtain the listType.
     * @return the listType
     */
    public MetisListType getListType() {
        return theListType;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Obtain the class.
     * @return the version
     */
    public Class<T> getTheClass() {
        return theClass;
    }

    /**
     * Is the list readOnly?
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Obtain the standard constructor for the item.
     * @return the constructor
     */
    private Constructor<T> getConstructor() {
        /* Protect against exceptions */
        try {
            return isReadOnly
                              ? null
                              : theClass.getConstructor();
        } catch (NoSuchMethodException
                | SecurityException e) {
            LOGGER.error("Unable to instantiate constructor", e);
            return null;
        }
    }

    /**
     * Set version.
     * @param pVersion the version
     */
    protected void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Cast List to correct type if possible.
     * @param pSource the source list
     * @return the correctly cast list list
     */
    @SuppressWarnings("unchecked")
    protected MetisVersionedList<T> castList(final MetisVersionedList<?> pSource) {
        /* Class must be the same */
        if (!theClass.equals(pSource.getTheClass())) {
            throw new InvalidParameterException("Inconsistent class");
        }

        /* Access as correctly cast list */
        return (MetisVersionedList<T>) pSource;
    }

    /**
     * Check reWind version.
     * @param pVersion the version to reWind to
     */
    protected void checkReWindVersion(final int pVersion) {
        /* Not supported for readOnly lists */
        if (isReadOnly()) {
            throw new UnsupportedOperationException();
        }

        /* Version must be less than current version and positive */
        if ((theVersion < pVersion)
            || (pVersion < 0)) {
            throw new IllegalArgumentException("Invalid Version");
        }
    }

    /**
     * ReWind the list to a particular version (Base and EditList only).
     * @param pVersion the version to reWind to
     */
    protected void doReWindToVersion(final int pVersion) {
        /* Create a new Change Detail */
        MetisListChange<T> myChange = new MetisListChange<>(MetisListEvent.REWIND);

        /* Note maximum version */
        int myMaxVersion = 0;

        /* Loop through the list */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myCurr = myIterator.next();
            MetisDataVersionControl myControl = myCurr.getVersionControl();

            /* If the version is later than the required version */
            int myVersion = myControl.getVersion();
            if (myVersion > pVersion) {
                /* If the item was created after the required version */
                if (myControl.getOriginalVersion() > pVersion) {
                    /* Remove from list */
                    myIterator.remove();
                    myChange.registerDeleted(myCurr);
                    continue;
                }

                /* Loop while version is too high */
                while (myVersion > pVersion) {
                    /* Pop history */
                    myControl.popTheHistory();
                    myVersion = myControl.getVersion();
                }

                /* Adjust the state */
                myControl.adjustState();

                /* Note maximum version */
                myMaxVersion = Math.max(myMaxVersion, myVersion);

                /* Register the change */
                myChange.registerChanged(myCurr);
            }
        }

        /* Fire the event */
        fireEvent(myChange);

        /* Set the version */
        theVersion = myMaxVersion;
    }

    /**
     * Create a New item representing a deletion.
     * @param pBase the base item
     * @return the new item
     */
    protected T newDiffDeletedItem(final T pBase) {
        /* Obtain a new item */
        T myNew = newListItem(pBase.getIndexedId());

        /* Obtain a deleted values set as the current value */
        MetisDataVersionControl myControl = pBase.getVersionControl();
        MetisDataVersionValues myBaseSet = myControl.getValueSet();
        MetisDataVersionValues mySet = myBaseSet.cloneIt();
        mySet.setDeletion(true);

        /* Obtain an undeleted set as the base value */
        myBaseSet = mySet.cloneIt();
        myBaseSet.setDeletion(false);

        /* Record as the history of the item */
        myControl = myNew.getVersionControl();
        myControl.setValues(mySet);
        myControl.setHistory(myBaseSet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New "changed" item for a difference list.
     * @param pCurr the current item
     * @param pBase the base item
     * @return the new item
     */
    protected T newDiffChangedItem(final T pCurr,
                                   final T pBase) {
        /* Obtain a new item */
        T myNew = newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisDataVersionControl myControl = pCurr.getVersionControl();
        MetisDataVersionValues mySet = myControl.getValueSet();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the value set as the base value */
        myControl = pBase.getVersionControl();
        MetisDataVersionValues myBaseSet = myControl.getValueSet();
        myBaseSet = myBaseSet.cloneIt();

        /* Record as the history of the item */
        myControl = myNew.getVersionControl();
        myControl.setValues(mySet);
        myControl.setHistory(myBaseSet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New "added" item for an update/difference list.
     * @param pCurr the current item
     * @return the new item
     */
    protected T newDiffAddedItem(final T pCurr) {
        /* Obtain a new item */
        T myNew = newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisDataVersionControl myControl = pCurr.getVersionControl();
        MetisDataVersionValues mySet = myControl.getValueSet();
        mySet = mySet.cloneIt();
        mySet.setVersion(1);

        /* Record as the history of the item */
        myControl = myNew.getVersionControl();
        myControl.setValues(mySet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New item for the list with the given id.
     * @param pId the id
     * @return the new item
     */
    protected T newListItem(final Integer pId) {
        /* Protect against exceptions */
        try {
            T myItem = theConstructor.newInstance();
            myItem.getVersionControl().setIndexedId(pId);
            return myItem;
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            LOGGER.error("Failed to instantiate object", e);
            return null;
        }
    }
}
