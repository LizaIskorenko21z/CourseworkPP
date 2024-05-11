import auth.authProvider
import component.budget.containerBudgetList
import component.category.containerCategoryList
import component.information.CInformationSearch
import component.transaction.containerTransactionList
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.create
import react.createContext
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.router.Route
import react.router.Routes
import react.router.dom.HashRouter
import react.router.dom.Link
import ru.altmanea.webapp.access.Token
import ru.altmanea.webapp.access.User
import ru.altmanea.webapp.config.Config
import tanstack.query.core.QueryClient
import tanstack.query.core.QueryKey
import tanstack.react.query.QueryClientProvider
import web.dom.document

typealias  UserInfo = Pair<User, Token>?

val invalidateRepoKey = createContext<QueryKey>()
val userInfoContext = createContext<UserInfo>(null)

fun main() {
    val container = document.getElementById("root")!!
    createRoot(container).render(app.create())
}

val app = FC<Props>("App") {
    HashRouter {
        authProvider {
            QueryClientProvider {
                client = QueryClient()
                div {
                    Link {
                        css {
                            border = Border(width = 2.px, style = LineStyle.solid)
                            background = Color("#cdb4db")
                            padding = Padding(vertical = 5.px, horizontal = 10.px)
                            hover {
                                backgroundColor = Color("#2980B9") // темнее при наведении
                            }
                        }
                        +"Управление бюджетом"
                        to = Config.budgetPath
                    }
                    Link {
                        css {
                            border = Border(width = 2.px, style = LineStyle.solid)
                            background = Color("#cdb4db")
                            padding = Padding(vertical = 5.px, horizontal = 10.px)
                            hover {
                                backgroundColor = Color("#2980B9") // темнее при наведении
                            }
                        }
                        +"Добавление доходов / расходов"
                        to = Config.transactionPath
                    }
                    Link {
                        css {
                            border = Border(width = 2.px, style = LineStyle.solid)
                            background = Color("#cdb4db")
                            padding = Padding(vertical = 5.px, horizontal = 10.px)
                            hover {
                                backgroundColor = Color("#2980B9") // темнее при наведении
                            }
                        }
                        +"Просмотр за определенный период"
                        to = Config.informationPath
                    }
                    Link {
                        css {
                            border = Border(width = 2.px, style = LineStyle.solid)
                            background = Color("#cdb4db")
                            padding = Padding(vertical = 5.px, horizontal = 10.px)
                            hover {
                                backgroundColor = Color("#2980B9") // темнее при наведении
                            }
                        }
                        +"Категории доходов / расходов"
                        to = Config.categoryPath
                    }
                }
                Routes {
                    Route {
                        path = Config.budgetPath
                        element = containerBudgetList.create()
                    }
                    Route {
                        path = Config.transactionPath
                        element = containerTransactionList.create()
                    }
                    Route {
                        path = Config.categoryPath
                        element = containerCategoryList.create()
                    }
                    Route {
                        path = Config.informationPath
                        element = CInformationSearch.create()
                    }
                }
            }
        }
    }
}