package component.transaction

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import ru.altmanea.webapp.data.Transaction
import tools.formatDate

external interface TransactionListProps : Props {
    var transactions: List<Transaction>
}

val CTransactionList = FC<TransactionListProps>("TransactionList") { props ->
    h2 { +"Список транзакций" }
    ol {
        props.transactions.forEach { transaction ->
            li {
                div {
                    +"Тип: ${transaction.type.type}"
                }
                div {
                    +"Категория: ${transaction.category}"
                }
                div {
                    +"Дата: ${formatDate(transaction.date)}"
                }
                div {
                    +"Сумма: ${transaction.amount} руб."
                }
                div {
                    +"Описание: ${transaction.description}"
                }
            }
        }
    }
}