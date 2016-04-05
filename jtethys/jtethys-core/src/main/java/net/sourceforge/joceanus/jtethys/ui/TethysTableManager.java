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
 * @param <C> the column identity
 * @param <R> the row type
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysTableManager<C, R, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The map of columns.
     */
    private final Map<C, TethysTableColumn<C, R, N, I>> theColumnMap;

    /**
     * The last child item.
     */
    private TethysTableColumn<C, R, N, I> theLastChild;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysTableManager(final TethysGuiFactory<N, I> pFactory) {
        theEventManager = new TethysEventManager<>();
        theId = pFactory.getNextId();
        theColumnMap = new HashMap<>();
    }

    @Override
    public Integer getId() {
        return theId;
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
    public TethysTableColumn<C, R, N, I> getColumn(final C pId) {
        return theColumnMap.get(pId);
    }

    /**
     * Repaint the column.
     * @param pId the column id
     */
    public void repaintColumn(final C pId) {
        TethysTableColumn<C, R, N, I> myCol = theColumnMap.get(pId);
        if ((myCol != null)
            && (myCol.isVisible())) {
            myCol.setVisible(false);
            myCol.setVisible(true);
        }
    }

    /**
     * Register the column.
     * @param pColumn the column
     */
    private void registerColumn(final TethysTableColumn<C, R, N, I> pColumn) {
        theColumnMap.put(pColumn.getId(), pColumn);
    }

    /**
     * Declare string column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareStringColumn(final C pId);

    /**
     * Declare short column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareShortColumn(final C pId);

    /**
     * Declare integer column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareIntegerColumn(final C pId);

    /**
     * Declare long column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareLongColumn(final C pId);

    /**
     * Declare money column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareMoneyColumn(final C pId);

    /**
     * Declare price column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declarePriceColumn(final C pId);

    /**
     * Declare rate column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareRateColumn(final C pId);

    /**
     * Declare units column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareUnitsColumn(final C pId);

    /**
     * Declare dilution column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareDilutionColumn(final C pId);

    /**
     * Declare ratio column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareRatioColumn(final C pId);

    /**
     * Declare dilutedPrice column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareDilutedPriceColumn(final C pId);

    /**
     * Declare date column.
     * @param pId the column id
     * @return the column
     */
    public abstract TethysTableColumn<C, R, N, I> declareDateColumn(final C pId);

    /**
     * Declare scroll column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<C, R, N, I> declareScrollColumn(final C pId,
                                                                          final Class<T> pClass);

    /**
     * Declare list column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<C, R, N, I> declareListColumn(final C pId,
                                                                        final Class<T> pClass);

    /**
     * Declare icon column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<C, R, N, I> declareIconColumn(final C pId,
                                                                        final Class<T> pClass);

    /**
     * Declare stateIcon column.
     * @param <T> the column type
     * @param pId the column id
     * @param pClass the column class
     * @return the column
     */
    public abstract <T> TethysTableColumn<C, R, N, I> declareStateIconColumn(final C pId,
                                                                             final Class<T> pClass);

    /**
     * Column Definition.
     * @param <C> the column identity
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public abstract static class TethysTableColumn<C, R, N, I>
            implements TethysEventProvider<TethysUIEvent> {
        /**
         * The table.
         */
        private final TethysTableManager<C, R, N, I> theTable;

        /**
         * The event manager.
         */
        private TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The id of the column.
         */
        private final C theId;

        /**
         * The cell type.
         */
        private final TethysFieldType theCellType;

        /**
         * The name of the column.
         */
        private String theName;

        /**
         * The previous sibling of this item.
         */
        private TethysTableColumn<C, R, N, I> thePrevSibling;

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
        protected TethysTableColumn(final TethysTableManager<C, R, N, I> pTable,
                                    final C pId,
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
            TethysTableColumn<C, R, N, I> myChild = theTable.theLastChild;
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
        public TethysTableManager<C, R, N, I> getTable() {
            return theTable;
        }

        /**
         * Obtain the id of the column.
         * @return the column id
         */
        public C getId() {
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
         * Set the name of the column.
         * @param pName the column name
         */
        public void setName(final String pName) {
            theName = pName;
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
            TethysTableColumn<C, R, N, I> mySibling = thePrevSibling;
            while (mySibling != null) {
                if (mySibling.isVisible) {
                    myCount++;
                }
                mySibling = mySibling.thePrevSibling;
            }
            return myCount;
        }

        /**
         * Set the column width.
         * @param pWidth the width
         */
        public abstract void setColumnWidth(final int pWidth);
    }

    /**
     * Cell interface.
     * @param <T> the value type
     * @param <C> the column identity
     * @param <R> the row type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public interface TethysTableCell<T, C, R, N, I> {
        /**
         * Obtain the column.
         * @return the column
         */
        TethysTableColumn<C, R, N, I> getColumn();

        /**
         * Obtain the control.
         * @return the field
         */
        TethysDataEditField<T, N, I> getControl();

        /**
         * Obtain the new value.
         * @return the new value
         */
        T getNewValue();

        /**
         * obtain the current row.
         * @return the row (or null)
         */
        R getActiveRow();

        /**
         * Obtain the id of the column.
         * @return the column id
         */
        C getColumnId();

        /**
         * Obtain the type of the column.
         * @return the column type
         */
        TethysFieldType getCellType();

        /**
         * Cell changed during edit.
         * @param pId the column id
         */
        void repaintColumnCell(final C pId);
    }
}
