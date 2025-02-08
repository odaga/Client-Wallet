package com.mcash.data.remote.model

data class RemoteHistory(
    val status_code: Int,
    val message:String?,
    val app_status:Boolean,
    val data: List<RemoteHistoryData>
)

data class RemoteHistoryData(
    val id:String?,
    val date:String?,
    val amount:String?,
    val type:Type?,
    val description:String?,
    val relatedAccount:RelatedAccount?,
    val transaction:Transaction?
)

data class Type(
    val id:String?,
    val name:String?,
    val internalName:String?
)

data class RelatedAccount(
    val id:String?,
    val type: Type?,
    val user:RelatedAccountUser?,
    val kind:String?
)

data class RelatedAccountUser(
    val id: String?,
    val display:String?
)

data class Transaction(
    val id:String?,
    val kind: String?
)