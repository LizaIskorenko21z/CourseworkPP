package component.transaction

import QueryError
import csstype.Color
import csstype.px
import emotion.react.css
import invalidateRepoKey
import js.core.jso
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.option
import react.dom.html.ReactHTML.select
import react.dom.html.ReactHTML.textarea
import ru.altmanea.webapp.config.Config
import ru.altmanea.webapp.data.Transaction
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
import web.html.HTMLTextAreaElement
import kotlin.js.json

external interface EditTransactionSelectProps : Props {
    var startName: String
    var onNoPick: (List<Transaction>) -> Unit
}

val CEditTransactionSelect = FC<EditTransactionSelectProps>("EditTransactionSelect") { props ->
    val selectQueryKey = arrayOf("TransactionSelectEdit", props.startName).unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
        queryKey = selectQueryKey,
        queryFn = {
            fetchText(
                "${Config.transactionPath}ByStartName/${props.startName}",
                jso {
                    headers = json("Authorization" to userInfo?.second?.authHeader)
                }
            )
        }
    )
    val selectRef = useRef<HTMLSelectElement>()
    val descriptionRef = useRef<HTMLTextAreaElement>()
    val transactions: List<Transaction> = try {
        Json.decodeFromString(query.data ?: "")
    } catch (e: Throwable) {
        emptyList()
    }

    var currentDescription by useState("")

    select {
        css { height = 21.px }
        ref = selectRef
        onChange = {
            val selectedId = it.target.value
            transactions.find { transaction -> transaction.id == selectedId }?.let { transaction ->
                currentDescription = transaction.description ?: ""
            }
        }
        transactions.forEach {
            option { +"${it.category} ${it.date} ${it.amount}"; value = it.id }
        }
    }
    div {
        textarea {
            ref = descriptionRef
            value = currentDescription
            onChange = { currentDescription = it.target.value }
        }

        button {
            css { width = 150.px; marginRight = 20.px; hover { backgroundColor = Color("#2980B9") } }
            +"Изменить"
            onClick = {
                val selectedId = selectRef.current?.value
                transactions.find { it.id == selectedId }?.let { transaction ->
                    val updatedTransaction = transaction.copy(description = currentDescription)
                    props.onNoPick(listOf(updatedTransaction))
                }
            }
        }
    }
}

val CEditTransaction = FC("EditTransaction") { _: Props ->
    val queryClient = useQueryClient()
    val invalidateRepoKey = useContext(invalidateRepoKey)
    var input by useState("")
    val inputRef = useRef<HTMLInputElement>()
    val userInfo = useContext(userInfoContext)
    val editMutation = useMutation<HTTPResult, Any, Transaction, Any>(
        { transaction: Transaction ->
            fetch(
                "${Config.transactionPath}${transaction.id}",
                jso {
                    method = "PUT"
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
                queryClient.invalidateQueries<Any>(invalidateRepoKey)
            }
        }
    )
    h2 { +"Изменение описания транзакции (по категории)" }
    div {
        input {
            ref = inputRef
            list = "TransactionsHint"
            onChange = { input = it.target.value }
        }
        CEditTransactionSelect {
            startName = input
            onNoPick = {
                editMutation.mutateAsync(it[0], null)
            }
        }
    }
}