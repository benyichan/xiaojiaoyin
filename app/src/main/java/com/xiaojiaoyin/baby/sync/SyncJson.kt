package com.xiaojiaoyin.baby.sync

import com.xiaojiaoyin.baby.data.db.entity.BabyEntity
import com.xiaojiaoyin.baby.data.db.entity.GoodItemEntity
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity
import com.xiaojiaoyin.baby.data.db.entity.SchoolStageEntity
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity
import org.json.JSONObject

object SyncJson {

    fun babyToJson(b: BabyEntity): String = JSONObject()
        .put("id", b.id)
        .put("name", b.name)
        .put("nickname", b.nickname)
        .put("gender", b.gender)
        .put("birthDateTime", b.birthDateTime)
        .put("avatarColorIndex", b.avatarColorIndex)
        .put("createdAt", b.createdAt)
        .toString()

    fun babyFromJson(o: JSONObject): BabyEntity = BabyEntity(
        id = o.optLong("id"),
        name = o.optString("name"),
        nickname = o.optString("nickname"),
        gender = o.optString("gender"),
        birthDateTime = o.optLong("birthDateTime"),
        avatarColorIndex = o.optInt("avatarColorIndex"),
        createdAt = o.optLong("createdAt")
    )

    fun recordToJson(r: RecordEntity): String = JSONObject()
        .put("id", r.id)
        .put("babyId", r.babyId)
        .put("type", r.type.name)
        .put("occurredAt", r.occurredAt)
        .put("detailJson", JSONObject(r.detailJson))
        .put("note", r.note)
        .put("createdAt", r.createdAt)
        .put("updatedAt", r.updatedAt)
        .toString()

    fun recordFromJson(o: JSONObject): RecordEntity = RecordEntity(
        id = o.optLong("id"),
        babyId = o.optLong("babyId"),
        type = runCatching { com.xiaojiaoyin.baby.data.db.entity.RecordType.valueOf(o.optString("type")) }
            .getOrDefault(com.xiaojiaoyin.baby.data.db.entity.RecordType.NODE),
        occurredAt = o.optLong("occurredAt"),
        detailJson = o.optJSONObject("detailJson")?.toString() ?: "{}",
        note = o.optString("note"),
        createdAt = o.optLong("createdAt"),
        updatedAt = o.optLong("updatedAt")
    )

    fun todoToJson(t: TodoEntity): String = JSONObject()
        .put("id", t.id)
        .put("babyId", t.babyId)
        .put("title", t.title)
        .put("timeAt", t.timeAt)
        .put("remindEnabled", t.remindEnabled)
        .put("completed", t.completed)
        .put("createdAt", t.createdAt)
        .toString()

    fun todoFromJson(o: JSONObject): TodoEntity = TodoEntity(
        id = o.optLong("id"),
        babyId = o.optLong("babyId"),
        title = o.optString("title"),
        timeAt = o.optLong("timeAt"),
        remindEnabled = o.optBoolean("remindEnabled"),
        completed = o.optBoolean("completed"),
        createdAt = o.optLong("createdAt")
    )

    fun schoolToJson(s: SchoolStageEntity): String = JSONObject()
        .put("id", s.id)
        .put("babyId", s.babyId)
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
        .toString()

    fun schoolFromJson(o: JSONObject): SchoolStageEntity = SchoolStageEntity(
        id = o.optLong("id"),
        babyId = o.optLong("babyId"),
        stageType = o.optString("stageType"),
        className = o.optString("className"),
        schoolName = o.optString("schoolName"),
        teacher = o.optString("teacher"),
        startAt = o.optString("startAt"),
        endAt = o.optString("endAt"),
        studentNo = o.optString("studentNo"),
        costYuan = o.optDouble("costYuan", 0.0),
        note = o.optString("note"),
        createdAt = o.optLong("createdAt")
    )

    fun goodToJson(g: GoodItemEntity): String = JSONObject()
        .put("id", g.id)
        .put("babyId", g.babyId)
        .put("name", g.name)
        .put("category", g.category)
        .put("priceYuan", g.priceYuan)
        .put("rating", g.rating)
        .put("note", g.note)
        .put("buyDate", g.buyDate)
        .put("createdAt", g.createdAt)
        .toString()

    fun goodFromJson(o: JSONObject): GoodItemEntity = GoodItemEntity(
        id = o.optLong("id"),
        babyId = o.optLong("babyId"),
        name = o.optString("name"),
        category = o.optString("category", "其他"),
        priceYuan = o.optDouble("priceYuan", 0.0),
        rating = o.optInt("rating"),
        note = o.optString("note"),
        buyDate = o.optLong("buyDate"),
        createdAt = o.optLong("createdAt")
    )
}
