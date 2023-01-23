# jsonista.jcs

[RFC 8785 JSON Canonicalization Scheme (JCS)](https://www.rfc-editor.org/rfc/rfc8785) extension for [metosin/jsonista](https://github.com/metosin/jsonista).

# TLDR;

## Include as a git-dependency (maven release will be cut off later) in `deps.edn`

``` edn
{:deps {io.github.DotFox/jsonista.jcs {:git/sha "7aa9a4bda9391006b3b051f0b83c5816d705ab57"}}}
```

## Prep java sources

``` shell
clj -X:deps prep
```

## Create custom `object-mapper`

``` clojure
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

