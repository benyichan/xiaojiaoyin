package com.xiaojiaoyin.baby.sync

import com.xiaojiaoyin.baby.data.db.entity.AnniversaryEntity
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.BabyCustomFieldEntity
import com.xiaojiaoyin.baby.data.db.entity.GoodItemEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.SchoolStageEntity
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import com.xiaojiaoyin.baby.data.db.entity.VaccinationEntity
import org.json.JSONObject

/**
 * 变更集 JSON 序列化。
 * 外键（babyId/parentId）一律以 uuid 引用传输，由 SyncManager 在应用侧
 * 映射回本机自增 id，避免两端自增 id 撞号导致数据串号。
 */
object SyncJson {

    fun babyToJson(b: BabyEntity): String = JSONObject()
        .put("uuid", b.uuid)
        .put("name", b.name)
        .put("nickname", b.nickname)
        .put("gender", b.gender)
        .put("birthDateTime", b.birthDateTime)
        .put("avatarColorIndex", b.avatarColorIndex)
        .put("createdAt", b.createdAt)
        .put("updatedAt", b.updatedAt)
        .toString()

    fun babyFromJson(o: JSONObject): BabyEntity = BabyEntity(
        uuid = o.optString("uuid", ""),
        name = o.optString("name"),
        nickname = o.optString("nickname"),
        gender = o.optString("gender"),
        birthDateTime = o.optLong("birthDateTime"),
        avatarColorIndex = o.optInt("avatarColorIndex"),
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )

    fun recordToJson(r: RecordEntity, babyUuid: String, parentUuid: String?): String = JSONObject()
        .put("uuid", r.uuid)
        .put("babyUuid", babyUuid)
        .put("type", r.type.name)
        .put("occurredAt", r.occurredAt)
        .put("detailJson", runCatching { JSONObject(r.detailJson) }.getOrDefault(JSONObject()))
        .put("note", r.note)
        .put("tags", r.tags)
        .put("parentUuid", parentUuid ?: JSONObject.NULL)
        .put("createdAt", r.createdAt)
        .put("updatedAt", r.updatedAt)
        .toString()

    fun recordFromJson(o: JSONObject): RecordEntity = RecordEntity(
        uuid = o.optString("uuid", ""),
        babyId = -1L,
        type = runCatching { com.xiaojiaoyin.baby.data.db.entity.RecordType.valueOf(o.optString("type")) }
            .getOrDefault(com.xiaojiaoyin.baby.data.db.entity.RecordType.NODE),
        occurredAt = o.optLong("occurredAt"),
        detailJson = o.optJSONObject("detailJson")?.toString() ?: "{}",
        note = o.optString("note"),
        tags = o.optString("tags"),
        parentId = null,
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )

    fun todoToJson(t: TodoEntity, babyUuid: String): String = JSONObject()
        .put("uuid", t.uuid)
        .put("babyUuid", babyUuid)
        .put("title", t.title)
        .put("timeAt", t.timeAt)
        .put("remindEnabled", t.remindEnabled)
        .put("completed", t.completed)
        .put("createdAt", t.createdAt)
        .put("updatedAt", t.updatedAt)
        .toString()

    fun todoFromJson(o: JSONObject): TodoEntity = TodoEntity(
        uuid = o.optString("uuid", ""),
        babyId = -1L,
        title = o.optString("title"),
        timeAt = o.optLong("timeAt"),
        remindEnabled = o.optBoolean("remindEnabled"),
        completed = o.optBoolean("completed"),
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )

    fun schoolToJson(s: SchoolStageEntity, babyUuid: String): String = JSONObject()
        .put("uuid", s.uuid)
        .put("babyUuid", babyUuid)
        .put("stageType", s.stageType)
        .put("className", s.className)
        .put("schoolName", s.schoolName)
        .put("teacher", s.teacher)
        .put("startAt", s.startAt)
        .put("endAt", s.endAt)
        .put("studentNo", s.studentNo)
        .put("costYuan", s.costYuan)
        .put("note", s.note)
        .put("createdAt", s.createdAt)
        .put("updatedAt", s.updatedAt)
        .toString()

    fun schoolFromJson(o: JSONObject): SchoolStageEntity = SchoolStageEntity(
        uuid = o.optString("uuid", ""),
        babyId = -1L,
        stageType = o.optString("stageType"),
        className = o.optString("className"),
        schoolName = o.optString("schoolName"),
        teacher = o.optString("teacher"),
        startAt = o.optString("startAt"),
        endAt = o.optString("endAt"),
        studentNo = o.optString("studentNo"),
        costYuan = o.optDouble("costYuan", 0.0),
        note = o.optString("note"),
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )

    fun goodToJson(g: GoodItemEntity, babyUuid: String): String = JSONObject()
        .put("uuid", g.uuid)
        .put("babyUuid", babyUuid)
        .put("name", g.name)
        .put("category", g.category)
        .put("priceYuan", g.priceYuan)
        .put("rating", g.rating)
        .put("note", g.note)
        .put("buyDate", g.buyDate)
        .put("createdAt", g.createdAt)
        .put("updatedAt", g.updatedAt)
        .toString()

    fun goodFromJson(o: JSONObject): GoodItemEntity = GoodItemEntity(
        uuid = o.optString("uuid", ""),
        babyId = -1L,
        name = o.optString("name"),
        category = o.optString("category", "其他"),
        priceYuan = o.optDouble("priceYuan", 0.0),
        rating = o.optInt("rating"),
        note = o.optString("note"),
        buyDate = o.optLong("buyDate"),
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )

    fun customFieldToJson(f: BabyCustomFieldEntity, babyUuid: String): String = JSONObject()
        .put("uuid", f.uuid)
        .put("babyUuid", babyUuid)
        .put("fieldKey", f.fieldKey)
        .put("fieldValue", f.fieldValue)
        .put("createdAt", f.createdAt)
        .put("updatedAt", f.updatedAt)
        .toString()

    fun customFieldFromJson(o: JSONObject): BabyCustomFieldEntity = BabyCustomFieldEntity(
        uuid = o.optString("uuid", ""),
        babyId = -1L,
        fieldKey = o.optString("fieldKey"),
        fieldValue = o.optString("fieldValue"),
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )

    fun anniversaryToJson(a: AnniversaryEntity, babyUuid: String): String = JSONObject()
        .put("uuid", a.uuid)
        .put("babyUuid", babyUuid)
        .put("name", a.name)
        .put("dateAt", a.dateAt)
        .put("repeatYearly", a.repeatYearly)
        .put("note", a.note)
        .put("createdAt", a.createdAt)
        .put("updatedAt", a.updatedAt)
        .toString()

    fun anniversaryFromJson(o: JSONObject): AnniversaryEntity = AnniversaryEntity(
        uuid = o.optString("uuid", ""),
        babyId = -1L,
        name = o.optString("name"),
        dateAt = o.optLong("dateAt"),
        repeatYearly = o.optBoolean("repeatYearly", true),
        note = o.optString("note"),
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )

    fun vaccineToJson(v: VaccinationEntity, babyUuid: String): String = JSONObject()
        .put("uuid", v.uuid)
        .put("babyUuid", babyUuid)
        .put("doseKey", v.doseKey)
        .put("vaccinatedAt", v.vaccinatedAt)
        .put("note", v.note)
        .put("createdAt", v.createdAt)
        .put("updatedAt", v.updatedAt)
        .toString()

    fun vaccineFromJson(o: JSONObject): VaccinationEntity = VaccinationEntity(
        uuid = o.optString("uuid", ""),
        babyId = -1L,
        doseKey = o.optString("doseKey"),
        vaccinatedAt = o.optLong("vaccinatedAt"),
        note = o.optString("note"),
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )
}
