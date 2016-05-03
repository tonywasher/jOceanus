/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.preference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisBooleanPreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisDatePreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisEnumPreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisIntegerPreference;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisPreferenceItem;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet.MetisStringPreference;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysCheckBox;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDirectorySelector;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;
import net.sourceforge.joceanus.jtethys.ui.TethysFlowPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Panel for editing a preference Set.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisPreferenceSetView<N, I>
        implements TethysEventProvider<MetisPreferenceEvent>, TethysNode<N> {
    /**
     * Text for Preferences Title.
     */
    private static final String NLS_PREFERENCES = MetisPreferenceResource.UI_TITLE_PREFERENCES.getValue();

    /**
     * Text for Options Title.
     */
    private static final String NLS_OPTIONS = MetisPreferenceResource.UI_TITLE_OPTIONS.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * The PreferenceSet for this panel.
     */
    private final MetisPreferenceSet thePreferences;

    /**
     * The Border Pane.
     */
    private final TethysBorderPaneManager<N, I> thePane;

    /**
     * The Grid Pane.
     */
    private final TethysGridPaneManager<N, I> theGrid;

    /**
     * The GUI factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * The Element list.
     */
    private final List<PreferenceElement> theElements;

    /**
     * The Set name.
     */
    private final String theName;

    /**
     * The Options Pane.
     */
    private TethysFlowPaneManager<N, I> theOptions;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pPreferenceSet the preference set
     */
    protected MetisPreferenceSetView(final TethysGuiFactory<N, I> pFactory,
                                     final MetisPreferenceSet pPreferenceSet) {
        /* Store parameters */
        theGuiFactory = pFactory;
        thePreferences = pPreferenceSet;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the list */
        theElements = new ArrayList<>();

        /* Create the Grid Pane */
        theGrid = pFactory.newGridPane();

        /* Loop through the preferences */
        for (MetisPreferenceItem myPref : thePreferences.getPreferences()) {
            /* Create element and add it to the list */
            theElements.add(allocatePreferenceElement(myPref));
        }

        /* Create the border pane */
        thePane = pFactory.newBorderPane();
        thePane.setBorderTitle(NLS_PREFERENCES);
        thePane.setCentre(theGrid);

        /* If we have an options pane */
        if (theOptions != null) {
            /* Add to the end */
            thePane.setSouth(theOptions);
        }

        /* Record the name of the set */
        theName = pPreferenceSet.getClass().getSimpleName();
    }

    @Override
    public String toString() {
        return theName;
    }

    @Override
    public Integer getId() {
        return thePane.getId();
    }

    @Override
    public N getNode() {
        return thePane.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePane.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theGrid.setEnabled(pEnabled);
    }

    @Override
    public TethysEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Does the Preference Set have changes.
     * @return does the set have changes
     */
    protected boolean hasChanges() {
        return thePreferences.hasChanges();
    }

    /**
     * Reset changes.
     */
    protected void resetChanges() {
        /* Reset changes and clear flag */
        thePreferences.resetChanges();

        /* Update the fields */
        updateFields();
    }

    /**
     * Store changes.
     * @throws OceanusException on error
     */
    protected void storeChanges() throws OceanusException {
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
        for (PreferenceElement myItem : theElements) {
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

    /**
     * Allocate element.
     * @param pItem the preference item
     * @return the element
     */
    private PreferenceElement allocatePreferenceElement(final MetisPreferenceItem pItem) {
        if (pItem instanceof MetisEnumPreference) {
            return new EnumPreferenceElement<>((MetisEnumPreference<?>) pItem);
        } else if (pItem instanceof MetisIntegerPreference) {
            return new IntegerPreferenceElement((MetisIntegerPreference) pItem);
        } else if (pItem instanceof MetisDatePreference) {
            return new DatePreferenceElement((MetisDatePreference) pItem);
        } else if (pItem instanceof MetisBooleanPreference) {
            return new BooleanPreferenceElement((MetisBooleanPreference) pItem);
        } else if (pItem instanceof MetisStringPreference) {
            MetisStringPreference myItem = (MetisStringPreference) pItem;
            switch (pItem.getType()) {
                case DIRECTORY:
                    return new DirectoryPreferenceElement(myItem);
                case FILE:
                    return new FilePreferenceElement(myItem);
                case COLOR:
                    return new ColorPreferenceElement(myItem);
                default:
                    return new StringPreferenceElement(myItem);
            }
        }
        throw new IllegalArgumentException("Bad Preference Type: " + pItem.getType());
    }

    /**
     * ensure the options pane.
     */
    private void ensureOptionsPane() {
        /* If we haven't created the options yet */
        if (theOptions == null) {
            /* create the options panel */
            theOptions = theGuiFactory.newFlowPane();
            theOptions.setBorderTitle(NLS_OPTIONS);
        }
    }

    /**
     * PreferenceElement.
     */
    private interface PreferenceElement {
        /**
         * Update the field.
         */
        void updateField();
    }

    /**
     * String preference element.
     */
    private class StringPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item
         */
        private final MetisStringPreference theItem;

        /**
         * The Field item
         */
        private final TethysDataEditField<String, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private StringPreferenceElement(final MetisStringPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newStringField();

            /* Create the label */
            TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                               + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.addCell(theField);
            theGrid.setCellColumnSpan(theField, 2);
            theGrid.allowCellGrowth(theField);
            theGrid.newRow();

            /* Create listener */
            theField.getEventRegistrar().addEventListener(e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
        }
    }

    /**
     * Integer preference element.
     */
    private class IntegerPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item
         */
        private final MetisIntegerPreference theItem;

        /**
         * The Field item
         */
        private final TethysDataEditField<Integer, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private IntegerPreferenceElement(final MetisIntegerPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newIntegerField();

            /* Create the label */
            TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                               + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.addCell(theField);
            theGrid.newRow();

            /* Create listener */
            theField.getEventRegistrar().addEventListener(e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
        }
    }

    /**
     * Boolean preference element.
     */
    private class BooleanPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item
         */
        private final MetisBooleanPreference theItem;

        /**
         * The CheckBox item
         */
        private final TethysCheckBox<N, I> theCheckBox;

        /**
         * Constructor.
         * @param pItem the item
         */
        private BooleanPreferenceElement(final MetisBooleanPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theCheckBox = theGuiFactory.newCheckBox(pItem.getDisplay());

            /* Ensure the options pane */
            ensureOptionsPane();

            /* Add to the Options Pane */
            theOptions.addNode(theCheckBox);

            /* Create listener */
            theCheckBox.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
                pItem.setValue(theCheckBox.isSelected());
                notifyChanges();
            });
        }

        @Override
        public void updateField() {
            /* Update the field */
            theCheckBox.setSelected(theItem.getValue());

            /* Set changed indication */
            theCheckBox.setChanged(theItem.isChanged());
        }
    }

    /**
     * Date preference element.
     */
    private class DatePreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item
         */
        private final MetisDatePreference theItem;

        /**
         * The Field item
         */
        private final TethysDataEditField<TethysDate, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private DatePreferenceElement(final MetisDatePreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newDateField();

            /* Create the label */
            TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                               + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.addCell(theField);
            theGrid.newRow();

            /* Create listener */
            theField.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
        }
    }

    /**
     * Enum preference element.
     */
    private class EnumPreferenceElement<E extends Enum<E>>
            implements PreferenceElement {
        /**
         * The Preference item
         */
        private final MetisEnumPreference<E> theItem;

        /**
         * The Field item
         */
        private final TethysDataEditField<E, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private EnumPreferenceElement(final MetisEnumPreference<E> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newScrollField();

            /* Build the menu */
            buildMenu();

            /* Create the label */
            TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                               + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.addCell(theField);
            theGrid.newRow();

            /* Create listeners */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = theField.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        /**
         * Build menu.
         */
        private void buildMenu() {
            /* Access field details */
            @SuppressWarnings("unchecked")
            TethysScrollField<E, N, I> myField = (TethysScrollField<E, N, I>) theField;
            TethysScrollMenu<E, I> myMenu = myField.getScrollManager().getMenu();

            /* For all values */
            for (E myEnum : theItem.getValues()) {
                /* Create a new MenuItem and add it to the popUp */
                myMenu.addItem(myEnum);
            }
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
        }
    }

    /**
     * Colour preference element.
     */
    private class ColorPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item
         */
        private final MetisStringPreference theItem;

        /**
         * The Field item
         */
        private final TethysDataEditField<String, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private ColorPreferenceElement(final MetisStringPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newStringField();

            /* Create the label */
            TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                               + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.addCell(theField);
            theGrid.newRow();

            /* Create listener */
            theField.getEventRegistrar().addEventListener(e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
        }
    }

    /**
     * File preference element.
     */
    private class FilePreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item
         */
        private final MetisStringPreference theItem;

        /**
         * The Field item
         */
        private final TethysDataEditField<String, N, I> theField;

        /**
         * The File Selector.
         */
        private TethysFileSelector theSelector;

        /**
         * Constructor.
         * @param pItem the item
         */
        private FilePreferenceElement(final MetisStringPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newStringField();

            /* Create the button */
            TethysButton<N, I> myButton = theGuiFactory.newButton();
            myButton.setTextOnly();
            myButton.setText(pItem.getDisplay());

            /* Add to the Grid Pane */
            theGrid.addCell(myButton);
            theGrid.addCell(theField);
            theGrid.setCellColumnSpan(theField, 2);
            theGrid.allowCellGrowth(theField);
            theGrid.newRow();

            /* Create listeners */
            myButton.getEventRegistrar().addEventListener(e -> handleDialog());
            theField.getEventRegistrar().addEventListener(e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
        }

        /**
         * Handle Dialog.
         */
        private void handleDialog() {
            ensureSelector();
            theSelector.setInitialFile(new File(theItem.getValue()));
            File myFile = theSelector.selectFile();
            if (myFile != null) {
                theItem.setValue(myFile.getAbsolutePath());
                notifyChanges();
            }
        }

        /**
         * Ensure that selector is created.
         */
        private void ensureSelector() {
            if (theSelector == null) {
                theSelector = theGuiFactory.newFileSelector();
                theSelector.setTitle(theItem.getDisplay());
            }
        }
    }

    /**
     * Directory preference element.
     */
    private class DirectoryPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item
         */
        private final MetisStringPreference theItem;

        /**
         * The Field item
         */
        private final TethysDataEditField<String, N, I> theField;

        /**
         * The Directory Selector.
         */
        private TethysDirectorySelector theSelector;

        /**
         * Constructor.
         * @param pItem the item
         */
        private DirectoryPreferenceElement(final MetisStringPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newStringField();

            /* Create the button */
            TethysButton<N, I> myButton = theGuiFactory.newButton();
            myButton.setTextOnly();
            myButton.setText(pItem.getDisplay());

            /* Add to the Grid Pane */
            theGrid.addCell(myButton);
            theGrid.addCell(theField);
            theGrid.setCellColumnSpan(theField, 2);
            theGrid.allowCellGrowth(theField);
            theGrid.newRow();

            /* Create listeners */
            myButton.getEventRegistrar().addEventListener(e -> handleDialog());
            theField.getEventRegistrar().addEventListener(e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
        }

        /**
         * Handle Dialog.
         */
        private void handleDialog() {
            ensureSelector();
            theSelector.setInitialDirectory(new File(theItem.getValue()));
            File myDir = theSelector.selectDirectory();
            if (myDir != null) {
                theItem.setValue(myDir.getAbsolutePath());
                notifyChanges();
            }
        }

        /**
         * Ensure that selector is created.
         */
        private void ensureSelector() {
            if (theSelector == null) {
                theSelector = theGuiFactory.newDirectorySelector();
                theSelector.setTitle(theItem.getDisplay());
            }
        }
    }
}
