package com.mooo.koziol.mkb2.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SharedSyncRequest(
    @SerialName("client")
    val client: Client = Client(),
    @SerialName("GET")
    val gET: GET,
) {
    @Serializable
    data class Client(
        @SerialName("enforces_layout_passwords")
        val enforcesLayoutPasswords: Int = 1,
        @SerialName("enforces_product_passwords")
        val enforcesProductPasswords: Int = 1,
        @SerialName("manages_power_responsibly")
        val managesPowerResponsibly: Int = 1,
        @SerialName("ufd")
        val ufd: Int = 1
    )

    @Serializable
    data class GET(
        @SerialName("query")
        val query: Query,
    ) {
        @Serializable
        data class Query(
            @SerialName("include_all_beta_links")
            val includeAllBetaLinks: Int = 1,
            @SerialName("include_multiframe_climbs")
            val includeMultiframeClimbs: Int = 1,
            @SerialName("include_null_climb_stats")
            val includeNullClimbStats: Int = 1,
            @SerialName("syncs")
            val syncs: Syncs,
            @SerialName("tables")
            val tables: List<String> = listOf("climbs", "climb_stats"),
        ) {
            @Serializable
            data class Syncs(
                @SerialName("shared_syncs")
                val sharedSyncs: List<SharedSync>,
            ) {
                @Serializable
                data class SharedSync(
                    @SerialName("last_synchronized_at")
                    val lastSynchronizedAt: String,
                    @SerialName("table_name")
                    val tableName: String
                )
            }
        }
    }
}