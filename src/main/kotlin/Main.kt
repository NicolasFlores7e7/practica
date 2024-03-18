package org.example

import java.sql.Connection
import java.sql.DriverManager

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

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
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

        val insertQuery = "INSERT INTO alumnos (dni, apenom, direc, pobla, telef) VALUES ($dni, '$apenom', '$direc', '$poblacion', $telef)"

        query.executeUpdate(insertQuery)
    }
    println("Nueva lista de estudiantes: ")

    ex1(connection)
}




