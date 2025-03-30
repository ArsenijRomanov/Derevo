import io.kotest.assertions.throwables.shouldThrow
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.*

class RBTForTest<K : Comparable<K>, V> : RedBlackTree<K, V>() {

    fun blackRoot(): Boolean {
        return isBlack(root)
    }

    fun noRedNodeWithRedChild(): Boolean {
        val stack = ArrayDeque<RBTNode<K, V>?>()
        stack.addLast(root)
        while (!stack.isEmpty()) {
            val curNode = stack.removeLast() ?: continue
            if (isRed(curNode) && isRed(curNode.parent)) {
                return false
            }
            stack.addLast(curNode.right)
            stack.addLast(curNode.left)
        }
        return true
    }

    private fun blackHeight(): Int {
        var curNode = root
        var cnt = 0
        while (curNode != null) {
            if (isBlack(curNode)) {
                ++cnt
            }
            curNode = curNode.left
        }
        return cnt
    }

    fun sameBlackHeight(): Boolean {
        root ?: return true
        val bh = blackHeight()
        val stack = ArrayDeque<Pair<RBTNode<K, V>?, Int>>()
        stack.addLast(root to 1)
        while (!stack.isEmpty()) {
            val curPair = stack.removeLast()
            if (curPair.first?.right == null && curPair.first?.left == null && curPair.second != bh) {
                return false
            }
            if (curPair.first?.right != null) {
                if (isBlack(curPair.first?.right)) {
                    stack.addLast(curPair.first?.right to curPair.second + 1)
                } else {
                    stack.addLast(curPair.first?.right to curPair.second)
                }
            }
            if (curPair.first?.left != null) {
                if (isBlack(curPair.first?.left)) {
                    stack.addLast(curPair.first?.left to curPair.second + 1)
                } else {
                    stack.addLast(curPair.first?.left to curPair.second)
                }
            }
        }
        return true
    }

    fun isBST(): Boolean {
        return check(root, null, null)
    }

    private fun check(
        node: RBTNode<K, V>?,
        min: K?,
        max: K?,
    ): Boolean {
        node ?: return true
        if ((min != null && node.key < min) && (max != null && node.key > max)) {
            return false
        }
        return check(node.left, min, node.key) && check(node.right, node.key, max)
    }

    fun size(): Int {
        var cnt = 0
        val stack = ArrayDeque<RBTNode<K, V>?>()
        stack.addLast(root)
        while (!stack.isEmpty()) {
            val curNode = stack.removeLast() ?: continue
            ++cnt
            stack.addLast(curNode.right)
            stack.addLast(curNode.left)
        }
        return cnt
    }

    fun isRBT(): Boolean {
        val stack = ArrayDeque<Pair<RBTNode<K, V>?, Int>>()
        var blackHeight = 0
        stack.addLast(root to 1)
        while (!stack.isEmpty()) {
            val n = stack.removeLast()

            if (isRed(n.first) && isRed(n.first?.parent)) {
                return false
            }
            if (n.first?.left == null && n.first?.right == null) {
                if (blackHeight == 0) {
                    blackHeight = n.second
                }
                if (n.second != blackHeight) {
                    return false
                }
            }
            if (n.first?.left != null) {
                if (isBlack(n.first?.left)) {
                    stack.addLast(n.first?.left to n.second + 1)
                } else {
                    stack.addLast(n.first?.left to n.second)
                }
            }
            if (n.first?.right != null) {
                if (isBlack(n.first?.right)) {
                    stack.addLast(n.first?.right to n.second + 1)
                } else {
                    stack.addLast(n.first?.right to n.second)
                }
            }
        }
        return true
    }
}

class RBTTest {

    @Test
    fun `search test`() {
        val Tree = RBTForTest<Int, Int>()
        for (i in 1..10) {
            Tree.insert(i, i * 10)
        }
        for (i in 1..10) {
            assertEquals(Tree.search(i), i * 10)
        }
    }

    @Test
    fun `missing item search`() {
        val Tree = RBTForTest<Int, Int>()
        for (i in 1..10) {
            Tree.insert(i, i * 10)
        }
        assertNull(Tree.search(20))
    }

    @Test
    fun `search in empty tree`() {
        val Tree = RBTForTest<Int, Int>()
        assertNull(Tree.search(1))
    }

    @Test
    fun `insert test`() {
        val Tree = RBTForTest<Int, String>()
        Tree.insert(1, "1")
        assertEquals(Tree.search(1), "1")
    }

    @Test
    fun `repetitive insertion`() {
        val Tree = RBTForTest<Int, String>()
        Tree.insert(1, "1")
        assertEquals(Tree.size(), 1)
    }

    @Test
    fun `insert random values`() {
        val Tree = RBTForTest<Int, String>()
        val elements: ArrayList<Int> = arrayListOf()
        repeat(100) {
            val newEl = Random.nextInt(-1000, 1000)
            if (Tree.search(newEl) == null) {
                elements.add(newEl)
            }
            Tree.insert(newEl, "$newEl")
        }
        assertEquals(Tree.size(), elements.size)
        for (i in elements)
            assertEquals(Tree.search(i), "$i")
    }

    @Test
    fun `max element`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..10)
            Tree.insert(i, "$i")
        assertEquals(Tree.max(), 10)
    }

    @Test
    fun `max element in empty tree`() {
        val Tree = RBTForTest<Int, String>()
        assertEquals(Tree.max(), null)
    }

    @Test
    fun `min element`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..10)
            Tree.insert(i, "$i")
        assertEquals(Tree.min(), 1)
    }

    @Test
    fun `min element in empty tree`() {
        val Tree = RBTForTest<Int, String>()
        assertEquals(Tree.min(), null)
    }

    @Test
    fun deleteTest() {
        val Tree = RBTForTest<Int, String>()
        Tree.insert(1, "1")
        Tree.delete(1)
        assertNull(Tree.search(1))
    }

    @Test
    fun `delete of a missing element`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..5)
            Tree.insert(i, "$i")
        Tree.delete(10)
        assertEquals(Tree.size(), 5)
    }

    @Test
    fun `delete 10 elements`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..10)
            Tree.insert(i, "$i")
        for (i in 1..10)
            Tree.delete(i)
        for (i in 1..10)
            assertNull(Tree.search(i))
    }

    @Test
    fun `iteration test`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..20)
            Tree.insert(i, "$i")
        var n = 1
        for (i in Tree) {
            assertEquals(Tree.search(n), "$n")
            ++n
        }
    }

    @Test
    fun `iteration test 2`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..20)
            Tree.insert(i, "$i")
        var n = 1
        for (i in Tree) {
            assertEquals(i.key, n)
            assertEquals(i.value, "$n")
            ++n
        }
    }

    @Test
    fun `iteration test 3`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..20)
            Tree.insert(i, "$i")
        var n = 1
        val iter = Tree.iterator()
        while (iter.hasNext()) {
            val i = iter.next()
            assertEquals(i.key, n)
            assertEquals(i.value, "$n")
            ++n
            if (n == 10) {
                val j = iter.next()
                assertEquals(j.key, 10)
                assertEquals(j.value, "10")
                Tree.insert(30, "30")
                break
            }
        }

        shouldThrow<ConcurrentModificationException> {
            if (iter.hasNext()) {
                val i = iter.next()
            }
        }
    }

    @Test
    fun `insert while iterating`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..10)
            Tree.insert(i, "$i")
        shouldThrow<ConcurrentModificationException> {
            for (i in Tree)
                Tree.insert(100, "100")
        }
    }

    @Test
    fun `delete while iterating`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..10)
            Tree.insert(i, "$i")
        shouldThrow<ConcurrentModificationException> {
            for (i in Tree)
                Tree.delete(1)
        }
    }

    @Test
    fun `black root`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..10) {
            Tree.insert(i, "$i")
            assert(Tree.blackRoot())
        }
        val arr = arrayOf(2, 7, 6, 4, 5, 1, 9, 10, 8, 3)
        for (i in arr) {
            Tree.delete(i)
            assert(Tree.blackRoot())
        }
    }

    @Test
    fun `no red node with a red child`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..100) {
            Tree.insert(i, "$i")
            assert(Tree.noRedNodeWithRedChild())
        }
        for (i in 1..100) {
            Tree.delete(i)
            assert(Tree.noRedNodeWithRedChild())
        }
    }

    @Test
    fun `same black height`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..100) {
            Tree.insert(i, "$i")
            assert(Tree.sameBlackHeight())
        }
        for (i in 1..100) {
            Tree.delete(i)
            assert(Tree.sameBlackHeight())
        }
    }

    @Test
    fun `is binary seek tree`() {
        val Tree = RBTForTest<Int, String>()
        for (i in 1..100) {
            Tree.insert(i, "$i")
            assert(Tree.isBST())
        }
        for (i in 1..100) {
            Tree.delete(i)
            assert(Tree.isBST())
        }
    }

    @Test
    fun `big test`() {
        val Tree = RBTForTest<Int, Int>()
        val cur: ArrayList<Int> = arrayListOf()
        var len = 0

        for (i in 1..20000) {
            val rand = Random.nextInt(0, 10)
            if (rand != 0 || len == 0) {
                val newNumber = Random.nextInt(-100000, 100000)
                if (!cur.contains(newNumber)) {
                    Tree.insert(newNumber, 0)
                    cur.add(newNumber)
                    ++len
                }
            } else {
                val index = Random.nextInt(0, len)
                Tree.delete(cur[index])
                cur.removeAt(index)
                --len
            }

            assert(Tree.isRBT())
        }

        while (!cur.isEmpty()) {
            val index = Random.nextInt(0, len)
            Tree.delete(cur[index])
            cur.removeAt(index)
            --len
            assert(Tree.isRBT())
        }
    }
}
