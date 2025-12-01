/*
 * Copyright 2002-2012 the original author or authors.
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

import org.springframework.lang.Nullable;

/**
 * 可作为层次结构一部分的 BeanFactory 会实现的子接口，
 * 提供对父级 BeanFactory 的访问能力。
 * <p>对于允许以可配置方式设置父级的 BeanFactory，其对应的
 * {@code setParentBeanFactory} 方法可以在
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * 接口中找到。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 07.07.2003
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
 */
public interface HierarchicalBeanFactory extends BeanFactory {

	/**
	 * 返回父级 BeanFactory；如果不存在则返回 {@code null}。
	 */
	@Nullable
	BeanFactory getParentBeanFactory();

	/**
	 * 返回本地 BeanFactory 是否包含给定名称的 Bean，忽略祖先上下文中定义的 Bean。
	 * <p>这是 {@link BeanFactory#containsBean} 的一种变体，会忽略祖先 BeanFactory
	 * 中具有同名的 Bean。
	 * @param name 要查询的 Bean 名称
	 * @return 是否在当前本地工厂中定义了具有给定名称的 Bean
	 * @see BeanFactory#containsBean
	 */
	boolean containsLocalBean(String name);

}