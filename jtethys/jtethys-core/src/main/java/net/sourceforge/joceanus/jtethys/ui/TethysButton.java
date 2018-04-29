/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;

/**
 * Tethys Button.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class TethysButton<N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * Default icon width.
     */
    protected static final int DEFAULT_ICONWIDTH = TethysIconBuilder.DEFAULT_ICONWIDTH;
    
    /**
     * Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The icon Width.
     */
    private int theWidth;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysButton(final TethysGuiFactory<N, I> pFactory) {
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();
        theWidth = DEFAULT_ICONWIDTH;
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain width.
     * @return the width
     */
    public int getIconWidth() {
        return theWidth;
    }

    /**
     * Set the width.
     * @param pWidth the width to set
     */
    public void setIconWidth(final int pWidth) {
        /* Store the width */
        theWidth = pWidth;
    }

    /**
     * Set text for button.
     * @param pText the text
     */
    public abstract void setText(String pText);

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    protected Integer getBorderPadding() {
        return thePadding;
    }

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    protected String getBorderTitle() {
        return theTitle;
    }

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    public void setBorderPadding(final Integer pPadding) {
        thePadding = pPadding;
    }

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    public void setBorderTitle(final String pTitle) {
        theTitle = pTitle;
    }

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(Integer pHeight);

    /**
     * Set icon for button.
     * @param <K> the keyId type
     * @param pId the icon Id
     */
    public abstract <K extends Enum<K> & TethysIconId> void setIcon(K pId);

    /**
     * Set icon for button.
     * @param pIcon the icon
     */
    public abstract void setIcon(TethysArrowIconId pIcon);

    /**
     * Set icon for button.
     * @param pIcon the icon
     */
    public abstract void setIcon(I pIcon);

    /**
     * Set toolTip for button.
     * @param pTip the toolTip
     */
    public abstract void setToolTip(String pTip);

    /**
     * Set Null Margins.
     */
    public abstract void setNullMargins();

    /**
     * Set Icon only.
     */
    public abstract void setIconOnly();

    /**
     * Set Text And Icon.
     */
    public abstract void setTextAndIcon();

    /**
     * Set Text Only.
     */
    public abstract void setTextOnly();

    /**
     * handle pressed.
     */
    protected void handlePressed() {
        theEventManager.fireEvent(TethysUIEvent.PRESSED);
    }
}
