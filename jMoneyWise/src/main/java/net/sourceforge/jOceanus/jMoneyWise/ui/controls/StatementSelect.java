/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.ui.controls;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import net.sourceforge.jOceanus.jEventManager.JEventPanel;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;

/**
 * Statement type selection panel.
 * @author Tony Washer
 */
public class StatementSelect extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5497562263117308110L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle
            .getBundle(StatementSelect.class.getName());

    /**
     * Text for View Label.
     */
    private static final String NLS_VIEW = NLS_BUNDLE.getString("ViewLabel");

    /**
     * Text for Selection Title.
     */
    private static final String NLS_TITLE = NLS_BUNDLE.getString("StatementTitle");

    /**
     * The State Box.
     */
    private final JComboBox<StatementType> theStateBox;

    /**
     * The Statement type.
     */
    private StatementType theType = null;

    /**
     * Are we refreshing data?
     */
    private boolean refreshingData = false;

    /**
     * Get the selected statement type.
     * @return the statement type
     */
    public StatementType getStatementType() {
        return theType;
    }

    /**
     * Constructor.
     */
    public StatementSelect() {
        /* Create the boxes */
        theStateBox = new JComboBox<StatementType>();

        /* Create the labels */
        JLabel myStateLabel = new JLabel(NLS_VIEW);

        /* Add the listener for item changes */
        StatementListener myListener = new StatementListener();
        theStateBox.addItemListener(myListener);

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myStateLabel);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theStateBox);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Lock/Unlock the selection */
        theStateBox.setEnabled(bEnabled && (theType != StatementType.Null));
    }

    /**
     * setSelection.
     * @param pAccount the account
     */
    public void setSelection(final Account pAccount) {
        /* Note that we are refreshing data */
        refreshingData = true;

        /* If we have state already populated */
        if (theStateBox.getItemCount() > 0) {
            /* Remove the types */
            theStateBox.removeAllItems();
        }

        /* If we have an account */
        if (pAccount != null) {
            /* Add Value if the account is Money/Debt */
            if (pAccount.isMoney() || pAccount.isDebt()) {
                theStateBox.addItem(StatementType.Value);
            } else if (theType == StatementType.Value) {
                theType = StatementType.Null;
            }

            /* Add Units if the account is Priced */
            if (pAccount.isPriced()) {
                theStateBox.addItem(StatementType.Units);
            } else if (theType == StatementType.Units) {
                theType = StatementType.Null;
            }

            /* Always add Extract */
            theStateBox.addItem(StatementType.Extract);

            /* If we have no type */
            if (theType == StatementType.Null) {
                /* Select the default type for the account */
                if (pAccount.isMoney()) {
                    theType = StatementType.Value;
                } else if (pAccount.isDebt()) {
                    theType = StatementType.Value;
                } else if (pAccount.isPriced()) {
                    theType = StatementType.Units;
                } else {
                    theType = StatementType.Extract;
                }
            }

            /* Select the correct type */
            theStateBox.setSelectedItem(theType);

            /* Else we have no selected type */
        } else {
            theType = StatementType.Null;
        }

        /* Enable/Disable the box */
        setEnabled(true);

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * TaxYear Listener class.
     */
    private final class StatementListener implements ItemListener {
        /* ItemStateChanged listener event */
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the Statement box */
            if ((evt.getSource() == theStateBox) && (evt.getStateChange() == ItemEvent.SELECTED)) {
                /* If the type has changed */
                StatementType myType = (StatementType) evt.getItem();
                if (!theType.equals(myType)) {
                    /* Record it and alert listeners */
                    theType = myType;
                    fireStateChanged();
                }
            }
        }
    }

    /**
     * Statement Types.
     */
    public enum StatementType {
        /**
         * Null type.
         */
        Null,

        /**
         * Extract type.
         */
        Extract,

        /**
         * Value Type.
         */
        Value,

        /**
         * Units Type.
         */
        Units;
    }
}
