/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.button;

import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;

/**
 * Button Factory.
 * @param <C> the color
 */
public interface TethysUIButtonFactory<C> {
    /**
     * Obtain a new button.
     * @return the new button
     */
    TethysUIButton newButton();

    /**
     * Obtain a new date button.
     * @return the new date button
     */
    TethysUIDateButtonManager newDateButton();

    /**
     * Obtain a new dateRange selector.
     * @return the new selector
     */
    default TethysUIDateRangeSelector newDateRangeSelector() {
        return newDateRangeSelector(false);
    }

    /**
     * Obtain a new dateRange selector.
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     * @return the new selector
     */
    TethysUIDateRangeSelector newDateRangeSelector(boolean pBaseIsStart);

    /**
     * Obtain a new icon button manager.
     * @param <T> the item type
     * @param pClazz the item class
     * @return the new manager
     */
    <T> TethysUIIconButtonManager<T> newIconButton(Class<T> pClazz);

    /**
     * Obtain a new icon MapSet.
     * @param <T> the item type
     * @return the new mapSet
     */
    <T> TethysUIIconMapSet<T> newIconMapSet();

    /**
     * Obtain a new icon MapSet.
     * @param <T> the item type
     * @param pWidth the icon width for the map set
     * @return the new mapSet
     */
    <T> TethysUIIconMapSet<T> newIconMapSet(final int pWidth);

    /**
     * Obtain a new scroll button manager.
     * @param <T> the item type
     * @param pClazz the item class
     * @return the new manager
     */
    <T> TethysUIScrollButtonManager<T> newScrollButton(Class<T> pClazz);

    /**
     * Obtain a new list button manager.
     * @param <T> the item type
     * @return the new manager
     */
    <T extends Comparable<T>> TethysUIListButtonManager<T> newListButton();

    /**
     * Obtain a new colorPicker.
     * @return the new picker
     */
    TethysUIColorPicker<C> newColorPicker();
}
