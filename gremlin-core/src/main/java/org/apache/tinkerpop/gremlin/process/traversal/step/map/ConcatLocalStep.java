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
import org.apache.tinkerpop.gremlin.process.traversal.step.TraversalParent;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.TraverserRequirement;
import org.apache.tinkerpop.gremlin.process.traversal.util.FastNoSuchElementException;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation for the {@code concat()} step
 */
public class ConcatLocalStep<S, E> extends ScalarMapStep<S, String> implements TraversalParent {

    private String[] concatStrings;
//    private Traversal.Admin<S , E> concatTraversal;
    private String traversalResult;

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

        // currently we are concatenating all traversal result of child traversers into one string,
        // need to consider if we need return multiple results per child traverser:
        // e.g. should the results of g.inject("a", "b").concat(_.inject("c","d))) be
        // [acd, bcd] or [ac, ad, bc, bd], currently it's the former. Not entirely sure how the latter will work as
        // we will need loop the map function base on the number of traversers from the argument
        this.traversalResult = processTraversal(concatTraversal.asAdmin());
    }

    @Override
    protected String map(final Traverser.Admin<S> traverser) {
        // all null values are skipped during appending
        final StringBuilder sb = new StringBuilder();
        final Iterator<E> iterator = IteratorUtils.asIterator(traverser.get());
        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                E result = iterator.next();
                if (null != result) {
                    // local scope throws when items in the array isn't a string
                    if (!(result instanceof String)) {
                        throw new IllegalArgumentException(
                                String.format("String concat() local scope can only take string as argument in arrays, encountered %s", result.getClass()));
                    }
                    sb.append(result);
                }
            }

            if (null != this.traversalResult) sb.append(this.traversalResult);

            if (null != this.concatStrings) {
                for (final String s : this.concatStrings) {
                    if (null != s) sb.append(s);
                }
            }
            return sb.toString();
        }
        throw FastNoSuchElementException.instance();
    }

    private String processTraversal(final Traversal.Admin<S , E> concatTraverser) {
        final StringBuilder sb = new StringBuilder();
        if (null != concatTraverser) {
            while (concatTraverser.hasNext()) {
                // TODO check result for type - should we allow list of strings in parameter for local scope?
                E result = concatTraverser.next();
                if (null != result) {
                    sb.append(result);
                }
            }
        }
        return sb.toString();
    }

    @Override
    public Set<TraverserRequirement> getRequirements() {
        return Collections.singleton(TraverserRequirement.OBJECT);
    }
}
