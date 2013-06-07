/*******************************************************************************
[] * jPreferenceSet: PreferenceSet Management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jPreferenceSet;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayButton;
import net.sourceforge.jOceanus.jEventManager.JEnableWrapper.JEnablePanel;
import net.sourceforge.jOceanus.jEventManager.JEventPanel;
import net.sourceforge.jOceanus.jFieldSet.JFieldManager;
import net.sourceforge.jOceanus.jLayoutManager.GridBagUtilities;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet.BooleanPreference;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet.ColorPreference;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet.DatePreference;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet.EnumPreference;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet.IntegerPreference;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet.PreferenceItem;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet.PreferenceType;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet.StringPreference;
import net.sourceforge.jOceanus.jPreferenceSet.ValueField.ValueClass;

/**
 * Preference Set panel.
 * @author Tony Washer
 */
public class PreferenceSetPanel
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -713132970269487546L;

    /**
     * String width.
     */
    private static final int WIDTH_STRING = 60;

    /**
     * Integer width.
     */
    private static final int WIDTH_INTEGER = 10;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PreferenceSetPanel.class.getName());

    /**
     * Text for Preferences Title.
     */
    private static final String NLS_PREFERENCES = NLS_BUNDLE.getString("PreferencesTitle");

    /**
     * Text for Options Title.
     */
    private static final String NLS_OPTIONS = NLS_BUNDLE.getString("OptionsTitle");

    /**
     * Title for Colour Dialog.
     */
    private static final String NLS_COLORTITLE = NLS_BUNDLE.getString("ColourTitle");

    /**
     * Text for Colour Button.
     */
    private static final String NLS_COLORTEXT = NLS_BUNDLE.getString("ColourText");

    /**
     * The PreferenceSet for this panel.
     */
    private final transient PreferenceSet thePreferences;

    /**
     * The Self Reference.
     */
    private final PreferenceSetPanel theSelf = this;

    /**
     * The individual preference elements.
     */
    private final transient List<PreferenceElement> theElList;

    /**
     * The list of components to enable.
     */
    private transient List<Component> theCompList;

    /**
     * The FieldManager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * General formatter.
     */
    private final transient JDataFormatter theFormatter;

    /**
     * The Set name.
     */
    private String theName = null;

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pSet the preference set
     */
    public PreferenceSetPanel(final JFieldManager pFieldMgr,
                              final PreferenceSet pSet) {
        /* Options SubPanel */
        JEnablePanel myOptions = null;
        int myRow = 0;

        /* Record the set and manager */
        thePreferences = pSet;
        theFieldMgr = pFieldMgr;
        theFormatter = pFieldMgr.getDataFormatter();

        /* Record the name of the set */
        theName = pSet.getClass().getSimpleName();

        /* Create the lists of elements and items */
        theElList = new ArrayList<PreferenceElement>();
        theCompList = new ArrayList<Component>();

        /* Set a border */
        setBorder(BorderFactory.createTitledBorder(NLS_PREFERENCES));

        /* Set the layout for this panel */
        setLayout(new GridBagLayout());
        GridBagConstraints myConstraints = new GridBagConstraints();

        /* Loop through the preferences */
        for (PreferenceItem myPref : pSet.getPreferences()) {
            /* Create the field */
            PreferenceElement myItem = new PreferenceElement(myPref);

            /* Add it to the list */
            theElList.add(myItem);

            /* Switch on the preference type */
            switch (myPref.getType()) {
                case Boolean:
                    /* If we do not yet have an options panel */
                    if (myOptions == null) {
                        /* Create options */
                        myOptions = new JEnablePanel();
                        myOptions.setLayout(new FlowLayout(FlowLayout.LEADING));
                        myOptions.setBorder(BorderFactory.createTitledBorder(NLS_OPTIONS));
                        theCompList.add(myOptions);
                    }

                    /* Add the item to the options panel */
                    myOptions.add(myItem.getComponent());
                    break;
                case String:
                    /* Add the Label into the first slot */
                    GridBagUtilities.setPanelLabel(myConstraints, myRow, GridBagConstraints.NONE);
                    add(myItem.getLabel(), myConstraints);

                    /* Add the Component into the second slot */
                    GridBagUtilities.setPanelField(myConstraints, myRow++, 1, GridBagConstraints.REMAINDER);
                    add(myItem.getComponent(), myConstraints);
                    theCompList.add(myItem.getComponent());
                    break;
                case Directory:
                case File:
                    /* Add the Label into the first slot */
                    GridBagUtilities.setPanelLabel(myConstraints, myRow, GridBagConstraints.HORIZONTAL);
                    add(myItem.getLabel(), myConstraints);
                    theCompList.add(myItem.getLabel());

                    /* Add the Component into the second slot */
                    GridBagUtilities.setPanelField(myConstraints, myRow++, 1, GridBagConstraints.REMAINDER);
                    add(myItem.getComponent(), myConstraints);
                    theCompList.add(myItem.getComponent());
                    break;
                default:
                    /* Add the Label into the first slot */
                    GridBagUtilities.setPanelLabel(myConstraints, myRow, GridBagConstraints.NONE);
                    add(myItem.getLabel(), myConstraints);

                    /* Add the Component into the second slot */
                    GridBagUtilities.setPanelField(myConstraints, myRow, 1, 1);
                    add(myItem.getComponent(), myConstraints);
                    theCompList.add(myItem.getComponent());

                    /* Add padding to the remainder */
                    GridBagUtilities.setPanelField(myConstraints, myRow++, 2, GridBagConstraints.REMAINDER);
                    add(new JLabel(), myConstraints);
                    break;
            }
        }

        /* If we have an options panel */
        if (myOptions != null) {
            /* Add the Label into the first slot */
            GridBagUtilities.setPanelRow(myConstraints, myRow);
            add(myOptions, myConstraints);
        }
    }

    /**
     * Does the Preference Set have changes.
     * @return does the set have changes
     */
    public boolean hasChanges() {
        return thePreferences.hasChanges();
    }

    /**
     * Reset changes.
     */
    public void resetChanges() {
        /* Reset changes and clear flag */
        thePreferences.resetChanges();

        /* Update the fields */
        updateFields();
    }

    /**
     * Store changes.
     * @throws JDataException on error
     */
    public void storeChanges() throws JDataException {
        /* Reset changes and clear flag */
        thePreferences.storeChanges();

        /* Update the fields */
        updateFields();
    }

    /**
     * Update fields.
     */
    private void updateFields() {
        /* Loop through the fields */
        for (PreferenceElement myItem : theElList) {
            /* Update the field */
            myItem.updateField();
        }
    }

    /**
     * Notify changes.
     */
    private void notifyChanges() {
        /* Update the fields */
        updateFields();

        /* Notify listeners */
        fireStateChanged();
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Loop through the registered components */
        for (Component myComp : theCompList) {
            /* Pass call on */
            myComp.setEnabled(bEnabled);
        }
    }

    /**
     * A Preference element.
     */
    private final class PreferenceElement {
        /**
         * The preferenceItem.
         */
        private final PreferenceItem thePreference;

        /**
         * The preference type.
         */
        private final PreferenceType theType;

        /**
         * Underlying field.
         */
        private final PreferenceField theField;

        /**
         * The label for the preference.
         */
        private final JLabel theLabel;

        /**
         * Constructor.
         * @param pPreference the preference
         */
        private PreferenceElement(final PreferenceItem pPreference) {
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
                    theLabel = new JLabel(thePreference.getDisplay()
                                          + ":");
                    theLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
            }

            /* Switch on field type */
            switch (theType) {
            /* Create the Underlying field */
                case String:
                case Directory:
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
                case Color:
                    theField = new ColorField(thePreference);
                    break;
                case Enum:
                    theField = new EnumField(thePreference);
                    break;
                default:
                    theField = null;
                    return;
            }

            /* Initialise the field */
            theField.updateField();
        }

        /**
         * Update the field.
         */
        protected void updateField() {
            theField.updateField();
        }

        /**
         * Obtain label.
         * @return the label
         */
        protected JComponent getLabel() {
            return theField.getLabel();
        }

        /**
         * Obtain component.
         * @return the component
         */
        protected JComponent getComponent() {
            return theField.getComponent();
        }

        /**
         * Abstract preference field.
         */
        private abstract class PreferenceField {
            /**
             * Obtain component.
             * @return the component
             */
            protected abstract JComponent getComponent();

            /**
             * Obtain label.
             * @return the label
             */
            protected JComponent getLabel() {
                return theLabel;
            }

            /**
             * Update the field value and adjust rendering.
             */
            protected abstract void updateField();
        }

        /**
         * StringField class.
         */
        private final class StringField
                extends PreferenceField {
            /**
             * The underlying value field.
             */
            private final ValueField theField;

            /**
             * The preference as a stringPreference.
             */
            private final StringPreference theString;

            /**
             * The button for the preference.
             */
            private JButton theButton = null;

            @Override
            protected JComponent getLabel() {
                return (theType == PreferenceType.String)
                        ? super.getLabel()
                        : theButton;
            }

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private StringField(final PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theString = (StringPreference) pPreference;
                theField = new ValueField(ValueClass.String, theFormatter);
                theField.setColumns(WIDTH_STRING);

                /* Add property change listener */
                PreferenceListener myListener = new PreferenceListener();
                theField.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);

                /* If the preference type is string we have finished */
                if (pPreference.getType() == PreferenceType.String) {
                    return;
                }

                /* Create a button */
                theButton = new JButton(pPreference.getDisplay());
                theButton.addActionListener(myListener);
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setValue(theString.getValue());

                /* Set font and foreground */
                theField.setForeground(theFieldMgr.getForeground(thePreferences, theString.getDataField()));
                theField.setFont(theFieldMgr.determineFont(thePreferences, theString.getDataField(), false));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class.
             */
            private class PreferenceListener
                    implements PropertyChangeListener, ActionListener {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if (theField.equals(o)) {
                        /* Set the new value of the preference */
                        String myValue = (String) theField.getValue();
                        theString.setValue(myValue);

                        /* Note if we have any changes */
                        notifyChanges();
                    }
                }

                @Override
                public void actionPerformed(final ActionEvent evt) {
                    Object o = evt.getSource();

                    /* If this is our button */
                    if (theButton.equals(o)) {
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
                            default:
                                break;
                        }
                    }
                }
            }
        }

        /**
         * IntegerField class.
         */
        private final class IntegerField
                extends PreferenceField {
            /**
             * The underlying value field.
             */
            private final ValueField theField;

            /**
             * The preference as an integerPreference.
             */
            private final IntegerPreference theInteger;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private IntegerField(final PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theInteger = (IntegerPreference) pPreference;
                theField = new ValueField(ValueClass.Integer, theFormatter);
                theField.setColumns(WIDTH_INTEGER);

                /* Add property change listener */
                theField.addPropertyChangeListener(ValueField.PROPERTY_VALUE, new PreferenceListener());
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setValue(theInteger.getValue());

                /* Set font and foreground */
                theField.setForeground(theFieldMgr.getForeground(thePreferences, theInteger.getDataField()));
                theField.setFont(theFieldMgr.determineFont(thePreferences, theInteger.getDataField(), true));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class.
             */
            private class PreferenceListener
                    implements PropertyChangeListener {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if (theField.equals(o)) {
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
         * BooleanField class.
         */
        private final class BooleanField
                extends PreferenceField {
            /**
             * The underlying button field.
             */
            private final JCheckBox theField;

            /**
             * The preference as a booleanPreference.
             */
            private final BooleanPreference theBoolean;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private BooleanField(final PreferenceItem pPreference) {
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
                theField.setForeground(theFieldMgr.getForeground(thePreferences, theBoolean.getDataField()));
                theField.setFont(theFieldMgr.determineFont(thePreferences, theBoolean.getDataField(), false));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class.
             */
            private class PreferenceListener
                    implements ItemListener {

                @Override
                public void itemStateChanged(final ItemEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if (theField.equals(o)) {
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
         * DateField class.
         */
        private final class DateField
                extends PreferenceField {
            /**
             * The underlying button field.
             */
            private final JDateDayButton theField;

            /**
             * The preference as a datePreference.
             */
            private final DatePreference theDate;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private DateField(final PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theDate = (DatePreference) pPreference;
                theField = new JDateDayButton();

                /* Add property change listener */
                theField.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, new PreferenceListener());
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setSelectedDateDay(theDate.getValue());

                /* Set font and foreground */
                theField.setForeground(theFieldMgr.getForeground(thePreferences, theDate.getDataField()));
                theField.setFont(theFieldMgr.determineFont(thePreferences, theDate.getDataField(), false));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class.
             */
            private final class PreferenceListener
                    implements PropertyChangeListener {

                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    Object o = evt.getSource();

                    /* If this is our preference */
                    if (theField.equals(o)) {
                        /* Set the new value of the preference */
                        JDateDay myValue = new JDateDay(theField.getSelectedDate());
                        theDate.setValue(myValue);

                        /* Note if we have any changes */
                        notifyChanges();
                    }
                }
            }
        }

        /**
         * ColorField class.
         */
        private final class ColorField
                extends PreferenceField {
            /**
             * The underlying button field.
             */
            private final JButton theField;

            /**
             * The preference as a colorPreference.
             */
            private final ColorPreference theColor;

            /**
             * The colour chooser.
             */
            private final JColorChooser theChooser;

            /**
             * The colour chooser.
             */
            private final JDialog theDialog;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private ColorField(final PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theColor = (ColorPreference) pPreference;
                theField = new JButton();

                /* Create listener */
                PreferenceListener myListener = new PreferenceListener();

                /* Create chooser and dialog */
                theChooser = new JColorChooser();
                theDialog = JColorChooser.createDialog(theField, NLS_COLORTITLE, true, theChooser, myListener, null);

                /* Add action listener */
                theField.addActionListener(myListener);
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setText(NLS_COLORTEXT);

                /* Set font and foreground */
                theField.setForeground(theColor.getValue());
                theField.setFont(theFieldMgr.determineFont(thePreferences, theColor.getDataField(), false));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PreferenceListener class.
             */
            private final class PreferenceListener
                    implements ActionListener {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    Object o = e.getSource();

                    /* If this is the button */
                    if (theField.equals(o)) {
                        /* Position the dialog just right of the button */
                        Point myPanelLoc = getLocationOnScreen();
                        Point myButtonLoc = theField.getLocationOnScreen();
                        theDialog.setLocation(myButtonLoc.x
                                              + theField.getWidth(), myPanelLoc.y);
                        theChooser.setColor(theColor.getValue());

                        /* Show the dialog */
                        theDialog.setVisible(true);

                        /* else if this is the dialog */
                    } else {
                        /* Record the colour */
                        Color myColor = theChooser.getColor();
                        theColor.setValue(myColor);

                        /* Note if we have any changes */
                        notifyChanges();
                    }
                }
            }
        }

        /**
         * EnumField class.
         */
        private final class EnumField
                extends PreferenceField {
            /**
             * The underlying combo box field.
             */
            private final JComboBox<String> theField;

            /**
             * The preference as an EnumPreference.
             */
            private final EnumPreference<?> theEnum;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private EnumField(final PreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theEnum = (EnumPreference<?>) pPreference;
                theField = new JComboBox<String>();

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
                theField.setSelectedItem(theEnum.getValue().name());

                /* Set font and foreground */
                theField.setForeground(theFieldMgr.getForeground(thePreferences, theEnum.getDataField()));
                theField.setFont(theFieldMgr.determineFont(thePreferences, theEnum.getDataField(), false));
            }

            @Override
            protected JComponent getComponent() {
                return theField;
            }

            /**
             * PropertyListener class.
             */
            private final class PreferenceListener
                    implements ItemListener {

                @Override
                public void itemStateChanged(final ItemEvent evt) {
                    Object o = evt.getSource();
                    /* If this is our preference */
                    if ((theField.equals(o))
                        && (evt.getStateChange() == ItemEvent.SELECTED)) {
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
