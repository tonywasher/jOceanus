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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/swing/DemoFilter.java $
 * $Revision: 579 $
 * $Author: Tony $
 * $Date: 2015-03-24 15:06:09 +0000 (Tue, 24 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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

import net.sourceforge.jdatebutton.swing.JDateButton;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuToggleItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButton.TethysSwingSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButton.TethysSwingStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButton.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

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
     * Open True icon.
     */
    private static final Icon OPEN_TRUE_ICON = TethysSwingGuiUtils.resizeImage(new ImageIcon(TethysScrollUITestHelper.class.getResource("GreenJellyOpenTrue.png")),
            TethysScrollMenuContent.DEFAULT_ICONWIDTH);

    /**
     * Open False icon.
     */
    private static final Icon OPEN_FALSE_ICON = TethysSwingGuiUtils.resizeImage(new ImageIcon(TethysScrollUITestHelper.class.getResource("GreenJellyOpenFalse.png")),
            TethysScrollMenuContent.DEFAULT_ICONWIDTH);

    /**
     * Closed True icon.
     */
    private static final Icon CLOSED_TRUE_ICON = TethysSwingGuiUtils.resizeImage(new ImageIcon(TethysScrollUITestHelper.class.getResource("BlueJellyClosedTrue.png")),
            TethysScrollMenuContent.DEFAULT_ICONWIDTH);

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
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysSwingScrollUIExample.class);

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
    private final TethysSwingListButtonManager<String> theListButtonMgr;

    /**
     * The date button manager.
     */
    private final TethysSwingDateButtonManager theDateButtonMgr;

    /**
     * The selected context value.
     */
    private final JLabel theContextValue;

    /**
     * The selected scroll value.
     */
    private final JLabel theScrollValue;

    /**
     * The selected date value.
     */
    private final JLabel theDateValue;

    /**
     * The selected simple icon value.
     */
    private final JLabel theSimpleIconValue;

    /**
     * The selected state icon values.
     */
    private final JLabel theStateIconValue;

    /**
     * The selected list values.
     */
    private final JLabel theListValues;

    /**
     * The selected list values.
     */
    private final List<String> theSelectedValues;

    /**
     * Constructor.
     */
    public TethysSwingScrollUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create resources */
        theScrollMenu = new TethysSwingScrollContextMenu<String>();
        theScrollButtonMgr = new TethysSwingScrollButtonManager<String>();
        theSimpleIconButtonMgr = new TethysSwingSimpleIconButtonManager<Boolean>();
        theStateIconButtonMgr = new TethysSwingStateIconButtonManager<Boolean, IconState>();
        theStateButtonMgr = new TethysSwingScrollButtonManager<IconState>();
        theListButtonMgr = new TethysSwingListButtonManager<String>();
        theDateButtonMgr = new TethysSwingDateButtonManager();
        theContextValue = new JLabel();
        theScrollValue = new JLabel();
        theDateValue = new JLabel();
        theSimpleIconValue = new JLabel();
        theStateIconValue = new JLabel();
        theListValues = new JLabel();
        theSelectedValues = new ArrayList<String>();
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
        myHelper.addFullLabeledRow(myContextArea, theContextValue);
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
        TethysSwingScrollButton myScrollButton = theScrollButtonMgr.getButton();
        JPanel myScrollArea = new JPanel();
        myScrollArea.setLayout(new BorderLayout());
        myScrollArea.setBorder(BorderFactory.createTitledBorder("ScrollButton"));
        myScrollArea.add(myScrollButton.getButton(), BorderLayout.CENTER);
        buildResultLabel(theScrollValue, "ScrollValue");
        myHelper.addFullLabeledRow(myScrollArea, theScrollValue);
        setScrollValue(null);

        /* Add listener */
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> setScrollValue(e.getDetails(String.class)));
        theScrollButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG,
                e -> theHelper.buildContextMenu(theScrollButtonMgr.getMenu()));

        /* Create list button line */
        TethysSwingListButton myListButton = theListButtonMgr.getButton();
        JPanel myListArea = new JPanel();
        myListArea.setLayout(new BorderLayout());
        myListArea.setBorder(BorderFactory.createTitledBorder("ListButton"));
        myListArea.add(myListButton.getButton(), BorderLayout.CENTER);
        buildResultLabel(theListValues, "ListValues");
        myHelper.addFullLabeledRow(myListArea, theListValues);
        setListValue(null);
        theListButtonMgr.getButton().setButtonText("Tag");
        theListButtonMgr.getMenu().setCloseOnToggle(false);

        /* Add listener */
        theListButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> setListValue(e.getDetails(TethysScrollMenuToggleItem.class)));
        theListButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG,
                e -> theHelper.buildAvailableItems(theListButtonMgr, theSelectedValues));

        /* Create date button line */
        JDateButton myDateButton = theDateButtonMgr.getButton();
        JPanel myDateArea = new JPanel();
        myDateArea.setLayout(new BorderLayout());
        myDateArea.setBorder(BorderFactory.createTitledBorder("DateButton"));
        myDateArea.add(myDateButton, BorderLayout.CENTER);
        myDateButton.setMinimumSize(new Dimension(20, 20));
        buildResultLabel(theDateValue, "DateValue");
        myHelper.addFullLabeledRow(myDateArea, theDateValue);

        /* Add listener */
        theDateButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setDateValue(e.getDetails(TethysDate.class)));

        /* Create simple icon button line */
        TethysSwingIconButton myIconButton = theSimpleIconButtonMgr.getButton();
        JPanel myIconArea = new JPanel();
        myIconArea.setLayout(new BorderLayout());
        myIconArea.setBorder(BorderFactory.createTitledBorder("SimpleIconButton"));
        myIconArea.add(myIconButton.getButton(), BorderLayout.CENTER);
        buildResultLabel(theSimpleIconValue, "IconValue");
        myHelper.addFullLabeledRow(myIconArea, theSimpleIconValue);
        theHelper.buildSimpleIconState(theSimpleIconButtonMgr,
                OPEN_FALSE_ICON,
                OPEN_TRUE_ICON);

        /* Add listener */
        theSimpleIconButtonMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE,
                e -> setSimpleIconValue(e.getDetails(Boolean.class)));

        /* Create state icon button line */
        myIconButton = theStateIconButtonMgr.getButton();
        myIconArea = new JPanel();
        myIconArea.setLayout(new BoxLayout(myIconArea, BoxLayout.X_AXIS));
        myIconArea.setBorder(BorderFactory.createTitledBorder("StateIconButton"));
        myIconArea.add(theStateButtonMgr.getNode());
        myIconArea.add(myIconButton.getButton());
        buildResultLabel(theStateIconValue, "StateIconValue");
        myHelper.addFullLabeledRow(myIconArea, theStateIconValue);
        theHelper.buildStateButton(theStateButtonMgr);
        theHelper.buildStateIconState(theStateIconButtonMgr,
                OPEN_FALSE_ICON, OPEN_TRUE_ICON, CLOSED_TRUE_ICON);

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
    private void buildResultLabel(final JLabel pLabel,
                                  final String pTitle) {
        pLabel.setBorder(BorderFactory.createTitledBorder(pTitle));
        theContextValue.setHorizontalAlignment(SwingConstants.CENTER);

        Dimension myDim = new Dimension(100, 39);
        theContextValue.setMinimumSize(myDim);
        theContextValue.setPreferredSize(myDim);
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
     * @param pValue the value to set
     */
    private void setListValue(final TethysScrollMenuToggleItem<?> pValue) {
        /* Record the value */
        if (pValue != null) {
            String myValue = (String) pValue.getValue();
            theHelper.adjustSelected(myValue, theSelectedValues);
        }
        theListValues.setText(theHelper.formatSelected(theSelectedValues));
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
