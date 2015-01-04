(ns leiningen.browserify-shim
  (:require [clojure.tools.reader :as reader])
  (:use [clojure.java.shell :only  [sh]])
  )

(defn read-all [input]
  (binding [reader/*default-data-reader-fn* (fn [t v] v)]
    (reader/read-string (str "(" input ")"))))

(defn collectRequires [forms requires]
  (-> (fn collectRequire [form]
        (when  (list? form)  
          (let  [[f m] form]  
            (when (-> f  (= 'js/require))
              (if (string? m)
                (swap! requires conj m)    
                (leiningen.core.main/warn 
                  (str "Can only include static " 
                       "strings for browserify, " 
                       "got " (pr-str m)))))))
        form)
    (clojure.walk/prewalk forms)))

(defn browserify-shim
  "Create browserify bundle from all (js/require \"[module]\") is configured package"
  [project & args]
  (doseq [{:keys [id source-paths]} (get-in project [:cljsbuild :builds])]
    (when-let [outputfile (get-in project [:browserify-builds id])]
      (let [requires (atom #{})]
        (doseq [source-path source-paths]
                (let [f (clojure.java.io/file source-path)
                      projectFiles (file-seq f)]
                  (doseq [f projectFiles]
                    (when (-> f .getName (.endsWith ".cljs"))
                      (let [contents (slurp (.getPath f))
                            forms (read-all contents)]
                        (collectRequires forms requires))))))
        (let [modules (mapcat #(vector "-r" %) @requires)
              bundle (:out (apply sh "browserify" modules))]
          (leiningen.core.main/info (str "Writing \"browserify "
            (clojure.string/join " " modules) "\" output to " outputfile))
          (spit outputfile bundle)
          )))))
