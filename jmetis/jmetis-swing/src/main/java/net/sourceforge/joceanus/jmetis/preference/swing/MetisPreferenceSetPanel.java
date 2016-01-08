/*******************************************************************************
[] * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.preference.swing;

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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceResource;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisBooleanPreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisDatePreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisEnumPreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisIntegerPreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisPreferenceItem;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisStringPreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceType;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceValueClass;
import net.sourceforge.joceanus.jmetis.preference.swing.MetisSwingPreferenceSet.ColorPreference;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.swing.TethysSwingDateButton;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGridBagUtilities;

/**
 * Preference Set panel.
 * @author Tony Washer
 */
public class MetisPreferenceSetPanel
        extends JPanel
        implements TethysEventProvider<MetisPreferenceEvent> {
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
     * Text for Preferences Title.
     */
    private static final String NLS_PREFERENCES = MetisPreferenceResource.UI_TITLE_PREFERENCES.getValue();

    /**
     * Text for Options Title.
     */
    private static final String NLS_OPTIONS = MetisPreferenceResource.UI_TITLE_OPTIONS.getValue();

    /**
     * Title for Colour Dialog.
     */
    private static final String NLS_COLORTITLE = MetisPreferenceResource.UI_TITLE_COLOR.getValue();

    /**
     * Text for Colour Button.
     */
    private static final String NLS_COLORTEXT = MetisPreferenceResource.UI_PROMPT_COLOR.getValue();

    /**
     * Text for Select prompt.
     */
    private static final String NLS_SELECT = MetisPreferenceResource.UI_HEADER_SELECT.getValue();

    /**
     * The Event Manager.
     */
    private final transient TethysEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * The PreferenceSet for this panel.
     */
    private final transient MetisPreferenceSet thePreferences;

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
    private final transient MetisFieldManager theFieldMgr;

    /**
     * General formatter.
     */
    private final transient MetisDataFormatter theFormatter;

    /**
     * The Set name.
     */
    private String theName = null;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pSet the preference set
     */
    public MetisPreferenceSetPanel(final MetisFieldManager pFieldMgr,
                                   final MetisPreferenceSet pSet) {
        /* Options SubPanel */
        TethysSwingEnablePanel myOptions = null;
        int myRow = 0;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Record the set and manager */
        thePreferences = pSet;
        theFieldMgr = pFieldMgr;
        theFormatter = pFieldMgr.getDataFormatter();

        /* Record the name of the set */
        theName = pSet.getClass().getSimpleName();

        /* Create the lists of elements and items */
        theElList = new ArrayList<>();
        theCompList = new ArrayList<>();

        /* Set a border */
        setBorder(BorderFactory.createTitledBorder(NLS_PREFERENCES));

        /* Set the layout for this panel */
        setLayout(new GridBagLayout());
        GridBagConstraints myConstraints = new GridBagConstraints();

        /* Loop through the preferences */
        for (MetisPreferenceItem myPref : pSet.getPreferences()) {
            /* Create the field */
            PreferenceElement myItem = new PreferenceElement(myPref);

            /* Add it to the list */
            theElList.add(myItem);

            /* Switch on the preference type */
            switch (myPref.getType()) {
                case BOOLEAN:
                    /* If we do not yet have an options panel */
                    if (myOptions == null) {
                        /* Create options */
                        myOptions = new TethysSwingEnablePanel();
                        myOptions.setLayout(new FlowLayout(FlowLayout.LEADING));
                        myOptions.setBorder(BorderFactory.createTitledBorder(NLS_OPTIONS));
                        theCompList.add(myOptions);
                    }

                    /* Add the item to the options panel */
                    myOptions.add(myItem.getComponent());
                    break;
                case STRING:
                    /* Add the Label into the first slot */
                    TethysSwingGridBagUtilities.setPanelLabel(myConstraints, myRow, GridBagConstraints.NONE);
                    add(myItem.getLabel(), myConstraints);

                    /* Add the Component into the second slot */
                    TethysSwingGridBagUtilities.setPanelField(myConstraints, myRow++, 1, GridBagConstraints.REMAINDER);
                    add(myItem.getComponent(), myConstraints);
                    theCompList.add(myItem.getComponent());
                    break;
                case DIRECTORY:
                case FILE:
                    /* Add the Label into the first slot */
                    TethysSwingGridBagUtilities.setPanelLabel(myConstraints, myRow, GridBagConstraints.HORIZONTAL);
                    add(myItem.getLabel(), myConstraints);
                    theCompList.add(myItem.getLabel());

                    /* Add the Component into the second slot */
                    TethysSwingGridBagUtilities.setPanelField(myConstraints, myRow++, 1, GridBagConstraints.REMAINDER);
                    add(myItem.getComponent(), myConstraints);
                    theCompList.add(myItem.getComponent());
                    break;
                default:
                    /* Add the Label into the first slot */
                    TethysSwingGridBagUtilities.setPanelLabel(myConstraints, myRow, GridBagConstraints.NONE);
                    add(myItem.getLabel(), myConstraints);

                    /* Add the Component into the second slot */
                    TethysSwingGridBagUtilities.setPanelField(myConstraints, myRow, 1, 1);
                    add(myItem.getComponent(), myConstraints);
                    theCompList.add(myItem.getComponent());

                    /* Add padding to the remainder */
                    TethysSwingGridBagUtilities.setPanelField(myConstraints, myRow++, 2, GridBagConstraints.REMAINDER);
                    add(new JLabel(), myConstraints);
                    break;
            }
        }

        /* If we have an options panel */
        if (myOptions != null) {
            /* Add the Label into the first slot */
            TethysSwingGridBagUtilities.setPanelRow(myConstraints, myRow);
            add(myOptions, myConstraints);
        }
    }

    @Override
    public TethysEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public String toString() {
        return theName;
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
     * @throws OceanusException on error
     */
    public void storeChanges() throws OceanusException {
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
        theEventManager.fireEvent(MetisPreferenceEvent.PREFCHANGED);
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
        private final MetisPreferenceItem thePreference;

        /**
         * The preference type.
         */
        private final MetisPreferenceType theType;

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
        private PreferenceElement(final MetisPreferenceItem pPreference) {
            /* Store the reference to the preference */
            thePreference = pPreference;
            theType = pPreference.getType();

            /* Create the label item */
            switch (theType) {
                case BOOLEAN:
                    theLabel = null;
                    break;
                default:
                    theLabel = new JLabel(thePreference.getDisplay()
                                          + ":");
                    theLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
            }

            /* Switch on field type */
            switch (theType) {
                /* Create the Underlying field */
                case STRING:
                case DIRECTORY:
                case FILE:
                    theField = new StringField(thePreference);
                    break;
                case INTEGER:
                    theField = new IntegerField(thePreference);
                    break;
                case BOOLEAN:
                    theField = new BooleanField(thePreference);
                    break;
                case DATE:
                    theField = new DateField(thePreference);
                    break;
                case COLOR:
                    theField = new ColorField(thePreference);
                    break;
                case ENUM:
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
            private final MetisValueField theField;

            /**
             * The preference as a stringPreference.
             */
            private final MetisStringPreference theString;

            /**
             * The button for the preference.
             */
            private JButton theButton = null;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private StringField(final MetisPreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theString = (MetisStringPreference) pPreference;
                theField = new MetisValueField(MetisPreferenceValueClass.STRING, theFormatter);
                theField.setColumns(WIDTH_STRING);

                /* Add property change listener */
                PreferenceListener myListener = new PreferenceListener();
                theField.addPropertyChangeListener(MetisValueField.PROPERTY_VALUE, myListener);

                /* If the preference type is string we have finished */
                if (pPreference.getType() == MetisPreferenceType.STRING) {
                    return;
                }

                /* Create a button */
                theButton = new JButton(pPreference.getDisplay());
                theButton.addActionListener(myListener);
            }

            @Override
            protected JComponent getLabel() {
                return (theType == MetisPreferenceType.STRING)
                                                               ? super.getLabel()
                                                               : theButton;
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
                            case DIRECTORY:
                                /* Create and show the dialog */
                                MetisFileSelector myDialog = new MetisFileSelector(MetisPreferenceSetPanel.this, NLS_SELECT
                                                                                                                 + " "
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
                            case FILE:
                                /* Create and show the dialog */
                                MetisFileSelector myFileDialog = new MetisFileSelector(MetisPreferenceSetPanel.this, NLS_SELECT
                                                                                                                     + " "
                                                                                                                     + theString.getDisplay(), new File(theString.getValue()),
                                        null, null);
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
            private final MetisValueField theField;

            /**
             * The preference as an integerPreference.
             */
            private final MetisIntegerPreference theInteger;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private IntegerField(final MetisPreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theInteger = (MetisIntegerPreference) pPreference;
                theField = new MetisValueField(MetisPreferenceValueClass.INTEGER, theFormatter);
                theField.setColumns(WIDTH_INTEGER);

                /* Add property change listener */
                theField.addPropertyChangeListener(MetisValueField.PROPERTY_VALUE, new PreferenceListener());
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
            private final MetisBooleanPreference theBoolean;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private BooleanField(final MetisPreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theBoolean = (MetisBooleanPreference) pPreference;
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
            private final TethysSwingDateButton theField;

            /**
             * The preference as a datePreference.
             */
            private final MetisDatePreference theDate;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private DateField(final MetisPreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theDate = (MetisDatePreference) pPreference;
                theField = new TethysSwingDateButton();

                /* Add property change listener */
                theField.addPropertyChangeListener(TethysSwingDateButton.PROPERTY_DATEDAY, new PreferenceListener());
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
                        TethysDate myValue = new TethysDate(theField.getSelectedDate());
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
            private ColorField(final MetisPreferenceItem pPreference) {
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
             * The underlying JScrollButton field.
             */
            private final JScrollButton<Enum<?>> theField;

            /**
             * The preference as an EnumPreference.
             */
            private final MetisEnumPreference<?> theEnum;

            /**
             * Constructor.
             * @param pPreference the preference
             */
            private EnumField(final MetisPreferenceItem pPreference) {
                /* Access the preference and create the underlying field */
                theEnum = (MetisEnumPreference<?>) pPreference;
                theField = new JScrollButton<Enum<?>>();

                /* Create the popUp Menu */
                JScrollMenuBuilder<Enum<?>> myBuilder = theField.getMenuBuilder();

                /* For all values */
                for (Enum<?> myEnum : theEnum.getValues()) {
                    /* Create a new JMenuItem and add it to the popUp */
                    myBuilder.addItem(myEnum);
                }

                /* Add action listener */
                theField.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, new PreferenceListener());
            }

            @Override
            protected void updateField() {
                /* Update the field */
                theField.setValue(theEnum.getValue());

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
                    implements PropertyChangeListener {
                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    /* Access source of the event */
                    Object o = evt.getSource();

                    /* Handle button */
                    if (theField.equals(o)) {
                        /* Set the new value of the preference */
                        theEnum.setValue(theField.getValue());

                        /* Note if we have any changes */
                        notifyChanges();
                    }
                }
            }
        }
    }
}
