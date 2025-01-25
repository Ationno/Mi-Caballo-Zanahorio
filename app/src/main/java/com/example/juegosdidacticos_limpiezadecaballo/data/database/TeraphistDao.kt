package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TeraphistEntity

@Dao
interface TeraphistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeraphist(teraphist: TeraphistEntity)

    @Delete
    suspend fun deleteTeraphist(teraphist: TeraphistEntity)

    @Query("SELECT * FROM teraphist_table")
    fun getAllTeraphists(): LiveData<List<TeraphistEntity>>

    @Query("SELECT COUNT(*) FROM teraphist_table")
    suspend fun getTeraphistCount(): Int
}