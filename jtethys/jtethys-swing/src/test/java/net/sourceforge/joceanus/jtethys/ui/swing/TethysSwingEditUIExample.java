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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/test/java/net/sourceforge/joceanus/jtethys/dateday/JDateDayExample.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEventListener;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuToggleItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingListButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutedPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingLongTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRateTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingShortTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButton.TethysSwingSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButton.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * Scroll utilities examples.
 */
public class TethysSwingEditUIExample
        extends JApplet {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = -561317220491311954L;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TethysSwingEditUIExample.class);

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
     * The padding.
     */
    private static final int PADDING = 5;

    /**
     * The default height.
     */
    private static final int DEFAULT_HEIGHT = 620;

    /**
     * The default width.
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * The Test helper.
     */
    private final TethysScrollUITestHelper<Icon> theHelper;

    /**
     * The string edit field.
     */
    private final TethysSwingStringTextField theStringField;

    /**
     * The short edit field.
     */
    private final TethysSwingShortTextField theShortField;

    /**
     * The integer edit field.
     */
    private final TethysSwingIntegerTextField theIntegerField;

    /**
     * The long edit field.
     */
    private final TethysSwingLongTextField theLongField;

    /**
     * The money edit field.
     */
    private final TethysSwingMoneyTextField theMoneyField;

    /**
     * The price edit field.
     */
    private final TethysSwingPriceTextField thePriceField;

    /**
     * The diluted price edit field.
     */
    private final TethysSwingDilutedPriceTextField theDilutedPriceField;

    /**
     * The dilution edit field.
     */
    private final TethysSwingDilutionTextField theDilutionField;

    /**
     * The units edit field.
     */
    private final TethysSwingUnitsTextField theUnitsField;

    /**
     * The rate edit field.
     */
    private final TethysSwingRateTextField theRateField;

    /**
     * The ratio edit field.
     */
    private final TethysSwingRatioTextField theRatioField;

    /**
     * The icon button manager.
     */
    private final TethysSwingSimpleIconButtonManager<Boolean> theIconButtonMgr;

    /**
     * The icon button field.
     */
    private final TethysSwingIconButtonField<Boolean> theIconField;

    /**
     * The scroll button manager.
     */
    private final TethysSwingScrollButtonManager<String> theScrollButtonMgr;

    /**
     * The scroll button field.
     */
    private final TethysSwingScrollButtonField<String> theScrollField;

    /**
     * The list button manager.
     */
    private final TethysSwingListButtonManager<String> theListButtonMgr;

    /**
     * The list button field.
     */
    private final TethysSwingListButtonField<String> theListField;

    /**
     * The source.
     */
    private final JLabel theSource;

    /**
     * The result.
     */
    private final JLabel theClass;

    /**
     * The result.
     */
    private final JLabel theValue;

    /**
     * The Decimal formatter.
     */
    private final TethysDecimalFormatter theFormatter;

    /**
     * The selected list values.
     */
    private final List<String> theSelectedValues;

    /**
     * Constructor.
     */
    public TethysSwingEditUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<Icon>();

        /* Create formatters/parsers */
        theFormatter = new TethysDecimalFormatter();
        TethysDecimalParser myParser = new TethysDecimalParser();

        /* Create resources */
        theStringField = new TethysSwingStringTextField();
        theStringField.showButton(true);
        theShortField = new TethysSwingShortTextField(theFormatter, myParser);
        theIntegerField = new TethysSwingIntegerTextField(theFormatter, myParser);
        theLongField = new TethysSwingLongTextField(theFormatter, myParser);
        theMoneyField = new TethysSwingMoneyTextField(theFormatter, myParser);
        thePriceField = new TethysSwingPriceTextField(theFormatter, myParser);
        theDilutedPriceField = new TethysSwingDilutedPriceTextField(theFormatter, myParser);
        theDilutionField = new TethysSwingDilutionTextField(theFormatter, myParser);
        theUnitsField = new TethysSwingUnitsTextField(theFormatter, myParser);
        theRateField = new TethysSwingRateTextField(theFormatter, myParser);
        theRatioField = new TethysSwingRatioTextField(theFormatter, myParser);
        theSource = new JLabel();
        theClass = new JLabel();
        theValue = new JLabel();
        theSelectedValues = new ArrayList<String>();

        /* Create button fields */
        theScrollButtonMgr = new TethysSwingScrollButtonManager<String>();
        theScrollField = new TethysSwingScrollButtonField<String>(theScrollButtonMgr);
        theIconButtonMgr = new TethysSwingSimpleIconButtonManager<Boolean>();
        theIconField = new TethysSwingIconButtonField<Boolean>(theIconButtonMgr);
        theListButtonMgr = new TethysSwingListButtonManager<String>();
        theListField = new TethysSwingListButtonField<String>(theListButtonMgr);
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
            JFrame myFrame = new JFrame("SwingEdit Demo");

            /* Create the UI */
            TethysSwingEditUIExample myExample = new TethysSwingEditUIExample();

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
     * @return the panel
     */
    private JPanel buildPanel() {
        /* Create a GridPane for the fields */
        JPanel myFields = buildFieldPane();

        /* Create a ControlPane for the buttons */
        JPanel myControls = buildControlPane();

        /* Create a GridPane for the results */
        JPanel myResults = buildResultsPane();

        /* Create borderPane for the window */
        JPanel myMain = new JPanel();
        myMain.setLayout(new BorderLayout());
        myFields.setBorder(BorderFactory.createTitledBorder("FieldArea"));
        myMain.add(myFields, BorderLayout.CENTER);
        myControls.setBorder(BorderFactory.createTitledBorder("Controls"));
        myMain.add(myControls, BorderLayout.PAGE_START);
        myResults.setBorder(BorderFactory.createTitledBorder("Results"));
        myMain.add(myResults, BorderLayout.PAGE_END);
        myMain.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        myMain.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        /* Return the panel */
        return myMain;
    }

    /**
     * Build field pane.
     * @return the field pane
     */
    private JPanel buildFieldPane() {
        /* Create a GridBagPanel for the results */
        JPanel myPanel = new JPanel();
        TethysSwingGridBagHelper myGridHelper = new TethysSwingGridBagHelper(myPanel);
        myGridHelper.setInsetSize(PADDING);

        /* Create String field line */
        JLabel myLabel = new JLabel("String:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theStringField.getNode());
        theStringField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theStringField, pEvent);
            }
        });

        /* Create Short field line */
        myLabel = new JLabel("Short:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theShortField.getNode());
        theShortField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theShortField, pEvent);
            }
        });

        /* Create Integer field line */
        myLabel = new JLabel("Integer:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theIntegerField.getNode());
        theIntegerField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theIntegerField, pEvent);
            }
        });

        /* Create Long field line */
        myLabel = new JLabel("Long:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theLongField.getNode());
        theLongField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theLongField, pEvent);
            }
        });

        /* Create Money field line */
        myLabel = new JLabel("Money:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theMoneyField.getNode());
        theMoneyField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theMoneyField, pEvent);
            }
        });

        /* Create Price field line */
        myLabel = new JLabel("Price:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, thePriceField.getNode());
        thePriceField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(thePriceField, pEvent);
            }
        });

        /* Create Units field line */
        myLabel = new JLabel("Units:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theUnitsField.getNode());
        theUnitsField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theUnitsField, pEvent);
            }
        });

        /* Create Rate field line */
        myLabel = new JLabel("Rate:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theRateField.getNode());
        theRateField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theRateField, pEvent);
            }
        });

        /* Create Ratio field line */
        myLabel = new JLabel("Ratio:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theRatioField.getNode());
        theRatioField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theRatioField, pEvent);
            }
        });

        /* Create Dilution field line */
        myLabel = new JLabel("Dilution:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theDilutionField.getNode());
        theDilutionField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theDilutionField, pEvent);
            }
        });

        /* Create DilutedPrice field line */
        myLabel = new JLabel("DilutedPrice:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theDilutedPriceField.getNode());
        theDilutedPriceField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                processActionEvent(theDilutedPriceField, pEvent);
            }
        });

        /* Create ScrollButton field line */
        myLabel = new JLabel("ScrollButton:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theScrollField.getNode());
        theScrollField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case TethysDataEditField.ACTION_NEW_VALUE:
                        processActionEvent(theScrollField, pEvent);
                        break;
                    case TethysDataEditField.ACTION_DIALOG_PREPARE:
                        theHelper.buildContextMenu(theScrollButtonMgr.getMenu());
                        break;
                    case TethysDataEditField.ACTION_DIALOG_CANCELLED:
                    default:
                        break;
                }
            }
        });

        /* Create IconButton field line */
        myLabel = new JLabel("IconButton:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theIconField.getNode());
        theHelper.buildSimpleIconState(theIconButtonMgr,
                OPEN_FALSE_ICON,
                OPEN_TRUE_ICON);
        theIconField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case TethysDataEditField.ACTION_NEW_VALUE:
                        processActionEvent(theIconField, pEvent);
                        break;
                    default:
                        break;
                }
            }
        });

        /* Create ListButton field line */
        myLabel = new JLabel("ListButton:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theListField.getNode());
        theListButtonMgr.getMenu().setCloseOnToggle(false);
        theListField.getEventRegistrar().addActionListener(new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                switch (pEvent.getActionId()) {
                    case TethysDataEditField.ACTION_ITEM_TOGGLED:
                        setListValue(pEvent.getDetails(TethysScrollMenuToggleItem.class));
                        processActionEvent(theListField, pEvent);
                        break;
                    case TethysDataEditField.ACTION_DIALOG_PREPARE:
                        theHelper.buildAvailableItems(theListButtonMgr, theSelectedValues);
                        break;
                    case TethysDataEditField.ACTION_DIALOG_CANCELLED:
                    default:
                        break;
                }
            }
        });

        /* Return the panel */
        return myPanel;
    }

    /**
     * Build Result pane.
     * @return the result pane
     */
    private JPanel buildResultsPane() {
        /* Create a GridBagPanel for the results */
        JPanel myPanel = new JPanel();
        TethysSwingGridBagHelper myHelper = new TethysSwingGridBagHelper(myPanel);
        myHelper.setInsetSize(PADDING);

        /* Build the grid */
        JLabel myLabel = new JLabel("Source:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theSource);
        myLabel = new JLabel("Class:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theClass);
        myLabel = new JLabel("Value:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theValue);

        /* Return the panel */
        return myPanel;
    }

    /**
     * Build Control pane.
     * @return the control pane
     */
    private JPanel buildControlPane() {
        /* Create Toggle button for edit mode */
        JToggleButton myEditButton = new JToggleButton("Edit");
        myEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (myEditButton.isSelected()) {
                    myEditButton.setText("Freeze");
                    setEditMode(true);
                } else {
                    myEditButton.setText("Edit");
                    setEditMode(false);
                }
            }
        });

        /* Create ScrollButton button for currency */
        TethysSwingScrollButtonManager<Currency> myCurrencyMgr = new TethysSwingScrollButtonManager<Currency>();
        TethysSwingScrollContextMenu<Currency> myMenu = myCurrencyMgr.getMenu();
        Currency myDefault = Currency.getInstance("GBP");
        myMenu.addItem(myDefault, "Pounds");
        myMenu.addItem(Currency.getInstance("USD"), "Dollars");
        myMenu.addItem(Currency.getInstance("JPY"), "Yen");
        myCurrencyMgr.setValue(myDefault, "Pounds");
        setCurrency(myDefault);
        myCurrencyMgr.getEventRegistrar().addFilteredActionListener(TethysSwingScrollButtonManager.ACTION_NEW_VALUE, new TethysActionEventListener() {
            @Override
            public void processAction(final TethysActionEvent pEvent) {
                setCurrency(pEvent.getDetails(Currency.class));
            }
        });

        /* Create an HBox for buttons */
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        myPanel.add(myEditButton);
        myPanel.add(Box.createHorizontalGlue());
        myPanel.add(myCurrencyMgr.getButton());

        /* Return the panel */
        return myPanel;
    }

    /**
     * Set edit mode.
     * @param pDoEdit true/false
     */
    private void setEditMode(final boolean pDoEdit) {
        /* Set the editable indication */
        theStringField.setEditable(pDoEdit);
        theShortField.setEditable(pDoEdit);
        theIntegerField.setEditable(pDoEdit);
        theLongField.setEditable(pDoEdit);
        theMoneyField.setEditable(pDoEdit);
        thePriceField.setEditable(pDoEdit);
        theUnitsField.setEditable(pDoEdit);
        theRateField.setEditable(pDoEdit);
        theRatioField.setEditable(pDoEdit);
        theDilutionField.setEditable(pDoEdit);
        theDilutedPriceField.setEditable(pDoEdit);
        theScrollField.setEditable(pDoEdit);
        theListField.setEditable(pDoEdit);
        theIconField.setEditable(pDoEdit);
    }

    /**
     * Set default currency.
     * @param pCurrency the default currency
     */
    private void setCurrency(final Currency pCurrency) {
        /* Set the deemed currency */
        theMoneyField.setDeemedCurrency(pCurrency);
        thePriceField.setDeemedCurrency(pCurrency);
        theDilutedPriceField.setDeemedCurrency(pCurrency);
    }

    /**
     * Process action.
     * @param pField the source of the action
     * @param pEvent the event
     */
    private void processActionEvent(final TethysSwingDataTextField<?> pField,
                                    final TethysActionEvent pEvent) {
        /* Determine source */
        String mySource = pField.getClass().getSimpleName();

        /* Switch on action */
        switch (pEvent.getActionId()) {
            case TethysDataEditField.ACTION_NEW_VALUE:
            case TethysDataEditField.ACTION_ITEM_TOGGLED:
                setResults(mySource, pEvent.getDetails());
                break;
            case TethysDataEditField.ACTION_NEW_COMMAND:
                setResults(mySource + "-Cmd", pEvent.getDetails());
                break;
            case TethysDataEditField.ACTION_COMMAND_PREPARE:
                pField.getCmdMenu().removeAllItems();
                pField.getCmdMenu().addItem("TestCmd");
                break;
            default:
                break;
        }
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
    }

    /**
     * Set the results.
     * @param pSource the source of the results
     * @param pResults the results
     */
    private void setResults(final String pSource,
                            final Object pResults) {
        /* Record the source */
        theSource.setText(pSource);

        /* Record class of results */
        theClass.setText(pResults == null
                                          ? "Null"
                                          : pResults.getClass().getSimpleName());

        /* Record results */
        if (pResults instanceof String) {
            theValue.setText((String) pResults);
        } else if (pResults instanceof Short) {
            theValue.setText(theFormatter.formatShort((Short) pResults));
        } else if (pResults instanceof Integer) {
            theValue.setText(theFormatter.formatInteger((Integer) pResults));
        } else if (pResults instanceof Long) {
            theValue.setText(theFormatter.formatLong((Long) pResults));
        } else if (pResults instanceof TethysDecimal) {
            theValue.setText(theFormatter.formatDecimal((TethysDecimal) pResults));
        } else if (pResults instanceof Boolean) {
            theValue.setText(pResults.toString());
        } else if (pResults instanceof TethysScrollMenuItem) {
            theValue.setText(((TethysScrollMenuItem<?>) pResults).getText());
        } else {
            theValue.setText(null);
        }
    }
}
