/*******************************************************************************
 * JDecimal: Java Small Decimal implementation
 * Copyright 2012 Tony Washer
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
/**
 * JDecimal is a package that provides a Decimal implementation in many ways similar to that provided by BigDecimal, except that
 * the class is mutable, allowing for much faster arithmetic.
 * <p>Rather than deal with arbitrarily large numbers, JDecimal store the value as a simple long, and so is not suitable for
 * extremely large values, since precision will be lost. Users should continue to use BigDecimal for these situations
 * <p>This implementation is particularly suited to financial applications since a monetary value of $10,000,000,000,000,000.00 can
 * easily be accommodated within a long. Hopefully this will be sufficient for most peoples purposes.
 */
package net.sourceforge.JDecimal;

