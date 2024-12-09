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
package net.sourceforge.joceanus.tethys.core.factory;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.factory.TethysUILogTextArea;
import net.sourceforge.joceanus.tethys.api.base.TethysUINode;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUITextArea;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIScrollPaneManager;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.base.TethysUIResource;

/**
 * Log text area.
 */
public class TethysUICoreLogTextArea
        extends TethysUICoreComponent
        implements TethysUILogTextArea {
    /**
     * Cancel Text.
     */
    private static final String TEXT_CANCEL = TethysUIResource.BUTTON_CANCEL.getValue();

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * Pane.
     */
    private final TethysUIBorderPaneManager thePane;

    /**
     * TextArea.
     */
    private final TethysUITextArea theTextArea;

    /**
     * Constructor.
     * @param pFactory the guiFactory
     */
    TethysUICoreLogTextArea(final TethysUICoreFactory<?> pFactory) {
        /* Create basics */
        theEventManager = new OceanusEventManager<>();
        theTextArea = pFactory.controlFactory().newTextArea();

        /* Create the clear button */
        final TethysUIButton theClearButton = pFactory.buttonFactory().newButton();
        theClearButton.setTextOnly();
        theClearButton.setText(TEXT_CANCEL);

        /* Create a new subPanel for the buttons */
        final TethysUIPaneFactory myFactory = pFactory.paneFactory();
        final TethysUIBoxPaneManager myButtonPanel = myFactory.newHBoxPane();
        myButtonPanel.addNode(theClearButton);

        /* Create a scroll manager */
        final TethysUIScrollPaneManager myScroll = myFactory.newScrollPane();
        myScroll.setContent(theTextArea);

        /* Add the components */
        thePane = myFactory.newBorderPane();
        thePane.setCentre(myScroll);
        thePane.setSouth(myButtonPanel);

        /* Add button listener */
        theClearButton.getEventRegistrar().addEventListener(e -> handleClear());
    }

    @Override
    public TethysUINode getNode() {
        return thePane.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePane.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePane.setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        thePane.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        thePane.setPreferredHeight(pHeight);
    }

    @Override
    public Integer getId() {
        return thePane.getId();
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void writeLogMessage(final String pMessage) {
        final int myPos = theTextArea.getTextLength();
        theTextArea.appendText(pMessage);
        theTextArea.appendText("\n");
        theTextArea.setCaretPosition(myPos + 1);
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE);
    }

    @Override
    public boolean isActive() {
        return theTextArea.getTextLength() > 0;
    }

    /**
     * Handle clear request.
     */
    private void handleClear() {
        /* Clear contents */
        theTextArea.setText(null);
        theEventManager.fireEvent(TethysUIEvent.WINDOWCLOSED);
    }
}
