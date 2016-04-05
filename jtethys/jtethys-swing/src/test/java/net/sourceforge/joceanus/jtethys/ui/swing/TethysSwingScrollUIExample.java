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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.ui.TethysHelperIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysListId;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager.TethysSwingSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager.TethysSwingStateIconButtonManager;

/**
 * Scroll utilities examples.
 */
public class TethysSwingScrollUIExample
        extends JApplet {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1335897095869650737L;

    /**
     * The padding.
     */
    private static final int PADDING = 5;

    /**
     * The default height.
     */
    private static final int DEFAULT_HEIGHT = 350;

    /**
     * The default width.
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * Default icon width.
     */
    private static final int DEFAULT_ICONWIDTH = 24;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysSwingScrollUIExample.class);

    /**
     * The GuiFactory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<JComponent, Icon> theHelper;

    /**
     * The popUp menu.
     */
    private final TethysSwingScrollContextMenu<String> theScrollMenu;

    /**
     * The scroll button manager.
     */
    private final TethysSwingScrollButtonManager<String> theScrollButtonMgr;

    /**
     * The simple icon button manager.
     */
    private final TethysSwingSimpleIconButtonManager<Boolean> theSimpleIconButtonMgr;

    /**
     * The state icon button manager.
     */
    private final TethysSwingStateIconButtonManager<Boolean, IconState> theStateIconButtonMgr;

    /**
     * The state scroll manager.
     */
    private final TethysSwingScrollButtonManager<IconState> theStateButtonMgr;

    /**
     * The list button manager.
     */
    private final TethysSwingListButtonManager<TethysListId> theListButtonMgr;

    /**
     * The date button manager.
     */
    private final TethysSwingDateButtonManager theDateButtonMgr;

    /**
     * The selected context value.
     */
    private final TethysSwingLabel theContextValue;

    /**
     * The selected scroll value.
     */
    private final TethysSwingLabel theScrollValue;

    /**
     * The selected date value.
     */
    private final TethysSwingLabel theDateValue;

    /**
     * The selected simple icon value.
     */
    private final TethysSwingLabel theSimpleIconValue;

    /**
     * The selected state icon values.
     */
    private final TethysSwingLabel theStateIconValue;

    /**
     * The selected list values.
     */
    private final TethysSwingLabel theListValues;

    /**
     * Constructor.
     */
    public TethysSwingScrollUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create GUI Factory */
        theGuiFactory = new TethysSwingGuiFactory();

        /* Create resources */
        theScrollMenu = theGuiFactory.newContextMenu();
        theScrollButtonMgr = theGuiFactory.newScrollButton();
        theSimpleIconButtonMgr = theGuiFactory.newSimpleIconButton();
        theStateIconButtonMgr = theGuiFactory.newStateIconButton();
        theStateButtonMgr = theGuiFactory.newScrollButton();
        theListButtonMgr = theGuiFactory.newListButton();
        theDateButtonMgr = theGuiFactory.newDateButton();
        theContextValue = theGuiFactory.newLabel();
        theScrollValue = theGuiFactory.newLabel();
        theDateValue = theGuiFactory.newLabel();
        theSimpleIconValue = theGuiFactory.newLabel();
        theStateIconValue = theGuiFactory.newLabel();
        theListValues = theGuiFactory.newLabel();
    }

    @Override
    public void init() {
        // Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    /* Access the panel */
                    JPanel myPanel = buildPanel();
                    setContentPane(myPanel);
                }
            });
        } catch (InvocationTargetException e) {
            LOGGER.error("Failed to invoke thread", e);
        } catch (InterruptedException e) {
            LOGGER.error("Thread was interrupted", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            JFrame myFrame = new JFrame("SwingScroll Demo");

            /* Create the UI */
            TethysSwingScrollUIExample myExample = new TethysSwingScrollUIExample();

            /* Build the panel */
            JPanel myPanel = myExample.buildPanel();

            /* Attach the panel to the frame */
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Build the panel.
     */
    private JPanel buildPanel() {
        /* Create a panel */
        JPanel myPanel = new JPanel();
        TethysSwingGridBagHelper myHelper = new TethysSwingGridBagHelper(myPanel);
        myHelper.setInsetSize(PADDING);
        myPanel.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        /* Create context menu line */
        JLabel myContextArea = new JLabel("Right-click for Menu");
        myContextArea.setBorder(BorderFactory.createTitledBorder("ContextArea"));
        myContextArea.setHorizontalAlignment(SwingConstants.CENTER);
        buildResultLabel(theContextValue, "ContextValue");
        myHelper.addFullLabeledRow(myContextArea, theContextValue.getNode());
        setContextValue(null);

        /* Build the context menu */
        theHelper.buildContextMenu(theScrollMenu);

        /* Add popUp listener */
        myContextArea.addMouseListener(new MouseAdapter() {
            public void mousePressed(final MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    theScrollMenu.showMenuAtPosition(evt.getComponent(), evt.getX(), evt.getY());
                }
            }

            public void mouseReleased(final MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    theScrollMenu.showMenuAtPosition(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });

        /* Add listener */
        theScrollMenu.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            /* If we selected a value */
            TethysScrollMenuItem<String> mySelected = theScrollMenu.getSelectedItem();
            if (mySelected != null) {
                setContextValue(mySelected.getValue());
            }
        });

        /* Create scroll button line */
        JPanel myScrollArea = new JPanel();
        myScrollArea.setLayout(new BorderLayout());
        myScrollArea.setBorder(BorderFactory.createTitledBorder("ScrollButton"));
        myScrollArea.add(theScrollButtonMgr.getNode(), BorderLayout.CENTER);
        buildResultLabel(theScrollValue, "ScrollValue");
        myHelper.addFullLabeledRow(myScrollArea, theScrollValue.getNode());
        setScrollValue(null);

        /* Add listener */
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> setScrollValue(e.getDetails(String.class)));
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG,
                e -> theHelper.buildContextMenu(theScrollButtonMgr.getMenu()));

        /* Create list button line */
        JPanel myListArea = new JPanel();
        myListArea.setLayout(new BorderLayout());
        myListArea.setBorder(BorderFactory.createTitledBorder("ListButton"));
        myListArea.add(theListButtonMgr.getNode(), BorderLayout.CENTER);
        buildResultLabel(theListValues, "ListValues");
        myHelper.addFullLabeledRow(myListArea, theListValues.getNode());

        theListButtonMgr.setValue(theHelper.buildToggleList(theListButtonMgr));
        theListButtonMgr.setText("Tag");

        /* Add listener */
        theListButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setListValue());

        /* Create date button line */
        JPanel myDateArea = new JPanel();
        myDateArea.setLayout(new BorderLayout());
        myDateArea.setBorder(BorderFactory.createTitledBorder("DateButton"));
        myDateArea.add(theDateButtonMgr.getNode(), BorderLayout.CENTER);
        theDateButtonMgr.getNode().setMinimumSize(new Dimension(20, 20));
        buildResultLabel(theDateValue, "DateValue");
        myHelper.addFullLabeledRow(myDateArea, theDateValue.getNode());

        /* Add listener */
        theDateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setDateValue(e.getDetails(TethysDate.class)));

        /* Create simple icon button line */
        JPanel myIconArea = new JPanel();
        myIconArea.setLayout(new BorderLayout());
        myIconArea.setBorder(BorderFactory.createTitledBorder("SimpleIconButton"));
        myIconArea.add(theSimpleIconButtonMgr.getNode(), BorderLayout.CENTER);
        buildResultLabel(theSimpleIconValue, "IconValue");
        myHelper.addFullLabeledRow(myIconArea, theSimpleIconValue.getNode());
        theSimpleIconButtonMgr.setWidth(DEFAULT_ICONWIDTH);
        theHelper.buildSimpleIconState(theSimpleIconButtonMgr, TethysHelperIcon.OPENFALSE, TethysHelperIcon.OPENTRUE);

        /* Add listener */
        theSimpleIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> setSimpleIconValue(e.getDetails(Boolean.class)));

        /* Create state icon button line */
        myIconArea = new JPanel();
        myIconArea.setLayout(new BoxLayout(myIconArea, BoxLayout.X_AXIS));
        myIconArea.setBorder(BorderFactory.createTitledBorder("StateIconButton"));
        myIconArea.add(theStateButtonMgr.getNode());
        myIconArea.add(theStateIconButtonMgr.getNode());
        buildResultLabel(theStateIconValue, "StateIconValue");
        myHelper.addFullLabeledRow(myIconArea, theStateIconValue.getNode());
        theHelper.buildStateButton(theStateButtonMgr);
        theStateIconButtonMgr.setWidth(DEFAULT_ICONWIDTH);
        theStateIconButtonMgr.setNullMargins();
        theHelper.buildStateIconState(theStateIconButtonMgr,
                TethysHelperIcon.OPENFALSE,
                TethysHelperIcon.OPENTRUE,
                TethysHelperIcon.CLOSEDTRUE);

        /* Add listener */
        theStateIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setStateIconValue(e.getDetails(Boolean.class)));

        /* Add listener */
        theStateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
            theStateIconButtonMgr.setMachineState(e.getDetails(IconState.class));
            setStateIconValue(theStateIconButtonMgr.getValue());
        });

        /* Return the panel */
        return myPanel;
    }

    /**
     * Build result.
     * @param pResult the result label
     * @param pTitle the title
     */
    private void buildResultLabel(final TethysSwingLabel pLabel,
                                  final String pTitle) {
        // pLabel.setBorder(BorderFactory.createTitledBorder(pTitle));
        theContextValue.setAlignment(TethysAlignment.CENTRE);

        JComponent myNode = pLabel.getNode();
        Dimension myDim = new Dimension(100, 39);
        myNode.setMinimumSize(myDim);
        myNode.setPreferredSize(myDim);
        myNode.setBorder(BorderFactory.createTitledBorder(pTitle));
    }

    /**
     * Set the active value.
     * @param pValue the value to set
     */
    private void setContextValue(final String pValue) {
        /* Record the value */
        theContextValue.setText(pValue);
    }

    /**
     * Set the scroll value.
     * @param pValue the value to set
     */
    private void setScrollValue(final String pValue) {
        /* Record the value */
        theScrollValue.setText(pValue);
    }

    /**
     * Set the date value.
     * @param pValue the value to set
     */
    private void setDateValue(final TethysDate pValue) {
        /* Record the value */
        theDateValue.setText(pValue == null
                                            ? null
                                            : pValue.toString());
    }

    /**
     * Set the list value.
     */
    private void setListValue() {
        /* Record the value */
        theListValues.setText(theListButtonMgr.getText());
    }

    /**
     * Set the simple icon value.
     * @param pValue the value to set
     */
    private void setSimpleIconValue(final Boolean pValue) {
        /* Record the value */
        theSimpleIconValue.setText(Boolean.toString(pValue));
    }

    /**
     * Set the state icon value.
     * @param pValue the value to set
     */
    private void setStateIconValue(final Boolean pValue) {
        /* Record the value */
        theStateIconValue.setText(theStateButtonMgr.getValue().toString() + ":" + Boolean.toString(pValue));
    }
}
