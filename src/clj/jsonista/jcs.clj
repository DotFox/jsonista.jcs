(ns jsonista.jcs
  (:require
   [jsonista.core :as json])
  (:import
   [clojure.lang Ratio]
   [com.fasterxml.jackson.core JsonFactory JsonFactoryBuilder]
   [com.fasterxml.jackson.core.json JsonWriteFeature]
   [com.fasterxml.jackson.databind Module]
   [com.fasterxml.jackson.databind.module SimpleModule]
   [java.util Map]
   [jsonista.jcs.jackson CanonicalDoubleSerializer CanonicalMapSerializer CanonicalRatioSerializer]))

(defn- canonical-json-module ^Module [key-serializer]
  (doto (SimpleModule. "RFC 8785 JSON Canonicalization Scheme (JCS)")
    (.addSerializer Double (CanonicalDoubleSerializer.))
    (.addSerializer Ratio (CanonicalRatioSerializer.))
    (.addSerializer Map (CanonicalMapSerializer. key-serializer))))

(defn- canonical-json-factory ^JsonFactory []
  (.build
   (doto (JsonFactoryBuilder.)
     (.configure JsonWriteFeature/WRITE_HEX_UPPER_CASE false))))

(defn- default-encode-key ^String [k]
  (cond
    (keyword? k) (str (symbol k))
    :else (str k)))

(defn object-mapper
  "Identical to `jsonista.core/object-mapper` except it ensures produced
  JSON to follow RFC 8785 JSON Canonicalization Scheme (JCS)"
  ([] (object-mapper {}))
  ([{:keys [encode-key-fn]
     :or   {encode-key-fn default-encode-key}
     :as   args}]
   (let [encode-key-fn (if (true? encode-key-fn) default-encode-key encode-key-fn)
         args          (-> args
                           ;; Ensure key encoding is handled by canonical module
                           (assoc :encode-key-fn false)
                           ;; Inject first part of Canonicalizer as jackson module
                           ;;  1. Decimal and Ratio -> custom serialization
                           ;;  2. Map -> TreeMap with string encoded keys
                           (update :modules (fnil conj []) (canonical-json-module encode-key-fn))
                           ;; Second part of Canonicalizer is to force lowercase encoding for HEX values in UTF-8 strings
                           (assoc :factory (canonical-json-factory)))]
     (json/object-mapper args))))

(comment

  (def obj
    {"numbers" [333333333.33333329, 1E30, 4.50,
                2e-3, 0.000000000000000000000000001],
     "string" "\u20ac$\u000F\u000aA'\u0042\u0022\u005c\\\"/",
     :literals [nil, true, false]
     "control characters" "\u0008\u0009\u000A\u000C\u000D"})

  (json/write-value-as-string obj (object-mapper))

  )
