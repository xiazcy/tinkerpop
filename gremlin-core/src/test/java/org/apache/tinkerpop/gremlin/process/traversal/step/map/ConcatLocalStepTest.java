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
        assertEquals("whatisthis", __.__("what").concat(Scope.local, __.__("is", "this")).next());
        assertEquals("thisisatest", __.__("this").concat(Scope.local, "is", "a", "test").next());
        assertEquals("thisisatest", __.__(new ArrayList<>(Arrays.asList("this", "is", "a", "test"))).concat(Scope.local).next());
        // Each traverser in inject() step is concatenated with the arguments to concat individual in local scope
        assertArrayEquals(new String[]{"whatatest", "isatest", "this?atest"},
                __.__("what", "is", new ArrayList<>(Arrays.asList("this","?"))).concat(Scope.local, "a", "test").toList().toArray());
        // a check for non-string elements
    }
}
