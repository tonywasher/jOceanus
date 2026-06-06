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
package io.github.tonywasher.joceanus.tethys.api.control;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;

/**
 * Split Manager, hosting a Tree and HTML in a split window.
 *
 * @param <T> the item type
 */
public interface TethysUISplitTreeManager<T>
        extends OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain the Tree Manager.
     *
     * @return the tree manager
     */
    TethysUITreeManager<T> getTreeManager();

    /**
     * Obtain the HTML Manager.
     *
     * @return the HTML manager
     */
    TethysUIHTMLManager getHTMLManager();

    /**
     * Set control Pane.
     *
     * @param pPane the control Pane
     */
    void setControlPane(TethysUIComponent pPane);

    /**
     * Set weight.
     *
     * @param pWeight the weight
     */
    void setWeight(double pWeight);

    /**
     * Get weight.
     *
     * @return the weight
     */
    double getWeight();
}
