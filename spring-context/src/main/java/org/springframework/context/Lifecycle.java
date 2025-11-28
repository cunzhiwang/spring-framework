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

package org.springframework.context;

/**
 * 一个通用接口，用于定义启动/停止生命周期控制的方法。
 * 典型用例是用来控制异步处理。
 * <b>注意：该接口并不包含具体的自动启动语义，如需自动启动能力，
 * 请考虑实现 {@link SmartLifecycle}。</b>
 *
 * <p>既可以由组件实现（通常是 Spring 容器中定义的 bean），
 * 也可以由容器实现（通常是 Spring {@link ApplicationContext} 本身）。
 * 容器会把启动/停止信号传播给其内部所有适用的组件，例如在运行时执行停止/重启场景。
 *
 * <p>既可以被直接调用，也可以通过 JMX 作为管理操作来使用。
 * 在后者场景下，通常会使用
 * {@link org.springframework.jmx.export.MBeanExporter}，
 * 并配合 {@link org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler}
 * 限制对受生命周期控制组件的可见性，仅暴露 {@code Lifecycle} 接口。
 *
 * <p>注意：当前的 {@code Lifecycle} 接口只在<b>顶层单例 bean</b> 上得到支持。
 * 对于其他组件，{@code Lifecycle} 接口将不会被检测到，从而被忽略。
 * 另外，扩展的 {@link SmartLifecycle} 接口为应用上下文的启动和关闭阶段
 * 提供了更完善的集成支持。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see SmartLifecycle
 * @see ConfigurableApplicationContext
 * 本接口也常用于 JMS 消息监听容器和 Quartz 定时任务等组件的生命周期管理场景。
 */
public interface Lifecycle {

	/**
	 * 启动该组件。
	 * <p>如果组件已经在运行，则不应抛出异常。
	 * <p>对于容器来说，该方法会把启动信号传播给所有适用的组件。
	 * @see SmartLifecycle#isAutoStartup()
	 */
	void start();

	/**
	 * 停止该组件，通常是以同步方式执行，使得在该方法返回时组件已完全停止。
	 * 如需异步停止行为，请考虑实现 {@link SmartLifecycle} 及其 {@code stop(Runnable)} 变体。
	 * <p>注意：无法保证停止通知一定优先于销毁回调：
	 * 在正常关闭时，{@code Lifecycle} bean 会先收到停止通知，
	 * 然后再触发通用的销毁回调；但是在上下文运行期的热刷新，或刷新失败被中止时，
	 * 某个 bean 的销毁方法可能会在没有任何提前停止信号的情况下被调用。
	 * <p>如果组件当前未在运行（尚未启动），不应抛出异常。
	 * <p>对于容器来说，该方法会把停止信号传播给所有适用的组件。
	 * @see SmartLifecycle#stop(Runnable)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	void stop();

	/**
	 * 检查该组件当前是否处于运行状态。
	 * <p>对于容器来说，当且仅当<i>所有</i>适用组件都处于运行状态时才返回 {@code true}。
	 * @return 该组件当前是否正在运行
	 */
	boolean isRunning();

}
