import java.io.File
import java.io.FileOutputStream

// Иерархия sealed классов для команд
sealed class Command {
    abstract fun isValid(): Boolean
}

data class AddPhoneCommand(val name: String, val phone: String) : Command() {
    override fun isValid(): Boolean {
        return isValidPhoneNumber(phone)
    }
}

data class AddEmailCommand(val name: String, val email: String) : Command() {
    override fun isValid(): Boolean {
        return isValidEmail(email)
    }
}

data class ShowCommand(val name: String) : Command() {
    override fun isValid(): Boolean {
        return true // Всегда валидна
    }
}

data class FindCommand(val value: String) : Command() {
    override fun isValid(): Boolean {
        return true // Всегда валидна
    }
}

data class ExportCommand(val path: String) : Command() {
    override fun isValid(): Boolean {
        return true // Всегда валидна
    }
}

// Класс для хранения информации о человеке
data class Person(val name: String, val phones: MutableList<String> = mutableListOf(), val emails: MutableList<String> = mutableListOf())

// Телефонная книга
val phoneBook = mutableMapOf<String, Person>()

fun main() {
    while (true) {
        print("Введите команду: ")
        val command = readCommand()

        println(command) // Выводим на экран получившийся экземпляр Command

        when (command) {
            is AddPhoneCommand -> {
                if (phoneBook.containsKey(command.name)) {
                    phoneBook[command.name]?.phones?.add(command.phone)
                    println("Телефон добавлен: ${command.phone} для ${command.name}")
                } else {
                    val person = Person(command.name, mutableListOf(command.phone))
                    phoneBook[command.name] = person
                    println("Контакт создан: ${command.name} - ${command.phone}")
                }
            }
            is AddEmailCommand -> {
                if (phoneBook.containsKey(command.name)) {
                    phoneBook[command.name]?.emails?.add(command.email)
                    println("Email добавлен: ${command.email} для ${command.name}")
                } else {
                    val person = Person(command.name, emails = mutableListOf(command.email))
                    phoneBook[command.name] = person
                    println("Контакт создан: ${command.name} - ${command.email}")
                }
            }
            is ShowCommand -> {
                val person = phoneBook[command.name]
                if (person != null) {
                    println("Телефоны и email для ${command.name}:")
                    println("Телефоны: ${person.phones.joinToString(", ")}")
                    println("Email: ${person.emails.joinToString(", ")}")
                } else {
                    println("Контакт не найден.")
                }
            }
            is FindCommand -> {
                val foundPeople = phoneBook.filterValues { person ->
                    person.phones.contains(command.value) || person.emails.contains(command.value)
                }.keys
                if (foundPeople.isNotEmpty()) {
                    println("Найденные контакты для ${command.value}:")
                    foundPeople.forEach { println(it) }
                } else {
                    println("Контакты с таким номером телефона или email не найдены.")
                }
            }
            is ExportCommand -> {
                exportData(command.path)
            }

            else -> {}
        }
    }
}

// Функция для чтения команды и возвращения экземпляра Command
fun readCommand(): Command {
    val input = readLine() ?: ""
    val parts = input.split(" ")

    return when (parts[0]) {
        "exit" -> ExitCommand()
        "help" -> HelpCommand()
        "addPhone" -> {
            if (parts.size == 3) {
                AddPhoneCommand(parts[1], parts[2])
            } else {
                InvalidCommand()
            }
        }
        "addEmail" -> {
            if (parts.size == 3) {
                AddEmailCommand(parts[1], parts[2])
            } else {
                InvalidCommand()
            }
        }
        "show" -> {
            if (parts.size == 2) {
                ShowCommand(parts[1])
            } else {
                InvalidCommand()
            }
        }
        "find" -> {
            if (parts.size == 2) {
                FindCommand(parts[1])
            } else {
                InvalidCommand()
            }
        }
        "export" -> {
            if (parts.size == 2) {
                ExportCommand(parts[1])
            } else {
                InvalidCommand()
            }
        }
        else -> InvalidCommand()
    }
}

// Классы для различных типов команд
data class HelpCommand(val helpMessage: String = "Список команд:\nexit - завершение программы\nhelp - вывод справки по командам\naddPhone <Имя> <Номер телефона> - добавление номера телефона к контакту\naddEmail <Имя> <Адрес электронной почты> - добавление адреса электронной почты к контакту\nshow <Имя> - вывод информации о контакте\nfind <Телефон или email> - поиск контактов по телефону или email\nexport <Путь к файлу> - экспорт данных в JSON файл") : Command() {
    override fun isValid(): Boolean = true // Всегда валидна
}

data class InvalidCommand(val errorMessage: String = "Неверная команда. Введите 'help' для получения списка команд.") : Command() {
    override fun isValid(): Boolean = false // Всегда не валидна
}

data class ExitCommand(val exitMessage: String = "Программа завершена.") : Command() {
    override fun isValid(): Boolean = true // Всегда валидна
}

// Функции для проверки валидности номера телефона и адреса электронной почты
fun isValidPhoneNumber(phoneNumber: String): Boolean {
    return Regex("""\+\d+""").matches(phoneNumber)
}

fun isValidEmail(email: String): Boolean {
    return Regex("""[a-zA-Z]+@[a-zA-Z]+\.[a-zA-Z]+""").matches(email)
}

// Функция для экспорта данных в JSON файл
fun exportData(path: String) {
    val json = buildJsonObject {
        phoneBook.forEach { (name, person) ->
            putJsonObject(name) {
                putJsonArray("phones") {
                    person.phones.forEach { add(it) }
                }
                putJsonArray("emails") {
                    person.emails.forEach { add(it) }
                }
            }
        }
    }

    val jsonString = json.toString()

    File(path).writeText(jsonString)
    println("Данные успешно экспортированы в файл: $path")
}

// DSL для конструирования JSON
fun buildJsonObject(builder: JsonObjectBuilder.() -> Unit): JsonObject {
    val jsonObjectBuilder = JsonObjectBuilder()
    jsonObjectBuilder.builder()
    return jsonObjectBuilder.build()
}

class JsonObjectBuilder {
    private val map = LinkedHashMap<String, Any>()

    fun putJsonArray(key: String, builder: JsonArrayBuilder.() -> Unit) {
        val jsonArrayBuilder = JsonArrayBuilder()
        jsonArrayBuilder.builder()
        map[key] = jsonArrayBuilder.build()
    }

    fun putJsonObject(key: String, builder: JsonObjectBuilder.() -> Unit) {
        val jsonObjectBuilder = JsonObjectBuilder()
        jsonObjectBuilder.builder()
        map[key] = jsonObjectBuilder.build()
    }

    fun add(value: Any) {
        map[listOf(map.size).toString()] = value
    }

    fun build(): Map<String, Any> {
        return map
    }
}

class JsonArrayBuilder {
    private val list = ArrayList<Any>()

    fun add(value: Any) {
        list.add(value)
    }

    fun build(): List<Any> {
        return list
    }
}