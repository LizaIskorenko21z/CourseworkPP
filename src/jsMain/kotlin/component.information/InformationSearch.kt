package component.information

import csstype.Color
import csstype.px
import emotion.react.css
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import web.html.HTMLInputElement
import web.html.InputType

val CInformationSearch = FC("InfoPeriod") { _: Props ->
    val startDate = useRef<HTMLInputElement>()
    val endDate = useRef<HTMLInputElement>()
    val (searchResults, setSearchResults) = useState<List<String>>()
    h1 { +"Доходы и расходы за период" }
    div {
        label { +"Начало периода: " }
        input {
            ref = startDate
            type = InputType.date
        }
    }
    div {
        label { +"Конец периода: " }
        input {
            ref = endDate
            type = InputType.date
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
            +"Поиск"
            onClick = {
                val start = startDate.current?.value
                val end = endDate.current?.value
                if (start != null && end != null) {
                    setSearchResults(listOf(start, end))
                }
            }
        }
    }
    searchResults?.let {
        CInformationContainer {
            dates = it
        }
    }
}