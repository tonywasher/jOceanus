/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceResource;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet.MetisBooleanPreference;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet.MetisByteArrayPreference;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet.MetisCharArrayPreference;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet.MetisDatePreference;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet.MetisEnumPreference;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet.MetisIntegerPreference;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet.MetisPreferenceItem;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet.MetisStringPreference;
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
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysValidatedEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDirectorySelector;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldType;
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
 * @param <K> the key type
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisPreferenceSetView<K extends Enum<K> & MetisPreferenceKey, N, I>
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
    private final MetisPreferenceSet<K> thePreferences;

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
     * The Options Pane.
     */
    private TethysFlowPaneManager<N, I> theOptions;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pPreferenceSet the preference set
     */
    protected MetisPreferenceSetView(final TethysGuiFactory<N, I> pFactory,
                                     final MetisPreferenceSet<K> pPreferenceSet) {
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
        for (MetisPreferenceItem<K> myPref : thePreferences.getPreferences()) {
            /* Create element and add it to the list */
            final PreferenceElement myElement = allocatePreferenceElement(myPref);
            if (myElement != null) {
                theElements.add(myElement);
            }
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

        /* initialise the fields */
        updateFields();
    }

    @Override
    public String toString() {
        return thePreferences.getName();
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
    private void notifyChanges() {
        /* AutoCorrect the preferences */
        thePreferences.autoCorrectPreferences();

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
    @SuppressWarnings("unchecked")
    private PreferenceElement allocatePreferenceElement(final MetisPreferenceItem<K> pItem) {
        if (pItem instanceof MetisEnumPreference) {
            return new EnumPreferenceElement<>((MetisEnumPreference<K, ?>) pItem);
        } else if (pItem instanceof MetisIntegerPreference) {
            return new IntegerPreferenceElement((MetisIntegerPreference<K>) pItem);
        } else if (pItem instanceof MetisDatePreference) {
            return new DatePreferenceElement((MetisDatePreference<K>) pItem);
        } else if (pItem instanceof MetisBooleanPreference) {
            return new BooleanPreferenceElement((MetisBooleanPreference<K>) pItem);
        } else if (pItem instanceof MetisCharArrayPreference) {
            return new CharArrayPreferenceElement((MetisCharArrayPreference<K>) pItem);
        } else if (pItem instanceof MetisStringPreference) {
            final MetisStringPreference<K> myItem = (MetisStringPreference<K>) pItem;
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
        } else if (!(pItem instanceof MetisByteArrayPreference)) {
            throw new IllegalArgumentException("Bad Preference Type: " + pItem.getType());
        }
        return null;
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
    private interface PreferenceElement {
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
        private final MetisStringPreference<K> theItem;

        /**
         * The Field item.
         */
        private final TethysDataEditField<String, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private StringPreferenceElement(final MetisStringPreference<K> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newStringField();
            theField.setEditable(true);

            /* Create the label */
            final TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                                     + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
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
        private final MetisIntegerPreference<K> theItem;

        /**
         * The Field item.
         */
        private final TethysValidatedEditField<Integer, N, I> theField;

        /**
         * The Label item.
         */
        private final TethysLabel<N, I> theRangeLabel;

        /**
         * Constructor.
         * @param pItem the item
         */
        private IntegerPreferenceElement(final MetisIntegerPreference<K> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newIntegerField();
            theField.setEditable(true);
            theField.setPreferredWidth(TethysFieldType.INTEGER.getDefaultWidth());

            /* Create the label */
            final TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                                     + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Create the range label */
            theRangeLabel = theGuiFactory.newLabel();

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
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
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
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
            theField.setValidator(p -> (p < myMin) || (p > myMax)
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
        private final MetisBooleanPreference<K> theItem;

        /**
         * The CheckBox item.
         */
        private final TethysCheckBox<N, I> theCheckBox;

        /**
         * Constructor.
         * @param pItem the item
         */
        private BooleanPreferenceElement(final MetisBooleanPreference<K> pItem) {
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
                theOptions = theGuiFactory.newFlowPane();
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
        private final MetisDatePreference<K> theItem;

        /**
         * The Field item.
         */
        private final TethysDataEditField<TethysDate, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private DatePreferenceElement(final MetisDatePreference<K> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newDateField();
            theField.setEditable(true);

            /* Create the label */
            final TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                                     + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Create the place-holder */
            final TethysLabel<N, I> myStub = theGuiFactory.newLabel();

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
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
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
            theField.adjustField();

            /* Handle hidden state */
            theField.setEnabled(!theItem.isHidden());
        }
    }

    /**
     * Enum preference element.
     * @param <E> the enum type
     */
    private final class EnumPreferenceElement<E extends Enum<E>>
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisEnumPreference<K, E> theItem;

        /**
         * The Field item.
         */
        private final TethysScrollButtonField<E, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private EnumPreferenceElement(final MetisEnumPreference<K, E> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newScrollField();
            theField.setEditable(true);

            /* Create the label */
            final TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                                     + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Create the place-holder */
            final TethysLabel<N, I> myStub = theGuiFactory.newLabel();

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
            theGrid.addCell(theField);
            theGrid.addCell(myStub);
            theGrid.allowCellGrowth(myStub);
            theGrid.newRow();

            /* Create listeners */
            theField.setMenuConfigurator(this::buildMenu);
            final TethysEventRegistrar<TethysUIEvent> myRegistrar = theField.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> {
                pItem.setValue(theField.getValue());
                notifyChanges();
            });
        }

        /**
         * Build menu.
         * @param pMenu the menu
         */
        private void buildMenu(final TethysScrollMenu<E, I> pMenu) {
            /* reset the menu */
            pMenu.removeAllItems();

            /* Obtain the filter */
            final Predicate<E> myFilter = theItem.getFilter();

            /* For all values */
            for (E myEnum : theItem.getValues()) {
                /* If the element is not filtered */
                if (myFilter.test(myEnum)) {
                    /* Create a new MenuItem and add it to the popUp */
                    pMenu.addItem(myEnum);
                }
            }
        }

        @Override
        public void updateField() {
            /* Update the field */
            theField.setValue(theItem.getValue());

            /* Set changed indication */
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
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
        private final MetisStringPreference<K> theItem;

        /**
         * The Field item.
         */
        private final TethysDataEditField<String, N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private ColorPreferenceElement(final MetisStringPreference<K> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newColorField();
            theField.setEditable(true);

            /* Create the label */
            final TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                                     + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Create the place-holder */
            final TethysLabel<N, I> myStub = theGuiFactory.newLabel();

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
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
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
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
        private final MetisStringPreference<K> theItem;

        /**
         * The Field item.
         */
        private final TethysDataEditField<String, N, I> theField;

        /**
         * The Button.
         */
        private final TethysButton<N, I> theButton;

        /**
         * The File Selector.
         */
        private TethysFileSelector theSelector;

        /**
         * Constructor.
         * @param pItem the item
         */
        private FilePreferenceElement(final MetisStringPreference<K> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newStringField();
            theField.setEditable(true);

            /* Create the button */
            theButton = theGuiFactory.newButton();
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
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
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
                theSelector = theGuiFactory.newFileSelector();
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
        private final MetisStringPreference<K> theItem;

        /**
         * The Field item.
         */
        private final TethysDataEditField<String, N, I> theField;

        /**
         * The Button.
         */
        private final TethysButton<N, I> theButton;

        /**
         * The Directory Selector.
         */
        private TethysDirectorySelector theSelector;

        /**
         * Constructor.
         * @param pItem the item
         */
        private DirectoryPreferenceElement(final MetisStringPreference<K> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newStringField();
            theField.setEditable(true);

            /* Create the button */
            theButton = theGuiFactory.newButton();
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
            theField.setTheAttributeState(TethysFieldAttribute.CHANGED, theItem.isChanged());
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
                theSelector = theGuiFactory.newDirectorySelector();
                theSelector.setTitle(theItem.getDisplay());
            }
        }
    }

    /**
     * CharArray preference element.
     */
    private final class CharArrayPreferenceElement
            implements PreferenceElement {
        /**
         * The Preference item.
         */
        private final MetisCharArrayPreference<K> theItem;

        /**
         * The Field item.
         */
        private final TethysDataEditField<char[], N, I> theField;

        /**
         * Constructor.
         * @param pItem the item
         */
        private CharArrayPreferenceElement(final MetisCharArrayPreference<K> pItem) {
            /* Store parameters */
            theItem = pItem;
            theField = theGuiFactory.newCharArrayField();
            theField.setEditable(true);

            /* Create the label */
            final TethysLabel<N, I> myLabel = theGuiFactory.newLabel(pItem.getDisplay()
                                                                     + TethysLabel.STR_COLON);
            myLabel.setAlignment(TethysAlignment.EAST);

            /* Add to the Grid Pane */
            theGrid.addCell(myLabel);
            theGrid.setCellAlignment(myLabel, TethysAlignment.EAST);
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
            theField.adjustField();

            /* Handle hidden state */
            theField.setEnabled(!theItem.isHidden());
        }
    }
}
