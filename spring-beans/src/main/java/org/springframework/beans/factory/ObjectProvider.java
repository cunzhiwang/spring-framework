/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * {@link ObjectFactory} 的一个变体，专门为依赖注入点设计，
 * 允许以编程方式处理可选依赖，并对非唯一候选情况进行宽松处理。
 *
 * <p>从 5.1 开始，该接口扩展了 {@link Iterable} 并提供了 {@link Stream} 支持。
 * 因此可以在 {@code for} 循环中使用，提供 {@link #forEach} 迭代能力，
 * 并允许通过 {@link #stream} 以类似集合的方式进行访问。
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @param <T> 对象类型
 * @see BeanFactory#getBeanProvider
 * @see org.springframework.beans.factory.annotation.Autowired
 */
public interface ObjectProvider<T> extends ObjectFactory<T>, Iterable<T> {

	/**
	 * 返回由该工厂管理的此对象的一个实例（可能是共享的，也可能是独立的）。
	 * <p>允许指定显式的构造参数，类似于
	 * {@link BeanFactory#getBean(String, Object...)}。
	 * @param args 创建对应实例时要使用的参数
	 * @return 该 Bean 的一个实例
	 * @throws BeansException 如果在创建过程中发生错误
	 * @see #getObject()
	 */
	T getObject(Object... args) throws BeansException;

	/**
	 * 返回由该工厂管理的此对象的一个实例（可能是共享的，也可能是独立的）。
	 * @return Bean 的一个实例；如果不可用则返回 {@code null}
	 * @throws BeansException 如果在创建过程中发生错误
	 * @see #getObject()
	 */
	@Nullable
	T getIfAvailable() throws BeansException;

	/**
	 * 返回由该工厂管理的此对象的一个实例（可能是共享的，也可能是独立的）。
	 * @param defaultSupplier 当工厂中不存在任何此类 Bean 时，用于提供默认对象的回调
	 * @return Bean 的一个实例；如果没有此类 Bean，则返回提供的默认对象
	 * @throws BeansException 如果在创建过程中发生错误
	 * @since 5.0
	 * @see #getIfAvailable()
	 */
	default T getIfAvailable(Supplier<T> defaultSupplier) throws BeansException {
		T dependency = getIfAvailable();
		return (dependency != null ? dependency : defaultSupplier.get());
	}

	/**
	 * 如果可用，则消费由该工厂管理的此对象的一个实例（可能是共享的，也可能是独立的）。
	 * @param dependencyConsumer 用于处理目标对象的回调（仅在可用时才会调用）
	 * @throws BeansException 如果在创建过程中发生错误
	 * @since 5.0
	 * @see #getIfAvailable()
	 */
	default void ifAvailable(Consumer<T> dependencyConsumer) throws BeansException {
		T dependency = getIfAvailable();
		if (dependency != null) {
			dependencyConsumer.accept(dependency);
		}
	}

	/**
	 * 返回由该工厂管理的此对象的一个实例（可能是共享的，也可能是独立的）。
	 * @return Bean 的一个实例；如果不可用或不唯一则返回 {@code null}
	 *（例如找到多个候选者且没有任何一个标记为 primary）
	 * @throws BeansException 如果在创建过程中发生错误
	 * @see #getObject()
	 */
	@Nullable
	T getIfUnique() throws BeansException;

	/**
	 * 返回由该工厂管理的此对象的一个实例（可能是共享的，也可能是独立的）。
	 * @param defaultSupplier 当工厂中不存在唯一候选者时，用于提供默认对象的回调
	 * @return Bean 的一个实例；如果没有此类 Bean，或在工厂中不唯一，
	 * 则返回提供的默认对象（例如找到多个候选者且没有任何一个标记为 primary）
	 * @throws BeansException 如果在创建过程中发生错误
	 * @since 5.0
	 * @see #getIfUnique()
	 */
	default T getIfUnique(Supplier<T> defaultSupplier) throws BeansException {
		T dependency = getIfUnique();
		return (dependency != null ? dependency : defaultSupplier.get());
	}

	/**
	 * 如果存在唯一候选者，则消费由该工厂管理的此对象的一个实例
	 *（可能是共享的，也可能是独立的）。
	 * @param dependencyConsumer 用于处理目标对象的回调（仅在唯一时才会调用）
	 * @throws BeansException 如果在创建过程中发生错误
	 * @since 5.0
	 * @see #getIfAvailable()
	 */
	default void ifUnique(Consumer<T> dependencyConsumer) throws BeansException {
		T dependency = getIfUnique();
		if (dependency != null) {
			dependencyConsumer.accept(dependency);
		}
	}

	/**
	 * 返回一个 {@link Iterator}，用于遍历所有匹配的对象实例，
	 * 不保证特定的顺序（但通常为注册顺序）。
	 * @since 5.1
	 * @see #stream()
	 */
	@Override
	default Iterator<T> iterator() {
		return stream().iterator();
	}

	/**
	 * 返回一个顺序的 {@link Stream}，用于遍历所有匹配的对象实例，
	 * 不保证特定的顺序（但通常为注册顺序）。
	 * @since 5.1
	 * @see #iterator()
	 * @see #orderedStream()
	 */
	default Stream<T> stream() {
		throw new UnsupportedOperationException("Multi element access not supported");
	}

	/**
	 * 返回一个顺序的 {@link Stream}，用于遍历所有匹配的对象实例，
	 * 并按照工厂的通用顺序比较器预先排序。
	 * <p>在标准的 Spring 应用上下文中，这将按照
	 * {@link org.springframework.core.Ordered} 约定进行排序，
	 * 并且在基于注解的配置场景下，还会考虑
	 * {@link org.springframework.core.annotation.Order} 注解，
	 * 其行为类似于对列表/数组类型的多元素注入点的处理方式。
	 * @since 5.1
	 * @see #stream()
	 * @see org.springframework.core.OrderComparator
	 */
	default Stream<T> orderedStream() {
		throw new UnsupportedOperationException("Ordered element access not supported");
	}

}
