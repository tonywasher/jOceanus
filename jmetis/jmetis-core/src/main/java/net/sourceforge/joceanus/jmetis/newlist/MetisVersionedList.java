/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.newlist;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
import net.sourceforge.joceanus.jmetis.data.MetisValueSetHistory;
import net.sourceforge.joceanus.jmetis.newlist.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Versioned List implementation.
 * @param <T> the item type
 */
public abstract class MetisVersionedList<T extends MetisVersionedItem>
        extends MetisIndexedList<T>
        implements TethysEventProvider<MetisListEvent> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisVersionedList.class);

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MetisVersionedList.class.getSimpleName(), MetisIndexedList.getBaseFields());

    /**
     * ListType Field Id.
     */
    private static final MetisField FIELD_TYPE = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_TYPE.getValue());

    /**
     * Class Field Id.
     */
    private static final MetisField FIELD_CLASS = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_CLASS.getValue());

    /**
     * Version Field Id.
     */
    private static final MetisField FIELD_VERSION = FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION.getValue());

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisListEvent> theEventManager;

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
     * The version of the list.
     */
    private int theVersion;

    /**
     * Constructor.
     * @param pListType the list type
     * @param pClass the class of the item
     */
    protected MetisVersionedList(final MetisListType pListType,
                                 final Class<T> pClass) {
        this(pListType, new ArrayList<>(), pClass);
    }

    /**
     * Constructor.
     * @param pListType the list type
     * @param pClass the class of the item
     * @param pList the list
     */
    protected MetisVersionedList(final MetisListType pListType,
                                 final List<T> pList,
                                 final Class<T> pClass) {
        /* Initialise underlying class */
        super(pList);
        theListType = pListType;
        theClass = pClass;
        theConstructor = getConstructor();
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_TYPE.equals(pField)) {
            return theListType;
        }
        if (FIELD_CLASS.equals(pField)) {
            return theClass;
        }
        if (FIELD_VERSION.equals(pField)) {
            return theVersion != 0
                                   ? theVersion
                                   : MetisFieldValue.SKIP;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisFields getBaseFields() {
        return FIELD_DEFS;
    }

    @Override
    public TethysEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
     * Obtain the standard constructor for the item.
     * @return the constructor
     */
    private Constructor<T> getConstructor() {
        /* Protect against exceptions */
        try {
            return theClass.getConstructor(Integer.class);
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
            MetisValueSetHistory myHistory = myCurr.getValueSetHistory();

            /* If the version is later than the required version */
            int myVersion = myHistory.getValueSet().getVersion();
            if (myVersion > pVersion) {
                /* If the item was created after the required version */
                if (myHistory.getOriginalValues().getVersion() > pVersion) {
                    /* Remove from list */
                    myIterator.remove();
                    myChange.registerDeleted(myCurr);
                    continue;
                }

                /* Loop while version is too high */
                while (myVersion > pVersion) {
                    /* Pop history */
                    myHistory.popTheHistory();
                    myVersion = myHistory.getValueSet().getVersion();
                }

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
     * Fire event.
     * @param pEvent the event
     */
    protected void fireEvent(final MetisListChange<T> pEvent) {
        /* If the change is non-empty */
        if (!pEvent.isEmpty()) {
            theEventManager.fireEvent(pEvent.getEventType(), pEvent);
        }
    }

    /**
     * Create a New item representing a deletion.
     * @param pBase the base item
     * @return the new item
     */
    protected T newDiffDeletedItem(final T pBase) {
        /* Obtain a new item */
        T myItem = newListItem(pBase.getIndexedId());

        /* Obtain a deleted values set as the current value */
        MetisValueSet myBase = pBase.getValueSet();
        MetisValueSet mySet = myBase.cloneIt();
        mySet.setDeletion(true);

        /* Obtain an undeleted set as the base value */
        myBase = mySet.cloneIt();
        myBase.setDeletion(false);

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);
        myHistory.setHistory(myBase);

        /* Return the new item */
        return myItem;
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
        T myItem = newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisValueSet mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the value set as the base value */
        MetisValueSet myBase = pBase.getValueSet();
        myBase = myBase.cloneIt();

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);
        myHistory.setHistory(myBase);

        /* Return the new item */
        return myItem;
    }

    /**
     * Create a New "added" item for an update/difference list.
     * @param pCurr the current item
     * @return the new item
     */
    protected T newDiffAddedItem(final T pCurr) {
        /* Obtain a new item */
        T myItem = newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisValueSet mySet = pCurr.getValueSet();
        mySet = mySet.cloneIt();
        mySet.setVersion(1);

        /* Record as the history of the item */
        MetisValueSetHistory myHistory = myItem.getValueSetHistory();
        myHistory.setValues(mySet);

        /* Return the new item */
        return myItem;
    }

    /**
     * Create a New item for the list with the given id.
     * @param pId the id
     * @return the new item
     */
    protected T newListItem(final Integer pId) {
        /* Protect against exceptions */
        try {
            return theConstructor.newInstance(pId);
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            LOGGER.error("Failed to instantiate object", e);
            return null;
        }
    }
}
