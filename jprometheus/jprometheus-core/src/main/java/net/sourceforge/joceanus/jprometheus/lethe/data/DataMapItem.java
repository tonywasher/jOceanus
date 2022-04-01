/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.data;

/**
 * Interface for a Data Instance Map.
 * @author Tony Washer
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public interface DataMapItem<T extends DataItem<E>, E extends Enum<E>> {
    /**
     * adjust maps for item.
     * @param pItem the item to map
     */
    void adjustForItem(T pItem);

    /**
     * Reset the map.
     */
    void resetMap();
}
