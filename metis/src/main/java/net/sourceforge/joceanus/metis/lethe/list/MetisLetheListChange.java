/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.lethe.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataIndexedItem;

/**
 * Metis VersionedList Event.
 * @param <T> the item type
 */
public class MetisLetheListChange<T extends MetisDataIndexedItem> {
    /**
     * The item type.
     */
    private final MetisLetheListKey theItemType;

    /**
     * The event type.
     */
    private final MetisLetheListEvent theEventType;

    /**
     * The version.
     */
    private int theVersion;

    /**
     * The items that are being added.
     */
    private List<T> theAdded;

    /**
     * The items that are being restored.
     */
    private List<T> theRestored;

    /**
     * The items that are being changed.
     */
    private List<T> theChanged;

    /**
     * The items that are being hidden.
     */
    private List<T> theHidden;

    /**
     * The items that are being deleted.
     */
    private List<T> theDeleted;

    /**
     * Constructor.
     * @param pItemType the item type
     * @param pEventType the event type
     */
    protected MetisLetheListChange(final MetisLetheListKey pItemType,
                                   final MetisLetheListEvent pEventType) {
        theItemType = pItemType;
        theEventType = pEventType;
    }

    /**
     * Obtain the item type.
     * @return the item type
     */
    public MetisLetheListKey getItemType() {
        return theItemType;
    }

    /**
     * Obtain the event type.
     * @return the event type
     */
    public MetisLetheListEvent getEventType() {
        return theEventType;
    }

    /**
     * Set the version.
     * @param pVersion the version
     */
    public void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Is this an empty changeSet?
     * @return true/false
     */
    public boolean isEmpty() {
        return !haveAdded()
               && !haveRestored()
               && !haveChanged()
               && !haveHidden()
               && !haveDeleted();
    }

    /**
     * Have we got any added items?
     * @return true/false
     */
    public boolean haveAdded() {
        return theAdded != null;
    }

    /**
     * Have we got any restored items?
     * @return true/false
     */
    public boolean haveRestored() {
        return theRestored != null;
    }

    /**
     * Have we got any changed items?
     * @return true/false
     */
    public boolean haveChanged() {
        return theChanged != null;
    }

    /**
     * Have we got any hidden items?
     * @return true/false
     */
    public boolean haveHidden() {
        return theHidden != null;
    }

    /**
     * Have we got any deleted items?
     * @return true/false
     */
    public boolean haveDeleted() {
        return theDeleted != null;
    }

    /**
     * Obtain the iterator for added items.
     * @return the iterator
     */
    public Iterator<T> addedIterator() {
        return theAdded != null
                                ? theAdded.iterator()
                                : Collections.emptyIterator();
    }

    /**
     * Obtain the iterator for restored items.
     * @return the iterator
     */
    public Iterator<T> restoredIterator() {
        return theRestored != null
                                   ? theRestored.iterator()
                                   : Collections.emptyIterator();
    }

    /**
     * Obtain the iterator for changed items.
     * @return the iterator
     */
    public Iterator<T> changedIterator() {
        return theChanged != null
                                  ? theChanged.iterator()
                                  : Collections.emptyIterator();
    }

    /**
     * Obtain the iterator for changed items.
     * @return the iterator
     */
    public Iterator<T> hiddenIterator() {
        return theHidden != null
                                 ? theHidden.iterator()
                                 : Collections.emptyIterator();
    }

    /**
     * Obtain the iterator for added items.
     * @return the iterator
     */
    public Iterator<T> deletedIterator() {
        return theDeleted != null
                                  ? theDeleted.iterator()
                                  : Collections.emptyIterator();
    }

    /**
     * Register added item.
     * @param pItem the item that was added
     */
    protected void registerAdded(final T pItem) {
        if (theAdded == null) {
            theAdded = new ArrayList<>();
        }
        theAdded.add(pItem);
    }

    /**
     * Register restored item.
     * @param pItem the item that was restored
     */
    protected void registerRestored(final T pItem) {
        if (theRestored == null) {
            theRestored = new ArrayList<>();
        }
        theRestored.add(pItem);
    }

    /**
     * Register changed item.
     * @param pItem the item that was changed
     */
    protected void registerChanged(final T pItem) {
        if (theChanged == null) {
            theChanged = new ArrayList<>();
        }
        theChanged.add(pItem);
    }

    /**
     * Register hidden item.
     * @param pItem the item that was hidden
     */
    protected void registerHidden(final T pItem) {
        if (theHidden == null) {
            theHidden = new ArrayList<>();
        }
        theHidden.add(pItem);
    }

    /**
     * Register deleted item.
     * @param pItem the item that was deleted
     */
    protected void registerDeleted(final T pItem) {
        if (theDeleted == null) {
            theDeleted = new ArrayList<>();
        }
        theDeleted.add(pItem);
    }
}
