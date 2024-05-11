package component.budget

import QueryError
import csstype.Color
import csstype.px
import emotion.react.css
import invalidateRepoKey
import js.core.jso
import kotlinx.serialization.json.Json
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Budget
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import web.html.HTMLInputElement
import web.html.HTMLSelectElement
import kotlin.js.json

external interface BudgetSelectProps: Props {
    var startName: String
    var onNoPick: (String) -> Unit
}

val CRemoveBudgetSelect = FC<BudgetSelectProps>("BudgetSelect") { props ->
    val selectQueryKey = arrayOf("BudgetSelectRemove", props.startName).unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = selectQueryKey,
        queryFn = {
            fetchText(
                "${Config.budgetPath}ByStartName/${props.startName}",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val selectRef = useRef<HTMLSelectElement>()
    val budgets: List<Budget> =
        try {
            Json.decodeFromString(query.data ?: "")
        } catch (e: Throwable) {
            emptyList()
        }
    select {
        css {
            height = 21.px
        }
        ref = selectRef
        budgets.map {
            option {
                +"${it.category} ${it.startDate} ${it.endDate} ${it.limit}"
                value = it.id
            }
        }
    }
    button {
        css {
            width = 150.px
            marginRight = 20.px
            hover {
                backgroundColor = Color("#2980B9")
            }
        }
        +"Удалить"
        onClick = {
            selectRef.current?.value?.let {
                props.onNoPick(it)
            }
        }
    }
}

val CRemoveBudget = FC("RemoveBudget") { _: Props ->
    val queryClient = useQueryClient()
    val invalidateRepoKey = useContext(invalidateRepoKey)
    var input by useState("")
    val inputRef = useRef<HTMLInputElement>()
    val userInfo = useContext(userInfoContext)
    val deleteMutation = useMutation<HTTPResult, Any, String, Any>(
        { id: String ->
            fetch(
                "${Config.budgetPath}budgetDelete/$id",
                jso {
                    method = "DELETE"
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(invalidateRepoKey)
            }
        }
    )
    h2 { +"Удаление бюджета (по категории)" }
    div {
        input {
            ref = inputRef
            list = "BudgetsHint"
            onChange = { input = it.target.value }
        }
        CRemoveBudgetSelect {
            startName = input
            onNoPick = {
                deleteMutation.mutateAsync(it, null)
            }
        }
    }
}