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

import java.util.function.Function;
import java.util.function.Predicate;

import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.field.TethysUIFieldType;
import net.sourceforge.joceanus.tethys.ui.api.table.TethysUITableColumn;

/**
 * Column Definition.
 * @param <T> the data type
 * @param <C> the column identity
 * @param <R> the row type
 */
public abstract class TethysUICoreTableColumn<T, C, R>
        implements TethysUITableColumn<T, C, R> {
    /**
     * The table.
     */
    private final TethysUICoreTableManager<C, R> theTable;

    /**
     * The event manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

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
    private TethysUICoreTableColumn<?, C, R> thePrevSibling;

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
     * Cell value factory.
     */
    private Function<R, T> theValueFactory;

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
    protected TethysUICoreTableColumn(final TethysUICoreTableManager<C, R> pTable,
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
        theEventManager = new OceanusEventManager<>();

        /* If the table already has children */
        final TethysUICoreTableColumn<?, C, R> myChild = theTable.getLastChild();
        if (myChild != null) {
            /* Link to last child */
            thePrevSibling = myChild;
        }

        /* Add as last child of pane */
        theTable.setLastChild(this);

        /* Register the column */
        theTable.registerColumn(this);

        /* Initialise editable */
        isCellEditable = p -> true;

        /* Set default value factory */
        theValueFactory = e -> null;
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
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
    public TethysUICoreTableColumn<T, C, R> setName(final String pName) {
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
    public TethysUICoreTableColumn<T, C, R> setRepaintColumnOnCommit(final boolean pRePaint) {
        doRePaintColOnCommit = pRePaint;
        return this;
    }

    @Override
    public C getRePaintColumnId() {
        return theRePaintId;
    }

    @Override
    public TethysUICoreTableColumn<T, C, R> setRepaintColumnId(final C pRePaintId) {
        theRePaintId = pRePaintId;
        return this;
    }

    @Override
    public TethysUICoreTableColumn<T, C, R> setVisible(final boolean pVisible) {
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
    public TethysUICoreTableColumn<T, C, R> setCellValueFactory(final Function<R, T> pFactory) {
        theValueFactory = pFactory;
        return this;
    }

    @Override
    public T getValueForRow(final R pRow) {
        return theValueFactory.apply(pRow);
    }

    /**
     * Get cell value Factory.
     *
     * @return the cell factory
     */
    public Function<R, T> getCellValueFactory() {
        return theValueFactory;
    }

    @Override
    public TethysUICoreTableColumn<T, C, R> setEditable(final boolean pEditable) {
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
        TethysUICoreTableColumn<?, C, R> mySibling = thePrevSibling;
        while (mySibling != null) {
            if (mySibling.isVisible) {
                myCount++;
            }
            mySibling = mySibling.thePrevSibling;
        }
        return myCount;
    }

    @Override
    public TethysUICoreTableColumn<T, C, R> setCellEditable(final Predicate<R> pEditable) {
        isCellEditable = pEditable;
        return this;
    }

    @Override
    public Predicate<R> getCellEditable() {
        return isCellEditable;
    }

    @Override
    public TethysUICoreTableColumn<T, C, R> setOnCommit(final TethysUIOnCellCommit<R, T> pOnCommit) {
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
