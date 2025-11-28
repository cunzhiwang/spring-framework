/*
 * Copyright 2002-2023 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 用于访问 Spring Bean 容器的根接口。
 * Spring Bean 工厂的根接口。
 * <p>这是对 Bean 容器的基础客户端视图；更丰富的功能由
 * {@link ListableBeanFactory} 以及
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * 等子接口提供，用于满足更具体的需求。
 *
 * <p>该接口的实现类会持有若干 Bean 定义，每个 Bean 都通过一个字符串名称唯一标识。
 * 根据 Bean 定义的不同，工厂会返回一个被包含对象的独立实例
 * （Prototype 设计模式），或者一个共享的单一实例（相比经典的 Singleton
 * 设计模式更优的方案，在这里单例的作用域限定在工厂本身）。究竟返回哪一种实例
 * 取决于 BeanFactory 的配置，但对调用方而言 API 是相同的。从 Spring 2.0 开始，
 * 还可以根据具体的应用上下文提供更多作用域（例如 Web 环境中的 "request" 和
 * "session" 作用域）。
 *
 * <p>这种设计的要点在于：{@code BeanFactory} 作为应用组件的集中注册表，
 * 统一管理这些组件的配置信息（例如，不再需要每个对象自己去读取 properties 文件）。
 * 关于这种方式的优势，可以参考《Expert One-on-One J2EE Design and Development》
 * 一书的第 4 章和第 11 章。
 *
 * <p>需要注意的是，一般更推荐依赖注入（Dependency Injection，“推”配置）来
 * 通过 setter 或构造器为应用对象注入依赖，而不是使用 BeanFactory 查找这类
 * “拉”配置。Spring 的依赖注入功能正是基于此 {@code BeanFactory} 接口及其
 * 子接口实现的。
 *
 * <p>通常，一个 {@code BeanFactory} 会从某种配置源（例如 XML 文档）中加载
 * Bean 定义，并使用 {@code org.springframework.beans} 包对 Bean 进行配置。
 * 然而，实现类也可以在 Java 代码中按需直接创建并返回对象。Bean 定义的存储方式
 * 没有任何限制：LDAP、RDBMS、XML、properties 文件等都可以。实现类通常也应当
 * 支持 Bean 之间的相互引用（依赖注入）。
 *
 * <p>与 {@link ListableBeanFactory} 中的方法相比，如果当前工厂实现了
 * {@link HierarchicalBeanFactory}，那么本接口中的所有操作在查找时也会检查父工厂。
 * 如果在当前工厂实例中找不到某个 Bean，则会委托给其直接父工厂进行查找。
 * 当前工厂中的 Bean 应视为覆盖父工厂中同名的 Bean。
 *
 * <p>BeanFactory 的实现应当尽可能支持标准的 Bean 生命周期接口。
 * 完整的初始化方法以及它们的标准调用顺序如下：
 * <ol>
 * <li>BeanNameAware 的 {@code setBeanName}
 * <li>BeanClassLoaderAware 的 {@code setBeanClassLoader}
 * <li>BeanFactoryAware 的 {@code setBeanFactory}
 * <li>EnvironmentAware 的 {@code setEnvironment}
 * <li>EmbeddedValueResolverAware 的 {@code setEmbeddedValueResolver}
 * <li>ResourceLoaderAware 的 {@code setResourceLoader}
 * （仅在运行于应用上下文中时适用）
 * <li>ApplicationEventPublisherAware 的 {@code setApplicationEventPublisher}
 * （仅在运行于应用上下文中时适用）
 * <li>MessageSourceAware 的 {@code setMessageSource}
 * （仅在运行于应用上下文中时适用）
 * <li>ApplicationContextAware 的 {@code setApplicationContext}
 * （仅在运行于应用上下文中时适用）
 * <li>ServletContextAware 的 {@code setServletContext}
 * （仅在运行于 Web 应用上下文中时适用）
 * <li>BeanPostProcessor 的 {@code postProcessBeforeInitialization} 方法
 * <li>InitializingBean 的 {@code afterPropertiesSet}
 * <li>自定义的 {@code init-method} 定义
 * <li>BeanPostProcessor 的 {@code postProcessAfterInitialization} 方法
 * </ol>
 *
 * <p>在 BeanFactory 关闭时，将会应用如下生命周期方法：
 * <ol>
 * <li>DestructionAwareBeanPostProcessor 的
 * {@code postProcessBeforeDestruction} 方法
 * <li>DisposableBean 的 {@code destroy}
 * <li>自定义的 {@code destroy-method} 定义
 * </ol>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.EnvironmentAware#setEnvironment
 * @see org.springframework.context.EmbeddedValueResolverAware#setEmbeddedValueResolver
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor#postProcessBeforeDestruction
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 * 用于对 {@link FactoryBean} 实例进行“去引用”，并将其与该 FactoryBean
	 * <i>创建</i>出来的 Bean 区分开来。
	 * 例如，如果名为 {@code myJndiObject} 的 Bean 是一个 FactoryBean，
	 * 那么获取 {@code &myJndiObject} 将返回工厂本身，而不是工厂返回的实例。
	 * 该前缀用于区分普通 Bean 和 FactoryBean，例如普通 Bean 的名称为
	 * {@code myProject}，而对应的 FactoryBean 名称则为 {@code &myProject}。
	 */
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * 返回指定名称的 Bean 实例，该实例可能是共享的，也可能是独立的。
	 * <p>此方法允许将 Spring {@code BeanFactory} 作为 Singleton 或 Prototype
	 * 设计模式的替代方案来使用。对于单例 Bean，调用方可以持有返回对象的引用。
	 * <p>会将别名解析回对应的规范 Bean 名称。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要检索的 Bean 名称
	 * @return 指定名称的 Bean 实例。
	 * 返回值本身绝不会是 {@code null}，但可能是一个代表 {@code null}
	 * 的桩对象（例如由工厂方法返回的 {@code null}），此时可以通过
	 * {@code equals(null)} 进行检查。
	 * 如需解析可选依赖关系，建议使用 {@link #getBeanProvider(Class)}。
	 * @throws NoSuchBeanDefinitionException 如果不存在具有指定名称的 Bean
	 * @throws BeansException 如果无法获取该 Bean
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * 返回指定名称的 Bean 实例，该实例可能是共享的，也可能是独立的。
	 * <p>其行为与 {@link #getBean(String)} 相同，但通过在 Bean 不符合
	 * 所需类型时抛出 {@link BeanNotOfRequiredTypeException} 来提供类型安全。
	 * 这意味着在对结果进行正确类型转换时不会抛出 ClassCastException，
	 * 这一点与直接调用 {@link #getBean(String)} 后再强制类型转换不同。
	 * <p>会将别名解析回对应的规范 Bean 名称。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要检索的 Bean 名称
	 * @param requiredType Bean 必须匹配的类型；可以是接口或父类
	 * @return 指定名称且类型匹配的 Bean 实例。
	 * 返回值本身绝不会是 {@code null}。如果为请求的 Bean 解析到了一个
	 * 代表 {@code null} 的桩对象（例如 NullBean），则会抛出
	 * 针对该 NullBean 桩对象的 {@code BeanNotOfRequiredTypeException}。
	 * 如需解析可选依赖关系，建议使用 {@link #getBeanProvider(Class)}。
	 * @throws NoSuchBeanDefinitionException 如果不存在这样的 Bean 定义
	 * @throws BeanNotOfRequiredTypeException 如果 Bean 不是所需的类型
	 * @throws BeansException 如果无法创建或获取该 Bean
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * 返回指定名称的 Bean 实例，该实例可能是共享的，也可能是独立的。
	 * <p>允许在创建 Bean 实例时显式指定构造函数参数 / 工厂方法参数，
	 * 从而覆盖 Bean 定义中指定的默认参数（如果有的话）。
	 * @param name 要检索的 Bean 名称
	 * @param args 在使用显式参数创建 Bean 实例时要使用的参数
	 * （仅在创建新实例时适用，而不是检索已有实例时）
	 * @return 指定名称的 Bean 实例
	 * @throws NoSuchBeanDefinitionException 如果不存在这样的 Bean 定义
	 * @throws BeanDefinitionStoreException 如果指定了参数，但目标 Bean 不是 prototype
	 * @throws BeansException 如果无法创建该 Bean
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * 返回与给定类型唯一匹配的 Bean 实例（如果存在）。
	 * <p>此方法类似于 {@link ListableBeanFactory} 中的按类型查找，但也可能
	 * 被转换为基于该类型名称的常规按名查找。对于需要在一组 Bean 上执行
	 * 更复杂的检索操作的场景，建议使用 {@link ListableBeanFactory} 和/或
	 * {@link BeanFactoryUtils}。
	 * @param requiredType Bean 必须匹配的类型；可以是接口或父类
	 * @return 与给定类型唯一匹配的 Bean 实例
	 * @throws NoSuchBeanDefinitionException 如果找不到给定类型的 Bean
	 * @throws NoUniqueBeanDefinitionException 如果找到多个给定类型的 Bean
	 * @throws BeansException 如果无法创建或获取该 Bean
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * 返回指定类型的 Bean 实例，该实例可能是共享的，也可能是独立的。
	 * <p>允许在创建 Bean 实例时显式指定构造函数参数 / 工厂方法参数，
	 * 从而覆盖 Bean 定义中指定的默认参数（如果有的话）。
	 * <p>此方法类似于 {@link ListableBeanFactory} 中的按类型查找，但也可能
	 * 被转换为基于该类型名称的常规按名查找。对于需要在一组 Bean 上执行
	 * 更复杂检索操作的场景，建议使用 {@link ListableBeanFactory} 和/或
	 * {@link BeanFactoryUtils}。
	 * @param requiredType Bean 必须匹配的类型；可以是接口或父类
	 * @param args 在使用显式参数创建 Bean 实例时要使用的参数
	 * （仅在创建新实例时适用，而不是检索已有实例时）
	 * @return 指定类型的 Bean 实例
	 * @throws NoSuchBeanDefinitionException 如果不存在这样的 Bean 定义
	 * @throws BeanDefinitionStoreException 如果指定了参数，但目标 Bean 不是 prototype
	 * @throws BeansException 如果无法创建该 Bean
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * 返回指定类型 Bean 的提供者（provider），允许以延迟、按需的方式
	 * 检索实例，并支持检查可用性和唯一性等选项。
	 * <p>如需匹配泛型类型，请考虑使用 {@link #getBeanProvider(ResolvableType)}。
	 * @param requiredType Bean 必须匹配的类型；可以是接口或父类
	 * @return 对应的 provider 句柄
	 * @since 5.1
	 * @see #getBeanProvider(ResolvableType)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * 返回指定类型 Bean 的提供者（provider），允许以延迟、按需的方式
	 * 检索实例，并支持检查可用性和唯一性等选项。
	 * 此变体允许指定要匹配的泛型类型，类似于在方法 / 构造函数参数的
	 * 泛型类型声明中通过反射进行依赖注入的方式。
	 * <p>需要注意，这里不支持 Bean 集合，与反射注入点不同。要以编程方式
	 * 检索某个具体类型的 Bean 列表，可以在此处指定实际 Bean 类型作为参数，
	 * 随后通过 {@link ObjectProvider#orderedStream()} 或其延迟流 / 迭代功能
	 * 进行访问。
	 * <p>此外，这里的泛型匹配是严格遵循 Java 赋值规则的。若想要类似
	 * “unchecked” 编译警告那样宽松的回退匹配（具有未检查语义），可以在
	 * 此变体未能 {@link ObjectProvider#getIfAvailable() 获取} 完整泛型匹配
	 * 的情况下，考虑使用 {@link #getBeanProvider(Class)} 并传入原始类型。
	 * @param requiredType Bean 必须匹配的类型；可以是泛型类型声明
	 * @return 对应的 provider 句柄
	 * @since 5.1
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	/**
	 * 此 BeanFactory 是否包含具有给定名称的 Bean 定义或外部注册的单例实例？
	 * <p>如果给定名称是别名，会被解析回对应的规范 Bean 名称。
	 * <p>如果该工厂是分层的（hierarchical），在当前工厂实例中找不到 Bean
	 * 时会委托其父工厂进行查找。
	 * <p>只要找到与给定名称匹配的 Bean 定义或单例实例，本方法就会返回
	 * {@code true}，无论该 Bean 定义是具体的还是抽象的、是否懒加载、是否
	 * 在作用域内。因此，需要注意的是，返回 {@code true} 并不一定意味着
	 * {@link #getBean} 能够成功返回该名称对应的实例。
	 * @param name 要查询的 Bean 名称
	 * @return 是否存在具有给定名称的 Bean
	 */
	boolean containsBean(String name);

	/**
	 * 给定名称的 Bean 是否是共享单例？也就是说，{@link #getBean} 是否总是
	 * 返回同一实例？
	 * <p>注意：本方法返回 {@code false} 并不能明确指示该 Bean 是否具有
	 * 独立实例。它只说明该 Bean 不是单例实例，也可能表示它是某种作用域
	 * Bean。若要显式检查是否为独立实例，请使用 {@link #isPrototype} 操作。
	 * <p>会将别名解析回对应的规范 Bean 名称。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要查询的 Bean 名称
	 * @return 该 Bean 是否对应单例实例
	 * @throws NoSuchBeanDefinitionException 如果不存在具有给定名称的 Bean
	 * @see #getBean
	 * @see #isPrototype
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 给定名称的 Bean 是否是 prototype？也就是说，{@link #getBean} 是否总是
	 * 返回彼此独立的实例？
	 * <p>注意：本方法返回 {@code false} 并不能明确指示该 Bean 是否为单例
	 * 对象。它只说明该 Bean 不是独立实例，也可能表示它是某种作用域 Bean。
	 * 若要显式检查是否为共享单例实例，请使用 {@link #isSingleton} 操作。
	 * <p>会将别名解析回对应的规范 Bean 名称。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要查询的 Bean 名称
	 * @return 该 Bean 是否总是返回彼此独立的实例
	 * @throws NoSuchBeanDefinitionException 如果不存在具有给定名称的 Bean
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 检查给定名称的 Bean 是否匹配指定类型。
	 * 更具体地说，检查对给定名称调用 {@link #getBean} 时返回的对象
	 * 是否可以赋值给指定的目标类型。
	 * <p>会将别名解析回对应的规范 Bean 名称。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要查询的 Bean 名称
	 * @param typeToMatch 要匹配的类型（作为 {@code ResolvableType}）
	 * @return 如果 Bean 类型匹配则返回 {@code true}；
	 * 如果不匹配或尚无法确定，则返回 {@code false}
	 * @throws NoSuchBeanDefinitionException 如果不存在具有给定名称的 Bean
	 * @since 4.2
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 检查给定名称的 Bean 是否匹配指定类型。
	 * 更具体地说，检查对给定名称调用 {@link #getBean} 时返回的对象
	 * 是否可以赋值给指定的目标类型。
	 * <p>会将别名解析回对应的规范 Bean 名称。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要查询的 Bean 名称
	 * @param typeToMatch 要匹配的类型（作为 {@code Class}）
	 * @return 如果 Bean 类型匹配则返回 {@code true}；
	 * 如果不匹配或尚无法确定，则返回 {@code false}
	 * @throws NoSuchBeanDefinitionException 如果不存在具有给定名称的 Bean
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 确定给定名称的 Bean 的类型。更具体地说，确定对给定名称调用
	 * {@link #getBean} 时将返回的对象类型。
	 * <p>对于 {@link FactoryBean}，返回的是该 FactoryBean 创建的对象类型，
	 * 由 {@link FactoryBean#getObjectType()} 暴露。某些情况下这可能会触发
	 * 之前尚未初始化的 {@code FactoryBean} 的初始化
	 *（参见 {@link #getType(String, boolean)}）。
	 * <p>会将别名解析回对应的规范 Bean 名称。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要查询的 Bean 名称
	 * @return 该 Bean 的类型；如果无法确定则返回 {@code null}
	 * @throws NoSuchBeanDefinitionException 如果不存在具有给定名称的 Bean
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 确定给定名称的 Bean 的类型。更具体地说，确定对给定名称调用
	 * {@link #getBean} 时将返回的对象类型。
	 * <p>对于 {@link FactoryBean}，返回的是该 FactoryBean 创建的对象类型，
	 * 由 {@link FactoryBean#getObjectType()} 暴露。根据
	 * {@code allowFactoryBeanInit} 标志的不同，如果没有可用的早期类型信息，
	 * 这可能会触发之前尚未初始化的 {@code FactoryBean} 的初始化。
	 * <p>会将别名解析回对应的规范 Bean 名称。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要查询的 Bean 名称
	 * @param allowFactoryBeanInit 是否允许仅为了确定对象类型而初始化
	 * 一个 {@code FactoryBean}
	 * @return 该 Bean 的类型；如果无法确定则返回 {@code null}
	 * @throws NoSuchBeanDefinitionException 如果不存在具有给定名称的 Bean
	 * @since 5.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	@Nullable
	Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

	/**
	 * 返回给定 Bean 名称的所有别名（如果有的话）。
	 * <p>在 {@link #getBean} 调用中使用这些别名时，它们都指向同一个 Bean。
	 * <p>如果给定名称本身是一个别名，则会返回对应的原始 Bean 名称以及
	 * 其他别名（如果有），其中原始 Bean 名称是数组中的第一个元素。
	 * <p>如果在当前工厂实例中找不到该 Bean，将委托父工厂进行查找。
	 * @param name 要检查别名的 Bean 名称
	 * @return 别名数组；如果没有别名则返回空数组
	 * @see #getBean
	 */
	String[] getAliases(String name);

}
