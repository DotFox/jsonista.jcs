# jsonista.jcs

[RFC 8785 JSON Canonicalization Scheme (JCS)](https://www.rfc-editor.org/rfc/rfc8785) extension for [metosin/jsonista](https://github.com/metosin/jsonista).

## deps.edn

```clojure
dev.dotfox/jsonista.jcs {:mvn/version "1.0.8"}
```

or as a git dependency

```clojure
io.github.DotFox/jsonista.jcs {:git/sha "99faeaa5da5eee98d682a4601e6e5f9b7adea44f"}
```

and prep java sources

```shell
clj -X:deps prep
```

## project.clj

```clojure
[dev.dotfox/jsonista.jcs "1.0.8"]
```

# TLDR;

## Create custom `object-mapper`

```clojure
(require '[jsonista.core :as json] '[jsonista.jcs :as jcs])

;; jcs/object-mapper expects the same arguments as json/object-mapper
(def jcs-object-mapper (jcs/object-mapper))

;; with custom object-mapper the result will be brought in line with the RFC 8785
(json/write-value-as-string obj jcs-object-mapper)
```

# Differences in compare with default `jsonista.core/object-mapper`

1. The numbers are serialised according to Section 7.1.12.1 of [ECMA-262](https://www.rfc-editor.org/rfc/rfc8785#ECMA-262)
2. Object keys are sorted recursively using serialized form. Custom `:encode-key-fn` will be used if provided.
3. Unicode code points in strings serialized to lowercase.

