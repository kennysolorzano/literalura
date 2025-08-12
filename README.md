
# 📚 LiterAlura

Aplicación de consola desarrollada en **Java + Spring Boot** que permite interactuar con la API de [Gutendex](https://gutendex.com/) 
y gestionar una base de datos de libros y autores utilizando **PostgreSQL**.

## 🚀 Características principales

- **Buscar libro por título** en la API y guardarlo/actualizarlo en la base de datos.
- **Listar todos los libros** guardados.
- **Listar autores** registrados.
- **Listar autores vivos** en un año determinado.
- **Importar libros por idioma** desde la API (con paginación automática).
- **Contar libros por idioma** en la base de datos.
- **Top 10 libros más descargados**.
- **Top N libros por idioma**.
- **Estadísticas de descargas** (mínimo, máximo, promedio, suma, cantidad).
- **Buscar autor por nombre**.
- **Listar autores por rangos** de nacimiento y fallecimiento.

## 🛠️ Requisitos

- **Java 17** o superior
- **Maven 3.8+**
- **PostgreSQL** (versión 12 o superior)
- Conexión a internet (para consultar la API de Gutendex)

## ⚙️ Configuración

Editar el archivo `src/main/resources/application.properties` con tus credenciales de base de datos:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/literalura
spring.datasource.username=postgres
spring.datasource.password=12345
spring.jpa.hibernate.ddl-auto=update
```

## 📦 Instalación y ejecución

1. **Clonar el repositorio**

```bash
git clone https://github.com/tuusuario/literalura.git
cd literalura
```

2. **Compilar el proyecto**

```bash
mvn clean install
```

3. **Ejecutar la aplicación**

```bash
mvn spring-boot:run
```

O bien, ejecutar el JAR generado:

```bash
java -jar target/literalura-0.0.1-SNAPSHOT.jar
```

## 📋 Menú principal

Al iniciar la aplicación, se mostrará un menú interactivo:

```
1) Buscar libro por título (API → Guardar en DB)
2) Listar todos los libros
3) Listar autores
4) Listar autores vivos en un año
5) Importar libros por idioma (API, paginado)
6) Contar libros por idioma
7) Top 10 libros más descargados (DB)
8) Top N por idioma (DB)
9) Estadísticas de descargas (global / por idioma)
10) Buscar autor por nombre (DB)
11) Listar autores por rangos (nac/fall) (DB)
0) Salir
```

## 📂 Estructura del proyecto

```
src/main/java/com/literalura
├── cli                # Aplicación de consola (interfaz de usuario)
├── domain             # Entidades (Libro, Autor)
├── repository         # Repositorios JPA
├── service            # Lógica de negocio y conexión con la API
└── service/dto        # Clases para mapear respuestas de la API
```

## 🌐 API utilizada

- [Gutendex API](https://gutendex.com/) — API pública de libros de dominio público.

## 🖋 Autor

Proyecto desarrollado como parte de un ejercicio de aprendizaje de Java, Spring Boot y PostgreSQL.

---
💡 **Sugerencia**: Puedes extender este proyecto añadiendo más consultas, filtrados y reportes para enriquecer la experiencia de usuario.
