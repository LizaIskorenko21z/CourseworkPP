package component.budget

import QueryError
import csstype.px
import emotion.react.css
import js.core.jso
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.dom.html.ReactHTML.details
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.summary
import react.useContext
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Budget
import ru.altmanea.webapp.data.Category
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import kotlin.js.json

val containerBudgetList = FC("QueryBudgetList") { _: Props ->
    val queryClient = useQueryClient()
    val budgetListQueryKey = arrayOf("BudgetList").unsafeCast<QueryKey>()
    val categoryListQueryKey = arrayOf("CategoryList").unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = budgetListQueryKey,
        queryFn = {
            fetchText(
                Config.budgetPath,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val queryTwo = useQuery<String, QueryError, String, QueryKey>(
        queryKey = categoryListQueryKey,
        queryFn = {
            fetchText(
                Config.categoryPath,
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val addBudgetMutation = useMutation<HTTPResult, Any, Budget, Any>(
        mutationFn = { budget: Budget ->
            fetch(
                Config.budgetPath,
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(budget)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(budgetListQueryKey)
            }
        }
    )
    if (query.isLoading || queryTwo.isLoading) div { +"Loading .." }
    else if (query.isError || queryTwo.isError) div { +"Error!" }
    else {
        val budgets =
            Json.decodeFromString<List<Budget>>(query.data ?: "")
        val categories =
            Json.decodeFromString<List<Category>>(queryTwo.data ?: "")
        div {
            css {
                marginTop = 20.px
            }
            details {
                summary { +"Добавить бюджет" }
                CAddBudget {
                    this.categories = categories
                    this.addBudget = {
                        addBudgetMutation.mutateAsync(it, null)
                    }
                }
            }
            details {
                summary { +"Удалить бюджет" }
                CRemoveBudget { }
            }
            CBudgetList {
                this.budgets = budgets
            }
        }
    }
}