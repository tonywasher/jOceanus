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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ids.MoneyWiseRateDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate.DepositRateList;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.base.MoneyWiseDialogTable;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseXView;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataId;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSetTableTab.PrometheusXFieldSetTable;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * Deposit Rate Table.
 */
public class MoneyWiseDepositRateTable
        extends MoneyWiseDialogTable<DepositRate>
        implements PrometheusXFieldSetTable<Deposit> {
    /**
     * Deposit.
     */
    private Deposit theDeposit;

    /**
     * The edit list.
     */
    private DepositRateList theRates;

    /**
     * The active column.
     */
    private final TethysUITableColumn<MetisAction, PrometheusDataFieldId, DepositRate> theActiveColumn;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     */
    public MoneyWiseDepositRateTable(final MoneyWiseXView pView,
                                     final UpdateSet pUpdateSet,
                                     final MetisErrorPanel pError) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.DEPOSITRATE);

        /* Access Gui factory */
        final TethysUITableManager<PrometheusDataFieldId, DepositRate> myTable = getTable();

        /* Set table configuration */
        myTable.setDisabled(DepositRate::isDisabled)
               .setComparator(DepositRate::compareTo);

        /* Create the rate column */
        myTable.declareRateColumn(MoneyWiseRateDataId.RATE)
               .setCellValueFactory(DepositRate::getRate)
               .setEditable(true)
               .setColumnWidth(WIDTH_RATE)
               .setOnCommit((r, v) -> updateField(DepositRate::setRate, r, v));

        /* Create the bonus column */
        myTable.declareRateColumn(MoneyWiseRateDataId.BONUS)
               .setCellValueFactory(DepositRate::getBonus)
               .setEditable(true)
               .setColumnWidth(WIDTH_RATE)
               .setOnCommit((r, v) -> updateField(DepositRate::setBonus, r, v));

        /* Create the endDate column */
        myTable.declareDateColumn(MoneyWiseRateDataId.ENDDATE)
                .setCellValueFactory(DepositRate::getEndDate)
                .setEditable(true)
                .setColumnWidth(WIDTH_DATE)
                .setOnCommit((r, v) -> updateField(DepositRate::setEndDate, r, v));

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(pView.getGuiFactory());
        theActiveColumn = myTable.declareIconColumn(PrometheusDataId.TOUCH, MetisAction.class)
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
        theRates = getUpdateSet().getDataList(MoneyWiseDataType.DEPOSITRATE, DepositRateList.class);
        getTable().setItems(theRates.getUnderlyingList());
    }

    @Override
    public void setItem(final Deposit pDeposit) {
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
            final DepositRate myRate = addNewRate();

            /* Shift display to line */
            updateTableData();
            selectItem(myRate);
            getUpdateSet().incrementVersion();
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
    private DepositRate addNewRate() throws OceanusException {
        /* Create the new rate */
        final DepositRate myRate = new DepositRate(theRates);

        /* Set the item value */
        myRate.setDeposit(theDeposit);
        myRate.setRate(TethysRate.getWholePercentage(0));

        /* Access iterator */
        final Iterator<DepositRate> myIterator = getTable().viewIterator();

        /* Access the last rate and look to see whether we have a null date */
        final DepositRate myLast = myIterator.hasNext()
                ? myIterator.next()
                : null;
        final boolean hasNullDate = myLast != null && myLast.getEndDate() == null;

        /* If we have a null date in the last element */
        if (hasNullDate) {
            /* Access the previous element */
            final DepositRate myLatest = myIterator.hasNext()
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
    protected boolean isFiltered(final DepositRate pRow) {
        return super.isFiltered(pRow)
                && theDeposit != null
                && theDeposit.equals(pRow.getDeposit());
    }
}
