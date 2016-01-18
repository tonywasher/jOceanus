/*******************************************************************************
 * jMetis: Java Data Framework
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/field/package-info.java $
 * $Revision: 587 $
 * $Author: Tony $
 * $Date: 2015-03-31 14:44:28 +0100 (Tue, 31 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.newfield.javafx;

import java.util.Currency;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.newfield.MetisFieldSetPanel;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyItem;
import net.sourceforge.joceanus.jtethys.ui.TethysDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXListButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataButtonField.TethysFXScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXDilutionTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXIntegerTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXLongTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXMoneyTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXPriceTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRateTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXRatioTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXShortTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXStringTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDataTextField.TethysFXUnitsTextField;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXIconButton.TethysFXSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXListButton.TethysFXListButtonManager;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXScrollButton.TethysFXScrollButtonManager;

/**
 * JavaFX FieldSet Panel.
 */
public class MetisFXFieldSetPanel
        extends MetisFieldSetPanel<Node, Color, Font, Node> {
    /**
     * The Node.
     */
    private final VBox theNode;

    /**
     * The Node Children.
     */
    private final ObservableList<Node> theChildren;

    /**
     * Constructor.
     * @param pParent the parent pair
     */
    protected MetisFXFieldSetPanel(final MetisFXFieldSetPanelPair pParent) {
        /* Initialise underlying panel */
        super(pParent);

        /* Create the Node */
        theNode = new VBox();
        theChildren = theNode.getChildren();
    }

    /**
     * Constructor.
     * @param pAttributes the attribute set
     */
    public MetisFXFieldSetPanel(final MetisFXFieldAttributeSet pAttributes) {
        this(pAttributes, new MetisDataFormatter());
    }

    /**
     * Constructor.
     * @param pAttributes the attribute set
     * @param pFormatter the formatter
     */
    public MetisFXFieldSetPanel(final MetisFXFieldAttributeSet pAttributes,
                                final MetisDataFormatter pFormatter) {
        /* Initialise underlying class */
        super(pAttributes, pFormatter);

        /* Create the Node */
        theNode = new VBox();
        theChildren = theNode.getChildren();
    }

    @Override
    public Node getNode() {
        return theNode;
    }

    @Override
    public MetisFXFieldSetStringItem addStringField(final MetisField pField) {
        return new MetisFXFieldSetStringItem(this, pField);
    }

    @Override
    public MetisFXFieldSetShortItem addShortField(final MetisField pField) {
        return new MetisFXFieldSetShortItem(this, pField);
    }

    @Override
    public MetisFXFieldSetIntegerItem addIntegerField(final MetisField pField) {
        return new MetisFXFieldSetIntegerItem(this, pField);
    }

    @Override
    public MetisFXFieldSetLongItem addLongField(final MetisField pField) {
        return new MetisFXFieldSetLongItem(this, pField);
    }

    @Override
    public MetisFXFieldSetMoneyItem addMoneyField(final MetisField pField) {
        return new MetisFXFieldSetMoneyItem(this, pField);
    }

    @Override
    public MetisFXFieldSetPriceItem addPriceField(final MetisField pField) {
        return new MetisFXFieldSetPriceItem(this, pField);
    }

    @Override
    public MetisFXFieldSetRateItem addRateField(final MetisField pField) {
        return new MetisFXFieldSetRateItem(this, pField);
    }

    @Override
    public MetisFXFieldSetUnitsItem addUnitsField(final MetisField pField) {
        return new MetisFXFieldSetUnitsItem(this, pField);
    }

    @Override
    public MetisFXFieldSetDilutionItem addDilutionField(final MetisField pField) {
        return new MetisFXFieldSetDilutionItem(this, pField);
    }

    @Override
    public MetisFXFieldSetRatioItem addRatioField(final MetisField pField) {
        return new MetisFXFieldSetRatioItem(this, pField);
    }

    @Override
    public MetisFXFieldSetDateItem addDateButtonField(final MetisField pField) {
        return new MetisFXFieldSetDateItem(this, pField);
    }

    @Override
    public MetisFXFieldSetDateItem addDateButtonField(final MetisField pField,
                                                      final TethysDateButtonManager<?> pManager) {
        return new MetisFXFieldSetDateItem(this, pField, (TethysFXDateButtonManager) pManager);
    }

    @Override
    public <T> MetisFXFieldSetScrollItem<T> addScrollButtonField(final MetisField pField,
                                                                 final Class<T> pClass) {
        return new MetisFXFieldSetScrollItem<>(this, pField, pClass);
    }

    @Override
    public <T> MetisFXFieldSetScrollItem<T> addScrollButtonField(final MetisField pField,
                                                                 final TethysScrollButtonManager<T, Node> pManager,
                                                                 final Class<T> pClass) {
        return new MetisFXFieldSetScrollItem<>(this, pField, pClass, (TethysFXScrollButtonManager<T>) pManager);
    }

    @Override
    public <T> MetisFXFieldSetListItem<T> addListButtonField(final MetisField pField,
                                                             final Class<T> pClass) {
        return new MetisFXFieldSetListItem<>(this, pField, pClass);
    }

    @Override
    public <T> MetisFXFieldSetListItem<T> addListButtonField(final MetisField pField,
                                                             final TethysListButtonManager<T, Node> pManager,
                                                             final Class<T> pClass) {
        return new MetisFXFieldSetListItem<>(this, pField, pClass, (TethysFXListButtonManager<T>) pManager);
    }

    @Override
    public <T> MetisFXFieldSetIconItem<T> addIconButtonField(final MetisField pField,
                                                             final Class<T> pClass) {
        return new MetisFXFieldSetIconItem<>(this, pField, pClass);
    }

    @Override
    public <T> MetisFXFieldSetIconItem<T> addIconButtonField(final MetisField pField,
                                                             final TethysIconButtonManager<T, Node> pManager,
                                                             final Class<T> pClass) {
        return new MetisFXFieldSetIconItem<>(this, pField, pClass, pManager);
    }

    /**
     * Item class.
     * @param <T> the item type
     */
    protected static class MetisFXFieldSetPanelItem<T>
            extends MetisFieldSetPanelItem<T, Node, Color, Font, Node> {
        /**
         * The node.
         */
        private final BorderPane theNode;

        /**
         * The label.
         */
        private final Label theLabel;

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field definition
         * @param pClass the item class
         * @param pNumeric is the field numeric?
         * @param pEdit the edit field
         */
        protected MetisFXFieldSetPanelItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField,
                                           final Class<T> pClass,
                                           final boolean pNumeric,
                                           final TethysDataEditField<T, Node, Color, Font, Node> pEdit) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, pNumeric, pEdit);

            /* Create the label */
            theLabel = new Label();
            theLabel.setText(pField.getName() + ":");
            theLabel.setAlignment(Pos.CENTER_RIGHT);

            /* Create the Node */
            theNode = new BorderPane();
            theNode.setLeft(theLabel);
            theNode.setCenter(pEdit.getNode());

            /* Add to panel */
            getPanel().theChildren.add(theNode);
        }

        @Override
        protected MetisFXFieldSetPanel getPanel() {
            return (MetisFXFieldSetPanel) super.getPanel();
        }

        @Override
        protected void attachAsChildNo(final int pChildNo) {
            getPanel().theChildren.add(pChildNo, theNode);
        }

        @Override
        protected void detachFromPanel(final int pIndex) {
            getPanel().theChildren.remove(pIndex);
        }

        @Override
        protected double getLabelWidth() {
            return theLabel.getWidth();
        }

        @Override
        protected void setLabelWidth(final double pWidth) {
            theLabel.setMinWidth(pWidth);
        }

        @Override
        protected void setTheValue(final T pValue) {
            getEditField().setValue(pValue);
        }

        @Override
        protected void setEditable(final boolean pEditable) {
            getEditField().setEditable(pEditable);
        }
    }

    /**
     * DateButtonField.
     */
    public static class MetisFXFieldSetDateItem
            extends MetisFXFieldSetPanelItem<TethysDate>
            implements MetisFieldSetDateItem {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetDateItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField) {
            /* Create a new dateButton Manager */
            this(pPanel, pField, new TethysFXDateButtonManager(pPanel.getFormatter()));
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pManager the date manager
         */
        protected MetisFXFieldSetDateItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField,
                                          final TethysFXDateButtonManager pManager) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDate.class, false, new TethysFXDateButtonField(pManager));
        }

        @Override
        protected TethysFXDateButtonField getEditField() {
            return (TethysFXDateButtonField) super.getEditField();
        }

        @Override
        public TethysFXDateButtonManager getManager() {
            return getEditField().getDateManager();
        }
    }

    /**
     * ScrollButtonField.
     * @param <T> the item class
     */
    public static class MetisFXFieldSetScrollItem<T>
            extends MetisFXFieldSetPanelItem<T>
            implements MetisFieldSetScrollItem<T, Node> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisFXFieldSetScrollItem(final MetisFXFieldSetPanel pPanel,
                                            final MetisField pField,
                                            final Class<T> pClass) {
            /* Create a new scrollButton Manager */
            this(pPanel, pField, pClass, new TethysFXScrollButtonManager<>());
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pManager the scroll manager
         * @param pClass the item class
         */
        protected MetisFXFieldSetScrollItem(final MetisFXFieldSetPanel pPanel,
                                            final MetisField pField,
                                            final Class<T> pClass,
                                            final TethysFXScrollButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, false, new TethysFXScrollButtonField<T>(pManager));
        }

        @Override
        protected TethysFXScrollButtonField<T> getEditField() {
            return (TethysFXScrollButtonField<T>) super.getEditField();
        }

        @Override
        public TethysFXScrollButtonManager<T> getManager() {
            return getEditField().getScrollManager();
        }
    }

    /**
     * ListButtonField.
     * @param <T> the item class
     */
    public static class MetisFXFieldSetListItem<T>
            extends MetisFXFieldSetPanelItem<T>
            implements MetisFieldSetListItem<T, Node> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisFXFieldSetListItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField,
                                          final Class<T> pClass) {
            /* Create a new listButton Manager */
            this(pPanel, pField, pClass, new TethysFXListButtonManager<>());
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pManager the list manager
         * @param pClass the item class
         */
        protected MetisFXFieldSetListItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField,
                                          final Class<T> pClass,
                                          final TethysFXListButtonManager<T> pManager) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, false, new TethysFXListButtonField<T>(pManager));
        }

        @Override
        protected TethysFXListButtonField<T> getEditField() {
            return (TethysFXListButtonField<T>) super.getEditField();
        }

        @Override
        public TethysFXListButtonManager<T> getManager() {
            return getEditField().getListManager();
        }
    }

    /**
     * IconButtonField.
     * @param <T> the item class
     */
    public static class MetisFXFieldSetIconItem<T>
            extends MetisFXFieldSetPanelItem<T>
            implements MetisFieldSetIconItem<T, Node> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisFXFieldSetIconItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField,
                                          final Class<T> pClass) {
            /* Create a new iconButton Manager */
            this(pPanel, pField, pClass, new TethysFXSimpleIconButtonManager<>());
        }

        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pManager the list manager
         * @param pClass the item class
         */
        protected MetisFXFieldSetIconItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField,
                                          final Class<T> pClass,
                                          final TethysIconButtonManager<T, Node> pManager) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, false, new TethysFXIconButtonField<T>(pManager));
        }

        @Override
        protected TethysFXIconButtonField<T> getEditField() {
            return (TethysFXIconButtonField<T>) super.getEditField();
        }

        @Override
        public TethysIconButtonManager<T, Node> getManager() {
            return getEditField().getIconManager();
        }
    }

    /**
     * StringField.
     */
    public static class MetisFXFieldSetStringItem
            extends MetisFXFieldSetPanelItem<String> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetStringItem(final MetisFXFieldSetPanel pPanel,
                                            final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, String.class, false, new TethysFXStringTextField());
        }

        @Override
        protected TethysFXStringTextField getEditField() {
            return (TethysFXStringTextField) super.getEditField();
        }
    }

    /**
     * ShortField.
     */
    public static class MetisFXFieldSetShortItem
            extends MetisFXFieldSetPanelItem<Short> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetShortItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Short.class, true, new TethysFXShortTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXShortTextField getEditField() {
            return (TethysFXShortTextField) super.getEditField();
        }
    }

    /**
     * IntegerField.
     */
    public static class MetisFXFieldSetIntegerItem
            extends MetisFXFieldSetPanelItem<Integer> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetIntegerItem(final MetisFXFieldSetPanel pPanel,
                                             final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Integer.class, true, new TethysFXIntegerTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXIntegerTextField getEditField() {
            return (TethysFXIntegerTextField) super.getEditField();
        }
    }

    /**
     * LongField.
     */
    public static class MetisFXFieldSetLongItem
            extends MetisFXFieldSetPanelItem<Long> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetLongItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Long.class, true, new TethysFXLongTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXLongTextField getEditField() {
            return (TethysFXLongTextField) super.getEditField();
        }
    }

    /**
     * MoneyField.
     */
    public static class MetisFXFieldSetMoneyItem
            extends MetisFXFieldSetPanelItem<TethysMoney>
            implements TethysCurrencyItem {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetMoneyItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysMoney.class, true, new TethysFXMoneyTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXMoneyTextField getEditField() {
            return (TethysFXMoneyTextField) super.getEditField();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditField().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * PriceField.
     */
    public static class MetisFXFieldSetPriceItem
            extends MetisFXFieldSetPanelItem<TethysPrice>
            implements TethysCurrencyItem {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetPriceItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysPrice.class, true, new TethysFXPriceTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXPriceTextField getEditField() {
            return (TethysFXPriceTextField) super.getEditField();
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            getEditField().setDeemedCurrency(pCurrency);
        }
    }

    /**
     * RateField.
     */
    public static class MetisFXFieldSetRateItem
            extends MetisFXFieldSetPanelItem<TethysRate> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetRateItem(final MetisFXFieldSetPanel pPanel,
                                          final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRate.class, true, new TethysFXRateTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXRateTextField getEditField() {
            return (TethysFXRateTextField) super.getEditField();
        }
    }

    /**
     * UnitsField.
     */
    public static class MetisFXFieldSetUnitsItem
            extends MetisFXFieldSetPanelItem<TethysUnits> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetUnitsItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysUnits.class, true, new TethysFXUnitsTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXUnitsTextField getEditField() {
            return (TethysFXUnitsTextField) super.getEditField();
        }
    }

    /**
     * DilutionField.
     */
    public static class MetisFXFieldSetDilutionItem
            extends MetisFXFieldSetPanelItem<TethysDilution> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetDilutionItem(final MetisFXFieldSetPanel pPanel,
                                              final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDilution.class, true, new TethysFXDilutionTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXDilutionTextField getEditField() {
            return (TethysFXDilutionTextField) super.getEditField();
        }
    }

    /**
     * RatioField.
     */
    public static class MetisFXFieldSetRatioItem
            extends MetisFXFieldSetPanelItem<TethysRatio> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFXFieldSetRatioItem(final MetisFXFieldSetPanel pPanel,
                                           final MetisField pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRatio.class, true, new TethysFXRatioTextField(pPanel.getFormatter()));
        }

        @Override
        protected TethysFXRatioTextField getEditField() {
            return (TethysFXRatioTextField) super.getEditField();
        }
    }
}
