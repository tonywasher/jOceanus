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
package net.sourceforge.joceanus.jtethys.date.swing;

import net.sourceforge.jdatebutton.swing.JDateCellRenderer;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableCellFactory.TethysSwingTableDateCell;

/**
 * Cell renderer for a {@link TethysDate} object extending {@link JDateCellRenderer}.
 * @author Tony Washer
 * @deprecated as of 1.5.0 use {@link TethysSwingTableDateCell}
 */
@Deprecated
public class TethysSwingDateCellRenderer
        extends JDateCellRenderer {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 4958174461345009403L;

    /**
     * The Date Configuration.
     */
    private final transient TethysSwingDateConfig theConfig;

    /**
     * Constructor.
     */
    public TethysSwingDateCellRenderer() {
        /* Create a new configuration */
        this(new TethysDateFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public TethysSwingDateCellRenderer(final TethysDateFormatter pFormatter) {
        /* Create a new configuration */
        super(new TethysSwingDateConfig(pFormatter));
        theConfig = (TethysSwingDateConfig) super.getDateConfig();
    }

    @Override
    public TethysSwingDateConfig getDateConfig() {
        return theConfig;
    }

    /**
     * Set value for the renderer. This will convert a Date into the required string format before
     * passing it on. If the object is already a string or is null it is passed directly on.
     * @param value the value to display (String, Calendar or null)
     */
    @Override
    public void setValue(final Object value) {
        Object o = value;

        /* Handle DateDay values */
        if (value instanceof TethysDate) {
            TethysDate d = (TethysDate) value;
            o = d.getDate();
        }

        /* Pass the value on */
        super.setValue(o);
    }
}
