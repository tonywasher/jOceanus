/*
 * Metis: Java Data Framework
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
package io.github.tonywasher.joceanus.metis.viewer;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.control.TethysUISlider;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;

/**
 * Viewer Control bar.
 */
public class MetisViewerControl
        implements TethysUIComponent {
    /**
     * The item text.
     */
    private static final String TEXT_ITEM = MetisViewerResource.VIEWER_SELECT_ITEM.getValue();

    /**
     * The of text.
     */
    private static final String TEXT_OF = MetisViewerResource.VIEWER_SELECT_OF.getValue();

    /**
     * Blank character.
     */
    private static final char CHAR_BLANK = ' ';

    /**
     * Default icon size.
     */
    private static final int ICON_SIZE = 24;

    /**
     * The HGap.
     */
    private static final int HGAP_SIZE = 10;

    /**
     * The panel.
     */
    private final TethysUIBorderPaneManager thePane;

    /**
     * The viewer.
     */
    private final MetisViewerWindow theViewer;

    /**
     * The parent button.
     */
    private final TethysUIButton theParentButton;

    /**
     * The mode button.
     */
    private final TethysUIScrollButtonManager<MetisViewerMode> theModeButton;

    /**
     * The next button.
     */
    private final TethysUIButton theNextButton;

    /**
     * The previous button.
     */
    private final TethysUIButton thePrevButton;

    /**
     * The label.
     */
    private final TethysUILabel theLabel;

    /**
     * The slider.
     */
    private final TethysUISlider theSlider;

    /**
     * The slider panel.
     */
    private final TethysUIBorderPaneManager theSliderPane;

    /**
     * The buttons panel.
     */
    private final TethysUIBoxPaneManager theButtonsPane;

    /**
     * Constructor.
     *
     * @param pFactory the GuiFactory
     * @param pViewer  the viewer window
     */
    protected MetisViewerControl(final TethysUIFactory<?> pFactory,
                                 final MetisViewerWindow pViewer) {
        /* Store parameters */
        theViewer = pViewer;

        /* Create the parent button */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        theParentButton = myButtons.newButton();
        configureButton(theParentButton, MetisViewerIcon.PARENT);
        theParentButton.getEventRegistrar().addEventListener(e -> theViewer.handleParentPage());

        /* Create the mode button */
        theModeButton = myButtons.newScrollButton(MetisViewerMode.class);
        theModeButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theViewer.handleMode(theModeButton.getValue()));

        /* Create the next button */
        theNextButton = myButtons.newButton();
        configureButton(theNextButton, MetisViewerIcon.NEXT);
        theNextButton.getEventRegistrar().addEventListener(e -> theViewer.handleNextPage());

        /* Create the previous button */
        thePrevButton = myButtons.newButton();
        configureButton(thePrevButton, MetisViewerIcon.PREV);
        thePrevButton.getEventRegistrar().addEventListener(e -> theViewer.handlePreviousPage());

        /* Create the label */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        theLabel = myControls.newLabel();

        /* Create the slider */
        theSlider = myControls.newSlider();
        theSlider.getEventRegistrar().addEventListener(e -> theViewer.handleExplicitPage(theSlider.getValue() + 1));

        /* Create the Inner Slider Pane */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        final TethysUIBorderPaneManager myInnerPane = myPanes.newBorderPane();
        myInnerPane.setCentre(theSlider);
        myInnerPane.setEast(theNextButton);
        myInnerPane.setWest(thePrevButton);

        /* Create the true Slider Pane */
        theSliderPane = myPanes.newBorderPane();
        theSliderPane.setHGap(HGAP_SIZE);
        theSliderPane.setCentre(myInnerPane);
        theSliderPane.setWest(theLabel);

        /* Create the Buttons Pane */
        theButtonsPane = myPanes.newHBoxPane();
        theButtonsPane.addNode(theParentButton);
        theButtonsPane.addNode(theModeButton);

        /* Create the Main Pane */
        thePane = myPanes.newBorderPane();
        thePane.setCentre(theSliderPane);
        thePane.setWest(theButtonsPane);
        setVisible(false);
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePane;
    }

    /**
     * Configure button.
     *
     * @param <K>     the key type
     * @param pButton the button
     * @param pId     the icon id
     */
    private static <K extends Enum<K> & TethysUIIconId> void configureButton(final TethysUIButton pButton,
                                                                             final K pId) {
        pButton.setIconOnly();
        pButton.setIcon(pId);
        pButton.setIconSize(ICON_SIZE);
        pButton.setNullMargins();
    }

    /**
     * Update the state according to the page.
     *
     * @param pPage the ViewerPage
     */
    protected void updateState(final MetisViewerPage pPage) {
        /* Only show Parent button if we have a parent */
        final boolean hasParent = pPage.hasParent();
        theParentButton.setVisible(hasParent);
        boolean isVisible = hasParent;

        /* Set the active mode */
        final MetisViewerMode myMode = pPage.getMode();
        final boolean isList = !MetisViewerMode.CONTENTS.equals(myMode);

        /* Set the Mode Button */
        theModeButton.setValue(myMode);
        final boolean isEnabled = pPage.hasMultiModes();
        theModeButton.setEnabled(isEnabled);
        if (isEnabled) {
            buildModeMenu(pPage);
        }
        isVisible |= isEnabled;

        /* Obtain list details */
        final int mySize = pPage.getSize();
        final int myPos = pPage.getItemNo();

        /* If we are a list */
        if (isList
                && (mySize > 1)) {
            /* Show the Slider */
            theSliderPane.setVisible(true);
            isVisible = true;

            /* Build the text */
            final String myText = TEXT_ITEM
                    + CHAR_BLANK
                    + myPos
                    + CHAR_BLANK
                    + TEXT_OF
                    + CHAR_BLANK
                    + mySize;
            theLabel.setText(myText);

            /* Configure the slider */
            theSlider.setMaximum(mySize - 1);
            theSlider.setValue(myPos - 1);

            /* Disable next/previous as necessary */
            theNextButton.setEnabled(pPage.hasNext());
            thePrevButton.setEnabled(pPage.hasPrevious());

            /* else not a list mode */
        } else {
            /* Hide the Slider */
            theSliderPane.setVisible(false);
        }

        /* Set visibility */
        setVisible(isVisible);
    }

    /**
     * Build the mode menu.
     *
     * @param pPage the ViewerPage
     */
    private void buildModeMenu(final MetisViewerPage pPage) {
        /* Access the menu and reset it */
        final TethysUIScrollMenu<MetisViewerMode> myMenu = theModeButton.getMenu();
        myMenu.removeAllItems();

        /* Loop through the modes */
        for (MetisViewerMode myMode : MetisViewerMode.values()) {
            /* If this is a valid mode */
            if (pPage.validMode(myMode)) {
                /* Add to menu */
                myMenu.addItem(myMode);
            }
        }
    }
}
