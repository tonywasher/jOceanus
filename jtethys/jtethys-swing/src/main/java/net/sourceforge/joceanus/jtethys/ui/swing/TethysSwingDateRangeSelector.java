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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.date.TethysDatePeriod;
import net.sourceforge.joceanus.jtethys.date.TethysDateRangeState;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysDateRangeSelector;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * Selection panel to select a standard DatePeriod from within a range of dates.
 */
public class TethysSwingDateRangeSelector
        extends TethysDateRangeSelector<JComponent> {
    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * The Node.
     */
    private final TethysSwingEnablePanel theNode;

    /**
     * The Period Panel.
     */
    private final JPanel thePeriodPane;

    /**
     * The Custom Panel.
     */
    private final JPanel theCustomPane;

    /**
     * The Standard Panel.
     */
    private final JPanel theStandardPane;

    /**
     * The Standard Label.
     */
    private final JLabel theStandardLabel;

    /**
     * The Next button.
     */
    private final JButton theNextButton;

    /**
     * The Previous button.
     */
    private final JButton thePrevButton;

    /**
     * Constructor.
     */
    public TethysSwingDateRangeSelector() {
        /* Call standard constructor */
        this(false);
    }

    /**
     * Constructor.
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public TethysSwingDateRangeSelector(final boolean pBaseIsStart) {
        /* Call standard constructor */
        this(new TethysDataFormatter(), pBaseIsStart);
    }

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public TethysSwingDateRangeSelector(final TethysDataFormatter pFormatter,
                                        final boolean pBaseIsStart) {
        /* Initialise the underlying class */
        super(pFormatter, pBaseIsStart);

        /* Create the Node */
        theNode = new TethysSwingEnablePanel();

        /* Create the period button */
        TethysSwingScrollButtonManager<TethysDatePeriod> myPeriodButton = new TethysSwingScrollButtonManager<>();
        declarePeriodButton(myPeriodButton);

        /* Create the period panel */
        JLabel myPeriodLabel = new JLabel(NLS_PERIOD);
        thePeriodPane = new TethysSwingEnablePanel();
        thePeriodPane.setLayout(new BoxLayout(thePeriodPane, BoxLayout.X_AXIS));
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePeriodPane.add(myPeriodLabel);
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePeriodPane.add(myPeriodButton.getNode());
        thePeriodPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create the DateButtons */
        TethysSwingDateButtonManager myStartButton = new TethysSwingDateButtonManager(pFormatter);
        declareStartButton(myStartButton);
        TethysSwingDateButtonManager myEndButton = new TethysSwingDateButtonManager(pFormatter);
        declareEndButton(myEndButton);
        TethysSwingDateButtonManager myBaseButton = new TethysSwingDateButtonManager(pFormatter);
        declareBaseButton(myBaseButton);

        /* Create the buttons */
        theNextButton = new JButton(TethysSwingArrowIcon.RIGHT);
        theNextButton.setToolTipText(NLS_NEXTTIP);
        theNextButton.addActionListener(e -> handleNextDate());
        thePrevButton = new JButton(TethysSwingArrowIcon.LEFT);
        thePrevButton.setToolTipText(NLS_PREVTIP);
        thePrevButton.addActionListener(e -> handlePreviousDate());

        /* Create the Custom Pane */
        theCustomPane = new TethysSwingEnablePanel();
        theCustomPane.setLayout(new BoxLayout(theCustomPane, BoxLayout.X_AXIS));
        JLabel myStartLabel = new JLabel(NLS_START);
        JLabel myEndLabel = new JLabel(NLS_END);
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theCustomPane.add(myStartLabel);
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theCustomPane.add(myStartButton.getNode());
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theCustomPane.add(myEndLabel);
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theCustomPane.add(myEndButton.getNode());
        theCustomPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create the Standard Pane */
        theStandardPane = new TethysSwingEnablePanel();
        theStandardPane.setLayout(new BoxLayout(theStandardPane, BoxLayout.X_AXIS));
        theStandardLabel = new JLabel();
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStandardPane.add(theStandardLabel);
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStandardPane.add(thePrevButton);
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStandardPane.add(myBaseButton.getNode());
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        theStandardPane.add(theNextButton);
        theStandardPane.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Create the panel */
        theNode.setLayout(new BoxLayout(theNode, BoxLayout.X_AXIS));
        theNode.add(thePeriodPane);
        theNode.add(Box.createHorizontalGlue());
        theNode.add(theStandardPane);
        theNode.add(theCustomPane);

        /* Create the full sub-panel */
        applyState();
    }

    @Override
    public JComponent getNode() {
        return theNode;
    }

    /**
     * Create titled pane wrapper around panel.
     * @return the titled pane
     */
    public JComponent getTitledSelectionPane() {
        theNode.setBorder(BorderFactory.createTitledBorder(NLS_TITLE));
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnable) {
        /* Pass call on to node */
        theNode.setEnabled(pEnable);

        /* If we are enabling */
        if (pEnable) {
            /* Ensure correct values */
            applyState();
        }
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public boolean isVisible() {
        return theNode.isVisible();
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

        /* Set the period visibility */
        thePeriodPane.setVisible(!isUpTo);

        /* If this is a custom state */
        if (pState.isCustom()) {
            /* Show the custom box */
            theStandardPane.setVisible(false);
            theCustomPane.setVisible(true);

            /* else is this is a full dates state */
        } else if (pState.isFull()) {
            /* Hide custom and standard boxes */
            theStandardPane.setVisible(false);
            theCustomPane.setVisible(false);

            /* else this is a standard state */
        } else {
            /* Enable/disable the adjustment buttons */
            theNextButton.setEnabled(pState.isNextOK());
            thePrevButton.setEnabled(pState.isPrevOK());

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

            /* Show the standard box */
            theCustomPane.setVisible(false);
            theStandardPane.setVisible(true);
        }
    }
}
