package io.github.bradenk04.housing.database.repositories

import io.github.bradenk04.housing.domain.House
import java.util.UUID

interface HouseRepository {
    suspend fun getHouse(uuid: UUID): House?
    suspend fun createHouse(house: House): Boolean
}