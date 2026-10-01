package com.example.data.history

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: HistoryDao) {
    val allHistory: Flow<List<HistoryEntity>> = dao.getAllHistory()
    val recentHistory: Flow<List<HistoryEntity>> = dao.getRecentHistory()

    suspend fun addHistory(expression: String, result: String, category: String = "standard") {
        if (expression.isNotBlank() && result.isNotBlank()) {
            dao.insert(
                HistoryEntity(
                    expression = expression,
                    result = result,
                    category = category
                )
            )
        }
    }

    suspend fun deleteHistory(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
