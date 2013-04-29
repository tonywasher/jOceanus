/*******************************************************************************
 * jDateDay: Java Date Day
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jDateDay;

import net.sourceforge.JDateButton.JDateCellRenderer;

/**
 * Cell renderer for a {@link JDateDay} object extending {@link JDateCellRenderer}.
 * @author Tony Washer
 */
public class JDateDayCellRenderer extends JDateCellRenderer {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 4958174461345009403L;

    /**
     * The Date Configuration.
     */
    private final transient JDateDayConfig theConfig;

    /**
     * Constructor.
     */
    public JDateDayCellRenderer() {
        /* Create a new configuration */
        this(new JDateDayFormatter());
    }

    /**
     * Constructor.
     * @param pFormatter the formatter
     */
    public JDateDayCellRenderer(final JDateDayFormatter pFormatter) {
        /* Create a new configuration */
        super(new JDateDayConfig(pFormatter));
        theConfig = (JDateDayConfig) super.getDateConfig();
    }

    @Override
    public JDateDayConfig getDateConfig() {
        return theConfig;
    }

    /**
     * Set value for the renderer. This will convert a Date into the required string format before passing it
     * on. If the object is already a string or is null it is passed directly on.
     * @param value the value to display (String, Calendar or null)
     */
    @Override
    public void setValue(final Object value) {
        Object o = value;

        /* Handle DateDay values */
        if (value instanceof JDateDay) {
            JDateDay d = (JDateDay) value;
            o = d.getCalendar();
        }

        /* Pass the value on */
        super.setValue(o);
    }
}
