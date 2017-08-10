/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.lethe.viewer;

import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysSlider;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Viewer Control bar.
 * @param <N> the Node type
 * @param <I> the Icon type
 */
public class MetisViewerControl<N, I>
        implements TethysNode<N> {
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
    private final TethysBorderPaneManager<N, I> thePane;

    /**
     * The viewer.
     */
    private final MetisViewerWindow<N, I> theViewer;

    /**
     * The parent button.
     */
    private final TethysButton<N, I> theParentButton;

    /**
     * The mode button.
     */
    private final TethysScrollButtonManager<MetisViewerMode, N, I> theModeButton;

    /**
     * The next button.
     */
    private final TethysButton<N, I> theNextButton;

    /**
     * The previous button.
     */
    private final TethysButton<N, I> thePrevButton;

    /**
     * The label.
     */
    private final TethysLabel<N, I> theLabel;

    /**
     * The slider.
     */
    private final TethysSlider<N, I> theSlider;

    /**
     * The slider panel.
     */
    private final TethysBorderPaneManager<N, I> theSliderPane;

    /**
     * The buttons panel.
     */
    private final TethysBoxPaneManager<N, I> theButtonsPane;

    /**
     * Constructor.
     * @param pFactory the GuiFactory
     * @param pViewer the viewer window
     */
    protected MetisViewerControl(final TethysGuiFactory<N, I> pFactory,
                                 final MetisViewerWindow<N, I> pViewer) {
        /* Store parameters */
        theViewer = pViewer;

        /* Create the parent button */
        theParentButton = pFactory.newButton();
        configureButton(theParentButton, MetisViewerIcon.PARENT);
        theParentButton.getEventRegistrar().addEventListener(e -> theViewer.handleParentPage());

        /* Create the mode button */
        theModeButton = pFactory.newScrollButton();
        theModeButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theViewer.handleMode(theModeButton.getValue()));

        /* Create the next button */
        theNextButton = pFactory.newButton();
        configureButton(theNextButton, MetisViewerIcon.NEXT);
        theNextButton.getEventRegistrar().addEventListener(e -> theViewer.handleNextPage());

        /* Create the previous button */
        thePrevButton = pFactory.newButton();
        configureButton(thePrevButton, MetisViewerIcon.PREV);
        thePrevButton.getEventRegistrar().addEventListener(e -> theViewer.handlePreviousPage());

        /* Create the label */
        theLabel = pFactory.newLabel();

        /* Create the slider */
        theSlider = pFactory.newSlider();
        theSlider.getEventRegistrar().addEventListener(e -> theViewer.handleExplicitPage(theSlider.getValue() + 1));

        /* Create the Inner Slider Pane */
        final TethysBorderPaneManager<N, I> myInnerPane = pFactory.newBorderPane();
        myInnerPane.setCentre(theSlider);
        myInnerPane.setEast(theNextButton);
        myInnerPane.setWest(thePrevButton);

        /* Create the true Slider Pane */
        theSliderPane = pFactory.newBorderPane();
        theSliderPane.setHGap(HGAP_SIZE);
        theSliderPane.setCentre(myInnerPane);
        theSliderPane.setWest(theLabel);

        /* Create the Buttons Pane */
        theButtonsPane = pFactory.newHBoxPane();
        theButtonsPane.addNode(theParentButton);
        theButtonsPane.addNode(theModeButton);

        /* Create the Main Pane */
        thePane = pFactory.newBorderPane();
        thePane.setCentre(theSliderPane);
        thePane.setWest(theButtonsPane);
        setVisible(false);
    }

    @Override
    public N getNode() {
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
    public Integer getId() {
        return thePane.getId();
    }

    /**
     * Configure button.
     * @param <K> the key type
     * @param pButton the button
     * @param pId the icon id
     */
    private static <K extends Enum<K> & TethysIconId> void configureButton(final TethysButton<?, ?> pButton,
                                                                           final K pId) {
        pButton.setIconOnly();
        pButton.setIcon(pId);
        pButton.setIconWidth(ICON_SIZE);
        pButton.setNullMargins();
    }

    /**
     * Update the state according to the page.
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
            final StringBuilder myBuilder = new StringBuilder();
            myBuilder.append(TEXT_ITEM);
            myBuilder.append(CHAR_BLANK);
            myBuilder.append(myPos);
            myBuilder.append(CHAR_BLANK);
            myBuilder.append(TEXT_OF);
            myBuilder.append(CHAR_BLANK);
            myBuilder.append(mySize);
            theLabel.setText(myBuilder.toString());

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
     * @param pPage the ViewerPage
     */
    private void buildModeMenu(final MetisViewerPage pPage) {
        /* Access the menu and reset it */
        final TethysScrollMenu<MetisViewerMode, I> myMenu = theModeButton.getMenu();
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
