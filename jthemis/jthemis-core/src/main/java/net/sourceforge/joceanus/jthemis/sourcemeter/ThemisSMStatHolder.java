/* *****************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jthemis.sourcemeter;

import java.util.Iterator;
import java.util.Map;

/**
 * SourceMeter stat holder.
 */
public interface ThemisSMStatHolder {
    /**
     * Obtain the id.
     *
     * @return the Id
     */
    String getId();

    /**
     * Obtain the name.
     *
     * @return the name
     */
    String getName();

    /**
     * Obtain the statistic map.
     *
     * @return the map
     */
    Map<ThemisSMStat, Integer> getStatistics();

    /**
     * Register Child.
     *
     * @param pChild the child
     */
    void registerChild(ThemisSMStatHolder pChild);

    /**
     * Set parent.
     *
     * @param pParent the parent
     */
    void setParent(ThemisSMStatHolder pParent);

    /**
     * Set statistic.
     *
     * @param pStat  the statistic
     * @param pValue the value of the statistic.
     */
    void setStatistic(ThemisSMStat pStat,
                      Integer pValue);

    /**
     * Obtain the child iterator.
     *
     * @return the iterator
     */
    Iterator<ThemisSMStatHolder> childIterator();
}
