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

import com.htmlhifive.sync.jsonctrl.JsonDataConvertor;
import com.htmlhifive.sync.resource.SyncResponse;

public class UploadResponseMessageOrdinary extends UploadResponseMessage {

    /**
     * 新規登録を除く同期の成功データを表現する上り更新レスポンスメッセージを生成します.
     *
     * @param response
     *            同期レスポンスオブジェクト
     */
    public UploadResponseMessageOrdinary(SyncResponse<?> response) {

        super(
                response.getHeader().getSyncDataId(),
                JsonDataConvertor.convertSyncMethodToAction(response.getHeader().getSyncMethod()));
    }
}
