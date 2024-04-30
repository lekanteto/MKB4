package com.mooo.koziol.mkb2.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    @SerialName("PUT")
    val pUT: PUT = PUT()
) {
    @Serializable
    data class PUT(
        @SerialName("ascents")
        val ascents: List<Ascent> = listOf(),
        @SerialName("bids")
        val bids: List<Bid> = listOf(),
        @SerialName("climb_stats")
        val climbStats: List<ClimbStat> = listOf(),
        @SerialName("climbs")
        val climbs: List<Climb> = listOf(),
        @SerialName("shared_syncs")
        val sharedSyncs: List<SharedSync> = listOf(),
        @SerialName("user_syncs")
        val userSyncs: List<UserSync> = listOf(),
        @SerialName("users")
        val users: List<User> = listOf()
    ) {
        @Serializable
        data class Ascent(
            @SerialName("angle")
            val angle: Int = 0,
            @SerialName("attempt_id")
            val attemptId: Int = 0,
            @SerialName("bid_count")
            val bidCount: Int = 0,
            @SerialName("climb_uuid")
            val climbUuid: String = "",
            @SerialName("climbed_at")
            val climbedAt: String = "",
            @SerialName("comment")
            val comment: String = "",
            @SerialName("created_at")
            val createdAt: String = "",
            @SerialName("difficulty")
            val difficulty: Int = 0,
            @SerialName("is_benchmark")
            val isBenchmark: Boolean = false,
            @SerialName("is_listed")
            val isListed: Boolean = false,
            @SerialName("is_mirror")
            val isMirror: Boolean = false,
            @SerialName("quality")
            val quality: Int = 0,
            @SerialName("updated_at")
            val updatedAt: String = "",
            @SerialName("user_id")
            val userId: Int = 0,
            @SerialName("uuid")
            val uuid: String = "",
            @SerialName("wall_uuid")
            val wallUuid: String? = null
        )

        @Serializable
        data class Bid(
            @SerialName("angle")
            val angle: Int = 0,
            @SerialName("bid_count")
            val bidCount: Int = 0,
            @SerialName("climb_uuid")
            val climbUuid: String = "",
            @SerialName("climbed_at")
            val climbedAt: String = "",
            @SerialName("comment")
            val comment: String = "",
            @SerialName("created_at")
            val createdAt: String = "",
            @SerialName("is_listed")
            val isListed: Boolean = false,
            @SerialName("is_mirror")
            val isMirror: Boolean = false,
            @SerialName("updated_at")
            val updatedAt: String = "",
            @SerialName("user_id")
            val userId: Int = 0,
            @SerialName("uuid")
            val uuid: String = ""
        )

        @Serializable
        data class ClimbStat(
            @SerialName("angle")
            val angle: Int = 0,
            @SerialName("ascensionist_count")
            val ascensionistCount: Int = 0,
            @SerialName("benchmark_difficulty")
            val benchmarkDifficulty: Double? = null,
            @SerialName("climb_uuid")
            val climbUuid: String = "",
            @SerialName("created_at")
            val createdAt: String = "",
            @SerialName("difficulty_average")
            val difficultyAverage: Double? = 0.0,
            @SerialName("fa_at")
            val faAt: String? = "",
            @SerialName("fa_uid")
            val faUid: Int? = 0,
            @SerialName("fa_username")
            val faUsername: String? = "",
            @SerialName("quality_average")
            val qualityAverage: Double? = 0.0,
            @SerialName("updated_at")
            val updatedAt: String = ""
        )

        @Serializable
        data class Climb(
            @SerialName("angle")
            val angle: Int = 0,
            @SerialName("created_at")
            val createdAt: String = "",
            @SerialName("description")
            val description: String = "",
            @SerialName("edge_bottom")
            val edgeBottom: Int = 0,
            @SerialName("edge_left")
            val edgeLeft: Int = 0,
            @SerialName("edge_right")
            val edgeRight: Int = 0,
            @SerialName("edge_top")
            val edgeTop: Int = 0,
            @SerialName("frames")
            val frames: String = "",
            @SerialName("frames_count")
            val framesCount: Int = 0,
            @SerialName("frames_pace")
            val framesPace: Int = 0,
            @SerialName("hsm")
            val hsm: Int = 0,
            @SerialName("is_draft")
            val isDraft: Boolean = false,
            @SerialName("is_listed")
            val isListed: Boolean = false,
            @SerialName("layout_id")
            val layoutId: Int = 0,
            @SerialName("name")
            val name: String = "",
            @SerialName("setter_id")
            val setterId: Int = 0,
            @SerialName("setter_username")
            val setterUsername: String = "",
            @SerialName("updated_at")
            val updatedAt: String = "",
            @SerialName("uuid")
            val uuid: String = ""
        )

        @Serializable
        data class SharedSync(
            @SerialName("last_synchronized_at")
            val lastSynchronizedAt: String = "",
            @SerialName("table_name")
            val tableName: String = ""
        )

        @Serializable
        data class UserSync(
            @SerialName("last_synchronized_at")
            val lastSynchronizedAt: String = "",
            @SerialName("table_name")
            val tableName: String = "",
            @SerialName("user_id")
            val userId: Int = 0
        )

        @Serializable
        data class User(
            @SerialName("avatar_image")
            val avatarImage: String = "",
            @SerialName("banner_image")
            val bannerImage: String? = null,
            @SerialName("city")
            val city: String? = null,
            @SerialName("country")
            val country: String? = null,
            @SerialName("created_at")
            val createdAt: String = "",
            @SerialName("email_address")
            val emailAddress: String = "",
            @SerialName("height")
            val height: Int? = null,
            @SerialName("id")
            val id: Int = 0,
            @SerialName("instagram_username")
            val instagramUsername: String? = null,
            @SerialName("is_listed")
            val isListed: Boolean = false,
            @SerialName("is_public")
            val isPublic: Boolean = false,
            @SerialName("is_verified")
            val isVerified: Boolean = false,
            @SerialName("name")
            val name: String = "",
            @SerialName("permissions")
            val permissions: List<String> = listOf(),
            @SerialName("updated_at")
            val updatedAt: String = "",
            @SerialName("username")
            val username: String = "",
            @SerialName("weight")
            val weight: Int? = null,
            @SerialName("wingspan")
            val wingspan: Int? = null
        )
    }
}