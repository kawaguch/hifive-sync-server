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

import java.util.ArrayList;
import java.util.List;

import com.htmlhifive.sync.resource.SyncMethod;
import com.htmlhifive.sync.resource.SyncResponse;
import com.htmlhifive.sync.service.SyncUploadResult;

/**
 * 上り更新リクエストに対する同期成功時のレスポンスデータクラス.
 *
 * @author kishigam
 */
public class UploadResponseOrdinary extends UploadResponse {

	/**
	 * 上り更新結果オブジェクトからレスポンスデータを生成します.
	 *
	 * @param uploadResult 上り更新結果オブジェクト
	 */
	public UploadResponseOrdinary(SyncUploadResult uploadResult) {

		List<UploadResponseMessage> dataList = new ArrayList<>();

		for (SyncResponse<?> response : uploadResult.getResultDataSet()) {

			UploadResponseMessage message = response.getHeader().getSyncMethod() == SyncMethod.POST ? new UploadResponseMessageForNewData(
					response) : new UploadResponseMessageOrdinary(response);

			dataList.add(message);
		}

		setDataList(dataList);
	}
}
