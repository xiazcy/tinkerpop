/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.util;

public class StringStepHelper {

    /**
     * Concatenates two strings.
     *
     * <pre>
     *     a = null, b = null -> null
     *     a = null, b = "str2" -> "str2"
     *     a = "str1", b = null -> "str1"
     *     a = "str1", b = "str2" -> "str1str2"
     *
     * </pre>
     *
     */
    public static String concat(final String a, final String b) {
        if (null == a || null == b)
            return null == a ? b : a;

        return a.concat(b);
    }

}
