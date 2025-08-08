package io.github.bradenk04.housing.database

import io.github.bradenk04.housing.HousingPlugin
import io.github.bradenk04.housing.database.repositories.HouseRepository
import io.github.bradenk04.housing.database.repositories.exposed.ExposedHouseRepository
import org.jetbrains.exposed.v1.jdbc.Database
import java.io.File

object Database {
    lateinit var houseRepository: HouseRepository

    fun init() {
        val housesDatabase = File(HousingPlugin.plugin.dataFolder, "houses.db")
        val db = Database.connect("jdbc:sqlite:${housesDatabase.absolutePath}", driver = "org.sqlite.JDBC")

        houseRepository = ExposedHouseRepository(db)
    }
}