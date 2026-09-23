TP – Adivina Quien
Realiza en java el un juego de adivinanzas donde el jugador pueda competir contra la máquina, y que tenga un modo de Maquina vs maquina en el cual se puedan presenciar todos los procesos realizados por la misma para poder acortar la búsqueda de solución por parte de ella.
Las reglas del juego son las siguientes:
Debe haber por igual para ambos jugadores 23 personajes con características distinguibles a declarar. Los personajes empiezan ordenados únicamente según su género y es la maquina quien debe disponerlos en una lista ordenada con de forma autoincremental según se agregan los personajes.
El jugador debe elegir 1 y la maquina debe poder elegir otro (la maquina no sabe, no puede acceder directamente a la variable del personaje elegido por el jugador humano)
A medida que avanza el juego (turno por turno) los usuarios pueden lanzar directamente su suposición
Filtros  aplicables:  Genero,  calvicie,  lentes,  3  colores  de  pelo  (colorado,  negro,  amarillo) El proyecto debe ser entregado con una documentación que aclare y justifique los patrones y lógicas algorítmicas utilizadas para abordar la confección de mecánicas y decisiones de diseño. El docente al evaluar el proyecto y la defensa puede preguntar sobre los algoritmos aplicados o sobre los no aplicados.


DOCUMENTACIÓN TÉCNICA – TRABAJO PRÁCTICO OBLIGATORIO
Asignatura: Diseño y Análisis de Algoritmos

Docente: López Juan Ignacio

Tema: "Adivina Quién"
Formato de entrega: El documento debe entregarse impreso el día de la defensa, junto con el código fuente en una carpeta digital (ZIP). La extensión máxima sugerida es de 10 a 15 páginas (sin contar capturas de pantalla).
Introducción:
Explicación breve de las estrategias utilizadas para completar los sistemas y subsistemas del juego.

DIAGRAMA DE CLASES (UML):
•	Estructura General del Proyecto.
•	Relaciones entre Clases (Asociación, Herencia, Dependencia).
•	Justificación del Modelo de Datos (Estructuras utilizadas: List, Map, Set).

BITÁCORA DE DESARROLLO:
Con una división basada en etapas o fechas, se debe escribir como fue el desarrollo de la app. Que herramientas usaron, si se usó o no IA y para qué. Debe haber capturas de pantalla de fragmentos de código que respondan a las estrategias algorítmicas vistas en clase, y la notación big o correspondiente a las funciones determinadas para el funcionamiento de la app. Se debe aclarar la división de trabajo que tuvo el equipo, quien hizo cada cosa y que problemas fue encontrando y como los solucionaron.
 
Justificación algorítmica de Divide y Conquista:
•	Algoritmo de Ordenamiento Inicial (Divide y Conquista).
•	Elección entre MergeSort / QuickSort.
•	Criterio de ordenamiento.
•	Estrategia de Selección del Filtro.
•	Función de Evaluación del Filtro.
•	Relación con el Árbol de Decisión del Juego.
•	Cómo se descartan los candidatos en cada turno.
•	Criterio de parada y suposición final.
•	Manejo de la Dependencia Lógica.
•	Precondiciones y validaciones implementadas.
Incluir una tabla comparativa de tiempos de ejecución (en milisegundos) entre el algoritmo de ordenamiento elegido (MergeSort/QuickSort) y un algoritmo de ordenamiento cuadrático (ej. Burbujeo o Inserción), ejecutando ambos sobre la misma lista de 23 personajes. Justificar por qué la diferencia es o no significativa para este tamaño de entrada.

Justificación de la Estrategia Greedy utilizada:
Explicar por qué eligieron esa variante de Greedy, cómo calculan el descarte y por qué es eficiente.
¿Existe algún escenario donde la estrategia Greedy elegida no sea óptima? De ser así, describirlo y explicar por qué ocurre. Si no existe, justificar por qué en este problema particular Greedy siempre encuentra la solución óptima.
Algoritmos no utilizados:
¿Qué algoritmo de los vistos hasta ahora NO usaron y por qué? Notación Big O para la mi APP:
¿Qué notación crees que es la más certera para asignarle al juego según la estructura de software que definieron? Justifica.
Reflexión sobre el TP:
Logros, dificultades y propuestas de mejora.

Bibliografía, fuentes y ayudas:
Aclare que herramientas y fuentes utilizó para llevar adelante la planificación, programación y ejecución del proyecto.
