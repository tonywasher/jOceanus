/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.field;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * DataEditConverter interface.
 * @param <T> the data type
 */
public abstract class TethysUICoreDataEditField<T>
        extends TethysUICoreComponent
        implements TethysUIDataEditField<T> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

    /**
     * The Attributes.
     */
    private final Map<TethysUIFieldAttribute, TethysUIFieldAttribute> theAttributes;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Is the field editable?
     */
    private boolean isEditable;

    /**
     * The value.
     */
    private T theValue;

    /**
     * The CommandMenu.
     */
    private TethysUIScrollMenu<String> theCmdMenu;

    /**
     * The CommandMenu Configurator.
     */
    private Consumer<TethysUIScrollMenu<String>> theCmdMenuConfigurator = c -> {
    };

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysUICoreDataEditField(final TethysUICoreFactory<?> pFactory) {
        /* Create event manager */
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();
        theAttributes = new EnumMap<>(TethysUIFieldAttribute.class);
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public void setEditable(final boolean pEditable) {
        isEditable = pEditable;
    }

    @Override
    public boolean isEditable() {
        return isEditable;
    }

    @Override
    public void setValue(final T pValue) {
        setTheValue(pValue);
    }

    /**
     * Set the value.
     * @param pValue the value
     */
    protected void setTheValue(final T pValue) {
        theValue = pValue;
    }

    @Override
    public void setTheAttributeState(final TethysUIFieldAttribute pAttr,
                                     final boolean pState) {
        if (pState) {
            setTheAttribute(pAttr);
        } else {
            clearTheAttribute(pAttr);
        }
    }

    @Override
    public void setTheAttribute(final TethysUIFieldAttribute pAttr) {
        theAttributes.put(pAttr, pAttr);
    }

    @Override
    public void clearTheAttribute(final TethysUIFieldAttribute pAttr) {
        theAttributes.remove(pAttr);
    }

    @Override
    public boolean isAttributeSet(final TethysUIFieldAttribute pAttr) {
        return theAttributes.containsKey(pAttr);
    }

    @Override
    public T getValue() {
        return theValue;
    }

    /**
     * Obtain the command menu.
     * @return the command menu
     */
    protected TethysUIScrollMenu<String> getCmdMenu() {
        return theCmdMenu;
    }

    @Override
    public void setCmdMenuConfigurator(final Consumer<TethysUIScrollMenu<String>> pConfigurator) {
        theCmdMenuConfigurator = pConfigurator;
    }

    @Override
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Fire event.
     * @param pEventId the eventId
     */
    protected void fireEvent(final TethysUIXEvent pEventId) {
        theEventManager.fireEvent(pEventId);
    }

    /**
     * Fire event.
     * @param pEventId the eventId
     * @param pValue the relevant value
     */
    protected void fireEvent(final TethysUIXEvent pEventId, final Object pValue) {
        theEventManager.fireEvent(pEventId, pValue);
    }

    /**
     * handleCmdMenuRequest.
     */
    protected void handleCmdMenuRequest() {
        /* fire menuBuild actionEvent */
        theCmdMenuConfigurator.accept(theCmdMenu);

        /* If a menu is provided */
        if (!theCmdMenu.isEmpty()) {
            /* Show the menu */
            showCmdMenu();
        }
    }

    /**
     * handleCmdMenuClosed.
     */
    protected void handleCmdMenuClosed() {
        /* If we selected a value */
        final TethysUIScrollItem<String> mySelected = theCmdMenu.getSelectedItem();
        if (mySelected != null) {
            /* fire new command Event */
            theEventManager.fireEvent(TethysUIXEvent.NEWCOMMAND, mySelected.getValue());
        }
    }

    /**
     * Show the command menu.
     */
    protected abstract void showCmdMenu();

    /**
     * Declare command menu.
     * @param pMenu the menu
     */
    protected void declareCmdMenu(final TethysUIScrollMenu<String> pMenu) {
        /* Store the menu */
        theCmdMenu = pMenu;
    }
}
