package com.github.yu_zhejian.ystr.hash;

/**
 * Constants for Fowler-Noll-Vo 1 hash.
 *
 * <p><b>Copyright</b>
 *
 * <p>Please do not copyright this code. This code is in the public domain.
 *
 * <p>LANDON CURT NOLL DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL LANDON CURT NOLL BE LIABLE FOR ANY
 * SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING
 * OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 * <p><b>References</b>
 *
 * <ol>
 *   <li><a href="http://isthe.com/chongo/tech/comp/fnv/#FNV-1">Official site.</a>
 *   <li><a href="http://isthe.com/chongo/src/fnv/fnv-5.0.3.tar.gz">Reference implementation</a>
 *   <li><a href="https://github.com/nstickney/fnv-java/blob/master/src/fnv/FNV.java">A Java
 *       implementation.</a>
 * </ol>
 *
 * @author chongo <a href="http://www.isthe.com/chongo/">Landon Curt Noll</a> The original author of
 *     this software.
 */
public abstract class FNV1Base implements HashInterface {
    /** Default constructor. */
    protected FNV1Base() {}

    protected static final int FNV_PRIME_32 = 16777619;
    protected static final long FNV_PRIME_64 = 1099511628211L;
    protected static final int FNV_INIT_32 = 0x811c9dc5;
    protected static final long FNV_INIT_64 = 0xcbf29ce484222325L;
}
