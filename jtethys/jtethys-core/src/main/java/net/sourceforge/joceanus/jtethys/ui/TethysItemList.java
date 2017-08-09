/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Selectable item list.
 * @param <T> the item type.
 */
public class TethysItemList<T> {
    /**
     * List of items.
     */
    private final List<TethysItem<T>> theList;

    /**
     * Constructor.
     */
    public TethysItemList() {
        theList = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pSource the source list
     */
    public TethysItemList(final TethysItemList<T> pSource) {
        /* Initialise */
        this();

        /* Iterate through the source list */
        final Iterator<TethysItem<T>> myIterator = pSource.iterator();
        while (myIterator.hasNext()) {
            final TethysItem<T> myItem = myIterator.next();

            /* Copy the item */
            theList.add(new TethysItem<>(myItem));
        }
    }

    /**
     * Set SelectableItem.
     * @param pItem the item
     */
    public void setSelectableItem(final T pItem) {
        theList.add(new TethysItem<>(pItem));
    }

    /**
     * Set SelectedItem.
     * @param pItem the item
     */
    public void setSelectedItem(final T pItem) {
        theList.add(new TethysItem<>(pItem, true));
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
     * Select Item.
     * @param pItem the item
     */
    public void selectItem(final T pItem) {
        final TethysItem<T> myItem = locateItem(pItem);
        if (myItem != null) {
            myItem.setSelected(true);
        }
    }

    /**
     * Toggle Item.
     * @param pItem the item
     */
    public void toggleItem(final T pItem) {
        final TethysItem<T> myItem = locateItem(pItem);
        if (myItem != null) {
            myItem.setSelected(!myItem.isSelected());
        }
    }

    /**
     * Clear Item.
     * @param pItem the item
     */
    public void clearItem(final T pItem) {
        final TethysItem<T> myItem = locateItem(pItem);
        if (myItem != null) {
            myItem.setSelected(false);
        }
    }

    /**
     * Obtain list iterator.
     * @return the iterator
     */
    public Iterator<TethysItem<T>> iterator() {
        return theList.iterator();
    }

    /**
     * Locate item in list.
     * @param pItem the item
     * @return the list item or null
     */
    private TethysItem<T> locateItem(final T pItem) {
        final Iterator<TethysItem<T>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final TethysItem<T> myItem = myIterator.next();
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
    public TethysItemList<T> getDifferences(final TethysItemList<T> pNewList) {
        /* Create the new list */
        final TethysItemList<T> myList = new TethysItemList<>();

        /* Loop through the lists */
        final Iterator<TethysItem<T>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final TethysItem<T> myItem = myIterator.next();

            /* Look up new value */
            final TethysItem<T> myNew = pNewList.locateItem(myItem.getItem());
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
        final Iterator<TethysItem<T>> myIterator = iterator();
        while (myIterator.hasNext()) {
            final TethysItem<T> myItem = myIterator.next();
            if (myItem.isSelected()) {
                if (myBuilder.length() > 0) {
                    myBuilder.append(',');
                }
                myBuilder.append(myItem.toString());
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
        if (!TethysItemList.class.isInstance(pThat)) {
            return false;
        }

        /* Check for equality */
        final TethysItemList<?> myThat = (TethysItemList<?>) pThat;
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
    public static final class TethysItem<T> {
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
        private TethysItem(final T pItem) {
            this(pItem, false);
        }

        /**
         * Constructor.
         * @param pSource the source item
         */
        private TethysItem(final TethysItem<T> pSource) {
            this(pSource.getItem(), pSource.isSelected());
        }

        /**
         * Constructor.
         * @param pItem the item
         * @param pSelected is the item initially selected?
         */
        private TethysItem(final T pItem,
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
            if (!TethysItem.class.isInstance(pThat)) {
                return false;
            }

            /* Check for equality */
            final TethysItem<?> myThat = (TethysItem<?>) pThat;
            return theItem.equals(myThat.getItem())
                   && isSelected == myThat.isSelected();
        }

        @Override
        public int hashCode() {
            return theItem.hashCode();
        }
    }
}
