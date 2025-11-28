/*
 * Copyright 2002-2024 the original author or authors.
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
 * {@link Lifecycle} 接口的扩展，适用于那些需要在 {@code ApplicationContext}
 * 刷新和/或关闭时按特定顺序启动和停止的对象。
 *
 * <p>{@link #isAutoStartup()} 的返回值表明该对象是否应在上下文刷新时自动启动。
 * 接受回调参数的 {@link #stop(Runnable)} 方法对于具有异步关闭过程的对象
 * 非常有用。任何该接口的实现类在完成关闭时<i>必须</i>调用回调的
 * {@code run()} 方法，以避免整个 {@code ApplicationContext} 关闭过程中
 * 出现不必要的延迟。
 *
 * <p>本接口扩展了 {@link Phased}，其 {@link #getPhase()} 方法的返回值用于
 * 指示该 {@code Lifecycle} 组件应在生命周期的哪个阶段启动和停止。
 * 启动过程从<i>最小</i>的阶段值开始，到<i>最大</i>的阶段值结束
 * （{@code Integer.MIN_VALUE} 为最小可能值，{@code Integer.MAX_VALUE}
 * 为最大可能值）。关闭过程则按相反顺序执行。在同一阶段值内的各组件
 * 会以任意顺序执行。
 *
 * <p>例如：如果组件 B 依赖于组件 A 已经启动，那么组件 A 的阶段值应当小于
 * 组件 B。在关闭过程中，组件 B 会先于组件 A 被停止。
 *
 * <p>任何显式的 "depends-on" 依赖关系都会优先于阶段顺序：
 * 依赖方 bean 总是会在其依赖之后启动，并且总是在其依赖之前停止。
 *
 * <p>在上下文中，任何实现了 {@code Lifecycle} 却没有同时实现
 * {@code SmartLifecycle} 的组件都将被视为其阶段值为 {@code 0}。
 * 因此，如果某个 {@code SmartLifecycle} 组件的阶段值为负数，
 * 它会先于这些 {@code Lifecycle} 组件启动；如果阶段值为正数，
 * 它则会在这些 {@code Lifecycle} 组件之后启动。
 *
 * <p>请注意，由于 {@code SmartLifecycle} 提供了自动启动支持，
 * {@code SmartLifecycle} bean 实例通常会在应用上下文启动时就被初始化。
 * 因此，对于 {@code SmartLifecycle} bean 来说，bean 定义上的 lazy-init
 * 标志在实际效果上非常有限。
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see LifecycleProcessor
 * @see ConfigurableApplicationContext
 */
public interface SmartLifecycle extends Lifecycle, Phased {

	/**
	 * {@code SmartLifecycle} 的默认阶段值：{@code Integer.MAX_VALUE}。
	 * <p>这不同于常规 {@link Lifecycle} 实现通常使用的阶段值 {@code 0}，
	 * 它会让通常自动启动的 {@code SmartLifecycle} bean 在更靠后的启动阶段，
	 * 以及更靠前的关闭阶段执行。
	 * @since 5.1
	 * @see #getPhase()
	 * @see org.springframework.context.support.DefaultLifecycleProcessor#getPhase(Lifecycle)
	 */
	int DEFAULT_PHASE = Integer.MAX_VALUE;


	/**
	 * 如果该 {@code Lifecycle} 组件应在其所属的 {@link ApplicationContext}
	 * 刷新时由容器自动启动，则返回 {@code true}。
	 * <p>返回值为 {@code false} 表明该组件预期通过显式调用 {@link #start()}
	 * 来启动，与普通的 {@link Lifecycle} 实现类似。
	 * <p>默认实现返回 {@code true}。
	 * @see #start()
	 * @see #getPhase()
	 * @see LifecycleProcessor#onRefresh()
	 * @see ConfigurableApplicationContext#refresh()
	 */
	default boolean isAutoStartup() {
		return true;
	}

	/**
	 * 指示如果当前正在运行，则该 {@code Lifecycle} 组件必须停止。
	 * <p>给定的回调用于让 {@link LifecycleProcessor} 支持按照给定的关闭顺序值
	 * 有序地、并且在需要时并发地关闭所有组件。只有在 {@code SmartLifecycle}
	 * 组件确实已经停止之后，<b>必须</b>执行该回调。
	 * <p>{@link LifecycleProcessor} 只会调用这一变体的 {@code stop} 方法；
	 * 换言之，对于 {@code SmartLifecycle} 的实现而言，除非在本方法实现中
	 * 显式地委托调用，否则不会调用 {@link Lifecycle#stop()}。
	 * <p>默认实现会委托调用 {@link #stop()}，并在调用线程中立即触发给定的
	 * 回调。需要注意的是，两者之间没有任何同步，因此自定义实现至少应该
	 * 将相同的步骤放在它们共同使用的生命周期监视器（如果有的话）里。
	 * @see #stop()
	 * @see #getPhase()
	 */
	default void stop(Runnable callback) {
		stop();
		callback.run();
	}

	/**
	 * 返回该生命周期对象应运行所在的阶段值。
	 * <p>默认实现返回 {@link #DEFAULT_PHASE}，以便让 {@code stop()} 回调
	 * 在普通 {@code Lifecycle} 实现之前执行。
	 * @see #isAutoStartup()
	 * @see #start()
	 * @see #stop(Runnable)
	 * @see org.springframework.context.support.DefaultLifecycleProcessor#getPhase(Lifecycle)
	 */
	@Override
	default int getPhase() {
		return DEFAULT_PHASE;
	}

}
