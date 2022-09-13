/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.base;

import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseCategoryDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CategoryBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CategoryInterface;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataId;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;

/**
 * MoneyWise Category Table.
 * @param <T> the Category Data type
 * @param <S> the Static Data type
 * @param <C> the Static Data class
 */
public abstract class MoneyWiseCategoryTable<T extends CategoryBase<T, S, C>, S extends StaticData<S, C, MoneyWiseDataType>, C extends Enum<C> & CategoryInterface>
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
    private final TethysBoxPaneManager theFilterPanel;

    /**
     * The select button.
     */
    private final TethysScrollButtonManager<T> theSelectButton;

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
                                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                     final MetisErrorPanel pError,
                                     final Class<T> pClazz,
                                     final MoneyWiseDataType pDataType) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, pDataType);

        /* Access Gui factory */
        final TethysGuiFactory myGuiFactory = pView.getGuiFactory();
        final TethysTableManager<PrometheusDataFieldId, T> myTable = getTable();

        /* Create new button */
        final TethysButton myNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(myNewButton);

        /* Create the filter components */
        final TethysLabel myPrompt = myGuiFactory.newLabel(TITLE_FILTER);
        theSelectButton = myGuiFactory.newScrollButton(pClazz);
        theSelectButton.setValue(null, FILTER_PARENTS);

        /* Create a filter panel */
        theFilterPanel = myGuiFactory.newHBoxPane();
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
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
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
        theSelectButton.getEventRegistrar().addEventListener(TethysXUIEvent.NEWVALUE, e -> handleParentSelection());
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
    public TethysBoxPaneManager getFilterPanel() {
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
            getTable().fireTableDataChanged();
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
                                                  TethysScrollMenu<S> pMenu);

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
        final TethysScrollMenu<T> myCategoryMenu = theSelectButton.getMenu();
        myCategoryMenu.removeAllItems();

        /* Cope if we have no categories */
        final List<T> myCategories = getCategories();
        if (myCategories == null) {
            return;
        }

        /* Record active item */
        TethysScrollMenuItem<T> myActive = null;

        /* Create the no filter MenuItem and add it to the popUp */
        TethysScrollMenuItem<T> myItem = myCategoryMenu.addItem(null, FILTER_PARENTS);

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
