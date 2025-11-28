/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.context;

import java.io.Closeable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ProtocolResolver;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.lang.Nullable;

/**
 * SPI 接口，大多数（如果不是全部的话）应用上下文都应实现该接口。
 * 除了 {@link org.springframework.context.ApplicationContext} 接口中
 * 面向客户端的方法之外，本接口还提供用于配置应用上下文的能力。
 *
 * <p>为避免在 ApplicationContext 客户端代码中直接暴露配置和生命周期相关的方法，
 * 这些配置和生命周期方法被封装在此接口中。此处的方法只应用于启动和关闭阶段的代码。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @since 03.11.2003
 */
public interface ConfigurableApplicationContext extends ApplicationContext, Lifecycle, Closeable {

	/**
	 * 在一个 String 值中，任意数量的这些字符都会被视为多个上下文配置路径之间的分隔符。
	 * 参见 org.springframework.context.support.AbstractXmlApplicationContext#setConfigLocation，
	 * org.springframework.web.context.ContextLoader#CONFIG_LOCATION_PARAM，
	 * org.springframework.web.servlet.FrameworkServlet#setContextConfigLocation 等配置位置设置方式。
	 */
	String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	/**
	 * 工厂中 ConversionService bean 的名称。
	 * 如果没有提供该 bean，则会应用默认的类型转换规则。
	 * @since 3.0
	 * @see org.springframework.core.convert.ConversionService
	 */
	String CONVERSION_SERVICE_BEAN_NAME = "conversionService";

	/**
	 * 工厂中 LoadTimeWeaver bean 的名称。如果提供了这样一个 bean，
	 * 上下文将在进行类型匹配时使用临时的 ClassLoader，
	 * 以便允许 LoadTimeWeaver 处理所有实际的 bean 类。
	 * @since 2.5
	 * @see org.springframework.instrument.classloading.LoadTimeWeaver
	 */
	String LOAD_TIME_WEAVER_BEAN_NAME = "loadTimeWeaver";

	/**
	 * 工厂中 {@link Environment} bean 的名称。
	 * @since 3.1
	 */
	String ENVIRONMENT_BEAN_NAME = "environment";

	/**
	 * 工厂中 System 属性 bean 的名称。
	 * @see java.lang.System#getProperties()
	 */
	String SYSTEM_PROPERTIES_BEAN_NAME = "systemProperties";

	/**
	 * 工厂中 System 环境变量 bean 的名称。
	 * @see java.lang.System#getenv()
	 */
	String SYSTEM_ENVIRONMENT_BEAN_NAME = "systemEnvironment";

	/**
	 * 工厂中 {@link ApplicationStartup} bean 的名称。
	 * @since 5.3
	 */
	String APPLICATION_STARTUP_BEAN_NAME = "applicationStartup";

	/**
	 * 通过 {@linkplain #registerShutdownHook() 注册关闭钩子} 时，
	 * 所创建线程的 {@link Thread#getName() 名称}：{@value}。
	 * @since 5.2
	 * @see #registerShutdownHook()
	 */
	String SHUTDOWN_HOOK_THREAD_NAME = "SpringContextShutdownHook";


	/**
	 * 设置此应用上下文的唯一 ID。
	 * @since 3.0
	 */
	void setId(String id);

	/**
	 * 设置此应用上下文的父上下文。
	 * <p>注意：父上下文一旦设置通常不应再变更。如果在创建本类实例时父上下文尚不可用，
	 * 则只应在构造方法之外进行设置，例如在 WebApplicationContext 初始化过程中。
	 * @param parent 父上下文
	 * @see org.springframework.web.context.ConfigurableWebApplicationContext
	 */
	void setParent(@Nullable ApplicationContext parent);

	/**
	 * 为此应用上下文设置 {@code Environment}。
	 * @param environment 新的环境对象
	 * @since 3.1
	 */
	void setEnvironment(ConfigurableEnvironment environment);

	/**
	 * 以可配置的形式返回此应用上下文的 {@code Environment}，允许进行进一步定制。
	 * @since 3.1
	 */
	@Override
	ConfigurableEnvironment getEnvironment();

	/**
	 * 为此应用上下文设置 {@link ApplicationStartup}。
	 * <p>这样可以在应用上下文启动期间记录相关的度量数据。
	 * @param applicationStartup 新的应用启动度量记录器
	 * @since 5.3
	 */
	void setApplicationStartup(ApplicationStartup applicationStartup);

	/**
	 * 返回此应用上下文使用的 {@link ApplicationStartup}。
	 * @since 5.3
	 */
	ApplicationStartup getApplicationStartup();

	/**
	 * 添加一个新的 BeanFactoryPostProcessor，它会在此应用上下文刷新时
	 * 作用于其内部的 BeanFactory，并且发生在任何 bean 定义被解析之前。
	 * 通常在上下文配置阶段调用此方法。
	 * @param postProcessor 要注册的工厂后置处理器
	 */
	void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor);

	/**
	 * 添加一个新的 ApplicationListener，使其能够在上下文事件（例如上下文刷新、
	 * 上下文关闭等）发生时得到通知。
	 * <p>注意：如果上下文尚未激活，在刷新时会应用这里注册的 ApplicationListener；
	 * 如果上下文已经激活，则会通过当前的事件多播器即时生效。
	 * @param listener 要注册的 ApplicationListener
	 * @see org.springframework.context.event.ContextRefreshedEvent
	 * @see org.springframework.context.event.ContextClosedEvent
	 */
	void addApplicationListener(ApplicationListener<?> listener);

	/**
	 * 指定用于加载类路径资源和 bean 类的 ClassLoader。
	 * <p>该上下文的 ClassLoader 将会传递给内部的 BeanFactory。
	 * @since 5.2.7
	 * @see org.springframework.core.io.DefaultResourceLoader#DefaultResourceLoader(ClassLoader)
	 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setBeanClassLoader
	 */
	void setClassLoader(ClassLoader classLoader);

	/**
	 * 在此应用上下文中注册给定的协议解析器，以便处理额外的资源协议。
	 * <p>此类解析器会在本上下文的标准解析规则之前被调用，
	 * 因此也可以覆盖任何默认规则。
	 * @since 4.3
	 */
	void addProtocolResolver(ProtocolResolver resolver);

	/**
	 * 加载或刷新配置的持久化表示形式，
	 * 其来源可能是基于 Java 的配置、XML 文件、properties 文件、
	 * 关系型数据库表结构或其他形式。
	 * <p>由于这是一个启动方法，如果执行失败，应销毁已经创建的单例 bean，
	 * 以避免资源悬挂。换句话说，在调用该方法之后，要么所有单例都已创建，
	 * 要么一个单例也没有创建。
	 * @throws BeansException 如果无法初始化 BeanFactory
	 * @throws IllegalStateException 如果上下文已经被初始化，且不支持多次刷新
	 */
	void refresh() throws BeansException, IllegalStateException;

	/**
	 * 向 JVM 运行时注册一个关闭钩子，在 JVM 关闭时关闭此上下文
	 *（除非在那之前已经显式关闭）。
	 * <p>此方法可以被调用多次，但每个上下文实例最多只会注册一个关闭钩子。
	 * <p>从 Spring Framework 5.2 起，关闭钩子线程的
	 * {@linkplain Thread#getName() 名称} 应为 {@link #SHUTDOWN_HOOK_THREAD_NAME}。
	 * @see java.lang.Runtime#addShutdownHook
	 * @see #close()
	 */
	void registerShutdownHook();

	/**
	 * 关闭此应用上下文，释放实现可能持有的所有资源和锁。
	 * 这包括销毁所有已缓存的单例 bean。
	 * <p>注意：不会对父上下文调用 {@code close}；父上下文拥有自己独立的生命周期。
	 * <p>此方法可以被多次调用而不会产生副作用：对已关闭上下文的后续
	 * {@code close} 调用将被忽略。
	 */
	@Override
	void close();

	/**
	 * 判断此应用上下文当前是否处于“活动”状态，
	 * 即是否至少已经刷新过一次且尚未被关闭。
	 * @return 上下文是否仍然处于活动状态
	 * @see #refresh()
	 * @see #close()
	 * @see #getBeanFactory()
	 */
	boolean isActive();

	/**
	 * 返回此应用上下文内部使用的 BeanFactory。
	 * 可用于访问底层工厂的特定功能。
	 * <p>注意：不要使用此方法对 BeanFactory 进行后处理；
	 * 在此之前单例 bean 很可能已经被实例化。
	 * 如果需要在 bean 被创建之前拦截 BeanFactory 的设置过程，
	 * 请使用 BeanFactoryPostProcessor。
	 * <p>通常情况下，该内部工厂只会在上下文“活动”期间可用，
	 * 即在 {@link #refresh()} 与 {@link #close()} 之间。
	 * 可以通过 {@link #isActive()} 标志检查上下文是否处于适当的状态。
	 * @return 底层的 BeanFactory
	 * @throws IllegalStateException 如果上下文不再持有内部 BeanFactory
	 *（通常是因为尚未调用 {@link #refresh()}，或已经调用过 {@link #close()}）
	 * @see #isActive()
	 * @see #refresh()
	 * @see #close()
	 * @see #addBeanFactoryPostProcessor
	 */
	ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

}