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
package net.sourceforge.joceanus.jtethys.test.ui;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUITextArea;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUICardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIScrollPaneManager;

/**
 * Chart test panel.
 */
public class TethysTestChart {
    /**
     * The TextArea height.
     */
    private static final int TEXTAREA_HEIGHT = 200;
    /**
     * The panel.
     */
    private final TethysUIBorderPaneManager thePane;

    /**
     * Constructor.
     * @param pFactory the factory.
     */
    TethysTestChart(final TethysUIFactory<?> pFactory) {
        /* Create the text area */
        final TethysUITextArea myTextArea = pFactory.controlFactory().newTextArea();

        /* Create the charts */
        final TethysTestAreaChart myArea = new TethysTestAreaChart(pFactory, myTextArea);
        final TethysTestBarChart myBar = new TethysTestBarChart(pFactory, myTextArea);
        final TethysTestPieChart myPie = new TethysTestPieChart(pFactory, myTextArea);

         /* Create the cardPane */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        final TethysUICardPaneManager<TethysUIComponent> myCards = myPanes.newCardPane();
        myCards.addCard(TethysChartType.AREA.toString(), myArea.getComponent());
        myCards.addCard(TethysChartType.BAR.toString(), myBar.getComponent());
        myCards.addCard(TethysChartType.PIE.toString(), myPie.getComponent());

        /* Create the button */
        final TethysUIScrollButtonManager<TethysChartType> myButton = pFactory.buttonFactory().newScrollButton(TethysChartType.class);
        myButton.setMenuConfigurator(c -> {
            c.removeAllItems();
            for (TethysChartType myType : TethysChartType.values()) {
                c.addItem(myType);
            }
        });
        myButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> myCards.selectCard(e.getDetails(TethysChartType.class).toString()));
        myButton.setValue(TethysChartType.AREA);

        /* Create the button pane */
        final TethysUIBoxPaneManager mySelect = myPanes.newHBoxPane();
        mySelect.addSpacer();
        mySelect.addNode(myButton);
        mySelect.addSpacer();

        /* Create the scroll pane */
        final TethysUIScrollPaneManager myScroll = myPanes.newScrollPane();
        myScroll.setContent(myTextArea);
        myScroll.setPreferredHeight(TEXTAREA_HEIGHT);

        /* Create the pane */
        thePane = myPanes.newBorderPane();
        thePane.setNorth(mySelect);
        thePane.setCentre(myCards);
        thePane.setSouth(myScroll);
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return thePane;
    }

    /**
     * Chart types.
     */
    private enum TethysChartType {
        /**
         * Area.
         */
        AREA("Area"),

        /**
         * Bar.
         */
        BAR("Bar"),

        /**
         * Pie.
         */
        PIE("Pie");

        /**
         * the chart type.
         */
        private final String theType;

        /**
         * Constructor.
         * @param pType the type
         */
        TethysChartType(final String pType) {
            theType = pType;
        }

        @Override
        public String toString() {
            return theType;
        }
    }
}
