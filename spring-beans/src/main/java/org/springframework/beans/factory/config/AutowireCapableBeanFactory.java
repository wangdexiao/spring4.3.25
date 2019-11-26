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

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

/**
 * Extension of the {@link org.springframework.beans.factory.BeanFactory}
 * interface to be implemented by bean factories that are capable of
 * autowiring, provided that they want to expose this functionality for
 * existing bean instances.
 *
 * {@link org.springframework.beans.factory.BeanFactory}接口的扩展将由能够自动装配的bean工厂实现，前提是它们希望为现有的bean实例公开此功能。
 *
 * <p>This subinterface of BeanFactory is not meant to be used in normal
 * application code: stick to {@link org.springframework.beans.factory.BeanFactory}
 * or {@link org.springframework.beans.factory.ListableBeanFactory} for
 * typical use cases.
 * <p>此BeanFactory的子接口不能在常规应用程序代码中使用：
 * 通常使用{@link org.springframework.beans.factory.BeanFactory}
 * 或{@link org.springframework.beans.factory.ListableBeanFactory} 案件。
 *
 * <p>Integration code for other frameworks can leverage this interface to
 * wire and populate existing bean instances that Spring does not control
 * the lifecycle of. This is particularly useful for WebWork Actions and
 * Tapestry Page objects, for example.
 * <p>其他框架的集成代码可以利用此接口来连接和填充Spring无法控制其生命周期的现有bean实例。 例如，这对于WebWork操作和Tapestry页面对象特别有用。
 *
 * <p>Note that this interface is not implemented by
 * {@link org.springframework.context.ApplicationContext} facades,
 * as it is hardly ever used by application code. That said, it is available
 * from an application context too, accessible through ApplicationContext's
 * {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}
 * method.
 * <p>请注意，@ link org.springframework.context.ApplicationContext}外观未实现此接口，因为应用程序代码几乎从未使用过此接口。
 * 也就是说，它也可以从应用程序上下文中获得，
 * 可以通过ApplicationContext的{@link org.springframework.context.ApplicationContext＃getAutowireCapableBeanFactory（）}方法进行访问。
 *
 * <p>You may also implement the {@link org.springframework.beans.factory.BeanFactoryAware}
 * interface, which exposes the internal BeanFactory even when running in an
 * ApplicationContext, to get access to an AutowireCapableBeanFactory:
 * simply cast the passed-in BeanFactory to AutowireCapableBeanFactory.
 *<p>您还可以实现{@link org.springframework.beans.factory.BeanFactoryAware}接口，
 * 该接口即使在ApplicationContext中运行时也公开内部BeanFactory，
 * 以访问AutowireCapableBeanFactory：只需将传入的BeanFactory强制转换为 AutowireCapableBeanFactory。
 *
 * @author Juergen Hoeller
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

	/**
	 * Constant that indicates no externally defined autowiring. Note that
	 * BeanFactoryAware etc and annotation-driven injection will still be applied.
	 * 指示没有外部定义的自动装配的常数。 注意
	 * BeanFactoryAware等和注释驱动的注入将仍然适用。
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_NO = 0;

	/**
	 * Constant that indicates autowiring bean properties by name
	 * (applying to all bean property setters).
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_BY_NAME = 1;

	/**
	 * Constant that indicates autowiring bean properties by type
	 * (applying to all bean property setters).
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_BY_TYPE = 2;

	/**
	 * Constant that indicates autowiring the greediest constructor that
	 * can be satisfied (involves resolving the appropriate constructor).
	 * @see #createBean
	 * @see #autowire
	 */
	int AUTOWIRE_CONSTRUCTOR = 3;

	/**
	 * Constant that indicates determining an appropriate autowire strategy
	 * through introspection of the bean class.
	 * @see #createBean
	 * @see #autowire
	 * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
	 * prefer annotation-based autowiring for clearer demarcation of autowiring needs.
	 */
	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;


	//-------------------------------------------------------------------------
	// Typical methods for creating and populating external bean instances
	//-------------------------------------------------------------------------

	/**
	 * Fully create a new bean instance of the given class.
	 * <p>Performs full initialization of the bean, including all applicable
	 * {@link BeanPostProcessor BeanPostProcessors}.
	 * <p>Note: This is intended for creating a fresh instance, populating annotated
	 * fields and methods as well as applying all standard bean initialization callbacks.
	 * It does <i>not</i> imply traditional by-name or by-type autowiring of properties;
	 * use {@link #createBean(Class, int, boolean)} for those purposes.
	 *
	 * 完全创建给定类的新bean实例。 <p>执行Bean的完全初始化，包括所有适用的{@link BeanPostProcessor BeanPostProcessors}。
	 * <p>注意：这用于创建一个新实例，填充带注释的字段和方法，以及应用所有标准的bean初始化回调。
	 * <i> </ i>并不暗含传统的按名称或按类型自动装配属性； 为此目的使用{@link #createBean（Class，int，boolean）}。
	 *
	 * @param beanClass the class of the bean to create
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 */
	<T> T createBean(Class<T> beanClass) throws BeansException;

	/**
	 * Populate the given bean instance through applying after-instantiation callbacks
	 * and bean property post-processing (e.g. for annotation-driven injection).
	 * <p>Note: This is essentially intended for (re-)populating annotated fields and
	 * methods, either for new instances or for deserialized instances. It does
	 * <i>not</i> imply traditional by-name or by-type autowiring of properties;
	 * use {@link #autowireBeanProperties} for those purposes.
	 * 通过应用实例化后的回调和bean属性的后处理来填充给定的bean实例（例如用于注释驱动的注入）。
	 * <p>注意：这实际上是用于（重新）填充带注释的字段和方法，无论是用于新实例还是反序列化实例。
	 * <i> </ i>并不暗含传统的按名称或按类型自动装配属性；
	 * 为此目的使用{@link #autowireBeanProperties}。
	 * @param existingBean the existing bean instance
	 * @throws BeansException if wiring failed
	 */
	void autowireBean(Object existingBean) throws BeansException;

	/**
	 * Configure the given raw bean: autowiring bean properties, applying
	 * bean property values, applying factory callbacks such as {@code setBeanName}
	 * and {@code setBeanFactory}, and also applying all bean post processors
	 * (including ones which might wrap the given raw bean).
	 * <p>This is effectively a superset of what {@link #initializeBean} provides,
	 * fully applying the configuration specified by the corresponding bean definition.
	 * <b>Note: This method requires a bean definition for the given name!</b>
	 * 配置给定的原始bean：自动装配bean属性，应用
	 * Bean属性值，应用工厂回调（例如{@code setBeanName}和{@code setBeanFactory}），还应用所有Bean后处理器（包括可能包装给定原始Bean的处理器）。
	 * <p>这实际上是{@link #initializeBean}提供的超集，完全应用了由相应bean定义指定的配置。
	 * <b>注意：此方法需要给定名称的bean定义！</ b>
	 * @param existingBean the existing bean instance 现有的bean实例
	 * @param beanName the name of the bean, to be passed to it if necessary
	 * (a bean definition of that name has to be available)
	 *  bean的名称，必要时将传递给它
	 * （该名称的bean定义必须可用）
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
	 * if there is no bean definition with the given name
	 * @throws BeansException if the initialization failed
	 * @see #initializeBean
	 */
	Object configureBean(Object existingBean, String beanName) throws BeansException;


	//-------------------------------------------------------------------------
	// Specialized methods for fine-grained control over the bean lifecycle
	//-------------------------------------------------------------------------

	/**
	 * Fully create a new bean instance of the given class with the specified
	 * autowire strategy. All constants defined in this interface are supported here.
	 * <p>Performs full initialization of the bean, including all applicable
	 * {@link BeanPostProcessor BeanPostProcessors}. This is effectively a superset
	 * of what {@link #autowire} provides, adding {@link #initializeBean} behavior.
	 * @param beanClass the class of the bean to create
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects
	 * (not applicable to autowiring a constructor, thus ignored there)
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 */
	Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Instantiate a new bean instance of the given class with the specified autowire
	 * strategy. All constants defined in this interface are supported here.
	 * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
	 * before-instantiation callbacks (e.g. for annotation-driven injection).
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the construction of the instance.
	 * 使用指定的自动装配策略实例化给定类的新bean实例。 这里支持此接口中定义的所有常量。
	 * 也可以使用{@code AUTOWIRE_NO}进行调用，以便仅应用实例化之前的回调（例如，用于注释驱动的注入）。
	 * <p> <i>不</ i>不应用标准的{@link BeanPostProcessor BeanPostProcessors}回调或对bean进行任何进一步的初始化。
	 * 该接口为此提供了独特的细粒度操作，例如
	 * {@link #initializeBean}。 但是，{ @link InstantiationAwareBeanPostProcessor}
	 * 如果适用于实例的构造，则将应用回调。
	 * @param beanClass the class of the bean to instantiate 实例化的bean的类
	 * @param autowireMode by name or type, using the constants in this interface 通过名称或类型，使用此接口中的常量
	 * @param dependencyCheck whether to perform a dependency check for object
	 * references in the bean instance (not applicable to autowiring a constructor,
	 * thus ignored there)
	 *                        是否对bean实例中的对象引用执行依赖项检查（不适用于自动装配构造函数，因此在此处被忽略）
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #initializeBean
	 * @see #applyBeanPostProcessorsBeforeInitialization
	 * @see #applyBeanPostProcessorsAfterInitialization
	 */
	Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Autowire the bean properties of the given bean instance by name or type.
	 * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply
	 * after-instantiation callbacks (e.g. for annotation-driven injection).
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the configuration of the instance.
	 * @param existingBean the existing bean instance
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for object
	 * references in the bean instance
	 * @throws BeansException if wiring failed
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_NO
	 */
	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
			throws BeansException;

	/**
	 * Apply the property values of the bean definition with the given name to
	 * the given bean instance. The bean definition can either define a fully
	 * self-contained bean, reusing its property values, or just property values
	 * meant to be used for existing bean instances.
	 * <p>This method does <i>not</i> autowire bean properties; it just applies
	 * explicitly defined property values. Use the {@link #autowireBeanProperties}
	 * method to autowire an existing bean instance.
	 * <b>Note: This method requires a bean definition for the given name!</b>
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the configuration of the instance.
	 * 将具有给定名称的Bean定义的属性值应用于
	 * 给定的bean实例。 Bean定义可以完全定义
	 * 自包含的Bean，重用其属性值，或仅重用属性值
	 * 旨在用于现有的bean实例。
	 * <p>此方法不会<i>自动</ i>自动装配bean属性； 它只是适用
	 * 明确定义的属性值。 使用{@link #autowireBeanProperties}
	 * 自动装配现有bean实例的方法。
	 * <b>注意：此方法需要给定名称的bean定义！</ b>
	 * <p> <i>不</ i>是否应用标准的{@link BeanPostProcessor BeanPostProcessors}
	 * 回调或执行Bean的任何进一步初始化。 这个介面
	 * 为此提供了独特的细粒度操作，例如
	 * {@link #initializeBean}。 但是，{ @link InstantiationAwareBeanPostProcessor}
	 * 如果适用于实例的配置，则将应用回调。
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean definition in the bean factory
	 * (a bean definition of that name has to be available)
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
	 * if there is no bean definition with the given name
	 * @throws BeansException if applying the property values failed
	 * @see #autowireBeanProperties
	 */
	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	/**
	 * Initialize the given raw bean, applying factory callbacks
	 * such as {@code setBeanName} and {@code setBeanFactory},
	 * also applying all bean post processors (including ones which
	 * might wrap the given raw bean).
	 * <p>Note that no bean definition of the given name has to exist
	 * in the bean factory. The passed-in bean name will simply be used
	 * for callbacks but not checked against the registered bean definitions.
	 * 初始化给定的原始bean，应用工厂回调
	 * 例如{@code setBeanName}和{@code setBeanFactory}，
	 * 还应用所有bean后处理器（包括那些
	 * 可能会包装给定的生豆）。
	 * <p>请注意，不必存在给定名称的bean定义
	 * 在豆子工厂。 传入的bean名称将被简单地使用
	 * 用于回调，但不对照已注册的bean定义进行检查。
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary
	 * (only passed to {@link BeanPostProcessor BeanPostProcessors})
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if the initialization failed
	 */
	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	/**
	 * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
	 * instance, invoking their {@code postProcessBeforeInitialization} methods.
	 * The returned bean instance may be a wrapper around the original.
	 * 将{@link BeanPostProcessor BeanPostProcessors}应用于给定的现有bean实例，调用其{@code postProcessBeforeInitialization}方法。
	 * 返回的bean实例可能是原始实例的包装。
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary
	 * (only passed to {@link BeanPostProcessor BeanPostProcessors})
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if any post-processing failed
	 * @see BeanPostProcessor#postProcessBeforeInitialization
	 */
	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
	 * instance, invoking their {@code postProcessAfterInitialization} methods.
	 * The returned bean instance may be a wrapper around the original.
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary
	 * (only passed to {@link BeanPostProcessor BeanPostProcessors})
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if any post-processing failed
	 * @see BeanPostProcessor#postProcessAfterInitialization
	 */
	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 * Destroy the given bean instance (typically coming from {@link #createBean}),
	 * applying the {@link org.springframework.beans.factory.DisposableBean} contract as well as
	 * registered {@link DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessors}.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 *
	 * 使用{@link org.springframework.beans.factory.DisposableBean}协定以及已注册的{@link DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessors}，销毁给定的bean实例（通常来自{@link #createBean}）。
	 * <p>在破坏过程中出现的任何异常都应被捕获
	 * 并记录下来，而不是传播到此方法的调用者。
	 * @param existingBean the bean instance to destroy
	 */
	void destroyBean(Object existingBean);


	//-------------------------------------------------------------------------
	// Delegate methods for resolving injection points
	//-------------------------------------------------------------------------

	/**
	 * Resolve the bean instance that uniquely matches the given object type, if any,
	 * including its bean name.
	 * <p>This is effectively a variant of {@link #getBean(Class)} which preserves the
	 * bean name of the matching instance.
	 * 解析与给定对象类型（如果有）唯一匹配的bean实例，包括其bean名称。
	 * <p>这实际上是{@link #getBean（Class）}的变体，保留了匹配实例的bean名称。
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return the bean name plus bean instance
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if the bean could not be created
	 * @since 4.3.3
	 * @see #getBean(Class)
	 */
	<T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory.
	 * 解决针对此工厂中定义的Bean的指定依赖关系。
	 * @param descriptor the descriptor for the dependency (field/method/constructor) 依赖项的描述符（字段/方法/构造函数）
	 * @param requestingBeanName the name of the bean which declares the given dependency 声明给定依赖项的bean的名称
	 * @return the resolved object, or {@code null} if none found
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if dependency resolution failed for any other reason
	 * @since 2.5
	 * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
	 */
	Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory.
	 * @param descriptor the descriptor for the dependency (field/method/constructor)
	 * @param requestingBeanName the name of the bean which declares the given dependency
	 * @param autowiredBeanNames a Set that all names of autowired beans (used for
	 * resolving the given dependency) are supposed to be added to
	 * @param typeConverter the TypeConverter to use for populating arrays and collections
	 * @return the resolved object, or {@code null} if none found
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if dependency resolution failed for any other reason
	 * @since 2.5
	 * @see DependencyDescriptor
	 */
	Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName,
			Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException;

}
