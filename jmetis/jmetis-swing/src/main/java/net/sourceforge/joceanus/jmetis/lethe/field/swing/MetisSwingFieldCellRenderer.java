/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.field.swing;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager.PopulateFieldData;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIcon;

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
                                        final MetisSwingFieldData pData) {
        /* Ignore if the model is not applicable */
        final TableModel myTableModel = pTable.getModel();
        if (!(myTableModel instanceof PopulateFieldData)) {
            return;
        }

        /* Access the table model. */
        final PopulateFieldData myModel = (PopulateFieldData) myTableModel;

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
    public static class MetisFieldStringCellRenderer
            extends DefaultTableCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2004841981078780283L;

        /**
         * The Render Data.
         */
        private final transient MetisSwingFieldData theData;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected MetisFieldStringCellRenderer(final MetisSwingFieldManager pManager) {
            this(pManager.allocateRenderData(false), SwingConstants.LEFT);
        }

        /**
         * Constructor for fixed width.
         * @param pData the render data
         * @param pAlignment the alignment
         */
        private MetisFieldStringCellRenderer(final MetisSwingFieldData pData,
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
    public static class MetisFieldIntegerCellRenderer
            extends MetisFieldStringCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2004841981078780283L;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected MetisFieldIntegerCellRenderer(final MetisSwingFieldManager pManager) {
            super(pManager.allocateRenderData(true), SwingConstants.CENTER);
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is an integer */
            if (o instanceof Integer) {
                /* Convert to a string */
                final Integer i = (Integer) o;
                o = i.toString();
            }

            /* Pass value on */
            super.setValue(o);
        }
    }

    /**
     * Icon Cell Renderer.
     * @param <T> the object type
     */
    public static class MetisFieldIconButtonCellRenderer<T>
            extends DefaultTableCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 103642334541674910L;

        /**
         * The GUI factory.
         */
        private final transient TethysSwingGuiFactory theFactory;

        /**
         * The Render Data.
         */
        private final transient MetisSwingFieldData theData;

        /**
         * The Class of the data.
         */
        private final transient Class<T> theClazz;

        /**
         * The iconMap.
         */
        private final transient Map<TethysIconId, TethysIcon> theIconMap;

        /**
         * The MapSet supplier.
         */
        private transient Function<Integer, TethysIconMapSet<T>> theSupplier;

        /**
         * Constructor.
         * @param pFactory the GUI factory
         * @param pManager the renderer manager
         * @param pClazz the item class
         */
        protected MetisFieldIconButtonCellRenderer(final TethysSwingGuiFactory pFactory,
                                                   final MetisSwingFieldManager pManager,
                                                   final Class<T> pClazz) {
            this(pFactory, pManager.allocateRenderData(true), pClazz);
        }

        /**
         * Constructor for fixed width.
         * @param pFactory the GUI factory
         * @param pData the render data
         * @param pClazz the item class
         */
        private MetisFieldIconButtonCellRenderer(final TethysSwingGuiFactory pFactory,
                                                 final MetisSwingFieldData pData,
                                                 final Class<T> pClazz) {
            /* Store data */
            theFactory = pFactory;
            theData = pData;
            theClazz = pClazz;
            setHorizontalAlignment(SwingConstants.CENTER);
            theSupplier = i -> null;
            theIconMap = new HashMap<>();
        }

        /**
         * Set the IconMapSet supplier.
         * @param pSupplier the supplier
         */
        public void setIconMapSet(final Function<Integer, TethysIconMapSet<T>> pSupplier) {
            theSupplier = pSupplier;
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

            /* Determine the row and value */
            final int iRow = pTable.convertRowIndexToModel(pRowIndex);
            final T myValue = theClazz.cast(pValue);

            /* Determine the IconMapSet */
            final TethysIconMapSet<T> myMapSet = theSupplier == null
                                                                     ? null
                                                                     : theSupplier.apply(iRow);

            /* Determine the icon and toolTip */
            final TethysIconId myIconId = myMapSet == null
                                                           ? null
                                                           : myMapSet.getIconForValue(myValue);
            final String myTooltip = myMapSet == null
                                                      ? null
                                                      : myMapSet.getTooltipForValue(myValue);

            /* Store details */
            final Icon myIcon = resolveIcon(myIconId);
            setIcon(myIcon);
            setText(null);
            if (theData.getToolTip() == null) {
                setToolTipText(myTooltip);
            }

            /* Return this as the render item */
            return this;
        }

        /**
         * ResolveIcon.
         * @param pIconId the iconId
         * @return the icon
         */
        private Icon resolveIcon(final TethysIconId pIconId) {
            /* Handle null icon */
            if (pIconId == null) {
                return null;
            }

            /* Look up icon */
            TethysIcon myIcon = theIconMap.get(pIconId);
            if (myIcon == null) {
                myIcon = theFactory.resolveIcon(pIconId, TethysIconButtonManager.DEFAULT_ICONWIDTH);
                theIconMap.put(pIconId, myIcon);
            }
            return TethysSwingIcon.getIcon(myIcon);
        }
    }

    /**
     * Calendar Cell Renderer.
     */
    public static class MetisFieldCalendarCellRenderer
            extends MetisFieldStringCellRenderer {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 1947211408966548011L;

        /**
         * Date Formatter.
         */
        private final transient TethysDateFormatter theFormatter;

        /**
         * Constructor.
         * @param pManager the renderer manager
         * @param pFormatter the formatter
         */
        protected MetisFieldCalendarCellRenderer(final MetisSwingFieldManager pManager,
                                                 final TethysDateFormatter pFormatter) {
            super(pManager.allocateRenderData(true), SwingConstants.CENTER);
            theFormatter = pFormatter;
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is a Date */
            if (o instanceof TethysDate) {
                /* Format it */
                o = theFormatter.formatDate((TethysDate) o);
            }

            /* Pass value on */
            super.setValue(o);
        }
    }

    /**
     * Decimal Cell Renderer.
     */
    public static class MetisFieldDecimalCellRenderer
            extends MetisFieldStringCellRenderer {
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
        protected MetisFieldDecimalCellRenderer(final MetisSwingFieldManager pManager,
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
    public static class MetisFieldRowCellRenderer
            extends MetisFieldStringCellRenderer {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = 8710214547908947657L;

        /**
         * Constructor.
         * @param pManager the renderer manager
         */
        protected MetisFieldRowCellRenderer(final MetisSwingFieldManager pManager) {
            super(pManager.allocateRenderData(true), SwingConstants.CENTER);
        }

        @Override
        public void setValue(final Object pValue) {
            Object o = pValue;

            /* If the value is an integer */
            if (o instanceof Integer) {
                /* Convert to a string */
                final Integer i = (Integer) o;
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
