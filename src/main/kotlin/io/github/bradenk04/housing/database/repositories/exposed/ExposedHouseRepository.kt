package io.github.bradenk04.housing.database.repositories.exposed

import io.github.bradenk04.housing.HousingPlugin
import io.github.bradenk04.housing.database.models.HouseTags
import io.github.bradenk04.housing.database.models.Houses
import io.github.bradenk04.housing.database.repositories.HouseRepository
import io.github.bradenk04.housing.domain.House
import io.github.bradenk04.housing.utils.schem.Schematic
import io.github.bradenk04.housing.utils.schem.SchematicIO
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.statements.UpsertSqlExpressionBuilder.inList
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.io.File
import java.util.UUID

class ExposedHouseRepository(
    private val database: Database
) : HouseRepository {
    companion object {
        private val PLOT_SCHEMATIC = File("${HousingPlugin.plugin.dataFolder}/template.schem")
    }
    init {
        transaction(database) {
            SchemaUtils.create(Houses, HouseTags)
        }
    }

    override suspend fun getHouse(uuid: UUID): House? = transaction(database) {
        Houses.select(Houses.uuid eq uuid).singleOrNull()?.toHouse()
    }

    override suspend fun createHouse(house: House): Boolean {
        transaction(database) {
            Houses.insert {
                it[uuid] = house.id
                it[owner] = house.owner
                it[theme] = house.theme
                it[plotSize] = house.plotSize
            }

            HouseTags.batchInsert(house.tags) { tag ->
                this[HouseTags.house] = house.id
                this[HouseTags.tag] = tag
            }
        }

        return true
    }

    override suspend fun getHouses(owner: UUID): List<House> = transaction(database) {
        Houses.select(Houses.owner eq owner).map { it.toHouse() }
    }

    override suspend fun getHouses(tags: List<String>): List<House> = transaction(database) {
        if (tags.isEmpty()) {
            return@transaction emptyList()
        }

        val tagCount = HouseTags.tag.count()

        val houseIds = HouseTags
            .select(HouseTags.house)
            .where { HouseTags.tag inList tags }
            .groupBy(HouseTags.house)
            .having { tagCount eq tags.size.toLong() }
            .map { it[HouseTags.house] }

        Houses.select(Houses.uuid inList houseIds ).map { it.toHouse() }
    }

    override suspend fun getHouses(): List<House> = transaction(database) {
        Houses.selectAll().map { it.toHouse() }
    }

    override suspend fun addTag(house: UUID, tag: String) = transaction(database) {
        val exists = HouseTags.select((HouseTags.house eq house) and (HouseTags.tag eq tag)).count() > 0
        if (!exists) {
            HouseTags.insert {
                it[HouseTags.house] = house
                it[HouseTags.tag] = tag
            }
        }
    }

    override suspend fun removeTag(house: UUID, tag: String) = transaction(database) {
        HouseTags.deleteWhere { (this.house eq house) and (this.tag eq tag) }
        return@transaction
    }

    override suspend fun setPlotSize(house: UUID, plotSize: Int) = transaction(database) {
        Houses.update({ Houses.uuid eq house }) {
            it[this.plotSize] = plotSize
        }
        return@transaction
    }

    override suspend fun setOwner(house: UUID, owner: UUID) = transaction(database) {
        Houses.update({ Houses.uuid eq house }) {
            it[this.owner] = owner
        }
        return@transaction
    }

    override suspend fun setTheme(house: UUID, theme: String) = transaction(database) {
        Houses.update({ Houses.uuid eq house }) {
            it[this.theme] = theme
        }
        return@transaction
    }

    private fun ResultRow.toHouse(): House {
        val houseId = this[Houses.uuid]
        val tags = HouseTags.select(HouseTags.house eq houseId).map { it[HouseTags.tag] }
        val theme = this[Houses.theme]
        val schematic = loadSchematic(theme, houseId)
        return House(
            id = houseId,
            theme = theme,
            data = schematic,
            owner = this[Houses.owner],
            plotSize = this[Houses.plotSize],
            tags = tags
        )
    }

    private fun loadSchematic(theme: String, id: UUID): Schematic {
        val schematicPath = "${HousingPlugin.plugin.dataFolder}/houses/${id}.schem"
        val schematicFile = File(schematicPath)

        if (!schematicFile.exists()) {
            return SchematicIO.createFromTemplate(id)
        }

        return SchematicIO.read(schematicFile)
    }
}