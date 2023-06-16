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

import org.apache.tinkerpop.gremlin.process.traversal.Scope;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.StepTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ConcatLocalStepTest extends StepTest {

    @Override
    protected List<Traversal> getTraversals() {
        return Collections.singletonList(__.concat(Scope.local, "test"));
    }

    @Test
    public void testReturnTypes() {
        assertEquals("abc", __.__("a").concat(Scope.local, __.__("b", "c")).next());
        assertEquals("abcd", __.__("a").concat(Scope.local, "b", "c", "d").next());
        assertEquals("abcd", __.__(Arrays.asList("a", "b", "c", "d")).concat(Scope.local).next());
        assertEquals("bd", __.__(Arrays.asList(null, "b", null, "d")).concat(Scope.local).next());
        // Each traverser in inject() step is concatenated with the arguments to concat individual in local scope
        assertArrayEquals(new String[]{"aef", "bef", "cdef"},
                __.__("a", "b", Arrays.asList("c","d")).concat(Scope.local, "e", "f").toList().toArray());

        // Traverser results from the argument are concatenated
        assertArrayEquals(new String[]{"aef", "bef", "cdef"},
                __.__("a", "b", Arrays.asList("c","d")).concat(Scope.local, __.__("e","f")).toList().toArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWithIncomingInteger() {
        __.__(1).concat(Scope.local, "a", "b").next();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWithIncomingIntegerInArray() {
        __.__(Arrays.asList("a", 1)).concat(Scope.local, "b", "c").next();
    }

}
