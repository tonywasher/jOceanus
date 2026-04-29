/*
 * Themis: Java Project Framework
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

package io.github.tonywasher.joceanus.themis.gui.base;

/**
 * HTML Tags.
 */
public enum ThemisUIHTMLTag {
    /**
     * Html.
     */
    HTML("html"),

    /**
     * Head.
     */
    HEAD("head"),

    /**
     * Style.
     */
    STYLE("style"),

    /**
     * Body.
     */
    BODY("body"),

    /**
     * PreFormatted.
     */
    PRE("pre"),

    /**
     * A.
     */
    A("a"),

    /**
     * H1.
     */
    H1("h1"),

    /**
     * H2.
     */
    H2("h2"),

    /**
     * H3.
     */
    H3("h3"),

    /**
     * H4.
     */
    H4("h4"),

    /**
     * H5.
     */
    H5("h5"),

    /**
     * HR.
     */
    HR("hr"),

    /**
     * Table.
     */
    TABLE("table"),

    /**
     * THead.
     */
    THEAD("thead"),

    /**
     * TBody.
     */
    TBODY("tbody"),

    /**
     * Table Header.
     */
    TH("th"),

    /**
     * Table Row.
     */
    TR("tr"),

    /**
     * Table Cell.
     */
    TD("td"),

    /**
     * Div.
     */
    DIV("div"),

    /**
     * Span.
     */
    SPAN("span"),

    /**
     * Break.
     */
    BR("br");

    /**
     * Tag text.
     */
    private final String theTag;

    /**
     * Constructor.
     *
     * @param pTag the tag text
     */
    ThemisUIHTMLTag(final String pTag) {
        theTag = pTag;
    }

    /**
     * Obtain the tag.
     *
     * @return the tag
     */
    public String getTag() {
        return theTag;
    }

    /**
     * Obtain the tag.
     *
     * @return the tag
     */
    @Override
    public String toString() {
        return getTag();
    }
}
