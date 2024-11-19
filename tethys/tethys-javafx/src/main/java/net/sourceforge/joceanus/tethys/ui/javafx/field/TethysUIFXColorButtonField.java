/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.javafx.field;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;

import net.sourceforge.joceanus.tethys.event.TethysEvent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUIColorButtonField;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.tethys.ui.javafx.button.TethysUIFXColorPicker;

/**
 * ColourButtonField class.
 */
public class TethysUIFXColorButtonField
        extends TethysUIFXDataTextField<String>
        implements TethysUIColorButtonField {
    /**
     * The colour picker.
     */
    private final TethysUIFXColorPicker thePicker;

    /**
     * Are we editing a cell?
     */
    private boolean isCellEditing;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUIFXColorButtonField(final TethysUICoreFactory<?> pFactory) {
        this(pFactory, (TethysUIFXColorPicker) pFactory.buttonFactory().newColorPicker());
    }

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pPicker  the picker
     */
    private TethysUIFXColorButtonField(final TethysUICoreFactory<?> pFactory,
                                       final TethysUIFXColorPicker pPicker) {
        /* Initialise underlying class */
        super(pFactory, TethysUIFXNode.getNode(pPicker));

        /* Store the picker */
        thePicker = pPicker;

        /* Set padding */
        getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

        /* Set listener on picker */
        pPicker.getEventRegistrar().addEventListener(this::handleEvent);

        /* Configure the label */
        getLabel().setContentDisplay(ContentDisplay.LEFT);
    }

    /**
     * handle Date Button event.
     *
     * @param pEvent the even
     */
    private void handleEvent(final TethysEvent<TethysUIEvent> pEvent) {
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                setValue(thePicker.getValue());
                fireEvent(TethysUIEvent.NEWVALUE, pEvent.getDetails());
                break;
            case EDITFOCUSLOST:
                haltCellEditing();
                break;
            default:
                break;
        }
    }

    @Override
    protected Button getEditControl() {
        return (Button) super.getEditControl();
    }

    @Override
    public void setValue(final String pValue) {
        /* Store the value */
        super.setValue(pValue);

        /* Declare value to the manager */
        thePicker.setValue(pValue);

        /* Configure the label */
        final Label myLabel = getLabel();
        myLabel.setText(pValue);
        myLabel.setGraphic(thePicker.getSwatch());
    }

    @Override
    public void startCellEditing(final Node pCell) {
        /* Note editing */
        isCellEditing = true;
    }

    /**
     * haltCellEditing.
     */
    private void haltCellEditing() {
        if (isCellEditing) {
            fireEvent(TethysUIEvent.EDITFOCUSLOST, this);
        }
        isCellEditing = false;
    }
}

