/*
 * Copyright (C) 2012 NS Solutions Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.htmlhifive.sync.resource;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import com.htmlhifive.sync.exception.SyncException;
import com.htmlhifive.sync.resource.separated.SeparatedCommonDataSyncResource;

/**
 * アプリケーション内に存在するリソースを管理するサービスオブジェクト.<br>
 * リソースをクラス内に保持し、必要に応じて呼び出すことができます.
 *
 * @author kishigam
 */
@Service
public class SyncResourceManager {

	/**
	 * リソース検索対象のコンテキスト.
	 */
	@Resource
	private ApplicationContext context;

	/**
	 * アプリケーション内のリソースを保持するMap
	 */
	private Map<String, Class<? extends SyncResource<?>>> resourceMap;

	/**
	 * リソースごとのロックマネージャを保持するMap
	 */
	private Map<String, Class<? extends LockManager>> lockManagerMap;

	/**
	 * リソースごとの更新戦略オブジェクトを保持するMap.<br>
	 */
	private Map<String, Class<? extends UpdateStrategy>> updateStrategyMap;

	/**
	 * インスタンスを生成し、Mapフィールドのセットアップを行います.
	 */
	public SyncResourceManager() {

		this.resourceMap = new HashMap<>();
		this.lockManagerMap = new HashMap<>();
		this.updateStrategyMap = new HashMap<>();

		init();
	}

	/**
	 * クラスパス上のリソースを検索し、Mapに保持してこのクラスのインスタンスを生成します.<br>
	 * {@link SyncResource} インターフェースを実装し、{@link SyncResourceService} アノテーションを付与したクラスをリソースとします.
	 */
	private void init() {

		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(SyncResourceService.class));

		Set<BeanDefinition> candidates = scanner.findCandidateComponents("");
		for (BeanDefinition def : candidates) {

			Class<? extends SeparatedCommonDataSyncResource<?, ?>> resourceClass = selectResource(def);
			if (resourceClass == null) {
				continue;
			}

			// リソースの情報をアノテーションから取得
			SyncResourceService resourceAnnotation = resourceClass.getAnnotation(SyncResourceService.class);

			// データモデルを特定する
			String dataModelName = resourceAnnotation.syncDataModel();
			if (dataModelName == null || dataModelName.isEmpty()) {
				continue;
			}
			resourceMap.put(dataModelName, resourceClass);

			// ロックマネージャを特定する
			lockManagerMap.put(dataModelName, resourceAnnotation.lockManager());

			// 更新戦略オブジェクトを特定する
			updateStrategyMap.put(dataModelName, resourceAnnotation.updateStrategy());

		}
	}

	/**
	 * BeanDefinitionオブジェクトからリソースのクラスオブジェクトを抽出して返します.<br>
	 *
	 * @param def リソース候補のクラスのBeanDefinition
	 * @return クラスオブジェクト
	 */
	@SuppressWarnings("unchecked")
	private Class<? extends SeparatedCommonDataSyncResource<?, ?>> selectResource(BeanDefinition def) {

		try {
			// null,interface,SeparatedSyncResourceのサブタイプ以外,abstractのクラスを除外
			Class<?> found = Class.forName(def.getBeanClassName());
			if (found == null || found.isInterface() || !SeparatedCommonDataSyncResource.class.isAssignableFrom(found)
					|| Modifier.isAbstract(found.getModifiers())) {
				return null;
			} else {
				return (Class<? extends SeparatedCommonDataSyncResource<?, ?>>) found;
			}
		} catch (ClassNotFoundException e) {
			throw new SyncException("An Exception thrown by SyncResourceLocator", e);
		}
	}

	/**
	 * データモデル名から対応するリソースを返します.<br>
	 * リソースが存在しない場合、nullを返します.
	 *
	 * @param dataModelName データモデル名
	 * @return リソースクラス
	 */
	public SyncResource<?> locateSyncResource(String dataModelName) {

		Class<? extends SyncResource<?>> resourceClass = resourceMap.get(dataModelName);

		if (resourceClass == null) {
			return null;
		}

		SyncResource<?> sr = context.getBean(resourceClass);

		// LockManager,UpdateStrategyのセット
		sr.setLockManager(context.getBean(lockManagerMap.get(dataModelName)));
		sr.setUpdateStrategy(context.getBean(updateStrategyMap.get(dataModelName)));

		return sr;
	}

	/**
	 * このクラスが管理するリソースに対応する全てのデータモデル名を返します.<br>
	 *
	 * @return データモデル名のセット
	 */
	public Set<String> getAllDataModelNames() {

		return resourceMap.keySet();
	}
}
