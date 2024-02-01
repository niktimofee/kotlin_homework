import java.util.regex.Pattern

fun main() {
    val contactList = mutableMapOf<String, String>()

    while (true) {
        print("Введите команду: ")
        val input = readLine()

        when {
            input.equals("exit", ignoreCase = true) -> {
                println("Программа завершена.")
                break
            }
            input.equals("help", ignoreCase = true) -> {
                printHelp()
            }
            input != null && input.startsWith("add") -> {
                processAddCommand(input, contactList)
            }
            else -> {
                println("Неизвестная команда. Введите 'help' для получения списка команд.")
            }
        }
    }
}

fun printHelp() {
    println("Список команд:")
    println("exit - завершение программы")
    println("help - вывод справки по командам")
    println("add <Имя> phone <Номер телефона> - добавление контакта с номером телефона")
    println("add <Имя> email <Адрес электронной почты> - добавление контакта с адресом электронной почты")
}

fun processAddCommand(input: String, contactList: MutableMap<String, String>) {
    val commandParts = input.split(" ")
    if (commandParts.size != 4) {
        println("Неверный формат команды. Используйте 'add <Имя> phone <Номер>' или 'add <Имя> email <Почта>'.")
        return
    }

    val name = commandParts[1]
    val type = commandParts[2]
    val value = commandParts[3]

    when (type) {
        "phone" -> {
            if (isValidPhoneNumber(value)) {
                contactList[name] = value
                println("Контакт добавлен: $name - $value")
            } else {
                println("Ошибка: Неверный формат номера телефона.")
            }
        }
        "email" -> {
            if (isValidEmail(value)) {
                contactList[name] = value
                println("Контакт добавлен: $name - $value")
            } else {
                println("Ошибка: Неверный формат адреса электронной почты.")
            }
        }
        else -> {
            println("Неверный тип контакта. Используйте 'phone' или 'email'.")
        }
    }
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val pattern = Pattern.compile("\\+\\d+")
    val matcher = pattern.matcher(phoneNumber)
    return matcher.matches()
}

fun isValidEmail(email: String): Boolean {
    val pattern = Pattern.compile("[a-zA-Z]+@[a-zA-Z]+\\.[a-zA-Z]+")
    val matcher = pattern.matcher(email)
    return matcher.matches()
}