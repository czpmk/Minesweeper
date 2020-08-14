package minesweeper

class Cell {
    var clear = true
    var exposed = false
        set(value) {
            when (field) {
                true -> println("This cell is already exposed!")
                else -> {
                    if (marked) marked = false
                    field = value
                    if (!clear) boom = true
                    else numOfExposed++
                }
            }
        }
    private var marked = false
        set(value) {
            if (exposed) {
                println("Attempt to mark an exposed Cell !")
            } else {
                if (field) {
                    if (clear) badMarkers--
                    else goodMarkers--
                } else {
                    if (clear) badMarkers++
                    else goodMarkers++
                }
                field = value
            }
        }
    var hasNeighbors = false
    var neighbors = 0
        set(value) {
            if (value > 0) {
                this.hasNeighbors = true
                field = value
            }
        }
    val value: Char
        get() {
            return if (this.exposed) {
                if (!this.clear) CellValue.MINE.symbol
                else {
                    if (this.hasNeighbors) {
                        this.neighbors.toString().first()
                    } else CellValue.CLEAR.symbol
                }
            } else {
                if (this.marked) CellValue.MARKED.symbol
                else CellValue.UNEXPOSED.symbol
            }
        }

    fun mark(): Boolean {
        val previous = marked
        marked = !marked
        return marked != previous
    }

    fun expose(): Boolean {
        val previous = exposed
        exposed = true
        return exposed != previous && !boom
    }

    companion object Statistics {
        var goodMarkers = 0
        var badMarkers = 0
        var numOfExposed = 0
        var boom = false
    }
}