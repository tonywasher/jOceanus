/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.base;

import java.util.List;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseCategoryDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataId;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUILabel;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise Category Table.
 * @param <T> the Category Data type
 * @param <S> the Static Data type
 */
public abstract class MoneyWiseCategoryTable<T extends CategoryBase<T, S>, S extends StaticDataItem<S>>
        extends MoneyWiseBaseTable<T> {
    /**
     * Filter Prompt.
     */
    private static final String TITLE_FILTER = MoneyWiseUIResource.CATEGORY_PROMPT_FILTER.getValue();

    /**
     * Filter Parents Title.
     */
    private static final String FILTER_PARENTS = MoneyWiseUIResource.CATEGORY_FILTER_PARENT.getValue();

    /**
     * The filter panel.
     */
    private final TethysUIBoxPaneManager theFilterPanel;

    /**
     * The select button.
     */
    private final TethysUIScrollButtonManager<T> theSelectButton;

    /**
     * Active parent.
     */
    private T theParent;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     * @param pClazz the dataType class
     * @param pDataType the dataType
     */
    protected MoneyWiseCategoryTable(final MoneyWiseView pView,
                                     final UpdateSet pUpdateSet,
                                     final MetisErrorPanel pError,
                                     final Class<T> pClazz,
                                     final MoneyWiseDataType pDataType) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, pDataType);

        /* Access Gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<PrometheusDataFieldId, T> myTable = getTable();

        /* Create new button */
        final TethysUIButtonFactory<?> myButtons = myGuiFactory.buttonFactory();
        final TethysUIButton myNewButton = myButtons.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create the filter components */
        final TethysUILabel myPrompt = myGuiFactory.controlFactory().newLabel(TITLE_FILTER);
        theSelectButton = myButtons.newScrollButton(pClazz);
        theSelectButton.setValue(null, FILTER_PARENTS);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.paneFactory().newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myPrompt);
        theFilterPanel.addNode(theSelectButton);
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(myNewButton);

        /* Set table configuration */
        myTable.setDisabled(CategoryBase::isDisabled)
               .setComparator(CategoryBase::compareTo);

        /* Create the short name column */
        myTable.declareStringColumn(MoneyWiseCategoryDataId.SUBCAT)
                .setValidator(this::isValidName)
                .setCellValueFactory(this::getShortName)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(CategoryBase::setSubCategoryName, r, v));

        /* Create the full name column */
        myTable.declareStringColumn(MoneyWiseCategoryDataId.NAME)
                .setCellValueFactory(CategoryBase::getName)
                .setEditable(false)
                .setColumnWidth(WIDTH_NAME);

        /* Create the category type column */
        addCategoryTypeColumn();

        /* Create the description column */
        myTable.declareStringColumn(MoneyWiseCategoryDataId.DESC)
                .setValidator(this::isValidDesc)
                .setCellValueFactory(CategoryBase::getDesc)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(CategoryBase::setDescription, r, v));

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(myGuiFactory);
        myTable.declareIconColumn(PrometheusDataId.TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        myNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theSelectButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleParentSelection());
        theSelectButton.setMenuConfigurator(e -> buildSelectMenu());
    }

    /**
     * Add categoryType column.
     */
    protected abstract void addCategoryTypeColumn();

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    public TethysUIBoxPaneManager getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Obtain the short name.
     * @param pCategory the category
     * @return the name
     */
    protected String getShortName(final T pCategory) {
        final String myName = pCategory.getSubCategory();
        return myName == null ? pCategory.getName() : myName;
    }

    /**
     * Obtain the parent.
     * @return the parent
     */
    protected T getParent() {
        return theParent;
    }

    /**
     * Update parent.
     * @param pParent the parent
     */
    protected void updateParent(final T pParent) {
        theParent = pParent;
        theSelectButton.setValue(theParent);
    }

    /**
     * Select parent.
     * @param pParent the parent category
     */
    protected void selectParent(final T pParent) {
        /* If the parent is being changed */
        if (!MetisDataDifference.isEqual(pParent, theParent)) {
            /* Store new value */
            theParent = pParent;

            /* Update select button */
            if (pParent == null) {
                theSelectButton.setValue(null, FILTER_PARENTS);
            } else {
                theSelectButton.setValue(pParent);
            }

            /* Notify table of change */
            updateTableData();
        }
    }

    /**
     * Handle parent selection.
     */
    private void handleParentSelection() {
        selectParent(theSelectButton.getValue());
    }

    /**
     * Build the category type list for the item.
     * @param pCategory the item
     * @param pMenu the menu to build
     */
    protected abstract void buildCategoryTypeMenu(T pCategory,
                                                  TethysUIScrollMenu<S> pMenu);

    /**
     * Obtain the categories.
     * @return the categories
     */
    protected abstract List<T> getCategories();

    /**
     * Is the categoryType a child category.
     * @param pCategoryType the categoryType
     * @return true/false
     */
    protected abstract boolean isChildCategory(S pCategoryType);

    /**
     * Build Select menu.
     */
    private void buildSelectMenu() {
        /* Clear the menu */
        final TethysUIScrollMenu<T> myCategoryMenu = theSelectButton.getMenu();
        myCategoryMenu.removeAllItems();

        /* Cope if we have no categories */
        final List<T> myCategories = getCategories();
        if (myCategories == null) {
            return;
        }

        /* Record active item */
        TethysUIScrollItem<T> myActive = null;

        /* Create the no filter MenuItem and add it to the popUp */
        TethysUIScrollItem<T> myItem = myCategoryMenu.addItem(null, FILTER_PARENTS);

        /* If this is the active parent */
        if (theParent == null) {
            /* Record it */
            myActive = myItem;
        }

        /* Loop through the available category values */
        for (T myCurr : myCategories) {
            final S myType = myCurr.getCategoryType();

            /* Ignore category if it is deleted or a child */
            if (myCurr.isDeleted() || isChildCategory(myType)) {
                continue;
            }

            /* Create a new MenuItem and add it to the popUp */
            myItem = myCategoryMenu.addItem(myCurr);

            /* If this is the active parent */
            if (myCurr.equals(theParent)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * New item.
     */
    protected abstract void addNewItem();

    @Override
    protected String getInvalidNameChars() {
        return ":";
    }
}
