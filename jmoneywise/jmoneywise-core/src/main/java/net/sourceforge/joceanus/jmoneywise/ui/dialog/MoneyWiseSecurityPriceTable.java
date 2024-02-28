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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice.MoneyWiseSecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseDialogTable;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSetTableTab.PrometheusFieldSetTable;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * Security Price Table.
 */
public class MoneyWiseSecurityPriceTable
        extends MoneyWiseDialogTable<MoneyWiseSecurityPrice>
        implements PrometheusFieldSetTable<MoneyWiseSecurity> {
    /**
     * Security.
     */
    private MoneyWiseSecurity theSecurity;

    /**
     * The edit list.
     */
    private MoneyWiseSecurityPriceList thePrices;

    /**
     * The active column.
     */
    private final TethysUITableColumn<MetisAction, MetisDataFieldId, MoneyWiseSecurityPrice> theActiveColumn;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    public MoneyWiseSecurityPriceTable(final MoneyWiseView pView,
                                       final PrometheusEditSet pEditSet,
                                       final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.SECURITYPRICE);

        /* Access Gui factory */
        final TethysUITableManager<MetisDataFieldId, MoneyWiseSecurityPrice> myTable = getTable();

        /* Set table configuration */
        myTable.setDisabled(MoneyWiseSecurityPrice::isDisabled)
                .setComparator(MoneyWiseSecurityPrice::compareTo);

        /* Create the date column */
        myTable.declareDateColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE)
                .setCellValueFactory(MoneyWiseSecurityPrice::getDate)
                .setEditable(true)
                .setColumnWidth(WIDTH_DATE)
                .setOnCommit((r, v) -> updateField(MoneyWiseSecurityPrice::setDate, r, v));

        /* Create the price column */
        myTable.declarePriceColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE)
                .setCellValueFactory(MoneyWiseSecurityPrice::getPrice)
                .setEditable(true)
                .setColumnWidth(WIDTH_PRICE)
                .setOnCommit((r, v) -> updateField(MoneyWiseSecurityPrice::setPrice, r, v));

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(pView.getGuiFactory());
        theActiveColumn = myTable.declareIconColumn(PrometheusDataResource.DATAITEM_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
                .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
                .setEditable(true)
                .setCellEditable(r -> !r.isActive())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));
    }

    @Override
    public void refreshData() {
        /* Access the prices list */
        thePrices = getEditSet().getDataList(MoneyWiseBasicDataType.SECURITYPRICE, MoneyWiseSecurityPriceList.class);
        getTable().setItems(thePrices.getUnderlyingList());
    }

    @Override
    public void setItem(final MoneyWiseSecurity pSecurity) {
        /* Store the security */
        if (!MetisDataDifference.isEqual(pSecurity, theSecurity)) {
            theSecurity = pSecurity;
            updateTableData();
        }
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Add a new price */
            final MoneyWiseSecurityPrice myPrice = addNewPrice(theSecurity);

            /* Shift display to line */
            updateTableData();
            selectItem(myPrice);
            getEditSet().incrementVersion();
            notifyChanges();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new price", e);

            /* Show the error */
            setError(myError);
        }
    }

    /**
     * Add a new price for a security.
     * @param pSecurity the security
     * @throws OceanusException on error
     * @return the price
     */
    public MoneyWiseSecurityPrice addNewPrice(final MoneyWiseSecurity pSecurity) throws OceanusException {
        /* Create the new price */
        final MoneyWiseSecurityPrice myPrice = new MoneyWiseSecurityPrice(thePrices);

        /* Set the item value */
        myPrice.setSecurity(pSecurity);
        myPrice.setPrice(TethysPrice.getWholeUnits(1, pSecurity.getCurrency()));

        /* Access iterator */
        final Iterator<MoneyWiseSecurityPrice> myIterator = getTable().viewIterator();

        /* Assume that we can use todays date */
        TethysDate myDate = new TethysDate();

        /* Access the last price */
        final MoneyWiseSecurityPrice myLast = myIterator.hasNext()
                ? myIterator.next()
                : null;

        /* If we have a most recent price */
        if (myLast != null) {
            /* Obtain the date that is one after the latest date used */
            final TethysDate myNew = new TethysDate(myLast.getDate());
            myNew.adjustDay(1);

            /* Use the latest of the two dates */
            if (myDate.compareTo(myNew) < 0) {
                myDate = myNew;
            }
        }

        /* Add to the list */
        myPrice.setDate(myDate);
        myPrice.setNewVersion();
        thePrices.add(myPrice);

        /*
         * Don't validate the price yet. We need to take care such that we can only add a new price
         * when there is a slot available, and that we validate the entire list after an update
         */
        return myPrice;
    }

    @Override
    public boolean isVisible() {
        return isViewActive();
    }

    @Override
    public void setEditable(final boolean pEditable) {
        /* Show/Hide columns/panels */
        super.setEditable(pEditable);
        theActiveColumn.setVisible(pEditable);
    }

    @Override
    protected boolean isFiltered(final MoneyWiseSecurityPrice pRow) {
        return super.isFiltered(pRow)
                && theSecurity != null
                && theSecurity.equals(pRow.getSecurity());
    }
}
