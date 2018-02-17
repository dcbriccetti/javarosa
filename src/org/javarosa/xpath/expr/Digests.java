/*
 * Copyright 2018 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.javarosa.xpath.expr;

import org.javarosa.xpath.XPathTypeMismatchException;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

/** Implements the digest function for XPathFuncExpr */
public class Digests {

    /**
     * Produces a digest.
     * @param value the value from which to produce a digest
     * @param algorithm the algorithm to use. See this method for the names of supported algorithms.
     * @param encoding how to encode the result. Will be base64 if null.
     * @return a digest, as described in the XForms specification
     */
    public static String digest(String value, String algorithm, String encoding) {
        final String algorithmNamesCommaSeparated = "MD5, SHA-1, SHA-256, SHA-384, SHA-512";
        final List<String> algorithmNames = Arrays.asList(algorithmNamesCommaSeparated.split(", "));
        final XPathTypeMismatchException badAlgorithm = new XPathTypeMismatchException(String.format(
                "The function digest received an unsupported cryptographic hashing algorithm: %s. Valid values are %s.",
                algorithm, algorithmNamesCommaSeparated));

        if (! algorithmNames.contains(algorithm.toUpperCase())) {
            throw badAlgorithm;
        }

        final MessageDigest digest;

        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) { // It’s unlikely that these algorithms would not all be implemented
            throw badAlgorithm;
        }

        // Use UTF-8 encoding when reading data to be hashed by default
        byte[] result;
        try {
            result = digest.digest(value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new XPathTypeMismatchException("The function digest failed to use UTF-8 encoding");
        }

        // Hash encoding method defaults to base64 if no third arg is received
        HashEncodingMethod encodingMethod = encoding == null ?
                HashEncodingMethod.BASE64 : HashEncodingMethod.from(encoding);
        return encodingMethod.encode(result);
    }

    enum HashEncodingMethod {
        HEX("hex") {
            @Override
            String encode(byte[] bytes) {
                return DatatypeConverter.printHexBinary(bytes).toLowerCase();
            }
        },
        BASE64("base64") {
            @Override
            String encode(byte[] bytes) {
                return DatatypeConverter.printBase64Binary(bytes);
            }
        };

        private final String value;

        HashEncodingMethod(String value) {
            this.value = value;
        }

        static HashEncodingMethod from(String value) {
            try {
                return valueOf(value.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new XPathTypeMismatchException("Unsupported hash encoding algorithm \"" + value + "\". Supported values are: \"hex\", \"base64\"");
            }
        }

        abstract String encode(byte[] bytes);
    }
}
