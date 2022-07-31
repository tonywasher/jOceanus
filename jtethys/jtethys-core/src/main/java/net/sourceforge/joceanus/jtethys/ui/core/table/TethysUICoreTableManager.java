/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.core.table;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldType;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Tethys Table Manager.
 * @param <C> the column identity
 * @param <R> the row type
 */
public abstract class TethysUICoreTableManager<C, R>
        extends TethysUICoreComponent
        implements TethysUITableManager<C, R> {
    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The map of columns.
     */
    private final Map<C, TethysUIBaseTableColumn<?, C, R>> theColumnMap;

    /**
     * The last child item.
     */
    private TethysUIBaseTableColumn<?, C, R> theLastChild;

    /**
     * The Error Predicate.
     */
    private BiPredicate<C, R> theError;

    /**
     * The Changed Predicate.
     */
    private BiPredicate<C, R> theChanged;

    /**
     * The Disabled Predicate.
     */
    private Predicate<R> theDisabled;

    /**
     * The OnCommit Consumer.
     */
    private TethysUIOnRowCommit<R> theOnCommit;

    /**
     * The OnSelect Consumer.
     */
    private TethysUIOnRowSelect<R> theOnSelect;

    /**
     * The OnCommitError Consumer.
     */
    private Consumer<OceanusException> theOnCommitError;

    /**
     * The OnValidateError Consumer.
     */
    private Consumer<String> theOnValidateError;

    /**
     * The OnCellEditState Consumer.
     */
    private Consumer<Boolean> theOnCellEditState;

    /**
     * The Comparator.
     */
    private Comparator<R> theComparator;

    /**
     * The Filter.
     */
    private Predicate<R> theFilter;

    /**
     * Is the table editable?
     */
    private boolean isEditable;

    /**
     * Repaint the row on commit?
     */
    private boolean doRePaintRowOnCommit;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysUICoreTableManager(final TethysUICoreFactory<?> pFactory) {
        theId = pFactory.getNextId();
        theColumnMap = new HashMap<>();
        isEditable = true;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public boolean isEditable() {
        return isEditable;
    }

    @Override
    public TethysUITableManager<C, R> setEditable(final boolean pEditable) {
        isEditable = pEditable;
        return this;
    }

    @Override
    public boolean doRePaintRowOnCommit() {
        return doRePaintRowOnCommit;
    }

    @Override
    public TethysUITableManager<C, R> setRepaintRowOnCommit(final boolean pRePaint) {
        doRePaintRowOnCommit = pRePaint;
        return this;
    }

    @Override
    public TethysUITableManager<C, R> setError(final BiPredicate<C, R> pError) {
        theError = pError;
        return this;
    }

    @Override
    public boolean isError(final C pId,
                           final R pRow) {
        return theError != null
                && theError.test(pId, pRow);
    }

    @Override
    public TethysUITableManager<C, R> setChanged(final BiPredicate<C, R> pChanged) {
        theChanged = pChanged;
        return this;
    }

    @Override
    public boolean isChanged(final C pId,
                             final R pRow) {
        return theChanged != null
                && theChanged.test(pId, pRow);
    }

    @Override
    public TethysUITableManager<C, R> setDisabled(final Predicate<R> pDisabled) {
        theDisabled = pDisabled;
        return this;
    }

    @Override
    public boolean isDisabled(final R pRow) {
        return theDisabled != null
                && theDisabled.test(pRow);
    }

    @Override
    public TethysUITableManager<C, R> setFilter(final Predicate<R> pFilter) {
        theFilter = pFilter;
        return this;
    }

    /**
     * Obtain the filter.
     * @return the filter
     */
    protected Predicate<R> getFilter() {
        return theFilter;
    }

    @Override
    public TethysUITableManager<C, R> setComparator(final Comparator<R> pComparator) {
        theComparator = pComparator;
        return this;
    }

    /**
     * Obtain the comparator.
     * @return the comparator
     */
    protected Comparator<R> getComparator() {
        return theComparator;
    }

    @Override
    public TethysUITableManager<C, R> setOnCommit(final TethysUIOnRowCommit<R> pOnCommit) {
        theOnCommit = pOnCommit;
        return this;
    }

    /**
     * process onCommit.
     * @param pRow the row
     * @throws OceanusException on error
     */
    void processOnCommit(final R pRow) throws OceanusException {
        /* If we have an onCommit consumer */
        if (theOnCommit != null) {
            /* call it */
            theOnCommit.commitRow(pRow);
        }
    }

    @Override
    public TethysUITableManager<C, R> setOnSelect(final TethysUIOnRowSelect<R> pOnSelect) {
        theOnSelect = pOnSelect;
        return this;
    }

    /**
     * process onSelect.
     * @param pRow the row
     */
    protected void processOnSelect(final R pRow) {
        /* If we have an onSelect consumer */
        if (theOnSelect != null) {
            /* call it */
            theOnSelect.selectRow(pRow);
        }
    }

    @Override
    public TethysUITableManager<C, R> setOnCellEditState(final Consumer<Boolean> pOnCellEditState) {
        theOnCellEditState = pOnCellEditState;
        return this;
    }

    /**
     * process onCellEditState.
     * @param pState the state
     */
    public void processOnCellEditState(final Boolean pState) {
        /* If we have an onCellEditState consumer */
        if (theOnCellEditState != null) {
            /* call it */
            theOnCellEditState.accept(pState);
        }
    }

    @Override
    public TethysUITableManager<C, R> setOnCommitError(final Consumer<OceanusException> pOnCommitError) {
        theOnCommitError = pOnCommitError;
        return this;
    }

    /**
     * process onCommitError.
     * @param pError the error
     */
    public void processOnCommitError(final OceanusException pError) {
        /* If we have an onCommitError consumer */
        if (theOnCommitError != null) {
            /* call it */
            theOnCommitError.accept(pError);
        }
    }

    @Override
    public TethysUITableManager<C, R> setOnValidateError(final Consumer<String> pOnValidateError) {
        theOnValidateError = pOnValidateError;
        return this;
    }

    @Override
    public Consumer<String> getOnValidateError() {
        return theOnValidateError;
    }

    @Override
    public TethysUITableColumn<?, C, R> getColumn(final C pId) {
        return theColumnMap.get(pId);
    }

    @Override
    public void repaintColumn(final C pId) {
        final TethysUITableColumn<?, C, R> myCol = theColumnMap.get(pId);
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
    void registerColumn(final TethysUIBaseTableColumn<?, C, R> pColumn) {
        theColumnMap.put(pColumn.getId(), pColumn);
    }

    /**
     * Repaint on Commit.
     * @param pCell the cell that was committed.
     */
    public void rePaintOnCommit(final TethysUITableCell<?, C, R> pCell) {
        /* If we should rePaint the row on commit */
        if (doRePaintRowOnCommit) {
            /* Action the repaint */
            pCell.repaintCellRow();
        }

        /* Obtain the update id */
        final C myId = pCell.getColumnId();

        /* Loop through the columns */
        for (TethysUIBaseTableColumn<?, C, R> myColumn : theColumnMap.values()) {
            /* If we should rePaint the column on commit */
            if (myColumn.doRePaintColumnOnCommit()) {
                /* Action the repaint */
                repaintColumn(myColumn.getId());
            }

            /* If we should repaint this cell on commit */
            if (myId.equals(myColumn.getRePaintColumnId())) {
                /* Action the rePaint */
                pCell.repaintColumnCell(myColumn.getId());
            }
        }
    }

    /**
     * Column Definition.
     * @param <T> the data type
     * @param <C> the column identity
     * @param <R> the row type
     */
    public abstract static class TethysUIBaseTableColumn<T, C, R>
            implements TethysUITableColumn<T, C, R> {
        /**
         * The table.
         */
        private final TethysUICoreTableManager<C, R> theTable;

        /**
         * The event manager.
         */
        private final TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The id of the column.
         */
        private final C theId;

        /**
         * The cell type.
         */
        private final TethysUIFieldType theCellType;

        /**
         * The name of the column.
         */
        private String theName;

        /**
         * The previous sibling of this item.
         */
        private TethysUIBaseTableColumn<?, C, R> thePrevSibling;

        /**
         * Is the column visible?
         */
        private boolean isVisible;

        /**
         * Is the column editable?
         */
        private boolean isEditable;

        /**
         * The cell-editable predicate.
         */
        private Predicate<R> isCellEditable;

        /**
         * The cell-commit consumer.
         */
        private TethysUIOnCellCommit<R, T> theOnCommit;

        /**
         * Repaint the column on commit?
         */
        private boolean doRePaintColOnCommit;

        /**
         * The column that when updated, will force this column to update.
         */
        private C theRePaintId;

        /**
         * Constructor.
         * @param pTable the containing table
         * @param pId the id of the column
         * @param pType the type of the column
         */
        protected TethysUIBaseTableColumn(final TethysUICoreTableManager<C, R> pTable,
                                          final C pId,
                                          final TethysUIFieldType pType) {
            /* Store parameters */
            theTable = pTable;
            theId = pId;
            theCellType = pType;
            theName = pId.toString();
            isVisible = true;
            isEditable = true;

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* If the table already has children */
            final TethysUIBaseTableColumn<?, C, R> myChild = theTable.theLastChild;
            if (myChild != null) {
                /* Link to last child */
                thePrevSibling = myChild;
            }

            /* Add as last child of pane */
            theTable.theLastChild = this;

            /* Register the column */
            theTable.registerColumn(this);

            /* Initialise editable */
            isCellEditable = p -> true;
        }

        @Override
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        @Override
        public TethysUICoreTableManager<C, R> getTable() {
            return theTable;
        }

        @Override
        public C getId() {
            return theId;
        }

        @Override
        public TethysUIFieldType getCellType() {
            return theCellType;
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public TethysUIBaseTableColumn<T, C, R> setName(final String pName) {
            theName = pName;
            return this;
        }

        @Override
        public boolean isVisible() {
            return isVisible;
        }

        @Override
        public boolean isEditable() {
            return isEditable;
        }

        @Override
        public boolean doRePaintColumnOnCommit() {
            return doRePaintColOnCommit;
        }

        @Override
        public TethysUIBaseTableColumn<T, C, R> setRepaintColumnOnCommit(final boolean pRePaint) {
            doRePaintColOnCommit = pRePaint;
            return this;
        }

        @Override
        public C getRePaintColumnId() {
            return theRePaintId;
        }

        @Override
        public TethysUIBaseTableColumn<T, C, R> setRepaintColumnId(final C pRePaintId) {
            theRePaintId = pRePaintId;
            return this;
        }

        @Override
        public TethysUIBaseTableColumn<T, C, R> setVisible(final boolean pVisible) {
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
            return this;
        }

        @Override
        public TethysUIBaseTableColumn<T, C, R> setEditable(final boolean pEditable) {
            isEditable = pEditable;
            return this;
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
            TethysUIBaseTableColumn<?, C, R> mySibling = thePrevSibling;
            while (mySibling != null) {
                if (mySibling.isVisible) {
                    myCount++;
                }
                mySibling = mySibling.thePrevSibling;
            }
            return myCount;
        }

        @Override
        public TethysUIBaseTableColumn<T, C, R> setCellEditable(final Predicate<R> pEditable) {
            isCellEditable = pEditable;
            return this;
        }

        @Override
        public Predicate<R> getCellEditable() {
            return isCellEditable;
        }

        @Override
        public TethysUIBaseTableColumn<T, C, R> setOnCommit(final TethysUIOnCellCommit<R, T> pOnCommit) {
            theOnCommit = pOnCommit;
            return this;
        }

        /**
         * process onCommit.
         * @param pRow the row
         * @param pValue the value
         * @throws OceanusException on error
         */
        public void processOnCommit(final R pRow,
                                    final T pValue) throws OceanusException {
            /* If we have an onCommit consumer */
            if (theOnCommit != null) {
                /* call it */
                theOnCommit.commitCell(pRow, pValue);
            }

            /* Call the table onCommit */
            getTable().processOnCommit(pRow);
        }
    }
}
