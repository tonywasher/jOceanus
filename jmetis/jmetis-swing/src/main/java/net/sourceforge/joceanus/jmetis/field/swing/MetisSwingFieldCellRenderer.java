/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.field.swing;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager.PopulateFieldData;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.date.swing.TethysSwingDateCellRenderer;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton.IconButtonState;

/**
 * Cell renderers.
 * @author Tony Washer
 */
public final class MetisSwingFieldCellRenderer {
    /**
     * Private constructor to avoid instantiation.
     */
    private MetisSwingFieldCellRenderer() {
    }

    /**
     * Render the component.
     * @param pTable the table
     * @param pComponent the component
     * @param pData the render data
     */
    private static void renderComponent(final JTable pTable,
                                        final JComponent pComponent,
                                        final MetisFieldData pData) {
        /* Ignore if the model is not applicable */
        TableModel myTableModel = pTable.getModel();
        if (!(myTableModel instanceof PopulateFieldData)) {
            return;
        }

        /* Access the table model. */
        PopulateFieldData myModel = (PopulateFieldData) myTableModel;

        /* Determine the field data */
        myModel.populateFieldData(pData);

        /* Apply the Field Data */
        pComponent.setForeground(pData.getForeGround());
        pComponent.setBackground(pData.getBackGround());
        pComponent.setFont(pData.getFont());
        pComponent.setToolTipText(pData.getToolTip());
    }

    /**
     * String Cell Renderer.
     */
    public static class StringCellRenderer
            extends DefaultTableCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2004841981078780283L;

        /**
         * The Render Data.
         */
        private final transient MetisFieldData theData;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected StringCellRenderer(final MetisFieldManager pManager) {
            this(pManager.allocateRenderData(false), SwingConstants.LEFT);
        }

        /**
         * Constructor for fixed width.
         * @param pData the render data
         * @param pAlignment the alignment
         */
        private StringCellRenderer(final MetisFieldData pData,
                                   final int pAlignment) {
            theData = pData;
            setHorizontalAlignment(pAlignment);
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* Convert null value */
            if (o == null) {
                o = "";
            }

            /* Pass value on */
            super.setValue(o);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable pTable,
                                                        final Object pValue,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int pRowIndex,
                                                        final int pColIndex) {
            /* Pass call on */
            super.getTableCellRendererComponent(pTable, pValue, isSelected, hasFocus, pRowIndex, pColIndex);

            /* Declare the Cell position */
            theData.setPosition(pRowIndex, pColIndex, isSelected);

            /* Determine the render data */
            renderComponent(pTable, this, theData);

            /* Return this as the render item */
            return this;
        }
    }

    /**
     * Integer Cell Renderer.
     */
    public static class IntegerCellRenderer
            extends StringCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2004841981078780283L;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected IntegerCellRenderer(final MetisFieldManager pManager) {
            super(pManager.allocateRenderData(true), SwingConstants.CENTER);
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is an integer */
            if (o instanceof Integer) {
                /* Convert to a string */
                Integer i = (Integer) o;
                o = i.toString();
            }

            /* Pass value on */
            super.setValue(o);
        }
    }

    /**
     * Boolean Cell Renderer.
     */
    public static class BooleanCellRenderer
            extends JCheckBox
            implements TableCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5574017062142502506L;

        /**
         * The Render Data.
         */
        private final transient MetisFieldData theData;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected BooleanCellRenderer(final MetisFieldManager pManager) {
            this(pManager.allocateRenderData(true), SwingConstants.CENTER);
        }

        /**
         * Constructor for fixed width.
         * @param pData the render data
         * @param pAlignment the alignment
         */
        protected BooleanCellRenderer(final MetisFieldData pData,
                                      final int pAlignment) {
            theData = pData;
            setHorizontalAlignment(pAlignment);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable pTable,
                                                        final Object pValue,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int pRowIndex,
                                                        final int pColIndex) {
            /* Pass call on */
            setSelected((pValue != null)
                        && ((Boolean) pValue).booleanValue());

            /* Declare the Cell position */
            theData.setPosition(pRowIndex, pColIndex, isSelected);

            /* Determine the render data */
            renderComponent(pTable, this, theData);

            /* Return this as the render item */
            return this;
        }
    }

    /**
     * Icon Cell Renderer.
     * @param <T> the object type
     */
    public static class IconButtonCellRenderer<T>
            extends DefaultTableCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 103642334541674910L;

        /**
         * The Render Data.
         */
        private final transient MetisFieldData theData;

        /**
         * IconState.
         */
        private final transient IconButtonState<T> theState;

        /**
         * Constructor.
         * @param pManager the renderer manager
         * @param pEditor the cell editor
         */
        protected IconButtonCellRenderer(final MetisFieldManager pManager,
                                         final IconButtonCellEditor<T> pEditor) {
            this(pManager.allocateRenderData(true), pEditor);
        }

        /**
         * Constructor for fixed width.
         * @param pData the render data
         * @param pEditor the cell editor
         */
        protected IconButtonCellRenderer(final MetisFieldData pData,
                                         final IconButtonCellEditor<T> pEditor) {
            /* Store data */
            theData = pData;
            theState = pEditor.getState();
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable pTable,
                                                        final Object pValue,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int pRowIndex,
                                                        final int pColIndex) {
            /* Pass call on */
            super.getTableCellRendererComponent(pTable, pValue, isSelected, hasFocus, pRowIndex, pColIndex);

            /* Declare the Cell position */
            theData.setPosition(pRowIndex, pColIndex, isSelected);

            /* Determine the render data */
            renderComponent(pTable, this, theData);

            /* If we are using a complexState */
            Icon myIcon;
            String myTooltip;
            if (theState instanceof ComplexIconButtonState) {
                ComplexIconButtonState<T, Boolean> myState = getComplexState();

                /* Determine whether the cell is editable */
                int iRow = pTable.convertRowIndexToModel(pRowIndex);
                int iCol = pTable.convertColumnIndexToModel(pColIndex);
                Boolean isEditable = pTable.getModel().isCellEditable(iRow, iCol);

                /* Determine icon */
                myIcon = myState.getIconForValueAndState(pValue, isEditable);
                myTooltip = myState.getToolTipForValueAndState(pValue, isEditable);
            } else {
                myIcon = theState.getIconForValue(pValue);
                myTooltip = theState.getToolTipForValue(pValue);
            }

            /* Store details */
            setIcon(myIcon);
            setText(null);
            if (theData.getToolTip() == null) {
                setToolTipText(myTooltip);
            }

            /* Return this as the render item */
            return this;
        }

        /**
         * Obtain complex state.
         * @return the state machine
         */
        @SuppressWarnings("unchecked")
        public ComplexIconButtonState<T, Boolean> getComplexState() {
            return (theState instanceof ComplexIconButtonState)
                                                                ? (ComplexIconButtonState<T, Boolean>) theState
                                                                : null;
        }
    }

    /**
     * Calendar Cell Renderer.
     */
    public static class CalendarCellRenderer
            extends TethysSwingDateCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 1947211408966548011L;

        /**
         * The Render Data.
         */
        private final transient MetisFieldData theData;

        /**
         * Constructor.
         * @param pManager the renderer manager
         * @param pFormatter the formatter
         */
        protected CalendarCellRenderer(final MetisFieldManager pManager,
                                       final TethysDateFormatter pFormatter) {
            super(pFormatter);
            theData = pManager.allocateRenderData(true);
            setHorizontalAlignment(SwingConstants.LEFT);
        }

        @Override
        public JComponent getTableCellRendererComponent(final JTable pTable,
                                                        final Object pValue,
                                                        final boolean isSelected,
                                                        final boolean hasFocus,
                                                        final int pRowIndex,
                                                        final int pColIndex) {
            /* Pass call on */
            super.getTableCellRendererComponent(pTable, pValue, isSelected, hasFocus, pRowIndex, pColIndex);

            /* Declare the Cell position in terms of the model */
            theData.setPosition(pRowIndex, pColIndex, isSelected);

            /* Determine the render data */
            renderComponent(pTable, this, theData);

            /* Return this as the render item */
            return this;
        }
    }

    /**
     * Decimal Cell Renderer.
     */
    public static class DecimalCellRenderer
            extends StringCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6571410292897989673L;

        /**
         * Decimal Parser.
         */
        private final transient TethysDecimalFormatter theFormatter;

        /**
         * Constructor.
         * @param pManager the renderer manager
         * @param pFormatter the formatter
         */
        protected DecimalCellRenderer(final MetisFieldManager pManager,
                                      final TethysDecimalFormatter pFormatter) {
            super(pManager.allocateRenderData(true), SwingConstants.RIGHT);
            theFormatter = pFormatter;
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is a Decimal */
            if (o instanceof TethysDecimal) {
                /* Format it */
                o = theFormatter.formatDecimal((TethysDecimal) o);
            }

            /* Pass value down */
            super.setValue(o);
        }
    }

    /**
     * Row Cell Renderer.
     */
    public static class RowCellRenderer
            extends StringCellRenderer {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 8710214547908947657L;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected RowCellRenderer(final MetisFieldManager pManager) {
            super(pManager.allocateRenderData(true), SwingConstants.CENTER);
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is an integer */
            if (o instanceof Integer) {
                /* Convert to a string */
                Integer i = (Integer) o;
                if (i == 0) {
                    o = "";
                } else {
                    o = i.toString();
                }
            }

            /* Pass value on */
            super.setValue(o);
        }
    }
}
