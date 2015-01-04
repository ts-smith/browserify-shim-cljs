(defproject browserify-shim "0.1.0-SNAPSHOT"
            :description "Finds instances of (js/require 'module') and calls
                         browserify with -r for each of them."

            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :eval-in-leiningen true)

