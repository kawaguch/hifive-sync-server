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
package com.htmlhifive.sync.sample.scd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.htmlhifive.sync.sample.person.PersonBean;

/**
 * 予定の情報を保持するエンティティクラス.<br>
 *
 * @author kishigam
 */
@Entity
@Table(name = "SCHEDULE")
public class ScheduleBean {

    /**
     * このエンティティのID.<br>
     */
    @Id
    private String scheduleId;

    /**
     * 予定の対象となっている人.
     */
    @ManyToMany
    @JoinTable(
            name = "SCHEDULE_PERSON", joinColumns = { @JoinColumn(name = "SCHEDULEID") },
            inverseJoinColumns = { @JoinColumn(name = "PERSONID") })
    private List<PersonBean> userBeans;

    /**
     * 予定のタイトル.
     */
    private String title;

    /**
     * この予定の種類.
     */
    private String category;

    /**
     * 予定の日付のリスト.<br/>
     */
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ScheduleDateBean> dateBeans;

    /**
     * 予定の開始時刻.<br>
     * 24時間・4桁文字列で表現することを前提としています. 予定の種類によっては、開始－終了でない単一時刻も保持します.
     */
    private String startTime;

    /**
     * 予定の終了時刻.<br>
     * 24時間・4桁文字列で表現することを前提としています.
     *
     * 開始－終了でない単一時刻の予定の場合、設定する必要はありません.
     */
    private String finishTime;

    /**
     * 予定の詳細を表す文字列.
     */
    private String detail;

    /**
     * 予定されている場所を表す文字列.
     */
    private String place;

    @SuppressWarnings("unused")
    private ScheduleBean() {
    }

    /**
     * 予定IDを指定して新規予定データを生成します.
     *
     * @param scheduleId
     *            予定ID
     */
    ScheduleBean(String scheduleId) {

        this.scheduleId = scheduleId;
    }

    /**
     * 予定IDが一致する場合同一となるよう同一性判定します.<br>
     * 他のフィールドが全て同一の予定であっても同一とはなりません.
     *
     * @see Object#equals()
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof ScheduleBean))
            return false;

        ScheduleBean otherObj = (ScheduleBean)obj;

        return new EqualsBuilder().append(this.scheduleId, otherObj.scheduleId).isEquals();
    }

    /**
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 37).append(scheduleId).hashCode();
    }

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * ユーザのリストをユーザーIDのリストとして返します.
     *
     * @return ユーザーID(文字列)のリスト
     */
    public List<String> getUserIds() {

        List<String> userIds = new ArrayList<>();

        for (PersonBean user : userBeans) {
            userIds.add(user.getId());
        }
        return userIds;
    }

    /**
     * 日付のリストを日付文字列のリストとして返します.
     *
     * @return
     */
    public List<String> getDates() {
        List<String> dateList = new ArrayList<>();
        for (ScheduleDateBean dateBean : this.dateBeans) {
            dateList.add(dateBean.getScheduleDate());
        }

        return dateList;
    }

    /**
     * 日付文字列のリストから新規予定日付オブジェクトを生成し、日付のリストを設定します.
     *
     * @param date
     *            スケジュールされた日付のリスト.
     */
    void setNewDateBeans(List<String> dates) {

        List<ScheduleDateBean> dateBeanList = new ArrayList<>();
        for (String date : dates) {
            dateBeanList.add(new ScheduleDateBean(this, date));
        }

        this.dateBeans = dateBeanList;
    }

    /**
     * 日付文字列のリストから日付のリストを更新します.
     *
     * @param date
     *            スケジュールされた日付のリスト.
     */
    void setUpdatedDateBeans(List<String> dates) {

        // DateBeanの双方向関連の更新
        Iterator<ScheduleDateBean> dateBeansItr = this.dateBeans.iterator();
        Iterator<String> dateItr = dates.iterator();

        List<ScheduleDateBean> removeList = new ArrayList<>();
        while (dateBeansItr.hasNext()) {
            if (dateItr.hasNext()) {
                ScheduleDateBean oldDateBean = dateBeansItr.next();
                String date = dateItr.next();

                oldDateBean.setScheduleDate(date);
            } else {
                // イテレーションの途中で削除せず、退避
                // iterator#removeが使えるかどうかはListの実装次第なので使用しない
                removeList.add(dateBeansItr.next());
            }
        }

        // 元より多い分は新規
        while (dateItr.hasNext()) {

            this.dateBeans.add(new ScheduleDateBean(this, dateItr.next()));
        }

        for (ScheduleDateBean removing : removeList) {
            this.dateBeans.remove(removing);
        }
    }

    /**
     * @return scheduleId
     */
    public String getScheduleId() {
        return scheduleId;
    }

    /**
     * @param scheduleId
     *            セットする scheduleId
     */
    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    /**
     * @return userBeans
     */
    public List<PersonBean> getUserBeans() {
        return userBeans;
    }

    /**
     * @param userBeans
     *            セットする userBeans
     */
    public void setUserBeans(List<PersonBean> userBeans) {
        this.userBeans = userBeans;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            セットする title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     *            セットする category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return dateBeans
     */
    public List<ScheduleDateBean> getDateBeans() {
        return dateBeans;
    }

    /**
     * @param dateBeans
     *            セットする dateBeans
     */
    public void setDateBeans(List<ScheduleDateBean> dateBeans) {
        this.dateBeans = dateBeans;
    }

    /**
     * @return startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime
     *            セットする startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return finishTime
     */
    public String getFinishTime() {
        return finishTime;
    }

    /**
     * @param finishTime
     *            セットする finishTime
     */
    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * @return detail
     */
    public String getDetail() {
        return detail;
    }

    /**
     * @param detail
     *            セットする detail
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * @return place
     */
    public String getPlace() {
        return place;
    }

    /**
     * @param place
     *            セットする place
     */
    public void setPlace(String place) {
        this.place = place;
    }
}
