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

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.TraverserRequirement;
import org.apache.tinkerpop.gremlin.process.traversal.util.FastNoSuchElementException;
import org.apache.tinkerpop.gremlin.util.StringStepHelper;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation for the {@code concat()} step
 */
public class ConcatLocalStep<E extends String , S extends Iterable<E>> extends ScalarMapStep<S, String> {

    protected String[] concatStrings;
    private Traversal.Admin<S , E> concatTraversal;

    /**
     * String data types or array, if local scope is used. If a non-string traverser,
     * or the list containing non-string values, is passed in then an IllegalArgumentException will be thrown
    */
    public ConcatLocalStep(Traversal.Admin traversal, final String... concatStrings) {
        super(traversal);
        // Needs some validation here
        this.concatStrings = concatStrings;
    }

    public ConcatLocalStep(Traversal.Admin traversal, final Traversal<S, E> concatTraversal) {
        super(traversal);
        this.concatTraversal = concatTraversal.asAdmin();
    }

    @Override
    protected E map(final Traverser.Admin<S> traverser) {
        final Iterator<E> iterator = IteratorUtils.asIterator(traverser.get());
        if (iterator.hasNext()) {
            // forward the iterator to the first non-null or return null
            String result = untilNonNull(iterator);
            while (iterator.hasNext()) {
                result = StringStepHelper.concat(result, iterator.next());
            }
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
        throw FastNoSuchElementException.instance();
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
        return Collections.singleton(TraverserRequirement.OBJECT);
    }
}
