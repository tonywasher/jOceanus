/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.tethys.api.control;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIcon;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;

/**
 * Tree Manager.
 *
 * @param <T> the item type
 */
public interface TethysUITreeManager<T>
        extends OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain the selected item.
     *
     * @return the item.
     */
    T getSelectedItem();

    /**
     * Set the root as visible.
     */
    void setRootVisible();

    /**
     * Obtain the root.
     *
     * @return the root
     */
    TethysUITreeItem<T> getRoot();

    /**
     * Set the root name.
     *
     * @param pName the root name
     */
    void setRootName(String pName);

    /**
     * Is the tree visible?
     *
     * @return true/false
     */
    boolean isVisible();

    /**
     * Select and display item.
     *
     * @param pName the name of the item
     * @return was an item selected? true/false
     */
    boolean lookUpAndSelectItem(String pName);

    /**
     * LookUp item by name.
     *
     * @param pName the name of the item
     * @return the item (or null)
     */
    TethysUITreeItem<T> lookUpItem(String pName);

    /**
     * Add item to root.
     *
     * @param pName the name
     * @param pItem the item
     * @return the new tree item
     */
    TethysUITreeItem<T> addRootItem(String pName,
                                    T pItem);

    /**
     * Add item to parent.
     *
     * @param pParent the parent
     * @param pName   the name
     * @param pItem   the item
     * @return the new tree item
     */
    TethysUITreeItem<T> addChildItem(TethysUITreeItem<T> pParent,
                                     String pName,
                                     T pItem);

    /**
     * Obtain the icon for the iconId.
     *
     * @param pIconId the iconId
     * @return the icon
     */
    TethysUIIcon getIcon(TethysUIIconId pIconId);

    /**
     * TreeItem class.
     *
     * @param <T> the data type
     */
    interface TethysUITreeItem<T> {
        /**
         * Obtain the item.
         *
         * @return the item
         */
        T getItem();

        /**
         * Obtain the iconId.
         *
         * @return the iconId
         */
        TethysUIIconId getIconId();

        /**
         * Obtain the unique name.
         *
         * @return the name
         */
        String getName();

        /**
         * Obtain the parent.
         *
         * @return the parent
         */
        TethysUITreeItem<T> getParent();

        /**
         * Obtain the tree.
         *
         * @return the tree
         */
        TethysUITreeManager<T> getTree();

        /**
         * Is the item visible?
         *
         * @return true/false
         */
        boolean isVisible();

        /**
         * Is the item root?
         *
         * @return true/false
         */
        boolean isRoot();

        /**
         * Set the item.
         *
         * @param pItem the item
         */
        void setItem(T pItem);

        /**
         * Set the iconId.
         *
         * @param pIconId the iconId
         */
        void setIcon(TethysUIIconId pIconId);

        /**
         * Set the visibility of the item.
         *
         * @param pVisible true/false
         */
        void setVisible(boolean pVisible);

        /**
         * Do we have children?
         *
         * @return true/false
         */
        boolean hasChildren();

        /**
         * Count previous visible items.
         *
         * @return the count
         */
        int countPreviousVisibleSiblings();

        /**
         * Remove all children.
         */
        void removeChildren();

        /**
         * Set focus to this item.
         */
        void setFocus();
    }
}
