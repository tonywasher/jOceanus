/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.control;

import net.sourceforge.joceanus.oceanus.date.OceanusDateConfig;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * General interfaces.
 */
public interface TethysUIControl {
    /**
     * DateButton Configuration.
     */
    interface TethysUIDateButton {
        /**
         * Set the dateConfig configurator.
         * @param pConfigurator the configurator
         */
        void setDateConfigurator(Consumer<OceanusDateConfig> pConfigurator);
    }

    /**
     * MapSet.
     * @param <T> the object type
     */
    interface TethysUIIconMapSet<T> {
        /**
         * Obtain the iconSize.
         * @return the iconSize
         */
        int getSize();

        /**
         * Clear the mapSet.
         */
        void clearMaps();

        /**
         * Set mappings for value with no icon.
         * @param pValue the value
         * @param pNext the next value for value
         * @param pTooltip the toolTip
         */
        default void setMappingsForValue(final T pValue,
                                         final T pNext,
                                         final String pTooltip) {
            setMappingsForValue(pValue, pNext, null, pTooltip);
        }

        /**
         * Set mappings for value with no toolTip.
         * @param pValue the value
         * @param pNext the next value for value
         * @param pId the mapped IconId
         */
        default void setMappingsForValue(final T pValue,
                                         final T pNext,
                                         final TethysUIIconId pId) {
            setMappingsForValue(pValue, pNext, pId, null);
        }

        /**
         * Set mappings for value.
         * @param pValue the value
         * @param pNext the next value for value
         * @param pId the mapped IconId
         * @param pTooltip the toolTip
         */
        void setMappingsForValue(T pValue,
                                 T pNext,
                                 TethysUIIconId pId,
                                 String pTooltip);

        /**
         * Obtain Icon for value.
         * @param pValue the value
         * @return the value
         */
        TethysUIIconId getIconForValue(T pValue);

        /**
         * Obtain ToolTip for value.
         * @param pValue the value
         * @return the value
         */
        String getTooltipForValue(T pValue);

        /**
         * Obtain Next value for value.
         * @param pValue the value
         * @return the value
         */
        T getNextValueForValue(T pValue);
    }

    /**
     * IconButton Configuration.
     * @param <T> the data type
     */
    interface TethysUIIconButton<T> {
        /**
         * Set the IconMapSet supplier.
         * @param pSupplier the supplier
         */
        void setIconMapSet(Supplier<TethysUIIconMapSet<T>> pSupplier);
    }

    /**
     * Scroll Button Configuration.
     * @param <T> the value type
     */
    interface TethysUIScrollButton<T> {
        /**
         * Set the menu configurator.
         * @param pConfigurator the configurator
         */
        void setMenuConfigurator(Consumer<TethysUIScrollMenu<T>> pConfigurator);
    }

    /**
     * List Button Configuration.
     * @param <T> the value type
     */
    interface TethysUIListButton<T extends Comparable<? super T>> {
        /**
         * Set the selectable supplier.
         * @param pSelectables the supplier
         */
        void setSelectables(Supplier<Iterator<T>> pSelectables);
    }
}
