package io.github.bradenk04.housing.database.repositories

import io.github.bradenk04.housing.domain.House
import java.util.UUID

interface HouseRepository {
    suspend fun getHouse(uuid: UUID): House?
    suspend fun createHouse(house: House): Boolean
    suspend fun getHouses(owner: UUID): List<House>
    suspend fun getHouses(tags: List<String>): List<House>
    suspend fun getHouses(): List<House>

    suspend fun addTag(house: UUID, tag: String)
    suspend fun removeTag(house: UUID, tag: String)
    suspend fun setPlotSize(house: UUID, plotSize: Int)
    suspend fun setOwner(house: UUID, owner: UUID)
    suspend fun setTheme(house: UUID, theme: String)
}