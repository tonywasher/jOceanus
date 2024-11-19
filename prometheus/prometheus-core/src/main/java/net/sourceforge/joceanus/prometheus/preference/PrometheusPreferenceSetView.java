/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.preference;

import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet.MetisPreferenceItem;
import net.sourceforge.joceanus.metis.ui.MetisPreferenceSetView;
import net.sourceforge.joceanus.prometheus.preference.PrometheusPreferenceSet.PrometheusByteArrayPreference;
import net.sourceforge.joceanus.prometheus.preference.PrometheusPreferenceSet.PrometheusCharArrayPreference;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIFieldAttribute;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIGridPaneManager;

/**
 * Panel for editing a preference Set.
 */
public class PrometheusPreferenceSetView
    extends MetisPreferenceSetView {
    /**
     * Constructor.
     *
     * @param pFactory       the GUI factory
     * @param pPreferenceSet the preference set
     */
    PrometheusPreferenceSetView(final TethysUIFactory<?> pFactory,
                                final PrometheusPreferenceSet pPreferenceSet) {
        super(pFactory, pPreferenceSet);
    }

    @Override
    protected PreferenceElement allocatePreferenceElement(final MetisPreferenceItem pItem) {
        if (pItem instanceof PrometheusCharArrayPreference) {
            return new CharArrayPreferenceElement((PrometheusCharArrayPreference) pItem);
        } else if (pItem instanceof PrometheusByteArrayPreference) {
            return null;
        } else {
            return super.allocatePreferenceElement(pItem);
        }
    }

    /**
     * CharArray preference element.
     */
    private final class CharArrayPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final PrometheusCharArrayPreference theItem;

        /**
         * The Field item.
         */
        private final TethysUICharArrayEditField theField;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        CharArrayPreferenceElement(final PrometheusCharArrayPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = getFactory().fieldFactory().newCharArrayField();
            theField.setEditable(true);

            /* Create the label */
            final TethysUILabel myLabel = getFactory().controlFactory().newLabel(pItem.getDisplay() + STR_COLON);
            myLabel.setAlignment(TethysUIAlignment.EAST);

            /* Add to the Grid Pane */
            final TethysUIGridPaneManager myGrid = getGrid();
            myGrid.addCell(myLabel);
            myGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
            myGrid.addCell(theField);
            myGrid.setCellColumnSpan(theField, 2);
            myGrid.allowCellGrowth(theField);
            myGrid.newRow();

            /* Create listener */
            theField.getEventRegistrar().addEventListener(e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            theField.setEnabled(!theItem.isHidden());
        }
    }
}
