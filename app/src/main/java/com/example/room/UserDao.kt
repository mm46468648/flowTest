package com.example.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    //返回插入行 ID 的Insert DAO 方法永远不会返回 -1，因为即使存在冲突，此策略也将始终插入行
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM user")
    fun getAll(): Flow<List<User>>

}
