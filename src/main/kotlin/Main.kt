package org.example

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

data class Alumno(
    val dni: Int,
    val apenom: String,
    val direc: String,
    val poblacion: String,
    val telef: Int
)

data class Asignatura(
    val cod: Int,
    val nombre: String
)

data class Nota(
    val dni: Int,
    val cod: Int,
    val nota: Int
)


fun main() {
    val jdbcUrl = "jdbc:postgresql://localhost:5432/school"
    val connection = DriverManager.getConnection(jdbcUrl)
    val listOfStudentsToAdd = mutableListOf<Alumno>()
    val student1 = Alumno(1254876, "Pedro Juan", "Calle Melià 2", "Barcelona", 698742155)
    val student2 = Alumno(6874214, "Manolo Perez", "Calle Falsa 123", "Cordoba", 698547711)
    val student3 = Alumno(3658745, "Juan Bartolo", "Calle Extraña 68", "Cornellà", 687622622)

    listOfStudentsToAdd.add(student1)
    listOfStudentsToAdd.add(student2)
    listOfStudentsToAdd.add(student3)

    ex1(connection)
    println()
    ex2(connection)
    println()
    ex3(connection)
    println()
    ex4(connection, listOfStudentsToAdd)
    println()
    ex5(connection, listOfStudentsToAdd)

}


fun ex1(connection: Connection) {
    val query = connection.createStatement()
    val result = query.executeQuery("SELECT * FROM alumnos")

    val students = mutableListOf<Alumno>()

    while (result.next()) {
        val dni = result.getInt("dni")
        val apenom = result.getString("apenom")
        val direc = result.getString("direc")
        val poblacion = result.getString("pobla")
        val telef = result.getInt("telef")
        students.add(Alumno(dni, apenom, direc, poblacion, telef))

    }
    println("Lista de estudiantes:")
    for (i in students.indices) {
        println(students[i])
    }
}

fun ex2(connection: Connection) {
    val query = connection.createStatement()
    val result = query.executeQuery("SELECT * FROM notas")

    val grades = mutableListOf<Nota>()

    while (result.next()) {
        val dni = result.getInt("dni")
        val cod = result.getInt("cod")
        val nota = result.getInt("nota")

        grades.add(Nota(dni, cod, nota))

    }
    println("Lista de notas: ")
    for (i in grades.indices) {
        println(grades[i])
    }
}

fun ex3(connection: Connection) {
    val query = connection.prepareStatement("SELECT * FROM notas WHERE dni = '4448242'")
    val result = query.executeQuery()
    val grades = mutableListOf<Nota>()
    while (result.next()) {
        val dni = result.getInt("dni")
        val cod = result.getInt("cod")
        val nota = result.getInt("nota")

        grades.add(Nota(dni, cod, nota))
    }
    println("Lista de notas del dni 4448242: ")
    for (i in grades.indices) {
        println(grades[i])
    }
}

fun ex4(connection: Connection, studentsToAdd: MutableList<Alumno>) {
    val query = connection.createStatement()
    for (student in studentsToAdd) {
        val dni = student.dni
        val apenom = student.apenom
        val direc = student.direc
        val poblacion = student.poblacion
        val telef = student.telef

        val insertQuery = "INSERT INTO alumnos (dni, apenom, direc, pobla, telef) VALUES ('$dni', '$apenom', '$direc', '$poblacion', '$telef')"

        try {
            query.executeUpdate(insertQuery)
        } catch (exception: SQLException) {
            if (exception.sqlState == "23505") { // SQLState para violación de restricción única (clave duplicada)
                println("Error: El estudiante con DNI $dni ya existe en la base de datos.")
            } else {
                exception.printStackTrace()
            }
        }
    }
    println("Nueva lista de estudiantes: ")
    ex1(connection)
}

fun ex5(connection: Connection, students: MutableList<Alumno>) {
    val insertQuery = "INSERT INTO notas (dni, cod, nota) VALUES (?, (SELECT cod FROM asignaturas WHERE nombre =   ?), ?)"

    val preparedStatement = connection.prepareStatement(insertQuery)

    students.forEach { student ->
        try {
            // INSERT de la nota para la asignatura FOL
            preparedStatement.setInt(1, student.dni)
            preparedStatement.setString(2, "FOL")
            preparedStatement.setInt(3, 8)
            preparedStatement.executeUpdate()

            // INSERT de la nota para la asignatura RET
            preparedStatement.setInt(1, student.dni)
            preparedStatement.setString(2, "RET")
            preparedStatement.setInt(3, 8)
            preparedStatement.executeUpdate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    ex2(connection)
}





