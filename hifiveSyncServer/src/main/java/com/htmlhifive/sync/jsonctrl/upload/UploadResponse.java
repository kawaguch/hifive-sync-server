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
package com.htmlhifive.sync.jsonctrl.upload;

import java.util.List;

import com.htmlhifive.sync.jsonctrl.ResponseBody;

/**
 * クライアントからの上り更新リクエストに対するレスポンスデータの抽象クラス.
 *
 * @author kishigam
 */
public abstract class UploadResponse implements ResponseBody {

	/**
	 * 上り更新レスポンスの本体となるメッセージオブジェクトのリスト.
	 */
	private List<UploadResponseMessage> dataList;

	/**
	 * @return dataList
	 */
	public List<UploadResponseMessage> getDataList() {
		return dataList;
	}

	/**
	 * @param dataList セットする dataList
	 */
	protected void setDataList(List<UploadResponseMessage> dataList) {
		this.dataList = dataList;
	}
}
