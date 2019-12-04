import java.io.File

fun pathLength(a: Point, path: List<Point>) =
        Distance(path.indexOfFirst { x -> x == a })


fun main() {

    val port = Point(1, 1)

    val inputLines = File("./src/day03.input").readLines()

    val wire1 = WirePath(
            inputLines[0].split(",").map { Movement.parse(it) },
            port)
    println("\n\n----------- Wire1")
    wire1.pointPath.forEach { println(it) }
    val wire2 = WirePath(
            inputLines[1].split(",").map { Movement.parse(it) },
            port)
    println("\n\n----------- Wire2")
    wire2.pointPath.forEach { println(it) }

    var minDistance = Distance(pathDistanceMax)
    println("\n\n----------- Intersections")
    (wire1.pointSet intersect wire2.pointSet).forEach { println(it) }


    (wire1.pointSet intersect wire2.pointSet)
            .forEach { intersection ->
                val d = pathLength(intersection, wire1.pointPath) + pathLength(intersection, wire2.pointPath)
                if (d < minDistance && d > Distance(0)) {
                    minDistance = d
                }
            }

    println("minDistance = $minDistance")
}
