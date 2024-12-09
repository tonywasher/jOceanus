/*******************************************************************************
 * Tethys: GUI Utilities
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
package net.sourceforge.joceanus.tethys.api.menu;

/**
 * ScrollMenu Toggle Item.
 * @param <T> the value type
 */
public interface TethysUIScrollToggle<T>
        extends TethysUIScrollItem<T> {
    /**
     * is the item selected?
     * @return true/false
     */
    boolean isSelected();

    /**
     * Set selection status.
     * @param pSelected true/false
     */
    void setSelected(boolean pSelected);

    /**
     * Toggle selected status.
     */
    void toggleSelected();
}
