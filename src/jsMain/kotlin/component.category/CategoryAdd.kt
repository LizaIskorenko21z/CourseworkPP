package component.category

import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import react.dom.html.ReactHTML.textarea
import react.useRef
import ru.altmanea.webapp.data.*
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import web.html.HTMLTextAreaElement

external interface AddCategoryProps : Props {
    var addCategory: (Category) -> Unit
}

val CAddCategory = FC<AddCategoryProps>("AddCategory") { props ->
    val typeRef = useRef<HTMLSelectElement>()
    val nameRef = useRef<HTMLInputElement>()
    val descriptionRef = useRef<HTMLTextAreaElement>()
    val types = listOf(Type.entries[0], Type.entries[1])
    h2 { +"Добавление категории" }
    div {
        css {
            marginTop = 15.px
            display = Display.flex
            flexDirection = FlexDirection.column
        }
        div {
            css {
                display = Display.flex
                marginBottom = 10.px
                alignItems = AlignItems.center
            }
            label {
                css {
                    width = 150.px
                    marginRight = 20.px
                }
                +"Тип категории: "
            }
            select {
                css {
                    width = 115.px
                }
                ref = typeRef
                types.map {
                    option {
                        +it.type
                        value = it.name
                    }
                }
            }
        }
        div {
            css {
                display = Display.flex
                marginBottom = 10.px
                alignItems = AlignItems.center
            }
            label {
                css {
                    width = 150.px
                    marginRight = 20.px
                }
                +"Название категории: "
            }
            input {
                ref = nameRef
            }
        }
        div {
            css {
                display = Display.flex
                marginBottom = 10.px
                alignItems = AlignItems.center
            }
            label {
                css {
                    width = 150.px
                    marginRight = 20.px
                }
                +"Описание категории: "
            }
            textarea { ref = descriptionRef }
        }
        div {
            button {
                css {
                    width = 150.px
                    marginRight = 20.px
                    hover {
                        backgroundColor = Color("#2980B9")
                    }
                }
                +"Добавить"
                onClick = {
                    typeRef.current?.value?.let { type ->
                        nameRef.current?.value?.let { name ->
                            descriptionRef.current?.value?.let { description ->
                                props.addCategory(
                                    Category(
                                        id = "", Type.valueOf(type), name, description
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}