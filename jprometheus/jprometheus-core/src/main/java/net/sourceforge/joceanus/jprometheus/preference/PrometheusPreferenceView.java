/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.preference;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.ui.MetisPreferenceSetView;
import net.sourceforge.joceanus.jmetis.ui.MetisPreferenceView;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;

/**
 * Panel for editing preference Sets.
 */
public class PrometheusPreferenceView
    extends MetisPreferenceView {
    /**
     * Constructor.
     *
     * @param pFactory       the GUI factory
     * @param pPreferenceMgr the preference manager
     */
    public PrometheusPreferenceView(final TethysUIFactory<?> pFactory,
                                    final PrometheusPreferenceManager pPreferenceMgr) {
        super(pFactory, pPreferenceMgr);
    }

    @Override
    protected MetisPreferenceSetView createView(final TethysUIFactory<?> pFactory,
                                                final MetisPreferenceSet pSet) {
        /* Create the underlying view */
        return (pSet instanceof PrometheusPreferenceSet)
            ? new PrometheusPreferenceSetView(pFactory, (PrometheusPreferenceSet) pSet)
            : super.createView(pFactory, pSet);
    }
}