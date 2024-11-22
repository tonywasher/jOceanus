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

import java.util.function.Consumer;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;

import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.tethys.ui.javafx.button.TethysUIFXScrollButtonManager;
import net.sourceforge.joceanus.tethys.ui.javafx.menu.TethysUIFXScrollMenu;

/**
 * ScrollButtonField class.
 *
 * @param <T> the data type
 */
public final class TethysUIFXScrollButtonField<T>
        extends TethysUIFXDataTextField<T>
        implements TethysUIScrollButtonField<T> {
    /**
     * The scroll manager.
     */
    private final TethysUIFXScrollButtonManager<T> theManager;

    /**
     * The configurator.
     */
    private Consumer<TethysUIScrollMenu<T>> theConfigurator;

    /**
     * The button.
     */
    private final Button theButton;

    /**
     * Are we editing a cell?
     */
    private boolean isCellEditing;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param  pClazz the value class
     */
    TethysUIFXScrollButtonField(final TethysUICoreFactory<?> pFactory,
                                final Class<T> pClazz) {
        this(pFactory, pFactory.buttonFactory().newScrollButton(pClazz));
    }

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @param pManager the manager
     */
    private TethysUIFXScrollButtonField(final TethysUICoreFactory<?> pFactory,
                                        final TethysUIScrollButtonManager<T> pManager) {
        /* Initialise underlying class */
        super(pFactory, TethysUIFXNode.getNode(pManager));

        /* Store the manager and button */
        theManager = (TethysUIFXScrollButtonManager<T>) pManager;
        theButton = getEditControl();

        /* Set padding */
        getLabel().setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));

        /* Set listener on manager */
        pManager.getEventRegistrar().addEventListener(this::handleEvent);

        /* Set configurator */
        theConfigurator = p -> {
        };
    }

    @Override
    public T getCastValue(final Object pValue) {
        return theManager.getValueClass().cast(pValue);
    }

    /**
     * handle Scroll Button event.
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
    public void setMenuConfigurator(final Consumer<TethysUIScrollMenu<T>> pConfigurator) {
        theConfigurator = pConfigurator;
        theManager.setMenuConfigurator(theConfigurator);
    }

    @Override
    protected Button getEditControl() {
        return (Button) super.getEditControl();
    }

    @Override
    public void setValue(final T pValue) {
        /* Store the value */
        super.setValue(pValue);

        /* Declare value to the manager */
        theManager.setValue(pValue);
        getLabel().setText(theButton.getText());
    }

    @Override
    public void startCellEditing(final Node pCell) {
        isCellEditing = true;
        final TethysUIFXScrollMenu<T> myMenu = theManager.getMenu();
        theConfigurator.accept(myMenu);
        if (!myMenu.isEmpty()) {
            myMenu.showMenuAtPosition(pCell, Side.BOTTOM);
        } else {
            haltCellEditing();
        }
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

