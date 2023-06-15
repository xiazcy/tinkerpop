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

import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ReducingBarrierStep;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.TraverserRequirement;
import org.apache.tinkerpop.gremlin.util.NumberHelper;
import org.apache.tinkerpop.gremlin.util.StringStepHelper;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BinaryOperator;

public class ConcatGlobalStep<S extends String, E extends String> extends ReducingBarrierStep<S, E> {

    protected String[] concatStrings;
    private Traversal.Admin<S, E> concatTraversal;

    private static final Set<TraverserRequirement> REQUIREMENTS = EnumSet.of(TraverserRequirement.OBJECT);
    public ConcatGlobalStep(Traversal.Admin traversal, final String... concatStrings) {
        super(traversal);
        this.concatStrings = concatStrings;
        this.setReducingBiOperator((BinaryOperator) Operator.concat);
    }

    public ConcatGlobalStep(Traversal.Admin traversal, final Traversal<S, E> concatTraversal) {
        super(traversal);
        this.concatTraversal = concatTraversal.asAdmin();
        this.setReducingBiOperator((BinaryOperator) Operator.concat);
    }

    @Override
    public E projectTraverser(Traverser.Admin<S> traverser) {
        final Iterator<E> iterator = IteratorUtils.asIterator(traverser.get());
        String result = null;
        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                result = StringStepHelper.concat(result, iterator.next());
            }
        }
        return null == traverser.get() ? null : (E) result;
    }

    @Override
    public E generateFinalResult(final E concatenatedString) {
        String result = concatenatedString;
        while (null != this.concatTraversal && this.concatTraversal.hasNext()) {
            result = StringStepHelper.concat(result, this.concatTraversal.next());
        }
        if (null != this.concatStrings) {
            for (final String s : this.concatStrings) {
                result = StringStepHelper.concat(result, s);
            }
        }
        return (E) result;
    }

    private E untilNonNull(final Iterator<E> itty) {
        E result = null;
        while (itty.hasNext() && null == result) {
            result = itty.next();
        }
        return result;
    }

    @Override
    public Set<TraverserRequirement> getRequirements() {
        return REQUIREMENTS;
    }


}
