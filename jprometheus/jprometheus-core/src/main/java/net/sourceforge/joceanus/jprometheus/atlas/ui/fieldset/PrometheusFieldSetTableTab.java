/* *****************************************************************************
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
package net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;

/**
 * FieldSet Table.
 * @param <T> the item type
 */
public class PrometheusFieldSetTableTab<T>
        implements PrometheusFieldSetPanel<T> {
    /**
     * Table interface.
     * @param <T> the item type
     */
    public interface PrometheusFieldSetTable<T>
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
         * Set editable item.
         * @param isEditable true/false
         */
        void setEditable(boolean isEditable);
    }

    /**
     * The panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The table.
     */
    private final PrometheusFieldSetTable<T> theTable;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pTable the table
     */
    PrometheusFieldSetTableTab(final TethysUIFactory<?> pFactory,
                               final PrometheusFieldSetTable<T> pTable) {
        /* Store the table */
        theTable = pTable;

        /* Create the panel */
        thePanel = pFactory.paneFactory().newBorderPane();
        thePanel.setCentre(theTable);
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public boolean isVisible() {
        return theTable.isVisible();
    }

    @Override
    public void setItem(final T pItem) {
        theTable.setItem(pItem);
    }

    @Override
    public void adjustChanged() {
        /* NoOp */
    }

    @Override
    public void setEditable(final boolean isEditable) {
        theTable.setEditable(isEditable);
    }

    @Override
    public void setEditable(final MetisDataFieldId pFieldId,
                            final boolean pEditable) {
        /* NoOp */
    }

    @Override
    public void setVisible(final MetisDataFieldId pFieldId,
                           final boolean pVisible) {
        /* NoOp */
    }
}
