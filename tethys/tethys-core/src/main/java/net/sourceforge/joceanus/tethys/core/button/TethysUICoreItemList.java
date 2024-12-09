/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.core.button;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Selectable item list.
 * @param <T> the item type.
 */
public class TethysUICoreItemList<T extends Comparable<? super T>> {
    /**
     * List of items.
     */
    private final List<TethysUICoreItem<T>> theList;

    /**
     * Constructor.
     */
    TethysUICoreItemList() {
        theList = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pSource the source list
     */
    TethysUICoreItemList(final TethysUICoreItemList<T> pSource) {
        /* Initialise */
        this();

        /* Iterate through the source list */
        final Iterator<TethysUICoreItem<T>> myIterator = pSource.iterator();
        while (myIterator.hasNext()) {
            final TethysUICoreItem<T> myItem = myIterator.next();

            /* Copy the item */
            theList.add(new TethysUICoreItem<>(myItem));
        }
    }

    /**
     * Set SelectableItem.
     * @param pItem the item
     */
    public void setSelectableItem(final T pItem) {
        theList.add(new TethysUICoreItem<>(pItem));
    }

    /**
     * Set SelectedItem.
     * @param pItem the item
     */
    public void setSelectedItem(final T pItem) {
        theList.add(new TethysUICoreItem<>(pItem, true));
    }

    /**
     * Obtain the number of items.
     * @return the number of items
     */
    public int size() {
        return theList.size();
    }

    /**
     * Clear Items.
     */
    public void clearItems() {
        theList.clear();
    }

    /**
     * Clear non-Selected Items.
     */
    public void clearNonSelectedItems() {
        final Iterator<TethysUICoreItem<T>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final TethysUICoreItem<T> myItem = myIterator.next();
            if (!myItem.isSelected()) {
                myIterator.remove();
            }
        }
    }

    /**
     * Select Item.
     * @param pItem the item
     */
    public void selectItem(final T pItem) {
        final TethysUICoreItem<T> myItem = locateItem(pItem);
        if (myItem != null) {
            myItem.setSelected(true);
        }
    }

    /**
     * Toggle Item.
     * @param pItem the item
     */
    public void toggleItem(final T pItem) {
        final TethysUICoreItem<T> myItem = locateItem(pItem);
        if (myItem != null) {
            myItem.setSelected(!myItem.isSelected());
        }
    }

    /**
     * Clear Item.
     * @param pItem the item
     */
    public void clearItem(final T pItem) {
        final TethysUICoreItem<T> myItem = locateItem(pItem);
        if (myItem != null) {
            myItem.setSelected(false);
        }
    }

    /**
     * Obtain list iterator.
     * @return the iterator
     */
    public Iterator<TethysUICoreItem<T>> iterator() {
        return theList.iterator();
    }

    /**
     * Sort the list.
     */
    public void sortList() {
        theList.sort(null);
    }

    /**
     * Locate item in list.
     * @param pItem the item
     * @return the list item or null
     */
    protected TethysUICoreItem<T> locateItem(final T pItem) {
        final Iterator<TethysUICoreItem<T>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final TethysUICoreItem<T> myItem = myIterator.next();
            if (pItem.equals(myItem.getItem())) {
                return myItem;
            }
        }
        return null;
    }

    /**
     * Obtain difference list.
     * @param pNewList the new list
     * @return the differences
     */
    public TethysUICoreItemList<T> getDifferences(final TethysUICoreItemList<T> pNewList) {
        /* Create the new list */
        final TethysUICoreItemList<T> myList = new TethysUICoreItemList<>();

        /* Loop through the lists */
        final Iterator<TethysUICoreItem<T>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final TethysUICoreItem<T> myItem = myIterator.next();

            /* Look up new value */
            final TethysUICoreItem<T> myNew = pNewList.locateItem(myItem.getItem());
            if (myNew == null) {
                throw new IllegalStateException();
            }
            if (myNew.isSelected() != myItem.isSelected()) {
                myList.theList.add(myNew);
            }
        }

        /* Return the list of differences */
        return myList;
    }

    @Override
    public String toString() {
        final StringBuilder myBuilder = new StringBuilder();
        final Iterator<TethysUICoreItem<T>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final TethysUICoreItem<T> myItem = myIterator.next();
            if (myItem.isSelected()) {
                if (myBuilder.length() > 0) {
                    myBuilder.append(',');
                }
                myBuilder.append(myItem);
            }
        }
        return myBuilder.toString();
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

        /* Make sure that the object is the same class */
        if (!(pThat instanceof TethysUICoreItemList)) {
            return false;
        }

        /* Check for equality */
        final TethysUICoreItemList<?> myThat = (TethysUICoreItemList<?>) pThat;
        return theList.equals(myThat.theList);
    }

    @Override
    public int hashCode() {
        return theList.hashCode();
    }

    /**
     * Item class.
     * @param <T> the item type.
     */
    static final class TethysUICoreItem<T extends Comparable<? super T>>
            implements Comparable<TethysUICoreItem<T>> {
        /**
         * The Item.
         */
        private final T theItem;

        /**
         * Is it selected?
         */
        private boolean isSelected;

        /**
         * Constructor.
         * @param pItem the item
         */
        private TethysUICoreItem(final T pItem) {
            this(pItem, false);
        }

        /**
         * Constructor.
         * @param pSource the source item
         */
        TethysUICoreItem(final TethysUICoreItem<T> pSource) {
            this(pSource.getItem(), pSource.isSelected());
        }

        /**
         * Constructor.
         * @param pItem the item
         * @param pSelected is the item initially selected?
         */
        private TethysUICoreItem(final T pItem,
                           final boolean pSelected) {
            theItem = pItem;
            isSelected = pSelected;
        }

        /**
         * Obtain the item.
         * @return the item
         */
        public T getItem() {
            return theItem;
        }

        /**
         * Is the item selected?
         * @return true/false
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * Set the item as selected.
         * @param pSelected is the item selected?
         */
        public void setSelected(final boolean pSelected) {
            isSelected = pSelected;
        }

        @Override
        public String toString() {
            return theItem.toString();
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

            /* Make sure that the object is the same class */
            if (!(pThat instanceof TethysUICoreItem)) {
                return false;
            }

            /* Check for equality */
            final TethysUICoreItem<?> myThat = (TethysUICoreItem<?>) pThat;
            return theItem.equals(myThat.getItem())
                    && isSelected == myThat.isSelected();
        }

        @Override
        public int hashCode() {
            return theItem.hashCode();
        }

        @Override
        public int compareTo(final TethysUICoreItem<T> pThat) {
            return theItem.compareTo(pThat.getItem());
        }
    }
}

