/* *****************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.prometheus.ui.fieldset;

import java.util.function.Consumer;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;

/**
 * FieldSet Panel.
 * @param <T> the item type
 */
public interface PrometheusFieldSetPanel<T>
        extends TethysUIComponent {
    /**
     * Are there any visible elements?
     * @return true/false
     */
    boolean isVisible();

    /**
     * Set item.
     * @param pItem the item
     */
    void setItem(T pItem);

    /**
     * Adjust changed indications.
     */
    void adjustChanged();

    /**
     * Set editable item.
     * @param isEditable true/false
     */
    void setEditable(boolean isEditable);

    /**
     * Set editable.
     * @param pFieldId the fieldId
     * @param pEditable true/false
     */
    void setEditable(MetisDataFieldId pFieldId,
                     boolean pEditable);

    /**
     * Set visible.
     * @param pFieldId the fieldId
     * @param pVisible true/false
     */
    void setVisible(MetisDataFieldId pFieldId,
                    boolean pVisible);

    /**
     * Adjust the label width.
     */
    default void adjustLabelWidth() {
        /* NoOp */
    }

    /**
     * Set reporter.
     * @param pReporter the reporter
     */
    void setReporter(Consumer<String> pReporter);
}
