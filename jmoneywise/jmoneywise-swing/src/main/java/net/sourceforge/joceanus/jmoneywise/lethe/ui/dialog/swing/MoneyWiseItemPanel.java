/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataItemPanel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;

/**
 * MoneyWise Data Item Panel.
 * @param <T> the item type
 */
public abstract class MoneyWiseItemPanel<T extends DataItem<MoneyWiseDataType> & Comparable<? super T>>
        extends PrometheusDataItemPanel<T, MoneyWiseGoToId, MoneyWiseDataType> {
    /**
     * Filter text.
     */
    private static final String FILTER_MENU = "Filter";

    /**
     * The DataItem GoToMenuMap.
     */
    private final List<DataItem<MoneyWiseDataType>> theGoToItemList;

    /**
     * The Filter GoToMenuMap.
     */
    private final List<AnalysisFilter<?, ?>> theGoToFilterList;

    /**
     * The Tab Panel.
     */
    private JTabbedPane theTabPane;

    /**
     * The tabs.
     */
    private List<MoneyWiseTab> theTabs;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    protected MoneyWiseItemPanel(final TethysSwingGuiFactory pFactory,
                                 final MetisSwingFieldManager pFieldMgr,
                                 final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                 final MetisErrorPanel<JComponent, Icon> pError) {
        super(pFactory, pFieldMgr, pUpdateSet, pError);
        theGoToFilterList = new ArrayList<>();
        theGoToItemList = new ArrayList<>();
    }

    @Override
    protected void buildGoToMenu(final TethysScrollMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> pMenu) {
        /* Clear the goTo lists */
        theGoToFilterList.clear();
        theGoToItemList.clear();
        pMenu.removeAllItems();

        /* Declare the goTo items */
        declareGoToItems(getUpdateSet().hasUpdates());

        /* Process the goTo items */
        processGoToItems(pMenu);
    }

    /**
     * Declare GoTo Items.
     * @param pUpdates are there active updates?
     */
    protected abstract void declareGoToItems(boolean pUpdates);

    /**
     * Declare GoTo Item.
     * @param pItem the item to declare
     */
    protected void declareGoToItem(final DataItem<MoneyWiseDataType> pItem) {
        /* Ignore null items */
        if (pItem == null) {
            return;
        }

        /* Ignore if the item is already listed */
        if (theGoToItemList.contains(pItem)) {
            return;
        }

        /* remember the item */
        theGoToItemList.add(pItem);
    }

    /**
     * Declare GoTo Filter.
     * @param pFilter the filter to declare
     */
    protected void declareGoToFilter(final AnalysisFilter<?, ?> pFilter) {
        /* Ignore null filters */
        if (pFilter == null) {
            return;
        }

        /* Ignore if the item is already listed */
        if (theGoToFilterList.contains(pFilter)) {
            return;
        }

        /* remember the item */
        theGoToFilterList.add(pFilter);
    }

    /**
     * Process goTo items.
     * @param pMenu the menu
     */
    private void processGoToItems(final TethysScrollMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> pMenu) {
        /* Process goTo filters */
        processGoToFilters(pMenu);

        /* Create a simple map for top-level categories */
        final Map<MoneyWiseDataType, TethysScrollSubMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?>> myMap = new EnumMap<>(MoneyWiseDataType.class);

        /* Loop through the items */
        final Iterator<DataItem<MoneyWiseDataType>> myIterator = theGoToItemList.iterator();
        while (myIterator.hasNext()) {
            final DataItem<MoneyWiseDataType> myItem = myIterator.next();

            /* Determine DataType and obtain parent menu */
            final MoneyWiseDataType myType = myItem.getItemType();
            TethysScrollSubMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> myMenu = myMap.get(myType);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(myType.getItemName());
                myMap.put(myType, myMenu);
            }

            /* set default values */
            MoneyWiseGoToId myId = null;
            String myName = null;

            /* Handle differing items */
            if (myItem instanceof StaticData) {
                final StaticData<?, ?, ?> myStatic = (StaticData<?, ?, ?>) myItem;
                myId = MoneyWiseGoToId.STATIC;
                myName = myStatic.getName();
            } else if (myItem instanceof AssetBase) {
                final AssetBase<?> myAccount = (AssetBase<?>) myItem;
                myId = MoneyWiseGoToId.ACCOUNT;
                myName = myAccount.getName();
            } else if (myItem instanceof CategoryBase) {
                final CategoryBase<?, ?, ?> myCategory = (CategoryBase<?, ?, ?>) myItem;
                myId = MoneyWiseGoToId.CATEGORY;
                myName = myCategory.getName();
            } else if (myItem instanceof Region) {
                final Region myRegion = (Region) myItem;
                myId = MoneyWiseGoToId.REGION;
                myName = myRegion.getName();
            } else if (myItem instanceof TransactionTag) {
                final TransactionTag myTag = (TransactionTag) myItem;
                myId = MoneyWiseGoToId.TAG;
                myName = myTag.getName();
            }

            /* Build the item */
            final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = createGoToEvent(myId, myItem);
            myMenu.getSubMenu().addItem(myEvent, myName);
        }
    }

    /**
     * Process goTo filters.
     * @param pMenu the menu
     */
    private void processGoToFilters(final TethysScrollMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> pMenu) {
        /* Create a simple map for top-level categories */
        TethysScrollSubMenu<PrometheusGoToEvent<MoneyWiseGoToId>, ?> myMenu = null;

        /* Loop through the items */
        final Iterator<AnalysisFilter<?, ?>> myIterator = theGoToFilterList.iterator();
        while (myIterator.hasNext()) {
            final AnalysisFilter<?, ?> myFilter = myIterator.next();

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(FILTER_MENU);
            }

            /* Determine action */
            final StatementSelect<JComponent, Icon> myStatement = new StatementSelect<>(null, myFilter);
            final MoneyWiseGoToId myId = MoneyWiseGoToId.STATEMENT;

            /* Build the item */
            final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = createGoToEvent(myId, myStatement);
            myMenu.getSubMenu().addItem(myEvent, myFilter.getName());
        }
    }

    /**
     * Define panel.
     * @param pPanel the main panel
     */
    protected void defineMainPanel(final MoneyWiseDataPanel pPanel) {
        /* Access the mainPanel */
        final JPanel myPanel = getMainPanel();

        /* Compact the panel */
        pPanel.compactPanel();

        /* If there is a tabbedPane */
        if (theTabPane != null) {
            /* Layout the main panel */
            myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
            myPanel.add(pPanel.getPanel());
            myPanel.add(theTabPane);

            /* else single panel */
        } else {
            myPanel.setLayout(new BorderLayout());
            myPanel.add(pPanel.getPanel(), BorderLayout.CENTER);
        }

        /* Layout the panel */
        layoutPanel();
    }

    /**
     * Define a tab item.
     * @param pTab the tabItem
     */
    protected void defineTabItem(final MoneyWiseTab pTab) {
        /* Make sure that the tabPane exists */
        if (theTabPane == null) {
            theTabPane = new JTabbedPane();
            theTabs = new ArrayList<>();
        }

        /* Define the tab */
        theTabPane.add(pTab.getName(), pTab.getPanel());
        theTabs.add(pTab);
    }

    @Override
    public void setEditable(final boolean isEditable) {
        /* Handle standard processing */
        super.setEditable(isEditable);

        /* If we have a selected item and tabs */
        if (getItem() != null
            && theTabs != null) {
            /* set the tab visibility */
            setTabVisibility();
        }
    }

    /**
     * Set the visibility of the tab.
     */
    private void setTabVisibility() {
        /* Loop through the tabs */
        int myVisible = 0;
        for (MoneyWiseTab myTab : theTabs) {
            /* Determine whether the visibility flags */
            final boolean isVisible = myTab.isVisible();
            final boolean requireVisible = myTab.requireVisible();

            /* If nothing is visible */
            if (!requireVisible) {
                /* If the tab is currently visible */
                if (isVisible) {
                    /* Remove tab and clear flag */
                    theTabPane.remove(myTab.getPanel());
                    myTab.setVisible(false);
                }

                /* else we should be visible */
            } else {
                /* If we are not currently visible */
                if (!isVisible) {
                    /* Add at correct place */
                    theTabPane.add(myTab.getPanel(), myTab.getName(), myVisible);
                    myTab.setVisible(true);
                }

                /* Increment visible tab count */
                myVisible++;
            }
        }
    }

    /**
     * Panel interface.
     */
    private interface MoneyWisePanel {
        /**
         * Obtain the panel.
         * @return the panel
         */
        JComponent getPanel();
    }

    /**
     * Standard Panel.
     */
    protected class MoneyWiseDataPanel
            implements MoneyWisePanel {
        /**
         * The field width.
         */
        private final int theWidth;

        /**
         * The box.
         */
        private final TethysSwingEnablePanel theBox;

        /**
         * The panel.
         */
        private final TethysSwingEnablePanel thePanel;

        /**
         * The layout.
         */
        private final SpringLayout theSpring;

        /**
         * Constructor.
         * @param pWidth the field width
         */
        protected MoneyWiseDataPanel(final int pWidth) {
            /* Store the parameters */
            theWidth = pWidth;

            /* Create the panel */
            thePanel = new TethysSwingEnablePanel();

            /* Layout the panel */
            theSpring = new SpringLayout();
            thePanel.setLayout(theSpring);

            /* Wrap the grid */
            theBox = new TethysSwingEnablePanel();
            theBox.setLayout(new BoxLayout(theBox, BoxLayout.Y_AXIS));
            theBox.add(Box.createVerticalGlue());
            theBox.add(thePanel);
            theBox.add(Box.createVerticalGlue());
        }

        @Override
        public JComponent getPanel() {
            return theBox;
        }

        /**
         * Remove Glue.
         */
        private void removeGlue() {
            if (theBox.getComponentCount() > 1) {
                theBox.remove(0);
                theBox.remove(1);
            }
        }

        /**
         * Add text field.
         * @param pField the field.
         * @param pType the dataType
         * @param pControl the control
         */
        protected void addField(final MetisField pField,
                                final MetisDataType pType,
                                final TethysSwingStringTextField pControl) {
            /* Add to the fieldSet */
            getFieldSet().addFieldElement(pField, pType, pControl);

            /* Restrict the control */
            restrictField(pControl, theWidth);

            /* Add the field to panel */
            addField(pField);
        }

        /**
         * Add scrollPane field.
         * @param pField the field.
         * @param pType the dataType
         * @param pControl the control
         */
        protected void addField(final MetisField pField,
                                final MetisDataType pType,
                                final TethysSwingScrollPaneManager pControl) {
            /* Add to the fieldSet */
            getFieldSet().addFieldElement(pField, pType, pControl);

            /* Add the field to panel */
            addField(pField);

            /* Remove Glue */
            removeGlue();
        }

        /**
         * Add date field.
         * @param pField the field.
         * @param pControl the control
         */
        protected void addField(final MetisField pField,
                                final TethysSwingDateButtonManager pControl) {
            /* Add to the fieldSet */
            getFieldSet().addFieldElement(pField, pControl);

            /* Restrict the control */
            restrictField(pControl, theWidth);

            /* Add the field to panel */
            addField(pField);
        }

        /**
         * Add scroll field.
         * @param <I> the value type
         * @param pField the field.
         * @param pClazz the class of the value
         * @param pControl the control
         */
        protected <I> void addField(final MetisField pField,
                                    final Class<I> pClazz,
                                    final TethysSwingScrollButtonManager<I> pControl) {
            /* Add to the fieldSet */
            getFieldSet().addFieldElement(pField, pClazz, pControl);

            /* Restrict the control */
            restrictField(pControl, theWidth);

            /* Add the field to panel */
            addField(pField);
        }

        /**
         * Add icon field.
         * @param <I> the value type
         * @param pField the field.
         * @param pClazz the class of the value
         * @param pControl the control
         */
        protected <I> void addField(final MetisField pField,
                                    final Class<I> pClazz,
                                    final TethysSwingIconButtonManager<I> pControl) {
            /* Add to the fieldSet */
            getFieldSet().addFieldElement(pField, pClazz, pControl);

            /* Restrict the control */
            restrictField(pControl, theWidth);

            /* Add the field to panel */
            addField(pField);
        }

        /**
         * Add icon field.
         * @param <I> the value type
         * @param pField the field.
         * @param pControl the control
         */
        protected <I extends Comparable<I>> void addField(final MetisField pField,
                                                          final TethysSwingListButtonManager<I> pControl) {
            /* Add to the fieldSet */
            getFieldSet().addFieldElement(pField, pControl);

            /* Restrict the control */
            restrictField(pControl, theWidth);

            /* Add the field to panel */
            addField(pField);
        }

        /**
         * Add field.
         * @param pField the field.
         */
        protected void addField(final MetisField pField) {
            /* Add the field to the panel */
            getFieldSet().addFieldToPanel(pField, thePanel);
        }

        /**
         * Compact panel.
         */
        protected void compactPanel() {
            TethysSwingSpringUtilities.makeCompactGrid(thePanel, theSpring, thePanel.getComponentCount() >> 1, 2, PADDING_SIZE);
        }
    }

    /**
     * Tab interface.
     */
    private interface MoneyWiseTab
            extends MoneyWisePanel {
        /**
         * Is the tab currently visible?
         * @return true/false
         */
        boolean isVisible();

        /**
         * Set the tab visibility status.
         * @param pVisible true/false
         */
        void setVisible(boolean pVisible);

        /**
         * Should the tab be visible?
         * @return true/false
         */
        boolean requireVisible();

        /**
         * Obtain the name of the tab.
         * @return the name
         */
        String getName();
    }

    /**
     * Standard Tab Item.
     */
    protected class MoneyWiseDataTabItem
            extends MoneyWiseDataPanel
            implements MoneyWiseTab {
        /**
         * The name.
         */
        private final String theName;

        /**
         * The fields.
         */
        private final List<MetisField> theFields;

        /**
         * isVisible.
         */
        private boolean isVisible;

        /**
         * Constructor.
         * @param pName the name of the tab.
         * @param pWidth the field width
         */
        protected MoneyWiseDataTabItem(final String pName,
                                       final int pWidth) {
            /* Initialise super-class */
            super(pWidth);

            /* Store parameters */
            theName = pName;

            /* Create the list */
            theFields = new ArrayList<>();

            /* Initialise flags */
            isVisible = true;

            /* Add the panel to the tabbedPane */
            defineTabItem(this);
        }

        @Override
        public boolean isVisible() {
            return isVisible;
        }

        @Override
        public void setVisible(final boolean pVisible) {
            isVisible = pVisible;
        }

        @Override
        public boolean requireVisible() {
            return getFieldSet().isAnyFieldVisible(theFields);
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        protected void addField(final MetisField pField) {
            /* Add the field to the panel */
            super.addField(pField);

            /* Add the field to the list of fields */
            theFields.add(pField);
        }
    }

    /**
     * Table Tab Item.
     */
    protected class MoneyWiseDataTabTable
            implements MoneyWiseTab {
        /**
         * The name.
         */
        private final String theName;

        /**
         * The table.
         */
        private final PrometheusDataTable<?, MoneyWiseDataType> theTable;

        /**
         * isVisible.
         */
        private boolean isVisible;

        /**
         * requireVisible.
         */
        private boolean requireVisible;

        /**
         * Constructor.
         * @param pName the name of the tab.
         * @param pTable the table
         */
        protected MoneyWiseDataTabTable(final String pName,
                                        final PrometheusDataTable<?, MoneyWiseDataType> pTable) {
            /* Store parameters */
            theName = pName;
            theTable = pTable;

            /* Initialise flags */
            isVisible = true;
            requireVisible = true;

            /* Add the panel to the tabbedPane */
            defineTabItem(this);
        }

        @Override
        public boolean isVisible() {
            return isVisible;
        }

        @Override
        public boolean requireVisible() {
            return requireVisible;
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public JComponent getPanel() {
            return theTable.getNode();
        }

        @Override
        public void setVisible(final boolean pVisible) {
            isVisible = pVisible;
        }

        /**
         * Require this table to be visible or not.
         * @param pVisible true/false
         */
        protected void setRequireVisible(final boolean pVisible) {
            requireVisible = pVisible;
        }
    }
}
