/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import net.sourceforge.jdatebutton.javafx.ArrowIcon;
import net.sourceforge.joceanus.jtethys.date.TethysDateRangeState;
import net.sourceforge.joceanus.jtethys.javafx.TethysFXGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysDateRangeSelector;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 */
public class TethysFXDateRangeSelector
        extends TethysDateRangeSelector<Node, Node> {
    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 5;

    /**
     * The Node.
     */
    private final HBox theNode;

    /**
     * The Period Box.
     */
    private final HBox thePeriodBox;

    /**
     * The Standard Box.
     */
    private final HBox theStandardBox;

    /**
     * The Period Box.
     */
    private final HBox theCustomBox;

    /**
     * The Spacer.
     */
    private final Region theSpacer;

    /**
     * The Standard Label.
     */
    private final Label theStandardLabel;

    /**
     * The Next button.
     */
    private final Button theNextButton;

    /**
     * The Previous button.
     */
    private final Button thePrevButton;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    protected TethysFXDateRangeSelector(final TethysFXGuiFactory pFactory,
                                        final boolean pBaseIsStart) {
        /* Initialise the underlying class */
        super(pFactory, pBaseIsStart);

        /* Create the Node */
        theNode = new HBox();

        /* Create the period box */
        Label myPeriodLabel = new Label(NLS_PERIOD);
        thePeriodBox = new HBox();
        thePeriodBox.setAlignment(Pos.CENTER);
        thePeriodBox.setSpacing(STRUT_WIDTH);
        thePeriodBox.getChildren().addAll(myPeriodLabel, getPeriodButton().getNode());

        /* Create the next button */
        theNextButton = new Button();
        theNextButton.setGraphic(ArrowIcon.RIGHT.getArrow());
        theNextButton.setTooltip(new Tooltip(NLS_NEXTTIP));
        theNextButton.setOnAction(e -> handleNextDate());

        /* Create the Previous button */
        thePrevButton = new Button();
        thePrevButton.setGraphic(ArrowIcon.LEFT.getArrow());
        thePrevButton.setTooltip(new Tooltip(NLS_PREVTIP));
        thePrevButton.setOnAction(e -> handlePreviousDate());

        /* Create the Custom HBox */
        theCustomBox = new HBox();
        theCustomBox.setAlignment(Pos.CENTER);
        theCustomBox.setSpacing(STRUT_WIDTH);
        Label myStartLabel = new Label(NLS_START);
        Label myEndLabel = new Label(NLS_END);
        theCustomBox.getChildren().addAll(myStartLabel, getStartButton().getNode(), myEndLabel, getEndButton().getNode());

        /* Create the Standard HBox */
        theStandardBox = new HBox();
        theStandardBox.setAlignment(Pos.CENTER);
        theStandardBox.setSpacing(STRUT_WIDTH);
        theStandardLabel = new Label();
        theStandardBox.getChildren().addAll(theStandardLabel, thePrevButton, getBaseButton().getNode(), theNextButton);

        /* Create a small region for the centre */
        theSpacer = new Region();
        theSpacer.setPrefWidth(STRUT_WIDTH << 2);
        HBox.setHgrow(theSpacer, Priority.ALWAYS);

        /* Create the full sub-panel */
        applyState();
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public boolean isVisible() {
        return theNode.isVisible();
    }

    /**
     * Create titled pane wrapper around panel.
     * @return the titled pane
     */
    public Node getTitledSelectionPane() {
        /* Create the panel */
        return TethysFXGuiUtils.getTitledPane(NLS_TITLE, theNode);
    }

    @Override
    public void setEnabled(final boolean pEnable) {
        /* Pass call on to node */
        theNode.setDisable(!pEnable);

        /* If we are enabling */
        if (pEnable) {
            /* Ensure correct values */
            applyState();
        }
    }

    @Override
    protected void applyState(final TethysDateRangeState pState) {
        /* Determine flags */
        boolean isUpTo = pState.isUpTo()
                         && pState.isLocked();
        boolean isAdjust = pState.isAdjustable();
        boolean isFull = pState.isFull();
        boolean isContaining = pState.isContaining();
        boolean isBaseStartOfPeriod = pState.isBaseStartOfPeriod();

        /* Access the children */
        ObservableList<Node> myChildren = theNode.getChildren();

        /* Handle period box */
        if (isUpTo) {
            myChildren.remove(thePeriodBox);
        } else if (!myChildren.contains(thePeriodBox)) {
            myChildren.clear();
            myChildren.addAll(thePeriodBox, theSpacer);
        }

        /* If this is a custom state */
        if (pState.isCustom()) {

            /* If the custom box is not displaying */
            if (!myChildren.contains(theCustomBox)) {
                /* Make sure correct box is displayed */
                myChildren.remove(theStandardBox);
                myChildren.add(theCustomBox);
            }

            /* else is this is a full dates state */
        } else if (pState.isFull()) {
            /* Make sure boxes are removed */
            myChildren.removeAll(theStandardBox, theCustomBox);

            /* else this is a standard state */
        } else {
            /* Enable/disable the adjustment buttons */
            theNextButton.setDisable(!pState.isNextOK());
            thePrevButton.setDisable(!pState.isPrevOK());

            /* Hide Next/Previous if necessary */
            theNextButton.setVisible(isAdjust);
            thePrevButton.setVisible(isAdjust);

            /* Label is hidden for Full and UpTo range */
            theStandardLabel.setVisible(!isFull
                                        && !isUpTo);

            /* Set correct text for label */
            theStandardLabel.setText(isContaining
                                                  ? NLS_CONTAIN
                                                  : isBaseStartOfPeriod
                                                                        ? NLS_START
                                                                        : NLS_END);

            /* If the standard box is not displaying */
            if (!myChildren.contains(theStandardBox)) {
                /* Make sure correct box is displayed */
                myChildren.remove(theCustomBox);
                myChildren.add(theStandardBox);
            }
        }
    }
}
