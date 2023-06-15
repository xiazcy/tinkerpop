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
package org.apache.tinkerpop.gremlin.process.traversal.step.map;

import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.process.AbstractGremlinProcessTest;
import org.apache.tinkerpop.gremlin.process.GremlinProcessRunner;
import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.apache.tinkerpop.gremlin.LoadGraphWith.GraphData.MODERN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(GremlinProcessRunner.class)
public abstract class ConcatTest  extends AbstractGremlinProcessTest {

    public abstract Traversal<String, String> get_g_injectXnull_a_b_nullX_concat();

    public abstract Traversal<List<String>, String> get_g_injectXlistXnull_a_b_nullXX_concatXlocalX();

    @Test
    @LoadGraphWith(MODERN)
    public void g_injectXnull_a_b_nullX_concat() {
        final Traversal<String, String> traversal = get_g_injectXnull_a_b_nullX_concat();
        printTraversalForm(traversal);
        final String concatenated = traversal.next();
        assertEquals("ab", concatenated);
        assertFalse(traversal.hasNext());
    }

    @Test
    @LoadGraphWith(MODERN)
    public void g_injectXlistXnull_a_b_nullXX_concatXlocalX() {
        final Traversal<List<String>, String> traversal = get_g_injectXlistXnull_a_b_nullXX_concatXlocalX();
        printTraversalForm(traversal);
        final String concatenated = traversal.next();
        assertEquals("ab", concatenated);
        assertFalse(traversal.hasNext());
    }

    public static class Traversals extends ConcatTest {
        @Override
        public Traversal<String, String> get_g_injectXnull_a_b_nullX_concat() {
            return g.inject(null, "a", "b", null).concat();
        }

        @Override
        public Traversal<List<String>, String> get_g_injectXlistXnull_a_b_nullXX_concatXlocalX() {
            return g.inject(Arrays.asList(null, "a", "b", null)).concat(Scope.local);
        }
    }

}
