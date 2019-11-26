/*
 * Copyright 2002-2013 the original author or authors.
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

package org.springframework.context.annotation;

/**
 * A variation of {@link ImportSelector} that runs after all {@code @Configuration} beans
 * have been processed. This type of selector can be particularly useful when the selected
 * imports are {@code @Conditional}.
 * {@link ImportSelector}的一种变体，在处理所有{@code @Configuration} bean之后运行。 当所选导入为{@code @Conditional}时，这种选择器特别有用。
 *
 * <p>Implementations can also extend the {@link org.springframework.core.Ordered}
 * interface or use the {@link org.springframework.core.annotation.Order} annotation to
 * indicate a precedence against other {@link DeferredImportSelector}s.
 * <p>实施还可以扩展{@link org.springframework.core.Ordered}接口
 * 或使用{@link org.springframework.core.annotation.Order}注释来指示相对于其他{@link DeferredImportSelector}的优先级。
 *
 * Deferred：延期
 * @author Phillip Webb
 * @since 4.0
 */
public interface DeferredImportSelector extends ImportSelector {

}
