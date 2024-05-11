package component.category

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import ru.altmanea.webapp.data.Category

external interface CategoryListProps : Props {
    var categories: List<Category>
}

val CCategoryList = FC<CategoryListProps>("CategoryList") { props ->
    h2 { +"Список категорий" }
    ol {
        props.categories.forEach { category ->
            li {
                div {
                    +"Тип: ${category.type.type}"
                }
                div {
                    +"Название: ${category.name}"
                }
                div {
                    +"Описание: ${category.description}"
                }
            }
        }
    }
}