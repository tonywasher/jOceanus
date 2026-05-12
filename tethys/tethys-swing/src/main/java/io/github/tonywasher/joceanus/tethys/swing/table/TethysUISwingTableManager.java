/*
 * Tethys: GUI Utilities
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.tethys.swing.table;

import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableColumn;
import io.github.tonywasher.joceanus.tethys.core.table.TethysUICoreTableManager;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import java.util.List;

/**
 * JavaSwing Table manager.
 *
 * @param <C> the column identity
 * @param <R> the row type
 */
public interface TethysUISwingTableManager<C, R>
        extends TethysUICoreTableManager<C, R> {
    /**
     * Obtain the table model.
     *
     * @return the table model
     */
    AbstractTableModel getTableModel();

    /**
     * Obtain the columnList.
     *
     * @return the columnModel
     */
    List<TethysUITableColumn<?, C, R>> getColumnList();

    /**
     * Obtain the columnModel.
     *
     * @return the columnModel
     */
    TableColumnModel getColumnModel();

    /**
     * Obtain the row for the model index.
     *
     * @param pIndex the index of the column
     * @return the table column
     */
    R getIndexedRow(final int pIndex);

    /**
     * Obtain the cell factory.
     *
     * @return the cell factory
     */
    TethysUISwingTableCellFactory<C, R> getCellFactory();

    /**
     * Notify model that the row has been updated.
     *
     * @param pRowIndex the view row index
     */
    void fireTableRowUpdated(int pRowIndex);
}
