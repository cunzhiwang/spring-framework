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
import org.springframework.lang.Nullable;

/**
 * 一个工厂级钩子接口，用于对新创建的 Bean 实例进行自定义修改，
 * 例如检查标记接口或使用代理对 Bean 进行包装。
 *
 * <p>通常，负责通过标记接口等方式为 Bean 填充属性或进行装配的后置处理器
 * 会实现 {@link #postProcessBeforeInitialization}；
 * 而负责使用代理包装 Bean 的后置处理器，一般会实现
 * {@link #postProcessAfterInitialization}。
 *
 * <h3>注册</h3>
 * <p>{@code ApplicationContext} 可以在其 Bean 定义中自动检测到
 * {@code BeanPostProcessor} Bean，并将这些后置处理器应用到随后创建的
 * 所有 Bean 上。一个普通的 {@code BeanFactory} 则允许通过编程方式
 * 注册后置处理器，从而将其应用到通过该 BeanFactory 创建的所有 Bean 上。
 *
 * <h3>顺序</h3>
 * <p>在 {@code ApplicationContext} 中自动检测到的
 * {@code BeanPostProcessor} Bean，将会根据
 * {@link org.springframework.core.PriorityOrdered} 和
 * {@link org.springframework.core.Ordered} 的语义进行排序。
 * 相比之下，以编程方式注册到 {@code BeanFactory} 的
 * {@code BeanPostProcessor} Bean 则会按照注册顺序被应用；
 * 对于这类编程式注册的后置处理器，即便通过实现
 * {@code PriorityOrdered} 或 {@code Ordered} 接口显式声明了顺序语义，
 * 也会被忽略。此外，{@link org.springframework.core.annotation.Order @Order}
 * 注解对 {@code BeanPostProcessor} Bean 同样不起作用。
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * 在任何 Bean 初始化回调（例如 InitializingBean 的
	 * {@code afterPropertiesSet} 方法或自定义 init-method）<i>之前</i>，
	 * 将此 {@code BeanPostProcessor} 应用于给定的新 Bean 实例。
	 * 此时 Bean 已经完成属性填充。返回的 Bean 实例可以是原始 Bean
	 * 本身，也可以是对原始 Bean 的包装。
	 * <p>默认实现会直接返回传入的 {@code bean}。
	 * @param bean 新创建的 Bean 实例
	 * @param beanName Bean 的名称
	 * @return 实际要使用的 Bean 实例，可以是原始实例或其包装；
	 * 如果返回 {@code null}，则后续的 BeanPostProcessor 将不会被调用
	 * @throws org.springframework.beans.BeansException 发生错误时抛出
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * 在任何 Bean 初始化回调（例如 InitializingBean 的
	 * {@code afterPropertiesSet} 方法或自定义 init-method）<i>之后</i>，
	 * 将此 {@code BeanPostProcessor} 应用于给定的新 Bean 实例。
	 * 此时 Bean 已经完成属性填充。返回的 Bean 实例可以是原始 Bean
	 * 本身，也可以是对原始 Bean 的包装。
	 * <p>对于 {@code FactoryBean}，从 Spring 2.0 开始，该回调会同时应用于
	 * FactoryBean 实例本身以及由该 FactoryBean 创建的对象。后置处理器
	 * 可以通过 {@code bean instanceof FactoryBean} 之类的检查，决定是
	 * 仅应用于 FactoryBean、仅应用于其创建的对象，还是两者都应用。
	 * <p>与其他 {@code BeanPostProcessor} 回调不同的是，即便某个
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation}
	 * 方法触发了“短路”实例化流程，本回调仍然会被调用。
	 * <p>默认实现会直接返回传入的 {@code bean}。
	 * @param bean 新创建的 Bean 实例
	 * @param beanName Bean 的名称
	 * @return 实际要使用的 Bean 实例，可以是原始实例或其包装；
	 * 如果返回 {@code null}，则后续的 BeanPostProcessor 将不会被调用
	 * @throws org.springframework.beans.BeansException 发生错误时抛出
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
