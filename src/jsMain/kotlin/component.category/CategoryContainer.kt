package component.category

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
import tanstack.query.core.QueryKey
import tanstack.react.query.useMutation
import tanstack.react.query.useQuery
import tanstack.react.query.useQueryClient
import tools.HTTPResult
import tools.fetch
import tools.fetchText
import userInfoContext
import kotlin.js.json

val containerCategoryList = FC("QueryCategoryList") { _: Props ->
    val queryClient = useQueryClient()
    val categoryListQueryKey = arrayOf("CategoryList").unsafeCast<QueryKey>()
    val userInfo = useContext(userInfoContext)
    val query = useQuery<String, QueryError, String, QueryKey>(
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
    val addCategoryMutation = useMutation<HTTPResult, Any, Category, Any>(
        mutationFn = { category: Category ->
            fetch(
                Config.categoryPath,
                jso {
                    method = "POST"
                    headers = json(
                        "Content-Type" to "application/json",
                        "Authorization" to userInfo?.second?.authHeader
                    )
                    body = Json.encodeToString(category)
                }
            )
        },
        options = jso {
            onSuccess = { _: Any, _: Any, _: Any? ->
                queryClient.invalidateQueries<Any>(categoryListQueryKey)
            }
        }
    )

    if (query.isLoading) div { +"Loading .." }
    else if (query.isError) div { +"Error!" }
    else {
        val categories = Json.decodeFromString<List<Category>>(query.data ?: "")
        div {
            css {
                marginTop = 20.px
            }
            details {
                summary { +"Добавить категорию" }
                CAddCategory {
                    this.addCategory = {
                        addCategoryMutation.mutateAsync(it, null)
                    }
                }
            }
            details {
                summary { +"Удалить категорию" }
                CRemoveCategory { }
            }
            details {
                summary { +"Изменить описание категории" }
                CEditCategory { }
            }
            CCategoryList {
                this.categories = categories
            }
        }
    }
}