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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.dialog;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWisePriceDataId;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseDialogTable;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataId;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSetTableTab.PrometheusFieldSetTable;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager.TethysTableColumn;

/**
 * Security Price Table.
 */
public class MoneyWiseSecurityPriceTable
        extends MoneyWiseDialogTable<SecurityPrice>
        implements PrometheusFieldSetTable<Security> {
    /**
     * Security.
     */
    private Security theSecurity;

    /**
     * The edit list.
     */
    private SecurityPriceList thePrices;

    /**
     * The active column.
     */
    private final TethysTableColumn<MetisAction, PrometheusDataFieldId, SecurityPrice> theActiveColumn;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    public MoneyWiseSecurityPriceTable(final MoneyWiseView pView,
                                       final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                       final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.SECURITYPRICE);

        /* Access Gui factory */
        final TethysTableManager<PrometheusDataFieldId, SecurityPrice> myTable = getTable();

        /* Set table configuration */
        myTable.setDisabled(SecurityPrice::isDisabled)
               .setComparator(SecurityPrice::compareTo);

        /* Create the date column */
        myTable.declareDateColumn(MoneyWisePriceDataId.DATE)
               .setCellValueFactory(SecurityPrice::getDate)
               .setEditable(true)
               .setColumnWidth(WIDTH_DATE)
               .setOnCommit((r, v) -> updateField(SecurityPrice::setDate, r, v));

        /* Create the price column */
        myTable.declarePriceColumn(MoneyWisePriceDataId.PRICE)
               .setCellValueFactory(SecurityPrice::getPrice)
               .setEditable(true)
               .setColumnWidth(WIDTH_PRICE)
               .setOnCommit((r, v) -> updateField(SecurityPrice::setPrice, r, v));

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        theActiveColumn = myTable.declareIconColumn(PrometheusDataId.TOUCH, MetisAction.class)
               .setIconMapSet(r -> myActionMapSet)
               .setCellValueFactory(r -> r.isActive() ? MetisAction.ACTIVE : MetisAction.DELETE)
               .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
               .setEditable(true)
               .setCellEditable(r -> !r.isActive())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Set standard size */
        getTable().setPreferredWidthAndHeight(WIDTH_PANEL >> 1, HEIGHT_PANEL >> 2);
    }

    @Override
    public void refreshData() {
        /* Access the prices list */
        thePrices = getUpdateSet().getDataList(MoneyWiseDataType.SECURITYPRICE, SecurityPriceList.class);
        getTable().setItems(thePrices.getUnderlyingList());
    }

    @Override
    public void setItem(final Security pSecurity) {
        /* Store the security */
        if (!MetisDataDifference.isEqual(pSecurity, theSecurity)) {
            theSecurity = pSecurity;
            getTable().fireTableDataChanged();
        }
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Add a new price */
            final SecurityPrice myPrice = addNewPrice(theSecurity);

            /* Shift display to line */
            getTable().fireTableDataChanged();
            getTable().selectRowWithScroll(myPrice);
            getUpdateSet().incrementVersion();
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
    public SecurityPrice addNewPrice(final Security pSecurity) throws OceanusException {
        /* Create the new price */
        final SecurityPrice myPrice = new SecurityPrice(thePrices);

        /* Set the item value */
        myPrice.setSecurity(pSecurity);
        myPrice.setPrice(TethysPrice.getWholeUnits(1, pSecurity.getCurrency()));

        /* Access iterator */
        final Iterator<SecurityPrice> myIterator = getTable().viewIterator();

        /* Assume that we can use todays date */
        TethysDate myDate = new TethysDate();

        /* Access the last price */
        final SecurityPrice myLast = myIterator.hasNext()
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
        return !isViewEmpty();
    }

    @Override
    public void setEditable(final boolean pEditable) {
        /* Show/Hide columns/panels */
        super.setEditable(pEditable);
        theActiveColumn.setVisible(pEditable);
    }

    @Override
    protected boolean isFiltered(final SecurityPrice pRow) {
        return super.isFiltered(pRow)
                && theSecurity != null
                && theSecurity.equals(pRow.getSecurity());
    }
}
