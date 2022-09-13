/* *****************************************************************************
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
package net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset;

import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

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
            extends TethysComponent {
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
    private final TethysBorderPaneManager thePanel;

    /**
     * The table.
     */
    private final PrometheusFieldSetTable<T> theTable;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pTable the table
     */
    PrometheusFieldSetTableTab(final TethysGuiFactory pFactory,
                               final PrometheusFieldSetTable<T> pTable) {
        /* Store the table */
        theTable = pTable;

        /* Create the panel */
        thePanel = pFactory.newBorderPane();
        thePanel.setCentre(theTable);
    }

    @Override
    public TethysNode getNode() {
        return thePanel.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
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
    public void setEditable(final PrometheusDataFieldId pFieldId,
                            final boolean pEditable) {
        /* NoOp */
    }

    @Override
    public void setVisible(final PrometheusDataFieldId pFieldId,
                           final boolean pVisible) {
        /* NoOp */
    }
}
