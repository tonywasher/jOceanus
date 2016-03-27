/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Tethys Table Manager.
 * @param <I> the column identity
 * @param <R> the row type
 * @param <N> the table node
 */
public abstract class TethysTableManager<I, R, N>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The event manager.
     */
    private TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The map of columns.
     */
    private Map<I, TethysTableColumn<I, R, N>> theColumnMap;

    /**
     * The last child item.
     */
    private TethysTableColumn<I, R, N> theLastChild;

    /**
     * Constructor.
     */
    protected TethysTableManager() {
        theEventManager = new TethysEventManager<>();
        theColumnMap = new HashMap<>();
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set the filter.
     * @param pFilter the filter
     */
    public abstract void setFilter(final Predicate<R> pFilter);

    /**
     * Set the comparator.
     * @param pComparator the comparator
     */
    public abstract void setComparator(final Comparator<R> pComparator);

    /**
     * Cascade event.
     * @param pEvent the event
     */
    protected void cascadeEvent(final TethysEvent<TethysUIEvent> pEvent) {
        theEventManager.cascadeEvent(pEvent);
    }

    /**
     * Obtain the column for the id.
     * @param pId the id of the column
     * @return the table column
     */
    public TethysTableColumn<I, R, N> getColumn(final I pId) {
        return theColumnMap.get(pId);
    }

    /**
     * Register the column.
     * @param pColumn the column
     */
    private void registerColumn(final TethysTableColumn<I, R, N> pColumn) {
        theColumnMap.put(pColumn.getId(), pColumn);
    }

    /**
     * Declare string column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareStringColumn(final I pId);

    /**
     * Declare short column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareShortColumn(final I pId);

    /**
     * Declare integer column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareIntegerColumn(final I pId);

    /**
     * Declare long column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareLongColumn(final I pId);

    /**
     * Declare money column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareMoneyColumn(final I pId);

    /**
     * Declare price column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declarePriceColumn(final I pId);

    /**
     * Declare rate column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareRateColumn(final I pId);

    /**
     * Declare units column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareUnitsColumn(final I pId);

    /**
     * Declare dilution column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareDilutionColumn(final I pId);

    /**
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareRatioColumn(final I pId);

    /**
     * Declare dilutedPrice column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareDilutedPriceColumn(final I pId);

    /**
     * Declare date column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<I, R, N> declareDateColumn(final I pId);

    /**
     * Declare scroll column.
     * @param <C> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <C> TethysTableColumn<I, R, N> declareScrollColumn(final I pId,
                                                                       final Class<C> pClass);

    /**
     * Declare list column.
     * @param <C> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <C> TethysTableColumn<I, R, N> declareListColumn(final I pId,
                                                                     final Class<C> pClass);

    /**
     * Declare icon column.
     * @param <C> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <C> TethysTableColumn<I, R, N> declareIconColumn(final I pId,
                                                                     final Class<C> pClass);

    /**
     * Declare stateIcon column.
     * @param <C> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <C> TethysTableColumn<I, R, N> declareStateIconColumn(final I pId,
                                                                          final Class<C> pClass);

    /**
     * Column Definition.
     * @param <I> the column identity
     * @param <R> the row type
     * @param <N> the table node
     */
    public abstract static class TethysTableColumn<I, R, N>
            implements TethysEventProvider<TethysUIEvent> {
        /**
         * The table.
         */
        private final TethysTableManager<I, R, N> theTable;

        /**
         * The event manager.
         */
        private TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The id of the column.
         */
        private final I theId;

        /**
         * The name of the column.
         */
        private final String theName;

        /**
         * The cell type.
         */
        private final TethysFieldType theCellType;

        /**
         * The previous sibling of this item.
         */
        private TethysTableColumn<I, R, N> thePrevSibling;

        /**
         * Is the column visible?
         */
        private boolean isVisible;

        /**
         * Constructor.
         * @param pTable the containing table
         * @param pId the id of the column
         * @param pType the type of the column
         */
        protected TethysTableColumn(final TethysTableManager<I, R, N> pTable,
                                    final I pId,
                                    final TethysFieldType pType) {
            /* Store parameters */
            theTable = pTable;
            theId = pId;
            theCellType = pType;
            theName = pId.toString();
            isVisible = true;

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* If the table already has children */
            TethysTableColumn<I, R, N> myChild = theTable.theLastChild;
            if (myChild != null) {
                /* Link to last child */
                thePrevSibling = myChild;
            }

            /* Add as last child of pane */
            theTable.theLastChild = this;

            /* Register the column */
            theTable.registerColumn(this);
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Obtain the table manager.
         * @return the table manager
         */
        public TethysTableManager<I, R, N> getTable() {
            return theTable;
        }

        /**
         * Obtain the id of the column.
         * @return the column id
         */
        public I getId() {
            return theId;
        }

        /**
         * Obtain the type of the column.
         * @return the column type
         */
        public TethysFieldType getCellType() {
            return theCellType;
        }

        /**
         * Obtain the name of the column.
         * @return the column name
         */
        public String getName() {
            return theName;
        }

        /**
         * Is the column visible?
         * @return true/false
         */
        public boolean isVisible() {
            return isVisible;
        }

        /**
         * Set the visibility of the column.
         * @param pVisible true/false
         */
        public void setVisible(final boolean pVisible) {
            /* If we are changing visibility */
            if (pVisible != isVisible) {
                /* Set new visibility */
                isVisible = pVisible;

                /* If we are showing the column */
                if (pVisible) {
                    /* Attach to table at required position */
                    attachToTable();

                    /* else just detach column */
                } else {
                    detachFromTable();
                }
            }
        }

        /**
         * Attach to table.
         */
        protected abstract void attachToTable();

        /**
         * Detach from table.
         */
        protected abstract void detachFromTable();

        /**
         * Count previous visible items.
         * @return the count
         */
        protected int countPreviousVisibleSiblings() {
            /* Determine the previous visible sibling */
            int myCount = 0;
            TethysTableColumn<I, R, N> mySibling = thePrevSibling;
            while (mySibling != null) {
                if (mySibling.isVisible) {
                    myCount++;
                }
                mySibling = mySibling.thePrevSibling;
            }
            return myCount;
        }
    }

    /**
     * Cell interface.
     * @param <I> the column identity
     * @param <R> the row type
     * @param <C> the value type
     */
    public interface TethysTableCell<I, R, C> {
        /**
         * Obtain the column.
         * @return the column
         */
        TethysTableColumn<I, R, ?> getColumn();

        /**
         * Obtain the control.
         * @return the field
         */
        TethysDataEditField<C, ?, ?, ?, ?> getControl();

        /**
         * Obtain the new value.
         * @return the new value
         */
        C getNewValue();

        /**
         * obtain the current row.
         * @return the row (or null)
         */
        R getActiveRow();

        /**
         * Obtain the id of the column.
         * @return the column id
         */
        I getColumnId();

        /**
         * Obtain the type of the column.
         * @return the column type
         */
        TethysFieldType getCellType();
    }
}
