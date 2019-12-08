data class SpaceObject(val value: String) {
    init {
        val msg = "must be a letter or a digit: '$value'"
        require(value.length <= 3) { "A planetary ID must be a three-character string: '$value'" }
        require(value.isNotEmpty()) { "A space object ID cannot be empty" }
        for (i in value.indices) {
            require(value[i].isLetterOrDigit()) { "character $i $msg" }
        }
    }

    fun asOrbitMapEntry() = OrbitMapEntry(this, mutableListOf<OrbitMapEntry>())

}

data class Orbit(val center: SpaceObject, val inOrbit: SpaceObject) {

    companion object {
        @JvmStatic
        fun from(x: String): Orbit {
            val xs = x.split(")")
            return Orbit(
                    SpaceObject(xs[0]),
                    SpaceObject(xs[1]))
        }
    }
}

data class OrbitMapEntry(val value: SpaceObject, var children: List<OrbitMapEntry>?) {
    fun dump(depth: Int = 0): String {
        var y = ""
        repeat(depth) { y += "|  " }
        y += "+ " + this.value.value + "\n"
        if (this.children != null) {
            // TODO: remove the !! hack here.
            val xs = this.children!!.toList()
            for (x in xs) {
                y += x.dump(depth + 1)
            }
        }
        return y
    }
}


fun mapEachSpaceObjectToItsOrbiters(xs: List<Orbit>): Map<SpaceObject, List<SpaceObject>> =
        xs.groupBy(
                Orbit::center,
                Orbit::inOrbit).toMap()


fun universalCenterOfMass() = SpaceObject("COM")


// Recursively build an orbit map rooted at each orbit map entry in the given list.
fun buildOrbitChildMap(lookup: Map<SpaceObject, List<SpaceObject>>, xs: List<OrbitMapEntry>?) {

    if (xs.isNullOrEmpty()) {
        return
    }

    for (x in xs) {
        val y = lookup[x.value]
        if (y != null && y.isNotEmpty()) {
            x.children = y.map(SpaceObject::asOrbitMapEntry)
            buildOrbitChildMap(lookup, x.children)
        } else {
            x.children = null
        }
    }
}

fun countTotalOrbits(x: OrbitMapEntry, depth: Int, accum: Int): Int {
    if (x.children.isNullOrEmpty()) {
        return accum
    }

    val children = x.children!!

    var newAccum = accum

    for (child in children) {
        newAccum += depth + countTotalOrbits(child, depth + 1, accum)
    }

    val n = children.size
    return newAccum + n
}


fun main() {

    val orbits = readOrbitsFromDisk()
    println("orbits.size = ${orbits.size}")

    val centerOfTheUniverse = buildOrbitMap(orbits)

    println(centerOfTheUniverse.dump())

    val n = countTotalOrbits(centerOfTheUniverse, 0, 0)

    println("Total orbits = $n")

}



