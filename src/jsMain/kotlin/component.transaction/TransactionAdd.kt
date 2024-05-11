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
import react.useState
import ru.altmanea.webapp.data.*
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import web.html.HTMLTextAreaElement
import web.html.InputType

external interface AddTransactionProps : Props {
    var categories: List<Category>
    var addTransaction: (Transaction) -> Unit
}

val CAddTransaction = FC<AddTransactionProps>("AddTransaction") { props ->
    val initialType = Type.INCOME
    val (selectedType, setSelectedType) = useState<Type?>(initialType)
    val (selectedCategory, setSelectedCategory) = useState<Category?>(null)

    val typeRef = useRef<HTMLSelectElement>()
    val categoryRef = useRef<HTMLSelectElement>()
    val dateRef = useRef<HTMLInputElement>()
    val amountRef = useRef<HTMLInputElement>()
    val descriptionRef = useRef<HTMLTextAreaElement>()

    val filteredCategories = props.categories.filter { it.type == selectedType }
    val availableTypes = Type.entries.toTypedArray()

    val handleTypeChange = { event: dynamic ->
        val newType = Type.valueOf(event.target.value)
        if (newType != selectedType) {
            setSelectedType(newType)
            setSelectedCategory(null)
        }
    }
    h2 { +"Добавление транзакции" }
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
                +"Тип транзакции: "
            }
            select {
                css {
                    width = 115.px
                }
                ref = typeRef
                value = selectedType?.name ?: ""
                onChange = handleTypeChange
                availableTypes.map {
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
                +"Категория: "
            }
            select {
                css {
                    width = 114.px
                }
                ref = categoryRef
                value = selectedCategory?.name ?: ""
                onChange = { event ->
                    val category = props.categories.find { it.name == event.target.value }
                    setSelectedCategory(category)
                }
                filteredCategories.forEach {
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
                +"Дата транзакции: "
            }
            input {
                ref = dateRef
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
                +"Сумма транзакции: "
            }
            input {
                ref = amountRef
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
                +"Описание транзакции: "
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
                        categoryRef.current?.value?.let { category ->
                            dateRef.current?.value?.let { date ->
                                amountRef.current?.value?.let { amount ->
                                    descriptionRef.current?.value?.let { description ->
                                        props.addTransaction(
                                            Transaction(
                                                id = "" , Type.valueOf(type), category,
                                                date, amount.toDouble(), description
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
}
