package com.mcash.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mcash.data.remote.model.FAQ
import kotlinx.coroutines.flow.Flow

@Dao
interface FAQDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFAQ(faq: FAQ)

    @Query("SELECT * FROM faqs")
    fun getFAQs(): List<FAQ>
}