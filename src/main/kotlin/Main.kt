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
//data class Asignatura(
//    val cod: Int,
//    val nombre: String
//)

data class Nota(
    val dni: Int,
    val cod: Int,
    val nota: Int
)


fun main() {
    // Conexión a la base de datos
    val jdbcUrl = "jdbc:postgresql://localhost:5432/school"
    val connection = DriverManager.getConnection(jdbcUrl)
    // Lista de estudiantes a añadir para utilizar en el ex4 y ex5
    val listOfStudentsToAdd = mutableListOf<Alumno>()
    val student1 = Alumno(1254876, "Pedro Juan", "Calle Melià 2", "Barcelona", 698742155)
    val student2 = Alumno(6874214, "Manolo Perez", "Calle Falsa 123", "Cordoba", 698547711)
    val student3 = Alumno(3658745, "Juan Bartolo", "Calle Extraña 68", "Cornellà", 687622622)

    // Añadir los estudiantes a la lista
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
    println()
    ex6(connection)
    println()
    ex7(connection)
    println()
    ex8(connection)

    connection.close()
}


//Funcion para el ejercicio 1 y sirve para imprimir la lista de estudiantes.
fun ex1(connection: Connection) {
    val query = connection.createStatement()
    val result = query.executeQuery("SELECT * FROM alumnos")

    val students = mutableListOf<Alumno>()
    //SELECT de todos los estudiantes
    while (result.next()) {
        val dni = result.getInt("dni")
        val apenom = result.getString("apenom")
        val direc = result.getString("direc")
        val poblacion = result.getString("pobla")
        val telef = result.getInt("telef")
        //Añadir los estudiantes a la lista
        students.add(Alumno(dni, apenom, direc, poblacion, telef))

    }
    //Imprimir la lista de estudiantes
    println("Lista de estudiantes:")
    for (i in students.indices) {
        println(students[i])
    }
}
//Funcion para el ejercicio 2 y sirve para imprimir la lista de notas.
fun ex2(connection: Connection) {
    val query = connection.createStatement()
    val result = query.executeQuery("SELECT * FROM notas")

    val grades = mutableListOf<Nota>()

    //SELECT de todas las notas
    while (result.next()) {
        val dni = result.getInt("dni")
        val cod = result.getInt("cod")
        val nota = result.getInt("nota")
    //Añadir las notas a la lista
        grades.add(Nota(dni, cod, nota))

    }
    //Imprimir la lista de notas
    println("Lista de notas: ")
    for (i in grades.indices) {
        println(grades[i])
    }
}

//Funcion para el ejercicio 3 y sirve para imprimir las notas del alumno con DNI 4448242.
fun ex3(connection: Connection) {
    val query = connection.prepareStatement("SELECT * FROM notas WHERE dni = '4448242'")
    val result = query.executeQuery()
    val grades = mutableListOf<Nota>()
    //SELECT de las notas del alumno con DNI 4448242
    while (result.next()) {
        val dni = result.getInt("dni")
        val cod = result.getInt("cod")
        val nota = result.getInt("nota")

        grades.add(Nota(dni, cod, nota))
    }
    //Imprimir las notas del alumno con DNI 4448242
    println("Lista de notas del dni 4448242: ")
    for (i in grades.indices) {
        println(grades[i])
    }
}

//Funcion para el ejercicio 4 y sirve para añadir los estudiantes de la lista previmente creada a la base de datos.
fun ex4(connection: Connection, studentsToAdd: MutableList<Alumno>) {
    val query = connection.createStatement()
    for (student in studentsToAdd) {
        val dni = student.dni
        val apenom = student.apenom
        val direc = student.direc
        val poblacion = student.poblacion
        val telef = student.telef

        val insertQuery =
            "INSERT INTO alumnos (dni, apenom, direc, pobla, telef) VALUES ('$dni', '$apenom', '$direc', '$poblacion', '$telef')"

        try {
            //INSERT de un nuevo estudiante por cada estudiante en la lista
            query.executeUpdate(insertQuery)
            // SQLState para violación de restricción única (clave duplicada)
        } catch (exception: SQLException) {
            if (exception.sqlState == "23505") {
                println("Error: El estudiante con DNI $dni ya existe en la base de datos.")
            } else {
                exception.printStackTrace()
            }
        }
    }
    //Imprimir la nueva lista de estudiantes haciendo la llamada a la función ex1
    println("Nueva lista de estudiantes: ")
    ex1(connection)
}

//Funcion para el ejercicio 5 y sirve para añadir notas de los alumnos añadidos en el ejercicio anterior a la base de datos.
fun ex5(connection: Connection, students: MutableList<Alumno>) {
    val insertQuery =
        "INSERT INTO notas (dni, cod, nota) VALUES (?, (SELECT cod FROM asignaturas WHERE nombre =   ?), ?)"

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
        } catch (e: SQLException) {
            // SQLState para violación de restricción única (clave duplicada)
            if (e.sqlState == "23505") { // Manejo de la excepción específica SQLState 23505
                println("Error: Ya existe una nota para esta combinación de alumno y asignatura.")
            } else {
                e.printStackTrace()
            }
        }
    }
    //Imprimir la nueva lista de notas haciendo la llamada a la función ex2
    ex2(connection)
}

//Funcion para el ejercicio 6 y sirve para modificar las notas de FOL y RET del alumno con nombre "Cerrato Vela, Luis"
fun ex6(connection: Connection) {
    val updateQuery =
        "UPDATE notas SET nota = ? WHERE dni = (SELECT dni FROM alumnos WHERE apenom = ?) AND cod = (SELECT cod FROM asignaturas WHERE nombre = ?)"

    val statement = connection.createStatement()

    try {
        // UPDATE de la nota para la asignatura FOL
        val updateQueryFOL =
            //Lo que hace el replaceFirst es sustituir el primer ? por el valor 9 y el segundo ? por el valor 'Cerrato Vela, Luis' y el tercer ? por el valor 'FOL'
            updateQuery.replaceFirst("?", "9").replace("?", "'Cerrato Vela, Luis'").replace("?", "'FOL'")
        statement.executeUpdate(updateQueryFOL)

        // UPDATE de la nota para la asignatura RET
        val updateQueryRET =
            //Lo que hace el replaceFirst es sustituir el primer ? por el valor 9 y el segundo ? por el valor 'Cerrato Vela, Luis' y el tercer ? por el valor 'RET'
            updateQuery.replaceFirst("?", "9").replace("?", "'Cerrato Vela, Luis'").replace("?", "'RET'")
        statement.executeUpdate(updateQueryRET)
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    //Imprimir la nueva lista de notas haciendo la llamada a la función ex2
    ex2(connection)
}

//Funcion para el ejercicio 7 y sirve para modificar el teléfono del alumno con DNI 12344345.
fun ex7(connection: Connection) {
    val updateQuery = "UPDATE alumnos SET telef = ? WHERE dni = ?"

    val statement = connection.createStatement()

    try {
        //UPDATE del teléfono del alumno con DNI 12344345
        //Lo que hace el replaceFirst es sustituir el primer ? por el valor 934885237 y el segundo ? por el valor 12344345
        val updateQueryWithValues = updateQuery.replaceFirst("?", "'934885237'").replace("?", "'12344345'")
        statement.executeUpdate(updateQueryWithValues)

    } catch (e: SQLException) {
        e.printStackTrace()
    }
    //Imprimir la nueva lista de alumnos haciendo la llamada a la función ex1
    println("Nueva lista de alumnos: ")
    ex1(connection)
}

//Funcion para el ejercicio 8 y sirve para eliminar el alumno que vive en Mostoles.
fun ex8(connection: Connection) {
    val deleteQuery = "DELETE FROM alumnos WHERE pobla = ?"
    val statement = connection.createStatement()

    try {
        //DELETE del alumno que vive en Mostoles
        //Lo que hace el replace es sustituir el ? por el valor 'Mostoles'
        val deleteQueryWithCity = deleteQuery.replace("?", "'Mostoles'")
        val rowsAffected = statement.executeUpdate(deleteQueryWithCity)

        if (rowsAffected > 0) {
            println("Se ha eliminado correctamente el alumno que vive en Mostoles.")
        } else {
            println("No se encontraron alumnos que vivan en Mostoles.")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    //Imprimir la nueva lista de alumnos haciendo la llamada a la función ex1
    println("Nueva lista de alumnos: ")
    ex1(connection)
}











