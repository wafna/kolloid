package pages

import API
import User
import UserWIP
import kotlinx.coroutines.launch
import mainScope
import react.FC
import react.Props
import react.dom.events.MouseEvent
import react.useEffectOnce
import react.useState
import util.*
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

private external interface RecordEditorProps : Props {
    var record: User?
    var updateRecord: (User) -> Unit
    var createRecord: (UserWIP) -> Unit
}

private val RecordEditor = FC<RecordEditorProps> { props ->

    val record = props.record
    var data by useState(record?.username ?: "")

    h.form {
        h.div {
            className = ClassName("form-group")
            val ctrlId = "the-data"
            h.label {
                +"Data"
                htmlFor = ctrlId
            }
            h.input {
                className = ClassName("form-control")
                id = ctrlId
                placeholder = "..."
                value = data
                onChange = withTargetValue { data = it }
            }
        }
        h.div {
            if (null == record) {
                h.button {
                    className = ClassName("btn btn-primary")
                    +"Create"
                    onClick = preventDefault {
                        props.createRecord(UserWIP(data))
                    }
                }
            } else {
                h.button {
                    className = classNames("btn", "btn-primary")
                    +"Update"
                    onClick = preventDefault {
                        props.updateRecord(User(record.id, data))
                    }
                }
            }
        }
    }
}

private external interface ItemCtrlProps : PropsSplat {
    var onClick: (MouseEvent<*, *>) -> Unit
}

private fun col(n: Int) = "col-lg-$n"

private val ItemCtrl = FC<ItemCtrlProps> { props ->
    h.div {
        className = ClassName(col(1))
        h.span {
            className = ClassName("clickable")
            children = props.children
            onClick = preventDefault { props.onClick(it) }
        }
    }
}

val RecordList = FC<Props> {

    var records: List<User>? by useState(null)
    var editedRecord: User? by useState(null)
    var createNew by useState(false)

    suspend fun updateList() {
        records = API.listRecords()
    }

    useEffectOnce {
        mainScope.launch {
            updateList()
        }
    }

    when (records) {
        null -> Loading
        else -> h.div {
            Container {
                Row {
                    Col {
                        scale = ColumnScale.Large
                        size = 1
                        h.small { +"delete" }
                    }
                    Col {
                        scale = ColumnScale.Large
                        size = 1
                        h.small { +"modify" }
                    }
                    Col {
                        scale = ColumnScale.Large
                        size = 5
                        h.strong { +"Id" }
                    }
                    Col {
                        scale = ColumnScale.Large
                        size = 5
                        h.strong { +"Data" }
                    }
                }
                if (records!!.isEmpty()) {
                    h.div {
                        className = ClassName("alert alert-info")
                        +"No records."
                    }
                }
                records!!.forEach { record ->
                    val id = record.id
                    h.div {
                        key = id
                        className = ClassName("row")
                        ItemCtrl {
                            +"∄"
                            onClick = preventDefault {
                                mainScope.launch {
                                    API.deleteRecord(id)
                                    updateList()
                                }
                            }
                        }
                        ItemCtrl {
                            +"∆"
                            onClick = preventDefault {
                                editedRecord = record
                            }
                        }
                        h.div {
                            className = ClassName(col(5))
                            h.pre { +record.id }
                        }
                        h.div {
                            className = ClassName(col(5))
                            h.span { +record.username }
                        }
                    }
                }
                h.div {
                    className = ClassName("row")
                    h.div {
                        className = ClassName(col(1))
                        h.button {
                            className = ClassName("btn btn-primary")
                            h.em { +"+" }
                            onClick = preventDefault {
                                createNew = true
                            }
                        }
                    }
                }
                Row {
                    if (null != editedRecord) {
                        RecordEditor {
                            record = editedRecord
                            updateRecord = { record ->
                                mainScope.launch {
                                    API.updateRecord(record)
                                    updateList()
                                    editedRecord = null
                                }
                            }
                        }
                    } else if (createNew) {
                        RecordEditor {
                            record = null
                            createRecord = { record ->
                                mainScope.launch {
                                    API.createRecord(record)
                                    updateList()
                                    createNew = false
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
