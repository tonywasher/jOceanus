/* *****************************************************************************
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

package net.sourceforge.joceanus.jprometheus.ui.fieldset;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;

/**
 * FieldSet Element.
 * @param <V> the value type
 */
public class PrometheusFieldSetElement<V> {
    /**
     * The fieldId.
     */
    private final MetisDataFieldId theFieldId;

    /**
     * The label.
     */
    private final TethysUILabel theLabel;

    /**
     * The editField.
     */
    private final TethysUIDataEditField<V> theField;

    /**
     * The element.
     */
    private final TethysUIBorderPaneManager theElement;

    /**
     * Is the panel visible?.
     */
    private boolean isVisible;

    /**
     * Constructor.
     *
     * @param pFactory the gui factory.
     * @param pFieldId the fieldId
     * @param pField   the field
     */
    PrometheusFieldSetElement(final TethysUIFactory<?> pFactory,
                              final MetisDataFieldId pFieldId,
                              final TethysUIDataEditField<V> pField) {
        /* Store field and id */
        theFieldId = pFieldId;
        theField = pField;

        /* Create the label */
        theLabel = pFactory.controlFactory().newLabel(theFieldId.toString() + ":");
        theLabel.setAlignment(TethysUIAlignment.EAST);

        /* Create border pane to centre the label */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        final TethysUIBorderPaneManager myPane = myPanes.newBorderPane();
        myPane.setCentre(theLabel);

        /* Create the element */
        theElement = myPanes.newBorderPane();
        theElement.setWest(myPane);
        theElement.setCentre(theField);
        isVisible = true;
    }

    /**
     * Obtain the fieldId.
     *
     * @return the fieldId
     */
    MetisDataFieldId getFieldId() {
        return theFieldId;
    }

    /**
     * Obtain the component.
     *
     * @return the component
     */
    TethysUIComponent getComponent() {
        return theElement;
    }

    /**
     * Set value.
     *
     * @param pValue the value
     */
    void setValue(final Object pValue) {
        theField.setValue(theField.getCastValue(pValue));
    }

    /**
     * Set editable.
     *
     * @param pEditable true/false
     */
    void setEditable(final boolean pEditable) {
        theField.setEditable(pEditable);
    }

    /**
     * Set visible.
     *
     * @param pVisible true/false
     */
    void setVisible(final boolean pVisible) {
        theElement.setVisible(pVisible);
        isVisible = pVisible;
    }

    /**
     * Is the element visible.
     *
     * @return true/false
     */
    boolean isVisible() {
        return isVisible;
    }

    /**
     * Obtain the label width.
     *
     * @return the label width
     */
    int getLabelWidth() {
        return theLabel.getWidth();
    }

    /**
     * Set the label width.
     *
     * @param pWidth the label width
     */
    void setLabelWidth(final int pWidth) {
        theLabel.setPreferredWidth(pWidth);
    }

    /**
     * Obtain the field height.
     *
     * @return the field height
     */
    int getFieldHeight() {
        return theField.getHeight();
    }

    /**
     * Set the field height.
     *
     * @param pHeight the field height
     */
    void setFieldHeight(final int pHeight) {
        theField.setPreferredHeight(pHeight);
    }

    /**
     * Adjust changed indications.
     *
     * @param pChanged is the field changed?
     */
    void adjustChanged(final boolean pChanged) {
        theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, pChanged);
        theField.adjustField();
    }
}
