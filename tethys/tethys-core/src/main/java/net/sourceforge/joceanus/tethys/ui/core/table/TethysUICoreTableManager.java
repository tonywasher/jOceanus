/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.core.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableCell;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableManager;
import net.sourceforge.joceanus.tethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;

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
    private final Map<C, TethysUICoreTableColumn<?, C, R>> theColumnMap;

    /**
     * The last child item.
     */
    private TethysUICoreTableColumn<?, C, R> theLastChild;

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
     * The item list.
     */
    private List<R> theItems;

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
    public void setItems(final List<R> pItems) {
        theItems = pItems;
    }

    @Override
    public Iterator<R> itemIterator() {
        return theItems == null
                ? Collections.emptyIterator()
                : theItems.iterator();
    }

    /**
     * Obtain items.
     * @return the items
     */
    public List<R> getItems() {
        return theItems;
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
    protected void processOnCommit(final R pRow) throws OceanusException {
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
    public Iterator<C> columnIterator() {
        return theColumnMap.keySet().iterator();
    }

    @Override
    public TethysUITableColumn<?, C, R> getColumn(final C pId) {
        return theColumnMap.get(pId);
    }

    @Override
    public void repaintColumn(final C pId) {
        final TethysUITableColumn<?, C, R> myCol = theColumnMap.get(pId);
        if (myCol != null
                && myCol.isVisible()) {
            myCol.setVisible(false);
            myCol.setVisible(true);
        }
    }

    /**
     * Register the column.
     * @param pColumn the column
     */
    void registerColumn(final TethysUICoreTableColumn<?, C, R> pColumn) {
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
        for (TethysUICoreTableColumn<?, C, R> myColumn : theColumnMap.values()) {
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
     * Get the last child.
     * @return the last child
     */
    TethysUICoreTableColumn<?, C, R> getLastChild() {
        return theLastChild;
    }

    /**
     * Set the last child.
     * @param pChild the last child
     */
     void setLastChild(final TethysUICoreTableColumn<?, C, R> pChild) {
        theLastChild = pChild;
    }
}
