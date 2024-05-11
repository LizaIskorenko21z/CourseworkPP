package auth

import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState
import ru.altmanea.webapp.access.User
import web.html.InputType

typealias Username = String
typealias Password = String

external interface AuthInProps : Props {
    var signIn: (Username, Password) -> Unit
}

external interface AuthOutProps : Props {
    var user: User
    var signOff: () -> Unit
}

val CAuthIn = FC<AuthInProps>("Auth") { props ->
    var name by useState("")
    var pass by useState("")
    var showPassword by useState(false)
    div {
        css {
            backgroundColor = Color("#cdb4db")
            padding = 32.px
            borderRadius = 8.px
            display = Display.flex
            flexDirection = FlexDirection.column
            gap = 16.px
            width = 350.px
            margin = Margin(0.px, Auto.auto)
            marginTop = 100.px
            boxSizing = BoxSizing.borderBox
            position = Position.relative
            alignItems = AlignItems.center
        }
        div {
            css {
                display = Display.flex
                alignItems = AlignItems.center
                width = 100.pct
            }
            label {
                css {
                    marginRight = 8.px
                    width = 50.px
                }
                +"Логин: "
            }
            input {
                type = InputType.text
                value = name
                onChange = { name = it.target.value }
                css {
                    padding = 8.px
                    borderRadius = 4.px
                }
            }
        }
        div {
            css {
                display = Display.flex
                alignItems = AlignItems.center
                width = 100.pct
            }
            label {
                css {
                    marginRight = 8.px
                    width = 50.px
                }
                +"Пароль: "
            }
            input {
                type = if (showPassword) InputType.text else InputType.password
                value = pass
                onChange = { pass = it.target.value }
                css {
                    padding = 8.px
                    borderRadius = 4.px
                }
            }
            button {
                css {
                    marginLeft = 8.px
                    padding = 8.px
                    cursor = Cursor.pointer
                    fontSize = 10.px
                }
                +if (showPassword) "🙈" else "👁️"
                onClick = { showPassword = !showPassword }
            }
        }
        button {
            +"Войти"
            css {
                marginTop = 16.px
                width = 100.pct
                backgroundColor = Color("#3498DB")
                color = NamedColor.white
                fontWeight = FontWeight.bold
                borderRadius = 4.px
                padding = 10.px
                cursor = Cursor.pointer
                hover {
                    backgroundColor = Color("#2980B9")
                }
            }
            onClick = {
                props.signIn(name, pass)
            }
        }
    }
}

val CAuthOut = FC<AuthOutProps>("Auth") { props ->
    div {
        css {
            position = Position.absolute
            top = 0.px
            right = 0.px
        }
        +"Привет, ${props.user.username} "
        button {
            css {
                hover {
                    backgroundColor = Color("#2980B9")
                }
            }
            +"Выход"
            onClick = {
                props.signOff()
            }
        }
    }
}