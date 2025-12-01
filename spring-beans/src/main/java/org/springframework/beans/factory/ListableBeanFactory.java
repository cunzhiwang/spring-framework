/*
 * Copyright 2002-2021 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 由可枚举其所有 Bean 实例的工厂实现，用于扩展 {@link BeanFactory} 接口。
 * <p>实现该接口的 Bean 工厂可以一次性枚举出所有 Bean，而不是像普通
 * {@code BeanFactory} 那样仅根据客户端请求按名称一个一个地查找。
 * 预先加载所有 Bean 定义的工厂实现（例如基于 XML 的工厂）通常都会实现本接口。
 *
 * <p>如果当前工厂同时也是一个 {@link HierarchicalBeanFactory}，那么本接口
 * 中方法的返回值<i>不会</i>考虑 BeanFactory 的层级结构，而只针对当前工厂中
 * 定义的 Bean。若需要将祖先工厂中的 Bean 一并考虑在内，可以使用
 * {@link BeanFactoryUtils} 辅助类。
 *
 * <p>本接口中的方法只会基于当前工厂的 Bean 定义进行操作。它们会忽略通过其他
 * 方式注册的单例 Bean，例如
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * 的 {@code registerSingleton} 方法注册的 Bean，只有
 * {@code getBeanNamesForType} 和 {@code getBeansOfType} 会去检查这类手动注册的
 * 单例。当然，通过 BeanFactory 的 {@code getBean} 依然可以透明地访问这些
 * 特殊 Bean。不过在典型场景中，所有 Bean 都是通过外部 Bean 定义来配置的，
 * 因此大多数应用无需特别区分这些情形。
 *
 * <p><b>注意：</b>除 {@code getBeanDefinitionCount} 和
 * {@code containsBeanDefinition} 之外，本接口中的方法都不适合被高频调用；
 * 其实现可能相对较慢。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * 检查该 Bean 工厂是否包含具有给定名称的 Bean 定义。
	 * <p>不会考虑该工厂可能参与的任何层级结构，并且会忽略通过
	 * 非 Bean 定义方式注册的单例 Bean。
	 * @param beanName 要查找的 Bean 名称
	 * @return 如果该 Bean 工厂包含具有给定名称的 Bean 定义则返回 {@code true}
	 * @see #containsBean
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * 返回该工厂中定义的 Bean 的数量。
	 * <p>不会考虑该工厂可能参与的任何层级结构，并且会忽略通过
	 * 非 Bean 定义方式注册的单例 Bean。
	 * @return 该工厂中定义的 Bean 数量
	 */
	int getBeanDefinitionCount();

	/**
	 * 返回此工厂中定义的所有 Bean 的名称。
	 * <p>不会考虑该工厂可能参与的任何层级结构，并且会忽略通过
	 * 非 Bean 定义方式注册的单例 Bean。
	 * @return 此工厂中定义的所有 Bean 名称；如果没有定义则返回空数组
	 */
	String[] getBeanDefinitionNames();

	/**
	 * 返回指定类型 Bean 的 provider（提供者），允许以惰性、按需的方式
	 * 获取实例，并支持可用性和唯一性等选项。
	 * @param requiredType Bean 必须匹配的类型；可以是接口或父类
	 * @param allowEagerInit 是否允许在基于流的访问过程中，为了进行类型检查而
	 * 初始化<i>懒加载单例</i>以及<i>由 FactoryBean 创建的对象</i>
	 *（或通过带有 "factory-bean" 引用的工厂方法创建的对象）
	 * @return 相应的 provider 句柄
	 * @since 5.3
	 * @see #getBeanProvider(ResolvableType, boolean)
	 * @see #getBeanProvider(Class)
	 * @see #getBeansOfType(Class, boolean, boolean)
	 * @see #getBeanNamesForType(Class, boolean, boolean)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType, boolean allowEagerInit);

	/**
	 * 返回指定类型 Bean 的 provider（提供者），允许以惰性、按需的方式
	 * 获取实例，并支持可用性和唯一性等选项。
	 * @param requiredType Bean 必须匹配的类型；可以是一个泛型类型声明。
	 * 需要注意的是，这里不支持集合类型，这一点不同于基于反射的依赖注入点。
	 * 如果需要以编程方式获取某个具体类型的 Bean 列表，可以在这里传入实际
	 * Bean 类型作为参数，随后通过 {@link ObjectProvider#orderedStream()} 或其
	 * 延迟流/迭代能力来访问。
	 * @param allowEagerInit 是否允许在基于流的访问过程中，为了进行类型检查而
	 * 初始化<i>懒加载单例</i>以及<i>由 FactoryBean 创建的对象</i>
	 *（或通过带有 "factory-bean" 引用的工厂方法创建的对象）
	 * @return 相应的 provider 句柄
	 * @since 5.3
	 * @see #getBeanProvider(ResolvableType)
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 * @see #getBeanNamesForType(ResolvableType, boolean, boolean)
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType, boolean allowEagerInit);

	/**
	 * 返回与给定类型（包括其子类）匹配的 Bean 名称数组，
	 * 判断依据为 Bean 定义，或者在 FactoryBean 情况下为其 {@code getObjectType} 的值。
	 * <p><b>注意：该方法只会内省顶层 Bean。</b>它<i>不会</i>检查嵌套 Bean，
	 * 即使这些嵌套 Bean 也可能匹配指定类型。
	 * <p>会考虑由 FactoryBean 创建的对象，这意味着会对这些 FactoryBean 进行初始化。
	 * 如果 FactoryBean 创建的对象不匹配，则会将原始的 FactoryBean 本身与类型进行匹配。
	 * <p>不会考虑该工厂可能参与的任何层级结构。若要将祖先工厂中的 Bean 一并考虑在内，
	 * 请使用 BeanFactoryUtils 的 {@code beanNamesForTypeIncludingAncestors} 方法。
	 * <p>注意：不会忽略通过非 Bean 定义方式注册的单例 Bean。
	 * <p>此版本的 {@code getBeanNamesForType} 会匹配所有种类的 Bean，
	 * 包括单例、原型以及 FactoryBean。在大多数实现中，其结果与
	 * {@code getBeanNamesForType(type, true, true)} 基本相同。
	 * <p>该方法返回的 Bean 名称应尽可能按照后端配置中<i>定义的顺序</i>排列。
	 * @param type 要匹配的泛型类或接口
	 * @return 匹配给定对象类型（包括子类）的 Bean 名称数组（或由 FactoryBean
	 * 创建的对象对应的 Bean 名称），如果没有匹配则返回空数组
	 * @since 4.2
	 * @see #isTypeMatch(String, ResolvableType)
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType)
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * 返回与给定类型（包括其子类）匹配的 Bean 名称数组，
	 * 判断依据为 Bean 定义，或者在 FactoryBean 情况下为其 {@code getObjectType} 的值。
	 * <p><b>注意：该方法只会内省顶层 Bean。</b>它<i>不会</i>检查嵌套 Bean，
	 * 即使这些嵌套 Bean 也可能匹配指定类型。
	 * <p>如果设置了 "allowEagerInit" 标志，则会考虑由 FactoryBean 创建的对象，
	 * 这意味着会对这些 FactoryBean 进行初始化。如果 FactoryBean 创建的对象不匹配，
	 * 则会将原始的 FactoryBean 本身与类型进行匹配。如果没有设置 "allowEagerInit"，
	 * 则只会检查原始的 FactoryBean（不需要初始化每个 FactoryBean）。
	 * <p>不会考虑该工厂可能参与的任何层级结构。若要将祖先工厂中的 Bean 一并考虑在内，
	 * 请使用 BeanFactoryUtils 的 {@code beanNamesForTypeIncludingAncestors} 方法。
	 * <p>注意：不会忽略通过非 Bean 定义方式注册的单例 Bean。
	 * <p>该方法返回的 Bean 名称应尽可能按照后端配置中<i>定义的顺序</i>排列。
	 * @param type 要匹配的泛型类或接口
	 * @param includeNonSingletons 是否也包含原型或其他作用域的 Bean，还是仅包含单例
	 *（同样适用于 FactoryBean）
	 * @param allowEagerInit 是否为了类型检查而初始化<i>懒加载单例</i>以及
	 * <i>由 FactoryBean 创建的对象</i>（或通过带有 "factory-bean" 引用的工厂方法
	 * 创建的对象）。请注意，FactoryBean 需要被提前初始化才能确定其类型：因此，
	 * 传入 {@code true} 会初始化 FactoryBean 以及 "factory-bean" 引用。
	 * @return 匹配给定对象类型（包括子类）的 Bean 名称数组（或由 FactoryBean
	 * 创建的对象对应的 Bean 名称），如果没有匹配则返回空数组
	 * @since 5.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, ResolvableType, boolean, boolean)
	 */
	String[] getBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * 返回与给定类型（包括其子类）匹配的 Bean 名称数组，
	 * 判断依据为 Bean 定义，或者在 FactoryBean 情况下为其 {@code getObjectType} 的值。
	 * <p><b>注意：该方法只会内省顶层 Bean。</b>它<i>不会</i>检查嵌套 Bean，
	 * 即使这些嵌套 Bean 也可能匹配指定类型。
	 * <p>会考虑由 FactoryBean 创建的对象，这意味着会对这些 FactoryBean 进行初始化。
	 * 如果 FactoryBean 创建的对象不匹配，则会将原始的 FactoryBean 本身与类型进行匹配。
	 * <p>不会考虑该工厂可能参与的任何层级结构。若要将祖先工厂中的 Bean 一并考虑在内，
	 * 请使用 BeanFactoryUtils 的 {@code beanNamesForTypeIncludingAncestors} 方法。
	 * <p>注意：不会忽略通过非 Bean 定义方式注册的单例 Bean。
	 * <p>此版本的 {@code getBeanNamesForType} 会匹配所有种类的 Bean，
	 * 包括单例、原型以及 FactoryBean。在大多数实现中，其结果与
	 * {@code getBeanNamesForType(type, true, true)} 基本相同。
	 * <p>该方法返回的 Bean 名称应尽可能按照后端配置中<i>定义的顺序</i>排列。
	 * @param type 要匹配的类或接口，或为 {@code null} 表示所有 Bean 名称
	 * @return 匹配给定对象类型（包括子类）的 Bean 名称数组（或由 FactoryBean
	 * 创建的对象对应的 Bean 名称），如果没有匹配则返回空数组
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type);

	/**
	 * 返回与给定类型（包括其子类）匹配的 Bean 名称数组，
	 * 判断依据为 Bean 定义，或者在 FactoryBean 情况下为其 {@code getObjectType} 的值。
	 * <p><b>注意：该方法只会内省顶层 Bean。</b>它<i>不会</i>检查嵌套 Bean，
	 * 即使这些嵌套 Bean 也可能匹配指定类型。
	 * <p>如果设置了 "allowEagerInit" 标志，则会考虑由 FactoryBean 创建的对象，
	 * 这意味着会对这些 FactoryBean 进行初始化。如果 FactoryBean 创建的对象不匹配，
	 * 则会将原始的 FactoryBean 本身与类型进行匹配。如果没有设置 "allowEagerInit"，
	 * 则只会检查原始的 FactoryBean（不需要初始化每个 FactoryBean）。
	 * <p>不会考虑该工厂可能参与的任何层级结构。若要将祖先工厂中的 Bean 一并考虑在内，
	 * 请使用 BeanFactoryUtils 的 {@code beanNamesForTypeIncludingAncestors} 方法。
	 * <p>注意：不会忽略通过非 Bean 定义方式注册的单例 Bean。
	 * <p>该方法返回的 Bean 名称应尽可能按照后端配置中<i>定义的顺序</i>排列。
	 * @param type 要匹配的类或接口，或为 {@code null} 表示所有 Bean 名称
	 * @param includeNonSingletons 是否也包含原型或其他作用域的 Bean，还是仅包含单例
	 *（同样适用于 FactoryBean）
	 * @param allowEagerInit 是否为了类型检查而初始化<i>懒加载单例</i>以及
	 * <i>由 FactoryBean 创建的对象</i>（或通过带有 "factory-bean" 引用的工厂方法
	 * 创建的对象）。请注意，FactoryBean 需要被提前初始化才能确定其类型：因此，
	 * 传入 {@code true} 会初始化 FactoryBean 以及 "factory-bean" 引用。
	 * @return 匹配给定对象类型（包括子类）的 Bean 名称数组（或由 FactoryBean
	 * 创建的对象对应的 Bean 名称），如果没有匹配则返回空数组
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * 返回与给定对象类型（包括其子类）匹配的 Bean 实例 Map，
	 * 判断依据为 Bean 定义，或者在 FactoryBean 情况下为其 {@code getObjectType} 的值。
	 * <p><b>注意：该方法只会内省顶层 Bean。</b>它<i>不会</i>检查嵌套 Bean，
	 * 即使这些嵌套 Bean 也可能匹配指定类型。
	 * <p>会考虑由 FactoryBean 创建的对象，这意味着会对这些 FactoryBean 进行初始化。
	 * 如果 FactoryBean 创建的对象不匹配，则会将原始的 FactoryBean 本身与类型进行匹配。
	 * <p>不会考虑该工厂可能参与的任何层级结构。若要将祖先工厂中的 Bean 一并考虑在内，
	 * 请使用 BeanFactoryUtils 的 {@code beansOfTypeIncludingAncestors} 方法。
	 * <p>注意：不会忽略通过非 Bean 定义方式注册的单例 Bean。
	 * <p>此版本的 {@code getBeansOfType} 会匹配所有种类的 Bean，
	 * 包括单例、原型以及 FactoryBean。在大多数实现中，其结果与
	 * {@code getBeansOfType(type, true, true)} 基本相同。
	 * <p>该方法返回的 Map 应尽可能按照后端配置中<i>定义的顺序</i>，
	 * 返回 Bean 名称及其对应实例。
	 * @param type 要匹配的类或接口，或为 {@code null} 表示所有具体 Bean
	 * @return 包含所有匹配 Bean 的 Map，其中 key 为 Bean 名称，value 为对应实例
	 * @throws BeansException 如果某个 Bean 无法被创建
	 * @since 1.1.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;

	/**
	 * 返回与给定对象类型（包括其子类）匹配的 Bean 实例 Map，
	 * 判断依据为 Bean 定义，或者在 FactoryBean 情况下为其 {@code getObjectType} 的值。
	 * <p><b>注意：该方法只会内省顶层 Bean。</b>它<i>不会</i>检查嵌套 Bean，
	 * 即使这些嵌套 Bean 也可能匹配指定类型。
	 * <p>如果设置了 "allowEagerInit" 标志，则会考虑由 FactoryBean 创建的对象，
	 * 这意味着会对这些 FactoryBean 进行初始化。如果 FactoryBean 创建的对象不匹配，
	 * 则会将原始的 FactoryBean 本身与类型进行匹配。如果没有设置 "allowEagerInit"，
	 * 则只会检查原始的 FactoryBean（不需要初始化每个 FactoryBean）。
	 * <p>不会考虑该工厂可能参与的任何层级结构。若要将祖先工厂中的 Bean 一并考虑在内，
	 * 请使用 BeanFactoryUtils 的 {@code beansOfTypeIncludingAncestors} 方法。
	 * <p>注意：不会忽略通过非 Bean 定义方式注册的单例 Bean。
	 * <p>该方法返回的 Map 应尽可能按照后端配置中<i>定义的顺序</i>，
	 * 返回 Bean 名称及其对应实例。
	 * @param type 要匹配的类或接口，或为 {@code null} 表示所有具体 Bean
	 * @param includeNonSingletons 是否也包含原型或其他作用域的 Bean，还是仅包含单例
	 *（同样适用于 FactoryBean）
	 * @param allowEagerInit 是否为了类型检查而初始化<i>懒加载单例</i>以及
	 * <i>由 FactoryBean 创建的对象</i>（或通过带有 "factory-bean" 引用的工厂方法
	 * 创建的对象）。请注意，FactoryBean 需要被提前初始化才能确定其类型：因此，
	 * 传入 {@code true} 会初始化 FactoryBean 以及 "factory-bean" 引用。
	 * @return 包含所有匹配 Bean 的 Map，其中 key 为 Bean 名称，value 为对应实例
	 * @throws BeansException 如果某个 Bean 无法被创建
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	/**
	 * 查找所有使用给定 {@link Annotation} 类型进行标注的 Bean 名称，
	 * 且在此过程中不会创建对应的 Bean 实例。
	 * <p>需要注意的是，该方法会考虑由 FactoryBean 创建的对象，这意味着为了确定
	 * 其对象类型，相关 FactoryBean 将会被初始化。
	 * @param annotationType 要查找的注解类型
	 * （在指定 Bean 的类、接口或工厂方法级别进行查找）
	 * @return 所有匹配 Bean 的名称数组
	 * @since 4.0
	 * @see #findAnnotationOnBean
	 */
	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);

	/**
	 * 查找所有使用给定 {@link Annotation} 类型进行标注的 Bean，
	 * 并返回一个包含 Bean 名称和对应实例的 Map。
	 * <p>需要注意的是，该方法会考虑由 FactoryBean 创建的对象，这意味着为了确定
	 * 其对象类型，相关 FactoryBean 将会被初始化。
	 * @param annotationType 要查找的注解类型
	 * （在指定 Bean 的类、接口或工厂方法级别进行查找）
	 * @return 包含所有匹配 Bean 的 Map，其中 key 为 Bean 名称，value 为对应实例
	 * @throws BeansException 如果某个 Bean 无法被创建
	 * @since 3.0
	 * @see #findAnnotationOnBean
	 */
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;

	/**
	 * 在指定 Bean 上查找给定 {@code annotationType} 类型的 {@link Annotation}，
	 * 如果在该类本身找不到，则会遍历其接口和父类，同时也会检查该 Bean 的
	 * 工厂方法（如果存在）。
	 * @param beanName 要在其上查找注解的 Bean 名称
	 * @param annotationType 要查找的注解类型
	 * （在指定 Bean 的类、接口或工厂方法级别进行查找）
	 * @return 如果找到则返回给定类型的注解，否则返回 {@code null}
	 * @throws NoSuchBeanDefinitionException 如果不存在具有给定名称的 Bean
	 * @since 3.0
	 * @see #getBeanNamesForAnnotation
	 * @see #getBeansWithAnnotation
	 * @see #getType(String)
	 */
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

	/**
	 * 在指定 Bean 上查找给定 {@code annotationType} 类型的 {@link Annotation}，
	 * 如果在该类本身找不到，则会遍历其接口和父类，同时也会检查该 Bean 的
	 * 工厂方法（如果存在）。
	 * @param beanName 要在其上查找注解的 Bean 名称
	 * @param annotationType 要查找的注解类型
	 * （在指定 Bean 的类、接口或工厂方法级别进行查找）
	 * @param allowFactoryBeanInit 是否允许仅为了确定对象类型而初始化一个
	 * {@code FactoryBean}
	 * @return 如果找到则返回给定类型的注解，否则返回 {@code null}
	 * @throws NoSuchBeanDefinitionException 如果不存在具有给定名称的 Bean
	 * @since 5.3.14
	 * @see #getBeanNamesForAnnotation
	 * @see #getBeansWithAnnotation
	 * @see #getType(String, boolean)
	 */
	@Nullable
	<A extends Annotation> A findAnnotationOnBean(
			String beanName, Class<A> annotationType, boolean allowFactoryBeanInit)
			throws NoSuchBeanDefinitionException;

}
