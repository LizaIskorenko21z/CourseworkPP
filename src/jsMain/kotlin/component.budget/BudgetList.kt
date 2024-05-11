package component.budget

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ol
import ru.altmanea.webapp.data.Budget
import tools.formatDate

external interface BudgetListProps: Props {
    var budgets: List<Budget>
}

val CBudgetList = FC<BudgetListProps>("BudgetList") { props ->
    h2 { +"Список бюджетов" }
    ol {
        props.budgets.forEach { budget ->
            li {
                div {
                    +"Дата начала: ${formatDate(budget.startDate)}"
                }
                div {
                    +"Дата окончания: ${formatDate(budget.endDate)}"
                }
                div {
                    +"Категория: ${budget.category}"
                }
                div {
                    +"Лимит: ${budget.limit} руб."
                }
            }
        }
    }
}