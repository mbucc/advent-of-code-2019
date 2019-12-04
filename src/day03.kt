import java.io.File


// Sanity check point coordinates and distances with this upper bound.
const val gridMax = 20000


// Origin is lower-left corner of grid.
enum class Direction { U, D, R, L }

// Distance is how far a wire travels before changing direction.
data class Distance(val value: Int) {
    init {
        require(value >= 0) { "distance $value too small" }
        require(value <= 2 * gridMax) { "distance $value too big" }
    }

    companion object {
        @JvmStatic
        fun parse(x: String) = Distance(x.toInt())
    }

    operator fun compareTo(other: Distance): Int = this.value.compareTo(other.value)


}

// A Movement combines a Direction and a Distance.
data class Movement(val direction: Direction, val distance: Distance) {
    companion object {
        @JvmStatic
        fun parse(x: String) =
                Movement(
                        Direction.valueOf(x.substring(0, 1)),
                        Distance.parse(x.substring(1)))
    }

    fun points(startAt: Point): List<Point> {
        val transformer: (Point) -> Point =
                when (this.direction) {
                    Direction.U -> {
                        { Point(it.x, it.y + 1) }
                    }
                    Direction.D -> {
                        { Point(it.x, it.y - 1) }
                    }
                    Direction.L -> {
                        { Point(it.x - 1, it.y) }
                    }
                    Direction.R -> {
                        { Point(it.x + 1, it.y) }
                    }
                }
        return generateSequence(
                // Sequence seed is included in the output, so start with seed moved one spot along.
                transformer(startAt),
                transformer).take(distance.value).toList()
    }
}

// Represents one point on the grid.  Both x and y can be negative.
data class Point(val x: Int, val y: Int) {
    init {
        require(x > -gridMax) { "x $x too small" }
        require(x < gridMax) { "x $x too big" }
        require(y > -gridMax) { "y $y too small" }
        require(y < gridMax) { "y $y too big" }
    }
}

// A WirePath represents the path a wire covers.
data class WirePath(val movements: List<Movement>, val startingAt: Point) {

    val pointPath: List<Point>

    init {
        val xs = mutableListOf(startingAt)
        this.movements.forEach { x: Movement -> xs.addAll(x.points(xs.last())) }
        pointPath = xs.toList()
    }

    val pointSet: Set<Point>
        get() = this.pointPath.toSet()
}

fun distance(a: Point, b: Point) = Distance(kotlin.math.abs(a.x - b.x) + kotlin.math.abs(a.y - b.y))


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

    var minDistance = Distance(gridMax + gridMax)
    println("\n\n----------- Intersections")
    (wire1.pointSet intersect wire2.pointSet).forEach { println(it) }


    (wire1.pointSet intersect wire2.pointSet)
            .forEach { point ->
                val d = distance(port, point)
                if (d < minDistance && d > Distance(0)) {
                    minDistance = d
                }
            }

    println("minDistance = $minDistance")
}
