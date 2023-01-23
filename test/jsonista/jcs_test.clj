(ns jsonista.jcs-test
  (:require [clojure.test :refer [deftest testing is]]
            [jsonista.core :as json]
            [jsonista.jcs :as jcs]))

(defn custom-key-encoder [v]
  (case v
    "1xxx" "abcd"
    "0xxx" "bcde"))

(def test-object-mapper
  (jcs/object-mapper))

(defn json-write-string [obj]
  (json/write-value-as-string obj test-object-mapper))

(deftest alphabetical-order-test
  (testing "sorted alphabetically before serialization"

    (testing "PersistentArrayMap"
      (is (= "{\"A\":null,\"Z\":null}"
             (json-write-string {:Z nil
                                 :A nil}))))

    (testing "PersistentHashMap"
      (is (= "{\"A\":null,\"Z\":null}"
             (json-write-string (hash-map :Z nil
                                          :A nil)))))

    (testing "deeply nested"
      (is (= "{\"A\":{\"B\":null,\"C\":null,\"Y\":null},\"Z\":null}"
             (json-write-string {:A {"B" nil "Y" nil "C" nil} :Z nil})))))

  (testing "mixed keys compared using stringified form"
    (is (= "{\"A\":null,\"B\":null}"
           (json-write-string
            {:A nil
             "B" nil})))

    (testing "custom key encoder"
      (is (= "{\"abcd\":null,\"bcde\":null}"
             (json/write-value-as-string
              {"1xxx" nil
               "0xxx" nil}
              (jcs/object-mapper {:encode-key-fn custom-key-encoder})))))))

(deftest double-test
  (is (= "[333333333.3333333,1e+30,4.5,0.002,1e-27]"
         (json-write-string
          [333333333.33333329, 1E30, 4.50,
           2e-3, 0.000000000000000000000000001]))))

(deftest unicode-string-test
  (is (= "\"€$\\u000f\\nA'B\\\"\\\\\\\\\\\"/\""
         (json-write-string "\u20ac$\u000F\u000aA'\u0042\u0022\u005c\\\"/"))))
