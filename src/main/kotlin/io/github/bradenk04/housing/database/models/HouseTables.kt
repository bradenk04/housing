package io.github.bradenk04.housing.database.models

import org.jetbrains.exposed.v1.core.Table

object Houses : Table() {
    val uuid = uuid("uuid").uniqueIndex()
    val owner = uuid("owner")
    val plotSize = integer("plot_size")
    val theme = varchar("theme", 255)
}

object HouseTags : Table() {
    val house = uuid("house").references(Houses.uuid)
    val tag = varchar("tag", 255)

    override val primaryKey = PrimaryKey(house, name = "uuid")
}