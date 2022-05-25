package com.e.security.utils


/**Returns list of different items
 * @return List<[T]>*/
fun <T> Collection<T>.differentItems(secondCollection: Collection<T>): List<T> {
    return if (this.size > secondCollection.size) getDifferentItems(this, secondCollection)
    else getDifferentItems(secondCollection, this)

}

private fun <T> getDifferentItems(
    firstCollection: Collection<T>,
    secondCollection: Collection<T>
): List<T> {
    val al = ArrayList<T>()
    firstCollection.forEach { value ->
        if (!secondCollection.contains(value)) {
            al.add(value)
        }
    }
    return al
}





