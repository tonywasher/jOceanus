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
package net.sourceforge.joceanus.tethys.ui.swing.field;

import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.tethys.event.TethysEvent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUIColorButtonField;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingNode;
import net.sourceforge.joceanus.tethys.ui.swing.button.TethysUISwingColorPicker;

/**
 * ColorButtonField class.
 */
public class TethysUISwingColorButtonField
        extends TethysUISwingDataTextField<String>
        implements TethysUIColorButtonField {
    /**
     * The colour picker.
     */
    private final TethysUISwingColorPicker thePicker;

    /**
     * Are we editing a cell?
     */
    private boolean isCellEditing;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    TethysUISwingColorButtonField(final TethysUICoreFactory<?> pFactory) {
        this(pFactory, new JLabel());
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pLabel the label
     */
    private TethysUISwingColorButtonField(final TethysUICoreFactory<?> pFactory,
                                          final JLabel pLabel) {
        this(pFactory, (TethysUISwingColorPicker) pFactory.buttonFactory().newColorPicker(), pLabel);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pPicker the picker
     * @param pLabel the label
     */
    private TethysUISwingColorButtonField(final TethysUICoreFactory<?> pFactory,
                                          final TethysUISwingColorPicker pPicker,
                                          final JLabel pLabel) {
        /* Initialise underlying class */
        super(pFactory, TethysUISwingNode.getComponent(pPicker), pLabel);

        /* Store the picker */
        thePicker = pPicker;

        /* Set listener on picker */
        pPicker.getEventRegistrar().addEventListener(this::handleEvent);

        /* Configure the label */
        getLabel().setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    /**
     * handle Date Button event.
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
    public JButton getEditControl() {
        return (JButton) super.getEditControl();
    }

    @Override
    public void setValue(final String pValue) {
        /* Store the value */
        super.setValue(pValue);

        /* Declare value to the manager */
        thePicker.setValue(pValue);

        /* Configure the label */
        final JLabel myLabel = getLabel();
        myLabel.setText(pValue);
        myLabel.setIcon(thePicker.getSwatch());
    }

    @Override
    public void startCellEditing(final Rectangle pCell) {
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

    @Override
    public TethysUISwingColorButtonField cloneField(final JLabel pLabel) {
        return new TethysUISwingColorButtonField(getGuiFactory(), pLabel);
    }
}
