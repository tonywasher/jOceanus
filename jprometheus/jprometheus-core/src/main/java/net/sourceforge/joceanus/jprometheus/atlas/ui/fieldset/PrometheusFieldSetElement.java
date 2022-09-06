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

import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;

/**
 * FieldSet Element.
 * @param <F> the fieldId type
 * @param <V> the value type
 */
public class PrometheusFieldSetElement<F, V> {
    /**
     * The fieldId.
     */
    private final F theFieldId;

    /**
     * The label.
     */
    private final TethysLabel theLabel;

    /**
     * The editField.
     */
    private final TethysDataEditField<V> theField;

    /**
     * The element.
     */
    private final TethysBorderPaneManager theElement;

    /**
     * Is the panel visible?.
     */
    private boolean isVisible;

    /**
     * Constructor.
     * @param pFactory the gui factory.
     * @param pFieldId the fieldId
     * @param pField the field
     */
    PrometheusFieldSetElement(final TethysGuiFactory pFactory,
                              final F pFieldId,
                              final TethysDataEditField<V> pField) {
        /* Store field and id */
        theFieldId = pFieldId;
        theField = pField;

        /* Create the label */
        theLabel = pFactory.newLabel(theFieldId.toString() + ":");
        theLabel.setAlignment(TethysAlignment.WEST);

        /* Create the element */
        theElement = pFactory.newBorderPane();
        theElement.setWest(theLabel);
        theElement.setCentre(theField);
        isVisible = true;
    }

    /**
     * Obtain the fieldId.
     * @return the fieldId
     */
    F getFieldId() {
        return theFieldId;
    }

    /**
     * Obtain the component.
     * @return the component
     */
    TethysComponent getComponent() {
        return theElement;
    }

    /**
     * Set value.
     * @param pValue the value
     */
    void setValue(final Object pValue) {
        theField.setValue(theField.getCastValue(pValue));
    }

    /**
     * Set editable.
     * @param pEditable true/false
     */
    void setEditable(final boolean pEditable) {
        theField.setEditable(pEditable);
    }

    /**
     * Set visible.
     * @param pVisible true/false
     */
    void setVisible(final boolean pVisible) {
        theElement.setVisible(pVisible);
        isVisible = pVisible;
    }

    /**
     * Is the element visible.
     * @return true/false
     */
    boolean isVisible() {
        return isVisible;
    }

    /**
     * Obtain the label width.
     * @return the label width
     */
    int getLabelWidth() {
        return theLabel.getWidth();
    }

    /**
     * Set the label width.
     * @param pWidth the label width
     */
    void setLabelWidth(final int pWidth) {
        theLabel.setPreferredWidth(pWidth);
    }
}
