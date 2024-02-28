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
package net.sourceforge.joceanus.jtethys.ui.api.table;

import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldType;

/**
 * Cell interface.
 * @param <T> the value type
 * @param <C> the column identity
 * @param <R> the row type
 */
public interface TethysUITableCell<T, C, R> {
    /**
     * Obtain the table.
     * @return the column
     */
    TethysUITableManager<C, R> getTable();

    /**
     * Obtain the column.
     * @return the column
     */
    TethysUITableColumn<T, C, R> getColumn();

    /**
     * Obtain the control.
     * @return the field
     */
    TethysUIDataEditField<T> getControl();

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
    TethysUIFieldType getCellType();

    /**
     * Cell changed during edit.
     * @param pId the column id
     */
    void repaintColumnCell(C pId);

    /**
     * Row changed during edit.
     */
    void repaintCellRow();
}

