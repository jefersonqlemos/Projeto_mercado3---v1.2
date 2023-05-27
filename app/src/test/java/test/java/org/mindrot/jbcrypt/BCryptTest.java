// Copyright (c) 2006 Damien Miller <djm@mindrot.org>
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
// ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
// OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

package test.java.org.mindrot.jbcrypt;

import main.java.org.mindrot.jbcrypt.BCrypt;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit unit tests for BCrypt routines
 * @author Damien Miller
 * @version 0.3m
 */
public class BCryptTest {

    String test_vectors[][] = {
        {"",
            "$2a$06$DCq7YPn5Rq63x1Lad4cll.TV4S6ytwfsfvkgY8jIucDrjc8deX1s."},
        {"",
            "$2a$08$HqWuK6/Ng6sg9gQzbLrgb.Tl.ZHfXLhvt/SgVyWhQqgqcZ7ZuUtye"},
        {"",
            "$2a$10$k1wbIrmNyFAPwPVPSVa/zecw2BCEnBwVS2GbrmgzxFUOqW9dk4TCW"},
        {"",
            "$2a$12$k42ZFHFWqBp3vWli.nIn8uYyIkbvYRvodzbfbK18SSsY.CsIQPlxO"},
        {"a",
            "$2a$06$m0CrhHm10qJ3lXRY.5zDGO3rS2KdeeWLuGmsfGlMfOxih58VYVfxe"},
        {"a",
            "$2a$08$cfcvVd2aQ8CMvoMpP2EBfeodLEkkFJ9umNEfPD18.hUF62qqlC/V."},
        {"a",
            "$2a$10$k87L/MF28Q673VKh8/cPi.SUl7MU/rWuSiIDDFayrKk/1tBsSQu4u"},
        {"a",
            "$2a$12$8NJH3LsPrANStV6XtBakCez0cKHXVxmvxIlcz785vxAIZrihHZpeS"},
        {"abc",
            "$2a$06$If6bvum7DFjUnE9p2uDeDu0YHzrHM6tf.iqN8.yx.jNN1ILEf7h0i"},
        {"abc",
            "$2a$08$Ro0CUfOqk6cXEKf3dyaM7OhSCvnwM9s4wIX9JeLapehKK5YdLxKcm"},
        {"abc",
            "$2a$10$WvvTPHKwdBJ3uk0Z37EMR.hLA2W6N9AEBhEgrAOljy2Ae5MtaSIUi"},
        {"abc",
            "$2a$12$EXRkfkdmXn2gzds2SSitu.MW9.gAVqa9eLS1//RYtYCmB1eLHg.9q"},
        {"abcdefghijklmnopqrstuvwxyz",
            "$2a$06$.rCVZVOThsIa97pEDOxvGuRRgzG64bvtJ0938xuqzv18d3ZpQhstC"},
        {"abcdefghijklmnopqrstuvwxyz",
            "$2a$08$aTsUwsyowQuzRrDqFflhgekJ8d9/7Z3GV3UcgvzQW3J5zMyrTvlz."},
        {"abcdefghijklmnopqrstuvwxyz",
            "$2a$10$fVH8e28OQRj9tqiDXs1e1uxpsjN0c7II7YPKXua2NAKYvM6iQk7dq"},
        {"abcdefghijklmnopqrstuvwxyz",
            "$2a$12$D4G5f18o7aMMfwasBL7GpuQWuP3pkrZrOAnqP.bmezbMng.QwJ/pG"},
        {"~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
            "$2a$06$fPIsBO8qRqkjj273rfaOI.HtSV9jLDpTbZn782DC6/t7qT67P6FfO"},
        {"~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
            "$2a$08$Eq2r4G/76Wv39MzSX262huzPz612MZiYHVUJe/OcOql2jo4.9UxTW"},
        {"~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
            "$2a$10$LgfYWkbzEvQ4JakH7rOvHe0y8pHKF9OaFgwUZ2q7W2FFZmZzJYlfS"},
        {"~!@#$%^&*()      ~!@#$%^&*()PNBFRD",
            "$2a$12$WApznUOJfkEGSmYRfnkrPOr466oFDCaj4b6HY3EXGvfxm43seyhgC"},
        { "", // Note that this must be 4 or more from the end to pass testCheckpw_failure
            "$2a$05$CCCCCCCCCCCCCCCCCCCCC.7uG0VCzI2bS7j6ymqJi9CdcdxiRTWNy" },
        { "U*U",
            "$2a$05$CCCCCCCCCCCCCCCCCCCCC.E5YPO9kmyuRGyh0XouQYb4YMJKvyOeW" },
        { "U*U*",
            "$2a$05$CCCCCCCCCCCCCCCCCCCCC.VGOzA784oUp/Z0DY336zx7pLYAy0lwK" },
        { "U*U*U",
            "$2a$05$XXXXXXXXXXXXXXXXXXXXXOAcXxm9kjPGEMsLznoKqmqw7tc8WCx4a" },
        { "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
            "$2a$05$abcdefghijklmnopqrstuu5s2v8.iXieOjg/.AySBTTZIIVFJeBui" }
    };

    byte[][] binary_test_vectors =
        {
            // ""
            {
            },

            // "U*U"
            {
                (byte) 'U', (byte) '*', (byte) 'U'
            },

            // "U*U*"
            {
                (byte) 'U', (byte) '*', (byte) 'U', (byte) '*'
            },

            // "U*U*U"
            {
                (byte) 'U', (byte) '*', (byte) 'U', (byte) '*', (byte) 'U'
            },

            // "\xa3"
            { (byte) 0xa3 },
            // "\xff\xff\xa3"
            { (byte) 0xff, (byte) 0xff, (byte) 0xa3 },
            // "\xff\xff\xa3"
            { (byte) 0xff, (byte) 0xff, (byte) 0xa3 },
            // "\xff\xff\xa3"
            { (byte) 0xff, (byte) 0xff, (byte) 0xa3 },
            // "\xff\xff\xa3"
            { (byte) 0xff, (byte) 0xff, (byte) 0xa3 },
            // "\xa3"
            { (byte) 0xa3 },
            // "\xa3"
            { (byte) 0xa3 },
            // "\xa3"
            { (byte) 0xa3 },
            // "1\xa3" "345"
            { (byte) '1', (byte) 0xa3, (byte) '3', (byte) '4', (byte) '5' },
            // "\xff\xa3" "345"
            { (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) '5' },
            // "\xff\xa3" "34" "\xff\xff\xff\xa3" "345"
            { (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) 0xff, (byte) 0xff,
              (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) '5' },
            // "\xff\xa3" "34" "\xff\xff\xff\xa3" "345"
            { (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) 0xff, (byte) 0xff,
              (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) '5' },
            // "\xff\xa3" "34" "\xff\xff\xff\xa3" "345"
            { (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) 0xff, (byte) 0xff,
              (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) '5' },
            // "\xff\xa3" "345"
            { (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) '5' },
            // "\xff\xa3" "345"
            { (byte) 0xff, (byte) 0xa3, (byte) '3', (byte) '4', (byte) '5' },
            // "\xa3" "ab"
            { (byte) 0xa3, (byte) 'a', (byte) 'b' },
            // "\xa3" "ab"
            { (byte) 0xa3, (byte) 'a', (byte) 'b' },
            // "\xa3" "ab"
            { (byte) 0xa3, (byte) 'a', (byte) 'b' },
            // "\xd1\x91"
            { (byte) 0xd1, (byte) 0x91 },
            // "\xd0\xc1\xd2\xcf\xcc\xd8"
            { (byte) 0xd0, (byte) 0xc1, (byte) 0xd2, (byte) 0xcf, (byte) 0xcc, (byte) 0xd8 },

            // [0xaa] * 72
            {
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa
            },

            // [0xaa] * 72 + "chars after 72 are ignored as usual"
            {
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                (byte) 'c', (byte) 'h', (byte) 'a', (byte) 'r', (byte) 's', (byte) ' ',
                (byte) 'a', (byte) 'f', (byte) 't', (byte) 'e', (byte) 'r', (byte) ' ',
                (byte) '7', (byte) '2', (byte) ' ', (byte) 'a', (byte) 'r', (byte) 'e',
                (byte) 'i', (byte) 'g', (byte) 'n', (byte) 'o', (byte) 'r', (byte) 'e',
                (byte) 'd', (byte) ' ', (byte) 'a', (byte) 's', (byte) ' ', (byte) 'u',
                (byte) 's', (byte) 'u', (byte) 'a', (byte) 'l'
            },

            // [0xaa, 0x55] * 36
            {
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55,
                (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55, (byte) 0xaa, (byte) 0x55
            },

            // [0x55, 0xaa, 0xff] * 24
            {
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff,
                (byte) 0x55, (byte) 0xaa, (byte) 0xff, (byte) 0x55, (byte) 0xaa, (byte) 0xff
            }
        };

    String[][] binary_test_match_vectors =
        {
            { // ""
              "$2a$05$CCCCCCCCCCCCCCCCCCCCC.7uG0VCzI2bS7j6ymqJi9CdcdxiRTWNy" },
            { // "U*U"
              "$2a$05$CCCCCCCCCCCCCCCCCCCCC.E5YPO9kmyuRGyh0XouQYb4YMJKvyOeW" },
            { // "U*U*"
              "$2a$05$CCCCCCCCCCCCCCCCCCCCC.VGOzA784oUp/Z0DY336zx7pLYAy0lwK" },
            { // "U*U*U"
              "$2a$05$XXXXXXXXXXXXXXXXXXXXXOAcXxm9kjPGEMsLznoKqmqw7tc8WCx4a" },
            { // "\xa3"
              "$2x$05$/OK.fbVrR/bpIqNJ5ianF.CE5elHaaO4EbggVDjb8P19RukzXSM3e" },
            { // "\xff\xff\xa3"
              "$2x$05$/OK.fbVrR/bpIqNJ5ianF.CE5elHaaO4EbggVDjb8P19RukzXSM3e" },
            { // "\xff\xff\xa3"
              "$2y$05$/OK.fbVrR/bpIqNJ5ianF.CE5elHaaO4EbggVDjb8P19RukzXSM3e" },
            { // "\xff\xff\xa3"
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.nqd1wy.pTMdcvrRWxyiGL2eMz.2a85." },
            { // "\xff\xff\xa3"
              "$2b$05$/OK.fbVrR/bpIqNJ5ianF.CE5elHaaO4EbggVDjb8P19RukzXSM3e" },
            { // "\xa3"
              "$2y$05$/OK.fbVrR/bpIqNJ5ianF.Sa7shbm4.OzKpvFnX1pQLmQW96oUlCq" },
            { // "\xa3"
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.Sa7shbm4.OzKpvFnX1pQLmQW96oUlCq" },
            { // "\xa3"
              "$2b$05$/OK.fbVrR/bpIqNJ5ianF.Sa7shbm4.OzKpvFnX1pQLmQW96oUlCq" },
            { // "1\xa3" "345"
              "$2x$05$/OK.fbVrR/bpIqNJ5ianF.o./n25XVfn6oAPaUvHe.Csk4zRfsYPi" },
            { // "\xff\xa3" "345"
              "$2x$05$/OK.fbVrR/bpIqNJ5ianF.o./n25XVfn6oAPaUvHe.Csk4zRfsYPi" },
            { // "\xff\xa3" "34" "\xff\xff\xff\xa3" "345"
              "$2x$05$/OK.fbVrR/bpIqNJ5ianF.o./n25XVfn6oAPaUvHe.Csk4zRfsYPi" },
            { // "\xff\xa3" "34" "\xff\xff\xff\xa3" "345"
              "$2y$05$/OK.fbVrR/bpIqNJ5ianF.o./n25XVfn6oAPaUvHe.Csk4zRfsYPi" },
            { // "\xff\xa3" "34" "\xff\xff\xff\xa3" "345"
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.ZC1JEJ8Z4gPfpe1JOr/oyPXTWl9EFd." },
            { // "\xff\xa3" "345"
              "$2y$05$/OK.fbVrR/bpIqNJ5ianF.nRht2l/HRhr6zmCp9vYUvvsqynflf9e" },
            { // "\xff\xa3" "345"
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.nRht2l/HRhr6zmCp9vYUvvsqynflf9e" },
            { // "\xa3" "ab"
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.6IflQkJytoRVc1yuaNtHfiuq.FRlSIS" },
            { // "\xa3" "ab"
              "$2x$05$/OK.fbVrR/bpIqNJ5ianF.6IflQkJytoRVc1yuaNtHfiuq.FRlSIS" },
            { // "\xa3" "ab"
              "$2y$05$/OK.fbVrR/bpIqNJ5ianF.6IflQkJytoRVc1yuaNtHfiuq.FRlSIS" },
            { // "\xd1\x91"
              "$2x$05$6bNw2HLQYeqHYyBfLMsv/OiwqTymGIGzFsA4hOTWebfehXHNprcAS" },
            { // "\xd0\xc1\xd2\xcf\xcc\xd8"
              "$2x$05$6bNw2HLQYeqHYyBfLMsv/O9LIGgn8OMzuDoHfof8AQimSGfcSWxnS" },
            { // [0xaa] * 72
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.swQOIzjOiJ9GHEPuhEkvqrUyvWhEMx6" },
            { // [0xaa] * 72 + "chars after 72 are ignored as usual"
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.swQOIzjOiJ9GHEPuhEkvqrUyvWhEMx6" },
            { // [0xaa, 0x55] * 36
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.R9xrDjiycxMbQE2bp.vgqlYpW5wx2yy" },
            { // [0x55, 0xaa, 0xff] * 24
              "$2a$05$/OK.fbVrR/bpIqNJ5ianF.9tQZzcJfm3uj2NvJ/n5xkhpqLrMpWCe" }
        };


    public BCryptTest() {
    }

    /**
     * Test method for 'BCrypt.hashpw(String, String)'
     */
    @Test
    public void testHashpw() {
        System.out.print("BCrypt.hashpw(): ");
        for (int i = 0; i < test_vectors.length; i++) {
            String plain = test_vectors[i][0];
            String salt = test_vectors[i][1].substring(0, 7+22+1);
            String expected = test_vectors[i][1];
            String hashed = BCrypt.hashpw(plain, salt);
            assertEquals(hashed, expected);
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.hashpw(byte[], String)'
     */
    @Test
    public void testHashpwBinary() {
        System.out.print("BCrypt.hashpw(byte[]): ");
        for (int i = 0; i < binary_test_vectors.length; i++) {
            byte[] plain = binary_test_vectors[i];
            String salt = binary_test_match_vectors[i][0].substring(0, 7+22+1);
            String expected = binary_test_match_vectors[i][0];
            String hashed = BCrypt.hashpw(plain, salt);
            assertEquals(hashed, expected);
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.gensalt(prefix, int)'
     */
    public void testGensaltPrefix() {
        String[] valid_prefixes = { "$2a", "$2y", "$2b" };
        System.out.print("BCrypt.gensalt(prefix, 5): ");
        for (String prefix: valid_prefixes) {
            System.out.print(" " + prefix + ":");
            for (int i = 0; i < test_vectors.length; i += 4) {
                String plain = test_vectors[i][0];
                String salt = BCrypt.gensalt(prefix, 5);
                String hashed1 = BCrypt.hashpw(plain, salt);
                String hashed2 = BCrypt.hashpw(plain, hashed1);
                assertEquals(hashed1, hashed2);
                System.out.print(".");
            }
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.gensalt(int)'
     */
    @Test
    public void testGensaltInt() {
        System.out.print("BCrypt.gensalt(log_rounds):");
        for (int i = 4; i <= 12; i++) {
            System.out.print(" " + Integer.toString(i) + ":");
            for (int j = 0; j < test_vectors.length; j += 4) {
                String plain = test_vectors[j][0];
                String salt = BCrypt.gensalt(i);
                String hashed1 = BCrypt.hashpw(plain, salt);
                String hashed2 = BCrypt.hashpw(plain, hashed1);
                assertEquals(hashed1, hashed2);
                System.out.print(".");
            }
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.gensalt()'
     */
    @Test
    public void testGensalt() {
        System.out.print("BCrypt.gensalt(): ");
        for (int i = 0; i < test_vectors.length; i += 4) {
            String plain = test_vectors[i][0];
            String salt = BCrypt.gensalt();
            String hashed1 = BCrypt.hashpw(plain, salt);
            String hashed2 = BCrypt.hashpw(plain, hashed1);
            assertEquals(hashed1, hashed2);
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.checkpw(String, String)'
     * expecting success
     */
    @Test
    public void testCheckpw_success() {
        System.out.print("BCrypt.checkpw w/ good passwords: ");
        for (int i = 0; i < test_vectors.length; i++) {
            String plain = test_vectors[i][0];
            String expected = test_vectors[i][1];
            assertTrue(BCrypt.checkpw(plain, expected));
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.checkpw(byte[], String)'
     * expecting success
     */
    @Test
    public void testCheckpwBinary_success() {
        System.out.print("BCrypt.checkpw(byte[]) w/ good passwords: ");
        for (int i = 0; i < binary_test_vectors.length; i++) {
            byte[] plain = binary_test_vectors[i];
            String expected = binary_test_match_vectors[i][0];
            assertTrue(BCrypt.checkpw(plain, expected));
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.checkpw(String, String)'
     * expecting failure
     */
    @Test
    public void testCheckpw_failure() {
        System.out.print("BCrypt.checkpw w/ bad passwords: ");
        for (int i = 0; i < test_vectors.length; i++) {
            int broken_index = (i + 4) % test_vectors.length;
            String plain = test_vectors[i][0];
            String expected = test_vectors[broken_index][1];
            assertFalse(BCrypt.checkpw(plain, expected));
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test method for 'BCrypt.checkpw(byte[], String)'
     * expecting failure
     */
    @Test
    public void testCheckpwBinary_failure() {
        System.out.print("BCrypt.checkpw(byte[]) w/ bad passwords: ");
        for (int i = 0; i < binary_test_vectors.length; i++) {
            int broken_index = (i + 8) % binary_test_vectors.length;
            byte[] plain = binary_test_vectors[i];
            String expected = binary_test_match_vectors[broken_index][0];
            assertFalse(BCrypt.checkpw(plain, expected));
            System.out.print(".");
        }
        System.out.println("");
    }

    /**
     * Test for correct hashing of non-US-ASCII passwords
     */
    @Test
    public void testInternationalChars() {
        System.out.print("BCrypt.hashpw w/ international chars: ");
        String pw1 = "ππππππππ";
        String pw2 = "????????";

        String h1 = BCrypt.hashpw(pw1, BCrypt.gensalt());
        assertFalse(BCrypt.checkpw(pw2, h1));
        System.out.print(".");

        String h2 = BCrypt.hashpw(pw2, BCrypt.gensalt());
        assertFalse(BCrypt.checkpw(pw1, h2));
        System.out.print(".");
        System.out.println("");
    }
}
