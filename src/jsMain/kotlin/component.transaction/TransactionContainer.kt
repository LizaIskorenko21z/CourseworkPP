package component.transaction

import CAddTransaction
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
import ru.altmanea.webapp.data.Category
import ru.altmanea.webapp.data.Transaction
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import kotlin.js.json

val containerTransactionList = FC("QueryTransactionList") { _: Props ->
    val queryClient = useQueryClient()
    val transactionListQueryKey = arrayOf("TransactionList").unsafeCast<QueryKey>()
    val categoryListQueryKey = arrayOf("CategoryList").unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = transactionListQueryKey,
        queryFn = {
            fetchText(
                Config.transactionPath,
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
    val infoListQueryKey = arrayOf("infoList").unsafeCast<QueryKey>()
    val queryThree = useQuery<String, QueryError, String, QueryKey>(
        queryKey = infoListQueryKey,
        queryFn = {
            fetchText(
                "${Config.transactionPath}info",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val addTransactionMutation = useMutation<HTTPResult, Any, Transaction, Any>(
        mutationFn = { transaction: Transaction ->
            fetch(
                Config.transactionPath,
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(transaction)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(transactionListQueryKey)
            }
        }
    )

    if (query.isLoading || queryTwo.isLoading || queryThree.isLoading) div { +"Loading .." }
    else if (query.isError || queryTwo.isError || queryThree.isError) div { +"Error!" }
    else {
        val transactions =
            Json.decodeFromString<List<Transaction>>(query.data ?: "")
        val categories =
            Json.decodeFromString<List<Category>>(queryTwo.data ?: "")
        div {
            css {
                marginTop = 20.px
            }
            details {
                summary { +"Добавить транзакцию" }
                CAddTransaction {
                    this.categories = categories
                    this.addTransaction = {
                        addTransactionMutation.mutateAsync(it, null)
                    }
                }
            }
            details {
                summary { +"Удалить транзакцию" }
                CRemoveTransaction { }
            }
            details {
                summary { +"Изменить описание транзакции" }
                CEditTransaction { }
            }
            CTransactionList {
                this.transactions = transactions
            }
        }
    }
}