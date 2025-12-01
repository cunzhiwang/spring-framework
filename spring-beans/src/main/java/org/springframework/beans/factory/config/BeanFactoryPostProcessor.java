/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * 一个工厂级钩子接口，用于自定义修改应用上下文中的 Bean 定义，
 * 即调整底层 BeanFactory 中 Bean 的属性值。
 * <p>可以在 BeanFactory 标准初始化之后、任何 Bean 实例化之前，
 * 通过该钩子对 Bean 定义进行修改。
 *
 * <p>这在面向系统管理员的自定义配置文件场景中非常有用，
 * 这些配置文件可以覆盖应用上下文中原本配置好的 Bean 属性。
 * 针对这类配置需求，可以直接使用 {@link PropertyResourceConfigurer}
 * 及其具体实现类提供的开箱即用方案。
 *
 * <p>{@code BeanFactoryPostProcessor} 可以与 Bean 定义交互并进行修改，
 * 但永远不应操作 Bean 实例本身。否则可能导致 Bean 被过早实例化，
 * 破坏容器的生命周期管理并产生意料之外的副作用。
 * 如果需要与 Bean 实例交互，请考虑改为实现 {@link BeanPostProcessor}。
 *
 * <h3>注册方式</h3>
 * <p>{@code ApplicationContext} 会自动检测其配置中的
 * {@code BeanFactoryPostProcessor} Bean，并在任何其他 Bean 被创建之前
 * 先应用这些后置处理器。也可以通过编程方式，将
 * {@code BeanFactoryPostProcessor} 注册到 {@code ConfigurableApplicationContext} 中。
 *
 * <h3>顺序</h3>
 * <p>在 {@code ApplicationContext} 中自动检测到的
 * {@code BeanFactoryPostProcessor} Bean，会按照
 * {@link org.springframework.core.PriorityOrdered} 和
 * {@link org.springframework.core.Ordered} 的语义进行排序。
 * 相比之下，以编程方式注册到 {@code ConfigurableApplicationContext} 的
 * {@code BeanFactoryPostProcessor} 则会按照注册顺序被应用；
 * 对于这类编程式注册的后置处理器，即便实现了
 * {@code PriorityOrdered} 或 {@code Ordered} 接口中所表达的顺序语义，
 * 也不会被考虑。此外，{@link org.springframework.core.annotation.Order @Order}
 * 注解对 {@code BeanFactoryPostProcessor} Bean 也不起作用。
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 06.07.2003
 * @see BeanPostProcessor
 * @see PropertyResourceConfigurer
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 * 在应用上下文的内部 BeanFactory 完成标准初始化之后，对其进行修改。
	 * 此时所有 Bean 定义都已经加载完毕，但还没有任何 Bean 被实例化。
	 * 这使得即便是“急切初始化”的 Bean，其属性也仍然可以在此处被覆盖或新增。
	 * @param beanFactory 应用上下文所使用的 BeanFactory
	 * @throws org.springframework.beans.BeansException 发生错误时抛出
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
