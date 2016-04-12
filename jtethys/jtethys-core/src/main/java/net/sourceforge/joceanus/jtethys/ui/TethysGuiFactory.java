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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;

/**
 * Tethys GUI Factory.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysGuiFactory<N, I> {
    /**
     * The Next nodeId.
     */
    private final AtomicInteger theNextNodeId = new AtomicInteger(1);

    /**
     * Data Formatter.
     */
    private final TethysDataFormatter theFormatter;

    /**
     * The node Map.
     */
    private final Map<Integer, TethysParentNode<N>> theParentMap;

    /**
     * ParentNode.
     * @param <N> the node type
     */
    @FunctionalInterface
    public interface TethysParentNode<N> {
        /**
         * Set child visibility.
         * @param pChild the child
         * @param pVisible the visibility
         */
        void setChildVisible(final TethysNode<N> pChild,
                             final boolean pVisible);
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    protected TethysGuiFactory(final TethysDataFormatter pFormatter) {
        theFormatter = pFormatter;
        theParentMap = new HashMap<>();
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    public TethysDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the next id.
     * @return the next id
     */
    public Integer getNextId() {
        return theNextNodeId.getAndIncrement();
    }

    /**
     * Set visibility of node.
     * @param pNode the node
     * @param pVisible true/false
     */
    public void setNodeVisible(final TethysNode<N> pNode,
                               final boolean pVisible) {
        /* Lookup parent */
        TethysParentNode<N> myParent = theParentMap.get(pNode.getId());
        if (myParent != null) {
            myParent.setChildVisible(pNode, pVisible);
        }
    }

    /**
     * Register Child.
     * @param pParent the parent
     * @param pChild the child node
     */
    protected void registerChild(final TethysParentNode<N> pParent,
                                 final TethysNode<N> pChild) {
        theParentMap.put(pChild.getId(), pParent);
    }

    /**
     * DeRegister Child.
     * @param pChild the child node
     */
    protected void deRegisterChild(final TethysNode<N> pChild) {
        theParentMap.remove(pChild.getId());
    }

    /**
     * Obtain a new label.
     * @return the new label
     */
    public abstract TethysLabel<N, I> newLabel();

    /**
     * Obtain a new label.
     * @param pText the label text
     * @return the new label
     */
    public abstract TethysLabel<N, I> newLabel(final String pText);

    /**
     * Obtain a check box.
     * @return the new check box
     */
    public abstract TethysCheckBox<N, I> newCheckBox();

    /**
     * Obtain a check box.
     * @param pText the checkBox text
     * @return the new check box
     */
    public abstract TethysCheckBox<N, I> newCheckBox(final String pText);

    /**
     * Obtain a new button.
     * @return the new button
     */
    public abstract TethysButton<N, I> newButton();

    /**
     * Obtain a context menu.
     * @param <T> the item type
     * @return the new menu
     */
    public abstract <T> TethysScrollMenu<T, I> newContextMenu();

    /**
     * Obtain a new date button manager.
     * @return the new manager
     */
    public abstract TethysDateButtonManager<N, I> newDateButton();

    /**
     * Obtain a new scroll button manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysScrollButtonManager<T, N, I> newScrollButton();

    /**
     * Obtain a new list button manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysListButtonManager<T, N, I> newListButton();

    /**
     * Obtain a new simple icon button manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysSimpleIconButtonManager<T, N, I> newSimpleIconButton();

    /**
     * Obtain a new state icon button manager.
     * @param <T> the item type
     * @param <S> the state type
     * @return the new manager
     */
    public abstract <T, S> TethysStateIconButtonManager<T, S, N, I> newStateIconButton();

    /**
     * Obtain a new dateRange selector.
     * @return the new selector
     */
    public abstract TethysDateRangeSelector<N, I> newDateRangeSelector();

    /**
     * Obtain a new dateRange selector.
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     * @return the new selector
     */
    public abstract TethysDateRangeSelector<N, I> newDateRangeSelector(final boolean pBaseIsStart);

    /**
     * Obtain a new HTML manager.
     * @return the new manager
     */
    public abstract TethysHTMLManager<N, I> newHTMLManager();

    /**
     * Obtain a new Tree manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysTreeManager<T, N> newTreeManager();

    /**
     * Obtain a new splitTree manager.
     * @param <T> the item type
     * @return the new manager
     */
    public abstract <T> TethysSplitTreeManager<T, N, I> newSplitTreeManager();

    /**
     * Obtain a new fileSelector.
     * @return the new selector
     */
    public abstract TethysFileSelector newFileSelector();

    /**
     * Obtain a new directorySelector.
     * @return the new selector
     */
    public abstract TethysDirectorySelector newDirectorySelector();

    /**
     * Obtain a new progressBar.
     * @return the new progressBar
     */
    public abstract TethysProgressBar<N, I> newProgressBar();

    /**
     * Obtain a new slider.
     * @return the new slider
     */
    public abstract TethysSlider<N, I> newSlider();

    /**
     * Obtain a new tabPane manager.
     * @return the new manager
     */
    public abstract TethysTabPaneManager<N, I> newTabPane();

    /**
     * Obtain a new cardPane manager.
     * @param <P> the card panel type
     * @return the new manager
     */
    public abstract <P extends TethysNode<N>> TethysCardPaneManager<N, I, P> newCardPane();

    /**
     * Obtain a new borderPane manager.
     * @return the new manager
     */
    public abstract TethysBorderPaneManager<N, I> newBorderPane();

    /**
     * Obtain a new flowPane manager.
     * @return the new manager
     */
    public abstract TethysFlowPaneManager<N, I> newFlowPane();

    /**
     * Obtain a new vertical boxPane manager.
     * @return the new manager
     */
    public abstract TethysBoxPaneManager<N, I> newVBoxPane();

    /**
     * Obtain a new horizontal boxPane manager.
     * @return the new manager
     */
    public abstract TethysBoxPaneManager<N, I> newHBoxPane();

    /**
     * Obtain a new gridPane manager.
     * @return the new manager
     */
    public abstract TethysGridPaneManager<N, I> newGridPane();

    /**
     * Obtain a new string data field.
     * @return the new field
     */
    public abstract TethysDataEditField<String, N, I> newStringField();

    /**
     * Obtain a new short data field.
     * @return the new field
     */
    public abstract TethysDataEditField<Short, N, I> newShortField();

    /**
     * Obtain a new string data field.
     * @return the new field
     */
    public abstract TethysDataEditField<Integer, N, I> newIntegerField();

    /**
     * Obtain a new string data field.
     * @return the new field
     */
    public abstract TethysDataEditField<Long, N, I> newLongField();

    /**
     * Obtain a new money data field.
     * @return the new field
     */
    public abstract TethysDataEditField<TethysMoney, N, I> newMoneyField();

    /**
     * Obtain a new price data field.
     * @return the new field
     */
    public abstract TethysDataEditField<TethysPrice, N, I> newPriceField();

    /**
     * Obtain a new dilutedPrice data field.
     * @return the new field
     */
    public abstract TethysDataEditField<TethysDilutedPrice, N, I> newDilutedPriceField();

    /**
     * Obtain a new rate data field.
     * @return the new field
     */
    public abstract TethysDataEditField<TethysRate, N, I> newRateField();

    /**
     * Obtain a new units data field.
     * @return the new field
     */
    public abstract TethysDataEditField<TethysUnits, N, I> newUnitsField();

    /**
     * Obtain a new dilution data field.
     * @return the new field
     */
    public abstract TethysDataEditField<TethysDilution, N, I> newDilutionField();

    /**
     * Obtain a new ratio data field.
     * @return the new field
     */
    public abstract TethysDataEditField<TethysRatio, N, I> newRatioField();

    /**
     * Obtain a new date data field.
     * @return the new field
     */
    public abstract TethysDataEditField<TethysDate, N, I> newDateField();

    /**
     * Obtain a new scroll data field.
     * @param <T> the item type
     * @return the new field
     */
    public abstract <T> TethysDataEditField<T, N, I> newScrollField();

    /**
     * Obtain a new list data field.
     * @param <T> the item type
     * @return the new field
     */
    public abstract <T> TethysDataEditField<TethysItemList<T>, N, I> newListField();

    /**
     * Obtain a new simple icon data field.
     * @param <T> the item type
     * @return the new field
     */
    public abstract <T> TethysDataEditField<T, N, I> newSimpleIconField();

    /**
     * Obtain a new state icon data field.
     * @param <T> the item type
     * @param <S> the state type
     * @return the new field
     */
    public abstract <T, S> TethysDataEditField<T, N, I> newStateIconField();

    /**
     * Obtain a new table.
     * @param <C> the column id type
     * @param <R> the row type
     * @return the new field
     */
    public abstract <C, R> TethysTableManager<C, R, N, I> newTable();
}
