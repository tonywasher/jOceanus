/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseDialogTable;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSetTableTab.PrometheusFieldSetTable;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * Deposit Rate Table.
 */
public class MoneyWiseDepositRateTable
        extends MoneyWiseDialogTable<MoneyWiseDepositRate>
        implements PrometheusFieldSetTable<MoneyWiseDeposit> {
    /**
     * Deposit.
     */
    private MoneyWiseDeposit theDeposit;

    /**
     * The edit list.
     */
    private MoneyWiseDepositRateList theRates;

    /**
     * The active column.
     */
    private final TethysUITableColumn<MetisAction, MetisDataFieldId, MoneyWiseDepositRate> theActiveColumn;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pError the error panel
     */
    public MoneyWiseDepositRateTable(final MoneyWiseView pView,
                                     final PrometheusEditSet pEditSet,
                                     final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseBasicDataType.DEPOSITRATE);

        /* Access Gui factory */
        final TethysUITableManager<MetisDataFieldId, MoneyWiseDepositRate> myTable = getTable();

        /* Set table configuration */
        myTable.setDisabled(MoneyWiseDepositRate::isDisabled)
                .setComparator(MoneyWiseDepositRate::compareTo);

        /* Create the rate column */
        myTable.declareRateColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE)
                .setCellValueFactory(MoneyWiseDepositRate::getRate)
                .setEditable(true)
                .setColumnWidth(WIDTH_RATE)
                .setOnCommit((r, v) -> updateField(MoneyWiseDepositRate::setRate, r, v));

        /* Create the bonus column */
        myTable.declareRateColumn(MoneyWiseBasicResource.DEPOSITRATE_BONUS)
                .setCellValueFactory(MoneyWiseDepositRate::getBonus)
                .setEditable(true)
                .setColumnWidth(WIDTH_RATE)
                .setOnCommit((r, v) -> updateField(MoneyWiseDepositRate::setBonus, r, v));

        /* Create the endDate column */
        myTable.declareDateColumn(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE)
                .setDateConfigurator(this::handleDateConfig)
                .setCellValueFactory(MoneyWiseDepositRate::getEndDate)
                .setEditable(true)
                .setColumnWidth(WIDTH_DATE)
                .setOnCommit((r, v) -> updateField(MoneyWiseDepositRate::setEndDate, r, v));

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
        /* Access the rates list */
        theRates = getEditSet().getDataList(MoneyWiseBasicDataType.DEPOSITRATE, MoneyWiseDepositRateList.class);
        getTable().setItems(theRates.getUnderlyingList());
    }

    @Override
    public void setItem(final MoneyWiseDeposit pDeposit) {
        /* Store the security */
        if (!MetisDataDifference.isEqual(pDeposit, theDeposit)) {
            theDeposit = pDeposit;
            updateTableData();
        }
    }

    @Override
    protected void addNewItem() {
        /* Protect against Exceptions */
        try {
            /* Add a new rate */
            final MoneyWiseDepositRate myRate = addNewRate();

            /* Shift display to line */
            updateTableData();
            selectItem(myRate);
            getEditSet().incrementVersion();
            notifyChanges();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            /* Build the error */
            final OceanusException myError = new MoneyWiseDataException("Failed to create new rate", e);

            /* Show the error */
            setError(myError);
        }
    }

    /**
     * Add a new rate for a deposit.
     * @throws OceanusException on error
     * @return the rate
     */
    private MoneyWiseDepositRate addNewRate() throws OceanusException {
        /* Create the new rate */
        final MoneyWiseDepositRate myRate = new MoneyWiseDepositRate(theRates);

        /* Set the item value */
        myRate.setDeposit(theDeposit);
        myRate.setRate(TethysRate.getWholePercentage(0));

        /* Access iterator */
        final Iterator<MoneyWiseDepositRate> myIterator = getTable().viewIterator();

        /* Access the last rate and look to see whether we have a null date */
        final MoneyWiseDepositRate myLast = myIterator.hasNext()
                ? myIterator.next()
                : null;
        final boolean hasNullDate = myLast != null && myLast.getEndDate() == null;

        /* If we have a null date in the last element */
        if (hasNullDate) {
            /* Access the previous element */
            final MoneyWiseDepositRate myLatest = myIterator.hasNext()
                    ? myIterator.next()
                    : null;

            /* Assume that we can use todays date */
            TethysDate myDate = new TethysDate();
            if (myLatest != null) {
                /* Obtain the date that is one after the latest date used */
                final TethysDate myNew = new TethysDate(myLatest.getEndDate());
                myNew.adjustDay(1);

                /* Use the latest of the two dates */
                if (myDate.compareTo(myNew) < 0) {
                    myDate = myNew;
                }
            }

            /* Adjust the last date */
            myLast.pushHistory();
            myLast.setNewVersion();
            myLast.setEndDate(myDate);
        }

        /* Add the new item */
        myRate.setNewVersion();
        theRates.add(myRate);

        /*
         * Don't validate the rate yet. We need to take care such that we can only add a new price
         * when there is a slot available, and that we validate the entire list after an update
         */
        return myRate;
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
    protected boolean isFiltered(final MoneyWiseDepositRate pRow) {
        return super.isFiltered(pRow)
                && theDeposit != null
                && theDeposit.equals(pRow.getDeposit());
    }

    /**
     * Handle dateConfig.
     * @param pRow the row
     * @param pConfig the dateConfig
     */
    private void handleDateConfig(final MoneyWiseDepositRate pRow,
                                  final TethysDateConfig pConfig) {
        /* Build a list of used dates */
        final List<TethysDate> myDates = new ArrayList<>();
        final Iterator<MoneyWiseDepositRate> myIterator = getTable().viewIterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDepositRate myRate = myIterator.next();
            myDates.add(myRate.getEndDate());
        }

        /* Set filter constraint */
        final TethysDate myCurrent = pRow.getEndDate();
        pConfig.setAllowed((d) -> {
            return d.equals(myCurrent) || !myDates.contains(d);
        });

        /* Set null constraint */
        final boolean hasNull = myDates.contains(null);
        pConfig.setAllowNullDateSelection(!hasNull || myCurrent == null);
    }
}
