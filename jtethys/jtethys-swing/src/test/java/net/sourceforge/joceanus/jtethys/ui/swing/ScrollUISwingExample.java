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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.swing.GuiUtils;
import net.sourceforge.joceanus.jtethys.ui.IconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.ListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.ScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuToggleItem;
import net.sourceforge.joceanus.jtethys.ui.ScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.ScrollUITestHelper.IconState;
import net.sourceforge.joceanus.jtethys.ui.swing.IconSwingButton.SimpleSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.IconSwingButton.StateSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.ListSwingButton.ListSwingButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.ScrollSwingButton.ScrollSwingButtonManager;

/**
 * Scroll utilities examples.
 */
public class ScrollUISwingExample
        extends JApplet {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1335897095869650737L;

    /**
     * Open True icon.
     */
    private static final Icon OPEN_TRUE_ICON = GuiUtils.resizeImage(new ImageIcon(ScrollUITestHelper.class.getResource("GreenJellyOpenTrue.png")), ScrollMenuContent.DEFAULT_ICONWIDTH);

    /**
     * Open False icon.
     */
    private static final Icon OPEN_FALSE_ICON = GuiUtils.resizeImage(new ImageIcon(ScrollUITestHelper.class.getResource("GreenJellyOpenFalse.png")), ScrollMenuContent.DEFAULT_ICONWIDTH);

    /**
     * Closed True icon.
     */
    private static final Icon CLOSED_TRUE_ICON = GuiUtils.resizeImage(new ImageIcon(ScrollUITestHelper.class.getResource("BlueJellyClosedTrue.png")), ScrollMenuContent.DEFAULT_ICONWIDTH);

    /**
     * The padding.
     */
    private static final int PADDING = 5;

    /**
     * The default height.
     */
    private static final int DEFAULT_HEIGHT = 300;

    /**
     * The default width.
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrollUISwingExample.class);

    /**
     * The Test helper.
     */
    private final ScrollUITestHelper<Icon> theHelper;

    /**
     * The popUp menu.
     */
    private final ScrollSwingContextMenu<String> theScrollMenu;

    /**
     * The scroll button manager.
     */
    private final ScrollSwingButtonManager<String> theScrollButtonMgr;

    /**
     * The simple icon button manager.
     */
    private final SimpleSwingIconButtonManager<Boolean> theSimpleIconButtonMgr;

    /**
     * The state icon button manager.
     */
    private final StateSwingIconButtonManager<Boolean, IconState> theStateIconButtonMgr;

    /**
     * The state scroll manager.
     */
    private final ScrollSwingButtonManager<IconState> theStateButtonMgr;

    /**
     * The list button manager.
     */
    private final ListSwingButtonManager<String> theListButtonMgr;

    /**
     * The selected context value.
     */
    private final JLabel theContextValue;

    /**
     * The selected scroll value.
     */
    private final JLabel theScrollValue;

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
    public ScrollUISwingExample() {
        /* Create helper */
        theHelper = new ScrollUITestHelper<Icon>();

        /* Create resources */
        theScrollMenu = new ScrollSwingContextMenu<String>();
        theScrollButtonMgr = new ScrollSwingButtonManager<String>();
        theSimpleIconButtonMgr = new SimpleSwingIconButtonManager<Boolean>();
        theStateIconButtonMgr = new StateSwingIconButtonManager<Boolean, IconState>();
        theStateButtonMgr = new ScrollSwingButtonManager<IconState>();
        theListButtonMgr = new ListSwingButtonManager<String>();
        theContextValue = new JLabel();
        theScrollValue = new JLabel();
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
            ScrollUISwingExample myExample = new ScrollUISwingExample();

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
        GridBagHelper myHelper = new GridBagHelper(myPanel);
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
        theScrollMenu.getEventRegistrar().addFilteredActionListener(ScrollSwingContextMenu.ACTION_SELECTED, new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent e) {
                /* If we selected a value */
                ScrollMenuItem<String> mySelected = theScrollMenu.getSelectedItem();
                if (mySelected != null) {
                    setContextValue(mySelected.getValue());
                }
            }
        });

        /* Create scroll button line */
        ScrollSwingButton myScrollButton = theScrollButtonMgr.getButton();
        JPanel myScrollArea = new JPanel();
        myScrollArea.setLayout(new BorderLayout());
        myScrollArea.setBorder(BorderFactory.createTitledBorder("ScrollButton"));
        myScrollArea.add(myScrollButton, BorderLayout.CENTER);
        buildResultLabel(theScrollValue, "ScrollValue");
        myHelper.addFullLabeledRow(myScrollArea, theScrollValue);
        setScrollValue(null);

        /* Add listener */
        theScrollButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case ScrollButtonManager.ACTION_NEW_VALUE:
                        setScrollValue(pEvent.getDetails(String.class));
                        break;
                    case ScrollButtonManager.ACTION_MENU_BUILD:
                        theHelper.buildContextMenu(theScrollButtonMgr.getMenu());
                        break;
                    case ScrollButtonManager.ACTION_MENU_CANCELLED:
                    default:
                        break;
                }
            }
        });

        /* Create list button line */
        ListSwingButton myListButton = theListButtonMgr.getButton();
        JPanel myListArea = new JPanel();
        myListArea.setLayout(new BorderLayout());
        myListArea.setBorder(BorderFactory.createTitledBorder("ListButton"));
        myListArea.add(myListButton, BorderLayout.CENTER);
        buildResultLabel(theListValues, "ListValues");
        myHelper.addFullLabeledRow(myListArea, theListValues);
        setListValue(null);
        theListButtonMgr.getButton().setButtonText("Tag");
        theListButtonMgr.getMenu().setCloseOnToggle(false);

        /* Add listener */
        theListButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case ListButtonManager.ACTION_TOGGLED:
                        setListValue(pEvent.getDetails(ScrollMenuToggleItem.class));
                        break;
                    case ScrollButtonManager.ACTION_MENU_BUILD:
                        theHelper.buildAvailableItems(theListButtonMgr.getMenu(), theSelectedValues);
                        break;
                    case ScrollButtonManager.ACTION_MENU_CANCELLED:
                    default:
                        break;
                }
            }
        });

        /* Create simple icon button line */
        IconSwingButton myIconButton = theSimpleIconButtonMgr.getButton();
        JPanel myIconArea = new JPanel();
        myIconArea.setLayout(new BorderLayout());
        myIconArea.setBorder(BorderFactory.createTitledBorder("SimpleIconButton"));
        myIconArea.add(myIconButton, BorderLayout.CENTER);
        buildResultLabel(theSimpleIconValue, "IconValue");
        myHelper.addFullLabeledRow(myIconArea, theSimpleIconValue);
        theHelper.buildSimpleIconState(theSimpleIconButtonMgr,
                OPEN_FALSE_ICON,
                OPEN_TRUE_ICON);

        /* Add listener */
        theSimpleIconButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case IconButtonManager.ACTION_NEW_VALUE:
                        setSimpleIconValue(pEvent.getDetails(Boolean.class));
                        break;
                    default:
                        break;
                }
            }
        });

        /* Create state icon button line */
        myIconButton = theStateIconButtonMgr.getButton();
        myIconArea = new JPanel();
        myIconArea.setLayout(new BoxLayout(myIconArea, BoxLayout.X_AXIS));
        myIconArea.setBorder(BorderFactory.createTitledBorder("StateIconButton"));
        myIconArea.add(theStateButtonMgr.getButton());
        myIconArea.add(myIconButton);
        buildResultLabel(theStateIconValue, "StateIconValue");
        myHelper.addFullLabeledRow(myIconArea, theStateIconValue);
        theHelper.buildStateButton(theStateButtonMgr);
        theHelper.buildStateIconState(theStateIconButtonMgr,
                OPEN_FALSE_ICON, OPEN_TRUE_ICON, CLOSED_TRUE_ICON);

        /* Add listener */
        theStateIconButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case IconButtonManager.ACTION_NEW_VALUE:
                        setStateIconValue(pEvent.getDetails(Boolean.class));
                        break;
                    default:
                        break;
                }
            }
        });

        /* Add listener */
        theStateButtonMgr.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case IconButtonManager.ACTION_NEW_VALUE:
                        theStateIconButtonMgr.setMachineState(pEvent.getDetails(IconState.class));
                        setStateIconValue(theStateIconButtonMgr.getValue());
                        break;
                    default:
                        break;
                }
            }
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
     * Set the list value.
     * @param pValue the value to set
     */
    private void setListValue(final ScrollMenuToggleItem<?> pValue) {
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
