/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.data;


import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldItemType;

/**
 * ListKey.
 */
public interface PrometheusListKey
    extends MetisFieldItemType {
    /**
     * Obtain the item key.
     * @return the item key
     */
    Integer getItemKey();

    /**
     * Obtain the list name.
     * @return the list name
     */
    String getListName();

    /**
     * Obtain the item name.
     * @return the item name
     */
    String getItemName();
}
