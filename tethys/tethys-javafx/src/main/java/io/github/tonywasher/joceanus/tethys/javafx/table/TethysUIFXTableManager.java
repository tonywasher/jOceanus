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

package io.github.tonywasher.joceanus.tethys.javafx.table;

import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableCell;
import io.github.tonywasher.joceanus.tethys.core.table.TethysUICoreTableManager;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

/**
 * Tethys Table Manager.
 *
 * @param <C> the column identity
 * @param <R> the row type
 */
public interface TethysUIFXTableManager<C, R>
        extends TethysUICoreTableManager<C, R> {
    /**
     * Obtain the columns.
     *
     * @return the columns
     */
    ObservableList<TableColumn<R, ?>> getColumns();

    /**
     * Obtain the cell factory.
     *
     * @return the cell factory
     */
    TethysUIFXTableCellFactory<C, R> getCellFactory();

    /**
     * Set the active cell.
     *
     * @param pCell the actively editing cell
     */
    void setActiveCell(final TethysUITableCell<?, C, R> pCell);

    /**
     * Is the table locked for editing.
     *
     * @return true/false
     */
    boolean isEditLocked();
}
