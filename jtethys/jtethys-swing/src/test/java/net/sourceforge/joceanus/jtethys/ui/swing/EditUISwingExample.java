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
import java.util.Currency;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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

import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.DilutedPriceSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.DilutionSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.IntegerSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.LongSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.MoneySwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.PriceSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.RateSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.RatioSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.ShortSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.StringSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.DataSwingEditField.UnitsSwingTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.ScrollSwingButton.ScrollSwingButtonManager;

/**
 * Scroll utilities examples.
 */
public class EditUISwingExample
        extends JApplet {
    /**
     * SerialId.
     */
    private static final long serialVersionUID = -561317220491311954L;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditUISwingExample.class);

    /**
     * The padding.
     */
    private static final int PADDING = 5;

    /**
     * The default height.
     */
    private static final int DEFAULT_HEIGHT = 500;

    /**
     * The default width.
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * The string edit field.
     */
    private final StringSwingTextField theStringField;

    /**
     * The short edit field.
     */
    private final ShortSwingTextField theShortField;

    /**
     * The integer edit field.
     */
    private final IntegerSwingTextField theIntegerField;

    /**
     * The long edit field.
     */
    private final LongSwingTextField theLongField;

    /**
     * The money edit field.
     */
    private final MoneySwingTextField theMoneyField;

    /**
     * The price edit field.
     */
    private final PriceSwingTextField thePriceField;

    /**
     * The diluted price edit field.
     */
    private final DilutedPriceSwingTextField theDilutedPriceField;

    /**
     * The dilution edit field.
     */
    private final DilutionSwingTextField theDilutionField;

    /**
     * The units edit field.
     */
    private final UnitsSwingTextField theUnitsField;

    /**
     * The rate edit field.
     */
    private final RateSwingTextField theRateField;

    /**
     * The ratio edit field.
     */
    private final RatioSwingTextField theRatioField;

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
    private final JDecimalFormatter theFormatter;

    /**
     * Constructor.
     */
    public EditUISwingExample() {
        /* Create formatters/parsers */
        theFormatter = new JDecimalFormatter();
        JDecimalParser myParser = new JDecimalParser();

        /* Create resources */
        theStringField = new StringSwingTextField();
        theShortField = new ShortSwingTextField(theFormatter, myParser);
        theIntegerField = new IntegerSwingTextField(theFormatter, myParser);
        theLongField = new LongSwingTextField(theFormatter, myParser);
        theMoneyField = new MoneySwingTextField(theFormatter, myParser);
        thePriceField = new PriceSwingTextField(theFormatter, myParser);
        theDilutedPriceField = new DilutedPriceSwingTextField(theFormatter, myParser);
        theDilutionField = new DilutionSwingTextField(theFormatter, myParser);
        theUnitsField = new UnitsSwingTextField(theFormatter, myParser);
        theRateField = new RateSwingTextField(theFormatter, myParser);
        theRatioField = new RatioSwingTextField(theFormatter, myParser);
        theSource = new JLabel();
        theClass = new JLabel();
        theValue = new JLabel();
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
            EditUISwingExample myExample = new EditUISwingExample();

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
        GridBagHelper myHelper = new GridBagHelper(myPanel);
        myHelper.setInsetSize(PADDING);

        /* Create String field line */
        JLabel myLabel = new JLabel("String:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theStringField.getNode());
        theStringField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("String", pEvent.getDetails());
            }
        });

        /* Create Short field line */
        myLabel = new JLabel("Short:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theShortField.getNode());
        theShortField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Short", pEvent.getDetails());
            }
        });

        /* Create Integer field line */
        myLabel = new JLabel("Integer:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theIntegerField.getNode());
        theIntegerField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Integer", pEvent.getDetails());
            }
        });

        /* Create Long field line */
        myLabel = new JLabel("Long:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theLongField.getNode());
        theLongField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Long", pEvent.getDetails());
            }
        });

        /* Create Money field line */
        myLabel = new JLabel("Money:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theMoneyField.getNode());
        theMoneyField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Money", pEvent.getDetails());
            }
        });

        /* Create Price field line */
        myLabel = new JLabel("Price:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, thePriceField.getNode());
        thePriceField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Price", pEvent.getDetails());
            }
        });

        /* Create Units field line */
        myLabel = new JLabel("Units:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theUnitsField.getNode());
        theUnitsField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Units", pEvent.getDetails());
            }
        });

        /* Create Rate field line */
        myLabel = new JLabel("Rate:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theRateField.getNode());
        theRateField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Rate", pEvent.getDetails());
            }
        });

        /* Create Ratio field line */
        myLabel = new JLabel("Ratio:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theRatioField.getNode());
        theRatioField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Ratio", pEvent.getDetails());
            }
        });

        /* Create Dilution field line */
        myLabel = new JLabel("Dilution:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theDilutionField.getNode());
        theDilutionField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("Dilution", pEvent.getDetails());
            }
        });

        /* Create DilutedPrice field line */
        myLabel = new JLabel("DilutedPrice:");
        myLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        myHelper.addFullLabeledRow(myLabel, theDilutedPriceField.getNode());
        theDilutedPriceField.getEventRegistrar().addActionListener(new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
                setResults("DilutedPrice", pEvent.getDetails());
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
        GridBagHelper myHelper = new GridBagHelper(myPanel);
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
        ScrollSwingButtonManager<Currency> myCurrencyMgr = new ScrollSwingButtonManager<Currency>();
        ScrollSwingContextMenu<Currency> myMenu = myCurrencyMgr.getMenu();
        Currency myDefault = Currency.getInstance("GBP");
        myMenu.addItem(myDefault, "Pounds");
        myMenu.addItem(Currency.getInstance("USD"), "Dollars");
        myMenu.addItem(Currency.getInstance("JPY"), "Yen");
        myCurrencyMgr.setValue(myDefault, "Pounds");
        setCurrency(myDefault);
        myCurrencyMgr.getEventRegistrar().addFilteredActionListener(ScrollSwingButtonManager.ACTION_NEW_VALUE, new JOceanusActionEventListener() {
            @Override
            public void processActionEvent(final JOceanusActionEvent pEvent) {
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
        } else if (pResults instanceof JDecimal) {
            theValue.setText(theFormatter.formatDecimal((JDecimal) pResults));
        } else {
            theValue.setText(null);
        }
    }
}
