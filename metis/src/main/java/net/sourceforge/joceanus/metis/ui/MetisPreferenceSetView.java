/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.metis.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.joceanus.metis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceResource;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet.MetisBooleanPreference;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet.MetisDatePreference;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet.MetisEnumPreference;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet.MetisIntegerPreference;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet.MetisPreferenceId;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet.MetisPreferenceItem;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet.MetisStringPreference;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceType;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIGenericWrapper;
import net.sourceforge.joceanus.tethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.ui.api.dialog.TethysUIDirectorySelector;
import net.sourceforge.joceanus.tethys.ui.api.dialog.TethysUIFileSelector;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUIColorButtonField;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUIIntegerEditField;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIFieldAttribute;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIFieldType;
import net.sourceforge.joceanus.tethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIFlowPaneManager;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIGridPaneManager;
import net.sourceforge.joceanus.tethys.ui.api.pane.TethysUIPaneFactory;

/**
 * Panel for editing a preference Set.
 */
public class MetisPreferenceSetView
        implements TethysEventProvider<MetisPreferenceEvent>, TethysUIComponent {
    /**
     * Colon String.
     */
    protected static final String STR_COLON = TethysUIConstant.STR_COLON;

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
    private final OceanusEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * The PreferenceSet for this panel.
     */
    private final MetisPreferenceSet thePreferences;

    /**
     * The Border Pane.
     */
    private final TethysUIBorderPaneManager thePane;

    /**
     * The Grid Pane.
     */
    private final TethysUIGridPaneManager theGrid;

    /**
     * The GUI factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * The Element list.
     */
    private final List<PreferenceElement> theElements;

    /**
     * The Options Pane.
     */
    private TethysUIFlowPaneManager theOptions;

    /**
     * Constructor.
     *
     * @param pFactory       the GUI factory
     * @param pPreferenceSet the preference set
     */
    protected MetisPreferenceSetView(final TethysUIFactory<?> pFactory,
                                     final MetisPreferenceSet pPreferenceSet) {
        /* Store parameters */
        theGuiFactory = pFactory;
        thePreferences = pPreferenceSet;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the list */
        theElements = new ArrayList<>();

        /* Create the Grid Pane */
        final TethysUIPaneFactory myPanes = theGuiFactory.paneFactory();
        theGrid = myPanes.newGridPane();

        /* Loop through the preferences */
        for (MetisPreferenceItem myPref : thePreferences.getPreferences()) {
            /* Create element and add it to the list */
            final PreferenceElement myElement = allocatePreferenceElement(myPref);
            if (myElement != null) {
                theElements.add(myElement);
            }
        }

        /* Create the border pane */
        thePane = myPanes.newBorderPane();
        thePane.setBorderTitle(NLS_PREFERENCES);
        thePane.setCentre(theGrid);

        /* If we have an options pane */
        if (theOptions != null) {
            /* Add to the end */
            thePane.setSouth(theOptions);
        }

        /* initialise the fields */
        updateFields();
    }

    /**
     * Obtain the GUI factory.
     * @return the factory
     */
    protected TethysUIFactory<?> getFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain the grid.
     * @return the grid
     */
    protected TethysUIGridPaneManager getGrid() {
        return theGrid;
    }

    @Override
    public String toString() {
        return thePreferences.getName();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePane;
    }

    @Override
    public OceanusEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Does the Preference Set have changes.
     *
     * @return does the set have changes
     */
    boolean hasChanges() {
        return thePreferences.hasChanges();
    }

    /**
     * Reset changes.
     */
    void resetChanges() {
        /* Reset changes and clear flag */
        thePreferences.resetChanges();

        /* Update the fields */
        updateFields();
    }

    /**
     * Store changes.
     *
     * @throws OceanusException on error
     */
    void storeChanges() throws OceanusException {
        /* Reset changes and clear flag */
        thePreferences.storeChanges();

        /* Update the fields */
        updateFields();
    }

    /**
     * Update fields.
     */
    private void updateFields() {
        /* Update the viewer entry */
        thePreferences.updateViewerEntry();

        /* Loop through the fields */
        for (PreferenceElement myItem : theElements) {
            /* Update the field */
            myItem.updateField();
        }
    }

    /**
     * Notify changes.
     */
    protected void notifyChanges() {
        /* AutoCorrect the preferences */
        thePreferences.autoCorrectPreferences();

        /* Update the fields */
        updateFields();

        /* Notify listeners */
        theEventManager.fireEvent(MetisPreferenceEvent.PREFCHANGED);
    }

    /**
     * Allocate element.
     *
     * @param pItem the preference item
     * @return the element
     */
    protected PreferenceElement allocatePreferenceElement(final MetisPreferenceItem pItem) {
        if (pItem instanceof MetisEnumPreference) {
            return new EnumPreferenceElement<>((MetisEnumPreference<?>) pItem);
        } else if (pItem instanceof MetisIntegerPreference) {
            return new IntegerPreferenceElement((MetisIntegerPreference) pItem);
        } else if (pItem instanceof MetisDatePreference) {
            return new DatePreferenceElement((MetisDatePreference) pItem);
        } else if (pItem instanceof MetisBooleanPreference) {
            return new BooleanPreferenceElement((MetisBooleanPreference) pItem);
        } else if (pItem instanceof MetisStringPreference) {
            final MetisStringPreference myItem = (MetisStringPreference) pItem;
            final MetisPreferenceId myId = pItem.getType();
            if (myId.equals(MetisPreferenceType.DIRECTORY)) {
                return new DirectoryPreferenceElement(myItem);
            } else if (myId.equals(MetisPreferenceType.FILE)) {
                return new FilePreferenceElement(myItem);
            } else if (myId.equals(MetisPreferenceType.COLOR)) {
                return new ColorPreferenceElement(myItem);
            } else {
                return new StringPreferenceElement(myItem);
            }
        } else  {
            throw new IllegalArgumentException("Bad Preference Type: " + pItem.getType());
        }
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        thePreferences.setFocus();
    }

    /**
     * PreferenceElement.
     */
    @FunctionalInterface
    protected interface PreferenceElement {
        /**
         * Update the field.
         */
        void updateField();
    }

    /**
     * String preference element.
     */
    private final class StringPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisStringPreference theItem;

        /**
         * The Field item.
         */
        private final TethysUIStringEditField theField;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        StringPreferenceElement(final MetisStringPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.fieldFactory().newStringField();
            theField.setEditable(true);

            /* Create the label */
            final TethysUILabel myLabel = theGuiFactory.controlFactory().newLabel(pItem.getDisplay() + STR_COLON);
            myLabel.setAlignment(TethysUIAlignment.EAST);

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
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
            theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            theField.setEnabled(!theItem.isHidden());
        }
    }

    /**
     * Integer preference element.
     */
    private final class IntegerPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisIntegerPreference theItem;

        /**
         * The Field item.
         */
        private final TethysUIIntegerEditField theField;

        /**
         * The Label item.
         */
        private final TethysUILabel theRangeLabel;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        IntegerPreferenceElement(final MetisIntegerPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.fieldFactory().newIntegerField();
            theField.setEditable(true);
            theField.setPreferredWidth(TethysUIFieldType.INTEGER.getDefaultWidth());

            /* Create the label */
            final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
            final TethysUILabel myLabel = myControls.newLabel(pItem.getDisplay() + STR_COLON);
            myLabel.setAlignment(TethysUIAlignment.EAST);

            /* Create the range label */
            theRangeLabel = myControls.newLabel();

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
            theGrid.addCell(theField);
            theGrid.addCell(theRangeLabel);
            theGrid.allowCellGrowth(theRangeLabel);
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

            /* handle the range */
            handleRange();

            /* Set changed indication */
            theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            theField.setEnabled(!theItem.isHidden());
        }

        /**
         * handle range.
         */
        private void handleRange() {
            /* Access the minimum/maximum */
            final Integer myMin = theItem.getMinimum();
            final Integer myMax = theItem.getMaximum();
            final boolean hasMin = myMin != null;
            final boolean hasMax = myMax != null;

            /* Set the valid range */
            theField.setValidator(p -> (hasMin && p < myMin) || (hasMax && p > myMax)
                                       ? MetisPreferenceResource.UI_RANGE_ERROR.getValue()
                                       : null);
            theRangeLabel.setText(null);

            /* If we have a minimum or maximum */
            if (hasMin || hasMax) {
                /* Format the details */
                final StringBuilder myBuilder = new StringBuilder();

                /* Handle minimum */
                if (hasMin) {
                    myBuilder.append(MetisPreferenceResource.UI_RANGE_MIN.getValue());
                    myBuilder.append(' ');
                    myBuilder.append(myMin);
                    if (hasMax) {
                        myBuilder.append(", ");
                    }
                }

                /* Handle maximum */
                if (hasMax) {
                    myBuilder.append(MetisPreferenceResource.UI_RANGE_MAX.getValue());
                    myBuilder.append(' ');
                    myBuilder.append(myMax);
                }

                /* Format the field */
                theRangeLabel.setText(myBuilder.toString());
            }
        }
    }

    /**
     * Boolean preference element.
     */
    private final class BooleanPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisBooleanPreference theItem;

        /**
         * The CheckBox item.
         */
        private final TethysUICheckBox theCheckBox;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        BooleanPreferenceElement(final MetisBooleanPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theCheckBox = theGuiFactory.controlFactory().newCheckBox(pItem.getDisplay());

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

            /* Handle hidden state */
            theCheckBox.setEnabled(!theItem.isHidden());
        }

        /**
         * ensure the options pane.
         */
        private void ensureOptionsPane() {
            /* If we haven't created the options yet */
            if (theOptions == null) {
                /* create the options panel */
                theOptions = theGuiFactory.paneFactory().newFlowPane();
                theOptions.setBorderTitle(NLS_OPTIONS);
            }
        }
    }

    /**
     * Date preference element.
     */
    private final class DatePreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisDatePreference theItem;

        /**
         * The Field item.
         */
        private final TethysUIDateButtonField theField;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        DatePreferenceElement(final MetisDatePreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.fieldFactory().newDateField();
            theField.setEditable(true);

            /* Create the label */
            final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
            final TethysUILabel myLabel = myControls.newLabel(pItem.getDisplay() + STR_COLON);
            myLabel.setAlignment(TethysUIAlignment.EAST);

            /* Create the place-holder */
            final TethysUILabel myStub = myControls.newLabel();

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
            theGrid.addCell(theField);
            theGrid.addCell(myStub);
            theGrid.allowCellGrowth(myStub);
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
            theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            theField.setEnabled(!theItem.isHidden());
        }
    }

    /**
     * Enum preference element.
     *
     * @param <E> the enum type
     */
    private final class EnumPreferenceElement<E extends Enum<E>>
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisEnumPreference<E> theItem;

        /**
         * The Field item.
         */
        private final TethysUIScrollButtonField<TethysUIGenericWrapper> theField;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        @SuppressWarnings("unchecked")
        EnumPreferenceElement(final MetisEnumPreference<E> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.fieldFactory().newScrollField(TethysUIGenericWrapper.class);
            theField.setEditable(true);

            /* Create the label */
            final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
            final TethysUILabel myLabel = myControls.newLabel(pItem.getDisplay() + STR_COLON);
            myLabel.setAlignment(TethysUIAlignment.EAST);

            /* Create the place-holder */
            final TethysUILabel myStub = myControls.newLabel();

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
            theGrid.addCell(theField);
            theGrid.addCell(myStub);
            theGrid.allowCellGrowth(myStub);
            theGrid.newRow();

            /* Create listeners */
            theField.setMenuConfigurator(this::buildMenu);
            final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theField.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> {
                 pItem.setValue((E) theField.getValue().getData());
                notifyChanges();
            });
        }

        /**
         * Build menu.
         *
         * @param pMenu the menu
         */
        private void buildMenu(final TethysUIScrollMenu<TethysUIGenericWrapper> pMenu) {
            /* reset the menu */
            pMenu.removeAllItems();

            /* Obtain the filter */
            final Predicate<E> myFilter = theItem.getFilter();

            /* For all values */
            for (E myEnum : theItem.getValues()) {
                /* If the element is not filtered */
                if (myFilter.test(myEnum)) {
                    /* Create a new MenuItem and add it to the popUp */
                    pMenu.addItem(new TethysUIGenericWrapper(myEnum));
                }
            }
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(new TethysUIGenericWrapper(theItem.getValue()));

            /* Set changed indication */
            theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            theField.setEnabled(!theItem.isHidden());
        }
    }

    /**
     * Colour preference element.
     */
    private final class ColorPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisStringPreference theItem;

        /**
         * The Field item.
         */
        private final TethysUIColorButtonField theField;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        ColorPreferenceElement(final MetisStringPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.fieldFactory().newColorField();
            theField.setEditable(true);

            /* Create the label */
            final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
            final TethysUILabel myLabel = myControls.newLabel(pItem.getDisplay() + STR_COLON);
            myLabel.setAlignment(TethysUIAlignment.EAST);

            /* Create the place-holder */
            final TethysUILabel myStub = myControls.newLabel();

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysUIAlignment.EAST);
            theGrid.addCell(theField);
            theGrid.addCell(myStub);
            theGrid.allowCellGrowth(myStub);
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
            theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            theField.setEnabled(!theItem.isHidden());
        }
    }

    /**
     * File preference element.
     */
    private final class FilePreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisStringPreference theItem;

        /**
         * The Field item.
         */
        private final TethysUIStringEditField theField;

        /**
         * The Button.
         */
        private final TethysUIButton theButton;

        /**
         * The File Selector.
         */
        private TethysUIFileSelector theSelector;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        FilePreferenceElement(final MetisStringPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.fieldFactory().newStringField();
            theField.setEditable(true);

            /* Create the button */
            theButton = theGuiFactory.buttonFactory().newButton();
            theButton.setTextOnly();
            theButton.setText(pItem.getDisplay());

            /* Add to the Grid Pane */
            theGrid.addCell(theButton);
            theGrid.addCell(theField);
            theGrid.setCellColumnSpan(theField, 2);
            theGrid.allowCellGrowth(theField);
            theGrid.newRow();

            /* Create listeners */
            theButton.getEventRegistrar().addEventListener(e -> handleDialog());
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
            theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            final boolean isEnabled = !theItem.isHidden();
            theField.setEnabled(isEnabled);
            theButton.setEnabled(isEnabled);
        }

        /**
         * Handle Dialog.
         */
        private void handleDialog() {
            ensureSelector();
            theSelector.setInitialFile(new File(theItem.getValue()));
            final File myFile = theSelector.selectFile();
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
                theSelector = theGuiFactory.dialogFactory().newFileSelector();
                theSelector.setTitle(theItem.getDisplay());
            }
        }
    }

    /**
     * Directory preference element.
     */
    private final class DirectoryPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisStringPreference theItem;

        /**
         * The Field item.
         */
        private final TethysUIStringEditField theField;

        /**
         * The Button.
         */
        private final TethysUIButton theButton;

        /**
         * The Directory Selector.
         */
        private TethysUIDirectorySelector theSelector;

        /**
         * Constructor.
         *
         * @param pItem the item
         */
        DirectoryPreferenceElement(final MetisStringPreference pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.fieldFactory().newStringField();
            theField.setEditable(true);

            /* Create the button */
            theButton = theGuiFactory.buttonFactory().newButton();
            theButton.setTextOnly();
            theButton.setText(pItem.getDisplay());

            /* Add to the Grid Pane */
            theGrid.addCell(theButton);
            theGrid.addCell(theField);
            theGrid.setCellColumnSpan(theField, 2);
            theGrid.allowCellGrowth(theField);
            theGrid.newRow();

            /* Create listeners */
            theButton.getEventRegistrar().addEventListener(e -> handleDialog());
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
            theField.setTheAttributeState(TethysUIFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            final boolean isEnabled = !theItem.isHidden();
            theField.setEnabled(isEnabled);
            theButton.setEnabled(isEnabled);
        }

        /**
         * Handle Dialog.
         */
        private void handleDialog() {
            ensureSelector();
            theSelector.setInitialDirectory(new File(theItem.getValue()));
            final File myDir = theSelector.selectDirectory();
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
                theSelector = theGuiFactory.dialogFactory().newDirectorySelector();
                theSelector.setTitle(theItem.getDisplay());
            }
        }
    }
}
