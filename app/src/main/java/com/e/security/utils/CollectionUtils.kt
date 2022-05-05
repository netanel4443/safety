package com.e.security.utils


/**Returns list of different items
 * @return List<[T]>*/
fun <T> Collection<T>.differentItems(secondCollection: Collection<T>): List<T> {
    return if (this.size > secondCollection.size) getDifferentItems(this, secondCollection)
    else getDifferentItems(secondCollection, this)

}

private fun <T> getDifferentItems(
    biggerCollection: Collection<T>,
    smallerCollection: Collection<T>
): List<T> {
    val al = ArrayList<T>()
    biggerCollection.forEach { value ->
        if (!smallerCollection.contains(value)) {
            al.add(value)
        }
    }
    return al
}

fun <T> Collection<T>.oldAndNewItemsPairs(oldCollection: Collection<T>): List<Pair<T,T>> {
    return if (this.size != oldCollection.size) return ArrayList()
    else {
        val al = ArrayList<Pair<T,T>>()
        forEachIndexed { index,value ->
            val oldItem=oldCollection.elementAt(index)!!
            if (!oldItem.equals(value)) {
                al.add(oldItem to value)
            }
        }
        al
    }
}





