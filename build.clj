(ns build
  (:refer-clojure :exclude [compile])
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

(def lib 'dev.dotfox/jsonista.jcs)
(defn- the-version
  ([patch] (the-version patch false))
  ([patch snapshot?] (str "1.0." patch (when snapshot? "-SNAPSHOT"))))
(def version (the-version (b/git-count-revs nil)))
(def snapshot (the-version (b/git-count-revs nil) true))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [opts]
  (println "Cleaning target directory...")
  (b/delete {:path "target"})
  opts)

(defn compile [opts]
  (println "Compiling java sources...")
  (b/javac {:src-dirs ["src/java"]
            :class-dir class-dir
            :basis basis
            :javac-opts ["-source" "8" "-target" "8"]})
  opts)

(defn- jar-opts [opts]
  (let [version (if (:snapshot opts) snapshot version)]
    (assoc opts
           :lib lib
           :version version
           :jar-file jar-file
           :scm {:tag (str "v" version)}
           :basis (b/create-basis {})
           :class-dir class-dir
           :target "target"
           :src-dirs ["src/clj" "src/java" "target/classes"]
           :src-pom "pom.xml")))

(defn jar [opts]
  (let [opts (jar-opts opts)]
    (println "Writing pom.xml...")
    (b/write-pom opts)
    (println "Copying source...")
    (b/copy-dir {:src-dirs ["src/clj" "src/java"] :target-dir class-dir})
    (println "Building JAR...")
    (b/jar opts)
    opts))

(defn deploy [opts]
  (let [{:keys [jar-file installer] :as opts} (jar-opts opts)]
    (dd/deploy {:installer installer
                :artifact (b/resolve-path jar-file)
                :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))})))

(defn deploy-remote [opts]
  (-> opts
      (clean)
      (compile)
      (jar)
      (assoc :installer :remote)
      (deploy)))

(defn deploy-local [opts]
  (-> opts
      (clean)
      (compile)
      (jar)
      (assoc :installer :local)
      (deploy)))
