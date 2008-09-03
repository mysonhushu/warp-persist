/**
 * Copyright (C) 2008 Wideplay Interactive.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wideplay.warp.persist;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Value object that indicates how a persistence service should be configured.
 * To create an instance, obtain a builder by calling {@link #builder()}, set
 * all the properties and call {@link PersistenceConfigurationImpl.PersistenceConfigurationBuilder#build()}.
 * 
 * @author Robbie Vanbrabant
 */
@ThreadSafe
@Immutable
public class PersistenceConfigurationImpl implements PersistenceConfiguration {
    private final UnitOfWork unitOfWork;
    private final TransactionStrategy txStrategy;
    private final Matcher<? super Class<?>> txClassMatcher;
    private final Matcher<? super Method> txMethodMatcher;
    private final Set<Class<?>> accessors;

    private PersistenceConfigurationImpl(PersistenceConfigurationBuilder builder) {
        this.unitOfWork = builder.unitOfWork;
        this.txStrategy = builder.txStrategy;
        this.txClassMatcher = builder.txClassMatcher;
        this.txMethodMatcher = builder.txMethodMatcher;
        this.accessors = Collections.unmodifiableSet(builder.accessors);
    }

    public UnitOfWork getUnitOfWork() {
        return this.unitOfWork;
    }
    public TransactionStrategy getTransactionStrategy() {
        return this.txStrategy;
    }
    public Matcher<? super Method> getTransactionMethodMatcher() {
        return this.txMethodMatcher;
    }
    public Matcher<? super Class<?>> getTransactionClassMatcher() {
        return this.txClassMatcher;
    }
    public Set<Class<?>> getAccessors() {
        return this.accessors;
    }
    
    public static PersistenceConfigurationBuilder builder() {
        return new PersistenceConfigurationBuilder();
    }

    static class PersistenceConfigurationBuilder {
        // default values
        private UnitOfWork unitOfWork = UnitOfWork.TRANSACTION;
        private TransactionStrategy txStrategy = TransactionStrategy.LOCAL;
        private Matcher<? super Class<?>> txClassMatcher = Matchers.any();
        private Matcher<? super Method> txMethodMatcher = Matchers.annotatedWith(Transactional.class);

        private final Set<Class<?>> accessors = new LinkedHashSet<Class<?>>();

        public PersistenceConfigurationBuilder unitOfWork(UnitOfWork unitOfWork) {
            this.unitOfWork = unitOfWork;
            return this;
        }
        public PersistenceConfigurationBuilder transactionStrategy(TransactionStrategy strategy) {
            this.txStrategy = strategy;
            return this;
        }
        public PersistenceConfigurationBuilder transactionClassMatcher(Matcher<? super Class<?>> matcher) {
            this.txClassMatcher = matcher;
            return this;
        }
        public PersistenceConfigurationBuilder transactionMethodMatcher(Matcher<? super Method> matcher) {
            this.txMethodMatcher = matcher;
            return this;
        }
        public PersistenceConfigurationBuilder accessor(Class<?> accessor) {
            accessors.add(accessor);
            return this;
        }

        public PersistenceConfiguration build() {
            // TODO validate state
            return new PersistenceConfigurationImpl(this);
        }
    }

}
