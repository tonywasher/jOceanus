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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingGuiUtils;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysItemList;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollUITestHelper;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataButtonField.TethysSwingDateButtonField;
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
    private final TethysScrollUITestHelper<JComponent, Icon> theHelper;

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
     * The date button manager.
     */
    private final TethysSwingDateButtonManager theDateButtonMgr;

    /**
     * The date button field.
     */
    private final TethysSwingDateButtonField theDateField;

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
     * The Date formatter.
     */
    private final TethysDateFormatter theDateFormatter;

    /**
     * The Decimal formatter.
     */
    private final TethysDecimalFormatter theDecimalFormatter;

    /**
     * The selected list values.
     */
    private final List<String> theSelectedValues;

    /**
     * Constructor.
     */
    public TethysSwingEditUIExample() {
        /* Create helper */
        theHelper = new TethysScrollUITestHelper<>();

        /* Create formatter */
        TethysDataFormatter myFormatter = new TethysDataFormatter();
        theDecimalFormatter = myFormatter.getDecimalFormatter();
        theDateFormatter = myFormatter.getDateFormatter();

        /* Create resources */
        theStringField = new TethysSwingStringTextField();
        theStringField.showCmdButton(true);
        theShortField = new TethysSwingShortTextField(myFormatter);
        theIntegerField = new TethysSwingIntegerTextField(myFormatter);
        theLongField = new TethysSwingLongTextField(myFormatter);
        theMoneyField = new TethysSwingMoneyTextField(myFormatter);
        thePriceField = new TethysSwingPriceTextField(myFormatter);
        theDilutedPriceField = new TethysSwingDilutedPriceTextField(myFormatter);
        theDilutionField = new TethysSwingDilutionTextField(myFormatter);
        theUnitsField = new TethysSwingUnitsTextField(myFormatter);
        theRateField = new TethysSwingRateTextField(myFormatter);
        theRatioField = new TethysSwingRatioTextField(myFormatter);
        theSource = new JLabel();
        theClass = new JLabel();
        theValue = new JLabel();
        theSelectedValues = new ArrayList<String>();

        /* Create button fields */
        theScrollButtonMgr = new TethysSwingScrollButtonManager<String>();
        theScrollField = new TethysSwingScrollButtonField<String>(theScrollButtonMgr);
        theDateButtonMgr = new TethysSwingDateButtonManager(myFormatter);
        theDateField = new TethysSwingDateButtonField(theDateButtonMgr);
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
        theStringField.getEventRegistrar().addEventListener(e -> processActionEvent(theStringField, e));
        theStringField.setValue("Test");

        /* Create Short field line */
        myLabel = new JLabel("Short:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theShortField.getNode());
        theShortField.getEventRegistrar().addEventListener(e -> processActionEvent(theShortField, e));
        theShortField.setValue((short) 1);

        /* Create Integer field line */
        myLabel = new JLabel("Integer:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theIntegerField.getNode());
        theIntegerField.getEventRegistrar().addEventListener(e -> processActionEvent(theIntegerField, e));
        theIntegerField.setValue(2);

        /* Create Long field line */
        myLabel = new JLabel("Long:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theLongField.getNode());
        theLongField.getEventRegistrar().addEventListener(e -> processActionEvent(theLongField, e));
        theLongField.setValue((long) 3);

        /* Create Money field line */
        myLabel = new JLabel("Money:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theMoneyField.getNode());
        theMoneyField.getEventRegistrar().addEventListener(e -> processActionEvent(theMoneyField, e));
        theMoneyField.setValue(new TethysMoney("12.45"));

        /* Create Price field line */
        myLabel = new JLabel("Price:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, thePriceField.getNode());
        thePriceField.getEventRegistrar().addEventListener(e -> processActionEvent(thePriceField, e));
        thePriceField.setValue(new TethysPrice("2.2"));

        /* Create Units field line */
        myLabel = new JLabel("Units:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theUnitsField.getNode());
        theUnitsField.getEventRegistrar().addEventListener(e -> processActionEvent(theUnitsField, e));
        theUnitsField.setValue(new TethysUnits("1"));

        /* Create Rate field line */
        myLabel = new JLabel("Rate:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theRateField.getNode());
        theRateField.getEventRegistrar().addEventListener(e -> processActionEvent(theRateField, e));
        theRateField.setValue(new TethysRate(".10"));

        /* Create Ratio field line */
        myLabel = new JLabel("Ratio:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theRatioField.getNode());
        theRatioField.getEventRegistrar().addEventListener(e -> processActionEvent(theRatioField, e));
        theRatioField.setValue(new TethysRatio("1.6"));

        /* Create Dilution field line */
        myLabel = new JLabel("Dilution:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theDilutionField.getNode());
        theDilutionField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutionField, e));
        theDilutionField.setValue(new TethysDilution("0.5"));

        /* Create DilutedPrice field line */
        myLabel = new JLabel("DilutedPrice:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theDilutedPriceField.getNode());
        theDilutedPriceField.getEventRegistrar().addEventListener(e -> processActionEvent(theDilutedPriceField, e));

        /* Create ScrollButton field line */
        myLabel = new JLabel("ScrollButton:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theScrollField.getNode());
        theScrollField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theScrollField, e));
        theScrollField.getEventRegistrar().addEventListener(TethysUIEvent.PREPAREDIALOG, e -> theHelper.buildContextMenu(theScrollButtonMgr.getMenu()));
        theScrollField.setValue("First");

        /* Create DateButton field line */
        myLabel = new JLabel("DateButton:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theDateField.getNode());
        theDateField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theDateField, e));
        theDateField.setValue(new TethysDate());

        /* Create IconButton field line */
        myLabel = new JLabel("IconButton:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theIconField.getNode());
        theHelper.buildSimpleIconState(theIconButtonMgr,
                OPEN_FALSE_ICON,
                OPEN_TRUE_ICON);
        theIconField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theIconField, e));
        theIconField.setValue(false);

        /* Create ListButton field line */
        myLabel = new JLabel("ListButton:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myGridHelper.addFullLabeledRow(myLabel, theListField.getNode());
        theListField.setValue(theHelper.buildToggleList(theListButtonMgr));
        theListField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> processActionEvent(theListField, e));

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
        myCurrencyMgr.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> setCurrency(e.getDetails(Currency.class)));

        /* Create an HBox for buttons */
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new BoxLayout(myPanel, BoxLayout.X_AXIS));
        myPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        myPanel.add(myEditButton);
        myPanel.add(Box.createHorizontalGlue());
        myPanel.add(myCurrencyMgr.getNode());

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
        theDateField.setEditable(pDoEdit);
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
                                    final TethysEvent<TethysUIEvent> pEvent) {
        /* Determine source */
        String mySource = pField.getClass().getSimpleName();

        /* Switch on action */
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                setResults(mySource, pEvent.getDetails());
                break;
            case NEWCOMMAND:
                setResults(mySource + "-Cmd", pEvent.getDetails());
                break;
            case PREPARECMDDIALOG:
                pField.getCmdMenu().removeAllItems();
                pField.getCmdMenu().addItem("TestCmd");
                break;
            default:
                break;
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
            theValue.setText(theDecimalFormatter.formatShort((Short) pResults));
        } else if (pResults instanceof Integer) {
            theValue.setText(theDecimalFormatter.formatInteger((Integer) pResults));
        } else if (pResults instanceof Long) {
            theValue.setText(theDecimalFormatter.formatLong((Long) pResults));
        } else if (pResults instanceof TethysDecimal) {
            theValue.setText(theDecimalFormatter.formatDecimal((TethysDecimal) pResults));
        } else if (pResults instanceof Boolean) {
            theValue.setText(pResults.toString());
        } else if (pResults instanceof TethysItemList) {
            theValue.setText(((TethysItemList<?>) pResults).toString());
        } else if (pResults instanceof TethysDate) {
            theValue.setText(theDateFormatter.formatDateDay((TethysDate) pResults));
        } else {
            theValue.setText(null);
        }
    }
}
