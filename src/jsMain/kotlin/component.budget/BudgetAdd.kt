package component.budget

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
import react.useRef
import ru.altmanea.webapp.data.*
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import web.html.InputType

external interface AddBudgetProps : Props {
    var categories: List<Category>
    var addBudget: (Budget) -> Unit
}

val CAddBudget = FC<AddBudgetProps>("AddBudget") { props ->
    val startDateRef = useRef<HTMLInputElement>()
    val endDateRef = useRef<HTMLInputElement>()
    val categoryRef = useRef<HTMLSelectElement>()
    val limitRef = useRef<HTMLInputElement>()
    h2 { +"Добавление бюджета" }
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
                +"Дата начала периода: "
            }
            input {
                ref = startDateRef
                type = InputType.date
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
                +"Дата конца периода: "
            }
            input {
                ref = endDateRef
                type = InputType.date
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
                +"Категория: "
            }
            select {
                css {
                    width = 150.px
                    marginRight = 20.px
                }
                ref = categoryRef
                props.categories.filter { it.type.type == "Расход" }.forEach {
                    option {
                        +it.name
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
                +"Лимит: "
            }
            input {
                ref = limitRef
            }
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
                    startDateRef.current?.value?.let { startDate ->
                        endDateRef.current?.value?.let { endDate ->
                            categoryRef.current?.value?.let { category ->
                                limitRef.current?.value?.let { limit ->
                                    props.addBudget(
                                        Budget(
                                            id = "", startDate, endDate, category, limit.toDouble()
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
}
