/*
 * Copyright 2002-2016 the original author or authors.
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

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;

/**
 * Configuration interface to be implemented by most bean factories. Provides
 * facilities to configure a bean factory, in addition to the bean factory
 * client methods in the {@link org.springframework.beans.factory.BeanFactory}
 * interface.
 * 大多数bean工厂都将实现配置接口。
 * 除了{@link org.springframework.beans.factory.BeanFactory}接口中的bean工厂客户端方法之外，还提供了用于配置bean工厂的工具。
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * code: Stick to {@link org.springframework.beans.factory.BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 * <p>此bean工厂接口不适合在常规应用程序代码中使用：出于典型需要，
 * 坚持使用{@link org.springframework.beans.factory.BeanFactory}或{@link org.springframework.beans.factory.ListableBeanFactory} 。
 * 此扩展接口仅用于允许在框架内部进行即插即用，并允许对bean工厂配置方法的特殊访问。
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * Custom scopes can be added via {@code registerScope}.
	 * @see #registerScope
	 */
	String SCOPE_SINGLETON = "singleton";

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * Custom scopes can be added via {@code registerScope}.
	 * @see #registerScope
	 */
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * Set the parent of this bean factory.
	 * <p>Note that the parent cannot be changed: It should only be set outside
	 * a constructor if it isn't available at the time of factory instantiation.
	 * @param parentBeanFactory the parent BeanFactory
	 * @throws IllegalStateException if this factory is already associated with
	 * a parent BeanFactory
	 * @see #getParentBeanFactory()
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * Set the class loader to use for loading bean classes.
	 * Default is the thread context class loader.
	 * <p>Note that this class loader will only apply to bean definitions
	 * that do not carry a resolved bean class yet. This is the case as of
	 * Spring 2.0 by default: Bean definitions only carry bean class names,
	 * to be resolved once the factory processes the bean definition.
	 *
	 * 设置类加载器以用于加载bean类。
	 * 默认值为线程上下文类加载器。
	 * <p>请注意，此类加载器仅适用于bean定义
	 * 还没有解决的bean类。 截至
	 * 默认情况下是Spring 2.0：Bean定义仅包含Bean类名称，
	 * 在工厂处理Bean定义后解决。
	 *
	 * @param beanClassLoader the class loader to use,
	 * or {@code null} to suggest the default class loader
	 */
	void setBeanClassLoader(ClassLoader beanClassLoader);

	/**
	 * Return this factory's class loader for loading bean classes.
	 */
	ClassLoader getBeanClassLoader();

	/**
	 * Specify a temporary ClassLoader to use for type matching purposes.
	 * Default is none, simply using the standard bean ClassLoader.
	 * <p>A temporary ClassLoader is usually just specified if
	 * <i>load-time weaving</i> is involved, to make sure that actual bean
	 * classes are loaded as lazily as possible. The temporary loader is
	 * then removed once the BeanFactory completes its bootstrap phase.
	 *
	 * 指定一个临时的ClassLoader以用于类型匹配。
	 *   缺省为none，仅使用标准bean ClassLoader。
	 *   <p>通常仅在以下情况下指定临时ClassLoader
	 *   要进行<i>加载时编织</ i>，以确保实际的bean
	 *   类尽可能延迟地加载。 临时装载器是
	 *   然后在BeanFactory完成其引导阶段后将其删除。
	 * @since 2.5
	 */
	void setTempClassLoader(ClassLoader tempClassLoader);

	/**
	 * Return the temporary ClassLoader to use for type matching purposes,
	 * if any.
	 * @since 2.5
	 */
	ClassLoader getTempClassLoader();

	/**
	 * Set whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes. Default is on.
	 * <p>Turn this flag off to enable hot-refreshing of bean definition objects
	 * and in particular bean classes. If this flag is off, any creation of a bean
	 * instance will re-query the bean class loader for newly resolved classes.
	 * 设置是否缓存bean元数据（例如给定的bean定义）
	 * （以合并方式）和已解析的Bean类。 默认为开。
	 * <p>关闭此标志以启用对bean定义对象的热刷新
	 * 特别是bean类。 如果关闭此标志，那么将创建任何bean
	 * 实例将重新查询Bean类加载器以获取新解析的类。
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * Return whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes.
	 */
	boolean isCacheBeanMetadata();

	/**
	 * Specify the resolution strategy for expressions in bean definition values.
	 * <p>There is no expression support active in a BeanFactory by default.
	 * An ApplicationContext will typically set a standard expression strategy
	 * here, supporting "#{...}" expressions in a Unified EL compatible style.
	 * 为bean定义值中的表达式指定解析策略。
	 * <p>默认情况下，BeanFactory中不支持任何表达式支持。
	 * 一个ApplicationContext通常会设置一个标准的表达策略
	 * 在此处，以Unified EL兼容样式支持“＃{...}”表达式。
	 * @since 3.0
	 */
	void setBeanExpressionResolver(BeanExpressionResolver resolver);

	/**
	 * Return the resolution strategy for expressions in bean definition values.
	 * @since 3.0
	 */
	BeanExpressionResolver getBeanExpressionResolver();

	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * 指定一个Spring 3.0 ConversionService来转换属性值，以替代JavaBeans PropertyEditor。
	 * @since 3.0
	 */
	void setConversionService(ConversionService conversionService);

	/**
	 * Return the associated ConversionService, if any.
	 * 返回关联的ConversionService（如果有）。
	 * @since 3.0
	 */
	ConversionService getConversionService();

	/**
	 * Add a PropertyEditorRegistrar to be applied to all bean creation processes.
	 * <p>Such a registrar creates new PropertyEditor instances and registers them
	 * on the given registry, fresh for each bean creation attempt. This avoids
	 * the need for synchronization on custom editors; hence, it is generally
	 * preferable to use this method instead of {@link #registerCustomEditor}.
	 * 添加一个PropertyEditorRegistrar以应用于所有bean创建过程。
	 * <p>这样的注册商可以创建新的PropertyEditor实例并进行注册
	 * 在给定的注册表中，每次创建bean时都是新鲜的。 这样可以避免
	 * 自定义编辑器上需要同步； 因此，通常
	 * 最好使用此方法代替{@link #registerCustomEditor}。
	 * @param registrar the PropertyEditorRegistrar to register
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	/**
	 * Register the given custom property editor for all properties of the
	 * given type. To be invoked during factory configuration.
	 * <p>Note that this method will register a shared custom editor instance;
	 * access to that instance will be synchronized for thread-safety. It is
	 * generally preferable to use {@link #addPropertyEditorRegistrar} instead
	 * of this method, to avoid for the need for synchronization on custom editors.
	 * 注册给定的自定义属性编辑器的所有属性
	 * 给定的类型。 在出厂配置期间调用。
	 * <p>请注意，此方法将注册一个共享的自定义编辑器实例；
	 * 为了线程安全，将同步对该实例的访问。 它是
	 * 通常最好使用{@link #addPropertyEditorRegistrar}代替
	 * 此方法，以避免在自定义编辑器上进行同步。
	 * @param requiredType type of the property
	 * @param propertyEditorClass the {@link PropertyEditor} class to register
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	/**
	 * Initialize the given PropertyEditorRegistry with the custom editors
	 * that have been registered with this BeanFactory.
	 * @param registry the PropertyEditorRegistry to initialize
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	/**
	 * Set a custom type converter that this BeanFactory should use for converting
	 * bean property values, constructor argument values, etc.
	 * <p>This will override the default PropertyEditor mechanism and hence make
	 * any custom editors or custom editor registrars irrelevant.
	 * @see #addPropertyEditorRegistrar
	 * @see #registerCustomEditor
	 * @since 2.5
	 */
	void setTypeConverter(TypeConverter typeConverter);

	/**
	 * Obtain a type converter as used by this BeanFactory. This may be a fresh
	 * instance for each call, since TypeConverters are usually <i>not</i> thread-safe.
	 * <p>If the default PropertyEditor mechanism is active, the returned
	 * TypeConverter will be aware of all custom editors that have been registered.
	 * 获取此BeanFactory使用的类型转换器。 这可能是每个调用的一个新实例，因为TypeConverters通常不是线程安全的。
	 * <p>如果默认的PropertyEditor机制处于活动状态，则返回的TypeConverter将知道所有已注册的自定义编辑器。
	 * @since 2.5
	 */
	TypeConverter getTypeConverter();

	/**
	 * Add a String resolver for embedded values such as annotation attributes.
	 * @param valueResolver the String resolver to apply to embedded values
	 * @since 3.0
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * Determine whether an embedded value resolver has been registered with this
	 * bean factory, to be applied through {@link #resolveEmbeddedValue(String)}.
	 * @since 4.3
	 */
	boolean hasEmbeddedValueResolver();

	/**
	 * Resolve the given embedded value, e.g. an annotation attribute.
	 * 解决给定的嵌入值，例如 注释属性。
	 * @param value the value to resolve
	 * @return the resolved value (may be the original value as-is) 解析值（可能是原始值）
	 * @since 3.0
	 */
	String resolveEmbeddedValue(String value);

	/**
	 * Add a new BeanPostProcessor that will get applied to beans created
	 * by this factory. To be invoked during factory configuration.
	 * <p>Note: Post-processors submitted here will be applied in the order of
	 * registration; any ordering semantics expressed through implementing the
	 * {@link org.springframework.core.Ordered} interface will be ignored. Note
	 * that autodetected post-processors (e.g. as beans in an ApplicationContext)
	 * will always be applied after programmatically registered ones.
	 *
	 * 添加一个新的BeanPostProcessor，它将应用于该工厂创建的bean。 在出厂配置期间调用。
	 * <p>注意：此处提交的后处理器将按照注册顺序应用； 通过实现{@link org.springframework.core.Ordered}接口表达的任何排序语义都将被忽略。
	 * 请注意，自动检测到的后处理器（例如，作为ApplicationContext中的bean）将始终以编程方式注册后的处理器。
	 *
	 * @param beanPostProcessor the post-processor to register
	 */
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	/**
	 * Return the current number of registered BeanPostProcessors, if any.
	 * 返回当前已注册的BeanPostProcessor的数量（如果有）。
	 */
	int getBeanPostProcessorCount();

	/**
	 * Register the given scope, backed by the given Scope implementation.
	 * @param scopeName the scope identifier
	 * @param scope the backing Scope implementation
	 */
	void registerScope(String scopeName, Scope scope);

	/**
	 * Return the names of all currently registered scopes.
	 * <p>This will only return the names of explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * @return the array of scope names, or an empty array if none
	 * @see #registerScope
	 */
	String[] getRegisteredScopeNames();

	/**
	 * Return the Scope implementation for the given scope name, if any.
	 * <p>This will only return explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * @param scopeName the name of the scope
	 * @return the registered Scope implementation, or {@code null} if none
	 * @see #registerScope
	 */
	Scope getRegisteredScope(String scopeName);

	/**
	 * Provides a security access control context relevant to this factory.
	 * 提供与此工厂有关的安全访问控制上下文。
	 * @return the applicable AccessControlContext (never {@code null})
	 * @since 3.0
	 */
	AccessControlContext getAccessControlContext();

	/**
	 * Copy all relevant configuration from the given other factory.
	 * <p>Should include all standard configuration settings as well as
	 * BeanPostProcessors, Scopes, and factory-specific internal settings.
	 * Should not include any metadata of actual bean definitions,
	 * such as BeanDefinition objects and bean name aliases.
	 * 从给定的其他工厂复制所有相关配置。
	 * <p>应包括所有标准配置设置以及BeanPostProcessor，范围和工厂特定的内部设置。
	 * 不应包含任何实际bean定义的元数据，例如BeanDefinition对象，并且应为名称的别名。
	 * @param otherFactory the other BeanFactory to copy from
	 */
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	/**
	 * Given a bean name, create an alias. We typically use this method to
	 * support names that are illegal within XML ids (used for bean names).
	 * <p>Typically invoked during factory configuration, but can also be
	 * used for runtime registration of aliases. Therefore, a factory
	 * implementation should synchronize alias access.
	 * @param beanName the canonical name of the target bean
	 * @param alias the alias to be registered for the bean
	 * @throws BeanDefinitionStoreException if the alias is already in use
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	/**
	 * Resolve all alias target names and aliases registered in this
	 * factory, applying the given StringValueResolver to them.
	 * <p>The value resolver may for example resolve placeholders
	 * in target bean names and even in alias names.
	 * @param valueResolver the StringValueResolver to apply
	 * @since 2.5
	 */
	void resolveAliases(StringValueResolver valueResolver);

	/**
	 * Return a merged BeanDefinition for the given bean name,
	 * merging a child bean definition with its parent if necessary.
	 * Considers bean definitions in ancestor factories as well.
	 * 返回给定bean名称的合并BeanDefinition，必要时将子bean定义与其父级合并。在祖先工厂中也考虑bean定义。
	 * @param beanName the name of the bean to retrieve the merged definition for 用于检索其合并定义的bean的名称
	 * @return a (potentially merged) BeanDefinition for the given bean 给定bean的（可能合并的）BeanDefinition
	 * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
	 * @since 2.5
	 */
	BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Determine whether the bean with the given name is a FactoryBean.
	 * 确定具有给定名称的bean是否为FactoryBean。
	 * @param name the name of the bean to check
	 * @return whether the bean is a FactoryBean
	 * ({@code false} means the bean exists but is not a FactoryBean)
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.5
	 */
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Explicitly control the current in-creation status of the specified bean.
	 * For container-internal use only.
	 * 明确控制指定bean的当前 正在创建 状态。 仅供容器内部使用。
	 * @param beanName the name of the bean
	 * @param inCreation whether the bean is currently in creation
	 * @since 3.1
	 */
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	/**
	 * Determine whether the specified bean is currently in creation.
	 * 确定指定的bean当前是否在创建中。
	 * @param beanName the name of the bean
	 * @return whether the bean is currently in creation
	 * @since 2.5
	 */
	boolean isCurrentlyInCreation(String beanName);

	/**
	 * Register a dependent bean for the given bean,
	 * to be destroyed before the given bean is destroyed.
	 * 为给定的bean注册一个从属bean，要在给定的bean被销毁之前将其销毁。
	 * @param beanName the name of the bean
	 * @param dependentBeanName the name of the dependent bean
	 * @since 2.5
	 */
	void registerDependentBean(String beanName, String dependentBeanName);

	/**
	 * Return the names of all beans which depend on the specified bean, if any.
	 * 返回依赖于指定bean的所有bean的名称（如果有）。
	 * @param beanName the name of the bean
	 * @return the array of dependent bean names, or an empty array if none
	 * @since 2.5
	 */
	String[] getDependentBeans(String beanName);

	/**
	 * Return the names of all beans that the specified bean depends on, if any.
	 * 返回指定bean依赖的所有bean的名称（如果有）。
	 *
	 * @param beanName the name of the bean
	 * @return the array of names of beans which the bean depends on,
	 * or an empty array if none
	 * 返回该bean依赖的bean的名称数组，如果没有则返回一个空数组
	 * @since 2.5
	 */
	String[] getDependenciesForBean(String beanName);

	/**
	 * Destroy the given bean instance (usually a prototype instance
	 * obtained from this factory) according to its bean definition.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * @param beanName the name of the bean definition
	 * @param beanInstance the bean instance to destroy
	 */
	void destroyBean(String beanName, Object beanInstance);

	/**
	 * Destroy the specified scoped bean in the current target scope, if any.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * @param beanName the name of the scoped bean
	 */
	void destroyScopedBean(String beanName);

	/**
	 * Destroy all singleton beans in this factory, including inner beans that have
	 * been registered as disposable. To be called on shutdown of a factory.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 */
	void destroySingletons();

}
