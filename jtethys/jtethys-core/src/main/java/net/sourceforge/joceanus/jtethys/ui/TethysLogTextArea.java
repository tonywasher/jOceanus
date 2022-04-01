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
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.logger.TethysLogSink;

/**
 * Log text area.
 */
public class TethysLogTextArea
        implements TethysLogSink, TethysEventProvider<TethysUIEvent>, TethysComponent {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * Pane.
     */
    private final TethysBorderPaneManager thePane;

    /**
     * TextArea.
     */
    private final TethysTextArea theTextArea;

    /**
     * Constructor.
     * @param pFactory the guiFactory
     */
    TethysLogTextArea(final TethysGuiFactory pFactory) {
        /* Create basics */
        theEventManager = new TethysEventManager<>();
        theTextArea = pFactory.newTextArea();

        /* Create the clear button */
        final TethysButton theClearButton = pFactory.newButton();
        theClearButton.setTextOnly();
        theClearButton.setText("Cancel");

        /* Create a new subPanel for the buttons */
        final TethysBoxPaneManager myButtonPanel = pFactory.newHBoxPane();
        myButtonPanel.addNode(theClearButton);

        /* Create a scroll manager */
        final TethysScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(theTextArea);

        /* Add the components */
        thePane = pFactory.newBorderPane();
        thePane.setCentre(myScroll);
        thePane.setSouth(myButtonPanel);

        /* Add button listener */
        theClearButton.getEventRegistrar().addEventListener(e -> handleClear());
    }

    @Override
    public TethysNode getNode() {
        return thePane.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theTextArea.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theTextArea.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return thePane.getId();
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
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

    /**
     * is the logArea active?
     * @return true/false
     */
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
