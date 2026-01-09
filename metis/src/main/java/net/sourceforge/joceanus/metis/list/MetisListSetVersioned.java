/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.metis.list;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Versioned ListSet.
 */
public class MetisListSetVersioned
            implements MetisFieldItem, OceanusEventProvider<MetisListEvent> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MetisListSetVersioned> FIELD_DEFS = MetisFieldSet.newFieldSet(MetisListSetVersioned.class);

    /*
     * Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_TYPE, MetisListSetVersioned::getStyle);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_VERSION, MetisListSetVersioned::getVersion);
        FIELD_DEFS.declareLocalField(MetisListResource.FIELD_BASE, MetisListSetVersioned::getBase);
    }

    /**
     * The Local fields.
     */
    private final MetisFieldSet<MetisListSetVersioned> theFields;

    /**
     * The listSetType.
     */
    private final MetisListStyle theStyle;

    /**
     * The base listSet.
     */
    private final MetisListSetVersioned theBase;

    /**
     * The versionedList Map.
     */
    private final Map<MetisListKey, MetisListVersioned<? extends MetisFieldVersionedItem>> theListMap;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<MetisListEvent> theEventManager;

    /**
     * The version of the listSet.
     */
    private int theVersion;

    /**
     * Constructor.
     */
    public MetisListSetVersioned() {
        this(MetisListStyle.BASE);
    }

    /**
     * Constructor.
     * @param pStyle the listSet Style
     */
    public MetisListSetVersioned(final MetisListStyle pStyle) {
        this(pStyle, null);
    }

    /**
     * Constructor.
     * @param pStyle the listSet style
     * @param pBase the base listSet
     */
    public MetisListSetVersioned(final MetisListStyle pStyle,
                                 final MetisListSetVersioned pBase) {
        /* Store parameters */
        theStyle = pStyle;
        theBase = pBase;

        /* Create the new fieldSet */
        theFields = MetisFieldSet.newFieldSet(this);

        /* Create the listMap */
        theListMap = new LinkedHashMap<>();

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theFields;
    }

    @Override
    public OceanusEventRegistrar<MetisListEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getDataFieldSet().getName();
    }

    /**
     * Obtain the style.
     * @return the style
     */
    public MetisListStyle getStyle() {
        return theStyle;
    }

    /**
     * Obtain the base listSet.
     * @return the base listSet
     */
    public MetisListSetVersioned getBase() {
        return theBase;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Set version.
     * @param pVersion the version
     */
    protected void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Obtain the key iterator.
     * @return the iterator
     */
    public Iterator<MetisListKey> keyIterator() {
        return theListMap.keySet().iterator();
    }

    /**
     * Obtain the reverse key iterator.
     * @return the iterator
     */
    public Iterator<MetisListKey> reverseKeyIterator() {
        final List<MetisListKey> myList = new ArrayList<>(theListMap.keySet());
        return new MetisListReverseIterator<>(myList.listIterator(myList.size()));
    }

    /**
     * Obtain the List iterator.
     * @return the iterator
     */
    public Iterator<MetisListVersioned<? extends MetisFieldVersionedItem>> listIterator() {
        return theListMap.values().iterator();
    }

    /**
     * Declare list.
     * @param pItemType the itemType for the list
     * @param pList the list
     */
    protected void declareList(final MetisListKey pItemType,
                               final MetisListVersioned<? extends MetisFieldVersionedItem> pList) {
        /* Add to the maps */
        theListMap.put(pItemType, pList);

        /* Create the DataField */
        theFields.declareLocalField(pItemType.getListName(), k -> pList);
    }

    /**
     * Obtain the relevant list.
     * @param <L> the list type
     * @param pListKey the list key
     * @param pClazz the list class
     * @return the list (or null)
     */
    public <L extends MetisListVersioned<?>> L getList(final MetisListKey pListKey,
                                                       final Class<L> pClazz) {
        return pClazz.cast(theListMap.get(pListKey));
    }

    /**
     * Is this an empty listSet?
     * @return true/false
     */
    public boolean isEmpty() {
        /* Loop through the lists */
        for (MetisListVersioned<?> myList : theListMap.values()) {
            if (!myList.isEmpty()) {
                return false;
            }
        }

        /* All lists are empty */
        return true;
    }

    /**
     * derive differences.
     * @param pListSet the listSet to compare against
     * @return the differences
     */
    public MetisListSetVersioned deriveDifferences(final MetisListSetVersioned pListSet) {
        /* Create the listSet */
        final MetisListSetVersioned myDiffSet = new MetisListSetVersioned(MetisListStyle.DIFFER);

        /* Loop through all the lists */
        for (MetisListKey myKey : theListMap.keySet()) {
            /* Rebase the list */
            final MetisListVersioned<?> myList = getList(myKey, MetisListVersioned.class);
            final MetisListVersioned<?> myBase = pListSet.getList(myKey, MetisListVersioned.class);
            myList.deriveDifferences(myDiffSet, myBase);
        }

        /* Update version if needed */
        if (!myDiffSet.isEmpty()) {
            myDiffSet.setVersion(1);
        }

        /* Return the listSet */
        return myDiffSet;
    }

    /**
     * derive updates.
     * @return the updates
     */
    public MetisListSetVersioned deriveUpdates() {
        /* Create the listSete */
        final MetisListSetVersioned myUpdateSet = new MetisListSetVersioned(MetisListStyle.UPDATE);

        /* Loop through all the lists */
        for (MetisListKey myKey : theListMap.keySet()) {
            /* Rebase the list */
            final MetisListVersioned<?> myList = getList(myKey, MetisListVersioned.class);
            myList.deriveUpdates(myUpdateSet);
        }

        /* Update version if needed */
        if (!myUpdateSet.isEmpty()) {
            myUpdateSet.setVersion(1);
        }

        /* Return the listSet */
        return myUpdateSet;
    }

    /**
     * Condense history.
     * @param pNewVersion the new maximum version
     */
    public void condenseHistory(final int pNewVersion) {
        /* Loop through all the lists */
        for (MetisListVersioned<?> myList : theListMap.values()) {
            /* condense history for the list */
            myList.condenseHistory(pNewVersion);
        }

        /* Record the new version */
        setVersion(pNewVersion);
    }

    /**
     * Re-base the listSet against a database image. This method is used to re-synchronise between two
     * sources. Items that are in this listSet, but not in the base listSet will be viewed as inserted.
     * Items that are in the base listSet but not in this listSet will be viewed as deleted. Items
     * that are in both listSets but differ will be viewed as changed.
     * @param pBase the base listSet
     */
    public void reBase(final MetisListSetVersioned pBase) {
        /* Note whether we have any changes */
        boolean bChanges = false;

        /* Loop through all the lists */
        for (MetisListKey myKey : theListMap.keySet()) {
            /* Rebase the list */
            final MetisListVersioned<?> myList = getList(myKey, MetisListVersioned.class);
            final MetisListVersioned<?> myBase = pBase.getList(myKey, MetisListVersioned.class);
            bChanges |= myList.reBase(myBase);
        }

        /* If we have changes, set Version to 1 */
        if (bChanges) {
            setVersion(1);

            /* else reset all list versions to zero */
        } else {
            for (MetisListVersioned<?> myList : theListMap.values()) {
                myList.setVersion(0);
            }
        }
    }

    /**
     * deriveChanges on version increment.
     */
    public void deriveChangesOnIncrement() {
        /* Create the listSetChange */
        final MetisListSetChange myEvent = new MetisListSetChange(theVersion);

        /* Loop through all the lists */
        for (MetisListVersioned<?> myList : theListMap.values()) {
            final MetisListChange<?> myChange = myList.deriveChangesOnIncrement();
            if (myChange != null) {
                myEvent.registerChangedList(myChange);
            }
        }

        /* report the change */
        fireEvent(myEvent);
    }

    /**
     * Rewind items to the required version.
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* Loop through all the lists */
        for (MetisListVersioned<?> myList : theListMap.values()) {
            myList.rewindToVersion(pVersion);
        }

        /* Adjust version */
        setVersion(pVersion);

        /* report the change */
        fireEvent(new MetisListSetChange(MetisListEvent.REWIND));
    }

    /**
     * Fire event.
     * @param pEvent the event
     */
    private void fireEvent(final MetisListSetChange pEvent) {
        /* If the change is non-empty */
        if (!pEvent.getEventType().hasContent()
                || !pEvent.isEmpty()) {
            theEventManager.fireEvent(pEvent.getEventType(), pEvent);
        }
    }
}
