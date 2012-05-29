/*******************************************************************************
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
package net.sourceforge.JDataManager.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.JDataManager.PreferenceSet;
import net.sourceforge.JDataManager.PreferenceSet.BooleanPreference;
import net.sourceforge.JDataManager.PreferenceSet.DatePreference;
import net.sourceforge.JDataManager.PreferenceSet.EnumPreference;
import net.sourceforge.JDataManager.PreferenceSet.IntegerPreference;
import net.sourceforge.JDataManager.PreferenceSet.PreferenceItem;
import net.sourceforge.JDataManager.PreferenceSet.PreferenceType;
import net.sourceforge.JDataManager.PreferenceSet.StringPreference;
import net.sourceforge.JDataManager.ui.ValueField.ValueClass;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayButton;
import uk.co.tolcroft.models.ui.StdInterfaces.stdPanel;

public class PreferenceSetPanel extends JPanel {
    /* Serial Id */
    private static final long serialVersionUID = -713132970269487546L;

    /**
     * The Parent for this panel
     */
    private final stdPanel theParent;

    /**
     * The PreferenceSet for this panel
     */
    private final PreferenceSet thePreferences;

    /**
     * The Self Reference
     */
    private final PreferenceSetPanel theSelf = this;

    /**
     * The individual preference elements
     */
    private List<PreferenceElement> theList = null;

    /**
     * The Set name
     */
    private String theName = null;

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Constructor
     * @param pParent the parent
     * @param pSet the preference set
     */
    public PreferenceSetPanel(stdPanel pParent,
                              PreferenceSet pSet) {
        /* Options SubPanel */
        JPanel myOptions = null;
        int myRow = 0;

        /* Record the set */
        thePreferences = pSet;

        /* Record the name of the set */
        theName = pSet.getClass().getSimpleName();

        /* Create the list of items */
        theList = new ArrayList<PreferenceElement>();

        /* Set a border */
        setBorder(javax.swing.BorderFactory.createTitledBorder("Preferences"));

        /* Set the layout for this panel */
        setLayout(new GridBagLayout());
        GridBagConstraints myConstraints = new GridBagConstraints();

        /* Loop through the preferences */
        for (PreferenceItem myPref : pSet.getPreferences()) {
            /* Create the field */
            PreferenceElement myItem = new PreferenceElement(myPref);

            /* Add it to the list */
            theList.add(myItem);

            /* Switch on the preference type */
            switch (myPref.getType()) {
                case Boolean:
                    /* If we do not yet have an options panel */
                    if (myOptions == null) {
                        /* Create options */
                        myOptions = new JPanel();
                        myOptions.setLayout(new FlowLayout(FlowLayout.LEADING));
                        myOptions.setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));
                    }

                    /* Add the item to the options panel */
                    myOptions.add(myItem.getComponent());
                    break;
                case String:
                    /* Add the Label into the first slot */
                    myConstraints.gridx = 0;
                    myConstraints.gridy = myRow;
                    myConstraints.gridwidth = 1;
                    myConstraints.fill = GridBagConstraints.NONE;
                    myConstraints.weightx = 0.0;
                    myConstraints.anchor = GridBagConstraints.LINE_END;
                    myConstraints.insets = new Insets(5, 5, 5, 5);
                    add(myItem.getLabel(), myConstraints);

                    /* Add the Component into the second slot */
                    myConstraints.gridx = 1;
                    myConstraints.gridy = myRow++;
                    myConstraints.gridwidth = GridBagConstraints.REMAINDER;
                    myConstraints.fill = GridBagConstraints.HORIZONTAL;
                    myConstraints.weightx = 1.0;
                    myConstraints.anchor = GridBagConstraints.LINE_START;
                    myConstraints.insets = new Insets(5, 5, 5, 5);
                    add(myItem.getComponent(), myConstraints);
                    break;
                case Directory:
                case File:
                    /* Add the Label into the first slot */
                    myConstraints.gridx = 0;
                    myConstraints.gridy = myRow;
                    myConstraints.gridwidth = 1;
                    myConstraints.fill = GridBagConstraints.HORIZONTAL;
                    myConstraints.weightx = 0.0;
                    myConstraints.anchor = GridBagConstraints.LINE_END;
                    myConstraints.insets = new Insets(5, 5, 5, 5);
                    add(myItem.getLabel(), myConstraints);

                    /* Add the Component into the second slot */
                    myConstraints.gridx = 1;
                    myConstraints.gridy = myRow++;
                    myConstraints.gridwidth = GridBagConstraints.REMAINDER;
                    myConstraints.fill = GridBagConstraints.HORIZONTAL;
                    myConstraints.weightx = 1.0;
                    myConstraints.anchor = GridBagConstraints.LINE_START;
                    myConstraints.insets = new Insets(5, 5, 5, 5);
                    add(myItem.getComponent(), myConstraints);
                    break;
                default:
                    /* Add the Label into the first slot */
                    myConstraints.gridx = 0;
                    myConstraints.gridy = myRow;
                    myConstraints.gridwidth = 1;
                    myConstraints.fill = GridBagConstraints.NONE;
                    myConstraints.weightx = 0.0;
                    myConstraints.anchor = GridBagConstraints.LINE_END;
                    myConstraints.insets = new Insets(5, 5, 5, 5);
                    add(myItem.getLabel(), myConstraints);

                    /* Add the Component into the second slot */
                    myConstraints.gridx = 1;
                    myConstraints.gridy = myRow;
                    myConstraints.gridwidth = 1;
                    myConstraints.fill = GridBagConstraints.NONE;
                    myConstraints.weightx = 0.0;
                    myConstraints.anchor = GridBagConstraints.LINE_START;
                    myConstraints.insets = new Insets(5, 5, 5, 5);
                    add(myItem.getComponent(), myConstraints);

                    /* Add the Component into the second slot */
                    myConstraints.gridx = 2;
                    myConstraints.gridy = myRow++;
                    myConstraints.gridwidth = GridBagConstraints.REMAINDER;
                    myConstraints.fill = GridBagConstraints.HORIZONTAL;
                    myConstraints.weightx = 1.0;
                    myConstraints.anchor = GridBagConstraints.LINE_START;
                    myConstraints.insets = new Insets(5, 5, 5, 5);
                    add(new JLabel(), myConstraints);
                    break;
            }
        }

        /* If we have an options panel */
        if (myOptions != null) {
            /* Add the Label into the first slot */
            myConstraints.gridx = 0;
            myConstraints.gridy = myRow++;
            myConstraints.gridwidth = GridBagConstraints.REMAINDER;
            myConstraints.fill = GridBagConstraints.HORIZONTAL;
            myConstraints.weightx = 1.0;
            myConstraints.anchor = GridBagConstraints.LINE_START;
            myConstraints.insets = new Insets(5, 5, 5, 5);
            add(myOptions, myConstraints);
        }

        /* Declare the parent */
        theParent = pParent;
    }

    /**
     * Does the Preference Set have changes
     * @return does the set have changes
     */
    public boolean hasChanges() {
        return thePreferences.hasChanges();
    }

    /**
     * Reset changes
     */
    public void resetChanges() {
        /* Reset changes and clear flag */
        thePreferences.resetChanges();

        /* Update the fields */
        updateFields();
    }

    /**
     * Store changes
     * @throws Exception
     */
    public void storeChanges() throws Exception {
        /* Reset changes and clear flag */
        thePreferences.storeChanges();

        /* Update the fields */
        updateFields();
    }

    /**
     * Update fields
     */
    private void updateFields() {
        /* Loop through the fields */
        for (PreferenceElement myItem : theList) {
            /* Update the field */
            myItem.updateField();
        }
    }

    /**
     * Notify changes
     */
    private void notifyChanges() {
        /* Update the fields */
        updateFields();

        /* Notify the parent */
        if (theParent != null)
            theParent.notifyChanges();
    }

    /**
     * A Preference element
     */
    private class PreferenceElement {
        /**
         * The preferenceItem
         */
        private final PreferenceItem thePreference;

        /**
         * The preference type
         */
        private final PreferenceType theType;

        /**
         * Underlying field
         */
        private final PreferenceField theField;

        /**
         * The label for the preference
         */
        private final JLabel theLabel;

        /**
         * Constructor
         * @param pPreference the preference
         */
        private PreferenceElement(PreferenceItem pPreference) {
            /* Store the reference to the preference */
            thePreference = pPreference;
            theType = pPreference.getType();

            /* Create the label item */
            switch (theType) {
                case Boolean:
                    theLabel = null;
                    break;
                default:
                    /* Create the label */
                    theLabel = new JLabel(thePreference.getDisplay() + ":");
                    theLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
            }

            /* Switch on field type */
            switch (theType) {
            /* Create the Underlying field */
                case String:
                    theField = new StringField(thePreference);
                    break;
                case Directory:
                    theField = new StringField(thePreference);
                    break;
                case File:
                    theField = new StringField(thePreference);
                    break;
                case Integer:
                    theField = new IntegerField(thePreference);
                    break;
                case Boolean:
                    theField = new BooleanField(thePreference);
                    break;
                case Date:
                    theField = new DateField(thePreference);
                    break;
                case Enum:
                    theField = new EnumField(thePreference);
                    break;
                default:
                    theField = null;
            }

            /* Initialise the field */
            theField.updateField();
        }

        /**
         * Update the field
         */
        protected void updateField() {
            theField.updateField();
        }

        /**
         * Obtain label
         * @return the label
         */
        protected JComponent getLabel() {
            return theField.getLabel();
        }

        /**
         * Obtain component
         * @return the component
         */
        protected JComponent getComponent() {
            return theField.getComponent();
        }

        /**
         * Abstract preference field
         */
        private abstract class PreferenceField {
            /**
             * Obtain component
             * @return the component
             */
            protected abstract JComponent getComponent();

            /**
             * Obtain label
             * @return the label
             */
            protected JComponent getLabel() {
                return theLabel;
            }

            /**
             * Update the field value and adjust rendering
             */
            protected abstract void updateField();
        }

        /**
         * StringField class
         */
        private class StringField extends PreferenceField {
            /**
             * The underlying value field
             */
            private final ValueField theField;

            /**
             * The preference as a stringPreference
             */
            private final StringPreference theString;

            /**
             * The button for the preference
             */
            private JButton theButton = null;

            @Override
            protected JComponent getLabel() {
                return (theType == PreferenceType.String) ? super.getLabel() : theButton;
            }

            /**
             * Constructor
             * @param pPreference
             */
            private StringField(PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theString = (StringPreference) pPreference;
                theField = new ValueField();
                theField.setColumns(60);

                /* Add property change listener */
                PreferenceListener myListener = new PreferenceListener();
                theField.addPropertyChangeListener(ValueField.valueName, myListener);

                /* If the preference type is string we have finished */
                if (pPreference.getType() == PreferenceType.String)
                    return;

                /* Create a button */
                theButton = new JButton(pPreference.getDisplay());
                theButton.addActionListener(myListener);
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setValue(theString.getValue());

                /* Set font and foreground */
                theField.setForeground(RenderData.getForeground(theString));
                theField.setFont(RenderData.getFont(theString));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class
             */
            private class PreferenceListener implements PropertyChangeListener, ActionListener {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if (o == theField) {
                        /* Set the new value of the preference */
                        String myValue = (String) theField.getValue();
                        theString.setValue(myValue);

                        /* Note if we have any changes */
                        notifyChanges();
                    }
                }

                @Override
                public void actionPerformed(ActionEvent evt) {
                    Object o = evt.getSource();

                    /* If this is our button */
                    if (o == theButton) {
                        /* Switch on the preference type */
                        switch (theType) {
                        /* If we are a directory preference */
                            case Directory:
                                /* Create and show the dialog */
                                FileSelector myDialog = new FileSelector(theSelf, "Select "
                                        + theString.getDisplay(), new File(theString.getValue()));
                                myDialog.showDialog();

                                /* Handle selection */
                                File myDir = myDialog.getSelectedFile();
                                if (myDir != null) {
                                    /* Set the new value of the preference */
                                    theString.setValue(myDir.getAbsolutePath());

                                    /* Note of any changes */
                                    notifyChanges();
                                }
                                break;

                            /* If we are a file preference */
                            case File:
                                /* Create and show the dialog */
                                FileSelector myFileDialog = new FileSelector(theSelf, "Select "
                                        + theString.getDisplay(), new File(theString.getValue()), null, null);
                                myFileDialog.showDialog();

                                /* Handle selection */
                                File myFile = myFileDialog.getSelectedFile();
                                if (myFile != null) {
                                    /* Set the new value of the preference */
                                    theString.setValue(myFile.getAbsolutePath());

                                    /* Note of any changes */
                                    notifyChanges();
                                }
                                break;
                        }
                    }
                }
            }
        }

        /**
         * IntegerField class
         */
        private class IntegerField extends PreferenceField {
            /**
             * The underlying value field
             */
            private final ValueField theField;

            /**
             * The preference as an integerPreference
             */
            private final IntegerPreference theInteger;

            /**
             * Constructor
             * @param pPreference
             */
            private IntegerField(PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theInteger = (IntegerPreference) pPreference;
                theField = new ValueField(ValueClass.Integer);
                theField.setColumns(10);

                /* Add property change listener */
                theField.addPropertyChangeListener(ValueField.valueName, new PreferenceListener());
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setValue(theInteger.getValue());

                /* Set font and foreground */
                theField.setForeground(RenderData.getForeground(theInteger));
                theField.setFont(RenderData.getFont(theInteger));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class
             */
            private class PreferenceListener implements PropertyChangeListener {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if (o == theField) {
                        /* Set the new value of the preference */
                        Integer myValue = (Integer) theField.getValue();
                        theInteger.setValue(myValue);

                        /* Note if we have any changes */
                        notifyChanges();
                    }
                }
            }
        }

        /**
         * BooleanField class
         */
        private class BooleanField extends PreferenceField {
            /**
             * The underlying button field
             */
            private final JCheckBox theField;

            /**
             * The preference as a booleanPreference
             */
            private final BooleanPreference theBoolean;

            /**
             * Constructor
             * @param pPreference
             */
            private BooleanField(PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theBoolean = (BooleanPreference) pPreference;
                theField = new JCheckBox(pPreference.getName());

                /* Add item listener */
                theField.addItemListener(new PreferenceListener());
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setSelected(theBoolean.getValue());

                /* Set font and foreground */
                theField.setForeground(RenderData.getForeground(theBoolean));
                theField.setFont(RenderData.getFont(theBoolean));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class
             */
            private class PreferenceListener implements ItemListener {

                @Override
                public void itemStateChanged(ItemEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if (o == theField) {
                        /* Set the new value of the preference */
                        Boolean myValue = theField.isSelected();
                        theBoolean.setValue(myValue);

                        /* Note if we have any changes */
                        notifyChanges();
                    }
                }
            }
        }

        /**
         * DateField class
         */
        private class DateField extends PreferenceField {
            /**
             * The underlying button field
             */
            private final DateDayButton theField;

            /**
             * The preference as a datePreference
             */
            private final DatePreference theDate;

            /**
             * Constructor
             * @param pPreference
             */
            private DateField(PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theDate = (DatePreference) pPreference;
                theField = new DateDayButton();

                /* Add property change listener */
                theField.addPropertyChangeListener(DateDayButton.PROPERTY_DATE, new PreferenceListener());
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setSelectedDateDay(theDate.getValue());

                /* Set font and foreground */
                theField.setForeground(RenderData.getForeground(theDate));
                theField.setFont(RenderData.getFont(theDate));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class
             */
            private class PreferenceListener implements PropertyChangeListener {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if (o == theField) {
                        /* Set the new value of the preference */
                        DateDay myValue = new DateDay(theField.getSelectedDate());
                        theDate.setValue(myValue);

                        /* Note if we have any changes */
                        notifyChanges();
                    }
                }
            }
        }

        /**
         * EnumField class
         */
        private class EnumField extends PreferenceField {
            /**
             * The underlying combo box field
             */
            private final JComboBox theField;

            /**
             * The preference as an EnumPreference
             */
            private final EnumPreference<?> theEnum;

            /**
             * Constructor
             * @param pPreference
             */
            private EnumField(PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theEnum = (EnumPreference<?>) pPreference;
                theField = new JComboBox();

                /* For all values */
                for (Enum<?> myEnum : theEnum.getValues()) {
                    /* Add to the combo box */
                    theField.addItem(myEnum.name());
                }

                /* Add item listener */
                theField.addItemListener(new PreferenceListener());
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setSelectedItem(theEnum.getValue());

                /* Set font and foreground */
                theField.setForeground(RenderData.getForeground(theEnum));
                theField.setFont(RenderData.getFont(theEnum));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PropertyListener class
             */
            private class PreferenceListener implements ItemListener {

                @Override
                public void itemStateChanged(ItemEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if (o == theField) {
                        if (evt.getStateChange() == ItemEvent.SELECTED) {
                            /* Set the new value of the preference */
                            String myName = (String) evt.getItem();
                            theEnum.setValue(myName);

                            /* Note if we have any changes */
                            notifyChanges();
                        }
                    }
                }
            }
        }
    }
}
