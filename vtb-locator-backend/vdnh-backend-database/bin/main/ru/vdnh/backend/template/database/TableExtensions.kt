package ru.vdnh.backend.template.database

import ru.vdnh.backend.template.pagination.FilterField
import ru.vdnh.backend.template.pagination.FilterJoinOperation
import ru.vdnh.backend.template.pagination.SortField
import ru.vdnh.backend.template.pagination.SortFieldOrder
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.compoundOr
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

fun <T> Column<*>.asType(): Column<T> {
    @Suppress("UNCHECKED_CAST")
    return this as Column<T>
}

/**
 * Создает запрос из списка пользовательских фильтров, используя AND в качестве выражения соединения
 */
fun Table.fromFilters(filters: List<FilterField>, joinOperation: FilterJoinOperation = FilterJoinOperation.AND): Query {
    val filtersOperations = this.createFilters(filters)
    return if (filtersOperations.isEmpty()) {
        this.selectAll()
    } else {
        this.select {
            when (joinOperation) {
                FilterJoinOperation.AND -> filtersOperations.compoundAnd()
                FilterJoinOperation.OR -> filtersOperations.compoundOr()
            }
        }
    }
}

/**
 * Создает список операций для базы данных из списка пользовательских фильтров.
 * Использует columnType.valueFromDB() для получения базового типа данных столбца.
 */
fun Table.createFilters(filters: List<FilterField>): List<Op<Boolean>> {
    return filters.map { filterField ->
        val column: Column<*> = this.columns.single { it.name == filterField.field }
        val valueFromDB = column.columnType.valueFromDB(filterField.values.first()).let {
            when (it) {
                is EntityID<*> -> it.value
                else -> it
            }
        }

        // Support to more types can be added as needed bellow
        when (valueFromDB) {
            is Long -> column.asType<Long>().let {
                filterField.values.map { value -> Op.build { it.eq(value.toLong()) } }.compoundOr()
            }
            is Int -> column.asType<Int>().let {
                filterField.values.map { value -> Op.build { it.eq(value.toInt()) } }.compoundOr()
            }
            is String -> column.asType<String>().let {
                filterField.values.map { value -> Op.build { it.eq(value) } }.compoundOr()
            }
            is Boolean -> column.asType<Boolean>().let {
                filterField.values.map { value -> Op.build { it.eq(value.toBoolean()) } }.compoundOr()
            }
            is UUID -> column.asType<UUID>().let {
                filterField.values.map { value -> Op.build { it.eq(UUID.fromString(value)) } }.compoundOr()
            }
            else -> throw NotImplementedError("Column ${column.columnType} is not implemented")
        }
    }
}

/**
 * Создает список упорядоченных выражений из списка пользовательских сортировок
 */
fun Table.createSorts(sorts: List<SortField>): List<Pair<Expression<*>, SortOrder>> {
    return sorts.map { sortField ->
        Pair(
            this.columns.single { it.name == sortField.field },
            if (sortField.order == SortFieldOrder.asc) SortOrder.ASC else SortOrder.DESC
        )
    }
}
