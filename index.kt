
class Solution {
    fun smallestUniqueSubarray(nums: IntArray): Int {
        val n = nums.size
        val m = n + 1

        // Build suffix array
        val a = IntArray(m)
        for (i in 0 until n) {
            a[i] = nums[i]
        }

        val p = IntArray(m)
        val c = IntArray(m)

        val cnt = IntArray(100002)

        for (x in a) cnt[x]++

        for (i in 1 until cnt.size) {
            cnt[i] += cnt[i - 1]
        }

        for (i in m - 1 downTo 0) {
            p[--cnt[a[i]]] = i
        }

        c[p[0]] = 0
        var classes = 1

        for (i in 1 until m) {
            if (a[p[i]] != a[p[i - 1]]) {
                classes++
            }
            c[p[i]] = classes - 1
        }

        var len = 1

        while (len < m) {
            val pn = IntArray(m)

            for (i in 0 until m) {
                pn[i] = (p[i] - len + m) % m
            }

            val count = IntArray(classes)

            for (x in pn) {
                count[c[x]]++
            }

            for (i in 1 until classes) {
                count[i] += count[i - 1]
            }

            for (i in m - 1 downTo 0) {
                val x = pn[i]
                p[--count[c[x]]] = x
            }

            val cn = IntArray(m)
            cn[p[0]] = 0

            var newClasses = 1

            for (i in 1 until m) {
                val cur = p[i]
                val prev = p[i - 1]

                if (
                    c[cur] != c[prev] ||
                    c[(cur + len) % m] !=
                    c[(prev + len) % m]
                ) {
                    newClasses++
                }

                cn[cur] = newClasses - 1
            }

            for (i in 0 until m) {
                c[i] = cn[i]
            }

            classes = newClasses
            len *= 2
        }

        // Build LCP array using Kasai's algorithm
        val rank = IntArray(m)

        for (i in 0 until m) {
            rank[p[i]] = i
        }

        val lcp = IntArray(m)
        var k = 0

        for (i in 0 until n) {
            val r = rank[i]

            if (r == 0) {
                k = 0
                continue
            }

            val j = p[r - 1]

            while (
                i + k < n &&
                j + k < n &&
                nums[i + k] == nums[j + k]
            ) {
                k++
            }

            lcp[r] = k

            if (k > 0) k--
        }

        // Find minimum unique subarray length
        var ans = n

        for (r in 1..n) {
            val start = p[r]

            val maxLcp = maxOf(
                lcp[r],
                if (r < n) lcp[r + 1] else 0
            )

            if (maxLcp < n - start) {
                ans = minOf(ans, maxLcp + 1)
            }
        }

        return ans
    }
}
