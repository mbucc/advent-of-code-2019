import java.io.File
import java.util.Stack

data class OrbitalPathFromCenterOfUniverse(val path: List<SpaceObject>) {
    init {
        require(path.isNotEmpty()) { "An OrbitalPathFromCenterOfUniverse cannot be empty" }
        require(path[0] == universalCenterOfMass())
        { "An OrbitalPathFromCenterOfUniverse must start at the universal center of mass" }
    }
}


fun readOrbitsFromDisk(): List<Orbit> = File("./src/day06.input")
        .readText()
        .split("\n")
        .filterNot { it.trim().isEmpty() }
        .map { Orbit.from(it) }


// Returns the root node of a fully-built orbit map.
fun buildOrbitMap(orbits: List<Orbit>): OrbitMapEntry {

    val spaceObjectToOrbiters = mapEachSpaceObjectToItsOrbiters(orbits)

    val orbitersAroundCenterOfTheUnivers =
            spaceObjectToOrbiters[universalCenterOfMass()]
            ?: error(
                    "Something is really wrong, everything revolves around the universal " +
                    "center of mass.")

    val centerOfTheUniverse = OrbitMapEntry(
            universalCenterOfMass(),
            orbitersAroundCenterOfTheUnivers.map(SpaceObject::asOrbitMapEntry))

    buildOrbitChildMap(spaceObjectToOrbiters, centerOfTheUniverse.children)

    return centerOfTheUniverse
}


// Return true if the orbit map contains the space object.
// If the destination is found then path contains the path from orbit map root to the destination.
fun orbitMapContainsSpaceObject(
        treeRoot: OrbitMapEntry,
        dest: SpaceObject,
        path: Stack<SpaceObject>): Boolean {

    if (treeRoot.value == dest) {
        return true
    }

    if (treeRoot.children == null) {
        path.pop()
        return false
    }

    for (x in treeRoot.children!!) {
        path.push(x.value)
        if (orbitMapContainsSpaceObject(x, dest, path)) {
            return true
        }
    }

    path.pop()
    return false

}


private fun findPathFromUniversalCenterOfMass(
        universe: OrbitMapEntry,
        target: SpaceObject): OrbitalPathFromCenterOfUniverse {
    val path = Stack<SpaceObject>()
    // Ouch, wicked inefficient memory use here ... for puzzle, we push over 300 trees onto stack
    path.push(universalCenterOfMass())
    val found = orbitMapContainsSpaceObject(universe, target, path)
    if (!found) {
        throw IllegalStateException("space object $target not found in orbit path rooted at ${universe.value}")
    }
    return OrbitalPathFromCenterOfUniverse(path.toList())
}

fun findDeepestSpaceObjectThatIsInBothPaths(
        path1: OrbitalPathFromCenterOfUniverse,
        path2: OrbitalPathFromCenterOfUniverse): SpaceObject {
    var i = 0
    while (path1.path[i] == path2.path[i]) {
        i++
    }
    return path1.path[i - 1]

}


// Examples:
//            [a, b, c, d, e] -> b -> 2
//              size = 5
//              index_b = 1
//              return = size - index_b - 2
//
//            [a, b, c, d, e] -> c -> 1
//              size = 5
//              index_c = 2
//              return = 1 (size - index_c - 2)
//
// So we need to subtract two to get the right answer.
fun tailLength(path : List<SpaceObject>, from : SpaceObject) =
        path.size - path.indexOfFirst { x -> x == from } - 2


fun main() {

    val orbits = readOrbitsFromDisk()
    println("orbits.size = ${orbits.size}")

    val universe = buildOrbitMap(orbits)
    println("universe = ${universe}")
    //println(universe.dump())

    val path1 = findPathFromUniversalCenterOfMass(universe, SpaceObject("YOU"))
    val path2 = findPathFromUniversalCenterOfMass(universe, SpaceObject("SAN"))


    val commonRoot = findDeepestSpaceObjectThatIsInBothPaths(path1, path2)


    println("commonRoot = $commonRoot")
//    println("")
//    path1.path.forEach { println(it) }
    println("tailLength(path1.path, commonRoot) = ${tailLength(path1.path, commonRoot)}")

//    println("")
//    path2.path.forEach { println(it) }
    println("tailLength(path2.path, commonRoot) = ${tailLength(path2.path, commonRoot)}")

    val totalPathLength =
            tailLength(path1.path, commonRoot) +
            tailLength(path2.path, commonRoot)
    println("totalPathLength = $totalPathLength")

}



