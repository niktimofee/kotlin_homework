// Иерархия sealed классов для команд
sealed class Command {
    abstract fun isValid(): Boolean
}

data class AddCommand(val name: String, val type: String, val value: String) : Command() {
    override fun isValid(): Boolean {
        return when (type) {
            "phone" -> isValidPhoneNumber(value)
            "email" -> isValidEmail(value)
            else -> false
        }
    }
}

data class ShowCommand(val person: Person?) : Command() {
    override fun isValid(): Boolean {
        return true // Всегда валидна
    }
}

// Класс для хранения информации о человеке
data class Person(val name: String, val phone: String?, val email: String?)

fun main() {
    val contactList = mutableListOf<Person>()
    var lastAddedPerson: Person? = null // Переменная для хранения последнего добавленного контакта

    while (true) {
        print("Введите команду: ")
        val command = readCommand()

        println(command) // Выводим на экран получившийся экземпляр Command

        when (command) {
            is AddCommand -> {
                if (command.isValid()) {
                    if (command.type == "phone") {
                        lastAddedPerson = Person(command.name, command.value, null)
                    } else if (command.type == "email") {
                        lastAddedPerson = Person(command.name, null, command.value)
                    }
                    contactList.add(lastAddedPerson!!)
                } else {
                    println("Ошибка: Неверный формат ${if (command.type == "phone") "номера телефона" else "адреса электронной почты"}.")
                }
            }
            is ShowCommand -> {
                if (lastAddedPerson != null) {
                    println(lastAddedPerson)
                } else {
                    println("Not initialized")
                }
            }

            else -> {}
        }
    }
}

// Функция для чтения команды и возвращения экземпляра Command
fun readCommand(): Command {
    val input = readlnOrNull() ?: ""
    val parts = input.split(" ")

    return when (parts[0]) {
        "exit" -> ExitCommand()
        "help" -> HelpCommand()
        "add" -> {
            if (parts.size == 4) {
                AddCommand(parts[1], parts[2], parts[3])
            } else {
                InvalidCommand()
            }
        }
        "show" -> ShowCommand(null)
        else -> InvalidCommand()
    }
}

// Классы для различных типов команд
data class HelpCommand(val helpMessage: String = "Список команд:\nexit - завершение программы\nhelp - вывод справки по командам\nadd <Имя> phone <Номер телефона> - добавление контакта с номером телефона\nadd <Имя> email <Адрес электронной почты> - добавление контакта с адресом электронной почты\nshow - вывод последнего добавленного контакта") : Command() {
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