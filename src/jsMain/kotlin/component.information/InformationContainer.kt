package component.information

import QueryError
import component.budget.CBudgetList
import js.core.jso
import kotlinx.serialization.json.Json
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.useContext
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Budget
import tanstack.query.core.QueryKey
import tanstack.react.query.useQuery
import tools.fetchText
import userInfoContext
import kotlin.js.json

external interface InformationListProps: Props {
    var dates: List<String>
}

val CInformationContainer = FC<InformationListProps>("IncomeExpenseResults") { props ->
    val userInfo = useContext(userInfoContext)
    val transactionQueryKey = arrayOf("incomeExpenseDetails", props.dates[0], props.dates[1]).unsafeCast<QueryKey>()
    val budgetQueryKey = arrayOf("budgetDetails", props.dates[0], props.dates[1]).unsafeCast<QueryKey>()
    val transactionQuery = useQuery<Map<String,MutableMap<String, Double>>, QueryError, Map<String,MutableMap<String, Double>>, QueryKey>(
        queryKey = transactionQueryKey,
        queryFn = {
            fetchText(
                "${Config.transactionPath}infoIncomeExpenses/period/${props.dates[0]}/${props.dates[1]}",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            ).then {
                Json.decodeFromString(it)
            }
        }
    )

    val budgetQuery = useQuery<List<Budget>, QueryError, List<Budget>, QueryKey>(
        queryKey = budgetQueryKey,
        queryFn = {
            fetchText(
                "${Config.budgetPath}infoBudgets/period/${props.dates[0]}/${props.dates[1]}",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            ).then {
                Json.decodeFromString(it)
            }
        }
    )

    if (transactionQuery.isLoading || budgetQuery.isLoading) div { +"Loading .." }
    else if (transactionQuery.isError || budgetQuery.isError) div { +"Error!" }
    else {
        transactionQuery.data?.let { data ->
            data["Доходы"]?.let {
                div {
                    h3 { +"Доходы по категориям: " }
                    it.forEach {
                        div { +"${it.key}: ${it.value} руб." }
                    }
                }
            }
            data["Расходы"]?.let {
                div {
                    h3 { +"Расходы по категориям: " }
                    it.forEach {
                        div { +"${it.key}: ${it.value} руб." }
                    }
                }
            }
        }
        budgetQuery.data?.let { budgets ->
            CBudgetList { this.budgets = budgets }
        }
    }
}