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
package io.github.tonywasher.joceanus.tethys.javafx.field;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEvent;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIListButtonManager;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIListButtonField;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.javafx.base.TethysUIFXNode;
import io.github.tonywasher.joceanus.tethys.javafx.button.TethysUIFXListButtonManager;
import io.github.tonywasher.joceanus.tethys.javafx.menu.TethysUIFXScrollMenu;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * ListButtonField class.
 *
 * @param <T> the data type
 */
public class TethysUIFXListButtonField<T extends Comparable<? super T>>
        extends TethysUIFXDataTextField<List<T>>
        implements TethysUIListButtonField<T> {
    /**
     * The list manager.
     */
    private final TethysUIFXListButtonManager<T> theManager;

    /**
     * Are we editing a cell?
     */
    private boolean isCellEditing;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysUIFXListButtonField(final TethysUICoreFactory<?> pFactory) {
        this(pFactory, pFactory.buttonFactory().newListButton());
    }

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pManager the manager
     */
    private TethysUIFXListButtonField(final TethysUICoreFactory<?> pFactory,
                                      final TethysUIListButtonManager<T> pManager) {
        /* Initialise underlying class */
        super(pFactory, TethysUIFXNode.getNode(pManager));

        /* Store the manager */
        theManager = (TethysUIFXListButtonManager<T>) pManager;

        /* Set padding */
        getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

        /* Set listener on manager */
        pManager.getEventRegistrar().addEventListener(this::handleEvent);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> getCastValue(final Object pValue) {
        return (List<T>) pValue;
    }

    /**
     * handle List Button event.
     *
     * @param pEvent the even
     */
    private void handleEvent(final OceanusEvent<TethysUIEvent> pEvent) {
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                setValue(theManager.getValue());
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
    public void setValue(final List<T> pValue) {
        super.setValue(pValue);
        theManager.setValue(pValue);
        updateText();
    }

    /**
     * Update the button text.
     */
    private void updateText() {
        getLabel().setText(theManager.getText());
    }

    @Override
    public void startCellEditing(final Node pCell) {
        isCellEditing = true;
        theManager.buildMenu();
        final TethysUIFXScrollMenu<T> myMenu = theManager.getMenu();
        myMenu.showMenuAtPosition(pCell, Side.BOTTOM);
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
    public void setSelectables(final Supplier<Iterator<T>> pSelectables) {
        theManager.setSelectables(pSelectables);
    }
}

